/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.team;

public final class ClassAttributeBonusPolicy {
    private static final double MAX_HEALTH_BONUS = 1024.0;
    private static final double MAX_SPEED_BONUS = 4.0;

    private ClassAttributeBonusPolicy() {
    }

    public static double healthAmount(int configuredBonus) {
        return Math.max(-1024.0, Math.min(1024.0, (double)configuredBonus));
    }

    public static double speedMultiplier(float configuredBonus) {
        if (!Float.isFinite(configuredBonus)) {
            return 0.0;
        }
        return Math.max(-0.95, Math.min(4.0, (double)configuredBonus));
    }

    public static float clampCurrentHealth(float currentHealth, float newMaximum) {
        if (!Float.isFinite(currentHealth) || !Float.isFinite(newMaximum) || newMaximum <= 0.0f) {
            return 0.0f;
        }
        return Math.max(0.0f, Math.min(currentHealth, newMaximum));
    }
}

