// patch-fort-factions.mjs — 工事定义新增可选「编制专属」字段 requirements.factions
import fs from 'node:fs';

function patch(file, pairs) {
  let text = fs.readFileSync(file, 'utf8');
  const eol = text.includes('\r\n') ? '\r\n' : '\n';
  for (const [from, to, label] of pairs) {
    const f = from.replace(/\r?\n/g, eol);
    const n = text.split(f).length - 1;
    if (n !== 1) { console.error(`  ❌ ${file.split('/').pop()} [${label}] 匹配 ${n} 次`); process.exitCode = 1; return; }
    text = text.replace(f, to.replace(/\r?\n/g, eol));
    console.log(`  ✓ [${label}]`);
  }
  fs.writeFileSync(file, text, 'utf8');
}

const FC = 'src/main/java/org/espetro/bastion/FortificationConfig.java';
const FM = 'src/main/java/org/espetro/bastion/FortificationManager.java';

console.log('== 1) FortificationConfig：新增 factions 字段 + 归一化 + 拼写告警 ==');
patch(FC, [
  [`    public static final class Requirements {
        @SerializedName("require_radio_range")
        public boolean requireRadioRange = true;
        @SerializedName("usable_by")
        public List<String> usableBy = new ArrayList<>(List.of(
            "commander", "squad_leader", "fireteam_leader"));

        void normalize(List<String> errors, String path) {
            if (usableBy == null || usableBy.isEmpty()) {
                errors.add(path + ".usable_by: 不得为空");
                return;
            }`,
   `    public static final class Requirements {
        @SerializedName("require_radio_range")
        public boolean requireRadioRange = true;
        @SerializedName("usable_by")
        public List<String> usableBy = new ArrayList<>(List.of(
            "commander", "squad_leader", "fireteam_leader"));

        /**
         * 编制专属限定（可选字段）。
         *
         * <p>省略或留空 = <strong>所有编制</strong>都能部署该工事；一旦填写，只有列出的编制能部署。
         * 取值为 {@code EsFactions} 里的编制 id（文件名，例如 {@code pla_112th_brigade}）。
         * 与 {@code usable_by} 是"与"关系：既要角色满足，也要编制在列表里。</p>
         */
        @SerializedName(value = "factions", alternate = {"usable_factions", "faction_ids"})
        public List<String> factions = new ArrayList<>();

        void normalize(List<String> errors, String path) {
            normalizeFactions(path);
            if (usableBy == null || usableBy.isEmpty()) {
                errors.add(path + ".usable_by: 不得为空");
                return;
            }`,
   'factions 字段 + normalize 调用'],
  [`            usableBy = normalized;
        }
    }`,
   `            usableBy = normalized;
        }

        /**
         * 编制列表去空去重；如果编制表此时已加载，对找不到的 id 打一条告警
         * （只告警不致命：配置里写错编制名时至少能在日志里看到）。
         */
        private void normalizeFactions(String path) {
            List<String> cleaned = new ArrayList<>();
            if (factions != null) {
                for (String raw : factions) {
                    String id = raw == null ? "" : raw.trim();
                    if (id.isEmpty() || cleaned.contains(id)) {
                        continue;
                    }
                    cleaned.add(id);
                }
            }
            factions = cleaned;
            if (cleaned.isEmpty()) {
                return;
            }
            try {
                String[] known = org.espetro.team.FactionDataProvider.getOrCreateLoader()
                    .getAllFactionIds();
                if (known == null || known.length == 0) {
                    return; // 编制表尚未加载，无法校验
                }
                for (String id : cleaned) {
                    boolean found = false;
                    for (String candidate : known) {
                        if (candidate != null && candidate.equalsIgnoreCase(id)) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        org.espetro.Espetro.LOGGER.warn(
                            "工事配置 {}.factions: 未找到编制 {}（该工事将无人能部署）", path, id);
                    }
                }
            } catch (Throwable ignored) {
                // 编制表不可用时跳过校验，不影响工事加载
            }
        }
    }`,
   'normalizeFactions 实现'],
  [`        requirements.add("usable_by", GSON.toJsonTree(List.of(
            "commander", "squad_leader", "fireteam_leader")));`,
   `        requirements.add("usable_by", GSON.toJsonTree(List.of(
            "commander", "squad_leader", "fireteam_leader")));
        // 可选：编制专属限定，留空数组 = 所有编制可用
        requirements.add("factions", new JsonArray());`,
   '默认配置示例字段'],
]);

console.log('== 2) FortificationManager：权限判定加入编制检查 ==');
patch(FM, [
  [`    public static boolean canUse(ServerPlayer player, FortificationConfig.FortificationDef def) {
        UUID uuid = player.getUUID();
        for (String raw : def.usableBy == null ? List.<String>of() : def.usableBy) {
            String role = raw.toLowerCase(Locale.ROOT);
            if ("commander".equals(role) && VoteManager.getInstance().isCommander(uuid)) return true;
            if ("squad_leader".equals(role) && SquadManager.getInstance().isSquadLeader(uuid)) return true;
            if ("fireteam_leader".equals(role) && SquadManager.getInstance().isFireteamLeader(uuid)) return true;
        }
        return false;
    }`,
   `    public static boolean canUse(ServerPlayer player, FortificationConfig.FortificationDef def) {
        if (player == null || def == null) {
            return false;
        }
        UUID uuid = player.getUUID();
        boolean roleOk = false;
        for (String raw : def.usableBy == null ? List.<String>of() : def.usableBy) {
            String role = raw.toLowerCase(Locale.ROOT);
            if ("commander".equals(role) && VoteManager.getInstance().isCommander(uuid)) { roleOk = true; break; }
            if ("squad_leader".equals(role) && SquadManager.getInstance().isSquadLeader(uuid)) { roleOk = true; break; }
            if ("fireteam_leader".equals(role) && SquadManager.getInstance().isFireteamLeader(uuid)) { roleOk = true; break; }
        }
        return roleOk && factionAllowed(player, def);
    }

    /**
     * 编制限定检查：{@code requirements.factions} 为空 = 所有编制可用；
     * 非空时只有列表内的编制可用（与角色 {@code usable_by} 是与关系）。
     */
    public static boolean factionAllowed(ServerPlayer player, FortificationConfig.FortificationDef def) {
        if (player == null || def == null || def.requirements == null) {
            return true;
        }
        List<String> allowed = def.requirements.factions;
        if (allowed == null || allowed.isEmpty()) {
            return true;
        }
        String factionId = org.espetro.team.ClassCountManager.getInstance()
            .getPlayerFaction(player.getUUID());
        if (factionId == null || factionId.isBlank()) {
            return false;
        }
        for (String raw : allowed) {
            if (raw != null && raw.equalsIgnoreCase(factionId)) {
                return true;
            }
        }
        return false;
    }`,
   'canUse + factionAllowed'],
  [`    private String validateSelectionRole(ServerPlayer player, Blueprint blueprint) {
        if (!canUse(player, blueprint.definition)) {
            return "§c你没有权限建造该工事。";
        }`,
   `    private String validateSelectionRole(ServerPlayer player, Blueprint blueprint) {
        if (!canUse(player, blueprint.definition)) {
            return factionAllowed(player, blueprint.definition)
                ? "§c你没有权限建造该工事。"
                : "§c你的编制无法部署该工事。";
        }`,
   '编制专属提示'],
]);

console.log(process.exitCode ? '\n有改动失败' : '\n全部改动成功');
