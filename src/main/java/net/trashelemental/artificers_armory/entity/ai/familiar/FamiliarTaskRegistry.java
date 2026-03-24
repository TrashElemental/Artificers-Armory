package net.trashelemental.artificers_armory.entity.ai.familiar;

import net.trashelemental.artificers_armory.entity.ai.familiar.combat_abilities.*;
import net.trashelemental.artificers_armory.entity.ai.familiar.passive_abilities.*;
import net.trashelemental.artificers_armory.entity.ai.familiar.passive_behaviors.*;
import net.trashelemental.artificers_armory.entity.ai.familiar.triggered_abilities.*;

import java.util.ArrayList;
import java.util.List;

public class FamiliarTaskRegistry {

    private static final List<FamiliarTask> PASSIVE_BEHAVIORS = new ArrayList<>();
    private static final List<FamiliarTask> PASSIVE_ABILITIES = new ArrayList<>();
    private static final List<FamiliarTask> TRIGGERED_ABILITIES = new ArrayList<>();
    private static final List<FamiliarTask> COMBAT_ABILITIES = new ArrayList<>();
    private static boolean initialized = false;

    public static void registerPassiveBehavior(FamiliarTask task) {
        PASSIVE_BEHAVIORS.add(task);
    }
    public static void registerPassiveAbility(FamiliarTask task) {
        PASSIVE_ABILITIES.add(task);
    }
    public static void registerTriggeredAbility(FamiliarTask task) {
        TRIGGERED_ABILITIES.add(task);
    }
    public static void registerCombatAbility(FamiliarTask task) {
        COMBAT_ABILITIES.add(task);
    }

    public static List<FamiliarTask> getPassiveBehaviors() {return PASSIVE_BEHAVIORS;}
    public static List<FamiliarTask> getPassiveAbilities() {return PASSIVE_ABILITIES;}
    public static List<FamiliarTask> getTriggeredAbilities() {return TRIGGERED_ABILITIES;}
    public static List<FamiliarTask> getCombatAbilities() {return COMBAT_ABILITIES;}


    public static void register() {
        if (initialized) return;
        initialized = true;

        registerPassiveBehavior(new LookAtMobTask());
        registerPassiveBehavior(new LookAtBlockTask());
        registerPassiveBehavior(new ExamineMobTask());
        registerPassiveBehavior(new ExamineBlockTask());
        registerPassiveBehavior(new WaveAtAllyTask());
        registerPassiveAbility(new AlertOwnerToMonsterTask());
        registerPassiveAbility(new HealOwnerTask());
        registerPassiveAbility(new HealAllyTask());
        registerPassiveAbility(new CharmVillagerTask());
        registerPassiveAbility(new SpotLootTask());
        registerPassiveAbility(new GrowCropsTask());
        registerPassiveAbility(new RandomSupportOwnerTask());
        registerPassiveAbility(new CleanseOwnerTask());
        registerPassiveAbility(new ConvertStoneToOreTask());
        registerPassiveAbility(new LifeBlessingTask());
        registerTriggeredAbility(new ExtinguishOwnerTask());
        registerTriggeredAbility(new ReplenishOwnerBreathTask());
        registerTriggeredAbility(new SaveOwnerFromFallingTask());
        registerTriggeredAbility(new KnockbackAroundOwnerTask());
        registerCombatAbility(new CombatSupportOwnerTask());
        registerCombatAbility(new DebuffEnemyTask());
        registerCombatAbility(new HijackEnemyTask());
        registerCombatAbility(new SummonWispsTask());
        registerCombatAbility(new AreaAttackTask());
        registerCombatAbility(new ChainLightningAttackTask());
        registerCombatAbility(new ProtectorTauntTask());
        registerCombatAbility(new BruiserSelfBuffTask());
        registerCombatAbility(new BruiserSuperPunchTask());
        registerCombatAbility(new BruiserUppercutTask());
        registerCombatAbility(new PranksterPossessionTask());
        registerCombatAbility(new PranksterStealItemTask());
    }
}
