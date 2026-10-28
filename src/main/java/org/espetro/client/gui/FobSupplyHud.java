package org.espetro.client.gui;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import org.espetro.Espetro;
import org.espetro.network.FobSupplySyncPacket;
import org.espetro.client.aui.GuiElement;

import java.io.IOException;

/**
 * Radio 范围补给状态缓存与 AUI HUD 元素。
 *
 * <p>关键信息变化时，两张补给图标在 CPU 侧重新烘焙并上传成一张透明底的动态纹理；
 * 相同数据不会重建或上传。卡片底（圆角玻璃）、进度槽与数值每帧由 {@link UiPaint}
 * 直接绘制，普通帧没有位图重建成本。</p>
 */
public final class FobSupplyHud {

    private static final int HUD_X = 8;
    private static final int HUD_Y = 8;
    private static final int PANEL_WIDTH = 104;
    private static final int PANEL_HEIGHT = 22;
    // 卡片内边距：左右各留 6px，顶部 4px，底部 2px（贴边不糊）。
    private static final int BAR_X = 6;
    private static final int BAR_Y = 4;
    private static final int BAR_HEIGHT = 4;
    private static final int BAR_WIDTH = 92;
    private static final int ICON_SIZE = 10;
    private static final int AMMO_ICON_X = 7;
    private static final int AMMO_ICON_Y = 10;
    private static final int AMMO_TEXT_X = 20;
    private static final int CONSTRUCTION_ICON_X = 56;
    private static final int CONSTRUCTION_ICON_Y = 10;
    private static final int CONSTRUCTION_TEXT_X = 69;
    private static final int VALUE_TEXT_Y = 10;

    private static final ResourceLocation AMMO_ICON = ResourceLocation.fromNamespaceAndPath(
        "espetro", "textures/gui/squad/ammo_supply.png");
    private static final ResourceLocation CONSTRUCTION_ICON = ResourceLocation.fromNamespaceAndPath(
        "espetro", "textures/gui/squad/construction_supply.png");

    private static boolean inRange;
    private static int construction;
    private static int ammunition;
    private static int maxConstruction = 1;
    private static int maxAmmunition = 1;
    private static int radioHealth;
    private static int radioMaxHealth = 1;
    private static SupplyElement activeElement;
    private static long stateRevision;

    private FobSupplyHud() {
    }

    /** Kept for binary/source compatibility; rendering is owned by {@link EspetroHudOverlay}. */
    public static void register() {
    }

    public static void update(FobSupplySyncPacket packet) {
        if (packet == null || !packet.isInRange()) {
            clear();
            return;
        }
        int nextConstruction = Math.max(0, packet.getConstruction());
        int nextAmmunition = Math.max(0, packet.getAmmunition());
        int nextMaxConstruction = Math.max(1, packet.getMaxConstruction());
        int nextMaxAmmunition = Math.max(1, packet.getMaxAmmunition());
        int nextRadioHealth = Math.max(0, packet.getRadioHealth());
        int nextRadioMaxHealth = Math.max(1, packet.getRadioMaxHealth());
        if (inRange
            && construction == nextConstruction
            && ammunition == nextAmmunition
            && maxConstruction == nextMaxConstruction
            && maxAmmunition == nextMaxAmmunition
            && radioHealth == nextRadioHealth
            && radioMaxHealth == nextRadioMaxHealth) {
            return;
        }
        inRange = true;
        construction = nextConstruction;
        ammunition = nextAmmunition;
        maxConstruction = nextMaxConstruction;
        maxAmmunition = nextMaxAmmunition;
        radioHealth = nextRadioHealth;
        radioMaxHealth = nextRadioMaxHealth;
        stateRevision++;
        applyStateToElement();
    }

    public static void clear() {
        if (!inRange) return;
        inRange = false;
        stateRevision++;
        applyStateToElement();
    }

    /** Resource-pack reloads invalidate the cached source sprites, not the HUD state. */
    public static void onResourceReload() {
        if (activeElement != null) {
            activeElement.invalidateResources();
        }
    }

    static GuiElement createElement() {
        if (activeElement != null) activeElement.dispose();
        activeElement = new SupplyElement();
        activeElement.applyState();
        return activeElement;
    }

    private static void applyStateToElement() {
        if (activeElement != null) activeElement.applyState();
    }

    static int scaledBarWidth(int value, int maximum, int width) {
        if (value <= 0 || maximum <= 0 || width <= 0) return 0;
        long clamped = Math.min((long) value, (long) maximum);
        return Math.max(1, (int) Math.min(width,
            Math.round(clamped * width / (double) maximum)));
    }

    static long stateRevisionForTest() {
        return stateRevision;
    }

    static void resetStateForTest() {
        inRange = false;
        construction = 0;
        ammunition = 0;
        maxConstruction = 1;
        maxAmmunition = 1;
        radioHealth = 0;
        radioMaxHealth = 1;
        stateRevision = 0;
        activeElement = null;
    }

    /**
     * Event-redrawn MUtil texture. The bitmap is rebuilt only after applyState marks
     * it dirty; normal render frames never redraw its individual contents.
     */
    private static final class SupplyElement extends GuiElement {

        private NativeImage pixels;
        private DynamicTexture texture;
        private ResourceLocation textureLocation;
        private NativeImage ammoIcon;
        private NativeImage constructionIcon;
        private boolean resourcesLoaded;
        private boolean dirty = true;
        private boolean failed;
        private long themeRevision = -1L;

        private SupplyElement() {
            super(HUD_X, HUD_Y, PANEL_WIDTH, PANEL_HEIGHT);
        }

        /** Network/root events only: mark one future bitmap upload as necessary. */
        private void applyState() {
            setVisible(inRange);
            if (inRange) dirty = true;
        }

        private void invalidateResources() {
            resourcesLoaded = false;
            failed = false;
            dirty = true;
        }

        @Override
        public void draw(GuiGraphics graphics, int parentX, int parentY, int drawWidth,
                         int drawHeight, int mouseX, int mouseY, float partialTick) {
            if (!isVisible()) return;
            if (themeRevision != UiTheme.revision()) {
                themeRevision = UiTheme.revision();
                dirty = true;
            }
            if (dirty && !failed) rebuildTexture();
            int panelX = parentX + getX();
            int panelY = parentY + getY();

            // 圆角玻璃卡：阴影 + 渐变底 + 发丝描边 + 顶部内高光。
            UiPaint.card(graphics, panelX, panelY, PANEL_WIDTH, PANEL_HEIGHT);
            // 电台血量：内凹槽 + 渐变填充 + 高光。
            float healthRatio = radioMaxHealth <= 0 ? 0f : radioHealth / (float) radioMaxHealth;
            UiPaint.progress(graphics, panelX + BAR_X, panelY + BAR_Y,
                BAR_WIDTH, BAR_HEIGHT, healthRatio, UiTheme.ACCENT);

            // Keep source sprite colors legible on either paper or ink surfaces.
            graphics.fill(panelX + AMMO_ICON_X, panelY + AMMO_ICON_Y,
                panelX + AMMO_ICON_X + ICON_SIZE, panelY + AMMO_ICON_Y + ICON_SIZE,
                UiTheme.ICON_PLATE);
            graphics.fill(panelX + CONSTRUCTION_ICON_X, panelY + CONSTRUCTION_ICON_Y,
                panelX + CONSTRUCTION_ICON_X + ICON_SIZE, panelY + CONSTRUCTION_ICON_Y + ICON_SIZE,
                UiTheme.ICON_PLATE);

            if (textureLocation != null) {
                HudRenderState.begin(graphics);
                graphics.blit(textureLocation, panelX, panelY,
                    0.0f, 0.0f, PANEL_WIDTH, PANEL_HEIGHT, PANEL_WIDTH, PANEL_HEIGHT);
                HudRenderState.restore(graphics);
            } else {
                drawFallbackIcons(graphics, panelX, panelY);
            }

            // 关键数值：标题字号 + 投影，保持高可读。
            UiPaint.textScaled(graphics, Integer.toString(ammunition),
                panelX + AMMO_TEXT_X, panelY + VALUE_TEXT_Y, UiTheme.TEXT,
                UiTheme.TYPE_HEADING, true);
            UiPaint.textScaled(graphics, Integer.toString(construction),
                panelX + CONSTRUCTION_TEXT_X, panelY + VALUE_TEXT_Y, UiTheme.TEXT,
                UiTheme.TYPE_HEADING, true);
        }

        private void rebuildTexture() {
            try {
                if (pixels == null) {
                    pixels = new NativeImage(PANEL_WIDTH, PANEL_HEIGHT, true);
                }
                loadResourcesIfNecessary();
                paintBitmap();
                if (texture == null) {
                    texture = new DynamicTexture(pixels);
                    texture.setFilter(false, false);
                    textureLocation = Minecraft.getInstance().getTextureManager()
                        .register("espetro_fob_supply", texture);
                } else {
                    texture.upload();
                }
                dirty = false;
            } catch (RuntimeException exception) {
                failed = true;
                Espetro.LOGGER.warn("Radio 补给面板动态纹理生成失败，已回退直接绘制", exception);
            }
        }

        /**
         * 只把两张图标烤进动态纹理（其余像素透明）：卡片底、进度槽与数值都改由
         * {@link UiPaint} 逐帧绘制，保持既有"状态变化才重上传"的缓存策略不变。
         */
        private void paintBitmap() {
            // 0 = 完全透明；底色/文字已改由 UiPaint 逐帧绘制，不再写死颜色。
            pixels.fillRect(0, 0, PANEL_WIDTH, PANEL_HEIGHT, 0);
            drawIcon(ammoIcon, AMMO_ICON_X, AMMO_ICON_Y, false);
            drawIcon(constructionIcon, CONSTRUCTION_ICON_X, CONSTRUCTION_ICON_Y, true);
        }

        private void loadResourcesIfNecessary() {
            if (resourcesLoaded) return;
            closeSourceImages();
            ammoIcon = loadScaledIcon(AMMO_ICON);
            constructionIcon = loadScaledIcon(CONSTRUCTION_ICON);
            resourcesLoaded = true;
        }

        private NativeImage loadScaledIcon(ResourceLocation location) {
            NativeImage source = loadIcon(location);
            if (source == null) return null;
            try {
                int width = source.getWidth();
                int height = source.getHeight();
                int[] sourcePixels = new int[width * height];
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        sourcePixels[y * width + x] = source.getPixelRGBA(x, y);
                    }
                }
                int[] fitted = IconRasterizer.fitAbgr(
                    sourcePixels, width, height, ICON_SIZE, ICON_SIZE);
                NativeImage scaled = new NativeImage(ICON_SIZE, ICON_SIZE, true);
                try {
                    for (int y = 0; y < ICON_SIZE; y++) {
                        for (int x = 0; x < ICON_SIZE; x++) {
                            scaled.setPixelRGBA(x, y, fitted[y * ICON_SIZE + x]);
                        }
                    }
                    return scaled;
                } catch (RuntimeException exception) {
                    scaled.close();
                    throw exception;
                }
            } finally {
                source.close();
            }
        }

        private NativeImage loadIcon(ResourceLocation location) {
            try {
                var resource = Minecraft.getInstance().getResourceManager().getResource(location);
                if (resource.isEmpty()) return null;
                return NativeImage.read(resource.get().open());
            } catch (IOException | RuntimeException exception) {
                Espetro.LOGGER.warn("无法读取 HUD 图标 {}，使用内置像素图标", location, exception);
                return null;
            }
        }

        private void drawIcon(NativeImage source, int targetX, int targetY,
                              boolean constructionType) {
            if (source == null) {
                drawFallbackIcon(targetX, targetY, constructionType);
                return;
            }
            for (int dy = 0; dy < ICON_SIZE; dy++) {
                for (int dx = 0; dx < ICON_SIZE; dx++) {
                    int sampled = source.getPixelRGBA(dx, dy);
                    if ((sampled >>> 24) != 0) {
                        pixels.blendPixel(targetX + dx, targetY + dy, sampled);
                    }
                }
            }
        }

        private void drawFallbackIcon(int x, int y, boolean constructionType) {
            int symbol = argbToAbgr(constructionType ? UiTheme.WORLD_WARNING : UiTheme.WORLD_NEGATIVE);
            int center = ICON_SIZE / 2;
            int radius = Math.max(2, (ICON_SIZE - 2) / 2);
            for (int row = 0; row < ICON_SIZE; row++) {
                for (int column = 0; column < ICON_SIZE; column++) {
                    int dx = column - center;
                    int dy = row - center;
                    if (dx * dx + dy * dy <= radius * radius) {
                        pixels.setPixelRGBA(x + column, y + row,
                            argbToAbgr(UiTheme.ICON_PLATE));
                    }
                }
            }
            if (constructionType) {
                pixels.setPixelRGBA(x + 3, y + 2, symbol);
                pixels.setPixelRGBA(x + 4, y + 2, symbol);
                pixels.setPixelRGBA(x + 5, y + 3, symbol);
                pixels.setPixelRGBA(x + 5, y + 4, symbol);
                pixels.setPixelRGBA(x + 4, y + 5, symbol);
                pixels.setPixelRGBA(x + 3, y + 6, symbol);
                pixels.setPixelRGBA(x + 2, y + 7, symbol);
            } else {
                for (int bullet = 0; bullet < 3; bullet++) {
                    for (int i = 0; i < 3; i++) {
                        int px = x + 2 + bullet + i;
                        int py = y + 6 - i - bullet;
                        if (px < x + ICON_SIZE && py >= y) {
                            pixels.setPixelRGBA(px, py, symbol);
                        }
                    }
                }
            }
        }

        /** 动态纹理不可用时的兜底：仍按新风格画两个语义色图标底，卡片与数值照常绘制。 */
        private void drawFallbackIcons(GuiGraphics graphics, int x, int y) {
            drawFallbackIconMark(graphics, x + AMMO_ICON_X, y + AMMO_ICON_Y, UiTheme.WORLD_NEGATIVE);
            drawFallbackIconMark(graphics, x + CONSTRUCTION_ICON_X, y + CONSTRUCTION_ICON_Y,
                UiTheme.WORLD_WARNING);
        }

        private void drawFallbackIconMark(GuiGraphics graphics, int x, int y, int accent) {
            int centerX = x + ICON_SIZE / 2;
            int centerY = y + ICON_SIZE / 2;
            int radius = Math.max(2, ICON_SIZE / 2 - 1);
            UiPaint.circle(graphics, centerX, centerY, radius, UiTheme.alpha(accent, 0.22f));
            UiPaint.ring(graphics, centerX, centerY, radius, accent);
        }

        private void dispose() {
            if (textureLocation != null) {
                Minecraft.getInstance().getTextureManager().release(textureLocation);
            } else if (texture != null) {
                texture.close();
            } else if (pixels != null) {
                pixels.close();
            }
            closeSourceImages();
            textureLocation = null;
            texture = null;
            pixels = null;
            ammoIcon = null;
            constructionIcon = null;
        }

        private void closeSourceImages() {
            if (ammoIcon != null) ammoIcon.close();
            if (constructionIcon != null) constructionIcon.close();
            ammoIcon = null;
            constructionIcon = null;
        }

        private static int argbToAbgr(int argb) {
            return (argb & 0xFF00FF00)
                | ((argb & 0x00FF0000) >>> 16)
                | ((argb & 0x000000FF) << 16);
        }
    }
}
