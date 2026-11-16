package org.espetro.network;

import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.espetro.Espetro;
import org.espetro.team.TeamManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 * 己方载具名单对账器（服务端）。
 *
 * <p>客户端拿不到实体标签，准心提示需要知道“这辆车是不是我方的”。
 * 每 0.5 秒扫描一次玩家周围 48 格内的载具，按 {@code espetro_team_*} 标签过滤出
 * 己方载具，只有名单变化时才下发 {@link FriendlyVehiclesPacket}。</p>
 */
@Mod.EventBusSubscriber(modid = Espetro.MOD_ID)
public final class FriendlyVehicleSync {

    private static final int INTERVAL_TICKS = 10;
    private static final double SCAN_RANGE = 48.0D;
    private static final String TEAM_TAG_PREFIX = "espetro_team_";

    private static final Map<UUID, List<UUID>> LAST_SENT = new HashMap<>();
    private static int cooldown;

    private FriendlyVehicleSync() {
    }

    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() != null) {
            LAST_SENT.remove(event.getEntity().getUUID());
        }
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        if (++cooldown < INTERVAL_TICKS) {
            return;
        }
        cooldown = 0;
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) {
            return;
        }
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            List<UUID> friendly = collect(server, player);
            if (friendly.equals(LAST_SENT.get(player.getUUID()))) {
                continue;
            }
            LAST_SENT.put(player.getUUID(), friendly);
            NetworkManager.NET.send(PacketDistributor.PLAYER.with(() -> player),
                new FriendlyVehiclesPacket(friendly));
        }
    }

    private static List<UUID> collect(MinecraftServer server, ServerPlayer player) {
        String name = player.getGameProfile().getName();
        String team;
        if (TeamManager.isInAttackTeam(server, name)) {
            team = "ATTACK";
        } else if (TeamManager.isInDefendTeam(server, name)) {
            team = "DEFEND";
        } else {
            return List.of();
        }
        List<UUID> ids = new ArrayList<>();
        for (VehicleEntity vehicle : player.level().getEntitiesOfClass(VehicleEntity.class,
                player.getBoundingBox().inflate(SCAN_RANGE))) {
            if (vehicle.isAlive() && hasTeamTag(vehicle, team)) {
                ids.add(vehicle.getUUID());
            }
        }
        ids.sort((a, b) -> Long.compare(a.getMostSignificantBits(), b.getMostSignificantBits()) != 0
            ? Long.compare(a.getMostSignificantBits(), b.getMostSignificantBits())
            : Long.compare(a.getLeastSignificantBits(), b.getLeastSignificantBits()));
        return List.copyOf(ids);
    }

    private static boolean hasTeamTag(Entity entity, String team) {
        String expected = TEAM_TAG_PREFIX + team.trim().toUpperCase(Locale.ROOT);
        for (String tag : entity.getTags()) {
            if (tag.equals(expected)) {
                return true;
            }
        }
        return false;
    }
}
