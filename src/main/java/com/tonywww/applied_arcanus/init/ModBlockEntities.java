package com.tonywww.applied_arcanus.init;

import appeng.block.AEBaseEntityBlock;
import appeng.blockentity.AEBaseBlockEntity;
import appeng.blockentity.ClientTickingBlockEntity;
import appeng.blockentity.ServerTickingBlockEntity;
import com.google.common.base.Preconditions;
import com.tonywww.applied_arcanus.AppliedArcanus;
import com.tonywww.applied_arcanus.blocks.entity.AdvancedClibanoBlockEntity;
import com.tonywww.applied_arcanus.blocks.entity.HephaestusForgeSupplierBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.valhelsia.valhelsia_core.api.common.registry.RegistryClass;
import net.valhelsia.valhelsia_core.api.common.registry.RegistryEntry;
import net.valhelsia.valhelsia_core.api.common.registry.helper.MappedRegistryHelper;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class ModBlockEntities implements RegistryClass {
    public static final MappedRegistryHelper<BlockEntityType<?>> HELPER = AppliedArcanus.REGISTRY_MANAGER.getHelper(Registries.BLOCK_ENTITY_TYPE);

    public static final RegistryEntry<BlockEntityType<?>, BlockEntityType<HephaestusForgeSupplierBlockEntity>> HEPHAESTUS_FORGE_SUPPLIER = createAe(
            "hephaestus_forge_supplier",
            HephaestusForgeSupplierBlockEntity.class,
            HephaestusForgeSupplierBlockEntity::new,
            ModBlocks.HEPHAESTUS_FORGE_SUPPLIER
    );

    public static final RegistryEntry<BlockEntityType<?>, BlockEntityType<AdvancedClibanoBlockEntity>> ADVANCED_CLIBANO = createAe(
            "advanced_clibano",
            AdvancedClibanoBlockEntity.class,
            AdvancedClibanoBlockEntity::new,
            ModBlocks.ADVANCED_CLIBANO
    );


    @SafeVarargs
    private static <T extends AEBaseBlockEntity> RegistryEntry<BlockEntityType<?>, BlockEntityType<T>> createAe(
            String id,
            Class<T> entityClass,
            BlockEntityFactory<T> factory,
            Supplier<? extends AEBaseEntityBlock<?>>... blockSuppliers
    ) {
        Preconditions.checkArgument(blockSuppliers.length > 0, "At least one block is required");

        return HELPER.register(id, () -> {
            AtomicReference<BlockEntityType<T>> typeRef = new AtomicReference<>();

            BlockEntityType.BlockEntitySupplier<T> supplier = (pos, state) ->
                    factory.create(typeRef.get(), pos, state);

            AEBaseEntityBlock<?>[] blocks = Arrays.stream(blockSuppliers)
                    .map(Supplier::get)
                    .toArray(AEBaseEntityBlock[]::new);

            Block[] vanillaBlocks = Arrays.stream(blocks).toArray(Block[]::new);

            BlockEntityType<T> type = BlockEntityType.Builder.of(supplier, vanillaBlocks).build(null);
            typeRef.set(type);

            // AE：绑定“物品形态”
            try {
                AEBaseBlockEntity.registerBlockEntityItem(type, blocks[0].asItem());
            } catch (Throwable ignored) {}

            // AE：自动 ticker
            BlockEntityTicker<T> serverTicker = null;
            if (ServerTickingBlockEntity.class.isAssignableFrom(entityClass)) {
                serverTicker = (lvl, p, st, be) -> ((ServerTickingBlockEntity) be).serverTick();
            }
            BlockEntityTicker<T> clientTicker = null;
            if (ClientTickingBlockEntity.class.isAssignableFrom(entityClass)) {
                clientTicker = (lvl, p, st, be) -> ((ClientTickingBlockEntity) be).clientTick();
            }

            // AE：把 type+ticker+class 绑定到每个方块
            for (var b : blocks) {
                @SuppressWarnings("unchecked")
                AEBaseEntityBlock<T> base = (AEBaseEntityBlock<T>) b;
                base.setBlockEntity(entityClass, type, clientTicker, serverTicker);
            }

            return type;
        });
    }

    @FunctionalInterface
    public interface BlockEntityFactory<T extends AEBaseBlockEntity> {
        T create(BlockEntityType<T> type, BlockPos pos, BlockState state);
    }

}
