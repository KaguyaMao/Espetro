/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.espetro.client.aui.GuiElement;
import org.espetro.client.gui.ClientGameState;
import org.espetro.client.gui.EspetroAuiWidgets;
import org.espetro.client.gui.EspetroMenuScreen;
import org.espetro.client.gui.TeamSelectionGui;
import org.espetro.network.TeamSelectStatePacket;

public class TeamSelectionScreen
extends EspetroMenuScreen {
    private static int attackCount;
    private static int defendCount;
    private static int remainingSeconds;
    private static long receivedAtMs;
    private static boolean selectionActive;
    private static String myTeam;
    private static String lockedTeam;
    private static String attackFactionImage;
    private static String defendFactionImage;
    private static final ResourceLocation DEFAULT_ATTACK;
    private static final ResourceLocation DEFAULT_DEFEND;
    private static final int IMG_W = 192;
    private static final int IMG_H = 108;
    private static final int IMG_GAP = 48;
    private static final int TEX_W = 128;
    private static final int TEX_H = 128;
    private static final int ATTACK_BORDER = -41386;
    private static final int DEFEND_BORDER = -10514945;
    private static final int ATTACK_HOVER = -30076;
    private static final int DEFEND_HOVER = -7688705;
    private static final int HEADER_TITLE_Y = 6;
    private static final int HEADER_STATUS_Y = 18;
    private static final int HEADER_DETAIL_Y = 30;
    private static final int HEADER_COUNT_Y = 42;
    private static final int HEADER_TIMER_Y = 54;
    private static final int HEADER_H = 66;
    private TeamFactionImageButton attackButton;
    private TeamFactionImageButton defendButton;

    public TeamSelectionScreen() {
        super(Component.m_237113_("\u9009\u62e9\u961f\u4f0d"));
    }

    public static void updateTeamState(TeamSelectStatePacket packet) {
        int oldAtk = attackCount;
        int oldDef = defendCount;
        String oldAtkImg = attackFactionImage;
        String oldDefImg = defendFactionImage;
        attackCount = packet.attackCount;
        defendCount = packet.defendCount;
        remainingSeconds = packet.remainingSeconds;
        receivedAtMs = System.currentTimeMillis();
        selectionActive = packet.active;
        myTeam = packet.myTeam;
        lockedTeam = packet.lockedTeam;
        attackFactionImage = packet.attackFactionImage;
        defendFactionImage = packet.defendFactionImage;
        if (packet.myTeam != null && !packet.myTeam.isBlank()) {
            ClientGameState.setPlayerTeam(packet.myTeam);
        }
        Minecraft mc = Minecraft.m_91087_();
        Screen screen = mc.f_91080_;
        if (screen instanceof TeamSelectionScreen) {
            TeamSelectionScreen screen2 = (TeamSelectionScreen)screen;
            if (!TeamSelectionScreen.eq(oldAtkImg, attackFactionImage) || !TeamSelectionScreen.eq(oldDefImg, defendFactionImage)) {
                screen2.rebuildMenuRoot();
            } else {
                screen2.refreshSelectionBorders();
            }
        }
    }

    private static boolean eq(String a, String b) {
        if (a == null) {
            return b == null;
        }
        return a.equals(b);
    }

    @Override
    public void m_7379_() {
    }

    @Override
    public boolean m_6913_() {
        return false;
    }

    @Override
    protected void renderBeforeMenu(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        EspetroAuiWidgets.drawScreenShade(graphics, this.f_96543_, this.f_96544_);
    }

    @Override
    protected void buildMenuRoot(GuiElement root) {
        root.addChild(EspetroAuiWidgets.centeredText(6, 6, Math.max(1, this.f_96543_ - 12), "\u00a76\u00a7l\u961f\u4f0d\u9009\u62e9", -1));
        String attackName = EspetroAuiWidgets.teamName("ATTACK");
        String defendName = EspetroAuiWidgets.teamName("DEFEND");
        root.addChild(EspetroAuiWidgets.centeredText(6, 18, Math.max(1, this.f_96543_ - 12), "\u00a7f\u8bf7\u9009\u62e9" + attackName + "\u6216" + defendName + "\u52a0\u5165\u6218\u6597", -2828064));
        root.addChild(EspetroAuiWidgets.centeredText(6, 30, Math.max(1, this.f_96543_ - 12), "\u00a78\u9009\u62e9\u540e\u5c06\u8fdb\u5165\u7f16\u5236\u6295\u7968\u9636\u6bb5", -5327681));
        int contentW = 432;
        int panelW = Math.min(540, Math.max(contentW + 40, this.f_96543_ - 36));
        int panelH = 134;
        int panelX = (this.f_96543_ - panelW) / 2;
        int panelY = 66 + Math.max(8, (this.f_96544_ - 66 - panelH) / 2);
        int totalImgW = 432;
        int imgsStartX = panelX + (panelW - totalImgW) / 2;
        int imgY = panelY + 2;
        ResourceLocation atkTex = TeamSelectionScreen.resolveTexture(attackFactionImage, DEFAULT_ATTACK);
        ResourceLocation defTex = TeamSelectionScreen.resolveTexture(defendFactionImage, DEFAULT_DEFEND);
        int attackImgX = imgsStartX;
        this.attackButton = new TeamFactionImageButton(attackImgX, imgY, 192, 108, 128, 128, atkTex, () -> {
            if (!"ATTACK".equals(lockedTeam)) {
                TeamSelectionGui.selectTeam("ATTACK");
            }
        }, -41386, -30076);
        root.addChild(this.attackButton);
        int defendImgX = imgsStartX + 192 + 48;
        this.defendButton = new TeamFactionImageButton(defendImgX, imgY, 192, 108, 128, 128, defTex, () -> {
            if (!"DEFEND".equals(lockedTeam)) {
                TeamSelectionGui.selectTeam("DEFEND");
            }
        }, -10514945, -7688705);
        root.addChild(this.defendButton);
        int labelY = imgY + 108 + 6;
        root.addChild(EspetroAuiWidgets.centeredText(attackImgX, labelY, 192, EspetroAuiWidgets.teamPrefix("ATTACK") + "\u00a7l" + attackName, -41386));
        root.addChild(EspetroAuiWidgets.centeredText(defendImgX, labelY, 192, EspetroAuiWidgets.teamPrefix("DEFEND") + "\u00a7l" + defendName, -10514945));
        this.refreshSelectionBorders();
    }

    private static ResourceLocation resolveTexture(String fullPath, ResourceLocation fallback) {
        if (fullPath == null || fullPath.isEmpty()) {
            return fallback;
        }
        int colon = fullPath.indexOf(58);
        if (colon > 0) {
            return ResourceLocation.fromNamespaceAndPath((String)fullPath.substring(0, colon), (String)fullPath.substring(colon + 1));
        }
        ResourceLocation rl = ResourceLocation.m_135820_(fullPath);
        return rl != null ? rl : fallback;
    }

    private void refreshSelectionBorders() {
        String team = TeamSelectionScreen.resolveMyTeam();
        if (this.attackButton != null) {
            this.attackButton.setSelectedBorderColor("ATTACK".equals(team) ? -41386 : 0);
            if ("ATTACK".equals(lockedTeam)) {
                this.attackButton.setBorderColor(-41386);
                this.attackButton.setHoverBorderColor(-41386);
            } else {
                this.attackButton.setBorderColor(0);
                this.attackButton.setHoverBorderColor(-30076);
            }
        }
        if (this.defendButton != null) {
            this.defendButton.setSelectedBorderColor("DEFEND".equals(team) ? -10514945 : 0);
            if ("DEFEND".equals(lockedTeam)) {
                this.defendButton.setBorderColor(-10514945);
                this.defendButton.setHoverBorderColor(-10514945);
            } else {
                this.defendButton.setBorderColor(0);
                this.defendButton.setHoverBorderColor(-7688705);
            }
        }
    }

    private static String resolveMyTeam() {
        if (myTeam != null && !myTeam.isBlank()) {
            return myTeam;
        }
        String local = ClientGameState.getPlayerTeam();
        return local == null || local.isBlank() ? null : local;
    }

    @Override
    public boolean m_7043_() {
        return false;
    }

    @Override
    protected void renderAfterMenu(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int elapsed = (int)((System.currentTimeMillis() - receivedAtMs) / 1000L);
        int left = Math.max(0, remainingSeconds - elapsed);
        String attackName = EspetroAuiWidgets.teamName("ATTACK");
        String defendName = EspetroAuiWidgets.teamName("DEFEND");
        String countLine = EspetroAuiWidgets.teamPrefix("ATTACK") + attackName + " \u00a7f" + attackCount + "   \u00a77|   " + EspetroAuiWidgets.teamPrefix("DEFEND") + defendName + " \u00a7f" + defendCount;
        if (lockedTeam != null) {
            countLine = countLine + "   \u00a7c\ud83d\udd12 " + EspetroAuiWidgets.teamName(lockedTeam) + "\u5df2\u9501\u5b9a";
        }
        graphics.m_280137_(this.f_96547_, countLine, this.f_96543_ / 2, 42, 0xFFFFFF);
        if (selectionActive) {
            graphics.m_280137_(this.f_96547_, "\u00a7e\u5269\u4f59 " + left + " \u79d2\uff0c\u53ef\u91cd\u65b0\u9009\u62e9", this.f_96543_ / 2, 54, 0xFFFFFF);
        }
    }

    public static void open() {
        Minecraft mc = Minecraft.m_91087_();
        if (!(mc.f_91080_ instanceof TeamSelectionScreen)) {
            mc.m_91152_(new TeamSelectionScreen());
        }
    }

    static void markLocalSelection(String team) {
        myTeam = team;
    }

    void refreshSelectionBordersPublic() {
        this.refreshSelectionBorders();
    }

    static {
        remainingSeconds = 60;
        DEFAULT_ATTACK = ResourceLocation.fromNamespaceAndPath((String)"espetro", (String)"textures/gui/attack_faction.png");
        DEFAULT_DEFEND = ResourceLocation.fromNamespaceAndPath((String)"espetro", (String)"textures/gui/defend_faction.png");
    }

    private static final class TeamFactionImageButton
    extends GuiElement {
        private final ResourceLocation texture;
        private final int texW;
        private final int texH;
        private final int dispW;
        private final int dispH;
        private final Runnable action;
        private int borderColor;
        private int hoverBorderColor;
        private int selectedBorderColor;

        TeamFactionImageButton(int x, int y, int dispW, int dispH, int texW, int texH, ResourceLocation texture, Runnable action, int borderColor, int hoverBorderColor) {
            super(x, y, dispW, dispH);
            this.dispW = dispW;
            this.dispH = dispH;
            this.texW = texW;
            this.texH = texH;
            this.texture = texture;
            this.action = action;
            this.borderColor = borderColor;
            this.hoverBorderColor = hoverBorderColor;
            this.selectedBorderColor = 0;
        }

        void setBorderColor(int c) {
            this.borderColor = c;
        }

        void setHoverBorderColor(int c) {
            this.hoverBorderColor = c;
        }

        void setSelectedBorderColor(int c) {
            this.selectedBorderColor = c;
        }

        @Override
        public boolean onMouseClick(int mouseX, int mouseY, int button) {
            if (button == 0 && this.hasFocus() && this.action != null) {
                this.action.run();
                return true;
            }
            return false;
        }

        @Override
        public void draw(GuiGraphics graphics, int refX, int refY, int screenWidth, int screenHeight, int mouseX, int mouseY, float opacity) {
            int outColor;
            if (!this.isVisible() || this.texture == null) {
                return;
            }
            int bx = refX + this.getX();
            int by = refY + this.getY();
            graphics.m_280411_(this.texture, bx, by, this.dispW, this.dispH, 0.0f, 0.0f, this.texW, this.texH, this.texW, this.texH);
            int n = this.selectedBorderColor != 0 ? this.selectedBorderColor : (outColor = this.hasFocus() ? this.hoverBorderColor : this.borderColor);
            if (outColor != 0) {
                graphics.m_280637_(bx, by, this.dispW, this.dispH, outColor);
            }
        }
    }
}

