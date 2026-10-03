/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.DecoderConfig;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.sbr.SBR;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax.ChannelElement;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax.Element;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax.SCE;
import java.util.List;

class LFE
extends SCE {
    public static final Element.Type TYPE = Element.Type.LFE;
    public static final List<Tag> TAGS = Element.createTagList(16, Tag::new);

    LFE(DecoderConfig config, Tag tag) {
        super(config, tag);
    }

    @Override
    protected SBR openSBR() {
        return null;
    }

    @Override
    public boolean isChannelPair() {
        return false;
    }

    public boolean isLFE() {
        return true;
    }

    static class Tag
    extends SCE.Tag {
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
            return new LFE(config, this);
        }
    }
}

