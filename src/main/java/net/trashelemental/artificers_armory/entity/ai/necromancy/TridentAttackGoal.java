package net.trashelemental.artificers_armory.entity.ai.necromancy;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class TridentAttackGoal extends RangedAttackGoal {
    private final RangedAttackMob mob;

    public TridentAttackGoal(RangedAttackMob mob, double speedModifier, int attackInterval, float attackRadius) {
        super(mob, speedModifier, attackInterval, attackRadius);
        this.mob = mob;
    }

    @Override
    public boolean canUse() {
        ItemStack mainHand = ItemStack.EMPTY;
        if (mob instanceof LivingEntity living) {
            mainHand = living.getMainHandItem();
        }
        return super.canUse() && mainHand.is(Items.TRIDENT);
    }

    @Override
    public void start() {
        super.start();
        if (mob instanceof Mob m) m.setAggressive(true);
        if (mob instanceof LivingEntity living) living.startUsingItem(InteractionHand.MAIN_HAND);
    }

    @Override
    public void stop() {
        super.stop();
        if (mob instanceof LivingEntity living) living.stopUsingItem();
        if (mob instanceof Mob m) m.setAggressive(false);
    }
}
