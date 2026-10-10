/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.element;

import com.sighs.apricityui.init.Document;
import com.sighs.apricityui.init.Element;
import com.sighs.apricityui.registry.annotation.ElementRegister;

@ElementRegister(value="SPAN")
public class Span
extends Element {
    public static final String TAG_NAME = "SPAN";

    public Span(Document document) {
        super(document, TAG_NAME);
    }

    @Override
    public String toString() {
        return super.toString() + "(" + this.innerText + ")";
    }
}

