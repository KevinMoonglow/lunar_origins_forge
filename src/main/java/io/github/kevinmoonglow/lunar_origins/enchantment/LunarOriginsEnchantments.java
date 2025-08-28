package io.github.kevinmoonglow.lunar_origins.enchantment;

import io.github.kevinmoonglow.lunar_origins.LunarOrigins;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class LunarOriginsEnchantments {
    public static final DeferredRegister<Enchantment> ENCHANTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, LunarOrigins.MOD_ID);

    public static final RegistryObject<Enchantment> CLEAR_VISION = ENCHANTS.register("clear_vision",
            () -> new ClearVisionEnchant(Enchantment.Rarity.RARE, EnchantmentCategory.ARMOR_HEAD,
                    new EquipmentSlot[]{EquipmentSlot.HEAD}));
    public static final RegistryObject<Enchantment> MOLDING = ENCHANTS.register("molding",
            () -> new MoldingEnchant(Enchantment.Rarity.UNCOMMON, EnchantmentCategory.ARMOR,
                    new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}));


    public static void register(IEventBus eventBus) {
        ENCHANTS.register(eventBus);
    }
}
