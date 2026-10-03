/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.editor.ore;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

final class OreEditorHistory {
    private static final int LIMIT = 100;
    private final List<Command> commands = new ArrayList<Command>();
    private int cursor;
    private int savedCursor;
    private String activeMergeKey;

    OreEditorHistory() {
    }

    void reset() {
        this.commands.clear();
        this.cursor = 0;
        this.savedCursor = 0;
        this.activeMergeKey = null;
    }

    void beginMerge(String key) {
        this.activeMergeKey = key == null || key.isBlank() ? null : key;
    }

    void endMerge() {
        this.activeMergeKey = null;
    }

    String activeMergeKey() {
        return this.activeMergeKey;
    }

    boolean canUndo() {
        return this.cursor > 0;
    }

    boolean canRedo() {
        return this.cursor < this.commands.size();
    }

    void markSaved() {
        this.savedCursor = this.cursor;
    }

    boolean isAtSavedRevision() {
        return this.savedCursor >= 0 && this.cursor == this.savedCursor;
    }

    void recordExecuted(Command command) {
        Command merged;
        Command previous;
        if (command == null) {
            return;
        }
        if (this.activeMergeKey != null && this.cursor > 0 && this.cursor == this.commands.size() && this.cursor != this.savedCursor && this.activeMergeKey.equals((previous = this.commands.get(this.cursor - 1)).mergeKey()) && (merged = previous.merge(command)) != null) {
            this.commands.set(this.cursor - 1, merged);
            return;
        }
        while (this.commands.size() > this.cursor) {
            this.commands.remove(this.commands.size() - 1);
        }
        if (this.savedCursor > this.cursor) {
            this.savedCursor = -1;
        }
        this.commands.add(command);
        if (this.commands.size() > 100) {
            this.commands.remove(0);
            this.savedCursor = this.savedCursor <= 0 ? -1 : this.savedCursor - 1;
        } else {
            ++this.cursor;
        }
        if (this.commands.size() == 100) {
            this.cursor = this.commands.size();
        }
    }

    Result undo() {
        this.activeMergeKey = null;
        if (!this.canUndo()) {
            return new Result(false, null);
        }
        Command command = this.commands.get(--this.cursor);
        command.undo();
        return new Result(true, command.undoSelection());
    }

    Result redo() {
        this.activeMergeKey = null;
        if (!this.canRedo()) {
            return new Result(false, null);
        }
        Command command = this.commands.get(this.cursor++);
        command.redo();
        return new Result(true, command.redoSelection());
    }

    static Command action(String type, UUID undoSelection, UUID redoSelection, Runnable undo, Runnable redo) {
        return OreEditorHistory.action(type, null, undoSelection, redoSelection, undo, redo);
    }

    static Command action(String type, String mergeKey, UUID undoSelection, UUID redoSelection, Runnable undo, Runnable redo) {
        return new ActionCommand(type, mergeKey, undoSelection, redoSelection, undo, redo);
    }

    static Command stringValue(String type, String mergeKey, UUID undoSelection, UUID redoSelection, String before, String after, Consumer<String> setter) {
        return new StringValueCommand(type, mergeKey, undoSelection, redoSelection, before, after, setter);
    }

    static Command booleanValue(String type, UUID undoSelection, UUID redoSelection, boolean before, boolean after, Consumer<Boolean> setter) {
        return OreEditorHistory.action(type, undoSelection, redoSelection, () -> setter.accept(before), () -> setter.accept(after));
    }

    static interface Command {
        public String type();

        public UUID undoSelection();

        public UUID redoSelection();

        public void undo();

        public void redo();

        default public String mergeKey() {
            return null;
        }

        default public Command merge(Command next) {
            return null;
        }
    }

    record Result(boolean changed, UUID selection) {
    }

    private record ActionCommand(String type, String mergeKey, UUID undoSelection, UUID redoSelection, Runnable undoAction, Runnable redoAction) implements Command
    {
        @Override
        public void undo() {
            this.undoAction.run();
        }

        @Override
        public void redo() {
            this.redoAction.run();
        }

        @Override
        public Command merge(Command next) {
            ActionCommand action;
            block3: {
                block2: {
                    if (!(next instanceof ActionCommand)) break block2;
                    action = (ActionCommand)next;
                    if (this.mergeKey != null && this.mergeKey.equals(action.mergeKey) && this.type.equals(action.type)) break block3;
                }
                return null;
            }
            return new ActionCommand(this.type, this.mergeKey, this.undoSelection, action.redoSelection, this.undoAction, action.redoAction);
        }
    }

    private record StringValueCommand(String type, String mergeKey, UUID undoSelection, UUID redoSelection, String before, String after, Consumer<String> setter) implements Command
    {
        @Override
        public void undo() {
            this.setter.accept(this.before);
        }

        @Override
        public void redo() {
            this.setter.accept(this.after);
        }

        @Override
        public Command merge(Command next) {
            StringValueCommand value;
            block3: {
                block2: {
                    if (!(next instanceof StringValueCommand)) break block2;
                    value = (StringValueCommand)next;
                    if (this.mergeKey.equals(value.mergeKey) && this.type.equals(value.type)) break block3;
                }
                return null;
            }
            return new StringValueCommand(this.type, this.mergeKey, this.undoSelection, value.redoSelection, this.before, value.after, this.setter);
        }
    }
}

