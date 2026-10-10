/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  dev.latvian.mods.rhino.mod.util.JsonSerializable
 *  dev.latvian.mods.rhino.mod.util.NBTSerializable
 *  dev.latvian.mods.rhino.mod.util.NBTUtils
 *  dev.latvian.mods.rhino.util.RemapForJS
 *  dev.latvian.mods.rhino.util.RemapPrefixForJS
 *  dev.latvian.mods.rhino.util.SpecialEquality
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.StringTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.Component$Serializer
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.EnchantedBookItem
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.item.enchantment.Enchantment
 *  net.minecraft.world.item.enchantment.EnchantmentHelper
 *  net.minecraft.world.item.enchantment.EnchantmentInstance
 *  net.minecraft.world.level.block.Blocks
 *  org.apache.commons.lang3.tuple.Pair
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.core;

import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.core.IngredientSupplierKJS;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.item.OutputItem;
import dev.latvian.mods.kubejs.level.BlockContainerJS;
import dev.latvian.mods.kubejs.platform.IngredientPlatformHelper;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.kubejs.util.Tags;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.mod.util.JsonSerializable;
import dev.latvian.mods.rhino.mod.util.NBTSerializable;
import dev.latvian.mods.rhino.mod.util.NBTUtils;
import dev.latvian.mods.rhino.util.RemapForJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import dev.latvian.mods.rhino.util.SpecialEquality;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.block.Blocks;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

@RemapPrefixForJS(value="kjs$")
public interface ItemStackKJS
extends SpecialEquality,
NBTSerializable,
JsonSerializable,
IngredientSupplierKJS {
    default public ItemStack kjs$self() {
        return (ItemStack)this;
    }

    default public boolean specialEquals(Object o, boolean shallow) {
        if (o instanceof CharSequence) {
            return this.kjs$getId().equals(UtilsJS.getID(o.toString()));
        }
        if (o instanceof ItemStack) {
            ItemStack s = (ItemStack)o;
            return this.kjs$equalsIgnoringCount(s);
        }
        return this.kjs$equalsIgnoringCount(ItemStackJS.of(o));
    }

    default public boolean kjs$equalsIgnoringCount(ItemStack stack) {
        ItemStack self = this.kjs$self();
        if (self == stack) {
            return true;
        }
        if (self.m_41619_()) {
            return stack.m_41619_();
        }
        return ItemStack.m_150942_((ItemStack)self, (ItemStack)stack);
    }

    default public ResourceLocation kjs$getIdLocation() {
        return this.kjs$self().m_41720_().kjs$getIdLocation();
    }

    default public String kjs$getId() {
        return this.kjs$self().m_41720_().kjs$getId();
    }

    default public Collection<ResourceLocation> kjs$getTags() {
        return Tags.byItem(this.kjs$self().m_41720_()).map(TagKey::f_203868_).collect(Collectors.toSet());
    }

    default public boolean kjs$hasTag(ResourceLocation tag) {
        return this.kjs$self().m_204117_(Tags.item(tag));
    }

    default public boolean kjs$isBlock() {
        return this.kjs$self().m_41720_() instanceof BlockItem;
    }

    default public ItemStack kjs$withCount(int c) {
        if (c <= 0 || this.kjs$self().m_41619_()) {
            return ItemStack.f_41583_;
        }
        ItemStack is = this.kjs$self().m_41777_();
        is.m_41764_(c);
        return is;
    }

    default public void kjs$removeTag() {
        this.kjs$self().m_41751_(null);
    }

    default public String kjs$getNbtString() {
        return String.valueOf(this.kjs$self().m_41783_());
    }

    default public ItemStack kjs$withNBT(CompoundTag nbt) {
        ItemStack is = this.kjs$self().m_41777_();
        CompoundTag tag0 = is.m_41783_();
        if (tag0 == null) {
            is.m_41751_(nbt);
        } else {
            is.m_41751_(tag0.m_128391_(nbt));
        }
        return is;
    }

    default public ItemStack kjs$withName(@Nullable Component displayName) {
        ItemStack is = this.kjs$self().m_41777_();
        if (displayName != null) {
            is.m_41714_(displayName);
        } else {
            is.m_41787_();
        }
        return is;
    }

    default public Map<String, Integer> kjs$getEnchantments() {
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        for (Map.Entry entry : EnchantmentHelper.m_44831_((ItemStack)this.kjs$self()).entrySet()) {
            ResourceLocation id = RegistryInfo.ENCHANTMENT.getId((Enchantment)entry.getKey());
            if (id == null) continue;
            map.put(id.toString(), (Integer)entry.getValue());
        }
        return map;
    }

    default public boolean kjs$hasEnchantment(Enchantment enchantment, int level) {
        return EnchantmentHelper.m_44843_((Enchantment)enchantment, (ItemStack)this.kjs$self()) >= level;
    }

    @RemapForJS(value="enchant")
    default public ItemStack kjs$enchantCopy(Map<?, ?> enchantments) {
        ItemStack is = this.kjs$self();
        for (Map.Entry<?, ?> entry : enchantments.entrySet()) {
            Object obj;
            Enchantment enchantment = RegistryInfo.ENCHANTMENT.getValue(UtilsJS.getMCID(null, entry.getKey()));
            if (enchantment == null || !((obj = entry.getValue()) instanceof Number)) continue;
            Number number = (Number)obj;
            is = is.kjs$enchantCopy(enchantment, number.intValue());
        }
        return is;
    }

    @RemapForJS(value="enchant")
    default public ItemStack kjs$enchantCopy(Enchantment enchantment, int level) {
        ItemStack is = this.kjs$self().m_41777_();
        if (is.m_41720_() == Items.f_42690_) {
            EnchantedBookItem.m_41153_((ItemStack)is, (EnchantmentInstance)new EnchantmentInstance(enchantment, level));
        } else {
            is.m_41663_(enchantment, level);
        }
        return is;
    }

    default public String kjs$getMod() {
        return this.kjs$self().m_41720_().kjs$getMod();
    }

    @Deprecated
    default public Ingredient kjs$ignoreNBT() {
        ConsoleJS console = ConsoleJS.getCurrent(ConsoleJS.SERVER);
        console.warn("You don't need to call .ignoreNBT() anymore, all item ingredients ignore NBT by default!");
        return this.kjs$self().m_41720_().kjs$asIngredient();
    }

    default public Ingredient kjs$weakNBT() {
        return IngredientPlatformHelper.get().weakNBT(this.kjs$self());
    }

    default public Ingredient kjs$strongNBT() {
        return IngredientPlatformHelper.get().strongNBT(this.kjs$self());
    }

    default public boolean kjs$areItemsEqual(ItemStack other) {
        return this.kjs$self().m_41720_() == other.m_41720_();
    }

    default public boolean kjs$isNBTEqual(ItemStack other) {
        if (this.kjs$self().m_41782_() == other.m_41782_()) {
            CompoundTag nbt = this.kjs$self().m_41783_();
            CompoundTag nbt2 = other.m_41783_();
            return Objects.equals(nbt, nbt2);
        }
        return false;
    }

    default public float kjs$getHarvestSpeed(@Nullable BlockContainerJS block) {
        return this.kjs$self().m_41691_(block == null ? Blocks.f_50016_.m_49966_() : block.getBlockState());
    }

    default public float kjs$getHarvestSpeed() {
        return this.kjs$getHarvestSpeed(null);
    }

    @RemapForJS(value="toNBT")
    default public CompoundTag toNBTJS() {
        return this.kjs$self().m_41739_(new CompoundTag());
    }

    default public CompoundTag kjs$getTypeData() {
        return this.kjs$self().m_41720_().kjs$getTypeData();
    }

    default public String kjs$toItemString() {
        ItemStack is = this.kjs$self();
        StringBuilder builder = new StringBuilder();
        int count = is.m_41613_();
        boolean hasNbt = is.m_41782_();
        if (count > 1 && !hasNbt) {
            builder.append('\'');
            builder.append(count);
            builder.append("x ");
            builder.append(this.kjs$getId());
            builder.append('\'');
        } else if (hasNbt) {
            CompoundTag t;
            builder.append("Item.of('");
            builder.append(is.kjs$getId());
            builder.append('\'');
            ArrayList<Pair> enchants = null;
            if (count > 1) {
                builder.append(", ");
                builder.append(count);
            }
            if ((t = is.m_41783_()) != null && !t.m_128456_()) {
                String key;
                String string = key = is.m_41720_() == Items.f_42690_ ? "StoredEnchantments" : "Enchantments";
                if (t.m_128425_(key, 9)) {
                    ListTag l = t.m_128437_(key, 10);
                    enchants = new ArrayList<Pair>(l.size());
                    for (int i = 0; i < l.size(); ++i) {
                        CompoundTag t1 = l.m_128728_(i);
                        enchants.add(Pair.of((Object)t1.m_128461_("id"), (Object)t1.m_128451_("lvl")));
                    }
                    t = t.m_6426_();
                    t.m_128473_(key);
                    if (t.m_128456_()) {
                        t = null;
                    }
                }
            }
            if (t != null) {
                builder.append(", ");
                NBTUtils.quoteAndEscapeForJS((StringBuilder)builder, (String)t.toString());
            }
            builder.append(')');
            if (enchants != null) {
                for (Pair e : enchants) {
                    builder.append(".enchant('");
                    builder.append((String)e.getKey());
                    builder.append("', ");
                    builder.append(e.getValue());
                    builder.append(')');
                }
            }
        } else {
            builder.append('\'');
            builder.append(this.kjs$getId());
            builder.append('\'');
        }
        return builder.toString();
    }

    @Override
    default public Ingredient kjs$asIngredient() {
        return this.kjs$self().m_41720_().kjs$asIngredient();
    }

    default public JsonObject toJsonJS() {
        JsonObject json = new JsonObject();
        json.addProperty("item", this.kjs$getId());
        json.addProperty("count", (Number)this.kjs$self().m_41613_());
        CompoundTag tag = this.kjs$self().m_41783_();
        if (tag != null) {
            json.addProperty("nbt", tag.toString());
        }
        return json;
    }

    default public OutputItem kjs$withChance(double chance) {
        return OutputItem.of(this.kjs$self(), chance);
    }

    default public ItemStack kjs$withLore(Component[] text) {
        ItemStack is = this.kjs$self().m_41777_();
        if (text.length > 0) {
            CompoundTag tag = is.m_41784_();
            CompoundTag display = tag.m_128469_("display");
            ListTag lore = display.m_128437_("Lore", 8);
            for (Component t : text) {
                lore.add((Object)StringTag.m_129297_((String)Component.Serializer.m_130703_((Component)t)));
            }
            display.m_128365_("Lore", (Tag)lore);
            tag.m_128365_("display", (Tag)display);
        }
        return is;
    }
}

