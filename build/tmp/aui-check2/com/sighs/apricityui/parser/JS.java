/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.parser;

import com.sighs.apricityui.ApricityUI;
import com.sighs.apricityui.init.Document;
import com.sighs.apricityui.loader.ClientLoader;
import com.sighs.apricityui.loader.Loader;
import com.sighs.apricityui.parser.ResourceUsageIndex;
import com.sighs.apricityui.parser.TagExtractor;
import com.sighs.apricityui.util.AuiLog;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JS {
    private static final Pattern ARRAY_SPREAD_PATTERN = Pattern.compile("\\[\\.\\.\\.([A-Za-z_$][\\w$]*)\\]");
    private static final Pattern FUNCTION_HEAD_PATTERN = Pattern.compile("\\bfunction\\s+([A-Za-z_$][\\w$]*)\\s*\\(");

    public static String rewriteForRhino(String code) {
        if (code == null) {
            return null;
        }
        code = ARRAY_SPREAD_PATTERN.matcher(code).replaceAll("$1.slice()");
        code = JS.rewriteDefaultParameters(code);
        return code;
    }

    private static String rewriteDefaultParameters(String code) {
        StringBuilder out = new StringBuilder();
        Matcher matcher = FUNCTION_HEAD_PATTERN.matcher(code);
        int lastEnd = 0;
        while (matcher.find()) {
            int bracePos;
            int paramsStart = matcher.end();
            int paramsEnd = JS.findMatchingParen(code, paramsStart);
            if (paramsEnd < 0) continue;
            for (bracePos = paramsEnd + 1; bracePos < code.length() && Character.isWhitespace(code.charAt(bracePos)); ++bracePos) {
            }
            if (bracePos >= code.length() || code.charAt(bracePos) != '{') continue;
            String params = code.substring(paramsStart, paramsEnd);
            List<String> parts = JS.splitTopLevel(params, ',');
            ArrayList<String> newParams = new ArrayList<String>(parts.size());
            StringBuilder assignments = new StringBuilder();
            boolean hasDefault = false;
            for (String part : parts) {
                String trimmed = part.trim();
                if (trimmed.isEmpty()) continue;
                int eq = JS.findTopLevelEquals(trimmed);
                if (eq >= 0) {
                    hasDefault = true;
                    String name = trimmed.substring(0, eq).trim();
                    String expr = trimmed.substring(eq + 1).trim();
                    newParams.add(name);
                    assignments.append("if (typeof ").append(name).append(" === 'undefined') ").append(name).append(" = ").append(expr).append(";");
                    continue;
                }
                newParams.add(trimmed);
            }
            if (!hasDefault) continue;
            out.append(code, lastEnd, matcher.start());
            out.append("function ").append(matcher.group(1)).append("(").append(String.join((CharSequence)", ", newParams)).append(") {").append((CharSequence)assignments);
            lastEnd = bracePos + 1;
        }
        out.append(code.substring(lastEnd));
        return out.toString();
    }

    private static int findMatchingParen(String text, int openPos) {
        int depth = 1;
        boolean inSingle = false;
        boolean inDouble = false;
        boolean inTemplate = false;
        boolean escape = false;
        for (int i = openPos; i < text.length(); ++i) {
            char c = text.charAt(i);
            if (escape) {
                escape = false;
                continue;
            }
            if (c == '\\') {
                escape = true;
                continue;
            }
            if (inSingle) {
                if (c != '\'') continue;
                inSingle = false;
                continue;
            }
            if (inDouble) {
                if (c != '\"') continue;
                inDouble = false;
                continue;
            }
            if (inTemplate) {
                if (c != '`') continue;
                inTemplate = false;
                continue;
            }
            if (c == '\'') {
                inSingle = true;
                continue;
            }
            if (c == '\"') {
                inDouble = true;
                continue;
            }
            if (c == '`') {
                inTemplate = true;
                continue;
            }
            if (c == '(') {
                ++depth;
                continue;
            }
            if (c != ')' || --depth != 0) continue;
            return i;
        }
        return -1;
    }

    private static List<String> splitTopLevel(String text, char delimiter) {
        ArrayList<String> result = new ArrayList<String>();
        StringBuilder current = new StringBuilder();
        int paren = 0;
        int bracket = 0;
        int brace = 0;
        boolean inSingle = false;
        boolean inDouble = false;
        boolean inTemplate = false;
        boolean escape = false;
        for (int i = 0; i < text.length(); ++i) {
            char c = text.charAt(i);
            if (escape) {
                current.append(c);
                escape = false;
                continue;
            }
            if (c == '\\') {
                current.append(c);
                escape = true;
                continue;
            }
            if (inSingle) {
                current.append(c);
                if (c != '\'') continue;
                inSingle = false;
                continue;
            }
            if (inDouble) {
                current.append(c);
                if (c != '\"') continue;
                inDouble = false;
                continue;
            }
            if (inTemplate) {
                current.append(c);
                if (c != '`') continue;
                inTemplate = false;
                continue;
            }
            if (c == '\'') {
                current.append(c);
                inSingle = true;
                continue;
            }
            if (c == '\"') {
                current.append(c);
                inDouble = true;
                continue;
            }
            if (c == '`') {
                current.append(c);
                inTemplate = true;
                continue;
            }
            if (c == '(') {
                ++paren;
                current.append(c);
                continue;
            }
            if (c == ')') {
                --paren;
                current.append(c);
                continue;
            }
            if (c == '[') {
                ++bracket;
                current.append(c);
                continue;
            }
            if (c == ']') {
                --bracket;
                current.append(c);
                continue;
            }
            if (c == '{') {
                ++brace;
                current.append(c);
                continue;
            }
            if (c == '}') {
                --brace;
                current.append(c);
                continue;
            }
            if (c == delimiter && paren == 0 && bracket == 0 && brace == 0) {
                result.add(current.toString());
                current.setLength(0);
                continue;
            }
            current.append(c);
        }
        result.add(current.toString());
        return result;
    }

    private static int findTopLevelEquals(String text) {
        int paren = 0;
        int bracket = 0;
        int brace = 0;
        boolean inSingle = false;
        boolean inDouble = false;
        boolean inTemplate = false;
        boolean escape = false;
        for (int i = 0; i < text.length(); ++i) {
            boolean nextIsArrow;
            char c = text.charAt(i);
            if (escape) {
                escape = false;
                continue;
            }
            if (c == '\\') {
                escape = true;
                continue;
            }
            if (inSingle) {
                if (c != '\'') continue;
                inSingle = false;
                continue;
            }
            if (inDouble) {
                if (c != '\"') continue;
                inDouble = false;
                continue;
            }
            if (inTemplate) {
                if (c != '`') continue;
                inTemplate = false;
                continue;
            }
            if (c == '\'') {
                inSingle = true;
                continue;
            }
            if (c == '\"') {
                inDouble = true;
                continue;
            }
            if (c == '`') {
                inTemplate = true;
                continue;
            }
            if (c == '(') {
                ++paren;
                continue;
            }
            if (c == ')') {
                --paren;
                continue;
            }
            if (c == '[') {
                ++bracket;
                continue;
            }
            if (c == ']') {
                --bracket;
                continue;
            }
            if (c == '{') {
                ++brace;
                continue;
            }
            if (c == '}') {
                --brace;
                continue;
            }
            if (c != '=' || paren != 0 || bracket != 0 || brace != 0) continue;
            boolean prevIsOp = i > 0 && "=!<>".indexOf(text.charAt(i - 1)) >= 0;
            boolean nextIsOp = i + 1 < text.length() && text.charAt(i + 1) == '=';
            boolean bl = nextIsArrow = i + 1 < text.length() && text.charAt(i + 1) == '>';
            if (prevIsOp || nextIsOp || nextIsArrow) continue;
            return i;
        }
        return -1;
    }

    public static class Extractor
    extends TagExtractor {
        private static final Pattern SCRIPT_TAG_PATTERN = Pattern.compile("(?i)<script\\b([^>]*)>(.*?)</script\\s*>", 32);
        private static final Pattern SRC_ATTR_PATTERN = Pattern.compile("(?i)\\bsrc\\s*=\\s*(['\"])(.*?)\\1");
        private static final Pattern SCRIPT_OPEN_MARKER = Pattern.compile("(?i)<script\\b");
        private static final Pattern SCRIPT_CLOSE_MARKER = Pattern.compile("(?i)</script\\s*>");

        public Extractor(String contextPath) {
            super(contextPath, SCRIPT_OPEN_MARKER, SCRIPT_CLOSE_MARKER, "JS", "script");
        }

        @Override
        protected String extract(String html) {
            return this.removeTags(html, SCRIPT_TAG_PATTERN);
        }

        @Override
        protected void onTag(String attrText, String inner) {
            boolean hasSrc = false;
            if (attrText != null) {
                Matcher srcMatcher = SRC_ATTR_PATTERN.matcher(attrText);
                if (srcMatcher.find()) {
                    String srcValue = srcMatcher.group(2);
                    if (srcValue != null && !srcValue.isEmpty()) {
                        this.cachedSrcs.add(srcValue);
                        hasSrc = true;
                    }
                } else if (attrText.toLowerCase().contains("src")) {
                    ApricityUI.LOGGER.warn("[AUI JS] script src attribute is malformed path={} attributes={}", (Object)AuiLog.source(this.contextPath), (Object)AuiLog.compact(attrText));
                }
            }
            if (inner != null && !inner.isBlank()) {
                this.cachedContents.add(inner.trim());
                if (hasSrc) {
                    ApricityUI.LOGGER.warn("[AUI JS] script has both src and inline code; both will execute path={} src={}", (Object)AuiLog.source(this.contextPath), (Object)AuiLog.compact(attrText));
                }
            }
        }

        @Override
        public void pushToDocument(Document document) {
            if (document == null) {
                ApricityUI.LOGGER.error("[AUI JS] cannot attach scripts without a document path={}", (Object)AuiLog.source(this.contextPath));
                return;
            }
            ResourceUsageIndex.recordJs(this.contextPath, this.cachedSrcs);
            document.JSCache.addAll(this.loadScripts());
        }

        public List<String> loadScripts() {
            ArrayList<String> scripts = new ArrayList<String>();
            for (String src : this.cachedSrcs) {
                String resolvedPath = Loader.resolve(this.contextPath, src);
                if (Loader.isRemotePath(resolvedPath)) {
                    ApricityUI.LOGGER.warn("[AUI JS] remote external script is unsupported; skipped document={} src={}", (Object)AuiLog.source(this.contextPath), (Object)resolvedPath);
                    continue;
                }
                try {
                    InputStream is = ClientLoader.getResourceStream(resolvedPath);
                    try {
                        if (is == null) {
                            ApricityUI.LOGGER.error("[AUI JS] external script resource is missing document={} src={} resolved={}", new Object[]{AuiLog.source(this.contextPath), src, resolvedPath});
                            continue;
                        }
                        String content = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                        if (content.isBlank()) {
                            ApricityUI.LOGGER.warn("[AUI JS] external script is empty resolved={}", (Object)resolvedPath);
                        }
                        scripts.add(content);
                    }
                    finally {
                        if (is == null) continue;
                        is.close();
                    }
                }
                catch (IOException e) {
                    ApricityUI.LOGGER.error("[AUI JS] failed to read external script document={} resolved={}", new Object[]{AuiLog.source(this.contextPath), resolvedPath, e});
                }
            }
            scripts.addAll(this.cachedContents);
            return List.copyOf(scripts);
        }
    }
}

