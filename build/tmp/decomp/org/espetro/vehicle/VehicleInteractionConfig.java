/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.common.ForgeConfigSpec
 *  net.minecraftforge.common.ForgeConfigSpec$Builder
 *  net.minecraftforge.common.ForgeConfigSpec$DoubleValue
 *  net.minecraftforge.common.ForgeConfigSpec$IntValue
 */
package org.espetro.vehicle;

import net.minecraftforge.common.ForgeConfigSpec;

public final class VehicleInteractionConfig {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.IntValue MOUNT_DELAY_TICKS;
    public static final ForgeConfigSpec.IntValue DISMOUNT_DELAY_TICKS;
    public static final ForgeConfigSpec.IntValue SEAT_SWITCH_DELAY_TICKS;
    public static final ForgeConfigSpec.DoubleValue MOUNT_MAX_DISTANCE;

    private VehicleInteractionConfig() {
    }

    public static int mountDelayTicks() {
        return (Integer)MOUNT_DELAY_TICKS.get();
    }

    public static int dismountDelayTicks() {
        return (Integer)DISMOUNT_DELAY_TICKS.get();
    }

    public static int seatSwitchDelayTicks() {
        return (Integer)SEAT_SWITCH_DELAY_TICKS.get();
    }

    public static double mountMaxDistance() {
        return (Double)MOUNT_MAX_DISTANCE.get();
    }

    static {
        ForgeConfigSpec.Builder b = new ForgeConfigSpec.Builder();
        b.push("vehicle");
        MOUNT_DELAY_TICKS = b.comment("Ticks holding INTERACT on wheel center to mount (0 = instant)").defineInRange("mountDelayTicks", 60, 0, 1200);
        DISMOUNT_DELAY_TICKS = b.comment("Ticks holding INTERACT to dismount (0 = native SBW)").defineInRange("dismountDelayTicks", 60, 0, 1200);
        SEAT_SWITCH_DELAY_TICKS = b.comment("Ticks holding Shift before a seat change is allowed").defineInRange("seatSwitchDelayTicks", 100, 0, 1200);
        MOUNT_MAX_DISTANCE = b.comment("Max distance from vehicle while mounting").defineInRange("mountMaxDistance", 5.0, 1.0, 16.0);
        b.pop();
        SPEC = b.build();
    }
}

