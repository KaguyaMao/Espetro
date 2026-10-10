/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.ps;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.ps.EnvTables;

public abstract class EnvMode {
    final int id;
    final int nr_par;

    public EnvMode(int id, int nr_par) {
        this.id = id;
        this.nr_par = nr_par;
    }

    abstract int stride();

    abstract int clip(int var1);

    abstract EnvTables tables();
}

