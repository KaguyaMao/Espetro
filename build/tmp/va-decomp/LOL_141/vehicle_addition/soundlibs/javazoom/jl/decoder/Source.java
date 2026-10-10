/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.javazoom.jl.decoder;

import java.io.IOException;

public interface Source {
    public static final long LENGTH_UNKNOWN = -1L;

    public int read(byte[] var1, int var2, int var3) throws IOException;

    public boolean willReadBlock();

    public boolean isSeekable();

    public long length();

    public long tell();

    public long seek(long var1);
}

