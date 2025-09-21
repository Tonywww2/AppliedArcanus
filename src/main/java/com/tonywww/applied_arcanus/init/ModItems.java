package com.tonywww.applied_arcanus.init;

import com.stal111.forbidden_arcanus.common.block.entity.forge.essence.EssenceType;
import com.stal111.forbidden_arcanus.common.essence.EssenceValue;
import com.stal111.forbidden_arcanus.core.init.ModDataComponents;
import com.tonywww.applied_arcanus.AppliedArcanus;
import net.minecraft.world.item.Item;
import net.valhelsia.valhelsia_core.api.common.registry.RegistryClass;
import net.valhelsia.valhelsia_core.api.common.registry.helper.item.ItemRegistryEntry;
import net.valhelsia.valhelsia_core.api.common.registry.helper.item.ItemRegistryHelper;

public class ModItems implements RegistryClass {
    public static final ItemRegistryHelper HELPER = AppliedArcanus.REGISTRY_MANAGER.getItemHelper();

    public static final ItemRegistryEntry<Item> BLOOD_SOURCE = HELPER.register("blood_source",
            () -> new Item(new Item.Properties().component(ModDataComponents.ESSENCE_VALUE, EssenceValue.of(EssenceType.BLOOD, 500)))
    );

}
