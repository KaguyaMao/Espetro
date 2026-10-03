/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.tools;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.AACException;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.SampleRate;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax.BitStream;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax.ICSInfo;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax.ICStream;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.tools.TNSTables;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.util.Utils;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TNS
implements TNSTables {
    static final Logger LOGGER = Logger.getLogger("jaad.aac.syntax.TNS");
    private static final int TNS_MAX_ORDER = 20;
    private static final int[] SHORT_BITS;
    private static final int[] LONG_BITS;
    private int[] nFilt = new int[8];
    private int[][] length = new int[8][4];
    private int[][] order;
    private boolean[][] direction = new boolean[8][4];
    private float[][][] coef;

    public TNS() {
        this.order = new int[8][4];
        this.coef = new float[8][4][20];
    }

    public void decode(BitStream in, ICSInfo info) {
        int windowCount = info.getWindowCount();
        int[] bits = info.isEightShortFrame() ? SHORT_BITS : LONG_BITS;
        for (int w = 0; w < windowCount; ++w) {
            this.nFilt[w] = in.readBits(bits[0]);
            if (this.nFilt[w] == 0) continue;
            int coefRes = in.readBit();
            for (int filt = 0; filt < this.nFilt[w]; ++filt) {
                this.length[w][filt] = in.readBits(bits[1]);
                this.order[w][filt] = in.readBits(bits[2]);
                if (this.order[w][filt] > 20) {
                    throw new AACException("TNS filter out of range: " + this.order[w][filt]);
                }
                if (this.order[w][filt] == 0) continue;
                this.direction[w][filt] = in.readBool();
                int coefCompress = in.readBit();
                int coefLen = coefRes + 3 - coefCompress;
                int tmp = 2 * coefCompress + coefRes;
                for (int i = 0; i < this.order[w][filt]; ++i) {
                    this.coef[w][filt][i] = TNS_TABLES[tmp][in.readBits(coefLen)];
                }
            }
        }
    }

    public void process(ICStream ics, float[] spec, SampleRate sf, boolean decode) {
        LOGGER.warning("TNS unavailable");
    }

    static {
        if (!Utils.isDebug) {
            LOGGER.setLevel(Level.OFF);
        }
        SHORT_BITS = new int[]{1, 4, 3};
        LONG_BITS = new int[]{2, 6, 5};
    }
}

