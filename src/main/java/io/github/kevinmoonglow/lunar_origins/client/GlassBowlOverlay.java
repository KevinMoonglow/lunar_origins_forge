package io.github.kevinmoonglow.lunar_origins.client;

import io.github.kevinmoonglow.lunar_origins.LunarOrigins;
import io.github.kevinmoonglow.lunar_origins.item.AmethystGlassBowl;
import io.github.kevinmoonglow.lunar_origins.item.DivingHelmet;
import io.github.kevinmoonglow.lunar_origins.item.GlassBowl;
import io.github.kevinmoonglow.lunar_origins.item.Goggles;
import io.github.kevinmoonglow.lunar_origins.mixin.GuiGraphicsModifier;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class GlassBowlOverlay {

    public static final ResourceLocation WATER_TEXTURE = ResourceLocation.fromNamespaceAndPath(LunarOrigins.MOD_ID, "textures/misc/waterfill.png");
    public static final ResourceLocation WATER_TEXTURE_BRIGHT = ResourceLocation.fromNamespaceAndPath(LunarOrigins.MOD_ID, "textures/misc/waterfill_bright.png");
    public static final ResourceLocation AIR_BUBBLE_TEXTURE = ResourceLocation.fromNamespaceAndPath(LunarOrigins.MOD_ID, "textures/misc/air_bubble_overlay.png");
    public static final ResourceLocation GLASS_BOWL_OVERLAY = ResourceLocation.fromNamespaceAndPath(LunarOrigins.MOD_ID, "textures/misc/glass_bowl_overlay.png");
    public static final ResourceLocation GLASS_BOWL_CRACK_OVERLAY = ResourceLocation.fromNamespaceAndPath(LunarOrigins.MOD_ID, "textures/misc/glass_bowl_crack_overlay.png");
    public static final ResourceLocation AMETHYST_BOWL_OVERLAY = ResourceLocation.fromNamespaceAndPath(LunarOrigins.MOD_ID, "textures/misc/amethyst_bowl_overlay.png");
    public static final ResourceLocation DIVING_HELMET_OVERLAY = ResourceLocation.fromNamespaceAndPath(LunarOrigins.MOD_ID, "textures/misc/diving_helmet_overlay.png");
    public static final ResourceLocation AMETHYST_BOWL_CRACK_OVERLAY = ResourceLocation.fromNamespaceAndPath(LunarOrigins.MOD_ID, "textures/misc/amethyst_bowl_crack_overlay.png");
    public static final ResourceLocation GOGGLES_OVERLAY = ResourceLocation.fromNamespaceAndPath(LunarOrigins.MOD_ID, "textures/misc/goggle_overlay.png");

    public static final IGuiOverlay GLASS_BOWL_SCREEN_OVERLAY = ((gui, guiGraphics, partialTick, screenWidth, screenHeight) -> {
        //RenderSystem.setShaderTexture(0, GLASS_BOWL_OVERLAY);


        Entity viewer = gui.getMinecraft().cameraEntity;
        if(viewer != null && viewer.isAlive() && gui.getMinecraft().options.getCameraType().isFirstPerson()) {
            LivingEntity entity = (LivingEntity) viewer;

            ItemStack head = entity.getItemBySlot(EquipmentSlot.HEAD);
            Item item = head.getItem();
            if(item instanceof GlassBowl) {
                CompoundTag nbt = head.getTag();

                if(nbt != null) {
                    long waterLevel = nbt.getLong("waterLevel");
                    double level = getLevel(entity, waterLevel);

                    if(item instanceof DivingHelmet) {
                        ((GuiGraphicsModifier) guiGraphics).invokeInnerBlitWithColor(
                                DIVING_HELMET_OVERLAY, 0, screenWidth, 0, screenHeight, 0,
                                0, 1, 0, 1, 1.0f, 1.0f, 1.0f, 1.0f);
                    }
                    else if(item instanceof AmethystGlassBowl) {
                        ((GuiGraphicsModifier) guiGraphics).invokeInnerBlitWithColor(
                                AMETHYST_BOWL_OVERLAY, 0, screenWidth, 0, screenHeight, 0,
                                0, 1, 0, 1, 1.0f, 1.0f, 1.0f, 0.75f);
                    }
                    else {
                        ((GuiGraphicsModifier) guiGraphics).invokeInnerBlitWithColor(
                                GLASS_BOWL_OVERLAY, 0, screenWidth, 0, screenHeight, 0,
                                0, 1, 0, 1, 1.0f, 1.0f, 1.0f, 0.75f);
                    }

                    //if((float)head.getDamageValue() / head.getMaxDamage() >= 0.5f) {
                    if(((GlassBowl)item).isCracked(head)) {
                        if (item instanceof AmethystGlassBowl) {
                            ((GuiGraphicsModifier) guiGraphics).invokeInnerBlitWithColor(
                                    AMETHYST_BOWL_CRACK_OVERLAY, 0, screenWidth, 0, screenHeight, 0,
                                    0, 1, 0, 1, 1.0f, 1.0f, 1.0f, 0.75f
                            );
                        }
                        else {
                            ((GuiGraphicsModifier) guiGraphics).invokeInnerBlitWithColor(
                                    GLASS_BOWL_CRACK_OVERLAY, 0, screenWidth, 0, screenHeight, 0,
                                    0, 1, 0, 1, 1.0f, 1.0f, 1.0f, 0.75f
                            );
                        }
                    }
                    if(entity.isUnderWater()) {
                        renderAir(level, guiGraphics, screenWidth, screenHeight);
                    }
                    else {
                        renderWater(level, guiGraphics, screenWidth, screenHeight);
                    }

                    guiGraphics.setColor(1, 1, 1, 1);
                }
            }
            else if (item instanceof Goggles) {
                ((GuiGraphicsModifier) guiGraphics).invokeInnerBlitWithColor(
                        GOGGLES_OVERLAY, 0, screenWidth, 0, screenHeight, 0,
                        0, 1, 0, 1, 1.0f, 1.0f, 1.0f, 1.0f
                );
            }
        }
    });

    private static double getLevel(LivingEntity entity, double waterLevel) {
        double viewLevelPitch = entity.getXRot() / 90f;
        double viewWaterScaling = viewLevelPitch >= 0f ? viewLevelPitch*2.0f + 1.0f : viewLevelPitch * 2.0f - 1.0f;

        double waterLevelPitch = waterLevel / GlassBowl.MAX_WATER;
        double eyeLevelPitch = (float) GlassBowl.EYE_LEVEL / GlassBowl.MAX_WATER;

        double waterLevelAdjusted;
        if(viewWaterScaling > 0)
            waterLevelAdjusted = waterLevelPitch * viewWaterScaling;
        else
            waterLevelAdjusted = 1 - ((1 - waterLevelPitch) * -viewWaterScaling);

        double waterDiff = waterLevelAdjusted - eyeLevelPitch;
        return waterDiff > 0f ? waterDiff / (1.0f - eyeLevelPitch) : waterDiff / eyeLevelPitch;
    }

    private static void renderWater(double level, GuiGraphics guiGraphics, int screenWidth, int screenHeight) {
        level *= 0.5f;
        level += 0.5f;
        level = Math.min(level, 1.0f);
        level = Math.max(level, 0.0f);

        double waterHeight = (double)screenHeight * level;
        double waterTop = (double)screenHeight - waterHeight;

        if(waterTop < screenHeight) {
            if(waterTop > 0) {
                ((GuiGraphicsModifier) guiGraphics).invokeInnerBlitWithColor(
                        WATER_TEXTURE, 0, screenWidth, (int) (waterTop), screenHeight, 0,
                        0, 1.0f, ((int)waterTop)/(float)screenHeight, 1.0f,
                        1.0f, 1.0f, 1.0f, 0.6f
                );
                ((GuiGraphicsModifier) guiGraphics).invokeInnerBlitWithColor(
                        WATER_TEXTURE_BRIGHT, 0, screenWidth, (int) waterTop, (int) waterTop + (screenHeight / 64), 0,
                        0, 1.0f, ((int)waterTop)/(float)screenHeight, (((int)waterTop)/(float)screenHeight) + (1f / 64f),
                        1.0f, 1.0f, 1.0f, 0.6f
                );
            }
            else {
                ((GuiGraphicsModifier) guiGraphics).invokeInnerBlitWithColor(
                        WATER_TEXTURE, 0, screenWidth, 0, screenHeight, 0,
                        0, 1.0f, 0f, 1.0f,
                        1.0f, 1.0f, 1.0f, 0.6f
                );
            }
        }
    }
    private static void renderAir(double level, GuiGraphics guiGraphics, int screenWidth, int screenHeight) {
        level *= 0.5f;
        level += 0.5f;
        level = Math.min(level, 1.0f);
        level = Math.max(level, 0.0f);

        double waterHeight = screenHeight * level;
        double waterTop = screenHeight - waterHeight;

        if(waterTop > 0) {
            //guiGraphics.blitNineSliced(AIR_BUBBLE_TEXTURE,0, 0, screenWidth, (int)waterHeight, 10, 0, 0, 64, 36);
            ((GuiGraphicsModifier) guiGraphics).invokeInnerBlitWithColor(
                    AIR_BUBBLE_TEXTURE, 0, screenWidth, 0, (int) waterTop, 0,
                    0, 1.0f, 0, 1.0f,
                    1.0f, 1.0f, 1.0f, 0.6f
            );
        }
    }
}
