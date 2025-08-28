package io.github.kevinmoonglow.lunar_origins.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SingleItem {
    private final Item item;
    private final int count;
    private final int maxCount;
    private final int chance;
    private final int chanceRange;
    private final int weight;
    private final List<SingleEnchantment> enchantments;


    public static final Codec<SingleItem> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    ForgeRegistries.ITEMS.getCodec().fieldOf("item").forGetter(m -> m.item),
                    Codec.INT.optionalFieldOf("count", 1).forGetter(m -> m.count),
                    Codec.INT.optionalFieldOf("max_count", 0).forGetter(m -> m.maxCount),
                    Codec.INT.optionalFieldOf("chance", 1).forGetter(m -> m.chance),
                    Codec.INT.optionalFieldOf("chance_range", 1).forGetter(m -> m.chanceRange),
                    Codec.INT.optionalFieldOf("weight", 1).forGetter(m -> m.weight),
                    SingleEnchantment.CODEC.listOf().optionalFieldOf("enchantments", new ArrayList<>()).forGetter(m -> m.enchantments)
            ).apply(instance, SingleItem::new));


    public SingleItem(Item item, int count, int maxCount, int chance, int chanceRange, int weight, List<SingleEnchantment> enchantments) {
        this.item = item;
        this.count = count;
        this.maxCount = maxCount;
        this.chance = chance;
        this.chanceRange = chanceRange;
        this.weight = weight;
        this.enchantments = enchantments;
    }
    @SuppressWarnings("UnusedReturnValue")
    public @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if(chance < chanceRange) {
            if(UniformGenerator.between(1, chanceRange).getInt(context) > chance) {
                return generatedLoot;
            }
        }

        ItemStack newItem = new ItemStack(this.item);

        if(maxCount > count) {
            int amount = UniformGenerator.between(count, maxCount).getInt(context);
            newItem.setCount(amount);
        }
        else newItem.setCount(count);

        enchantments.forEach(x -> x.applyEnchant(newItem));

        generatedLoot.add(newItem);
        return generatedLoot;
    }

    public int getWeight() {
        return weight;
    }
}
