/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenCustomHashMap
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenCustomHashMap
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.ItemStackLinkedSet
 */
package net.minecraftforge.common.util;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenCustomHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenCustomHashMap;
import java.util.Map;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackLinkedSet;

public class ItemStackMap {
    public static <V> Map<ItemStack, V> createTypeAndTagLinkedMap() {
        return new Object2ObjectLinkedOpenCustomHashMap(ItemStackLinkedSet.f_260558_);
    }

    public static <V> Map<ItemStack, V> createTypeAndTagMap() {
        return new Object2ObjectOpenCustomHashMap(ItemStackLinkedSet.f_260558_);
    }
}

