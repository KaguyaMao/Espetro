/*
 * Decompiled with CFR 0.152.
 */
package com.example.espoints.tactical;

import java.util.Arrays;

public enum TacticalMarkerType {
    ENEMY_INFANTRY("\u654c\u65b9\u6b65\u5175", -2076078),
    ENEMY_TANK("\u654c\u65b9\u5766\u514b", -2606534),
    ENEMY_IFV("\u654c\u65b9\u6b65\u6218", -1676734),
    ENEMY_LIGHT_VEHICLE("\u654c\u65b9\u8f7b\u578b\u8f7d\u5177", -1013173),
    ENEMY_HELICOPTER("\u654c\u65b9\u76f4\u5347\u673a", -1814649),
    ATTACK_HERE("\u8fdb\u653b\u8be5\u5904", -19154, true, true),
    DEFEND_HERE("\u9632\u5b88\u8be5\u5904", -11690497, true, true),
    ARTILLERY_TARGET("155\u70ae\u51fb\u70b9", -50384, false, false);

    private final String displayName;
    private final int color;
    private final boolean selectableFromMenu;
    private final boolean persistentUntilRemoved;

    private TacticalMarkerType(String displayName, int color) {
        this(displayName, color, true, false);
    }

    private TacticalMarkerType(String displayName, int color, boolean selectableFromMenu) {
        this(displayName, color, selectableFromMenu, false);
    }

    private TacticalMarkerType(String displayName, int color, boolean selectableFromMenu, boolean persistentUntilRemoved) {
        this.displayName = displayName;
        this.color = color;
        this.selectableFromMenu = selectableFromMenu;
        this.persistentUntilRemoved = persistentUntilRemoved;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public int getColor() {
        return this.color;
    }

    public boolean isSelectableFromMenu() {
        return this.selectableFromMenu;
    }

    public boolean isPersistentUntilRemoved() {
        return this.persistentUntilRemoved;
    }

    public static TacticalMarkerType[] selectableValues() {
        return (TacticalMarkerType[])Arrays.stream(TacticalMarkerType.values()).filter(TacticalMarkerType::isSelectableFromMenu).toArray(TacticalMarkerType[]::new);
    }

    public static TacticalMarkerType fromNetworkId(int id) {
        TacticalMarkerType[] values = TacticalMarkerType.values();
        return id >= 0 && id < values.length ? values[id] : null;
    }
}

