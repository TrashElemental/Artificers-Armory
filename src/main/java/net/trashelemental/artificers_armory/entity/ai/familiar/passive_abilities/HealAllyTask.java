package net.trashelemental.artificers_armory.entity.ai.familiar.passive_abilities;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.trashelemental.artificers_armory.entity.ai.familiar.FamiliarRole;
import net.trashelemental.artificers_armory.entity.ai.familiar.FamiliarTask;
import net.trashelemental.artificers_armory.entity.custom.FamiliarEntity;
import net.trashelemental.artificers_armory.junkyard_lib.visual.particle.ParticleMethods;

import java.util.List;

/**
 * The familiar can heal the player's allies, such as its pets, golems, and friendly players. Level 3 ability (copper and up.)
 */

public class HealAllyTask implements FamiliarTask {

    private LivingEntity target;
    private float healAmount;

    @Override
    public boolean canRun(FamiliarEntity familiar) {
        target = null;
        int level = familiar.getLevel();

        if (level < 3) return false;

        LivingEntity livingEntity = familiar.getOwner();
        if (!(livingEntity instanceof Player owner)) return false;
        if (familiar.getRole() == FamiliarRole.BRUISER) return false;
        List<LivingEntity> nearby = familiar.level().getEntitiesOfClass(LivingEntity.class, familiar.getBoundingBox().inflate(12));
        LivingEntity best = null;
        int bestPriority = -1;

        for (LivingEntity entity : nearby) {
            if (entity.getHealth() >= entity.getMaxHealth()) continue;
            int priority = getPriority(owner, entity);
            if (priority > bestPriority) {
                bestPriority = priority;
                best = entity;
            }
        }

        target = best;

        healAmount = 1 + (2 * level);
        return target != null;
    }

    private int getPriority(Player owner, LivingEntity entity) {
        if (entity instanceof OwnableEntity ownable) {
            if (ownable.getOwner() == owner) {
                return 4;
            }
        }
        if (entity instanceof Player player) {
            if (player != owner && player.getLastHurtByMob() != owner && owner.getLastHurtMob() != player) {
                return 3;
            }
        }
        if (entity instanceof AbstractGolem golem) {
            if (golem.getTarget() != owner && owner.getLastHurtMob() != entity) {
                return 2;
            }
        }
        if (entity instanceof Villager) {
            if (owner.getLastHurtMob() != entity) {
                return 1;
            }
        }

        return -1;
    }

    @Override
    public int getWeight(FamiliarEntity familiar) {
        int base = 6;
        if (target == null) return 0;
        if (familiar.getRole() == FamiliarRole.PRANKSTER) return 3;
        if (familiar.getRole() == FamiliarRole.HEALER) base = 8;
        float missing = target.getMaxHealth() - target.getHealth();
        float ratio = missing / target.getMaxHealth();
        return Math.min((int)(ratio * 8), base);
    }

    @Override
    public void start(FamiliarEntity familiar) {
        if (target == null) return;
        familiar.getNavigation().stop();
        familiar.getLookControl().setLookAt(target);
        target.heal(healAmount);
        ParticleMethods.ParticleTrailEntityToEntity(familiar.level(), ParticleTypes.HEART, familiar, target, 5);
        familiar.level().playSound(null, familiar.blockPosition(),
                SoundEvents.ALLAY_AMBIENT_WITH_ITEM, SoundSource.NEUTRAL, 0.5f, 1f);
        familiar.triggerAnim("behavior", "minorSupport");
    }

    @Override
    public void tick(FamiliarEntity familiar) {
        if (target == null) return;
        familiar.getLookControl().setLookAt(target);
        familiar.freezeMovement();
    }


    @Override
    public int getDuration(FamiliarEntity familiar) {
        return 20;
    }

    @Override
    public void stop(FamiliarEntity familiar) {

    }
}
