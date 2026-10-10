/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.event;

import dev.latvian.mods.kubejs.event.EventExit;
import dev.latvian.mods.kubejs.event.EventResult;
import dev.latvian.mods.kubejs.typings.Info;
import org.jetbrains.annotations.Nullable;

public class EventJS {
    @Nullable
    protected Object defaultExitValue() {
        return null;
    }

    @Nullable
    protected Object mapExitValue(@Nullable Object value) {
        return value;
    }

    @Info(value="Cancels the event with default exit value. Execution will be stopped **immediately**.\n\n`cancel` denotes a `false` outcome.\n")
    public Object cancel() throws EventExit {
        return this.cancel(this.defaultExitValue());
    }

    @Info(value="Stops the event with default exit value. Execution will be stopped **immediately**.\n\n`success` denotes a `true` outcome.\n")
    public Object success() throws EventExit {
        return this.success(this.defaultExitValue());
    }

    @Info(value="Stops the event with default exit value. Execution will be stopped **immediately**.\n\n`exit` denotes a `default` outcome.\n")
    public Object exit() throws EventExit {
        return this.exit(this.defaultExitValue());
    }

    @Info(value="Cancels the event with the given exit value. Execution will be stopped **immediately**.\n\n`cancel` denotes a `false` outcome.\n")
    public Object cancel(@Nullable Object value) throws EventExit {
        throw EventResult.Type.INTERRUPT_FALSE.exit(this.mapExitValue(value));
    }

    @Info(value="Stops the event with the given exit value. Execution will be stopped **immediately**.\n\n`success` denotes a `true` outcome.\n")
    public Object success(@Nullable Object value) throws EventExit {
        throw EventResult.Type.INTERRUPT_TRUE.exit(this.mapExitValue(value));
    }

    @Info(value="Stops the event with the given exit value. Execution will be stopped **immediately**.\n\n`exit` denotes a `default` outcome.\n")
    public Object exit(@Nullable Object value) throws EventExit {
        throw EventResult.Type.INTERRUPT_DEFAULT.exit(this.mapExitValue(value));
    }

    protected void afterPosted(EventResult result) {
    }
}

