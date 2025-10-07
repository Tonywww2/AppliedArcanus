package com.tonywww.applied_arcanus.init;

import com.tonywww.applied_arcanus.AppliedArcanus;
import com.tonywww.applied_arcanus.menu.AdvancedClibanoMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.network.IContainerFactory;
import net.valhelsia.valhelsia_core.api.common.registry.RegistryClass;
import net.valhelsia.valhelsia_core.api.common.registry.RegistryEntry;
import net.valhelsia.valhelsia_core.api.common.registry.helper.MappedRegistryHelper;

public class ModMenuTypes implements RegistryClass {
    public static final MappedRegistryHelper<MenuType<?>> HELPER = AppliedArcanus.REGISTRY_MANAGER.getHelper(Registries.MENU);

    private static <T extends AbstractContainerMenu> RegistryEntry<MenuType<?>, MenuType<T>> register(String name, IContainerFactory<T> factory) {
        return HELPER.register(name, () -> {
            return IMenuTypeExtension.create(factory);
        });
    }

    public static final RegistryEntry<MenuType<?>, MenuType<AdvancedClibanoMenu>> ADVANCED_CLIBANO =
            register("advanced_clibano", AdvancedClibanoMenu::new);

}