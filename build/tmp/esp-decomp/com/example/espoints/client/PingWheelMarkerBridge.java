/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.api.distmarker.OnlyIn
 *  nx.pingwheel.common.config.ClientConfig
 *  nx.pingwheel.common.core.GameContext
 *  nx.pingwheel.common.core.PingManager
 *  nx.pingwheel.common.core.PingView
 */
package com.example.espoints.client;

import com.example.espoints.tactical.TacticalMarker;
import com.example.espoints.tactical.TacticalMarkerIcons;
import com.example.espoints.tactical.TacticalMarkerType;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import nx.pingwheel.common.config.ClientConfig;
import nx.pingwheel.common.core.GameContext;
import nx.pingwheel.common.core.PingManager;
import nx.pingwheel.common.core.PingView;

@OnlyIn(value=Dist.CLIENT)
public final class PingWheelMarkerBridge {
    private static final Map<UUID, PingView> VIEWS_BY_MARKER = new HashMap<UUID, PingView>();
    private static final Map<PingView, TacticalMarkerType> TYPES_BY_VIEW = new IdentityHashMap<PingView, TacticalMarkerType>();
    private static final ThreadLocal<PingView> CURRENT_RENDER = new ThreadLocal();
    private static int nextSequence = Integer.MIN_VALUE;

    private PingWheelMarkerBridge() {
    }

    public static void replaceSnapshot(List<TacticalMarker> markers) {
        PingWheelMarkerBridge.clear();
        if (markers == null) {
            return;
        }
        for (TacticalMarker marker : markers) {
            PingWheelMarkerBridge.add(marker, false);
        }
    }

    public static void add(TacticalMarker marker, boolean playSound) {
        if (marker == null || marker.id() == null || marker.type() == null) {
            return;
        }
        PingWheelMarkerBridge.remove(marker.id());
        if (!PingWheelMarkerBridge.isWithinPingDistance(marker)) {
            return;
        }
        PingView view = PingView.of((Vec3)new Vec3(marker.x(), marker.y(), marker.z()), null, (UUID)marker.ownerId(), (int)nextSequence++, (int)GameContext.getDimension());
        VIEWS_BY_MARKER.put(marker.id(), view);
        TYPES_BY_VIEW.put(view, marker.type());
        PingManager.addOrReplacePing((PingView)view);
        if (playSound) {
            view.playSoundInDimension();
        }
    }

    public static void refreshVisibility(List<TacticalMarker> markers) {
        if (markers == null || markers.isEmpty()) {
            PingWheelMarkerBridge.clear();
            return;
        }
        HashMap<UUID, TacticalMarker> byId = new HashMap<UUID, TacticalMarker>(markers.size());
        for (TacticalMarker marker : markers) {
            if (marker == null || marker.id() == null) continue;
            byId.put(marker.id(), marker);
        }
        for (UUID id : List.copyOf(VIEWS_BY_MARKER.keySet())) {
            TacticalMarker marker = (TacticalMarker)byId.get(id);
            if (marker != null && PingWheelMarkerBridge.isWithinPingDistance(marker)) continue;
            PingWheelMarkerBridge.remove(id);
        }
        for (TacticalMarker marker : byId.values()) {
            if (VIEWS_BY_MARKER.containsKey(marker.id()) || !PingWheelMarkerBridge.isWithinPingDistance(marker)) continue;
            PingWheelMarkerBridge.add(marker, false);
        }
    }

    public static void remove(UUID markerId) {
        PingView view = VIEWS_BY_MARKER.remove(markerId);
        if (view == null) {
            return;
        }
        TYPES_BY_VIEW.remove(view);
        PingManager.PING_REPO.remove(view);
    }

    public static void clear() {
        if (!VIEWS_BY_MARKER.isEmpty()) {
            PingManager.PING_REPO.removeAll(VIEWS_BY_MARKER.values());
        }
        VIEWS_BY_MARKER.clear();
        TYPES_BY_VIEW.clear();
        CURRENT_RENDER.remove();
    }

    public static boolean isManaged(PingView view) {
        return view != null && TYPES_BY_VIEW.containsKey(view);
    }

    public static void beginRender(PingView view) {
        if (PingWheelMarkerBridge.isManaged(view)) {
            CURRENT_RENDER.set(view);
        } else {
            CURRENT_RENDER.remove();
        }
    }

    public static void endRender() {
        CURRENT_RENDER.remove();
    }

    public static TacticalMarkerType currentType() {
        return TYPES_BY_VIEW.get(CURRENT_RENDER.get());
    }

    public static ResourceLocation currentTexture() {
        return TacticalMarkerIcons.textureFor(PingWheelMarkerBridge.currentType());
    }

    public static int currentTint() {
        TacticalMarkerType type = PingWheelMarkerBridge.currentType();
        if (type == null) {
            return -1;
        }
        return switch (type) {
            case TacticalMarkerType.ATTACK_HERE, TacticalMarkerType.DEFEND_HERE -> type.getColor();
            case TacticalMarkerType.ENEMY_INFANTRY, TacticalMarkerType.ENEMY_TANK, TacticalMarkerType.ENEMY_IFV, TacticalMarkerType.ENEMY_LIGHT_VEHICLE, TacticalMarkerType.ENEMY_HELICOPTER -> -1;
            default -> -2076078;
        };
    }

    private static boolean isWithinPingDistance(TacticalMarker marker) {
        double dz;
        double dy;
        LocalPlayer player = Minecraft.m_91087_().f_91074_;
        if (player == null) {
            return false;
        }
        int configured = ((ClientConfig)ClientConfig.HANDLER.getConfig()).getPingDistance();
        if (configured >= 2048) {
            return true;
        }
        double max = Math.max(0, configured);
        double dx = marker.x() - player.m_20185_();
        return dx * dx + (dy = marker.y() - player.m_20186_()) * dy + (dz = marker.z() - player.m_20189_()) * dz <= max * max;
    }
}

