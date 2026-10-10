/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.audio;

import java.util.Locale;

public final class AudioCuePolicy {
    public static final double EASTER_EGG_CHANCE = 0.1;

    private AudioCuePolicy() {
    }

    public static String normalizeTeam(String team) {
        if (team == null) {
            return null;
        }
        return switch (team.trim().toUpperCase(Locale.ROOT)) {
            case "ATTACK", "ESPETRO_ATTACK" -> "ATTACK";
            case "DEFEND", "ESPETRO_DEFEND" -> "DEFEND";
            default -> null;
        };
    }

    public static String opposingTeam(String team) {
        String normalized = AudioCuePolicy.normalizeTeam(team);
        if ("ATTACK".equals(normalized)) {
            return "DEFEND";
        }
        if ("DEFEND".equals(normalized)) {
            return "ATTACK";
        }
        return null;
    }

    public static String resolveNeutralizingTeam(String originalOwnerTeam, String activeAttackingTeam) {
        String owner = AudioCuePolicy.normalizeTeam(originalOwnerTeam);
        if (owner == null) {
            return null;
        }
        String attacker = AudioCuePolicy.normalizeTeam(activeAttackingTeam);
        return attacker != null && !attacker.equals(owner) ? attacker : AudioCuePolicy.opposingTeam(owner);
    }

    public static boolean useEasterEgg(double roll) {
        return Double.isFinite(roll) && roll >= 0.0 && roll < 0.1;
    }
}

