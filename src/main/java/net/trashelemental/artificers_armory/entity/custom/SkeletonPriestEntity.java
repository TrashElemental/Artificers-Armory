package net.trashelemental.artificers_armory.entity.custom;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.trashelemental.artificers_armory.junkyard_lib.visual.particle.ParticleMethods;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

public class SkeletonPriestEntity extends Entity implements GeoEntity {
    public SkeletonPriestEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.noPhysics = true;
    }

    private int animationTicks = 0;
    private final int maxAnimationTicks = 25;
    private boolean animationStarted = false;

    @Override
    protected void defineSynchedData() {

    }
    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {

    }
    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {

    }

    @Override
    public void tick() {
        super.tick();
        if (animationStarted) {
            animationTicks++;
            if (animationTicks == 11) {
                ParticleMethods.ParticlesBurst(level(), ParticleTypes.SCULK_SOUL, this.getX(), this.getY() + 3, this.getZ(), 10,0.2);
            }

            if (animationTicks >= maxAnimationTicks) {
                this.remove(RemovalReason.DISCARDED);
            }
        }
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        this.playSound(SoundEvents.EVOKER_PREPARE_SUMMON, 0.3f, 1f);
        this.triggerAnim("castController", "CAST");
        animationStarted = true;
    }

    //GeckoLib
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

        controllers.add(new AnimationController<>(
                this, "castController", 0, state -> PlayState.STOP)
                .triggerableAnim("CAST", RawAnimation.begin().then("CAST", Animation.LoopType.PLAY_ONCE)));

    }

    private PlayState predicate(AnimationState<GeoAnimatable> state) {
        return PlayState.CONTINUE;
    }

    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
