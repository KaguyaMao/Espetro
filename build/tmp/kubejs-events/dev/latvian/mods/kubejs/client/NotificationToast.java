/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.BufferBuilder
 *  com.mojang.blaze3d.vertex.BufferBuilder$RenderedBuffer
 *  com.mojang.blaze3d.vertex.BufferUploader
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.Tesselator
 *  com.mojang.blaze3d.vertex.VertexFormat$Mode
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.toasts.Toast
 *  net.minecraft.client.gui.components.toasts.Toast$Visibility
 *  net.minecraft.client.gui.components.toasts.ToastComponent
 *  net.minecraft.client.renderer.GameRenderer
 *  net.minecraft.client.renderer.texture.TextureAtlasSprite
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.FastColor$ARGB32
 *  net.minecraft.util.FormattedCharSequence
 *  net.minecraft.world.inventory.InventoryMenu
 *  net.minecraft.world.item.ItemStack
 *  org.joml.Matrix4f
 */
package dev.latvian.mods.kubejs.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import dev.latvian.mods.kubejs.bindings.TextWrapper;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.util.NotificationBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;

public class NotificationToast
implements Toast {
    public static final Map<Integer, BiFunction<Minecraft, String, ToastIcon>> ICONS = new HashMap<Integer, BiFunction<Minecraft, String, ToastIcon>>(Map.of(1, TextureIcon::new, 2, ItemIcon::new, 3, AtlasIcon::of));
    private final NotificationBuilder notification;
    private final long duration;
    private final ToastIcon icon;
    private final List<FormattedCharSequence> text;
    private int width;
    private int height;
    private long lastChanged;
    private boolean changed;

    public NotificationToast(Minecraft mc, NotificationBuilder notification) {
        this.notification = notification;
        this.duration = notification.duration.toMillis();
        this.icon = ICONS.containsKey(this.notification.iconType) ? ICONS.get(this.notification.iconType).apply(mc, this.notification.icon) : null;
        this.text = new ArrayList<FormattedCharSequence>(2);
        this.width = 0;
        this.height = 0;
        if (!TextWrapper.isEmpty(notification.text)) {
            this.text.addAll(mc.f_91062_.m_92923_((FormattedText)notification.text, 240));
        }
        for (FormattedCharSequence l : this.text) {
            this.width = Math.max(this.width, mc.f_91062_.m_92724_(l));
        }
        this.width += 12;
        if (this.icon != null) {
            this.width += 24;
        }
        this.height = Math.max(this.text.size() * 10 + 12, 28);
        if (this.text.isEmpty() && this.icon != null) {
            this.width = 28;
            this.height = 28;
        }
    }

    public int m_7828_() {
        return this.width;
    }

    public int m_94899_() {
        return this.height;
    }

    private void drawRectangle(Matrix4f m, int x0, int y0, int x1, int y1, int r, int g, int b) {
        RenderSystem.setShader(GameRenderer::m_172811_);
        BufferBuilder buf = Tesselator.m_85913_().m_85915_();
        buf.m_166779_(VertexFormat.Mode.QUADS, DefaultVertexFormat.f_85815_);
        buf.m_252986_(m, (float)x0, (float)y1, 0.0f).m_6122_(r, g, b, 255).m_5752_();
        buf.m_252986_(m, (float)x1, (float)y1, 0.0f).m_6122_(r, g, b, 255).m_5752_();
        buf.m_252986_(m, (float)x1, (float)y0, 0.0f).m_6122_(r, g, b, 255).m_5752_();
        buf.m_252986_(m, (float)x0, (float)y0, 0.0f).m_6122_(r, g, b, 255).m_5752_();
        BufferUploader.m_231202_((BufferBuilder.RenderedBuffer)buf.m_231175_());
    }

    public Toast.Visibility m_7172_(GuiGraphics graphics, ToastComponent toastComponent, long l) {
        if (this.changed) {
            this.lastChanged = l;
            this.changed = false;
        }
        Minecraft mc = toastComponent.m_94929_();
        PoseStack poseStack = graphics.m_280168_();
        poseStack.m_85836_();
        poseStack.m_85837_(-2.0, 2.0, 0.0);
        Matrix4f m = poseStack.m_85850_().m_252922_();
        int w = this.m_7828_();
        int h = this.m_94899_();
        int oc = this.notification.outlineColor.getRgbJS();
        int ocr = FastColor.ARGB32.m_13665_((int)oc);
        int ocg = FastColor.ARGB32.m_13667_((int)oc);
        int ocb = FastColor.ARGB32.m_13669_((int)oc);
        int bc = this.notification.borderColor.getRgbJS();
        int bcr = FastColor.ARGB32.m_13665_((int)bc);
        int bcg = FastColor.ARGB32.m_13667_((int)bc);
        int bcb = FastColor.ARGB32.m_13669_((int)bc);
        int bgc = this.notification.backgroundColor.getRgbJS();
        int bgcr = FastColor.ARGB32.m_13665_((int)bgc);
        int bgcg = FastColor.ARGB32.m_13667_((int)bgc);
        int bgcb = FastColor.ARGB32.m_13669_((int)bgc);
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        this.drawRectangle(m, 2, 0, w - 2, h, ocr, ocg, ocb);
        this.drawRectangle(m, 0, 2, w, h - 2, ocr, ocg, ocb);
        this.drawRectangle(m, 1, 1, w - 1, h - 1, ocr, ocg, ocb);
        this.drawRectangle(m, 2, 1, w - 2, h - 1, bcr, bcg, bcb);
        this.drawRectangle(m, 1, 2, w - 1, h - 2, bcr, bcg, bcb);
        this.drawRectangle(m, 2, 2, w - 2, h - 2, bgcr, bgcg, bgcb);
        if (this.icon != null) {
            this.icon.draw(mc, graphics, 14, h / 2, this.notification.iconSize);
        }
        int th = this.icon == null ? 6 : 26;
        int tv = (h - this.text.size() * 10) / 2 + 1;
        for (int i = 0; i < this.text.size(); ++i) {
            graphics.m_280649_(mc.f_91062_, this.text.get(i), th, tv + i * 10, 0xFFFFFF, this.notification.textShadow);
        }
        poseStack.m_85849_();
        return l - this.lastChanged < this.duration ? Toast.Visibility.SHOW : Toast.Visibility.HIDE;
    }

    public static interface ToastIcon {
        public void draw(Minecraft var1, GuiGraphics var2, int var3, int var4, int var5);
    }

    public record AtlasIcon(TextureAtlasSprite sprite) implements ToastIcon
    {
        public static AtlasIcon of(Minecraft mc, String icon) {
            String[] s = icon.split("\\|");
            if (s.length == 2) {
                return new AtlasIcon((TextureAtlasSprite)mc.m_91258_(new ResourceLocation(s[0])).apply(new ResourceLocation(s[1])));
            }
            return new AtlasIcon((TextureAtlasSprite)mc.m_91258_(InventoryMenu.f_39692_).apply(new ResourceLocation(icon)));
        }

        @Override
        public void draw(Minecraft mc, GuiGraphics graphics, int x, int y, int size) {
            RenderSystem.setShaderTexture((int)0, (ResourceLocation)this.sprite.m_247685_());
            RenderSystem.setShader(GameRenderer::m_172820_);
            RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            Matrix4f m = graphics.m_280168_().m_85850_().m_252922_();
            int p0 = -size / 2;
            int p1 = p0 + size;
            float u0 = this.sprite.m_118409_();
            float v0 = this.sprite.m_118411_();
            float u1 = this.sprite.m_118410_();
            float v1 = this.sprite.m_118412_();
            BufferBuilder buf = Tesselator.m_85913_().m_85915_();
            buf.m_166779_(VertexFormat.Mode.QUADS, DefaultVertexFormat.f_85819_);
            buf.m_252986_(m, (float)(x + p0), (float)(y + p1), 0.0f).m_7421_(u0, v1).m_6122_(255, 255, 255, 255).m_5752_();
            buf.m_252986_(m, (float)(x + p1), (float)(y + p1), 0.0f).m_7421_(u1, v1).m_6122_(255, 255, 255, 255).m_5752_();
            buf.m_252986_(m, (float)(x + p1), (float)(y + p0), 0.0f).m_7421_(u1, v0).m_6122_(255, 255, 255, 255).m_5752_();
            buf.m_252986_(m, (float)(x + p0), (float)(y + p0), 0.0f).m_7421_(u0, v0).m_6122_(255, 255, 255, 255).m_5752_();
            BufferUploader.m_231202_((BufferBuilder.RenderedBuffer)buf.m_231175_());
        }
    }

    public record ItemIcon(ItemStack stack) implements ToastIcon
    {
        public ItemIcon(Minecraft ignored, String icon) {
            this(ItemStackJS.of(icon));
        }

        @Override
        public void draw(Minecraft mc, GuiGraphics graphics, int x, int y, int size) {
            PoseStack m = RenderSystem.getModelViewStack();
            m.m_85836_();
            m.m_85837_((double)x - 2.0, (double)y + 2.0, 0.0);
            float s = (float)size / 16.0f;
            m.m_85841_(s, s, s);
            RenderSystem.applyModelViewMatrix();
            graphics.m_280203_(this.stack, -8, -8);
            m.m_85849_();
            RenderSystem.applyModelViewMatrix();
        }
    }

    public record TextureIcon(ResourceLocation texture) implements ToastIcon
    {
        public TextureIcon(Minecraft ignored, String icon) {
            this(new ResourceLocation(icon));
        }

        @Override
        public void draw(Minecraft mc, GuiGraphics graphics, int x, int y, int size) {
            RenderSystem.setShaderTexture((int)0, (ResourceLocation)this.texture);
            RenderSystem.setShader(GameRenderer::m_172820_);
            RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            Matrix4f m = graphics.m_280168_().m_85850_().m_252922_();
            int p0 = -size / 2;
            int p1 = p0 + size;
            BufferBuilder buf = Tesselator.m_85913_().m_85915_();
            buf.m_166779_(VertexFormat.Mode.QUADS, DefaultVertexFormat.f_85819_);
            buf.m_252986_(m, (float)(x + p0), (float)(y + p1), 0.0f).m_7421_(0.0f, 1.0f).m_6122_(255, 255, 255, 255).m_5752_();
            buf.m_252986_(m, (float)(x + p1), (float)(y + p1), 0.0f).m_7421_(1.0f, 1.0f).m_6122_(255, 255, 255, 255).m_5752_();
            buf.m_252986_(m, (float)(x + p1), (float)(y + p0), 0.0f).m_7421_(1.0f, 0.0f).m_6122_(255, 255, 255, 255).m_5752_();
            buf.m_252986_(m, (float)(x + p0), (float)(y + p0), 0.0f).m_7421_(0.0f, 0.0f).m_6122_(255, 255, 255, 255).m_5752_();
            BufferUploader.m_231202_((BufferBuilder.RenderedBuffer)buf.m_231175_());
        }
    }
}

