package io.github.kevinmoonglow.lunar_origins.loot;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Supplier;

public class AddItemChoiceModifier extends LootModifier {

    public static final Supplier<Codec<AddItemChoiceModifier>> CODEC = Suppliers.memoize(
            () -> RecordCodecBuilder.create(instance -> codecStart(instance)
                    .and(instance.group(
                            SingleItem.CODEC.listOf().fieldOf("items").forGetter(m -> m.items),
                            Codec.INT.optionalFieldOf("rolls", 1).forGetter(m -> m.rolls),
                            Codec.INT.optionalFieldOf("max_rolls", 0).forGetter(m -> m.maxRolls)
                    )).apply(instance, AddItemChoiceModifier::new))
    );
    private final List<SingleItem> items;
    private final int rolls;
    private final int maxRolls;

    public AddItemChoiceModifier(LootItemCondition[] conditionsIn, List<SingleItem> items, int rolls, int maxRolls) {
        super(conditionsIn);
        this.items = items;
        this.rolls = rolls;
        this.maxRolls = maxRolls;
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        for(LootItemCondition condition : this.conditions) {
            if(!condition.test(context)) {
                return generatedLoot;
            }
        }


        int rollCount = rolls;
        if(maxRolls > rolls)
            rollCount = UniformGenerator.between(rolls, maxRolls).getInt(context);
        for (int i = 0; i < rollCount; i++) {
            int weightTotal = items.stream().mapToInt(SingleItem::getWeight).sum();
            int randomValue = UniformGenerator.between(1, weightTotal).getInt(context);
            for (SingleItem m : items) {
                randomValue -= m.getWeight();
                if (randomValue <= 0) {
                    m.doApply(generatedLoot, context);
                    break;
                }
            }
        }
        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}
