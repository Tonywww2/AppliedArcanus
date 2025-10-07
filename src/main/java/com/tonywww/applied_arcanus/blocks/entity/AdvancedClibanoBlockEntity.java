package com.tonywww.applied_arcanus.blocks.entity;

import com.stal111.forbidden_arcanus.common.block.entity.clibano.ClibanoInputSlot;
import com.stal111.forbidden_arcanus.common.block.entity.clibano.logic.ClibanoAccessor;
import com.stal111.forbidden_arcanus.common.item.crafting.ClibanoRecipe;
import com.stal111.forbidden_arcanus.common.item.crafting.ClibanoRecipeInput;
import com.stal111.forbidden_arcanus.common.item.enhancer.EnhancerDefinition;
import com.stal111.forbidden_arcanus.common.item.enhancer.EnhancerHelper;
import com.stal111.forbidden_arcanus.core.init.ModRecipeTypes;
import com.tonywww.applied_arcanus.blocks.AdvancedClibanoBlock;
import com.tonywww.applied_arcanus.blocks.entity.logic.AdvancedDoubleSmeltLogic;
import com.tonywww.applied_arcanus.init.ModBlockEntities;
import com.tonywww.applied_arcanus.menu.AdvancedClibanoMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AdvancedClibanoBlockEntity extends BlockEntity implements MenuProvider, ClibanoAccessor {
    private static final int SLOT_FIRST_INPUT = 0;
    private static final int SLOT_SECOND_INPUT = 1;
    private static final int SLOT_ENHANCER = 2;
    private static final int SLOT_OUTPUT = 3;

    private float speedMultiplier = 10.0f;

    private final ItemStackHandler itemHandler = new ItemStackHandler(4) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (level != null && !level.isClientSide) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return slot != SLOT_OUTPUT;
        }
    };

    private AdvancedDoubleSmeltLogic smeltLogic;
    private int[] cookingProgress = new int[2];
    private int[] cookingDuration = new int[2];

    public AdvancedClibanoBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ADVANCED_CLIBANO.get(), pos, state);
        this.smeltLogic = new AdvancedDoubleSmeltLogic(this, null) {

        };
    }

    public ItemStack getInputStack(ClibanoInputSlot slot) {
        return switch (slot) {
            case FIRST -> itemHandler.getStackInSlot(SLOT_FIRST_INPUT);
            case SECOND -> itemHandler.getStackInSlot(SLOT_SECOND_INPUT);
            case BOTH -> itemHandler.getStackInSlot(SLOT_FIRST_INPUT);
        };
    }

    public ItemStack getResultStack() {
        return itemHandler.getStackInSlot(SLOT_OUTPUT);
    }

    public HolderSet<EnhancerDefinition> getEnhancers() {
        ItemStack enhancerStack = itemHandler.getStackInSlot(SLOT_ENHANCER);
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
        ItemStack outputStack = itemHandler.getStackInSlot(SLOT_OUTPUT);

        if (outputStack.isEmpty()) return true;
        if (!ItemStack.isSameItemSameComponents(outputStack, result)) return false;

        return outputStack.getCount() + result.getCount() <= outputStack.getMaxStackSize();
    }

    public void finishRecipe(@Nullable RecipeHolder<ClibanoRecipe> recipe, ClibanoInputSlot inputSlot) {
        if (recipe == null) return;

        itemHandler.extractItem(SLOT_FIRST_INPUT, 1, false);
        itemHandler.extractItem(SLOT_SECOND_INPUT, 1, false);

        if (recipe.value().requiredEnhancer().isPresent()) {
            itemHandler.extractItem(SLOT_ENHANCER, 1, false);
        }

        ItemStack result = recipe.value().result().copy();
        ItemStack outputStack = itemHandler.getStackInSlot(SLOT_OUTPUT);

        if (outputStack.isEmpty()) {
            itemHandler.setStackInSlot(SLOT_OUTPUT, result);
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

    // ========== Tick 逻辑 ==========
    public static void tick(Level level, BlockPos pos, BlockState state, AdvancedClibanoBlockEntity entity) {
        if (level.isClientSide) return;

        List<RecipeHolder<ClibanoRecipe>> recipes = entity.findRecipes(level);
        entity.smeltLogic.updateRecipes(recipes);

        boolean hasRecipe = !recipes.isEmpty() && entity.smeltLogic.canSmelt();
        entity.smeltLogic.tick(hasRecipe);

        if (state.getValue(AdvancedClibanoBlock.LIT) != hasRecipe) {
            level.setBlock(pos, state.setValue(AdvancedClibanoBlock.LIT, hasRecipe), 3);
        }
        System.out.println(entity.smeltLogic.cookingProgress[0] + ", " + entity.smeltLogic.cookingDuration[0]);
        System.out.println(entity.smeltLogic.cookingProgress[1] + ", " + entity.smeltLogic.cookingDuration[1]);
        entity.setChanged();
    }

    private List<RecipeHolder<ClibanoRecipe>> findRecipes(Level level) {
        ClibanoRecipeInput input = new ClibanoRecipeInput(
                itemHandler.getStackInSlot(SLOT_FIRST_INPUT),
                itemHandler.getStackInSlot(SLOT_SECOND_INPUT)
        );

        return level.getRecipeManager()
                .getAllRecipesFor(ModRecipeTypes.CLIBANO_COMBUSTION.get())
                .stream()
                .filter(recipe -> recipe.value().matches(input, level, getEnhancers()))
                .toList();
    }

    // ========== MenuProvider ==========
    @Override
    public Component getDisplayName() {
        return Component.translatable("container.applied_arcanus.advanced_clibano");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, @NotNull Inventory playerInventory, @NotNull Player player) {
        return new AdvancedClibanoMenu(containerId, playerInventory, this, new SimpleContainerData(2) {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> cookingProgress[0];
                    case 1 -> cookingDuration[0];
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                if (index == 0) cookingProgress[0] = value;
                else if (index == 1) cookingDuration[0] = value;
            }
        });
    }

    // ========== NBT ==========
    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Inventory", itemHandler.serializeNBT(registries));
        tag.putIntArray("CookingProgress", cookingProgress);
        tag.putIntArray("CookingDuration", cookingDuration);
        tag.putFloat("SpeedMultiplier", speedMultiplier);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);
        itemHandler.deserializeNBT(registries, tag.getCompound("Inventory"));
        cookingProgress = tag.getIntArray("CookingProgress");
        cookingDuration = tag.getIntArray("CookingDuration");
        if (cookingProgress.length != 2) cookingProgress = new int[2];
        if (cookingDuration.length != 2) cookingDuration = new int[2];
        speedMultiplier = tag.getFloat("SpeedMultiplier");
        if (speedMultiplier <= 0) speedMultiplier = 4.0f;
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        saveAdditional(tag, registries);
        return tag;
    }

    @Override
    public void handleUpdateTag(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider lookupProvider) {
        loadAdditional(tag, lookupProvider);
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(@NotNull Connection net, @NotNull ClientboundBlockEntityDataPacket pkt, HolderLookup.@NotNull Provider lookupProvider) {
        handleUpdateTag(pkt.getTag(), lookupProvider);
    }

    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }
}