/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.org.jflac.metadata;

import LOL_141.vehicle_addition.soundlibs.org.jflac.io.BitInputStream;
import LOL_141.vehicle_addition.soundlibs.org.jflac.metadata.Metadata;
import LOL_141.vehicle_addition.soundlibs.org.jflac.metadata.VorbisString;
import java.io.IOException;
import java.util.Vector;

public class VorbisComment
extends Metadata {
    protected byte[] vendorString = new byte[0];
    protected int numComments = 0;
    protected VorbisString[] comments;

    public VorbisComment(BitInputStream is, int length, boolean isLast) throws IOException {
        super(isLast);
        int len = is.readRawIntLittleEndian();
        this.vendorString = new byte[len];
        is.readByteBlockAlignedNoCRC(this.vendorString, this.vendorString.length);
        this.numComments = is.readRawIntLittleEndian();
        if (this.numComments > 0) {
            this.comments = new VorbisString[this.numComments];
        }
        for (int i = 0; i < this.numComments; ++i) {
            this.comments[i] = new VorbisString(is);
        }
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("VendorString '" + new String(this.vendorString) + "'\n");
        sb.append("VorbisComment (count=" + this.numComments + ")");
        for (int i = 0; i < this.numComments; ++i) {
            sb.append("\n\t" + this.comments[i].toString());
        }
        return sb.toString();
    }

    public String[] getCommentByName(String key) {
        if (this.numComments == 0 || key == null) {
            return null;
        }
        Vector<String> sbuff = new Vector<String>();
        for (int i = 0; i < this.comments.length; ++i) {
            String comment = this.comments[i].toString();
            int eqpos = comment.indexOf(61);
            if (eqpos == -1 || !comment.substring(0, eqpos).equalsIgnoreCase(key)) continue;
            sbuff.add(comment.substring(eqpos + 1, comment.length()));
        }
        return sbuff.toArray(new String[0]);
    }

    public VorbisString getComment(int index) throws IndexOutOfBoundsException {
        if (index < 0 || index > this.numComments - 1) {
            throw new IndexOutOfBoundsException();
        }
        return this.comments[index];
    }

    public int getNumComments() {
        return this.numComments;
    }
}

