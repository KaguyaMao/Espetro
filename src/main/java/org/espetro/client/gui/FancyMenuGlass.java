package org.espetro.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4fc;
import org.slf4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Client-only bridge to FancyMenu 3.9.9's local, live background blur.
 * No FancyMenu type is linked from Espetro's common/server code or at build time.
 */
@OnlyIn(Dist.CLIENT)
public final class FancyMenuGlass {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String RENDERING_PACKAGE = "de.keksuccino.fancymenu.util.rendering.";
    private static final float TRANSFORM_EPSILON = 0.00001F;

    private static Bridge bridge;
    private static boolean resolved;
    private static volatile boolean failed;

    private FancyMenuGlass() {
    }

    /** API availability; shader availability is established by the first render call. */
    public static boolean isAvailable() {
        return resolve() != null;
    }

    /**
     * Draws a square-cornered glass area, with radius expressed in local GUI units.
     * Must run before the area's text/content is drawn. A false result asks the caller
     * to draw its normal translucent fallback. This method never changes the pose stack.
     */
    public static boolean draw(GuiGraphics graphics, int x, int y, int width, int height,
                               float radius, int argbTint, float partialTick) {
        if (graphics == null || !RenderSystem.isOnRenderThread()) {
            return false;
        }
        Area area = screenArea(graphics.pose().last().pose(), x, y, width, height, radius);
        if (area == null) {
            return false;
        }
        Bridge api = resolve();
        if (api == null) {
            return false;
        }

        Object state = null;
        boolean drawn = false;
        try {
            // Capture before FancyMenu initializes/resizes its FBO and before its
            // GuiGraphics.flush(), which may itself alter the depth-test state.
            state = api.captureState.invoke(null);
            Object tint = api.colorOf.invoke(null, (argbTint >>> 16) & 255,
                (argbTint >>> 8) & 255, argbTint & 255, (argbTint >>> 24) & 255);
            // The Scaled overload reads FancyMenu's own transform trackers, not
            // GuiGraphics.pose(). Our rectangle is already in screen GUI units.
            api.render.invoke(null, graphics, area.x, area.y, area.width, area.height,
                area.radius, 0.0F, tint, Float.isFinite(partialTick) ? partialTick : 0.0F);
            if (api.shaderFailed.getBoolean(null) || api.postChain.get(null) == null) {
                fail(new IllegalStateException("FancyMenu's GUI blur shader is unavailable"));
            } else {
                drawn = true;
            }
        } catch (InvocationTargetException exception) {
            failInvocation(exception);
        } catch (ReflectiveOperationException | RuntimeException | LinkageError exception) {
            fail(exception);
        } finally {
            if (state != null) {
                try {
                    // Native renderBlurArea restores most state, then resets the
                    // shader color to white. Restore our outer snapshot as well so
                    // caller opacity, depth, blending, FBO and clipping all survive.
                    api.restoreState.invoke(state);
                } catch (InvocationTargetException exception) {
                    drawn = false;
                    failInvocation(exception);
                } catch (ReflectiveOperationException | RuntimeException | LinkageError exception) {
                    drawn = false;
                    fail(exception);
                }
            }
        }
        return drawn;
    }

    /**
     * Only axis-aligned 2D poses can be represented by the renderer's rectangle API.
     * In particular, AuiScreen's slide translation and HUD/UI scale remain aligned.
     * A rotated, sheared or perspective pose uses the caller's ordinary paint path.
     */
    static Area screenArea(Matrix4fc pose, int x, int y, int width, int height, float radius) {
        if (width <= 0 || height <= 0 || !Float.isFinite(radius)
            || !nearZero(pose.m01()) || !nearZero(pose.m10())
            || !nearZero(pose.m02()) || !nearZero(pose.m12())
            || !nearZero(pose.m03()) || !nearZero(pose.m13())
            || !nearZero(pose.m23()) || !nearZero(pose.m33() - 1.0F)) {
            return null;
        }
        float scaleX = pose.m00();
        float scaleY = pose.m11();
        float x1 = scaleX * x + pose.m30();
        float y1 = scaleY * y + pose.m31();
        float x2 = scaleX * ((float) x + width) + pose.m30();
        float y2 = scaleY * ((float) y + height) + pose.m31();
        float screenWidth = Math.abs(x2 - x1);
        float screenHeight = Math.abs(y2 - y1);
        float screenRadius = Math.max(0.0F, radius) * Math.min(Math.abs(scaleX), Math.abs(scaleY));
        if (!Float.isFinite(x1) || !Float.isFinite(y1) || !Float.isFinite(x2)
            || !Float.isFinite(y2) || !Float.isFinite(screenRadius)
            || !Float.isFinite(screenWidth) || !Float.isFinite(screenHeight)
            || screenWidth <= 0.0F || screenHeight <= 0.0F) {
            return null;
        }
        return new Area(Math.min(x1, x2), Math.min(y1, y2), screenWidth, screenHeight, screenRadius);
    }

    private static boolean nearZero(float value) {
        return Float.isFinite(value) && Math.abs(value) <= TRANSFORM_EPSILON;
    }

    private static synchronized Bridge resolve() {
        if (failed) {
            return null;
        }
        if (!resolved) {
            resolved = true;
            try {
                ClassLoader loader = FancyMenuGlass.class.getClassLoader();
                Class<?> renderer = Class.forName(RENDERING_PACKAGE + "GuiBlurRenderer", false, loader);
                Class<?> color = Class.forName(RENDERING_PACKAGE + "DrawableColor", false, loader);
                Class<?> utils = Class.forName(RENDERING_PACKAGE + "RenderingUtils", false, loader);
                Method capture = utils.getMethod("captureRenderState");
                Method restore = capture.getReturnType().getMethod("restore");
                Field shaderFailed = renderer.getDeclaredField("blurPostChainFailed");
                Field postChain = renderer.getDeclaredField("blurPostChain");
                if (!shaderFailed.trySetAccessible() || !postChain.trySetAccessible()) {
                    throw new IllegalAccessException("Cannot inspect FancyMenu GUI blur initialization");
                }
                bridge = new Bridge(renderer.getMethod("renderBlurArea", GuiGraphics.class,
                    float.class, float.class, float.class, float.class, float.class,
                    float.class, color, float.class),
                    color.getMethod("of", int.class, int.class, int.class, int.class),
                    capture, restore, shaderFailed, postChain);
            } catch (ReflectiveOperationException | RuntimeException | LinkageError exception) {
                fail(exception);
            }
        }
        return failed ? null : bridge;
    }

    private static void failInvocation(InvocationTargetException exception) {
        Throwable cause = exception.getCause();
        // Reflection wraps even VM failures; never convert these into a visual fallback.
        if (cause instanceof VirtualMachineError fatal) {
            throw fatal;
        }
        if (cause instanceof ThreadDeath death) {
            throw death;
        }
        if (cause instanceof Error error && !(error instanceof LinkageError)) {
            throw error;
        }
        fail(cause == null ? exception : cause);
    }

    private static synchronized void fail(Throwable exception) {
        if (!failed) {
            failed = true;
            LOGGER.warn("FancyMenu live glass is unavailable; using the normal translucent UI fallback", exception);
        }
    }

    record Area(float x, float y, float width, float height, float radius) {
    }

    private record Bridge(Method render, Method colorOf, Method captureState, Method restoreState,
                          Field shaderFailed, Field postChain) {
    }
}
