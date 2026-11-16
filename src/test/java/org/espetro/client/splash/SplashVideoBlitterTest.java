package org.espetro.client.splash;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.*;

import java.nio.ByteBuffer;
import java.time.Duration;
import static org.junit.jupiter.api.Assertions.*;

/** Real hidden-window GL tests: read the pixels that will actually be swapped. */
class SplashVideoBlitterTest {
    private static long window;
    private static GLFWErrorCallback errors;

    @BeforeAll
    static void openContext() {
        errors = GLFWErrorCallback.createPrint(System.err).set();
        assertTrue(GLFW.glfwInit(), "GLFW initialization");
        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 2);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
        window = GLFW.glfwCreateWindow(160, 120, "Splash regression", 0, 0);
        assertNotEquals(0L, window, "hidden OpenGL 3.2 context");
        GLFW.glfwMakeContextCurrent(window);
        GL.createCapabilities();
        RenderSystem.initRenderThread();
        System.out.println("Splash GL: " + GL11.glGetString(GL11.GL_RENDERER)
            + " / " + GL11.glGetString(GL11.GL_VERSION));
    }

    @AfterAll
    static void closeContext() {
        if (window != 0) GLFW.glfwDestroyWindow(window);
        GLFW.glfwTerminate();
        if (errors != null) errors.free();
    }

    @Test
    void drawsTopAndBottomInCorrectOrientationWithLetterboxAndFade() {
        resetDefaultFramebuffer();
        try (SplashVideoBlitter blitter = new SplashVideoBlitter();
             NativeImage image = new NativeImage(16, 8, false)) {
            for (int y = 0; y < 8; y++) for (int x = 0; x < 16; x++) {
                image.setPixelRGBA(x, y, y < 4 ? 0xFF0000FF : 0xFFFF0000);
            }
            blitter.upload(image);
            blitter.draw(160, 120, 1f);
            assertPixel(80, 85, 255, 0, 0); // NativeImage top must be screen top
            assertPixel(80, 35, 0, 0, 255);
            assertPixel(80, 5, 0, 0, 0); // 2:1 video in a 4:3 window
            assertPixel(80, 115, 0, 0, 0);
            clear(0, 1, 0);
            blitter.draw(160, 120, 0.5f);
            assertPixel(80, 85, 128, 127, 0);
            clear(0, 1, 0);
            blitter.draw(160, 120, 0f);
            assertPixel(80, 85, 0, 255, 0);
            assertEquals(GL11.GL_NO_ERROR, GL11.glGetError());
        }
    }

    @Test
    void eachUploadReachesWindowEvenWithDifferentFramebufferAndHostileState() {
        resetDefaultFramebuffer();
        int fbo = GL30.glGenFramebuffers();
        int pbo = GL15.glGenBuffers();
        int hostTexture = GL11.glGenTextures();
        int hostVao = GL30.glGenVertexArrays();
        boolean samplers = GL.getCapabilities().OpenGL33 || GL.getCapabilities().GL_ARB_sampler_objects;
        int hostSampler = samplers ? GL33.glGenSamplers() : 0;
        if (samplers) GL33.glSamplerParameteri(hostSampler, GL11.GL_TEXTURE_MIN_FILTER,
            GL11.GL_NEAREST_MIPMAP_LINEAR);
        try (SplashVideoBlitter blitter = new SplashVideoBlitter();
             NativeImage image = new NativeImage(16, 8, false)) {
            for (int frame = 0; frame < 4; frame++) {
                for (int y = 0; y < 8; y++) for (int x = 0; x < 16; x++) {
                    image.setPixelRGBA(x, y, frame % 2 == 0 ? 0xFF0000FF : 0xFFFF0000);
                }
                GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, fbo);
                GL30.glBindVertexArray(hostVao);
                GL13.glActiveTexture(GL13.GL_TEXTURE0);
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, hostTexture);
                if (samplers) GL33.glBindSampler(0, hostSampler);
                GL13.glActiveTexture(GL13.GL_TEXTURE3);
                GL15.glBindBuffer(GL21.GL_PIXEL_UNPACK_BUFFER, pbo);
                GL11.glPixelStorei(GL11.GL_UNPACK_ROW_LENGTH, 31);
                GL11.glPixelStorei(GL11.GL_UNPACK_SKIP_ROWS, 2);
                GL11.glPixelStorei(GL11.GL_UNPACK_SKIP_PIXELS, 3);
                GL11.glViewport(11, 12, 13, 14);
                GL11.glEnable(GL11.GL_DEPTH_TEST);
                GL11.glEnable(GL11.GL_SCISSOR_TEST);
                GL11.glEnable(GL11.GL_CULL_FACE);
                GL11.glEnable(GL30.GL_RASTERIZER_DISCARD);
                GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
                GL11.glColorMask(false, true, false, true);
                blitter.upload(image);
                blitter.draw(160, 120, 1f);
                assertEquals(fbo, GL11.glGetInteger(GL30.GL_DRAW_FRAMEBUFFER_BINDING));
                assertEquals(hostVao, GL11.glGetInteger(GL30.GL_VERTEX_ARRAY_BINDING));
                assertEquals(GL13.GL_TEXTURE3, GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE));
                assertEquals(pbo, GL11.glGetInteger(GL21.GL_PIXEL_UNPACK_BUFFER_BINDING));
                assertEquals(31, GL11.glGetInteger(GL11.GL_UNPACK_ROW_LENGTH));
                assertEquals(2, GL11.glGetInteger(GL11.GL_UNPACK_SKIP_ROWS));
                assertEquals(3, GL11.glGetInteger(GL11.GL_UNPACK_SKIP_PIXELS));
                int[] viewport = new int[4];
                GL11.glGetIntegerv(GL11.GL_VIEWPORT, viewport);
                assertArrayEquals(new int[] {11, 12, 13, 14}, viewport);
                assertTrue(GL11.glIsEnabled(GL11.GL_DEPTH_TEST));
                assertTrue(GL11.glIsEnabled(GL11.GL_SCISSOR_TEST));
                assertTrue(GL11.glIsEnabled(GL11.GL_CULL_FACE));
                assertTrue(GL11.glIsEnabled(GL30.GL_RASTERIZER_DISCARD));
                int[] polygonMode = new int[2];
                GL11.glGetIntegerv(GL11.GL_POLYGON_MODE, polygonMode);
                assertEquals(GL11.GL_LINE, polygonMode[0]); // core profile returns one mode
                ByteBuffer mask = BufferUtils.createByteBuffer(4);
                GL11.glGetBooleanv(GL11.GL_COLOR_WRITEMASK, mask);
                assertEquals(0, mask.get(0));
                assertEquals(1, mask.get(1));
                GL13.glActiveTexture(GL13.GL_TEXTURE0);
                assertEquals(hostTexture, GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D));
                if (samplers) assertEquals(hostSampler, GL11.glGetInteger(GL33.GL_SAMPLER_BINDING));
                assertPixel(80, 60, frame % 2 == 0 ? 255 : 0, 0, frame % 2 == 0 ? 0 : 255);
                assertEquals(GL11.GL_NO_ERROR, GL11.glGetError());
            }
        } finally {
            resetDefaultFramebuffer();
            GL15.glBindBuffer(GL21.GL_PIXEL_UNPACK_BUFFER, 0);
            GL11.glPixelStorei(GL11.GL_UNPACK_ROW_LENGTH, 0);
            GL11.glPixelStorei(GL11.GL_UNPACK_SKIP_ROWS, 0);
            GL11.glPixelStorei(GL11.GL_UNPACK_SKIP_PIXELS, 0);
            GL30.glDeleteFramebuffers(fbo);
            GL15.glDeleteBuffers(pbo);
            GL11.glDeleteTextures(hostTexture);
            GL30.glDeleteVertexArrays(hostVao);
            if (samplers) {
                GL33.glBindSampler(0, 0);
                GL33.glDeleteSamplers(hostSampler);
            }
        }
    }

    @Test
    void classpathDecoderDeliversFirstAndFinalFramesAndReleasesCache() {
        assertTimeoutPreemptively(Duration.ofSeconds(12), () -> {
            SplashVideoAssets assets = SplashVideoAssets.openClasspath();
            assertNotNull(assets);
            try (assets) {
                assertEquals(394, assets.frameCount());
                assets.prewarm();
                SplashVideoAssets.DecodedFrame first = awaitFrame(assets, 0);
                try (NativeImage image = first.image()) {
                    assertEquals(0, first.index());
                    assertEquals(1280, image.getWidth());
                    assertEquals(720, image.getHeight());
                }
                SplashVideoAssets.DecodedFrame last;
                do {
                    last = awaitFrame(assets, 393);
                    last.image().close();
                } while (last.index() != 393);
                assertTrue(assets.buffered() <= 20);
                assertNull(assets.lastError());
            }
            assertEquals(0, assets.buffered());
            assertNull(assets.takeFrame(393));
        });
    }

    private static SplashVideoAssets.DecodedFrame awaitFrame(SplashVideoAssets assets, int wanted)
            throws InterruptedException {
        SplashVideoAssets.DecodedFrame frame;
        while ((frame = assets.takeFrame(wanted)) == null) Thread.sleep(5);
        return frame;
    }

    private static void resetDefaultFramebuffer() {
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
        GL11.glDrawBuffer(GL11.GL_BACK);
        GL11.glReadBuffer(GL11.GL_BACK);
        GL11.glViewport(0, 0, 160, 120);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL30.GL_RASTERIZER_DISCARD);
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
        GL11.glColorMask(true, true, true, true);
        clear(0, 1, 0);
    }

    private static void clear(float red, float green, float blue) {
        GL11.glClearColor(red, green, blue, 1);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
    }

    private static void assertPixel(int x, int y, int red, int green, int blue) {
        ByteBuffer pixel = BufferUtils.createByteBuffer(4);
        GL11.glReadPixels(x, y, 1, 1, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, pixel);
        assertEquals(red, pixel.get(0) & 255, 2, "red at " + x + "," + y);
        assertEquals(green, pixel.get(1) & 255, 2, "green at " + x + "," + y);
        assertEquals(blue, pixel.get(2) & 255, 2, "blue at " + x + "," + y);
    }
}
