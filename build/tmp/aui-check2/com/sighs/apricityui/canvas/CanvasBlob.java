/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.canvas;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class CanvasBlob {
    private final byte[] bytes;
    private final String type;

    public CanvasBlob(byte[] bytes, String type) {
        this.bytes = bytes == null ? new byte[]{} : (byte[])bytes.clone();
        this.type = type == null || type.isBlank() ? "application/octet-stream" : type;
    }

    public int getSize() {
        return this.bytes.length;
    }

    public String getType() {
        return this.type;
    }

    public byte[] arrayBuffer() {
        return (byte[])this.bytes.clone();
    }

    public String text() {
        return new String(this.bytes, StandardCharsets.UTF_8);
    }

    public String toDataURL() {
        return "data:" + this.type + ";base64," + Base64.getEncoder().encodeToString(this.bytes);
    }
}

