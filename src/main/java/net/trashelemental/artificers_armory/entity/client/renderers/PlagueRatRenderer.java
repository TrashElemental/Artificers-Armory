package net.trashelemental.artificers_armory.entity.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.trashelemental.artificers_armory.ArtificersArmory;
import net.trashelemental.artificers_armory.entity.client.models.PlagueRatModel;
import net.trashelemental.artificers_armory.entity.client.models.WispModel;
import net.trashelemental.artificers_armory.entity.custom.PlagueRatEntity;
import net.trashelemental.artificers_armory.entity.custom.WispEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.core.object.Color;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import java.awt.*;

public class PlagueRatRenderer extends GeoEntityRenderer<PlagueRatEntity> {
   public PlagueRatRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new PlagueRatModel());

    }

    @Override
    public RenderType getRenderType(PlagueRatEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(texture);
    }

    @Override
    public ResourceLocation getTextureLocation(PlagueRatEntity animatable) {
        return new ResourceLocation(ArtificersArmory.MOD_ID, "textures/entity/plague_rat.png");
    }

    @Override
    public void render(PlagueRatEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();

        poseStack.scale(1f, 1f, 1f);
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);

        poseStack.popPose();
    }

    @Override
    public void renderFinal(PoseStack poseStack, PlagueRatEntity animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        super.renderFinal(poseStack, animatable, model, bufferSource, buffer, partialTick, packedLight, packedOverlay, red, green, blue, 0.3f);
    }
}
