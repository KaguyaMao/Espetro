/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.Util
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.fml.loading.FMLEnvironment
 *  net.minecraftforge.fml.loading.FMLPaths
 *  org.joml.Vector3f
 */
package com.sighs.apricityui.forge;

import com.mojang.blaze3d.vertex.PoseStack;
import com.sighs.apricityui.client.Client;
import com.sighs.apricityui.forge.ReflectionUtils;
import com.sighs.apricityui.layout.Position;
import com.sighs.apricityui.layout.Size;
import com.sighs.apricityui.screen.ApricityScreen;
import com.sighs.apricityui.spi.AuiClientService;
import com.sighs.apricityui.style.Text;
import java.awt.Canvas;
import java.awt.Font;
import java.io.File;
import java.lang.annotation.Annotation;
import java.net.URI;
import java.nio.file.Path;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLPaths;
import org.joml.Vector3f;

public final class ClientService
implements AuiClientService {
    public static final ClientService INSTANCE = new ClientService();

    private ClientService() {
    }

    @Override
    public Size getWindowSize() {
        try {
            return Client.getWindowSize();
        }
        catch (LinkageError | RuntimeException ignored) {
            return new Size(1920.0, 1080.0);
        }
    }

    @Override
    public Position getMousePosition() {
        try {
            return Client.getMousePosition();
        }
        catch (LinkageError | RuntimeException ignored) {
            return new Position(0.0, 0.0);
        }
    }

    @Override
    public Position getMousePositionDirectly() {
        try {
            return Client.getMousePositionDirectly();
        }
        catch (LinkageError | RuntimeException ignored) {
            return null;
        }
    }

    @Override
    public double getWindowWidth() {
        try {
            return Client.getWindow().m_85441_();
        }
        catch (LinkageError | RuntimeException ignored) {
            return 1920.0;
        }
    }

    @Override
    public double getWindowHeight() {
        try {
            return Client.getWindow().m_85442_();
        }
        catch (LinkageError | RuntimeException ignored) {
            return 1080.0;
        }
    }

    @Override
    public int getScaledWidth() {
        try {
            return Client.getWindow().m_85445_();
        }
        catch (LinkageError | RuntimeException ignored) {
            return 1920;
        }
    }

    @Override
    public int getScaledHeight() {
        try {
            return Client.getWindow().m_85446_();
        }
        catch (LinkageError | RuntimeException ignored) {
            return 1080;
        }
    }

    @Override
    public int getDefaultFontWidth(String text, boolean bold, boolean oblique, double strokeWidth) {
        try {
            return Client.getDefaultFontWidth(text, bold, oblique, strokeWidth);
        }
        catch (LinkageError | RuntimeException ignored) {
            double stroke = Math.max(0.0, strokeWidth) * 2.0;
            int fontStyle = 0;
            if (bold) {
                fontStyle |= 1;
            }
            if (oblique) {
                fontStyle |= 2;
            }
            Font fallbackFont = new Font("Microsoft YaHei", fontStyle, 16);
            int width = new Canvas().getFontMetrics(fallbackFont).stringWidth(text == null ? "" : text);
            return (int)Math.ceil((double)width + stroke);
        }
    }

    @Override
    public void drawDefaultFont(PoseStack poseStack, Text text, String content, Position position) {
        try {
            Client.drawDefaultFont(poseStack, text, content, position);
        }
        catch (LinkageError | RuntimeException throwable) {
            // empty catch block
        }
    }

    @Override
    public boolean isKeyPressed(String keyName) {
        try {
            return Client.isKeyPressed(keyName);
        }
        catch (LinkageError | RuntimeException ignored) {
            return false;
        }
    }

    @Override
    public Position getMousePositionForWorldInteraction() {
        try {
            return Client.getMousePositionForWorldInteraction();
        }
        catch (LinkageError | RuntimeException ignored) {
            return null;
        }
    }

    @Override
    public void openScreen(String templatePath) {
        try {
            Minecraft.m_91087_().m_91152_((Screen)new ApricityScreen(templatePath));
        }
        catch (LinkageError | RuntimeException throwable) {
            // empty catch block
        }
    }

    @Override
    public void closeScreen() {
        try {
            Minecraft.m_91087_().m_91152_(null);
        }
        catch (LinkageError | RuntimeException throwable) {
            // empty catch block
        }
    }

    @Override
    public Path getGameDirectory() {
        try {
            return FMLPaths.GAMEDIR.get().toAbsolutePath().normalize();
        }
        catch (LinkageError | RuntimeException ignored) {
            return Path.of("", new String[0]).toAbsolutePath().normalize();
        }
    }

    @Override
    public Path getConfigDirectory() {
        try {
            return FMLPaths.CONFIGDIR.get().toAbsolutePath().normalize();
        }
        catch (LinkageError | RuntimeException ignored) {
            return null;
        }
    }

    @Override
    public boolean isProduction() {
        return FMLEnvironment.production;
    }

    @Override
    public void addScanPackage(String basePackage) {
        ReflectionUtils.addScanPackage(basePackage);
    }

    @Override
    public void addScanPackages(String ... basePackages) {
        ReflectionUtils.addScanPackages(basePackages);
    }

    @Override
    public void scanAnnotationClasses(Class<? extends Annotation> annotationClass, Predicate<Map<String, Object>> annotationPredicate, Consumer<Class<?>> consumer, Runnable onFinished) {
        try {
            ReflectionUtils.findAnnotationClasses(annotationClass, annotationPredicate, consumer, onFinished);
        }
        catch (LinkageError | RuntimeException throwable) {
            // empty catch block
        }
    }

    @Override
    public void openUri(URI uri) {
        try {
            Util.m_137581_().m_137648_(uri);
        }
        catch (LinkageError | RuntimeException throwable) {
            // empty catch block
        }
    }

    @Override
    public void openFile(File file) {
        try {
            Util.m_137581_().m_137644_(file);
        }
        catch (LinkageError | RuntimeException throwable) {
            // empty catch block
        }
    }

    @Override
    public long getWindowHandle() {
        try {
            return Minecraft.m_91087_().m_91268_().m_85439_();
        }
        catch (LinkageError | RuntimeException ignored) {
            return 0L;
        }
    }

    @Override
    public Vec3 getCameraPosition() {
        try {
            return Minecraft.m_91087_().f_91063_.m_109153_().m_90583_();
        }
        catch (LinkageError | RuntimeException ignored) {
            return new Vec3(0.0, 0.0, 0.0);
        }
    }

    @Override
    public Vector3f getCameraLookVector() {
        try {
            return Minecraft.m_91087_().f_91063_.m_109153_().m_253058_();
        }
        catch (LinkageError | RuntimeException ignored) {
            return new Vector3f(0.0f, 0.0f, -1.0f);
        }
    }
}

