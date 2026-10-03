/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.MP4InputStream;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.FullBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.Utils;
import java.io.IOException;

public class CopyrightBox
extends FullBox {
    private String languageCode;
    private String notice;

    public CopyrightBox() {
        super("Copyright Box");
    }

    @Override
    public void decode(MP4InputStream in) throws IOException {
        if (this.parent.getType() == 1969517665L) {
            super.decode(in);
            this.languageCode = Utils.getLanguageCode(in.readBytes(2));
            this.notice = in.readUTFString((int)this.getLeft(in));
        } else if (this.parent.getType() == 1768715124L) {
            this.readChildren(in);
        }
    }

    public String getLanguageCode() {
        return this.languageCode;
    }

    public String getNotice() {
        return this.notice;
    }
}

