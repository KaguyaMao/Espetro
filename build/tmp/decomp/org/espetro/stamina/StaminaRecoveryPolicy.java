/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.stamina;

final class StaminaRecoveryPolicy {
    private StaminaRecoveryPolicy() {
    }

    static int effectiveDelaySeconds(int configuredDelaySeconds, int fullRecoverySeconds) {
        int configured = Math.max(0, configuredDelaySeconds);
        if (fullRecoverySeconds <= 0) {
            return configured;
        }
        return Math.min(configured, Math.max(0, fullRecoverySeconds - 1));
    }

    static int restorePerSecond(int maxStamina, int configuredRestorePerSecond, int configuredDelaySeconds, int fullRecoverySeconds) {
        int configured = Math.max(0, configuredRestorePerSecond);
        if (maxStamina <= 0 || fullRecoverySeconds <= 0) {
            return configured;
        }
        int delay = StaminaRecoveryPolicy.effectiveDelaySeconds(configuredDelaySeconds, fullRecoverySeconds);
        int recoveryEvents = Math.max(1, fullRecoverySeconds - delay + 1);
        int required = (int)Math.min(Integer.MAX_VALUE, ((long)maxStamina + (long)recoveryEvents - 1L) / (long)recoveryEvents);
        return Math.max(configured, required);
    }
}

