/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.util;

import java.io.IOException;
import java.io.InputStream;

public class Mp3Util {
    private Mp3Util() {
    }

    public static void skipID3(InputStream inputStream) throws IOException {
        inputStream.mark(10);
        byte[] header = new byte[10];
        int read = inputStream.read(header, 0, 10);
        if (read < 10) {
            inputStream.reset();
            return;
        }
        if (header[0] == 73 && header[1] == 68 && header[2] == 51) {
            int skip;
            int size = header[6] << 21 | header[7] << 14 | header[8] << 7 | header[9];
            int skipped = 0;
            do {
                if ((skip = (int)inputStream.skip(size - skipped)) == 0) continue;
                skipped += skip;
            } while (skipped < size && skip != 0);
        } else {
            inputStream.reset();
        }
    }
}

