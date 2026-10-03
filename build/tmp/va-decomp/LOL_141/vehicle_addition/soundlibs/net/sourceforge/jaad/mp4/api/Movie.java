/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.api;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.MP4InputStream;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.api.AudioTrack;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.api.Frame;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.api.MetaData;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.api.Protection;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.api.Track;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.api.Type;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.api.Utils;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.api.VideoTrack;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.Box;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.HandlerBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.MovieHeaderBox;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Movie {
    private final MP4InputStream in;
    private final MovieHeaderBox mvhd;
    private final List<Track> tracks;
    private final MetaData metaData;
    private final List<Protection> protections;

    public Movie(Box moov, MP4InputStream in) {
        Box udta;
        this.in = in;
        this.mvhd = (MovieHeaderBox)moov.getChild(1836476516L);
        List<Box> trackBoxes = moov.getChildren(1953653099L);
        this.tracks = new ArrayList<Track>(trackBoxes.size());
        for (int i = 0; i < trackBoxes.size(); ++i) {
            Track track = this.createTrack(trackBoxes.get(i));
            if (track == null) continue;
            this.tracks.add(track);
        }
        this.metaData = new MetaData();
        if (moov.hasChild(1835365473L)) {
            this.metaData.parse(null, moov.getChild(1835365473L));
        } else if (moov.hasChild(1969517665L) && (udta = moov.getChild(1969517665L)).hasChild(1835365473L)) {
            this.metaData.parse(udta, udta.getChild(1835365473L));
        }
        this.protections = new ArrayList<Protection>();
        if (moov.hasChild(1768977007L)) {
            Box ipro = moov.getChild(1768977007L);
            for (Box sinf : ipro.getChildren(1936289382L)) {
                this.protections.add(Protection.parse(sinf));
            }
        }
    }

    private Track createTrack(Box trak) {
        HandlerBox hdlr = (HandlerBox)trak.getChild(1835297121L).getChild(1751411826L);
        return switch ((int)hdlr.getHandlerType()) {
            case 1986618469 -> new VideoTrack(trak, this.in);
            case 1936684398 -> new AudioTrack(trak, this.in);
            default -> null;
        };
    }

    public List<Track> getTracks() {
        return Collections.unmodifiableList(this.tracks);
    }

    public List<Track> getTracks(Type type) {
        ArrayList<Track> l = new ArrayList<Track>();
        for (Track t : this.tracks) {
            if (!t.getType().equals((Object)type)) continue;
            l.add(t);
        }
        return Collections.unmodifiableList(l);
    }

    public List<Track> getTracks(Track.Codec codec) {
        ArrayList<Track> l = new ArrayList<Track>();
        for (Track t : this.tracks) {
            if (!t.getCodec().equals(codec)) continue;
            l.add(t);
        }
        return Collections.unmodifiableList(l);
    }

    public boolean containsMetaData() {
        return this.metaData.containsMetaData();
    }

    public MetaData getMetaData() {
        return this.metaData;
    }

    public List<Protection> getProtections() {
        return Collections.unmodifiableList(this.protections);
    }

    public Date getCreationTime() {
        return Utils.getDate(this.mvhd.getCreationTime());
    }

    public Date getModificationTime() {
        return Utils.getDate(this.mvhd.getModificationTime());
    }

    public double getDuration() {
        return (double)this.mvhd.getDuration() / (double)this.mvhd.getTimeScale();
    }

    public boolean hasMoreFrames() {
        for (Track track : this.tracks) {
            if (!track.hasMoreFrames()) continue;
            return true;
        }
        return false;
    }

    public Frame readNextFrame() throws IOException {
        Track track = null;
        for (Track t : this.tracks) {
            if (!t.hasMoreFrames() || track != null && !(t.getNextTimeStamp() < track.getNextTimeStamp())) continue;
            track = t;
        }
        return track == null ? null : track.readNextFrame();
    }
}

