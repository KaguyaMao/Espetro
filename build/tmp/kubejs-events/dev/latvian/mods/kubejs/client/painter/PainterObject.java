/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.latvian.mods.rhino.util.SpecialEquality
 *  dev.latvian.mods.unit.FixedBooleanUnit
 *  dev.latvian.mods.unit.Unit
 *  net.minecraft.nbt.CompoundTag
 */
package dev.latvian.mods.kubejs.client.painter;

import dev.latvian.mods.kubejs.client.painter.PainterObjectProperties;
import dev.latvian.mods.kubejs.client.painter.PainterObjectStorage;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.rhino.util.SpecialEquality;
import dev.latvian.mods.unit.FixedBooleanUnit;
import dev.latvian.mods.unit.Unit;
import net.minecraft.nbt.CompoundTag;

public abstract class PainterObject
implements SpecialEquality {
    public String id = "";
    public PainterObjectStorage parent;
    public Unit visible = FixedBooleanUnit.TRUE;

    public PainterObject id(String i) {
        this.id = i;
        return this;
    }

    protected void load(PainterObjectProperties properties) {
        this.visible = properties.getUnit("visible", this.visible);
    }

    public final void update(CompoundTag tag) {
        if (tag.m_128471_("remove")) {
            if (this.parent != null) {
                this.parent.remove(this.id);
            }
        } else {
            try {
                this.load(new PainterObjectProperties(tag));
            }
            catch (Exception ex) {
                ConsoleJS.CLIENT.error("Failed to update Painter object " + this.id + "/" + this.getClass().getSimpleName() + ": " + String.valueOf(ex));
            }
        }
    }

    public boolean equals(Object o) {
        return o == this || o instanceof PainterObject && this.id.equals(((PainterObject)o).id);
    }

    public int hashCode() {
        return this.id.hashCode();
    }

    public String toString() {
        return this.id;
    }

    public boolean specialEquals(Object o, boolean shallow) {
        if (this == o || this.id == o) {
            return true;
        }
        if (o instanceof PainterObject) {
            PainterObject po = (PainterObject)o;
            return this.id.equals(po.id);
        }
        if (o instanceof String) {
            return this.id.equals(this.toString());
        }
        return false;
    }
}

