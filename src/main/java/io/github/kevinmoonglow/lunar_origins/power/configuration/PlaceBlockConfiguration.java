package io.github.kevinmoonglow.lunar_origins.power.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBlockAction;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredBlockCondition;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredEntityAction;
import io.github.edwinmindcraft.calio.api.network.CalioCodecHelper;
import net.minecraft.core.Holder;
import net.minecraft.world.level.ClipContext;

public record PlaceBlockConfiguration(
        Holder<ConfiguredBlockAction<?, ?>> hitBlockAction,
        Holder<ConfiguredBlockAction<?, ?>> adjacentBlockAction,
        Holder<ConfiguredBlockAction<?, ?>> missBlockAction,
        Holder<ConfiguredBlockCondition<? ,?>> blockCondition,
        Holder<ConfiguredBlockCondition<? ,?>> hitBlockCondition,
        Holder<ConfiguredBlockCondition<? ,?>> adjacentBlockCondition,
        Holder<ConfiguredBlockCondition<? ,?>> missBlockCondition,
        Holder<ConfiguredEntityAction<?, ?>> successAction,
        Holder<ConfiguredEntityAction<?, ?>> hitAction,
        Holder<ConfiguredEntityAction<?, ?>> missAction,
        Holder<ConfiguredEntityAction<?, ?>> beforeAction,
        ClipContext.Block shapeType,
        ClipContext.Fluid fluidHandling,
        float hitReach,
        float missReach,
        boolean block
) implements IDynamicFeatureConfiguration {
    public static final Codec<PlaceBlockConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ConfiguredBlockAction.optional("hit_block_action").forGetter(PlaceBlockConfiguration::hitBlockAction),
            ConfiguredBlockAction.optional("adjacent_block_action").forGetter(PlaceBlockConfiguration::adjacentBlockAction),
            ConfiguredBlockAction.optional("miss_block_action").forGetter(PlaceBlockConfiguration::missBlockAction),
            ConfiguredBlockCondition.optional("block_condition").forGetter(PlaceBlockConfiguration::blockCondition),
            ConfiguredBlockCondition.optional("hit_block_condition").forGetter(PlaceBlockConfiguration::hitBlockCondition),
            ConfiguredBlockCondition.optional("adjacent_block_condition").forGetter(PlaceBlockConfiguration::adjacentBlockCondition),
            ConfiguredBlockCondition.optional("miss_block_condition").forGetter(PlaceBlockConfiguration::missBlockCondition),
            ConfiguredEntityAction.optional("success_action").forGetter(PlaceBlockConfiguration::successAction),
            ConfiguredEntityAction.optional("hit_action").forGetter(PlaceBlockConfiguration::hitAction),
            ConfiguredEntityAction.optional("miss_action").forGetter(PlaceBlockConfiguration::missAction),
            ConfiguredEntityAction.optional("before_action").forGetter(PlaceBlockConfiguration::beforeAction),
            CalioCodecHelper.optionalField(SerializableDataTypes.SHAPE_TYPE, "shape_type", ClipContext.Block.OUTLINE).forGetter(PlaceBlockConfiguration::shapeType),
            CalioCodecHelper.optionalField(SerializableDataTypes.FLUID_HANDLING, "fluid_handling", ClipContext.Fluid.ANY).forGetter(PlaceBlockConfiguration::fluidHandling),
            CalioCodecHelper.FLOAT.optionalFieldOf("hit_reach", 4.5f).forGetter(PlaceBlockConfiguration::hitReach),
            CalioCodecHelper.FLOAT.optionalFieldOf("miss_reach", 2f).forGetter(PlaceBlockConfiguration::missReach),
            CalioCodecHelper.BOOL.optionalFieldOf("block", true).forGetter(PlaceBlockConfiguration::block)
    ).apply(instance, PlaceBlockConfiguration::new));
}
