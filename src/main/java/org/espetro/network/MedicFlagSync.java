package org.espetro.network;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.espetro.Espetro;
import org.espetro.api.EspetroAPI;
import org.espetro.team.FactionDataProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 医疗兵标记的对账器（服务端）。
 *
 * <p>{@link NetworkManager#sendMedicFlag(ServerPlayer)} 只在登录/换职业等事件里推送，
 * 一旦这些事件的先后顺序与职业恢复顺序错位（例如登录时职业还没恢复），客户端会一直拿着
 * “不是医疗兵”的旧标记，导致医疗兵看不到头顶/地图伤员标识。这里每 2 秒对账一次：
 * 服务端真实状态与最后一次推送不一致时补发，保证最终一致。</p>
 */
@Mod.EventBusSubscriber(modid = Espetro.MOD_ID)
public final class MedicFlagSync {

    /** 每 40 tick 对账一次（2 秒），开销可忽略 */
    private static final int CHECK_INTERVAL_TICKS = 40;

    /** 最后一次推送给某玩家的医疗兵状态 */
    private static final Map<UUID, Boolean> LAST_SENT = new HashMap<>();
    private static int cooldown = 0;

    private MedicFlagSync() {
    }

    /** 记录一次已推送的状态（由 NetworkManager.sendMedicFlag 调用） */
    public static void note(UUID playerId, boolean medic) {
        LAST_SENT.put(playerId, medic);
    }

    /** 返回指定玩家是否被确实地标记为“非医疗兵”（用于诊断日志） */
    static boolean lastSent(UUID playerId) {
        return Boolean.TRUE.equals(LAST_SENT.get(playerId));
    }

    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() != null) LAST_SENT.remove(event.getEntity().getUUID());
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (++cooldown < CHECK_INTERVAL_TICKS) return;
        cooldown = 0;
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) return;
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            boolean medic = FactionDataProvider.getOrCreateLoader()
                    .isMedicClass(EspetroAPI.getPlayerClass(player.getUUID()));
            if (!Boolean.valueOf(medic).equals(LAST_SENT.get(player.getUUID()))) {
                NetworkManager.sendMedicFlag(player);
            }
        }
    }
}
