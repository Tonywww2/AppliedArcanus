package com.tonywww.applied_arcanus.init;

import com.tonywww.applied_arcanus.AppliedArcanus;
import com.tonywww.applied_arcanus.blocks.entity.HephaestusForgeSupplierBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.valhelsia.valhelsia_core.api.common.registry.RegistryClass;
import net.valhelsia.valhelsia_core.api.common.registry.RegistryEntry;
import net.valhelsia.valhelsia_core.api.common.registry.helper.MappedRegistryHelper;

public class ModBlockEntities implements RegistryClass {
    public static final MappedRegistryHelper<BlockEntityType<?>> HELPER = AppliedArcanus.REGISTRY_MANAGER.getHelper(Registries.BLOCK_ENTITY_TYPE);

    public static final RegistryEntry<BlockEntityType<?>, BlockEntityType<HephaestusForgeSupplierBlockEntity>> HEPHAESTUS_FORGE_SUPPLIER = HELPER.register("hephaestus_forge_supplier",
            () -> BlockEntityType.Builder.of(HephaestusForgeSupplierBlockEntity::new, ModBlocks.HEPHAESTUS_FORGE_SUPPLIER.get()).build(null));


}
