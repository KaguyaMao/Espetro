/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.DecoderConfig;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax.BitStream;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax.CCE;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax.CPE;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax.DSE;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax.LFE;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax.PCE;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax.SCE;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.util.Utils;
import java.util.AbstractList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.IntFunction;

public interface Element {
    public static Type readType(BitStream in) {
        return Type.get(in.readBits(3));
    }

    public InstanceTag getElementInstanceTag();

    public void decode(BitStream var1);

    public static <T extends InstanceTag> List<T> createTagList(final int count, final IntFunction<T> newTag) {
        AbstractList tags = new AbstractList<T>(){

            @Override
            public int size() {
                return count;
            }

            @Override
            public T get(int index) {
                return (InstanceTag)newTag.apply(index);
            }
        };
        return Utils.listCopyOf(tags);
    }

    public static Map<Type, IntFunction<InstanceTag>> tagFactory() {
        EnumMap<Type, IntFunction<InstanceTag>> types = new EnumMap<Type, IntFunction<InstanceTag>>(Type.class);
        types.put(Type.PCE, PCE.TAGS::get);
        types.put(Type.SCE, SCE.TAGS::get);
        types.put(Type.CPE, CPE.TAGS::get);
        types.put(Type.LFE, LFE.TAGS::get);
        types.put(Type.CCE, CCE.TAGS::get);
        types.put(Type.DSE, DSE.TAGS::get);
        return types;
    }

    public static enum Type {
        SCE,
        CPE,
        CCE,
        LFE,
        DSE,
        PCE,
        FIL,
        END;

        public static final List<Type> VALUES;

        public static Type get(int i) {
            return VALUES.get(i);
        }

        static {
            VALUES = Utils.listOf(Type.values());
        }
    }

    public static abstract class InstanceTag {
        protected final int id;
        transient String toString;

        protected InstanceTag(int id) {
            this.id = id;
        }

        public int getId() {
            return this.id;
        }

        public abstract Type getType();

        public abstract Element newElement(DecoderConfig var1);

        public int hashCode() {
            return this.getType().ordinal() + 8 * this.id;
        }

        public boolean equals(Object obj) {
            return this.getClass().equals(obj.getClass()) && this.equalsTag((InstanceTag)obj);
        }

        boolean equalsTag(InstanceTag other) {
            return this.id == other.id && Objects.equals(this.getId(), other.getId());
        }

        public String toString() {
            if (this.toString == null) {
                this.toString = String.format("%s:[%d]", this.getType().name(), this.id);
            }
            return this.toString;
        }
    }
}

