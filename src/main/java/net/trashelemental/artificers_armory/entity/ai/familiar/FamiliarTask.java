package net.trashelemental.artificers_armory.entity.ai.familiar;

import net.minecraft.world.damagesource.DamageSource;
import net.trashelemental.artificers_armory.entity.custom.FamiliarEntity;

public interface FamiliarTask {

    boolean canRun(FamiliarEntity familiar);

    int getWeight(FamiliarEntity familiar);

    void start(FamiliarEntity familiar);

    void tick(FamiliarEntity familiar);

    int getDuration(FamiliarEntity familiar);

    default boolean canTrigger(FamiliarEntity familiar, DamageSource source) {
        return false;
    }

    default boolean canTriggerFall(FamiliarEntity familiar, float fallDistance) {
        return false;
    }

    void stop(FamiliarEntity familiar);

    default boolean isInterruptible() {
        return true;
    }
}