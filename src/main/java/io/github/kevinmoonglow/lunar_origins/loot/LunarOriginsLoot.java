package io.github.kevinmoonglow.lunar_origins.loot;

import com.mojang.serialization.Codec;
import io.github.kevinmoonglow.lunar_origins.LunarOrigins;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class LunarOriginsLoot {
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIER =
            DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, LunarOrigins.MOD_ID);

    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> ADD_ITEM =
            LOOT_MODIFIER.register("add_item", AddItemModifier.CODEC);
    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> ADD_ITEM_CHOICE =
            LOOT_MODIFIER.register("add_item_choice", AddItemChoiceModifier.CODEC);
    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> ADD_POTION =
            LOOT_MODIFIER.register("add_potion", AddPotionModifier.CODEC);
    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> ADD_ENCHANTED_BOOK =
            LOOT_MODIFIER.register("add_enchanted_book", AddEnchantedBookModifier.CODEC);

    public static void register(IEventBus eventBus) {
        LOOT_MODIFIER.register(eventBus);
    }
}
