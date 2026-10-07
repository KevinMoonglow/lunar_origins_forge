package io.github.kevinmoonglow.lunar_origins.client;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.kevinmoonglow.lunar_origins.entity.GlowItemEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class GlowItemRenderer extends EntityRenderer<ItemEntity> {
    private final ItemRenderer itemRenderer;
    protected GlowItemRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
        itemRenderer = pContext.getItemRenderer();
    }




    @Override
    public void render(ItemEntity pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        ItemStack stack = pEntity.getItem();

        if(!stack.isEmpty()) {
            pPoseStack.pushPose();

            // Make it always face the camera
            pPoseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());

            // Flat scale
            pPoseStack.scale(0.25F, 0.25F, 0.05F);


            BakedModel bakedModel = this.itemRenderer.getModel(stack, pEntity.level(), null, pEntity.getId());


            itemRenderer.render(stack, ItemDisplayContext.GUI, false, pPoseStack, pBuffer, 0xF000F0, OverlayTexture.NO_OVERLAY, bakedModel);


            pPoseStack.popPose();
        }
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(ItemEntity pEntity) {
        return InventoryMenu.BLOCK_ATLAS;
    }
}
