/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.renderer.GameRenderer
 *  net.minecraft.resources.ResourceLocation
 */
package cc.sighs.auratip.api.radiamenu.icon;

import cc.sighs.auratip.api.radiamenu.icon.IRadialIcon;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;

public record TextureIcon(ResourceLocation id, float scale) implements IRadialIcon
{
    public static final ResourceLocation TYPE = new ResourceLocation("auratip", "texture");
    public static final Codec<TextureIcon> CODEC = RecordCodecBuilder.create(inst -> inst.group((App)ResourceLocation.f_135803_.fieldOf("id").forGetter(TextureIcon::id), (App)Codec.FLOAT.optionalFieldOf("scale", (Object)Float.valueOf(1.0f)).forGetter(TextureIcon::scale)).apply((Applicative)inst, TextureIcon::new));

    public TextureIcon(ResourceLocation id) {
        this(id, 1.0f);
    }

    @Override
    public void render(GuiGraphics graphics, int x, int y, float s, float alpha) {
        float finalScale = s * this.scale;
        if (finalScale <= 0.0f || alpha <= 0.0f) {
            return;
        }
        int size = (int)(24.0f * finalScale);
        int drawX = x - size / 2;
        int drawY = y - size / 2;
        RenderSystem.setShader(GameRenderer::m_172817_);
        RenderSystem.setShaderTexture((int)0, (ResourceLocation)this.id);
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)alpha);
        graphics.m_280163_(this.id, drawX, drawY, 0.0f, 0.0f, size, size, size, size);
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
    }

    public Codec<TextureIcon> codec() {
        return CODEC;
    }
}

