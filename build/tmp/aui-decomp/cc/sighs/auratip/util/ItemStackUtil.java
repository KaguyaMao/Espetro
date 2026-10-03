/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonParser
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  net.minecraft.world.item.ItemStack
 */
package cc.sighs.auratip.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import net.minecraft.world.item.ItemStack;

public final class ItemStackUtil {
    private ItemStackUtil() {
    }

    public static ItemStack fromJson(String json) {
        JsonElement element = JsonParser.parseString((String)json);
        return (ItemStack)ItemStack.f_41582_.parse((DynamicOps)JsonOps.INSTANCE, (Object)element).getOrThrow(false, errorMsg -> {
            throw new RuntimeException("Parse error: " + errorMsg);
        });
    }

    public static ItemStack fromJsonElement(JsonElement json) {
        return (ItemStack)ItemStack.f_41582_.parse((DynamicOps)JsonOps.INSTANCE, (Object)json).getOrThrow(false, errorMsg -> {
            throw new RuntimeException("Failed to parse ItemStack: " + errorMsg);
        });
    }
}

