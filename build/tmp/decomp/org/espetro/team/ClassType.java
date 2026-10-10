/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.team;

import java.util.Arrays;

public enum ClassType {
    RIFLEMAN("\u6b65\u67aa\u5175", "\u6807\u51c6\u7a81\u51fb\u6b65\u67aa\u914d\u7f6e", "ATTACK"),
    MEDIC("\u533b\u7597\u5175", "\u914d\u5907\u533b\u7597\u7528\u54c1", "ATTACK"),
    HEAVY("\u91cd\u706b\u529b", "\u8f7b\u673a\u67aa\u624b", "ATTACK"),
    RECON("\u4fa6\u5bdf\u5175", "\u72d9\u51fb\u624b\u914d\u7f6e", "ATTACK"),
    SUPPORT("\u652f\u63f4\u5175", "\u5de5\u7a0b\u5e08\u914d\u7f6e", "ATTACK"),
    DEFENDER("\u9632\u5fa1\u8005", "\u6807\u51c6\u9632\u5b88\u914d\u7f6e", "DEFEND"),
    GUARD("\u5b88\u536b", "\u8fd1\u6218\u9632\u5b88", "DEFEND"),
    SNIPER("\u72d9\u51fb\u624b", "\u8fdc\u7a0b\u72d9\u51fb", "DEFEND"),
    ENGINEER("\u5de5\u7a0b\u5e08", "\u5de5\u4e8b\u5efa\u9020", "DEFEND"),
    MEDIC_DEFEND("\u6218\u5730\u533b\u62a4", "\u9632\u5b88\u533b\u7597", "DEFEND");

    private final String defaultDisplayName;
    private final String defaultDescription;
    private final String team;

    private ClassType(String displayName, String description, String team) {
        this.defaultDisplayName = displayName;
        this.defaultDescription = description;
        this.team = team;
    }

    public String getDisplayName() {
        return this.defaultDisplayName;
    }

    public String getDescription() {
        return this.defaultDescription;
    }

    public String getTeam() {
        return this.team;
    }

    public static ClassType[] getClassesForTeam(String team) {
        return (ClassType[])Arrays.stream(ClassType.values()).filter(c -> c.team.equals(team)).toArray(ClassType[]::new);
    }

    public static ClassType fromName(String name) {
        try {
            return ClassType.valueOf(name);
        }
        catch (IllegalArgumentException e) {
            return null;
        }
    }
}

