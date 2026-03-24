package net.trashelemental.artificers_armory.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.trashelemental.artificers_armory.ArtificersArmory;
import net.trashelemental.artificers_armory.entity.ai.familiar.*;
import net.trashelemental.artificers_armory.entity.ai.necromancy.MinionDefendOwnerGoal;
import net.trashelemental.artificers_armory.item.custom.ChimeItem;
import net.trashelemental.artificers_armory.junkyard_lib.entity.TamableEntity;
import net.trashelemental.artificers_armory.junkyard_lib.visual.particle.ParticleMethods;
import net.trashelemental.artificers_armory.util.event.FirebrandEvents;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;

import java.util.List;

public class FamiliarEntity extends TamableEntity implements GeoEntity {
    public FamiliarEntity(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level, null, null);
        this.moveControl = new FlyingMoveControl(this, 20, true);
        this.setNoGravity(true);
    }

    private static final double TELEPORT_DISTANCE = 32;
    private static final int TELEPORT_CHECK_INTERVAL = 20;

    @Override
    protected void registerGoals() {
        super.registerGoals();
        {
            this.goalSelector.addGoal(0, new OwnerHurtByTargetGoal(this) {
                @Override
                public boolean canUse() {
                    return super.canUse() && !isStaying();
                }

                @Override
                public boolean canContinueToUse() {
                    return super.canContinueToUse() && !isStaying();
                }
            });
            this.targetSelector.addGoal(1, new OwnerHurtTargetGoal(this) {
                @Override
                public boolean canUse() {
                    return super.canUse() && !isStaying();
                }

                @Override
                public boolean canContinueToUse() {
                    return super.canContinueToUse() && !isStaying();
                }
            });
            this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
            this.goalSelector.addGoal(3, new MinionDefendOwnerGoal(this) {
                @Override
                public boolean canUse() {
                    return super.canUse() && !isTaskActive() && !isNonCombatRole()
                            && getRole() == FamiliarRole.BRUISER && !isStaying();
                }

                @Override
                public boolean canContinueToUse() {
                    return super.canContinueToUse() && !isTaskActive()
                            && getRole() == FamiliarRole.BRUISER && !isStaying();
                }
            });
            this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.2, false) {
                @Override
                public boolean canUse() {
                    return super.canUse() && !isTaskActive() && !isNonCombatRole() && !isStaying();
                }

                @Override
                public boolean canContinueToUse() {
                    return super.canContinueToUse() && !isTaskActive() && !isStaying();
                }
            });
            this.goalSelector.addGoal(5, new FollowOwnerGoal(this, 1, 10, 2, true) {
                @Override
                public boolean canUse() {
                    return super.canUse() && !isTaskActive() && isFollowing();
                }

                @Override
                public boolean canContinueToUse() {
                    return super.canContinueToUse() && !isTaskActive() && isFollowing();
                }
            });
            this.goalSelector.addGoal(6, new CollectItemsGoal(this) {
                @Override
                public boolean canUse() {
                    return super.canUse() && !isTaskActive() && !isAggressive() && !isStaying();
                }

                @Override
                public boolean canContinueToUse() {
                    return super.canContinueToUse() && !isTaskActive() && !isAggressive() && !isStaying();
                }
            });
            this.goalSelector.addGoal(11, new WaterAvoidingRandomFlyingGoal(this, 1) {
                @Override
                public boolean canUse() {
                    return super.canUse() && !isTaskActive() && !isStaying();
                }

                @Override
                public boolean canContinueToUse() {
                    return super.canContinueToUse() && !isTaskActive() && !isStaying();
                }
            });
            this.goalSelector.addGoal(12, new LookAtPlayerGoal(this, Player.class, (float) 6) {
                @Override
                public boolean canUse() {
                    return super.canUse() && !isTaskActive();
                }

                @Override
                public boolean canContinueToUse() {
                    return super.canContinueToUse() && !isTaskActive();
                }
            });
            this.goalSelector.addGoal(13, new RandomLookAroundGoal(this) {
                @Override
                public boolean canUse() {
                    return super.canUse() && !isTaskActive();
                }

                @Override
                public boolean canContinueToUse() {
                    return super.canContinueToUse() && !isTaskActive();
                }
            });
        }
    }

    private boolean isNonCombatRole() {
        return this.getRole() == FamiliarRole.HEALER || this.getRole() == FamiliarRole.PRANKSTER;
    }

    public boolean canCollect() {
        return this.isTame() && !this.isAggressive();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 20)
                .add(Attributes.MOVEMENT_SPEED, 0.6D)
                .add(Attributes.ATTACK_DAMAGE, 3)
                .add(Attributes.ARMOR, 0)
                .add(Attributes.FOLLOW_RANGE, 16)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.3)
                .add(Attributes.FLYING_SPEED, 0.6D)
                .add(Attributes.ATTACK_KNOCKBACK, 0);
    }

    private FamiliarAwareness awareness = new FamiliarAwareness();
    public int idleCooldown = 200;
    public int combatCooldown = 60;
    public int triggeredCooldown = 200;
    public int itemPickupCooldown = 100;

    public void setIdleCooldown(int ticks) {
        this.idleCooldown = ticks;
    }

    public void setCombatCooldown(int ticks) {
        this.combatCooldown = ticks;
    }

    public void setTriggeredCooldown(int ticks) {
        this.triggeredCooldown = ticks;
    }

    public void setTaskTimer(int ticks) {
        this.taskTimer = ticks;
    }

    private FamiliarTask activeTask;
    private int taskTimer;

    public void startTask(FamiliarTask task, int duration) {
        this.activeTask = task;
        this.taskTimer = duration;
    }

    public FamiliarTask getCurrentTask() {
        return this.activeTask;
    }

    public FamiliarAwareness getAwareness() {
        return awareness;
    }

    public void setAwareness(FamiliarAwareness awareness) {
        this.awareness = awareness;
    }

    public void setItemPickupCooldown(int itemPickupCooldown) {
        this.itemPickupCooldown = itemPickupCooldown;
    }

    public int getItemPickupCooldown() {
        return itemPickupCooldown;
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide) {

            if (this.tickCount % TELEPORT_CHECK_INTERVAL == 0) {
                tryTeleportToOwner();
            }

            if (this.tickCount % 40 == 0) {
                AttributeInstance move = this.getAttribute(Attributes.MOVEMENT_SPEED);
                AttributeInstance fly = this.getAttribute(Attributes.FLYING_SPEED);
                if (move != null && fly != null) {
                    fly.setBaseValue(move.getValue());
                }
            }

            if (this.isSpawning()) {
                int timer = this.getSpawnTimer() - 1;
                this.setSpawnTimer(timer);

                if (timer <= 0) {
                    this.setSpawning(false);
                    this.setNoAi(false);
                    this.setInvulnerable(false);
                    this.playSound(SoundEvents.ALLAY_AMBIENT_WITH_ITEM, 0.3f, 1);
                }
            }

            if (!this.isDespawning() && this.getShouldDespawn()) {
                int age = this.entityData.get(AGE_TICKS) + 1;
                this.entityData.set(AGE_TICKS, age);

                if (age >= this.getLifespan() && !isTaskActive()) {
                    FamiliarAI.despawnFromLifespan(this);
                }
            }

            if (this.isDespawning()) {
                int timer = this.getDespawnTimer() - 1;
                this.setDespawnTimer(timer);

                if (timer <= 0) {
                    this.playSound(SoundEvents.AMETHYST_BLOCK_BREAK, 0.2f, 1.5f);
                    ParticleMethods.ParticlesBurst(this.level(), ParticleTypes.FIREWORK,
                            this.getX(), this.getEyeY(), this.getZ(), 5, 0.05);
                    this.discard();
                }
            }

            if (itemPickupCooldown > 0) {
                itemPickupCooldown--;
            }

            if (idleCooldown > 0) {
                idleCooldown--;
            }

            if (triggeredCooldown > 0) {
                triggeredCooldown--;
            }

            if (combatCooldown > 0) {
                combatCooldown--;
            }

            if (this.getTarget() != null && combatCooldown <= 0 && !isTaskActive() && hasNearbyEnemy()) {
                FamiliarAI.doCombatAbility(this);
            }

            if (idleCooldown <= 0 && !isTaskActive() && !this.hasCarriedItem()) {
                if (this.getTarget() == null || !this.getTarget().isAlive()) {
                    FamiliarAI.doIdle(this);
                } else {
                    setIdleCooldown(300);
                }
            }

            if (activeTask != null) {
                FamiliarTask currentTask = activeTask;
                try {
                    currentTask.tick(this);
                } catch (Exception e) {
                    ArtificersArmory.LOGGER.warn("Familiar task crashed, cancelling task", e);
                    cancelCurrentTask();
                }
                taskTimer--;
                if (taskTimer <= 0) {
                    currentTask.stop(this);
                    if (activeTask == currentTask) {
                        activeTask = null;
                    }
                }
                if (taskTimer < -20) {
                    cancelCurrentTask();
                }
            }

            Vec3 motion = this.getDeltaMovement();

            if (Math.abs(motion.y) > 0.05 && activeTask != null) {
                this.setDeltaMovement(motion.x, motion.y * 0.3, motion.z);
            }
        }
    }

    private void tryTeleportToOwner() {
        LivingEntity owner = this.getOwner();
        if (!(owner instanceof Player player)) return;
        if (this.isTaskActive()) return;
        double distance = this.distanceToSqr(player);

        if (distance < TELEPORT_DISTANCE * TELEPORT_DISTANCE) return;
        BlockPos target = player.blockPosition();

        for (int i = 0; i < 10; i++) {
            BlockPos pos = target.offset(
                    this.random.nextInt(7) - 3,
                    this.random.nextInt(3) - 1,
                    this.random.nextInt(7) - 3
            );

            if (canTeleportTo(pos)) {
                this.moveTo(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, this.getYRot(), this.getXRot());
                this.getNavigation().stop();
                return;
            }
        }

        this.triggerAnim("popIn", "POP_IN");
    }

    private boolean canTeleportTo(BlockPos pos) {
        BlockState state = this.level().getBlockState(pos);
        BlockState above = this.level().getBlockState(pos.above());
        return state.getCollisionShape(this.level(), pos).isEmpty()
                && above.getCollisionShape(this.level(), pos.above()).isEmpty();
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack held = this.getCarriedItem();

        LivingEntity owner = this.getOwner();
        if (owner == player) {
            if (player.getItemInHand(hand).getItem() instanceof ChimeItem){
                if (player.isCrouching()) {
                    FamiliarAI.despawnFromLifespan(this);
                    this.level().playSound(null, this.blockPosition(),
                            SoundEvents.ALLAY_THROW, SoundSource.NEUTRAL, 0.4f, 1f);
                    return InteractionResult.SUCCESS;
                } else {
                    if (!this.level().isClientSide) {
                        cycleItemBehavior(player);
                    }
                    return InteractionResult.SUCCESS;
                }
            }
        }

        //If holding an item, it can be interacted with by its owner to take it.
        if (!this.level().isClientSide && this.isTame() && this.isOwnedBy(player) && !held.isEmpty()) {
            ItemStack copy = held.copy();
            boolean added = player.addItem(copy);
            if (!added) {
                ItemEntity it = new ItemEntity(this.level(), player.getX(), player.getY(), player.getZ(), copy);
                this.level().addFreshEntity(it);
            }
            this.setCarriedItem(ItemStack.EMPTY);
            this.playSound(SoundEvents.ITEM_PICKUP, 1.0F, 1.0F);
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }

        return super.mobInteract(player, hand);
    }

    public enum ItemBehavior {
        RETRIEVE, DEPOSIT, IGNORE
    }

    public FamiliarEntity.ItemBehavior itemBehavior = ItemBehavior.RETRIEVE;

    private void setItemBehaviorInPersistentData() {
        this.getPersistentData().putString("ItemBehavior", itemBehavior.name());
    }

    public boolean isRetrievingItems() { return itemBehavior == FamiliarEntity.ItemBehavior.RETRIEVE; }
    public boolean isDepositingItems() { return itemBehavior == FamiliarEntity.ItemBehavior.DEPOSIT; }
    public boolean isIgnoringItems() { return itemBehavior == FamiliarEntity.ItemBehavior.IGNORE; }

    private void cycleItemBehavior(Player player) {
        itemBehavior = switch (itemBehavior) {
            case RETRIEVE -> ItemBehavior.DEPOSIT;
            case DEPOSIT -> ItemBehavior.IGNORE;
            case IGNORE -> ItemBehavior.RETRIEVE;
        };
        setItemBehaviorInPersistentData();

        String key = switch (itemBehavior) {
            case RETRIEVE -> "message.artificers_armory.familiar_retrieve_items";
            case DEPOSIT -> "message.artificers_armory.familiar_deposit_items";
            case IGNORE -> "message.artificers_armory.familiar_ignore_items";
        };
        player.displayClientMessage(Component.translatable(key, getEntityName()), true);
    }

    private boolean hasNearbyEnemy() {
        if (!(this.getOwner() instanceof Player owner)) return false;
        List<LivingEntity> nearby = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(12));
        for (LivingEntity entity : nearby) {
            if (entity == owner) continue;
            if (!entity.isAlive()) continue;
            if (FirebrandEvents.isAlly(owner, entity)) continue;
            return true;
        }
        return false;
    }

    protected PathNavigation createNavigation(Level pLevel) {
        FlyingPathNavigation flyingpathnavigation = new FlyingPathNavigation(this, pLevel);
        flyingpathnavigation.setCanOpenDoors(false);
        flyingpathnavigation.setCanFloat(true);
        flyingpathnavigation.setCanPassDoors(true);
        return flyingpathnavigation;
    }


    protected float getStandingEyeHeight(Pose pPose, EntityDimensions pDimensions) {
        return pDimensions.height * 0.6F;
    }

    protected void playStepSound(BlockPos pPos, BlockState pState) {
    }

    protected void checkFallDamage(double pY, boolean pOnGround, BlockState pState, BlockPos pPos) {
    }

    @Override
    public void playAmbientSound() {
    }

    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return SoundEvents.ALLAY_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.ALLAY_DEATH;
    }

    protected float getSoundVolume() {
        return 0.1F;
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        if (!this.level().isClientSide && this.tickCount < 5) {
            FamiliarAI.doSpawnAnimation(this);
            this.behavior = Behavior.FOLLOW;
        }
    }

    @Override
    public int getExperienceReward() {
        return 0;
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        if (entity == this.getOwner()) {
            return false;
        }
        return super.canCollideWith(entity);
    }

    // Cancel any idle behavior if it gets hurt
    @Override
    protected void actuallyHurt(DamageSource pDamageSource, float pDamageAmount) {

        cancelCurrentTask();
        resetIdleCooldown();
        dropItem();

        super.actuallyHurt(pDamageSource, pDamageAmount);
    }

    public void cancelCurrentTask() {
        if (activeTask != null) {
            FamiliarTask task = activeTask;
            activeTask = null;
            task.stop(this);
        }
        taskTimer = 0;
    }

    public void dropItem() {
        ItemStack held = this.getCarriedItem();
        if (!held.isEmpty()) {
            ItemStack copy = held.copy();
            ItemEntity it = new ItemEntity(this.level(), this.getX(), this.getY(), this.getZ(), copy);
            this.level().addFreshEntity(it);
            this.setCarriedItem(ItemStack.EMPTY);
            this.playSound(SoundEvents.ITEM_PICKUP, 1.0F, 1.0F);
            this.setItemPickupCooldown(200 + this.getRandom().nextInt(60));
        }
    }

    public void resetIdleCooldown() {
        this.idleCooldown = 200 + this.getRandom().nextInt(60);
    }

    //Cancel any idle behavior if it's hurting something
    @Override
    public boolean doHurtTarget(Entity pEntity) {
        cancelCurrentTask();
        resetIdleCooldown();
        return super.doHurtTarget(pEntity);
    }

    public void freezeMovement() {
        this.getNavigation().stop();
        this.setDeltaMovement(Vec3.ZERO);
    }

    public boolean isTaskActive() {
        return this.activeTask != null;
    }

    @Override
    public double getMeleeAttackRangeSqr(LivingEntity pEntity) {
        return this.getBbWidth() * 3.0F * this.getBbWidth() * 3.0F + pEntity.getBbWidth();
    }


    @Override
    public void die(DamageSource pCause) {
        this.setNoAi(true);
        if (!this.level().isClientSide) {
            this.triggerAnim("behavior", "death");
        }

        ArtificersArmory.queueServerWork(20, () -> {
                    this.level().broadcastEntityEvent(this, (byte) 60);
                    this.remove(Entity.RemovalReason.KILLED);
                    super.die(pCause);
                }
        );
    }

    @Override
    public void remove(RemovalReason pReason) {
        if (this.getOwner() instanceof Player player) {
            player.getPersistentData().remove("artificers_armory_familiar");
        }
        super.remove(pReason);
    }

    @Override
    protected void tickDeath() {

    }

    //Item Carrying
    public ItemStack getCarriedItem() {
        ItemStack stack = this.entityData.get(CARRIED_ITEM);
        return stack == null ? ItemStack.EMPTY : stack;
    }

    public void setCarriedItem(ItemStack stack) {
        if (stack == null) stack = ItemStack.EMPTY;
        this.entityData.set(CARRIED_ITEM, stack.copy());
    }

    public boolean hasCarriedItem() {
        return !this.getCarriedItem().isEmpty();
    }


    //GeckoLib
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 4, this::predicate));

        //Spawning
        controllers.add(new AnimationController<>(this, "popIn", 0, state -> PlayState.STOP)
                .triggerableAnim("POP_IN", RawAnimation.begin().then("POP_IN", Animation.LoopType.PLAY_ONCE)));
        controllers.add(new AnimationController<>(this, "teleportIn", 0, state -> PlayState.STOP)
                .triggerableAnim("TELEPORT_IN", RawAnimation.begin().then("TELEPORT_IN", Animation.LoopType.PLAY_ONCE)));
        controllers.add(new AnimationController<>(this, "riseIn", 0, state -> PlayState.STOP)
                .triggerableAnim("RISE_IN", RawAnimation.begin().then("RISE_IN", Animation.LoopType.PLAY_ONCE)));

        //Despawning
        controllers.add(new AnimationController<>(this, "popOut", 0, state -> PlayState.STOP)
                .triggerableAnim("POP_OUT", RawAnimation.begin().then("WAVE", Animation.LoopType.PLAY_ONCE)
                        .then("POP_OUT", Animation.LoopType.HOLD_ON_LAST_FRAME)));
        controllers.add(new AnimationController<>(this, "teleportOut", 0, state -> PlayState.STOP)
                .triggerableAnim("TELEPORT_OUT", RawAnimation.begin().then("WAVE", Animation.LoopType.PLAY_ONCE)
                        .then("TELEPORT_OUT", Animation.LoopType.HOLD_ON_LAST_FRAME)));
        controllers.add(new AnimationController<>(this, "riseOut", 0, state -> PlayState.STOP)
                .triggerableAnim("RISE_OUT", RawAnimation.begin().then("WAVE", Animation.LoopType.PLAY_ONCE)
                        .then("RISE_OUT", Animation.LoopType.HOLD_ON_LAST_FRAME)));

        //Wave
        controllers.add(new AnimationController<>(this, "wave", 4, state -> PlayState.STOP)
                .triggerableAnim("wave", RawAnimation.begin().then("WAVE", Animation.LoopType.PLAY_ONCE)));

        //Idle
        controllers.add(new AnimationController<>(this, "idle", 4, state -> PlayState.STOP)
                .triggerableAnim("think", RawAnimation.begin().then("THINK", Animation.LoopType.PLAY_ONCE))
                .triggerableAnim("spin", RawAnimation.begin().then("SPIN", Animation.LoopType.PLAY_ONCE))
                .triggerableAnim("curious", RawAnimation.begin().then("CURIOUS", Animation.LoopType.PLAY_ONCE))
                .triggerableAnim("inspect", RawAnimation.begin().then("INSPECT", Animation.LoopType.PLAY_ONCE))
                .triggerableAnim("idea_fail", RawAnimation.begin().then("IDEA_FAIL", Animation.LoopType.PLAY_ONCE))
                .triggerableAnim("scheme", RawAnimation.begin().then("SCHEME", Animation.LoopType.PLAY_ONCE))
                .triggerableAnim("shadow_box", RawAnimation.begin().then("SHADOW_BOX", Animation.LoopType.PLAY_ONCE))
                .triggerableAnim("look", RawAnimation.begin().then("LOOK_AROUND", Animation.LoopType.PLAY_ONCE)));

        //Behavior
        controllers.add(new AnimationController<>(this, "behavior", 4, state -> PlayState.STOP)
                .triggerableAnim("minorSupport", RawAnimation.begin().then("GROW", Animation.LoopType.PLAY_ONCE))
                .triggerableAnim("swirl", RawAnimation.begin().then("SWIRL_AROUND", Animation.LoopType.PLAY_ONCE))
                .triggerableAnim("death", RawAnimation.begin().then("DESPAWN_FROM_DAMAGE", Animation.LoopType.HOLD_ON_LAST_FRAME))
                .triggerableAnim("punch", RawAnimation.begin().then("BIG_PUNCH", Animation.LoopType.PLAY_ONCE))
                .triggerableAnim("uppercut", RawAnimation.begin().then("UPPERCUT", Animation.LoopType.PLAY_ONCE))
                .triggerableAnim("support", RawAnimation.begin().then("SUPPORT", Animation.LoopType.PLAY_ONCE)));
    }

    private PlayState predicate(AnimationState<GeoAnimatable> state) {

        if (state.isMoving()) {
            if (this.isAggressive()) {
                state.getController().setAnimation(RawAnimation.begin().then("MOVE_FAST", Animation.LoopType.LOOP));
            } else {
                if (this.hasCarriedItem()) {
                    state.getController().setAnimation(RawAnimation.begin().then("MOVE_HOLD_ITEM", Animation.LoopType.LOOP));
                } else {
                    state.getController().setAnimation(RawAnimation.begin().then("MOVE", Animation.LoopType.LOOP));
                }
            }
            return PlayState.CONTINUE;
        }

        if (this.hasCarriedItem()) {
            state.getController().setAnimation(RawAnimation.begin().then("IDLE_HOLD_ITEM", Animation.LoopType.LOOP));
        } else {
            state.getController().setAnimation(RawAnimation.begin().then("IDLE", Animation.LoopType.LOOP));
        }
        return PlayState.CONTINUE;

    }

    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    private static final EntityDataAccessor<Integer> SKIN = SynchedEntityData.defineId(FamiliarEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> ROLE = SynchedEntityData.defineId(FamiliarEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> MAX_LIFESPAN = SynchedEntityData.defineId(FamiliarEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> AGE_TICKS = SynchedEntityData.defineId(FamiliarEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> SHOULD_DESPAWN = SynchedEntityData.defineId(FamiliarEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DESPAWNING = SynchedEntityData.defineId(FamiliarEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DESPAWN_TIMER = SynchedEntityData.defineId(FamiliarEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> SPAWNING = SynchedEntityData.defineId(FamiliarEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> SPAWN_TIMER = SynchedEntityData.defineId(FamiliarEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> LEVEL = SynchedEntityData.defineId(FamiliarEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> ENCHANT_LEVEL = SynchedEntityData.defineId(FamiliarEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<ItemStack> CARRIED_ITEM = SynchedEntityData.defineId(FamiliarEntity.class, EntityDataSerializers.ITEM_STACK);

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SKIN, FamiliarSkin.DEFAULT.getId());
        this.entityData.define(ROLE, FamiliarRole.NONE.getId());
        this.entityData.define(MAX_LIFESPAN, 300);
        this.entityData.define(AGE_TICKS, 0);
        this.entityData.define(SHOULD_DESPAWN, true);
        this.entityData.define(DESPAWNING, false);
        this.entityData.define(DESPAWN_TIMER, 0);
        this.entityData.define(SPAWNING, false);
        this.entityData.define(SPAWN_TIMER, 0);
        this.entityData.define(LEVEL, 1);
        this.entityData.define(ENCHANT_LEVEL, 0);
        this.entityData.define(CARRIED_ITEM, ItemStack.EMPTY);
    }

    public void setSkin(FamiliarSkin skin) {
        this.entityData.set(SKIN, skin.getId());
    }

    public FamiliarSkin getSkin() {
        return FamiliarSkin.fromId(this.entityData.get(SKIN));
    }

    public void setRole(FamiliarRole role) {
        this.entityData.set(ROLE, role.getId());
    }

    public FamiliarRole getRole() {
        return FamiliarRole.fromId(this.entityData.get(ROLE));
    }

    public void setLifespan(int lifespan) {
        this.entityData.set(MAX_LIFESPAN, lifespan);
    }

    public int getLifespan() {
        return this.entityData.get(MAX_LIFESPAN);
    }

    public void setShouldDespawn(boolean shouldDespawn) {
        this.entityData.set(SHOULD_DESPAWN, shouldDespawn);
    }

    public boolean getShouldDespawn() {
        return this.entityData.get(SHOULD_DESPAWN);
    }

    public boolean isDespawning() {
        return this.entityData.get(DESPAWNING);
    }

    public void setDespawning(boolean value) {
        this.entityData.set(DESPAWNING, value);
    }

    public int getDespawnTimer() {
        return this.entityData.get(DESPAWN_TIMER);
    }

    public void setDespawnTimer(int ticks) {
        this.entityData.set(DESPAWN_TIMER, ticks);
    }

    public boolean isSpawning() {
        return this.entityData.get(SPAWNING);
    }

    public void setSpawning(boolean value) {
        this.entityData.set(SPAWNING, value);
    }

    public int getSpawnTimer() {
        return this.entityData.get(SPAWN_TIMER);
    }

    public void setSpawnTimer(int ticks) {
        this.entityData.set(SPAWN_TIMER, ticks);
    }

    public int getLevel() {
        return this.entityData.get(LEVEL);
    }

    public void setLevel(int level) {
        this.entityData.set(LEVEL, level);
    }

    public void setEnchantLevel(int level) {
        this.entityData.set(ENCHANT_LEVEL, level);
    }

    public int getEnchantLevel() {
        return this.entityData.get(ENCHANT_LEVEL);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("FamiliarSkin", this.getSkin().getId());
        tag.putInt("FamiliarRole", this.entityData.get(ROLE));
        tag.putInt("MaxLifespan", this.getLifespan());
        tag.putInt("AgeTicks", this.entityData.get(AGE_TICKS));
        tag.putBoolean("ShouldDespawn", this.getShouldDespawn());
        tag.putInt("Level", this.entityData.get(LEVEL));
        tag.putInt("EnchantLevel", this.entityData.get(ENCHANT_LEVEL));
        ItemStack carried = this.getCarriedItem();
        if (!carried.isEmpty()) {
            CompoundTag itemTag = new CompoundTag();
            carried.save(itemTag);
            tag.put("CarriedItem", itemTag);
        }
        tag.putString("ItemBehavior", itemBehavior.name());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("FamiliarSkin")) {
            this.setSkin(FamiliarSkin.fromId(tag.getInt("FamiliarSkin")));
        }
        if (tag.contains("FamiliarRole")) {
            this.entityData.set(ROLE, tag.getInt("FamiliarRole"));
        }
        if (tag.contains("MaxLifespan")) {
            this.setLifespan(tag.getInt("MaxLifespan"));
        }
        if (tag.contains("AgeTicks")) {
            this.entityData.set(AGE_TICKS, tag.getInt("AgeTicks"));
        }
        if (tag.contains("ShouldDespawn")) {
            this.setShouldDespawn(tag.getBoolean("ShouldDespawn"));
        }
        if (tag.contains("Level")) {
            this.entityData.set(LEVEL, tag.getInt("Level"));
        }
        if (tag.contains("EnchantLevel")) {
            this.entityData.set(ENCHANT_LEVEL, tag.getInt("EnchantLevel"));
        }
        if (tag.contains("CarriedItem")) {
            CompoundTag itemTag = tag.getCompound("CarriedItem");
            this.setCarriedItem(ItemStack.of(itemTag));
        } else {
            this.setCarriedItem(ItemStack.EMPTY);
        }
        if (tag.contains("ItemBehavior")) {
            try {
                itemBehavior = ItemBehavior.valueOf(tag.getString("ItemBehavior"));
            } catch (Exception e) {
                itemBehavior = ItemBehavior.RETRIEVE;
            }
        }
    }
}
