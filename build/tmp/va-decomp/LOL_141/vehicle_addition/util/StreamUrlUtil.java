/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.util;

import java.net.URI;
import java.net.URL;

public final class StreamUrlUtil {
    private static final String HTTP = "http";
    private static final String HTTPS = "https";
    private static final String PLAY_URL = "https://apicnrapp.cnr.cn/html/play.html";

    private StreamUrlUtil() {
    }

    public static boolean isValidStreamUrl(String url) {
        try {
            if (url == null || url.isBlank()) {
                return false;
            }
            if (url.startsWith(PLAY_URL)) {
                return true;
            }
            return StreamUrlUtil.isM3u8Url(URI.create(url.trim()).toURL());
        }
        catch (Exception e) {
            return false;
        }
    }

    public static boolean isM3u8Url(URL url) {
        if (url == null) {
            return false;
        }
        String protocol = url.getProtocol();
        if (!HTTP.equalsIgnoreCase(protocol) && !HTTPS.equalsIgnoreCase(protocol)) {
            return false;
        }
        String path = url.getPath();
        return path != null && path.toLowerCase().endsWith(".m3u8");
    }
}

