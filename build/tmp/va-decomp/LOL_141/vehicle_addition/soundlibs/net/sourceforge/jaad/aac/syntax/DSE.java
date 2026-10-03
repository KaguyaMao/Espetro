/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.DecoderConfig;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax.BitStream;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax.Element;
import java.util.List;

class DSE
implements Element {
    public static final Element.Type TYPE = Element.Type.DSE;
    public static final List<Tag> TAGS = Element.createTagList(32, Tag::new);
    private final Tag tag;
    private byte[] dataStreamBytes;

    @Override
    public Tag getElementInstanceTag() {
        return this.tag;
    }

    public DSE(DecoderConfig config, Tag tag) {
        this.tag = tag;
    }

    @Override
    public void decode(BitStream in) {
        boolean byteAlign = in.readBool();
        int count = in.readBits(8);
        if (count == 255) {
            count += in.readBits(8);
        }
        if (byteAlign) {
            in.byteAlign();
        }
        this.dataStreamBytes = new byte[count];
        for (int i = 0; i < count; ++i) {
            this.dataStreamBytes[i] = (byte)in.readBits(8);
        }
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
            return new DSE(config, this);
        }
    }
}

