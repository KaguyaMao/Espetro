/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.eventbus.api.Event
 */
package org.espetro.api.event;

import net.minecraftforge.eventbus.api.Event;
import org.espetro.team.GamePhase;

public final class GamePhaseChangedEvent
extends Event {
    private final GamePhase previous;
    private final GamePhase current;

    public GamePhaseChangedEvent(GamePhase previous, GamePhase current) {
        this.previous = previous;
        this.current = current;
    }

    public GamePhase previous() {
        return this.previous;
    }

    public GamePhase current() {
        return this.current;
    }
}

