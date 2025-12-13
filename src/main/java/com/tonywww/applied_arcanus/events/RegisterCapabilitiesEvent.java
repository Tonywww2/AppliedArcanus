package com.tonywww.applied_arcanus.events;

import com.tonywww.applied_arcanus.blocks.entity.HephaestusForgeSupplierBlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber
public class RegisterCapabilitiesEvent {
    @SubscribeEvent
    public static void registerCapabilities(net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent event) {
        HephaestusForgeSupplierBlockEntity.onRegisterCaps(event);
    }
}
