package io.github.kevinmoonglow.lunar_origins.loot;


import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class AddItemModifier extends LootModifier {
    public static final Supplier<Codec<AddItemModifier>> CODEC = Suppliers.memoize(
            () -> RecordCodecBuilder.create(inst -> codecStart(inst)
                    .and(inst.group(
                            ForgeRegistries.ITEMS.getCodec().fieldOf("item").forGetter(m -> m.item),
                            Codec.INT.optionalFieldOf("count", 1).forGetter(m -> m.count),
                            Codec.INT.optionalFieldOf("max_count", 0).forGetter(m -> m.maxCount),
                            Codec.INT.optionalFieldOf("chance", 1).forGetter(m -> m.chance),
                            Codec.INT.optionalFieldOf("chance_range", 1).forGetter(m -> m.chanceRange),
                            Codec.INT.optionalFieldOf("weight", 1).forGetter(m -> m.weight)
                    )).apply(inst, AddItemModifier::new)
            ));
    private final Item item;
    private final int count;
    private final int maxCount;
    private final int chance;
    private final int chanceRange;
    private final int weight;

    public AddItemModifier(LootItemCondition[] conditionsIn, Item item, int count, int maxCount, int chance, int chanceRange, int weight) {
        super(conditionsIn);
        this.item = item;
        this.count = count;
        this.maxCount = maxCount;
        this.chance = chance;
        this.chanceRange = chanceRange;
        this.weight = weight;
    }

    @Override
    public @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        for(LootItemCondition condition : this.conditions) {
            if(!condition.test(context)) {
                return generatedLoot;
            }
        }
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

        generatedLoot.add(newItem);
        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }

    public int getWeight() {
        return weight;
    }
}
