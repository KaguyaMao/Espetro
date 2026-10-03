/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.dev.devtools;

import com.sighs.apricityui.init.Document;
import com.sighs.apricityui.init.Element;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

final class DevToolsEditHistory {
    private static final int MAX_ENTRIES = 200;
    private final Map<UUID, History> histories = new LinkedHashMap<UUID, History>();

    DevToolsEditHistory() {
    }

    Snapshot snapshot(Element target, Map<String, String> disabledStyles) {
        return new Snapshot(target == null ? Map.of() : new LinkedHashMap<String, String>(target.getAttributes()), (Map<String, String>)(disabledStyles == null ? Map.of() : new LinkedHashMap<String, String>(disabledStyles)));
    }

    void record(Document document, EditAction undo, EditAction redo, String description) {
        if (document == null || undo == null || redo == null) {
            return;
        }
        History history = this.histories.computeIfAbsent(document.getUuid(), ignored -> new History());
        history.undo.push(new Change(undo, redo, description == null ? "Edit" : description));
        while (history.undo.size() > 200) {
            history.undo.removeLast();
        }
        history.redo.clear();
    }

    Applied undo(Document document) {
        History history = this.history(document);
        if (history == null || history.undo.isEmpty()) {
            return null;
        }
        Change change = history.undo.peek();
        if (!change.undo().apply()) {
            return null;
        }
        history.undo.pop();
        history.redo.push(change);
        return new Applied(change.description());
    }

    Applied redo(Document document) {
        History history = this.history(document);
        if (history == null || history.redo.isEmpty()) {
            return null;
        }
        Change change = history.redo.peek();
        if (!change.redo().apply()) {
            return null;
        }
        history.redo.pop();
        history.undo.push(change);
        return new Applied(change.description());
    }

    void clear() {
        this.histories.clear();
    }

    private History history(Document document) {
        return document == null ? null : this.histories.get(document.getUuid());
    }

    record Snapshot(Map<String, String> attributes, Map<String, String> disabledStyles) {
        Snapshot {
            attributes = Map.copyOf(Objects.requireNonNull(attributes));
            disabledStyles = Map.copyOf(Objects.requireNonNull(disabledStyles));
        }
    }

    private static final class History {
        private final Deque<Change> undo = new ArrayDeque<Change>();
        private final Deque<Change> redo = new ArrayDeque<Change>();

        private History() {
        }
    }

    private record Change(EditAction undo, EditAction redo, String description) {
    }

    @FunctionalInterface
    static interface EditAction {
        public boolean apply();
    }

    record Applied(String description) {
    }
}

