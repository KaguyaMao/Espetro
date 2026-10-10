/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax.BitStream;

public class DRC {
    private static final int MAX_NBR_BANDS = 7;
    private final boolean[] excludeMask = new boolean[7];
    private final boolean[] additionalExcludedChannels = new boolean[7];
    private boolean pceTagPresent;
    private int pceInstanceTag;
    private int tagReservedBits;
    private boolean excludedChannelsPresent;
    private boolean bandsPresent;
    private int bandsIncrement;
    private int interpolationScheme;
    private int[] bandTop;
    private boolean progRefLevelPresent;
    private int progRefLevel;
    private int progRefLevelReservedBits;
    private boolean[] dynRngSgn;
    private int[] dynRngCtl;

    public void decode(BitStream in) {
        int i;
        int bandCount = 1;
        this.pceTagPresent = in.readBool();
        if (this.pceTagPresent) {
            this.pceInstanceTag = in.readBits(4);
            this.tagReservedBits = in.readBits(4);
        }
        if (this.excludedChannelsPresent = in.readBool()) {
            this.decodeExcludedChannels(in);
        }
        if (this.bandsPresent = in.readBool()) {
            this.bandsIncrement = in.readBits(4);
            this.interpolationScheme = in.readBits(4);
            this.bandTop = new int[bandCount += this.bandsIncrement];
            for (i = 0; i < bandCount; ++i) {
                this.bandTop[i] = in.readBits(8);
            }
        }
        if (this.progRefLevelPresent = in.readBool()) {
            this.progRefLevel = in.readBits(7);
            this.progRefLevelReservedBits = in.readBits(1);
        }
        this.dynRngSgn = new boolean[bandCount];
        this.dynRngCtl = new int[bandCount];
        for (i = 0; i < bandCount; ++i) {
            this.dynRngSgn[i] = in.readBool();
            this.dynRngCtl[i] = in.readBits(7);
        }
    }

    private int decodeExcludedChannels(BitStream in) {
        int exclChs = 0;
        do {
            for (int i = 0; i < 7; ++i) {
                this.excludeMask[exclChs] = in.readBool();
                ++exclChs;
            }
        } while (exclChs < 57 && in.readBool());
        return exclChs / 7 * 8;
    }
}

