package io.github.kevinmoonglow.lunar_origins.block;

import io.github.edwinmindcraft.apoli.api.component.IPowerContainer;
import io.github.kevinmoonglow.lunar_origins.LunarOrigins;
import io.github.kevinmoonglow.lunar_origins.power.LunarOriginsPowers;
import io.github.kevinmoonglow.lunar_origins.power.configuration.ModifyBreathingConfiguration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class KelpBedBlock extends HorizontalDirectionalBlock implements LiquidBlockContainer {

    public static final VoxelShape SHAPE = Block.box(0d, 0d, 0d, 16d, 4d, 16d);
    public static final EnumProperty<BedPart> PART = BlockStateProperties.BED_PART;
    public static final BooleanProperty OCCUPIED = BlockStateProperties.OCCUPIED;


    public KelpBedBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any().setValue(PART, BedPart.FOOT).setValue(OCCUPIED, Boolean.FALSE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING, PART, OCCUPIED);
    }

    @Nullable
    public static Direction getBedOrientation(BlockGetter level, BlockPos pos) {
        BlockState blockState = level.getBlockState(pos);
        return blockState.getBlock() instanceof KelpBedBlock ? blockState.getValue(FACING) : null;
    }

    @Override
    public boolean canPlaceLiquid(BlockGetter pLevel, BlockPos pPos, BlockState pState, Fluid pFluid) {
        return false;
    }

    @Override
    public boolean placeLiquid(LevelAccessor pLevel, BlockPos pPos, BlockState pState, FluidState pFluidState) {
        return false;
    }


    @SuppressWarnings("deprecation")
    @Override
    public @NotNull FluidState getFluidState(BlockState pState) {
        return Fluids.WATER.getSource(false);
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pPos, BlockPos pNeighborPos) {
        if (pDirection == getNeighborDirection(pState.getValue(PART), pState.getValue(FACING))) {
            if (pNeighborState.is(this) && pNeighborState.getValue(PART) != pState.getValue(PART))
                return pState.setValue(OCCUPIED, pNeighborState.getValue(OCCUPIED));
            else
                return Blocks.AIR.defaultBlockState();
        } else {
            return super.updateShape(pState, pDirection, pNeighborState, pLevel, pPos, pNeighborPos);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (pLevel.isClientSide) {
            return InteractionResult.CONSUME;
        }
        if (pState.getValue(PART) != BedPart.HEAD) {
            pPos = pPos.relative(pState.getValue(FACING));
            pState = pLevel.getBlockState(pPos);
            if (!pState.is(this)) return InteractionResult.CONSUME;
        }
        if (!BedBlock.canSetSpawn(pLevel)) {
            pPlayer.displayClientMessage(Component.translatable("block.lunar_origins.bed.bad_place"), true);
            return InteractionResult.SUCCESS;
        }
        if (pState.getValue(OCCUPIED)) {
            pPlayer.displayClientMessage(Component.translatable("block.minecraft.bed.occupied"), true);
            return InteractionResult.SUCCESS;
        }
        LazyOptional<IPowerContainer> powerOptional = IPowerContainer.get(pPlayer);
        boolean waterBreather = false;
        boolean comfortable = false;

        if(powerOptional.isPresent()) {
            IPowerContainer powers = powerOptional.resolve().orElseThrow();
            if(IPowerContainer.hasPower(pPlayer, LunarOriginsPowers.MODIFY_BREATHING.get())) {
                List<ModifyBreathingConfiguration> breathingPowers = IPowerContainer.getPowers(pPlayer, LunarOriginsPowers.MODIFY_BREATHING.get())
                        .stream().map(x -> (ModifyBreathingConfiguration)x.get().getConfiguration())
                        .toList();
                Optional<ModifyBreathingConfiguration> prioritized = breathingPowers.stream().max(Comparator.comparingInt(ModifyBreathingConfiguration::priority));
                if(prioritized.isPresent()) {
                    ModifyBreathingConfiguration power = prioritized.orElseThrow();

                    BlockPos finalPPos = pPos;
                    boolean isBreathableBlock = power.blockCondition().get().check(pLevel, finalPPos, () -> pLevel.getBlockState(finalPPos));
                    boolean isBreathableEffect = power.breathingStatusEffects().getContent().stream().anyMatch(pPlayer::hasEffect);

                    waterBreather = isBreathableBlock || isBreathableEffect;
                }
            }
            else if(pPlayer.hasEffect(MobEffects.WATER_BREATHING)) {
                waterBreather = true;
            }
            else if(powers.hasPower(ResourceLocation.fromNamespaceAndPath(LunarOrigins.MOD_ID, "water_breather")))
                waterBreather = true;
            else if(powers.hasPower(ResourceLocation.fromNamespaceAndPath("origins", "water_breathing")))
                waterBreather = true;

            if(powers.hasPower(ResourceLocation.fromNamespaceAndPath(LunarOrigins.MOD_ID, "sleeps_underwater"))) {
                comfortable = true;
            }
        }
        else if(pPlayer.hasEffect(MobEffects.WATER_BREATHING)) {
            waterBreather = true;
        }

        if(!waterBreather && !comfortable) {
            pPlayer.displayClientMessage(Component.translatable("block.lunar_origins.bed.underwater"), true);
            return InteractionResult.SUCCESS;
        }

        pPlayer.startSleepInBed(pPos).ifLeft(x -> {
            if (x.getMessage() != null) {
                pPlayer.displayClientMessage(x.getMessage(), true);
            }
        });
        return InteractionResult.SUCCESS;
    }

    private static Direction getNeighborDirection(BedPart part, Direction direction) {
        if (part == BedPart.FOOT) return direction;
        return direction.getOpposite();
    }

    @Override
    public void playerWillDestroy(Level pLevel, BlockPos pPos, BlockState pState, Player pPlayer) {

        super.playerWillDestroy(pLevel, pPos, pState, pPlayer);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext pContext) {
        Direction direction = pContext.getHorizontalDirection();
        BlockPos clickedPos = pContext.getClickedPos();
        BlockPos otherPos = clickedPos.relative(direction);
        Level level = pContext.getLevel();
        if (level.getBlockState(otherPos).canBeReplaced(pContext) && level.getWorldBorder().isWithinBounds(otherPos)) {
            return this.defaultBlockState().setValue(FACING, direction);
        } else return null;
    }

    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
        if (!pLevel.isClientSide) {
            BlockPos blockPos = pPos.relative(pState.getValue(FACING));
            pLevel.setBlock(blockPos, pState.setValue(PART, BedPart.HEAD), 3);
            pLevel.blockUpdated(pPos, Blocks.AIR);
            pState.updateIndirectNeighbourShapes(pLevel, pPos, 3);
        }
    }

    @Override
    public boolean isBed(BlockState state, BlockGetter level, BlockPos pos, @Nullable Entity player) {
        return true;
    }

    @Override
    protected boolean isAir(BlockState state) {
        return false;
    }
}
