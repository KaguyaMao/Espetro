/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.eventbus.api.Event
 */
package org.espetro.api.event;

import net.minecraftforge.eventbus.api.Event;
import org.espetro.api.ActiveBattlefieldSnapshot;

public abstract class BattlefieldLifecycleEvent
extends Event {
    private final ActiveBattlefieldSnapshot snapshot;

    protected BattlefieldLifecycleEvent(ActiveBattlefieldSnapshot snapshot) {
        this.snapshot = snapshot;
    }

    public ActiveBattlefieldSnapshot snapshot() {
        return this.snapshot;
    }

    public static final class Cleared
    extends BattlefieldLifecycleEvent {
        public Cleared(ActiveBattlefieldSnapshot snapshot) {
            super(snapshot);
        }
    }

    public static final class Activated
    extends BattlefieldLifecycleEvent {
        public Activated(ActiveBattlefieldSnapshot snapshot) {
            super(snapshot);
        }
    }
}

