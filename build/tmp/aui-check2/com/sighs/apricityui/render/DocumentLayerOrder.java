/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.render;

import com.sighs.apricityui.init.Document;
import com.sighs.apricityui.init.Element;
import com.sighs.apricityui.layout.Position;
import com.sighs.apricityui.style.Transform;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public final class DocumentLayerOrder {
    private DocumentLayerOrder() {
    }

    public static List<Document> backToFront(Collection<Document> documents) {
        List<Document> ordered = DocumentLayerOrder.copyNonNull(documents);
        ordered.sort(Comparator.comparingDouble(DocumentLayerOrder::translateZ));
        return ordered;
    }

    public static List<Document> frontToBack(Collection<Document> documents) {
        List<Document> ordered = DocumentLayerOrder.backToFront(documents);
        Collections.reverse(ordered);
        return ordered;
    }

    public static boolean hasPersistentScreenDocumentAt(Collection<Document> documents, Document excludedDocument, Position screenPosition) {
        if (documents == null || screenPosition == null) {
            return false;
        }
        for (Document document : DocumentLayerOrder.frontToBack(documents)) {
            if (document == excludedDocument || document.inWorld || document.isManuallyRendered() || !document.isReloadPersistent() || !document.interceptsMouseEventsAt(screenPosition)) continue;
            return true;
        }
        return false;
    }

    static double translateZ(Document document) {
        if (document == null) {
            return 0.0;
        }
        try (Document.ContextScope ignored = Document.withContext(document);){
            double d = DocumentLayerOrder.elementTranslateZ(document.documentElement) + DocumentLayerOrder.elementTranslateZ(document.body);
            return d;
        }
    }

    private static double elementTranslateZ(Element element) {
        if (element == null) {
            return 0.0;
        }
        double value = Transform.getTranslateZ(element.getComputedStyle().transform);
        return Double.isFinite(value) ? value : 0.0;
    }

    private static List<Document> copyNonNull(Collection<Document> documents) {
        ArrayList<Document> result = new ArrayList<Document>();
        if (documents == null) {
            return result;
        }
        for (Document document : documents) {
            if (document == null) continue;
            result.add(document);
        }
        return result;
    }
}

