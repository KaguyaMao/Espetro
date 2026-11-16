package org.espetro.client.gui;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;

import java.nio.ByteBuffer;

/**
 * 界面共用的毛玻璃背景：优先使用 FancyMenu 的实时 GPU 局部模糊，
 * 同一次界面渲染中的嵌套预览复用已绘制的背景，避免把 UI 文字再次采样进去。
 *
 * <p>实时模糊不可用时，保留低频 CPU 降级：每 2.5 秒读取主帧缓冲，块平均到
 * 160×90 后再做 3×3 盒式模糊，以双线性过滤放大。读回仅切换 READ_FRAMEBUFFER，
 * 不改变绘制目标或 viewport；读回和绘制各自通过 finally 恢复调用方状态。</p>
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
    private static int screenDepth;
    private static boolean screenLayerDrawn;

    private FrostedBackdrop() {
    }

    /** Nested tutorial previews share their host's blur rather than sampling UI text. */
    public static void beginScreen() {
        if (screenDepth++ == 0) screenLayerDrawn = false;
    }

    public static void endScreen() {
        screenDepth = Math.max(0, screenDepth - 1);
        if (screenDepth == 0) screenLayerDrawn = false;
    }

    public static boolean isScreenLayerActive() {
        return screenDepth > 0 && screenLayerDrawn;
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
        if (isScreenLayerActive()) return;
        if (screenDepth > 0) screenLayerDrawn = true;
        if (FancyMenuGlass.draw(graphics, 0, 0, width, height,
                UiTheme.GLASS_BLUR_RADIUS, UiTheme.FROST_SCRIM, 0.0F)) {
            return;
        }
        // Capture before flush: GuiGraphics.flush() itself changes depth testing.
        FallbackRenderState state = new FallbackRenderState();
        try {
            graphics.flush();
            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            long now = System.currentTimeMillis();
            if (dirty || now - lastCaptureMs >= REFRESH_INTERVAL_MS) {
                lastCaptureMs = now;
                dirty = false;
                if (!capture() || texture == null) {
                    // 截不到可用帧时只压一层暗幕，不使用噪点/黑块盖住视野。
                    graphics.fill(0, 0, width, height, UiTheme.FROST_SCRIM);
                    return;
                }
            }
            if (texture == null) {
                graphics.fill(0, 0, width, height, UiTheme.FROST_SCRIM);
                return;
            }
            // 直接画四边形，不经过把纹理当作 atlas 算 UV 的 GuiGraphics#blit。
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
            // 只压暗，不再叠会被误认为白噪点的颗粒。
            graphics.fill(0, 0, width, height, UiTheme.FROST_SCRIM);
        } finally {
            try {
                // Complete queued tint while its temporary blend/color state is active.
                graphics.flush();
            } finally {
                state.restore();
            }
        }
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
        int readFramebuffer = GL11.glGetInteger(GL30.GL_READ_FRAMEBUFFER_BINDING);
        int packAlignment = GL11.glGetInteger(GL11.GL_PACK_ALIGNMENT);
        try {
            // 先把之前残留的 GL 错误清掉，才能判断这次读回是否真的失败。
            while (GL11.glGetError() != GL11.GL_NO_ERROR) {
                // drain
            }
            buffer = ByteBuffer.allocateDirect(pixelWidth * pixelHeight * 4);
            // Reading needs neither a new draw target nor a viewport change.
            GlStateManager._glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, target.frameBufferId);
            GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1);
            GL11.glReadPixels(0, 0, pixelWidth, pixelHeight,
                GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
        } catch (RuntimeException exception) {
            logFailure(exception);
            return false;
        } finally {
            GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, packAlignment);
            GlStateManager._glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, readFramebuffer);
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
                // setPixels 会接管这张图片的所有权（旧图由它自己关闭），
                // 置空以免 finally 把纹理正在使用的图关掉，导致
                // "Image is not allocated" 崩溃。
                texture.setPixels(image);
                image = null;
                texture.upload();
            }
            return true;
        } finally {
            if (image != null) {
                image.close();
            }
        }
    }

    /** Only state this fallback changes; independent of FancyMenu availability. */
    private static final class FallbackRenderState {
        private final boolean depthTest = GL11.glIsEnabled(GL11.GL_DEPTH_TEST);
        private final boolean depthMask = GL11.glGetBoolean(GL11.GL_DEPTH_WRITEMASK);
        private final boolean blend = GL11.glIsEnabled(GL11.GL_BLEND);
        private final int srcRgb = GL11.glGetInteger(GL14.GL_BLEND_SRC_RGB);
        private final int dstRgb = GL11.glGetInteger(GL14.GL_BLEND_DST_RGB);
        private final int srcAlpha = GL11.glGetInteger(GL14.GL_BLEND_SRC_ALPHA);
        private final int dstAlpha = GL11.glGetInteger(GL14.GL_BLEND_DST_ALPHA);
        private final float[] shaderColor = RenderSystem.getShaderColor().clone();
        private final ShaderInstance shader = RenderSystem.getShader();
        private final int shaderTexture = RenderSystem.getShaderTexture(0);
        private final int activeTexture = GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE);
        private final int activeBinding = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
        private final int texture0Binding;

        private FallbackRenderState() {
            RenderSystem.activeTexture(GL13.GL_TEXTURE0);
            try {
                texture0Binding = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
            } finally {
                RenderSystem.activeTexture(activeTexture);
            }
        }

        private void restore() {
            RenderSystem.setShaderTexture(0, shaderTexture);
            RenderSystem.activeTexture(GL13.GL_TEXTURE0);
            RenderSystem.bindTexture(texture0Binding);
            RenderSystem.activeTexture(activeTexture);
            RenderSystem.bindTexture(activeBinding);
            RenderSystem.setShader(() -> shader);
            RenderSystem.setShaderColor(shaderColor[0], shaderColor[1], shaderColor[2], shaderColor[3]);
            RenderSystem.blendFuncSeparate(srcRgb, dstRgb, srcAlpha, dstAlpha);
            if (blend) RenderSystem.enableBlend();
            else RenderSystem.disableBlend();
            RenderSystem.depthMask(depthMask);
            if (depthTest) RenderSystem.enableDepthTest();
            else RenderSystem.disableDepthTest();
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
