package io.github.kevinmoonglow.lunar_origins.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.NotNull;

public class KelpBedItem extends BlockItem {
    public KelpBedItem(Block pBlock, Properties pProperties) {
        super(pBlock, pProperties);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext pContext) {
        BlockPos pos = pContext.getClickedPos();
        Direction clickedFace = pContext.getClickedFace();
        BlockPos adjacent = pos.relative(clickedFace);
        BlockState newPosState = pContext.getLevel().getBlockState(adjacent);
        FluidState fluidState = newPosState.getFluidState();
        Fluid fluidType = fluidState.getType();

        if(fluidType != Fluids.WATER) {
            return InteractionResult.FAIL;
        }

        return super.useOn(pContext);
    }

    @Override
    protected boolean placeBlock(BlockPlaceContext pContext, BlockState pState) {
        return pContext.getLevel().setBlock(pContext.getClickedPos(), pState, 26);
    }
}
