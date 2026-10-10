// patch-party-ui.mjs — 修复队伍密码设置/输入 + 成员名单可见
import fs from 'node:fs';
function patch(file, edits) {
  let text = fs.readFileSync(file, 'utf8');
  const eol = text.includes('\r\n') ? '\r\n' : '\n';
  for (const [from, to, label, expect] of edits) {
    const f = typeof from === 'string' ? from.replace(/\r?\n/g, eol) : from;
    let n;
    if (typeof f === 'string') n = text.split(f).length - 1;
    else { n = (text.match(f) || []).length; }
    const want = expect ?? 1;
    if (n !== want) { console.error(`  ❌ ${file.split('/').pop()} [${label}] 匹配 ${n} 次（期望 ${want}）`); process.exitCode = 1; return false; }
    const toS = typeof to === 'string' ? to.replace(/\r?\n/g, eol) : to;
    text = typeof f === 'string' ? text.split(f).join(toS) : text.replace(f, toS);
    console.log(`  ✓ ${file.split('/').pop()} [${label}]`);
  }
  fs.writeFileSync(file, text, 'utf8');
  return true;
}
const PM = 'src/main/java/org/espetro/team/PartyManager.java';
const PA = 'src/main/java/org/espetro/network/PartyActionPacket.java';
const PL = 'src/main/java/org/espetro/network/PartyListPacket.java';
const NM = 'src/main/java/org/espetro/network/NetworkManager.java';
const PG = 'src/main/java/org/espetro/client/gui/PartyScreen.java';

console.log('== 1) 服务端：密码不再等于锁定 + 支持设置密码 ==');
patch(PM, [
  [`            this.password = (password == null || password.isEmpty()) ? null : password;
            this.locked = password != null && !password.isEmpty();`,
   `            this.password = (password == null || password.isEmpty()) ? null : password;
            // 密码 ≠ 锁定：有密码的队伍仍可凭密码加入，锁定由队长单独切换（原实现把两者绑定，
            // 导致设了密码的队伍在 joinParty 的 locked 判定处被直接拒绝）。
            this.locked = false;`,
   'passwordNotLock'],
  [`    // -------------------- 广播 --------------------`,
   `    /** 设置 / 修改 / 清除队伍密码（仅队长）。空字符串 = 清除密码。 */
    public String setPassword(UUID partyId, UUID ownerId, String password) {
        PartyData party = partiesByOwner.get(partyId);
        if (party == null) return "队伍不存在。";
        if (!party.ownerId.equals(ownerId)) return "只有队长才能设置密码。";
        if (password == null || password.isEmpty()) {
            party.password = null;
            party.locked = false;
        } else {
            party.password = password;
            party.locked = false; // 设了密码就不再是"禁止加入"
        }
        broadcastPartyList();
        return null;
    }

    // -------------------- 广播 --------------------`,
   'setPassword'],
]);

console.log('== 2) C→S 包：新增 SET_PASSWORD ==');
patch(PA, [
  [`        TOGGLE_LOCK,
        DISBAND,`,
   `        TOGGLE_LOCK,
        /** 队长设置 / 修改 / 清除密码。 */
        SET_PASSWORD,
        DISBAND,`,
   'enumAction'],
  [`    public static PartyActionPacket toggleLock(UUID partyId) {
        return new PartyActionPacket(Action.TOGGLE_LOCK, partyId, null, null);
    }`,
   `    public static PartyActionPacket toggleLock(UUID partyId) {
        return new PartyActionPacket(Action.TOGGLE_LOCK, partyId, null, null);
    }

    public static PartyActionPacket setPassword(UUID partyId, String password) {
        return new PartyActionPacket(Action.SET_PASSWORD, partyId, password, null);
    }`,
   'factory'],
  [`                case DISBAND:`,
   `                case SET_PASSWORD:
                    if (partyId == null) break;
                    String pwErr = pm.setPassword(partyId, player.getUUID(), password);
                    if (pwErr != null) {
                        player.sendSystemMessage(Component.literal("§c" + pwErr));
                    } else {
                        player.sendSystemMessage(Component.literal(password == null || password.isEmpty()
                            ? "§a已清除队伍密码。" : "§a已更新队伍密码。"));
                    }
                    break;

                case DISBAND:`,
   'handlerCase'],
]);

console.log('== 3) S→C 包：携带"我的队伍"成员名单 ==');
patch(PL, [
  [`        public final boolean hasPassword;
        /** 当前客户端玩家是否在此队伍中（由服务端写入）。 */
        public final UUID myPartyId;`,
   `        public final boolean hasPassword;
        /** 当前客户端玩家是否在此队伍中（由服务端写入）。 */
        public final UUID myPartyId;
        /** 只有"自己所在的队伍"才带成员名单，其它队伍为空（不泄露信息）。 */
        public final List<String> memberNames;`,
   'field'],
  [`        public PartyInfo(UUID partyId, String ownerName, int memberCount, boolean locked,
                         boolean hasPassword, UUID myPartyId) {
            this.partyId = partyId;
            this.ownerName = ownerName;
            this.memberCount = memberCount;
            this.locked = locked;
            this.hasPassword = hasPassword;
            this.myPartyId = myPartyId;
        }`,
   `        public PartyInfo(UUID partyId, String ownerName, int memberCount, boolean locked,
                         boolean hasPassword, UUID myPartyId, List<String> memberNames) {
            this.partyId = partyId;
            this.ownerName = ownerName;
            this.memberCount = memberCount;
            this.locked = locked;
            this.hasPassword = hasPassword;
            this.myPartyId = myPartyId;
            this.memberNames = memberNames != null ? List.copyOf(memberNames) : List.of();
        }`,
   'ctor'],
  [`            list.add(new PartyInfo(p.partyId, p.ownerName, p.members.size(),
                p.locked, p.password != null && !p.password.isEmpty(),
                viewerInParty ? p.partyId : null));`,
   `            List<String> memberNames = List.of();
            if (viewerInParty) {
                memberNames = resolveMemberNames(p);
            }
            list.add(new PartyInfo(p.partyId, p.ownerName, p.members.size(),
                p.locked, p.password != null && !p.password.isEmpty(),
                viewerInParty ? p.partyId : null, memberNames));`,
   'fromFill'],
  [`    public static PartyListPacket read(FriendlyByteBuf buf) {`,
   `    /** 解析队伍成员名（在线玩家取名字，离线的标为"离线"）。 */
    private static List<String> resolveMemberNames(PartyManager.PartyData party) {
        List<String> names = new ArrayList<>();
        net.minecraft.server.MinecraftServer server = org.espetro.Espetro.getServer();
        for (UUID id : party.members) {
            net.minecraft.server.level.ServerPlayer sp =
                server == null ? null : server.getPlayerList().getPlayer(id);
            names.add(sp != null ? sp.getName().getString() : "离线");
        }
        return names;
    }

    public static PartyListPacket read(FriendlyByteBuf buf) {`,
   'resolveNames'],
  [`            UUID myPid = buf.readBoolean() ? buf.readUUID() : null;
            list.add(new PartyInfo(id, owner, count, locked, hasPw, myPid));`,
   `            UUID myPid = buf.readBoolean() ? buf.readUUID() : null;
            int nm = buf.readVarInt();
            List<String> names = new ArrayList<>(nm);
            for (int k = 0; k < nm; k++) names.add(buf.readUtf(32));
            list.add(new PartyInfo(id, owner, count, locked, hasPw, myPid, names));`,
   'read'],
  [`            buf.writeBoolean(p.myPartyId != null);
            if (p.myPartyId != null) buf.writeUUID(p.myPartyId);`,
   `            buf.writeBoolean(p.myPartyId != null);
            if (p.myPartyId != null) buf.writeUUID(p.myPartyId);
            buf.writeVarInt(p.memberNames.size());
            for (String nm : p.memberNames) buf.writeUtf(nm, 32);`,
   'write'],
]);

console.log('== 4) NetworkManager：发包方法 ==');
patch(NM, [
  [`    public static void sendPartyLeave() {`,
   `    public static void sendPartySetPassword(java.util.UUID partyId, String password) {
        NET.sendToServer(PartyActionPacket.setPassword(partyId, password));
    }

    public static void sendPartyLeave() {`,
   'sendSetPassword'],
]);

console.log('== 5) 客户端 PartyScreen：我的队伍成员 + 加入密码输入 + 管理密码 ==');
patch(PG, [
  // 5a 成员名单
  [`        y += 20;
        // 分隔线
        root.addChild(EspetroAuiWidgets.rect(panelX + 8, y, panelW - 16, 1, 0x30FFFFFF));`,
   `        y += 20;
        // 分隔线
        root.addChild(EspetroAuiWidgets.rect(panelX + 8, y, panelW - 16, 1, 0x30FFFFFF));
        y += 4;

        // 我的队伍：成员名单（队长与队员都能看到自己队里有哪些人）
        if (latest.myPartyId != null) {
            PartyListPacket.PartyInfo mine = null;
            for (PartyListPacket.PartyInfo p : latest.parties) {
                if (p.myPartyId != null && p.myPartyId.equals(latest.myPartyId)) { mine = p; break; }
            }
            String members = (mine != null && !mine.memberNames.isEmpty())
                ? "§f" + String.join("§7，§f", mine.memberNames)
                : "§7（成员同步中…）";
            root.addChild(EspetroAuiWidgets.text(panelX + 16, y,
                "§a我的队伍： " + members, EspetroAuiWidgets.TEXT));
            y += 12;
        }`,
   'myPartyMembers'],
  // 5b JoinPartyScreen：加密码输入框
  [`    private static final class JoinPartyScreen extends EspetroMenuScreen {
        private final PartyScreen parent;
        private final UUID partyId;

        JoinPartyScreen(PartyScreen parent, UUID partyId) {
            super(Component.literal("输入密码"));
            this.parent = parent;
            this.partyId = partyId;
        }`,
   `    private static final class JoinPartyScreen extends EspetroMenuScreen {
        private final PartyScreen parent;
        private final UUID partyId;
        private EditBox passwordField;

        JoinPartyScreen(PartyScreen parent, UUID partyId) {
            super(Component.literal("输入密码"));
            this.parent = parent;
            this.partyId = partyId;
        }

        @Override
        protected void init() {
            super.init();
            int pw = Math.min(240, width - 40);
            int px = (width - pw) / 2;
            int py = (height - 70) / 2;
            int bw = pw - 60;
            int bx = px + 30;
            passwordField = new EditBox(Minecraft.getInstance().font, bx, py + 26, bw, 14,
                Component.literal("队伍密码"));
            passwordField.setMaxLength(64);
            passwordField.setBordered(true);
            passwordField.setCanLoseFocus(true);
            addRenderableWidget(passwordField);
            setFocused(passwordField);
        }`,
   'joinInit'],
  [`            int ph = 70;
            int px = (width - pw) / 2;
            int py = (height - ph) / 2;

            root.addChild(EspetroAuiWidgets.panel(px, py, pw, ph, 0x00000000, 0x00000000));
            root.addChild(EspetroAuiWidgets.centeredText(px, py + 8, pw,
                "§b输入队伍密码", EspetroAuiWidgets.TEXT));

            int bw = pw - 60;
            int bx = px + 30;
            root.addChild(EspetroAuiWidgets.button(bx, py + 30, bw / 2 - 4, 14,
                "§a确认加入（1234）", () -> {
                    if (!tutorialPreviewMode) {
                        NetworkManager.sendPartyJoin(partyId, "1234");
                        Minecraft.getInstance().setScreen(parent);
                    }
                })`,
   `            int ph = 78;
            int px = (width - pw) / 2;
            int py = (height - ph) / 2;

            root.addChild(EspetroAuiWidgets.panel(px, py, pw, ph, 0x00000000, 0x00000000));
            root.addChild(EspetroAuiWidgets.centeredText(px, py + 8, pw,
                "§b输入队伍密码", EspetroAuiWidgets.TEXT));

            int bw = pw - 60;
            int bx = px + 30;
            root.addChild(EspetroAuiWidgets.button(bx, py + 48, bw / 2 - 4, 14,
                "§a确认加入", () -> {
                    if (!tutorialPreviewMode) {
                        String pwd = passwordField != null ? passwordField.getValue().trim() : "";
                        NetworkManager.sendPartyJoin(partyId, pwd);
                        Minecraft.getInstance().setScreen(parent);
                    }
                })`,
   'joinButton'],
  [`            root.addChild(EspetroAuiWidgets.button(bx + bw / 2 + 4, py + 30, bw / 2 - 4, 14,
                "§c返回", () -> Minecraft.getInstance().setScreen(parent))`,
   `            root.addChild(EspetroAuiWidgets.button(bx + bw / 2 + 4, py + 48, bw / 2 - 4, 14,
                "§c返回", () -> Minecraft.getInstance().setScreen(parent))`,
   'joinBack'],
  // 5c ManagePartyScreen：密码设置行 + 面板加高
  [`    private static final class ManagePartyScreen extends EspetroMenuScreen {
        private final PartyScreen parent;
        private final UUID partyId;

        ManagePartyScreen(PartyScreen parent, UUID partyId) {
            super(Component.literal("管理队伍"));
            this.parent = parent;
            this.partyId = partyId;
        }`,
   `    private static final class ManagePartyScreen extends EspetroMenuScreen {
        private final PartyScreen parent;
        private final UUID partyId;
        private EditBox passwordField;

        ManagePartyScreen(PartyScreen parent, UUID partyId) {
            super(Component.literal("管理队伍"));
            this.parent = parent;
            this.partyId = partyId;
        }

        @Override
        protected void init() {
            super.init();
            int pw = Math.min(300, width - 40);
            int px = (width - pw) / 2;
            int py = (height - 150) / 2;
            passwordField = new EditBox(Minecraft.getInstance().font, px + 20, py + 44, pw - 40, 14,
                Component.literal("密码"));
            passwordField.setMaxLength(64);
            passwordField.setBordered(true);
            passwordField.setCanLoseFocus(true);
            addRenderableWidget(passwordField);
        }`,
   'manageInit'],
  [`            int pw = Math.min(300, width - 40);
            int py = (height - 110) / 2; // smaller panel without password button
            int px = (width - pw) / 2;
            int gap = 4;

            root.addChild(EspetroAuiWidgets.panel(px, py, pw, 110, 0x00000000, 0x00000000));`,
   `            int pw = Math.min(300, width - 40);
            int py = (height - 150) / 2;
            int px = (width - pw) / 2;
            int gap = 4;

            root.addChild(EspetroAuiWidgets.panel(px, py, pw, 150, 0x00000000, 0x00000000));`,
   'managePanel'],
  [`            by += bh + gap;

            root.addChild(EspetroAuiWidgets.button(bx, by, bw, bh,
                "§c解散队伍",`,
   `            by += bh + gap;

            // 设置 / 修改密码（留空保存 = 清除密码）；输入框由 init() 创建在 py+44。
            root.addChild(EspetroAuiWidgets.button(bx, by, bw, bh,
                "§6保存密码（留空=清除）",
                () -> {
                    if (!tutorialPreviewMode) {
                        String pwd = passwordField != null ? passwordField.getValue().trim() : "";
                        NetworkManager.sendPartySetPassword(partyId, pwd);
                        Minecraft.getInstance().setScreen(parent);
                    }
                })
                .setColors(0x00000000, 0x20303020, 0x30403020)
                .setBorderColor(0x00000000));
            by += bh + gap;

            root.addChild(EspetroAuiWidgets.button(bx, by, bw, bh,
                "§c解散队伍",`,
   'managePasswordRow'],
]);
console.log(process.exitCode ? '\n有改动失败' : '\n全部改动成功');
