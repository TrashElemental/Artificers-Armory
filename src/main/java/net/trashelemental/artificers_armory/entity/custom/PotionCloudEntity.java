package net.trashelemental.artificers_armory.entity.custom;

import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.trashelemental.artificers_armory.junkyard_lib.util.UtilMethods;
import net.trashelemental.artificers_armory.junkyard_lib.visual.particle.ParticleMethods;
import net.trashelemental.artificers_armory.magic.effects.ModMobEffects;
import net.trashelemental.artificers_armory.particle.ModParticles;
import net.trashelemental.artificers_armory.util.event.FirebrandEvents;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PotionCloudEntity extends AbstractHurtingProjectile implements GeoEntity {

    private int lifetime;
    private int maxLifetime;
    private final List<MobEffectInstance> effects = new ArrayList<>();

    public static final EntityDataAccessor<Boolean> PURIFYING = SynchedEntityData.defineId(PotionCloudEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> TRANSMUTATION = SynchedEntityData.defineId(PotionCloudEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Float> SIZE = SynchedEntityData.defineId(PotionCloudEntity.class, EntityDataSerializers.FLOAT);

    public PotionCloudEntity(EntityType<? extends AbstractHurtingProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.noPhysics = true;

        this.maxLifetime = 300;
        this.lifetime = 0;
        this.effects.add(new MobEffectInstance(ModMobEffects.BLESSING.get(), 100, 0));
    }

    public PotionCloudEntity(EntityType<? extends AbstractHurtingProjectile> pEntityType, @Nullable Player owner, Vec3 direction, Level pLevel, int maxLifetime, List<MobEffectInstance> effects) {
        super(pEntityType, pLevel);
        this.noPhysics = true;

        this.setOwner(owner);
        this.setDeltaMovement(direction.scale(0.03));

        this.maxLifetime = maxLifetime;
        this.effects.addAll(effects);
    }

    public void setLifetime(int lifetime) {
        this.maxLifetime = lifetime;
    }
    public void addEffect(MobEffectInstance effect) {
        this.effects.add(effect);
    }
    public float size() {
        return this.entityData.get(SIZE);
    }
    public void setSize(float size) { this.entityData.set(SIZE, size); }
    public boolean isPurifying() { return this.entityData.get(PURIFYING); }
    public void setPurifying(boolean purifying) { this.entityData.set(PURIFYING, purifying); }
    public boolean isTransmutation() { return this.entityData.get(TRANSMUTATION); }
    public void setTransmutation(boolean transmutation) { this.entityData.set(TRANSMUTATION, transmutation); }

    @Override
    public void tick() {
        super.tick();

        lifetime++;

        float size = this.size();
        double radius = size * 2.0;

        if (level() instanceof ServerLevel level) {
            AABB area = new AABB(this.getX() - radius, this.getY() - radius * 0.5, this.getZ() - radius,
                    this.getX() + radius, this.getY() + radius * 0.5, this.getZ() + radius);
            int color = PotionUtils.getColor(effects);
            double r = (color >> 16 & 255) / 255.0F;
            double g = (color >> 8 & 255) / 255.0F;
            double b = (color & 255) / 255.0F;

            if (this.tickCount % 15 == 1) {
                int particleCount = (int)(10 * size);

                for (int i = 0; i < particleCount; i++) {
                    double angle = level.random.nextDouble() * Math.PI * 2;
                    double dist = Math.sqrt(level.random.nextDouble()) * radius;

                    double px = this.getX() + Math.cos(angle) * dist;
                    double pz = this.getZ() + Math.sin(angle) * dist;
                    double py = this.getY() + (level.random.nextDouble() - 0.5) * radius * 0.5;

                    level.sendParticles(ModParticles.POTION_CLOUD.get(),
                            px, py, pz, 0,
                            r, g, b, 1);
                }
            }

            if (this.tickCount % 10 == 1) {
                List<LivingEntity> targets = level().getEntitiesOfClass(LivingEntity.class, area);
                Vec3 center = this.position();

                for (LivingEntity target : targets) {
                    double dx = target.getX() - center.x;
                    double dz = target.getZ() - center.z;
                    if ((dx * dx + dz * dz) > (radius * radius)) continue;
                    boolean isAlly = target == this.getOwner() || (this.getOwner() instanceof Player player && FirebrandEvents.isAlly(player, target));
                    boolean isEnemy = !isAlly;

                    for (MobEffectInstance effectInstance : effects) {
                        MobEffect effect = effectInstance.getEffect();
                        boolean isBeneficial = effect.isBeneficial();

                        if (isBeneficial && isAlly) {
                            apply(target, effectInstance);
                        } else if (!isBeneficial && isEnemy) {
                            apply(target, effectInstance);
                        }

                        if (isTransmutation()) {
                            MobEffect converted = getTransmutedEffect(effect);

                            if (converted != effect) {
                                if (isBeneficial && isEnemy) {
                                    apply(target, new MobEffectInstance(converted,
                                            Math.max(20, effectInstance.getDuration() / 2), Math.max(0, effectInstance.getAmplifier() / 2)));
                                } else if (!isBeneficial && isAlly) {
                                    apply(target, new MobEffectInstance(
                                            converted, Math.max(20, effectInstance.getDuration() / 2), Math.max(0, effectInstance.getAmplifier() / 2)));
                                }
                            }
                        }
                    }
                }
            }

            if (this.tickCount % 30 == 1 && isPurifying()) {
                List<LivingEntity> targets = level().getEntitiesOfClass(LivingEntity.class, area);
                Vec3 center = this.position();

                for (LivingEntity target : targets) {
                    double dx = target.getX() - center.x;
                    double dz = target.getZ() - center.z;
                    if ((dx * dx + dz * dz) > (radius * radius)) continue;
                    boolean isAlly = target == this.getOwner() || (this.getOwner() instanceof Player player && FirebrandEvents.isAlly(player, target));

                    if (isAlly) {
                        target.heal(2);
                        ParticleMethods.ParticlesAroundServerSide(level, ParticleTypes.HAPPY_VILLAGER,
                                target.getX(), target.getEyeY(), target.getZ(), 3, 2);
                    } else {
                        UtilMethods.damageEntity(target, DamageTypes.MAGIC, 2);
                        ParticleMethods.ParticlesAroundServerSide(level, ParticleTypes.CRIT,
                                target.getX(), target.getEyeY(), target.getZ(), 3, 2);
                    }
                }
            }

            if (this.isInWater() || lifetime >= maxLifetime) {
                level().playSound(null, getX(), getY(), getZ(),
                        SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS, 0.3F, 1.3F);
                this.discard();
            }
        }
    }

    private void apply(LivingEntity target, MobEffectInstance effectInstance) {
        UtilMethods.applyEffectWithParticles(target, effectInstance.getEffect(), effectInstance.getDuration(), effectInstance.getAmplifier());
    }

    public static final Map<MobEffect, MobEffect> TRANSMUTATION_MAP = Map.ofEntries(
            Map.entry(ModMobEffects.BLESSING.get(), MobEffects.WEAKNESS),
            Map.entry(ModMobEffects.BLACK_DEATH.get(), ModMobEffects.BLESSING.get()),
            Map.entry(MobEffects.MOVEMENT_SPEED, MobEffects.MOVEMENT_SLOWDOWN),
            Map.entry(MobEffects.MOVEMENT_SLOWDOWN, MobEffects.MOVEMENT_SPEED),
            Map.entry(MobEffects.DIG_SPEED, MobEffects.DIG_SLOWDOWN),
            Map.entry(MobEffects.DIG_SLOWDOWN, MobEffects.DIG_SPEED),
            Map.entry(MobEffects.DAMAGE_BOOST, MobEffects.WEAKNESS),
            Map.entry(MobEffects.WEAKNESS, MobEffects.DAMAGE_BOOST),
            Map.entry(MobEffects.REGENERATION, MobEffects.WITHER),
            Map.entry(MobEffects.WITHER, MobEffects.REGENERATION),
            Map.entry(MobEffects.POISON, MobEffects.REGENERATION)
    );

    public static MobEffect getTransmutedEffect(MobEffect effect) {
        MobEffect mapped = TRANSMUTATION_MAP.get(effect);
        if (mapped != null) {
            return mapped;
        }

        if (effect.isBeneficial()) {
            return MobEffects.WEAKNESS;
        } else {
            return ModMobEffects.BLESSING.get();
        }
    }

    @Override
    protected float getInertia() {
        return 1f;
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean isOnFire() {
        return false;
    }

    @Override
    protected boolean shouldBurn() {
        return false;
    }

    @Override
    public void push(Entity pEntity) {}

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(PURIFYING, false);
        this.entityData.define(TRANSMUTATION, false);
        this.entityData.define(SIZE, 1.0f);
    }

    @Override
    protected ParticleOptions getTrailParticle() {
        return new SimpleParticleType(false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);

        ListTag list = new ListTag();
        for (MobEffectInstance effect : effects) {
            list.add(effect.save(new CompoundTag()));
        }

        tag.put("Effects", list);
        tag.putInt("MaxLifetime", maxLifetime);
        tag.putInt("Lifetime", lifetime);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        effects.clear();

        ListTag list = tag.getList("Effects", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            effects.add(MobEffectInstance.load(list.getCompound(i)));
        }

        maxLifetime = tag.getInt("MaxLifetime");
        lifetime = tag.getInt("Lifetime");
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
