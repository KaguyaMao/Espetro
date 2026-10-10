/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.javazoom.jl.converter;

import LOL_141.vehicle_addition.soundlibs.javazoom.jl.converter.RiffFile;

public class WaveFile
extends RiffFile {
    public static final int MAX_WAVE_CHANNELS = 2;
    private WaveFormat_Chunk wave_format;
    private RiffFile.RiffChunkHeader pcm_data = new RiffFile.RiffChunkHeader();
    private long pcm_data_offset = 0L;
    private int num_samples = 0;

    public WaveFile() {
        this.wave_format = new WaveFormat_Chunk();
        this.pcm_data.ckID = WaveFile.FourCC("data");
        this.pcm_data.ckSize = 0;
        this.num_samples = 0;
    }

    public int OpenForWrite(String Filename, int SamplingRate, short BitsPerSample, short NumChannels) {
        byte[] theWave;
        if (Filename == null || BitsPerSample != 8 && BitsPerSample != 16 || NumChannels < 1 || NumChannels > 2) {
            return 4;
        }
        this.wave_format.data.Config(SamplingRate, BitsPerSample, NumChannels);
        int retcode = this.Open(Filename, 1);
        if (retcode == 0 && (retcode = this.Write(theWave = new byte[]{87, 65, 86, 69}, 4)) == 0) {
            retcode = this.Write(this.wave_format.header, 8);
            retcode = this.Write(this.wave_format.data.wFormatTag, 2);
            retcode = this.Write(this.wave_format.data.nChannels, 2);
            retcode = this.Write(this.wave_format.data.nSamplesPerSec, 4);
            retcode = this.Write(this.wave_format.data.nAvgBytesPerSec, 4);
            retcode = this.Write(this.wave_format.data.nBlockAlign, 2);
            retcode = this.Write(this.wave_format.data.nBitsPerSample, 2);
            if (retcode == 0) {
                this.pcm_data_offset = this.CurrentFilePosition();
                retcode = this.Write(this.pcm_data, 8);
            }
        }
        return retcode;
    }

    public int WriteData(short[] data, int numData) {
        int extraBytes = numData * 2;
        this.pcm_data.ckSize += extraBytes;
        return super.Write(data, extraBytes);
    }

    public int Close() {
        int rc = 0;
        if (this.fmode == 1) {
            rc = this.Backpatch(this.pcm_data_offset, this.pcm_data, 8);
        }
        if (rc == 0) {
            rc = super.Close();
        }
        return rc;
    }

    public int SamplingRate() {
        return this.wave_format.data.nSamplesPerSec;
    }

    public short BitsPerSample() {
        return this.wave_format.data.nBitsPerSample;
    }

    public short NumChannels() {
        return this.wave_format.data.nChannels;
    }

    public int NumSamples() {
        return this.num_samples;
    }

    public int OpenForWrite(String Filename, WaveFile OtherWave) {
        return this.OpenForWrite(Filename, OtherWave.SamplingRate(), OtherWave.BitsPerSample(), OtherWave.NumChannels());
    }

    public long CurrentFilePosition() {
        return super.CurrentFilePosition();
    }

    public class WaveFileSample {
        public short[] chan = new short[2];
    }

    class WaveFormat_Chunk {
        public RiffFile.RiffChunkHeader header;
        public WaveFormat_ChunkData data;

        public WaveFormat_Chunk() {
            this.header = new RiffFile.RiffChunkHeader();
            this.data = new WaveFormat_ChunkData();
            this.header.ckID = RiffFile.FourCC("fmt ");
            this.header.ckSize = 16;
        }

        public int VerifyValidity() {
            boolean ret;
            boolean bl = ret = this.header.ckID == RiffFile.FourCC("fmt ") && (this.data.nChannels == 1 || this.data.nChannels == 2) && this.data.nAvgBytesPerSec == this.data.nChannels * this.data.nSamplesPerSec * this.data.nBitsPerSample / 8 && this.data.nBlockAlign == this.data.nChannels * this.data.nBitsPerSample / 8;
            if (ret) {
                return 1;
            }
            return 0;
        }
    }

    class WaveFormat_ChunkData {
        public short wFormatTag = 1;
        public short nChannels = 0;
        public int nSamplesPerSec = 0;
        public int nAvgBytesPerSec = 0;
        public short nBlockAlign = 0;
        public short nBitsPerSample = 0;

        public WaveFormat_ChunkData() {
            this.Config(44100, (short)16, (short)1);
        }

        public void Config(int NewSamplingRate, short NewBitsPerSample, short NewNumChannels) {
            this.nSamplesPerSec = NewSamplingRate;
            this.nChannels = NewNumChannels;
            this.nBitsPerSample = NewBitsPerSample;
            this.nAvgBytesPerSec = this.nChannels * this.nSamplesPerSec * this.nBitsPerSample / 8;
            this.nBlockAlign = (short)(this.nChannels * this.nBitsPerSample / 8);
        }
    }
}

