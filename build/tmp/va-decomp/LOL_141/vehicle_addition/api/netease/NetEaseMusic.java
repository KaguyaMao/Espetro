/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package LOL_141.vehicle_addition.api.netease;

import java.util.HashMap;
import org.apache.commons.lang3.StringUtils;

public class NetEaseMusic {
    private final HashMap<String, String> requestPropertyData = new HashMap();

    public NetEaseMusic() {
        this.init();
    }

    public NetEaseMusic(String cookie) {
        this.init();
        this.requestPropertyData.put("Cookie", cookie);
    }

    public static String getHost() {
        return "music.163.com";
    }

    public static String getOrigin() {
        return "http://music.163.com";
    }

    public static String getReferer() {
        return "http://music.163.com/";
    }

    public static String getUserAgent() {
        return StringUtils.joinWith((String)" ", (Object[])new Object[]{"Mozilla/5.0 (Windows NT 6.1; Win64; x64)", "AppleWebKit/537.36 (KHTML, like Gecko)", "Chrome/81.0.4044.138", "Safari/537.36"});
    }

    private void init() {
        this.requestPropertyData.put("Origin", NetEaseMusic.getOrigin());
        this.requestPropertyData.put("Referer", NetEaseMusic.getReferer());
        this.requestPropertyData.put("Content-Type", "application/x-www-form-urlencoded");
        this.requestPropertyData.put("User-Agent", NetEaseMusic.getUserAgent());
    }

    public HashMap<String, String> getRequestPropertyData() {
        return this.requestPropertyData;
    }
}

