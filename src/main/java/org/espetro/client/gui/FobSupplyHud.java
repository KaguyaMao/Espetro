package org.espetro.client.gui;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import org.espetro.Espetro;
import org.espetro.network.FobSupplySyncPacket;
import org.espetro.client.aui.GuiElement;


/**
 * Radio 范围补给状态缓存与 AUI HUD 元素。
 *
 * <p>关键信息变化时，原素材补给图标重新烘焙并上传成一张透明底的高分辨率动态纹理；
 * 相同数据不会重建或上传。卡片底（圆角玻璃）、进度槽与数值每帧由 {@link UiPaint}
 * 直接绘制，普通帧没有位图重建成本。</p>
 */
public final class FobSupplyHud {

    private static final int HUD_X = 8;
    private static final int HUD_Y = 8;
    private static final int PANEL_WIDTH = 120;
    private static final int PANEL_HEIGHT = 26;
    // 卡片内边距：左右各留 6px，顶部 4px，底部 2px（贴边不糊）。
    private static final int BAR_X = 6;
    private static final int BAR_Y = 4;
    private static final int BAR_HEIGHT = 4;
    private static final int BAR_WIDTH = 108;
    private static final int ICON_SIZE = 14;
    private static final int AMMO_ICON_X = 7;
    private static final int AMMO_ICON_Y = 10;
    private static final int AMMO_TEXT_X = 24;
    private static final int CONSTRUCTION_ICON_X = 64;
    private static final int CONSTRUCTION_ICON_Y = 10;
    private static final int CONSTRUCTION_TEXT_X = 81;
    private static final int VALUE_TEXT_Y = 12;

    private static final int ICON_RASTER_SCALE = 4;

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

    /** Resource-pack reloads invalidate the cached glyph atlas, not the HUD state. */
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
        private SupplyIcons.Sources iconSources;
        private DynamicTexture texture;
        private ResourceLocation textureLocation;
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
            closeIconSources();
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

            // 无框玻璃卡；数值和图标保留清晰对比。
            UiPaint.card(graphics, panelX, panelY, PANEL_WIDTH, PANEL_HEIGHT);
            // 电台血量沿用统一进度条。
            float healthRatio = radioMaxHealth <= 0 ? 0f : radioHealth / (float) radioMaxHealth;
            UiPaint.progress(graphics, panelX + BAR_X, panelY + BAR_Y,
                BAR_WIDTH, BAR_HEIGHT, healthRatio, UiTheme.ACCENT);

            if (textureLocation != null && !failed) {
                HudRenderState.begin(graphics);
                graphics.blit(textureLocation, panelX, panelY,
                    PANEL_WIDTH, PANEL_HEIGHT, 0f, 0f,
                    PANEL_WIDTH * ICON_RASTER_SCALE, PANEL_HEIGHT * ICON_RASTER_SCALE,
                    PANEL_WIDTH * ICON_RASTER_SCALE, PANEL_HEIGHT * ICON_RASTER_SCALE);
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
                    pixels = new NativeImage(PANEL_WIDTH * ICON_RASTER_SCALE,
                        PANEL_HEIGHT * ICON_RASTER_SCALE, true);
                }
                if (iconSources == null) iconSources = new SupplyIcons.Sources();
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
            } catch (java.io.IOException | RuntimeException exception) {
                failed = true;
                Espetro.LOGGER.warn("Radio 补给面板动态纹理生成失败，已回退直接绘制", exception);
            }
        }

        /** Sample the original sprites at four pixels per GUI unit instead of flattening them to 10px. */
        private void paintBitmap() {
            pixels.fillRect(0, 0, pixels.getWidth(), pixels.getHeight(), 0);
            iconSources.paint(pixels, AMMO_ICON_X * ICON_RASTER_SCALE,
                AMMO_ICON_Y * ICON_RASTER_SCALE, ICON_SIZE * ICON_RASTER_SCALE, false);
            iconSources.paint(pixels, CONSTRUCTION_ICON_X * ICON_RASTER_SCALE,
                CONSTRUCTION_ICON_Y * ICON_RASTER_SCALE, ICON_SIZE * ICON_RASTER_SCALE, true);
        }

        /** Texture failure draws the same original source sprites directly. */
        private void drawFallbackIcons(GuiGraphics graphics, int x, int y) {
            SupplyIcons.ammunition(graphics, x + AMMO_ICON_X, y + AMMO_ICON_Y, ICON_SIZE);
            SupplyIcons.construction(graphics, x + CONSTRUCTION_ICON_X,
                y + CONSTRUCTION_ICON_Y, ICON_SIZE);
        }

        private void dispose() {
            if (textureLocation != null) {
                Minecraft.getInstance().getTextureManager().release(textureLocation);
            } else if (texture != null) {
                texture.close();
            } else if (pixels != null) {
                pixels.close();
            }
            closeIconSources();
            textureLocation = null;
            texture = null;
            pixels = null;
        }

        private void closeIconSources() {
            if (iconSources != null) iconSources.close();
            iconSources = null;
        }
    }
}
