/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.render;

import com.sighs.apricityui.render.Operation;

public class ClipboardDataBridge {
    public String getData(String type) {
        if (type == null) {
            return null;
        }
        if ("text/plain".equalsIgnoreCase(type)) {
            return Operation.getClipboardText();
        }
        if ("text/html".equalsIgnoreCase(type)) {
            return Operation.getInternalClipboardHtml();
        }
        return null;
    }

    public void setData(String type, String value) {
        if (type == null) {
            return;
        }
        if ("text/plain".equalsIgnoreCase(type)) {
            Operation.setClipboardText(value);
        } else if ("text/html".equalsIgnoreCase(type)) {
            Operation.setInternalClipboardHtml(value);
        }
    }
}

