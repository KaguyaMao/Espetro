/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.dom;

import com.sighs.apricityui.init.Document;
import com.sighs.apricityui.init.Node;

public interface DocumentExpander {
    public void apply(Document var1);

    default public void validateRuntimeInsertion(Document document, Node parent, Node child) {
    }

    default public void normalizeRuntimeChildren(Document document, Node parent) {
    }

    default public void restoreRequiredContent(Document document, Node parent) {
    }
}

