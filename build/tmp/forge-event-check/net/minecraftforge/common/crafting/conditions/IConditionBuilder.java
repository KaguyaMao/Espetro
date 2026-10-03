/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.item.Item
 */
package net.minecraftforge.common.crafting.conditions;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.crafting.conditions.AndCondition;
import net.minecraftforge.common.crafting.conditions.FalseCondition;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.ItemExistsCondition;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;
import net.minecraftforge.common.crafting.conditions.NotCondition;
import net.minecraftforge.common.crafting.conditions.OrCondition;
import net.minecraftforge.common.crafting.conditions.TagEmptyCondition;
import net.minecraftforge.common.crafting.conditions.TrueCondition;

public interface IConditionBuilder {
    default public ICondition and(ICondition ... values) {
        return new AndCondition(values);
    }

    default public ICondition FALSE() {
        return FalseCondition.INSTANCE;
    }

    default public ICondition TRUE() {
        return TrueCondition.INSTANCE;
    }

    default public ICondition not(ICondition value) {
        return new NotCondition(value);
    }

    default public ICondition or(ICondition ... values) {
        return new OrCondition(values);
    }

    default public ICondition itemExists(String namespace, String path) {
        return new ItemExistsCondition(namespace, path);
    }

    default public ICondition modLoaded(String modid) {
        return new ModLoadedCondition(modid);
    }

    default public ICondition tagEmpty(TagKey<Item> tag) {
        return new TagEmptyCondition(tag.f_203868_());
    }
}

