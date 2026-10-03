/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Registry
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.stats.Stats
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.entity.monster.EnderMan
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.food.FoodProperties
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.item.crafting.RecipeType
 *  net.minecraft.world.item.enchantment.Enchantment
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.state.pattern.BlockInWorld
 *  net.minecraft.world.phys.AABB
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.common.extensions;

import java.util.Map;
import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IForgeItemStack
extends ICapabilitySerializable<CompoundTag> {
    private ItemStack self() {
        return (ItemStack)this;
    }

    default public ItemStack getCraftingRemainingItem() {
        return this.self().m_41720_().getCraftingRemainingItem(this.self());
    }

    default public boolean hasCraftingRemainingItem() {
        return this.self().m_41720_().hasCraftingRemainingItem(this.self());
    }

    default public int getBurnTime(@Nullable RecipeType<?> recipeType) {
        return this.self().m_41720_().getBurnTime(this.self(), recipeType);
    }

    default public InteractionResult onItemUseFirst(UseOnContext context) {
        Player entityplayer = context.m_43723_();
        BlockPos blockpos = context.m_8083_();
        BlockInWorld blockworldstate = new BlockInWorld((LevelReader)context.m_43725_(), blockpos, false);
        Registry registry = entityplayer.m_9236_().m_9598_().m_175515_(Registries.f_256747_);
        if (entityplayer != null && !entityplayer.m_150110_().f_35938_ && !this.self().m_204121_(registry, blockworldstate)) {
            return InteractionResult.PASS;
        }
        Item item = this.self().m_41720_();
        InteractionResult enumactionresult = item.onItemUseFirst(this.self(), context);
        if (entityplayer != null && enumactionresult == InteractionResult.SUCCESS) {
            entityplayer.m_36246_(Stats.f_12982_.m_12902_((Object)item));
        }
        return enumactionresult;
    }

    @Override
    default public CompoundTag serializeNBT() {
        CompoundTag ret = new CompoundTag();
        this.self().m_41739_(ret);
        return ret;
    }

    default public boolean canPerformAction(ToolAction toolAction) {
        return this.self().m_41720_().canPerformAction(this.self(), toolAction);
    }

    default public boolean onBlockStartBreak(BlockPos pos, Player player) {
        return !this.self().m_41619_() && this.self().m_41720_().onBlockStartBreak(this.self(), pos, player);
    }

    default public boolean shouldCauseBlockBreakReset(ItemStack newStack) {
        return this.self().m_41720_().shouldCauseBlockBreakReset(this.self(), newStack);
    }

    default public boolean canApplyAtEnchantingTable(Enchantment enchantment) {
        return this.self().m_41720_().canApplyAtEnchantingTable(this.self(), enchantment);
    }

    default public int getEnchantmentLevel(Enchantment enchantment) {
        return this.self().m_41720_().getEnchantmentLevel(this.self(), enchantment);
    }

    default public Map<Enchantment, Integer> getAllEnchantments() {
        return this.self().m_41720_().getAllEnchantments(this.self());
    }

    default public int getEnchantmentValue() {
        return this.self().m_41720_().getEnchantmentValue(this.self());
    }

    @Nullable
    default public EquipmentSlot getEquipmentSlot() {
        return this.self().m_41720_().getEquipmentSlot(this.self());
    }

    default public boolean canDisableShield(ItemStack shield, LivingEntity entity, LivingEntity attacker) {
        return this.self().m_41720_().canDisableShield(this.self(), shield, entity, attacker);
    }

    default public boolean onEntitySwing(LivingEntity entity) {
        return this.self().m_41720_().onEntitySwing(this.self(), entity);
    }

    default public void onStopUsing(LivingEntity entity, int count) {
        this.self().m_41720_().onStopUsing(this.self(), entity, count);
    }

    default public int getEntityLifespan(Level level) {
        return this.self().m_41720_().getEntityLifespan(this.self(), level);
    }

    default public boolean onEntityItemUpdate(ItemEntity entity) {
        return this.self().m_41720_().onEntityItemUpdate(this.self(), entity);
    }

    default public float getXpRepairRatio() {
        return this.self().m_41720_().getXpRepairRatio(this.self());
    }

    @Deprecated(forRemoval=true, since="1.20.1")
    default public void onArmorTick(Level level, Player player) {
        this.self().m_41720_().onArmorTick(this.self(), level, player);
    }

    default public void onInventoryTick(Level level, Player player, int slotIndex, int selectedIndex) {
        this.self().m_41720_().onInventoryTick(this.self(), level, player, slotIndex, selectedIndex);
    }

    default public void onHorseArmorTick(Level level, Mob horse) {
        this.self().m_41720_().onHorseArmorTick(this.self(), level, horse);
    }

    default public boolean canEquip(EquipmentSlot armorType, Entity entity) {
        return this.self().m_41720_().canEquip(this.self(), armorType, entity);
    }

    default public boolean isBookEnchantable(ItemStack book) {
        return this.self().m_41720_().isBookEnchantable(this.self(), book);
    }

    default public boolean onDroppedByPlayer(Player player) {
        return this.self().m_41720_().onDroppedByPlayer(this.self(), player);
    }

    default public Component getHighlightTip(Component displayName) {
        return this.self().m_41720_().getHighlightTip(this.self(), displayName);
    }

    @Nullable
    default public CompoundTag getShareTag() {
        return this.self().m_41720_().getShareTag(this.self());
    }

    default public void readShareTag(@Nullable CompoundTag nbt) {
        this.self().m_41720_().readShareTag(this.self(), nbt);
    }

    default public boolean doesSneakBypassUse(LevelReader level, BlockPos pos, Player player) {
        return this.self().m_41619_() || this.self().m_41720_().doesSneakBypassUse(this.self(), level, pos, player);
    }

    default public boolean areShareTagsEqual(ItemStack other) {
        CompoundTag shareTagA = this.self().getShareTag();
        CompoundTag shareTagB = other.getShareTag();
        if (shareTagA == null) {
            return shareTagB == null;
        }
        return shareTagB != null && shareTagA.equals((Object)shareTagB);
    }

    default public boolean equals(ItemStack other, boolean limitTags) {
        if (this.self().m_41619_()) {
            return other.m_41619_();
        }
        return !other.m_41619_() && this.self().m_41613_() == other.m_41613_() && this.self().m_41720_() == other.m_41720_() && (limitTags ? this.self().areShareTagsEqual(other) : Objects.equals(this.self().m_41783_(), other.m_41783_()));
    }

    default public boolean isRepairable() {
        return this.self().m_41720_().isRepairable(this.self());
    }

    default public boolean isPiglinCurrency() {
        return this.self().m_41720_().isPiglinCurrency(this.self());
    }

    default public boolean makesPiglinsNeutral(LivingEntity wearer) {
        return this.self().m_41720_().makesPiglinsNeutral(this.self(), wearer);
    }

    default public boolean isEnderMask(Player player, EnderMan endermanEntity) {
        return this.self().m_41720_().isEnderMask(this.self(), player, endermanEntity);
    }

    default public boolean canElytraFly(LivingEntity entity) {
        return this.self().m_41720_().canElytraFly(this.self(), entity);
    }

    default public boolean elytraFlightTick(LivingEntity entity, int flightTicks) {
        return this.self().m_41720_().elytraFlightTick(this.self(), entity, flightTicks);
    }

    default public boolean canWalkOnPowderedSnow(LivingEntity wearer) {
        return this.self().m_41720_().canWalkOnPowderedSnow(this.self(), wearer);
    }

    @NotNull
    default public AABB getSweepHitBox(@NotNull Player player, @NotNull Entity target) {
        return this.self().m_41720_().getSweepHitBox(this.self(), player, target);
    }

    default public void onDestroyed(ItemEntity itemEntity, DamageSource damageSource) {
        this.self().m_41720_().onDestroyed(itemEntity, damageSource);
    }

    @Nullable
    default public FoodProperties getFoodProperties(@Nullable LivingEntity entity) {
        return this.self().m_41720_().getFoodProperties(this.self(), entity);
    }

    default public boolean isNotReplaceableByPickAction(Player player, int inventorySlot) {
        return this.self().m_41720_().isNotReplaceableByPickAction(this.self(), player, inventorySlot);
    }

    default public boolean canGrindstoneRepair() {
        return this.self().m_41720_().canGrindstoneRepair(this.self());
    }
}

