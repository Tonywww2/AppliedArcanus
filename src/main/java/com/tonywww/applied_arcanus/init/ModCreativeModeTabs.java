package com.tonywww.applied_arcanus.init;

import com.tonywww.applied_arcanus.AppliedArcanus;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.valhelsia.valhelsia_core.api.common.item.tab.CreativeTabFactory;
import net.valhelsia.valhelsia_core.api.common.registry.RegistryClass;
import net.valhelsia.valhelsia_core.api.common.registry.RegistryEntry;
import net.valhelsia.valhelsia_core.api.common.registry.helper.MappedRegistryHelper;

public class ModCreativeModeTabs implements RegistryClass {
    public static final MappedRegistryHelper<CreativeModeTab> HELPER = AppliedArcanus.REGISTRY_MANAGER.getHelper(Registries.CREATIVE_MODE_TAB);


    public static final RegistryEntry<CreativeModeTab, CreativeModeTab> MAIN = HELPER.register("main", CreativeTabFactory.create(builder -> {
                builder.icon(() -> new ItemStack(ModItems.BLOOD_SOURCE.get()))
                        .title(Component.translatable("itemGroup.applied_arcanus.main"))
                        .displayItems((itemDisplayParameters, output) -> {
                            output.accept(ModItems.BLOOD_SOURCE.get());

                            output.accept(ModBlocks.HEPHAESTUS_FORGE_SUPPLIER.get());
                        });
            })
    );

}
