/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.BiMap
 *  com.google.common.collect.HashBiMap
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
 *  it.unimi.dsi.fastutil.booleans.BooleanConsumer
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.server.packs.resources.PreparableReloadListener
 *  net.minecraft.server.packs.resources.Resource
 *  net.minecraft.server.packs.resources.ResourceManager
 *  net.minecraft.server.packs.resources.SimplePreparableReloadListener
 *  net.minecraft.tags.BlockTags
 *  net.minecraft.tags.TagKey
 *  net.minecraft.util.GsonHelper
 *  net.minecraft.util.profiling.ProfilerFiller
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Tier
 *  net.minecraft.world.item.Tiers
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.fml.loading.FMLEnvironment
 *  net.minecraftforge.fml.loading.toposort.TopologicalSort
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.common;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
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
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.toposort.TopologicalSort;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TierSortingRegistry {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final ResourceLocation ITEM_TIER_ORDERING_JSON = new ResourceLocation("forge", "item_tier_ordering.json");
    private static boolean hasCustomTiers = false;
    private static final BiMap<ResourceLocation, Tier> tiers = HashBiMap.create();
    private static final Multimap<ResourceLocation, ResourceLocation> edges = HashMultimap.create();
    private static final Multimap<ResourceLocation, ResourceLocation> vanillaEdges = HashMultimap.create();
    private static final List<Tier> sortedTiers;
    private static final List<Tier> sortedTiersUnmodifiable;
    private static final ResourceLocation CHANNEL_NAME;
    private static final String PROTOCOL_VERSION = "1.0";
    private static final SimpleChannel SYNC_CHANNEL;

    public static synchronized Tier registerTier(Tier tier, ResourceLocation name, List<Object> after, List<Object> before) {
        if (tiers.containsKey((Object)name)) {
            throw new IllegalStateException("Duplicate tier name " + String.valueOf(name));
        }
        TierSortingRegistry.processTier(tier, name, after, before);
        hasCustomTiers = true;
        return tier;
    }

    public static List<Tier> getSortedTiers() {
        return sortedTiersUnmodifiable;
    }

    @Nullable
    public static Tier byName(ResourceLocation name) {
        return (Tier)tiers.get((Object)name);
    }

    @Nullable
    public static ResourceLocation getName(Tier tier) {
        return (ResourceLocation)tiers.inverse().get((Object)tier);
    }

    public static boolean isTierSorted(Tier tier) {
        return TierSortingRegistry.getName(tier) != null;
    }

    public static boolean isCorrectTierForDrops(Tier tier, BlockState state) {
        if (!TierSortingRegistry.isTierSorted(tier)) {
            return TierSortingRegistry.isCorrectTierVanilla(tier, state);
        }
        for (int x = sortedTiers.indexOf(tier) + 1; x < sortedTiers.size(); ++x) {
            TagKey tag = sortedTiers.get(x).getTag();
            if (tag == null || !state.m_204336_(tag)) continue;
            return false;
        }
        return true;
    }

    public static List<Tier> getTiersLowerThan(Tier tier) {
        if (!TierSortingRegistry.isTierSorted(tier)) {
            return List.of();
        }
        return sortedTiers.stream().takeWhile(t -> t != tier).toList();
    }

    private static boolean isCorrectTierVanilla(Tier tier, BlockState state) {
        int i = tier.m_6604_();
        if (i < 3 && state.m_204336_(BlockTags.f_144284_)) {
            return false;
        }
        if (i < 2 && state.m_204336_(BlockTags.f_144285_)) {
            return false;
        }
        return i >= 1 || !state.m_204336_(BlockTags.f_144286_);
    }

    private static void processTier(Tier tier, ResourceLocation name, List<Object> afters, List<Object> befores) {
        ResourceLocation other;
        tiers.put((Object)name, (Object)tier);
        for (Object after : afters) {
            other = TierSortingRegistry.getTierName(after);
            edges.put((Object)other, (Object)name);
        }
        for (Object before : befores) {
            other = TierSortingRegistry.getTierName(before);
            edges.put((Object)name, (Object)other);
        }
    }

    private static ResourceLocation getTierName(Object entry) {
        if (entry instanceof String) {
            String s = (String)entry;
            return new ResourceLocation(s);
        }
        if (entry instanceof ResourceLocation) {
            ResourceLocation rl = (ResourceLocation)entry;
            return rl;
        }
        if (entry instanceof Tier) {
            Tier t = (Tier)entry;
            return Objects.requireNonNull(TierSortingRegistry.getName(t), "Can't have sorting dependencies for tiers not registered in the TierSortingRegistry");
        }
        throw new IllegalStateException("Invalid object type passed into the tier dependencies " + String.valueOf(entry.getClass()));
    }

    static boolean allowVanilla() {
        return !hasCustomTiers;
    }

    static void init() {
        SYNC_CHANNEL.registerMessage(0, SyncPacket.class, SyncPacket::encode, TierSortingRegistry::receive, TierSortingRegistry::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        MinecraftForge.EVENT_BUS.addListener(TierSortingRegistry::playerLoggedIn);
        if (FMLEnvironment.dist == Dist.CLIENT) {
            ClientEvents.init();
        }
    }

    static PreparableReloadListener getReloadListener() {
        return new SimplePreparableReloadListener<JsonObject>(){
            final Gson gson = new GsonBuilder().create();

            @NotNull
            protected JsonObject prepare(@NotNull ResourceManager resourceManager, ProfilerFiller p) {
                JsonObject jsonObject;
                block9: {
                    Optional res = resourceManager.m_213713_(ITEM_TIER_ORDERING_JSON);
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
                            LOGGER.error("Could not read Tier sorting file " + String.valueOf(ITEM_TIER_ORDERING_JSON), (Throwable)e);
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
                        ArrayList<Tier> customOrder = new ArrayList<Tier>();
                        for (JsonElement entry : order) {
                            ResourceLocation id = new ResourceLocation(entry.getAsString());
                            Tier tier2 = TierSortingRegistry.byName(id);
                            if (tier2 == null) {
                                throw new IllegalStateException("Tier not found with name " + String.valueOf(id));
                            }
                            customOrder.add(tier2);
                        }
                        List<Tier> missingTiers = tiers.values().stream().filter(tier -> !customOrder.contains(tier)).toList();
                        if (missingTiers.size() > 0) {
                            throw new IllegalStateException("Tiers missing from the ordered list: " + missingTiers.stream().map(tier -> Objects.toString(TierSortingRegistry.getName(tier))).collect(Collectors.joining(", ")));
                        }
                        TierSortingRegistry.setTierOrder(customOrder);
                        return;
                    }
                }
                catch (Exception e) {
                    LOGGER.error("Error parsing Tier sorting file " + String.valueOf(ITEM_TIER_ORDERING_JSON), (Throwable)e);
                }
                TierSortingRegistry.recalculateItemTiers();
            }
        };
    }

    private static void recalculateItemTiers() {
        MutableGraph graph = GraphBuilder.directed().nodeOrder(ElementOrder.insertion()).build();
        for (Tier tier : tiers.values()) {
            graph.addNode((Object)tier);
        }
        edges.forEach((key, value) -> {
            if (tiers.containsKey(key) && tiers.containsKey(value)) {
                graph.putEdge((Object)((Tier)tiers.get(key)), (Object)((Tier)tiers.get(value)));
            }
        });
        List tierList = TopologicalSort.topologicalSort((Graph)graph, null);
        TierSortingRegistry.setTierOrder(tierList);
    }

    private static void setTierOrder(List<Tier> tierList) {
        TierSortingRegistry.runInServerThreadIfPossible(hasServer -> {
            sortedTiers.clear();
            sortedTiers.addAll(tierList);
            if (hasServer) {
                TierSortingRegistry.syncToAll();
            }
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

    private static void syncToAll() {
        for (ServerPlayer serverPlayer : ServerLifecycleHooks.getCurrentServer().m_6846_().m_11314_()) {
            TierSortingRegistry.syncToPlayer(serverPlayer);
        }
    }

    private static void playerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        if (player instanceof ServerPlayer) {
            ServerPlayer serverPlayer = (ServerPlayer)player;
            TierSortingRegistry.syncToPlayer(serverPlayer);
        }
    }

    private static void syncToPlayer(ServerPlayer serverPlayer) {
        if (SYNC_CHANNEL.isRemotePresent(serverPlayer.f_8906_.f_9742_) && !serverPlayer.f_8906_.f_9742_.m_129531_()) {
            SYNC_CHANNEL.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new SyncPacket(sortedTiers.stream().map(TierSortingRegistry::getName).toList()));
        }
    }

    private static SyncPacket receive(FriendlyByteBuf buffer) {
        int count = buffer.m_130242_();
        ArrayList<ResourceLocation> list = new ArrayList<ResourceLocation>();
        for (int i = 0; i < count; ++i) {
            list.add(buffer.m_130281_());
        }
        return new SyncPacket(list);
    }

    private static void handle(SyncPacket packet, Supplier<NetworkEvent.Context> context) {
        TierSortingRegistry.setTierOrder(packet.tiers.stream().map(TierSortingRegistry::byName).toList());
        context.get().setPacketHandled(true);
    }

    static {
        ResourceLocation wood = new ResourceLocation("wood");
        ResourceLocation stone = new ResourceLocation("stone");
        ResourceLocation iron = new ResourceLocation("iron");
        ResourceLocation diamond = new ResourceLocation("diamond");
        ResourceLocation netherite = new ResourceLocation("netherite");
        ResourceLocation gold = new ResourceLocation("gold");
        TierSortingRegistry.processTier((Tier)Tiers.WOOD, wood, List.of(), List.of());
        TierSortingRegistry.processTier((Tier)Tiers.GOLD, gold, List.of(wood), List.of(stone));
        TierSortingRegistry.processTier((Tier)Tiers.STONE, stone, List.of(wood), List.of(iron));
        TierSortingRegistry.processTier((Tier)Tiers.IRON, iron, List.of(stone), List.of(diamond));
        TierSortingRegistry.processTier((Tier)Tiers.DIAMOND, diamond, List.of(iron), List.of(netherite));
        TierSortingRegistry.processTier((Tier)Tiers.NETHERITE, netherite, List.of(diamond), List.of());
        vanillaEdges.putAll(edges);
        sortedTiers = new ArrayList<Tier>();
        sortedTiersUnmodifiable = Collections.unmodifiableList(sortedTiers);
        CHANNEL_NAME = new ResourceLocation("forge:tier_sorting");
        SYNC_CHANNEL = NetworkRegistry.newSimpleChannel(CHANNEL_NAME, () -> PROTOCOL_VERSION, versionFromServer -> PROTOCOL_VERSION.equals(versionFromServer) || TierSortingRegistry.allowVanilla() && NetworkRegistry.ACCEPTVANILLA.equals(versionFromServer), versionFromClient -> PROTOCOL_VERSION.equals(versionFromClient) || TierSortingRegistry.allowVanilla() && NetworkRegistry.ACCEPTVANILLA.equals(versionFromClient));
    }

    private record SyncPacket(List<ResourceLocation> tiers) {
        private void encode(FriendlyByteBuf buffer) {
            buffer.m_130130_(this.tiers.size());
            for (ResourceLocation loc : this.tiers) {
                buffer.m_130085_(loc);
            }
        }
    }

    private static class ClientEvents {
        private ClientEvents() {
        }

        public static void init() {
            MinecraftForge.EVENT_BUS.addListener(ClientEvents::clientLogInToServer);
        }

        private static void clientLogInToServer(ClientPlayerNetworkEvent.LoggingIn event) {
            if (event.getConnection() == null || !event.getConnection().m_129531_()) {
                TierSortingRegistry.recalculateItemTiers();
            }
        }
    }
}

