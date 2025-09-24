package com.tonywww.applied_arcanus.blocks;

import appeng.core.definitions.AEItems;
import com.mojang.serialization.MapCodec;
import com.tonywww.applied_arcanus.blocks.entity.HephaestusForgeSupplierBlockEntity;
import com.tonywww.applied_arcanus.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HephaestusForgeSupplierBlock extends BaseEntityBlock {
    public static final MapCodec<HephaestusForgeSupplierBlock> CODEC = simpleCodec(HephaestusForgeSupplierBlock::new);

    public HephaestusForgeSupplierBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {

        if (level instanceof ServerLevel serverLevel) {
            if (stack.is(AEItems.SPEED_CARD.asItem())) {
                BlockEntity blockEntity = serverLevel.getBlockEntity(pos);
                if (blockEntity instanceof  HephaestusForgeSupplierBlockEntity hephaestusForgeSupplierBlockEntity) {
                    int toInsert = HephaestusForgeSupplierBlockEntity.MAX_UPGRADE_COUNT - hephaestusForgeSupplierBlockEntity.upgradeCount;
                    hephaestusForgeSupplierBlockEntity.upgradeCount += toInsert;
                    stack.shrink(toInsert);
                }
            }
        }

        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        if (state.hasBlockEntity()) {
            BlockEntity blockEntity = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
            if (blockEntity instanceof HephaestusForgeSupplierBlockEntity hephaestusForgeSupplierBlockEntity) {
                ItemStack stackMain = new ItemStack(this);
                ItemStack stackUpgrade = new ItemStack(AEItems.SPEED_CARD, hephaestusForgeSupplierBlockEntity.upgradeCount);

                return List.of(stackMain, stackUpgrade);
            }
        }
        return super.getDrops(state, builder);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new HephaestusForgeSupplierBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level, BlockState state, BlockEntityType<T> type
    ) {
        return level.isClientSide ?
                null :
                createTickerHelper(type, ModBlockEntities.HEPHAESTUS_FORGE_SUPPLIER.get(), HephaestusForgeSupplierBlockEntity::serverTick);
    }
}