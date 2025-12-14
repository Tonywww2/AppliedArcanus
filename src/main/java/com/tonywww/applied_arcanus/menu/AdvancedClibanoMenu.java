package com.tonywww.applied_arcanus.menu;

import appeng.menu.SlotSemantics;
import appeng.menu.guisync.GuiSync;
import appeng.menu.implementations.UpgradeableMenu;
import appeng.menu.slot.AppEngSlot;
import appeng.util.inv.AppEngInternalInventory;
import com.tonywww.applied_arcanus.blocks.entity.AdvancedClibanoBlockEntity;
import com.tonywww.applied_arcanus.init.ModMenus;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class AdvancedClibanoMenu extends UpgradeableMenu<AdvancedClibanoBlockEntity> {

    @GuiSync(10)
    public int cookingProgress;

    @GuiSync(11)
    public int cookingTime;

    public AdvancedClibanoMenu(int id, Inventory playerInv, @NotNull AdvancedClibanoBlockEntity host)
    {
        super(ModMenus.ADVANCED_CLIBANO_MENU.get(), id, playerInv, host);
    }

    // 放除了升级槽之外的其他真实库存
    // 注：玩家槽位已经由UpgradeableMenu处理，不必再写
    @Override
    protected void setupInventorySlots()
    {
        // 机器槽位
        AppEngInternalInventory inventory = getHost().getInventory();
        this.addSlot(new AppEngSlot(inventory, 0), SlotSemantics.MACHINE_INPUT);
        this.addSlot(new AppEngSlot(inventory, 1), SlotSemantics.MACHINE_INPUT);

        this.addSlot(new AppEngSlot(inventory, 2), SlotSemantics.STORAGE);
        this.addSlot(new AppEngSlot(inventory, 3){
            @Override
            public boolean mayPlace(ItemStack stack)
            {
                return super.mayPlace(stack) && false;
            }
        }, SlotSemantics.MACHINE_OUTPUT);
    }

    @Override
    public void broadcastChanges()
    {
        cookingProgress = getHost().getCookingProgress();
        cookingTime = getHost().getCookingDuration();

        super.broadcastChanges();
    }

    @Override
    public boolean stillValid(@NotNull Player player)
    {
        return !getHost().isRemoved();
    }
}