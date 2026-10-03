/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.MP4InputStream;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.Box;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.BoxImpl;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.BoxTypes;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.FullBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.UnknownBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.AppleLosslessBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.BinaryXMLBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.BitRateBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.ChapterBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.ChunkOffsetBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.CleanApertureBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.CompositionTimeToSampleBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.CopyrightBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.DataEntryUrlBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.DataEntryUrnBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.DataReferenceBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.DecodingTimeToSampleBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.DegradationPriorityBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.ESDBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.EditListBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.FileTypeBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.FreeSpaceBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.HandlerBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.HintMediaHeaderBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.IPMPControlBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.IPMPInfoBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.ItemInformationBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.ItemInformationEntry;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.ItemLocationBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.ItemProtectionBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.MediaDataBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.MediaHeaderBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.MetaBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.MetaBoxRelationBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.MovieExtendsHeaderBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.MovieFragmentHeaderBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.MovieFragmentRandomAccessOffsetBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.MovieHeaderBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.ObjectDescriptorBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.OriginalFormatBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.PaddingBitBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.PixelAspectRatioBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.PrimaryItemBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.ProgressiveDownloadInformationBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.SampleDependencyBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.SampleDependencyTypeBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.SampleDescriptionBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.SampleGroupDescriptionBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.SampleScaleBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.SampleSizeBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.SampleToChunkBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.SampleToGroupBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.SchemeTypeBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.ShadowSyncSampleBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.SoundMediaHeaderBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.SubSampleInformationBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.SyncSampleBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.TrackExtendsBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.TrackFragmentHeaderBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.TrackFragmentRandomAccessBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.TrackFragmentRunBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.TrackHeaderBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.TrackReferenceBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.TrackSelectionBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.VideoMediaHeaderBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.XMLBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.drm.FairPlayDataBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.fd.FDItemInformationBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.fd.FDSessionGroupBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.fd.FECReservoirBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.fd.FilePartitionBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.fd.GroupIDToNameBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.meta.EncoderBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.meta.GenreBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.meta.ID3TagBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.meta.ITunesMetadataBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.meta.ITunesMetadataMeanBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.meta.ITunesMetadataNameBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.meta.NeroMetadataTagsBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.meta.RatingBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.meta.RequirementBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.meta.ThreeGPPAlbumBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.meta.ThreeGPPKeywordsBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.meta.ThreeGPPLocationBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.meta.ThreeGPPMetadataBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.meta.ThreeGPPRecordingYearBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.oma.OMAAccessUnitFormatBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.oma.OMACommonHeadersBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.oma.OMAContentIDBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.oma.OMAContentObjectBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.oma.OMADiscreteMediaHeadersBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.oma.OMARightsObjectBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.oma.OMATransactionTrackingBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.oma.OMAURLBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.sampleentries.AudioSampleEntry;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.sampleentries.FDHintSampleEntry;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.sampleentries.MPEGSampleEntry;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.sampleentries.RTPHintSampleEntry;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.sampleentries.TextMetadataSampleEntry;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.sampleentries.VideoSampleEntry;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.sampleentries.XMLMetadataSampleEntry;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.sampleentries.codec.AC3SpecificBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.sampleentries.codec.AMRSpecificBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.sampleentries.codec.AVCSpecificBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.sampleentries.codec.EAC3SpecificBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.sampleentries.codec.EVRCSpecificBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.sampleentries.codec.H263SpecificBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.sampleentries.codec.QCELPSpecificBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.sampleentries.codec.SMVSpecificBox;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class BoxFactory
implements BoxTypes {
    private static final Map<Long, Class<? extends BoxImpl>> BOX_CLASSES = new HashMap<Long, Class<? extends BoxImpl>>();
    private static final Map<Long, Class<? extends BoxImpl>[]> BOX_MULTIPLE_CLASSES = new HashMap<Long, Class<? extends BoxImpl>[]>();
    private static final Map<Long, String[]> PARAMETER = new HashMap<Long, String[]>();

    public static Box parseBox(Box parent, MP4InputStream in) throws IOException {
        long offset = in.getOffset();
        long size = in.readBytes(4);
        long type = in.readBytes(4);
        return BoxFactory.parseBox(parent, offset, size, type, in);
    }

    public static Box parseBox(Box parent, long offset, long size, long type, MP4InputStream in) throws IOException {
        long left;
        long parentLeft;
        if (size == 1L) {
            size = in.readBytes(8);
        }
        if (type == 1970628964L) {
            in.skipBytes(16L);
        }
        if (parent != null && size > (parentLeft = parent.getOffset() + parent.getSize() - offset)) {
            throw new IOException("error while decoding box '" + BoxFactory.typeToString(type) + "' at offset " + offset + ": box too large for parent");
        }
        LOGGER.finest(() -> BoxFactory.typeToString(type));
        BoxImpl box = BoxFactory.forType(type, in.getOffset());
        box.setParams(parent, size, type, offset);
        box.decode(in);
        Class<?> cl = box.getClass();
        if (cl == BoxImpl.class || cl == FullBox.class) {
            box.readChildren(in);
        }
        if (!((left = box.getOffset() + box.getSize() - in.getOffset()) <= 0L || box instanceof MediaDataBox || box instanceof UnknownBox || box instanceof FreeSpaceBox)) {
            LOGGER.log(Level.INFO, "bytes left after reading box {0}: left: {1}, offset: {2}", new Object[]{BoxFactory.typeToString(type), left, in.getOffset()});
        } else if (left < 0L) {
            LOGGER.log(Level.SEVERE, "box {0} overread: {1} bytes, offset: {2}", new Object[]{BoxFactory.typeToString(type), -left, in.getOffset()});
        }
        if (box.getType() != 1835295092L || in.seekSupported()) {
            in.skipBytes(left);
        }
        return box;
    }

    public static Box parseBox(MP4InputStream in, Class<? extends BoxImpl> boxClass) throws IOException {
        long offset = in.getOffset();
        long size = in.readBytes(4);
        long type = in.readBytes(4);
        if (size == 1L) {
            size = in.readBytes(8);
        }
        if (type == 1970628964L) {
            in.skipBytes(16L);
        }
        BoxImpl box = null;
        try {
            box = boxClass.newInstance();
        }
        catch (InstantiationException instantiationException) {
        }
        catch (IllegalAccessException illegalAccessException) {
            // empty catch block
        }
        if (box != null) {
            box.setParams(null, size, type, offset);
            box.decode(in);
            long left = box.getOffset() + box.getSize() - in.getOffset();
            in.skipBytes(left);
        }
        return box;
    }

    private static BoxImpl forType(long type, long offset) {
        BoxImpl box = null;
        Long l = type;
        if (BOX_CLASSES.containsKey(l)) {
            Class<? extends BoxImpl> cl = BOX_CLASSES.get(l);
            if (PARAMETER.containsKey(l)) {
                String[] s = PARAMETER.get(l);
                try {
                    Constructor<? extends BoxImpl> con = cl.getConstructor(String.class);
                    box = con.newInstance(s[0]);
                }
                catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "BoxFactory: could not call constructor for " + BoxFactory.typeToString(type), e);
                    box = new UnknownBox();
                }
            } else {
                try {
                    box = cl.newInstance();
                }
                catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "BoxFactory: could not instantiate box " + BoxFactory.typeToString(type), e);
                }
            }
        }
        if (box == null) {
            LOGGER.log(Level.INFO, "BoxFactory: unknown box type: {0}; position: {1}", new Object[]{BoxFactory.typeToString(type), offset});
            box = new UnknownBox();
        }
        return box;
    }

    public static String typeToString(long l) {
        byte[] b = new byte[]{(byte)(l >> 24 & 0xFFL), (byte)(l >> 16 & 0xFFL), (byte)(l >> 8 & 0xFFL), (byte)(l & 0xFFL)};
        return new String(b);
    }

    static {
        BOX_CLASSES.put(1835361135L, BoxImpl.class);
        BOX_CLASSES.put(1634492771L, AppleLosslessBox.class);
        BOX_CLASSES.put(1652059500L, BinaryXMLBox.class);
        BOX_CLASSES.put(1651798644L, BitRateBox.class);
        BOX_CLASSES.put(1667788908L, ChapterBox.class);
        BOX_CLASSES.put(1937007471L, ChunkOffsetBox.class);
        BOX_CLASSES.put(1668232756L, ChunkOffsetBox.class);
        BOX_CLASSES.put(1668047216L, CleanApertureBox.class);
        BOX_CLASSES.put(1937013298L, SampleSizeBox.class);
        BOX_CLASSES.put(1668576371L, CompositionTimeToSampleBox.class);
        BOX_CLASSES.put(1668313716L, CopyrightBox.class);
        BOX_CLASSES.put(1970433568L, DataEntryUrnBox.class);
        BOX_CLASSES.put(1970433056L, DataEntryUrlBox.class);
        BOX_CLASSES.put(1684631142L, BoxImpl.class);
        BOX_CLASSES.put(1685218662L, DataReferenceBox.class);
        BOX_CLASSES.put(0x73747473L, DecodingTimeToSampleBox.class);
        BOX_CLASSES.put(1937007728L, DegradationPriorityBox.class);
        BOX_CLASSES.put(1701082227L, BoxImpl.class);
        BOX_CLASSES.put(1701606260L, EditListBox.class);
        BOX_CLASSES.put(0x6669696EL, FDItemInformationBox.class);
        BOX_CLASSES.put(1936025458L, FDSessionGroupBox.class);
        BOX_CLASSES.put(1717920626L, FECReservoirBox.class);
        BOX_CLASSES.put(1718641010L, FilePartitionBox.class);
        BOX_CLASSES.put(1718909296L, FileTypeBox.class);
        BOX_CLASSES.put(1718773093L, FreeSpaceBox.class);
        BOX_CLASSES.put(1734964334L, GroupIDToNameBox.class);
        BOX_CLASSES.put(1751411826L, HandlerBox.class);
        BOX_CLASSES.put(1752000612L, HintMediaHeaderBox.class);
        BOX_CLASSES.put(1768975715L, IPMPControlBox.class);
        BOX_CLASSES.put(0x696D6966L, IPMPInfoBox.class);
        BOX_CLASSES.put(0x69696E66L, ItemInformationBox.class);
        BOX_CLASSES.put(1768842853L, ItemInformationEntry.class);
        BOX_CLASSES.put(1768714083L, ItemLocationBox.class);
        BOX_CLASSES.put(1768977007L, ItemProtectionBox.class);
        BOX_CLASSES.put(1835297121L, BoxImpl.class);
        BOX_CLASSES.put(1835295092L, MediaDataBox.class);
        BOX_CLASSES.put(1835296868L, MediaHeaderBox.class);
        BOX_CLASSES.put(1835626086L, BoxImpl.class);
        BOX_CLASSES.put(1835365473L, MetaBox.class);
        BOX_CLASSES.put(1835364965L, MetaBoxRelationBox.class);
        BOX_CLASSES.put(1836019574L, BoxImpl.class);
        BOX_CLASSES.put(1836475768L, BoxImpl.class);
        BOX_CLASSES.put(1835362404L, MovieExtendsHeaderBox.class);
        BOX_CLASSES.put(0x6D6F6F66L, BoxImpl.class);
        BOX_CLASSES.put(1835427940L, MovieFragmentHeaderBox.class);
        BOX_CLASSES.put(1835430497L, BoxImpl.class);
        BOX_CLASSES.put(1835430511L, MovieFragmentRandomAccessOffsetBox.class);
        BOX_CLASSES.put(1836476516L, MovieHeaderBox.class);
        BOX_CLASSES.put(1952540531L, NeroMetadataTagsBox.class);
        BOX_CLASSES.put(1852663908L, FullBox.class);
        BOX_CLASSES.put(1718775137L, OriginalFormatBox.class);
        BOX_CLASSES.put(1885430882L, PaddingBitBox.class);
        BOX_CLASSES.put(1885431150L, BoxImpl.class);
        BOX_CLASSES.put(1885434736L, PixelAspectRatioBox.class);
        BOX_CLASSES.put(1885959277L, PrimaryItemBox.class);
        BOX_CLASSES.put(1885628782L, ProgressiveDownloadInformationBox.class);
        BOX_CLASSES.put(1936289382L, BoxImpl.class);
        BOX_CLASSES.put(1935963248L, SampleDependencyTypeBox.class);
        BOX_CLASSES.put(1937011556L, SampleDescriptionBox.class);
        BOX_CLASSES.put(1936158820L, SampleGroupDescriptionBox.class);
        BOX_CLASSES.put(1937011564L, SampleScaleBox.class);
        BOX_CLASSES.put(1937011578L, SampleSizeBox.class);
        BOX_CLASSES.put(1937007212L, BoxImpl.class);
        BOX_CLASSES.put(1937011555L, SampleToChunkBox.class);
        BOX_CLASSES.put(1935828848L, SampleToGroupBox.class);
        BOX_CLASSES.put(1935894637L, SchemeTypeBox.class);
        BOX_CLASSES.put(1935894633L, BoxImpl.class);
        BOX_CLASSES.put(1937011560L, ShadowSyncSampleBox.class);
        BOX_CLASSES.put(1936419184L, FreeSpaceBox.class);
        BOX_CLASSES.put(1936549988L, SoundMediaHeaderBox.class);
        BOX_CLASSES.put(1937072755L, SubSampleInformationBox.class);
        BOX_CLASSES.put(0x73747373L, SyncSampleBox.class);
        BOX_CLASSES.put(1953653099L, BoxImpl.class);
        BOX_CLASSES.put(1953654136L, TrackExtendsBox.class);
        BOX_CLASSES.put(1953653094L, BoxImpl.class);
        BOX_CLASSES.put(1952868452L, TrackFragmentHeaderBox.class);
        BOX_CLASSES.put(1952871009L, TrackFragmentRandomAccessBox.class);
        BOX_CLASSES.put(1953658222L, TrackFragmentRunBox.class);
        BOX_CLASSES.put(1953196132L, TrackHeaderBox.class);
        BOX_CLASSES.put(1953654118L, TrackReferenceBox.class);
        BOX_CLASSES.put(1953719660L, TrackSelectionBox.class);
        BOX_CLASSES.put(1969517665L, BoxImpl.class);
        BOX_CLASSES.put(1986881636L, VideoMediaHeaderBox.class);
        BOX_CLASSES.put(2003395685L, FreeSpaceBox.class);
        BOX_CLASSES.put(2020437024L, XMLBox.class);
        BOX_CLASSES.put(1768907891L, ObjectDescriptorBox.class);
        BOX_CLASSES.put(1935959408L, SampleDependencyBox.class);
        BOX_CLASSES.put(1768174386L, ID3TagBox.class);
        BOX_CLASSES.put(1768715124L, BoxImpl.class);
        BOX_CLASSES.put(0x2D2D2D2DL, BoxImpl.class);
        BOX_CLASSES.put(1684108385L, ITunesMetadataBox.class);
        BOX_CLASSES.put(1851878757L, ITunesMetadataNameBox.class);
        BOX_CLASSES.put(1835360622L, ITunesMetadataMeanBox.class);
        BOX_CLASSES.put(1631670868L, BoxImpl.class);
        BOX_CLASSES.put(1936679265L, BoxImpl.class);
        BOX_CLASSES.put(2841734242L, BoxImpl.class);
        BOX_CLASSES.put(1936679276L, BoxImpl.class);
        BOX_CLASSES.put(2839630420L, BoxImpl.class);
        BOX_CLASSES.put(1936679282L, BoxImpl.class);
        BOX_CLASSES.put(1667331175L, BoxImpl.class);
        BOX_CLASSES.put(2841865588L, BoxImpl.class);
        BOX_CLASSES.put(1668311404L, BoxImpl.class);
        BOX_CLASSES.put(2843177588L, BoxImpl.class);
        BOX_CLASSES.put(1936679791L, BoxImpl.class);
        BOX_CLASSES.put(1668249202L, BoxImpl.class);
        BOX_CLASSES.put(2842125678L, BoxImpl.class);
        BOX_CLASSES.put(1684370275L, BoxImpl.class);
        BOX_CLASSES.put(1684632427L, BoxImpl.class);
        BOX_CLASSES.put(2841996899L, EncoderBox.class);
        BOX_CLASSES.put(2842980207L, EncoderBox.class);
        BOX_CLASSES.put(1701276004L, BoxImpl.class);
        BOX_CLASSES.put(1885823344L, BoxImpl.class);
        BOX_CLASSES.put(1735291493L, GenreBox.class);
        BOX_CLASSES.put(2842129008L, BoxImpl.class);
        BOX_CLASSES.put(1751414372L, BoxImpl.class);
        BOX_CLASSES.put(1634748740L, BoxImpl.class);
        BOX_CLASSES.put(1634421060L, BoxImpl.class);
        BOX_CLASSES.put(1668172100L, BoxImpl.class);
        BOX_CLASSES.put(1936083268L, BoxImpl.class);
        BOX_CLASSES.put(1801812343L, BoxImpl.class);
        BOX_CLASSES.put(1818518899L, BoxImpl.class);
        BOX_CLASSES.put(2842458482L, BoxImpl.class);
        BOX_CLASSES.put(1937009003L, BoxImpl.class);
        BOX_CLASSES.put(1885565812L, BoxImpl.class);
        BOX_CLASSES.put(1886745196L, BoxImpl.class);
        BOX_CLASSES.put(1886745188L, BoxImpl.class);
        BOX_CLASSES.put(1920233063L, RatingBox.class);
        BOX_CLASSES.put(2841928057L, BoxImpl.class);
        BOX_CLASSES.put(2842846577L, RequirementBox.class);
        BOX_CLASSES.put(1953329263L, BoxImpl.class);
        BOX_CLASSES.put(2842583405L, BoxImpl.class);
        BOX_CLASSES.put(1953655662L, BoxImpl.class);
        BOX_CLASSES.put(1936682605L, BoxImpl.class);
        BOX_CLASSES.put(1953916275L, BoxImpl.class);
        BOX_CLASSES.put(1953916270L, BoxImpl.class);
        BOX_CLASSES.put(1953918574L, BoxImpl.class);
        BOX_CLASSES.put(1953919854L, BoxImpl.class);
        BOX_CLASSES.put(1953919848L, BoxImpl.class);
        BOX_CLASSES.put(1936683886L, BoxImpl.class);
        BOX_CLASSES.put(1634493037L, ThreeGPPAlbumBox.class);
        BOX_CLASSES.put(1635087464L, ThreeGPPMetadataBox.class);
        BOX_CLASSES.put(1668051814L, ThreeGPPMetadataBox.class);
        BOX_CLASSES.put(1685283696L, ThreeGPPMetadataBox.class);
        BOX_CLASSES.put(1803122532L, ThreeGPPKeywordsBox.class);
        BOX_CLASSES.put(1819239273L, ThreeGPPLocationBox.class);
        BOX_CLASSES.put(1885696614L, ThreeGPPMetadataBox.class);
        BOX_CLASSES.put(2037543523L, ThreeGPPRecordingYearBox.class);
        BOX_CLASSES.put(1953068140L, ThreeGPPMetadataBox.class);
        BOX_CLASSES.put(1735616616L, BoxImpl.class);
        BOX_CLASSES.put(1735618669L, BoxImpl.class);
        BOX_CLASSES.put(1735618677L, BoxImpl.class);
        BOX_CLASSES.put(1735619428L, BoxImpl.class);
        BOX_CLASSES.put(1735619444L, BoxImpl.class);
        BOX_CLASSES.put(1735619684L, BoxImpl.class);
        BOX_CLASSES.put(1836070006L, VideoSampleEntry.class);
        BOX_CLASSES.put(1932670515L, VideoSampleEntry.class);
        BOX_CLASSES.put(1701733238L, VideoSampleEntry.class);
        BOX_CLASSES.put(1635148593L, VideoSampleEntry.class);
        BOX_CLASSES.put(1836069985L, AudioSampleEntry.class);
        BOX_CLASSES.put(1633889587L, AudioSampleEntry.class);
        BOX_CLASSES.put(1700998451L, AudioSampleEntry.class);
        BOX_CLASSES.put(1685220723L, AudioSampleEntry.class);
        BOX_CLASSES.put(1935764850L, AudioSampleEntry.class);
        BOX_CLASSES.put(1935767394L, AudioSampleEntry.class);
        BOX_CLASSES.put(1936029283L, AudioSampleEntry.class);
        BOX_CLASSES.put(1936810864L, AudioSampleEntry.class);
        BOX_CLASSES.put(1936944502L, AudioSampleEntry.class);
        BOX_CLASSES.put(1701733217L, AudioSampleEntry.class);
        BOX_CLASSES.put(1836070003L, MPEGSampleEntry.class);
        BOX_CLASSES.put(1835365492L, TextMetadataSampleEntry.class);
        BOX_CLASSES.put(1835365496L, XMLMetadataSampleEntry.class);
        BOX_CLASSES.put(1920233504L, RTPHintSampleEntry.class);
        BOX_CLASSES.put(1717858336L, FDHintSampleEntry.class);
        BOX_CLASSES.put(1702061171L, ESDBox.class);
        BOX_CLASSES.put(1681012275L, H263SpecificBox.class);
        BOX_CLASSES.put(1635148611L, AVCSpecificBox.class);
        BOX_CLASSES.put(1684103987L, AC3SpecificBox.class);
        BOX_CLASSES.put(1684366131L, EAC3SpecificBox.class);
        BOX_CLASSES.put(1684106610L, AMRSpecificBox.class);
        BOX_CLASSES.put(1684371043L, EVRCSpecificBox.class);
        BOX_CLASSES.put(1685152624L, QCELPSpecificBox.class);
        BOX_CLASSES.put(1685286262L, SMVSpecificBox.class);
        BOX_CLASSES.put(1868849510L, OMAAccessUnitFormatBox.class);
        BOX_CLASSES.put(1869112434L, OMACommonHeadersBox.class);
        BOX_CLASSES.put(1667459428L, OMAContentIDBox.class);
        BOX_CLASSES.put(1868850273L, OMAContentObjectBox.class);
        BOX_CLASSES.put(1668706933L, OMAURLBox.class);
        BOX_CLASSES.put(1868851301L, OMADiscreteMediaHeadersBox.class);
        BOX_CLASSES.put(1868853869L, FullBox.class);
        BOX_CLASSES.put(1768124021L, OMAURLBox.class);
        BOX_CLASSES.put(1768842869L, OMAURLBox.class);
        BOX_CLASSES.put(1819435893L, OMAURLBox.class);
        BOX_CLASSES.put(1835299433L, BoxImpl.class);
        BOX_CLASSES.put(1868852077L, FullBox.class);
        BOX_CLASSES.put(1868853858L, OMARightsObjectBox.class);
        BOX_CLASSES.put(1868854388L, OMATransactionTrackingBox.class);
        BOX_CLASSES.put(1970496882L, FairPlayDataBox.class);
        BOX_CLASSES.put(1851878757L, FairPlayDataBox.class);
        BOX_CLASSES.put(1801812256L, FairPlayDataBox.class);
        BOX_CLASSES.put(0x69766976L, FairPlayDataBox.class);
        BOX_CLASSES.put(1886546294L, FairPlayDataBox.class);
        PARAMETER.put(1835361135L, new String[]{"Additional Metadata Container Box"});
        PARAMETER.put(1684631142L, new String[]{"Data Information Box"});
        PARAMETER.put(1701082227L, new String[]{"Edit Box"});
        PARAMETER.put(1835297121L, new String[]{"Media Box"});
        PARAMETER.put(1835626086L, new String[]{"Media Information Box"});
        PARAMETER.put(1836019574L, new String[]{"Movie Box"});
        PARAMETER.put(1836475768L, new String[]{"Movie Extends Box"});
        PARAMETER.put(0x6D6F6F66L, new String[]{"Movie Fragment Box"});
        PARAMETER.put(1835430497L, new String[]{"Movie Fragment Random Access Box"});
        PARAMETER.put(1852663908L, new String[]{"Null Media Header Box"});
        PARAMETER.put(1885431150L, new String[]{"Partition Entry"});
        PARAMETER.put(1936289382L, new String[]{"Protection Scheme Information Box"});
        PARAMETER.put(1937007212L, new String[]{"Sample Table Box"});
        PARAMETER.put(1935894633L, new String[]{"Scheme Information Box"});
        PARAMETER.put(1953653099L, new String[]{"Track Box"});
        PARAMETER.put(1953653094L, new String[]{"Track Fragment Box"});
        PARAMETER.put(1969517665L, new String[]{"User Data Box"});
        PARAMETER.put(1768715124L, new String[]{"iTunes Meta List Box"});
        PARAMETER.put(0x2D2D2D2DL, new String[]{"Custom iTunes Metadata Box"});
        PARAMETER.put(1631670868L, new String[]{"Album Artist Name Box"});
        PARAMETER.put(1936679265L, new String[]{"Album Artist Sort Box"});
        PARAMETER.put(2841734242L, new String[]{"Album Name Box"});
        PARAMETER.put(1936679276L, new String[]{"Album Sort Box"});
        PARAMETER.put(2839630420L, new String[]{"Artist Name Box"});
        PARAMETER.put(1936679282L, new String[]{"Artist Sort Box"});
        PARAMETER.put(1667331175L, new String[]{"Category Box"});
        PARAMETER.put(2841865588L, new String[]{"Comments Box"});
        PARAMETER.put(1668311404L, new String[]{"Compilation Part Box"});
        PARAMETER.put(2843177588L, new String[]{"Composer Name Box"});
        PARAMETER.put(1936679791L, new String[]{"Composer Sort Box"});
        PARAMETER.put(1668249202L, new String[]{"Cover Box"});
        PARAMETER.put(2842125678L, new String[]{"Custom Genre Box"});
        PARAMETER.put(1684370275L, new String[]{"Description Cover Box"});
        PARAMETER.put(1684632427L, new String[]{"Disk Number Box"});
        PARAMETER.put(1701276004L, new String[]{"Episode Global Unique ID Box"});
        PARAMETER.put(1885823344L, new String[]{"Gapless Playback Box"});
        PARAMETER.put(2842129008L, new String[]{"Grouping Box"});
        PARAMETER.put(1751414372L, new String[]{"HD Video Box"});
        PARAMETER.put(1634748740L, new String[]{"iTunes Purchase Account Box"});
        PARAMETER.put(1634421060L, new String[]{"iTunes Account Type Box"});
        PARAMETER.put(1668172100L, new String[]{"iTunes Catalogue ID Box"});
        PARAMETER.put(1936083268L, new String[]{"iTunes Country Code Box"});
        PARAMETER.put(1801812343L, new String[]{"Keyword Box"});
        PARAMETER.put(1818518899L, new String[]{"Long Description Box"});
        PARAMETER.put(2842458482L, new String[]{"Lyrics Box"});
        PARAMETER.put(1937009003L, new String[]{"Meta Type Box"});
        PARAMETER.put(1885565812L, new String[]{"Podcast Box"});
        PARAMETER.put(1886745196L, new String[]{"Podcast URL Box"});
        PARAMETER.put(1886745188L, new String[]{"Purchase Date Box"});
        PARAMETER.put(2841928057L, new String[]{"Release Date Box"});
        PARAMETER.put(1953329263L, new String[]{"Tempo Box"});
        PARAMETER.put(2842583405L, new String[]{"Track Name Box"});
        PARAMETER.put(1953655662L, new String[]{"Track Number Box"});
        PARAMETER.put(1936682605L, new String[]{"Track Sort Box"});
        PARAMETER.put(1953916275L, new String[]{"TV Episode Box"});
        PARAMETER.put(1953916270L, new String[]{"TV Episode Number Box"});
        PARAMETER.put(1953918574L, new String[]{"TV Network Name Box"});
        PARAMETER.put(1953919854L, new String[]{"TV Season Box"});
        PARAMETER.put(1953919848L, new String[]{"TV Show Box"});
        PARAMETER.put(1936683886L, new String[]{"TV Show Sort Box"});
        PARAMETER.put(1635087464L, new String[]{"3GPP Author Box"});
        PARAMETER.put(1668051814L, new String[]{"3GPP Classification Box"});
        PARAMETER.put(1685283696L, new String[]{"3GPP Description Box"});
        PARAMETER.put(1885696614L, new String[]{"3GPP Performer Box"});
        PARAMETER.put(1953068140L, new String[]{"3GPP Title Box"});
        PARAMETER.put(1735616616L, new String[]{"Google Host Header Box"});
        PARAMETER.put(1735618669L, new String[]{"Google Ping Message Box"});
        PARAMETER.put(1735618677L, new String[]{"Google Ping URL Box"});
        PARAMETER.put(1735619428L, new String[]{"Google Source Data Box"});
        PARAMETER.put(1735619444L, new String[]{"Google Start Time Box"});
        PARAMETER.put(1735619684L, new String[]{"Google Track Duration Box"});
        PARAMETER.put(1836070006L, new String[]{"MPEG-4 Video Sample Entry"});
        PARAMETER.put(1932670515L, new String[]{"H263 Video Sample Entry"});
        PARAMETER.put(1701733238L, new String[]{"Encrypted Video Sample Entry"});
        PARAMETER.put(1635148593L, new String[]{"AVC Video Sample Entry"});
        PARAMETER.put(1836069985L, new String[]{"MPEG- 4Audio Sample Entry"});
        PARAMETER.put(1633889587L, new String[]{"AC-3 Audio Sample Entry"});
        PARAMETER.put(1700998451L, new String[]{"Extended AC-3 Audio Sample Entry"});
        PARAMETER.put(1685220723L, new String[]{"DRMS Audio Sample Entry"});
        PARAMETER.put(1935764850L, new String[]{"AMR Audio Sample Entry"});
        PARAMETER.put(1935767394L, new String[]{"AMR-Wideband Audio Sample Entry"});
        PARAMETER.put(1936029283L, new String[]{"EVC Audio Sample Entry"});
        PARAMETER.put(1936810864L, new String[]{"QCELP Audio Sample Entry"});
        PARAMETER.put(1936944502L, new String[]{"SMV Audio Sample Entry"});
        PARAMETER.put(1701733217L, new String[]{"Encrypted Audio Sample Entry"});
        PARAMETER.put(1668706933L, new String[]{"OMA DRM Cover URI Box"});
        PARAMETER.put(1868853869L, new String[]{"OMA DRM Container Box"});
        PARAMETER.put(1768124021L, new String[]{"OMA DRM Icon URI Box"});
        PARAMETER.put(1768842869L, new String[]{"OMA DRM Info URL Box"});
        PARAMETER.put(1819435893L, new String[]{"OMA DRM Lyrics URI Box"});
        PARAMETER.put(1835299433L, new String[]{"OMA DRM Mutable DRM Information Box"});
    }
}

