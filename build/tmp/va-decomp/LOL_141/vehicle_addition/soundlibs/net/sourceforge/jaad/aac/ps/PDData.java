/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.ps;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.ps.EnvData;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.ps.IIDMode;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.ps.PDMode;

public class PDData
extends EnvData<PDMode> {
    private final PDMode[] modes;
    public float[][][] prev = new float[20][2][2];

    PDData(PDMode[] modes) {
        super(17);
        this.modes = modes;
    }

    void setMode(IIDMode mode) {
        this.mode = mode == null ? null : this.mode(mode.id);
    }

    @Override
    protected PDMode mode(int id) {
        return this.modes[id];
    }
}

