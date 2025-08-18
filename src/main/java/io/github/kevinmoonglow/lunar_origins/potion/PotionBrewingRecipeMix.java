package io.github.kevinmoonglow.lunar_origins.potion;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraftforge.common.brewing.IBrewingRecipe;
import org.jetbrains.annotations.NotNull;

public class PotionBrewingRecipeMix implements IBrewingRecipe {
    @NotNull private final Potion input;
    @NotNull private final Item ingredient;
    @NotNull private final Potion output;

    public PotionBrewingRecipeMix(@NotNull Potion input, @NotNull Item ingredient, @NotNull Potion output) {
        this.input = input;
        this.ingredient = ingredient;
        this.output = output;
    }
    @Override
    public boolean isInput(@NotNull ItemStack stack) {
        if(stack.isEmpty()) return false;
        Item item = stack.getItem();
        Potion stackPotion = PotionUtils.getPotion(stack);
        return item == Items.POTION && stackPotion == this.input;
    }

    @Override
    public boolean isIngredient(ItemStack ingredient) {
        if(ingredient.isEmpty()) return false;
        return ingredient.getItem() == this.ingredient;
    }

    @Override
    public @NotNull ItemStack getOutput(@NotNull ItemStack input, @NotNull ItemStack ingredient) {
        if(!isIngredient(ingredient) || !isInput(input))
            return ItemStack.EMPTY;

        ItemStack result = new ItemStack(Items.POTION);
        result.setCount(1);
        PotionUtils.setPotion(result, this.output);
        return result;
    }
}
