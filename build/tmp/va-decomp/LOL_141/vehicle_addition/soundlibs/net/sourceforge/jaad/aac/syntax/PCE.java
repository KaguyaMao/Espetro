/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.AudioDecoderInfo;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.ChannelConfiguration;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.DecoderConfig;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.Profile;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.SampleFrequency;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax.BitStream;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax.Element;
import java.util.List;
import java.util.logging.Logger;

public class PCE
implements Element,
AudioDecoderInfo {
    static final Logger LOGGER = Logger.getLogger("jaad.aac.syntax.PCE");
    public static final Element.Type TYPE = Element.Type.PCE;
    public static final List<Tag> TAGS = Element.createTagList(32, Tag::new);
    private final Tag tag;
    private static final int MAX_FRONT_CHANNEL_ELEMENTS = 16;
    private static final int MAX_SIDE_CHANNEL_ELEMENTS = 16;
    private static final int MAX_BACK_CHANNEL_ELEMENTS = 16;
    private static final int MAX_LFE_CHANNEL_ELEMENTS = 4;
    private static final int MAX_ASSOC_DATA_ELEMENTS = 8;
    private static final int MAX_VALID_CC_ELEMENTS = 16;
    private Profile profile;
    private SampleFrequency sampleFrequency;
    private int frontChannelElementsCount;
    private int sideChannelElementsCount;
    private int backChannelElementsCount;
    private int lfeChannelElementsCount;
    private int assocDataElementsCount;
    private int validCCElementsCount;
    boolean monoMixdown;
    boolean stereoMixdown;
    boolean matrixMixdownIDXPresent;
    int monoMixdownElementNumber;
    int stereoMixdownElementNumber;
    int matrixMixdownIDX;
    boolean pseudoSurround;
    private final TaggedElement[] frontElements;
    private final TaggedElement[] sideElements;
    private final TaggedElement[] backElements;
    private final int[] lfeElementTags;
    private final int[] assocDataElementTags;
    private final CCE[] ccElements;
    private byte[] commentFieldData;

    public static PCE read(BitStream in) {
        Tag tag = TAGS.get(in.readBits(4));
        PCE pce = new PCE(tag);
        pce.decode(in);
        return pce;
    }

    @Override
    public Tag getElementInstanceTag() {
        return this.tag;
    }

    protected PCE(Tag tag) {
        this.tag = tag;
        this.frontElements = new TaggedElement[16];
        this.sideElements = new TaggedElement[16];
        this.backElements = new TaggedElement[16];
        this.lfeElementTags = new int[4];
        this.assocDataElementTags = new int[8];
        this.ccElements = new CCE[16];
        this.sampleFrequency = SampleFrequency.SF_NONE;
    }

    @Override
    public void decode(BitStream in) {
        int i;
        this.profile = Profile.forInt(1 + in.readBits(2));
        this.sampleFrequency = SampleFrequency.decode(in);
        this.frontChannelElementsCount = in.readBits(4);
        this.sideChannelElementsCount = in.readBits(4);
        this.backChannelElementsCount = in.readBits(4);
        this.lfeChannelElementsCount = in.readBits(2);
        this.assocDataElementsCount = in.readBits(3);
        this.validCCElementsCount = in.readBits(4);
        this.monoMixdown = in.readBool();
        if (this.monoMixdown) {
            LOGGER.warning("mono mixdown present, but not yet supported");
            this.monoMixdownElementNumber = in.readBits(4);
        }
        if (this.stereoMixdown = in.readBool()) {
            LOGGER.warning("stereo mixdown present, but not yet supported");
            this.stereoMixdownElementNumber = in.readBits(4);
        }
        if (this.matrixMixdownIDXPresent = in.readBool()) {
            LOGGER.warning("matrix mixdown present, but not yet supported");
            this.matrixMixdownIDX = in.readBits(2);
            this.pseudoSurround = in.readBool();
        }
        this.readTaggedElementArray(this.frontElements, in, this.frontChannelElementsCount);
        this.readTaggedElementArray(this.sideElements, in, this.sideChannelElementsCount);
        this.readTaggedElementArray(this.backElements, in, this.backChannelElementsCount);
        for (i = 0; i < this.lfeChannelElementsCount; ++i) {
            this.lfeElementTags[i] = in.readBits(4);
        }
        for (i = 0; i < this.assocDataElementsCount; ++i) {
            this.assocDataElementTags[i] = in.readBits(4);
        }
        for (i = 0; i < this.validCCElementsCount; ++i) {
            this.ccElements[i] = new CCE(in.readBool(), in.readBits(4));
        }
        in.byteAlign();
        int commentFieldBytes = in.readBits(8);
        this.commentFieldData = new byte[commentFieldBytes];
        for (int i2 = 0; i2 < commentFieldBytes; ++i2) {
            this.commentFieldData[i2] = (byte)in.readBits(8);
        }
    }

    private void readTaggedElementArray(TaggedElement[] te, BitStream in, int len) {
        for (int i = 0; i < len; ++i) {
            te[i] = new TaggedElement(in.readBool(), in.readBits(4));
        }
    }

    @Override
    public Profile getProfile() {
        return this.profile;
    }

    @Override
    public SampleFrequency getSampleFrequency() {
        return this.sampleFrequency;
    }

    public int getChannelCount() {
        int n;
        int count = this.lfeChannelElementsCount;
        for (n = 0; n < this.frontChannelElementsCount; ++n) {
            count += this.frontElements[n].isCPE ? 2 : 1;
        }
        for (n = 0; n < this.sideChannelElementsCount; ++n) {
            count += this.sideElements[n].isCPE ? 2 : 1;
        }
        for (n = 0; n < this.backChannelElementsCount; ++n) {
            count += this.backElements[n].isCPE ? 2 : 1;
        }
        return count;
    }

    @Override
    public ChannelConfiguration getChannelConfiguration() {
        return ChannelConfiguration.forChannelCount(this.getChannelCount());
    }

    static class Tag
    extends Element.InstanceTag {
        protected Tag(int id) {
            super(id);
        }

        @Override
        public Element.Type getType() {
            return TYPE;
        }

        @Override
        public Element newElement(DecoderConfig config) {
            return new PCE(this);
        }
    }

    public static class TaggedElement {
        private final boolean isCPE;
        private final int tag;

        public TaggedElement(boolean isCPE, int tag) {
            this.isCPE = isCPE;
            this.tag = tag;
        }

        public boolean isIsCPE() {
            return this.isCPE;
        }

        public int getTag() {
            return this.tag;
        }
    }

    public static class CCE {
        private final boolean isIndSW;
        private final int tag;

        public CCE(boolean isIndSW, int tag) {
            this.isIndSW = isIndSW;
            this.tag = tag;
        }

        public boolean isIsIndSW() {
            return this.isIndSW;
        }

        public int getTag() {
            return this.tag;
        }
    }
}

