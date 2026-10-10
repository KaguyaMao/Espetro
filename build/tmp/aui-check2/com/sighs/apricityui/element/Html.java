/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.element;

import com.sighs.apricityui.element.Div;
import com.sighs.apricityui.init.Document;
import com.sighs.apricityui.registry.annotation.ElementRegister;

@ElementRegister(value="HTML")
public class Html
extends Div {
    public static final String TAG_NAME = "HTML";

    public Html(Document document) {
        super(document);
        this.tagName = TAG_NAME;
    }
}

