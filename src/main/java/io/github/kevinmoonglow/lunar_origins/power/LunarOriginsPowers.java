package io.github.kevinmoonglow.lunar_origins.power;

import io.github.edwinmindcraft.apoli.api.component.IPowerContainer;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredModifier;
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

    public static final RegistryObject<PowerFactory<?>> MODIFY_BREATHING = POWERS.register("modify_breathing",
            ModifyBreathingPower::new);
    public static final RegistryObject<PowerFactory<?>> CRAWLING = POWERS.register("crawling", CrawlingPower::new);


    public static void register(IEventBus eventBus) {
        POWERS.register(eventBus);
    }

    @SubscribeEvent
    public static void onBreathingEvent(LivingBreatheEvent event) {
        LivingEntity livingEntity = event.getEntity();
        Level world = livingEntity.level();
        BlockPos eyePos = BlockPos.containing(livingEntity.getEyePosition());
        ItemStack stack = livingEntity.getItemBySlot(EquipmentSlot.HEAD);
        Item helmItem = stack.getItem();

        boolean hasVanillaWaterBreathing = livingEntity.hasEffect(MobEffects.WATER_BREATHING);
        boolean originsWaterBreathing = false; // The standard origins Water Breathing power (AKA Gills)
        boolean waterBreather = false; // LunarOrigins defined power representing water-breathing species
        boolean isWaterBreathing; // Do they breathe water according to Origins or LunarOrigins

        LazyOptional<IPowerContainer> entityPowers = IPowerContainer.get(livingEntity);
        if(entityPowers.isPresent()) {
            IPowerContainer powers = entityPowers.resolve().orElseThrow();
            originsWaterBreathing = powers.hasPower(
                    ResourceLocation.fromNamespaceAndPath("origins", "water_breathing"));
            waterBreather = powers.hasPower(
                    ResourceLocation.fromNamespaceAndPath(LunarOrigins.MOD_ID, "water_breather"));
        }
        isWaterBreathing = originsWaterBreathing || waterBreather;
        enum BowlState {
            NONE,
            AIR,
            WATER,
        }

        BowlState bowlState = BowlState.NONE;

        if(helmItem instanceof GlassBowl) {
            CompoundTag nbt = stack.getTag();
            if(nbt != null) {
                if(isWaterBreathing) {
                    // Determines the effective water bowl state for a water breather. This is forgiving and lets you
                    // breathe whenever there's water in the bowl. Even if it's below eye level.
                    long waterLevel = nbt.getLong("waterLevel");
                    if(waterLevel >= GlassBowl.BREATHE_LEVEL)
                        bowlState = BowlState.WATER;
                    else
                        bowlState = BowlState.AIR;
                }
                else {
                    // Determines effective water bowl state for an air-breather. This version is based on eye level,
                    // so you can lift your head into the air bubble to take a breath.
                    boolean waterEyeLevel = nbt.getBoolean("waterEyeLevel");
                    if(waterEyeLevel)
                        bowlState = BowlState.WATER;
                    else
                        bowlState = BowlState.AIR;
                }
            }
        }


        List<ModifyBreathingConfiguration> powers = IPowerContainer.getPowers(event.getEntity(), LunarOriginsPowers.MODIFY_BREATHING.get())
                .stream().map(x -> (ModifyBreathingConfiguration)x.get().getConfiguration())
                .toList();

        Optional<ModifyBreathingConfiguration> prioritized = powers.stream().max(Comparator.comparingInt(ModifyBreathingConfiguration::priority));
        if(prioritized.isPresent()) {
            ModifyBreathingConfiguration power = prioritized.orElseThrow();

            boolean isBreathableBlock = power.blockCondition().get().check(world, eyePos, () -> world.getBlockState(eyePos));
            boolean isBreathableEffect = power.breathingStatusEffects().getContent().stream().anyMatch(livingEntity::hasEffect);

            boolean canBreathe = isBreathableBlock || isBreathableEffect;
            boolean canRefillAir = isBreathableBlock;

            // Special handling for glass bowl helmets which uses the water in the bowl rather than the
            // breathing block state.
            // TODO: Maybe we could update this to be more configurable in the datapack?
            if(bowlState != BowlState.NONE) {
                canRefillAir =
                        (bowlState == BowlState.AIR && !isWaterBreathing)
                        || (bowlState == BowlState.WATER && isWaterBreathing);
                canBreathe = canRefillAir || isBreathableEffect;
            }

            int loseInterval = power.loseAirInterval();
            int gainInterval = power.gainAirInterval();

            // Maybe not the best way to do this, but since respiration is factored into the base consume amount
            // beforehand, the only other way would be to override LivingEntity.decreaseAirSupply()
            double baseLoseAmount = power.ignoreRespiration() ? 1 : event.getConsumeAirAmount();
            double baseGainAmount = event.getRefillAirAmount();
            double loseAmount = baseLoseAmount;
            double gainAmount = baseGainAmount;

            if(loseInterval <= 0) loseInterval = 1;
            if(gainInterval <= 0) gainInterval = 1;

            event.setCanBreathe(canBreathe);
            if(canBreathe) {
                for (ConfiguredModifier<?> m : power.gainAirModifiers().getContent()) {
                    gainAmount = m.apply(livingEntity, baseGainAmount, gainAmount);
                }
                double refill = livingEntity.tickCount % gainInterval == 0 ? gainAmount : 0;
                event.setRefillAirAmount((int)refill);
                event.setCanRefillAir(canRefillAir);
            }
            else {
                for (ConfiguredModifier<?> m : power.loseAirModifiers().getContent()) {
                    loseAmount = m.apply(livingEntity, baseLoseAmount, loseAmount);
                }
                double consume = livingEntity.tickCount % loseInterval == 0 ? loseAmount : 0;
                event.setConsumeAirAmount((int) consume);
            }
        }
        else {
            switch(bowlState) {
                case WATER -> {
                    if(isWaterBreathing || hasVanillaWaterBreathing) {
                        // Origins water breathing has some hardcoded behavior, so we'll have to grant the potion effect
                        // if the Origin in use is relying on that power.
                        if(originsWaterBreathing) {
                            livingEntity.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 100, 0, false, false, false));
                        }
                        else {
                            event.setCanBreathe(true);
                            event.setCanRefillAir(true);
                        }
                    }
                    else {
                        event.setCanBreathe(false);
                    }
                }
                case AIR -> {
                    // Origins water breathing has hardcoded behavior, so we'll just do nothing when that power is
                    // active.
                    if(!originsWaterBreathing) {
                        if (isWaterBreathing && !hasVanillaWaterBreathing) {
                            event.setCanBreathe(false);
                        } else {
                            event.setCanBreathe(true);
                            event.setCanRefillAir(true);
                        }
                    }
                }
            }
        }
    }
    @SubscribeEvent
    public static void onDrowning(LivingDrownEvent event) {
        List<ModifyBreathingConfiguration> powers = IPowerContainer.getPowers(event.getEntity(), LunarOriginsPowers.MODIFY_BREATHING.get())
                .stream().map(x -> (ModifyBreathingConfiguration)x.get().getConfiguration())
                .toList();

        Optional<ModifyBreathingConfiguration> prioritized = powers.stream().max(Comparator.comparingInt(ModifyBreathingConfiguration::priority));
        if (prioritized.isPresent()) {
            LivingEntity player = event.getEntity();

            ModifyBreathingConfiguration power = prioritized.orElseThrow();

            int interval = power.damageInterval() * power.loseAirInterval();

            if(interval <= 0) interval = 20;



            boolean respirationSave = false;
            if(player.tickCount % interval == 0) {
                int RespirationLevel = EnchantmentHelper.getRespiration(player);
                respirationSave = RespirationLevel > 0 && player.getRandom().nextInt(RespirationLevel + 1) > 0;
            }

            if(player.tickCount % interval == 0 && !respirationSave) {
                player.setAirSupply(0);
                float baseDamage = event.getDamageAmount();
                double damage = baseDamage;

                for(ConfiguredModifier<?> m: power.damageModifiers().getContent()) {
                    damage = m.apply(player, baseDamage, damage);
                }

                if(power.damageSource() != null || power.particleEffect() != null) {
                    event.setDamageAmount(0);
                    event.setDrowning(true);
                    DamageSource d = power.damageSource() != null ? power.damageSource().create(player.damageSources()) : player.damageSources().drown();
                    ParticleOptions particleOptions = power.particleEffect();
                    if(particleOptions == null) particleOptions = ParticleTypes.BUBBLE;

                    player.hurt(d, (float)damage);
                    Vec3 movement = player.getDeltaMovement();

                    for (int i = 0; i < event.getBubbleCount(); ++i)
                    {
                        double addX = player.getRandom().nextDouble() - player.getRandom().nextDouble();
                        double addY = player.getRandom().nextDouble() - player.getRandom().nextDouble();
                        double addZ = player.getRandom().nextDouble() - player.getRandom().nextDouble();
                        player.level().addParticle(particleOptions, player.getX() + addX, player.getY() + addY, player.getZ() + addZ, movement.x, movement.y, movement.z);
                    }
                }
                else {
                    event.setDamageAmount((float)damage);
                    event.setDrowning(true);
                }
            }
            else
                event.setDrowning(false);

        }

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
