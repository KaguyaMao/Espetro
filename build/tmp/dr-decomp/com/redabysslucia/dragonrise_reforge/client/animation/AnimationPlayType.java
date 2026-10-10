/*
 * Decompiled with CFR 0.152.
 */
package com.redabysslucia.dragonrise_reforge.client.animation;

public enum AnimationPlayType {
    PLAY_ONCE_STOP,
    PLAY_ONCE_HOLD,
    LOOP;


    public static AnimationPlayType fromString(String str) {
        if (str == null) {
            return LOOP;
        }
        return switch (str.toLowerCase()) {
            case "play_once_stop" -> PLAY_ONCE_STOP;
            case "play_once_hold" -> PLAY_ONCE_HOLD;
            default -> LOOP;
        };
    }
}

