/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.MP4Exception;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.MP4InputStream;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.api.Brand;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.api.Movie;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.Box;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.BoxFactory;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.FileTypeBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.ProgressiveDownloadInformationBox;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MP4Container {
    private final MP4InputStream in;
    private final List<Box> boxes;
    private Brand major;
    private Brand minor;
    private Brand[] compatible;
    private FileTypeBox ftyp;
    private ProgressiveDownloadInformationBox pdin;
    private Box moov;
    private Movie movie;

    public MP4Container(MP4InputStream in) throws IOException {
        this.in = in;
        this.boxes = new ArrayList<Box>();
        this.readContent();
    }

    private void readContent() throws IOException {
        Box box = null;
        boolean moovFound = false;
        while (this.in.hasLeft()) {
            box = BoxFactory.parseBox(null, this.in);
            if (this.boxes.isEmpty() && box.getType() != 1718909296L) {
                throw new MP4Exception("no MP4 signature found");
            }
            this.boxes.add(box);
            long type = box.getType();
            if (type == 1718909296L) {
                if (this.ftyp != null) continue;
                this.ftyp = (FileTypeBox)box;
                continue;
            }
            if (type == 1836019574L) {
                if (this.movie == null) {
                    this.moov = box;
                }
                moovFound = true;
                continue;
            }
            if (type == 1885628782L) {
                if (this.pdin != null) continue;
                this.pdin = (ProgressiveDownloadInformationBox)box;
                continue;
            }
            if (type != 1835295092L) continue;
            if (moovFound) break;
            if (this.in.seekSupported()) continue;
            throw new MP4Exception("movie box at end of file, need seekable");
        }
    }

    public Brand getMajorBrand() {
        if (this.major == null) {
            this.major = Brand.forID(this.ftyp.getMajorBrand());
        }
        return this.major;
    }

    public Brand getMinorBrand() {
        if (this.minor == null) {
            this.minor = Brand.forID(this.ftyp.getMajorBrand());
        }
        return this.minor;
    }

    public Brand[] getCompatibleBrands() {
        if (this.compatible == null) {
            String[] s = this.ftyp.getCompatibleBrands();
            this.compatible = new Brand[s.length];
            for (int i = 0; i < s.length; ++i) {
                this.compatible[i] = Brand.forID(s[i]);
            }
        }
        return this.compatible;
    }

    public Movie getMovie() {
        if (this.moov == null) {
            return null;
        }
        if (this.movie == null) {
            this.movie = new Movie(this.moov, this.in);
        }
        return this.movie;
    }

    public List<Box> getBoxes() {
        return Collections.unmodifiableList(this.boxes);
    }
}

