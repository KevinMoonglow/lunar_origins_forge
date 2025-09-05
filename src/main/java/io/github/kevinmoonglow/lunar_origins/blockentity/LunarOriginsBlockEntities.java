package io.github.kevinmoonglow.lunar_origins.blockentity;

import io.github.kevinmoonglow.lunar_origins.LunarOrigins;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class LunarOriginsBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, LunarOrigins.MOD_ID);

    //public static final RegistryObject<BlockEntityType<KelpBedEntity>> KELP_BED = BLOCK_ENTITIES.register("kelp_bed",
    //        () -> BlockEntityType.Builder.of(KelpBedEntity::new, LunarOriginsBlocks.KELP_BED_BLOCK.get()).build(null));


    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}