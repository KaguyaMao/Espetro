/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.editor.ore.model;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public final class OreDocumentMetadata {
    private final Map<String, String> htmlAttributes = new LinkedHashMap<String, String>();
    private final Map<String, String> bodyAttributes = new LinkedHashMap<String, String>();
    private String doctype = "<!DOCTYPE html>";
    private String headContent = "";
    private String bodyScriptContent = "";

    public Map<String, String> htmlAttributes() {
        return Collections.unmodifiableMap(new LinkedHashMap<String, String>(this.htmlAttributes));
    }

    public Map<String, String> bodyAttributes() {
        return Collections.unmodifiableMap(new LinkedHashMap<String, String>(this.bodyAttributes));
    }

    public String doctype() {
        return this.doctype;
    }

    public String headContent() {
        return this.headContent;
    }

    public String bodyScriptContent() {
        return this.bodyScriptContent;
    }

    public void setHtmlAttribute(String name, String value) {
        OreDocumentMetadata.put(this.htmlAttributes, name, value);
    }

    public void setBodyAttribute(String name, String value) {
        OreDocumentMetadata.put(this.bodyAttributes, name, value);
    }

    public void setDoctype(String doctype) {
        if (doctype != null && doctype.matches("(?is)\\s*<!doctype\\s+[^>]+>\\s*")) {
            this.doctype = doctype.trim();
        }
    }

    public void setHeadContent(String headContent) {
        this.headContent = headContent == null ? "" : headContent.trim();
    }

    public void setBodyScriptContent(String bodyScriptContent) {
        this.bodyScriptContent = bodyScriptContent == null ? "" : bodyScriptContent.trim();
    }

    private static void put(Map<String, String> target, String name, String value) {
        if (name == null || !name.matches("[A-Za-z_:][A-Za-z0-9:_.-]*")) {
            return;
        }
        target.put(name.toLowerCase(Locale.ROOT), value == null ? "" : value);
    }
}

