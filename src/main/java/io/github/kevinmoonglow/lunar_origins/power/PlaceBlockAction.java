package io.github.kevinmoonglow.lunar_origins.power;

import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBlockAction;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBlockCondition;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredEntityAction;
import io.github.edwinmindcraft.apoli.api.power.factory.EntityAction;
import io.github.kevinmoonglow.lunar_origins.power.configuration.PlaceBlockConfiguration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class PlaceBlockAction extends EntityAction<PlaceBlockConfiguration> {
    protected PlaceBlockAction() {
        super(PlaceBlockConfiguration.CODEC);
    }

    @Override
    public void execute(PlaceBlockConfiguration configuration, Entity entity) {
        ConfiguredEntityAction.execute(configuration.beforeAction(), entity);

        Level level = entity.level();

        Vec3 eye = entity.getEyePosition(1);
        Vec3 direction = entity.getViewVector(1);
        Vec3 hitTarget = eye.add(direction.normalize().scale(configuration.hitReach()));


        ClipContext context = new ClipContext(eye, hitTarget, configuration.shapeType(), configuration.fluidHandling(), entity);
        BlockHitResult hit = level.clip(context);

        if(configuration.block() && hit.getType() != HitResult.Type.MISS) {
            BlockPos hitPos = hit.getBlockPos();
            Direction hitDirection = hit.getDirection();
            BlockPos adjacentPos = hit.getBlockPos().relative(hitDirection);

            boolean hitCheck = ConfiguredBlockCondition.check(configuration.hitBlockCondition(), level, hitPos);
            boolean adjacentCheck = ConfiguredBlockCondition.check(configuration.adjacentBlockCondition(), level, adjacentPos);
            boolean generalCheck = ConfiguredBlockCondition.check(configuration.blockCondition(), level, adjacentPos);

            if(hitCheck && adjacentCheck && generalCheck) {
                ConfiguredBlockAction.execute(configuration.hitBlockAction(), level, hitPos, hitDirection);
                ConfiguredBlockAction.execute(configuration.adjacentBlockAction(), level, adjacentPos, hitDirection);
                ConfiguredEntityAction.execute(configuration.hitAction(), entity);
                ConfiguredEntityAction.execute(configuration.successAction(), entity);
            }
        }
        else {
            Vec3 missTarget = eye.add(direction.normalize().scale(configuration.missReach()));

            BlockPos missBlockPos = BlockPos.containing(missTarget.x, missTarget.y, missTarget.z);

            boolean missCheck = ConfiguredBlockCondition.check(configuration.missBlockCondition(), level, missBlockPos);
            boolean generalCheck = ConfiguredBlockCondition.check(configuration.blockCondition(), level, missBlockPos);

            if (missCheck && generalCheck) {
                ConfiguredBlockAction.execute(configuration.missBlockAction(), level, missBlockPos, entity.getDirection());
                ConfiguredEntityAction.execute(configuration.missAction(), entity);
                ConfiguredEntityAction.execute(configuration.successAction(), entity);
            }
        }
    }
}
