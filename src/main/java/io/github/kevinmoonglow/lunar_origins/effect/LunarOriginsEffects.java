package io.github.kevinmoonglow.lunar_origins.effect;

import io.github.kevinmoonglow.lunar_origins.LunarOrigins;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class LunarOriginsEffects {
    public static final DeferredRegister<MobEffect> STATUS_EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, LunarOrigins.MOD_ID);

    public static final RegistryObject<MobEffect> CLEAR_VISION = STATUS_EFFECTS.register("clear_vision",
            () -> new LunarOriginsGenericEffect(MobEffectCategory.BENEFICIAL, 0x277FD6));
    public static final RegistryObject<MobEffect> HYDRATION = STATUS_EFFECTS.register("hydration",
            () -> new LunarOriginsGenericEffect(MobEffectCategory.BENEFICIAL, 0x62FFE5));
    public static final RegistryObject<MobEffect> WATER_BLEND = STATUS_EFFECTS.register("water_blending",
            () -> new WaterBlending(MobEffectCategory.BENEFICIAL, 0x62FFE5));
    public static final RegistryObject<MobEffect> AIR_SWIMMING = STATUS_EFFECTS.register("air_swimming",
            () -> new LunarOriginsGenericEffect(MobEffectCategory.BENEFICIAL, 0x5B99F4));

    public static void register(IEventBus eventBus) {
        STATUS_EFFECTS.register(eventBus);
    }
}
