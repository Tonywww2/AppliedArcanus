package com.tonywww.applied_arcanus.events;

import com.tonywww.applied_arcanus.blocks.entity.HephaestusForgeSupplierBlockEntity;
import com.tonywww.applied_arcanus.init.ModBlockEntities;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;

@EventBusSubscriber
public class RegisterCapabilitiesEvent {
    @SubscribeEvent
    public static void registerCapabilities(net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntities.HEPHAESTUS_FORGE_SUPPLIER.get(),
                (blockEntity, direction) -> {
                    if (blockEntity instanceof HephaestusForgeSupplierBlockEntity supplier) {
                        return supplier.getCapability(blockEntity, direction);
                    }
                    return null;
                }
        );
    }
}
