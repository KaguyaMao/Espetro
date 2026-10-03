// patch-observer-blind.mjs — 观战者进入战局后解除失明/锁位 + 投票名单显式剔除观战者
import fs from 'node:fs';
function patch(file, edits) {
  let text = fs.readFileSync(file, 'utf8');
  const eol = text.includes('\r\n') ? '\r\n' : '\n';
  for (const [from, to, label, expect] of edits) {
    const f = from.replace(/\r?\n/g, eol);
    const n = text.split(f).length - 1;
    const want = expect ?? 1;
    if (n !== want) { console.error(`  ❌ ${file.split('/').pop()} [${label}] 匹配 ${n} 次（期望 ${want}）`); process.exitCode = 1; return; }
    text = text.split(f).join(to.replace(/\r?\n/g, eol));
    console.log(`  ✓ ${file.split('/').pop()} [${label}]`);
  }
  fs.writeFileSync(file, text, 'utf8');
}

const GSM = 'src/main/java/org/espetro/team/GameStateManager.java';
const VM = 'src/main/java/org/espetro/team/VoteManager.java';

console.log('== 1) 观战者在战局中不再失明（enforceSpectatorBlindness 提前返回）==');
patch(GSM, [
  [`    public static void enforceSpectatorBlindness(ServerPlayer player, boolean forceClientResync) {
        if (player == null) {
            return;
        }
        if (!player.isSpectator()) {`,
   `    public static void enforceSpectatorBlindness(ServerPlayer player, boolean forceClientResync) {
        if (player == null) {
            return;
        }
        // 观战者进入战局（对战/结算）后不再失明，也不再被锁位，可自由飞行观战；
        // 投票与准备阶段仍然保持隐藏（只显示"当前正在投票，请等待"）。
        if (isObserverInMatch(player)) {
            player.setGameMode(GameType.SPECTATOR);
            player.removeEffect(MobEffects.BLINDNESS);
            return;
        }
        if (!player.isSpectator()) {`,
   'blindBail'],
  // 新增辅助 + 释放方法（插在 enforceSpectatorBlindness(player) 单参重载之前）
  [`    public static void enforceSpectatorBlindness(ServerPlayer player) {
        enforceSpectatorBlindness(player, true);
    }`,
   `    public static void enforceSpectatorBlindness(ServerPlayer player) {
        enforceSpectatorBlindness(player, true);
    }

    /** 该观战者是否处于“已进入战局”的阶段（对战 / 回合结算）。 */
    public static boolean isObserverInMatch(ServerPlayer player) {
        if (player == null) {
            return false;
        }
        GameStateManager manager = getInstance();
        if (manager == null || !manager.isObserver(player.getUUID())) {
            return false;
        }
        return currentPhase == GamePhase.BATTLE || currentPhase == GamePhase.ROUND_END;
    }

    /**
     * 观战者进入战局：解除失明与位置锁，保持旁观模式自由观战。
     */
    public void releaseObserverHold(ServerPlayer player) {
        if (player == null) {
            return;
        }
        player.setGameMode(GameType.SPECTATOR);
        player.removeEffect(MobEffects.BLINDNESS);
        BastionManager.getInstance().unlockPlayerPosition(player.getUUID());
        BastionManager.getInstance().clearWaiting(player.getUUID());
    }`,
   'helpers'],
  // 开战时对观战者放行
  [`        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (isRecordedUnassigned(player)) {
                // 开战事件不得像普通已部署玩家一样摘除失明。
                applyMatchHoldState(player, HoldAnchor.CURRENT_LOCK);`,
   `        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (isObserver(player.getUUID())) {
                // 观战者：进入战局即解除失明与锁位（不再走"未分配"的隐藏态）。
                releaseObserverHold(player);
            } else if (isRecordedUnassigned(player)) {
                // 开战事件不得像普通已部署玩家一样摘除失明。
                applyMatchHoldState(player, HoldAnchor.CURRENT_LOCK);`,
   'battleStartRelease'],
]);

console.log('== 2) 指挥官投票名单显式剔除观战者 ==');
patch(VM, [
  [`        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            factionId = ClassCountManager.getInstance().getPlayerFaction(player.getUUID());`,
   `        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            // 观战者不参与指挥官选举（既不是候选人也不能投票）。正常情况下他们已因
            // clearPlayerRoundAssignment 失去编制而被上面的判断过滤，这里再显式挡一层。
            if (GameStateManager.getInstance().isObserver(player.getUUID())) {
                continue;
            }
            factionId = ClassCountManager.getInstance().getPlayerFaction(player.getUUID());`,
   'skipObservers'],
]);
console.log(process.exitCode ? '有失败' : '全部成功');
