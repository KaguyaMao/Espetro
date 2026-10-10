/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.MP4InputStream;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.FullBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.Utils;
import java.io.IOException;

public class MovieHeaderBox
extends FullBox {
    private long creationTime;
    private long modificationTime;
    private long timeScale;
    private long duration;
    private double rate;
    private double volume;
    private double[] matrix = new double[9];
    private long nextTrackID;

    public MovieHeaderBox() {
        super("Movie Header Box");
    }

    @Override
    public void decode(MP4InputStream in) throws IOException {
        super.decode(in);
        int len = this.version == 1 ? 8 : 4;
        this.creationTime = in.readBytes(len);
        this.modificationTime = in.readBytes(len);
        this.timeScale = in.readBytes(4);
        this.duration = Utils.detectUndetermined(in.readBytes(len));
        this.rate = in.readFixedPoint(16, 16);
        this.volume = in.readFixedPoint(8, 8);
        in.skipBytes(10L);
        for (int i = 0; i < 9; ++i) {
            this.matrix[i] = i < 6 ? in.readFixedPoint(16, 16) : in.readFixedPoint(2, 30);
        }
        in.skipBytes(24L);
        this.nextTrackID = in.readBytes(4);
    }

    public long getCreationTime() {
        return this.creationTime;
    }

    public long getModificationTime() {
        return this.modificationTime;
    }

    public long getTimeScale() {
        return this.timeScale;
    }

    public long getDuration() {
        return this.duration;
    }

    public double getRate() {
        return this.rate;
    }

    public double getVolume() {
        return this.volume;
    }

    public double[] getTransformationMatrix() {
        return this.matrix;
    }

    public long getNextTrackID() {
        return this.nextTrackID;
    }
}

