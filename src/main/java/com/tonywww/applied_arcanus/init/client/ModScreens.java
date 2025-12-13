package com.tonywww.applied_arcanus.init.client;

import com.tonywww.applied_arcanus.AppliedArcanus;
import com.tonywww.applied_arcanus.init.ModMenus;
import com.tonywww.applied_arcanus.screen.HephaestusForgeSupplierScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = AppliedArcanus.MODID, value = Dist.CLIENT)
public class ModScreens
{
    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event)
    {
        event.register(ModMenus.HEPHAESTUS_FORGE_SUPPLIER_MENU.get(), HephaestusForgeSupplierScreen::new);
    }
}
