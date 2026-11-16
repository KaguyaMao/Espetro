package org.espetro.client.gui;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.espetro.network.MapVoteStatePacket;
import org.espetro.network.NetworkManager;
import org.espetro.client.aui.GuiElement;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Forced global map-vote screen with up to six candidates in a 2×3 grid. */
public final class MapVoteScreen extends EspetroMenuScreen {
    private static final int COLUMNS = 3;
    private static final int MAX_ROWS = 2;
    private static final int MAX_CANDIDATES = COLUMNS * MAX_ROWS;
    /** 与编制选择 ClassSelectScreen 同级的卡片间距。 */
    private static final int CARD_PAD = 4;
    /** 最底部仅保留名称/票数一行。 */
    private static final int FOOTER_H = 14;

    private static MapVoteStatePacket latest = new MapVoteStatePacket(
        false, 0, 0L, List.of(), java.util.Map.of(), null, null, null);

    /** 缓存已从客户端 EsWorld 目录解码的地图预览纹理：mapFolder → 纹理与源尺寸。 */
    private static final Map<String, PreviewTexture> previewTextureCache = new LinkedHashMap<>();

    private record PreviewTexture(ResourceLocation location, int texW, int texH) {
    }

    private final List<MapCardButton> mapButtons = new ArrayList<>();
    private long receivedAtMs;
    private int receivedRemaining;
    private VotePhase phaseHeader;
    /** 上次写入顶栏的显示秒，避免每 tick 改字符串加重闪烁感。 */
    private int lastStatusSecond = Integer.MIN_VALUE;

    public MapVoteScreen() {
        super(Component.literal("地图投票"));
        receivedAtMs = System.currentTimeMillis();
        receivedRemaining = latest.remainingSeconds;
        preloadPreviewTextures();
    }

    /** 从客户端游戏根目录 EsWorld/&lt;mapFolder&gt;.png 解码并注册纹理。 */
    private static void preloadPreviewTextures() {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null) return;

        for (MapVoteStatePacket.Candidate c : latest.candidates) {
            String key = c.mapFolder;
            if (previewTextureCache.containsKey(key)) continue;
            Path previewPath = MapVotePreviewResolver.resolve(mc.gameDirectory.toPath(), key);
            if (previewPath == null) continue;

            try (InputStream in = Files.newInputStream(previewPath)) {
                NativeImage image = NativeImage.read(in);
                int texW = image.getWidth();
                int texH = image.getHeight();
                DynamicTexture texture = new DynamicTexture(image);
                ResourceLocation rl = ResourceLocation.fromNamespaceAndPath(
                    "espetro", "map_preview/" + Integer.toHexString(key.hashCode()));
                mc.getTextureManager().register(rl, texture);
                previewTextureCache.put(key, new PreviewTexture(rl, texW, texH));
            } catch (IOException e) {
                // 解码失败视为无预览图
            }
        }
    }

    public static void update(MapVoteStatePacket packet) {
        latest = packet;
        preloadPreviewTextures();
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen instanceof MapVoteScreen screen) {
            boolean structureChanged = screen.mapButtons.size() != packet.candidates.size();
            screen.receivedAtMs = System.currentTimeMillis();
            screen.receivedRemaining = packet.remainingSeconds;
            if (structureChanged) screen.rebuildMenuRoot();
            else screen.refreshLabels();
        }
    }

    @Override
    protected void renderBeforeMenu(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        CurrentMapBackgroundRenderer.render(
            graphics, this.width, this.height, resolveBackgroundMapFolder());
    }

    /** 本局背景地图：本人投票 → 胜出地图 → 已加载地图 → 首个候选。其它阶段页复用同一回退链。 */
    static String resolveBackgroundMapFolder() {
        if (latest.myVoteMapFolder != null && !latest.myVoteMapFolder.isBlank()) {
            return latest.myVoteMapFolder;
        }
        if (latest.winnerMapFolder != null && !latest.winnerMapFolder.isBlank()) {
            return latest.winnerMapFolder;
        }
        String current = ClientGameState.getCurrentMapFolder();
        if (current != null && !current.isBlank()) {
            return current;
        }
        return latest.candidates.isEmpty() ? null : latest.candidates.get(0).mapFolder;
    }

    /**
     * 候选卡片紧凑居中，逐行居中排列，底部始终预留真实阶段与倒计时。
     */
    @Override
    protected void buildMenuRoot(GuiElement root) {
        mapButtons.clear();
        phaseHeader = VotePhase.add(root, width, height, "全局地图投票",
            "全服统一计票，票数最高的地图胜出", () -> buildResultText(computeDisplaySeconds()),
            () -> VotePhase.Stage.MAP);
        int count = Math.min(latest.candidates.size(), MAX_CANDIDATES);
        VotePhase.Grid layout = VotePhase.grid(width, height, count, 3, FOOTER_H);

        for (int i = 0; i < count; i++) {
            MapVoteStatePacket.Candidate candidate = latest.candidates.get(i);
            var button = new MapCardButton(
                layout.x(i), layout.y(i),
                layout.cardWidth(), layout.cardHeight(),
                Math.max(1, layout.cardWidth() - CARD_PAD * 2),
                Math.max(1, layout.cardHeight() - FOOTER_H - 4),
                candidate,
                () -> {
                    if (tutorialPreviewMode) {
                        return;
                    }
                    NetworkManager.sendMapVoteCast(candidate.mapFolder);
                });
            mapButtons.add(button);
            root.addChild(button);
        }
        refreshLabels();
    }

    private void refreshLabels() {
        for (int i = 0; i < mapButtons.size() && i < latest.candidates.size(); i++) {
            var candidate = latest.candidates.get(i);
            int votes = latest.tally.getOrDefault(candidate.mapFolder, 0);
            boolean selected = candidate.mapFolder.equals(latest.myVoteMapFolder);
            mapButtons.get(i).update(candidate, votes, selected, latest.active);
        }
        updateStatusHeader(true);
    }

    private int computeDisplaySeconds() {
        int elapsed = (int) ((System.currentTimeMillis() - receivedAtMs) / 1000L);
        return Math.max(0, receivedRemaining - elapsed);
    }

    private String buildResultText(int remaining) {
        return latest.active ? "剩余 " + remaining + " 秒"
            : "胜出地图：" + (latest.winnerDisplayName == null ? "随机" : latest.winnerDisplayName);
    }

    private void updateStatusHeader(boolean force) {
        if (phaseHeader == null) return;
        int remaining = computeDisplaySeconds();
        if (!force && remaining == lastStatusSecond) return;
        lastStatusSecond = remaining;
        phaseHeader.setStatus(buildResultText(remaining));
    }

    @Override
    public void tick() {
        super.tick();
        updateStatusHeader(false);
    }

    @Override
    public void onClose() {
        if (!latest.active) super.onClose();
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return !latest.active;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    /** 16:9 预览图卡片，显示地图截图或占位文本；尺寸由外层布局计算。 */
    private static final class MapCardButton extends GuiElement {
        private MapVoteStatePacket.Candidate candidate;
        private int votes;
        private boolean selected;
        private boolean enabled;
        private final int imgW;
        private final int imgH;
        private final Runnable action;

        private MapCardButton(int x, int y, int width, int height,
                              int imgW, int imgH,
                              MapVoteStatePacket.Candidate candidate, Runnable action) {
            super(x, y, width, height);
            this.imgW = Math.max(1, imgW);
            this.imgH = Math.max(1, imgH);
            this.candidate = candidate;
            this.action = action;
        }

        private void update(MapVoteStatePacket.Candidate candidate, int votes,
                            boolean selected, boolean enabled) {
            this.candidate = candidate;
            this.votes = votes;
            this.selected = selected;
            this.enabled = enabled;
        }

        @Override
        public boolean onMouseClick(int mouseX, int mouseY, int button) {
            if (button != 0 || !enabled || !isVisible() || !hasFocus()) {
                return false;
            }
            if (action != null) {
                action.run();
            }
            return true;
        }

        @Override
        public void draw(GuiGraphics graphics, int x, int y, int width, int height,
                         int mouseX, int mouseY, float partialTick) {
            if (!isVisible()) {
                return;
            }
            int bx = x + getX();
            int by = y + getY();
            int bw = getWidth();
            int bh = getHeight();

            // 无边框玻璃卡片，以浅色底反馈悬停与选中。
            boolean hovered = hasFocus() && enabled;
            UiPaint.glassPanel(graphics, bx, by, bw, bh,
                selected ? UiTheme.CONTROL_SELECTED
                    : !enabled ? UiTheme.CONTROL_DISABLED
                    : hovered ? UiTheme.CONTROL_HOVER : UiTheme.CONTROL_NORMAL);

            // 预览区：贴卡片顶部水平居中，固定 16:9 横向，尽量放大
            int maxImgW = Math.max(1, bw - CARD_PAD * 2);
            int maxImgH = Math.max(1, bh - FOOTER_H - 4);
            int imgAreaW = Math.max(1, Math.min(imgW, maxImgW));
            int imgAreaH = Math.max(1, imgAreaW * 9 / 16);
            if (imgAreaH > maxImgH) {
                imgAreaH = maxImgH;
                imgAreaW = Math.max(1, imgAreaH * 16 / 9);
            }
            int imgX = bx + (bw - imgAreaW) / 2;
            int imgY = by + 4;

            PreviewTexture preview = previewTextureCache.get(candidate.mapFolder);
            if (preview != null) {
                graphics.blit(preview.location(), imgX, imgY, imgAreaW, imgAreaH,
                    0f, 0f, preview.texW(), preview.texH(),
                    preview.texW(), preview.texH());
            } else {
                UiPaint.sunken(graphics, imgX, imgY, imgAreaW, imgAreaH, UiTheme.RADIUS_CONTROL);
                UiPaint.textCentered(graphics, "暂未添加图片", imgX, imgY + imgAreaH / 2 - 4,
                    imgAreaW, UiTheme.TEXT_THIRD, UiTheme.TYPE_BODY, false);
            }

            // 票数：右下角大号数字（与编制投票页面一致，不再用预览图上的胶囊徽章）
            String voteText = String.valueOf(votes);
            int voteW = Math.round(Minecraft.getInstance().font.width(voteText)
                * UiTheme.TYPE_NUMERAL);
            int voteX = bx + bw - 6 - voteW;
            int voteY = by + bh - 14;
            EspetroAuiWidgets.drawScaledString(graphics, voteText, voteX, voteY,
                enabled ? UiTheme.GOLD : UiTheme.TEXT_THIRD, UiTheme.TYPE_NUMERAL, true);

            // 名称：固定放在外框最底下一行，左侧留出票数宽度
            int nameMaxW = Math.max(8, bw - 12 - voteW);
            String mapName = EspetroAuiWidgets.trimToWidth(
                (selected ? "✔ " : "") + candidate.displayName, nameMaxW);
            UiPaint.text(graphics, mapName, bx + 6, by + bh - 10,
                selected ? UiTheme.GOLD : enabled ? UiTheme.TEXT : UiTheme.TEXT_DISABLED);

            super.draw(graphics, x, y, width, height, mouseX, mouseY, partialTick);
        }
    }
}
