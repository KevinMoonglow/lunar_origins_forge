package io.github.kevinmoonglow.lunar_origins.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public class WaterBlending extends MobEffect {
    public WaterBlending(MobEffectCategory category, int color) {
        super(category, color);
    }
    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % 20 == 0;
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if(entity.isInWater()) {
            entity.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 40, 0, true, false));
        }
        else {
            entity.removeEffect(LunarOriginsEffects.WATER_BLEND.get());
        }
    }

}
