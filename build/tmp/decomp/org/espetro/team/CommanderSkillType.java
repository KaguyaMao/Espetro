/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.team;

public enum CommanderSkillType {
    DRONE_DETECTION("drone_detection", "\u65e0\u4eba\u673a\u4fa6\u6d4b", "\u91ca\u653e\u65e0\u4eba\u673a\uff0c\u4f7f\u5468\u56f4\u4e00\u5b9a\u8303\u56f4\u5185\u7684\u654c\u65b9\u73a9\u5bb6\u9ad8\u4eae\u663e\u793a"),
    VEHICLE_SUPPLY_STATION("vehicle_supply_station", "\u8f7d\u5177\u8865\u7ed9\u7ad9", "\u5728\u5f53\u524d\u4f4d\u7f6e\u653e\u7f6e\u53ef\u914d\u7f6e\u7684\u8f7d\u5177\u8865\u7ed9\u7269\u8d44"),
    ARTILLERY_155("artillery_155", "155\u706b\u70ae\u652f\u63f4", "\u6253\u5f00\u6218\u672f\u5730\u56fe\u9009\u62e9\u70ae\u51fb\u5750\u6807\uff0c\u4ea4\u7531 KubeJS \u56de\u8c03\u6267\u884c\u706b\u529b\u6548\u679c");

    private final String id;
    private final String displayName;
    private final String description;

    private CommanderSkillType(String id, String displayName, String description) {
        this.id = id;
        this.displayName = displayName;
        this.description = description;
    }

    public String getId() {
        return this.id;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public String getDescription() {
        return this.description;
    }

    public static CommanderSkillType fromId(String id) {
        for (CommanderSkillType type : CommanderSkillType.values()) {
            if (!type.id.equals(id)) continue;
            return type;
        }
        return null;
    }
}

