package com.tonywww.applied_arcanus.screen;

import appeng.client.gui.implementations.UpgradeableScreen;
import appeng.client.gui.style.StyleManager;
import com.tonywww.applied_arcanus.menu.HephaestusForgeSupplierMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class HephaestusForgeSupplierScreen extends UpgradeableScreen<HephaestusForgeSupplierMenu>
{
    public HephaestusForgeSupplierScreen(HephaestusForgeSupplierMenu menu, Inventory inv, Component title)
    {
        super(menu, inv, title, StyleManager.loadStyleDoc("TODO"));
    }
}
