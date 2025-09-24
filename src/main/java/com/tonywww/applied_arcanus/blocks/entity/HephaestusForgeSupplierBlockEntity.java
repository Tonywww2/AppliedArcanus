package com.tonywww.applied_arcanus.blocks.entity;

import com.mojang.authlib.GameProfile;
import com.stal111.forbidden_arcanus.common.block.entity.PedestalBlockEntity;
import com.stal111.forbidden_arcanus.common.block.entity.forge.ritual.ActiveRitualData;
import com.stal111.forbidden_arcanus.common.block.entity.forge.ritual.RitualManager;
import com.stal111.forbidden_arcanus.common.block.pedestal.effect.PedestalEffectTrigger;
import com.stal111.forbidden_arcanus.common.essence.EssenceValue;
import com.stal111.forbidden_arcanus.core.init.ModDataComponents;
import com.tonywww.applied_arcanus.init.ModBlockEntities;
import com.stal111.forbidden_arcanus.common.block.HephaestusForgeBlock;
import com.stal111.forbidden_arcanus.common.block.entity.forge.HephaestusForgeBlockEntity;
import com.tonywww.applied_arcanus.mixins.ActiveRitualDataAccessor;
import com.tonywww.applied_arcanus.mixins.RitualManagerAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HephaestusForgeSupplierBlockEntity extends BlockEntity implements WorldlyContainer, ICapabilityProvider<BlockEntity, Direction, IItemHandler> {
    private static final int FORGE_SLOTS = 5;

    public static final int FORGE_MAIN_SLOT = HephaestusForgeBlockEntity.MAIN_SLOT - 4;

    public static final int MAX_UPGRADE_COUNT = 4;

    private HephaestusForgeBlockEntity forgeBlockEntity;
    private List<PedestalBlockEntity> pedestalBlockEntities;
    private IItemHandler proxyItemHandler;

    private FakePlayer fakePlayer;

    public int upgradeCount = 0;

    public HephaestusForgeSupplierBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.HEPHAESTUS_FORGE_SUPPLIER.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, HephaestusForgeSupplierBlockEntity blockEntity) {
        BlockPos below = pos.below();
        BlockState belowState = level.getBlockState(below);
        if (belowState.getBlock() instanceof HephaestusForgeBlock) {
            BlockEntity be = level.getBlockEntity(below);
            if (be instanceof HephaestusForgeBlockEntity forgeEntity) {
                blockEntity.forgeBlockEntity = forgeEntity;
                blockEntity.pedestalBlockEntities = blockEntity.collectPedestals((ServerLevel) level, below);

                if (blockEntity.proxyItemHandler == null) {
                    blockEntity.proxyItemHandler = blockEntity.new ProxyItemHandler();
                }
                if (blockEntity.fakePlayer == null) {
                    GameProfile gameProfile = new GameProfile(UUID.randomUUID(), "HephaestusForgeSupplier");
                    blockEntity.fakePlayer = new FakePlayer((ServerLevel) level, gameProfile);
                    blockEntity.fakePlayer.setPos(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
                }

                forgeEntity.setChanged();
                for (PedestalBlockEntity pedestal : blockEntity.pedestalBlockEntities) {
                    pedestal.setChanged();
                }

                RitualManager ritualManager = blockEntity.forgeBlockEntity.getRitualManager();
                if (ritualManager.getValidRitual().isPresent()) {
                    ActiveRitualData data = ((RitualManagerAccessor) ritualManager).getActiveRitualDataField();
                    if (data == null) {
                        forgeEntity.getRitualManager().startRitual(blockEntity.fakePlayer, blockEntity.forgeBlockEntity.getEssenceManager().getStorage());
                    } else {
                        int counter = data.getCounter();

                        ActiveRitualDataAccessor accessor = (ActiveRitualDataAccessor) data;
                        int toAccelerate  = 8 * blockEntity.upgradeCount;
                        accessor.setCounter(Math.min(data.getRitual().duration(), counter + toAccelerate));
                    }
                }

            } else {
                blockEntity.forgeBlockEntity = null;
                blockEntity.proxyItemHandler = null;
            }
        }
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

    private class ProxyItemHandler implements IItemHandler {
        @Override
        public int getSlots() {
            return 5 + pedestalBlockEntities.size();
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
            } else if (slot < getSlots()) {
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
            } else if (slot < getSlots()) {
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
            } else if (slot < getSlots()) {
                return 1;
            }
            return 0;
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if (forgeBlockEntity == null) return false;
            if (slot < FORGE_SLOTS) {
                return forgeBlockEntity.getItemStackHandler().isItemValid(slot + 4, stack);
            } else if (slot < getSlots()) {
                return pedestalBlockEntities.get(slot - FORGE_SLOTS).getStack().isEmpty();
            }
            return false;
        }
    }

    @Override
    public @Nullable IItemHandler getCapability(BlockEntity blockEntity, Direction context) {
        if (this.proxyItemHandler != null) {
            return this.proxyItemHandler;
        }
        return null;
    }

    @Override
    public int @NotNull [] getSlotsForFace(Direction side) {
        if (forgeBlockEntity == null) {
            return new int[0];
        }
        int totalSlots = FORGE_SLOTS + pedestalBlockEntities.size();
        int[] slots = new int[totalSlots];
        for (int i = 0; i < totalSlots; i++) {
            slots[i] = i;
        }

        return slots;
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack itemStack, @Nullable Direction direction) {
        if (forgeBlockEntity == null) return false;
        if (index >= 1 && index <= 4 && itemStack.getComponents().has(ModDataComponents.ESSENCE_VALUE.get())) {
            EssenceValue essenceValue = itemStack.getComponents().get(ModDataComponents.ESSENCE_VALUE.get());
            if (essenceValue != null) {
                int forgeSlot = HephaestusForgeBlockEntity.SLOT_FROM_ESSENCE_TYPE_MAP.get(essenceValue.type());
                // 检查目标槽位是否为空
                return index == (forgeSlot - 4) && forgeBlockEntity.getItemStackHandler().getStackInSlot(forgeSlot).isEmpty();
            }
        }
        if (index == 0 && forgeBlockEntity.getItemStackHandler().getStackInSlot(4).isEmpty()) {
            return true;
        }
        if (index >= FORGE_SLOTS && index < FORGE_SLOTS + pedestalBlockEntities.size()) {
            PedestalBlockEntity pedestalBlockEntity = pedestalBlockEntities.get(index - FORGE_SLOTS);
            return pedestalBlockEntity.getStack().isEmpty();
        }
        return false;
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
        return direction == Direction.UP && index < FORGE_SLOTS + pedestalBlockEntities.size();
    }

    @Override
    public int getContainerSize() {
        return forgeBlockEntity == null ?
                0 :
                (FORGE_SLOTS + pedestalBlockEntities.size());
    }

    @Override
    public boolean isEmpty() {
        if (forgeBlockEntity != null) {
            for (int i = 4; i < 9; i++) {
                if (!forgeBlockEntity.getItemStackHandler().getStackInSlot(i).isEmpty()) {
                    return false;
                }
            }

            for (PedestalBlockEntity pedestal : pedestalBlockEntities) {
                if (!pedestal.getStack().isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int i) {
        return proxyItemHandler != null ? proxyItemHandler.getStackInSlot(i) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        return proxyItemHandler != null ? proxyItemHandler.extractItem(slot, amount, false) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        if (proxyItemHandler == null) return ItemStack.EMPTY;
        ItemStack stack = proxyItemHandler.extractItem(slot, 1, false);
        return stack;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (proxyItemHandler != null) {
            proxyItemHandler.insertItem(slot, stack, false);
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clearContent() {
    }
}