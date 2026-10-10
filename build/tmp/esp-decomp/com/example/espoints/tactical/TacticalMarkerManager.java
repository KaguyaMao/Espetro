/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.ClipContext
 *  net.minecraft.world.level.ClipContext$Block
 *  net.minecraft.world.level.ClipContext$Fluid
 *  net.minecraft.world.level.levelgen.Heightmap$Types
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult$Type
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.network.PacketDistributor
 *  org.espetro.api.EspetroAPI
 */
package com.example.espoints.tactical;

import com.example.espoints.ESPointsMod;
import com.example.espoints.config.TacticalMapJsonConfig;
import com.example.espoints.network.NetworkHandler;
import com.example.espoints.network.SyncTacticalMarkersMessage;
import com.example.espoints.network.TacticalMarkerDeltaMessage;
import com.example.espoints.tactical.TacticalMarker;
import com.example.espoints.tactical.TacticalMarkerType;
import com.example.espoints.util.EspetroTeamBridge;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import org.espetro.api.EspetroAPI;

public final class TacticalMarkerManager {
    private static final int MAX_MARKERS_PER_TEAM = 64;
    private static final long PLACE_COOLDOWN_MS = 400L;
    private static final double MAX_PLACE_DISTANCE = 256.0;
    private static final Map<String, List<TacticalMarker>> MARKERS_BY_TEAM = new HashMap<String, List<TacticalMarker>>();
    private static final Map<UUID, Long> LAST_PLACE_MS = new ConcurrentHashMap<UUID, Long>();
    private static int cleanupTickCounter;

    private TacticalMarkerManager() {
    }

    public static void place(ServerPlayer player, TacticalMarkerType type, double x, double z) {
        double y;
        double d = y = player != null ? player.m_20186_() : 64.0;
        if (player != null && player.m_284548_() != null) {
            int hx = (int)Math.floor(x);
            int hz = (int)Math.floor(z);
            BlockPos probe = new BlockPos(hx, player.m_284548_().m_141937_(), hz);
            if (player.m_284548_().m_46805_(probe)) {
                int ground = player.m_284548_().m_6924_(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, hx, hz);
                y = (double)ground + 1.15;
            }
        }
        TacticalMarkerManager.place(player, type, x, y, z);
    }

    public static void place(ServerPlayer player, TacticalMarkerType type, double x, double y, double z) {
        TacticalMarkerManager.placeInternal(player, type, x, y, z, false);
    }

    public static void placeFromView(ServerPlayer player, TacticalMarkerType type, double x, double y, double z) {
        TacticalMarkerManager.placeInternal(player, type, x, y, z, true);
    }

    private static void placeInternal(ServerPlayer player, TacticalMarkerType type, double x, double y, double z, boolean validateView) {
        String team;
        if (!(type != null && Double.isFinite(x) && Double.isFinite(y) && Double.isFinite(z))) {
            return;
        }
        if (player == null) {
            return;
        }
        if (!EspetroAPI.isActiveBattlefield((ServerLevel)player.m_284548_())) {
            player.m_213846_((Component)Component.m_237113_((String)"\u00a7c\u8bf7\u8fdb\u5165\u5f53\u524d\u6218\u573a\u540e\u518d\u653e\u7f6e\u6807\u70b9\u3002"));
            return;
        }
        if (!EspetroTeamBridge.canPlaceTacticalMarker(player)) {
            player.m_213846_((Component)Component.m_237113_((String)"\u00a7c\u53ea\u6709\u6307\u6325\u5b98\u3001\u5c0f\u961f\u957f\u3001\u706b\u529b\u7ec4\u7ec4\u957f\u6216\u5408\u6cd5\u8f7d\u5177\u5ea7\u4f4d\u53ef\u4ee5\u653e\u7f6e\u6218\u672f\u6807\u70b9\u3002"));
            return;
        }
        long now = System.currentTimeMillis();
        Long last = LAST_PLACE_MS.get(player.m_20148_());
        if (last != null && now - last < 400L) {
            return;
        }
        if (validateView) {
            double dz;
            double dy;
            double dx = x - player.m_20185_();
            if (dx * dx + (dy = y - player.m_20186_()) * dy + (dz = z - player.m_20189_()) * dz > 65536.0) {
                player.m_213846_((Component)Component.m_237113_((String)"\u00a7c\u6807\u70b9\u8ddd\u79bb\u8fc7\u8fdc\u3002"));
                return;
            }
            if (!TacticalMarkerManager.isReasonableAim(player, x, y, z)) {
                player.m_213846_((Component)Component.m_237113_((String)"\u00a7c\u6807\u70b9\u4f4d\u7f6e\u4e0e\u5f53\u524d\u89c6\u7ebf\u4e0d\u7b26\u3002"));
                return;
            }
        }
        if ((team = EspetroTeamBridge.getServerPlayerTeam(player)) == null) {
            return;
        }
        TacticalMapJsonConfig.TacticalMapBounds bounds = TacticalMapJsonConfig.getInstance().getBounds();
        if (!bounds.contains(x, z)) {
            player.m_213846_((Component)Component.m_237113_((String)"\u00a7c\u6807\u70b9\u4f4d\u7f6e\u8d85\u51fa\u6218\u672f\u5730\u56fe\u5141\u8bb8\u8303\u56f4\u3002"));
            return;
        }
        LAST_PLACE_MS.put(player.m_20148_(), now);
        List markers = MARKERS_BY_TEAM.computeIfAbsent(team, ignored -> new ArrayList());
        ArrayList<UUID> removed = new ArrayList<UUID>(1);
        while (markers.size() >= 64) {
            removed.add(((TacticalMarker)markers.remove(0)).id());
        }
        TacticalMarker marker = new TacticalMarker(UUID.randomUUID(), type, x, y, z, team, player.m_20148_(), player.m_7755_().getString(), now, EspetroTeamBridge.getPlayerSquadId(player), EspetroTeamBridge.isCommander(player));
        markers.add(marker);
        TacticalMarkerManager.sendDeltaToTeam(team, TacticalMarkerDeltaMessage.add(List.of(marker), removed));
        player.m_240418_((Component)Component.m_237113_((String)("\u00a7a\u5df2\u6807\u8bb0\uff1a" + type.getDisplayName())), true);
    }

    public static void removeOwn(ServerPlayer player, UUID markerId) {
        if (markerId == null || player == null) {
            return;
        }
        String team = EspetroTeamBridge.getServerPlayerTeam(player);
        if (team == null) {
            return;
        }
        List<TacticalMarker> markers = MARKERS_BY_TEAM.get(team);
        if (markers == null) {
            return;
        }
        TacticalMarker owned = markers.stream().filter(marker -> marker.id().equals(markerId) && marker.ownerId().equals(player.m_20148_())).findFirst().orElse(null);
        if (owned != null && markers.remove(owned)) {
            if (markers.isEmpty()) {
                MARKERS_BY_TEAM.remove(team);
            }
            TacticalMarkerManager.sendDeltaToTeam(team, TacticalMarkerDeltaMessage.add(List.of(), List.of(markerId)));
        }
    }

    public static void sendTo(ServerPlayer player) {
        String team = EspetroTeamBridge.getServerPlayerTeam(player);
        List<TacticalMarker> markers = team == null || !EspetroAPI.isActiveBattlefield((ServerLevel)player.m_284548_()) ? List.of() : MARKERS_BY_TEAM.getOrDefault(team, List.of());
        NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), (Object)new SyncTacticalMarkersMessage(markers));
    }

    private static void sendDeltaToTeam(String team, TacticalMarkerDeltaMessage message) {
        MinecraftServer server = ESPointsMod.getServer();
        if (server == null || team == null || message == null) {
            return;
        }
        for (ServerPlayer player : server.m_6846_().m_11314_()) {
            if (!EspetroAPI.isActiveBattlefield((ServerLevel)player.m_284548_()) || !EspetroTeamBridge.isSameTeam(team, EspetroTeamBridge.getServerPlayerTeam(player))) continue;
            NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), (Object)message);
        }
    }

    public static void tick() {
        if (MARKERS_BY_TEAM.isEmpty()) {
            return;
        }
        if (++cleanupTickCounter < 20) {
            return;
        }
        cleanupTickCounter = 0;
        Map<String, List<UUID>> removals = TacticalMarkerManager.removeExpiredMarkers();
        for (Map.Entry<String, List<UUID>> entry : removals.entrySet()) {
            TacticalMarkerManager.sendDeltaToTeam(entry.getKey(), TacticalMarkerDeltaMessage.add(List.of(), entry.getValue()));
        }
    }

    private static Map<String, List<UUID>> removeExpiredMarkers() {
        long lifetime = TacticalMapJsonConfig.getInstance().getTacticalMarkerDurationMillis();
        long cutoff = System.currentTimeMillis() - lifetime;
        HashMap<String, List<UUID>> removedByTeam = new HashMap<String, List<UUID>>();
        Iterator<Map.Entry<String, List<TacticalMarker>>> iterator = MARKERS_BY_TEAM.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, List<TacticalMarker>> entry = iterator.next();
            Iterator<TacticalMarker> markers = entry.getValue().iterator();
            while (markers.hasNext()) {
                TacticalMarker marker = markers.next();
                if (marker.type().isPersistentUntilRemoved() || marker.createdAtMillis() > cutoff) continue;
                removedByTeam.computeIfAbsent(entry.getKey(), ignored -> new ArrayList()).add(marker.id());
                markers.remove();
            }
            if (!entry.getValue().isEmpty()) continue;
            iterator.remove();
        }
        return removedByTeam;
    }

    public static void reset() {
        boolean hadState = !MARKERS_BY_TEAM.isEmpty() || !LAST_PLACE_MS.isEmpty();
        MARKERS_BY_TEAM.clear();
        LAST_PLACE_MS.clear();
        cleanupTickCounter = 0;
        MinecraftServer server = ESPointsMod.getServer();
        if (server == null || !hadState) {
            return;
        }
        TacticalMarkerDeltaMessage empty = TacticalMarkerDeltaMessage.clearAll();
        for (ServerPlayer player : server.m_6846_().m_11314_()) {
            NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), (Object)empty);
        }
    }

    public static void clearPlayerCooldown(UUID id) {
        if (id != null) {
            LAST_PLACE_MS.remove(id);
        }
    }

    private static boolean isReasonableAim(ServerPlayer player, double x, double y, double z) {
        Vec3 target = new Vec3(x, y, z);
        Vec3 eye = player.m_146892_();
        Vec3 delta = target.m_82546_(eye);
        double distance = delta.m_82553_();
        if (distance < 0.25 || distance > 256.0) {
            return false;
        }
        Vec3 direction = delta.m_82490_(1.0 / distance);
        if (player.m_20154_().m_82526_(direction) < 0.95) {
            return false;
        }
        BlockHitResult hit = player.m_284548_().m_45547_(new ClipContext(eye, target.m_82549_(direction.m_82490_(0.5)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, (Entity)player));
        return hit.m_6662_() == HitResult.Type.MISS || hit.m_82450_().m_82557_(target) <= 9.0;
    }
}

