/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.latvian.mods.rhino.mod.util.color.Color
 *  dev.latvian.mods.rhino.mod.wrapper.ColorWrapper
 *  dev.latvian.mods.unit.FixedColorUnit
 *  dev.latvian.mods.unit.FixedNumberUnit
 *  dev.latvian.mods.unit.Unit
 *  dev.latvian.mods.unit.UnitContext
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.resources.ResourceLocation
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.client.painter;

import dev.latvian.mods.rhino.mod.util.color.Color;
import dev.latvian.mods.rhino.mod.wrapper.ColorWrapper;
import dev.latvian.mods.unit.FixedColorUnit;
import dev.latvian.mods.unit.FixedNumberUnit;
import dev.latvian.mods.unit.Unit;
import dev.latvian.mods.unit.UnitContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class PainterObjectProperties {
    public final CompoundTag tag;

    public PainterObjectProperties(CompoundTag t) {
        this.tag = t;
    }

    public boolean hasAny(String key) {
        return this.tag.m_128441_(key);
    }

    public boolean has(String key, int type) {
        return this.tag.m_128425_(key, type);
    }

    public boolean hasNumber(String key) {
        return this.tag.m_128425_(key, 99);
    }

    public boolean hasString(String key) {
        return this.tag.m_128425_(key, 8);
    }

    public String getString(String key, String def) {
        return this.has(key, 8) ? this.tag.m_128461_(key) : def;
    }

    @Nullable
    public ResourceLocation getResourceLocation(String key, @Nullable ResourceLocation def) {
        String s = this.getString(key, "").trim();
        return s.isEmpty() ? def : new ResourceLocation(s);
    }

    public Unit getUnit(String key, Unit def) {
        if (this.hasString(key)) {
            return UnitContext.DEFAULT.parse(this.tag.m_128461_(key));
        }
        if (this.hasNumber(key)) {
            return FixedNumberUnit.of((double)this.tag.m_128457_(key));
        }
        return def;
    }

    public Unit getColor(String key, Unit def) {
        Color col;
        if (this.hasString(key) && (col = (Color)ColorWrapper.MAP.get(this.getString(key, ""))) != null) {
            return FixedColorUnit.of((int)col.getArgbJS(), (boolean)true);
        }
        return this.getUnit(key, def);
    }
}

