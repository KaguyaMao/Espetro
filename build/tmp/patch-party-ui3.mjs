// patch-party-ui3.mjs — 行内按钮右对齐 + 新增[密码]按钮与设置密码弹窗
import fs from 'node:fs';
function patch(file, edits) {
  let text = fs.readFileSync(file, 'utf8');
  const eol = text.includes('\r\n') ? '\r\n' : '\n';
  for (const [from, to, label, expect] of edits) {
    const f = from.replace(/\r?\n/g, eol);
    const n = text.split(f).length - 1;
    const want = expect ?? 1;
    if (n !== want) { console.error(`  ❌ ${file.split('/').pop()} [${label}] 匹配 ${n} 次（期望 ${want}）`); process.exitCode = 1; return false; }
    text = text.split(f).join(to.replace(/\r?\n/g, eol));
    console.log(`  ✓ ${file.split('/').pop()} [${label}]`);
  }
  fs.writeFileSync(file, text, 'utf8');
  return true;
}
const PG = 'src/main/java/org/espetro/client/gui/PartyScreen.java';

console.log('== 行内按钮右对齐 + [密码] 按钮 ==');
patch(PG, [
  [`                if (canManage) {
                    // 锁定 / 解散：紧跟在队伍名字后面
                    String lockLabel = p.locked ? "§a解锁" : "§c锁定";
                    int lbw = EspetroAuiWidgets.textButtonWidth(lockLabel);
                    int dbw = EspetroAuiWidgets.textButtonWidth("解散");
                    int bx1 = panelX + 16 + 150;
                    root.addChild(EspetroAuiWidgets.button(bx1, listY - 2, lbw, 14, lockLabel,
                        () -> {
                            if (!tutorialPreviewMode) NetworkManager.sendPartyToggleLock(p.partyId);
                        })
                        .setColors(0x00000000, 0x20303050, 0x30404060)
                        .setBorderColor(0x00000000));
                    root.addChild(EspetroAuiWidgets.button(bx1 + lbw + 6, listY - 2, dbw, 14, "§c解散",
                        () -> {
                            if (!tutorialPreviewMode) NetworkManager.sendPartyDisband(p.partyId);
                        })
                        .setColors(0x00000000, 0x20402020, 0x30502020)
                        .setBorderColor(0x00000000));
                } else if (latest.myPartyId == null && !p.locked) {`,
   `                if (canManage) {
                    // 锁定 / 密码 / 解散：紧跟在队伍名字后面（右对齐，先算宽度再裁队名）
                    String lockLabel = p.locked ? "§a解锁" : "§c锁定";
                    int lbw = EspetroAuiWidgets.textButtonWidth(lockLabel);
                    int pwdbw = EspetroAuiWidgets.textButtonWidth("密码");
                    int dbw = EspetroAuiWidgets.textButtonWidth("解散");
                    int right = panelX + panelW - 16;
                    int bx1 = right - dbw - lbw - pwdbw - 12;
                    root.addChild(EspetroAuiWidgets.button(bx1, listY - 2, lbw, 14, lockLabel,
                        () -> {
                            if (!tutorialPreviewMode) NetworkManager.sendPartyToggleLock(p.partyId);
                        })
                        .setColors(0x00000000, 0x20303050, 0x30404060)
                        .setBorderColor(0x00000000));
                    root.addChild(EspetroAuiWidgets.button(bx1 + lbw + 6, listY - 2, pwdbw, 14, "§e密码",
                        () -> {
                            if (!tutorialPreviewMode) {
                                Minecraft.getInstance().setScreen(
                                    new SetPartyPasswordScreen(this, p.partyId));
                            }
                        })
                        .setColors(0x00000000, 0x20303020, 0x30403020)
                        .setBorderColor(0x00000000));
                    root.addChild(EspetroAuiWidgets.button(bx1 + lbw + pwdbw + 12, listY - 2, dbw, 14, "§c解散",
                        () -> {
                            if (!tutorialPreviewMode) NetworkManager.sendPartyDisband(p.partyId);
                        })
                        .setColors(0x00000000, 0x20402020, 0x30502020)
                        .setBorderColor(0x00000000));
                } else if (latest.myPartyId == null && !p.locked) {`,
   'rowButtons'],
  // 设置密码弹窗（放在 JoinPartyScreen 之后）
  [`    // ==================== 子界面：加入队伍 ====================`,
   `    // ==================== 子界面：设置队伍密码 ====================

    private static final class SetPartyPasswordScreen extends EspetroMenuScreen {
        private final PartyScreen parent;
        private final UUID partyId;
        private EditBox passwordField;

        SetPartyPasswordScreen(PartyScreen parent, UUID partyId) {
            super(Component.literal("设置队伍密码"));
            this.parent = parent;
            this.partyId = partyId;
        }

        @Override
        protected void init() {
            super.init();
            int pw = Math.min(260, width - 40);
            int px = (width - pw) / 2;
            int py = (height - 84) / 2;
            int bw = pw - 60;
            int bx = px + 30;
            passwordField = new EditBox(Minecraft.getInstance().font, bx, py + 28, bw, 14,
                Component.literal("队伍密码"));
            passwordField.setMaxLength(64);
            passwordField.setBordered(true);
            passwordField.setCanLoseFocus(true);
            addRenderableWidget(passwordField);
            setFocused(passwordField);
        }

        @Override
        protected void renderBeforeMenu(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            EspetroAuiWidgets.drawScreenShade(graphics, this.width, this.height);
        }

        @Override
        protected void buildMenuRoot(GuiElement root) {
            int pw = Math.min(260, width - 40);
            int ph = 84;
            int px = (width - pw) / 2;
            int py = (height - ph) / 2;

            root.addChild(EspetroAuiWidgets.panel(px, py, pw, ph, 0x00000000, 0x00000000));
            root.addChild(EspetroAuiWidgets.centeredText(px, py + 8, pw,
                "§b设置队伍密码", EspetroAuiWidgets.TEXT));
            root.addChild(EspetroAuiWidgets.centeredText(px, py + 18, pw,
                "§7留空保存 = 清除密码（有密码时需密码才能加入）", EspetroAuiWidgets.MUTED));

            int bw = pw - 60;
            int bx = px + 30;
            root.addChild(EspetroAuiWidgets.button(bx, py + 52, bw / 2 - 4, 14,
                "§a保存密码", () -> {
                    if (!tutorialPreviewMode) {
                        String pwd = passwordField != null ? passwordField.getValue().trim() : "";
                        NetworkManager.sendPartySetPassword(partyId, pwd);
                        Minecraft.getInstance().setScreen(parent);
                    }
                })
                .setColors(0x00000000, 0x20303050, 0x30404060)
                .setBorderColor(0x00000000));
            root.addChild(EspetroAuiWidgets.button(bx + bw / 2 + 4, py + 52, bw / 2 - 4, 14,
                "§c返回", () -> Minecraft.getInstance().setScreen(parent))
                .setColors(0x00000000, 0x20402020, 0x30502020)
                .setBorderColor(0x00000000));
        }

        @Override
        public boolean shouldCloseOnEsc() { return true; }
        @Override
        public boolean isPauseScreen() { return false; }
    }

    // ==================== 子界面：加入队伍 ====================`,
   'setPasswordScreen'],
]);
console.log(process.exitCode ? '\n有失败' : '\n全部成功');
