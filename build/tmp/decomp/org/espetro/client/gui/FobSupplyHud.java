/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.client.gui;

import com.mojang.blaze3d.platform.NativeImage;
import java.io.IOException;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import org.espetro.Espetro;
import org.espetro.client.aui.GuiElement;
import org.espetro.client.gui.HudRenderState;
import org.espetro.client.gui.IconRasterizer;
import org.espetro.network.FobSupplySyncPacket;

public final class FobSupplyHud {
    private static final int HUD_X = 8;
    private static final int HUD_Y = 8;
    private static final int PANEL_WIDTH = 104;
    private static final int PANEL_HEIGHT = 22;
    private static final int BAR_Y = 3;
    private static final int BAR_HEIGHT = 3;
    private static final int BAR_X = 4;
    private static final int BAR_WIDTH = 96;
    private static final int HALF_BAR_WIDTH = 48;
    private static final int AMMO_BAR_X = 4;
    private static final int CONSTRUCTION_BAR_X = 52;
    private static final int ICON_SIZE = 10;
    private static final int PANEL_BACKGROUND = -804187887;
    private static final int PANEL_BORDER = -1335991188;
    private static final int BAR_BACKGROUND = -13421773;
    private static final int BAR_HEALTH_COLOR = -11887617;
    private static final int AMMO_COLOR = -3390396;
    private static final int CONSTRUCTION_COLOR = -2706688;
    private static final ResourceLocation AMMO_ICON = ResourceLocation.fromNamespaceAndPath((String)"espetro", (String)"textures/gui/squad/ammo_supply.png");
    private static final ResourceLocation CONSTRUCTION_ICON = ResourceLocation.fromNamespaceAndPath((String)"espetro", (String)"textures/gui/squad/construction_supply.png");
    private static final ResourceLocation VANILLA_ASCII_FONT = ResourceLocation.fromNamespaceAndPath((String)"minecraft", (String)"textures/font/ascii.png");
    private static boolean inRange;
    private static int construction;
    private static int ammunition;
    private static int maxConstruction;
    private static int maxAmmunition;
    private static int radioHealth;
    private static int radioMaxHealth;
    private static SupplyElement activeElement;
    private static long stateRevision;

    private FobSupplyHud() {
    }

    public static void register() {
    }

    public static void update(FobSupplySyncPacket packet) {
        if (packet == null || !packet.isInRange()) {
            FobSupplyHud.clear();
            return;
        }
        int nextConstruction = Math.max(0, packet.getConstruction());
        int nextAmmunition = Math.max(0, packet.getAmmunition());
        int nextMaxConstruction = Math.max(1, packet.getMaxConstruction());
        int nextMaxAmmunition = Math.max(1, packet.getMaxAmmunition());
        int nextRadioHealth = Math.max(0, packet.getRadioHealth());
        int nextRadioMaxHealth = Math.max(1, packet.getRadioMaxHealth());
        if (inRange && construction == nextConstruction && ammunition == nextAmmunition && maxConstruction == nextMaxConstruction && maxAmmunition == nextMaxAmmunition && radioHealth == nextRadioHealth && radioMaxHealth == nextRadioMaxHealth) {
            return;
        }
        inRange = true;
        construction = nextConstruction;
        ammunition = nextAmmunition;
        maxConstruction = nextMaxConstruction;
        maxAmmunition = nextMaxAmmunition;
        radioHealth = nextRadioHealth;
        radioMaxHealth = nextRadioMaxHealth;
        ++stateRevision;
        FobSupplyHud.applyStateToElement();
    }

    public static void clear() {
        if (!inRange) {
            return;
        }
        inRange = false;
        ++stateRevision;
        FobSupplyHud.applyStateToElement();
    }

    public static void onResourceReload() {
        if (activeElement != null) {
            activeElement.invalidateResources();
        }
    }

    static GuiElement createElement() {
        if (activeElement != null) {
            activeElement.dispose();
        }
        activeElement = new SupplyElement();
        activeElement.applyState();
        return activeElement;
    }

    private static void applyStateToElement() {
        if (activeElement != null) {
            activeElement.applyState();
        }
    }

    static int scaledBarWidth(int value, int maximum, int width) {
        if (value <= 0 || maximum <= 0 || width <= 0) {
            return 0;
        }
        long clamped = Math.min((long)value, (long)maximum);
        return Math.max(1, (int)Math.min((long)width, Math.round((double)(clamped * (long)width) / (double)maximum)));
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
        stateRevision = 0L;
        activeElement = null;
    }

    static {
        maxConstruction = 1;
        maxAmmunition = 1;
        radioMaxHealth = 1;
    }

    private static final class SupplyElement
    extends GuiElement {
        private NativeImage pixels;
        private DynamicTexture texture;
        private ResourceLocation textureLocation;
        private NativeImage ammoIcon;
        private NativeImage constructionIcon;
        private NativeImage asciiFont;
        private boolean resourcesLoaded;
        private boolean dirty = true;
        private boolean failed;

        private SupplyElement() {
            super(8, 8, 104, 22);
        }

        private void applyState() {
            this.setVisible(inRange);
            if (inRange) {
                this.dirty = true;
            }
        }

        private void invalidateResources() {
            this.resourcesLoaded = false;
            this.failed = false;
            this.dirty = true;
        }

        @Override
        public void draw(GuiGraphics graphics, int parentX, int parentY, int drawWidth, int drawHeight, int mouseX, int mouseY, float partialTick) {
            if (!this.isVisible()) {
                return;
            }
            if (this.dirty && !this.failed) {
                this.rebuildTexture();
            }
            if (this.textureLocation == null) {
                this.drawSafeFallback(graphics, parentX + this.getX(), parentY + this.getY());
                return;
            }
            HudRenderState.begin(graphics);
            graphics.m_280163_(this.textureLocation, parentX + this.getX(), parentY + this.getY(), 0.0f, 0.0f, 104, 22, 104, 22);
            HudRenderState.restore(graphics);
        }

        private void rebuildTexture() {
            try {
                if (this.pixels == null) {
                    this.pixels = new NativeImage(104, 22, true);
                }
                this.loadResourcesIfNecessary();
                this.paintBitmap();
                if (this.texture == null) {
                    this.texture = new DynamicTexture(this.pixels);
                    this.texture.m_117960_(false, false);
                    this.textureLocation = Minecraft.m_91087_().m_91097_().m_118490_("espetro_fob_supply", this.texture);
                } else {
                    this.texture.m_117985_();
                }
                this.dirty = false;
            }
            catch (RuntimeException exception) {
                this.failed = true;
                Espetro.LOGGER.warn("Radio \u8865\u7ed9\u9762\u677f\u52a8\u6001\u7eb9\u7406\u751f\u6210\u5931\u8d25\uff0c\u5df2\u56de\u9000\u76f4\u63a5\u7ed8\u5236", (Throwable)exception);
            }
        }

        private void paintBitmap() {
            this.pixels.m_84997_(0, 0, 104, 22, 0);
            this.fillArgb(0, 0, 104, 22, -804187887);
            this.fillArgb(0, 0, 104, 1, -1335991188);
            this.fillArgb(0, 21, 104, 1, -1335991188);
            this.fillArgb(0, 1, 1, 20, -1335991188);
            this.fillArgb(103, 1, 1, 20, -1335991188);
            this.fillArgb(4, 3, 96, 3, -13421773);
            int healthWidth = FobSupplyHud.scaledBarWidth(radioHealth, radioMaxHealth, 96);
            if (healthWidth > 0) {
                this.fillArgb(4, 3, healthWidth, 3, -11887617);
            }
            this.drawIcon(this.ammoIcon, 8, 9, false);
            this.drawIcon(this.constructionIcon, 57, 9, true);
            this.drawNumber(Integer.toString(ammunition), 19, 10);
            this.drawNumber(Integer.toString(construction), 68, 10);
        }

        private void fillArgb(int x, int y, int width, int height, int argb) {
            if (width <= 0 || height <= 0) {
                return;
            }
            this.pixels.m_84997_(x, y, width, height, SupplyElement.argbToAbgr(argb));
        }

        private void drawNumber(String value, int x, int y) {
            if (this.asciiFont == null) {
                return;
            }
            int cursor = x;
            for (int i = 0; i < value.length(); ++i) {
                int column;
                int row;
                char character = value.charAt(i);
                if (character < '0' || character > '9') continue;
                int glyphX = (character & 0xF) * 8;
                int glyphY = (character >>> 4 & 0xF) * 8;
                int left = 8;
                int right = -1;
                for (row = 0; row < 8; ++row) {
                    for (column = 0; column < 8; ++column) {
                        if (this.asciiFont.m_84985_(glyphX + column, glyphY + row) >>> 24 == 0) continue;
                        left = Math.min(left, column);
                        right = Math.max(right, column);
                    }
                }
                if (right < left) {
                    cursor += 4;
                    continue;
                }
                for (row = 0; row < 8; ++row) {
                    for (column = left; column <= right; ++column) {
                        int alpha = this.asciiFont.m_84985_(glyphX + column, glyphY + row) >>> 24;
                        int targetX = cursor + column - left;
                        int targetY = y + row;
                        if (alpha == 0 || targetX >= 104 || targetY >= 22) continue;
                        this.pixels.m_166411_(targetX, targetY, alpha << 24 | 0xFFFFFF);
                    }
                }
                cursor += right - left + 2;
            }
        }

        private void loadResourcesIfNecessary() {
            if (this.resourcesLoaded) {
                return;
            }
            this.closeSourceImages();
            this.ammoIcon = this.loadScaledIcon(AMMO_ICON);
            this.constructionIcon = this.loadScaledIcon(CONSTRUCTION_ICON);
            this.asciiFont = this.loadIcon(VANILLA_ASCII_FONT);
            this.resourcesLoaded = true;
        }

        private NativeImage loadScaledIcon(ResourceLocation location) {
            NativeImage source = this.loadIcon(location);
            if (source == null) {
                return null;
            }
            try {
                int width = source.m_84982_();
                int height = source.m_85084_();
                int[] sourcePixels = new int[width * height];
                for (int y = 0; y < height; ++y) {
                    for (int x = 0; x < width; ++x) {
                        sourcePixels[y * width + x] = source.m_84985_(x, y);
                    }
                }
                int[] fitted = IconRasterizer.fitAbgr(sourcePixels, width, height, 10, 10);
                NativeImage scaled = new NativeImage(10, 10, true);
                try {
                    for (int y = 0; y < 10; ++y) {
                        for (int x = 0; x < 10; ++x) {
                            scaled.m_84988_(x, y, fitted[y * 10 + x]);
                        }
                    }
                    NativeImage y = scaled;
                    return y;
                }
                catch (RuntimeException exception) {
                    scaled.close();
                    throw exception;
                }
            }
            finally {
                source.close();
            }
        }

        private NativeImage loadIcon(ResourceLocation location) {
            try {
                Optional<Resource> resource = Minecraft.m_91087_().m_91098_().m_213713_(location);
                if (resource.isEmpty()) {
                    return null;
                }
                return NativeImage.m_85058_(resource.get().m_215507_());
            }
            catch (IOException | RuntimeException exception) {
                Espetro.LOGGER.warn("\u65e0\u6cd5\u8bfb\u53d6 HUD \u56fe\u6807 {}\uff0c\u4f7f\u7528\u5185\u7f6e\u50cf\u7d20\u56fe\u6807", (Object)location, (Object)exception);
                return null;
            }
        }

        private void drawIcon(NativeImage source, int targetX, int targetY, boolean constructionType) {
            if (source == null) {
                this.drawFallbackIcon(targetX, targetY, constructionType);
                return;
            }
            for (int dy = 0; dy < 10; ++dy) {
                for (int dx = 0; dx < 10; ++dx) {
                    int sampled = source.m_84985_(dx, dy);
                    if (sampled >>> 24 == 0) continue;
                    this.pixels.m_166411_(targetX + dx, targetY + dy, sampled);
                }
            }
        }

        private void drawFallbackIcon(int x, int y, boolean constructionType) {
            int symbol = SupplyElement.argbToAbgr(constructionType ? -2706688 : -3390396);
            int center = 5;
            int radius = Math.max(2, 4);
            for (int row = 0; row < 10; ++row) {
                for (int column = 0; column < 10; ++column) {
                    int dx = column - center;
                    int dy = row - center;
                    if (dx * dx + dy * dy > radius * radius) continue;
                    this.pixels.m_84988_(x + column, y + row, -16250872);
                }
            }
            if (constructionType) {
                this.pixels.m_84988_(x + 3, y + 2, symbol);
                this.pixels.m_84988_(x + 4, y + 2, symbol);
                this.pixels.m_84988_(x + 5, y + 3, symbol);
                this.pixels.m_84988_(x + 5, y + 4, symbol);
                this.pixels.m_84988_(x + 4, y + 5, symbol);
                this.pixels.m_84988_(x + 3, y + 6, symbol);
                this.pixels.m_84988_(x + 2, y + 7, symbol);
            } else {
                for (int bullet = 0; bullet < 3; ++bullet) {
                    for (int i = 0; i < 3; ++i) {
                        int px = x + 2 + bullet + i;
                        int py = y + 6 - i - bullet;
                        if (px >= x + 10 || py < y) continue;
                        this.pixels.m_84988_(px, py, symbol);
                    }
                }
            }
        }

        private void drawSafeFallback(GuiGraphics graphics, int x, int y) {
            graphics.m_280509_(x, y, x + 104, y + 22, -804187887);
            graphics.m_280637_(x, y, 104, 22, -1335991188);
            graphics.m_280509_(x + 4, y + 3, x + 4 + 96, y + 3 + 3, -13421773);
            int healthWidth = FobSupplyHud.scaledBarWidth(radioHealth, radioMaxHealth, 96);
            if (healthWidth > 0) {
                graphics.m_280509_(x + 4, y + 3, x + 4 + healthWidth, y + 3 + 3, -11887617);
            }
            graphics.m_280056_(Minecraft.m_91087_().f_91062_, Integer.toString(ammunition), x + 19, y + 11, -1, false);
            graphics.m_280056_(Minecraft.m_91087_().f_91062_, Integer.toString(construction), x + 68, y + 11, -1, false);
        }

        private void dispose() {
            if (this.textureLocation != null) {
                Minecraft.m_91087_().m_91097_().m_118513_(this.textureLocation);
            } else if (this.texture != null) {
                this.texture.close();
            } else if (this.pixels != null) {
                this.pixels.close();
            }
            this.closeSourceImages();
            this.textureLocation = null;
            this.texture = null;
            this.pixels = null;
            this.ammoIcon = null;
            this.constructionIcon = null;
            this.asciiFont = null;
        }

        private void closeSourceImages() {
            if (this.ammoIcon != null) {
                this.ammoIcon.close();
            }
            if (this.constructionIcon != null) {
                this.constructionIcon.close();
            }
            if (this.asciiFont != null) {
                this.asciiFont.close();
            }
            this.ammoIcon = null;
            this.constructionIcon = null;
            this.asciiFont = null;
        }

        private static int argbToAbgr(int argb) {
            return argb & 0xFF00FF00 | (argb & 0xFF0000) >>> 16 | (argb & 0xFF) << 16;
        }
    }
}

