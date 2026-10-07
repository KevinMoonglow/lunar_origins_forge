package io.github.kevinmoonglow.lunar_origins.power.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import net.minecraft.core.Holder;

public record OffsetHitSideActionConfiguration<T>(Holder<T> value, int distance) implements IDynamicFeatureConfiguration {
    public static <T>Codec<OffsetHitSideActionConfiguration<T>> codec(MapCodec<Holder<T>> codec) {
        return RecordCodecBuilder.create(instance -> instance.group(
                codec.forGetter(OffsetHitSideActionConfiguration::value),
                CalioCodecHelper.optionalField(CalioCodecHelper.INT, "distance", 1).forGetter(OffsetHitSideActionConfiguration::distance)
        ).apply(instance, OffsetHitSideActionConfiguration::new));
    }
}
