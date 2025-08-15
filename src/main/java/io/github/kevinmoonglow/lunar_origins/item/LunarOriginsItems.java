package io.github.kevinmoonglow.lunar_origins.item;

import io.github.kevinmoonglow.lunar_origins.LunarOrigins;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class LunarOriginsItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, LunarOrigins.MOD_ID);

    //public static final RegistryObject<Item> GLASS_BOWL = ITEMS.register("glass_bowl", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> BOWL_SEALANT = ITEMS.register("bowl_sealant", () -> new BowlSealant(new Item.Properties()));
    public static final RegistryObject<Item> BOWL_SUPER_SEALANT = ITEMS.register("bowl_super_sealant", () -> new Item(new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
