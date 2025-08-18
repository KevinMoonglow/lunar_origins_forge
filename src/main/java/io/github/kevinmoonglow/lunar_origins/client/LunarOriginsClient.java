package io.github.kevinmoonglow.lunar_origins.client;


import io.github.kevinmoonglow.lunar_origins.LunarOrigins;
import io.github.kevinmoonglow.lunar_origins.item.GlassBowl;
import io.github.kevinmoonglow.lunar_origins.item.LunarOriginsItems;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = LunarOrigins.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class LunarOriginsClient {

    @SubscribeEvent
    public static void onClientInit(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemProperties.register(LunarOriginsItems.GLASS_BOWL.get(),
                    ResourceLocation.parse("water_level"), (itemStack, level, living, id) -> {
                        if (!itemStack.hasTag()) {
                            return 0.0f;
                        }

                        assert itemStack.getTag() != null;
                        int waterLevel = itemStack.getTag().getInt("waterLevel");

                        return waterLevel / (float) GlassBowl.MAX_WATER;

                    });
           ItemProperties.register(LunarOriginsItems.GLASS_BOWL.get(),
                   ResourceLocation.parse("worn"), (itemStack, level, livingEntity, id) -> {
                       if (livingEntity != null && itemStack == livingEntity.getItemBySlot(EquipmentSlot.HEAD)) {
                           return 1.0f;
                       }
                       return 0.0f;
                   });
        });
    }
}
