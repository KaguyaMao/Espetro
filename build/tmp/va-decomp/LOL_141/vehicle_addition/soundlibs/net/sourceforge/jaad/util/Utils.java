/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public interface Utils {
    public static final boolean isDebug = Boolean.parseBoolean(System.getProperty("LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.debug", "false").toLowerCase(Locale.ROOT));

    public static boolean[] copyOf(boolean[] array) {
        return array == null ? null : Arrays.copyOf(array, array.length);
    }

    public static int[] copyOf(int[] array) {
        return array == null ? null : Arrays.copyOf(array, array.length);
    }

    public static float[] copyOf(float[] array) {
        return array == null ? null : Arrays.copyOf(array, array.length);
    }

    public static void copyRange(int[] array, int srcPos, int destPos, int length) {
        System.arraycopy(array, srcPos, array, destPos, length);
    }

    public static int clip(int idx, int min, int max) {
        idx = Math.max(idx, min);
        idx = Math.min(idx, max);
        return idx;
    }

    @SafeVarargs
    public static <E> List<E> listOf(E ... elements) {
        if (elements.length == 0) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(Arrays.asList(elements));
    }

    public static <E> List<E> listCopyOf(Collection<? extends E> coll) {
        if (coll == Collections.emptyList()) {
            return (List)coll;
        }
        return Utils.listOf(coll.toArray());
    }

    public static int readNBytes(InputStream in, byte[] b, int off, int len) throws IOException {
        int n;
        int count;
        if (off < 0 || off + len > b.length) {
            throw new ArrayIndexOutOfBoundsException();
        }
        for (n = 0; n < len && (count = in.read(b, off + n, len - n)) >= 0; n += count) {
        }
        return n;
    }

    public static int readNBytes(InputStream in, byte[] b) throws IOException {
        return Utils.readNBytes(in, b, 0, b.length);
    }
}

