/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.util.valueproviders.IntProvider
 *  net.minecraft.util.valueproviders.UniformInt
 *  net.minecraft.world.item.ItemStack
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.item;

import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.platform.IngredientPlatformHelper;
import dev.latvian.mods.kubejs.recipe.OutputReplacement;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.ReplacementMatch;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class OutputItem
implements OutputReplacement {
    public static final OutputItem EMPTY = new OutputItem(ItemStack.f_41583_, Double.NaN, null);
    public final ItemStack item;
    public final double chance;
    public final IntProvider rolls;

    public static OutputItem of(ItemStack item, double chance) {
        return item.m_41619_() ? EMPTY : new OutputItem(item, chance, null);
    }

    public static OutputItem of(Object from) {
        if (from instanceof OutputItem) {
            OutputItem out = (OutputItem)from;
            return out;
        }
        if (from instanceof ItemStack) {
            ItemStack stack = (ItemStack)from;
            return OutputItem.of(stack, Double.NaN);
        }
        ItemStack item = ItemStackJS.of(from);
        if (item.m_41619_()) {
            return EMPTY;
        }
        double chance = Double.NaN;
        UniformInt rolls = null;
        if (from instanceof JsonObject) {
            JsonObject j = (JsonObject)from;
            if (j.has("chance")) {
                chance = j.get("chance").getAsDouble();
            }
            if (j.has("minRolls") && j.has("maxRolls")) {
                rolls = UniformInt.m_146622_((int)j.get("minRolls").getAsInt(), (int)j.get("maxRolls").getAsInt());
            }
        }
        return new OutputItem(item, chance, (IntProvider)rolls);
    }

    @Deprecated
    protected OutputItem(ItemStack item, double chance) {
        this(item, chance, null);
    }

    protected OutputItem(ItemStack item, double chance, @Nullable IntProvider rolls) {
        this.item = item;
        this.chance = chance;
        this.rolls = rolls;
    }

    public OutputItem withCount(int count) {
        return new OutputItem(this.item.kjs$withCount(count), this.chance, this.rolls);
    }

    public OutputItem withChance(double chance) {
        return new OutputItem(this.item.m_41777_(), chance, this.rolls);
    }

    public OutputItem withRolls(IntProvider rolls) {
        return new OutputItem(this.item.m_41777_(), this.chance, rolls);
    }

    public OutputItem withRolls(int min, int max) {
        return this.withRolls((IntProvider)UniformInt.m_146622_((int)min, (int)max));
    }

    public boolean hasChance() {
        return !Double.isNaN(this.chance);
    }

    public double getChance() {
        return this.chance;
    }

    public int getCount() {
        return this.item.m_41613_();
    }

    public CompoundTag getNbt() {
        return this.item.m_41783_();
    }

    public String toString() {
        return this.item.kjs$toItemString();
    }

    public boolean isEmpty() {
        return this == EMPTY;
    }

    @Override
    public Object replaceOutput(RecipeJS recipe, ReplacementMatch match, OutputReplacement original) {
        if (original instanceof OutputItem) {
            OutputItem o = (OutputItem)original;
            OutputItem replacement = new OutputItem(this.item.m_41777_(), o.chance, o.rolls);
            replacement.item.m_41764_(o.getCount());
            return replacement;
        }
        return new OutputItem(this.item.m_41777_(), Double.NaN, null);
    }

    @Deprecated
    public InputItem ignoreNBT() {
        ConsoleJS console = ConsoleJS.getCurrent(ConsoleJS.SERVER);
        console.warn("You don't need to call .ignoreNBT() anymore, all item ingredients ignore NBT by default!");
        return InputItem.of(this.item.m_41720_().kjs$asIngredient(), this.item.m_41613_());
    }

    public InputItem weakNBT() {
        return InputItem.of(IngredientPlatformHelper.get().weakNBT(this.item), this.item.m_41613_());
    }

    public InputItem strongNBT() {
        return InputItem.of(IngredientPlatformHelper.get().strongNBT(this.item), this.item.m_41613_());
    }
}

