package org.espetro.client.gui;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.espetro.network.ClassSelectScreenPacket;
import org.espetro.network.NetworkManager;
import org.espetro.client.aui.GuiElement;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 编制选择界面
 * 当前阵营全体玩家投票选择队伍编制。
 */
public class ClassSelectScreen extends EspetroMenuScreen {

    /** 最底部一行同时放名称与票数，图片区尽量放大（票数为大号数字，行高略增）。 */
    private static final int CARD_FOOTER_PAD = 3;
    private static final int CARD_NAME_LINE = 13;
    private static final int CARD_FOOTER_H = CARD_FOOTER_PAD + CARD_NAME_LINE;
    /** 约 12 逻辑像素观感的小字（相对默认字号缩小）。 */
    private static final float CARD_NAME_SCALE = 0.85f;

    private String team;
    private boolean isCommander;
    private List<ClassSelectScreenPacket.FactionInfo> factions;
    private int timeRemaining;
    private String opponentTeamName;
    private String opponentFaction;
    private int opponentTimeRemaining;

    private String lastSelectedFaction = null;
    private int scrollOffset = 0;
    private int maxScrollOffset = 0;
    private int scrollStep = 1;
    /** 当前展开「?」详情面板的编制 id；null 表示关闭。 */
    private String openInfoId;
    private EspetroAuiWidgets.PhaseHeader phaseHeader;
    private final Map<String, FactionCardButton> factionCards = new HashMap<>();

    public ClassSelectScreen(String team, boolean isCommander, List<ClassSelectScreenPacket.FactionInfo> factions,
                              int timeRemaining, String opponentTeamName, String opponentFaction,
                              int opponentTimeRemaining, String selectedFactionId) {
        super(Component.literal("编制选择"));
        this.team = team;
        this.isCommander = isCommander;
        this.factions = factions == null ? new ArrayList<>() : new ArrayList<>(factions);
        this.timeRemaining = timeRemaining;
        this.opponentTeamName = opponentTeamName;
        this.opponentFaction = opponentFaction;
        this.opponentTimeRemaining = opponentTimeRemaining;
        this.lastSelectedFaction = selectedFactionId == null || selectedFactionId.isEmpty()
            ? null : selectedFactionId;
    }

    @Override
    protected void renderBeforeMenu(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        CurrentMapBackgroundRenderer.render(
            graphics, this.width, this.height, ClientGameState.getCurrentMapFolder());
    }

    @Override
    protected void buildMenuRoot(GuiElement root) {
        factionCards.clear();
        int count = factions == null ? 0 : factions.size();
        // 编制候选池固定为六个：始终按 Squad 风格的 3 列 × 2 行展示。
        int columns = 3;
        int visibleRows = 2;
        int cardGap = 6;
        int panelW = Math.min(this.width - 16,
            24 + columns * 360 + (columns - 1) * cardGap);
        int cardW = (panelW - 24 - (columns - 1) * cardGap) / columns;
        int startY = EspetroAuiWidgets.PHASE_HEADER_HEIGHT + 8;
        int availableH = Math.max(2 * 67 + cardGap, this.height - startY - 18);
        int maxCardH = Math.max(67, (availableH - cardGap) / visibleRows);
        // 图片画框固定 16:9 横向：先按卡片宽计算高度，放不下时再按高度反推宽度
        int imageW = Math.max(1, cardW - 6);
        int imageH = Math.max(1, imageW * 9 / 16);
        int maxImageSlotH = Math.max(36, maxCardH - CARD_FOOTER_H);
        int imageSlotH = imageH + 6;
        if (imageSlotH > maxImageSlotH) {
            imageSlotH = maxImageSlotH;
            imageH = Math.max(1, imageSlotH - 6);
            imageW = Math.max(1, imageH * 16 / 9);
        }
        int cardH = imageSlotH + CARD_FOOTER_H;
        int panelX = (this.width - panelW) / 2;

        boolean selectingOpen = timeRemaining > 0;
        phaseHeader = EspetroAuiWidgets.addMutablePhaseHeader(root, this.width,
            "编制投票 | " + EspetroAuiWidgets.teamName(team) + " [全员投票]",
            buildTimeText(), "", EspetroAuiWidgets.teamColor(team));
        int headerH = EspetroAuiWidgets.PHASE_HEADER_HEIGHT;

        // 等待对方投票：不渲染编制卡片，仅屏幕正中显示稍大字号提示。
        if (isWaitingForOwnSelection()) {
            root.addChild(new WaitingText(this.width / 2, this.height / 2));
            return;
        }

        if (count == 0) {
            root.addChild(EspetroAuiWidgets.centeredText(panelX, headerH + 18, panelW,
                "没有可选编制", UiTheme.NEGATIVE));
            return;
        }

        int startX = panelX + 12;
        int visibleCount = Math.max(columns, visibleRows * columns);
        maxScrollOffset = Math.max(0,
            ((count - visibleCount + columns - 1) / columns) * columns);
        scrollOffset = Math.min(scrollOffset, maxScrollOffset);
        scrollOffset -= scrollOffset % columns;
        scrollStep = columns;
        int maxVisible = Math.min(count, scrollOffset + visibleCount);
        int openCardX = -1;
        int openCardY = -1;

        for (int i = scrollOffset; i < maxVisible; i++) {
            ClassSelectScreenPacket.FactionInfo faction = factions.get(i);
            int localIndex = i - scrollOffset;
            int col = localIndex % columns;
            int row = localIndex / columns;
            int x = startX + col * (cardW + cardGap);
            int y = startY + row * (cardH + cardGap);

            boolean selected = faction.id != null && faction.id.equals(lastSelectedFaction);
            FactionCardButton card = new FactionCardButton(x, y, cardW, cardH, imageSlotH, faction,
                selectingOpen, selected, () -> selectFaction(faction.id),
                () -> toggleFactionInfo(faction.id));
            root.addChild(card);
            factionCards.put(faction.id, card);
            if (faction.id != null && faction.id.equals(openInfoId)) {
                openCardX = x;
                openCardY = y;
            }
        }

        if (openInfoId != null) {
            ClassSelectScreenPacket.FactionInfo openFaction = findFaction(openInfoId);
            if (openFaction == null || openCardX < 0) {
                // 编制池刷新或卡片滚出视野：收起详情面板。
                openInfoId = null;
            } else {
                int popupW = Math.min(300, this.width - 16);
                int popupH = FactionInfoPopup.heightFor(openFaction);
                int popupX = openCardX + cardW - popupW;
                int popupY = openCardY + 22;
                if (popupY + popupH > this.height - 4) {
                    popupY = Math.max(4, this.height - 4 - popupH);
                }
                popupX = Math.max(4, Math.min(popupX, this.width - popupW - 4));
                popupY = Math.max(4, popupY);
                root.addChild(new FactionInfoPopup(popupX, popupY, popupW, openFaction,
                    () -> toggleFactionInfo(openFaction.id)));
            }
        }

        if (maxScrollOffset > 0) {
            EspetroAuiWidgets.Text hint = EspetroAuiWidgets.centeredText(panelX,
                Math.max(headerH, this.height - 12), panelW,
                "滚轮浏览  " + (scrollOffset + 1) + "-" + maxVisible + "/" + count,
                UiTheme.TEXT_THIRD);
            hint.setTextScale(UiTheme.TYPE_MICRO);
            root.addChild(hint);
        }
    }

    /** 点击卡片右上角「?」：展开/收起该编制的详情面板。 */
    private void toggleFactionInfo(String factionId) {
        if (factionId == null) return;
        openInfoId = factionId.equals(openInfoId) ? null : factionId;
        rebuildMenuRoot();
    }

    private ClassSelectScreenPacket.FactionInfo findFaction(String factionId) {
        if (factionId == null || factions == null) return null;
        for (ClassSelectScreenPacket.FactionInfo faction : factions) {
            if (factionId.equals(faction.id)) return faction;
        }
        return null;
    }

    private static class FactionCardButton extends GuiElement {
        private record FactionTexture(ResourceLocation location, int width, int height) {
        }

        /** 「?」圆钮半径（逻辑像素）。 */
        private static final int HELP_RADIUS = 7;
        private static final int HELP_MARGIN = 4;
        private static final int HELP_HIT_PAD = 2;
        /** 70% 黑圆底（0xB3 ≈ 179/255）。 */
        private static final int HELP_CIRCLE_BG = 0xB3000000;
        private static final int HELP_CIRCLE_RING = 0x59FFFFFF;
        private static final int HELP_CIRCLE_GLYPH = 0xFFFFFFFF;

        private ClassSelectScreenPacket.FactionInfo faction;
        private final int imageHeight;
        private boolean enabled;
        private boolean selected;
        private final Runnable action;
        private final Runnable helpAction;
        private FactionTexture texture;
        /** 最近一帧的绝对左上角，用于「?」圆钮命中测试。 */
        private int absX;
        private int absY;

        FactionCardButton(int x, int y, int width, int height, int imageHeight,
                          ClassSelectScreenPacket.FactionInfo faction,
                          boolean enabled, boolean selected, Runnable action, Runnable helpAction) {
            super(x, y, width, height);
            this.faction = faction;
            this.imageHeight = imageHeight;
            this.enabled = enabled;
            this.selected = selected;
            this.action = action;
            this.helpAction = helpAction;
            this.texture = resolveTexture(faction.selectionImage, faction.imageData);
        }

        void update(ClassSelectScreenPacket.FactionInfo faction, boolean enabled, boolean selected) {
            if (!Objects.equals(this.faction.selectionImage, faction.selectionImage)
                    || !java.util.Arrays.equals(this.faction.imageData, faction.imageData)) {
                this.texture = resolveTexture(faction.selectionImage, faction.imageData);
            }
            this.faction = faction;
            this.enabled = enabled;
            this.selected = selected;
        }

        @Override
        public boolean onMouseClick(int mouseX, int mouseY, int button) {
            if (button != 0 || !isVisible()) return false;
            // 「?」圆钮优先于选中卡片：点击只切换详情面板，不投票。
            if (isInHelpCircle(mouseX, mouseY)) {
                if (helpAction != null) helpAction.run();
                return true;
            }
            if (!enabled || !hasFocus()) return false;
            if (action != null) action.run();
            return true;
        }

        /** 圆钮命中测试：使用上一帧记录下来的绝对坐标。 */
        private boolean isInHelpCircle(int mouseX, int mouseY) {
            int cx = absX + getWidth() - HELP_MARGIN - HELP_RADIUS;
            int cy = absY + HELP_MARGIN + HELP_RADIUS;
            int dx = mouseX - cx;
            int dy = mouseY - cy;
            int r = HELP_RADIUS + HELP_HIT_PAD;
            return dx * dx + dy * dy <= r * r;
        }

        @Override
        public void draw(GuiGraphics graphics, int x, int y, int width, int height,
                         int mouseX, int mouseY, float partialTick) {
            if (!isVisible()) return;
            int bx = x + getX();
            int by = y + getY();
            int bw = getWidth();
            int bh = getHeight();
            absX = bx;
            absY = by;

            // 与地图投票卡片一致：直角实底、黄色端线与角标，悬停亮一档。
            // 票数仍在右下角、名称在左下角（位置按用户要求保持不动）。
            boolean hovered = hasFocus() && enabled;
            UiPaint.card(graphics, bx, by, bw, bh, UiTheme.RADIUS_CARD,
                UiTheme.CARD_TOP, UiTheme.CARD_BOTTOM);
            if (selected) {
                UiPaint.accentBarBottom(graphics, bx + 6, by + bh - 3, Math.max(1, bw - 12),
                    UiTheme.ACCENT);
                UiPaint.brackets(graphics, bx - 2, by - 2, bw + 4, bh + 4,
                    UiTheme.ACCENT_LINE);
                UiPaint.strokeRoundRect(graphics, bx, by, bw, bh, UiTheme.RADIUS_CARD,
                    UiTheme.ACCENT_LINE);
            } else if (hovered) {
                UiPaint.roundRect(graphics, bx, by, bw, bh, UiTheme.RADIUS_CARD,
                    UiTheme.SURFACE_2);
                UiPaint.strokeRoundRect(graphics, bx, by, bw, bh, UiTheme.RADIUS_CARD,
                    UiTheme.ACCENT_LINE);
            }

            int imageBoxY = by + 3;
            int imageBoxW = Math.max(1, bw - 6);
            int imageSlotH = Math.max(1, imageHeight - 6);
            AspectFit.Size fitted = texture == null
                ? AspectFit.within(16, 9, imageBoxW, imageSlotH)
                : AspectFit.within(texture.width(), texture.height(), imageBoxW, imageSlotH);
            int imageX = bx + Math.max(0, (bw - fitted.width()) / 2);
            int imageY = imageBoxY + Math.max(0, (imageSlotH - fitted.height()) / 2);

            if (texture != null) {
                graphics.setColor(1f, 1f, 1f, enabled ? 1f : 0.55f);
                graphics.blit(texture.location(), imageX, imageY, fitted.width(), fitted.height(),
                    0f, 0f, texture.width(), texture.height(), texture.width(), texture.height());
                graphics.setColor(1f, 1f, 1f, 1f);
            } else {
                String missing = "还没配置图片喵";
                UiPaint.textCentered(graphics, missing, bx,
                    imageBoxY + Math.max(8, imageSlotH / 2), bw,
                    UiTheme.TEXT_THIRD, UiTheme.TYPE_BODY, false);
            }

            // 票数：右下角大号数字，TYPE_NUMERAL + 阴影。
            String voteText = String.valueOf(faction.voteCount);
            int voteW = (int) (Minecraft.getInstance().font.width(voteText) * UiTheme.TYPE_NUMERAL);
            int voteX = bx + bw - 6 - voteW;
            int voteY = by + bh - 14;
            EspetroAuiWidgets.drawScaledString(graphics, voteText, voteX, voteY,
                enabled ? UiTheme.GOLD : UiTheme.TEXT_THIRD, UiTheme.TYPE_NUMERAL, true);

            // 名称：固定放在外框最底下一行，与右下角票数同一行
            String nameOnly = faction.name == null ? "" : faction.name;
            int nameMaxW = Math.max(8,
                (int) ((bw - 12 - voteW) / CARD_NAME_SCALE));
            String drawnName = EspetroAuiWidgets.trimToWidth(
                (selected ? "✔ " : "") + nameOnly, nameMaxW);
            int nameColor = selected ? UiTheme.GOLD
                : enabled ? UiTheme.TEXT : UiTheme.TEXT_DISABLED;
            int nameX = bx + 5;
            int nameY = by + bh - 11;
            EspetroAuiWidgets.drawScaledString(graphics, drawnName,
                nameX, nameY, nameColor, CARD_NAME_SCALE);

            // 右上角「?」圆钮：70% 黑色圆底 + 白色问号，点击展开编制详情。
            int helpCx = bx + bw - HELP_MARGIN - HELP_RADIUS;
            int helpCy = by + HELP_MARGIN + HELP_RADIUS;
            UiPaint.roundRect(graphics, helpCx - HELP_RADIUS, helpCy - HELP_RADIUS,
                HELP_RADIUS * 2, HELP_RADIUS * 2, HELP_RADIUS, HELP_CIRCLE_BG);
            UiPaint.strokeRoundRect(graphics, helpCx - HELP_RADIUS, helpCy - HELP_RADIUS,
                HELP_RADIUS * 2, HELP_RADIUS * 2, HELP_RADIUS, HELP_CIRCLE_RING);
            int questionW = Minecraft.getInstance().font.width("?");
            EspetroAuiWidgets.drawScaledString(graphics, "?", helpCx - questionW / 2,
                helpCy - 4, HELP_CIRCLE_GLYPH, 1.0f, true);

            super.draw(graphics, x, y, width, height, mouseX, mouseY, partialTick);
        }

        private static final Map<String, FactionTexture> DISK_TEXTURE_CACHE = new HashMap<>();
        private static final Map<String, Boolean> DISK_TEXTURE_FAILED = new HashMap<>();
        /** 缓存服务端发来的图片字节创建的纹理，key 为 selectionImage */
        private static final Map<String, FactionTexture> SERVER_IMAGE_CACHE = new HashMap<>();
        /** 资源包纹理的尺寸只读取一次，避免界面重建时重复解码 PNG。 */
        private static final Map<ResourceLocation, FactionTexture> RESOURCE_TEXTURE_CACHE = new HashMap<>();

        private static FactionTexture resolveTexture(String value, byte[] imageData) {
            if (value == null || value.isBlank()) return null;
            // 1. 服务端发来了图片字节 → 直接动态注册
            if (imageData != null && imageData.length > 0) {
                return resolveServerImage(value, imageData);
            }
            // 2. 尝试 ResourceLocation 格式 (espetro:textures/gui/factions/xxx.png)
            ResourceLocation location = ResourceLocation.tryParse(value);
            if (location != null && Minecraft.getInstance().getResourceManager()
                    .getResource(location).isPresent()) {
                return resolveResourceTexture(location);
            }
            // 3. 尝试从本地 EsFactions/ 目录加载（单机/局域网备用）
            return resolveEsFactionsTexture(value);
        }

        private static FactionTexture resolveServerImage(String key, byte[] imageData) {
            FactionTexture cached = SERVER_IMAGE_CACHE.get(key);
            if (cached != null) return cached;
            try {
                NativeImage image = NativeImage.read(
                    new java.io.ByteArrayInputStream(imageData));
                int width = image.getWidth();
                int height = image.getHeight();
                DynamicTexture texture = new DynamicTexture(image);
                String safe = Integer.toHexString(key.hashCode());
                ResourceLocation id = ResourceLocation.fromNamespaceAndPath(
                    "espetro", "dynamic/srv_faction_" + safe);
                Minecraft.getInstance().getTextureManager().register(id, texture);
                FactionTexture resolved = new FactionTexture(id, width, height);
                SERVER_IMAGE_CACHE.put(key, resolved);
                return resolved;
            } catch (Exception e) {
                return null;
            }
        }

        private static FactionTexture resolveResourceTexture(ResourceLocation location) {
            FactionTexture cached = RESOURCE_TEXTURE_CACHE.get(location);
            if (cached != null) return cached;
            try {
                var resource = Minecraft.getInstance().getResourceManager().getResource(location);
                if (resource.isEmpty()) return null;
                try (InputStream in = resource.get().open(); NativeImage image = NativeImage.read(in)) {
                    FactionTexture resolved = new FactionTexture(
                        location, image.getWidth(), image.getHeight());
                    RESOURCE_TEXTURE_CACHE.put(location, resolved);
                    return resolved;
                }
            } catch (Exception e) {
                return null;
            }
        }

        private static FactionTexture resolveEsFactionsTexture(String fileName) {
            String key = fileName.trim();
            if (DISK_TEXTURE_FAILED.containsKey(key)) return null;
            FactionTexture cached = DISK_TEXTURE_CACHE.get(key);
            if (cached != null) return cached;
            // 按服务端规则读取本地 EsFactions/ 目录；尝试游戏目录、当前工作目录、
            // 开发 run 目录和上一级目录，保证与服务端存放位置一致。
            Path gameDir = Minecraft.getInstance().gameDirectory.toPath();
            List<Path> candidateRoots = new ArrayList<>();
            candidateRoots.add(gameDir.resolve("EsFactions").normalize());
            candidateRoots.add(Path.of("EsFactions").toAbsolutePath().normalize());
            candidateRoots.add(Path.of("run", "EsFactions").toAbsolutePath().normalize());
            candidateRoots.add(Path.of("..", "EsFactions").toAbsolutePath().normalize());
            for (Path esFactionsDir : candidateRoots) {
                try {
                    Path imagePath = esFactionsDir.resolve(key).normalize();
                    // 安全检查：确保路径仍在对应 EsFactions 目录下
                    if (!imagePath.startsWith(esFactionsDir.normalize())) {
                        continue;
                    }
                    if (!Files.isRegularFile(imagePath)) {
                        continue;
                    }
                    try (InputStream in = Files.newInputStream(imagePath)) {
                        NativeImage image = NativeImage.read(in);
                        int width = image.getWidth();
                        int height = image.getHeight();
                        DynamicTexture texture = new DynamicTexture(image);
                        String safe = Integer.toHexString(key.hashCode());
                        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(
                            "espetro", "dynamic/faction_" + safe);
                        Minecraft.getInstance().getTextureManager().register(id, texture);
                        FactionTexture resolved = new FactionTexture(id, width, height);
                        DISK_TEXTURE_CACHE.put(key, resolved);
                        return resolved;
                    }
                } catch (Exception ignored) {
                    // 尝试下一个候选目录
                }
            }
            DISK_TEXTURE_FAILED.put(key, true);
            return null;
        }
    }

    private void selectFaction(String factionId) {
        if (tutorialPreviewMode || timeRemaining <= 0 || factionId == null || factionId.isEmpty()) {
            return;
        }

        NetworkManager.sendClassSelect("", factionId);
        lastSelectedFaction = factionId;
        refreshDynamicElements();
    }

    private boolean isWaitingForOwnSelection() {
        // 编制先后顺序：本方倒计时未启动（timeRemaining<=0）且对方仍在投票时，本方处于等待。
        return timeRemaining <= 0 && opponentTimeRemaining > 0;
    }

    public void updateFromPacket(ClassSelectScreenPacket packet) {
        List<ClassSelectScreenPacket.FactionInfo> nextFactions = packet.getFactions() == null
            ? new ArrayList<>() : new ArrayList<>(packet.getFactions());
        boolean layoutChanged = !Objects.equals(this.team, packet.getTeam())
            || !hasSameFactionLayout(this.factions, nextFactions);
        this.team = packet.getTeam();
        this.isCommander = packet.isCommander();
        this.factions = nextFactions;
        boolean wasWaiting = isWaitingForOwnSelection();
        this.timeRemaining = packet.getTimeRemaining();
        this.opponentTeamName = packet.getOpponentTeamName();
        this.opponentFaction = packet.getOpponentFaction();
        this.opponentTimeRemaining = packet.getOpponentTimeRemaining();
        this.lastSelectedFaction = packet.getSelectedFactionId().isEmpty()
            ? null : packet.getSelectedFactionId();
        if (this.root != null) {
            // 等待页 ↔ 卡片页 结构切换必须重建整棵树（含时间耗尽后进入等待对方）
            if (layoutChanged || wasWaiting != isWaitingForOwnSelection()) {
                rebuildMenuRoot();
            } else {
                refreshDynamicElements();
            }
        }
    }

    /** 轻量倒计时包：只改时间/选中态，绝不 rebuild。 */
    public void updateTimer(int timeRemaining, int opponentTimeRemaining,
                            String selectedFactionId, boolean isCommander) {
        boolean wasWaiting = isWaitingForOwnSelection();
        this.timeRemaining = timeRemaining;
        this.opponentTimeRemaining = opponentTimeRemaining;
        this.isCommander = isCommander;
        this.lastSelectedFaction = selectedFactionId == null || selectedFactionId.isEmpty()
            ? null : selectedFactionId;
        boolean nowWaiting = isWaitingForOwnSelection();
        if (root != null) {
            // 等待页 ↔ 卡片页 结构切换必须重建整棵树
            if (wasWaiting != nowWaiting) {
                rebuildMenuRoot();
            } else {
                refreshDynamicElements();
            }
        }
    }

    private void refreshDynamicElements() {
        if (phaseHeader != null) {
            phaseHeader.setTitle("编制投票 | "
                + EspetroAuiWidgets.teamName(team) + " [全员投票]");
            phaseHeader.setStatus(buildTimeText());
            phaseHeader.setDetail("");
        }
        boolean enabled = timeRemaining > 0;
        for (ClassSelectScreenPacket.FactionInfo faction : factions) {
            FactionCardButton card = factionCards.get(faction.id);
            if (card != null) {
                card.update(faction, enabled, Objects.equals(faction.id, lastSelectedFaction));
            }
        }
    }

    private String buildTimeText() {
        if (timeRemaining > 0) {
            return "剩余时间: " + timeRemaining + "秒";
        }
        if (isWaitingForOwnSelection()) {
            // 等待对方时顶部第二行与投票一致：显示对方剩余倒计时
            int remaining = Math.max(0, opponentTimeRemaining);
            return "对方选择剩余: " + remaining + "秒";
        }
        return "本方编制已确定";
    }

    private String buildOpponentText() {
        if (opponentTeamName == null || opponentTeamName.isEmpty()) {
            return "";
        }
        String text;
        if (opponentFaction != null && !opponentFaction.isEmpty()) {
            text = opponentTeamName + " 编制: " + opponentFaction;
        } else {
            text = opponentTeamName + " 编制尚未确定";
        }
        if (opponentTimeRemaining >= 0) {
            text += " · 剩余 " + opponentTimeRemaining + "秒";
        }
        return text;
    }

    /** 屏幕正中的“等待对方投票中…”大字提示（自绘缩放）。 */
    private static final class WaitingText extends GuiElement {
        private final int centerX;
        private final int centerY;

        WaitingText(int centerX, int centerY) {
            super(0, 0, 1, 1);
            this.centerX = centerX;
            this.centerY = centerY;
        }

        @Override
        public void draw(GuiGraphics graphics, int x, int y, int width, int height,
                         int mouseX, int mouseY, float partialTick) {
            if (!isVisible()) return;
            String label = "等待对方投票中…";
            float scale = 2.0f;
            UiPaint.textCentered(graphics, label, 0, centerY, centerX * 2,
                UiTheme.GOLD, scale, false);
        }
    }

    /**
     * 编制「?」详情面板：所属阵营 show_name、编制性质 description、
     * 左列载具（刷新时间）、右列专属工事（可部署数量）。点击面板任意处收起。
     */
    private static final class FactionInfoPopup extends GuiElement {
        private static final int PAD = 8;
        private static final int TITLE_H = 12;
        private static final int HEAD_H = 12;
        private static final int ROW_H = 10;
        private static final int FOOT_PAD = 6;
        private static final float SCALE = 0.8f;
        private static final int MAX_ROWS = 14;

        private final ClassSelectScreenPacket.FactionInfo faction;
        private final Runnable closeAction;
        private final int rows;

        FactionInfoPopup(int x, int y, int width, ClassSelectScreenPacket.FactionInfo faction,
                         Runnable closeAction) {
            super(x, y, width, heightFor(faction));
            this.faction = faction;
            this.closeAction = closeAction;
            this.rows = Math.min(MAX_ROWS,
                Math.max(faction.vehicles.size(), faction.forts.size()));
        }

        static int heightFor(ClassSelectScreenPacket.FactionInfo faction) {
            int lines = Math.min(MAX_ROWS,
                Math.max(faction.vehicles.size(), faction.forts.size()));
            return PAD + TITLE_H + HEAD_H + Math.max(1, lines) * ROW_H + FOOT_PAD;
        }

        @Override
        public boolean onMouseClick(int mouseX, int mouseY, int button) {
            if (button == 0 && hasFocus() && closeAction != null) {
                closeAction.run();
                return true;
            }
            return false;
        }

        @Override
        public void draw(GuiGraphics graphics, int refX, int refY, int screenWidth,
                         int screenHeight, int mouseX, int mouseY, float opacity) {
            if (!isVisible()) return;
            int bx = refX + getX();
            int by = refY + getY();
            int bw = getWidth();
            int bh = getHeight();
            UiPaint.roundRect(graphics, bx, by, bw, bh, UiTheme.RADIUS_CARD, 0xF0121216);
            UiPaint.strokeRoundRect(graphics, bx, by, bw, bh, UiTheme.RADIUS_CARD,
                UiTheme.ACCENT_LINE);

            int halfW = bw / 2;
            int leftX = bx + PAD;
            int rightX = bx + halfW + 4;
            int leftRight = bx + halfW - 6;
            int rightRight = bx + bw - PAD;
            int leftTextW = Math.max(10, (int) ((leftRight - leftX) / SCALE));
            int rightTextW = Math.max(10, (int) ((rightRight - rightX) / SCALE));

            int y = by + PAD;
            String title = faction.showName == null || faction.showName.isBlank()
                ? faction.name : faction.showName;
            EspetroAuiWidgets.drawScaledString(graphics,
                EspetroAuiWidgets.trimToWidth(title, leftTextW),
                leftX, y, UiTheme.GOLD, 0.9f, true);
            String nature = faction.description == null ? "" : faction.description.trim();
            if (!nature.isEmpty()) {
                String drawn = EspetroAuiWidgets.trimToWidth(nature, rightTextW);
                int drawnW = (int) (Minecraft.getInstance().font.width(drawn) * SCALE);
                EspetroAuiWidgets.drawScaledString(graphics, drawn, rightRight - drawnW,
                    y + 1, UiTheme.TEXT_SECOND, SCALE, false);
            }

            UiPaint.roundRect(graphics, bx + PAD, by + PAD + TITLE_H - 2,
                Math.max(1, bw - PAD * 2), 1, 0, UiTheme.HAIRLINE);

            y = by + PAD + TITLE_H;
            EspetroAuiWidgets.drawScaledString(graphics, "载具", leftX, y + 1,
                UiTheme.ACCENT, SCALE, false);
            EspetroAuiWidgets.drawScaledString(graphics, "专属工事", rightX, y + 1,
                UiTheme.ACCENT, SCALE, false);

            y += HEAD_H;
            if (rows == 0) {
                EspetroAuiWidgets.drawScaledString(graphics, "暂无可展示信息", leftX, y,
                    UiTheme.TEXT_THIRD, SCALE, false);
            }
            for (int i = 0; i < rows; i++) {
                int rowY = y + i * ROW_H;
                if (i < faction.vehicles.size()) {
                    ClassSelectScreenPacket.VehicleLine line = faction.vehicles.get(i);
                    // 时间后面追加该型载具总数量，如 "15:00 ×1"。
                    String time = String.format("%02d:%02d", line.respawnMinutes, 0)
                        + (line.count > 0 ? " \u00d7" + line.count : "");
                    int timeW = Math.max(20, (int) (Minecraft.getInstance().font.width(time) * SCALE));
                    EspetroAuiWidgets.drawScaledString(graphics,
                        EspetroAuiWidgets.trimToWidth(line.name,
                            Math.max(10, leftTextW - (int) (timeW / SCALE) - 6)),
                        leftX, rowY, UiTheme.TEXT, SCALE, false);
                    EspetroAuiWidgets.drawScaledString(graphics, time, leftRight - timeW, rowY,
                        UiTheme.TEXT_SECOND, SCALE, false);
                }
                if (i < faction.forts.size()) {
                    ClassSelectScreenPacket.FortLine line = faction.forts.get(i);
                    String amount = "×" + line.limit;
                    int amountW = Math.max(12, (int) (Minecraft.getInstance().font.width(amount) * SCALE));
                    EspetroAuiWidgets.drawScaledString(graphics,
                        EspetroAuiWidgets.trimToWidth(line.name,
                            Math.max(10, rightTextW - (int) (amountW / SCALE) - 6)),
                        rightX, rowY, UiTheme.TEXT, SCALE, false);
                    EspetroAuiWidgets.drawScaledString(graphics, amount, rightRight - amountW,
                        rowY, UiTheme.GOLD, SCALE, false);
                }
            }
            int hidden = Math.max(faction.vehicles.size(), faction.forts.size()) - rows;
            if (hidden > 0) {
                EspetroAuiWidgets.drawScaledString(graphics, "… 另有 " + hidden + " 项",
                    leftX, y + rows * ROW_H, UiTheme.TEXT_THIRD, SCALE, false);
            }
            super.draw(graphics, refX, refY, screenWidth, screenHeight, mouseX, mouseY, opacity);
        }
    }

    private static boolean hasSameFactionLayout(List<ClassSelectScreenPacket.FactionInfo> current,
                                                List<ClassSelectScreenPacket.FactionInfo> updated) {
        if (current == null || current.size() != updated.size()) {
            return false;
        }
        for (int i = 0; i < current.size(); i++) {
            ClassSelectScreenPacket.FactionInfo left = current.get(i);
            ClassSelectScreenPacket.FactionInfo right = updated.get(i);
            if (!Objects.equals(left.id, right.id)
                    || !Objects.equals(left.name, right.name)
                    || !Objects.equals(left.selectionImage, right.selectionImage)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public void onClose() {
        // 编制界面由服务端阶段推进后替换，玩家不能手动跳过选择流程。
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (maxScrollOffset > 0) {
            int nextOffset = scrollOffset + (delta < 0 ? scrollStep : -scrollStep);
            nextOffset = Math.max(0, Math.min(maxScrollOffset, nextOffset));
            if (nextOffset != scrollOffset) {
                scrollOffset = nextOffset;
                rebuildMenuRoot();
                return true;
            }
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
