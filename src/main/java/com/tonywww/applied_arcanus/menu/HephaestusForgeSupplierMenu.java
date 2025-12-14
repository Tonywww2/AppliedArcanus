package com.tonywww.applied_arcanus.menu;

import appeng.api.inventories.InternalInventory;
import appeng.api.util.IConfigManager;
import appeng.menu.SlotSemantics;
import appeng.menu.guisync.GuiSync;
import appeng.menu.implementations.UpgradeableMenu;
import appeng.menu.slot.AppEngSlot;
import appeng.util.inv.AppEngInternalInventory;
import com.tonywww.applied_arcanus.blocks.entity.HephaestusForgeSupplierBlockEntity;
import com.tonywww.applied_arcanus.init.ModMenus;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class HephaestusForgeSupplierMenu extends UpgradeableMenu<HephaestusForgeSupplierBlockEntity>
{
    @GuiSync(10)
    public int aurealAmount;
    @GuiSync(11)
    public int aurealCapacity;
    @GuiSync(12)
    public int soulsAmount;
    @GuiSync(13)
    public int soulsCapacity;
    @GuiSync(14)
    public int bloodAmount;
    @GuiSync(15)
    public int bloodCapacity;
    @GuiSync(16)
    public int experienceAmount;
    @GuiSync(17)
    public int experienceCapacity;


    public HephaestusForgeSupplierMenu(int id, Inventory playerInv, @NotNull HephaestusForgeSupplierBlockEntity host)
    {
        super(ModMenus.HEPHAESTUS_FORGE_SUPPLIER_MENU.get(), id, playerInv, host);
    }

    // 放除了升级槽之外的其他真实库存
    // 注：玩家槽位已经由UpgradeableMenu处理，不必再写
    @Override
    protected void setupInventorySlots()
    {
        // 0-4为锻炉槽位
        // 5-12为周围祭坛
        InternalInventory proxyInv;
        if(isServerSide())
        {
            proxyInv = getHost().getProxyItemHandler();
        }
        else
        {
            // 客户端使用空容器等待同步
            proxyInv = new AppEngInternalInventory(13);
        }

        AppEngSlot mainSlot = new AppEngSlot(proxyInv, 0);
        AppEngSlot aurealSlot = new AppEngSlot(proxyInv, 1);
        AppEngSlot soulsSlot = new AppEngSlot(proxyInv, 2);
        AppEngSlot bloodSlot = new AppEngSlot(proxyInv, 3);
        AppEngSlot experienceSlot = new AppEngSlot(proxyInv, 4);

        List<AppEngSlot> leftSlots = new ArrayList<>();
        List<AppEngSlot> rightSlots = new ArrayList<>();
        for(int i = 5; i < 5 + 4; i++)
        {
            AppEngSlot slot = new AppEngSlot(proxyInv, i);
            leftSlots.add(slot);
        }
        for(int i = 9; i < 9 + 4; i++)
        {
            AppEngSlot slot = new AppEngSlot(proxyInv, i);
            rightSlots.add(slot);
        }
        mainSlot.x = 80; mainSlot.y = 24;
        aurealSlot.x = 10; aurealSlot.y = 25;
        soulsSlot.x = 10; soulsSlot.y = 43;
        bloodSlot.x = 150; bloodSlot.y = 25;
        experienceSlot.x = 150; experienceSlot.y = 43;

        for (int i = 0; i < leftSlots.size(); i++) {
            int row = i / 2;
            int col = i % 2;
            AppEngSlot slot = leftSlots.get(i);
            slot.x = 26 + col * 18;
            slot.y = 73 + row * 18;
        }
        for (int i = 0; i < rightSlots.size(); i++) {
            int row = i / 2;
            int col = i % 2;
            AppEngSlot slot = rightSlots.get(i);
            slot.x = 116 + col * 18;
            slot.y = 73 + row * 18;
        }

        addSlot(mainSlot, SlotSemantics.MACHINE_OUTPUT);
        addSlot(aurealSlot, SlotSemantics.MACHINE_INPUT);
        addSlot(soulsSlot, SlotSemantics.MACHINE_INPUT);
        addSlot(bloodSlot, SlotSemantics.MACHINE_INPUT);
        addSlot(experienceSlot, SlotSemantics.MACHINE_INPUT);
        for(AppEngSlot slot : leftSlots)
        {
            addSlot(slot, SlotSemantics.MACHINE_INPUT);
        }
        for(AppEngSlot slot : rightSlots)
        {
            addSlot(slot, SlotSemantics.MACHINE_INPUT);
        }

    }

    @Override
    public void broadcastChanges()
    {
        aurealAmount = getHost().getAurealAmount();
        aurealCapacity = getHost().getAurealCapacity();
        soulsAmount = getHost().getSoulsAmount();
        soulsCapacity = getHost().getSoulsCapacity();
        bloodAmount = getHost().getBloodAmount();
        bloodCapacity = getHost().getBloodCapacity();
        experienceAmount = getHost().getExperienceAmount();
        experienceCapacity = getHost().getExperienceCapacity();

        super.broadcastChanges();
    }

    @Override
    protected void loadSettingsFromHost(IConfigManager cm)
    {
    }

    @Override
    public boolean stillValid(@NotNull Player player)
    {
        return !getHost().isRemoved();
    }
}
