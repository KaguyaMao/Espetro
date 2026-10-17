package org.espetro.support;

import net.minecraftforge.common.ForgeConfigSpec;

public final class FireRequestConfig {
    private static final ForgeConfigSpec.Builder B = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec.IntValue MAX = B.defineInRange("maximumPerTeam", 48, 1, 96);
    public static final ForgeConfigSpec.IntValue INTERVAL =
            B.defineInRange("createIntervalTicks", 200, 0, 72000);
    public static final ForgeConfigSpec.IntValue TTL =
            B.defineInRange("lifetimeTicks", 6000, 20, 1728000);
    public static final ForgeConfigSpec.IntValue LENGTH =
            B.defineInRange("maximumBarrageLength", 1500, 1, 10000);
    public static final ForgeConfigSpec SPEC = B.build();

    public static FireRequestBoard.Rules rules() {
        return new FireRequestBoard.Rules(MAX.get(), INTERVAL.get(), TTL.get(), LENGTH.get());
    }
}
