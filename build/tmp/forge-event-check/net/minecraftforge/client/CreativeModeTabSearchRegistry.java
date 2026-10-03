/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.searchtree.SearchRegistry
 *  net.minecraft.client.searchtree.SearchRegistry$Key
 *  net.minecraft.world.item.CreativeModeTab
 *  net.minecraft.world.item.CreativeModeTabs
 *  net.minecraft.world.item.ItemStack
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.client;

import java.util.IdentityHashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.searchtree.SearchRegistry;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.CreativeModeTabRegistry;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

public class CreativeModeTabSearchRegistry {
    private static final Map<CreativeModeTab, SearchRegistry.Key<ItemStack>> NAME_SEARCH_KEYS = new IdentityHashMap<CreativeModeTab, SearchRegistry.Key<ItemStack>>();
    private static final Map<CreativeModeTab, SearchRegistry.Key<ItemStack>> TAG_SEARCH_KEYS = new IdentityHashMap<CreativeModeTab, SearchRegistry.Key<ItemStack>>();

    public static Map<CreativeModeTab, SearchRegistry.Key<ItemStack>> getNameSearchKeys() {
        IdentityHashMap<CreativeModeTab, SearchRegistry.Key<ItemStack>> nameSearchKeys = new IdentityHashMap<CreativeModeTab, SearchRegistry.Key<ItemStack>>();
        nameSearchKeys.put(CreativeModeTabs.m_258007_(), CreativeModeTabSearchRegistry.getNameSearchKey(CreativeModeTabs.m_258007_()));
        for (CreativeModeTab tab : CreativeModeTabRegistry.getSortedCreativeModeTabs()) {
            SearchRegistry.Key<ItemStack> nameSearchKey = CreativeModeTabSearchRegistry.getNameSearchKey(tab);
            if (nameSearchKey == null) continue;
            nameSearchKeys.put(tab, nameSearchKey);
        }
        return nameSearchKeys;
    }

    public static Map<CreativeModeTab, SearchRegistry.Key<ItemStack>> getTagSearchKeys() {
        IdentityHashMap<CreativeModeTab, SearchRegistry.Key<ItemStack>> tagSearchKeys = new IdentityHashMap<CreativeModeTab, SearchRegistry.Key<ItemStack>>();
        tagSearchKeys.put(CreativeModeTabs.m_258007_(), CreativeModeTabSearchRegistry.getTagSearchKey(CreativeModeTabs.m_258007_()));
        for (CreativeModeTab tab : CreativeModeTabRegistry.getSortedCreativeModeTabs()) {
            SearchRegistry.Key<ItemStack> tagSearchKey = CreativeModeTabSearchRegistry.getTagSearchKey(tab);
            if (tagSearchKey == null) continue;
            tagSearchKeys.put(tab, tagSearchKey);
        }
        return tagSearchKeys;
    }

    @Nullable
    public static SearchRegistry.Key<ItemStack> getNameSearchKey(CreativeModeTab tab) {
        if (tab == CreativeModeTabs.m_258007_()) {
            return SearchRegistry.f_119941_;
        }
        if (!tab.hasSearchBar()) {
            return null;
        }
        return NAME_SEARCH_KEYS.computeIfAbsent(tab, k -> new SearchRegistry.Key());
    }

    @Nullable
    public static SearchRegistry.Key<ItemStack> getTagSearchKey(CreativeModeTab tab) {
        if (tab == CreativeModeTabs.m_258007_()) {
            return SearchRegistry.f_119942_;
        }
        if (!tab.hasSearchBar()) {
            return null;
        }
        return TAG_SEARCH_KEYS.computeIfAbsent(tab, k -> new SearchRegistry.Key());
    }

    @ApiStatus.Internal
    public static void createSearchTrees() {
        Minecraft.m_91087_().m_91271_();
    }
}

