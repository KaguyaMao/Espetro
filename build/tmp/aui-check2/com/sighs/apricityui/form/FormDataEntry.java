/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.form;

public record FormDataEntry(String name, String value, String filename) {
    public FormDataEntry(String name, String value) {
        this(name, value, "");
    }

    public String getName() {
        return this.name;
    }

    public String getValue() {
        return this.value;
    }

    public String getFilename() {
        return this.filename;
    }
}

