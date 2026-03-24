package net.trashelemental.artificers_armory.entity.custom.necromancy;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.trashelemental.artificers_armory.entity.ai.necromancy.*;
import net.trashelemental.artificers_armory.entity.custom.OwnableMinion;
import net.trashelemental.artificers_armory.util.spirit_candle.SpiritCandleEvents;

import javax.annotation.Nullable;
import java.util.UUID;

public class DrownedMinionEntity extends Drowned implements OwnableMinion {

    @Nullable
    private UUID ownerUUID;

    public DrownedMinionEntity(EntityType<? extends Drowned> type, Level level) {
        super(type, level);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.removeAllGoals(g -> true);
        this.targetSelector.removeAllGoals(g -> true);

        // Follow owner like zombies
        this.goalSelector.addGoal(1, new MinionFollowOwnerGoal(this, 1.1D, 12.0F, 6.0F));
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(2, new TridentAttackGoal(this, 1.0D, 40, 10.0F));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(9, new RandomStrollGoal(this, 1));

        this.targetSelector.addGoal(1, new MinionDefendOwnerGoal(this));
        this.targetSelector.addGoal(2, new MinionOwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(3, new MinionOwnerHurtTargetGoal(this));
    }

    @Override
    public void setTarget(@Nullable LivingEntity target) {
        if (target != null && isOwnedBy(target)) return;
        if (target instanceof OwnableEntity ownable && ownable.getOwnerUUID() == this.getOwnerUUID()) return;
        super.setTarget(target);
    }

    @Override
    public void tick() {
        if (!checkOwnerHoldingCandle() && !this.level().isClientSide) {
            SpiritCandleEvents.despawnMinion(this);
        }

        if (level().isClientSide && isNearbyIronGolem()) {
            if (tickCount % 10 == 0) {
                for (int i = 0; i < 4; i++) {
                    double xOffset = (random.nextDouble() - 0.5) * 0.5;
                    double zOffset = (random.nextDouble() - 0.5) * 0.5;
                    level().addParticle(ParticleTypes.SPLASH,
                            getX() + xOffset, getEyeY(), getZ() + zOffset,
                            0, 0, 0);
                }
            }
        }

        super.tick();
    }

    public boolean isNearbyIronGolem() {
        if (!level().isClientSide) return false;

        return !level().getEntitiesOfClass(IronGolem.class, this.getBoundingBox().inflate(8.0D)).isEmpty();
    }

    @Override
    public void setOwner(@Nullable LivingEntity owner) {
        this.ownerUUID = owner != null ? owner.getUUID() : null;
    }

    @Override
    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    @Override
    public LivingEntity getOwner() {
        return ownerUUID == null ? null : level().getPlayerByUUID(ownerUUID);
    }

    @Override
    public boolean isOwnedBy(LivingEntity entity) {
        return entity != null && entity.getUUID().equals(ownerUUID);
    }

    @Override
    public boolean isAlliedTo(Entity entity) {
        if (entity instanceof Player player && isOwnedBy(player)) return true;
        if (entity instanceof OwnableEntity ownable && ownable.getOwnerUUID() == ownerUUID) return true;
        return false;
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason,
                                        @Nullable SpawnGroupData spawnData, @Nullable CompoundTag dataTag) {
        spawnData = super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
        this.setCanPickUpLoot(false);
        this.setBaby(false);
        // Give drowned a guaranteed trident
        this.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.TRIDENT));
        return spawnData;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (ownerUUID != null) tag.putUUID("Owner", ownerUUID);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.hasUUID("Owner")) ownerUUID = tag.getUUID("Owner");
    }

    /**
     * Don't push me!
     */
    @Override
    protected void doPush(Entity other) {
        if (other instanceof Player player && isOwnedBy(player)) return;
        super.doPush(other);
    }

    @Override
    public boolean canCollideWith(Entity other) {
        if (other instanceof Player player && isOwnedBy(player)) return false;
        return super.canCollideWith(other);
    }

    @Override
    public void push(Entity other) {
        if (other instanceof Player player && isOwnedBy(player)) {
            return;
        }
        super.push(other);
    }

    /**
     * Does not prevent any players from resting.
     */
    @Override
    public boolean isPreventingPlayerRest(Player pPlayer) {
        return false;
    }

    /**
     * Does not burn in sunlight.
     */
    @Override
    protected boolean isSunSensitive() {
        return false;
    }

    /**
     * BE QUIET!
     */
    @Override
    protected float getSoundVolume() {
        return 0.05f;
    }

    public boolean canHoldItem(ItemStack pStack) {
        return pStack.is(Items.TRIDENT);
    }

    @Override
    public int getExperienceReward() {
        return 0;
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHit) {
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.getEntity() instanceof LivingEntity attacker
                && isOwnedBy(attacker)
                && !attacker.isCrouching()) {
            return false;
        }

        return super.hurt(source, amount);
    }

    @Override
    protected void randomizeReinforcementsChance() {
        this.getAttribute(Attributes.SPAWN_REINFORCEMENTS_CHANCE)
                .setBaseValue(0.0D);
    }
}
