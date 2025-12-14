package com.tonywww.applied_arcanus;

import appeng.api.upgrades.Upgrades;
import appeng.core.definitions.AEItems;
import com.mojang.logging.LogUtils;
import com.tonywww.applied_arcanus.init.ModBlocks;
import com.tonywww.applied_arcanus.init.ModMenus;
import com.tonywww.applied_arcanus.init.ModRegistryCollector;
import com.tonywww.applied_arcanus.utils.ModEventHandler;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.valhelsia.valhelsia_core.api.common.registry.RegistryManager;
import net.valhelsia.valhelsia_core.core.ModDefinition;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(AppliedArcanus.MODID)
public class AppliedArcanus {

    public static final String MODID = "applied_arcanus";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final RegistryManager REGISTRY_MANAGER = new RegistryManager(new ModRegistryCollector(AppliedArcanus.MODID));

    public AppliedArcanus(IEventBus modEventBus, ModContainer modContainer) {
        ModDefinition.of(AppliedArcanus.MODID)
                .withRegistryManager(REGISTRY_MANAGER)
                .withEventHandler(new ModEventHandler(modEventBus))
                .create();

        modEventBus.addListener(this::commonSetup);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        ModMenus.registerMenus(modEventBus);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        Upgrades.add(AEItems.SPEED_CARD, ModBlocks.HEPHAESTUS_FORGE_SUPPLIER.get(), 4);
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("Applied Arcanus is starting on the server");
    }

    public static ResourceLocation makeId(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}
