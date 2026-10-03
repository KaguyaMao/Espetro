/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.ItemStack
 */
package cc.sighs.auratip.api.radiamenu.icon;

import cc.sighs.auratip.api.radiamenu.icon.IRadialIcon;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public record ItemIcon(ItemStack stack, float scale) implements IRadialIcon
{
    public static final ResourceLocation TYPE = new ResourceLocation("auratip", "item");
    public static final Codec<ItemIcon> CODEC = RecordCodecBuilder.create(inst -> inst.group((App)ItemStack.f_41582_.fieldOf("stack").forGetter(ItemIcon::stack), (App)Codec.FLOAT.optionalFieldOf("scale", (Object)Float.valueOf(1.0f)).forGetter(ItemIcon::scale)).apply((Applicative)inst, ItemIcon::new));

    public ItemIcon(ItemStack stack) {
        this(stack, 1.0f);
    }

    @Override
    public void render(GuiGraphics graphics, int x, int y, float s, float alpha) {
        float finalScale = s * this.scale;
        if (finalScale <= 0.0f || alpha <= 0.0f) {
            return;
        }
        if (this.stack.m_41619_()) {
            return;
        }
        int iconSize = (int)(16.0f * finalScale);
        int drawX = x - iconSize / 2;
        int drawY = y - iconSize / 2;
        graphics.m_280168_().m_85836_();
        graphics.m_280168_().m_252880_((float)drawX, (float)drawY, 0.0f);
        graphics.m_280168_().m_85841_(finalScale, finalScale, 1.0f);
        graphics.m_280246_(1.0f, 1.0f, 1.0f, alpha);
        graphics.m_280203_(this.stack, 0, 0);
        graphics.m_280246_(1.0f, 1.0f, 1.0f, 1.0f);
        graphics.m_280168_().m_85849_();
    }

    public Codec<ItemIcon> codec() {
        return CODEC;
    }
}

