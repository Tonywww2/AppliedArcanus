package com.tonywww.applied_arcanus;

import com.tonywww.applied_arcanus.utils.ModEventHandler;
import com.tonywww.applied_arcanus.init.ModRegistryCollector;
import net.minecraft.resources.ResourceLocation;
import net.valhelsia.valhelsia_core.api.common.registry.RegistryManager;
import net.valhelsia.valhelsia_core.core.ModDefinition;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

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
    }

    private void commonSetup(FMLCommonSetupEvent event) {
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("Applied Arcanus is starting on the server");
    }

    public static ResourceLocation makeId(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}
