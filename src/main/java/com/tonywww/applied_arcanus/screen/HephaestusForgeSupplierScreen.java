package com.tonywww.applied_arcanus.screen;

import appeng.client.gui.implementations.UpgradeableScreen;
import appeng.client.gui.style.StyleManager;
import appeng.client.gui.widgets.ProgressBar;
import appeng.menu.interfaces.IProgressProvider;
import com.tonywww.applied_arcanus.menu.HephaestusForgeSupplierMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class HephaestusForgeSupplierScreen extends UpgradeableScreen<HephaestusForgeSupplierMenu>
{
    ProgressBar aurealBar;
    ProgressBar soulsBar;
    ProgressBar bloodBar;
    ProgressBar experienceBar;

    public HephaestusForgeSupplierScreen(HephaestusForgeSupplierMenu menu, Inventory inv, Component title)
    {
        super(menu, inv, title, StyleManager.loadStyleDoc("/screens/hephaestus_forge_supplier_menu.json"));

        this.aurealBar = new ProgressBar(new IProgressProvider()
        {
            @Override
            public int getCurrentProgress()
            {
                return getMenu().aurealAmount;
            }

            @Override
            public int getMaxProgress()
            {
                return getMenu().aurealCapacity;
            }
        }, style.getImage("aurealBar"), ProgressBar.Direction.VERTICAL);
        widgets.add("aurealBar", this.aurealBar);

        this.soulsBar = new ProgressBar(new IProgressProvider()
        {
            @Override
            public int getCurrentProgress()
            {
                return getMenu().soulsAmount;
            }

            @Override
            public int getMaxProgress()
            {
                return getMenu().soulsCapacity;
            }
        }, style.getImage("soulsBar"), ProgressBar.Direction.VERTICAL);
        widgets.add("soulsBar", this.soulsBar);

        this.bloodBar = new ProgressBar(new IProgressProvider()
        {
            @Override
            public int getCurrentProgress()
            {
                return getMenu().bloodAmount;
            }

            @Override
            public int getMaxProgress()
            {
                return getMenu().bloodCapacity;
            }
        }, style.getImage("bloodBar"), ProgressBar.Direction.VERTICAL);
        widgets.add("bloodBar", this.bloodBar);

        this.experienceBar = new ProgressBar(new IProgressProvider()
        {
            @Override
            public int getCurrentProgress()
            {
                return getMenu().experienceAmount;
            }

            @Override
            public int getMaxProgress()
            {
                return getMenu().experienceCapacity;
            }
        }, style.getImage("experienceBar"), ProgressBar.Direction.VERTICAL);
        widgets.add("experienceBar", this.experienceBar);
    }
}
