package io.github.kevinmoonglow.lunar_origins.power;

import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredItemCondition;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.api.power.factory.PowerFactory;
import io.github.kevinmoonglow.lunar_origins.power.configuration.MagnetConfiguration;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class MagnetPower extends PowerFactory<MagnetConfiguration> {
    public MagnetPower() {
        super(MagnetConfiguration.CODEC);
        this.ticking(false);
    }

    @Override
    public void tick(ConfiguredPower<MagnetConfiguration, ?> configuration, Entity entity) {
        if(entity instanceof Player player) {
            MagnetConfiguration config = configuration.getConfiguration();
            MagnetAction.doMagnet(player, config.radius(), config.bypassDelay(),
                    stack -> ConfiguredItemCondition.check(config.itemCondition(), player.level(), stack));
        }
    }
}
