/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.bastion;

public final class FortificationProgressPolicy {
    private FortificationProgressPolicy() {
    }

    public static int stage(int progress, int required) {
        if (progress <= 0 || required <= 0) {
            return 0;
        }
        return Math.min(6, (progress * 6 + required - 1) / required);
    }

    public static int damagePerPart(int required, int parts) {
        int safeRequired = Math.max(1, required);
        int safeParts = Math.max(1, parts);
        return (safeRequired + safeParts - 1) / safeParts;
    }

    public static int appliedDamage(int maximum, int parts, double reduction) {
        int base = FortificationProgressPolicy.damagePerPart(maximum, parts);
        if (reduction >= 1.0) {
            return 0;
        }
        return Math.max(1, (int)Math.ceil((double)base * (1.0 - reduction)));
    }

    public static int oncePerPartTotal(int maximum, int parts, double reduction) {
        return FortificationProgressPolicy.appliedDamage(maximum, parts, reduction) * Math.max(1, parts);
    }

    public static boolean oncePerPartDestroys(int maximum, int parts, double reduction) {
        return FortificationProgressPolicy.oncePerPartTotal(maximum, parts, reduction) >= Math.max(1, maximum);
    }

    public static int desiredPresentParts(int progress, int required, int parts) {
        if (progress <= 0 || required <= 0 || parts <= 0) {
            return 0;
        }
        return Math.min(parts, (progress * parts + required - 1) / required);
    }
}

