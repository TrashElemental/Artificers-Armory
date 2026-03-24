package net.trashelemental.artificers_armory.entity.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.trashelemental.artificers_armory.ArtificersArmory;
import net.trashelemental.artificers_armory.entity.client.models.FamiliarHeldItemLayer;
import net.trashelemental.artificers_armory.entity.client.models.FamiliarModel;
import net.trashelemental.artificers_armory.entity.custom.FamiliarEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class FamiliarRenderer extends GeoEntityRenderer<FamiliarEntity> {
    public FamiliarRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new FamiliarModel());
        this.addRenderLayer(new FamiliarHeldItemLayer(this));
    }

    ResourceLocation DEFAULT_TEXTURE = new ResourceLocation(ArtificersArmory.MOD_ID, "textures/entity/familiar.png");
    ResourceLocation ALLAY_TEXTURE = new ResourceLocation(ArtificersArmory.MOD_ID, "textures/entity/familiar_allay.png");
    ResourceLocation VEX_TEXTURE = new ResourceLocation(ArtificersArmory.MOD_ID, "textures/entity/familiar_vex.png");
    ResourceLocation PROTECTOR_TEXTURE = new ResourceLocation(ArtificersArmory.MOD_ID, "textures/entity/familiar_protector.png");
    ResourceLocation BRUISER_TEXTURE = new ResourceLocation(ArtificersArmory.MOD_ID, "textures/entity/familiar_bruiser.png");
    ResourceLocation CHEERLEADER_TEXTURE = new ResourceLocation(ArtificersArmory.MOD_ID, "textures/entity/familiar_cheerleader.png");
    ResourceLocation FIEND_TEXTURE = new ResourceLocation(ArtificersArmory.MOD_ID, "textures/entity/familiar_fiend.png");
    ResourceLocation GABBY_TEXTURE = new ResourceLocation(ArtificersArmory.MOD_ID, "textures/entity/familiar_gabby.png");
    ResourceLocation ZAGGY_TEXTURE = new ResourceLocation(ArtificersArmory.MOD_ID, "textures/entity/familiar_zaggy.png");
    ResourceLocation CAMMY_TEXTURE = new ResourceLocation(ArtificersArmory.MOD_ID, "textures/entity/familiar_cammy.png");
    ResourceLocation SPAMTON_TEXTURE = new ResourceLocation(ArtificersArmory.MOD_ID, "textures/entity/familiar_spamton.png");
    ResourceLocation COMPANIONS_TEXTURE = new ResourceLocation(ArtificersArmory.MOD_ID, "textures/entity/familiar_companions.png");
    ResourceLocation TAMABLE_BEASTS_TEXTURE = new ResourceLocation(ArtificersArmory.MOD_ID, "textures/entity/familiar_tamable_beasts.png");

    @Override
    public ResourceLocation getTextureLocation(FamiliarEntity animatable) {
        return switch (animatable.getSkin()) {
            case ALLAY -> ALLAY_TEXTURE;
            case VEX -> VEX_TEXTURE;
            case PROTECTOR -> PROTECTOR_TEXTURE;
            case BRUISER -> BRUISER_TEXTURE;
            case HEALER -> CHEERLEADER_TEXTURE;
            case PRANKSTER -> FIEND_TEXTURE;
            case GABBY -> GABBY_TEXTURE;
            case ZAGGY -> ZAGGY_TEXTURE;
            case CAMMY -> CAMMY_TEXTURE;
            case SPAMTON -> SPAMTON_TEXTURE;
            case MAGE -> COMPANIONS_TEXTURE;
            case SCARECROW -> TAMABLE_BEASTS_TEXTURE;
            default -> DEFAULT_TEXTURE;
        };
    }

    @Override
    public void render(FamiliarEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        poseStack.popPose();
    }
}
