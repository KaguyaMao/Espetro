/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.org.jflac.metadata;

import LOL_141.vehicle_addition.soundlibs.org.jflac.io.BitInputStream;
import LOL_141.vehicle_addition.soundlibs.org.jflac.io.BitOutputStream;
import LOL_141.vehicle_addition.soundlibs.org.jflac.metadata.Metadata;
import LOL_141.vehicle_addition.soundlibs.org.jflac.metadata.SeekPoint;
import java.io.IOException;

public class SeekTable
extends Metadata {
    private static final int SEEKPOINT_LENGTH_BYTES = 18;
    protected SeekPoint[] points;

    public SeekTable(BitInputStream is, int length, boolean isLast) throws IOException {
        super(isLast);
        int numPoints = length / 18;
        this.points = new SeekPoint[numPoints];
        for (int i = 0; i < this.points.length; ++i) {
            this.points[i] = new SeekPoint(is);
        }
        if ((length -= length * 18) > 0) {
            is.readByteBlockAlignedNoCRC(null, length);
        }
    }

    public SeekTable(SeekPoint[] points, boolean isLast) {
        super(isLast);
        this.points = points;
    }

    public void write(BitOutputStream os, boolean isLast) throws IOException {
        os.writeRawUInt(isLast, 1);
        os.writeRawUInt(3, 7);
        os.writeRawUInt(this.calcLength(), 24);
        for (int i = 0; i < this.points.length; ++i) {
            this.points[i].write(os);
        }
        os.flushByteAligned();
    }

    public int calcLength() {
        return this.points.length * 18;
    }

    public SeekPoint getSeekPoint(int idx) {
        if (idx < 0 || idx >= this.points.length) {
            return null;
        }
        return this.points[idx];
    }

    public int numberOfPoints() {
        return this.points.length;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("SeekTable: points=" + this.points.length + "\n");
        for (int i = 0; i < this.points.length; ++i) {
            sb.append("\tPoint " + this.points[i].toString() + "\n");
        }
        return sb.toString();
    }
}

