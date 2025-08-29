package io.github.kevinmoonglow.lunar_origins.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

public class GnapGlassesRenderer implements ICurioRenderer {
    @Override
    public <T extends LivingEntity, M extends EntityModel<T>> void render(ItemStack stack,
                                                                          SlotContext slotContext,
                                                                          PoseStack matrixStack,
                                                                          RenderLayerParent<T, M> renderLayerParent,
                                                                          MultiBufferSource renderTypeBuffer,
                                                                          int light,
                                                                          float limbSwing,
                                                                          float limbSwingAmount,
                                                                          float partialTicks,
                                                                          float ageInTicks,
                                                                          float netHeadYaw,
                                                                          float headPitch) {
        Minecraft mc = Minecraft.getInstance();
        matrixStack.pushPose();
        LivingEntity entity = slotContext.entity();
        M model = renderLayerParent.getModel();
        if(model instanceof HumanoidModel<?> humanoidModel) {
            humanoidModel.head.translateAndRotate(matrixStack);

            matrixStack.translate(0, 0.3, -0.5 );
            matrixStack.mulPose(Axis.ZP.rotationDegrees(180f));
            matrixStack.scale(0.65f, 0.625f, 0.5f);

            ItemStack s = stack.copy();
            CompoundTag nbt = s.getOrCreateTag();
            nbt.putBoolean("faceModel", true);

            mc.getItemRenderer().renderStatic(s, ItemDisplayContext.HEAD, light, 0, matrixStack, renderTypeBuffer, mc.level, 0);
        }






        matrixStack.popPose();
    }
}
