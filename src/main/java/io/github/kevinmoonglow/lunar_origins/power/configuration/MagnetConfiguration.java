package io.github.kevinmoonglow.lunar_origins.power.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredItemCondition;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import net.minecraft.core.Holder;

public record MagnetConfiguration(
        float radius,
        Holder<ConfiguredItemCondition<?, ?>> itemCondition,
        boolean bypassDelay
) implements IDynamicFeatureConfiguration {
    public static final Codec<MagnetConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            CalioCodecHelper.FLOAT.optionalFieldOf("radius", 5.0f).forGetter(MagnetConfiguration::radius),
            ConfiguredItemCondition.optional("item_condition").forGetter(MagnetConfiguration::itemCondition),
            CalioCodecHelper.BOOL.optionalFieldOf("bypass_delay", false).forGetter(MagnetConfiguration::bypassDelay)
    ).apply(instance, MagnetConfiguration::new));
}
