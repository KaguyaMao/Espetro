/*
 * Decompiled with CFR 0.152.
 */
package dev.latvian.mods.kubejs.util;

import dev.latvian.mods.kubejs.util.TickTemporalUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.util.List;

public record TickDuration(long ticks) implements TemporalAmount
{
    public static final TickDuration ZERO = new TickDuration(0L);
    private static final List<TemporalUnit> UNITS = List.of(TickTemporalUnit.INSTANCE);

    @Override
    public long get(TemporalUnit unit) {
        return 0L;
    }

    @Override
    public List<TemporalUnit> getUnits() {
        return UNITS;
    }

    @Override
    public Temporal addTo(Temporal temporal) {
        if (this.ticks != 0L) {
            return temporal.plus(this.ticks, TickTemporalUnit.INSTANCE);
        }
        return temporal;
    }

    @Override
    public Temporal subtractFrom(Temporal temporal) {
        if (this.ticks != 0L) {
            return temporal.minus(this.ticks, TickTemporalUnit.INSTANCE);
        }
        return temporal;
    }
}

