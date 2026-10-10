/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Multimap
 *  net.minecraft.core.BlockPos
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.ai.attributes.Attribute
 *  net.minecraft.world.entity.ai.attributes.AttributeModifier
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.entity.monster.EnderMan
 *  net.minecraft.world.entity.monster.piglin.PiglinAi
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.food.FoodProperties
 *  net.minecraft.world.item.ArmorItem
 *  net.minecraft.world.item.ArmorMaterials
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.item.crafting.RecipeType
 *  net.minecraft.world.item.enchantment.Enchantment
 *  net.minecraft.world.item.enchantment.EnchantmentHelper
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.AABB
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.common.extensions;

import com.google.common.collect.Multimap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.wrapper.ShulkerItemStackInvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IForgeItem {
    private Item self() {
        return (Item)this;
    }

    default public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        return this.self().m_7167_(slot);
    }

    default public boolean onDroppedByPlayer(ItemStack item, Player player) {
        return true;
    }

    default public Component getHighlightTip(ItemStack item, Component displayName) {
        return displayName;
    }

    default public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        return InteractionResult.PASS;
    }

    default public boolean isPiglinCurrency(ItemStack stack) {
        return stack.m_41720_() == PiglinAi.f_34794_;
    }

    default public boolean makesPiglinsNeutral(ItemStack stack, LivingEntity wearer) {
        return stack.m_41720_() instanceof ArmorItem && ((ArmorItem)stack.m_41720_()).m_40401_() == ArmorMaterials.GOLD;
    }

    public boolean isRepairable(ItemStack var1);

    default public float getXpRepairRatio(ItemStack stack) {
        return 2.0f;
    }

    @Nullable
    default public CompoundTag getShareTag(ItemStack stack) {
        return stack.m_41783_();
    }

    default public void readShareTag(ItemStack stack, @Nullable CompoundTag nbt) {
        stack.m_41751_(nbt);
    }

    default public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, Player player) {
        return false;
    }

    default public void onStopUsing(ItemStack stack, LivingEntity entity, int count) {
    }

    default public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        return false;
    }

    default public ItemStack getCraftingRemainingItem(ItemStack itemStack) {
        if (!this.hasCraftingRemainingItem(itemStack)) {
            return ItemStack.f_41583_;
        }
        return new ItemStack((ItemLike)this.self().m_41469_());
    }

    default public boolean hasCraftingRemainingItem(ItemStack stack) {
        return this.self().m_41470_();
    }

    default public int getEntityLifespan(ItemStack itemStack, Level level) {
        return 6000;
    }

    default public boolean hasCustomEntity(ItemStack stack) {
        return false;
    }

    @Nullable
    default public Entity createEntity(Level level, Entity location, ItemStack stack) {
        return null;
    }

    default public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        return false;
    }

    default public boolean doesSneakBypassUse(ItemStack stack, LevelReader level, BlockPos pos, Player player) {
        return false;
    }

    @Deprecated(forRemoval=true, since="1.20.1")
    default public void onArmorTick(ItemStack stack, Level level, Player player) {
    }

    default public void onInventoryTick(ItemStack stack, Level level, Player player, int slotIndex, int selectedIndex) {
        Inventory inv = player.m_150109_();
        int vanillaIndex = slotIndex;
        if (slotIndex >= inv.f_35974_.size()) {
            if ((vanillaIndex -= inv.f_35974_.size()) >= inv.f_35975_.size()) {
                vanillaIndex -= inv.f_35975_.size();
            } else {
                this.onArmorTick(stack, level, player);
            }
        }
        stack.m_41666_(level, (Entity)player, vanillaIndex, selectedIndex == vanillaIndex);
    }

    default public boolean canEquip(ItemStack stack, EquipmentSlot armorType, Entity entity) {
        return Mob.m_147233_((ItemStack)stack) == armorType;
    }

    @Nullable
    default public EquipmentSlot getEquipmentSlot(ItemStack stack) {
        return null;
    }

    default public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return true;
    }

    @Nullable
    default public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return null;
    }

    default public boolean onEntitySwing(ItemStack stack, LivingEntity entity) {
        return false;
    }

    default public int getDamage(ItemStack stack) {
        return !stack.m_41782_() ? 0 : stack.m_41783_().m_128451_("Damage");
    }

    default public int getMaxDamage(ItemStack stack) {
        return this.self().m_41462_();
    }

    default public boolean isDamaged(ItemStack stack) {
        return stack.m_41773_() > 0;
    }

    default public void setDamage(ItemStack stack, int damage) {
        stack.m_41784_().m_128405_("Damage", Math.max(0, damage));
    }

    default public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
        return false;
    }

    default public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        return this.self().m_8096_(state);
    }

    default public int getMaxStackSize(ItemStack stack) {
        return this.self().m_41459_();
    }

    default public int getEnchantmentValue(ItemStack stack) {
        return this.self().m_6473_();
    }

    default public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return enchantment.f_44672_.m_7454_(stack.m_41720_());
    }

    default public int getEnchantmentLevel(ItemStack stack, Enchantment enchantment) {
        return EnchantmentHelper.getTagEnchantmentLevel((Enchantment)enchantment, (ItemStack)stack);
    }

    default public Map<Enchantment, Integer> getAllEnchantments(ItemStack stack) {
        return EnchantmentHelper.m_44882_((ListTag)stack.m_41785_());
    }

    default public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return !oldStack.equals(newStack);
    }

    default public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
        if (!newStack.m_150930_(oldStack.m_41720_())) {
            return true;
        }
        if (!newStack.m_41763_() || !oldStack.m_41763_()) {
            return !ItemStack.m_150942_((ItemStack)newStack, (ItemStack)oldStack);
        }
        CompoundTag newTag = newStack.m_41783_();
        CompoundTag oldTag = oldStack.m_41783_();
        if (newTag == null || oldTag == null) {
            return newTag != null || oldTag != null;
        }
        HashSet newKeys = new HashSet(newTag.m_128431_());
        HashSet oldKeys = new HashSet(oldTag.m_128431_());
        newKeys.remove("Damage");
        oldKeys.remove("Damage");
        if (!newKeys.equals(oldKeys)) {
            return true;
        }
        return !newKeys.stream().allMatch(key -> Objects.equals(newTag.m_128423_(key), oldTag.m_128423_(key)));
    }

    default public boolean canContinueUsing(ItemStack oldStack, ItemStack newStack) {
        if (oldStack == newStack) {
            return true;
        }
        return !oldStack.m_41619_() && !newStack.m_41619_() && ItemStack.m_41656_((ItemStack)newStack, (ItemStack)oldStack);
    }

    @Nullable
    default public String getCreatorModId(ItemStack itemStack) {
        return ForgeHooks.getDefaultCreatorModId(itemStack);
    }

    @Nullable
    default public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        ICapabilityProvider ret = ShulkerItemStackInvWrapper.createDefaultProvider(stack);
        return ret;
    }

    default public boolean canDisableShield(ItemStack stack, ItemStack shield, LivingEntity entity, LivingEntity attacker) {
        return attacker.m_213824_();
    }

    default public int getBurnTime(ItemStack itemStack, @Nullable RecipeType<?> recipeType) {
        return -1;
    }

    default public void onHorseArmorTick(ItemStack stack, Level level, Mob horse) {
    }

    default public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
        return amount;
    }

    default public void onDestroyed(ItemEntity itemEntity, DamageSource damageSource) {
        this.self().m_142023_(itemEntity);
    }

    default public boolean isEnderMask(ItemStack stack, Player player, EnderMan endermanEntity) {
        return stack.m_41720_() == Blocks.f_50143_.m_5456_();
    }

    default public boolean canElytraFly(ItemStack stack, LivingEntity entity) {
        return false;
    }

    default public boolean elytraFlightTick(ItemStack stack, LivingEntity entity, int flightTicks) {
        return false;
    }

    default public boolean canWalkOnPowderedSnow(ItemStack stack, LivingEntity wearer) {
        return stack.m_150930_(Items.f_42463_);
    }

    default public boolean isDamageable(ItemStack stack) {
        return this.self().m_41465_();
    }

    @NotNull
    default public AABB getSweepHitBox(@NotNull ItemStack stack, @NotNull Player player, @NotNull Entity target) {
        return target.m_20191_().m_82377_(1.0, 0.25, 1.0);
    }

    default public int getDefaultTooltipHideFlags(@NotNull ItemStack stack) {
        return 0;
    }

    @Nullable
    default public FoodProperties getFoodProperties(ItemStack stack, @Nullable LivingEntity entity) {
        return this.self().m_41473_();
    }

    default public boolean isNotReplaceableByPickAction(ItemStack stack, Player player, int inventorySlot) {
        return stack.m_41793_();
    }

    default public boolean canGrindstoneRepair(ItemStack stack) {
        return false;
    }
}

