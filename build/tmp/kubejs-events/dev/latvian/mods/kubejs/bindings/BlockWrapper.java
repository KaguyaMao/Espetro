/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.Util
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Holder
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.level.block.Block
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.bindings;

import dev.latvian.mods.kubejs.block.predicate.BlockEntityPredicate;
import dev.latvian.mods.kubejs.block.predicate.BlockIDPredicate;
import dev.latvian.mods.kubejs.block.predicate.BlockPredicate;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.util.Tags;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

@Info(value="Various block related helper functions")
public class BlockWrapper {
    private static Map<String, Direction> facingMap;

    public static BlockIDPredicate id(ResourceLocation id) {
        return new BlockIDPredicate(id);
    }

    public static BlockIDPredicate id(ResourceLocation id, Map<String, Object> properties) {
        BlockIDPredicate b = BlockWrapper.id(id);
        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            b = b.with(entry.getKey(), entry.getValue().toString());
        }
        return b;
    }

    public static BlockEntityPredicate entity(ResourceLocation id) {
        return new BlockEntityPredicate(id);
    }

    public static BlockPredicate custom(BlockPredicate predicate) {
        return predicate;
    }

    @Info(value="Get a map of direction name to Direction. Functionally identical to Direction.ALL")
    public static Map<String, Direction> getFacing() {
        if (facingMap == null) {
            facingMap = new HashMap<String, Direction>(6);
            for (Direction facing : Direction.values()) {
                facingMap.put(facing.m_7912_(), facing);
            }
        }
        return facingMap;
    }

    @Info(value="Gets a Block from a block id")
    public static Block getBlock(ResourceLocation id) {
        return RegistryInfo.BLOCK.getValue(id);
    }

    @Info(value="Gets a blocks id from the Block")
    @Nullable
    public static ResourceLocation getId(Block block) {
        return RegistryInfo.BLOCK.getId(block);
    }

    @Info(value="Gets a list of the classname of all registered blocks")
    public static List<String> getTypeList() {
        ArrayList<String> list = new ArrayList<String>();
        for (Map.Entry<ResourceKey<Block>, Block> block : RegistryInfo.BLOCK.entrySet()) {
            list.add(block.getKey().m_135782_().toString());
        }
        return list;
    }

    @Info(value="Gets a list of all blocks with tags")
    public static List<ResourceLocation> getTaggedIds(ResourceLocation tag) {
        return (List)Util.m_137469_(new LinkedList(), list -> {
            for (Holder holder : BuiltInRegistries.f_256975_.m_206058_(Tags.block(tag))) {
                holder.m_203543_().map(ResourceKey::m_135782_).ifPresent(list::add);
            }
        });
    }
}

