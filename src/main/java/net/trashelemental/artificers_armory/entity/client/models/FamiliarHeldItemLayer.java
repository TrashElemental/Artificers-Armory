package net.trashelemental.artificers_armory.entity.client.models;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.trashelemental.artificers_armory.entity.custom.FamiliarEntity;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.BlockAndItemGeoLayer;

public class FamiliarHeldItemLayer extends BlockAndItemGeoLayer<FamiliarEntity> {
    public FamiliarHeldItemLayer(GeoRenderer<FamiliarEntity> renderer) {
        super(renderer,
                (bone, familiar) -> "arms".equals(bone.getName()) ? familiar.getCarriedItem() : null,
                (bone, familiar) -> null
        );
    }

    @Override
    protected ItemDisplayContext getTransformTypeForStack(GeoBone bone, ItemStack stack, FamiliarEntity animatable) {
        return ItemDisplayContext.FIXED;
    }

    @Override
    protected void renderStackForBone(PoseStack poseStack, GeoBone bone, ItemStack stack, FamiliarEntity animatable, MultiBufferSource bufferSource, float partialTick, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        poseStack.translate(0, -0.2f, -0.1f);
        poseStack.scale(0.4f, 0.4f, 0.4f);
        super.renderStackForBone(poseStack, bone, stack, animatable, bufferSource, partialTick, packedLight, packedOverlay);
        poseStack.popPose();
    }
}
