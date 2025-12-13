package com.tonywww.applied_arcanus;

import com.tonywww.applied_arcanus.init.ModMenuTypes;
import com.tonywww.applied_arcanus.screen.AdvancedClibanoScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = AppliedArcanus.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = AppliedArcanus.MODID, value = Dist.CLIENT)
public class AppliedArcanusClient {
    public AppliedArcanusClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenuTypes.ADVANCED_CLIBANO.get(), AdvancedClibanoScreen::new);
    }
}
