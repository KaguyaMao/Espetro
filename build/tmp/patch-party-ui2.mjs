// patch-party-ui2.mjs — 修输入框 + 重排组队界面（成员列在队名下、锁定/解散跟在名字后、删除管理页面）
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
const MS = 'src/main/java/org/espetro/client/gui/EspetroMenuScreen.java';
const PG = 'src/main/java/org/espetro/client/gui/PartyScreen.java';

// ---------- 1) EspetroMenuScreen：让原版输入框优先拿到输入 ----------
console.log('== 1) 输入事件优先给原版控件（修输入框不能输入）==');
patch(MS, [
  [`    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (tutorialPreviewMode) {
            return TutorialHudOverlay.mouseClicked(mouseX, mouseY, button) || true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }`,
   `    /** 点到输入框时直接交给它：AUI 层会先消费点击，导致原版输入框无法聚焦、无法输入。 */
    private net.minecraft.client.gui.components.EditBox editBoxAt(double mouseX, double mouseY) {
        for (net.minecraft.client.gui.components.events.GuiEventListener child : this.children()) {
            if (child instanceof net.minecraft.client.gui.components.EditBox box
                && box.visible && box.isMouseOver(mouseX, mouseY)) {
                return box;
            }
        }
        return null;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        net.minecraft.client.gui.components.EditBox box = editBoxAt(mouseX, mouseY);
        if (box != null) {
            this.setFocused(box);
            box.mouseClicked(mouseX, mouseY, button);
            return true;
        }
        if (tutorialPreviewMode) {
            return TutorialHudOverlay.mouseClicked(mouseX, mouseY, button) || true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }`,
   'mouseToEditBox'],
  [`    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (TutorialClientController.handleKeyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (tutorialPreviewMode) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }`,
   `    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // 输入框获得焦点时优先处理（AUI 层会吞按键，导致打不了字）。
        if (this.getFocused() instanceof net.minecraft.client.gui.components.EditBox box) {
            if (box.keyPressed(keyCode, scanCode, modifiers)) {
                return true;
            }
            if (keyCode == 256) { // ESC：先取消输入框焦点，不关闭界面
                this.setFocused(null);
                return true;
            }
        }
        if (TutorialClientController.handleKeyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (tutorialPreviewMode) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }`,
   'keyToEditBox'],
  [`    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (tutorialPreviewMode) {
            return true;
        }
        return super.charTyped(codePoint, modifiers);
    }`,
   `    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (this.getFocused() instanceof net.minecraft.client.gui.components.EditBox box) {
            if (box.charTyped(codePoint, modifiers)) {
                return true;
            }
        }
        if (tutorialPreviewMode) {
            return true;
        }
        return super.charTyped(codePoint, modifiers);
    }`,
   'charToEditBox'],
]);

// ---------- 2) PartyScreen：去掉管理队伍按钮 ----------
console.log('== 2) 顶部只保留"退出队伍"，删除管理队伍入口 ==');
patch(PG, [
  [`            int bw1 = EspetroAuiWidgets.textButtonWidth("§c退出队伍");
            int bw2 = EspetroAuiWidgets.textButtonWidth(latest.isOwner ? "§e管理队伍" : "§7管理队伍");
            int rowW = bw1 + bw2 + 8;
            root.addChild(EspetroAuiWidgets.button(
                panelX + panelW / 2 - rowW / 2, y, bw1, 14,
                "§c退出队伍", () -> {
                    if (!tutorialPreviewMode) NetworkManager.sendPartyLeave();
                })
                .setColors(0x00000000, 0x20402020, 0x30502020)
                .setBorderColor(0x00000000));
            if (latest.isOwner) {
                root.addChild(EspetroAuiWidgets.button(
                    panelX + panelW / 2 - rowW / 2 + bw1 + 8, y, bw2, 14,
                    "§e管理队伍", () -> {
                        if (!tutorialPreviewMode) {
                            Minecraft.getInstance().setScreen(new ManagePartyScreen(this, latest.myPartyId));
                        }
                    })
                    .setColors(0x00000000, 0x20303020, 0x30403020)
                    .setBorderColor(0x00000000));
            }`,
   `            int bw1 = EspetroAuiWidgets.textButtonWidth("§c退出队伍");
            root.addChild(EspetroAuiWidgets.button(
                panelX + panelW / 2 - bw1 / 2, y, bw1, 14,
                "§c退出队伍", () -> {
                    if (!tutorialPreviewMode) NetworkManager.sendPartyLeave();
                })
                .setColors(0x00000000, 0x20402020, 0x30502020)
                .setBorderColor(0x00000000));`,
   'removeManageButton'],
  // 我的队伍成员那一行删掉（改为显示在队名下面）
  [`        y += 4;

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
   `        y += 4;`,
   'dropMyPartyLine'],
  // 列表行重排
  [`            int listW = panelW - 24;
            int entryH = 16;
            int listY = y;
            for (PartyListPacket.PartyInfo p : latest.parties) {
                String lockIcon = p.locked ? "§c🔒 " : "";
                String pwIcon = p.hasPassword ? " §7🔑" : "";
                boolean isMyParty = p.myPartyId != null && p.myPartyId.equals(latest.myPartyId);
                String ownerText = "§f" + p.ownerName + " 的队伍";
                String infoText = "§7[" + p.memberCount + "/" + latest.maxPartySize + "]";
                String fullText = lockIcon + ownerText + pwIcon + "  " + infoText;
                if (isMyParty) fullText = "§a§l● " + fullText;

                String label = EspetroAuiWidgets.trimToWidth(fullText, listW - 50);
                root.addChild(EspetroAuiWidgets.text(panelX + 16, listY + 2,
                    label, isMyParty ? 0xFFFFFF : EspetroAuiWidgets.TEXT));
                // 加入按钮
                if (latest.myPartyId == null && !p.locked) {
                    int jbw = EspetroAuiWidgets.textButtonWidth("加入");
                    root.addChild(EspetroAuiWidgets.button(
                        panelX + panelW - 30 - jbw, listY, jbw, 14,
                        "§a加入", () -> {
                            if (!tutorialPreviewMode) {
                                if (p.hasPassword) {
                                    Minecraft.getInstance().setScreen(
                                        new JoinPartyScreen(this, p.partyId));
                                } else {
                                    NetworkManager.sendPartyJoin(p.partyId, "");
                                }
                            }
                        })
                        .setColors(0x00000000, 0x20254530, 0x30305530)
                        .setBorderColor(0x00000000));
                }
                listY += entryH;
            }`,
   `            int listW = panelW - 24;
            int listY = y;
            for (PartyListPacket.PartyInfo p : latest.parties) {
                String lockIcon = p.locked ? "§c🔒 " : "";
                String pwIcon = p.hasPassword ? " §7🔑" : "";
                boolean isMyParty = p.myPartyId != null && p.myPartyId.equals(latest.myPartyId);
                boolean canManage = isMyParty && latest.isOwner;
                String infoText = "§7[" + p.memberCount + "/" + latest.maxPartySize + "]";
                String nameText = (isMyParty ? "§a§l● §f" : "§f") + p.ownerName + " 的队伍";

                // 队伍名称行
                root.addChild(EspetroAuiWidgets.text(panelX + 16, listY,
                    EspetroAuiWidgets.trimToWidth(lockIcon + nameText + pwIcon + "  " + infoText, listW - 120),
                    isMyParty ? 0xFFFFFF : EspetroAuiWidgets.TEXT));

                if (canManage) {
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
                } else if (latest.myPartyId == null && !p.locked) {
                    int jbw = EspetroAuiWidgets.textButtonWidth("加入");
                    root.addChild(EspetroAuiWidgets.button(
                        panelX + panelW - 30 - jbw, listY - 2, jbw, 14,
                        "§a加入", () -> {
                            if (!tutorialPreviewMode) {
                                if (p.hasPassword) {
                                    Minecraft.getInstance().setScreen(
                                        new JoinPartyScreen(this, p.partyId));
                                } else {
                                    NetworkManager.sendPartyJoin(p.partyId, "");
                                }
                            }
                        })
                        .setColors(0x00000000, 0x20254530, 0x30305530)
                        .setBorderColor(0x00000000));
                }

                if (isMyParty) {
                    // 成员名单显示在队伍名称下面（队长与队员都能看到）
                    String members = !p.memberNames.isEmpty()
                        ? "§7成员： §f" + String.join("§7， §f", p.memberNames)
                        : "§7成员： §8（同步中…）";
                    root.addChild(EspetroAuiWidgets.text(panelX + 28, listY + 12,
                        EspetroAuiWidgets.trimToWidth(members, listW - 16), EspetroAuiWidgets.TEXT));
                    listY += 30;
                } else {
                    listY += 16;
                }
            }`,
   'listRowRedesign'],
  // 输入框打开即聚焦（创建队伍）
  [`            passwordField.setCanLoseFocus(true);
            addRenderableWidget(passwordField);
        }

        @Override
        protected void renderBeforeMenu(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            EspetroAuiWidgets.drawScreenShade(graphics, this.width, this.height);
        }

        @Override
        protected void buildMenuRoot(GuiElement root) {
            int pw = Math.min(280, width - 40);
            int ph = 100;`,
   `            passwordField.setCanLoseFocus(true);
            addRenderableWidget(passwordField);
            setFocused(passwordField);
        }

        @Override
        protected void renderBeforeMenu(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            EspetroAuiWidgets.drawScreenShade(graphics, this.width, this.height);
        }

        @Override
        protected void buildMenuRoot(GuiElement root) {
            int pw = Math.min(280, width - 40);
            int ph = 100;`,
   'createFocus'],
]);

// ---------- 3) 删除 ManagePartyScreen 整个类 ----------
console.log('== 3) 删除"管理队伍"页面 ==');
{
  const P = PG;
  let text = fs.readFileSync(P, 'utf8');
  const eol = text.includes('\r\n') ? '\r\n' : '\n';
  const marker = '    // ==================== 子界面：管理队伍 ====================';
  const i = text.indexOf(marker);
  if (i < 0) { console.error('  ❌ 未找到管理队伍区块'); process.exitCode = 1; }
  else {
    // 从 marker 开始按花括号配对找到 ManagePartyScreen 类结束
    const braceStart = text.indexOf('{', text.indexOf('class ManagePartyScreen', i));
    let depth = 0, end = -1;
    for (let k = braceStart; k < text.length; k++) {
      if (text[k] === '{') depth++;
      else if (text[k] === '}') { depth--; if (depth === 0) { end = k; break; } }
    }
    if (end < 0) { console.error('  ❌ 类结尾未找到'); process.exitCode = 1; }
    else {
      const removed = text.slice(i, end + 1);
      text = text.slice(0, i) + text.slice(end + 1);
      fs.writeFileSync(P, text, 'utf8');
      console.log(`  ✓ 已删除 ManagePartyScreen（${removed.split('\n').length} 行）`);
    }
  }
}
console.log(process.exitCode ? '\n有改动失败' : '\n全部改动成功');
