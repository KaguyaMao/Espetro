/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.ps;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.ps.IIDMode;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.ps.PDData;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.ps.PDMode;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax.BitStream;

class ExtData {
    boolean enabled = false;
    final PDData ipd = PDMode.IpdData();
    final PDData opd = PDMode.OpdData();

    ExtData() {
    }

    void setMode(IIDMode mode) {
        this.ipd.setMode(mode);
        this.opd.setMode(mode);
    }

    void readData(BitStream ld, int num_env) {
        this.enabled = ld.readBool();
        if (this.enabled) {
            this.ipd.readData(ld, num_env);
            this.opd.readData(ld, num_env);
        }
        ld.readBit();
    }

    void decode(int num_env) {
        if (this.enabled) {
            this.ipd.decode(num_env);
            this.opd.decode(num_env);
        }
    }

    void update(int num_env) {
        this.ipd.update(num_env);
        this.opd.update(num_env);
    }

    void restore(int num_env) {
        this.ipd.update(num_env);
        this.opd.update(num_env);
    }

    public void mapTo34(int num_env) {
        this.ipd.mapTo34(num_env);
        this.opd.mapTo34(num_env);
    }

    public int nr_par() {
        int nr_par = ((PDMode)this.ipd.mode).nr_par;
        if (nr_par < 11) {
            nr_par = 11;
        }
        return nr_par;
    }
}

