package org.espetro.client.gui;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.nio.ByteBuffer;

/**
 * 主目标截屏 → 强降采样 → 线性放大，得到一层"毛玻璃"底：不遮死玩家视野，
 * 但足以把世界压成不易读的色块，让叠在上面的 HUD 文字仍然清楚。
 *
 * <p>实现取舍：不引入 post-chain / FBO 后处理（1.20.1 原版没有模糊链，自己写 shader
 * 在整合包里风险高），而是每秒最多对主帧缓冲做一次 {@code glReadPixels}，在 CPU 上按
 * 块平均到 256×144，再用双线性过滤放大铺满屏幕。块平均本身已经抹掉高频细节，
 * 放大后的模糊感接近高斯模糊，代价只有一秒一次（约 10~20ms）的读回。</p>
 */
public final class FrostedBackdrop {

    /** 归一化底图尺寸（固定，避免窗口缩放时重建纹理）。 */
    private static final int TEX_WIDTH = 256;
    private static final int TEX_HEIGHT = 144;
    /** 两次截屏之间的最小间隔：读回是同步的（会卡一帧），所以只做低频刷新。 */
    private static final long REFRESH_INTERVAL_MS = 2500L;
    private static final ResourceLocation TEXTURE_ID =
        ResourceLocation.fromNamespaceAndPath("espetro", "dynamic/frosted_backdrop");

    private static DynamicTexture texture;
    private static boolean registered;
    private static boolean loggedFailure;
    private static final org.slf4j.Logger LOGGER = com.mojang.logging.LogUtils.getLogger();
    /** 首帧/重开界面时必须重截；用独立标志位，避免 Long.MIN_VALUE 参与减法溢出。 */
    private static boolean dirty = true;
    private static long lastCaptureMs;

    private FrostedBackdrop() {
    }

    /** 界面重开/窗口尺寸变化后强制重新截屏。 */
    public static void invalidate() {
        dirty = true;
    }

    /**
     * 画出毛玻璃底。必须在界面内容之前调用：此刻主帧缓冲里还只有世界画面，
     * 截到的才是"玩家视野"而不是叠了 UI 的画面。
     */
    public static void draw(GuiGraphics graphics, int width, int height) {
        if (width <= 0 || height <= 0) {
            return;
        }
        long now = System.currentTimeMillis();
        if (dirty || now - lastCaptureMs >= REFRESH_INTERVAL_MS) {
            lastCaptureMs = now;
            dirty = false;
            if (!capture()) {
                // 截屏失败（离屏渲染 / 驱动拒绝读回）时只压一层暗幕：
                // 宁可没有模糊，也不能用不透明底把玩家视野盖掉。
                dirty = true;
                graphics.fill(0, 0, width, height, UiTheme.FROST_SCRIM);
                return;
            }
        }
        if (texture == null) {
            graphics.fill(0, 0, width, height, UiTheme.FROST_SCRIM);
            return;
        }
        graphics.flush();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        // 直接画一个全屏四边形，不经过 GuiGraphics#blit（后者会把纹理当作 atlas 算 UV）。
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, TEXTURE_ID);
        Matrix4f matrix = graphics.pose().last().pose();
        BufferBuilder builder = Tesselator.getInstance().getBuilder();
        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        builder.vertex(matrix, 0.0F, height, 0.0F).uv(0.0F, 1.0F).endVertex();
        builder.vertex(matrix, width, height, 0.0F).uv(1.0F, 1.0F).endVertex();
        builder.vertex(matrix, width, 0.0F, 0.0F).uv(1.0F, 0.0F).endVertex();
        builder.vertex(matrix, 0.0F, 0.0F, 0.0F).uv(0.0F, 0.0F).endVertex();
        BufferUploader.drawWithShader(builder.end());
        // 压暗 + 极轻的颗粒，让底图不出现色带，也保证白字对比度。
        graphics.fill(0, 0, width, height, UiTheme.FROST_SCRIM);
        UiPaint.grain(graphics, width, height, 0.03f);
    }

    /** @return 是否拿到了可用帧。 */
    private static boolean capture() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft == null || minecraft.getWindow() == null) {
            return false;
        }
        RenderTarget target = minecraft.getMainRenderTarget();
        int pixelWidth = target.width;
        int pixelHeight = target.height;
        if (pixelWidth <= 0 || pixelHeight <= 0) {
            return false;
        }
        ByteBuffer buffer;
        try {
            buffer = ByteBuffer.allocateDirect(pixelWidth * pixelHeight * 4);
            target.bindWrite(true);
            GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1);
            GL11.glReadPixels(0, 0, pixelWidth, pixelHeight,
                GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
        } catch (RuntimeException exception) {
            if (!loggedFailure) {
                loggedFailure = true;
                LOGGER.warn("毛玻璃底图截屏失败，改为纯压暗底", exception);
            }
            return false;
        }
        NativeImage image = new NativeImage(TEX_WIDTH, TEX_HEIGHT, false);
        try {
            long luma = downsample(buffer, pixelWidth, pixelHeight, image);
            // 全黑说明读回没拿到画面（某些驱动/离屏情况下会全 0），不要拿黑图盖住视野。
            if (luma < 3L) {
                return false;
            }
            if (texture == null) {
                texture = new DynamicTexture(image);
                texture.setFilter(true, false);
                image = null;
                if (!registered) {
                    minecraft.getTextureManager().register(TEXTURE_ID, texture);
                    registered = true;
                }
            } else {
                texture.setPixels(image);
                texture.upload();
            }
            return true;
        } finally {
            if (image != null) {
                image.close();
            }
        }
    }

    /**
     * 块平均降采样并同时上下翻转（{@code glReadPixels} 第 0 行是屏幕底部）。
     */
    private static long downsample(ByteBuffer buffer, int pixelWidth, int pixelHeight,
                                   NativeImage out) {
        long lumaTotal = 0L;
        int lumaCount = 0;
        float stepX = (float) pixelWidth / TEX_WIDTH;
        float stepY = (float) pixelHeight / TEX_HEIGHT;
        int sampleStepX = Math.max(1, Math.round(stepX / 2.0F));
        int sampleStepY = Math.max(1, Math.round(stepY / 2.0F));
        for (int y = 0; y < TEX_HEIGHT; y++) {
            int rowStart = Math.min(pixelHeight - 1, (int) (y * stepY));
            int rowEnd = Math.min(pixelHeight, Math.max(rowStart + 1, (int) ((y + 1) * stepY)));
            for (int x = 0; x < TEX_WIDTH; x++) {
                int colStart = Math.min(pixelWidth - 1, (int) (x * stepX));
                int colEnd = Math.min(pixelWidth, Math.max(colStart + 1, (int) ((x + 1) * stepX)));
                long red = 0L;
                long green = 0L;
                long blue = 0L;
                int count = 0;
                for (int sourceY = rowStart; sourceY < rowEnd; sourceY += sampleStepY) {
                    int flippedY = pixelHeight - 1 - sourceY;
                    int rowOffset = flippedY * pixelWidth;
                    for (int sourceX = colStart; sourceX < colEnd; sourceX += sampleStepX) {
                        int index = (rowOffset + sourceX) * 4;
                        red += buffer.get(index) & 0xFF;
                        green += buffer.get(index + 1) & 0xFF;
                        blue += buffer.get(index + 2) & 0xFF;
                        count++;
                    }
                }
                if (count == 0) {
                    continue;
                }
                int a = 255;
                int r = (int) (red / count);
                int g = (int) (green / count);
                int b = (int) (blue / count);
                // NativeImage#setPixelRGBA 期望 ABGR 顺序。
                out.setPixelRGBA(x, y, (a << 24) | (b << 16) | (g << 8) | r);
                lumaTotal += (r * 299L + g * 587L + b * 114L) / 1000L;
                lumaCount++;
            }
        }
        return lumaCount == 0 ? 0L : lumaTotal / lumaCount;
    }
}
