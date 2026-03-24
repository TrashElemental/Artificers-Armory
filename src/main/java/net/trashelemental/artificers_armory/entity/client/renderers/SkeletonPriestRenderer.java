package net.trashelemental.artificers_armory.entity.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;
import net.trashelemental.artificers_armory.ArtificersArmory;
import net.trashelemental.artificers_armory.entity.client.models.FireballModel;
import net.trashelemental.artificers_armory.entity.client.models.SkeletonPriestModel;
import net.trashelemental.artificers_armory.entity.custom.FireballEntity;
import net.trashelemental.artificers_armory.entity.custom.SkeletonPriestEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SkeletonPriestRenderer extends GeoEntityRenderer<SkeletonPriestEntity> {
   public SkeletonPriestRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new SkeletonPriestModel());
    }

    @Override
    public ResourceLocation getTextureLocation(SkeletonPriestEntity animatable) {
        return new ResourceLocation(ArtificersArmory.MOD_ID, "textures/entity/skeleton_priest.png");
    }

    @Override
    public void render(SkeletonPriestEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();

        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);

        poseStack.popPose();
    }
}
