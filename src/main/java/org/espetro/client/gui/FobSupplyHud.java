package org.espetro.client.gui;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.espetro.Espetro;
import org.espetro.network.FobSupplySyncPacket;
import org.espetro.client.aui.GuiElement;


/**
 * Radio 范围补给状态缓存与 AUI HUD 元素。
 *
 * <p>关键信息变化时，原素材补给图标重新烘焙并上传成一张透明底的高分辨率动态纹理；
 * 相同数据不会重建或上传。进度槽与数值每帧直接绘制，普通帧没有位图重建成本。</p>
 */
public final class FobSupplyHud {

    private static final int PANEL_WIDTH = 104;
    private static final int PANEL_HEIGHT = 39;
    private static final int PAD = 5;
    private static final int ICON_SIZE = 11;
    private static final int CONTENT_X = PAD + ICON_SIZE + 4;
    private static final int RADIO_CENTER = 8;
    private static final int SUPPLY_CENTER = 20;
    private static final int HAB_CENTER = 32;
    private static final int BAR_HEIGHT = 3;
    private static final int BAR_WIDTH = PANEL_WIDTH - CONTENT_X - PAD;
    private static final int CONSTRUCTION_X = PANEL_WIDTH / 2;
    private static final ResourceLocation RADIO_ICON = ResourceLocation.fromNamespaceAndPath(
        "espetro", "textures/gui/map/radio.png");
    private static final ResourceLocation HAB_ICON = ResourceLocation.fromNamespaceAndPath(
        "espetro", "textures/gui/map/hab.png");

    private static final int ICON_RASTER_SCALE = 4;

    private static boolean inRange;
    private static int construction;
    private static int ammunition;
    private static int maxConstruction = 1;
    private static int maxAmmunition = 1;
    private static int radioHealth;
    private static int radioMaxHealth = 1;
    private static FobSupplySyncPacket.HabStatus habStatus = FobSupplySyncPacket.HabStatus.UNKNOWN;
    private static SupplyElement activeElement;
    private static long stateRevision;

    private FobSupplyHud() {
    }

    static boolean isShowing() { return inRange; }

    static int bottomEdge(int height) { return topInset(height) + PANEL_HEIGHT; }

    private static int topInset(int height) { return Math.max(4, Math.round(height * 0.016f)); }

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
            && radioMaxHealth == nextRadioMaxHealth
            && habStatus == packet.getHabStatus()) {
            return;
        }
        inRange = true;
        construction = nextConstruction;
        ammunition = nextAmmunition;
        maxConstruction = nextMaxConstruction;
        maxAmmunition = nextMaxAmmunition;
        radioHealth = nextRadioHealth;
        radioMaxHealth = nextRadioMaxHealth;
        habStatus = packet.getHabStatus();
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
        habStatus = FobSupplySyncPacket.HabStatus.UNKNOWN;
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
            super(0, 0, PANEL_WIDTH, PANEL_HEIGHT);
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
            int panelX = parentX + Math.max(4, Math.round(drawWidth * 0.008f));
            int panelY = parentY + topInset(drawHeight);

            drawStatusIcon(graphics, RADIO_ICON, panelX + PAD, panelY + RADIO_CENTER,
                26, 76, UiTheme.HUD_CYAN);
            int barX = panelX + CONTENT_X;
            int barY = panelY + RADIO_CENTER - BAR_HEIGHT / 2;
            graphics.fill(barX, barY, barX + BAR_WIDTH, barY + BAR_HEIGHT, UiTheme.HUD_TRACK);
            int filled = scaledBarWidth(radioHealth, radioMaxHealth, BAR_WIDTH);
            if (filled > 0) graphics.fill(barX, barY, barX + filled, barY + BAR_HEIGHT, UiTheme.HUD_CYAN);

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

            int textY = panelY + SUPPLY_CENTER - Minecraft.getInstance().font.lineHeight / 2;
            UiPaint.textScaled(graphics, Integer.toString(ammunition), panelX + CONTENT_X,
                textY, UiTheme.WORLD_NEGATIVE, 1f, true);
            UiPaint.textScaled(graphics, Integer.toString(construction), panelX + CONSTRUCTION_X + ICON_SIZE + 4,
                textY, UiTheme.WORLD_WARNING, 1f, true);

            boolean operational = habStatus == FobSupplySyncPacket.HabStatus.ACTIVE;
            drawStatusIcon(graphics, HAB_ICON, panelX + PAD, panelY + HAB_CENTER,
                43, 51, operational ? UiTheme.HUD_CYAN : UiTheme.WORLD_MUTED);
            String status = switch (habStatus) {
                case ACTIVE -> "hud.espetro.fob.hab_active";
                case INACTIVE -> "hud.espetro.fob.hab_inactive";
                case UNKNOWN -> "hud.espetro.fob.hab_unknown";
            };
            int habTextHeight = Math.round(Minecraft.getInstance().font.lineHeight * UiTheme.TYPE_MICRO);
            UiPaint.textScaled(graphics, Component.translatable(status).getString(),
                panelX + CONTENT_X, panelY + HAB_CENTER - habTextHeight / 2,
                operational ? UiTheme.WORLD_TEXT : UiTheme.WORLD_MUTED,
                UiTheme.TYPE_MICRO, true);
        }

        private static void drawStatusIcon(GuiGraphics graphics, ResourceLocation icon,
                                           int x, int centerY, int sourceY, int sourceHeight, int color) {
            // Both map glyphs have a 91px wide painted region in a 128px square.
            int height = Math.round(ICON_SIZE * sourceHeight / 91f);
            UiPaint.tint(graphics, color);
            graphics.blit(icon, x, centerY - height / 2, ICON_SIZE, height,
                19, sourceY, 91, sourceHeight, 128, 128);
            UiPaint.tint(graphics, UiTheme.TEXTURE_WHITE);
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
            int y = SUPPLY_CENTER * ICON_RASTER_SCALE - ICON_SIZE * ICON_RASTER_SCALE / 2;
            iconSources.paint(pixels, PAD * ICON_RASTER_SCALE,
                y, ICON_SIZE * ICON_RASTER_SCALE, false);
            iconSources.paint(pixels, CONSTRUCTION_X * ICON_RASTER_SCALE,
                y, ICON_SIZE * ICON_RASTER_SCALE, true);
        }

        /** Texture failure draws the same original source sprites directly. */
        private void drawFallbackIcons(GuiGraphics graphics, int x, int y) {
            int iconY = y + SUPPLY_CENTER - ICON_SIZE / 2;
            SupplyIcons.ammunition(graphics, x + PAD, iconY, ICON_SIZE);
            SupplyIcons.construction(graphics, x + CONSTRUCTION_X, iconY, ICON_SIZE);
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
