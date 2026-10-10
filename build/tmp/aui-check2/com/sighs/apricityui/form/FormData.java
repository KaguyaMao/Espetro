/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.form;

import com.sighs.apricityui.form.FormDataEntry;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public final class FormData
implements Iterable<FormDataEntry> {
    private final ArrayList<FormDataEntry> entries = new ArrayList();

    public FormData() {
    }

    public FormData(List<FormDataEntry> initialEntries) {
        if (initialEntries != null) {
            this.entries.addAll(initialEntries);
        }
    }

    public void append(String name, String value) {
        this.entries.add(new FormDataEntry(name == null ? "" : name, value == null ? "" : value));
    }

    public void append(String name, String value, String filename) {
        this.entries.add(new FormDataEntry(name == null ? "" : name, value == null ? "" : value, filename == null ? "" : filename));
    }

    public void delete(String name) {
        String key = name == null ? "" : name;
        this.entries.removeIf(entry -> key.equals(entry.name()));
    }

    public String get(String name) {
        String key = name == null ? "" : name;
        for (FormDataEntry entry : this.entries) {
            if (!key.equals(entry.name())) continue;
            return entry.value();
        }
        return null;
    }

    public List<String> getAll(String name) {
        String key = name == null ? "" : name;
        ArrayList<String> result = new ArrayList<String>();
        for (FormDataEntry entry : this.entries) {
            if (!key.equals(entry.name())) continue;
            result.add(entry.value());
        }
        return Collections.unmodifiableList(result);
    }

    public boolean has(String name) {
        return this.get(name) != null;
    }

    public void set(String name, String value) {
        this.set(name, value, "");
    }

    public void set(String name, String value, String filename) {
        String key = name == null ? "" : name;
        String normalized = value == null ? "" : value;
        String normalizedFilename = filename == null ? "" : filename;
        boolean replaced = false;
        ArrayList<FormDataEntry> next = new ArrayList<FormDataEntry>();
        for (FormDataEntry entry : this.entries) {
            if (!key.equals(entry.name())) {
                next.add(entry);
                continue;
            }
            if (replaced) continue;
            next.add(new FormDataEntry(key, normalized, normalizedFilename));
            replaced = true;
        }
        if (!replaced) {
            next.add(new FormDataEntry(key, normalized, normalizedFilename));
        }
        this.entries.clear();
        this.entries.addAll(next);
    }

    public List<FormDataEntry> getEntries() {
        return Collections.unmodifiableList(this.entries);
    }

    public List<FormDataEntry> entries() {
        return this.getEntries();
    }

    @Override
    public Iterator<FormDataEntry> iterator() {
        return this.getEntries().iterator();
    }

    public List<String> keys() {
        ArrayList<String> result = new ArrayList<String>();
        for (FormDataEntry entry : this.entries) {
            result.add(entry.name());
        }
        return Collections.unmodifiableList(result);
    }

    public List<String> values() {
        ArrayList<String> result = new ArrayList<String>();
        for (FormDataEntry entry : this.entries) {
            result.add(entry.value());
        }
        return Collections.unmodifiableList(result);
    }

    public int getSize() {
        return this.entries.size();
    }

    public String toString() {
        StringBuilder out = new StringBuilder();
        for (FormDataEntry entry : this.entries) {
            if (out.length() > 0) {
                out.append('&');
            }
            out.append(URLEncoder.encode(entry.name(), StandardCharsets.UTF_8));
            out.append('=');
            out.append(URLEncoder.encode(entry.value(), StandardCharsets.UTF_8));
        }
        return out.toString();
    }
}

