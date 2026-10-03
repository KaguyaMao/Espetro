/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.od;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.MP4InputStream;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.od.DecoderConfigDescriptor;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.od.DecoderSpecificInfo;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.od.ESDescriptor;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.od.InitialObjectDescriptor;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.od.ObjectDescriptor;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.od.UnknownDescriptor;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.util.Utils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class Descriptor {
    static final Logger LOGGER = Logger.getLogger("jaad.mp4.od.Descriptor");
    public static final int TYPE_OBJECT_DESCRIPTOR = 1;
    public static final int TYPE_INITIAL_OBJECT_DESCRIPTOR = 2;
    public static final int TYPE_ES_DESCRIPTOR = 3;
    public static final int TYPE_DECODER_CONFIG_DESCRIPTOR = 4;
    public static final int TYPE_DECODER_SPECIFIC_INFO = 5;
    public static final int TYPE_SL_CONFIG_DESCRIPTOR = 6;
    public static final int TYPE_ES_ID_INC = 14;
    public static final int TYPE_MP4_INITIAL_OBJECT_DESCRIPTOR = 16;
    protected int type;
    protected int size;
    protected long start;
    private List<Descriptor> children = new ArrayList<Descriptor>();

    public static Descriptor createDescriptor(MP4InputStream in) throws IOException {
        int type = in.readByte();
        int read = 1;
        int size = 0;
        int b = 0;
        do {
            b = in.readByte();
            size <<= 7;
            size |= b & 0x7F;
            ++read;
        } while ((b & 0x80) == 128);
        Descriptor desc = Descriptor.forTag(type);
        desc.type = type;
        desc.size = size;
        desc.start = in.getOffset();
        desc.decode(in);
        long remaining = (long)size - (in.getOffset() - desc.start);
        if (remaining > 0L) {
            LOGGER.log(Level.INFO, "Descriptor: bytes left: {0}, offset: {1}", new Long[]{remaining, in.getOffset()});
            in.skipBytes(remaining);
        }
        desc.size += read;
        return desc;
    }

    private static Descriptor forTag(int tag) {
        return switch (tag) {
            case 1 -> new ObjectDescriptor();
            case 2, 16 -> new InitialObjectDescriptor();
            case 3 -> new ESDescriptor();
            case 4 -> new DecoderConfigDescriptor();
            case 5 -> new DecoderSpecificInfo();
            default -> {
                LOGGER.log(Level.INFO, "Unknown descriptor type: {0}", tag);
                yield new UnknownDescriptor();
            }
        };
    }

    protected Descriptor() {
    }

    abstract void decode(MP4InputStream var1) throws IOException;

    protected void readChildren(MP4InputStream in) throws IOException {
        while ((long)this.size - (in.getOffset() - this.start) > 0L) {
            Descriptor desc = Descriptor.createDescriptor(in);
            this.children.add(desc);
        }
    }

    public List<Descriptor> getChildren() {
        return Collections.unmodifiableList(this.children);
    }

    public int getType() {
        return this.type;
    }

    public int getSize() {
        return this.size;
    }

    static {
        if (!Utils.isDebug) {
            LOGGER.setLevel(Level.WARNING);
        }
    }
}

