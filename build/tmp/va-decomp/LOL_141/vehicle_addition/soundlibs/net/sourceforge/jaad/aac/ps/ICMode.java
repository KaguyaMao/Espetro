/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.ps;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.ps.EnvMode;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.ps.FBType;

public abstract class ICMode
extends EnvMode {
    public ICMode(int id, int nr_par) {
        super(id, nr_par);
    }

    FBType fbType() {
        return this.id % 3 == 2 ? FBType.T34 : FBType.T20;
    }

    @Override
    int stride() {
        return this.id % 3 == 0 ? 2 : 0;
    }
}

