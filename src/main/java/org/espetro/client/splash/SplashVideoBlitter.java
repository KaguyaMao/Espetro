package org.espetro.client.splash;

import com.mojang.blaze3d.platform.NativeImage;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL21;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL33;

/**
 * Self-contained OpenGL 3.2 pass for the final window back buffer. It deliberately
 * has no resource-manager shader or GUI projection dependency during startup.
 * Raw GL state is restored before returning, leaving Minecraft's state caches valid.
 */
final class SplashVideoBlitter implements AutoCloseable {
    private static final String VERTEX_SHADER = """
        #version 150
        out vec2 texCoord;
        void main() {
            vec2 p = vec2((gl_VertexID & 1) == 0 ? -1.0 : 1.0,
                          gl_VertexID < 2 ? -1.0 : 1.0);
            gl_Position = vec4(p, 0.0, 1.0);
            texCoord = vec2(p.x * 0.5 + 0.5, 0.5 - p.y * 0.5);
        }
        """;
    private static final String FRAGMENT_SHADER = """
        #version 150
        uniform sampler2D video;
        uniform vec2 videoScale;
        uniform float opacity;
        in vec2 texCoord;
        out vec4 fragColor;
        void main() {
            vec2 uv = (texCoord - 0.5) / videoScale + 0.5;
            bool inside = all(greaterThanEqual(uv, vec2(0.0)))
                       && all(lessThanEqual(uv, vec2(1.0)));
            fragColor = vec4(inside ? texture(video, uv).rgb : vec3(0.0), opacity);
        }
        """;

    private int program;
    private int vertexArray;
    private int texture;
    private int width;
    private int height;
    private int scaleUniform;
    private int opacityUniform;

    void upload(NativeImage image) {
        try (State state = new State()) {
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            GL15.glBindBuffer(GL21.GL_PIXEL_UNPACK_BUFFER, 0);
            if (texture == 0) texture = GL11.glGenTextures();
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
            if (width != image.getWidth() || height != image.getHeight()) {
                width = image.getWidth();
                height = image.getHeight();
                // NativeImage.upload only calls glTexSubImage2D: storage must exist first.
                GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, width, height,
                    0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, 0L);
                if (GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0,
                        GL11.GL_TEXTURE_WIDTH) != width) {
                    throw new IllegalStateException("Splash texture storage allocation failed");
                }
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
            }
            image.upload(0, 0, 0, false);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        }
    }

    void draw(int framebufferWidth, int framebufferHeight, float opacity) {
        if (texture == 0 || framebufferWidth <= 0 || framebufferHeight <= 0) return;
        try (State state = new State()) {
            initialize();
            GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, 0);
            int previousDrawBuffer = GL11.glGetInteger(GL11.GL_DRAW_BUFFER);
            try {
                GL11.glDrawBuffer(GL11.GL_BACK);
                GL11.glViewport(0, 0, framebufferWidth, framebufferHeight);
                GL11.glDisable(GL11.GL_DEPTH_TEST);
                GL11.glDisable(GL11.GL_SCISSOR_TEST);
                GL11.glDisable(GL11.GL_STENCIL_TEST);
                GL11.glDisable(GL11.GL_CULL_FACE);
                GL11.glDisable(GL11.GL_COLOR_LOGIC_OP);
                GL11.glDisable(GL30.GL_RASTERIZER_DISCARD);
                GL11.glDisable(GL30.GL_FRAMEBUFFER_SRGB);
                GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
                GL11.glDepthMask(false);
                GL11.glColorMask(true, true, true, true);
                GL11.glEnable(GL11.GL_BLEND);
                GL20.glBlendEquationSeparate(GL14.GL_FUNC_ADD, GL14.GL_FUNC_ADD);
                GL14.glBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA,
                    GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
                GL20.glUseProgram(program);
                GL30.glBindVertexArray(vertexArray);
                GL13.glActiveTexture(GL13.GL_TEXTURE0);
                if (state.samplerObjects) GL33.glBindSampler(0, 0);
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
                float fit = Math.min(framebufferWidth / (float) width, framebufferHeight / (float) height);
                GL20.glUniform2f(scaleUniform, width * fit / framebufferWidth, height * fit / framebufferHeight);
                GL20.glUniform1f(opacityUniform, Math.max(0f, Math.min(1f, opacity)));
                GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);
            } finally {
                GL11.glDrawBuffer(previousDrawBuffer);
            }
        }
    }

    private void initialize() {
        if (program != 0) return;
        int vertex = compile(GL20.GL_VERTEX_SHADER, VERTEX_SHADER);
        int fragment = 0;
        int linked = 0;
        try {
            fragment = compile(GL20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER);
            linked = GL20.glCreateProgram();
            GL20.glAttachShader(linked, vertex);
            GL20.glAttachShader(linked, fragment);
            GL30.glBindFragDataLocation(linked, 0, "fragColor");
            GL20.glLinkProgram(linked);
            if (GL20.glGetProgrami(linked, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
                throw new IllegalStateException("Splash shader link: " + GL20.glGetProgramInfoLog(linked));
            }
            GL20.glUseProgram(linked);
            GL20.glUniform1i(GL20.glGetUniformLocation(linked, "video"), 0);
            scaleUniform = GL20.glGetUniformLocation(linked, "videoScale");
            opacityUniform = GL20.glGetUniformLocation(linked, "opacity");
            vertexArray = GL30.glGenVertexArrays();
            program = linked;
        } finally {
            GL20.glDeleteShader(vertex);
            if (fragment != 0) GL20.glDeleteShader(fragment);
            if (program == 0 && linked != 0) GL20.glDeleteProgram(linked);
        }
    }

    private static int compile(int type, String source) {
        int shader = GL20.glCreateShader(type);
        GL20.glShaderSource(shader, source);
        GL20.glCompileShader(shader);
        if (GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            String message = GL20.glGetShaderInfoLog(shader);
            GL20.glDeleteShader(shader);
            throw new IllegalStateException("Splash shader compile: " + message);
        }
        return shader;
    }

    @Override
    public void close() {
        if (texture != 0) GL11.glDeleteTextures(texture);
        if (vertexArray != 0) GL30.glDeleteVertexArrays(vertexArray);
        if (program != 0) GL20.glDeleteProgram(program);
        texture = vertexArray = program = width = height = 0;
    }

    /** Capture actual driver state; no RenderSystem cache is mutated by this pass. */
    private static final class State implements AutoCloseable {
        private static final int[] CAPABILITIES = { GL11.GL_DEPTH_TEST, GL11.GL_SCISSOR_TEST,
            GL11.GL_STENCIL_TEST, GL11.GL_CULL_FACE, GL11.GL_BLEND, GL11.GL_COLOR_LOGIC_OP,
            GL30.GL_RASTERIZER_DISCARD, GL30.GL_FRAMEBUFFER_SRGB };
        private final boolean[] enabled = new boolean[CAPABILITIES.length];
        private final int[] viewport = new int[4];
        private final int[] polygonMode = new int[2];
        private final java.nio.ByteBuffer colorMask = org.lwjgl.BufferUtils.createByteBuffer(4);
        private final int framebuffer = GL11.glGetInteger(GL30.GL_DRAW_FRAMEBUFFER_BINDING);
        private final int program = GL11.glGetInteger(GL20.GL_CURRENT_PROGRAM);
        private final int vertexArray = GL11.glGetInteger(GL30.GL_VERTEX_ARRAY_BINDING);
        private final int activeTexture = GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE);
        private final int pixelBuffer = GL11.glGetInteger(GL21.GL_PIXEL_UNPACK_BUFFER_BINDING);
        private final int unpackAlignment = GL11.glGetInteger(GL11.GL_UNPACK_ALIGNMENT);
        private final int unpackRows = GL11.glGetInteger(GL11.GL_UNPACK_ROW_LENGTH);
        private final int unpackSkipRows = GL11.glGetInteger(GL11.GL_UNPACK_SKIP_ROWS);
        private final int unpackSkipPixels = GL11.glGetInteger(GL11.GL_UNPACK_SKIP_PIXELS);
        private final boolean depthMask = GL11.glGetBoolean(GL11.GL_DEPTH_WRITEMASK);
        private final int srcRgb = GL11.glGetInteger(GL14.GL_BLEND_SRC_RGB);
        private final int dstRgb = GL11.glGetInteger(GL14.GL_BLEND_DST_RGB);
        private final int srcAlpha = GL11.glGetInteger(GL14.GL_BLEND_SRC_ALPHA);
        private final int dstAlpha = GL11.glGetInteger(GL14.GL_BLEND_DST_ALPHA);
        private final int equationRgb = GL11.glGetInteger(GL20.GL_BLEND_EQUATION_RGB);
        private final int equationAlpha = GL11.glGetInteger(GL20.GL_BLEND_EQUATION_ALPHA);
        private final boolean samplerObjects = GL.getCapabilities().OpenGL33
            || GL.getCapabilities().GL_ARB_sampler_objects;
        private final int texture0;
        private final int sampler0;

        private State() {
            GL11.glGetIntegerv(GL11.GL_VIEWPORT, viewport);
            GL11.glGetIntegerv(GL11.GL_POLYGON_MODE, polygonMode);
            GL11.glGetBooleanv(GL11.GL_COLOR_WRITEMASK, colorMask);
            for (int i = 0; i < CAPABILITIES.length; i++) enabled[i] = GL11.glIsEnabled(CAPABILITIES[i]);
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            texture0 = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
            sampler0 = samplerObjects ? GL11.glGetInteger(GL33.GL_SAMPLER_BINDING) : 0;
            GL13.glActiveTexture(activeTexture);
        }

        @Override
        public void close() {
            GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, framebuffer);
            GL20.glUseProgram(program);
            GL30.glBindVertexArray(vertexArray);
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture0);
            if (samplerObjects) GL33.glBindSampler(0, sampler0);
            GL13.glActiveTexture(activeTexture);
            GL15.glBindBuffer(GL21.GL_PIXEL_UNPACK_BUFFER, pixelBuffer);
            GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, unpackAlignment);
            GL11.glPixelStorei(GL11.GL_UNPACK_ROW_LENGTH, unpackRows);
            GL11.glPixelStorei(GL11.GL_UNPACK_SKIP_ROWS, unpackSkipRows);
            GL11.glPixelStorei(GL11.GL_UNPACK_SKIP_PIXELS, unpackSkipPixels);
            GL11.glViewport(viewport[0], viewport[1], viewport[2], viewport[3]);
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, polygonMode[0]);
            GL11.glDepthMask(depthMask);
            GL11.glColorMask(colorMask.get(0) != 0, colorMask.get(1) != 0,
                colorMask.get(2) != 0, colorMask.get(3) != 0);
            GL14.glBlendFuncSeparate(srcRgb, dstRgb, srcAlpha, dstAlpha);
            GL20.glBlendEquationSeparate(equationRgb, equationAlpha);
            for (int i = 0; i < CAPABILITIES.length; i++) {
                if (enabled[i]) GL11.glEnable(CAPABILITIES[i]);
                else GL11.glDisable(CAPABILITIES[i]);
            }
        }
    }
}
