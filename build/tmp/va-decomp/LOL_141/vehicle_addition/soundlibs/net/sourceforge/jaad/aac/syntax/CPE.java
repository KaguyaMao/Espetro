/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.AACException;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.DecoderConfig;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.filterbank.FilterBank;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.sbr.SBR;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.sbr.SBR2;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax.BitStream;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax.CCE;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax.ChannelElement;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax.Element;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax.ICSInfo;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax.ICStream;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.tools.IS;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.tools.MS;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.tools.MSMask;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CPE
extends ChannelElement {
    static final Logger LOGGER = Logger.getLogger("jaad.aac.syntax.CPE");
    public static final Element.Type TYPE = Element.Type.CPE;
    public static final List<Tag> TAGS = Element.createTagList(16, Tag::new);
    public static final int MAX_MS_MASK = 128;
    private MSMask msMask;
    private boolean[] msUsed = new boolean[128];
    private boolean commonWindow;
    private final ICStream icsL;
    private final ICStream icsR;

    public CPE(DecoderConfig config, ChannelElement.ChannelTag tag) {
        super(config, tag);
        this.icsL = new ICStream(config);
        this.icsR = new ICStream(config);
    }

    @Override
    public boolean isChannelPair() {
        return true;
    }

    @Override
    public boolean isStereo() {
        return true;
    }

    @Override
    protected SBR openSBR() {
        return new SBR2(this.config);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public void decode(BitStream in) {
        super.decode(in);
        this.commonWindow = in.readBool();
        ICSInfo infoL = this.icsL.getInfo();
        ICSInfo infoR = this.icsR.getInfo();
        LOGGER.log(Level.FINE, () -> String.format("CPE %s", this.commonWindow ? "common" : ""));
        if (this.commonWindow) {
            infoL.decode(in, this.commonWindow);
            infoR.setCommonData(in, infoL);
            this.msMask = MSMask.forInt(in.readBits(2));
            if (this.msMask.equals((Object)MSMask.TYPE_USED)) {
                int maxSFB = infoL.getMaxSFB();
                int windowGroupCount = infoL.getWindowGroupCount();
                for (int idx = 0; idx < windowGroupCount * maxSFB; ++idx) {
                    this.msUsed[idx] = in.readBool();
                }
            } else if (this.msMask.equals((Object)MSMask.TYPE_ALL_1)) {
                Arrays.fill(this.msUsed, true);
            } else {
                if (!this.msMask.equals((Object)MSMask.TYPE_ALL_0)) throw new AACException("reserved MS mask type used");
                Arrays.fill(this.msUsed, false);
            }
        } else {
            this.msMask = MSMask.TYPE_ALL_0;
            Arrays.fill(this.msUsed, false);
        }
        this.icsL.decode(in, this.commonWindow, this.config);
        this.icsR.decode(in, this.commonWindow, this.config);
    }

    public ICStream getLeftChannel() {
        return this.icsL;
    }

    public ICStream getRightChannel() {
        return this.icsR;
    }

    public MSMask getMSMask() {
        return this.msMask;
    }

    public boolean isMSUsed(int off) {
        return this.msUsed[off];
    }

    public boolean isMSMaskPresent() {
        return !this.msMask.equals((Object)MSMask.TYPE_ALL_0);
    }

    public boolean isCommonWindow() {
        return this.commonWindow;
    }

    @Override
    public void process(FilterBank filterBank, List<CCE> cces, Consumer<float[]> target) {
        float[] dataL = this.getDataL();
        float[] dataR = this.getDataR();
        float[] iqDataL = this.icsL.getInvQuantData();
        float[] iqDataR = this.icsR.getInvQuantData();
        if (this.isCommonWindow() & this.isMSMaskPresent()) {
            MS.process(this, iqDataL, iqDataR);
        }
        this.icsL.processICP();
        this.icsR.processICP();
        IS.process(this, iqDataL, iqDataR);
        this.icsL.processLTP(filterBank);
        this.icsR.processLTP(filterBank);
        this.processDependentCoupling(cces, 0, iqDataL, iqDataR);
        this.icsL.processTNS();
        this.icsR.processTNS();
        this.processDependentCoupling(cces, 1, iqDataL, iqDataR);
        this.icsL.process(dataL, filterBank);
        this.icsR.process(dataR, filterBank);
        this.icsL.updateLTP(dataL);
        this.icsR.updateLTP(dataR);
        this.processIndependentCoupling(cces, dataL, dataR);
        this.icsL.processGainControl();
        this.icsR.processGainControl();
        if (this.isSBRPresent() && this.config.isSBREnabled()) {
            if (dataL.length == this.config.getFrameLength()) {
                LOGGER.log(Level.WARNING, "SBR data present, but buffer has normal size!");
            }
            this.getSBR().process(dataL, dataR);
        } else if (dataL.length != this.config.getFrameLength()) {
            SBR.upsample(dataL);
            SBR.upsample(dataR);
        }
        target.accept(dataL);
        target.accept(dataR);
    }

    static class Tag
    extends ChannelElement.ChannelTag {
        protected Tag(int id) {
            super(id);
        }

        @Override
        public boolean isChannelPair() {
            return true;
        }

        @Override
        public Element.Type getType() {
            return TYPE;
        }

        @Override
        public ChannelElement newElement(DecoderConfig config) {
            return new CPE(config, this);
        }
    }
}

