package io.github.kevinmoonglow.lunar_origins.potion;

import io.github.kevinmoonglow.lunar_origins.LunarOrigins;
import io.github.kevinmoonglow.lunar_origins.effect.LunarOriginsEffects;
import io.github.kevinmoonglow.lunar_origins.item.LunarOriginsItems;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class LunarOriginsPotions {
    public static final DeferredRegister<Potion> POTIONS =
            DeferredRegister.create(ForgeRegistries.POTIONS, LunarOrigins.MOD_ID);

    public static final RegistryObject<Potion> CLEAR_VISION_POTION = POTIONS.register("clear_vision",
            () -> new Potion(new MobEffectInstance(LunarOriginsEffects.CLEAR_VISION.get(), 3600)));
    public static final RegistryObject<Potion> LONG_CLEAR_VISION_POTION = POTIONS.register("long_clear_vision",
            () -> new Potion("clear_vision", new MobEffectInstance(LunarOriginsEffects.CLEAR_VISION.get(), 9600)));
    public static final RegistryObject<Potion> HYDRATION_POTION = POTIONS.register("hydration",
            () -> new Potion(new MobEffectInstance(LunarOriginsEffects.HYDRATION.get(), 3600)));
    public static final RegistryObject<Potion> LONG_HYDRATION_POTION = POTIONS.register("long_hydration",
            () -> new Potion("hydration", new MobEffectInstance(LunarOriginsEffects.HYDRATION.get(), 9600)));


    public static void addRecipe(Potion base, Item ingredient, Potion result) {

        BrewingRecipeRegistry.addRecipe(new PotionBrewingRecipeMix(base, ingredient, result));
    }

    public static void register(IEventBus eventBus) {
        POTIONS.register(eventBus);
    }

    public static void initPotionRecipes() {
        addRecipe(Potions.AWKWARD, LunarOriginsItems.KELP_CARROT.get(), LunarOriginsPotions.CLEAR_VISION_POTION.get());
        addRecipe(LunarOriginsPotions.CLEAR_VISION_POTION.get(), Items.REDSTONE, LunarOriginsPotions.LONG_CLEAR_VISION_POTION.get());
        addRecipe(Potions.AWKWARD, LunarOriginsItems.GLIMMERING_AQUA_GUMMY.get(), LunarOriginsPotions.HYDRATION_POTION.get());
        addRecipe(LunarOriginsPotions.HYDRATION_POTION.get(), Items.REDSTONE, LunarOriginsPotions.LONG_HYDRATION_POTION.get());
    }
}
