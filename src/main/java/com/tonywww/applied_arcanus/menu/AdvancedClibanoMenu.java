package com.tonywww.applied_arcanus.menu;

import com.tonywww.applied_arcanus.blocks.entity.AdvancedClibanoBlockEntity;
import com.tonywww.applied_arcanus.init.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class AdvancedClibanoMenu extends AbstractContainerMenu {
    private final AdvancedClibanoBlockEntity blockEntity;
    private final ContainerData data;

    // 客户端构造方法
    public AdvancedClibanoMenu(int containerId, Inventory playerInventory, FriendlyByteBuf extraData) {
        this(containerId, playerInventory, (AdvancedClibanoBlockEntity) playerInventory.player.level()
                .getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(2));
    }

    // 服务端构造方法
    public AdvancedClibanoMenu(int containerId, Inventory playerInventory, AdvancedClibanoBlockEntity blockEntity, ContainerData data) {
        super(ModMenuTypes.ADVANCED_CLIBANO.get(), containerId);
        this.blockEntity = blockEntity;
        this.data = data;

        // 机器槽位
        this.addSlot(new SlotItemHandler(blockEntity.getItemHandler(), 0, 40, 35));  // First
        this.addSlot(new SlotItemHandler(blockEntity.getItemHandler(), 1, 58, 35));  // Second

        this.addSlot(new SlotItemHandler(blockEntity.getItemHandler(), 2, 16, 35));  // Enhancer
        this.addSlot(new SlotItemHandler(blockEntity.getItemHandler(), 3, 116, 35) { // Output
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return false;
            }
        });

        // 玩家背包
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        // 快捷栏
        for (int k = 0; k < 9; k++) {
            this.addSlot(new Slot(playerInventory, k, 8 + k * 18, 142));
        }

        this.addDataSlots(data);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            itemstack = stackInSlot.copy();

            if (index == 3) { // 输出槽
                if (!this.moveItemStackTo(stackInSlot, 4, 40, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(stackInSlot, itemstack);
            } else if (index >= 4) { // 玩家背包
                if (!this.moveItemStackTo(stackInSlot, 0, 3, false)) {
                    if (index < 31) {
                        if (!this.moveItemStackTo(stackInSlot, 31, 40, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (!this.moveItemStackTo(stackInSlot, 4, 31, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            } else if (!this.moveItemStackTo(stackInSlot, 4, 40, false)) {
                return ItemStack.EMPTY;
            }

            if (stackInSlot.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (stackInSlot.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, stackInSlot);
        }

        return itemstack;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return this.blockEntity.getLevel() != null &&
                this.blockEntity.getLevel().getBlockEntity(this.blockEntity.getBlockPos()) == this.blockEntity &&
                player.distanceToSqr(this.blockEntity.getBlockPos().getX() + 0.5,
                        this.blockEntity.getBlockPos().getY() + 0.5,
                        this.blockEntity.getBlockPos().getZ() + 0.5) <= 64;
    }

    public int getCookingProgress() {
        return this.data.get(0);
    }

    public int getCookingTime() {
        return this.data.get(1);
    }
}