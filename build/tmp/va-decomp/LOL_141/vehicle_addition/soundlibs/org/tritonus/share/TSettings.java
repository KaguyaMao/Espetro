/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.org.tritonus.share;

import LOL_141.vehicle_addition.soundlibs.org.tritonus.share.TDebug;
import java.security.AccessControlException;

public class TSettings {
    public static boolean SHOW_ACCESS_CONTROL_EXCEPTIONS = false;
    private static final String PROPERTY_PREFIX = "tritonus.";
    public static boolean AlsaUsePlughw = TSettings.getBooleanProperty("AlsaUsePlughw");

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
}

