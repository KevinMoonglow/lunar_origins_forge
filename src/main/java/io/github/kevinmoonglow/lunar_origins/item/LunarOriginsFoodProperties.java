package io.github.kevinmoonglow.lunar_origins.item;

import io.github.kevinmoonglow.lunar_origins.effect.LunarOriginsEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;

public class LunarOriginsFoodProperties {
    public static final FoodProperties KELP_CARROT = new FoodProperties.Builder()
            .nutrition(4)
            .saturationMod(0.9f)
            .effect(() -> new MobEffectInstance(LunarOriginsEffects.CLEAR_VISION.get(), 1200, 0, false, false, true), 1.0f)
            .build();
    public static final FoodProperties AQUA_GUMMY = new FoodProperties.Builder()
            .alwaysEat()
            .fast()
            .effect(() -> new MobEffectInstance(LunarOriginsEffects.HYDRATION.get(), 600), 1.0f)
            .nutrition(1)
            .saturationMod(0)
            .build();
    public static final FoodProperties GLIMMERING_AQUA_GUMMY = new FoodProperties.Builder()
            .alwaysEat()
            .fast()
            .effect(() -> new MobEffectInstance(LunarOriginsEffects.HYDRATION.get(), 1200), 1.0f)
            .nutrition(1)
            .saturationMod(0)
            .build();

}
