package io.github.kevinmoonglow.lunar_origins.item;

import io.github.kevinmoonglow.lunar_origins.LunarOrigins;
import io.github.kevinmoonglow.lunar_origins.material.ArmorMaterials;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class LunarOriginsItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, LunarOrigins.MOD_ID);

    // Food Items
    public static final RegistryObject<Item> KELP_CARROT = ITEMS.register("kelp_carrot",
            () -> new Item(new Item.Properties().food(LunarOriginsFoodProperties.KELP_CARROT)));
    public static final RegistryObject<Item> AQUA_GUMMY = ITEMS.register("aqua_gummy",
            () -> new Item(new Item.Properties()
                    .food(LunarOriginsFoodProperties.AQUA_GUMMY)
                    .stacksTo(16)));
    public static final RegistryObject<Item> GLIMMERING_AQUA_GUMMY = ITEMS.register("glimmering_aqua_gummy",
            () -> new Item(new Item.Properties()
                    .food(LunarOriginsFoodProperties.GLIMMERING_AQUA_GUMMY)
                    .stacksTo(16)));


    // Curios
    public static final RegistryObject<Item> GNAP_GLASSES = ITEMS.register("gnap_glasses",
            () -> new GnapGlasses(new Item.Properties().stacksTo(1)));


    // Equippables
    public static final RegistryObject<Item> GLASS_BOWL = ITEMS.register("glass_bowl",
            () -> new GlassBowl(ArmorMaterials.GLASS_BOWL, ArmorItem.Type.HELMET,
                    new Item.Properties().stacksTo(1), new int[]{15, 6, 4, 2, 1}));
    public static final RegistryObject<Item> AMETHYST_BOWL = ITEMS.register("amethyst_bowl",
            () -> new AmethystGlassBowl(ArmorMaterials.AMETHYST_GLASS_BOWL, ArmorItem.Type.HELMET,
                    new Item.Properties().stacksTo(1), new int[]{15, 4, 2, 1, 0}));
    public static final RegistryObject<Item> GOGGLES = ITEMS.register("goggles",
            () -> new Goggles(ArmorMaterials.GOGGLES, ArmorItem.Type.HELMET,
                    new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> DIVING_HELMET = ITEMS.register("diving_helmet",
            () -> new DivingHelmet(ArmorMaterials.COPPER_DIVING_HELMET, ArmorItem.Type.HELMET,
                    new Item.Properties().stacksTo(1), new int[]{6, 4, 1, 0, 0}));

    // Glass Bowl Sealant
    public static final RegistryObject<Item> BOWL_SEALANT = ITEMS.register("bowl_sealant", () -> new BowlSealant(new Item.Properties()));
    public static final RegistryObject<Item> BOWL_SUPER_SEALANT = ITEMS.register("bowl_super_sealant", () -> new BowlSuperSealant(new Item.Properties()));

    // Icons
    public static final RegistryObject<Item> LUNAR_BOOK = ITEMS.register("lunar_book",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> WOPOL_ICON = ITEMS.register("wopol_icon",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> GNAPOREON_ICON = ITEMS.register("gnaporeon_icon",
            () -> new Item(new Item.Properties()));

    // Crafting materials
    public static final RegistryObject<Item> GLASSES_ARM = ITEMS.register("glasses_arm",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> GLASSES_ARM_RED = ITEMS.register("glasses_arm_red",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> GLASSES_FRAME = ITEMS.register("glasses_frame",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> GLASSES_FRAME_RED = ITEMS.register("glasses_frame_red",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> GLASSES_HINGE = ITEMS.register("glasses_hinge",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> HINGE_PART = ITEMS.register("hinge_part",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> HINGE_PIN = ITEMS.register("hinge_pin",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> HINGE_SPRING = ITEMS.register("hinge_spring",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> GLASSES_LENS = ITEMS.register("glasses_lens",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> WIRE_COIL = ITEMS.register("wire_coil",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> CRUDE_LENS = ITEMS.register("crude_lens",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> LENS_CAST = ITEMS.register("lens_cast",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> UNFOCUSED_LENS = ITEMS.register("unfocused_lens",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> INCOMPLETE_CAST = ITEMS.register("incomplete_cast",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> INCOMPLETE_LENS = ITEMS.register("incomplete_lens",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> INCOMPLETE_FRAME = ITEMS.register("incomplete_frame",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> INCOMPLETE_HINGE = ITEMS.register("incomplete_hinge",
            () -> new Item(new Item.Properties()));


    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

    public static void initItems() {
    }

    @SubscribeEvent
    public static void onDamage(LivingDamageEvent event) {
        LivingEntity livingEntity = event.getEntity();
        ItemStack head = livingEntity.getItemBySlot(EquipmentSlot.HEAD);
        if (head != null && head.getItem() instanceof Goggles) {
            CompoundTag nbt = head.getTag();
            if (nbt != null) {
                if (Math.random() < 0.1) {
                    nbt.putBoolean("waterEyeLevel", livingEntity.isUnderWater());
                }
            }
        }
    }
}

