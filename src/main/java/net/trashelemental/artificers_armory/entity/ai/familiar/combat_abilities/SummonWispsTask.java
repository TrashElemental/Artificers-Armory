package net.trashelemental.artificers_armory.entity.ai.familiar.combat_abilities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.trashelemental.artificers_armory.entity.ModEntities;
import net.trashelemental.artificers_armory.entity.ai.familiar.FamiliarRole;
import net.trashelemental.artificers_armory.entity.ai.familiar.FamiliarTask;
import net.trashelemental.artificers_armory.entity.custom.FamiliarEntity;
import net.trashelemental.artificers_armory.entity.custom.WispEntity;
import net.trashelemental.artificers_armory.junkyard_lib.entity.method.SummonMethods;
import net.trashelemental.artificers_armory.junkyard_lib.visual.particle.ParticleMethods;

public class SummonWispsTask implements FamiliarTask {

    private Player owner;

    @Override
    public boolean isInterruptible() {
        return false;
    }

    @Override
    public boolean canRun(FamiliarEntity familiar) {
        owner = null;
        if (familiar.getRole() == FamiliarRole.HEALER) return false;
        if (!(familiar.getOwner() instanceof Player)) return false;
        return familiar.getLevel() >= 7;
    }

    @Override
    public int getWeight(FamiliarEntity familiar) {
        return 3;
    }

    @Override
    public void start(FamiliarEntity familiar) {
        if (!(familiar.getOwner() instanceof Player player)) return;
        owner = player;
        familiar.freezeMovement();
        familiar.noPhysics = true;
        familiar.setInvulnerable(true);

        int count = 3 + familiar.getRandom().nextInt(2);

        for (int i = 0; i < count; i++) {
            for (int attempts = 0; attempts < 8; attempts++) {
                double radius = 2 + familiar.getRandom().nextDouble() * 2;
                double angle = familiar.getRandom().nextDouble() * Math.PI * 2;
                double x = familiar.getX() + Math.cos(angle) * radius;
                double z = familiar.getZ() + Math.sin(angle) * radius;
                double y = familiar.getY() + 0.5 + familiar.getRandom().nextDouble();

                BlockPos pos = BlockPos.containing(x, y, z);
                BlockState state = familiar.level().getBlockState(pos);

                if (!state.blocksMotion() && familiar.level().noCollision(new AABB(pos))) {
                    WispEntity wisp = new WispEntity(ModEntities.WISP.get(), familiar.level());
                    SummonMethods.summonMinion(familiar.level(), pos, wisp, 300, false, owner);
                    break;
                }
            }
        }

        familiar.level().playSound(null, familiar.blockPosition(),
                SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.NEUTRAL, 1f, 1f);
        familiar.triggerAnim("behavior", "swirl");
    }

    @Override
    public void tick(FamiliarEntity familiar) {
        if (owner != null) {
            familiar.freezeMovement();
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
