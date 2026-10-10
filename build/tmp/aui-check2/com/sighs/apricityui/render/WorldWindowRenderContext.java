/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.render;

import com.sighs.apricityui.world.WorldWindowDisplayPrecision;
import java.util.ArrayDeque;

public final class WorldWindowRenderContext {
    private static final ThreadLocal<ArrayDeque<WorldWindowDisplayPrecision>> STACK = ThreadLocal.withInitial(ArrayDeque::new);

    private WorldWindowRenderContext() {
    }

    public static Scope push(WorldWindowDisplayPrecision precision) {
        ArrayDeque<WorldWindowDisplayPrecision> stack = STACK.get();
        stack.push(precision == null ? WorldWindowDisplayPrecision.FULL : precision);
        return new Scope(stack);
    }

    public static WorldWindowDisplayPrecision current() {
        ArrayDeque<WorldWindowDisplayPrecision> stack = STACK.get();
        return stack.isEmpty() ? WorldWindowDisplayPrecision.FULL : stack.peek();
    }

    public static boolean isWorldWindowRender() {
        return !STACK.get().isEmpty();
    }

    public static boolean shouldRenderEffects() {
        return WorldWindowRenderContext.current() == WorldWindowDisplayPrecision.FULL;
    }

    public static boolean shouldRenderContent() {
        return WorldWindowRenderContext.current() != WorldWindowDisplayPrecision.MINIMAL;
    }

    public static boolean shouldRenderBackgroundDetails() {
        return WorldWindowRenderContext.current() != WorldWindowDisplayPrecision.MINIMAL;
    }

    public static final class Scope
    implements AutoCloseable {
        private final ArrayDeque<WorldWindowDisplayPrecision> stack;
        private boolean closed;

        private Scope(ArrayDeque<WorldWindowDisplayPrecision> stack) {
            this.stack = stack;
        }

        @Override
        public void close() {
            if (this.closed) {
                return;
            }
            this.closed = true;
            if (!this.stack.isEmpty()) {
                this.stack.pop();
            }
        }
    }
}

