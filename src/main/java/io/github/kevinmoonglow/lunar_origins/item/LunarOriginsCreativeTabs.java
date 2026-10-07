package io.github.kevinmoonglow.lunar_origins.item;

import io.github.kevinmoonglow.lunar_origins.LunarOrigins;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class LunarOriginsCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, LunarOrigins.MOD_ID);

    public static final RegistryObject<CreativeModeTab> LUNAR_ORIGINS_TAB = CREATIVE_TABS.register("lunar_origins",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(LunarOriginsItems.LUNAR_BOOK.get()))
                    .title(Component.translatable("itemGroup.lunar_origins.lunar_origins"))
                    .displayItems((pParameters, pOutput) -> {
                        pOutput.accept(LunarOriginsItems.BOWL_SEALANT.get());
                        pOutput.accept(LunarOriginsItems.BOWL_SUPER_SEALANT.get());
                        pOutput.accept(LunarOriginsItems.KELP_CARROT.get());
                        pOutput.accept(LunarOriginsItems.GNAP_GLASSES.get());
                        pOutput.accept(LunarOriginsItems.GLASS_BOWL.get());
                        pOutput.accept(LunarOriginsItems.AMETHYST_BOWL.get());
                        pOutput.accept(LunarOriginsItems.DIVING_HELMET.get());
                        pOutput.accept(LunarOriginsItems.GOGGLES.get());
                        pOutput.accept(LunarOriginsItems.AQUA_GUMMY.get());
                        pOutput.accept(LunarOriginsItems.GLIMMERING_AQUA_GUMMY.get());
                        pOutput.accept(LunarOriginsItems.KELP_BED_ITEM.get());
                        pOutput.accept(LunarOriginsItems.LIGHT_ORB_ITEM.get());

                    })
                    .build());
    public static final RegistryObject<CreativeModeTab> LUNAR_ORIGINS_INGREDIENT_TAB = CREATIVE_TABS.register("lunar_origins_ingredients",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(LunarOriginsItems.GLASSES_LENS.get()))
                    .title(Component.translatable("itemGroup.lunar_origins.lunar_origins_ingredients"))
                    .displayItems((pParameters, pOutput) -> {
                        pOutput.accept(LunarOriginsItems.GLASSES_ARM.get());
                        pOutput.accept(LunarOriginsItems.GLASSES_ARM_RED.get());
                        pOutput.accept(LunarOriginsItems.GLASSES_FRAME.get());
                        pOutput.accept(LunarOriginsItems.GLASSES_FRAME_RED.get());
                        pOutput.accept(LunarOriginsItems.GLASSES_HINGE.get());
                        pOutput.accept(LunarOriginsItems.HINGE_PART.get());
                        pOutput.accept(LunarOriginsItems.HINGE_PIN.get());
                        pOutput.accept(LunarOriginsItems.HINGE_SPRING.get());
                        pOutput.accept(LunarOriginsItems.GLASSES_LENS.get());
                        pOutput.accept(LunarOriginsItems.WIRE_COIL.get());
                        pOutput.accept(LunarOriginsItems.CRUDE_LENS.get());
                        pOutput.accept(LunarOriginsItems.LENS_CAST.get());
                        pOutput.accept(LunarOriginsItems.UNFOCUSED_LENS.get());
                        pOutput.accept(LunarOriginsItems.INCOMPLETE_CAST.get());
                        pOutput.accept(LunarOriginsItems.INCOMPLETE_LENS.get());
                        pOutput.accept(LunarOriginsItems.INCOMPLETE_FRAME.get());
                        pOutput.accept(LunarOriginsItems.INCOMPLETE_HINGE.get());
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_TABS.register(eventBus);
    }
}
