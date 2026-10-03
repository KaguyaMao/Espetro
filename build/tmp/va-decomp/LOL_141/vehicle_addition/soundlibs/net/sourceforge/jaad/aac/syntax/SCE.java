/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.DecoderConfig;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.filterbank.FilterBank;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.sbr.SBR;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.sbr.SBR1;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax.BitStream;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax.CCE;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax.ChannelElement;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax.Element;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax.ICStream;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

class SCE
extends ChannelElement {
    static final Logger LOGGER = Logger.getLogger("jaad.SCE");
    public static final Element.Type TYPE = Element.Type.SCE;
    public static final List<Tag> TAGS = Element.createTagList(16, Tag::new);
    private final ICStream ics;

    SCE(DecoderConfig config, Tag tag) {
        super(config, tag);
        this.ics = new ICStream(config);
    }

    @Override
    protected SBR openSBR() {
        return new SBR1(this.config);
    }

    @Override
    public void decode(BitStream in) {
        super.decode(in);
        this.ics.decode(in, false, this.config);
    }

    public ICStream getICStream() {
        return this.ics;
    }

    @Override
    public boolean isChannelPair() {
        return false;
    }

    @Override
    public boolean isStereo() {
        return this.sbr != null && this.config.isSBREnabled();
    }

    @Override
    public void process(FilterBank filterBank, List<CCE> cces, Consumer<float[]> target) {
        float[] iqData = this.ics.getInvQuantData();
        float[] dataL = this.getDataL();
        this.ics.processICP();
        this.ics.processLTP(filterBank);
        this.processDependentCoupling(cces, 0, iqData, null);
        this.ics.processTNS();
        this.processDependentCoupling(cces, 1, iqData, null);
        this.ics.process(dataL, filterBank);
        this.ics.updateLTP(dataL);
        this.processIndependentCoupling(cces, dataL, null);
        this.ics.processGainControl();
        target.accept(dataL);
        if (this.isSBRPresent() && this.config.isSBREnabled()) {
            if (dataL.length != this.config.getSampleLength()) {
                LOGGER.log(Level.WARNING, "SBR data present, but buffer has normal size!");
            }
            float[] dataR = this.getDataR();
            this.getSBR().process(dataL, dataR);
            target.accept(dataR);
        } else if (dataL.length != this.config.getFrameLength()) {
            SBR.upsample(dataL);
        }
    }

    static class Tag
    extends ChannelElement.ChannelTag {
        protected Tag(int id) {
            super(id);
        }

        @Override
        public boolean isChannelPair() {
            return false;
        }

        @Override
        public Element.Type getType() {
            return TYPE;
        }

        @Override
        public ChannelElement newElement(DecoderConfig config) {
            return new SCE(config, this);
        }
    }
}

