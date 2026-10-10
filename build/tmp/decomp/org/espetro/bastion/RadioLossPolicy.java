/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.bastion;

public final class RadioLossPolicy {
    private RadioLossPolicy() {
    }

    public static boolean explosionScoresRadioLoss(boolean radioCoreInAffectedBlocks, boolean indexedAsFortification) {
        return radioCoreInAffectedBlocks;
    }

    public static boolean deductManpower(boolean isRadio, boolean friendlyShovelDismantle, boolean silentMatchEnd) {
        return isRadio && !friendlyShovelDismantle && !silentMatchEnd;
    }
}

