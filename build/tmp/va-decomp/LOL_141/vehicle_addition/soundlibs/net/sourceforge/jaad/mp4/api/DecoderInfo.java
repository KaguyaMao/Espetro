/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.api;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.api.codec.AC3DecoderInfo;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.api.codec.AMRDecoderInfo;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.api.codec.AVCDecoderInfo;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.api.codec.EAC3DecoderInfo;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.api.codec.EVRCDecoderInfo;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.api.codec.H263DecoderInfo;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.api.codec.QCELPDecoderInfo;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.api.codec.SMVDecoderInfo;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.sampleentries.codec.CodecSpecificBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.util.Utils;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class DecoderInfo {
    static final Logger LOGGER = Logger.getLogger("jaad.mp4.Api");

    static DecoderInfo parse(CodecSpecificBox css) {
        long l = css.getType();
        DecoderInfo info = l == 1681012275L ? new H263DecoderInfo(css) : (l == 1684106610L ? new AMRDecoderInfo(css) : (l == 1684371043L ? new EVRCDecoderInfo(css) : (l == 1685152624L ? new QCELPDecoderInfo(css) : (l == 1685286262L ? new SMVDecoderInfo(css) : (l == 1635148611L ? new AVCDecoderInfo(css) : (l == 1684103987L ? new AC3DecoderInfo(css) : (l == 1684366131L ? new EAC3DecoderInfo(css) : new UnknownDecoderInfo())))))));
        return info;
    }

    static {
        if (!Utils.isDebug) {
            LOGGER.setLevel(Level.WARNING);
        }
    }

    private static class UnknownDecoderInfo
    extends DecoderInfo {
        private UnknownDecoderInfo() {
        }
    }
}

