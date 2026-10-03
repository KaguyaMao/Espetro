/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.ParseResults
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraftforge.eventbus.api.Cancelable
 *  net.minecraftforge.eventbus.api.Event
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.event;

import com.mojang.brigadier.ParseResults;
import net.minecraft.commands.CommandSourceStack;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.Nullable;

@Cancelable
public class CommandEvent
extends Event {
    private ParseResults<CommandSourceStack> parse;
    @Nullable
    private Throwable exception;

    public CommandEvent(ParseResults<CommandSourceStack> parse) {
        this.parse = parse;
    }

    public ParseResults<CommandSourceStack> getParseResults() {
        return this.parse;
    }

    public void setParseResults(ParseResults<CommandSourceStack> parse) {
        this.parse = parse;
    }

    @Nullable
    public Throwable getException() {
        return this.exception;
    }

    public void setException(@Nullable Throwable exception) {
        this.exception = exception;
    }
}

