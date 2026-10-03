/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.latvian.mods.unit.FixedNumberUnit
 *  dev.latvian.mods.unit.Unit
 *  dev.latvian.mods.unit.UnitVariables
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 */
package dev.latvian.mods.kubejs.client.painter.screen;

import dev.latvian.mods.kubejs.client.painter.Painter;
import dev.latvian.mods.kubejs.client.painter.PainterObject;
import dev.latvian.mods.kubejs.client.painter.PainterObjectProperties;
import dev.latvian.mods.kubejs.client.painter.PainterObjectStorage;
import dev.latvian.mods.kubejs.client.painter.screen.BoxObject;
import dev.latvian.mods.kubejs.client.painter.screen.MultiMaxFunc;
import dev.latvian.mods.kubejs.client.painter.screen.PaintScreenEventJS;
import dev.latvian.mods.kubejs.client.painter.screen.ScreenPainterObject;
import dev.latvian.mods.unit.FixedNumberUnit;
import dev.latvian.mods.unit.Unit;
import dev.latvian.mods.unit.UnitVariables;
import java.util.ArrayList;
import java.util.Collection;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

public class ScreenGroup
extends BoxObject {
    public final PainterObjectStorage storage;
    public Unit scaleX = FixedNumberUnit.ONE;
    public Unit scaleY = FixedNumberUnit.ONE;
    public Unit paddingW = FixedNumberUnit.ZERO;
    public Unit paddingH = FixedNumberUnit.ZERO;

    public ScreenGroup(Painter painter) {
        super(painter);
        this.storage = new PainterObjectStorage(painter);
    }

    @Override
    protected void load(PainterObjectProperties properties) {
        super.load(properties);
        Tag c = properties.tag.m_128423_("children");
        if (c instanceof CompoundTag) {
            CompoundTag tag = (CompoundTag)c;
            this.storage.handle(tag);
        }
        this.paddingW = properties.getUnit("paddingW", this.paddingW);
        this.paddingH = properties.getUnit("paddingH", this.paddingH);
        if (properties.hasAny("scale")) {
            this.scaleX = this.scaleY = properties.getUnit("scale", (Unit)FixedNumberUnit.ONE);
        } else {
            this.scaleX = properties.getUnit("scaleX", this.scaleX);
            this.scaleY = properties.getUnit("scaleY", this.scaleY);
        }
    }

    @Override
    public void preDraw(PaintScreenEventJS event) {
        this.w = FixedNumberUnit.ZERO;
        this.h = FixedNumberUnit.ZERO;
        Collection<PainterObject> objects = this.storage.getObjects();
        if (objects.isEmpty()) {
            return;
        }
        ArrayList<Unit> wunits = new ArrayList<Unit>(objects.size());
        ArrayList<Unit> hunits = new ArrayList<Unit>(objects.size());
        for (PainterObject object : objects) {
            ScreenPainterObject s;
            if (object instanceof ScreenPainterObject) {
                s = (ScreenPainterObject)object;
                s.preDraw(event);
            }
            if (!(object instanceof BoxObject)) continue;
            s = (BoxObject)object;
            wunits.add(((BoxObject)s).x.add(((BoxObject)s).w));
            hunits.add(((BoxObject)s).y.add(((BoxObject)s).h));
        }
        this.w = new MultiMaxFunc(wunits).add(this.paddingW);
        this.h = new MultiMaxFunc(hunits).add(this.paddingH);
    }

    @Override
    public void draw(PaintScreenEventJS event) {
        float ax = event.alignX(this.x.getFloat((UnitVariables)event), this.w.getFloat((UnitVariables)event), this.alignX);
        float ay = event.alignY(this.y.getFloat((UnitVariables)event), this.h.getFloat((UnitVariables)event), this.alignY);
        float az = this.z.getFloat((UnitVariables)event);
        event.push();
        event.translate(ax, ay, az);
        if (this.scaleX != FixedNumberUnit.ONE || this.scaleY != FixedNumberUnit.ONE) {
            event.scale(this.scaleX.getFloat((UnitVariables)event), this.scaleY.getFloat((UnitVariables)event), 1.0f);
        }
        for (PainterObject object : this.storage.getObjects()) {
            if (!(object instanceof ScreenPainterObject)) continue;
            ScreenPainterObject s = (ScreenPainterObject)object;
            s.draw(event);
        }
        event.pop();
    }
}

