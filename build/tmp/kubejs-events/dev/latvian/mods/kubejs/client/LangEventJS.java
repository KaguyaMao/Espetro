/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 */
package dev.latvian.mods.kubejs.client;

import dev.latvian.mods.kubejs.event.EventJS;
import java.util.Map;
import java.util.regex.Pattern;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class LangEventJS
extends EventJS {
    public static final Pattern PATTERN = Pattern.compile("[a-z_]+");
    public final String lang;
    public final Map<Key, String> map;

    public LangEventJS(String lang, Map<Key, String> map) {
        this.lang = lang;
        this.map = map;
    }

    public void add(String namespace, String key, String value) {
        if (namespace == null || key == null || value == null || namespace.isEmpty() || key.isEmpty() || value.isEmpty()) {
            throw new IllegalArgumentException("Invalid namespace, key or value: [" + namespace + ", " + key + ", " + value + "]");
        }
        this.map.put(new Key(namespace, this.lang, key), value);
    }

    public void addAll(String namespace, Map<String, String> map) {
        for (Map.Entry<String, String> e : map.entrySet()) {
            this.add(namespace, e.getKey(), e.getValue());
        }
    }

    public void add(String key, String value) {
        this.add("minecraft", key, value);
    }

    public void addAll(Map<String, String> map) {
        this.addAll("minecraft", map);
    }

    public void renameItem(ItemStack item, String name) {
        String d;
        if (item != null && !item.m_41619_() && (d = item.m_41778_()) != null && !d.isEmpty()) {
            this.add(item.kjs$getMod(), d, name);
        }
    }

    public void renameBlock(Block block, String name) {
        String d;
        if (block != null && block != Blocks.f_50016_ && (d = block.m_7705_()) != null && !d.isEmpty()) {
            this.add(block.kjs$getMod(), d, name);
        }
    }

    public void renameEntity(ResourceLocation id, String name) {
        this.add(id.m_135827_(), "entity." + id.m_135827_() + "." + id.m_135815_().replace('/', '.'), name);
    }

    public void renameBiome(ResourceLocation id, String name) {
        this.add(id.m_135827_(), "biome." + id.m_135827_() + "." + id.m_135815_().replace('/', '.'), name);
    }

    public record Key(String namespace, String lang, String key) {
    }
}

