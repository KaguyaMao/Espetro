/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.api;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.api.Artwork;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.api.DecoderInfo;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.api.ID3Frame;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.api.ID3Tag;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.Box;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.CopyrightBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.meta.ID3TagBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.meta.ITunesMetadataBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.meta.NeroMetadataTagsBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.meta.ThreeGPPAlbumBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.meta.ThreeGPPLocationBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.meta.ThreeGPPMetadataBox;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;

public class MetaData {
    private static final String[] STANDARD_GENRES = new String[]{"undefined", "blues", "classic rock", "country", "dance", "disco", "funk", "grunge", "hip hop", "jazz", "metal", "new age", "oldies", "other", "pop", "r and b", "rap", "reggae", "rock", "techno", "industrial", "alternative", "ska", "death metal", "pranks", "soundtrack", "euro techno", "ambient", "trip hop", "vocal", "jazz funk", "fusion", "trance", "classical", "instrumental", "acid", "house", "game", "sound clip", "gospel", "noise", "alternrock", "bass", "soul", "punk", "space", "meditative", "instrumental pop", "instrumental rock", "ethnic", "gothic", "darkwave", "techno industrial", "electronic", "pop folk", "eurodance", "dream", "southern rock", "comedy", "cult", "gangsta", "top ", "christian rap", "pop funk", "jungle", "native american", "cabaret", "new wave", "psychedelic", "rave", "showtunes", "trailer", "lo fi", "tribal", "acid punk", "acid jazz", "polka", "retro", "musical", "rock and roll", "hard rock", "folk", "folk rock", "national folk", "swing", "fast fusion", "bebob", "latin", "revival", "celtic", "bluegrass", "avantgarde", "gothic rock", "progressive rock", "psychedelic rock", "symphonic rock", "slow rock", "big band", "chorus", "easy listening", "acoustic", "humour", "speech", "chanson", "opera", "chamber music", "sonata", "symphony", "booty bass", "primus", "porn groove", "satire", "slow jam", "club", "tango", "samba", "folklore", "ballad", "power ballad", "rhythmic soul", "freestyle", "duet", "punk rock", "drum solo", "a capella", "euro house", "dance hall"};
    private static final String[] NERO_TAGS = new String[]{"artist", "title", "album", "track", "totaltracks", "year", "genre", "disc", "totaldiscs", "url", "copyright", "comment", "lyrics", "credits", "rating", "label", "composer", "isrc", "mood", "tempo"};
    private Map<Field<?>, Object> contents = new HashMap();

    MetaData() {
    }

    void parse(Box udta, Box meta) {
        if (meta.hasChild(1668313716L)) {
            CopyrightBox cprt = (CopyrightBox)meta.getChild(1668313716L);
            this.put(Field.LANGUAGE, new Locale(cprt.getLanguageCode()));
            this.put(Field.COPYRIGHT, cprt.getNotice());
        }
        if (udta != null) {
            this.parse3GPPData(udta);
        }
        if (meta.hasChild(1768174386L)) {
            this.parseID3((ID3TagBox)meta.getChild(1768174386L));
        }
        if (meta.hasChild(1768715124L)) {
            this.parseITunesMetaData(meta.getChild(1768715124L));
        }
        if (meta.hasChild(1952540531L)) {
            this.parseNeroTags((NeroMetadataTagsBox)meta.getChild(1952540531L));
        }
    }

    private void parse3GPPData(Box udta) {
        if (udta.hasChild(1634493037L)) {
            ThreeGPPAlbumBox albm = (ThreeGPPAlbumBox)udta.getChild(1634493037L);
            this.put(Field.ALBUM, albm.getData());
            this.put(Field.TRACK_NUMBER, albm.getTrackNumber());
        }
        if (udta.hasChild(1685283696L)) {
            this.put(Field.DESCRIPTION, ((ThreeGPPMetadataBox)udta.getChild(1685283696L)).getData());
        }
        if (udta.hasChild(1803122532L)) {
            this.put(Field.KEYWORDS, ((ThreeGPPMetadataBox)udta.getChild(1803122532L)).getData());
        }
        if (udta.hasChild(1819239273L)) {
            this.put(Field.LOCATION, ((ThreeGPPLocationBox)udta.getChild(1819239273L)).getPlaceName());
        }
        if (udta.hasChild(1885696614L)) {
            this.put(Field.ARTIST, ((ThreeGPPMetadataBox)udta.getChild(1885696614L)).getData());
        }
        if (udta.hasChild(2037543523L)) {
            String value = ((ThreeGPPMetadataBox)udta.getChild(2037543523L)).getData();
            try {
                this.put(Field.RELEASE_DATE, new Date(Integer.parseInt(value)));
            }
            catch (NumberFormatException e) {
                DecoderInfo.LOGGER.log(Level.INFO, "unable to parse 3GPP metadata: recording year value: {0}", value);
            }
        }
        if (udta.hasChild(1953068140L)) {
            this.put(Field.TITLE, ((ThreeGPPMetadataBox)udta.getChild(1953068140L)).getData());
        }
    }

    private void parseITunesMetaData(Box ilst) {
        List<Box> boxes = ilst.getChildren();
        for (Box box : boxes) {
            long l = box.getType();
            ITunesMetadataBox data = (ITunesMetadataBox)box.getChild(1684108385L);
            if (l == 2839630420L) {
                this.put(Field.ARTIST, data.getText());
                continue;
            }
            if (l == 2842583405L) {
                this.put(Field.TITLE, data.getText());
                continue;
            }
            if (l == 1631670868L) {
                this.put(Field.ALBUM_ARTIST, data.getText());
                continue;
            }
            if (l == 2841734242L) {
                this.put(Field.ALBUM, data.getText());
                continue;
            }
            if (l == 1953655662L) {
                byte[] b = data.getData();
                this.put(Field.TRACK_NUMBER, Integer.valueOf(b[3]));
                this.put(Field.TOTAL_TRACKS, Integer.valueOf(b[5]));
                continue;
            }
            if (l == 1684632427L) {
                this.put(Field.DISK_NUMBER, data.getInteger());
                continue;
            }
            if (l == 2843177588L) {
                this.put(Field.COMPOSER, data.getText());
                continue;
            }
            if (l == 2841865588L) {
                this.put(Field.COMMENTS, data.getText());
                continue;
            }
            if (l == 1953329263L) {
                this.put(Field.TEMPO, data.getInteger());
                continue;
            }
            if (l == 2841928057L) {
                this.put(Field.RELEASE_DATE, data.getDate());
                continue;
            }
            if (l == 1735291493L || l == 2842125678L) {
                String s = null;
                if (data.getDataType() == ITunesMetadataBox.DataType.UTF8) {
                    s = data.getText();
                } else {
                    int i = data.getInteger();
                    if (i > 0 && i < STANDARD_GENRES.length) {
                        s = STANDARD_GENRES[data.getInteger()];
                    }
                }
                if (s == null) continue;
                this.put(Field.GENRE, s);
                continue;
            }
            if (l == 2841996899L) {
                this.put(Field.ENCODER_NAME, data.getText());
                continue;
            }
            if (l == 2842980207L) {
                this.put(Field.ENCODER_TOOL, data.getText());
                continue;
            }
            if (l == 1668313716L) {
                this.put(Field.COPYRIGHT, data.getText());
                continue;
            }
            if (l == 1668311404L) {
                this.put(Field.COMPILATION, data.getBoolean());
                continue;
            }
            if (l == 1668249202L) {
                Artwork aw = new Artwork(Artwork.Type.forDataType(data.getDataType()), data.getData());
                if (this.contents.containsKey(Field.COVER_ARTWORKS)) {
                    this.get(Field.COVER_ARTWORKS).add(aw);
                    continue;
                }
                ArrayList<Artwork> list = new ArrayList<Artwork>();
                list.add(aw);
                this.put(Field.COVER_ARTWORKS, list);
                continue;
            }
            if (l == 2842129008L) {
                this.put(Field.GROUPING, data.getText());
                continue;
            }
            if (l == 2842458482L) {
                this.put(Field.LYRICS, data.getText());
                continue;
            }
            if (l == 1920233063L) {
                this.put(Field.RATING, data.getInteger());
                continue;
            }
            if (l == 1885565812L) {
                this.put(Field.PODCAST, data.getInteger());
                continue;
            }
            if (l == 1886745196L) {
                this.put(Field.PODCAST_URL, data.getText());
                continue;
            }
            if (l == 1667331175L) {
                this.put(Field.CATEGORY, data.getText());
                continue;
            }
            if (l == 1801812343L) {
                this.put(Field.KEYWORDS, data.getText());
                continue;
            }
            if (l == 1684370275L) {
                this.put(Field.DESCRIPTION, data.getText());
                continue;
            }
            if (l == 1818518899L) {
                this.put(Field.DESCRIPTION, data.getText());
                continue;
            }
            if (l == 1953919848L) {
                this.put(Field.TV_SHOW, data.getText());
                continue;
            }
            if (l == 1953918574L) {
                this.put(Field.TV_NETWORK, data.getText());
                continue;
            }
            if (l == 1953916275L) {
                this.put(Field.TV_EPISODE, data.getText());
                continue;
            }
            if (l == 1953916270L) {
                this.put(Field.TV_EPISODE_NUMBER, data.getInteger());
                continue;
            }
            if (l == 1953919854L) {
                this.put(Field.TV_SEASON, data.getInteger());
                continue;
            }
            if (l == 1886745188L) {
                this.put(Field.PURCHASE_DATE, data.getText());
                continue;
            }
            if (l == 1885823344L) {
                this.put(Field.GAPLESS_PLAYBACK, data.getText());
                continue;
            }
            if (l == 1751414372L) {
                this.put(Field.HD_VIDEO, data.getBoolean());
                continue;
            }
            if (l == 1936679282L) {
                this.put(Field.ARTIST_SORT_TEXT, data.getText());
                continue;
            }
            if (l == 1936682605L) {
                this.put(Field.TITLE_SORT_TEXT, data.getText());
                continue;
            }
            if (l != 1936679276L) continue;
            this.put(Field.ALBUM_SORT_TEXT, data.getText());
        }
    }

    private void parseID3(ID3TagBox box) {
        try {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(box.getID3Data()));
            ID3Tag tag = new ID3Tag(in);
            for (ID3Frame frame : tag.getFrames()) {
                switch (frame.getID()) {
                    case 1414091826: {
                        this.put(Field.TITLE, frame.getEncodedText());
                        break;
                    }
                    case 1413565506: {
                        this.put(Field.ALBUM, frame.getEncodedText());
                        break;
                    }
                    case 1414677323: {
                        int[] num = frame.getNumbers();
                        this.put(Field.TRACK_NUMBER, num[0]);
                        if (num.length <= 1) break;
                        this.put(Field.TOTAL_TRACKS, num[1]);
                        break;
                    }
                    case 1414546737: {
                        this.put(Field.ARTIST, frame.getEncodedText());
                        break;
                    }
                    case 1413697357: {
                        this.put(Field.COMPOSER, frame.getEncodedText());
                        break;
                    }
                    case 1413632077: {
                        this.put(Field.TEMPO, frame.getNumber());
                        break;
                    }
                    case 1414284622: {
                        this.put(Field.LENGTH_IN_MILLISECONDS, frame.getNumber());
                        break;
                    }
                    case 1414283598: {
                        this.put(Field.LANGUAGE, frame.getLocale());
                        break;
                    }
                    case 1413697360: {
                        this.put(Field.COPYRIGHT, frame.getEncodedText());
                        break;
                    }
                    case 1414550850: {
                        this.put(Field.PUBLISHER, frame.getEncodedText());
                        break;
                    }
                    case 1414681422: {
                        this.put(Field.INTERNET_RADIO_STATION, frame.getEncodedText());
                        break;
                    }
                    case 0x5444454E: {
                        this.put(Field.ENCODING_DATE, frame.getDate());
                        break;
                    }
                    case 1413763660: {
                        this.put(Field.RELEASE_DATE, frame.getDate());
                        break;
                    }
                    case 0x54535345: {
                        this.put(Field.ENCODER_TOOL, frame.getEncodedText());
                        break;
                    }
                    case 1414745936: {
                        this.put(Field.ARTIST_SORT_TEXT, frame.getEncodedText());
                        break;
                    }
                    case 1414745940: {
                        this.put(Field.TITLE_SORT_TEXT, frame.getEncodedText());
                        break;
                    }
                    case 1414745921: {
                        this.put(Field.ALBUM_SORT_TEXT, frame.getEncodedText());
                    }
                }
            }
        }
        catch (IOException e) {
            DecoderInfo.LOGGER.log(Level.SEVERE, "Exception in MetaData.parseID3: {0}", e.toString());
        }
    }

    private void parseNeroTags(NeroMetadataTagsBox tags) {
        Map<String, String> pairs = tags.getPairs();
        for (String key : pairs.keySet()) {
            String val = pairs.get(key);
            try {
                if (key.equals(NERO_TAGS[0])) {
                    this.put(Field.ARTIST, val);
                }
                if (key.equals(NERO_TAGS[1])) {
                    this.put(Field.TITLE, val);
                }
                if (key.equals(NERO_TAGS[2])) {
                    this.put(Field.ALBUM, val);
                }
                if (key.equals(NERO_TAGS[3])) {
                    this.put(Field.TRACK_NUMBER, Integer.parseInt(val));
                }
                if (key.equals(NERO_TAGS[4])) {
                    this.put(Field.TOTAL_TRACKS, Integer.parseInt(val));
                }
                if (key.equals(NERO_TAGS[5])) {
                    Calendar c = Calendar.getInstance();
                    c.set(1, Integer.parseInt(val));
                    this.put(Field.RELEASE_DATE, c.getTime());
                }
                if (key.equals(NERO_TAGS[6])) {
                    this.put(Field.GENRE, val);
                }
                if (key.equals(NERO_TAGS[7])) {
                    this.put(Field.DISK_NUMBER, Integer.parseInt(val));
                }
                if (key.equals(NERO_TAGS[8])) {
                    this.put(Field.TOTAL_DISKS, Integer.parseInt(val));
                }
                if (key.equals(NERO_TAGS[9])) {
                    // empty if block
                }
                if (key.equals(NERO_TAGS[10])) {
                    this.put(Field.COPYRIGHT, val);
                }
                if (key.equals(NERO_TAGS[11])) {
                    this.put(Field.COMMENTS, val);
                }
                if (key.equals(NERO_TAGS[12])) {
                    this.put(Field.LYRICS, val);
                }
                if (key.equals(NERO_TAGS[13])) {
                    // empty if block
                }
                if (key.equals(NERO_TAGS[14])) {
                    this.put(Field.RATING, Integer.parseInt(val));
                }
                if (key.equals(NERO_TAGS[15])) {
                    this.put(Field.PUBLISHER, val);
                }
                if (key.equals(NERO_TAGS[16])) {
                    this.put(Field.COMPOSER, val);
                }
                if (key.equals(NERO_TAGS[17])) {
                    // empty if block
                }
                if (key.equals(NERO_TAGS[18])) {
                    // empty if block
                }
                if (!key.equals(NERO_TAGS[19])) continue;
                this.put(Field.TEMPO, Integer.parseInt(val));
            }
            catch (NumberFormatException e) {
                DecoderInfo.LOGGER.log(Level.SEVERE, "Exception in MetaData.parseNeroTags: {0}", e.toString());
            }
        }
    }

    private <T> void put(Field<T> field, T value) {
        this.contents.put(field, value);
    }

    boolean containsMetaData() {
        return !this.contents.isEmpty();
    }

    public <T> T get(Field<T> field) {
        return (T)this.contents.get(field);
    }

    public Map<Field<?>, Object> getAll() {
        return Collections.unmodifiableMap(this.contents);
    }

    public static class Field<T> {
        public static final Field<String> ARTIST = new Field("Artist");
        public static final Field<String> TITLE = new Field("Title");
        public static final Field<String> ALBUM_ARTIST = new Field("Album Artist");
        public static final Field<String> ALBUM = new Field("Album");
        public static final Field<Integer> TRACK_NUMBER = new Field("Track Number");
        public static final Field<Integer> TOTAL_TRACKS = new Field("Total Tracks");
        public static final Field<Integer> DISK_NUMBER = new Field("Disk Number");
        public static final Field<Integer> TOTAL_DISKS = new Field("Total disks");
        public static final Field<String> COMPOSER = new Field("Composer");
        public static final Field<String> COMMENTS = new Field("Comments");
        public static final Field<Integer> TEMPO = new Field("Tempo");
        public static final Field<Integer> LENGTH_IN_MILLISECONDS = new Field("Length in milliseconds");
        public static final Field<Date> RELEASE_DATE = new Field("Release Date");
        public static final Field<String> GENRE = new Field("Genre");
        public static final Field<String> ENCODER_NAME = new Field("Encoder Name");
        public static final Field<String> ENCODER_TOOL = new Field("Encoder Tool");
        public static final Field<Date> ENCODING_DATE = new Field("Encoding Date");
        public static final Field<String> COPYRIGHT = new Field("Copyright");
        public static final Field<String> PUBLISHER = new Field("Publisher");
        public static final Field<Boolean> COMPILATION = new Field("Part of compilation");
        public static final Field<List<Artwork>> COVER_ARTWORKS = new Field("Cover Artworks");
        public static final Field<String> GROUPING = new Field("Grouping");
        public static final Field<String> LOCATION = new Field("Location");
        public static final Field<String> LYRICS = new Field("Lyrics");
        public static final Field<Integer> RATING = new Field("Rating");
        public static final Field<Integer> PODCAST = new Field("Podcast");
        public static final Field<String> PODCAST_URL = new Field("Podcast URL");
        public static final Field<String> CATEGORY = new Field("Category");
        public static final Field<String> KEYWORDS = new Field("Keywords");
        public static final Field<Integer> EPISODE_GLOBAL_UNIQUE_ID = new Field("Episode Global Unique ID");
        public static final Field<String> DESCRIPTION = new Field("Description");
        public static final Field<String> TV_SHOW = new Field("TV Show");
        public static final Field<String> TV_NETWORK = new Field("TV Network");
        public static final Field<String> TV_EPISODE = new Field("TV Episode");
        public static final Field<Integer> TV_EPISODE_NUMBER = new Field("TV Episode Number");
        public static final Field<Integer> TV_SEASON = new Field("TV Season");
        public static final Field<String> INTERNET_RADIO_STATION = new Field("Internet Radio Station");
        public static final Field<String> PURCHASE_DATE = new Field("Purchase Date");
        public static final Field<String> GAPLESS_PLAYBACK = new Field("Gapless Playback");
        public static final Field<Boolean> HD_VIDEO = new Field("HD Video");
        public static final Field<Locale> LANGUAGE = new Field("Language");
        public static final Field<String> ARTIST_SORT_TEXT = new Field("Artist Sort Text");
        public static final Field<String> TITLE_SORT_TEXT = new Field("Title Sort Text");
        public static final Field<String> ALBUM_SORT_TEXT = new Field("Album Sort Text");
        private String name;

        private Field(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public String toString() {
            return this.name;
        }
    }
}

