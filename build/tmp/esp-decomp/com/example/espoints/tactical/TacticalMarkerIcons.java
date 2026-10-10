/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 */
package com.example.espoints.tactical;

import com.example.espoints.tactical.TacticalMarkerType;
import net.minecraft.resources.ResourceLocation;

public final class TacticalMarkerIcons {
    public static final ResourceLocation EN_SOLDIER = ResourceLocation.fromNamespaceAndPath((String)"espoints", (String)"textures/gui/map/en_soldier.png");
    public static final ResourceLocation EN_TANK = ResourceLocation.fromNamespaceAndPath((String)"espoints", (String)"textures/gui/map/en_tank.png");
    public static final ResourceLocation EN_IFV = ResourceLocation.fromNamespaceAndPath((String)"espoints", (String)"textures/gui/map/en_ifv.png");
    public static final ResourceLocation EN_TRUCK = ResourceLocation.fromNamespaceAndPath((String)"espoints", (String)"textures/gui/map/en_truck.png");
    public static final ResourceLocation EN_HELI = ResourceLocation.fromNamespaceAndPath((String)"espoints", (String)"textures/gui/map/en_heli.png");
    public static final ResourceLocation MARK_ATTACK = ResourceLocation.fromNamespaceAndPath((String)"espoints", (String)"textures/gui/map/mark_attack.png");
    public static final ResourceLocation MARK_DEFEND = ResourceLocation.fromNamespaceAndPath((String)"espoints", (String)"textures/gui/map/mark_defend.png");
    public static final ResourceLocation ACP = ResourceLocation.fromNamespaceAndPath((String)"espoints", (String)"textures/gui/map/acp.png");
    public static final int ENEMY_RED = -2076078;

    private TacticalMarkerIcons() {
    }

    public static ResourceLocation textureFor(TacticalMarkerType type) {
        if (type == null) {
            return EN_SOLDIER;
        }
        return switch (type) {
            default -> throw new IncompatibleClassChangeError();
            case TacticalMarkerType.ENEMY_INFANTRY -> EN_SOLDIER;
            case TacticalMarkerType.ENEMY_TANK -> EN_TANK;
            case TacticalMarkerType.ENEMY_IFV -> EN_IFV;
            case TacticalMarkerType.ENEMY_LIGHT_VEHICLE -> EN_TRUCK;
            case TacticalMarkerType.ENEMY_HELICOPTER -> EN_HELI;
            case TacticalMarkerType.ATTACK_HERE -> MARK_ATTACK;
            case TacticalMarkerType.DEFEND_HERE -> MARK_DEFEND;
            case TacticalMarkerType.ARTILLERY_TARGET -> ACP;
        };
    }

    public static boolean isEnemyUnit(TacticalMarkerType type) {
        if (type == null) {
            return false;
        }
        return switch (type) {
            case TacticalMarkerType.ENEMY_INFANTRY, TacticalMarkerType.ENEMY_TANK, TacticalMarkerType.ENEMY_IFV, TacticalMarkerType.ENEMY_LIGHT_VEHICLE, TacticalMarkerType.ENEMY_HELICOPTER -> true;
            default -> false;
        };
    }
}

