/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.common.ForgeConfigSpec
 *  net.minecraftforge.common.ForgeConfigSpec$Builder
 *  net.minecraftforge.common.ForgeConfigSpec$ConfigValue
 *  net.minecraftforge.common.ForgeConfigSpec$EnumValue
 *  net.minecraftforge.common.ForgeConfigSpec$IntValue
 */
package com.example.espoints.config;

import com.example.espoints.config.MapImageQuality;
import com.example.espoints.hud.MapDisplayMode;
import net.minecraftforge.common.ForgeConfigSpec;

public class TacticalMapConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.EnumValue<MapDisplayMode> displayMode;
    public static final ForgeConfigSpec.IntValue miniMapScale;
    public static final ForgeConfigSpec.ConfigValue<String> attackerProgressBarColor;
    public static final ForgeConfigSpec.ConfigValue<String> defenderProgressBarColor;
    public static final ForgeConfigSpec.IntValue tileTextureCacheMiB;
    public static final ForgeConfigSpec.EnumValue<MapImageQuality> mapImageQuality;

    static {
        BUILDER.push("tacticalMap");
        displayMode = BUILDER.comment("\u6218\u672f\u5730\u56fe\u663e\u793a\u6a21\u5f0f").defineEnum("displayMode", (Enum)MapDisplayMode.TOGGLE_KEY);
        miniMapScale = BUILDER.comment("\u8ff7\u4f60\u5730\u56fe\u7f29\u653e\u6bd4\u4f8b (\u767e\u5206\u6bd4)").defineInRange("miniMapScale", 75, 25, 100);
        attackerProgressBarColor = BUILDER.comment("\u653b\u65b9\u8fdb\u5ea6\u6761\u989c\u8272 (\u5341\u516d\u8fdb\u5236\uff0c\u5982\uff1a#FF5500)").define("attackerProgressBarColor", (Object)"#FF5500");
        defenderProgressBarColor = BUILDER.comment("\u5b88\u65b9\u8fdb\u5ea6\u6761\u989c\u8272 (\u5341\u516d\u8fdb\u5236\uff0c\u5982\uff1a#0055FF)").define("defenderProgressBarColor", (Object)"#0055FF");
        tileTextureCacheMiB = BUILDER.comment("\u6218\u672f\u5730\u56fe\u5ba2\u6237\u7aef\u74e6\u7247\u7eb9\u7406\u7f13\u5b58\uff08MiB\uff09").defineInRange("tileTextureCacheMiB", 64, 16, 512);
        mapImageQuality = BUILDER.comment("\u6218\u672f\u5730\u56fe\u56fe\u50cf\u8d28\u91cf\uff1aPERFORMANCE\uff08\u6027\u80fd\uff09\u3001BALANCED\uff08\u5e73\u8861\uff09\u3001HIGH\uff08\u9ad8\u6e05\uff09").defineEnum("mapImageQuality", (Enum)MapImageQuality.BALANCED);
        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}

