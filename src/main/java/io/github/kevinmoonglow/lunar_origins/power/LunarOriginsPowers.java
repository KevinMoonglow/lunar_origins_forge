package io.github.kevinmoonglow.lunar_origins.power;

import io.github.edwinmindcraft.apoli.api.component.IPowerContainer;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredModifier;
import io.github.edwinmindcraft.apoli.api.power.factory.BlockAction;
import io.github.edwinmindcraft.apoli.api.power.factory.EntityAction;
import io.github.edwinmindcraft.apoli.api.power.factory.PowerFactory;
import io.github.edwinmindcraft.apoli.api.registry.ApoliRegistries;
import io.github.kevinmoonglow.lunar_origins.LunarOrigins;
import io.github.kevinmoonglow.lunar_origins.item.GlassBowl;
import io.github.kevinmoonglow.lunar_origins.power.configuration.ModifyBreathingConfiguration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingBreatheEvent;
import net.minecraftforge.event.entity.living.LivingDrownEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class LunarOriginsPowers {
    public static final DeferredRegister<PowerFactory<?>> POWERS =
            DeferredRegister.create(ApoliRegistries.POWER_FACTORY_KEY, LunarOrigins.MOD_ID);
    public static final DeferredRegister<BlockAction<?>> BLOCK_ACTIONS =
            DeferredRegister.create(ApoliRegistries.BLOCK_ACTION_KEY, LunarOrigins.MOD_ID);
    public static final DeferredRegister<EntityAction<?>> ENTITY_ACTIONS =
            DeferredRegister.create(ApoliRegistries.ENTITY_ACTION_KEY, LunarOrigins.MOD_ID);

    public static final RegistryObject<PowerFactory<?>> MODIFY_BREATHING = POWERS.register("modify_breathing",
            ModifyBreathingPower::new);
    public static final RegistryObject<PowerFactory<?>> CRAWLING = POWERS.register("crawling", CrawlingPower::new);
    public static final RegistryObject<PowerFactory<?>> MAGNET_POWER = POWERS.register("magnet", MagnetPower::new);

    public static final RegistryObject<BlockAction<?>> OFFSET_HIT_SIDE = BLOCK_ACTIONS.register("offset_by_direction", OffsetHitSideAction::new);
    public static final RegistryObject<EntityAction<?>> FIXED_RAYCAST_ACTION = ENTITY_ACTIONS.register("fixed_raycast", FixedRayCastAction::new);
    public static final RegistryObject<EntityAction<?>> PLACE_BLOCK = ENTITY_ACTIONS.register("place_block", PlaceBlockAction::new);
    public static final RegistryObject<EntityAction<?>> MAGNET_ACTION = ENTITY_ACTIONS.register("magnet", MagnetAction::new);

    public static void register(IEventBus eventBus) {
        POWERS.register(eventBus);
        BLOCK_ACTIONS.register(eventBus);
        ENTITY_ACTIONS.register(eventBus);
    }

    @SubscribeEvent
    public static void onEntityTick(TickEvent.PlayerTickEvent event) {
        if(event.phase == TickEvent.Phase.END) {
            LivingEntity entity = event.player;
            boolean hasPower = IPowerContainer.hasPower(entity, LunarOriginsPowers.CRAWLING.get());
            if (hasPower && !entity.isSwimming())
                entity.setPose(Pose.SWIMMING);
        }
    }

}
