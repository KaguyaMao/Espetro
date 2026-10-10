// patch-spectator-fix.mjs — 补上两处失败的空行锚点
import fs from 'node:fs';
function patch(file, edits) {
  let text = fs.readFileSync(file, 'utf8');
  const eol = text.includes('\r\n') ? '\r\n' : '\n';
  for (const [from, to, label] of edits) {
    const f = from.replace(/\r?\n/g, eol);
    const n = text.split(f).length - 1;
    if (n !== 1) { console.error(`  ❌ ${file.split('/').pop()} [${label}] 匹配 ${n} 次`); process.exitCode = 1; return; }
    text = text.replace(f, to.replace(/\r?\n/g, eol));
    console.log(`  ✓ ${file.split('/').pop()} [${label}]`);
  }
  fs.writeFileSync(file, text, 'utf8');
}

const GSM = 'src/main/java/org/espetro/team/GameStateManager.java';
const CPH = 'src/main/java/org/espetro/client/ClientPacketHandlers.java';

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
]);
console.log(process.exitCode ? '有失败' : '补齐成功');
