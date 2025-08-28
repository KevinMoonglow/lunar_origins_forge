package io.github.kevinmoonglow.lunar_origins.loot;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class AddPotionModifier extends LootModifier {
    public static final Supplier<Codec<AddPotionModifier>> CODEC = Suppliers.memoize(
            () -> RecordCodecBuilder.create(instance -> codecStart(instance)
                    .and(instance.group(
                            ForgeRegistries.POTIONS.getCodec().fieldOf("potion").forGetter(m -> m.potionType),
                            Codec.INT.optionalFieldOf("count", 1).forGetter(m -> m.count),
                            Codec.INT.optionalFieldOf("max_count", 0).forGetter(m -> m.maxCount),
                            Codec.INT.optionalFieldOf("chance", 1).forGetter(m -> m.chance),
                            Codec.INT.optionalFieldOf("chance_range", 1).forGetter(m -> m.chanceRange)
                    )).apply(instance, AddPotionModifier::new)

    ));
    private final Potion potionType;
    private final int count;
    private final int maxCount;
    private final int chance;
    private final int chanceRange;

    protected AddPotionModifier(LootItemCondition[] conditionsIn, Potion potion, int count, int max_count, int chance, int chance_range) {
        super(conditionsIn);
        this.potionType = potion;
        this.count = count;
        this.maxCount = max_count;
        this.chance = chance;
        this.chanceRange = chance_range;
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
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

        ItemStack newPotion = new ItemStack(Items.POTION);
        PotionUtils.setPotion(newPotion, potionType);

        int finalCount;
        if(maxCount > count) {
            finalCount = UniformGenerator.between(count, maxCount).getInt(context);
        }
        else finalCount = count;
        if(finalCount > 0) generatedLoot.add(newPotion);
        for (int i = 2; i <= finalCount ; i++) {
            generatedLoot.add(newPotion.copy());
        }
        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}
