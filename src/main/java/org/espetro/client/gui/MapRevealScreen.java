package org.espetro.client.gui;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.espetro.client.aui.GuiElement;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * 地图揭晓页：地图投票结束后显示胜出地图名称与预览图（图片居中，无图显示占位）。
 */
public class MapRevealScreen extends EspetroMenuScreen {

    private static final int IMG_MAX_W = 640;
    private static final int IMG_MAX_H = 360;

    /** 预览图缓存：mapFolder → 纹理与源尺寸。 */
    private static final Map<String, RevealTexture> TEXTURE_CACHE = new HashMap<>();

    private record RevealTexture(ResourceLocation location, int texW, int texH) {
    }

    private final String mapDisplayName;
    private final String mapFolder;
    private int ticksLeft;
    private boolean done;
    private EspetroAuiWidgets.PhaseHeader phaseHeader;

    public MapRevealScreen(String mapDisplayName, String mapFolder, int durationSeconds) {
        super(Component.literal("地图揭晓"));
        this.mapDisplayName = mapDisplayName == null || mapDisplayName.isBlank()
            ? "未知地图" : mapDisplayName;
        this.mapFolder = mapFolder == null ? "" : mapFolder;
        this.ticksLeft = Math.max(1, durationSeconds) * 20;
        resolveTexture();
    }

    /** 从客户端 EsWorld 目录加载预览图；失败时 UI 显示「暂无图片」。 */
    private void resolveTexture() {
        if (mapFolder.isEmpty() || TEXTURE_CACHE.containsKey(mapFolder)) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc == null) return;
        Path previewPath = MapVotePreviewResolver.resolve(mc.gameDirectory.toPath(), mapFolder);
        if (previewPath == null) return;
        try (InputStream in = Files.newInputStream(previewPath)) {
            NativeImage image = NativeImage.read(in);
            int w = image.getWidth();
            int h = image.getHeight();
            DynamicTexture texture = new DynamicTexture(image);
            ResourceLocation rl = ResourceLocation.fromNamespaceAndPath(
                "espetro", "map_reveal/" + Integer.toHexString(mapFolder.hashCode()));
            mc.getTextureManager().register(rl, texture);
            TEXTURE_CACHE.put(mapFolder, new RevealTexture(rl, w, h));
        } catch (IOException | RuntimeException e) {
            // 无预览图：占位显示
        }
    }

    @Override
    protected void renderBeforeMenu(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        EspetroAuiWidgets.drawScreenShade(graphics, this.width, this.height);
    }

    @Override
    protected void buildMenuRoot(GuiElement root) {
        phaseHeader = EspetroAuiWidgets.addMutablePhaseHeader(root, this.width,
            "§6§l地图揭晓",
            "§f胜出地图: §e" + mapDisplayName,
            "§7" + getSecondsRemaining() + " 秒后进入装载",
            EspetroAuiWidgets.GOLD);

        int headerH = EspetroAuiWidgets.PHASE_HEADER_HEIGHT;
        // 预览图区域：全屏宽度减去边距，保持 16:9，垂直居中于标题下方
        int availW = Math.max(200, Math.min(IMG_MAX_W, this.width - 80));
        int availH = Math.max(112, Math.min(IMG_MAX_H, this.height - headerH - 100));
        RevealTexture tex = TEXTURE_CACHE.get(mapFolder);
        int imgW;
        int imgH;
        if (tex != null) {
            float scale = Math.min((float) availW / tex.texW(), (float) availH / tex.texH());
            imgW = Math.max(1, Math.round(tex.texW() * scale));
            imgH = Math.max(1, Math.round(tex.texH() * scale));
            int imgX = (this.width - imgW) / 2;
            int imgY = headerH + Math.max(0, (this.height - headerH - imgH) / 2 - 20);
            root.addChild(new RevealImageElement(imgX, imgY, imgW, imgH, tex));
        } else {
            // 无图占位：灰色方框 + 「暂无图片」
            imgW = Math.max(240, Math.min(availW, this.width / 2));
            imgH = Math.max(135, imgW * 9 / 16);
            int imgX = (this.width - imgW) / 2;
            int imgY = headerH + Math.max(0, (this.height - headerH - imgH) / 2 - 20);
            root.addChild(new PlaceholderElement(imgX, imgY, imgW, imgH));
        }
    }

    /** 居中预览图绘制。 */
    private static final class RevealImageElement extends GuiElement {
        private final RevealTexture tex;

        RevealImageElement(int x, int y, int w, int h, RevealTexture tex) {
            super(x, y, w, h);
            this.tex = tex;
        }

        @Override
        public void draw(GuiGraphics graphics, int x, int y, int width, int height,
                         int mouseX, int mouseY, float partialTick) {
            if (!isVisible() || tex == null) return;
            int bx = x + getX();
            int by = y + getY();
            graphics.blit(tex.location(), bx, by, getWidth(), getHeight(),
                0, 0, tex.texW(), tex.texH(), tex.texW(), tex.texH());
        }
    }

    /** 无预览图占位。 */
    private static final class PlaceholderElement extends GuiElement {
        PlaceholderElement(int x, int y, int w, int h) {
            super(x, y, w, h);
        }

        @Override
        public void draw(GuiGraphics graphics, int x, int y, int width, int height,
                         int mouseX, int mouseY, float partialTick) {
            if (!isVisible()) return;
            int bx = x + getX();
            int by = y + getY();
            int bw = getWidth();
            int bh = getHeight();
            graphics.fill(bx, by, bx + bw, by + bh, 0x60303030);
            graphics.renderOutline(bx, by, bw, bh, 0x805B6260);
            graphics.drawCenteredString(Minecraft.getInstance().font,
                Component.literal("§7暂无图片"),
                bx + bw / 2, by + bh / 2 - 4, 0xFFAAAAAA);
        }
    }

    private int getSecondsRemaining() {
        return Math.max(0, (ticksLeft + 19) / 20);
    }

    @Override
    public void tick() {
        super.tick();
        if (done) return;
        ticksLeft--;
        if (phaseHeader != null && ticksLeft % 20 == 0) {
            phaseHeader.setDetail("§7" + getSecondsRemaining() + " 秒后进入装载");
        }
        if (ticksLeft <= 0 && Minecraft.getInstance().screen == this && !tutorialPreviewMode) {
            done = true;
            // 服务端 5 秒后推进到 MAP_LOADING（GamePhaseSync 会清屏）。
            // 若已在淡出中则等待完成，避免重复关闭。
            if (!isFadingOut()) {
                org.espetro.client.aui.AuiScreen.closeWithFade(this);
            }
        }
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
