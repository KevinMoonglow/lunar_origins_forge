package io.github.kevinmoonglow.lunar_origins.entity;

import io.github.kevinmoonglow.lunar_origins.LunarOrigins;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class LunarOriginsEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, LunarOrigins.MOD_ID);

    public static final RegistryObject<EntityType<GlowItemEntity>> GLOW_ITEM_ENTITY = ENTITIES.register("glow_item_entity", () ->
            EntityType.Builder.<GlowItemEntity>of(GlowItemEntity::new, MobCategory.MISC)
                    .sized(0.25f, 0.25f)
                    .clientTrackingRange(6)
                    .updateInterval(20)
                    .build("glow_item_entity"));


    public static void register(IEventBus eventBus) {
        ENTITIES.register(eventBus);
    }
}
