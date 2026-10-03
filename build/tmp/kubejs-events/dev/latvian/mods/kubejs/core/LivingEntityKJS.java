/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.latvian.mods.rhino.util.RemapPrefixForJS
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.ai.attributes.Attribute
 *  net.minecraft.world.entity.ai.attributes.AttributeInstance
 *  net.minecraft.world.entity.ai.attributes.AttributeModifier
 *  net.minecraft.world.entity.ai.attributes.AttributeModifier$Operation
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.entity.ai.behavior.BehaviorUtils
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.Ingredient
 */
package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.bindings.event.ItemEvents;
import dev.latvian.mods.kubejs.core.EntityKJS;
import dev.latvian.mods.kubejs.entity.EntityPotionEffectsJS;
import dev.latvian.mods.kubejs.entity.RayTraceResultJS;
import dev.latvian.mods.kubejs.item.FoodEatenEventJS;
import dev.latvian.mods.kubejs.item.ItemBuilder;
import dev.latvian.mods.kubejs.platform.LevelPlatformHelper;
import dev.latvian.mods.kubejs.script.ScriptTypeHolder;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

@RemapPrefixForJS(value="kjs$")
public interface LivingEntityKJS
extends EntityKJS {
    public static final UUID KJS_PLAYER_CUSTOM_SPEED = UUID.fromString("6715D9C6-1DA0-4B78-971A-5C32F5709F66");
    public static final String KJS_PLAYER_CUSTOM_SPEED_NAME = "kubejs.player.speed.modifier";

    default public LivingEntity kjs$self() {
        return (LivingEntity)this;
    }

    default public void kjs$foodEaten(ItemStack is) {
        LivingEntityKJS livingEntityKJS = this;
        if (livingEntityKJS instanceof LivingEntity) {
            LivingEntity entity = (LivingEntity)livingEntityKJS;
            FoodEatenEventJS event = new FoodEatenEventJS(entity, is);
            Item i = is.m_41720_();
            ItemBuilder b = i.kjs$getItemBuilder();
            if (b != null && b.foodBuilder != null && b.foodBuilder.eaten != null) {
                b.foodBuilder.eaten.accept(event);
            }
            if (ItemEvents.FOOD_EATEN.hasListeners()) {
                ItemEvents.FOOD_EATEN.post((ScriptTypeHolder)entity, (Object)i, event);
            }
        }
    }

    @Override
    default public boolean kjs$isLiving() {
        return true;
    }

    default public void kjs$setMaxHealth(float hp) {
        this.kjs$self().m_21051_(Attributes.f_22276_).m_22100_((double)hp);
    }

    default public boolean kjs$isUndead() {
        return this.kjs$self().m_21222_();
    }

    default public EntityPotionEffectsJS kjs$getPotionEffects() {
        return new EntityPotionEffectsJS(this.kjs$self());
    }

    default public void kjs$swing(InteractionHand hand) {
        this.kjs$self().m_21011_(hand, true);
    }

    default public void kjs$swing() {
        this.kjs$self().m_21011_(InteractionHand.MAIN_HAND, true);
    }

    default public ItemStack kjs$getEquipment(EquipmentSlot slot) {
        return this.kjs$self().m_6844_(slot);
    }

    default public void kjs$setEquipment(EquipmentSlot slot, ItemStack item) {
        this.kjs$self().m_8061_(slot, item);
    }

    default public ItemStack kjs$getHeldItem(InteractionHand hand) {
        return this.kjs$self().m_21120_(hand);
    }

    default public void kjs$setHeldItem(InteractionHand hand, ItemStack item) {
        this.kjs$self().m_21008_(hand, item);
    }

    default public ItemStack kjs$getMainHandItem() {
        return this.kjs$getEquipment(EquipmentSlot.MAINHAND);
    }

    default public void kjs$setMainHandItem(ItemStack item) {
        this.kjs$setEquipment(EquipmentSlot.MAINHAND, item);
    }

    default public ItemStack kjs$getOffHandItem() {
        return this.kjs$getEquipment(EquipmentSlot.OFFHAND);
    }

    default public void kjs$setOffHandItem(ItemStack item) {
        this.kjs$setEquipment(EquipmentSlot.OFFHAND, item);
    }

    default public ItemStack kjs$getHeadArmorItem() {
        return this.kjs$getEquipment(EquipmentSlot.HEAD);
    }

    default public void kjs$setHeadArmorItem(ItemStack item) {
        this.kjs$setEquipment(EquipmentSlot.HEAD, item);
    }

    default public ItemStack kjs$getChestArmorItem() {
        return this.kjs$getEquipment(EquipmentSlot.CHEST);
    }

    default public void kjs$setChestArmorItem(ItemStack item) {
        this.kjs$setEquipment(EquipmentSlot.CHEST, item);
    }

    default public ItemStack kjs$getLegsArmorItem() {
        return this.kjs$getEquipment(EquipmentSlot.LEGS);
    }

    default public void kjs$setLegsArmorItem(ItemStack item) {
        this.kjs$setEquipment(EquipmentSlot.LEGS, item);
    }

    default public ItemStack kjs$getFeetArmorItem() {
        return this.kjs$getEquipment(EquipmentSlot.FEET);
    }

    default public void kjs$setFeetArmorItem(ItemStack item) {
        this.kjs$setEquipment(EquipmentSlot.FEET, item);
    }

    default public void kjs$damageEquipment(EquipmentSlot slot, int amount, Consumer<ItemStack> onBroken) {
        ItemStack stack = this.kjs$self().m_6844_(slot);
        if (!stack.m_41619_()) {
            stack.m_41622_(amount, this.kjs$self(), livingEntity -> onBroken.accept(stack));
            if (stack.m_41619_()) {
                this.kjs$self().m_8061_(slot, ItemStack.f_41583_);
            }
        }
    }

    default public void kjs$damageEquipment(EquipmentSlot slot, int amount) {
        this.kjs$damageEquipment(slot, amount, stack -> {});
    }

    default public void kjs$damageEquipment(EquipmentSlot slot) {
        this.kjs$damageEquipment(slot, 1);
    }

    default public void kjs$damageHeldItem(InteractionHand hand, int amount, Consumer<ItemStack> onBroken) {
        this.kjs$damageEquipment(hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND, amount, onBroken);
    }

    default public void kjs$damageHeldItem(InteractionHand hand, int amount) {
        this.kjs$damageHeldItem(hand, amount, stack -> {});
    }

    default public void kjs$damageHeldItem() {
        this.kjs$damageHeldItem(InteractionHand.MAIN_HAND, 1);
    }

    default public boolean kjs$isHoldingInAnyHand(Ingredient i) {
        return i.test(this.kjs$self().m_21120_(InteractionHand.MAIN_HAND)) || i.test(this.kjs$self().m_21120_(InteractionHand.OFF_HAND));
    }

    default public double kjs$getTotalMovementSpeed() {
        return this.kjs$self().m_21133_(Attributes.f_22279_);
    }

    default public double kjs$getDefaultMovementSpeed() {
        return this.kjs$self().m_21172_(Attributes.f_22279_);
    }

    default public void kjs$setDefaultMovementSpeed(double speed) {
        this.kjs$self().m_21051_(Attributes.f_22279_).m_22100_(speed);
    }

    default public void kjs$setMovementSpeedAddition(double speed) {
        AttributeInstance instance = this.kjs$self().m_21051_(Attributes.f_22279_);
        if (instance != null) {
            instance.m_22120_(KJS_PLAYER_CUSTOM_SPEED);
            instance.m_22118_(this.kjs$createSpeedModifier(speed, AttributeModifier.Operation.ADDITION));
        }
    }

    default public void kjs$setDefaultMovementSpeedMultiplier(double speed) {
        AttributeInstance instance = this.kjs$self().m_21051_(Attributes.f_22279_);
        if (instance != null) {
            instance.m_22120_(KJS_PLAYER_CUSTOM_SPEED);
            instance.m_22118_(this.kjs$createSpeedModifier(speed, AttributeModifier.Operation.MULTIPLY_BASE));
        }
    }

    default public void kjs$setTotalMovementSpeedMultiplier(double speed) {
        AttributeInstance instance = this.kjs$self().m_21051_(Attributes.f_22279_);
        if (instance != null) {
            instance.m_22120_(KJS_PLAYER_CUSTOM_SPEED);
            instance.m_22118_(this.kjs$createSpeedModifier(speed, AttributeModifier.Operation.MULTIPLY_TOTAL));
        }
    }

    default public boolean kjs$canEntityBeSeen(LivingEntity entity) {
        return BehaviorUtils.m_22667_((LivingEntity)this.kjs$self(), (LivingEntity)entity);
    }

    default public double kjs$getReachDistance() {
        return LevelPlatformHelper.get().getReachDistance(this.kjs$self());
    }

    default public RayTraceResultJS kjs$rayTrace() {
        return this.kjs$rayTrace(this.kjs$getReachDistance());
    }

    default public double kjs$getAttributeTotalValue(Attribute attribute) {
        AttributeInstance instance = this.kjs$self().m_21051_(attribute);
        if (instance != null) {
            return instance.m_22135_();
        }
        return 0.0;
    }

    default public double kjs$getAttributeBaseValue(Attribute attribute) {
        AttributeInstance instance = this.kjs$self().m_21051_(attribute);
        if (instance != null) {
            return instance.m_22115_();
        }
        return 0.0;
    }

    default public void kjs$setAttributeBaseValue(Attribute attribute, double value) {
        AttributeInstance instance = this.kjs$self().m_21051_(attribute);
        if (instance != null) {
            instance.m_22100_(value);
        }
    }

    default public void kjs$modifyAttribute(Attribute attribute, String identifier, double d, AttributeModifier.Operation operation) {
        AttributeInstance instance = this.kjs$self().m_21051_(attribute);
        if (instance != null) {
            UUID uuid = new UUID(identifier.hashCode(), identifier.hashCode());
            instance.m_22120_(uuid);
            instance.m_22118_(new AttributeModifier(uuid, identifier, d, operation));
        }
    }

    default public void kjs$removeAttribute(Attribute attribute, String identifier) {
        AttributeInstance instance = this.kjs$self().m_21051_(attribute);
        if (instance != null) {
            instance.m_22120_(new UUID(identifier.hashCode(), identifier.hashCode()));
        }
    }

    private AttributeModifier kjs$createSpeedModifier(double speed, AttributeModifier.Operation operation) {
        return new AttributeModifier(KJS_PLAYER_CUSTOM_SPEED, KJS_PLAYER_CUSTOM_SPEED_NAME, speed, operation);
    }
}

