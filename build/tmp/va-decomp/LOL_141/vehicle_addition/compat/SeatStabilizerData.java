/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.api.event.LoadingDataEvent
 *  com.atsuishio.superbwarfare.api.event.LoadingDataEvent$Vehicle
 *  com.atsuishio.superbwarfare.api.event.LoadingJsonEvent
 *  com.atsuishio.superbwarfare.data.vehicle.DefaultVehicleData
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  net.minecraft.resources.FileToIdConverter
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.packs.resources.PreparableReloadListener
 *  net.minecraft.server.packs.resources.Resource
 *  net.minecraft.server.packs.resources.ResourceManager
 *  net.minecraft.server.packs.resources.SimplePreparableReloadListener
 *  net.minecraft.util.profiling.ProfilerFiller
 *  net.minecraft.world.entity.Entity
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.client.event.RegisterClientReloadListenersEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 *  net.minecraftforge.registries.ForgeRegistries
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package LOL_141.vehicle_addition.compat;

import com.atsuishio.superbwarfare.api.event.LoadingDataEvent;
import com.atsuishio.superbwarfare.api.event.LoadingJsonEvent;
import com.atsuishio.superbwarfare.data.vehicle.DefaultVehicleData;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.Reader;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SeatStabilizerData {
    private static final Logger LOGGER = LoggerFactory.getLogger(SeatStabilizerData.class);
    private static final Map<String, Map<Integer, Boolean>> CACHE = new ConcurrentHashMap<String, Map<Integer, Boolean>>();
    private static Field stabilizerField;

    private SeatStabilizerData() {
    }

    public static boolean isStabilized(Entity vehicle, int seatIndex) {
        Boolean b;
        if (vehicle == null) {
            return true;
        }
        ResourceLocation key = ForgeRegistries.ENTITY_TYPES.getKey((Object)vehicle.m_6095_());
        if (key == null) {
            return true;
        }
        Map<Integer, Boolean> m = CACHE.get(key.toString());
        if (m != null && (b = m.get(seatIndex)) != null) {
            return b;
        }
        return true;
    }

    public static boolean isStabilized(Entity vehicle, int seatIndex, Object seat) {
        if (vehicle == null) {
            return true;
        }
        ResourceLocation key = ForgeRegistries.ENTITY_TYPES.getKey((Object)vehicle.m_6095_());
        if (key == null) {
            return true;
        }
        Map<Integer, Boolean> m = CACHE.get(key.toString());
        if (m != null && m.containsKey(seatIndex)) {
            return Boolean.TRUE.equals(m.get(seatIndex));
        }
        Boolean fieldValue = SeatStabilizerData.readField(seat);
        return fieldValue == null ? true : fieldValue;
    }

    private static Boolean readField(Object seat) {
        if (seat == null) {
            return null;
        }
        try {
            if (stabilizerField == null) {
                stabilizerField = seat.getClass().getField("stabilizer");
            }
            return stabilizerField.getBoolean(seat);
        }
        catch (Throwable t) {
            return null;
        }
    }

    @SubscribeEvent
    public static void onLoadingJson(LoadingJsonEvent event) {
        try {
            String id = event.getId();
            if (id == null) {
                return;
            }
            JsonObject root = event.getAsGsonObject();
            if (root == null || !root.has("Seats")) {
                return;
            }
            Map<Integer, Boolean> map = SeatStabilizerData.extractStabilizers(root.get("Seats"));
            if (!map.isEmpty()) {
                CACHE.put(id, map);
            }
        }
        catch (Throwable t) {
            LOGGER.warn("[Stabilizer] LoadingJsonEvent parse failed: {}", (Object)t.toString());
        }
    }

    @SubscribeEvent
    public static void onLoadingData(LoadingDataEvent event) {
        if (!(event instanceof LoadingDataEvent.Vehicle)) {
            return;
        }
        LoadingDataEvent.Vehicle veh = (LoadingDataEvent.Vehicle)event;
        try {
            Map<Integer, Boolean> m;
            String id = veh.getId();
            Map<Integer, Boolean> map = m = id == null ? null : CACHE.get(id);
            if (m == null || m.isEmpty()) {
                return;
            }
            List seats = ((DefaultVehicleData)veh.getData()).seats();
            for (int i = 0; i < seats.size(); ++i) {
                Boolean v = m.get(i);
                if (v == null) continue;
                Object seat = seats.get(i);
                if (stabilizerField == null) {
                    stabilizerField = seat.getClass().getField("stabilizer");
                }
                stabilizerField.setBoolean(seat, v);
            }
        }
        catch (Throwable t) {
            LOGGER.warn("[Stabilizer] LoadingDataEvent fill failed: {}", (Object)t.toString());
        }
    }

    private static Map<Integer, Boolean> extractStabilizers(JsonElement seats) {
        JsonObject o;
        HashMap<Integer, Boolean> map = new HashMap<Integer, Boolean>();
        if (seats == null || seats.isJsonNull()) {
            return map;
        }
        if (seats.isJsonArray()) {
            JsonArray arr = seats.getAsJsonArray();
            for (int i = 0; i < arr.size(); ++i) {
                JsonObject o2;
                JsonElement el = arr.get(i);
                if (!el.isJsonObject() || !(o2 = el.getAsJsonObject()).has("Stabilizer")) continue;
                map.put(i, o2.get("Stabilizer").getAsBoolean());
            }
        } else if (seats.isJsonObject() && (o = seats.getAsJsonObject()).has("Stabilizer")) {
            map.put(0, o.get("Stabilizer").getAsBoolean());
        }
        return map;
    }

    private static void parseIntoCache(String id, String jsonStr) {
        try {
            JsonObject root = JsonParser.parseString((String)jsonStr).getAsJsonObject();
            if (root == null || !root.has("Seats")) {
                return;
            }
            Map<Integer, Boolean> map = SeatStabilizerData.extractStabilizers(root.get("Seats"));
            if (!map.isEmpty()) {
                CACHE.put(id, map);
            }
        }
        catch (Throwable t) {
            LOGGER.warn("[Stabilizer] datapack parse failed for {}: {}", (Object)id, (Object)t.toString());
        }
    }

    @Mod.EventBusSubscriber(modid="vehicle_addition", bus=Mod.EventBusSubscriber.Bus.MOD, value={Dist.CLIENT})
    public static final class ClientReloadEvents {
        @SubscribeEvent
        public static void onRegisterClientReloadListeners(RegisterClientReloadListenersEvent event) {
            event.registerReloadListener((PreparableReloadListener)new ClientSeatDataReloadListener());
        }
    }

    public static final class ClientSeatDataReloadListener
    extends SimplePreparableReloadListener<Void> {
        protected Void prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
            FileToIdConverter converter = FileToIdConverter.m_246568_((String)"sbw/vehicles");
            for (Map.Entry entry : converter.m_247457_(resourceManager).entrySet()) {
                ResourceLocation file = (ResourceLocation)entry.getKey();
                String id = converter.m_245273_(file).toString();
                try {
                    BufferedReader reader = ((Resource)entry.getValue()).m_215508_();
                    try {
                        String jsonStr = new BufferedReader(reader).lines().collect(Collectors.joining("\n"));
                        SeatStabilizerData.parseIntoCache(id, jsonStr);
                    }
                    finally {
                        if (reader == null) continue;
                        ((Reader)reader).close();
                    }
                }
                catch (Exception e) {
                    LOGGER.warn("[Stabilizer] client reload failed for {}: {}", (Object)id, (Object)e.toString());
                }
            }
            return null;
        }

        protected void apply(Void data, ResourceManager resourceManager, ProfilerFiller profiler) {
        }
    }
}

