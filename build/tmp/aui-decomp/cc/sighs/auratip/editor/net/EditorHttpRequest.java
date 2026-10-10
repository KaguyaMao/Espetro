/*
 * Decompiled with CFR 0.152.
 */
package cc.sighs.auratip.editor.net;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

record EditorHttpRequest(String method, String uri, Map<String, String> headersLower) {
    EditorHttpRequest(String method, String uri, Map<String, String> headersLower) {
        this.method = method == null ? "" : method;
        this.uri = uri == null ? "" : uri;
        this.headersLower = headersLower == null ? Collections.emptyMap() : Map.copyOf(headersLower);
    }

    public String path() {
        int q = this.uri.indexOf(63);
        return q >= 0 ? this.uri.substring(0, q) : this.uri;
    }

    public String headerLower(String nameLower) {
        if (nameLower == null || nameLower.isEmpty()) {
            return "";
        }
        return this.headersLower.getOrDefault(nameLower, "");
    }

    static Map<String, String> headersLowerMutable() {
        return new LinkedHashMap<String, String>();
    }
}

