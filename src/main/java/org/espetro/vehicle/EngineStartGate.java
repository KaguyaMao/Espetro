package org.espetro.vehicle;

import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import org.espetro.Espetro;
import org.espetro.team.GamePhase;
import org.espetro.team.GameStateManager;
import org.espetro.team.TeamManager;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 部署阶段禁止发动载具——**只锁进攻方**，防守方在部署阶段可以正常发动（提前布置防空/装填）。
 *
 * <p>队伍判定走计分板（{@code TeamManager.isInAttackTeam}），未选队的玩家不受限；
 * 一旦进入战斗阶段（非 {@link GamePhase#DEPLOYING}）整段逻辑不生效。</p>
 *
 * <p>dragonrise_reforge 与 FCP 各自带一套同名的“引擎启停”状态机
 * （{@code ...entity.vehicle.engine.EngineControl}，API 完全一致：{@code state}/{@code stop}），
 * 但 Espetro 不依赖它们，因此这里按类名反射调用；两套都没安装时就是空表，行为退化为只压
 * SBW 自己的启停开关。</p>
 *
 * <p>每个检查周期把在场载具的引擎强制熄火：SBW 的 {@code engineStart}/{@code engineStartOver}
 * 一律置 false（FCP 与其它走原版启动流程的载具靠这两个 flag），两套模组的引擎状态机则调用
 * {@code stop(vehicle)}。只要状态被点亮过一次，就判定玩家尝试发动，给小标题提示。</p>
 */
@Mod.EventBusSubscriber(modid = Espetro.MOD_ID)
public final class EngineStartGate {

    /** 检查周期（tick）。4 tick 足以在 STARTING 阶段（各车 16~56 tick）内把引擎按回去。 */
    private static final int CHECK_INTERVAL_TICKS = 4;

    /** 同一条提示的最小间隔（tick），免得按住键时每 4 tick 刷一次。 */
    private static final int MESSAGE_COOLDOWN_TICKS = 60;

    private static final Component BLOCKED_MESSAGE = Component.literal("§c部署阶段无法发动载具");

    /** 已知的引擎状态机类名（装哪个就反射哪个）。 */
    private static final String[] ENGINE_CLASS_NAMES = {
            "com.redabysslucia.dragonrise_reforge.entity.vehicle.engine.EngineControl",
            "frontline.combat.fcp.entity.vehicle.engine.EngineControl"
    };

    private static final List<EngineBridge> BRIDGES = EngineBridge.resolveAll();
    private static final Map<UUID, Long> LAST_MESSAGE_TICK = new HashMap<>();

    private static int tickCounter;

    private EngineStartGate() {
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        if (++tickCounter % CHECK_INTERVAL_TICKS != 0) {
            return;
        }
        if (GameStateManager.getInstance().getCurrentPhase() != GamePhase.DEPLOYING) {
            if (!LAST_MESSAGE_TICK.isEmpty()) {
                LAST_MESSAGE_TICK.clear();
            }
            return;
        }
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) {
            return;
        }
        long now = server.getTickCount();
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            // 只有进攻方在部署阶段被锁；防守方本来就该能提前把车打着准备防区。
            if (!TeamManager.isInAttackTeam(server, player.getGameProfile().getName())) {
                continue;
            }
            if (!(player.getRootVehicle() instanceof VehicleEntity vehicle)) {
                continue;
            }
            if (blockEngine(vehicle)) {
                notifyBlocked(player, now);
            }
        }
    }

    /** @return true 当这辆车的引擎本轮被判定为“曾经想发动” */
    private static boolean blockEngine(VehicleEntity vehicle) {
        boolean attempted = false;

        // SBW 自己的启停开关：FCP 以及没有自带状态机的载具都靠这两个 flag 决定引擎是否运转。
        if (vehicle.getEngineStart()) {
            vehicle.setEngineStart(false);
            attempted = true;
        }
        if (vehicle.getEngineStartOver()) {
            vehicle.setEngineStartOver(false);
            attempted = true;
        }

        // dragonrise / FCP 的引擎状态机：OFF=0、STARTING=1、RUNNING=2。
        for (EngineBridge bridge : BRIDGES) {
            if (bridge.state(vehicle) != 0) {
                bridge.stop(vehicle);
                attempted = true;
            }
        }
        return attempted;
    }

    private static void notifyBlocked(ServerPlayer player, long now) {
        Long last = LAST_MESSAGE_TICK.get(player.getUUID());
        if (last != null && now - last < MESSAGE_COOLDOWN_TICKS) {
            return;
        }
        LAST_MESSAGE_TICK.put(player.getUUID(), now);
        player.displayClientMessage(BLOCKED_MESSAGE, true);
    }

    /** 对某一个模组引擎状态机的反射封装；解析失败时整条桥被丢弃。 */
    private static final class EngineBridge {

        private final String className;
        private final Method stateMethod;
        private final Method stopMethod;
        private final Method appliesMethod;

        private EngineBridge(String className, Method stateMethod, Method stopMethod, Method appliesMethod) {
            this.className = className;
            this.stateMethod = stateMethod;
            this.stopMethod = stopMethod;
            this.appliesMethod = appliesMethod;
        }

        static List<EngineBridge> resolveAll() {
            List<EngineBridge> bridges = new ArrayList<>();
            for (String className : ENGINE_CLASS_NAMES) {
                try {
                    Class<?> type = Class.forName(className);
                    Method state = type.getMethod("state", VehicleEntity.class);
                    Method stop = type.getMethod("stop", VehicleEntity.class);
                    Method applies;
                    try {
                        applies = type.getMethod("appliesTo", VehicleEntity.class);
                    } catch (NoSuchMethodException ignored) {
                        applies = null;
                    }
                    bridges.add(new EngineBridge(className, state, stop, applies));
                } catch (ClassNotFoundException ignored) {
                    // 该模组没装，正常情况。
                } catch (NoSuchMethodException error) {
                    Espetro.LOGGER.warn("[部署引擎锁] {} 的引擎 API 不匹配，已跳过", className, error);
                }
            }
            return List.copyOf(bridges);
        }

        /** @return 0=熄火（或这套状态机不管这辆车），非 0 表示引擎已点亮 */
        int state(VehicleEntity vehicle) {
            try {
                if (appliesMethod != null && !(Boolean) appliesMethod.invoke(null, vehicle)) {
                    return 0;
                }
                Object state = stateMethod.invoke(null, vehicle);
                return state instanceof Byte value ? value : 0;
            } catch (ReflectiveOperationException | RuntimeException ignored) {
                return 0;
            }
        }

        void stop(VehicleEntity vehicle) {
            try {
                stopMethod.invoke(null, vehicle);
            } catch (ReflectiveOperationException | RuntimeException error) {
                Espetro.LOGGER.debug("[部署引擎锁] {} 熄火失败", className, error);
            }
        }
    }
}
