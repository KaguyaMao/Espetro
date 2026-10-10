/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.AACException;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.AudioDecoderInfo;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.ChannelConfiguration;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.Profile;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.SampleFrequency;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.SampleRate;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.ps.PSImpl;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.sbr.PS;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.sbr.SBR;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax.BitStream;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax.PCE;

public class DecoderConfig {
    private Profile profile = Profile.AAC_MAIN;
    private Profile extProfile = Profile.UNKNOWN;
    private SampleRate sampleFrequency = SampleFrequency.SF_NONE;
    private ChannelConfiguration channelConfiguration = ChannelConfiguration.CHANNEL_CONFIG_UNSUPPORTED;
    private ChannelConfiguration extChannelConfiguration = ChannelConfiguration.CHANNEL_CONFIG_UNSUPPORTED;
    private boolean frameLengthFlag = false;
    private boolean dependsOnCoreCoder = false;
    private int coreCoderDelay = 0;
    private boolean extensionFlag = false;
    private final boolean sbrEnabled;
    private boolean sbrPresent = false;
    private SampleRate outputFrequency;
    private boolean psEnabled = true;
    private boolean psPresent = false;
    private boolean sectionDataResilience = false;
    private boolean scalefactorResilience = false;
    private boolean spectralDataResilience = false;

    public PS openPS(SBR sbr) {
        this.psPresent = true;
        return new PSImpl(sbr.numTimeSlotsRate);
    }

    DecoderConfig(boolean sbrEnabled) {
        this.sbrEnabled = sbrEnabled;
    }

    DecoderConfig() {
        this(true);
    }

    public ChannelConfiguration getChannelConfiguration() {
        return this.channelConfiguration;
    }

    public DecoderConfig setAudioDecoderInfo(AudioDecoderInfo info) {
        this.profile = info.getProfile();
        this.sampleFrequency = info.getSampleFrequency();
        this.channelConfiguration = info.getChannelConfiguration();
        return this;
    }

    public int getCoreCoderDelay() {
        return this.coreCoderDelay;
    }

    public boolean isDependsOnCoreCoder() {
        return this.dependsOnCoreCoder;
    }

    public Profile getExtObjectType() {
        return this.extProfile;
    }

    public int getFrameLength() {
        return this.frameLengthFlag ? 960 : 1024;
    }

    public int getSampleLength() {
        int upsampled = this.outputFrequency != null && this.sampleFrequency != this.outputFrequency ? 2 : 1;
        return upsampled * this.getFrameLength();
    }

    public boolean isSmallFrameUsed() {
        return this.frameLengthFlag;
    }

    public Profile getProfile() {
        return this.profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public SampleRate getSampleFrequency() {
        return this.sampleFrequency;
    }

    public SampleRate getOutputFrequency() {
        return this.outputFrequency != null ? this.outputFrequency : this.sampleFrequency;
    }

    public int getChannelCount() {
        if (this.sbrEnabled && this.channelConfiguration == ChannelConfiguration.MONO) {
            return 2;
        }
        return this.channelConfiguration.getChannelCount();
    }

    public boolean setSBRPresent() {
        this.sbrPresent = true;
        if (this.outputFrequency == null) {
            SampleRate duplicated = this.sampleFrequency.duplicated();
            if (duplicated == SampleFrequency.SF_NONE) {
                return false;
            }
            this.outputFrequency = duplicated;
        }
        return this.isUpSampled();
    }

    boolean isUpSampled() {
        return this.outputFrequency != null && this.outputFrequency != this.sampleFrequency;
    }

    public boolean isSBREnabled() {
        return this.sbrEnabled;
    }

    public boolean isPSEnabled() {
        return this.psEnabled;
    }

    public boolean isScalefactorResilienceUsed() {
        return this.scalefactorResilience;
    }

    public boolean isSectionDataResilienceUsed() {
        return this.sectionDataResilience;
    }

    public boolean isSpectralDataResilienceUsed() {
        return this.spectralDataResilience;
    }

    public static DecoderConfig create(AudioDecoderInfo info) {
        return new DecoderConfig().setAudioDecoderInfo(info);
    }

    public DecoderConfig decode(BitStream in) {
        this.profile = DecoderConfig.readProfile(in);
        this.outputFrequency = this.sampleFrequency = SampleRate.decode(in);
        this.channelConfiguration = ChannelConfiguration.forInt(in.readBits(4));
        switch (this.profile) {
            case AAC_PS: {
                this.psPresent = true;
            }
            case AAC_SBR: {
                SampleRate frequency = SampleRate.decode(in);
                this.extProfile = this.profile;
                this.profile = DecoderConfig.readProfile(in);
                if (!this.sbrEnabled) break;
                this.outputFrequency = frequency;
                break;
            }
            case AAC_MAIN: 
            case AAC_LC: 
            case AAC_SSR: 
            case AAC_LTP: 
            case ER_AAC_LC: 
            case ER_AAC_LTP: 
            case ER_AAC_LD: {
                this.frameLengthFlag = in.readBool();
                if (this.frameLengthFlag) {
                    throw new AACException("config uses 960-sample frames, not yet supported");
                }
                this.dependsOnCoreCoder = in.readBool();
                this.coreCoderDelay = this.dependsOnCoreCoder ? in.readBits(14) : 0;
                this.extensionFlag = in.readBool();
                if (this.extensionFlag) {
                    if (this.profile.isErrorResilientProfile()) {
                        this.sectionDataResilience = in.readBool();
                        this.scalefactorResilience = in.readBool();
                        this.spectralDataResilience = in.readBool();
                    }
                    in.skipBit();
                }
                if (this.channelConfiguration == ChannelConfiguration.NONE) {
                    PCE pce = PCE.read(in);
                    this.setAudioDecoderInfo(pce);
                }
                if (!this.sbrEnabled || in.getBitsLeft() <= 10) break;
                this.readSyncExtension(in);
                break;
            }
            default: {
                throw new AACException("profile not supported: " + this.profile.getIndex());
            }
        }
        return this;
    }

    private static Profile readProfile(BitStream in) {
        int i = in.readBits(5);
        if (i == 31) {
            i = 32 + in.readBits(6);
        }
        return Profile.forInt(i);
    }

    private void readSyncExtension(BitStream in) {
        int extensionType = in.readBits(11);
        if (extensionType == 695) {
            this.extProfile = Profile.forInt(in.readBits(5));
            if (this.extProfile.equals((Object)Profile.AAC_SBR) || this.extProfile.equals((Object)Profile.ER_BSAC)) {
                this.sbrPresent = in.readBool();
                if (this.sbrPresent) {
                    this.outputFrequency = SampleRate.decode(in);
                }
                if (this.extProfile.equals((Object)Profile.AAC_SBR)) {
                    if (in.getBitsLeft() > 12 && (extensionType = in.readBits(11)) == 1352) {
                        this.psPresent = in.readBool();
                    }
                } else if (this.extProfile.equals((Object)Profile.ER_BSAC)) {
                    this.extChannelConfiguration = ChannelConfiguration.forInt(in.readBits(4));
                }
            }
        }
    }
}

