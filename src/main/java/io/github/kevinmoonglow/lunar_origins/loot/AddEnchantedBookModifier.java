package io.github.kevinmoonglow.lunar_origins.loot;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class AddEnchantedBookModifier extends LootModifier {
    public static final Supplier<Codec<AddEnchantedBookModifier>> CODEC = Suppliers.memoize(
            () -> RecordCodecBuilder.create(instance -> codecStart(instance)
                    .and(instance.group(
                            ForgeRegistries.ENCHANTMENTS.getCodec().fieldOf("enchantment").forGetter(m -> m.enchantment),
                            Codec.INT.optionalFieldOf("count", 1).forGetter(m -> m.count),
                            Codec.INT.optionalFieldOf("max_count", 0).forGetter(m -> m.maxCount),
                            Codec.INT.optionalFieldOf("chance", 1).forGetter(m -> m.chance),
                            Codec.INT.optionalFieldOf("chance_range", 1).forGetter(m -> m.chanceRange),
                            Codec.INT.optionalFieldOf("level", 1).forGetter(m -> m.level)
                    )).apply(instance, AddEnchantedBookModifier::new)
            )
    );

    private final Enchantment enchantment;
    private final int count;
    private final int maxCount;
    private final int chance;
    private final int chanceRange;
    private final int level;

    public AddEnchantedBookModifier(LootItemCondition[] conditions, Enchantment enchantment, int count, int maxCount, int chance, int chanceRange, int level) {
        super(conditions);
        this.enchantment = enchantment;
        this.count = count;
        this.maxCount = maxCount;
        this.chance = chance;
        this.chanceRange = chanceRange;
        this.level = level;
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
        ItemStack newEnchantedBook = EnchantedBookItem.createForEnchantment(
                new EnchantmentInstance(enchantment, level)
        );
        int finalCount;
        if(maxCount > count) {
            finalCount = UniformGenerator.between(count, maxCount).getInt(context);
        }
        else finalCount = count;
        if(finalCount > 0) generatedLoot.add(newEnchantedBook);
        for (int i = 2; i <= finalCount ; i++) {
            generatedLoot.add(newEnchantedBook.copy());
        }
        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}
