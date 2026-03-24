package net.trashelemental.artificers_armory.entity.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.trashelemental.artificers_armory.ArtificersArmory;
import net.trashelemental.artificers_armory.entity.client.models.SkeletonPriestModel;
import net.trashelemental.artificers_armory.entity.client.models.WispModel;
import net.trashelemental.artificers_armory.entity.custom.SkeletonPriestEntity;
import net.trashelemental.artificers_armory.entity.custom.WispEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class WispRenderer extends GeoEntityRenderer<WispEntity> {
   public WispRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new WispModel());
    }

    @Override
    public ResourceLocation getTextureLocation(WispEntity animatable) {
        return new ResourceLocation(ArtificersArmory.MOD_ID, "textures/entity/wisp.png");
    }

    @Override
    public void render(WispEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();

        poseStack.scale(0.5f, 0.5f, 0.5f);
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);

        poseStack.popPose();
    }
}
