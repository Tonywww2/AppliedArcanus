package com.tonywww.applied_arcanus.init;

import com.tonywww.applied_arcanus.AppliedArcanus;
import com.tonywww.applied_arcanus.blocks.HephaestusForgeSupplierBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.valhelsia.valhelsia_core.api.common.registry.RegistryClass;
import net.valhelsia.valhelsia_core.api.common.registry.helper.block.BlockRegistryEntry;
import net.valhelsia.valhelsia_core.api.common.registry.helper.block.BlockRegistryHelper;

public class ModBlocks implements RegistryClass {
    public static final BlockRegistryHelper HELPER = AppliedArcanus.REGISTRY_MANAGER.getBlockHelper();

    public static final BlockRegistryEntry<HephaestusForgeSupplierBlock> HEPHAESTUS_FORGE_SUPPLIER = HELPER.register("hephaestus_forge_supplier",
            () -> new HephaestusForgeSupplierBlock(Block.Properties.ofLegacyCopy(Blocks.STONE)
                    .strength(2.0F, 8.0F)
                    .requiresCorrectToolForDrops()
            ))
            .withItem();


}
