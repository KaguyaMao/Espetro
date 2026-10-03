/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  com.google.gson.FieldNamingPolicy
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonObject
 *  com.google.gson.TypeAdapterFactory
 *  com.google.gson.reflect.TypeToken
 *  kotlin.Lazy
 *  kotlin.LazyKt
 *  kotlin.Metadata
 *  kotlin.Unit
 *  kotlin.collections.CollectionsKt
 *  kotlin.jvm.JvmField
 *  kotlin.jvm.JvmOverloads
 *  kotlin.jvm.JvmStatic
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlinx.serialization.SerializationStrategy
 *  kotlinx.serialization.SerializersKt
 *  kotlinx.serialization.json.Json
 *  kotlinx.serialization.json.JsonBuilder
 *  kotlinx.serialization.json.JsonKt
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.server.packs.resources.PreparableReloadListener
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.phys.Vec2
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.client.event.RegisterClientReloadListenersEvent
 *  net.minecraftforge.event.AddReloadListenerEvent
 *  net.minecraftforge.event.OnDatapackSyncEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.atsuishio.superbwarfare.data;

import com.atsuishio.superbwarfare.data.ComplexJsonResourceReloadListener;
import com.atsuishio.superbwarfare.data.DataMap;
import com.atsuishio.superbwarfare.data.ModColor;
import com.atsuishio.superbwarfare.data.ObjectToList;
import com.atsuishio.superbwarfare.data.ResourceLocationAdapter;
import com.atsuishio.superbwarfare.data.SoundEventAdapter;
import com.atsuishio.superbwarfare.data.StringOrVec3;
import com.atsuishio.superbwarfare.data.StringToObject;
import com.atsuishio.superbwarfare.data.Vec2Adapter;
import com.atsuishio.superbwarfare.data.Vec3Adapter;
import com.atsuishio.superbwarfare.data.vehicle.subdata.CollisionLevel;
import com.atsuishio.superbwarfare.network.message.receive.DataSyncMessage;
import com.atsuishio.superbwarfare.serialization.SerializersModuleKt;
import com.atsuishio.superbwarfare.tools.MinecraftUtil;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.JvmField;
import kotlin.jvm.JvmOverloads;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlinx.serialization.SerializationStrategy;
import kotlinx.serialization.SerializersKt;
import kotlinx.serialization.json.Json;
import kotlinx.serialization.json.JsonBuilder;
import kotlinx.serialization.json.JsonKt;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Mod.EventBusSubscriber(modid="superbwarfare")
@Metadata(mv={2, 0, 0}, k=1, xi=48, d1={"\u0000z\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010%\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010$\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u00c7\u0002\u0018\u00002\u00020\u0001:\u000234B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\u001c\u001a\u00020\u001d2\u0006\u0010\u001e\u001a\u00020\u001fH\u0007J\\\u0010 \u001a\b\u0012\u0004\u0012\u0002H\"0!\"\u0004\b\u0000\u0010\"2\u0006\u0010#\u001a\u00020\u00102\f\u0010$\u001a\b\u0012\u0004\u0012\u0002H\"0%2\b\b\u0002\u0010&\u001a\u00020'2\b\b\u0002\u0010(\u001a\u00020'2\u001c\b\u0002\u0010)\u001a\u0016\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u00010+\u0018\u00010*H\u0007JR\u0010,\u001a\b\u0012\u0004\u0012\u0002H\"0!\"\u0004\b\u0000\u0010\"2\u0006\u0010#\u001a\u00020\u00102\f\u0010$\u001a\b\u0012\u0004\u0012\u0002H\"0%2\b\b\u0002\u0010(\u001a\u00020'2\u001c\b\u0002\u0010)\u001a\u0016\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u00010+\u0018\u00010*H\u0007J\b\u0010-\u001a\u00020.H\u0007J\u0014\u0010/\u001a\u0004\u0018\u00010\u00012\b\u00100\u001a\u0004\u0018\u00010\u0001H\u0007J\u0010\u00101\u001a\u00020\u001d2\u0006\u0010\u001e\u001a\u000202H\u0007R\u0010\u0010\u0004\u001a\u00020\u00058\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\u000e\n\u0000\u0012\u0004\b\b\u0010\u0003\u001a\u0004\b\t\u0010\nR\u001c\u0010\u000b\u001a\u000e\u0012\u0004\u0012\u00020\u0001\u0012\u0004\u0012\u00020\r0\f8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R!\u0010\u000e\u001a\u0012\u0012\u0004\u0012\u00020\u0010\u0012\b\u0012\u0006\u0012\u0002\b\u00030\u00110\u000f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R!\u0010\u0014\u001a\u0012\u0012\u0004\u0012\u00020\u0010\u0012\b\u0012\u0006\u0012\u0002\b\u00030\u00110\u000f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0013R\u0011\u0010\u0016\u001a\u00020\u0017\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0019R\u0011\u0010\u001a\u001a\u00020\u0017\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u0019\u00a8\u00065"}, d2={"Lcom/atsuishio/superbwarfare/data/DataLoader;", "", "<init>", "()V", "GSON", "Lcom/google/gson/Gson;", "JSON", "Lkotlinx/serialization/json/Json;", "getJSON$annotations", "getJSON", "()Lkotlinx/serialization/json/Json;", "JSON_OBJECT_CACHE", "Lcom/google/common/cache/LoadingCache;", "Lcom/google/gson/JsonObject;", "LOADED_DATA", "", "", "Lcom/atsuishio/superbwarfare/data/DataLoader$GeneralData;", "getLOADED_DATA", "()Ljava/util/Map;", "LOADED_RESOURCE", "getLOADED_RESOURCE", "SERVER_LISTENER", "Lcom/atsuishio/superbwarfare/data/ComplexJsonResourceReloadListener;", "getSERVER_LISTENER", "()Lcom/atsuishio/superbwarfare/data/ComplexJsonResourceReloadListener;", "CLIENT_LISTENER", "getCLIENT_LISTENER", "addDataReloadListener", "", "event", "Lnet/minecraftforge/event/AddReloadListenerEvent;", "createData", "Lcom/atsuishio/superbwarfare/data/DataMap;", "T", "directory", "clazz", "Ljava/lang/Class;", "synced", "", "isKtData", "onReload", "Ljava/util/function/Consumer;", "", "createResource", "createCommonBuilder", "Lcom/google/gson/GsonBuilder;", "processValue", "value", "onDataPackSync", "Lnet/minecraftforge/event/OnDatapackSyncEvent;", "GeneralData", "ClientReloadListener", "superbwarfare"})
@SourceDebugExtension(value={"SMAP\nDataLoader.kt\nKotlin\n*S Kotlin\n*F\n+ 1 DataLoader.kt\ncom/atsuishio/superbwarfare/data/DataLoader\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 Maps.kt\nkotlin/collections/MapsKt__MapsKt\n+ 4 _Maps.kt\nkotlin/collections/MapsKt___MapsKt\n*L\n1#1,184:1\n1557#2:185\n1628#2,3:186\n535#3:189\n520#3,6:190\n216#4,2:196\n*S KotlinDebug\n*F\n+ 1 DataLoader.kt\ncom/atsuishio/superbwarfare/data/DataLoader\n*L\n130#1:185\n130#1:186,3\n161#1:189\n161#1:190,6\n161#1:196,2\n*E\n"})
public final class DataLoader {
    @NotNull
    public static final DataLoader INSTANCE = new DataLoader();
    @JvmField
    @NotNull
    public static final Gson GSON;
    @NotNull
    private static final Json JSON;
    @JvmField
    @NotNull
    public static final LoadingCache<Object, JsonObject> JSON_OBJECT_CACHE;
    @NotNull
    private static final Map<String, GeneralData<?>> LOADED_DATA;
    @NotNull
    private static final Map<String, GeneralData<?>> LOADED_RESOURCE;
    @NotNull
    private static final ComplexJsonResourceReloadListener SERVER_LISTENER;
    @NotNull
    private static final ComplexJsonResourceReloadListener CLIENT_LISTENER;

    private DataLoader() {
    }

    @NotNull
    public final Json getJSON() {
        return JSON;
    }

    public static /* synthetic */ void getJSON$annotations() {
    }

    @NotNull
    public final Map<String, GeneralData<?>> getLOADED_DATA() {
        return LOADED_DATA;
    }

    @NotNull
    public final Map<String, GeneralData<?>> getLOADED_RESOURCE() {
        return LOADED_RESOURCE;
    }

    @NotNull
    public final ComplexJsonResourceReloadListener getSERVER_LISTENER() {
        return SERVER_LISTENER;
    }

    @NotNull
    public final ComplexJsonResourceReloadListener getCLIENT_LISTENER() {
        return CLIENT_LISTENER;
    }

    @SubscribeEvent
    public final void addDataReloadListener(@NotNull AddReloadListenerEvent event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        event.addListener((PreparableReloadListener)SERVER_LISTENER);
    }

    @JvmOverloads
    @NotNull
    public final <T> DataMap<T> createData(@NotNull String directory, @NotNull Class<T> clazz, boolean synced, boolean isKtData, @Nullable Consumer<Map<String, Object>> onReload) {
        Intrinsics.checkNotNullParameter((Object)directory, (String)"directory");
        Intrinsics.checkNotNullParameter(clazz, (String)"clazz");
        GeneralData<?> data = LOADED_DATA.get(directory);
        if (data != null) {
            DataMap<?> dataMap = data.getProxyMap();
            Intrinsics.checkNotNull(dataMap, (String)"null cannot be cast to non-null type com.atsuishio.superbwarfare.data.DataMap<T of com.atsuishio.superbwarfare.data.DataLoader.createData>");
            return dataMap;
        }
        DataMap proxyMap = new DataMap(directory, LOADED_DATA);
        LOADED_DATA.put(directory, new GeneralData(clazz, proxyMap, new HashMap<String, Object>(), synced, isKtData, onReload));
        return proxyMap;
    }

    public static /* synthetic */ DataMap createData$default(DataLoader dataLoader, String string, Class clazz, boolean bl, boolean bl2, Consumer consumer, int n, Object object) {
        if ((n & 4) != 0) {
            bl = false;
        }
        if ((n & 8) != 0) {
            bl2 = false;
        }
        if ((n & 0x10) != 0) {
            consumer = null;
        }
        return dataLoader.createData(string, clazz, bl, bl2, consumer);
    }

    @JvmOverloads
    @NotNull
    public final <T> DataMap<T> createResource(@NotNull String directory, @NotNull Class<T> clazz, boolean isKtData, @Nullable Consumer<Map<String, Object>> onReload) {
        Intrinsics.checkNotNullParameter((Object)directory, (String)"directory");
        Intrinsics.checkNotNullParameter(clazz, (String)"clazz");
        GeneralData<?> resource = LOADED_RESOURCE.get(directory);
        if (resource != null) {
            DataMap<?> dataMap = resource.getProxyMap();
            Intrinsics.checkNotNull(dataMap, (String)"null cannot be cast to non-null type com.atsuishio.superbwarfare.data.DataMap<T of com.atsuishio.superbwarfare.data.DataLoader.createResource>");
            return dataMap;
        }
        DataMap proxyMap = new DataMap(directory, LOADED_RESOURCE);
        LOADED_RESOURCE.put(directory, new GeneralData(clazz, proxyMap, new HashMap<String, Object>(), false, isKtData, onReload));
        return proxyMap;
    }

    public static /* synthetic */ DataMap createResource$default(DataLoader dataLoader, String string, Class clazz, boolean bl, Consumer consumer, int n, Object object) {
        if ((n & 4) != 0) {
            bl = false;
        }
        if ((n & 8) != 0) {
            consumer = null;
        }
        return dataLoader.createResource(string, clazz, bl, consumer);
    }

    @JvmStatic
    @NotNull
    public static final GsonBuilder createCommonBuilder() {
        GsonBuilder gsonBuilder = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).setLenient().serializeSpecialFloatingPointValues().registerTypeAdapter((Type)((Object)Vec2.class), (Object)new Vec2Adapter()).registerTypeAdapter((Type)((Object)Vec3.class), (Object)new Vec3Adapter()).registerTypeAdapter((Type)((Object)ResourceLocation.class), (Object)new ResourceLocationAdapter()).registerTypeAdapter((Type)((Object)SoundEvent.class), (Object)new SoundEventAdapter()).registerTypeAdapter((Type)((Object)ModColor.class), (Object)new ModColor.ModColorAdapter()).registerTypeAdapter((Type)((Object)StringOrVec3.class), (Object)new StringOrVec3.StringOrVec3Adapter()).registerTypeAdapter((Type)((Object)CollisionLevel.Limit.class), (Object)new CollisionLevel.LimitAdapter()).registerTypeAdapterFactory((TypeAdapterFactory)new ObjectToList.AdapterFactory()).registerTypeAdapterFactory((TypeAdapterFactory)new StringToObject.AdapterFactory());
        Intrinsics.checkNotNullExpressionValue((Object)gsonBuilder, (String)"registerTypeAdapterFactory(...)");
        return gsonBuilder;
    }

    /*
     * WARNING - void declaration
     */
    @JvmStatic
    @Nullable
    public static final Object processValue(@Nullable Object value) {
        Object object;
        Object object2 = value;
        if (object2 instanceof ObjectToList) {
            void $this$mapTo$iv$iv;
            Iterable $this$map$iv = ((ObjectToList)value).list;
            boolean $i$f$map = false;
            Iterable iterable = $this$map$iv;
            Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
            boolean $i$f$mapTo = false;
            Iterator iterator = $this$mapTo$iv$iv.iterator();
            while (iterator.hasNext()) {
                void value2;
                Object item$iv$iv;
                Object t = item$iv$iv = iterator.next();
                Collection collection = destination$iv$iv;
                boolean bl = false;
                collection.add(DataLoader.processValue(value2));
            }
            object = (List)destination$iv$iv;
        } else {
            object = object2 instanceof StringToObject ? DataLoader.processValue(((StringToObject)value).value) : value;
        }
        return object;
    }

    /*
     * WARNING - void declaration
     */
    @SubscribeEvent
    public final void onDataPackSync(@NotNull OnDatapackSyncEvent event) {
        void $this$filterTo$iv$iv;
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        MinecraftServer server = event.getPlayerList().m_7873_();
        Map<String, GeneralData<?>> $this$filter$iv = LOADED_DATA;
        boolean $i$f$filter = false;
        Object object = $this$filter$iv;
        Map destination$iv$iv = new LinkedHashMap();
        boolean $i$f$filterTo = false;
        Iterator iterator = $this$filterTo$iv$iv.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry element$iv$iv;
            Map.Entry it = element$iv$iv = iterator.next();
            boolean bl = false;
            if (!((GeneralData)it.getValue()).getSynced()) continue;
            destination$iv$iv.put(element$iv$iv.getKey(), element$iv$iv.getValue());
        }
        Map $this$forEach$iv = destination$iv$iv;
        boolean $i$f$forEach = false;
        object = $this$forEach$iv.entrySet().iterator();
        while (object.hasNext()) {
            ServerPlayer player;
            Map.Entry element$iv;
            Map.Entry entry = element$iv = (Map.Entry)object.next();
            boolean bl = false;
            String key = (String)entry.getKey();
            GeneralData data = (GeneralData)entry.getValue();
            DataSyncMessage packet = new DataSyncMessage(key, data.serializeToString());
            ServerPlayer serverPlayer = player = event.getPlayer();
            for (ServerPlayer player2 : serverPlayer != null ? CollectionsKt.listOf((Object)serverPlayer) : event.getPlayerList().m_11314_()) {
                if (server.m_7779_(player2.m_36316_())) continue;
                MinecraftUtil.sendPacket((Player)player2, packet);
            }
        }
    }

    @JvmOverloads
    @NotNull
    public final <T> DataMap<T> createData(@NotNull String directory, @NotNull Class<T> clazz, boolean synced, boolean isKtData) {
        Intrinsics.checkNotNullParameter((Object)directory, (String)"directory");
        Intrinsics.checkNotNullParameter(clazz, (String)"clazz");
        return DataLoader.createData$default(this, directory, clazz, synced, isKtData, null, 16, null);
    }

    @JvmOverloads
    @NotNull
    public final <T> DataMap<T> createData(@NotNull String directory, @NotNull Class<T> clazz, boolean synced) {
        Intrinsics.checkNotNullParameter((Object)directory, (String)"directory");
        Intrinsics.checkNotNullParameter(clazz, (String)"clazz");
        return DataLoader.createData$default(this, directory, clazz, synced, false, null, 24, null);
    }

    @JvmOverloads
    @NotNull
    public final <T> DataMap<T> createData(@NotNull String directory, @NotNull Class<T> clazz) {
        Intrinsics.checkNotNullParameter((Object)directory, (String)"directory");
        Intrinsics.checkNotNullParameter(clazz, (String)"clazz");
        return DataLoader.createData$default(this, directory, clazz, false, false, null, 28, null);
    }

    @JvmOverloads
    @NotNull
    public final <T> DataMap<T> createResource(@NotNull String directory, @NotNull Class<T> clazz, boolean isKtData) {
        Intrinsics.checkNotNullParameter((Object)directory, (String)"directory");
        Intrinsics.checkNotNullParameter(clazz, (String)"clazz");
        return DataLoader.createResource$default(this, directory, clazz, isKtData, null, 8, null);
    }

    @JvmOverloads
    @NotNull
    public final <T> DataMap<T> createResource(@NotNull String directory, @NotNull Class<T> clazz) {
        Intrinsics.checkNotNullParameter((Object)directory, (String)"directory");
        Intrinsics.checkNotNullParameter(clazz, (String)"clazz");
        return DataLoader.createResource$default(this, directory, clazz, false, null, 12, null);
    }

    private static final Unit JSON$lambda$0(JsonBuilder $this$Json) {
        Intrinsics.checkNotNullParameter((Object)$this$Json, (String)"$this$Json");
        $this$Json.setLenient(true);
        $this$Json.setIgnoreUnknownKeys(true);
        $this$Json.setSerializersModule(SerializersModuleKt.getSerializersModule());
        $this$Json.setAllowTrailingComma(true);
        $this$Json.setAllowSpecialFloatingPointValues(true);
        return Unit.INSTANCE;
    }

    static {
        Gson gson = DataLoader.createCommonBuilder().create();
        Intrinsics.checkNotNullExpressionValue((Object)gson, (String)"create(...)");
        GSON = gson;
        JSON = JsonKt.Json$default(null, DataLoader::JSON$lambda$0, (int)1, null);
        LoadingCache loadingCache = CacheBuilder.newBuilder().weakKeys().build((CacheLoader)new CacheLoader<Object, JsonObject>(){

            public JsonObject load(Object obj) {
                Intrinsics.checkNotNullParameter((Object)obj, (String)"obj");
                JsonObject jsonObject = DataLoader.GSON.toJsonTree(obj).getAsJsonObject();
                Intrinsics.checkNotNullExpressionValue((Object)jsonObject, (String)"getAsJsonObject(...)");
                return jsonObject;
            }
        });
        Intrinsics.checkNotNullExpressionValue((Object)loadingCache, (String)"build(...)");
        JSON_OBJECT_CACHE = loadingCache;
        LOADED_DATA = new LinkedHashMap();
        LOADED_RESOURCE = new LinkedHashMap();
        SERVER_LISTENER = new ComplexJsonResourceReloadListener(LOADED_DATA);
        CLIENT_LISTENER = new ComplexJsonResourceReloadListener(LOADED_RESOURCE);
    }

    @Mod.EventBusSubscriber(modid="superbwarfare", bus=Mod.EventBusSubscriber.Bus.MOD)
    @Metadata(mv={2, 0, 0}, k=1, xi=48, d1={"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u00c1\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0007\u00a8\u0006\b"}, d2={"Lcom/atsuishio/superbwarfare/data/DataLoader$ClientReloadListener;", "", "<init>", "()V", "addResourceReloadListener", "", "event", "Lnet/minecraftforge/client/event/RegisterClientReloadListenersEvent;", "superbwarfare"})
    public static final class ClientReloadListener {
        @NotNull
        public static final ClientReloadListener INSTANCE = new ClientReloadListener();

        private ClientReloadListener() {
        }

        @SubscribeEvent
        public final void addResourceReloadListener(@NotNull RegisterClientReloadListenersEvent event) {
            Intrinsics.checkNotNullParameter((Object)event, (String)"event");
            event.registerReloadListener((PreparableReloadListener)INSTANCE.getCLIENT_LISTENER());
        }
    }

    @Metadata(mv={2, 0, 0}, k=1, xi=48, d1={"\u0000J\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010$\n\u0002\b\u000e\n\u0002\u0018\u0002\n\u0002\b\u0010\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u0000*\u0004\b\u0000\u0010\u00012\u00020\u0002Bs\u0012\n\u0010\u0003\u001a\u0006\u0012\u0002\b\u00030\u0004\u0012\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00028\u00000\u0006\u0012\"\u0010\u0007\u001a\u001e\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\u00020\nj\u000e\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\u0002`\b\u0012\u0006\u0010\u000b\u001a\u00020\f\u0012\b\b\u0002\u0010\r\u001a\u00020\f\u0012\u001a\u0010\u000e\u001a\u0016\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\u00020\u0010\u0018\u00010\u000f\u00a2\u0006\u0004\b\u0011\u0010\u0012J\u0006\u0010$\u001a\u00020\tJ\r\u0010%\u001a\u0006\u0012\u0002\b\u00030\u0004H\u00c6\u0003J\u000f\u0010&\u001a\b\u0012\u0004\u0012\u00028\u00000\u0006H\u00c6\u0003J*\u0010'\u001a\u001e\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\u00020\nj\u000e\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\u0002`\bH\u00c6\u0003\u00a2\u0006\u0002\u0010\u0018J\t\u0010(\u001a\u00020\fH\u00c6\u0003J\t\u0010)\u001a\u00020\fH\u00c6\u0003J\u001d\u0010*\u001a\u0016\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\u00020\u0010\u0018\u00010\u000fH\u00c6\u0003J\u008a\u0001\u0010+\u001a\b\u0012\u0004\u0012\u00028\u00000\u00002\f\b\u0002\u0010\u0003\u001a\u0006\u0012\u0002\b\u00030\u00042\u000e\b\u0002\u0010\u0005\u001a\b\u0012\u0004\u0012\u00028\u00000\u00062$\b\u0002\u0010\u0007\u001a\u001e\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\u00020\nj\u000e\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\u0002`\b2\b\b\u0002\u0010\u000b\u001a\u00020\f2\b\b\u0002\u0010\r\u001a\u00020\f2\u001c\b\u0002\u0010\u000e\u001a\u0016\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\u00020\u0010\u0018\u00010\u000fH\u00c6\u0001\u00a2\u0006\u0002\u0010,J\u0013\u0010-\u001a\u00020\f2\b\u0010.\u001a\u0004\u0018\u00010\u0002H\u00d6\u0003J\t\u0010/\u001a\u000200H\u00d6\u0001J\t\u00101\u001a\u00020\tH\u00d6\u0001R\u0015\u0010\u0003\u001a\u0006\u0012\u0002\b\u00030\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0017\u0010\u0005\u001a\b\u0012\u0004\u0012\u00028\u00000\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R/\u0010\u0007\u001a\u001e\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\u00020\nj\u000e\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\u0002`\b\u00a2\u0006\n\n\u0002\u0010\u0019\u001a\u0004\b\u0017\u0010\u0018R\u0011\u0010\u000b\u001a\u00020\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u001bR\u0011\u0010\r\u001a\u00020\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u001bR%\u0010\u000e\u001a\u0016\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\u00020\u0010\u0018\u00010\u000f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u001dR\u001f\u0010\u001e\u001a\u0006\u0012\u0002\b\u00030\u001f8FX\u0086\u0084\u0002\u00a2\u0006\f\n\u0004\b\"\u0010#\u001a\u0004\b \u0010!\u00a8\u00062"}, d2={"Lcom/atsuishio/superbwarfare/data/DataLoader$GeneralData;", "T", "", "type", "Ljava/lang/Class;", "proxyMap", "Lcom/atsuishio/superbwarfare/data/DataMap;", "dataMap", "Lkotlin/collections/HashMap;", "", "Ljava/util/HashMap;", "synced", "", "isKtData", "onReload", "Ljava/util/function/Consumer;", "", "<init>", "(Ljava/lang/Class;Lcom/atsuishio/superbwarfare/data/DataMap;Ljava/util/HashMap;ZZLjava/util/function/Consumer;)V", "getType", "()Ljava/lang/Class;", "getProxyMap", "()Lcom/atsuishio/superbwarfare/data/DataMap;", "getDataMap", "()Ljava/util/HashMap;", "Ljava/util/HashMap;", "getSynced", "()Z", "getOnReload", "()Ljava/util/function/Consumer;", "mapType", "Lcom/google/gson/reflect/TypeToken;", "getMapType", "()Lcom/google/gson/reflect/TypeToken;", "mapType$delegate", "Lkotlin/Lazy;", "serializeToString", "component1", "component2", "component3", "component4", "component5", "component6", "copy", "(Ljava/lang/Class;Lcom/atsuishio/superbwarfare/data/DataMap;Ljava/util/HashMap;ZZLjava/util/function/Consumer;)Lcom/atsuishio/superbwarfare/data/DataLoader$GeneralData;", "equals", "other", "hashCode", "", "toString", "superbwarfare"})
    public static final class GeneralData<T> {
        @NotNull
        private final Class<?> type;
        @NotNull
        private final DataMap<T> proxyMap;
        @NotNull
        private final HashMap<String, Object> dataMap;
        private final boolean synced;
        private final boolean isKtData;
        @Nullable
        private final Consumer<Map<String, Object>> onReload;
        @NotNull
        private final Lazy mapType$delegate;

        public GeneralData(@NotNull Class<?> type, @NotNull DataMap<T> proxyMap, @NotNull HashMap<String, Object> dataMap, boolean synced, boolean isKtData, @Nullable Consumer<Map<String, Object>> onReload) {
            Intrinsics.checkNotNullParameter(type, (String)"type");
            Intrinsics.checkNotNullParameter(proxyMap, (String)"proxyMap");
            Intrinsics.checkNotNullParameter(dataMap, (String)"dataMap");
            this.type = type;
            this.proxyMap = proxyMap;
            this.dataMap = dataMap;
            this.synced = synced;
            this.isKtData = isKtData;
            this.onReload = onReload;
            this.mapType$delegate = LazyKt.lazy(() -> GeneralData.mapType_delegate$lambda$0(this));
        }

        public /* synthetic */ GeneralData(Class clazz, DataMap dataMap, HashMap hashMap, boolean bl, boolean bl2, Consumer consumer, int n, DefaultConstructorMarker defaultConstructorMarker) {
            if ((n & 0x10) != 0) {
                bl2 = false;
            }
            this(clazz, dataMap, hashMap, bl, bl2, consumer);
        }

        @NotNull
        public final Class<?> getType() {
            return this.type;
        }

        @NotNull
        public final DataMap<T> getProxyMap() {
            return this.proxyMap;
        }

        @NotNull
        public final HashMap<String, Object> getDataMap() {
            return this.dataMap;
        }

        public final boolean getSynced() {
            return this.synced;
        }

        public final boolean isKtData() {
            return this.isKtData;
        }

        @Nullable
        public final Consumer<Map<String, Object>> getOnReload() {
            return this.onReload;
        }

        @NotNull
        public final TypeToken<?> getMapType() {
            Lazy lazy = this.mapType$delegate;
            return (TypeToken)lazy.getValue();
        }

        @NotNull
        public final String serializeToString() {
            String string;
            if (this.isKtData) {
                Json json = INSTANCE.getJSON();
                Type type = this.getMapType().getType();
                Intrinsics.checkNotNullExpressionValue((Object)type, (String)"getType(...)");
                string = json.encodeToString((SerializationStrategy)SerializersKt.serializer((Type)type), this.dataMap);
            } else {
                String string2 = GSON.toJson(this.dataMap);
                string = string2;
                Intrinsics.checkNotNull((Object)string2);
            }
            return string;
        }

        @NotNull
        public final Class<?> component1() {
            return this.type;
        }

        @NotNull
        public final DataMap<T> component2() {
            return this.proxyMap;
        }

        @NotNull
        public final HashMap<String, Object> component3() {
            return this.dataMap;
        }

        public final boolean component4() {
            return this.synced;
        }

        public final boolean component5() {
            return this.isKtData;
        }

        @Nullable
        public final Consumer<Map<String, Object>> component6() {
            return this.onReload;
        }

        @NotNull
        public final GeneralData<T> copy(@NotNull Class<?> type, @NotNull DataMap<T> proxyMap, @NotNull HashMap<String, Object> dataMap, boolean synced, boolean isKtData, @Nullable Consumer<Map<String, Object>> onReload) {
            Intrinsics.checkNotNullParameter(type, (String)"type");
            Intrinsics.checkNotNullParameter(proxyMap, (String)"proxyMap");
            Intrinsics.checkNotNullParameter(dataMap, (String)"dataMap");
            return new GeneralData<T>(type, proxyMap, dataMap, synced, isKtData, onReload);
        }

        public static /* synthetic */ GeneralData copy$default(GeneralData generalData, Class clazz, DataMap dataMap, HashMap hashMap, boolean bl, boolean bl2, Consumer consumer, int n, Object object) {
            if ((n & 1) != 0) {
                clazz = generalData.type;
            }
            if ((n & 2) != 0) {
                dataMap = generalData.proxyMap;
            }
            if ((n & 4) != 0) {
                hashMap = generalData.dataMap;
            }
            if ((n & 8) != 0) {
                bl = generalData.synced;
            }
            if ((n & 0x10) != 0) {
                bl2 = generalData.isKtData;
            }
            if ((n & 0x20) != 0) {
                consumer = generalData.onReload;
            }
            return generalData.copy(clazz, dataMap, hashMap, bl, bl2, consumer);
        }

        @NotNull
        public String toString() {
            return "GeneralData(type=" + this.type + ", proxyMap=" + this.proxyMap + ", dataMap=" + this.dataMap + ", synced=" + this.synced + ", isKtData=" + this.isKtData + ", onReload=" + this.onReload + ")";
        }

        public int hashCode() {
            int result = this.type.hashCode();
            result = result * 31 + this.proxyMap.hashCode();
            result = result * 31 + this.dataMap.hashCode();
            result = result * 31 + Boolean.hashCode(this.synced);
            result = result * 31 + Boolean.hashCode(this.isKtData);
            result = result * 31 + (this.onReload == null ? 0 : this.onReload.hashCode());
            return result;
        }

        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof GeneralData)) {
                return false;
            }
            GeneralData generalData = (GeneralData)other;
            if (!Intrinsics.areEqual(this.type, generalData.type)) {
                return false;
            }
            if (!Intrinsics.areEqual(this.proxyMap, generalData.proxyMap)) {
                return false;
            }
            if (!Intrinsics.areEqual(this.dataMap, generalData.dataMap)) {
                return false;
            }
            if (this.synced != generalData.synced) {
                return false;
            }
            if (this.isKtData != generalData.isKtData) {
                return false;
            }
            return Intrinsics.areEqual(this.onReload, generalData.onReload);
        }

        private static final TypeToken mapType_delegate$lambda$0(GeneralData this$0) {
            Intrinsics.checkNotNullParameter((Object)this$0, (String)"this$0");
            Type[] typeArray = new Type[]{String.class, this$0.type};
            TypeToken typeToken = TypeToken.getParameterized((Type)((Type)((Object)HashMap.class)), (Type[])typeArray);
            Intrinsics.checkNotNull((Object)typeToken);
            return typeToken;
        }
    }
}
