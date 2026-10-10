/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.util;

import java.net.URI;
import java.net.URISyntaxException;

public final class BrowserLocation {
    private final String href;
    private final String protocol;
    private final String host;
    private final String hostname;
    private final String port;
    private final String origin;
    private final String pathname;
    private final String search;
    private final String hash;

    public BrowserLocation(String href) {
        Object resolvedHash;
        Object resolvedSearch;
        String resolvedPathname;
        String resolvedOrigin;
        String resolvedPort;
        String resolvedHostname;
        String resolvedHost;
        Object resolvedProtocol;
        String rawHref;
        block7: {
            rawHref = href == null ? "" : href;
            resolvedProtocol = "";
            resolvedHost = "";
            resolvedHostname = "";
            resolvedPort = "";
            resolvedOrigin = "";
            resolvedPathname = rawHref;
            resolvedSearch = "";
            resolvedHash = "";
            try {
                URI uri = new URI(rawHref);
                if (uri.getScheme() != null && !uri.getScheme().isBlank()) {
                    resolvedProtocol = uri.getScheme() + ":";
                }
                if (uri.getHost() != null && !uri.getHost().isBlank()) {
                    resolvedHostname = uri.getHost();
                    resolvedPort = uri.getPort() >= 0 ? String.valueOf(uri.getPort()) : "";
                    resolvedHost = resolvedPort.isEmpty() ? resolvedHostname : resolvedHostname + ":" + resolvedPort;
                    String string = resolvedOrigin = ((String)resolvedProtocol).isEmpty() ? "" : (String)resolvedProtocol + "//" + resolvedHost;
                }
                resolvedPathname = uri.getRawPath() != null && !uri.getRawPath().isEmpty() ? uri.getRawPath() : (resolvedHostname.isEmpty() ? BrowserLocation.stripQueryAndHash(rawHref) : "");
                if (uri.getRawQuery() != null) {
                    resolvedSearch = "?" + uri.getRawQuery();
                }
                if (uri.getRawFragment() != null) {
                    resolvedHash = "#" + uri.getRawFragment();
                }
            }
            catch (URISyntaxException ignored) {
                resolvedPathname = BrowserLocation.stripQueryAndHash(rawHref);
                int queryIndex = rawHref.indexOf(63);
                int hashIndex = rawHref.indexOf(35);
                if (queryIndex >= 0) {
                    int queryEnd = hashIndex >= 0 && hashIndex > queryIndex ? hashIndex : rawHref.length();
                    resolvedSearch = rawHref.substring(queryIndex, queryEnd);
                }
                if (hashIndex < 0) break block7;
                resolvedHash = rawHref.substring(hashIndex);
            }
        }
        this.href = rawHref;
        this.protocol = resolvedProtocol;
        this.host = resolvedHost;
        this.hostname = resolvedHostname;
        this.port = resolvedPort;
        this.origin = resolvedOrigin;
        this.pathname = resolvedPathname == null ? "" : resolvedPathname;
        this.search = resolvedSearch;
        this.hash = resolvedHash;
    }

    public String getHref() {
        return this.href;
    }

    public String getProtocol() {
        return this.protocol;
    }

    public String getHost() {
        return this.host;
    }

    public String getHostname() {
        return this.hostname;
    }

    public String getPort() {
        return this.port;
    }

    public String getOrigin() {
        return this.origin;
    }

    public String getPathname() {
        return this.pathname;
    }

    public String getSearch() {
        return this.search;
    }

    public String getHash() {
        return this.hash;
    }

    public void assign(String ignoredHref) {
    }

    public void replace(String ignoredHref) {
    }

    public void reload() {
    }

    private static String stripQueryAndHash(String href) {
        if (href == null || href.isEmpty()) {
            return "";
        }
        int queryIndex = href.indexOf(63);
        int hashIndex = href.indexOf(35);
        int end = href.length();
        if (queryIndex >= 0) {
            end = Math.min(end, queryIndex);
        }
        if (hashIndex >= 0) {
            end = Math.min(end, hashIndex);
        }
        return href.substring(0, end);
    }
}

