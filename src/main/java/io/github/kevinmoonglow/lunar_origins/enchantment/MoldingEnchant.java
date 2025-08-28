package io.github.kevinmoonglow.lunar_origins.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class MoldingEnchant extends Enchantment {
    public MoldingEnchant(Rarity rarity, EnchantmentCategory category, EquipmentSlot[] slots) {
        super(rarity, category, slots);
    }

    @Override
    public boolean isTreasureOnly() {
        return true;
    }
}
