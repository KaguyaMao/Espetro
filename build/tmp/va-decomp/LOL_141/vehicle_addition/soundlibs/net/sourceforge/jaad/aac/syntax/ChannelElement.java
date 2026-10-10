/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.DecoderConfig;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.filterbank.FilterBank;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.sbr.SBR;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax.BitStream;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax.CCE;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax.Element;
import java.util.List;
import java.util.function.Consumer;

public abstract class ChannelElement
implements Element {
    protected final DecoderConfig config;
    protected final ChannelTag tag;
    protected SBR sbr;
    private float[] dataL;
    private float[] dataR;

    protected ChannelElement(DecoderConfig config, ChannelTag tag) {
        this.config = config;
        this.tag = tag;
    }

    @Override
    public ChannelTag getElementInstanceTag() {
        return this.tag;
    }

    public abstract boolean isChannelPair();

    public abstract boolean isStereo();

    @Override
    public void decode(BitStream in) {
        if (this.sbr != null) {
            this.sbr.invalidate();
        }
    }

    protected abstract SBR openSBR();

    void decodeSBR(BitStream in, boolean crc) {
        if (!this.config.isSBREnabled()) {
            return;
        }
        if (this.sbr == null) {
            this.sbr = this.openSBR();
        }
        if (this.sbr != null) {
            this.sbr.decode(in, crc);
        }
    }

    boolean isSBRPresent() {
        return this.sbr != null && this.sbr.isValid();
    }

    SBR getSBR() {
        return this.sbr;
    }

    private float[] newData() {
        int len = this.config.getSampleLength();
        return new float[len];
    }

    public float[] getDataL() {
        if (this.dataL == null) {
            this.dataL = this.newData();
        }
        return this.dataL;
    }

    public float[] getDataR() {
        if (this.dataR == null) {
            this.dataR = this.newData();
        }
        return this.dataR;
    }

    public abstract void process(FilterBank var1, List<CCE> var2, Consumer<float[]> var3);

    void processDependentCoupling(List<CCE> cces, int couplingPoint, float[] dataL, float[] dataR) {
        int elementID = this.getElementInstanceTag().getId();
        for (CCE cce : cces) {
            int index = 0;
            if (cce == null || cce.getCouplingPoint() != couplingPoint) continue;
            for (int c = 0; c <= cce.getCoupledCount(); ++c) {
                int chSelect = cce.getCHSelect(c);
                if (cce.isChannelPair(c) == this.isChannelPair() && cce.getIDSelect(c) == elementID) {
                    if (chSelect != 1) {
                        cce.applyDependentCoupling(index, dataL);
                        if (chSelect != 0) {
                            ++index;
                        }
                    }
                    if (chSelect == 2) continue;
                    cce.applyDependentCoupling(index, dataR);
                    ++index;
                    continue;
                }
                index += 1 + (chSelect == 3 ? 1 : 0);
            }
        }
    }

    void processIndependentCoupling(List<CCE> cces, float[] dataL, float[] dataR) {
        int elementID = this.getElementInstanceTag().getId();
        for (CCE cce : cces) {
            int index = 0;
            if (cce == null || cce.getCouplingPoint() != 2) continue;
            for (int c = 0; c <= cce.getCoupledCount(); ++c) {
                int chSelect = cce.getCHSelect(c);
                if (cce.isChannelPair(c) == this.isChannelPair() && cce.getIDSelect(c) == elementID) {
                    if (chSelect != 1) {
                        cce.applyIndependentCoupling(index, dataL);
                        if (chSelect != 0) {
                            ++index;
                        }
                    }
                    if (chSelect == 2) continue;
                    cce.applyIndependentCoupling(index, dataR);
                    ++index;
                    continue;
                }
                index += 1 + (chSelect == 3 ? 1 : 0);
            }
        }
    }

    static abstract class ChannelTag
    extends Element.InstanceTag {
        protected ChannelTag(int id) {
            super(id);
        }

        public abstract boolean isChannelPair();

        @Override
        public abstract ChannelElement newElement(DecoderConfig var1);
    }
}

