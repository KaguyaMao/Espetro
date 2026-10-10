/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.Monitor
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.screens.LoadingOverlay
 *  net.minecraft.server.packs.resources.ReloadInstance
 *  org.lwjgl.glfw.GLFW
 */
package net.minecraftforge.client.loading;

import com.mojang.blaze3d.platform.Monitor;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.LoadingOverlay;
import net.minecraft.server.packs.resources.ReloadInstance;
import org.lwjgl.glfw.GLFW;

public final class NoVizFallback {
    private static long WINDOW;

    public static LongSupplier windowHandoff(IntSupplier width, IntSupplier height, Supplier<String> title, LongSupplier monitor) {
        return () -> {
            WINDOW = GLFW.glfwCreateWindow((int)width.getAsInt(), (int)height.getAsInt(), (CharSequence)((CharSequence)title.get()), (long)monitor.getAsLong(), (long)0L);
            return WINDOW;
        };
    }

    public static Supplier<LoadingOverlay> loadingOverlay(Supplier<Minecraft> mc, Supplier<ReloadInstance> ri, Consumer<Optional<Throwable>> ex, boolean fadein) {
        return () -> new LoadingOverlay((Minecraft)mc.get(), (ReloadInstance)ri.get(), ex, fadein);
    }

    public static Boolean windowPositioning(Optional<Monitor> monitor, IntConsumer widthSetter, IntConsumer heightSetter, IntConsumer xSetter, IntConsumer ySetter) {
        return Boolean.FALSE;
    }

    public static String glVersion() {
        if (WINDOW != 0L) {
            int maj = GLFW.glfwGetWindowAttrib((long)WINDOW, (int)139266);
            int min = GLFW.glfwGetWindowAttrib((long)WINDOW, (int)139267);
            return maj + "." + min;
        }
        return "3.2";
    }
}

