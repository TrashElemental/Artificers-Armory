package net.trashelemental.artificers_armory.entity.ai.familiar.triggered_abilities;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.trashelemental.artificers_armory.entity.ai.familiar.FamiliarEventHandlers;
import net.trashelemental.artificers_armory.entity.ai.familiar.FamiliarRole;
import net.trashelemental.artificers_armory.entity.ai.familiar.FamiliarTask;
import net.trashelemental.artificers_armory.entity.custom.FamiliarEntity;
import net.trashelemental.artificers_armory.junkyard_lib.util.UtilMethods;
import net.trashelemental.artificers_armory.util.event.FirebrandEvents;

import java.util.List;

/**
 * If the owner takes damage from a mob attack while an enemy is nearby, the familiar can swirl around them and deal its
 * damage in an area and knock away enemies for one second.
 * Improved by the protector role.
 */

public class KnockbackAroundOwnerTask implements FamiliarTask {

    private Player owner;

    @Override
    public boolean isInterruptible() {
        return false;
    }

    @Override
    public boolean canTrigger(FamiliarEntity familiar, DamageSource source) {
        if (familiar.getLevel() < 6) return false;
        if (!(familiar.getOwner() instanceof Player player)) return false;
        if (!(source.is(DamageTypes.MOB_ATTACK) || source.is(DamageTypes.PLAYER_ATTACK))) return false;

        List<LivingEntity> nearby = familiar.level().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(2));

        for (LivingEntity entity : nearby) {
            if (!(entity instanceof Mob)) continue;
            if (!entity.isAlive()) continue;
            if (FirebrandEvents.isAlly(player, entity)) continue;
            return true;
        }

        return false;
    }

    @Override
    public boolean canRun(FamiliarEntity familiar) {
        return false;
    }

    @Override
    public int getWeight(FamiliarEntity familiar) {
        return 0;
    }

    @Override
    public void start(FamiliarEntity familiar) {
        if (!(familiar.getOwner() instanceof Player player)) return;
        owner = player;
        familiar.noPhysics = true;
        familiar.setInvulnerable(true);
        familiar.teleportTo(owner.getX(), owner.getY() + 0.5, owner.getZ());
        familiar.triggerAnim("behavior", "swirl");
        familiar.level().playSound(null, owner.blockPosition(), SoundEvents.ALLAY_AMBIENT_WITHOUT_ITEM, SoundSource.NEUTRAL, 0.4f, 1f);

        doAreaDamage(familiar);

        FamiliarEventHandlers.giveHealerBonusEffects(familiar, player);
        FamiliarEventHandlers.giveProtectorBonusEffects(familiar, player);
    }

    private void doAreaDamage(FamiliarEntity familiar) {
        if (!(familiar.getOwner() instanceof Player owner)) return;
        if (familiar.getRole() == FamiliarRole.HEALER) return;
        if (familiar.getRole() == FamiliarRole.PRANKSTER) return;
        List<LivingEntity> nearby = familiar.level().getEntitiesOfClass(LivingEntity.class, owner.getBoundingBox().inflate(3));
        float damage = (float) familiar.getAttributeValue(Attributes.ATTACK_DAMAGE);
        if (familiar.getRole() == FamiliarRole.PROTECTOR) damage = (float) familiar.getAttributeValue(Attributes.ATTACK_DAMAGE) * 2;

        for (LivingEntity entity : nearby) {
            if (!(entity instanceof Mob)) continue;
            if (entity == familiar) continue;
            if (!entity.isAlive()) continue;
            if (FirebrandEvents.isAlly(owner, entity)) continue;
            entity.hurt(familiar.damageSources().mobAttack(familiar), damage);
        }
    }

    private void doKnockback(FamiliarEntity familiar) {
        if (!(familiar.getOwner() instanceof Player owner)) return;
        List<LivingEntity> nearby = familiar.level().getEntitiesOfClass(LivingEntity.class, owner.getBoundingBox().inflate(3));
        double knockback = 0.2;
        if (familiar.getRole() == FamiliarRole.PROTECTOR) knockback = 0.6;

        for (LivingEntity entity : nearby) {
            if (!(entity instanceof Mob)) continue;
            if (entity == familiar) continue;
            if (!entity.isAlive()) continue;
            if (FirebrandEvents.isAlly(owner, entity)) continue;
            double dx = owner.getX() - entity.getX();
            double dz = owner.getZ() - entity.getZ();
            entity.knockback(knockback, dx, dz);
            entity.setDeltaMovement(entity.getDeltaMovement().add(0, 0.05, 0));
        }
    }

    @Override
    public void tick(FamiliarEntity familiar) {
        if (owner != null) {
            familiar.setPos(owner.getX(), owner.getY() + 0.5, owner.getZ());
            doKnockback(familiar);
        }
    }

    @Override
    public int getDuration(FamiliarEntity familiar) {
        return 20;
    }

    @Override
    public void stop(FamiliarEntity familiar) {
        familiar.noPhysics = false;
        familiar.setInvulnerable(false);
        owner = null;
    }
}
