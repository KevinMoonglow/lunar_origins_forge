package io.github.kevinmoonglow.lunar_origins.client;

import io.github.kevinmoonglow.lunar_origins.LunarOrigins;
import io.github.kevinmoonglow.lunar_origins.item.AmethystGlassBowl;
import io.github.kevinmoonglow.lunar_origins.item.DivingHelmet;
import io.github.kevinmoonglow.lunar_origins.item.GlassBowl;
import io.github.kevinmoonglow.lunar_origins.item.Goggles;
import io.github.kevinmoonglow.lunar_origins.mixin.GuiGraphicsModifier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = LunarOrigins.MOD_ID, value = Dist.CLIENT)
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

    private static float prevEyeLight = 15f;
    private static float eyeLight = 15f;

    private static void simpleOverlay(GuiGraphicsModifier ctx, ResourceLocation texture, float alpha, float brightness, int screenWidth, int screenHeight) {
        ctx.invokeInnerBlitWithColor(
                texture, 0, screenWidth, 0, screenHeight, 0,
                0, 1, 0, 1, brightness, brightness, brightness, alpha
        );
    }
    private static void partialOverlayScaled(GuiGraphicsModifier ctx, ResourceLocation texture, float alpha, float brightness, int top, int bottom, int screenWidth, int screenHeight) {
        ctx.invokeInnerBlitWithColor(
                texture, 0, screenWidth, top, bottom, 0,
                0, 1, 0, 1, brightness, brightness, brightness, alpha
        );
    }
    private static void partialOverlayCropped(GuiGraphicsModifier ctx, ResourceLocation texture, float alpha, float brightness, int top, int bottom, int screenWidth, int screenHeight) {
        ctx.invokeInnerBlitWithColor(
                texture, 0, screenWidth, top, bottom, 0,
                0, 1, top/(float)screenHeight, bottom/(float)screenHeight, brightness, brightness, brightness, alpha
        );
    }


    public static final IGuiOverlay GLASS_BOWL_SCREEN_OVERLAY = ((gui, guiGraphics, partialTick, screenWidth, screenHeight) -> {
        //RenderSystem.setShaderTexture(0, GLASS_BOWL_OVERLAY);


        Entity viewer = gui.getMinecraft().cameraEntity;
        if(viewer != null && viewer.isAlive() && gui.getMinecraft().options.getCameraType().isFirstPerson()) {
            LivingEntity entity = (LivingEntity) viewer;
            //ClientLevel world = gui.getMinecraft().level;

            ItemStack head = entity.getItemBySlot(EquipmentSlot.HEAD);
            Item item = head.getItem();

            //BlockPos eyePos = BlockPos.containing(viewer.getEyePosition());
            //int light = world != null ? world.getMaxLocalRawBrightness(eyePos) : eyeLight;
            float lerpedLight = Mth.lerp(partialTick, prevEyeLight, eyeLight);

            float brightness = lerpedLight/15.0f;

            GuiGraphicsModifier graphicsModifier = (GuiGraphicsModifier) guiGraphics;

            if(item instanceof GlassBowl bowl) {
                CompoundTag nbt = head.getTag();

                if(nbt != null) {
                    long waterLevel = nbt.getLong("waterLevel");
                    double level = getLevel(entity, waterLevel);


                    if(item instanceof DivingHelmet)
                        simpleOverlay(graphicsModifier, DIVING_HELMET_OVERLAY, 1.0f, brightness, screenWidth, screenHeight);
                    else if(item instanceof AmethystGlassBowl)
                        simpleOverlay(graphicsModifier, AMETHYST_BOWL_OVERLAY, 0.75f, brightness, screenWidth, screenHeight);
                    else
                        simpleOverlay(graphicsModifier, GLASS_BOWL_OVERLAY, 0.75f, brightness, screenWidth, screenHeight);

                    //if((float)head.getDamageValue() / head.getMaxDamage() >= 0.5f) {
                    if(bowl.isCracked(head)) {
                        if (item instanceof AmethystGlassBowl)
                            simpleOverlay(graphicsModifier, AMETHYST_BOWL_CRACK_OVERLAY, 0.75f, brightness, screenWidth, screenHeight);
                        else
                            simpleOverlay(graphicsModifier, GLASS_BOWL_CRACK_OVERLAY, 0.75f, brightness, screenWidth, screenHeight);
                    }
                    if(entity.isUnderWater())
                        renderAir(level, graphicsModifier, screenWidth, screenHeight, brightness);
                    else
                        renderWater(level, graphicsModifier, screenWidth, screenHeight, brightness);
                    //guiGraphics.setColor(1, 1, 1, 1);
                }
            }
            else if (item instanceof Goggles) {
                simpleOverlay(graphicsModifier, GOGGLES_OVERLAY, 1.0f, brightness, screenWidth, screenHeight);
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

    private static void renderWater(double level, GuiGraphicsModifier guiGraphics, int screenWidth, int screenHeight, float brightness) {
        level *= 0.5f;
        level += 0.5f;
        level = Math.min(level, 1.0f);
        level = Math.max(level, 0.0f);

        double waterHeight = (double)screenHeight * level;
        double waterTop = (double)screenHeight - waterHeight;

        if(waterTop < screenHeight) {
            if(waterTop > 0) {
                partialOverlayCropped(guiGraphics, WATER_TEXTURE, 0.4f, brightness, (int)waterTop, screenHeight, screenWidth, screenHeight);
                partialOverlayCropped(guiGraphics, WATER_TEXTURE_BRIGHT, 0.6f, brightness, (int)waterTop, (int)waterTop+(screenHeight/64), screenWidth, screenHeight);
            }
            else simpleOverlay(guiGraphics, WATER_TEXTURE, 0.4f, brightness, screenWidth, screenHeight);
        }
    }
    private static void renderAir(double level, GuiGraphicsModifier guiGraphics, int screenWidth, int screenHeight, float brightness) {
        level *= 0.5f;
        level += 0.5f;
        level = Math.min(level, 1.0f);
        level = Math.max(level, 0.0f);

        double waterHeight = screenHeight * level;
        double waterTop = screenHeight - waterHeight;

        if(waterTop > 0) {
            partialOverlayScaled(guiGraphics, AIR_BUBBLE_TEXTURE, 0.6f, brightness, 0, (int)waterTop, screenWidth, screenHeight);
        }
    }
    @SubscribeEvent
    public static void checkLight(TickEvent.ClientTickEvent event) {
        if(event.phase != TickEvent.Phase.START) return;

        Entity viewer = Minecraft.getInstance().cameraEntity;
        if (viewer != null && viewer.isAlive()) {
            LivingEntity entity = (LivingEntity) viewer;
            ClientLevel world = Minecraft.getInstance().level;

            if (world != null) {
                BlockPos eyePos = BlockPos.containing(entity.getEyePosition());
                prevEyeLight = eyeLight;
                eyeLight = Mth.approach(eyeLight, world.getMaxLocalRawBrightness(eyePos), 0.2f);
            }
        }
    }
}
