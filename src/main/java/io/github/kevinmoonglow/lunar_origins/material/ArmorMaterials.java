package io.github.kevinmoonglow.lunar_origins.material;

import io.github.kevinmoonglow.lunar_origins.LunarOrigins;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.Tags;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
public enum ArmorMaterials implements ArmorMaterial {
    GLASSES("luna_glasses", 4, 25, SoundEvents.ARMOR_EQUIP_GOLD,
            0f, 0f, () -> Ingredient.of(Tags.Items.GLASS),
            new int[]{13, 15, 16, 11}, new int[]{1, 2, 3, 1}),
    GOGGLES("goggles", 1, 15, SoundEvents.ARMOR_EQUIP_GENERIC,
            0, 0, () -> Ingredient.of(Tags.Items.GLASS),
            new int[]{60, 60, 60, 60}, new int[]{1, 1, 1, 1}),
    GLASS_BOWL("fish_bowl", 1, 15, SoundEvents.ARMOR_EQUIP_DIAMOND,
            0, 0, () -> Ingredient.of(Tags.Items.GLASS),
            new int[]{6, 7, 8, 2}, new int[]{1, 2, 3, 1}),
    AMETHYST_GLASS_BOWL("amethyst_fish_bowl", 1, 30, SoundEvents.ARMOR_EQUIP_DIAMOND,
            0, 0, () -> Ingredient.of(Items.AMETHYST_SHARD),
            new int[]{6, 7, 8, 10}, new int[]{1, 2, 3, 1}),
    COPPER_DIVING_HELMET("diving", 15, 12, SoundEvents.ARMOR_EQUIP_IRON,
            0, 0, () -> Ingredient.of(Items.COPPER_INGOT),
            new int[]{13, 15, 16, 11}, new int[]{1, 4, 5, 2}),
    ;


    private final String name;
    private final int durabilityMultiplier;
    private final int enchantmentValue;
    private final SoundEvent equipSound;
    private final float toughness;
    private final float knockbackResistance;
    private final Supplier<Ingredient> repairIngredient;


    private final int[] baseDurability;
    private final int[] protectionValues;

    ArmorMaterials(String name, int durabilityMultiplier, int enchantmentValue, SoundEvent equipSound, float toughness, float knockbackResistance, Supplier<Ingredient> repairIngredient, int[] baseDurability, int[] protectionValues) {
        this.name = name;
        this.durabilityMultiplier = durabilityMultiplier;
        this.enchantmentValue = enchantmentValue;
        this.equipSound = equipSound;
        this.toughness = toughness;
        this.knockbackResistance = knockbackResistance;
        this.repairIngredient = repairIngredient;
        this.baseDurability = baseDurability;
        this.protectionValues = protectionValues;
    }

    @Override
    public int getDurabilityForType(ArmorItem.Type pType) {
        return baseDurability[pType.getSlot().getIndex()] * durabilityMultiplier;
    }

    @Override
    public int getDefenseForType(ArmorItem.Type pType) {
        return protectionValues[pType.getSlot().getIndex()];
    }

    @Override
    public int getEnchantmentValue() {
        return enchantmentValue;
    }

    @Override
    public @NotNull SoundEvent getEquipSound() {
        return equipSound;
    }

    @Override
    public @NotNull Ingredient getRepairIngredient() {
        return repairIngredient.get();
    }

    @Override
    public @NotNull String getName() {
        return LunarOrigins.MOD_ID + ":" + name;
    }

    @Override
    public float getToughness() {
        return toughness;
    }

    @Override
    public float getKnockbackResistance() {
        return knockbackResistance;
    }
}
