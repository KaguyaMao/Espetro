/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.common.ForgeConfigSpec
 *  net.minecraftforge.common.ForgeConfigSpec$Builder
 *  net.minecraftforge.common.ForgeConfigSpec$IntValue
 */
package com.rhythm.dragon_vehicle_deployer;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.IntValue DEFAULT_SPAWN_INTERVAL;
    public static final ForgeConfigSpec.IntValue IDLE_CLEAR_TIMEOUT_SECONDS;

    static {
        BUILDER.push("General");
        DEFAULT_SPAWN_INTERVAL = BUILDER.comment("Default auto-spawn interval in seconds for new deployers").defineInRange("defaultSpawnInterval", 5, 5, 3600);
        IDLE_CLEAR_TIMEOUT_SECONDS = BUILDER.comment("Seconds of idle time after which a vehicle that has been occupied will be cleared and respawned. Set to 0 to disable.").defineInRange("idleClearTimeoutSeconds", 300, 0, 36000);
        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}

