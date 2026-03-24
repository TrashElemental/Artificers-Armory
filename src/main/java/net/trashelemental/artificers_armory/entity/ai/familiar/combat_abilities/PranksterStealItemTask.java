package net.trashelemental.artificers_armory.entity.ai.familiar.combat_abilities;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.trashelemental.artificers_armory.entity.ai.familiar.FamiliarRole;
import net.trashelemental.artificers_armory.entity.ai.familiar.FamiliarTask;
import net.trashelemental.artificers_armory.entity.custom.FamiliarEntity;
import net.trashelemental.artificers_armory.junkyard_lib.util.UtilMethods;
import net.trashelemental.artificers_armory.junkyard_lib.visual.particle.ParticleMethods;
import net.trashelemental.artificers_armory.particle.ModParticles;
import net.trashelemental.artificers_armory.util.event.FirebrandEvents;

import java.util.ArrayList;
import java.util.List;

public class PranksterStealItemTask implements FamiliarTask {

    private LivingEntity target;

    @Override
    public boolean isInterruptible() {
        return false;
    }

    @Override
    public boolean canRun(FamiliarEntity familiar) {
        target = null;
        if (!familiar.getCarriedItem().isEmpty()) return false;
        if (familiar.getRole() != FamiliarRole.PRANKSTER) return false;
        if (!(familiar.getOwner() instanceof Player owner)) return false;
        List<LivingEntity> nearby = familiar.level().getEntitiesOfClass(LivingEntity.class, familiar.getBoundingBox().inflate(12));
        LivingEntity best = null;
        double bestDistance = Double.MAX_VALUE;

        for (LivingEntity entity : nearby) {
            if (entity == owner) continue;
            if (entity == familiar) continue;
            if (!entity.isAlive()) continue;
            if (FirebrandEvents.isAlly(owner, entity)) continue;
            double dist = entity.distanceToSqr(owner);
            if (dist < bestDistance) {
                bestDistance = dist;
                best = entity;
            }
        }
        target = best;
        return best != null;
    }

    @Override
    public int getWeight(FamiliarEntity familiar) {
        return 2;
    }

    @Override
    public void start(FamiliarEntity familiar) {
        if (target == null || !target.isAlive()) return;
        familiar.freezeMovement();
        familiar.getLookControl().setLookAt(target);
        familiar.noPhysics = true;
        familiar.setInvulnerable(true);
        familiar.moveTo(target.getX(), target.getEyeY(), target.getZ());


        ParticleMethods.ParticlesAroundServerSide(familiar.level(), ParticleTypes.SMOKE,
                target.getX(), target.getEyeY(), target.getZ(), 5, 1.2);
        familiar.level().playSound(null, familiar.blockPosition(),
                SoundEvents.VEX_CHARGE, SoundSource.NEUTRAL, 0.5f, 1f);
        familiar.triggerAnim("behavior", "swirl");
    }

    @Override
    public void tick(FamiliarEntity familiar) {
        if (target != null && target.isAlive()) {
            familiar.freezeMovement();
            familiar.getLookControl().setLookAt(target);
            familiar.moveTo(target.getX(), target.getEyeY(), target.getZ());
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
        ItemStack stolen = ItemStack.EMPTY;
        if (target == null || !target.isAlive()) {
            target = null;
            return;
        }

        ItemStack main = target.getMainHandItem();
        if (!main.isEmpty()) {
            stolen = main.copy();
            target.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        }

        if (stolen.isEmpty()) {
            ItemStack off = target.getOffhandItem();
            if (!off.isEmpty()) {
                stolen = off.copy();
                target.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
            }
        }

        if (stolen.isEmpty()) {
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                if (slot.getType() == EquipmentSlot.Type.ARMOR) {
                    ItemStack armor = target.getItemBySlot(slot);
                    if (!armor.isEmpty()) {
                        stolen = armor.copy();
                        target.setItemSlot(slot, ItemStack.EMPTY);
                        break;
                    }
                }
            }
        }

        if (stolen.isEmpty() && target instanceof Player player) {
            List<Integer> filledSlots = new ArrayList<>();
            for (int i = 0; i < player.getInventory().items.size(); i++) {
                if (!player.getInventory().items.get(i).isEmpty()) {
                    filledSlots.add(i);
                }
            }
            if (!filledSlots.isEmpty()) {
                int slot = filledSlots.get(familiar.getRandom().nextInt(filledSlots.size()));
                ItemStack stack = player.getInventory().items.get(slot);

                stolen = stack.copy();
                player.getInventory().items.set(slot, ItemStack.EMPTY);
            }
        }

        if (stolen.isEmpty() && target instanceof Mob mob) {

            LootTable table = familiar.level().getServer().getLootData().getLootTable(mob.getLootTable());
            DamageSource source = familiar.damageSources().magic();

            LootParams params = new LootParams.Builder((ServerLevel) familiar.level())
                    .withParameter(LootContextParams.THIS_ENTITY, mob)
                    .withParameter(LootContextParams.ORIGIN, mob.position())
                    .withParameter(LootContextParams.DAMAGE_SOURCE, source)
                    .create(LootContextParamSets.ENTITY);

            List<ItemStack> generated = table.getRandomItems(params);
            if (!generated.isEmpty()) {
                stolen = generated.get(familiar.getRandom().nextInt(generated.size())).copy();
            }
        }

        if (stolen.isEmpty()) {
            UtilMethods.applyEffectWithParticles(target, MobEffects.WITHER, 60, 0);
        }


        if (!stolen.isEmpty() && familiar.isRetrievingItems() && familiar.getOwner() instanceof Player owner) {
            owner.getInventory().placeItemBackInInventory(stolen);
            familiar.level().playSound(null, familiar.blockPosition(),
                    SoundEvents.ITEM_PICKUP, SoundSource.NEUTRAL, 0.6f, 1.2f);
        }

        else if (!stolen.isEmpty()) {
            ItemEntity itemEntity = familiar.spawnAtLocation(stolen);

            if (itemEntity != null) {
                Vec3 randomOffset = new Vec3((familiar.getRandom().nextDouble() - 0.5) * 0.6, 0.3,
                        (familiar.getRandom().nextDouble() - 0.5) * 0.6).normalize().scale(0.4);

                itemEntity.setDeltaMovement(randomOffset);
                itemEntity.setPickUpDelay(20);
            }

            familiar.level().playSound(null, familiar.blockPosition(),
                    SoundEvents.ITEM_PICKUP, SoundSource.NEUTRAL, 0.6f, 1.2f);

        }

        ParticleMethods.ParticlesAroundServerSide(familiar.level(), ModParticles.IMP.get(),
                target.getX(), target.getEyeY(), target.getZ(), 5, 2);

        target = null;
    }
}