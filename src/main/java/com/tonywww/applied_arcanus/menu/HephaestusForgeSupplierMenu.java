package com.tonywww.applied_arcanus.menu;

import appeng.menu.implementations.UpgradeableMenu;
import com.tonywww.applied_arcanus.blocks.entity.HephaestusForgeSupplierBlockEntity;
import com.tonywww.applied_arcanus.init.ModMenus;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class HephaestusForgeSupplierMenu extends UpgradeableMenu<HephaestusForgeSupplierBlockEntity>
{

    public HephaestusForgeSupplierMenu(int id, Inventory playerInv, @NotNull HephaestusForgeSupplierBlockEntity host)
    {
        super(ModMenus.HEPHAESTUS_FORGE_SUPPLIER_MENU.get(), id, playerInv, host);
    }

    // 放除了升级槽之外的其他真实库存
    // 注：玩家槽位已经由UpgradeableMenu处理，不必再写
    @Override
    protected void setupInventorySlots()
    {
        // TODO 添加slot后，手动指定槽位位置
        // 0-4为锻炉槽位，放在中间
        // 5-12为周围祭坛，放到外围围一圈
    }

    @Override
    public boolean stillValid(@NotNull Player player)
    {
        return !getHost().isRemoved();
    }
}
