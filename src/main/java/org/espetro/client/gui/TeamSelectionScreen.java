package org.espetro.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.espetro.client.aui.GuiElement;
import net.minecraft.client.gui.GuiGraphics;
import org.espetro.network.TeamSelectStatePacket;

/**
 * 攻守方选择界面
 * 展示攻击方/防守方编制图片，图片下方显示名称标签。
 * 中途加入时若服务端已发送编制选择图则优先使用；否则回退到默认攻/防图片。
 */
public class TeamSelectionScreen extends EspetroMenuScreen {
    private static int attackCount;
    private static int defendCount;
    private static int remainingSeconds = 60;
    private static long receivedAtMs;
    private static boolean selectionActive;
    /** 本机当前已选队伍："ATTACK" / "DEFEND" / null。 */
    private static String myTeam;
    /** 因平衡被锁定的队伍："ATTACK" / "DEFEND" / null。 */
    private static String lockedTeam;
    /** 攻击方编制选择图 ResourceLocation 字符串（服务端下发）。 */
    private static String attackFactionImage;
    /** 防守方编制选择图 ResourceLocation 字符串（服务端下发）。 */
    private static String defendFactionImage;

    private static final ResourceLocation DEFAULT_ATTACK =
        ResourceLocation.fromNamespaceAndPath("espetro", "textures/gui/attack_faction.png");
    private static final ResourceLocation DEFAULT_DEFEND =
        ResourceLocation.fromNamespaceAndPath("espetro", "textures/gui/defend_faction.png");

    // 16:9 横向图片显示尺寸
    private static final int IMG_W = 192;
    private static final int IMG_H = 108;
    // 图片间距
    private static final int IMG_GAP = 48;
    // 纹理原始尺寸（128x128 含透明留白，避免边缘黑边）
    private static final int TEX_W = 128, TEX_H = 128;

    /**
     * 固定顶栏几何：避免依赖 this.font.lineHeight（缩放/字库变化时会改 headerH，
     * 导致整页按钮纵坐标每秒抖动）。
     */
    private static final int HEADER_TITLE_Y = 6;
    private static final int HEADER_STATUS_Y = 18;
    private static final int HEADER_DETAIL_Y = 30;
    private static final int HEADER_COUNT_Y = 42;
    private static final int HEADER_TIMER_Y = 54;
    private static final int HEADER_H = 66;

    private TeamFactionImageButton attackButton;
    private TeamFactionImageButton defendButton;
    /** 倒计时状态胶囊（选中阶段才显示，橙色/红色警示）。 */
    private EspetroAuiWidgets.Chip timerChip;

    public TeamSelectionScreen() {
        super(Component.literal("选择队伍"));
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
        // 同步到 ClientGameState，避免本地与权威状态漂移。
        if (packet.myTeam != null && !packet.myTeam.isBlank()) {
            ClientGameState.setPlayerTeam(packet.myTeam);
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.screen instanceof TeamSelectionScreen screen) {
            // 编制图片变了 → 重建按钮
            if (!eq(oldAtkImg, attackFactionImage) || !eq(oldDefImg, defendFactionImage)) {
                screen.rebuildMenuRoot();
            } else {
                // 只刷新选中边框
                screen.refreshSelectionBorders();
            }
        }
    }

    private static boolean eq(String a, String b) {
        if (a == null) return b == null;
        return a.equals(b);
    }

    @Override
    public void onClose() {
        // 由服务端在选定队伍或阶段推进后替换界面。
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    protected void renderBeforeMenu(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        EspetroAuiWidgets.drawScreenShade(graphics, this.width, this.height);
    }

    @Override
    protected void buildMenuRoot(GuiElement root) {
        // 全屏底由 renderBeforeMenu 绘制；此处为标题/状态胶囊 + 阵营图片 + 装饰卡片。
        // 计数与倒计时按帧刷新，不放在元素树里，避免每秒重建。
        root.addChild(EspetroAuiWidgets.centeredText(
            6, HEADER_TITLE_Y, Math.max(1, this.width - 12),
            "队伍选择", UiTheme.GOLD)
            .setTextScale(UiTheme.TYPE_TITLE)
            .setShadow(true));

        String attackName = EspetroAuiWidgets.teamName("ATTACK");
        String defendName = EspetroAuiWidgets.teamName("DEFEND");
        EspetroAuiWidgets.Chip statusChip = EspetroAuiWidgets.chip(0, 0,
            "请选择 " + attackName + " 或 " + defendName + " 加入战斗", UiTheme.ACCENT);
        statusChip.setX((this.width - statusChip.getWidth()) / 2);
        statusChip.setY(HEADER_STATUS_Y - 1);
        root.addChild(statusChip);

        root.addChild(EspetroAuiWidgets.centeredText(
            6, HEADER_DETAIL_Y + 3, Math.max(1, this.width - 12),
            "选择后将进入编制投票阶段", UiTheme.TEXT_THIRD)
            .setTextScale(UiTheme.TYPE_MICRO));

        // 保持双列16:9，在常见GUI缩放下缩小图片，避免固定432px内容越过屏幕边缘。
        int imageGap = Math.min(IMG_GAP, Math.max(UiTheme.GAP_L, this.width / 12));
        int imageW = Math.max(1, Math.min(IMG_W,
            (this.width - UiTheme.GAP_L * 4 - imageGap) / 2));
        int imageH = Math.max(1, imageW * IMG_H / IMG_W);
        int contentW = imageW * 2 + imageGap;
        int panelW = Math.min(540, Math.max(contentW + 40, this.width - 36));

        int panelH = imageH + 70;
        int panelX = (this.width - panelW) / 2;
        int panelY = HEADER_H + Math.max(8, (this.height - HEADER_H - panelH) / 2);

        int totalImgW = imageW * 2 + imageGap;
        int imgsStartX = panelX + (panelW - totalImgW) / 2;
        int imgY = panelY + 6;

        // 装饰性卡片：垫在图片/标签/观战按钮之下，不拦截点击。
        int cardX = panelX;
        int cardY = imgY - 6;
        int cardW = panelW;
        int cardH = imageH + 50;
        root.addChild(EspetroAuiWidgets.card(cardX, cardY, cardW, cardH));

        ResourceLocation atkTex = resolveTexture(attackFactionImage, DEFAULT_ATTACK);
        ResourceLocation defTex = resolveTexture(defendFactionImage, DEFAULT_DEFEND);

        // 攻击方图片（左侧，16:9 横向）
        int attackImgX = imgsStartX;
        attackButton = new TeamFactionImageButton(attackImgX, imgY, imageW, imageH, TEX_W, TEX_H, atkTex,
            () -> { if (!"ATTACK".equals(lockedTeam)) TeamSelectionGui.selectTeam("ATTACK"); },
            UiTheme.ATTACK, UiTheme.ACCENT_LINE);
        root.addChild(attackButton);

        // 防守方图片（右侧，16:9 横向）
        int defendImgX = imgsStartX + imageW + imageGap;
        defendButton = new TeamFactionImageButton(defendImgX, imgY, imageW, imageH, TEX_W, TEX_H, defTex,
            () -> { if (!"DEFEND".equals(lockedTeam)) TeamSelectionGui.selectTeam("DEFEND"); },
            UiTheme.DEFEND, UiTheme.ACCENT_LINE);
        root.addChild(defendButton);

        // 图片下方的文字标签
        int labelY = imgY + imageH + 6;
        root.addChild(EspetroAuiWidgets.centeredText(attackImgX, labelY, imageW,
            attackName, UiTheme.ATTACK)
            .setTextScale(UiTheme.TYPE_HEADING)
            .setShadow(true));
        root.addChild(EspetroAuiWidgets.centeredText(defendImgX, labelY, imageW,
            defendName, UiTheme.DEFEND)
            .setTextScale(UiTheme.TYPE_HEADING)
            .setShadow(true));

        // 中途加入者可以直接选择观战（不再选边），由服务端设为观察者。
        int spectateW = 120;
        int spectateX = panelX + (panelW - spectateW) / 2;
        int spectateY = labelY + 16;
        root.addChild(EspetroAuiWidgets.button(spectateX, spectateY, spectateW, 20,
            "进入观战", () -> {
                org.espetro.network.NetworkManager.sendSpectateRequest();
                net.minecraft.client.Minecraft.getInstance().setScreen(null);
            }));

        timerChip = EspetroAuiWidgets.chip(0, HEADER_TIMER_Y - 2, "", UiTheme.WARNING);
        root.addChild(timerChip);
        refreshTimerChip();

        refreshSelectionBorders();
    }

    /** 按剩余时间刷新倒计时胶囊：正常橙色、≤10 秒红色；未开启选择时隐藏。 */
    private void refreshTimerChip() {
        if (timerChip == null) return;
        timerChip.setVisible(selectionActive);
        if (!selectionActive) return;
        int elapsed = (int) ((System.currentTimeMillis() - receivedAtMs) / 1000L);
        int left = Math.max(0, remainingSeconds - elapsed);
        timerChip.setLabel("剩余 " + left + " 秒 · 可重新选择");
        timerChip.setAccent(left <= 10 ? UiTheme.NEGATIVE : UiTheme.WARNING);
        timerChip.setX((this.width - timerChip.getWidth()) / 2);
    }

    @Override
    public void tick() {
        super.tick();
        refreshTimerChip();
    }

    private static ResourceLocation resolveTexture(String fullPath, ResourceLocation fallback) {
        if (fullPath == null || fullPath.isEmpty()) return fallback;
        String trimmed = fullPath.trim();
        // 1. 尝试 ResourceLocation 格式（espetro:textures/...）且资源确实存在，
        //    避免客户端资源缺失时直接 blit 显示紫黑块。
        ResourceLocation rl = ResourceLocation.tryParse(trimmed);
        if (rl != null) {
            try {
                if (Minecraft.getInstance().getResourceManager()
                        .getResource(rl).isPresent()) {
                    return rl;
                }
            } catch (Exception ignored) {
                // fall through
            }
        }
        // 2. 尝试客户端本地 EsFactions/ 目录下的图片文件（单机/局域网备用）。
        Minecraft mc = Minecraft.getInstance();
        if (mc != null) {
            java.nio.file.Path imagePath =
                FactionSelectionImageResolver.resolveClientFile(mc.gameDirectory.toPath(), trimmed);
            if (imagePath != null) {
                ResourceLocation dynamic = tryRegisterLocalImage(imagePath, trimmed);
                if (dynamic != null) return dynamic;
            }
        }
        // 3. 回退默认图。
        return fallback;
    }

    /** 读取客户端本地图片并注册动态纹理；失败返回 null。 */
    private static ResourceLocation tryRegisterLocalImage(java.nio.file.Path imagePath, String key) {
        try (java.io.InputStream in = java.nio.file.Files.newInputStream(imagePath)) {
            com.mojang.blaze3d.platform.NativeImage image =
                com.mojang.blaze3d.platform.NativeImage.read(in);
            net.minecraft.client.renderer.texture.DynamicTexture texture =
                new net.minecraft.client.renderer.texture.DynamicTexture(image);
            ResourceLocation location = ResourceLocation.fromNamespaceAndPath(
                "espetro", "dynamic/team_select_" + Integer.toHexString(key.hashCode()));
            Minecraft.getInstance().getTextureManager().register(location, texture);
            return location;
        } catch (Exception ignored) {
            return null;
        }
    }

    /** 按当前 myTeam 和 lockedTeam 刷新两侧图片的边框。 */
    private void refreshSelectionBorders() {
        String team = resolveMyTeam();
        if (attackButton != null) {
            attackButton.setSelectedBorderColor("ATTACK".equals(team) ? UiTheme.ACCENT_LINE : 0);
            if ("ATTACK".equals(lockedTeam)) {
                attackButton.setBorderColor(UiTheme.ATTACK);
                attackButton.setHoverBorderColor(UiTheme.ATTACK);
            } else {
                attackButton.setBorderColor(UiTheme.HAIRLINE);
                attackButton.setHoverBorderColor(UiTheme.ACCENT_LINE);
            }
        }
        if (defendButton != null) {
            defendButton.setSelectedBorderColor("DEFEND".equals(team) ? UiTheme.ACCENT_LINE : 0);
            if ("DEFEND".equals(lockedTeam)) {
                defendButton.setBorderColor(UiTheme.DEFEND);
                defendButton.setHoverBorderColor(UiTheme.DEFEND);
            } else {
                defendButton.setBorderColor(UiTheme.HAIRLINE);
                defendButton.setHoverBorderColor(UiTheme.ACCENT_LINE);
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
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    protected void renderAfterMenu(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // 队伍人数：名称常态 + 数字放大（TYPE_NUMERAL），手动居中保证整体对称。
        String attackName = EspetroAuiWidgets.teamName("ATTACK");
        String defendName = EspetroAuiWidgets.teamName("DEFEND");
        String attackValue = String.valueOf(attackCount);
        String defendValue = String.valueOf(defendCount);
        String lockText = lockedTeam == null ? ""
            : "· " + EspetroAuiWidgets.teamName(lockedTeam) + " 已锁定";
        float nameScale = 0.95f;
        float lockScale = 0.85f;
        int attackNameW = (int) (this.font.width(attackName) * nameScale);
        int defendNameW = (int) (this.font.width(defendName) * nameScale);
        int attackValueW = (int) (this.font.width(attackValue) * UiTheme.TYPE_NUMERAL);
        int defendValueW = (int) (this.font.width(defendValue) * UiTheme.TYPE_NUMERAL);
        int separatorW = Math.max(10, this.font.width("|") + 14);
        int lockW = lockText.isEmpty() ? 0 : (int) (this.font.width(lockText) * lockScale) + 10;
        int totalW = attackNameW + 4 + attackValueW + separatorW
            + defendNameW + 4 + defendValueW + lockW;
        int x = (this.width - totalW) / 2;

        EspetroAuiWidgets.drawScaledString(graphics, attackName, x, HEADER_COUNT_Y + 1,
            UiTheme.ATTACK, nameScale, true);
        x += attackNameW + 4;
        EspetroAuiWidgets.drawScaledString(graphics, attackValue, x, HEADER_COUNT_Y - 4,
            UiTheme.TEXT, UiTheme.TYPE_NUMERAL, true);
        x += attackValueW;
        int sepX = x + (separatorW - this.font.width("|")) / 2;
        EspetroAuiWidgets.drawScaledString(graphics, "|", sepX, HEADER_COUNT_Y + 1,
            UiTheme.TEXT_THIRD, 1.0f, false);
        x += separatorW;
        EspetroAuiWidgets.drawScaledString(graphics, defendName, x, HEADER_COUNT_Y + 1,
            UiTheme.DEFEND, nameScale, true);
        x += defendNameW + 4;
        EspetroAuiWidgets.drawScaledString(graphics, defendValue, x, HEADER_COUNT_Y - 4,
            UiTheme.TEXT, UiTheme.TYPE_NUMERAL, true);
        x += defendValueW;
        if (!lockText.isEmpty()) {
            EspetroAuiWidgets.drawScaledString(graphics, lockText, x + 10, HEADER_COUNT_Y + 1,
                UiTheme.NEGATIVE, lockScale, true);
        }
    }

    public static void open() {
        Minecraft mc = Minecraft.getInstance();
        if (!(mc.screen instanceof TeamSelectionScreen)) {
            mc.setScreen(new TeamSelectionScreen());
        }
    }

    /** 点击后立即写入本地选中态（服务端广播到达前）。 */
    static void markLocalSelection(String team) {
        myTeam = team;
    }

    void refreshSelectionBordersPublic() {
        refreshSelectionBorders();
    }

    /**
     * 可点击的阵营图片按钮。
     * 渲染时从纹理中裁剪中心 16:9 区域填满 IMG_W×IMG_H，支持选中/悬浮边框。
     */
    private static final class TeamFactionImageButton extends GuiElement {
        private final ResourceLocation texture;
        private final int texW, texH;
        private final int dispW, dispH;
        private final Runnable action;
        private int borderColor;
        private int hoverBorderColor;
        private int selectedBorderColor;

        TeamFactionImageButton(int x, int y, int dispW, int dispH,
                               int texW, int texH, ResourceLocation texture,
                               Runnable action, int borderColor, int hoverBorderColor) {
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

        void setBorderColor(int c) { this.borderColor = c; }
        void setHoverBorderColor(int c) { this.hoverBorderColor = c; }
        void setSelectedBorderColor(int c) { this.selectedBorderColor = c; }

        @Override
        public boolean onMouseClick(int mouseX, int mouseY, int button) {
            if (button == 0 && hasFocus() && action != null) {
                action.run();
                return true;
            }
            return false;
        }

        @Override
        public void draw(GuiGraphics graphics, int refX, int refY, int screenWidth,
                         int screenHeight, int mouseX, int mouseY, float opacity) {
            if (!isVisible() || texture == null) return;
            int bx = refX + getX();
            int by = refY + getY();

            // 图片外圈：直角槽位、细线角标；黄色只标记交互和当前选择。
            int frameX = bx - 3;
            int frameY = by - 3;
            int frameW = dispW + 6;
            int frameH = dispH + 6;
            UiPaint.sunken(graphics, frameX, frameY, frameW, frameH, UiTheme.RADIUS_CARD);
            graphics.blit(texture, bx, by, dispW, dispH, 0, 0, texW, texH, texW, texH);

            boolean selected = UiTheme.visible(selectedBorderColor);
            int outColor = selected ? selectedBorderColor
                : hasFocus() ? hoverBorderColor : borderColor;
            if (UiTheme.visible(outColor)) {
                if (selected) {
                    UiPaint.accentBarBottom(graphics, frameX, frameY + frameH - 2,
                        frameW, UiTheme.ACCENT);
                    UiPaint.brackets(graphics, frameX - 2, frameY - 2, frameW + 4,
                        frameH + 4, UiTheme.ACCENT_LINE);
                }
                UiPaint.strokeRoundRect(graphics, frameX, frameY, frameW, frameH,
                    UiTheme.RADIUS_CARD, outColor);
            }
        }
    }
}
