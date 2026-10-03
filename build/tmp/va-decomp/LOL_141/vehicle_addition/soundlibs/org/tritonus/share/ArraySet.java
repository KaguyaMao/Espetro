/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.org.tritonus.share;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ArraySet<E>
extends ArrayList<E>
implements Set<E> {
    private static final long serialVersionUID = 1L;

    public ArraySet() {
    }

    public ArraySet(Collection<E> c) {
        this();
        this.addAll(c);
    }

    @Override
    public boolean add(E element) {
        if (!this.contains(element)) {
            super.add(element);
            return true;
        }
        return false;
    }

    @Override
    public void add(int index, E element) {
        throw new UnsupportedOperationException("ArraySet.add(int index, Object element) unsupported");
    }

    @Override
    public E set(int index, E element) {
        throw new UnsupportedOperationException("ArraySet.set(int index, Object element) unsupported");
    }
}

