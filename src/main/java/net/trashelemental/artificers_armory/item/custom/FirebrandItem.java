package net.trashelemental.artificers_armory.item.custom;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import net.trashelemental.artificers_armory.Config;
import net.trashelemental.artificers_armory.entity.custom.FireballEntity;
import net.trashelemental.artificers_armory.junkyard_lib.util.UtilMethods;
import net.trashelemental.artificers_armory.magic.enchantments.ModEnchantments;
import net.trashelemental.artificers_armory.util.EnchantmentChecker;
import net.trashelemental.artificers_armory.util.ModTags;
import net.trashelemental.artificers_armory.util.event.FirebrandEvents;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class FirebrandItem extends AbstractWeaponItem {

    public final int projectileDamage;
    public final int cooldownTime;
    public final int burnSeconds;
    protected final int enchantmentValue;
    public final boolean isSoulFire;

    public FirebrandItem(Properties properties, int meleeDamage, int projectileDamage, int cooldownTime, int burnTime, float meleeSpeed, boolean isSoulFire, int enchantmentValue) {
        super(meleeDamage, meleeSpeed, properties);
        this.projectileDamage = projectileDamage;
        this.cooldownTime = cooldownTime;
        this.burnSeconds = burnTime;
        this.isSoulFire = isSoulFire;
        this.enchantmentValue = enchantmentValue;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag flag) {

        if (UtilMethods.hasEnchantment(stack, ModEnchantments.FLAMETHROWER.get())) {
            components.add(Component.translatable("tooltip.artificers_armory.area_damage",
                    FirebrandEvents.getAdjustedFlamethrowerDamage(stack)).withStyle(ChatFormatting.DARK_GREEN));
        }
        if (UtilMethods.hasEnchantment(stack, ModEnchantments.WARMING_LIGHT.get())) {
            components.add(Component.translatable("tooltip.artificers_armory.healing",
                    FirebrandEvents.getAdjustedDamage(stack)).withStyle(ChatFormatting.DARK_GREEN));
            components.add(Component.translatable("tooltip.artificers_armory.area_damage",
                    FirebrandEvents.getAdjustedWarmingLightDamage(stack)).withStyle(ChatFormatting.DARK_GREEN));
        }

        if (shouldDoNormalProjectileAttack(stack) || UtilMethods.hasEnchantment(stack, ModEnchantments.CHARGE_BLAST.get())) {
            components.add(Component.translatable("tooltip.artificers_armory.projectile_damage",
                    FirebrandEvents.getAdjustedDamage(stack)).withStyle(ChatFormatting.DARK_GREEN));
        }
        components.add(Component.translatable("tooltip.artificers_armory.burn_time",
                FirebrandEvents.getAdjustedBurnTime(stack)).withStyle(ChatFormatting.DARK_GREEN));

        if (ModList.get().isLoaded("irons_spellbooks") && Config.ISS_COMPAT.get()) {
            components.add(Component.translatable("tooltip.artificers_armory.iss_fire_spell_power",
                    formatPercent(Config.ISS_BOOST_AMOUNT.get())).withStyle(ChatFormatting.BLUE));
        }

        if ((!shouldDoNormalProjectileAttack(stack) || UtilMethods.hasEnchantment(stack, ModEnchantments.SOUL_BLAZE.get()))
                && Config.ENCHANT_TOOLTIPS.get()) {
            if (!Screen.hasShiftDown()) {
                components.add(Component.translatable("tooltip.artificers_armory.hold_shift_enchant",
                        FirebrandEvents.getAdjustedDamage(stack)).withStyle(ChatFormatting.DARK_GRAY));
            } else {
                if (UtilMethods.hasEnchantment(stack, ModEnchantments.CHARGE_BLAST.get())) {
                    components.add(Component.translatable("enchantment.artificers_armory.charge_blast.desc",
                            FirebrandEvents.getAdjustedDamage(stack)).withStyle(ChatFormatting.GOLD));
                    if (getFireAspectAndFlameLevels(stack) > 0) {
                        components.add(Component.translatable("tooltip.artificers_armory.charge_shot_cooldown_reduce",
                                FirebrandEvents.getAdjustedDamage(stack)).withStyle(ChatFormatting.GOLD));
                    }
                }
                if (UtilMethods.hasEnchantment(stack, ModEnchantments.FLAMETHROWER.get())) {
                    components.add(Component.translatable("enchantment.artificers_armory.flamethrower.desc",
                            FirebrandEvents.getAdjustedDamage(stack)).withStyle(ChatFormatting.GOLD));
                    if (getFireAspectAndFlameLevels(stack) > 0) {
                        components.add(Component.translatable("tooltip.artificers_armory.flamethrower_warming_light_cooldown_reduce",
                                FirebrandEvents.getAdjustedDamage(stack)).withStyle(ChatFormatting.GOLD));
                    }
                }
                if (UtilMethods.hasEnchantment(stack, ModEnchantments.WARMING_LIGHT.get())) {
                    components.add(Component.translatable("enchantment.artificers_armory.warming_light.desc",
                            FirebrandEvents.getAdjustedDamage(stack)).withStyle(ChatFormatting.GOLD));
                    if (getFireAspectAndFlameLevels(stack) > 0) {
                        components.add(Component.translatable("tooltip.artificers_armory.flamethrower_warming_light_cooldown_reduce",
                                FirebrandEvents.getAdjustedDamage(stack)).withStyle(ChatFormatting.GOLD));
                    }
                }
                if (UtilMethods.hasEnchantment(stack, ModEnchantments.SOUL_BLAZE.get())) {
                    components.add(Component.translatable("tooltip.artificers_armory.soul_blaze",
                            FirebrandEvents.getAdjustedDamage(stack)).withStyle(ChatFormatting.BLUE));
                }
            }


        }
        super.appendHoverText(stack, level, components, flag);
    }


    /**
     * Checks if the Firebrand has an enchantment that changes its attack from a projectile to a channeled or charge
     * attack.
     */
    public static boolean shouldDoNormalProjectileAttack(ItemStack stack) {
        return !UtilMethods.hasEnchantment(stack, ModEnchantments.CHARGE_BLAST.get()) &&
                !UtilMethods.hasEnchantment(stack, ModEnchantments.FLAMETHROWER.get()) &&
                !UtilMethods.hasEnchantment(stack, ModEnchantments.WARMING_LIGHT.get());
    }

    public static int getFireAspectAndFlameLevels(ItemStack stack) {
        return UtilMethods.getEnchantmentLevel(stack, Enchantments.FIRE_ASPECT) + UtilMethods.getEnchantmentLevel(stack, Enchantments.FLAMING_ARROWS);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {

        ItemStack stack = player.getItemInHand(hand);

        if (shouldDoNormalProjectileAttack(stack)) {
            FirebrandEvents.performProjectileAttack(player, stack);
            player.getCooldowns().addCooldown(this, cooldownTime);
            player.swing(hand);
            stack.hurtAndBreak(1, player, (p_43296_) -> {
                p_43296_.broadcastBreakEvent(EquipmentSlot.MAINHAND);
            });
        } else player.startUsingItem(hand);

        if (player instanceof ServerPlayer serverPlayer) {
            UtilMethods.grantAdvancement(serverPlayer, "firebrand_shoot");
        }

        return InteractionResultHolder.consume(stack);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(ItemStack pStack) {
        return 720000;
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int remaining) {
        if (level.isClientSide) return;
        if (!(entity instanceof Player player)) return;

        int ticksUsed = getUseDuration(stack) - remaining;

        FirebrandEvents.handleChanneledAttack(player, stack, ticksUsed);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeCharged) {
        if (!(entity instanceof Player player)) return;
        if (level.isClientSide) return;

        if (UtilMethods.hasEnchantment(stack, ModEnchantments.CHARGE_BLAST.get())) {
            FirebrandEvents.releaseChargeShot(player, stack);
            stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(player.getUsedItemHand()));
        }
    }

    /**
     * If the target is flammable, set it on fire if it isn't currently on fire. If it is already on fire, set the remaining
     * time on fire to be at least the burn time of the Firebrand.
     */
    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {

        int burnTicks = burnSeconds * 20;

        if (!target.fireImmune()) {
            if (target.isOnFire()) {
                if (target.getRemainingFireTicks() < burnTicks) {
                    target.setRemainingFireTicks(burnTicks);
                }
            } else {
                target.setSecondsOnFire(burnSeconds);
            }
        }

        return super.hurtEnemy(stack, target, attacker);
    }

    /**
     * Firebrands destroy cobwebs, snow, and ice blocks very quickly.
     */
    public float getDestroySpeed(ItemStack pStack, BlockState pState) {
        if (pState.is(Blocks.COBWEB) || pState.is(Blocks.POWDER_SNOW) || pState.is(Blocks.SNOW_BLOCK) || pState.is(Blocks.ICE)) {
            return 200.0F;
        } else if (pState.is(Blocks.PACKED_ICE) || pState.is(Blocks.BLUE_ICE)) {
            return 15.0F;
        } else {
            return 1.0F;
        }
    }

    /**
     * Enchanting
     */
    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return isEnchantmentInTags(enchantment);
    }

    @Override
    public boolean isEnchantable(ItemStack pStack) {
        return true;
    }

    @Override
    public int getEnchantmentValue(ItemStack stack) {
        return this.enchantmentValue;
    }

    public static boolean isEnchantmentInTags(Enchantment enchantment) {
        Holder<Enchantment> holder = BuiltInRegistries.ENCHANTMENT.wrapAsHolder(enchantment);
        return holder.is(ModTags.Enchantments.FIREBRAND_ALLOWED) || holder.is(ModTags.Enchantments.MULTISHOT);
    }



    /**
     * ISS Compatibility
     */
    private static final ResourceLocation FIRE_SPELL_POWER = new ResourceLocation("irons_spellbooks", "fire_spell_power");

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {

        Multimap<Attribute, AttributeModifier> modifiers = HashMultimap.create(super.getAttributeModifiers(slot, stack));

        if ((slot == EquipmentSlot.MAINHAND || slot == EquipmentSlot.OFFHAND) && ModList.get().isLoaded("irons_spellbooks")
                && Config.ISS_COMPAT.get()) {
            addISSAttribute(modifiers, FIRE_SPELL_POWER, Config.ISS_BOOST_AMOUNT.get(), "iss_fire_spell_power_bonus");
        }

        return modifiers;
    }

    private void addISSAttribute(Multimap<Attribute, AttributeModifier> modifiers, ResourceLocation attributeId, double amount, String uuidName) {

        Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(attributeId);

        if (attribute != null && amount != 0) {
            AttributeModifier modifier =
                    new AttributeModifier(UUID.nameUUIDFromBytes(uuidName.getBytes()), uuidName, amount, AttributeModifier.Operation.MULTIPLY_TOTAL);
            modifiers.put(attribute, modifier);
        }
    }

    private String formatPercent(double value) {
        return String.valueOf((int) Math.round(value * 100));
    }
}