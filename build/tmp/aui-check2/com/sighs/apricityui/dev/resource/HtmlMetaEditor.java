/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.dev.resource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public final class HtmlMetaEditor {
    private static final Set<String> VOID_ELEMENTS = Set.of("area", "base", "br", "col", "embed", "hr", "img", "input", "link", "meta", "param", "source", "track", "wbr");

    private HtmlMetaEditor() {
    }

    public static LoadResult load(Path path) {
        String error = HtmlMetaEditor.validateTarget(path);
        if (!error.isBlank()) {
            return LoadResult.failure(error);
        }
        try {
            String html = Files.readString(path, StandardCharsets.UTF_8);
            return LoadResult.success(HtmlMetaEditor.extractMetaMarkup(html));
        }
        catch (IOException ignored) {
            return LoadResult.failure("Could not read the HTML file");
        }
    }

    public static EditResult save(Path path, String metaMarkup) {
        String error = HtmlMetaEditor.validateTarget(path);
        if (!error.isBlank()) {
            return EditResult.failure(error);
        }
        if (!HtmlMetaEditor.isValidMetaMarkup(metaMarkup)) {
            return EditResult.failure("META editor accepts only <meta> tags");
        }
        try {
            String html = Files.readString(path, StandardCharsets.UTF_8);
            String updated = HtmlMetaEditor.replaceMetaMarkup(html, metaMarkup);
            Files.writeString(path, (CharSequence)updated, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
            return EditResult.success(path);
        }
        catch (IOException ignored) {
            return EditResult.failure("Could not update the HTML file");
        }
    }

    public static boolean isValidMetaMarkup(String markup) {
        String source = HtmlMetaEditor.normalizeNewlines(markup).trim();
        if (source.isEmpty()) {
            return true;
        }
        List<Tag> tags = HtmlMetaEditor.scanTags(source, 0, source.length());
        if (tags.isEmpty()) {
            return false;
        }
        int cursor = 0;
        for (Tag tag : tags) {
            if (!source.substring(cursor, tag.start()).isBlank()) {
                return false;
            }
            if (tag.closing() || !"meta".equals(tag.name())) {
                return false;
            }
            cursor = tag.end();
        }
        return source.substring(cursor).isBlank();
    }

    public static MetaSettings parseSettings(String markup) {
        String source = markup == null ? "" : markup;
        String charset = "";
        String fontMode = "";
        String viewport = "";
        String mouseEvents = "";
        ArrayList<String> preserved = new ArrayList<String>();
        block10: for (Tag tag : HtmlMetaEditor.scanTags(source, 0, source.length())) {
            if (tag.closing() || !"meta".equals(tag.name())) continue;
            String raw = source.substring(tag.start(), tag.end()).trim();
            String tagCharset = HtmlMetaEditor.attributeValue(source, tag, "charset");
            if (tagCharset != null) {
                if (!charset.isEmpty()) continue;
                charset = tagCharset;
                continue;
            }
            String name = HtmlMetaEditor.attributeValue(source, tag, "name");
            String content = HtmlMetaEditor.attributeValue(source, tag, "content");
            if (name == null) {
                preserved.add(raw);
                continue;
            }
            switch (name.trim().toLowerCase(Locale.ROOT)) {
                case "aui-font-mode": {
                    if (!fontMode.isEmpty()) continue block10;
                    fontMode = HtmlMetaEditor.safe(content);
                    continue block10;
                }
                case "aui-viewport": {
                    if (!viewport.isEmpty()) continue block10;
                    viewport = HtmlMetaEditor.safe(content);
                    continue block10;
                }
                case "aui-mouse-events": {
                    if (!mouseEvents.isEmpty()) continue block10;
                    mouseEvents = HtmlMetaEditor.safe(content);
                    continue block10;
                }
            }
            preserved.add(raw);
        }
        return new MetaSettings(charset, fontMode, viewport, mouseEvents, preserved);
    }

    public static String toMetaMarkup(MetaSettings settings) {
        if (settings == null) {
            return "";
        }
        ArrayList<String> tags = new ArrayList<String>();
        HtmlMetaEditor.appendAttributeMeta(tags, "charset", settings.charset());
        HtmlMetaEditor.appendNamedMeta(tags, "aui-font-mode", settings.fontMode());
        HtmlMetaEditor.appendNamedMeta(tags, "aui-viewport", settings.viewport());
        HtmlMetaEditor.appendNamedMeta(tags, "aui-mouse-events", settings.mouseEvents());
        for (String preserved : settings.preservedMeta()) {
            if (preserved == null || preserved.isBlank()) continue;
            tags.add(preserved.trim());
        }
        return String.join((CharSequence)"\n", tags);
    }

    static String extractMetaMarkup(String html) {
        HeadRange head = HtmlMetaEditor.findHead(html);
        List<Tag> metas = head == null ? HtmlMetaEditor.findImplicitHeadMetas(html) : HtmlMetaEditor.scanTags(html, head.contentStart(), head.contentEnd()).stream().filter(tag -> !tag.closing() && "meta".equals(tag.name())).toList();
        ArrayList<String> tags = new ArrayList<String>();
        for (Tag tag2 : metas) {
            tags.add(html.substring(tag2.start(), tag2.end()).trim());
        }
        return String.join((CharSequence)"\n", tags);
    }

    static String replaceMetaMarkup(String html, String metaMarkup) {
        String source = html == null ? "" : html;
        String replacement = HtmlMetaEditor.normalizeNewlines(metaMarkup).trim();
        if (!HtmlMetaEditor.isValidMetaMarkup(replacement)) {
            throw new IllegalArgumentException("Only meta tags are allowed");
        }
        HeadRange head = HtmlMetaEditor.findHead(source);
        if (head == null) {
            List<Tag> implicitMetas = HtmlMetaEditor.findImplicitHeadMetas(source);
            String withoutImplicitMetas = HtmlMetaEditor.removeTags(source, implicitMetas);
            return HtmlMetaEditor.addHead(withoutImplicitMetas, replacement);
        }
        List<Tag> metas = HtmlMetaEditor.scanTags(source, head.contentStart(), head.contentEnd()).stream().filter(tag -> !tag.closing() && "meta".equals(tag.name())).toList();
        if (metas.isEmpty()) {
            if (replacement.isEmpty()) {
                return source;
            }
            String newline = HtmlMetaEditor.newlineOf(source);
            String indent = HtmlMetaEditor.childIndent(source, head.openStart());
            String block = newline + indent + HtmlMetaEditor.indentLines(replacement, indent, newline) + newline;
            return source.substring(0, head.contentStart()) + block + source.substring(head.contentStart());
        }
        Tag first = metas.get(0);
        String indent = HtmlMetaEditor.lineIndent(source, first.start());
        String newline = HtmlMetaEditor.newlineOf(source);
        StringBuilder updated = new StringBuilder(source.length() + replacement.length());
        updated.append(source, 0, first.start());
        if (!replacement.isEmpty()) {
            updated.append(HtmlMetaEditor.indentLines(replacement, indent, newline));
        }
        int cursor = first.end();
        for (int i = 1; i < metas.size(); ++i) {
            Tag tag2 = metas.get(i);
            updated.append(source, cursor, tag2.start());
            cursor = tag2.end();
        }
        updated.append(source, cursor, source.length());
        return updated.toString();
    }

    private static List<Tag> findImplicitHeadMetas(String html) {
        ArrayList<Tag> metas = new ArrayList<Tag>();
        if (html == null || html.isEmpty()) {
            return metas;
        }
        ArrayDeque<String> ancestors = new ArrayDeque<String>();
        for (Tag tag : HtmlMetaEditor.scanTags(html, 0, html.length())) {
            boolean documentLevel;
            if (tag.closing()) {
                HtmlMetaEditor.popThrough(ancestors, tag.name());
                continue;
            }
            if ("body".equals(tag.name())) break;
            boolean bl = documentLevel = ancestors.isEmpty() || ancestors.size() == 1 && "html".equals(ancestors.peek());
            if (documentLevel && "meta".equals(tag.name())) {
                metas.add(tag);
            }
            if (tag.selfClosing() || VOID_ELEMENTS.contains(tag.name())) continue;
            ancestors.push(tag.name());
        }
        return metas;
    }

    private static void popThrough(Deque<String> ancestors, String name) {
        if (!ancestors.contains(name)) {
            return;
        }
        while (!ancestors.isEmpty()) {
            if (!name.equals(ancestors.pop())) continue;
            return;
        }
    }

    private static String removeTags(String source, List<Tag> tags) {
        if (tags.isEmpty()) {
            return source;
        }
        StringBuilder result = new StringBuilder(source.length());
        int cursor = 0;
        for (Tag tag : tags) {
            result.append(source, cursor, tag.start());
            cursor = tag.end();
        }
        result.append(source, cursor, source.length());
        return result.toString();
    }

    private static String addHead(String html, String replacement) {
        if (replacement.isEmpty()) {
            return html;
        }
        String newline = HtmlMetaEditor.newlineOf(html);
        String block = "<head>" + newline + "    " + HtmlMetaEditor.indentLines(replacement, "    ", newline) + newline + "</head>";
        List<Tag> tags = HtmlMetaEditor.scanTags(html, 0, html.length());
        Tag body = HtmlMetaEditor.firstTag(tags, "body", false);
        if (body != null) {
            return html.substring(0, body.start()) + block + newline + html.substring(body.start());
        }
        Tag root = HtmlMetaEditor.firstTag(tags, "html", false);
        if (root != null) {
            return html.substring(0, root.end()) + newline + block + html.substring(root.end());
        }
        return block + newline + html;
    }

    private static HeadRange findHead(String html) {
        if (html == null || html.isEmpty()) {
            return null;
        }
        List<Tag> tags = HtmlMetaEditor.scanTags(html, 0, html.length());
        Tag open = HtmlMetaEditor.firstTag(tags, "head", false);
        if (open == null) {
            return null;
        }
        for (Tag tag : tags) {
            if (tag.start() < open.end() || !tag.closing() || !"head".equals(tag.name())) continue;
            return new HeadRange(open.start(), open.end(), tag.start());
        }
        return null;
    }

    private static Tag firstTag(List<Tag> tags, String name, boolean closing) {
        for (Tag tag : tags) {
            if (tag.closing() != closing || !name.equals(tag.name())) continue;
            return tag;
        }
        return null;
    }

    private static List<Tag> scanTags(String source, int from, int to) {
        int start;
        ArrayList<Tag> result = new ArrayList<Tag>();
        if (source == null || source.isEmpty()) {
            return result;
        }
        int limit = Math.min(source.length(), Math.max(from, to));
        int cursor = Math.max(0, from);
        while (cursor < limit && (start = source.indexOf(60, cursor)) >= 0 && start < limit) {
            if (source.startsWith("<!--", start)) {
                int commentEnd = source.indexOf("-->", start + 4);
                cursor = commentEnd < 0 ? limit : commentEnd + 3;
                continue;
            }
            int end = HtmlMetaEditor.findTagEnd(source, start + 1, limit);
            if (end < 0) break;
            Tag tag = HtmlMetaEditor.parseTag(source, start, end + 1);
            if (tag != null) {
                int rawClose;
                result.add(tag);
                if (!tag.closing() && ("script".equals(tag.name()) || "style".equals(tag.name())) && (rawClose = HtmlMetaEditor.indexOfIgnoreCase(source, "</" + tag.name(), tag.end(), limit)) >= 0) {
                    cursor = rawClose;
                    continue;
                }
            }
            cursor = end + 1;
        }
        return result;
    }

    private static int findTagEnd(String source, int from, int limit) {
        char quote = '\u0000';
        for (int i = from; i < limit; ++i) {
            char ch = source.charAt(i);
            if (quote != '\u0000') {
                if (ch != quote) continue;
                quote = '\u0000';
                continue;
            }
            if (ch == '\'' || ch == '\"') {
                quote = ch;
                continue;
            }
            if (ch != '>') continue;
            return i;
        }
        return -1;
    }

    private static String attributeValue(String source, Tag tag, String attributeName) {
        int cursor;
        int limit = tag.end() - 1;
        for (cursor = tag.start() + 1; cursor < limit && Character.isWhitespace(source.charAt(cursor)); ++cursor) {
        }
        while (cursor < limit && HtmlMetaEditor.isNameCharacter(source.charAt(cursor))) {
            ++cursor;
        }
        while (cursor < limit) {
            while (cursor < limit && Character.isWhitespace(source.charAt(cursor))) {
                ++cursor;
            }
            if (cursor >= limit || source.charAt(cursor) == '/') break;
            int nameStart = cursor;
            while (cursor < limit && HtmlMetaEditor.isNameCharacter(source.charAt(cursor))) {
                ++cursor;
            }
            if (cursor == nameStart) {
                ++cursor;
                continue;
            }
            String name = source.substring(nameStart, cursor);
            while (cursor < limit && Character.isWhitespace(source.charAt(cursor))) {
                ++cursor;
            }
            String value = "";
            if (cursor < limit && source.charAt(cursor) == '=') {
                ++cursor;
                while (cursor < limit && Character.isWhitespace(source.charAt(cursor))) {
                    ++cursor;
                }
                if (cursor < limit && (source.charAt(cursor) == '\'' || source.charAt(cursor) == '\"')) {
                    char quote = source.charAt(cursor++);
                    int valueStart = cursor;
                    while (cursor < limit && source.charAt(cursor) != quote) {
                        ++cursor;
                    }
                    value = source.substring(valueStart, cursor);
                    if (cursor < limit) {
                        ++cursor;
                    }
                } else {
                    int valueStart = cursor;
                    while (cursor < limit && !Character.isWhitespace(source.charAt(cursor)) && source.charAt(cursor) != '/') {
                        ++cursor;
                    }
                    value = source.substring(valueStart, cursor);
                }
            }
            if (!attributeName.equalsIgnoreCase(name)) continue;
            return HtmlMetaEditor.decodeAttribute(value);
        }
        return null;
    }

    private static Tag parseTag(String source, int start, int end) {
        int slash;
        char ch;
        boolean closing;
        int cursor;
        for (cursor = start + 1; cursor < end && Character.isWhitespace(source.charAt(cursor)); ++cursor) {
        }
        boolean bl = closing = cursor < end && source.charAt(cursor) == '/';
        if (closing) {
            ++cursor;
        }
        while (cursor < end && Character.isWhitespace(source.charAt(cursor))) {
            ++cursor;
        }
        int nameStart = cursor;
        while (cursor < end && (Character.isLetterOrDigit(ch = source.charAt(cursor)) || ch == '-' || ch == ':')) {
            ++cursor;
        }
        if (cursor == nameStart) {
            return null;
        }
        for (slash = end - 2; slash > cursor && Character.isWhitespace(source.charAt(slash)); --slash) {
        }
        boolean selfClosing = !closing && slash >= cursor && source.charAt(slash) == '/';
        return new Tag(start, end, source.substring(nameStart, cursor).toLowerCase(Locale.ROOT), closing, selfClosing);
    }

    private static boolean isNameCharacter(char ch) {
        return Character.isLetterOrDigit(ch) || ch == '-' || ch == ':' || ch == '_';
    }

    private static void appendAttributeMeta(List<String> tags, String name, String value) {
        if (value == null || value.isBlank()) {
            return;
        }
        tags.add("<meta " + name + "=\"" + HtmlMetaEditor.encodeAttribute(value.trim()) + "\">");
    }

    private static void appendNamedMeta(List<String> tags, String name, String content) {
        if (content == null || content.isBlank()) {
            return;
        }
        tags.add("<meta name=\"" + name + "\" content=\"" + HtmlMetaEditor.encodeAttribute(content.trim()) + "\">");
    }

    private static String encodeAttribute(String value) {
        return value.replace("&", "&amp;").replace("\"", "&quot;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private static String decodeAttribute(String value) {
        return HtmlMetaEditor.safe(value).replace("&quot;", "\"").replace("&apos;", "'").replace("&lt;", "<").replace("&gt;", ">").replace("&amp;", "&");
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }

    private static int indexOfIgnoreCase(String source, String target, int from, int limit) {
        int max = Math.min(source.length(), limit) - target.length();
        for (int i = Math.max(0, from); i <= max; ++i) {
            if (!source.regionMatches(true, i, target, 0, target.length())) continue;
            return i;
        }
        return -1;
    }

    private static String childIndent(String source, int parentStart) {
        return HtmlMetaEditor.lineIndent(source, parentStart) + "    ";
    }

    private static String lineIndent(String source, int position) {
        int lineStart;
        char ch;
        int cursor;
        for (cursor = lineStart = Math.max(source.lastIndexOf(10, Math.max(0, position - 1)) + 1, 0); cursor < position && ((ch = source.charAt(cursor)) == ' ' || ch == '\t'); ++cursor) {
        }
        return source.substring(lineStart, cursor);
    }

    private static String indentLines(String value, String indent, String newline) {
        return HtmlMetaEditor.normalizeNewlines(value).replace("\n", newline + indent);
    }

    private static String normalizeNewlines(String value) {
        return value == null ? "" : value.replace("\r\n", "\n").replace('\r', '\n');
    }

    private static String newlineOf(String html) {
        return html != null && html.contains("\r\n") ? "\r\n" : "\n";
    }

    private static String validateTarget(Path path) {
        if (path == null || !Files.isRegularFile(path, new LinkOption[0])) {
            return "HTML file is unavailable";
        }
        Path name = path.getFileName();
        if (name == null || !name.toString().toLowerCase(Locale.ROOT).endsWith(".html")) {
            return "META editor supports HTML files only";
        }
        return "";
    }

    public record LoadResult(boolean success, String metaMarkup, String message) {
        private static LoadResult success(String markup) {
            return new LoadResult(true, markup, "");
        }

        private static LoadResult failure(String message) {
            return new LoadResult(false, "", message);
        }
    }

    public record EditResult(boolean success, Path target, String message) {
        private static EditResult success(Path target) {
            return new EditResult(true, target, "");
        }

        private static EditResult failure(String message) {
            return new EditResult(false, null, message);
        }
    }

    private record Tag(int start, int end, String name, boolean closing, boolean selfClosing) {
    }

    public record MetaSettings(String charset, String fontMode, String viewport, String mouseEvents, List<String> preservedMeta) {
        public MetaSettings {
            charset = HtmlMetaEditor.safe(charset);
            fontMode = HtmlMetaEditor.safe(fontMode);
            viewport = HtmlMetaEditor.safe(viewport);
            mouseEvents = HtmlMetaEditor.safe(mouseEvents);
            preservedMeta = preservedMeta == null ? List.of() : List.copyOf(preservedMeta);
        }
    }

    private record HeadRange(int openStart, int contentStart, int contentEnd) {
    }
}

