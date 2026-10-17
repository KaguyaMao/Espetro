package org.espetro.support;

import static org.espetro.support.FireRequestBoard.*;
import static org.espetro.support.FireRequestNetwork.*;

import com.google.gson.*;

import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;

import org.espetro.Espetro;
import org.espetro.api.EspetroAPI;
import org.espetro.mapconfig.BattlefieldContext;
import org.espetro.team.*;

import java.util.*;

@Mod.EventBusSubscriber(modid = "espetro")
public final class FireRequestService {
    private static FireRequestBoard board;
    private static long clock;
    private static boolean wasBattle;
    private static final Map<UUID, Long> viewers = new HashMap<>(),
            lastAction = new HashMap<>(),
            lastView = new HashMap<>();
    private static final String SKILL = "artillery_155";

    public static Actor actor(ServerPlayer p) {
        return new Actor(
                p.getUUID(),
                p.getGameProfile().getName(),
                EspetroAPI.getPlayerTeam(p),
                EspetroAPI.getPlayerSquadId(p.getUUID()),
                EspetroAPI.isSquadLeader(p.getUUID()),
                EspetroAPI.isCommander(p.getUUID()),
                EspetroAPI.isPlayerVisibleOnTacticalMap(p));
    }

    private static FireRequestBoard board() {
        long session = BattlefieldContext.getSessionId();
        if (board == null) {
            board = new FireRequestBoard(FireRequestConfig.rules());
            board.reset(session);
        } else if (board.session() != session) {
            board.reset(session);
            viewers.clear();
            wasBattle = false;
        }
        return board;
    }

    private static boolean battle() {
        return GameStateManager.getInstance().getCurrentPhase() == GamePhase.BATTLE;
    }

    private static String dimension() {
        return BattlefieldContext.getActiveDimensionKey()
                .map(k -> k.location().toString())
                .orElse("");
    }

    private static Bounds bounds() {
        try {
            var snapshot = EspetroAPI.getActiveBattlefieldSnapshot().orElse(null);
            if (snapshot == null) return null;
            JsonObject j = JsonParser.parseString(snapshot.tacticalMapJson()).getAsJsonObject();
            double x = number(j, "topLeftX", -512),
                    z = number(j, "topLeftZ", -512),
                    xx = number(j, "bottomRightX", 512),
                    zz = number(j, "bottomRightZ", 512);
            if (Math.max(Math.max(Math.abs(x), Math.abs(z)), Math.max(Math.abs(xx), Math.abs(zz)))
                    > 29999984) return null;
            return new Bounds(Math.min(x, xx), Math.min(z, zz), Math.max(x, xx), Math.max(z, zz));
        } catch (RuntimeException ex) {
            return null;
        }
    }

    private static double number(JsonObject j, String key, double fallback) {
        return j.has(key) ? j.get(key).getAsDouble() : fallback;
    }

    public static void action(ServerPlayer player, Action action) {
        var b = board();
        UUID id = player.getUUID();
        var limiter = action.type() == Type.VIEW ? lastView : lastAction;
        if (clock - limiter.getOrDefault(id, Long.MIN_VALUE / 2)
                < (action.type() == Type.VIEW ? 20 : 4)) return;
        limiter.put(id, clock);
        viewers.put(id, clock);
        Actor actor = actor(player);
        String message = "";
        try {
            if (action.type() != Type.VIEW) {
                require(action.session() == b.session(), "战局已改变，请重新打开火力面板");
                require(battle(), "只能在战斗阶段处理火力申请");
                switch (action.type()) {
                    case CREATE_POINT, CREATE_LINE -> {
                        var r =
                                b.create(
                                        actor,
                                        dimension(),
                                        bounds(),
                                        action.type() == Type.CREATE_LINE,
                                        action.x(),
                                        action.z(),
                                        action.endX(),
                                        action.endZ(),
                                        action.note(),
                                        clock);
                        Espetro.broadcastToTeam(
                                actor.team(),
                                "§e[火力申请] "
                                        + actor.name()
                                        + " 申请了"
                                        + r.title()
                                        + "，指挥官可按火力支援键查看（默认 U）。");
                    }
                    case REJECT -> b.reject(action.id(), actor, clock);
                    case CANCEL -> b.cancel(action.id(), actor, clock);
                    case APPROVE -> {
                        Request r = b.get(action.id());
                        require(
                                r != null
                                        && actor.member()
                                        && actor.commander()
                                        && actor.team().equals(r.team()),
                                "无权审批该申请");
                        preflight(player, r);
                        require(
                                b.approve(action.id(), actor, clock, rq -> execute(player, rq)),
                                "火力未执行，请检查原有技能冷却或配置");
                        Espetro.broadcastToTeam(
                                actor.team(), "§e[火力支援] 指挥官已下达" + r.title() + "。请避开目标区域。");
                    }
                    default -> {}
                }
                message = "操作已完成";
            }
        } catch (Denied denied) {
            message = denied.getMessage();
        }
        send(player, message);
    }

    private static void preflight(ServerPlayer p, Request r) {
        require(
                battle()
                        && actor(p).deployed()
                        && r.dimension().equals(p.level().dimension().location().toString()),
                "请在本局战场部署后审批");
        Bounds bounds = bounds();
        require(
                bounds != null
                        && bounds.contains(r.x(), r.z())
                        && (!r.line() || bounds.contains(r.endX(), r.endZ())),
                "目标地图已改变");
        require(
                !CommanderSkillManager.getInstance()
                        .hasConflictingPendingTarget(p.getUUID(), SKILL),
                "请先完成或取消其他指挥官技能选点");
    }

    private static boolean execute(ServerPlayer p, Request r) {
        if (!EspetroAPI.getCommanderSkillStatus(p, SKILL).canUse()) return false;
        return r.line()
                ? CommanderSkillManager.getInstance()
                        .submitBarrageTarget(p, r.x(), r.z(), r.endX(), r.endZ())
                : EspetroAPI.submitArtillerySupportTarget(p, r.x(), r.z());
    }

    private static void require(boolean condition, String reason) {
        if (!condition) throw new Denied(reason);
    }

    private static void send(ServerPlayer p, String message) {
        var status = EspetroAPI.getCommanderSkillStatus(p, SKILL);
        var b = board();
        FireRequestNetwork.send(
                p,
                new Snapshot(
                        b.session(),
                        b.revision(),
                        clock,
                        actor(p),
                        battle(),
                        status.getCooldownSeconds(),
                        status.isRegistered() && status.isTargetMap(),
                        bounds(),
                        b.visible(actor(p)),
                        message));
    }

    @SubscribeEvent
    public static void tick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        clock++;
        var server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) return;
        var b = board();
        boolean battle = battle();
        if (wasBattle && !battle) b.closeRound(clock);
        wasBattle = battle;
        if (clock % 20 != 0) return;
        b.maintain(clock);
        for (var it = viewers.entrySet().iterator(); it.hasNext(); ) {
            var v = it.next();
            var p = server.getPlayerList().getPlayer(v.getKey());
            if (p == null || clock - v.getValue() > 120) {
                it.remove();
                continue;
            }
            send(p, "");
        }
    }

    @SubscribeEvent
    public static void commands(RegisterCommandsEvent event) {
        event.getDispatcher()
                .register(
                        Commands.literal("esfire")
                                .executes(
                                        c -> {
                                            var p = c.getSource().getPlayerOrException();
                                            viewers.put(p.getUUID(), clock);
                                            send(p, "OPEN");
                                            return 1;
                                        }));
    }

    @SubscribeEvent
    public static void logout(PlayerEvent.PlayerLoggedOutEvent event) {
        UUID id = event.getEntity().getUUID();
        viewers.remove(id);
        lastAction.remove(id);
        lastView.remove(id);
    }

    @SubscribeEvent
    public static void stop(ServerStoppedEvent event) {
        board = null;
        clock = 0;
        wasBattle = false;
        viewers.clear();
        lastAction.clear();
        lastView.clear();
    }
}
