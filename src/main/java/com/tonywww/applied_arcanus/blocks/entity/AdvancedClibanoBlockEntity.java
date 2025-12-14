package com.tonywww.applied_arcanus.blocks.entity;

import appeng.api.upgrades.IUpgradeableObject;
import appeng.blockentity.AEBaseBlockEntity;
import appeng.blockentity.ServerTickingBlockEntity;
import appeng.util.inv.AppEngInternalInventory;
import com.stal111.forbidden_arcanus.common.block.entity.clibano.ClibanoInputSlot;
import com.stal111.forbidden_arcanus.common.block.entity.clibano.logic.ClibanoAccessor;
import com.stal111.forbidden_arcanus.common.item.crafting.ClibanoRecipe;
import com.stal111.forbidden_arcanus.common.item.crafting.ClibanoRecipeInput;
import com.stal111.forbidden_arcanus.common.item.enhancer.EnhancerDefinition;
import com.stal111.forbidden_arcanus.common.item.enhancer.EnhancerHelper;
import com.stal111.forbidden_arcanus.core.init.ModRecipeTypes;
import com.tonywww.applied_arcanus.blocks.AdvancedClibanoBlock;
import com.tonywww.applied_arcanus.blocks.entity.logic.AdvancedDoubleSmeltLogic;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AdvancedClibanoBlockEntity extends AEBaseBlockEntity implements ClibanoAccessor, ServerTickingBlockEntity,
        IUpgradeableObject
{
    private static final int SLOT_FIRST_INPUT = 0;
    private static final int SLOT_SECOND_INPUT = 1;
    private static final int SLOT_ENHANCER = 2;
    private static final int SLOT_OUTPUT = 3;

    private float speedMultiplier = 10.0f;

    private final AppEngInternalInventory inventory = new AppEngInternalInventory(4) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            setChanged();
            if (level != null && !level.isClientSide) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return super.isItemValid(slot, stack) && slot != SLOT_OUTPUT;
        }
    };

    private final AdvancedDoubleSmeltLogic smeltLogic;
    private int[] cookingProgress = new int[2];
    private int[] cookingDuration = new int[2];

    public AdvancedClibanoBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.smeltLogic = new AdvancedDoubleSmeltLogic(this, null);
    }

    public ItemStack getInputStack(ClibanoInputSlot slot) {
        return switch (slot) {
            case FIRST -> inventory.getStackInSlot(SLOT_FIRST_INPUT);
            case SECOND -> inventory.getStackInSlot(SLOT_SECOND_INPUT);
            case BOTH -> inventory.getStackInSlot(SLOT_FIRST_INPUT);
        };
    }

    public ItemStack getResultStack() {
        return inventory.getStackInSlot(SLOT_OUTPUT);
    }

    public HolderSet<EnhancerDefinition> getEnhancers() {
        ItemStack enhancerStack = inventory.getStackInSlot(SLOT_ENHANCER);
        if (enhancerStack.isEmpty()) {
            return HolderSet.empty();
        } else {
            var optional = EnhancerHelper.getEnhancerHolder(this.level.registryAccess(), enhancerStack);
            return optional.isPresent() ? HolderSet.direct(optional.get()) : HolderSet.empty();
        }
    }

    public boolean canSmelt(@Nullable RecipeHolder<ClibanoRecipe> recipe, ClibanoInputSlot inputSlot) {
        if (recipe == null) return false;

        ItemStack result = recipe.value().result();
        ItemStack outputStack = inventory.getStackInSlot(SLOT_OUTPUT);

        if (outputStack.isEmpty()) return true;
        if (!ItemStack.isSameItemSameComponents(outputStack, result)) return false;

        return outputStack.getCount() + result.getCount() <= outputStack.getMaxStackSize();
    }

    public void finishRecipe(@Nullable RecipeHolder<ClibanoRecipe> recipe, ClibanoInputSlot inputSlot) {
        if (recipe == null) return;

        inventory.extractItem(SLOT_FIRST_INPUT, 1, false);
        inventory.extractItem(SLOT_SECOND_INPUT, 1, false);

        if (recipe.value().requiredEnhancer().isPresent()) {
            inventory.extractItem(SLOT_ENHANCER, 1, false);
        }

        ItemStack result = recipe.value().result().copy();
        ItemStack outputStack = inventory.getStackInSlot(SLOT_OUTPUT);

        if (outputStack.isEmpty()) {
            inventory.setItemDirect(SLOT_OUTPUT, result);
        } else {
            outputStack.grow(result.getCount());
        }

        setChanged();
    }

    public int getCookingTime(@Nullable RecipeHolder<ClibanoRecipe> recipe) {
        if (recipe == null) return 0;
        int baseTime = recipe.value().getDefaultCookingTime();
        return (int) (baseTime / speedMultiplier);
    }

    public float getSpeedMultiplier() {
        return speedMultiplier;
    }

    public void setSpeedMultiplier(float multiplier) {
        this.speedMultiplier = Math.max(0.1f, multiplier);
        setChanged();
    }

    @Override
    public void serverTick()
    {
        if (level == null) return;

        List<RecipeHolder<ClibanoRecipe>> recipes = this.findRecipes(level);
        this.smeltLogic.updateRecipes(recipes);

        boolean hasRecipe = !recipes.isEmpty() && this.smeltLogic.canSmelt();
        this.smeltLogic.tick(hasRecipe);

        if (this.getBlockState().getValue(AdvancedClibanoBlock.LIT) != hasRecipe) {
            level.setBlock(this.getBlockPos(), this.getBlockState().setValue(AdvancedClibanoBlock.LIT, hasRecipe), 3);
        }
        System.out.println(this.smeltLogic.cookingProgress[0] + ", " + this.smeltLogic.cookingDuration[0]);
        System.out.println(this.smeltLogic.cookingProgress[1] + ", " + this.smeltLogic.cookingDuration[1]);
        this.setChanged();
    }

    private List<RecipeHolder<ClibanoRecipe>> findRecipes(Level level) {
        ClibanoRecipeInput input = new ClibanoRecipeInput(
                inventory.getStackInSlot(SLOT_FIRST_INPUT),
                inventory.getStackInSlot(SLOT_SECOND_INPUT)
        );

        return level.getRecipeManager()
                .getAllRecipesFor(ModRecipeTypes.CLIBANO_COMBUSTION.get())
                .stream()
                .filter(recipe -> recipe.value().matches(input, level, getEnhancers()))
                .toList();
    }

    // ========== NBT ==========
    @Override
    public void saveAdditional(CompoundTag data, HolderLookup.Provider registries)
    {
        super.saveAdditional(data, registries);
        inventory.writeToNBT(data, "Inventory", registries);
        data.putIntArray("CookingProgress", cookingProgress);
        data.putIntArray("CookingDuration", cookingDuration);
        data.putFloat("SpeedMultiplier", speedMultiplier);
    }

    @Override
    public void loadTag(CompoundTag data, HolderLookup.Provider registries)
    {
        super.loadTag(data, registries);
        inventory.readFromNBT(data, "Inventory", registries);
        cookingProgress = data.getIntArray("CookingProgress");
        cookingDuration = data.getIntArray("CookingDuration");
        if (cookingProgress.length != 2) cookingProgress = new int[2];
        if (cookingDuration.length != 2) cookingDuration = new int[2];
        speedMultiplier = data.getFloat("SpeedMultiplier");
        if (speedMultiplier <= 0) speedMultiplier = 4.0f;
    }

    public AppEngInternalInventory getInventory() {
        return inventory;
    }

    public int getCookingDuration()
    {
        return cookingDuration[0];
    }

    public int getCookingProgress()
    {
        return cookingProgress[0];
    }

    @Override
    public void addAdditionalDrops(Level level, BlockPos pos, List<ItemStack> drops)
    {
        super.addAdditionalDrops(level, pos, drops);
        for(ItemStack stack : inventory)
        {
            drops.add(stack);
        }
    }

    @Override
    public void clearContent()
    {
        super.clearContent();
        inventory.clear();
    }
}