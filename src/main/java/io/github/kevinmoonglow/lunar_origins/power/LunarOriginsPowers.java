package io.github.kevinmoonglow.lunar_origins.power;

import io.github.edwinmindcraft.apoli.api.power.factory.PowerFactory;
import io.github.edwinmindcraft.apoli.api.registry.ApoliRegistries;
import io.github.kevinmoonglow.lunar_origins.LunarOrigins;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class LunarOriginsPowers {
    public static final DeferredRegister<PowerFactory<?>> POWERS =
            DeferredRegister.create(ApoliRegistries.POWER_FACTORY_KEY, LunarOrigins.MOD_ID);

    public static final RegistryObject<PowerFactory<?>> MODIFY_BREATHING = POWERS.register("modify_breathing",
            ModifyBreathingPower::new);


    public static void register(IEventBus eventBus) {
        POWERS.register(eventBus);
    }
}
