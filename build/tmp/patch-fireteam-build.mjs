// patch-fireteam-build.mjs — 火力组长可开战术轮盘建工事（除电台/队包，且限 FOB 内）
import fs from 'node:fs';

const CTS = 'src/main/java/org/espetro/client/gui/ClientTacticalState.java';
const ATR = 'src/main/java/org/espetro/client/gui/AuraTipRadialController.java';
const FM = 'src/main/java/org/espetro/bastion/FortificationManager.java';
const CFG = 'build/tmp/fortifications-before.json';

function patchFile(file, edits) {
  let text = fs.readFileSync(file, 'utf8');
  const eol = text.includes('\r\n') ? '\r\n' : '\n';
  for (const [from, to, label] of edits) {
    const f = from.replace(/\r?\n/g, eol);
    const n = text.split(f).length - 1;
    if (n !== 1) { console.error(`  ❌ ${file.split('/').pop()} [${label}] 匹配 ${n} 次`); process.exitCode = 1; return false; }
    text = text.replace(f, to.replace(/\r?\n/g, eol));
  }
  fs.writeFileSync(file, text, 'utf8');
  console.log(`  ✓ ${file.split('/').pop()}: ${edits.length} 处`);
  return true;
}

console.log('== 1) ClientTacticalState：开放轮盘权限给火力组长 ==');
patchFile(CTS, [
  [`    public static boolean canLocalPlayerOpenTacticalRadial(String playerName) {
        MarkerInfo info = markersByName.get(key(playerName));
        return hasSquadLeaderAccess(mySquadId, info);
    }`,
   `    public static boolean canLocalPlayerOpenTacticalRadial(String playerName) {
        MarkerInfo info = markersByName.get(key(playerName));
        return hasSquadLeaderAccess(mySquadId, info) || hasFireteamLeaderAccess(mySquadId, info);
    }`,
   'openGate'],
  [`    static boolean hasSquadLeaderAccess(int localSquadId, MarkerInfo info) {
        return localSquadId != NO_SQUAD
            && info != null
            && info.squadId == localSquadId
            && info.leader;
    }`,
   `    static boolean hasSquadLeaderAccess(int localSquadId, MarkerInfo info) {
        return localSquadId != NO_SQUAD
            && info != null
            && info.squadId == localSquadId
            && info.leader;
    }

    /** 本地玩家是否是所在火力组的组长（火力组长可开轮盘建工事，但不能建电台/放队包）。 */
    public static boolean isLocalFireteamLeader(String playerName) {
        MarkerInfo info = markersByName.get(key(playerName));
        return hasFireteamLeaderAccess(mySquadId, info);
    }

    static boolean hasFireteamLeaderAccess(int localSquadId, MarkerInfo info) {
        return localSquadId != NO_SQUAD
            && info != null
            && info.squadId == localSquadId
            && info.fireteamLeader;
    }`,
   'fireteamHelpers'],
]);

console.log('== 2) AuraTipRadialController：火力组长隐藏 Rally 与电台 ==');
patchFile(ATR, [
  ['    private static final ResourceLocation BUILD_FORT_ACTION = id("build_fortification");',
   `    private static final ResourceLocation BUILD_FORT_ACTION = id("build_fortification");
    /** 火力组长不允许建造的工事 id（电台：仍限指挥官/小队长）。 */
    private static final String FIRETEAM_FORBIDDEN_FORT_ID = "espetro:radio";`,
   'radioConstant'],
  [`    private static cc.sighs.auratip.data.RadialMenuData buildMenu() {
        var builder = base(BUILD_MENU)
            .slot("espetro.rally", RALLY, action(RadialActionPacket.Action.DEPLOY_RALLY),
                Component.translatable("radial.espetro.rally"), "#FF7DAE82");
        for (FortificationCatalogPacket.Entry fort : cachedFortifications) {
            if (fort == null || fort.id() == null || fort.id().isBlank()) {
                continue;
            }`,
   `    private static cc.sighs.auratip.data.RadialMenuData buildMenu() {
        // 只是火力组长（非指挥官、非小队长）时：不给 Rally（队包）和电台，其余工事照常。
        boolean fireteamOnly = isFireteamLeaderOnly();
        var builder = base(BUILD_MENU);
        if (!fireteamOnly) {
            builder = builder.slot("espetro.rally", RALLY, action(RadialActionPacket.Action.DEPLOY_RALLY),
                Component.translatable("radial.espetro.rally"), "#FF7DAE82");
        }
        for (FortificationCatalogPacket.Entry fort : cachedFortifications) {
            if (fort == null || fort.id() == null || fort.id().isBlank()) {
                continue;
            }
            if (fireteamOnly && FIRETEAM_FORBIDDEN_FORT_ID.equals(fort.id())) {
                continue;
            }`,
   'buildMenuFilter'],
  [`    /** Rally plus one slot per catalog fort; radio/HAB must not be hard-coded again. */
    static List<String> buildMenuSlotIds(List<FortificationCatalogPacket.Entry> forts) {
        List<String> ids = new ArrayList<>();
        ids.add("espetro.rally");
        if (forts == null) {
            return ids;
        }
        for (FortificationCatalogPacket.Entry fort : forts) {
            if (fort == null || fort.id() == null || fort.id().isBlank()) {
                continue;
            }
            ids.add("espetro.fort." + fort.id());
        }
        return ids;
    }`,
   `    /** Rally plus one slot per catalog fort; radio/HAB must not be hard-coded again. */
    static List<String> buildMenuSlotIds(List<FortificationCatalogPacket.Entry> forts) {
        List<String> ids = new ArrayList<>();
        boolean fireteamOnly = isFireteamLeaderOnly();
        if (!fireteamOnly) {
            ids.add("espetro.rally");
        }
        if (forts == null) {
            return ids;
        }
        for (FortificationCatalogPacket.Entry fort : forts) {
            if (fort == null || fort.id() == null || fort.id().isBlank()) {
                continue;
            }
            if (fireteamOnly && FIRETEAM_FORBIDDEN_FORT_ID.equals(fort.id())) {
                continue;
            }
            ids.add("espetro.fort." + fort.id());
        }
        return ids;
    }

    /**
     * 当前本地玩家是否"只是火力组长"——非指挥官、非小队长，仅火力组长。
     * 这类玩家可以开轮盘建工事，但不能建电台，也不能放 Rally/队包。
     */
    private static boolean isFireteamLeaderOnly() {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.player == null) {
            return false;
        }
        String name = mc.player.getName().getString();
        if (ClientTacticalState.isCommander(name) || ClientTacticalState.isLocalSquadLeader(name)) {
            return false;
        }
        return ClientTacticalState.isLocalFireteamLeader(name);
    }`,
   'slotIdsFilter'],
]);

console.log('== 3) FortificationManager：服务端拒绝火力组长建电台 ==');
patchFile(FM, [
  [`    private String validateSelectionRole(ServerPlayer player, Blueprint blueprint) {
        return canUse(player, blueprint.definition) ? null : "§c你没有权限建造该工事。";
    }`,
   `    private String validateSelectionRole(ServerPlayer player, Blueprint blueprint) {
        if (!canUse(player, blueprint.definition)) {
            return "§c你没有权限建造该工事。";
        }
        // 电台始终只允许指挥官/小队长：火力组长虽可建其它工事，但不能建电台
        // （与 config/espetro/fortifications.json 的 usable_by 互为双保险）。
        if (blueprint.definition.behaviorType == FortificationConfig.Behavior.RADIO) {
            UUID uuid = player.getUUID();
            if (!VoteManager.getInstance().isCommander(uuid)
                && !SquadManager.getInstance().isSquadLeader(uuid)) {
                return "§c只有小队长或指挥官才能部署电台。";
            }
        }
        return null;
    }`,
   'radioRoleGuard'],
]);

console.log('== 4) fortifications.json：电台去掉 fireteam_leader ==');
{
  const t = fs.readFileSync(CFG, 'utf8');
  const start = t.indexOf('"id": "espetro:radio"');
  const end = t.indexOf('"id": "espetro:hab"', start);
  if (start < 0 || end < 0 || end <= start) { console.error('  ❌ 未定位到 radio 定义段'); process.exitCode = 1; }
  else {
    const seg = t.slice(start, end);
    const out = seg.replace(/"squad_leader",(\s*)"fireteam_leader"/, '"squad_leader"$1');
    if (out === seg) { console.error('  ❌ radio.usable_by 未匹配 fireteam_leader'); process.exitCode = 1; }
    else {
      fs.writeFileSync('build/tmp/fortifications-after.json', t.slice(0, start) + out + t.slice(end), 'utf8');
      const j = JSON.parse(fs.readFileSync('build/tmp/fortifications-after.json', 'utf8'));
      const radio = j.fortifications.find((d) => d.id === 'espetro:radio');
      const hab = j.fortifications.find((d) => d.id === 'espetro:hab');
      const others = j.fortifications.filter((d) => d.id !== 'espetro:radio');
      const othersOk = others.every((d) => (d.requirements?.usable_by ?? []).includes('fireteam_leader'));
      console.log(`  ✓ radio.usable_by = ${JSON.stringify(radio.requirements.usable_by)}`);
      console.log(`     其余 ${others.length} 个定义仍含 fireteam_leader: ${othersOk ? '是 ✓' : '否 ❌'}（hab=${JSON.stringify(hab.requirements.usable_by)}）`);
      if (radio.requirements.usable_by.includes('fireteam_leader') || !othersOk) process.exitCode = 1;
    }
  }
}
console.log(process.exitCode ? '\n有改动失败' : '\n全部改动成功（服务端配置已生成 build/tmp/fortifications-after.json）');
