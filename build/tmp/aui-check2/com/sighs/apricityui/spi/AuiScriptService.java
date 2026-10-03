/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.spi;

import com.sighs.apricityui.event.Event;

public interface AuiScriptService {
    public void eval(String var1, Event var2, String var3);

    default public void evalGlobal(String code, String documentUuid) {
        if (code == null) {
            return;
        }
        this.eval(code.replace("__AUI_DOCUMENT_UUID__", documentUuid == null ? "" : documentUuid), null, "global.js");
    }

    public void reload();

    default public void warmUp() {
    }
}

