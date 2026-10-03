/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.oma;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.MP4InputStream;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.FullBox;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class OMACommonHeadersBox
extends FullBox {
    private int encryptionMethod;
    private int paddingScheme;
    private long plaintextLength;
    private byte[] contentID;
    private byte[] rightsIssuerURL;
    private Map<String, String> textualHeaders;

    public OMACommonHeadersBox() {
        super("OMA DRM Common Header Box");
    }

    @Override
    public void decode(MP4InputStream in) throws IOException {
        String value;
        String key;
        super.decode(in);
        this.encryptionMethod = in.readByte();
        this.paddingScheme = in.readByte();
        this.plaintextLength = in.readBytes(8);
        int contentIDLength = (int)in.readBytes(2);
        int rightsIssuerURLLength = (int)in.readBytes(2);
        this.contentID = new byte[contentIDLength];
        in.readBytes(this.contentID);
        this.rightsIssuerURL = new byte[rightsIssuerURLLength];
        in.readBytes(this.rightsIssuerURL);
        this.textualHeaders = new HashMap<String, String>();
        for (int textualHeadersLength = (int)in.readBytes(2); textualHeadersLength > 0; textualHeadersLength -= key.length() + value.length() + 2) {
            key = new String(in.readTerminated((int)this.getLeft(in), 58));
            value = new String(in.readTerminated((int)this.getLeft(in), 0));
            this.textualHeaders.put(key, value);
        }
        this.readChildren(in);
    }

    public int getEncryptionMethod() {
        return this.encryptionMethod;
    }

    public int getPaddingScheme() {
        return this.paddingScheme;
    }

    public long getPlaintextLength() {
        return this.plaintextLength;
    }

    public byte[] getContentID() {
        return this.contentID;
    }

    public byte[] getRightsIssuerURL() {
        return this.rightsIssuerURL;
    }

    public Map<String, String> getTextualHeaders() {
        return this.textualHeaders;
    }
}

