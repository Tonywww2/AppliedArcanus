package com.tonywww.applied_arcanus.blocks;

import appeng.block.AEBaseEntityBlock;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocators;
import com.tonywww.applied_arcanus.blocks.entity.HephaestusForgeSupplierBlockEntity;
import com.tonywww.applied_arcanus.init.ModMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

public class HephaestusForgeSupplierBlock extends AEBaseEntityBlock<HephaestusForgeSupplierBlockEntity>
{

    public HephaestusForgeSupplierBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void neighborChanged(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Block neighborBlock, @NotNull BlockPos neighborPos, boolean movedByPiston)
    {
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston);
        level.invalidateCapabilities(pos); // 其实我觉得甚至不需要这样，不过保险起见...
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull BlockHitResult hitResult) {
        super.useWithoutItem(state,level,pos,player,hitResult);
        if(!level.isClientSide()&&!player.isShiftKeyDown()) {
            if(level.getBlockEntity(pos) instanceof HephaestusForgeSupplierBlockEntity be)
                MenuOpener.open(ModMenus.HEPHAESTUS_FORGE_SUPPLIER_MENU.get(), player, MenuLocators.forBlockEntity(be));
        }
        return InteractionResult.SUCCESS_NO_ITEM_USED;
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }
}