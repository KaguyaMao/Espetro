/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.AbstractSliderButton
 *  net.minecraft.network.chat.Component
 *  net.minecraft.util.Mth
 */
package net.minecraftforge.client.gui.widget;

import java.text.DecimalFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class ForgeSlider
extends AbstractSliderButton {
    protected Component prefix;
    protected Component suffix;
    protected double minValue;
    protected double maxValue;
    protected double stepSize;
    protected boolean drawString;
    private final DecimalFormat format;

    public ForgeSlider(int x, int y, int width, int height, Component prefix, Component suffix, double minValue, double maxValue, double currentValue, double stepSize, int precision, boolean drawString) {
        super(x, y, width, height, (Component)Component.m_237119_(), 0.0);
        this.prefix = prefix;
        this.suffix = suffix;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.stepSize = Math.abs(stepSize);
        this.f_93577_ = this.snapToNearest((currentValue - minValue) / (maxValue - minValue));
        this.drawString = drawString;
        if (stepSize == 0.0) {
            precision = Math.min(precision, 4);
            StringBuilder builder = new StringBuilder("0");
            if (precision > 0) {
                builder.append('.');
            }
            while (precision-- > 0) {
                builder.append('0');
            }
            this.format = new DecimalFormat(builder.toString());
        } else {
            this.format = Mth.m_14082_((double)this.stepSize, (double)Math.floor(this.stepSize)) ? new DecimalFormat("0") : new DecimalFormat(Double.toString(this.stepSize).replaceAll("\\d", "0"));
        }
        this.m_5695_();
    }

    public ForgeSlider(int x, int y, int width, int height, Component prefix, Component suffix, double minValue, double maxValue, double currentValue, boolean drawString) {
        this(x, y, width, height, prefix, suffix, minValue, maxValue, currentValue, 1.0, 0, drawString);
    }

    public double getValue() {
        return this.f_93577_ * (this.maxValue - this.minValue) + this.minValue;
    }

    public long getValueLong() {
        return Math.round(this.getValue());
    }

    public int getValueInt() {
        return (int)this.getValueLong();
    }

    public void m_93611_(double value) {
        this.f_93577_ = this.snapToNearest((value - this.minValue) / (this.maxValue - this.minValue));
        this.m_5695_();
    }

    public String getValueString() {
        return this.format.format(this.getValue());
    }

    public void m_5716_(double mouseX, double mouseY) {
        this.m_93585_(mouseX);
    }

    protected void m_7212_(double mouseX, double mouseY, double dragX, double dragY) {
        super.m_7212_(mouseX, mouseY, dragX, dragY);
        this.m_93585_(mouseX);
    }

    public boolean m_7933_(int keyCode, int scanCode, int modifiers) {
        boolean flag;
        boolean bl = flag = keyCode == 263;
        if (flag || keyCode == 262) {
            float f;
            if (this.minValue > this.maxValue) {
                flag = !flag;
            }
            float f2 = f = flag ? -1.0f : 1.0f;
            if (this.stepSize <= 0.0) {
                this.setSliderValue(this.f_93577_ + (double)(f / (float)(this.f_93618_ - 8)));
            } else {
                this.m_93611_(this.getValue() + (double)f * this.stepSize);
            }
        }
        return false;
    }

    private void m_93585_(double mouseX) {
        this.setSliderValue((mouseX - (double)(this.m_252754_() + 4)) / (double)(this.f_93618_ - 8));
    }

    private void setSliderValue(double value) {
        double oldValue = this.f_93577_;
        this.f_93577_ = this.snapToNearest(value);
        if (!Mth.m_14082_((double)oldValue, (double)this.f_93577_)) {
            this.m_5697_();
        }
        this.m_5695_();
    }

    private double snapToNearest(double value) {
        if (this.stepSize <= 0.0) {
            return Mth.m_14008_((double)value, (double)0.0, (double)1.0);
        }
        value = Mth.m_14139_((double)Mth.m_14008_((double)value, (double)0.0, (double)1.0), (double)this.minValue, (double)this.maxValue);
        value = this.stepSize * (double)Math.round(value / this.stepSize);
        value = this.minValue > this.maxValue ? Mth.m_14008_((double)value, (double)this.maxValue, (double)this.minValue) : Mth.m_14008_((double)value, (double)this.minValue, (double)this.maxValue);
        return Mth.m_144914_((double)value, (double)this.minValue, (double)this.maxValue, (double)0.0, (double)1.0);
    }

    protected void m_5695_() {
        if (this.drawString) {
            this.m_93666_((Component)Component.m_237113_((String)"").m_7220_(this.prefix).m_130946_(this.getValueString()).m_7220_(this.suffix));
        } else {
            this.m_93666_((Component)Component.m_237119_());
        }
    }

    protected void m_5697_() {
    }

    public void m_87963_(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        Minecraft mc = Minecraft.m_91087_();
        guiGraphics.blitWithBorder(f_263683_, this.m_252754_(), this.m_252907_(), 0, this.m_264355_(), this.f_93618_, this.f_93619_, 200, 20, 2, 3, 2, 2);
        guiGraphics.blitWithBorder(f_263683_, this.m_252754_() + (int)(this.f_93577_ * (double)(this.f_93618_ - 8)), this.m_252907_(), 0, this.m_264270_(), 8, this.f_93619_, 200, 20, 2, 3, 2, 2);
        this.m_280372_(guiGraphics, mc.f_91062_, 2, this.getFGColor() | Mth.m_14167_((float)(this.f_93625_ * 255.0f)) << 24);
    }
}

