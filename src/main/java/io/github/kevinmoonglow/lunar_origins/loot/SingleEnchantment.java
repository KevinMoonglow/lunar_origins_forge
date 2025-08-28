package io.github.kevinmoonglow.lunar_origins.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraftforge.registries.ForgeRegistries;

public class SingleEnchantment {
    private final Enchantment enchantment;
    private final int level;

    public static final Codec<SingleEnchantment> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    ForgeRegistries.ENCHANTMENTS.getCodec().fieldOf("enchantment").forGetter(m -> m.enchantment),
                    Codec.INT.optionalFieldOf("level", 1).forGetter(m -> m.level)
            ).apply(instance, SingleEnchantment::new));
    public SingleEnchantment(Enchantment enchantment, int level) {
        this.enchantment = enchantment;
        this.level = level;
    }
    void applyEnchant(ItemStack itemStack) {
        if(enchantment != null) {
            if(itemStack.getItem() instanceof EnchantedBookItem) {
                EnchantedBookItem.addEnchantment(itemStack, new EnchantmentInstance(enchantment, level));
            }
            else {
                itemStack.enchant(enchantment, level);
            }
        }
    }
}
