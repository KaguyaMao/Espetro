/*
 * Decompiled with CFR 0.152.
 */
package frontline.combat.fcp.effects;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

final class MuzzleBurstTracker {
    private static final Map<Integer, State> STATES = new ConcurrentHashMap<Integer, State>();
    private static final int GAP_RESET_TICKS = 18;
    private static final int SUSTAINED_SHOTS = 3;

    private MuzzleBurstTracker() {
    }

    static int recordShot(int vehicleId, long gameTick) {
        State state = STATES.computeIfAbsent(vehicleId, id -> new State());
        state.onShot(gameTick);
        return state.count;
    }

    static boolean isSustained(int vehicleId, long gameTick) {
        State state = STATES.get(vehicleId);
        return state != null && state.isSustained(gameTick);
    }

    static void clear(int vehicleId) {
        STATES.remove(vehicleId);
    }

    private static final class State {
        private long lastTick;
        private int count;

        private State() {
        }

        private void onShot(long tick) {
            if (tick - this.lastTick > 18L) {
                this.count = 0;
            }
            ++this.count;
            this.lastTick = tick;
        }

        private boolean isSustained(long tick) {
            return this.count > 3 && tick - this.lastTick <= 2L;
        }
    }
}

