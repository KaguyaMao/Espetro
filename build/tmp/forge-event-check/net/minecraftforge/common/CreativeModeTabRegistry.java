/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.HashMultimap
 *  com.google.common.collect.Multimap
 *  com.google.common.graph.ElementOrder
 *  com.google.common.graph.Graph
 *  com.google.common.graph.GraphBuilder
 *  com.google.common.graph.MutableGraph
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.booleans.BooleanConsumer
 *  net.minecraft.core.Holder
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.packs.resources.PreparableReloadListener
 *  net.minecraft.server.packs.resources.Resource
 *  net.minecraft.server.packs.resources.ResourceManager
 *  net.minecraft.server.packs.resources.SimplePreparableReloadListener
 *  net.minecraft.util.GsonHelper
 *  net.minecraft.util.profiling.ProfilerFiller
 *  net.minecraft.world.item.CreativeModeTab
 *  net.minecraft.world.item.CreativeModeTabs
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.fml.loading.FMLEnvironment
 *  net.minecraftforge.fml.loading.FMLLoader
 *  net.minecraftforge.fml.loading.toposort.TopologicalSort
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraftforge.common;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.graph.ElementOrder;
import com.google.common.graph.Graph;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.CreativeModeTabSearchRegistry;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.toposort.TopologicalSort;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public final class CreativeModeTabRegistry {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ResourceLocation CREATIVE_MODE_TAB_ORDERING_JSON = new ResourceLocation("forge", "creative_mode_tab_ordering.json");
    private static final List<CreativeModeTab> SORTED_TABS = new ArrayList<CreativeModeTab>();
    private static final List<CreativeModeTab> SORTED_TABS_VIEW = Collections.unmodifiableList(SORTED_TABS);
    private static final List<CreativeModeTab> DEFAULT_TABS = new ArrayList<CreativeModeTab>();
    private static final Multimap<ResourceLocation, ResourceLocation> edges = HashMultimap.create();

    public static List<CreativeModeTab> getSortedCreativeModeTabs() {
        return SORTED_TABS_VIEW;
    }

    public static List<CreativeModeTab> getDefaultTabs() {
        return Collections.unmodifiableList(DEFAULT_TABS);
    }

    @Nullable
    public static CreativeModeTab getTab(ResourceLocation name) {
        return (CreativeModeTab)BuiltInRegistries.f_279662_.m_7745_(name);
    }

    @Nullable
    public static ResourceLocation getName(CreativeModeTab tab) {
        return BuiltInRegistries.f_279662_.m_7981_((Object)tab);
    }

    static PreparableReloadListener getReloadListener() {
        return new SimplePreparableReloadListener<JsonObject>(){
            final Gson gson = new GsonBuilder().create();

            @NotNull
            protected JsonObject prepare(@NotNull ResourceManager resourceManager, ProfilerFiller profiler) {
                JsonObject jsonObject;
                block9: {
                    Optional res = resourceManager.m_213713_(CREATIVE_MODE_TAB_ORDERING_JSON);
                    if (res.isEmpty()) {
                        return new JsonObject();
                    }
                    BufferedReader reader = ((Resource)res.get()).m_215508_();
                    try {
                        jsonObject = (JsonObject)this.gson.fromJson((Reader)reader, JsonObject.class);
                        if (reader == null) break block9;
                    }
                    catch (Throwable throwable) {
                        try {
                            if (reader != null) {
                                try {
                                    ((Reader)reader).close();
                                }
                                catch (Throwable throwable2) {
                                    throwable.addSuppressed(throwable2);
                                }
                            }
                            throw throwable;
                        }
                        catch (IOException e) {
                            LOGGER.error("Could not read CreativeModeTab sorting file " + String.valueOf(CREATIVE_MODE_TAB_ORDERING_JSON), (Throwable)e);
                            return new JsonObject();
                        }
                    }
                    ((Reader)reader).close();
                }
                return jsonObject;
            }

            protected void apply(@NotNull JsonObject data, @NotNull ResourceManager resourceManager, ProfilerFiller p) {
                try {
                    if (data.size() > 0) {
                        JsonArray order = GsonHelper.m_13933_((JsonObject)data, (String)"order");
                        ArrayList<CreativeModeTab> customOrder = new ArrayList<CreativeModeTab>();
                        for (JsonElement entry : order) {
                            ResourceLocation id = new ResourceLocation(entry.getAsString());
                            CreativeModeTab CreativeModeTab3 = CreativeModeTabRegistry.getTab(id);
                            if (CreativeModeTab3 == null) {
                                throw new IllegalStateException("CreativeModeTab not found with name " + String.valueOf(id));
                            }
                            customOrder.add(CreativeModeTab3);
                        }
                        List<CreativeModeTab> missingCreativeModeTabs = BuiltInRegistries.f_279662_.m_123024_().filter(CreativeModeTab2 -> !customOrder.contains(CreativeModeTab2)).toList();
                        if (!missingCreativeModeTabs.isEmpty()) {
                            throw new IllegalStateException("CreativeModeTabs missing from the ordered list: " + missingCreativeModeTabs.stream().map(CreativeModeTab2 -> Objects.toString(CreativeModeTabRegistry.getName(CreativeModeTab2))).collect(Collectors.joining(", ")));
                        }
                        CreativeModeTabRegistry.setCreativeModeTabOrder(customOrder);
                        return;
                    }
                }
                catch (Exception e) {
                    LOGGER.error("Error parsing CreativeModeTab sorting file " + String.valueOf(CREATIVE_MODE_TAB_ORDERING_JSON), (Throwable)e);
                }
                CreativeModeTabRegistry.recalculateItemCreativeModeTabs();
            }
        };
    }

    private static void recalculateItemCreativeModeTabs() {
        MutableGraph graph = GraphBuilder.directed().nodeOrder(ElementOrder.insertion()).build();
        for (CreativeModeTab tab2 : BuiltInRegistries.f_279662_) {
            if (DEFAULT_TABS.contains(tab2)) continue;
            graph.addNode((Object)tab2);
        }
        edges.forEach((key, value) -> {
            CreativeModeTab keyC = CreativeModeTabRegistry.getTab(key);
            CreativeModeTab valueC = CreativeModeTabRegistry.getTab(value);
            if (keyC != null && valueC != null) {
                graph.putEdge((Object)keyC, (Object)valueC);
            }
        });
        List tierList = TopologicalSort.topologicalSort((Graph)graph, Comparator.comparing(tab -> CreativeModeTabRegistry.getName(tab)));
        CreativeModeTabRegistry.setCreativeModeTabOrder(tierList);
    }

    private static void setCreativeModeTabOrder(List<CreativeModeTab> tierList) {
        CreativeModeTabRegistry.runInServerThreadIfPossible(hasServer -> {
            SORTED_TABS.clear();
            SORTED_TABS.addAll(tierList);
        });
    }

    private static void runInServerThreadIfPossible(BooleanConsumer runnable) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            server.execute(() -> runnable.accept(true));
        } else {
            runnable.accept(false);
        }
    }

    @ApiStatus.Internal
    public static void sortTabs() {
        edges.clear();
        DEFAULT_TABS.add((CreativeModeTab)BuiltInRegistries.f_279662_.m_6246_(CreativeModeTabs.f_256917_));
        DEFAULT_TABS.add((CreativeModeTab)BuiltInRegistries.f_279662_.m_6246_(CreativeModeTabs.f_256750_));
        DEFAULT_TABS.add((CreativeModeTab)BuiltInRegistries.f_279662_.m_6246_(CreativeModeTabs.f_256837_));
        DEFAULT_TABS.add((CreativeModeTab)BuiltInRegistries.f_279662_.m_6246_(CreativeModeTabs.f_257039_));
        ArrayList indexed = new ArrayList();
        BuiltInRegistries.f_279662_.m_203611_().filter(c -> !DEFAULT_TABS.contains(c.get())).forEach(indexed::add);
        int vanillaTabs = 10;
        for (int i = 0; i < vanillaTabs; ++i) {
            Holder value = (Holder)indexed.get(i);
            CreativeModeTab tab = (CreativeModeTab)value.get();
            ResourceLocation name = ((ResourceKey)value.m_203543_().orElseThrow()).m_135782_();
            if (!tab.tabsBefore.isEmpty() || !tab.tabsAfter.isEmpty()) {
                CreativeModeTabRegistry.addTabOrder(tab, name);
                continue;
            }
            if (i != 0) {
                edges.put((Object)((ResourceKey)((Holder)indexed.get(i - 1)).m_203543_().orElseThrow()).m_135782_(), (Object)name);
            }
            if (i + 1 >= indexed.size()) continue;
            edges.put((Object)name, (Object)((ResourceKey)((Holder)indexed.get(i + 1)).m_203543_().orElseThrow()).m_135782_());
        }
        ResourceLocation lastVanilla = ((ResourceKey)((Holder)indexed.get(vanillaTabs - 1)).m_203543_().orElseThrow()).m_135782_();
        for (int i = vanillaTabs; i < indexed.size(); ++i) {
            Holder value = (Holder)indexed.get(i);
            CreativeModeTab tab = (CreativeModeTab)value.get();
            ResourceLocation name = ((ResourceKey)value.m_203543_().orElseThrow()).m_135782_();
            if (!tab.tabsBefore.isEmpty() || !tab.tabsAfter.isEmpty()) {
                CreativeModeTabRegistry.addTabOrder(tab, name);
                continue;
            }
            edges.put((Object)lastVanilla, (Object)name);
        }
        CreativeModeTabRegistry.recalculateItemCreativeModeTabs();
        if (FMLEnvironment.dist == Dist.CLIENT && !FMLLoader.getLaunchHandler().isData()) {
            CreativeModeTabSearchRegistry.createSearchTrees();
        }
    }

    private static void addTabOrder(CreativeModeTab tab, ResourceLocation name) {
        for (ResourceLocation after : tab.tabsAfter) {
            edges.put((Object)name, (Object)after);
        }
        for (ResourceLocation before : tab.tabsBefore) {
            edges.put((Object)before, (Object)name);
        }
    }
}

