package net.trashelemental.artificers_armory.junkyard_lib.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/**
 * A basic tamed mob that can be tamed if wild, and can be interacted with to cycle its
 * behavior between follow, stay, and wander, sending an action bar message.
 *
 */

@SuppressWarnings("Deprecated")
public class TamableEntity extends TamableAnimal {

    /**
     * The item that can be used to tame the mob if it is not tamed.
     */
    private final Item tameItem;
    /**
     * The food/breed item for the mob. Pass in 'null' if you don't want it to
     * be able to breed, and override the breed offspring to be null as well just in case.
     */
    private final Item breedItem;

    public TamableEntity(EntityType<? extends TamableAnimal> entityType, Level level, Item tameItem, Item breedItem) {
        super(entityType, level);
        this.tameItem = tameItem;
        this.breedItem = breedItem;
    }

    public static boolean isTame(TamableEntity entity) {
        return entity.isTame();
    }


    /**
     * Item that can be used to breed it.
     */
    @Override
    public boolean isFood(ItemStack itemStack) {
        return itemStack.is(breedItem);
    }

    /**
     * Make this null and remove breed goals if you don't want your entity to breed.
     */
    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return (AgeableMob) this.getType().create(serverLevel);
    }

    /**
     * Prevents the tamed mob from attacking your other tamed mobs if you do so by accident.
     */
    @Override
    public void tick() {
        super.tick();

        LivingEntity target = this.getTarget();
        LivingEntity owner = this.getOwner();

        if (!(owner == null)) {
            if (target instanceof TamableAnimal ta && !(ta.getOwner() == null) && (ta.isOwnedBy(owner))) {
                this.setTarget(null);
            }
        }
    }

    /**
     * Mob interact behavior.
     */
    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);

        if (!this.isTame() && itemstack.is(tameItem)) {
            this.usePlayerItem(player, hand, itemstack);
            if (!this.level().isClientSide) {
                if (this.random.nextInt(5) == 0) {
                    this.tame(player);
                    behavior = Behavior.FOLLOW;
                    onTamed(player);
                    this.level().broadcastEntityEvent(this, (byte) 7);
                } else {
                    this.level().broadcastEntityEvent(this, (byte) 6);
                }
                this.setPersistenceRequired();
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide());
        }

        if (this.isTame() && this.isOwnedBy(player) && !(itemstack.is(breedItem))) {

            if (!this.level().isClientSide) {
                cycleBehavior(player);
            }

            return InteractionResult.sidedSuccess(this.level().isClientSide());
        }
        return super.mobInteract(player, hand);
    }


    /**
     * Behavior NBT and cycling method.
     */
    private void setBehaviorInPersistentData() {
        this.getPersistentData().putString("Behavior", behavior.name());
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putString("Behavior", behavior.name());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Behavior")) {
            behavior = Behavior.valueOf(compound.getString("Behavior"));
        }
    }

    public enum Behavior {
        FOLLOW, WANDER, STAY
    }

    public Behavior behavior = Behavior.WANDER;

    public boolean isFollowing() { return behavior == Behavior.FOLLOW; }
    public boolean isWandering() { return behavior == Behavior.WANDER; }
    public boolean isStaying() { return behavior == Behavior.STAY; }

    private void cycleBehavior(Player player) {
        behavior = switch (behavior) {
            case FOLLOW -> Behavior.WANDER;
            case WANDER -> Behavior.STAY;
            case STAY -> Behavior.FOLLOW;
        };
        player.displayClientMessage(Component.literal(getEntityName() + " will " + behavior.name().toLowerCase()), true);
        setBehaviorInPersistentData();
        onBehaviorChanged(behavior);
    }

    protected void onTamed(Player owner) {}
    protected void onBehaviorChanged(Behavior newBehavior) {}

    /**
     * Gets the entity's custom name if it has one, or defaults to its lang entry.
     * Useful for unique skins for custom names.
     */
    public String getEntityName() {
        if (this.hasCustomName()) {
            return this.getCustomName().getString();
        }
        return this.getType().getDescription().getString();
    }


}
