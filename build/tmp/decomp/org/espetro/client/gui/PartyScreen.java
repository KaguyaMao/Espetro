/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.client.gui;

import java.util.List;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.espetro.client.aui.GuiElement;
import org.espetro.client.gui.EspetroAuiWidgets;
import org.espetro.client.gui.EspetroMenuScreen;
import org.espetro.network.NetworkManager;
import org.espetro.network.PartyListPacket;

public final class PartyScreen
extends EspetroMenuScreen {
    private static PartyListPacket latest;
    private EspetroAuiWidgets.PhaseHeader phaseHeader;
    private int refreshTimer;

    public PartyScreen() {
        super(Component.m_237113_("\u7ec4\u961f\u5339\u914d"));
    }

    public static void update(PartyListPacket packet) {
        latest = packet;
        Minecraft mc = Minecraft.m_91087_();
        Screen screen = mc.f_91080_;
        if (screen instanceof PartyScreen) {
            PartyScreen screen2 = (PartyScreen)screen;
            screen2.rebuildMenuRoot();
        }
    }

    @Override
    protected void renderBeforeMenu(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        EspetroAuiWidgets.drawScreenShade(graphics, this.f_96543_, this.f_96544_);
    }

    @Override
    protected void buildMenuRoot(GuiElement root) {
        if (latest == null) {
            latest = new PartyListPacket(List.of(), 7, null, false);
        }
        int panelW = Math.min(400, this.f_96543_ - 20);
        int headerH = 46;
        this.phaseHeader = EspetroAuiWidgets.addMutablePhaseHeader(root, this.f_96543_, "\u00a7b\u00a7l\u7ec4\u961f\u5339\u914d", PartyScreen.latest.myPartyId == null ? "\u00a77\u4f60\u5c1a\u672a\u52a0\u5165\u4efb\u4f55\u961f\u4f0d" : "\u00a7a\u4f60\u5df2\u52a0\u5165\u961f\u4f0d", "\u00a77\u4e0a\u9650 \u00a7e" + PartyScreen.latest.maxPartySize + " \u4eba \u00a78| \u00a77\u6309 J \u952e\u547c\u51fa", -14490);
        int y = headerH + 4;
        int panelX = (this.f_96543_ - panelW) / 2;
        if (PartyScreen.latest.myPartyId == null) {
            int bw = EspetroAuiWidgets.textButtonWidth("\u00a7a+ \u521b\u5efa\u65b0\u961f\u4f0d");
            root.addChild(EspetroAuiWidgets.button(panelX + panelW / 2 - bw / 2, y, bw, 14, "\u00a7a+ \u521b\u5efa\u65b0\u961f\u4f0d", () -> {
                if (!this.tutorialPreviewMode) {
                    Minecraft.m_91087_().m_91152_(new CreatePartyScreen(this));
                }
            }).setColors(0, 539316272, 0x30306030).setBorderColor(0));
        } else {
            int bw1 = EspetroAuiWidgets.textButtonWidth("\u00a7c\u9000\u51fa\u961f\u4f0d");
            int bw2 = EspetroAuiWidgets.textButtonWidth(PartyScreen.latest.isOwner ? "\u00a7e\u7ba1\u7406\u961f\u4f0d" : "\u00a77\u7ba1\u7406\u961f\u4f0d");
            int rowW = bw1 + bw2 + 8;
            root.addChild(EspetroAuiWidgets.button(panelX + panelW / 2 - rowW / 2, y, bw1, 14, "\u00a7c\u9000\u51fa\u961f\u4f0d", () -> {
                if (!this.tutorialPreviewMode) {
                    NetworkManager.sendPartyLeave();
                }
            }).setColors(0, 0x20402020, 810557472).setBorderColor(0));
            if (PartyScreen.latest.isOwner) {
                root.addChild(EspetroAuiWidgets.button(panelX + panelW / 2 - rowW / 2 + bw1 + 8, y, bw2, 14, "\u00a7e\u7ba1\u7406\u961f\u4f0d", () -> {
                    if (!this.tutorialPreviewMode) {
                        Minecraft.m_91087_().m_91152_(new ManagePartyScreen(this, PartyScreen.latest.myPartyId));
                    }
                }).setColors(0, 0x20303020, 809512992).setBorderColor(0));
            }
        }
        root.addChild(EspetroAuiWidgets.rect(panelX + 8, y += 20, panelW - 16, 1, 0x30FFFFFF));
        y += 8;
        if (PartyScreen.latest.parties.isEmpty()) {
            root.addChild(EspetroAuiWidgets.centeredText(panelX, y + 20, panelW, "\u00a78\u6682\u65e0\u961f\u4f0d\uff0c\u70b9\u51fb\u4e0a\u65b9\u6309\u94ae\u521b\u5efa", -2828064));
        } else {
            int listW = panelW - 24;
            int entryH = 16;
            int listY = y;
            for (PartyListPacket.PartyInfo p : PartyScreen.latest.parties) {
                String lockIcon = p.locked ? "\u00a7c\ud83d\udd12 " : "";
                String pwIcon = p.hasPassword ? " \u00a77\ud83d\udd11" : "";
                boolean isMyParty = p.myPartyId != null && p.myPartyId.equals(PartyScreen.latest.myPartyId);
                String ownerText = "\u00a7f" + p.ownerName + " \u7684\u961f\u4f0d";
                String infoText = "\u00a77[" + p.memberCount + "/" + PartyScreen.latest.maxPartySize + "]";
                String fullText = lockIcon + ownerText + pwIcon + "  " + infoText;
                if (isMyParty) {
                    fullText = "\u00a7a\u00a7l\u25cf " + fullText;
                }
                String label = EspetroAuiWidgets.trimToWidth(fullText, listW - 50);
                root.addChild(EspetroAuiWidgets.text(panelX + 16, listY + 2, label, isMyParty ? 0xFFFFFF : -1));
                if (PartyScreen.latest.myPartyId == null && !p.locked) {
                    int jbw = EspetroAuiWidgets.textButtonWidth("\u52a0\u5165");
                    root.addChild(EspetroAuiWidgets.button(panelX + panelW - 30 - jbw, listY, jbw, 14, "\u00a7a\u52a0\u5165", () -> {
                        if (!this.tutorialPreviewMode) {
                            if (p.hasPassword) {
                                Minecraft.m_91087_().m_91152_(new JoinPartyScreen(this, p.partyId));
                            } else {
                                NetworkManager.sendPartyJoin(p.partyId, "");
                            }
                        }
                    }).setColors(0, 539313456, 0x30305530).setBorderColor(0));
                }
                listY += entryH;
            }
        }
    }

    @Override
    public void m_86600_() {
        super.m_86600_();
        ++this.refreshTimer;
        if (this.refreshTimer % 60 == 0) {
            NetworkManager.requestPartyList();
        }
    }

    @Override
    public void m_7379_() {
        super.m_7379_();
    }

    @Override
    public boolean m_6913_() {
        return true;
    }

    @Override
    public boolean m_7043_() {
        return false;
    }

    private static final class JoinPartyScreen
    extends EspetroMenuScreen {
        private final PartyScreen parent;
        private final UUID partyId;

        JoinPartyScreen(PartyScreen parent, UUID partyId) {
            super(Component.m_237113_("\u8f93\u5165\u5bc6\u7801"));
            this.parent = parent;
            this.partyId = partyId;
        }

        @Override
        protected void renderBeforeMenu(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            EspetroAuiWidgets.drawScreenShade(graphics, this.f_96543_, this.f_96544_);
        }

        @Override
        protected void buildMenuRoot(GuiElement root) {
            int pw = Math.min(240, this.f_96543_ - 40);
            int ph = 70;
            int px = (this.f_96543_ - pw) / 2;
            int py = (this.f_96544_ - ph) / 2;
            root.addChild(EspetroAuiWidgets.panel(px, py, pw, ph, 0, 0));
            root.addChild(EspetroAuiWidgets.centeredText(px, py + 8, pw, "\u00a7b\u8f93\u5165\u961f\u4f0d\u5bc6\u7801", -1));
            int bw = pw - 60;
            int bx = px + 30;
            root.addChild(EspetroAuiWidgets.button(bx, py + 30, bw / 2 - 4, 14, "\u00a7a\u786e\u8ba4\u52a0\u5165\uff081234\uff09", () -> {
                if (!this.tutorialPreviewMode) {
                    NetworkManager.sendPartyJoin(this.partyId, "1234");
                    Minecraft.m_91087_().m_91152_(this.parent);
                }
            }).setColors(0, 540029008, 809517152).setBorderColor(0));
            root.addChild(EspetroAuiWidgets.button(bx + bw / 2 + 4, py + 30, bw / 2 - 4, 14, "\u00a7c\u8fd4\u56de", () -> Minecraft.m_91087_().m_91152_(this.parent)).setColors(0, 0x20402020, 810557472).setBorderColor(0));
        }

        @Override
        public boolean m_6913_() {
            return true;
        }

        @Override
        public boolean m_7043_() {
            return false;
        }
    }

    private static final class ManagePartyScreen
    extends EspetroMenuScreen {
        private final PartyScreen parent;
        private final UUID partyId;

        ManagePartyScreen(PartyScreen parent, UUID partyId) {
            super(Component.m_237113_("\u7ba1\u7406\u961f\u4f0d"));
            this.parent = parent;
            this.partyId = partyId;
        }

        @Override
        protected void renderBeforeMenu(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            EspetroAuiWidgets.drawScreenShade(graphics, this.f_96543_, this.f_96544_);
        }

        @Override
        protected void buildMenuRoot(GuiElement root) {
            int pw = Math.min(300, this.f_96543_ - 40);
            int py = (this.f_96544_ - 110) / 2;
            int px = (this.f_96543_ - pw) / 2;
            int gap = 4;
            root.addChild(EspetroAuiWidgets.panel(px, py, pw, 110, 0, 0));
            root.addChild(EspetroAuiWidgets.centeredText(px, py + 6, pw, "\u00a7b\u7ba1\u7406\u961f\u4f0d", -1));
            PartyListPacket.PartyInfo myInfo = null;
            if (latest != null) {
                for (PartyListPacket.PartyInfo p : PartyScreen.latest.parties) {
                    if (!p.partyId.equals(this.partyId)) continue;
                    myInfo = p;
                    break;
                }
            }
            boolean isLocked = myInfo != null && myInfo.locked;
            int bw = pw - 40;
            int bx = px + 20;
            int by = py + 26;
            int bh = 14;
            root.addChild(EspetroAuiWidgets.button(bx, by, bw, bh, isLocked ? "\u00a7a\u89e3\u9501\u961f\u4f0d\uff08\u5141\u8bb8\u52a0\u5165\uff09" : "\u00a7c\u9501\u5b9a\u961f\u4f0d\uff08\u7981\u6b62\u52a0\u5165\uff09", () -> {
                if (!this.tutorialPreviewMode) {
                    NetworkManager.sendPartyToggleLock(this.partyId);
                    Minecraft.m_91087_().m_91152_(this.parent);
                }
            }).setColors(0, 540029008, 809517152).setBorderColor(0));
            root.addChild(EspetroAuiWidgets.button(bx, by += bh + gap, bw, bh, "\u00a7c\u89e3\u6563\u961f\u4f0d", () -> {
                if (!this.tutorialPreviewMode) {
                    NetworkManager.sendPartyDisband(this.partyId);
                    Minecraft.m_91087_().m_91152_(this.parent);
                }
            }).setColors(0, 0x20402020, 810557472).setBorderColor(0));
            int cbw = EspetroAuiWidgets.textButtonWidth("\u00a77\u8fd4\u56de");
            root.addChild(EspetroAuiWidgets.button(px + pw / 2 - cbw / 2, (by += bh + gap) + 8, cbw, 14, "\u00a77\u8fd4\u56de", () -> Minecraft.m_91087_().m_91152_(this.parent)).setColors(0, 0x20404040, 0x30505050).setBorderColor(0));
        }

        @Override
        public boolean m_6913_() {
            return true;
        }

        @Override
        public boolean m_7043_() {
            return false;
        }
    }

    private static final class CreatePartyScreen
    extends EspetroMenuScreen {
        private final PartyScreen parent;
        private EditBox passwordField;

        CreatePartyScreen(PartyScreen parent) {
            super(Component.m_237113_("\u521b\u5efa\u961f\u4f0d"));
            this.parent = parent;
        }

        @Override
        protected void m_7856_() {
            super.m_7856_();
            int pw = Math.min(280, this.f_96543_ - 40);
            int px = (this.f_96543_ - pw) / 2;
            int py = (this.f_96544_ - 100) / 2;
            int bw = pw - 60;
            int bx = px + 30;
            this.passwordField = new EditBox(Minecraft.m_91087_().f_91062_, bx, py + 42, bw, 14, Component.m_237113_("\u5bc6\u7801"));
            this.passwordField.m_94199_(64);
            this.passwordField.m_94182_(true);
            this.passwordField.m_94190_(true);
            this.m_142416_(this.passwordField);
        }

        @Override
        protected void renderBeforeMenu(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            EspetroAuiWidgets.drawScreenShade(graphics, this.f_96543_, this.f_96544_);
        }

        @Override
        protected void buildMenuRoot(GuiElement root) {
            int pw = Math.min(280, this.f_96543_ - 40);
            int ph = 100;
            int px = (this.f_96543_ - pw) / 2;
            int py = (this.f_96544_ - ph) / 2;
            root.addChild(EspetroAuiWidgets.panel(px, py, pw, ph, 0, 0));
            root.addChild(EspetroAuiWidgets.centeredText(px, py + 8, pw, "\u00a7b\u521b\u5efa\u65b0\u961f\u4f0d", -1));
            root.addChild(EspetroAuiWidgets.centeredText(px, py + 24, pw, "\u00a77\u8f93\u5165\u5bc6\u7801\uff08\u7559\u7a7a\u5219\u4e0d\u8bbe\u5bc6\u7801\uff09", -2828064));
            int bw = pw - 60;
            int bx = px + 30;
            root.addChild(EspetroAuiWidgets.button(bx, py + 60, bw / 2 - 4, 14, "\u00a7a\u521b\u5efa\uff08\u65e0\u5bc6\u7801\uff09", () -> {
                if (!this.tutorialPreviewMode) {
                    NetworkManager.sendPartyCreate("");
                    Minecraft.m_91087_().m_91152_(this.parent);
                }
            }).setColors(0, 540029008, 809517152).setBorderColor(0));
            root.addChild(EspetroAuiWidgets.button(bx + bw / 2 + 4, py + 60, bw / 2 - 4, 14, "\u00a76\u521b\u5efa", () -> {
                if (!this.tutorialPreviewMode) {
                    String pwd = this.passwordField != null ? this.passwordField.m_94155_().trim() : "";
                    NetworkManager.sendPartyCreate(pwd);
                    Minecraft.m_91087_().m_91152_(this.parent);
                }
            }).setColors(0, 540029008, 809517152).setBorderColor(0));
            int cbw = EspetroAuiWidgets.textButtonWidth("\u00a7c\u8fd4\u56de");
            root.addChild(EspetroAuiWidgets.button(px + pw / 2 - cbw / 2, py + 78, cbw, 14, "\u00a7c\u8fd4\u56de", () -> Minecraft.m_91087_().m_91152_(this.parent)).setColors(0, 0x20402020, 810557472).setBorderColor(0));
        }

        @Override
        public boolean m_6913_() {
            return true;
        }

        @Override
        public boolean m_7043_() {
            return false;
        }
    }
}

