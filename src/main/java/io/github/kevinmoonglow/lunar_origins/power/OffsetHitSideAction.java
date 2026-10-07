package io.github.kevinmoonglow.lunar_origins.power;

import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBlockAction;
import io.github.edwinmindcraft.apoli.api.power.factory.BlockAction;
import io.github.kevinmoonglow.lunar_origins.power.configuration.OffsetHitSideActionConfiguration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

public class OffsetHitSideAction extends BlockAction<OffsetHitSideActionConfiguration<ConfiguredBlockAction<?, ?>>> {
    public OffsetHitSideAction() {
        super(OffsetHitSideActionConfiguration.codec(ConfiguredBlockAction.required("action")));
    }

    @Override
    public void execute(OffsetHitSideActionConfiguration<ConfiguredBlockAction<?, ?>> configuration, Level world, BlockPos pos, Direction direction) {
        BlockPos newPos = pos.relative(direction, configuration.distance());
        ConfiguredBlockAction.execute(configuration.value(), world, newPos, direction);
    }
}
