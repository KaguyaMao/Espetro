/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.meta;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.MP4InputStream;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.FullBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.Utils;
import java.io.IOException;

public class GenreBox
extends FullBox {
    private String languageCode;
    private String genre;

    public GenreBox() {
        super("Genre Box");
    }

    @Override
    public void decode(MP4InputStream in) throws IOException {
        if (this.parent.getType() == 1969517665L) {
            super.decode(in);
            this.languageCode = Utils.getLanguageCode(in.readBytes(2));
            byte[] b = in.readTerminated((int)this.getLeft(in), 0);
            this.genre = new String(b, "UTF-8");
        } else {
            this.readChildren(in);
        }
    }

    public String getLanguageCode() {
        return this.languageCode;
    }

    public String getGenre() {
        return this.genre;
    }
}

