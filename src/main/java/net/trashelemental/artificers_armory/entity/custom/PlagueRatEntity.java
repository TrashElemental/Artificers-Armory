package net.trashelemental.artificers_armory.entity.custom;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.Event;
import net.trashelemental.artificers_armory.junkyard_lib.entity.MinionEntity;
import net.trashelemental.artificers_armory.junkyard_lib.util.UtilMethods;
import net.trashelemental.artificers_armory.junkyard_lib.visual.particle.ParticleMethods;
import net.trashelemental.artificers_armory.magic.effects.ModMobEffects;
import net.trashelemental.artificers_armory.particle.ModParticles;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;

import java.util.Objects;

public class PlagueRatEntity extends MinionEntity implements GeoEntity {
    public PlagueRatEntity(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level, ModParticles.PLAGUE_RATS.get(), ModParticles.PLAGUE_RATS.get(), SoundEvents.BAT_HURT);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        {
            this.targetSelector.addGoal(1,
                    new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, true, false,
                            target -> isValidTarget(target) && isPreferredTarget(target)
                    )
            );
            this.targetSelector.addGoal(2,
                    new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, true, false,
                            this::isValidTarget
                    )
            );
            this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, false, false) {
                @Override
                public boolean canUse() { return super.canUse() && !isTame(); }
            });
            this.targetSelector.addGoal(4, new OwnerHurtByTargetGoal(this));
            this.targetSelector.addGoal(5, new OwnerHurtTargetGoal(this));
            this.goalSelector.addGoal(6, new MeleeAttackGoal(this, 1.2, false));
            this.goalSelector.addGoal(7, new FollowOwnerGoal(this, 1, 10, 2, false));
            this.goalSelector.addGoal(8, new RandomStrollGoal(this, 1));
        }
    }

    private boolean isPreferredTarget(LivingEntity entity) {
        return isValidTarget(entity) && !entity.hasEffect(ModMobEffects.PLAGUE.get());
    }

    private boolean isValidTarget(LivingEntity entity) {
        if (!(entity instanceof Monster)) return false;

        if (entity instanceof OwnableEntity ownable) {
            return !Objects.equals(ownable.getOwnerUUID(), this.getOwnerUUID());
        }

        return true;
    }

    @Override
    public void tick() {

        if (this.tickCount % 20 == 1) {
            ParticleMethods.ParticlesAroundServerSide(this.level(), ModParticles.PLAGUE_RATS.get(),
                    this.getX(), this.getY(), this.getZ(), 3, 1.4);
        }

        super.tick();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 1)
                .add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.ATTACK_DAMAGE, 1)
                .add(Attributes.FOLLOW_RANGE, 16)
                .add(Attributes.ATTACK_KNOCKBACK, 0);
    }

    @Override
    public void playAmbientSound() {
    }

    @Override
    protected void playHurtSound(DamageSource pSource) {
    }

    @Override
    public SoundEvent getDeathSound() {
        return SoundEvents.EMPTY;
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        if (entity == this.getOwner()) {
            return false;
        }
        return super.canCollideWith(entity);
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        this.despawnFromLifespan();
        if (entity instanceof LivingEntity target) {
            UtilMethods.applyEffectNoParticles(target, ModMobEffects.PLAGUE.get(), 100, 0);

            if (this.random.nextBoolean()) {
                UtilMethods.applyEffectNoParticles(target, ModMobEffects.PESTILENCE.get(), 100, 0);
            }
        }
        return super.doHurtTarget(entity);
    }

    @Override
    public void despawnFromLifespan() {
        this.discard();
    }

    @Override
    public boolean canBeAffected(MobEffectInstance instance) {
        return false;
    }

    //GeckoLib
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 4, this::predicate));
    }

    private PlayState predicate(AnimationState<GeoAnimatable> state) {

        if (state.isMoving()) {
            state.getController().setAnimation(RawAnimation.begin().then("MOVE", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }

        state.getController().setAnimation(RawAnimation.begin().then("IDLE", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;

    }

    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
