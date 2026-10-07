package io.github.kevinmoonglow.lunar_origins.client;


import com.mojang.datafixers.util.Either;
import io.github.edwinmindcraft.apoli.api.component.IPowerContainer;
import io.github.kevinmoonglow.lunar_origins.LunarOrigins;
import io.github.kevinmoonglow.lunar_origins.entity.LunarOriginsEntities;
import io.github.kevinmoonglow.lunar_origins.item.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import java.util.List;
import java.util.Optional;
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
        EntityRenderers.register(LunarOriginsEntities.GLOW_ITEM_ENTITY.get(), GlowItemRenderer::new);

        //ItemBlockRenderTypes.setRenderLayer(LunarOriginsBlocks.KELP_BED_BLOCK.get(), RenderType.cutout());
    }

    @SubscribeEvent
    public static void registerOverlays(RegisterGuiOverlaysEvent event) {
        event.registerBelowAll("glass_helmet", GlassBowlOverlay.GLASS_BOWL_SCREEN_OVERLAY);
    }





}
