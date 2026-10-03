/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.ps;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.ps.EnvData;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.ps.FBType;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.ps.ICMode;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax.BitStream;

public abstract class ICData<Mode extends ICMode>
extends EnvData<Mode> {
    ICData() {
        super(34);
    }

    FBType fbType() {
        return this.mode == null ? null : ((ICMode)this.mode).fbType();
    }

    Mode readMode(BitStream ld) {
        boolean enabled = ld.readBool();
        if (enabled) {
            int id = ld.readBits(3);
            this.mode = this.mode(id);
        } else {
            this.mode = null;
        }
        return (Mode)((ICMode)this.mode);
    }
}

