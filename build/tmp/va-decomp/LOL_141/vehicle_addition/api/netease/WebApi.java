/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.net.UrlEscapers
 *  org.apache.commons.lang3.StringUtils
 */
package LOL_141.vehicle_addition.api.netease;

import LOL_141.vehicle_addition.api.NetWorker;
import LOL_141.vehicle_addition.api.netease.EncryptUtils;
import com.google.common.net.UrlEscapers;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import org.apache.commons.lang3.StringUtils;

public final class WebApi {
    public static final int TYPE_SONG = 1;
    public static final int TYPE_ALBUM = 10;
    public static final int TYPE_SINGER = 100;
    public static final int TYPE_PLAY_LIST = 1000;
    public static final int TYPE_USER = 1002;
    public static final int TYPE_RADIO = 1009;
    private final HashMap<String, String> requestPropertyData;

    public WebApi(HashMap<String, String> requestPropertyData) {
        this.requestPropertyData = requestPropertyData;
    }

    public static String getSearchUrl(String searchText, int type, int limit) {
        String escape = UrlEscapers.urlPathSegmentEscaper().escape(searchText);
        return "https://music.163.com/api/search/get/web?s=%s&type=%d&limit=%d".formatted(escape, type, limit);
    }

    public String song(long songId) throws IOException {
        String url = "http://music.163.com/api/song/detail/?id=" + songId + "&ids=%5B" + songId + "%5D";
        return NetWorker.get(url, this.requestPropertyData);
    }

    public String songs(long ... songIds) throws IOException {
        String ids = StringUtils.deleteWhitespace((String)Arrays.toString(songIds));
        String url = "http://music.163.com/api/song/detail/?ids=" + URLEncoder.encode(ids, "utf-8");
        return NetWorker.get(url, this.requestPropertyData);
    }

    public String mp3(long quality, long ... songIds) throws Exception {
        String url = "http://music.163.com/weapi/song/enhance/player/url?csrf_token=";
        String param = "{\"ids\":" + Arrays.toString(songIds) + ",\"br\":" + quality + ",\"csrf_token\":\"\"}";
        String encrypt = EncryptUtils.encryptedParam(param);
        return NetWorker.post(url, encrypt, this.requestPropertyData);
    }

    public String list(long listId) throws Exception {
        String url = "http://music.163.com/weapi/v3/playlist/detail?csrf_token=";
        String param = "{\"id\":" + listId + ",\"csrf_token\":\"\"}";
        String encrypt = EncryptUtils.encryptedParam(param);
        return NetWorker.post(url, encrypt, this.requestPropertyData);
    }

    public String lyric(long songId) throws IOException {
        String url = "http://music.163.com/api/song/lyric/?id=" + songId + "&lv=-1&kv=-1&tv=-1";
        return NetWorker.get(url, this.requestPropertyData);
    }

    public String getRedirectMusicUrl(long musicId) {
        return String.format("https://music.163.com/song/media/outer/url?id=%d.mp3", musicId);
    }

    public HashMap<String, String> getRequestPropertyData() {
        return this.requestPropertyData;
    }
}

