package org.espetro.client.gui;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;

/** Original supply sprites, without an extra backing plate or recoloring. */
final class SupplyIcons {
    private static final ResourceLocation AMMUNITION = ResourceLocation.fromNamespaceAndPath(
        "espetro", "textures/gui/squad/ammo_supply.png");
    private static final ResourceLocation CONSTRUCTION = ResourceLocation.fromNamespaceAndPath(
        "espetro", "textures/gui/squad/construction_supply.png");
    private static final int SOURCE_SIZE = 128;
    /** Original transparent padding; the complete black circular backing remains in the crop. */
    private static final int SOURCE_PADDING = 10;
    private static final int SOURCE_CONTENT = SOURCE_SIZE - SOURCE_PADDING * 2;

    private SupplyIcons() { }

    static void ammunition(GuiGraphics graphics, int x, int y, int size) {
        draw(graphics, AMMUNITION, x, y, size);
    }

    static void construction(GuiGraphics graphics, int x, int y, int size) {
        draw(graphics, CONSTRUCTION, x, y, size);
    }

    private static void draw(GuiGraphics graphics, ResourceLocation sprite, int x, int y, int size) {
        if (size <= 0) return;
        graphics.blit(sprite, x, y, size, size, SOURCE_PADDING, SOURCE_PADDING,
            SOURCE_CONTENT, SOURCE_CONTENT, SOURCE_SIZE, SOURCE_SIZE);
    }

    /** Owns source images only for the event-redrawn FOB atlas; reload/dispose releases them. */
    static final class Sources implements AutoCloseable {
        private NativeImage ammunition;
        private NativeImage construction;
        private int cachedSize;
        private int[] fittedAmmunition;
        private int[] fittedConstruction;

        Sources() throws IOException {
            try {
                ammunition = read(AMMUNITION);
                construction = read(CONSTRUCTION);
            } catch (IOException | RuntimeException failure) {
                close();
                throw failure;
            }
        }

        void paint(NativeImage destination, int x, int y, int size, boolean constructionType) {
            if (size <= 0) return;
            if (cachedSize != size) {
                fittedAmmunition = fit(ammunition, size);
                fittedConstruction = fit(construction, size);
                cachedSize = size;
            }
            int[] pixels = constructionType ? fittedConstruction : fittedAmmunition;
            for (int row = 0; row < size; row++) {
                for (int column = 0; column < size; column++) {
                    int sample = pixels[row * size + column];
                    if ((sample >>> 24) != 0) destination.blendPixel(x + column, y + row, sample);
                }
            }
        }

        private static NativeImage read(ResourceLocation location) throws IOException {
            var resource = Minecraft.getInstance().getResourceManager().getResource(location);
            if (resource.isEmpty()) throw new IOException("Missing supply sprite: " + location);
            try (var input = resource.get().open()) {
                return NativeImage.read(input);
            }
        }

        private static int[] fit(NativeImage source, int size) {
            int width = source.getWidth();
            int height = source.getHeight();
            int[] pixels = new int[width * height];
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) pixels[y * width + x] = source.getPixelRGBA(x, y);
            }
            // Premultiplied-alpha area sampling retains antialiased edges and the original black disc.
            return IconRasterizer.fitAbgr(pixels, width, height, size, size);
        }

        @Override
        public void close() {
            if (ammunition != null) ammunition.close();
            if (construction != null) construction.close();
            ammunition = null;
            construction = null;
            fittedAmmunition = null;
            fittedConstruction = null;
            cachedSize = 0;
        }
    }
}
