// patch-spectator.mjs — 观战模式改造：主城设置跨局保持 + 投票阶段只显示等待 + 中途加入面板进入观战 + 玩家名补全
import fs from 'node:fs';

function patch(file, edits, { requireCount = 1 } = {}) {
  if (!fs.existsSync(file)) { console.error('  ❌ 文件不存在 ' + file); process.exitCode = 1; return false; }
  let text = fs.readFileSync(file, 'utf8');
  const eol = text.includes('\r\n') ? '\r\n' : '\n';
  for (const [from, to, label, expect] of edits) {
    const f = from.replace(/\r?\n/g, eol);
    const n = text.split(f).length - 1;
    const want = expect ?? requireCount;
    if (n !== want) { console.error(`  ❌ ${file.split('/').pop()} [${label}] 匹配 ${n} 次（期望 ${want}）`); process.exitCode = 1; return false; }
    text = text.split(f).join(to.replace(/\r?\n/g, eol));
    console.log(`  ✓ ${file.split('/').pop()} [${label}]`);
  }
  fs.writeFileSync(file, text, 'utf8');
  return true;
}

const CMD = 'src/main/java/org/espetro/command/EspetroCommand.java';
const GSM = 'src/main/java/org/espetro/team/GameStateManager.java';
const NM = 'src/main/java/org/espetro/network/NetworkManager.java';
const CPH = 'src/main/java/org/espetro/client/ClientPacketHandlers.java';
const TSS = 'src/main/java/org/espetro/client/gui/TeamSelectionScreen.java';

// ============ A. 玩家名补全（4 处 player 参数）============
console.log('== A. /espetro 命令玩家名补全 ==');
patch(CMD, [
  ['Commands.argument("player", StringArgumentType.string())',
   `Commands.argument("player", StringArgumentType.string())
                    .suggests((ctx, builder) -> net.minecraft.commands.SharedSuggestionProvider
                        .suggest(ctx.getSource().getServer().getPlayerNames(), builder))`,
   'player-suggests', 4],
]);

// ============ B. GameStateManager ============
console.log('== B. GameStateManager：跨局观战 + 投票阶段提示 + 排除自动分配 ==');
patch(GSM, [
  // B1 字段
  [`    /** 管理员设为观察者的玩家（局内观战）：本局结束（beginCleanup/forceStop/reset）时自动清除恢复。 */
    private final Set<UUID> observers = new HashSet<>();`,
   `    /** 管理员设为观察者的玩家（局内观战）：本局结束（beginCleanup/forceStop/reset）时自动清除恢复。 */
    private final Set<UUID> observers = new HashSet<>();
    /**
     * 主城等待期间被设为观察者的玩家：<b>跨局保持</b>，下一局继续观战，
     * 直到管理员用跳边指令把他编入队伍。与 {@link #observers} 的区别只有生命周期。
     */
    private final Set<UUID> persistentObservers = new HashSet<>();
    /** 投票阶段给观战者刷新"请等待"提示的节流计数。 */
    private int votingNoticeTicker = 0;`,
   'fields'],
  // B2 isObserver
  [`    public boolean isObserver(UUID playerId) {
        return playerId != null && observers.contains(playerId);
    }`,
   `    public boolean isObserver(UUID playerId) {
        return playerId != null
            && (observers.contains(playerId) || persistentObservers.contains(playerId));
    }`,
   'isObserver'],
  // B3 adminSetObserver：按阶段决定生命周期 + 自助入口
  [`    public boolean adminSetObserver(ServerPlayer player) {
        if (player == null) return false;
        UUID id = player.getUUID();
        observers.add(id);`,
   `    public boolean adminSetObserver(ServerPlayer player) {
        return setObserver(player, false);
    }

    /** 玩家自己通过面板按钮进入观战（局内有效，本局结束自动恢复）。 */
    public boolean selfSetObserver(ServerPlayer player) {
        return setObserver(player, true);
    }

    private boolean setObserver(ServerPlayer player, boolean selfRequest) {
        if (player == null) return false;
        UUID id = player.getUUID();
        // 主城等待期间设置 → 跨局保持（下一局继续观战）；局内设置 → 本局结束自动恢复。
        boolean lobbyLike = currentPhase == null || currentPhase.isLobbyLike();
        if (lobbyLike) {
            persistentObservers.add(id);
            observers.remove(id);
        } else {
            observers.add(id);
            persistentObservers.remove(id);
        }`,
   'setObserverHead'],
  // B4 观战提示语
  [`        player.sendSystemMessage(Component.literal(
            "§e你已被管理员设为观察者，本局结束后将自动恢复为正常玩家。"));
        return true;`,
   `        if (selfRequest) {
            player.sendSystemMessage(Component.literal("§e你已进入观战模式。"));
        } else if (lobbyLike) {
            player.sendSystemMessage(Component.literal(
                "§e你已被设为观察者：本局与下一局都将保持观战，直到被跳边编入队伍。"));
        } else {
            player.sendSystemMessage(Component.literal(
                "§e你已被管理员设为观察者，本局结束后将自动恢复为正常玩家。"));
        }
        return true;`,
   'observerMessage'],
  // B5 跳边时清掉两种观战标记
  [`        if (observers.remove(id)) {`,
   `        if (observers.remove(id) | persistentObservers.remove(id)) {`,
   'changeTeamRemove'],
  // B6 clearAllObservers 保留跨局观战
  [`    public void clearAllObservers() {
        observers.clear();`,
   `    public void clearAllObservers() {
        // 只清"局内观战"；主城设置的跨局观战者保持旁观，下一局继续观战。
        observers.clear();`,
   'clearAllObservers'],
  // B7 中途加入判断改用 isObserver
  [`        if (observers.contains(player.getUUID())) {`,
   `        if (isObserver(player.getUUID())) {`,
   'midJoinObserverCheck'],
  // B8 自动分配跳过观战者
  [`        for (ServerPlayer player : allPlayers) {
            String team = assignments.get(player.getUUID());
            if (team == null) team = "ATTACK";`,
   `        for (ServerPlayer player : allPlayers) {
            // 观战者（含主城设置的跨局观战）不参与自动分配，继续保持旁观。
            if (isObserver(player.getUUID())) {
                continue;
            }
            String team = assignments.get(player.getUUID());
            if (team == null) team = "ATTACK";`,
   'skipObserversInAssign'],
  // B9 投票阶段提示
  [`    public void onServerTick() {
        switch (currentPhase) {`,
   `    public void onServerTick() {
        notifySpectatorsDuringVoting();
        switch (currentPhase) {`,
   'tickHook'],
]);
// B10 提示方法实现（插在 onServerTick 之前）
patch(GSM, [
  [`    // ========== 服务器Tick ==========
    public void onServerTick() {`,
   `    /**
     * 投票/揭示阶段：观战者不参与投票，只显示"当前正在投票，请等待"（动作栏，1.5 秒刷新一次）。
     */
    private void notifySpectatorsDuringVoting() {
        if (currentPhase == null) return;
        boolean voting = switch (currentPhase) {
            case MAP_VOTE, MAP_REVEAL, MAP_LOADING, TEAM_SELECT, TEAM_ASSIGN_SHOW,
                 COMMANDER_VOTE, DEFEND_COMMANDER_VOTE, ATTACK_COMMANDER_VOTE,
                 DEFEND_FACTION_SELECT, ATTACK_FACTION_SELECT, FACTION_REVEAL -> true;
            default -> false;
        };
        if (!voting) return;
        if (++votingNoticeTicker % 30 != 0) return;
        MinecraftServer server = Espetro.getServer();
        if (server == null) return;
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (isObserver(player.getUUID())) {
                NetworkManager.sendWaitingStatus(player, "§e当前正在投票，请等待", true);
            }
        }
    }

    // ========== 服务器Tick ==========
    public void onServerTick() {`,
   'noticeMethod'],
]);

// ============ C. 新包 SpectateRequestPacket ============
console.log('== C. 新增观战请求包 ==');
fs.writeFileSync('src/main/java/org/espetro/network/SpectateRequestPacket.java',
`package org.espetro.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import org.espetro.team.GameStateManager;

import java.util.function.Supplier;

/** 客户端请求进入观战（中途加入面板的"进入观战"按钮，C→S）。 */
public class SpectateRequestPacket {

    public SpectateRequestPacket() {
    }

    public static SpectateRequestPacket read(FriendlyByteBuf buf) {
        return new SpectateRequestPacket();
    }

    public void write(FriendlyByteBuf buf) {
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) {
                return;
            }
            GameStateManager.getInstance().selfSetObserver(player);
            NetworkManager.sendCloseModScreens(player);
        });
        ctx.get().setPacketHandled(true);
    }
}
`, 'utf8');
console.log('  ✓ 新建 SpectateRequestPacket.java');

// ============ D. NetworkManager 注册 + 发送 ============
console.log('== D. NetworkManager 注册与发送 ==');
patch(NM, [
  [`        // 职业选择包（包含结果）`,
   `        // 观战请求包（中途加入面板"进入观战"）
        NET.registerMessage(
            nextId(),
            SpectateRequestPacket.class,
            SpectateRequestPacket::write,
            SpectateRequestPacket::read,
            SpectateRequestPacket::handle
        );

        // 职业选择包（包含结果）`,
   'register'],
  [`    public static void sendFactionSelect(String factionId) {
        NET.sendToServer(new TeamSelectPacket(factionId));
    }`,
   `    public static void sendFactionSelect(String factionId) {
        NET.sendToServer(new TeamSelectPacket(factionId));
    }

    /** 请求进入观战（中途加入面板按钮，C→S）。 */
    public static void sendSpectateRequest() {
        NET.sendToServer(new SpectateRequestPacket());
    }`,
   'sendHelper'],
]);

// ============ E. 客户端：观战者不打开投票界面 ============
console.log('== E. ClientPacketHandlers 观战者拦截 ==');
patch(CPH, [
  [`    // ==================== OpenFactionScreenPacket ====================
    public static void handleOpenFactionScreen() {`,
   `    /** 本地玩家是否处于旁观（观战）模式：观战者在投票阶段只显示等待提示。 */
    private static boolean isLocalSpectator() {
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        return mc != null && mc.gameMode != null
            && mc.gameMode.getPlayerMode() == net.minecraft.world.level.GameType.SPECTATOR;
    }

    // ==================== OpenFactionScreenPacket ====================
    public static void handleOpenFactionScreen() {`,
   'spectatorHelper'],
  [`    public static void handleTeamAssign(org.espetro.network.TeamAssignPacket packet) {
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        if (mc.player == null) return;`,
   `    public static void handleTeamAssign(org.espetro.network.TeamAssignPacket packet) {
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        if (mc.player == null || isLocalSpectator()) return;`,
   'guardTeamAssign'],
  [`    public static void handleMapReveal(org.espetro.network.MapRevealPacket packet) {
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        if (mc.player == null) return;`,
   `    public static void handleMapReveal(org.espetro.network.MapRevealPacket packet) {
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        if (mc.player == null || isLocalSpectator()) return;`,
   'guardMapReveal'],
  [`    public static void handleFactionReveal(FactionRevealPacket packet) {
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        if (mc.player != null) {`,
   `    public static void handleFactionReveal(FactionRevealPacket packet) {
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        if (mc.player != null && !isLocalSpectator()) {`,
   'guardFactionReveal'],
  [`    public static void handleMapVoteState(MapVoteStatePacket packet) {
        org.espetro.client.gui.MapVoteScreen.update(packet);
    }`,
   `    public static void handleMapVoteState(MapVoteStatePacket packet) {
        if (isLocalSpectator()) return;
        org.espetro.client.gui.MapVoteScreen.update(packet);
    }`,
   'guardMapVoteState'],
  [`    public static void handleOpenMapVoteScreen() {
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        if (mc.player != null && !(mc.screen instanceof org.espetro.client.gui.MapVoteScreen)) {`,
   `    public static void handleOpenMapVoteScreen() {
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        if (mc.player != null && !isLocalSpectator()
            && !(mc.screen instanceof org.espetro.client.gui.MapVoteScreen)) {`,
   'guardOpenMapVote'],
  [`    public static void handleTeamSelectState(TeamSelectStatePacket packet) {
        org.espetro.client.gui.TeamSelectionScreen.updateTeamState(packet);
    }`,
   `    public static void handleTeamSelectState(TeamSelectStatePacket packet) {
        if (isLocalSpectator()) return;
        org.espetro.client.gui.TeamSelectionScreen.updateTeamState(packet);
    }`,
   'guardTeamSelectState'],
]);

// ============ F. 选边面板加"进入观战"按钮 ============
console.log('== F. TeamSelectionScreen 进入观战按钮 ==');
patch(TSS, [
  [`        root.addChild(EspetroAuiWidgets.centeredText(defendImgX, labelY, IMG_W,
            EspetroAuiWidgets.teamPrefix("DEFEND") + "§l" + defendName, EspetroAuiWidgets.DEFEND));

        refreshSelectionBorders();`,
   `        root.addChild(EspetroAuiWidgets.centeredText(defendImgX, labelY, IMG_W,
            EspetroAuiWidgets.teamPrefix("DEFEND") + "§l" + defendName, EspetroAuiWidgets.DEFEND));

        // 中途加入者可以直接选择观战（不再选边），由服务端设为观察者。
        int spectateW = 120;
        int spectateX = panelX + (panelW - spectateW) / 2;
        int spectateY = labelY + 14;
        root.addChild(EspetroAuiWidgets.button(spectateX, spectateY, spectateW, 20,
            "进入观战", () -> {
                org.espetro.network.NetworkManager.sendSpectateRequest();
                net.minecraft.client.Minecraft.getInstance().setScreen(null);
            }));

        refreshSelectionBorders();`,
   'spectateButton'],
]);

console.log(process.exitCode ? '\n有改动失败' : '\n全部改动成功');
