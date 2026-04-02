package net.trashelemental.artificers_armory.magic.effects.custom;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.trashelemental.artificers_armory.ArtificersArmory;
import net.trashelemental.artificers_armory.junkyard_lib.visual.particle.ParticleMethods;
import net.trashelemental.artificers_armory.particle.ModParticles;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class DeliriumEffect extends MobEffect {
    public DeliriumEffect() {
        super(MobEffectCategory.HARMFUL, 16750487);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {

        MobEffectInstance nausea = new MobEffectInstance(MobEffects.CONFUSION, 200, amplifier, false, false);
        entity.addEffect(nausea);

        if (!(entity.level() instanceof ServerLevel level)) return;
        CompoundTag data = entity.getPersistentData();

        ParticleMethods.ParticlesAroundServerSide(entity.level(), ModParticles.DELIRIUM.get(),
                entity.getX(), entity.getEyeY(), entity.getZ(), 4, 2);

        if (entity instanceof Player player) {
            float chance = Mth.clamp(0.5f + (0.1f * amplifier), 0f, 0.9f);
            if (player.getRandom().nextFloat() < chance) {
                double strength = 1 + (0.5 * amplifier);
                double angle = player.getRandom().nextDouble() * Math.PI * 2;
                double x = Math.cos(angle) * strength;
                double z = Math.sin(angle) * strength;

                Vec3 current = player.getDeltaMovement();
                player.setDeltaMovement(current.add(x, 0, z));
                player.hurtMarked = true;
            }
        }

        if (!(entity instanceof Mob mob)) return;
        float chance = Mth.clamp(0.5f + (0.1f * amplifier), 0f, 0.9f);
        if (entity.getRandom().nextFloat() > chance) return;
        Player owner = null;

        if (data.hasUUID("lastPlagueApplier")) {
            UUID ownerID = data.getUUID("lastPlagueApplier");
            owner = level.getPlayerByUUID(ownerID);
        }

        LivingEntity newTarget = getRandomNearbyTarget(level, mob, owner);

        if (newTarget != null) {
            mob.setLastHurtByMob(null);
            mob.setTarget(newTarget);
        }
    }

    private static LivingEntity getRandomNearbyTarget(ServerLevel level, Mob mob, @Nullable Player owner) {

        double radius = 12.0;
        AABB area = mob.getBoundingBox().inflate(radius);

        List<LivingEntity> candidates = level.getEntitiesOfClass(LivingEntity.class, area, target -> {
            if (target == mob) return false;
            if (!target.isAlive()) return false;
            if (!(target instanceof Mob)) return false;

            if (owner != null) {
                if (target == owner) return false;

                if (target instanceof OwnableEntity ownable) {
                    if (Objects.equals(ownable.getOwnerUUID(), owner.getUUID())) {
                        return false;
                    }
                }
            }
            return true;
        });

        if (candidates.isEmpty()) return null;

        return candidates.get(level.random.nextInt(candidates.size()));
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % 40 == 0;
    }

    public ResourceLocation getIcon() {
        return new ResourceLocation(ArtificersArmory.MOD_ID, "textures/mob_effect/delirium.png");
    }
}
