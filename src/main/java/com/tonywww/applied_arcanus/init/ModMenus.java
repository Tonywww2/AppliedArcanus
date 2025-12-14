package com.tonywww.applied_arcanus.init;

import appeng.menu.implementations.MenuTypeBuilder;
import com.tonywww.applied_arcanus.AppliedArcanus;
import com.tonywww.applied_arcanus.blocks.entity.AdvancedClibanoBlockEntity;
import com.tonywww.applied_arcanus.blocks.entity.HephaestusForgeSupplierBlockEntity;
import com.tonywww.applied_arcanus.menu.AdvancedClibanoMenu;
import com.tonywww.applied_arcanus.menu.HephaestusForgeSupplierMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModMenus
{
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(Registries.MENU, AppliedArcanus.MODID);


    public static final Supplier<MenuType<HephaestusForgeSupplierMenu>> HEPHAESTUS_FORGE_SUPPLIER_MENU = MENU_TYPES.register("hephaestus_forge_supplier_menu",
            () -> MenuTypeBuilder.create(HephaestusForgeSupplierMenu::new, HephaestusForgeSupplierBlockEntity.class)
                    .build(AppliedArcanus.makeId("hephaestus_forge_supplier_menu"))
    );

    public static final Supplier<MenuType<AdvancedClibanoMenu>> ADVANCED_CLIBANO_MENU = MENU_TYPES.register("advanced_clibano_menu",
            () -> MenuTypeBuilder.create(AdvancedClibanoMenu::new, AdvancedClibanoBlockEntity.class)
                    .build(AppliedArcanus.makeId("advanced_clibano_menu"))
    );

    public static void registerMenus(IEventBus eventBus)
    {
        MENU_TYPES.register(eventBus);
    }
}
