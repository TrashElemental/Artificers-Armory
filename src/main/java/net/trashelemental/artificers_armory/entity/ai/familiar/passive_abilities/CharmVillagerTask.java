package net.trashelemental.artificers_armory.entity.ai.familiar.passive_abilities;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.village.ReputationEventType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.trading.MerchantOffers;
import net.trashelemental.artificers_armory.entity.ai.familiar.FamiliarRole;
import net.trashelemental.artificers_armory.entity.ai.familiar.FamiliarTask;
import net.trashelemental.artificers_armory.entity.custom.FamiliarEntity;
import net.trashelemental.artificers_armory.junkyard_lib.visual.particle.ParticleMethods;

import java.util.List;

/**
 * Tier 5+ ability. Charm villagers to lower their prices and improve the owner's reputation, and restock and upgrade
 * them at higher tiers.
 */

public class CharmVillagerTask implements FamiliarTask {

    private Villager target;

    @Override
    public boolean canRun(FamiliarEntity familiar) {
        target = null;
        int level = familiar.getLevel();
        if (level < 5) return false;

        if (!(familiar.getOwner() instanceof Player player)) return false;
        if (familiar.getRole() == FamiliarRole.BRUISER) return false;
        List<Villager> villagers = familiar.level().getEntitiesOfClass(Villager.class, familiar.getBoundingBox().inflate(10));
        List<Villager> valid = villagers.stream()
                .filter(v -> v.getVillagerData().getProfession() != VillagerProfession.NONE)
                .filter(v -> v.getVillagerData().getProfession() != VillagerProfession.NITWIT)
                .filter(v -> v.getPlayerReputation(player) < 20)
                .toList();

        if (valid.isEmpty()) return false;
        target = valid.get(familiar.getRandom().nextInt(valid.size()));
        return true;
    }

    @Override
    public int getWeight(FamiliarEntity familiar) {
        return 2;
    }

    @Override
    public void start(FamiliarEntity familiar) {

        if (target == null) return;
        if (familiar.getOwner() == null) return;
        if (!(familiar.level() instanceof ServerLevel serverLevel)) return;
        if (!(familiar.getOwner() instanceof Player player)) return;
        int level = familiar.getLevel();
        familiar.freezeMovement();
        familiar.getLookControl().setLookAt(target);

        if (!(target.getPlayerReputation(player) >= 20)) {
            serverLevel.onReputationEvent(ReputationEventType.TRADE, familiar.getOwner(), target);
        }

        if (level >= 6) {
            VillagerData data = target.getVillagerData();
            if (data.getLevel() < 5) {
                target.setVillagerXp(target.getVillagerXp() + 15);
            }
            target.restock();
        }

        if (level >= 7) {
            VillagerData data = target.getVillagerData();
            if (data.getLevel() < 5) {
                target.setVillagerXp(target.getVillagerXp() + 30);
            }
            target.restock();
        }

        ParticleMethods.ParticleTrailEntityToEntity(familiar.level(), ParticleTypes.HAPPY_VILLAGER, familiar, target, 8);
        familiar.level().playSound(null, target.blockPosition(),
                SoundEvents.ALLAY_AMBIENT_WITH_ITEM, SoundSource.NEUTRAL, 0.5f, 1.2f);
        familiar.triggerAnim("behavior", "minorSupport");
        target.level().broadcastEntityEvent(target, (byte)14);
    }

    @Override
    public void tick(FamiliarEntity familiar) {
        if (target == null) return;
        familiar.freezeMovement();
        familiar.getLookControl().setLookAt(target);
    }

    @Override
    public int getDuration(FamiliarEntity familiar) {
        return 40;
    }

    @Override
    public void stop(FamiliarEntity familiar) {

    }
}
