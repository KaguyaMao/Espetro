/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.element;

import com.sighs.apricityui.ApricityUI;
import com.sighs.apricityui.init.Document;
import com.sighs.apricityui.init.Element;
import com.sighs.apricityui.registry.annotation.ElementRegister;
import com.sighs.apricityui.spi.AuiServices;
import java.net.URI;

@ElementRegister(value="A")
public class A
extends Element {
    public static final String TAG_NAME = "A";

    public A(Document document) {
        super(document, TAG_NAME);
        this.addEventListener("mouseup", event -> {
            String href = this.getAttribute("href");
            if (href == null || href.isBlank()) {
                return;
            }
            try {
                AuiServices.client().openUri(new URI(href.trim()));
            }
            catch (Exception e) {
                ApricityUI.LOGGER.warn("Failed to open href: {}", (Object)href, (Object)e);
            }
        });
    }
}

