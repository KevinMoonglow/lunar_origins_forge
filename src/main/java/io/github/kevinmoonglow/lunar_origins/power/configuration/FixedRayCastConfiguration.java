package io.github.kevinmoonglow.lunar_origins.power.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBlockAction;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredEntityAction;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import net.minecraft.core.Holder;

public record FixedRayCastConfiguration(
        double distance,
        Holder<ConfiguredBlockAction<?, ?>> endpointBlockAction,
        Holder<ConfiguredEntityAction<?, ?>> beforeAction
) implements IDynamicFeatureConfiguration {
    public static final Codec<FixedRayCastConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            CalioCodecHelper.DOUBLE.fieldOf("distance").forGetter(FixedRayCastConfiguration::distance),
            ConfiguredBlockAction.optional("endpoint_block_action").forGetter(FixedRayCastConfiguration::endpointBlockAction),
            ConfiguredEntityAction.optional("before_action").forGetter(FixedRayCastConfiguration::beforeAction)
    ).apply(instance, FixedRayCastConfiguration::new));
}
