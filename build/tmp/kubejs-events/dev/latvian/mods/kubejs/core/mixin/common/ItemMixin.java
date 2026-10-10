/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.architectury.registry.fuel.FuelRegistry
 *  dev.latvian.mods.rhino.util.RemapForJS
 *  dev.latvian.mods.rhino.util.RemapPrefixForJS
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResultHolder
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.food.FoodProperties
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.ItemUtils
 *  net.minecraft.world.item.Rarity
 *  net.minecraft.world.item.TooltipFlag
 *  net.minecraft.world.item.UseAnim
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  org.jetbrains.annotations.Nullable
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Mutable
 *  org.spongepowered.asm.mixin.gen.Accessor
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package dev.latvian.mods.kubejs.core.mixin.common;

import dev.architectury.registry.fuel.FuelRegistry;
import dev.latvian.mods.kubejs.core.ItemKJS;
import dev.latvian.mods.kubejs.item.ItemBuilder;
import dev.latvian.mods.kubejs.item.ItemStackKey;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.util.RemapForJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@RemapPrefixForJS(value="kjs$")
@Mixin(value={Item.class}, priority=1001)
public abstract class ItemMixin
implements ItemKJS {
    private ItemBuilder kjs$itemBuilder;
    private CompoundTag kjs$typeData;
    private Ingredient kjs$asIngredient;
    private ItemStackKey kjs$typeItemStackKey;
    private ResourceLocation kjs$id;
    private String kjs$idString;

    @Override
    @Nullable
    public ItemBuilder kjs$getItemBuilder() {
        return this.kjs$itemBuilder;
    }

    @Override
    @RemapForJS(value="getItem")
    public Item kjs$self() {
        return (Item)this;
    }

    @Override
    public ResourceLocation kjs$getIdLocation() {
        if (this.kjs$id == null) {
            ResourceLocation id = RegistryInfo.ITEM.getId(this.kjs$self());
            this.kjs$id = id == null ? UtilsJS.UNKNOWN_ID : id;
        }
        return this.kjs$id;
    }

    @Override
    public String kjs$getId() {
        if (this.kjs$idString == null) {
            this.kjs$idString = this.kjs$getIdLocation().toString();
        }
        return this.kjs$idString;
    }

    @Override
    public void kjs$setItemBuilder(ItemBuilder b) {
        this.kjs$itemBuilder = b;
    }

    @Override
    public CompoundTag kjs$getTypeData() {
        if (this.kjs$typeData == null) {
            this.kjs$typeData = new CompoundTag();
        }
        return this.kjs$typeData;
    }

    @Override
    @Accessor(value="maxStackSize")
    @Mutable
    public abstract void kjs$setMaxStackSize(int var1);

    @Override
    @Accessor(value="maxDamage")
    @Mutable
    public abstract void kjs$setMaxDamage(int var1);

    @Override
    @Accessor(value="craftingRemainingItem")
    @Mutable
    public abstract void kjs$setCraftingRemainder(Item var1);

    @Override
    @Accessor(value="isFireResistant")
    @Mutable
    public abstract void kjs$setFireResistant(boolean var1);

    @Override
    @Accessor(value="rarity")
    @Mutable
    public abstract void kjs$setRarity(Rarity var1);

    @Override
    @RemapForJS(value="setBurnTime")
    public void kjs$setBurnTime(int i) {
        FuelRegistry.register((int)i, (ItemLike[])new ItemLike[]{(Item)this});
    }

    @Override
    @Accessor(value="foodProperties")
    @Mutable
    public abstract void kjs$overrideFood(@Nullable FoodProperties var1);

    @Inject(method={"isFoil"}, at={@At(value="HEAD")}, cancellable=true)
    private void isFoilKJS(ItemStack itemStack, CallbackInfoReturnable<Boolean> ci) {
        if (this.kjs$itemBuilder != null && this.kjs$itemBuilder.glow) {
            ci.setReturnValue((Object)true);
        }
    }

    @Inject(method={"appendHoverText"}, at={@At(value="RETURN")})
    private void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flagIn, CallbackInfo ci) {
        if (this.kjs$itemBuilder != null && !this.kjs$itemBuilder.tooltip.isEmpty()) {
            tooltip.addAll(this.kjs$itemBuilder.tooltip);
        }
    }

    @Inject(method={"isBarVisible"}, at={@At(value="HEAD")}, cancellable=true)
    private void isBarVisible(ItemStack stack, CallbackInfoReturnable<Boolean> ci) {
        if (this.kjs$itemBuilder != null && this.kjs$itemBuilder.barWidth != null && this.kjs$itemBuilder.barWidth.applyAsInt(stack) <= 13) {
            ci.setReturnValue((Object)true);
        }
    }

    @Inject(method={"getBarWidth"}, at={@At(value="HEAD")}, cancellable=true)
    private void getBarWidth(ItemStack stack, CallbackInfoReturnable<Integer> ci) {
        if (this.kjs$itemBuilder != null && this.kjs$itemBuilder.barWidth != null) {
            ci.setReturnValue((Object)this.kjs$itemBuilder.barWidth.applyAsInt(stack));
        }
    }

    @Inject(method={"getBarColor"}, at={@At(value="HEAD")}, cancellable=true)
    private void getBarColor(ItemStack stack, CallbackInfoReturnable<Integer> ci) {
        if (this.kjs$itemBuilder != null && this.kjs$itemBuilder.barColor != null) {
            ci.setReturnValue((Object)this.kjs$itemBuilder.barColor.apply(stack).getRgbJS());
        }
    }

    @Inject(method={"getUseDuration"}, at={@At(value="HEAD")}, cancellable=true)
    private void getUseDuration(ItemStack itemStack, CallbackInfoReturnable<Integer> ci) {
        if (this.kjs$itemBuilder != null && this.kjs$itemBuilder.useDuration != null) {
            ci.setReturnValue((Object)this.kjs$itemBuilder.useDuration.applyAsInt(itemStack));
        }
    }

    @Inject(method={"getUseAnimation"}, at={@At(value="HEAD")}, cancellable=true)
    private void getUseAnimation(ItemStack itemStack, CallbackInfoReturnable<UseAnim> ci) {
        if (this.kjs$itemBuilder != null && this.kjs$itemBuilder.anim != null) {
            ci.setReturnValue((Object)this.kjs$itemBuilder.anim);
        }
    }

    @Inject(method={"getName"}, at={@At(value="HEAD")}, cancellable=true)
    private void getName(ItemStack itemStack, CallbackInfoReturnable<Component> ci) {
        if (this.kjs$itemBuilder != null && this.kjs$itemBuilder.nameGetter != null) {
            ci.setReturnValue((Object)this.kjs$itemBuilder.nameGetter.apply(itemStack));
        }
    }

    @Inject(method={"use"}, at={@At(value="HEAD")}, cancellable=true)
    private void use(Level level, Player player, InteractionHand interactionHand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> ci) {
        if (this.kjs$itemBuilder != null && this.kjs$itemBuilder.use != null) {
            ItemStack itemStack = player.m_21120_(interactionHand);
            if (this.kjs$itemBuilder.use.use(level, player, interactionHand)) {
                ci.setReturnValue((Object)ItemUtils.m_150959_((Level)level, (Player)player, (InteractionHand)interactionHand));
            } else {
                ci.setReturnValue((Object)InteractionResultHolder.m_19100_((Object)itemStack));
            }
        }
    }

    @Inject(method={"finishUsingItem"}, at={@At(value="HEAD")}, cancellable=true)
    private void finishUsingItem(ItemStack itemStack, Level level, LivingEntity livingEntity, CallbackInfoReturnable<ItemStack> ci) {
        if (this.kjs$itemBuilder != null && this.kjs$itemBuilder.finishUsing != null) {
            ci.setReturnValue((Object)this.kjs$itemBuilder.finishUsing.finishUsingItem(itemStack, level, livingEntity));
        }
    }

    @Inject(method={"releaseUsing"}, at={@At(value="HEAD")})
    private void releaseUsing(ItemStack itemStack, Level level, LivingEntity livingEntity, int i, CallbackInfo ci) {
        if (this.kjs$itemBuilder != null && this.kjs$itemBuilder.releaseUsing != null) {
            this.kjs$itemBuilder.releaseUsing.releaseUsing(itemStack, level, livingEntity, i);
        }
    }

    @Inject(method={"hurtEnemy"}, at={@At(value="HEAD")}, cancellable=true)
    private void hurtEnemy(ItemStack itemStack, LivingEntity livingEntity, LivingEntity livingEntity2, CallbackInfoReturnable<Boolean> cir) {
        if (this.kjs$itemBuilder != null && this.kjs$itemBuilder.hurtEnemy != null) {
            cir.setReturnValue((Object)this.kjs$itemBuilder.hurtEnemy.test(new ItemBuilder.HurtEnemyContext(itemStack, livingEntity, livingEntity2)));
        }
    }

    @Override
    public Ingredient kjs$asIngredient() {
        if (this.kjs$asIngredient == null) {
            ItemStack is = new ItemStack((ItemLike)this.kjs$self());
            this.kjs$asIngredient = is.m_41619_() ? Ingredient.f_43901_ : Ingredient.m_43921_(Stream.of(is));
        }
        return this.kjs$asIngredient;
    }

    @Override
    @Accessor(value="descriptionId")
    @Mutable
    public abstract void kjs$setNameKey(String var1);

    @Override
    public ItemStackKey kjs$getTypeItemStackKey() {
        if (this.kjs$typeItemStackKey == null) {
            this.kjs$typeItemStackKey = new ItemStackKey(this.kjs$self(), null);
        }
        return this.kjs$typeItemStackKey;
    }
}

