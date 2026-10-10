/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.latvian.mods.unit.Unit
 *  dev.latvian.mods.unit.UnitVariables
 */
package dev.latvian.mods.kubejs.client.painter.screen;

import dev.latvian.mods.unit.Unit;
import dev.latvian.mods.unit.UnitVariables;
import java.util.List;

public class MultiMaxFunc
extends Unit {
    public final List<Unit> units;

    public MultiMaxFunc(List<Unit> units) {
        this.units = units;
    }

    public double get(UnitVariables variables) {
        double d = 0.0;
        for (Unit unit : this.units) {
            d = Math.max(d, unit.get(variables));
        }
        return d;
    }
}

