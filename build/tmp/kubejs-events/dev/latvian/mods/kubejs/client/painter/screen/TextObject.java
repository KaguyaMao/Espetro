/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.latvian.mods.unit.FixedBooleanUnit
 *  dev.latvian.mods.unit.FixedColorUnit
 *  dev.latvian.mods.unit.FixedNumberUnit
 *  dev.latvian.mods.unit.Unit
 *  dev.latvian.mods.unit.UnitVariables
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.util.FormattedCharSequence
 */
package dev.latvian.mods.kubejs.client.painter.screen;

import dev.latvian.mods.kubejs.bindings.TextWrapper;
import dev.latvian.mods.kubejs.client.painter.Painter;
import dev.latvian.mods.kubejs.client.painter.PainterObjectProperties;
import dev.latvian.mods.kubejs.client.painter.screen.BoxObject;
import dev.latvian.mods.kubejs.client.painter.screen.PaintScreenEventJS;
import dev.latvian.mods.unit.FixedBooleanUnit;
import dev.latvian.mods.unit.FixedColorUnit;
import dev.latvian.mods.unit.FixedNumberUnit;
import dev.latvian.mods.unit.Unit;
import dev.latvian.mods.unit.UnitVariables;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.FormattedCharSequence;

public class TextObject
extends BoxObject {
    public FormattedCharSequence[] text = new FormattedCharSequence[0];
    public Unit shadow = FixedBooleanUnit.FALSE;
    public Unit scale = FixedNumberUnit.ONE;
    public Unit color = FixedColorUnit.WHITE;
    public Unit centered = FixedBooleanUnit.FALSE;
    public Unit lineSpacing = FixedNumberUnit.TEN;
    private float[] textWidth = new float[0];

    public TextObject(Painter painter) {
        super(painter);
    }

    @Override
    protected void load(PainterObjectProperties properties) {
        super.load(properties);
        Tag tag = properties.tag.m_128423_("textLines");
        if (tag instanceof ListTag) {
            ListTag list = (ListTag)tag;
            this.text = new FormattedCharSequence[list.size()];
            for (int i = 0; i < list.size(); ++i) {
                this.text[i] = TextWrapper.of(list.get(i)).m_7532_();
            }
        } else {
            this.text = new FormattedCharSequence[]{TextWrapper.of(properties.tag.m_128423_("text")).m_7532_()};
        }
        this.shadow = properties.getUnit("shadow", this.shadow);
        this.scale = properties.getUnit("scale", this.scale);
        this.color = properties.getColor("color", this.color);
        this.centered = properties.getUnit("centered", this.centered);
        this.lineSpacing = properties.getUnit("lineSpacing", this.lineSpacing);
        this.textWidth = new float[this.text.length];
    }

    @Override
    public void preDraw(PaintScreenEventJS event) {
        float maxTextWidth = 0.0f;
        for (int i = 0; i < this.text.length; ++i) {
            this.textWidth[i] = event.font.m_92865_().m_92336_(this.text[i]);
            maxTextWidth = Math.max(maxTextWidth, this.textWidth[i]);
        }
        this.w = this.scale.mul((Unit)FixedNumberUnit.of((double)maxTextWidth));
        this.h = this.scale.mul(this.lineSpacing).mul((Unit)FixedNumberUnit.of((double)this.text.length));
    }

    @Override
    public void draw(PaintScreenEventJS event) {
        float ls = this.lineSpacing.getFloat((UnitVariables)event);
        float ax = event.alignX(this.x.getFloat((UnitVariables)event), this.w.getFloat((UnitVariables)event), this.alignX);
        float ay = event.alignY(this.y.getFloat((UnitVariables)event), this.h.getFloat((UnitVariables)event), this.alignY);
        float az = this.z.getFloat((UnitVariables)event);
        boolean c = this.centered.getBoolean((UnitVariables)event);
        boolean s = this.shadow.getBoolean((UnitVariables)event);
        event.push();
        event.translate(ax, ay, az);
        event.scale(this.scale.getFloat((UnitVariables)event));
        event.blend(true);
        for (int i = 0; i < this.text.length; ++i) {
            event.rawText(this.text[i], c ? (int)(-(this.textWidth[i] / 2.0f)) : 0, (int)((float)i * ls), this.color.getInt((UnitVariables)event), s);
        }
        event.pop();
    }
}

