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
 * 但足以把世界压成不易读的色块，让叠在上面的文字仍然清楚。
 *
 * <p>实现取舍：不引入 post-chain / FBO 后处理（1.20.1 原版没有模糊链，自己写 shader
 * 在整合包里风险高），而是每 2.5 秒对主帧缓冲做一次 {@code glReadPixels}，在 CPU 上
 * 块平均到 160×90 再叠一次 3×3 盒式模糊，最后用双线性过滤放大铺满屏幕。</p>
 *
 * <p>读回有两个已知的"看起来像满屏白噪点"的失败模式，必须显式挡掉：
 * <ol>
 *   <li>读回被驱动拒绝（GL 报错），缓冲区里全是未初始化内存 —— 颜色完全随机；</li>
 *   <li>读回拿到的是同帧内被其它后处理写过一半的缓冲。</li>
 * </ol>
 * 所以落地前先查 GL 错误，再统计"横向相邻像素亮度差"：真实世界的降采样结果非常平滑，
 * 随机噪点的相邻差极大。判据不通过就只压一层暗幕（世界清晰可见，但绝不出现噪点）。</p>
 */
public final class FrostedBackdrop {

    /** 归一化底图尺寸（固定，避免窗口缩放时重建纹理）。 */
    private static final int TEX_WIDTH = 160;
    private static final int TEX_HEIGHT = 90;
    /** 两次截屏之间的最小间隔：读回是同步的（会卡一帧），所以只做低频刷新。 */
    private static final long REFRESH_INTERVAL_MS = 2500L;
    /** 低于这个平均亮度说明没截到画面（全黑），退回纯压暗。 */
    private static final long MIN_LUMA = 4L;
    /** 高于这个相邻亮度差说明拿到的是随机内存（满屏噪点），同样退回纯压暗。 */
    private static final long MAX_ROUGHNESS = 26L;
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
            if (!capture() || texture == null) {
                // 截不到可用帧时只压一层暗幕：宁可没有模糊，也不能用噪点/黑块盖住视野。
                graphics.fill(0, 0, width, height, UiTheme.FROST_SCRIM);
                return;
            }
        }
        graphics.flush();
        boolean depthEnabled = GL11.glIsEnabled(GL11.GL_DEPTH_TEST);
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
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
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        if (depthEnabled) {
            RenderSystem.enableDepthTest();
        }
        RenderSystem.depthMask(true);
        // 只压暗，不再叠颗粒：深色底上 3% 的白噪点会被看成"满屏白噪点"。
        graphics.fill(0, 0, width, height, UiTheme.FROST_SCRIM);
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
            // 先把之前残留的 GL 错误清掉，才能判断这次读回是否真的失败。
            while (GL11.glGetError() != GL11.GL_NO_ERROR) {
                // drain
            }
            buffer = ByteBuffer.allocateDirect(pixelWidth * pixelHeight * 4);
            target.bindWrite(true);
            GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1);
            GL11.glReadPixels(0, 0, pixelWidth, pixelHeight,
                GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
        } catch (RuntimeException exception) {
            logFailure(exception);
            return false;
        }
        int error = GL11.glGetError();
        if (error != GL11.GL_NO_ERROR) {
            logFailure(new IllegalStateException("glReadPixels failed: 0x"
                + Integer.toHexString(error)));
            return false;
        }
        NativeImage image = new NativeImage(TEX_WIDTH, TEX_HEIGHT, false);
        try {
            long[] stats = downsample(buffer, pixelWidth, pixelHeight, image);
            long luma = stats[0];
            long roughness = stats[1];
            // 全黑 / 随机噪点都不拿来做底图。
            if (luma < MIN_LUMA || roughness > MAX_ROUGHNESS) {
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

    private static void logFailure(RuntimeException exception) {
        if (!loggedFailure) {
            loggedFailure = true;
            LOGGER.warn("毛玻璃底图截屏失败，改为纯压暗底", exception);
        }
    }

    /**
     * 块平均降采样 + 3×3 盒式模糊，并同时上下翻转（{@code glReadPixels} 第 0 行是屏幕底部）。
     *
     * @return {@code [平均亮度, 相邻亮度差]}，用于判定这次读回是不是有效画面。
     */
    private static long[] downsample(ByteBuffer buffer, int pixelWidth, int pixelHeight,
                                     NativeImage out) {
        int[] pixels = new int[TEX_WIDTH * TEX_HEIGHT];
        float stepX = (float) pixelWidth / TEX_WIDTH;
        float stepY = (float) pixelHeight / TEX_HEIGHT;
        int sampleStepX = Math.max(1, Math.round(stepX / 4.0F));
        int sampleStepY = Math.max(1, Math.round(stepY / 4.0F));
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
                int r = (int) (red / count);
                int g = (int) (green / count);
                int b = (int) (blue / count);
                pixels[y * TEX_WIDTH + x] = (r << 16) | (g << 8) | b;
            }
        }
        int[] blurred = boxBlur(pixels, TEX_WIDTH, TEX_HEIGHT);
        long lumaTotal = 0L;
        long roughnessTotal = 0L;
        int roughnessCount = 0;
        int previousLuma = -1;
        for (int y = 0; y < TEX_HEIGHT; y++) {
            for (int x = 0; x < TEX_WIDTH; x++) {
                int packed = blurred[y * TEX_WIDTH + x];
                int r = (packed >>> 16) & 0xFF;
                int g = (packed >>> 8) & 0xFF;
                int b = packed & 0xFF;
                // NativeImage#setPixelRGBA 期望 ABGR 顺序。
                out.setPixelRGBA(x, y, (255 << 24) | (b << 16) | (g << 8) | r);
                int luma = (r * 299 + g * 587 + b * 114) / 1000;
                lumaTotal += luma;
                if (previousLuma >= 0) {
                    roughnessTotal += Math.abs(luma - previousLuma);
                    roughnessCount++;
                }
                previousLuma = x == TEX_WIDTH - 1 ? -1 : luma;
            }
        }
        long lumaAverage = lumaTotal / (long) (TEX_WIDTH * TEX_HEIGHT);
        long roughness = roughnessCount == 0 ? 0L : roughnessTotal / roughnessCount;
        return new long[] {lumaAverage, roughness};
    }

    /** 3×3 盒式模糊（边缘钳制），把块平均残留的格子感抹平。 */
    private static int[] boxBlur(int[] pixels, int width, int height) {
        int[] result = new int[pixels.length];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int red = 0;
                int green = 0;
                int blue = 0;
                for (int dy = -1; dy <= 1; dy++) {
                    int sy = Math.max(0, Math.min(height - 1, y + dy));
                    for (int dx = -1; dx <= 1; dx++) {
                        int sx = Math.max(0, Math.min(width - 1, x + dx));
                        int packed = pixels[sy * width + sx];
                        red += (packed >>> 16) & 0xFF;
                        green += (packed >>> 8) & 0xFF;
                        blue += packed & 0xFF;
                    }
                }
                result[y * width + x] = ((red / 9) << 16) | ((green / 9) << 8) | (blue / 9);
            }
        }
        return result;
    }
}
