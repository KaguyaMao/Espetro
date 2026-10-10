/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.AACException;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.AudioDecoderInfo;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.ChannelConfiguration;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.DecoderConfig;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.EOSException;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.Profile;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.Receiver;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax.BitStream;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax.PCE;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax.SyntacticElements;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.transport.ADIFHeader;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.util.Utils;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFormat;

public class Decoder {
    static final Logger LOGGER = Logger.getLogger("jaad.aac.Decoder");
    private final DecoderConfig config;
    private final SyntacticElements syntacticElements;
    public int frames = 0;
    private ADIFHeader adifHeader;

    public static boolean canDecode(Profile profile) {
        return profile.isDecodingSupported();
    }

    public static Decoder create(byte[] data) {
        return Decoder.create(BitStream.open(data));
    }

    public static Decoder create(BitStream in) {
        DecoderConfig config = new DecoderConfig().decode(in);
        return Decoder.create(config);
    }

    public static Decoder create(AudioDecoderInfo info) {
        DecoderConfig config = DecoderConfig.create(info);
        return Decoder.create(config);
    }

    public static Decoder create(DecoderConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("illegal MP4 decoder specific info");
        }
        return new Decoder(config);
    }

    private Decoder(DecoderConfig config) {
        this.config = config;
        this.syntacticElements = new SyntacticElements(config);
        LOGGER.log(Level.FINE, "profile: {0}", (Object)config.getProfile());
        LOGGER.log(Level.FINE, "sf: {0}", config.getSampleFrequency().getFrequency());
        LOGGER.log(Level.FINE, "channels: {0}", config.getChannelConfiguration().getDescription());
    }

    public DecoderConfig getConfig() {
        return this.config;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void decodeFrame(byte[] frame, Receiver buffer) {
        BitStream in = BitStream.open(frame);
        try {
            LOGGER.log(Level.FINE, () -> String.format("frame %d @%d", this.frames, 8 * frame.length));
            this.decode(in, buffer);
            LOGGER.log(Level.FINEST, () -> String.format("left %d", in.getBitsLeft()));
        }
        catch (EOSException e) {
            LOGGER.log(Level.WARNING, "unexpected end of frame", e);
        }
        finally {
            ++this.frames;
        }
    }

    private void decode(BitStream in, Receiver buffer) {
        if (ADIFHeader.isPresent(in)) {
            this.adifHeader = ADIFHeader.readHeader(in);
            PCE pce = this.adifHeader.getFirstPCE();
            this.config.setAudioDecoderInfo(pce);
        }
        if (!Decoder.canDecode(this.config.getProfile())) {
            throw new AACException("unsupported profile: " + this.config.getProfile().getDescription());
        }
        this.syntacticElements.startNewFrame();
        this.syntacticElements.decode(in);
        List<float[]> channels = this.syntacticElements.process();
        buffer.accept(channels, this.config.getSampleLength(), this.config.getOutputFrequency().getFrequency());
    }

    public AudioFormat getAudioFormat() {
        int freq = this.config.getSampleFrequency().getFrequency();
        if (!this.config.getProfile().isErrorResilientProfile() && this.config.getChannelConfiguration() == ChannelConfiguration.MONO && freq < 24000) {
            freq *= 2;
        }
        return new AudioFormat(freq, 16, this.config.getChannelCount(), true, false);
    }

    static {
        if (!Utils.isDebug) {
            LOGGER.setLevel(Level.INFO);
        }
    }
}

