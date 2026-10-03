/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.dom;

import com.sighs.apricityui.init.Document;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public final class MutationObserverManager {
    private final Document document;
    private final CopyOnWriteArrayList<Document.MutationObserver> observers = new CopyOnWriteArrayList();

    public MutationObserverManager(Document document) {
        this.document = document;
    }

    public Document.MutationObserver create(Consumer<Object> callback) {
        Document.MutationObserver observer = new Document.MutationObserver(this.document, callback);
        if (this.document.isActive()) {
            this.observers.add(observer);
        } else {
            observer.disconnect();
        }
        return observer;
    }

    public void remove(Document.MutationObserver observer) {
        this.observers.remove(observer);
    }

    public void queue(Document.MutationRecord record) {
        if (record == null || !this.document.isActive()) {
            return;
        }
        if (record.target != null) {
            record.target.invalidateSubtreeMutationVersion();
        }
        for (Document.MutationObserver observer : this.observers) {
            if (observer == null) continue;
            observer.enqueue(record);
        }
    }

    public void flush() {
        if (!this.document.isActive()) {
            return;
        }
        for (Document.MutationObserver observer : this.observers) {
            if (observer == null) continue;
            observer.flush();
            if (!observer.disconnected) continue;
            this.observers.remove(observer);
        }
    }

    public void clearAll() {
        for (Document.MutationObserver observer : this.observers) {
            if (observer == null) continue;
            observer.disconnect();
        }
        this.observers.clear();
    }
}

