package io.github.kevinmoonglow.lunar_origins.client;


import io.github.kevinmoonglow.lunar_origins.LunarOrigins;
import io.github.kevinmoonglow.lunar_origins.item.AmethystGlassBowl;
import io.github.kevinmoonglow.lunar_origins.item.DivingHelmet;
import io.github.kevinmoonglow.lunar_origins.item.GlassBowl;
import io.github.kevinmoonglow.lunar_origins.item.LunarOriginsItems;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import java.util.function.Predicate;

@Mod.EventBusSubscriber(modid = LunarOrigins.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class LunarOriginsClient {

    @SubscribeEvent
    public static void onClientInit(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemProperties.register(LunarOriginsItems.GLASS_BOWL.get(),
                    ResourceLocation.parse("water_level"), (itemStack, level, living, id) -> {
                        CompoundTag nbt = itemStack.getTag();
                        if(nbt == null) return 0f;
                        int waterLevel = nbt.getInt("waterLevel");
                        return waterLevel / (float) GlassBowl.MAX_WATER;
                    });
           ItemProperties.register(LunarOriginsItems.GLASS_BOWL.get(),
                   ResourceLocation.parse("worn"), (itemStack, level, livingEntity, id) -> {
                       if (livingEntity != null && itemStack == livingEntity.getItemBySlot(EquipmentSlot.HEAD)) {
                           return 1.0f;
                       }
                       return 0.0f;
                   });
            ItemProperties.register(LunarOriginsItems.AMETHYST_BOWL.get(),
                    ResourceLocation.parse("water_level"), (itemStack, level, living, id) -> {
                        CompoundTag nbt = itemStack.getTag();
                        if(nbt == null) return 0f;
                        int waterLevel = nbt.getInt("waterLevel");
                        return waterLevel / (float) AmethystGlassBowl.MAX_WATER;
                    });
            ItemProperties.register(LunarOriginsItems.AMETHYST_BOWL.get(),
                    ResourceLocation.parse("worn"), (itemStack, level, livingEntity, id) -> {
                        if (livingEntity != null && itemStack == livingEntity.getItemBySlot(EquipmentSlot.HEAD)) {
                            return 1.0f;
                        }
                        return 0.0f;
                    });
            ItemProperties.register(LunarOriginsItems.DIVING_HELMET.get(),
                    ResourceLocation.parse("water_level"), (itemStack, level, livingEntity, id) -> {
                        CompoundTag nbt = itemStack.getTag();
                        if (nbt == null) return 0.0f;
                        int waterLevel = nbt.getInt("waterLevel");
                        return waterLevel / (float) DivingHelmet.MAX_WATER;
                    });
            ItemProperties.register(LunarOriginsItems.GOGGLES.get(),
                    ResourceLocation.parse("water_level"), (itemStack, level, livingEntity, id) -> {
                        CompoundTag nbt = itemStack.getTag();
                        if (nbt == null) return 0.0f;
                        return (float)nbt.getInt("waterEyeLevel");
                    });

           ItemProperties.register(LunarOriginsItems.GNAP_GLASSES.get(),
                   ResourceLocation.parse("worn"), (itemStack, level, livingEntity, id) -> {
                       LazyOptional<ICuriosItemHandler> optional = CuriosApi.getCuriosInventory(livingEntity);
                       if(optional.isPresent()) {
                           ICuriosItemHandler inventory = optional.resolve().orElseThrow();
                           if(inventory.isEquipped(Predicate.isEqual(itemStack))) {
                               return 1.0f;
                           }
                       }
                       return 0.0f;
                   });
            ItemProperties.register(LunarOriginsItems.GNAP_GLASSES.get(),
                    ResourceLocation.parse("face"), (itemStack, level, livingEntity, id) -> {
                        if (!itemStack.hasTag()) {
                            return 0.0f;
                        }

                        assert itemStack.getTag() != null;
                        boolean faceModel = itemStack.getTag().getBoolean("faceModel");

                        return faceModel ? 1.0f : 0.0f;

                    });

        });

        CuriosRendererRegistry.register(LunarOriginsItems.GNAP_GLASSES.get(), GnapGlassesRenderer::new);
        //BlockEntityRenderers.register(LunarOriginsBlockEntities.KELP_BED.get(), KelpBedRenderer::new);

        //ItemBlockRenderTypes.setRenderLayer(LunarOriginsBlocks.KELP_BED_BLOCK.get(), RenderType.cutout());
    }

    @SubscribeEvent
    public static void registerOverlays(RegisterGuiOverlaysEvent event) {
        event.registerBelowAll("glass_helmet", GlassBowlOverlay.GLASS_BOWL_SCREEN_OVERLAY);
    }



}
