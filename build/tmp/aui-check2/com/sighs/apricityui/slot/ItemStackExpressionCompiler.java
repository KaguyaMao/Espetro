/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.TagParser
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.ItemLike
 */
package com.sighs.apricityui.slot;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Locale;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public final class ItemStackExpressionCompiler {
    private ItemStackExpressionCompiler() {
    }

    public static ItemStack parse(String rawLiteral) {
        String literal = ItemStackExpressionCompiler.normalize(rawLiteral);
        if (literal.isBlank() || "minecraft:air".equals(literal)) {
            return ItemStack.f_41583_;
        }
        if (literal.startsWith("{") && literal.endsWith("}")) {
            try {
                CompoundTag stackTag = TagParser.m_129359_((String)literal);
                ItemStack parsed = ItemStack.m_41712_((CompoundTag)stackTag);
                return parsed == null ? ItemStack.f_41583_ : parsed;
            }
            catch (CommandSyntaxException ignored) {
                return ItemStack.f_41583_;
            }
        }
        int nbtStart = literal.indexOf(123);
        String itemLiteral = nbtStart >= 0 ? literal.substring(0, nbtStart).trim() : literal;
        ResourceLocation itemId = ResourceLocation.m_135820_((String)itemLiteral.toLowerCase(Locale.ROOT));
        if (itemId == null || !BuiltInRegistries.f_257033_.m_7804_(itemId)) {
            return ItemStack.f_41583_;
        }
        Item item = (Item)BuiltInRegistries.f_257033_.m_7745_(itemId);
        ItemStack stack = new ItemStack((ItemLike)item);
        if (nbtStart < 0) {
            return stack;
        }
        try {
            stack.m_41751_(TagParser.m_129359_((String)literal.substring(nbtStart).trim()));
            return stack;
        }
        catch (CommandSyntaxException ignored) {
            return ItemStack.f_41583_;
        }
    }

    public static String serialize(ItemStack stack) {
        if (stack == null || stack.m_41619_()) {
            return "minecraft:air";
        }
        CompoundTag tag = new CompoundTag();
        stack.m_41739_(tag);
        return tag.toString();
    }

    public static String withCount(String rawLiteral, int requestedCount) {
        ItemStack stack = ItemStackExpressionCompiler.parse(rawLiteral);
        if (stack.m_41619_()) {
            return ItemStackExpressionCompiler.normalize(rawLiteral);
        }
        stack.m_41764_(Math.max(1, Math.min(stack.m_41741_(), requestedCount)));
        return ItemStackExpressionCompiler.serialize(stack);
    }

    public static String normalize(String raw) {
        if (raw == null) {
            return "";
        }
        String normalized = raw.trim();
        if (normalized.length() >= 2) {
            char first = normalized.charAt(0);
            char last = normalized.charAt(normalized.length() - 1);
            if (first == '\"' && last == '\"' || first == '\'' && last == '\'') {
                normalized = normalized.substring(1, normalized.length() - 1).trim();
            }
        }
        return normalized;
    }
}

