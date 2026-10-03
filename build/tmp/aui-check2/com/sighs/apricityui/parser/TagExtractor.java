/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.parser;

import com.sighs.apricityui.ApricityUI;
import com.sighs.apricityui.init.Document;
import com.sighs.apricityui.parser.HTML;
import com.sighs.apricityui.util.AuiLog;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

abstract class TagExtractor {
    private final Pattern openMarker;
    private final Pattern closeMarker;
    private final String logPrefix;
    private final String tagName;
    protected final String contextPath;
    protected final List<String> cachedSrcs = new ArrayList<String>();
    protected final List<String> cachedContents = new ArrayList<String>();

    TagExtractor(String contextPath, Pattern openMarker, Pattern closeMarker, String logPrefix, String tagName) {
        this.contextPath = contextPath;
        this.openMarker = openMarker;
        this.closeMarker = closeMarker;
        this.logPrefix = logPrefix;
        this.tagName = tagName;
    }

    public String handle(String html) {
        int closeCount;
        if (html == null || html.isEmpty()) {
            return html;
        }
        int openCount = HTML.countMatches(this.openMarker, html);
        if (openCount != (closeCount = HTML.countMatches(this.closeMarker, html))) {
            ApricityUI.LOGGER.warn("[AUI {}] unmatched {} tag path={} openTags={} closeTags={}", new Object[]{this.logPrefix, this.tagName, AuiLog.source(this.contextPath), openCount, closeCount});
        }
        return this.extract(html);
    }

    protected String removeTags(String html, Pattern tagPattern) {
        Matcher matcher = tagPattern.matcher(html);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            this.onTag(matcher.group(1), matcher.group(2));
            matcher.appendReplacement(sb, "");
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    protected abstract String extract(String var1);

    protected abstract void onTag(String var1, String var2);

    public List<String> sourceSnapshot() {
        return List.copyOf(this.cachedSrcs);
    }

    public List<String> contentSnapshot() {
        return List.copyOf(this.cachedContents);
    }

    public abstract void pushToDocument(Document var1);
}

