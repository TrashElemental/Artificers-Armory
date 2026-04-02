package net.trashelemental.artificers_armory.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;
import net.trashelemental.artificers_armory.ArtificersArmory;
import net.trashelemental.artificers_armory.Config;
import net.trashelemental.artificers_armory.entity.ModEntities;
import net.trashelemental.artificers_armory.item.custom.FirebrandItem;
import net.trashelemental.artificers_armory.junkyard_lib.util.UtilMethods;
import net.trashelemental.artificers_armory.junkyard_lib.visual.particle.ParticleMethods;
import net.trashelemental.artificers_armory.magic.effects.ModMobEffects;
import net.trashelemental.artificers_armory.util.event.FirebrandLightEvents;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

public class FireballEntity extends AbstractHurtingProjectile implements GeoEntity {
    private int damage;
    private int lifetime;
    private int maxLifetime;
    private int burnSeconds;
    private boolean isSoulFire;
    private BlockPos lastLightPos = null;
    private BlockPos currentLightPos;

    public static final EntityDataAccessor<Boolean> SOUL_FIRE = SynchedEntityData.defineId(FireballEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> SHOULD_EXPLODE = SynchedEntityData.defineId(FireballEntity.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Float> SIZE = SynchedEntityData.defineId(FireballEntity.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Integer> SOUL_BURN_LEVEL = SynchedEntityData.defineId(FireballEntity.class, EntityDataSerializers.INT);

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SOUL_FIRE, false);
        this.entityData.define(SHOULD_EXPLODE, false);
        this.entityData.define(SIZE, 1.0f);
        this.entityData.define(SOUL_BURN_LEVEL, -1);
    }

    public FireballEntity(EntityType<? extends FireballEntity> entityType, Level level) {
        super(entityType, level);
        this.lifetime = 0;
        this.maxLifetime = 50;
        this.damage = 2;
        this.burnSeconds = 3;
        this.isSoulFire = false;
    }

    public FireballEntity(Level level, LivingEntity owner, Vec3 direction, int damage, int burnSeconds, boolean isSoulFire) {
        this(ModEntities.FIREBALL_ENTITY.get(), level);
        this.setOwner(owner);
        this.setPos(owner.getX(), owner.getEyeY() - 0.5, owner.getZ());
        this.setDeltaMovement(direction.scale(0.5));

        this.damage = damage;
        this.burnSeconds = burnSeconds;
        this.isSoulFire = isSoulFire;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public void setLifetime(int lifetime) {
        this.maxLifetime = lifetime;
    }

    public void setBurnSeconds(int burnSeconds) {
        this.burnSeconds = burnSeconds;
    }

    public boolean isSoulFire() {
        return this.entityData.get(SOUL_FIRE);
    }

    public void setSoulFire(boolean value) {
        this.entityData.set(SOUL_FIRE, value);
    }

    public boolean shouldExplode() {
        return this.entityData.get(SHOULD_EXPLODE);
    }

    public void setShouldExplode(boolean value) { this.entityData.set(SHOULD_EXPLODE, value); }

    public float size() {
        return this.entityData.get(SIZE);
    }

    public void setSize(float size) { this.entityData.set(SIZE, size); }

    public void setSoulBurnLevel(int level) { this.entityData.set(SOUL_BURN_LEVEL, level);}

    public int getSoulBurnLevel() {
        return this.getEntityData().get(SOUL_BURN_LEVEL);
    }

    @Override
    protected AABB getBoundingBoxForPose(Pose pose) {
        AABB baseBox = super.getBoundingBoxForPose(pose);
        float size = this.entityData.get(SIZE);
        Vec3 center = baseBox.getCenter();
        double width = baseBox.getXsize() * size;
        double height = baseBox.getYsize() * size;
        double depth = baseBox.getZsize() * size;
        return new AABB(center.x - width / 2, center.y - height / 2, center.z - depth / 2,
                center.x + width / 2, center.y + height / 2, center.z + depth / 2);
    }

    @Override
    public void tick() {
        super.tick();
        lifetime++;

        if (!level().isClientSide) {
            BlockPos pos = this.blockPosition();
            BlockState state = level().getBlockState(pos);

            // Fireball destroys cobwebs and powder snow.
            if (state.getBlock() instanceof WebBlock || state.getBlock() instanceof PowderSnowBlock) {
                level().destroyBlock(pos, false);

                ParticleMethods.ParticlesBurst(level(), ParticleTypes.LAVA,
                        getX(), getY(), getZ(), 8, 0.4);

                level().playSound(null, pos,
                        SoundEvents.FIRE_EXTINGUISH, SoundSource.NEUTRAL, 0.4F, 1.0F);

                removeLight();
                this.discard();
                return;
            }

            // Fireball fizzles out if it reaches the end of its lifespan or enters water while not soul fire.
            if (lifetime >= maxLifetime || (this.isInWater() && !this.isSoulFire())) {
                ParticleMethods.ParticlesBurst(level(), getDestroyParticle(),
                        getX(), getY() + 1, getZ(), 5, 0.1);

                level().playSound(null, getX(), getY(), getZ(),
                        SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS, 0.3F, 1F);

                if (this.shouldExplode()) {
                    triggerExplosion();
                }
                removeLight();
                this.discard();
            }


        }

        if (Config.FIREBRANDS_SHED_LIGHT.get() && !this.isRemoved()) {
            updateProjectileLight();
        }

        // Particle trail
        if (level().isClientSide && (this.tickCount % 3 == 0)) {
            ParticleOptions particle = this.getFollowingParticle();
            if (particle != null) {
                double spread = 0.4;
                double offsetX = (random.nextDouble() - 0.5) * spread;
                double offsetY = (random.nextDouble() - 0.5) * spread;
                double offsetZ = (random.nextDouble() - 0.5) * spread;
                level().addParticle(particle, getX() + offsetX, getY() + 0.25 + offsetY, getZ() + offsetZ, 0.0, 0.0, 0.0);
            }
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);

        Level level = this.level();

        if (!level.isClientSide) {
            Entity entity = result.getEntity();
            Entity owner = this.getOwner();
            DamageSource damageSource = this.damageSources().indirectMagic(this, owner);

            // Damage End Crystals
            if (entity instanceof EndCrystal endCrystal) {
                endCrystal.hurt(damageSource, 1F);
            }

            if (entity instanceof LivingEntity target) {

                if (this.isSoulFire() || (!this.isSoulFire() && !(target.fireImmune()))) {
                    UtilMethods.damageEntity(target, DamageTypes.MAGIC, damage);
                }

                applySoulBurn(target);

                if (!target.fireImmune()) {
                    if (target.isOnFire()) {
                        if (target.getRemainingFireTicks() < burnSeconds) {
                            target.setRemainingFireTicks(burnSeconds);
                        }
                    } else {
                        target.setSecondsOnFire(burnSeconds);
                    }
                }

                if (this.shouldExplode()) {
                    triggerExplosion();
                }

                level.playSound(null, this.getX(), this.getY(), this.getZ(),
                        SoundEvents.PLAYER_HURT_ON_FIRE, SoundSource.PLAYERS, 0.5F, 1F);
            }
            removeLight();
            this.discard();
        }
    }


    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);

        Level level = this.level();
        BlockPos blockPos = result.getBlockPos();
        BlockState blockState = level.getBlockState(blockPos);

        if (!level.isClientSide) {
            ParticleMethods.ParticlesBurst(this.level(), getDestroyParticle(),
                    this.getX(), this.getY(), this.getZ(), 5, 0.1);
            level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS, 0.3F, 1F);

            //Trigger target blocks
            if (blockState.getBlock() instanceof TargetBlock targetBlock) {
                targetBlock.onProjectileHit(level, blockState, result, this);
            }

            //Destroy certain blocks if it hits them.
            if (shouldBreakBlock(blockState)) {
                level.destroyBlock(blockPos, false);
            }

            if (this.shouldExplode()) {
                triggerExplosion();
            }
        }
        removeLight();
        this.discard();
    }

    private void triggerExplosion() {
        if (!shouldExplode() || level().isClientSide) return;

        double radius = 3.0;
        double centerX = this.getX();
        double centerY = this.getY();
        double centerZ = this.getZ();
        int explosionDamage = Math.max(1, damage / 2);

        ParticleMethods.ParticlesAroundServerSide(level(), ParticleTypes.EXPLOSION,
                centerX, centerY, centerZ, 5, 3);

        level().playSound(null, centerX, centerY, centerZ,
                SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 0.5F, 1F);

        AABB area = new AABB(centerX - radius, centerY - radius, centerZ - radius,
                centerX + radius, centerY + radius, centerZ + radius);

        for (LivingEntity entity : level().getEntitiesOfClass(LivingEntity.class, area)) {
            if (entity == this.getOwner()) continue;

            UtilMethods.damageEntity(entity, DamageTypes.MAGIC, explosionDamage);

            if (!entity.fireImmune()) {
                if (entity.isOnFire()) {
                    entity.setRemainingFireTicks(Math.max(entity.getRemainingFireTicks(), 2 * 20));
                } else {
                    entity.setSecondsOnFire(2);
                }
            }
        }
    }

    private void applySoulBurn(LivingEntity entity) {
        int level = getSoulBurnLevel();
        if (level < 0) return;

        MobEffect effect = ModMobEffects.SOUL_BURN.get();
        MobEffectInstance soulBurn = new MobEffectInstance(effect, this.burnSeconds * 20, level, false, false);

        MobEffectInstance existing = entity.getEffect(effect);
        if (existing == null || existing.getAmplifier() < level) {
            entity.addEffect(soulBurn);
        }
    }

    public static boolean shouldBreakBlock(BlockState state) {
        return state.getBlock() instanceof SnowLayerBlock ||
                state.getBlock() instanceof IceBlock ||
                state.is(Blocks.SNOW_BLOCK);
    }

    protected ParticleOptions getFollowingParticle() {
        if (this.isSoulFire()) {
            return ParticleTypes.SOUL_FIRE_FLAME;
        }
        return ParticleTypes.SMALL_FLAME;
    }

    protected ParticleOptions getDestroyParticle() {
        if (this.isSoulFire()) {
            return ParticleTypes.SOUL;
        }
        return ParticleTypes.LAVA;
    }

    //Projectile Boosting from Ultrakill because why not
    @Override
    public boolean hurt(DamageSource source, float amount) {

        Entity entity = source.getEntity();

        if (entity != this.getOwner()) return false;

        if (this.tickCount <= 5) {
            if (!shouldExplode()) setShouldExplode(true);
            setDamage((int) (damage * 1.5f));

            if (this.getOwner() instanceof ServerPlayer player) {
                UtilMethods.grantAdvancement(player, "projectile_boost");
            }
            return super.hurt(source, amount);
        }

        return false;
    }

    @Override
    protected ParticleOptions getTrailParticle() {
        return new SimpleParticleType(false);
    }

    @Override
    protected boolean canHitEntity(Entity target) {
        return !(target instanceof FireballEntity) && !(target == this.getOwner()) && super.canHitEntity(target);
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return !(entity instanceof FireballEntity) && !(entity == this.getOwner()) && super.canCollideWith(entity);
    }

    @Override
    protected float getInertia() {
        return 1.0F;
    }

    @Override
    protected boolean shouldBurn() {
        return false;
    }


    public ResourceLocation getTexture() {
        if (this.isSoulFire()) {
            return new ResourceLocation(ArtificersArmory.MOD_ID, "textures/entity/soul_fireball.png");
        }
        return new ResourceLocation(ArtificersArmory.MOD_ID, "textures/entity/fireball.png");
    }

    private void updateProjectileLight() {
        BlockPos newPos = this.blockPosition();

        if (!newPos.equals(lastLightPos)) {
            removeLight();

            BlockPos lightPos = FirebrandLightEvents.findValidLightPos(level(), this);
            if (lightPos != null) {
                level().setBlock(lightPos,
                        Blocks.LIGHT.defaultBlockState().setValue(LightBlock.LEVEL, 15),
                        Block.UPDATE_CLIENTS);
                lastLightPos = lightPos;
            }
        }
    }

    private void removeLight() {
        if (lastLightPos != null) {
            BlockState state = level().getBlockState(lastLightPos);
            if (state.is(Blocks.LIGHT)) {
                level().setBlock(lastLightPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_CLIENTS);
            }
            lastLightPos = null;
        }
    }

    @Override
    public void remove(RemovalReason reason) {
        if (!level().isClientSide) {
            removeLight();
        }
        super.remove(reason);
    }

    //GeckoLib
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 4, this::predicate));
    }

    private PlayState predicate(AnimationState<GeoAnimatable> state) {

        state.getController().setAnimation(RawAnimation.begin().then("fly", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;

    }

    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
