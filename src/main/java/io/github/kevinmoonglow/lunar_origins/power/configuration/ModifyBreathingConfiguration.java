package io.github.kevinmoonglow.lunar_origins.power.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.data.DamageSourceDescription;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.configuration.ListConfiguration;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBlockCondition;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredModifier;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.effect.MobEffect;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record ModifyBreathingConfiguration(
        Holder<ConfiguredBlockCondition<?, ?>> blockCondition,
        ListConfiguration<MobEffect> breathingStatusEffects,
        ListConfiguration<ConfiguredModifier<?>> gainAirModifiers,
        int gainAirInterval,
        ListConfiguration<ConfiguredModifier<?>> loseAirModifiers,
        int loseAirInterval,
        @Nullable DamageSourceDescription damageSource,
        ListConfiguration<ConfiguredModifier<?>> damageModifiers,
        int damageInterval,
        @Nullable ParticleOptions particleEffect,
        boolean ignoreRespiration,
        int priority) implements IDynamicFeatureConfiguration {
    public static final Codec<ModifyBreathingConfiguration> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            ConfiguredBlockCondition.required("breathable_block_condition").forGetter(ModifyBreathingConfiguration::blockCondition),
            ListConfiguration.mapCodec(SerializableDataTypes.STATUS_EFFECT, "breathing_status_effect", "breathing_status_effects").forGetter(ModifyBreathingConfiguration::breathingStatusEffects),
            ListConfiguration.mapCodec(ConfiguredModifier.CODEC, "gain_air_modifier", "gain_air_modifiers").forGetter(ModifyBreathingConfiguration::gainAirModifiers),
            CalioCodecHelper.optionalField(CalioCodecHelper.INT, "gain_air_interval", 1).forGetter(ModifyBreathingConfiguration::gainAirInterval),
            ListConfiguration.mapCodec(ConfiguredModifier.CODEC, "lose_air_modifier", "lose_air_modifiers").forGetter(ModifyBreathingConfiguration::loseAirModifiers),
            CalioCodecHelper.optionalField(CalioCodecHelper.INT, "lose_air_interval", 1).forGetter(ModifyBreathingConfiguration::loseAirInterval),
            CalioCodecHelper.optionalField(ApoliDataTypes.DAMAGE_SOURCE_DESCRIPTION, "damage_source").forGetter((x) -> Optional.ofNullable(x.damageSource())),



            ListConfiguration.mapCodec(ConfiguredModifier.CODEC, "damage_modifier", "damage_modifiers").forGetter(ModifyBreathingConfiguration::damageModifiers),
            CalioCodecHelper.optionalField(CalioCodecHelper.INT, "damage_interval", 20).forGetter(ModifyBreathingConfiguration::damageInterval),
            CalioCodecHelper.optionalField(SerializableDataTypes.PARTICLE_EFFECT_OR_TYPE, "particle").forGetter((x) -> Optional.ofNullable(x.particleEffect())),
            CalioCodecHelper.optionalField(CalioCodecHelper.BOOL, "ignore_respiration", false).forGetter(ModifyBreathingConfiguration::ignoreRespiration),

            CalioCodecHelper.optionalField(CalioCodecHelper.INT, "priority", 0).forGetter(ModifyBreathingConfiguration::priority)
    ).apply(instance, (bc, bse, gam, gai, lam, lai, ds, dm, di, pe, ir, p) ->
            new ModifyBreathingConfiguration(bc, bse, gam, gai, lam, lai, ds.orElse(null), dm, di, pe.orElse(null), ir, p)));


}
