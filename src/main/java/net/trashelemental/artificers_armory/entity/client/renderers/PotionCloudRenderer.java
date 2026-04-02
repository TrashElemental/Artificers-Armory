package net.trashelemental.artificers_armory.entity.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.trashelemental.artificers_armory.ArtificersArmory;
import net.trashelemental.artificers_armory.entity.client.models.PlagueRatModel;
import net.trashelemental.artificers_armory.entity.client.models.PotionCloudModel;
import net.trashelemental.artificers_armory.entity.custom.PlagueRatEntity;
import net.trashelemental.artificers_armory.entity.custom.PotionCloudEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class PotionCloudRenderer extends GeoEntityRenderer<PotionCloudEntity> {
   public PotionCloudRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new PotionCloudModel());

    }

    @Override
    public RenderType getRenderType(PotionCloudEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(texture);
    }

    @Override
    public ResourceLocation getTextureLocation(PotionCloudEntity animatable) {
        return new ResourceLocation(ArtificersArmory.MOD_ID, "textures/entity/potion_cloud.png");
    }

    @Override
    public void render(PotionCloudEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();

        poseStack.scale(1f, 1f, 1f);
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);

        poseStack.popPose();
    }

    @Override
    public void renderFinal(PoseStack poseStack, PotionCloudEntity animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        super.renderFinal(poseStack, animatable, model, bufferSource, buffer, partialTick, packedLight, packedOverlay, red, green, blue, 0f);
    }
}
