/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.ps;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.ps.ICCMode;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.ps.ICData;

public class ICCData
extends ICData<ICCMode> {
    @Override
    ICCMode mode() {
        return this.mode == null ? this.mode(1) : (ICCMode)this.mode;
    }

    @Override
    protected ICCMode mode(int id) {
        return ICCMode.mode(id);
    }
}

