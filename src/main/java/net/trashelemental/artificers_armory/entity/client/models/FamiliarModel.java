package net.trashelemental.artificers_armory.entity.client.models;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.trashelemental.artificers_armory.ArtificersArmory;
import net.trashelemental.artificers_armory.entity.custom.FamiliarEntity;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class FamiliarModel extends GeoModel<FamiliarEntity> {
    @Override
    public ResourceLocation getModelResource(FamiliarEntity animatable) {
        return new ResourceLocation(ArtificersArmory.MOD_ID,"geo/models/familiar.geo.json");
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
    public ResourceLocation getTextureResource(FamiliarEntity animatable) {
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
    public ResourceLocation getAnimationResource(FamiliarEntity animatable) {
        return new ResourceLocation(ArtificersArmory.MOD_ID,"animations/familiar.animation.json");
    }

    @Override
    public void setCustomAnimations(FamiliarEntity animatable, long instanceId, AnimationState<FamiliarEntity> animationState) {
        CoreGeoBone head = getAnimationProcessor().getBone("head");

        if (head != null) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
            head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
        }
    }
}
