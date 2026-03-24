package net.trashelemental.artificers_armory.entity.ai.familiar.combat_abilities;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.trashelemental.artificers_armory.entity.ai.familiar.FamiliarRole;
import net.trashelemental.artificers_armory.entity.ai.familiar.FamiliarTask;
import net.trashelemental.artificers_armory.entity.custom.FamiliarEntity;
import net.trashelemental.artificers_armory.junkyard_lib.visual.particle.ParticleMethods;
import net.trashelemental.artificers_armory.particle.ModParticles;
import net.trashelemental.artificers_armory.util.event.FirebrandEvents;

import java.util.*;

/**
 * In combat, the familiar can hijack the target of one or more existing enemies to cause infighting.
 * Prankster role boosts this ability.
 */

public class HijackEnemyTask implements FamiliarTask {

    private final Map<Mob, Mob> hijacks = new HashMap<>();
    private int affectedNumber;

    @Override
    public boolean isInterruptible() {
        return false;
    }

    @Override
    public boolean canRun(FamiliarEntity familiar) {

        hijacks.clear();

        if (familiar.getRole() == FamiliarRole.PRANKSTER) {
            affectedNumber = 1 + familiar.getEnchantLevel();
        } else affectedNumber = 1;

        if (familiar.getLevel() < 5) return false;
        if (familiar.getRole() == FamiliarRole.BRUISER) return false;
        if (familiar.getRole() == FamiliarRole.HEALER) return false;
        if (familiar.getRole() == FamiliarRole.PROTECTOR) return false;
        List<Mob> valid = getValidTargets(familiar);
        return valid.size() >= 2;
    }

    @Override
    public int getWeight(FamiliarEntity familiar) {
        if (familiar.getRole() == FamiliarRole.PRANKSTER) return 5;
        return 3;
    }

    @Override
    public void start(FamiliarEntity familiar) {
        if (!(familiar.getOwner() instanceof Player owner)) return;

        hijacks.clear();

        List<Mob> valid = getValidTargets(familiar);

        Collections.shuffle(valid);
        int count = Math.min(affectedNumber, valid.size() - 1);

        for (int i = 0; i < count; i++) {
            Mob hijacker = valid.get(i);
            List<Mob> possibleVictims = new ArrayList<>(valid);
            possibleVictims.remove(hijacker);
            Mob victim = possibleVictims.get(familiar.getRandom().nextInt(possibleVictims.size()));
            hijacks.put(hijacker, victim);
            hijacker.setTarget(victim);

            if (familiar.getRole() == FamiliarRole.PRANKSTER) {
                ParticleMethods.ParticlesAroundServerSide(familiar.level(), ModParticles.IMP.get(),
                        hijacker.getX(), hijacker.getEyeY(), hijacker.getZ(), 5, 1.2);
            } else {
                ParticleMethods.ParticlesAroundServerSide(familiar.level(), ParticleTypes.ANGRY_VILLAGER,
                        hijacker.getX(), hijacker.getEyeY(), hijacker.getZ(), 5, 1.2);
            }
        }

        if (!hijacks.isEmpty()) {
            Mob first = hijacks.keySet().iterator().next();
            familiar.setPos(first.getX(), first.getY() + 0.5, first.getZ());
            familiar.noPhysics = true;
            familiar.setInvulnerable(true);
        }

        familiar.level().playSound(null, familiar.blockPosition(),
                SoundEvents.ALLAY_ITEM_TAKEN, SoundSource.NEUTRAL, 0.5f, 1f);

        familiar.triggerAnim("behavior", "swirl");
    }

    @Override
    public void tick(FamiliarEntity familiar) {

        for (Map.Entry<Mob, Mob> entry : hijacks.entrySet()) {
            Mob hijacker = entry.getKey();
            Mob victim = entry.getValue();
            if (hijacker.isAlive() && victim.isAlive()) {
                hijacker.setTarget(victim);
            }
        }

        if (!hijacks.isEmpty()) {
            Mob first = hijacks.keySet().iterator().next();
            if (first.isAlive()) {
                familiar.setPos(first.getX(), first.getY() + 0.5, first.getZ());
            }
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

        hijacks.clear();
    }

    private List<Mob> getValidTargets(FamiliarEntity familiar) {
        if (!(familiar.getOwner() instanceof Player owner)) return List.of();
        List<Mob> valid = new ArrayList<>();
        List<LivingEntity> nearby = familiar.level()
                .getEntitiesOfClass(LivingEntity.class, familiar.getBoundingBox().inflate(12));

        for (LivingEntity entity : nearby) {
            if (!(entity instanceof Mob mob)) continue;
            if (entity == familiar) continue;
            if (!entity.isAlive()) continue;
            if (entity.getHealth() <= 3) continue;
            if (FirebrandEvents.isAlly(owner, entity)) continue;
            valid.add(mob);
        }
        return valid;
    }
}