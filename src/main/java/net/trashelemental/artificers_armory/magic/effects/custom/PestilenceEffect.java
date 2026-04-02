package net.trashelemental.artificers_armory.magic.effects.custom;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.trashelemental.artificers_armory.ArtificersArmory;
import net.trashelemental.artificers_armory.entity.ModEntities;
import net.trashelemental.artificers_armory.entity.custom.PlagueRatEntity;
import net.trashelemental.artificers_armory.junkyard_lib.visual.particle.ParticleMethods;
import net.trashelemental.artificers_armory.particle.ModParticles;

import java.util.UUID;

public class PestilenceEffect extends MobEffect {
    public PestilenceEffect() {
        super(MobEffectCategory.HARMFUL, 9685817);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {

        if (entity.level().isClientSide) return;

        ParticleMethods.ParticlesAroundServerSide(entity.level(), ModParticles.PLAGUE_RATS.get(),
                entity.getX(), entity.getEyeY(), entity.getZ(), 4, 2);

        float chance = 0.2f + (0.05f * amplifier);
        if (entity.getRandom().nextFloat() > chance) return;
        ServerLevel level = (ServerLevel) entity.level();
        PlagueRatEntity rat = new PlagueRatEntity(ModEntities.PLAGUE_RAT.get(), level);

        rat.moveTo(entity.getX(), entity.getY() + 0.5, entity.getZ(), level.random.nextFloat() * 360F, 0);
        rat.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(amplifier + 1);

        CompoundTag data = entity.getPersistentData();
        if (data.hasUUID("lastPlagueApplier")) {
            UUID ownerID = data.getUUID("lastPlagueApplier");
            Player owner = level.getPlayerByUUID(ownerID);

            if (owner != null) {
                rat.tame(owner);
                rat.setOwnerUUID(ownerID);
                rat.setLifespan(200, false);
            }
        }

        level.addFreshEntity(rat);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % 20 == 0;
    }

    public ResourceLocation getIcon() {
        return new ResourceLocation(ArtificersArmory.MOD_ID, "textures/mob_effect/pestilence.png");
    }
}
