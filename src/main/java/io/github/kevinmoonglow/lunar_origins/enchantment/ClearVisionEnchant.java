package io.github.kevinmoonglow.lunar_origins.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class ClearVisionEnchant extends Enchantment {
    public ClearVisionEnchant(Rarity rarity, EnchantmentCategory category, EquipmentSlot[] slots) {
        super(rarity, category, slots);
    }

    @Override
    public int getMinCost(int pLevel) {
        return 20;
    }

    @Override
    public int getMaxCost(int pLevel) {
        return 50;
    }

    @Override
    public boolean isTreasureOnly() {
        return true;
    }
}
