package io.github.kevinmoonglow.lunar_origins.power;

import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBlockAction;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredEntityAction;
import io.github.edwinmindcraft.apoli.api.power.factory.EntityAction;
import io.github.kevinmoonglow.lunar_origins.power.configuration.FixedRayCastConfiguration;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class FixedRayCastAction extends EntityAction<FixedRayCastConfiguration> {
    protected FixedRayCastAction() {
        super(FixedRayCastConfiguration.CODEC);
    }

    @Override
    public void execute(FixedRayCastConfiguration configuration, Entity entity) {
        ConfiguredEntityAction.execute(configuration.beforeAction(), entity);
        Vec3 eye = entity.getEyePosition(1);
        Vec3 direction = entity.getViewVector(1);
        Vec3 target = eye.add(direction.normalize().scale(configuration.distance()));

        BlockPos blockPos = BlockPos.containing(target.x, target.y, target.z);

        ConfiguredBlockAction.execute(configuration.endpointBlockAction(), entity.level(), blockPos, entity.getDirection());
    }
}
