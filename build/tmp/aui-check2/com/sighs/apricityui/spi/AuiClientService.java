/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Vector3f
 */
package com.sighs.apricityui.spi;

import com.mojang.blaze3d.vertex.PoseStack;
import com.sighs.apricityui.layout.Position;
import com.sighs.apricityui.layout.Size;
import com.sighs.apricityui.style.Text;
import java.io.File;
import java.lang.annotation.Annotation;
import java.net.URI;
import java.nio.file.Path;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public interface AuiClientService {
    public Size getWindowSize();

    public Position getMousePosition();

    public Position getMousePositionDirectly();

    public double getWindowWidth();

    public double getWindowHeight();

    public int getScaledWidth();

    public int getScaledHeight();

    public int getDefaultFontWidth(String var1, boolean var2, boolean var3, double var4);

    public void drawDefaultFont(PoseStack var1, Text var2, String var3, Position var4);

    public boolean isKeyPressed(String var1);

    public Position getMousePositionForWorldInteraction();

    public void openScreen(String var1);

    default public void closeScreen() {
    }

    public Path getGameDirectory();

    public Path getConfigDirectory();

    public boolean isProduction();

    public void addScanPackage(String var1);

    public void addScanPackages(String ... var1);

    public void scanAnnotationClasses(Class<? extends Annotation> var1, Predicate<Map<String, Object>> var2, Consumer<Class<?>> var3, Runnable var4);

    public void openUri(URI var1);

    public void openFile(File var1);

    public long getWindowHandle();

    public Vec3 getCameraPosition();

    public Vector3f getCameraLookVector();
}

