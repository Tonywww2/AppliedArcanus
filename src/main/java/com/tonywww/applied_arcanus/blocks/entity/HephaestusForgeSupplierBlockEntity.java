package com.tonywww.applied_arcanus.blocks.entity;

import appeng.api.inventories.InternalInventory;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.api.upgrades.UpgradeInventories;
import appeng.blockentity.AEBaseBlockEntity;
import appeng.blockentity.ServerTickingBlockEntity;
import appeng.core.definitions.AEItems;
import com.mojang.authlib.GameProfile;
import com.stal111.forbidden_arcanus.common.block.HephaestusForgeBlock;
import com.stal111.forbidden_arcanus.common.block.entity.PedestalBlockEntity;
import com.stal111.forbidden_arcanus.common.block.entity.forge.HephaestusForgeBlockEntity;
import com.stal111.forbidden_arcanus.common.block.entity.forge.ritual.ActiveRitualData;
import com.stal111.forbidden_arcanus.common.block.entity.forge.ritual.RitualManager;
import com.stal111.forbidden_arcanus.common.block.pedestal.effect.PedestalEffectTrigger;
import com.stal111.forbidden_arcanus.common.essence.EssenceValue;
import com.stal111.forbidden_arcanus.core.init.ModDataComponents;
import com.tonywww.applied_arcanus.init.ModBlockEntities;
import com.tonywww.applied_arcanus.init.ModBlocks;
import com.tonywww.applied_arcanus.mixins.ActiveRitualDataAccessor;
import com.tonywww.applied_arcanus.mixins.RitualManagerAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HephaestusForgeSupplierBlockEntity extends AEBaseBlockEntity implements ServerTickingBlockEntity,
        IUpgradeableObject
{

    private static final int FORGE_SLOTS = 5;
    public static final int FORGE_MAIN_SLOT = HephaestusForgeBlockEntity.MAIN_SLOT - 4;

    private final IUpgradeInventory upgrades = UpgradeInventories.forMachine(ModBlocks.HEPHAESTUS_FORGE_SUPPLIER.get(), 4, this::onUpgradesChanged);

    private @Nullable HephaestusForgeBlockEntity forgeBlockEntity;
    private List<PedestalBlockEntity> pedestalBlockEntities;
    private final InternalInventory proxyItemHandler;
    private FakePlayer fakePlayer;
    public int upgradeCount = 0;


    public HephaestusForgeSupplierBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        // ProxyItemHandler已经根据forgeBlockEntity的状态做过判空，因此其本身可以始终保持非空状态
        proxyItemHandler = new ProxyItemHandler();
    }

    public static void onRegisterCaps(RegisterCapabilitiesEvent event)
    {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntities.HEPHAESTUS_FORGE_SUPPLIER.get(),
                (be, direction) -> {
                    IItemHandler base = be.proxyItemHandler.toItemHandler();
                    if (base == null) return null;

                    // UP（以及 null）允许抽取；其余方向禁止抽取
                    if (direction == null || direction == Direction.UP) {
                        return base;
                    }

                    // 返回一个禁止抽取的包装类
                    return new IItemHandler() {
                        @Override public int getSlots() { return base.getSlots(); }
                        @Override public @NotNull ItemStack getStackInSlot(int slot) { return base.getStackInSlot(slot); }
                        @Override public int getSlotLimit(int slot) { return base.getSlotLimit(slot); }
                        @Override public boolean isItemValid(int slot, @NotNull ItemStack stack) { return base.isItemValid(slot, stack); }

                        @Override
                        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
                            return base.insertItem(slot, stack, simulate);
                        }

                        @Override
                        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
                            return ItemStack.EMPTY;
                        }
                    };
                }
        );
    }


    public void onUpgradesChanged()
    {
        this.upgradeCount = upgrades.getInstalledUpgrades(AEItems.SPEED_CARD);
        setChanged();
    }

    @Override
    public IUpgradeInventory getUpgrades()
    {
        return upgrades;
    }

    private List<PedestalBlockEntity> collectPedestals(ServerLevel level, BlockPos pos) {
        List<BlockEntity> tempList = new ArrayList<>(9);
        List<PedestalBlockEntity> out = new ArrayList<>(9);

        tempList.add(level.getBlockEntity(pos.north(3)));
        tempList.add(level.getBlockEntity(pos.east(3)));
        tempList.add(level.getBlockEntity(pos.south(3)));
        tempList.add(level.getBlockEntity(pos.west(3)));

        tempList.add(level.getBlockEntity(pos.north(2).east(2)));
        tempList.add(level.getBlockEntity(pos.north(2).west(2)));
        tempList.add(level.getBlockEntity(pos.south(2).east(2)));
        tempList.add(level.getBlockEntity(pos.south(2).west(2)));

        for (BlockEntity be : tempList) {
            if (be instanceof PedestalBlockEntity pedestal) {
                out.add(pedestal);
            }
        }
        return out;

    }

    @Override
    public void serverTick() {
        if(level == null) return;

        BlockPos pos = this.getBlockPos();
        BlockPos below = pos.below();
        BlockState belowState = level.getBlockState(below);
        if (belowState.getBlock() instanceof HephaestusForgeBlock) {
            BlockEntity be = level.getBlockEntity(below);
            if (be instanceof HephaestusForgeBlockEntity forgeEntity) {
                this.forgeBlockEntity = forgeEntity;
                this.pedestalBlockEntities = this.collectPedestals((ServerLevel) level, below);

                if (this.fakePlayer == null) {
                    GameProfile gameProfile = new GameProfile(UUID.randomUUID(), "HephaestusForgeSupplier");
                    this.fakePlayer = new FakePlayer((ServerLevel) level, gameProfile);
                    this.fakePlayer.setPos(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
                }

                forgeEntity.setChanged();
                for (PedestalBlockEntity pedestal : this.pedestalBlockEntities) {
                    pedestal.setChanged();
                }

                RitualManager ritualManager = this.forgeBlockEntity.getRitualManager();
                if (ritualManager.getValidRitual().isPresent()) {
                    ActiveRitualData data = ((RitualManagerAccessor) ritualManager).getActiveRitualDataField();
                    if (data == null) {
                        forgeEntity.getRitualManager().startRitual(this.fakePlayer, this.forgeBlockEntity.getEssenceManager().getStorage());
                    } else {
                        int counter = data.getCounter();

                        ActiveRitualDataAccessor accessor = (ActiveRitualDataAccessor) data;
                        int toAccelerate  = 8 * this.upgradeCount;
                        accessor.setCounter(Math.min(data.getRitual().duration(), counter + toAccelerate));
                    }
                }

            } else {
                this.forgeBlockEntity = null;
            }
        }
    }

    @Override
    public void saveAdditional(CompoundTag data, HolderLookup.Provider registries)
    {
        super.saveAdditional(data, registries);
        this.upgrades.writeToNBT(data, "upgrades", registries);
    }

    @Override
    public void loadTag(CompoundTag data, HolderLookup.Provider registries)
    {
        super.loadTag(data, registries);
        this.upgrades.readFromNBT(data, "upgrades", registries);
    }

    @Override
    public void addAdditionalDrops(Level level, BlockPos pos, List<ItemStack> drops)
    {
        super.addAdditionalDrops(level, pos, drops);
        for(ItemStack stack : upgrades) {
            drops.add(stack);
        }
    }

    @Override
    public void clearContent()
    {
        super.clearContent();
        this.upgrades.clear();
    }

    private class ProxyItemHandler implements InternalInventory {

        @Override
        public int size() {
            return FORGE_SLOTS + (pedestalBlockEntities == null ? 0 : pedestalBlockEntities.size());
        }

        @Override
        public @NotNull ItemStack getStackInSlot(int slot) {
            if (forgeBlockEntity == null) return ItemStack.EMPTY;
            if (slot < FORGE_SLOTS) {
                // 映射到锻炉的4-8槽位
                return forgeBlockEntity.getItemStackHandler().getStackInSlot(slot + 4);
            } else if (slot < FORGE_SLOTS + pedestalBlockEntities.size()) {
                // 基座槽位
                int pedestalIndex = slot - FORGE_SLOTS;
                return pedestalBlockEntities.get(pedestalIndex).getStack();
            }
            return ItemStack.EMPTY;
        }

        @Override
        public void setItemDirect(int slot, ItemStack stack) {
            if (forgeBlockEntity == null) return;

            if (slot < FORGE_SLOTS) {
                ItemStackHandler handler = forgeBlockEntity.getItemStackHandler();
                handler.setStackInSlot(slot + 4, stack);
                forgeBlockEntity.setChanged();
                return;
            }

            int idx = slot - FORGE_SLOTS;
            if (pedestalBlockEntities != null && idx >= 0 && idx < pedestalBlockEntities.size()) {
                var pedestal = pedestalBlockEntities.get(idx);
                if (stack.isEmpty()) {
                    pedestal.setStack(ItemStack.EMPTY, null, PedestalEffectTrigger.PLAYER_REMOVE_ITEM);
                } else {
                    pedestal.setStack(stack, null, PedestalEffectTrigger.PLAYER_PLACE_ITEM);
                }
                pedestal.setChanged();
            }
        }

        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            if (forgeBlockEntity == null) return stack;
            if (slot < FORGE_SLOTS) {
                ItemStackHandler handler = forgeBlockEntity.getItemStackHandler();
                if (handler != null) {
                    if (stack.getComponents().has(ModDataComponents.ESSENCE_VALUE.get())) {
                        EssenceValue essenceValue = stack.getComponents().get(ModDataComponents.ESSENCE_VALUE.get());
                        if (essenceValue != null && slot == HephaestusForgeBlockEntity.SLOT_FROM_ESSENCE_TYPE_MAP.get(essenceValue.type()) - 4) {
                            return forgeBlockEntity.getItemStackHandler().insertItem(slot + 4, stack, simulate);
                        }
                    } else {
                        if (handler.getStackInSlot(HephaestusForgeBlockEntity.MAIN_SLOT).isEmpty()) {
                            return forgeBlockEntity.getItemStackHandler().insertItem(HephaestusForgeBlockEntity.MAIN_SLOT, stack, simulate);
                        }
                    }
                }
                return stack;
            } else if (slot < size()) {
                PedestalBlockEntity pedestalBlockEntity = pedestalBlockEntities.get(slot - FORGE_SLOTS);
                if (pedestalBlockEntity.getStack().isEmpty()) {
                    if (!simulate) {
                        pedestalBlockEntity.setStack(stack.copyWithCount(1), null, PedestalEffectTrigger.PLAYER_PLACE_ITEM);
                    }
                    ItemStack remain = stack.copy();
                    remain.shrink(1);
                    return remain;
                }
            }
            return stack;
        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (forgeBlockEntity == null) return ItemStack.EMPTY;
            if (slot < FORGE_SLOTS) {
                return forgeBlockEntity.getItemStackHandler().extractItem(slot + 4, amount, simulate);
            } else if (slot < size()) {
                PedestalBlockEntity pedestalBlockEntity = pedestalBlockEntities.get(slot - FORGE_SLOTS);
                if (simulate) {
                    return pedestalBlockEntity.getStack().copy();
                } else {
                    ItemStack stack = pedestalBlockEntity.getStack();
                    pedestalBlockEntity.setStack(ItemStack.EMPTY, null, PedestalEffectTrigger.PLAYER_REMOVE_ITEM);
                    return stack;
                }
            }
            return ItemStack.EMPTY;
        }

        @Override
        public int getSlotLimit(int slot) {
            if (forgeBlockEntity == null) return 0;
            if (slot < FORGE_SLOTS) {
                return forgeBlockEntity.getItemStackHandler().getSlotLimit(slot + 4);
            } else if (slot < size()) {
                return 1;
            }
            return 0;
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if (forgeBlockEntity == null) return false;
            if (slot < FORGE_SLOTS) {
                return forgeBlockEntity.getItemStackHandler().isItemValid(slot + 4, stack);
            } else if (slot < size()) {
                return pedestalBlockEntities.get(slot - FORGE_SLOTS).getStack().isEmpty();
            }
            return false;
        }
    }
}