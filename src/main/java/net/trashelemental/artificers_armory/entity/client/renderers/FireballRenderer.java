package net.trashelemental.artificers_armory.entity.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.trashelemental.artificers_armory.ArtificersArmory;
import net.trashelemental.artificers_armory.entity.client.models.FireballModel;
import net.trashelemental.artificers_armory.entity.custom.FireballEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class FireballRenderer extends GeoEntityRenderer<FireballEntity> {
    public FireballRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new FireballModel());
    }

    @Override
    public ResourceLocation getTextureLocation(FireballEntity animatable) {
        return animatable.getTexture();
    }

    @Override
    public void render(FireballEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();

        float scale = 0.7f;

        float adjustedScale = scale * entity.size();
        poseStack.scale(adjustedScale, adjustedScale, adjustedScale);

        Vec3 velocity = entity.getDeltaMovement();
        if (velocity.lengthSqr() > 0.001) {
            double motionX = velocity.x;
            double motionY = velocity.y;
            double motionZ = velocity.z;

            float yaw = (float) (Math.toDegrees(Math.atan2(motionZ, motionX))) - 90.0F;
            float pitch = (float) (Math.toDegrees(Math.atan2(motionY, Math.sqrt(motionX * motionX + motionZ * motionZ))));

            poseStack.mulPose(Axis.YP.rotationDegrees(-yaw));
            poseStack.mulPose(Axis.XP.rotationDegrees(pitch));
        }

        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);

        poseStack.popPose();
    }
}
