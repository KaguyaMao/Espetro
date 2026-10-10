/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.org.tritonus.share;

import java.io.PrintStream;
import java.security.AccessControlException;
import java.util.StringTokenizer;

public class TDebug {
    public static boolean SHOW_ACCESS_CONTROL_EXCEPTIONS = false;
    private static final String PROPERTY_PREFIX = "tritonus.";
    public static PrintStream m_printStream = System.out;
    private static String indent = "";
    public static boolean TraceAllExceptions = TDebug.getBooleanProperty("TraceAllExceptions");
    public static boolean TraceAllWarnings = TDebug.getBooleanProperty("TraceAllWarnings");
    public static boolean TraceInit = TDebug.getBooleanProperty("TraceInit");
    public static boolean TraceCircularBuffer = TDebug.getBooleanProperty("TraceCircularBuffer");
    public static boolean TraceService = TDebug.getBooleanProperty("TraceService");
    public static boolean TraceAudioSystem = TDebug.getBooleanProperty("TraceAudioSystem");
    public static boolean TraceAudioConfig = TDebug.getBooleanProperty("TraceAudioConfig");
    public static boolean TraceAudioInputStream = TDebug.getBooleanProperty("TraceAudioInputStream");
    public static boolean TraceMixerProvider = TDebug.getBooleanProperty("TraceMixerProvider");
    public static boolean TraceControl = TDebug.getBooleanProperty("TraceControl");
    public static boolean TraceLine = TDebug.getBooleanProperty("TraceLine");
    public static boolean TraceDataLine = TDebug.getBooleanProperty("TraceDataLine");
    public static boolean TraceMixer = TDebug.getBooleanProperty("TraceMixer");
    public static boolean TraceSourceDataLine = TDebug.getBooleanProperty("TraceSourceDataLine");
    public static boolean TraceTargetDataLine = TDebug.getBooleanProperty("TraceTargetDataLine");
    public static boolean TraceClip = TDebug.getBooleanProperty("TraceClip");
    public static boolean TraceAudioFileReader = TDebug.getBooleanProperty("TraceAudioFileReader");
    public static boolean TraceAudioFileWriter = TDebug.getBooleanProperty("TraceAudioFileWriter");
    public static boolean TraceAudioConverter = TDebug.getBooleanProperty("TraceAudioConverter");
    public static boolean TraceAudioOutputStream = TDebug.getBooleanProperty("TraceAudioOutputStream");
    public static boolean TraceEsdNative = TDebug.getBooleanProperty("TraceEsdNative");
    public static boolean TraceEsdStreamNative = TDebug.getBooleanProperty("TraceEsdStreamNative");
    public static boolean TraceEsdRecordingStreamNative = TDebug.getBooleanProperty("TraceEsdRecordingStreamNative");
    public static boolean TraceAlsaNative = TDebug.getBooleanProperty("TraceAlsaNative");
    public static boolean TraceAlsaMixerNative = TDebug.getBooleanProperty("TraceAlsaMixerNative");
    public static boolean TraceAlsaPcmNative = TDebug.getBooleanProperty("TraceAlsaPcmNative");
    public static boolean TraceMixingAudioInputStream = TDebug.getBooleanProperty("TraceMixingAudioInputStream");
    public static boolean TraceOggNative = TDebug.getBooleanProperty("TraceOggNative");
    public static boolean TraceVorbisNative = TDebug.getBooleanProperty("TraceVorbisNative");
    public static boolean TraceMidiSystem = TDebug.getBooleanProperty("TraceMidiSystem");
    public static boolean TraceMidiConfig = TDebug.getBooleanProperty("TraceMidiConfig");
    public static boolean TraceMidiDeviceProvider = TDebug.getBooleanProperty("TraceMidiDeviceProvider");
    public static boolean TraceSequencer = TDebug.getBooleanProperty("TraceSequencer");
    public static boolean TraceSynthesizer = TDebug.getBooleanProperty("TraceSynthesizer");
    public static boolean TraceMidiDevice = TDebug.getBooleanProperty("TraceMidiDevice");
    public static boolean TraceAlsaSeq = TDebug.getBooleanProperty("TraceAlsaSeq");
    public static boolean TraceAlsaSeqDetails = TDebug.getBooleanProperty("TraceAlsaSeqDetails");
    public static boolean TraceAlsaSeqNative = TDebug.getBooleanProperty("TraceAlsaSeqNative");
    public static boolean TracePortScan = TDebug.getBooleanProperty("TracePortScan");
    public static boolean TraceAlsaMidiIn = TDebug.getBooleanProperty("TraceAlsaMidiIn");
    public static boolean TraceAlsaMidiOut = TDebug.getBooleanProperty("TraceAlsaMidiOut");
    public static boolean TraceAlsaMidiChannel = TDebug.getBooleanProperty("TraceAlsaMidiChannel");
    public static boolean TraceFluidNative = TDebug.getBooleanProperty("TraceFluidNative");
    public static boolean TraceAlsaCtlNative = TDebug.getBooleanProperty("TraceAlsaCtlNative");
    public static boolean TraceCdda = TDebug.getBooleanProperty("TraceCdda");
    public static boolean TraceCddaNative = TDebug.getBooleanProperty("TraceCddaNative");

    public static void out(String strMessage) {
        if (strMessage.length() > 0 && strMessage.charAt(0) == '<') {
            indent = indent.length() > 2 ? indent.substring(2) : "";
        }
        String newMsg = null;
        if (indent != "" && strMessage.indexOf("\n") >= 0) {
            newMsg = "";
            StringTokenizer tokenizer = new StringTokenizer(strMessage, "\n");
            while (tokenizer.hasMoreTokens()) {
                newMsg = newMsg + indent + tokenizer.nextToken() + "\n";
            }
        } else {
            newMsg = indent + strMessage;
        }
        m_printStream.println(newMsg);
        if (strMessage.length() > 0 && strMessage.charAt(0) == '>') {
            indent = indent + "  ";
        }
    }

    public static void out(Throwable throwable) {
        throwable.printStackTrace(m_printStream);
    }

    public static void assertion(boolean bAssertion) {
        if (!bAssertion) {
            throw new AssertException();
        }
    }

    private static boolean getBooleanProperty(String strName) {
        String strValue;
        block2: {
            String strPropertyName = PROPERTY_PREFIX + strName;
            strValue = "false";
            try {
                strValue = System.getProperty(strPropertyName, "false");
            }
            catch (AccessControlException e) {
                if (!SHOW_ACCESS_CONTROL_EXCEPTIONS) break block2;
                TDebug.out(e);
            }
        }
        boolean bValue = strValue.toLowerCase().equals("true");
        return bValue;
    }

    public static class AssertException
    extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public AssertException() {
        }

        public AssertException(String sMessage) {
            super(sMessage);
        }
    }
}

