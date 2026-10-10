/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.ps;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.ps.ExtData;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.ps.IIDData;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.ps.IIDMode;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax.BitStream;

public class Extension {
    final IIDData parent;
    boolean enabled = false;
    ExtData data = null;

    public Extension(IIDData parent) {
        this.parent = parent;
    }

    ExtData data() {
        if (this.enabled && this.data == null) {
            this.data = new ExtData();
        }
        return this.data;
    }

    boolean readMode(BitStream ld) {
        this.enabled = ld.readBool();
        ExtData data = this.data();
        if (data != null) {
            data.setMode(this.enabled ? (IIDMode)this.parent.mode : null);
        }
        return this.enabled;
    }

    void readData(BitStream ld, int num_env) {
        if (this.enabled) {
            int cnt = ld.readBits(4);
            if (cnt == 15) {
                cnt += ld.readBits(8);
            }
            ld = ld.readSubStream(8 * cnt);
            while (ld.getBitsLeft() > 7) {
                ExtData data;
                int ps_extension_id = ld.readBits(2);
                if (ps_extension_id != 0 || (data = this.data()) == null) continue;
                data.readData(ld, num_env);
            }
        }
    }

    void decode(int num_env) {
        if (this.enabled && this.data != null) {
            this.data.decode(num_env);
        }
    }

    void update(int num_env) {
        if (this.enabled && this.data != null) {
            this.data.update(num_env);
        }
    }

    void restore(int num_env) {
        if (this.enabled && this.data != null) {
            this.data.restore(num_env);
        }
    }

    public void mapTo34(int num_env) {
        if (this.enabled && this.data != null) {
            this.data.mapTo34(num_env);
        }
    }

    int nr_par() {
        if (this.enabled && this.data != null) {
            return this.data.nr_par();
        }
        return 0;
    }
}

