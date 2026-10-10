package org.espetro.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import org.espetro.network.NetworkManager;
import org.espetro.network.PartyListPacket;
import org.espetro.client.aui.GuiElement;

import java.util.UUID;

/**
 * 主城 J 键组队面板。
 * 显示队伍列表 + 创建/加入/退出/锁定/踢人操作。
 */
public final class PartyScreen extends EspetroMenuScreen {

    private static PartyListPacket latest;
    private EspetroAuiWidgets.PhaseHeader phaseHeader;
    /** 每 tick 刷新 timer，便于重拉数据。 */
    private int refreshTimer;

    public PartyScreen() {
        super(Component.literal("组队匹配"));
    }

    public static void update(PartyListPacket packet) {
        latest = packet;
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen instanceof PartyScreen screen) {
            screen.rebuildMenuRoot();
        }
    }

    @Override
    protected void renderBeforeMenu(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        EspetroAuiWidgets.drawScreenShade(graphics, this.width, this.height);
    }

    @Override
    protected void buildMenuRoot(GuiElement root) {
        if (latest == null) latest = new PartyListPacket(
            java.util.List.of(), 7, null, false);

        int panelW = Math.min(400, this.width - 20);
        int headerH = EspetroAuiWidgets.PHASE_HEADER_HEIGHT + 4;

        phaseHeader = EspetroAuiWidgets.addMutablePhaseHeader(root, this.width,
            "§b§l组队匹配",
            latest.myPartyId == null ? "§7你尚未加入任何队伍" : "§a你已加入队伍",
            "§7上限 §e" + latest.maxPartySize + " 人 §8| §7按 J 键呼出",
            EspetroAuiWidgets.GOLD);

        int y = headerH + 4;
        int panelX = (this.width - panelW) / 2;

        // 创建 / 退出 按钮行
        if (latest.myPartyId == null) {
            int bw = EspetroAuiWidgets.textButtonWidth("§a+ 创建新队伍");
            root.addChild(EspetroAuiWidgets.button(panelX + panelW / 2 - bw / 2, y, bw, 14,
                "§a+ 创建新队伍", () -> {
                    if (!tutorialPreviewMode) {
                        Minecraft.getInstance().setScreen(new CreatePartyScreen(this));
                    }
                })
                .setColors(0x00000000, 0x20255030, 0x30306030)
                .setBorderColor(0x00000000));
        } else {
            int bw1 = EspetroAuiWidgets.textButtonWidth("§c退出队伍");
            root.addChild(EspetroAuiWidgets.button(
                panelX + panelW / 2 - bw1 / 2, y, bw1, 14,
                "§c退出队伍", () -> {
                    if (!tutorialPreviewMode) NetworkManager.sendPartyLeave();
                })
                .setColors(0x00000000, 0x20402020, 0x30502020)
                .setBorderColor(0x00000000));
        }

        y += 20;
        // 分隔线
        root.addChild(EspetroAuiWidgets.rect(panelX + 8, y, panelW - 16, 1, 0x30FFFFFF));
        y += 4;
        y += 8;

        // 队伍列表
        if (latest.parties.isEmpty()) {
            root.addChild(EspetroAuiWidgets.centeredText(panelX, y + 20, panelW,
                "§8暂无队伍，点击上方按钮创建", EspetroAuiWidgets.MUTED));
        } else {
            int listW = panelW - 24;
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
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        refreshTimer++;
        if (refreshTimer % 60 == 0) {
            // 每 3 秒拉取最新队伍列表
            NetworkManager.requestPartyList();
        }
    }

    @Override
    public void onClose() {
        super.onClose();
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    // ==================== 子界面：创建队伍 ====================

    /** 简单输入密码并创建队伍。 */
    private static final class CreatePartyScreen extends EspetroMenuScreen {
        private final PartyScreen parent;
        private EditBox passwordField;

        CreatePartyScreen(PartyScreen parent) {
            super(Component.literal("创建队伍"));
            this.parent = parent;
        }

        @Override
        protected void init() {
            super.init();
            int pw = Math.min(280, width - 40);
            int px = (width - pw) / 2;
            int py = (height - 100) / 2;
            int bw = pw - 60;
            int bx = px + 30;
            passwordField = new EditBox(Minecraft.getInstance().font, bx, py + 42, bw, 14,
                Component.literal("密码"));
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
            int pw = Math.min(280, width - 40);
            int ph = 100;
            int px = (width - pw) / 2;
            int py = (height - ph) / 2;

            root.addChild(EspetroAuiWidgets.panel(px, py, pw, ph, 0x00000000, 0x00000000));
            root.addChild(EspetroAuiWidgets.centeredText(px, py + 8, pw,
                "\u00a7b创建新队伍", EspetroAuiWidgets.TEXT));
            root.addChild(EspetroAuiWidgets.centeredText(px, py + 24, pw,
                "\u00a77输入密码（留空则不设密码）", EspetroAuiWidgets.MUTED));

            int bw = pw - 60;
            int bx = px + 30;
            root.addChild(EspetroAuiWidgets.button(bx, py + 60, bw / 2 - 4, 14,
                "\u00a7a创建（无密码）", () -> {
                    if (!tutorialPreviewMode) {
                        NetworkManager.sendPartyCreate("");
                        Minecraft.getInstance().setScreen(parent);
                    }
                })
                .setColors(0x00000000, 0x20303050, 0x30404060)
                .setBorderColor(0x00000000));
            root.addChild(EspetroAuiWidgets.button(bx + bw / 2 + 4, py + 60, bw / 2 - 4, 14,
                "\u00a76创建", () -> {
                    if (!tutorialPreviewMode) {
                        String pwd = passwordField != null ? passwordField.getValue().trim() : "";
                        NetworkManager.sendPartyCreate(pwd);
                        Minecraft.getInstance().setScreen(parent);
                    }
                })
                .setColors(0x00000000, 0x20303050, 0x30404060)
                .setBorderColor(0x00000000));

            int cbw = EspetroAuiWidgets.textButtonWidth("\u00a7c返回");
            root.addChild(EspetroAuiWidgets.button(px + pw / 2 - cbw / 2, py + 78, cbw, 14,
                "\u00a7c返回", () -> Minecraft.getInstance().setScreen(parent))
                .setColors(0x00000000, 0x20402020, 0x30502020)
                .setBorderColor(0x00000000));
        }

        @Override
        public boolean shouldCloseOnEsc() { return true; }
        @Override
        public boolean isPauseScreen() { return false; }
    }

    // ==================== 子界面：设置队伍密码 ====================

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

    // ==================== 子界面：加入队伍 ====================

    private static final class JoinPartyScreen extends EspetroMenuScreen {
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
        }

        @Override
        protected void renderBeforeMenu(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            EspetroAuiWidgets.drawScreenShade(graphics, this.width, this.height);
        }

        @Override
        protected void buildMenuRoot(GuiElement root) {
            int pw = Math.min(240, width - 40);
            int ph = 78;
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
                })
                .setColors(0x00000000, 0x20303050, 0x30404060)
                .setBorderColor(0x00000000));
            root.addChild(EspetroAuiWidgets.button(bx + bw / 2 + 4, py + 48, bw / 2 - 4, 14,
                "§c返回", () -> Minecraft.getInstance().setScreen(parent))
                .setColors(0x00000000, 0x20402020, 0x30502020)
                .setBorderColor(0x00000000));
        }

        @Override
        public boolean shouldCloseOnEsc() { return true; }
        @Override
        public boolean isPauseScreen() { return false; }
    }


}
