/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.org.tritonus.share;

import LOL_141.vehicle_addition.soundlibs.org.tritonus.share.ArraySet;
import java.util.Collection;
import java.util.Iterator;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class StringHashedSet<E>
extends ArraySet<E> {
    private static final long serialVersionUID = 1L;

    public StringHashedSet() {
    }

    public StringHashedSet(Collection<E> c) {
        super(c);
    }

    @Override
    public boolean add(E elem) {
        if (elem == null) {
            return false;
        }
        return super.add(elem);
    }

    @Override
    public boolean contains(Object elem) {
        if (elem == null) {
            return false;
        }
        String comp = elem.toString();
        Iterator it = this.iterator();
        while (it.hasNext()) {
            if (!comp.equals(it.next().toString())) continue;
            return true;
        }
        return false;
    }

    public E get(Object elem) {
        if (elem == null) {
            return null;
        }
        String comp = elem.toString();
        for (Object thisElem : this) {
            if (!comp.equals(thisElem.toString())) continue;
            return thisElem;
        }
        return null;
    }
}

