/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.GlStateManager
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.BufferBuilder
 *  com.mojang.blaze3d.vertex.BufferBuilder$RenderedBuffer
 *  com.mojang.blaze3d.vertex.BufferUploader
 *  com.mojang.blaze3d.vertex.BufferVertexConsumer
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  com.mojang.blaze3d.vertex.Tesselator
 *  com.mojang.blaze3d.vertex.VertexFormat$Mode
 *  com.mojang.blaze3d.vertex.VertexSorting
 *  net.minecraft.Util
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.screens.LoadingOverlay
 *  net.minecraft.client.renderer.GameRenderer
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.packs.resources.ReloadInstance
 *  net.minecraft.util.Mth
 *  net.minecraftforge.fml.StartupMessageManager
 *  net.minecraftforge.fml.earlydisplay.ColourScheme$Colour
 *  net.minecraftforge.fml.earlydisplay.DisplayWindow
 *  net.minecraftforge.fml.loading.progress.ProgressMeter
 *  org.jetbrains.annotations.NotNull
 *  org.joml.Matrix4f
 *  org.lwjgl.opengl.GL30C
 */
package net.minecraftforge.client.loading;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.BufferVertexConsumer;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexSorting;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.LoadingOverlay;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ReloadInstance;
import net.minecraft.util.Mth;
import net.minecraftforge.fml.StartupMessageManager;
import net.minecraftforge.fml.earlydisplay.ColourScheme;
import net.minecraftforge.fml.earlydisplay.DisplayWindow;
import net.minecraftforge.fml.loading.progress.ProgressMeter;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL30C;

public class ForgeLoadingOverlay
extends LoadingOverlay {
    private final Minecraft minecraft;
    private final ReloadInstance reload;
    private final Consumer<Optional<Throwable>> onFinish;
    private final DisplayWindow displayWindow;
    private final ProgressMeter progress;
    private long fadeOutStart = -1L;

    public ForgeLoadingOverlay(Minecraft mc, ReloadInstance reloader, Consumer<Optional<Throwable>> errorConsumer, DisplayWindow displayWindow) {
        super(mc, reloader, errorConsumer, false);
        this.minecraft = mc;
        this.reload = reloader;
        this.onFinish = errorConsumer;
        this.displayWindow = displayWindow;
        displayWindow.addMojangTexture(mc.m_91097_().m_118506_(new ResourceLocation("textures/gui/title/mojangstudios.png")).m_117963_());
        this.progress = StartupMessageManager.prependProgressBar((String)"Minecraft Progress", (int)100);
    }

    public static Supplier<LoadingOverlay> newInstance(Supplier<Minecraft> mc, Supplier<ReloadInstance> ri, Consumer<Optional<Throwable>> handler, DisplayWindow window) {
        return () -> new ForgeLoadingOverlay((Minecraft)mc.get(), (ReloadInstance)ri.get(), handler, window);
    }

    public void m_88315_(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        long millis = Util.m_137550_();
        float fadeouttimer = this.fadeOutStart > -1L ? (float)(millis - this.fadeOutStart) / 1000.0f : -1.0f;
        this.progress.setAbsolute(Mth.m_14045_((int)((int)(this.reload.m_7750_() * 100.0f)), (int)0, (int)100));
        float fade = 1.0f - Mth.m_14036_((float)(fadeouttimer - 1.0f), (float)0.0f, (float)1.0f);
        ColourScheme.Colour colour = this.displayWindow.context().colourScheme().background();
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)fade);
        if (fadeouttimer >= 1.0f) {
            if (this.minecraft.f_91080_ != null) {
                this.minecraft.f_91080_.m_88315_(graphics, 0, 0, partialTick);
            }
            this.displayWindow.render(255);
        } else {
            GlStateManager._clearColor((float)colour.redf(), (float)colour.greenf(), (float)colour.bluef(), (float)1.0f);
            GlStateManager._clear((int)16384, (boolean)Minecraft.f_91002_);
            this.displayWindow.render(255);
        }
        RenderSystem.enableBlend();
        RenderSystem.blendFunc((int)770, (int)771);
        int fbWidth = this.minecraft.m_91268_().m_85441_();
        int fbHeight = this.minecraft.m_91268_().m_85442_();
        GL30C.glViewport((int)0, (int)0, (int)fbWidth, (int)fbHeight);
        int twidth = this.displayWindow.context().width();
        int theight = this.displayWindow.context().height();
        float wscale = (float)fbWidth / (float)twidth;
        float hscale = (float)fbHeight / (float)theight;
        float scale = (float)this.displayWindow.context().scale() * Math.min(wscale, hscale) / 2.0f;
        float wleft = Mth.m_14036_((float)((float)fbWidth * 0.5f - scale * (float)twidth), (float)0.0f, (float)fbWidth);
        float wtop = Mth.m_14036_((float)((float)fbHeight * 0.5f - scale * (float)theight), (float)0.0f, (float)fbHeight);
        float wright = Mth.m_14036_((float)((float)fbWidth * 0.5f + scale * (float)twidth), (float)0.0f, (float)fbWidth);
        float wbottom = Mth.m_14036_((float)((float)fbHeight * 0.5f + scale * (float)theight), (float)0.0f, (float)fbHeight);
        GlStateManager.glActiveTexture((int)33984);
        RenderSystem.disableCull();
        BufferBuilder bufferbuilder = Tesselator.m_85913_().m_85915_();
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)fade);
        RenderSystem.getModelViewMatrix().identity();
        RenderSystem.setProjectionMatrix((Matrix4f)new Matrix4f().setOrtho(0.0f, (float)fbWidth, 0.0f, (float)fbHeight, 0.1f, -0.1f), (VertexSorting)VertexSorting.f_276633_);
        RenderSystem.setShader(GameRenderer::m_172811_);
        bufferbuilder.m_166779_(VertexFormat.Mode.QUADS, DefaultVertexFormat.f_85815_);
        ForgeLoadingOverlay.addQuad((BufferVertexConsumer)bufferbuilder, 0.0f, fbWidth, wtop, fbHeight, colour, fade);
        ForgeLoadingOverlay.addQuad((BufferVertexConsumer)bufferbuilder, 0.0f, fbWidth, 0.0f, wtop, colour, fade);
        ForgeLoadingOverlay.addQuad((BufferVertexConsumer)bufferbuilder, 0.0f, wleft, wtop, wbottom, colour, fade);
        ForgeLoadingOverlay.addQuad((BufferVertexConsumer)bufferbuilder, wright, fbWidth, wtop, wbottom, colour, fade);
        BufferUploader.m_231202_((BufferBuilder.RenderedBuffer)bufferbuilder.m_231175_());
        RenderSystem.enableBlend();
        RenderSystem.blendFunc((int)770, (int)771);
        RenderSystem.setShader(GameRenderer::m_172820_);
        RenderSystem.setShaderTexture((int)0, (int)this.displayWindow.getFramebufferTextureId());
        bufferbuilder.m_166779_(VertexFormat.Mode.QUADS, DefaultVertexFormat.f_85819_);
        bufferbuilder.m_5483_((double)wleft, (double)wbottom, 0.0).m_7421_(0.0f, 0.0f).m_85950_(1.0f, 1.0f, 1.0f, fade).m_5752_();
        bufferbuilder.m_5483_((double)wright, (double)wbottom, 0.0).m_7421_(1.0f, 0.0f).m_85950_(1.0f, 1.0f, 1.0f, fade).m_5752_();
        bufferbuilder.m_5483_((double)wright, (double)wtop, 0.0).m_7421_(1.0f, 1.0f).m_85950_(1.0f, 1.0f, 1.0f, fade).m_5752_();
        bufferbuilder.m_5483_((double)wleft, (double)wtop, 0.0).m_7421_(0.0f, 1.0f).m_85950_(1.0f, 1.0f, 1.0f, fade).m_5752_();
        GL30C.glTexParameterIi((int)3553, (int)10241, (int)9728);
        GL30C.glTexParameterIi((int)3553, (int)10240, (int)9728);
        BufferUploader.m_231202_((BufferBuilder.RenderedBuffer)bufferbuilder.m_231175_());
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        if (fadeouttimer >= 2.0f) {
            this.minecraft.m_91150_(null);
            this.displayWindow.close();
        }
        if (this.fadeOutStart == -1L && this.reload.m_7746_()) {
            this.progress.complete();
            this.fadeOutStart = Util.m_137550_();
            try {
                this.reload.m_7748_();
                this.onFinish.accept(Optional.empty());
            }
            catch (Throwable throwable) {
                this.onFinish.accept(Optional.of(throwable));
            }
            if (this.minecraft.f_91080_ != null) {
                this.minecraft.f_91080_.m_6575_(this.minecraft, this.minecraft.m_91268_().m_85445_(), this.minecraft.m_91268_().m_85446_());
            }
        }
    }

    private static void addQuad(BufferVertexConsumer bufferbuilder, float x0, float x1, float y0, float y1, ColourScheme.Colour colour, float fade) {
        bufferbuilder.m_5483_((double)x0, (double)y0, 0.0).m_85950_(colour.redf(), colour.greenf(), colour.bluef(), fade).m_5752_();
        bufferbuilder.m_5483_((double)x0, (double)y1, 0.0).m_85950_(colour.redf(), colour.greenf(), colour.bluef(), fade).m_5752_();
        bufferbuilder.m_5483_((double)x1, (double)y1, 0.0).m_85950_(colour.redf(), colour.greenf(), colour.bluef(), fade).m_5752_();
        bufferbuilder.m_5483_((double)x1, (double)y0, 0.0).m_85950_(colour.redf(), colour.greenf(), colour.bluef(), fade).m_5752_();
    }
}

