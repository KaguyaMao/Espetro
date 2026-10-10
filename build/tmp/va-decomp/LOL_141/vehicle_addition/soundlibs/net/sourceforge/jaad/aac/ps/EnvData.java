/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.ps;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.ps.EnvMode;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.ps.Envelope;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax.BitStream;
import java.util.Arrays;

public abstract class EnvData<Mode extends EnvMode> {
    final int[] first;
    final Envelope[] envs;
    Mode mode = null;

    EnvData(int len) {
        this.first = new int[len];
        this.envs = new Envelope[5];
        int[] prev = this.first;
        for (int l = 0; l < this.envs.length; ++l) {
            Envelope e;
            this.envs[l] = e = new Envelope(prev);
            prev = e.index;
        }
    }

    Mode mode() {
        return this.mode == null ? this.mode(0) : this.mode;
    }

    protected abstract Mode mode(int var1);

    void readData(BitStream ld, int num_env) {
        if (this.mode != null) {
            for (int n = 0; n < num_env; ++n) {
                this.envs[n].read(ld, ((EnvMode)this.mode).tables(), ((EnvMode)this.mode).nr_par);
            }
        }
    }

    void decode(int num_env) {
        if (num_env == 0) {
            if (this.mode != null) {
                this.envs[0].restore();
            } else {
                this.envs[0].reset();
            }
        } else {
            for (int env = 0; env < num_env; ++env) {
                this.envs[env].decode((EnvMode)this.mode);
            }
        }
    }

    void update(int num_env) {
        if (num_env == 0) {
            Arrays.fill(this.first, 0);
        } else {
            System.arraycopy(this.envs[num_env - 1].index, 0, this.first, 0, this.first.length);
        }
    }

    void restore(int num_env) {
        this.envs[num_env].restore();
    }

    void mapTo34(int num_env) {
        if (this.mode != null && ((EnvMode)this.mode).id % 3 != 2) {
            for (int env = 0; env < num_env; ++env) {
                this.envs[env].map20To34();
            }
        }
    }
}

