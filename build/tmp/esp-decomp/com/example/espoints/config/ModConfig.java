/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.common.ForgeConfigSpec
 *  net.minecraftforge.common.ForgeConfigSpec$BooleanValue
 *  net.minecraftforge.common.ForgeConfigSpec$Builder
 *  net.minecraftforge.common.ForgeConfigSpec$DoubleValue
 *  net.minecraftforge.common.ForgeConfigSpec$IntValue
 */
package com.example.espoints.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ModConfig {
    public static ForgeConfigSpec.BooleanValue enableHUD;
    public static ForgeConfigSpec.BooleanValue enableCarousel;
    public static ForgeConfigSpec.BooleanValue enableTeams;
    public static ForgeConfigSpec.BooleanValue enableTeamIndicator;
    public static ForgeConfigSpec.IntValue checkInterval;
    public static ForgeConfigSpec.IntValue tacticalMapServerMemoryMiB;
    public static ForgeConfigSpec.IntValue tacticalMapDiskCacheMiB;
    public static ForgeConfigSpec.IntValue tacticalMapPlayerTransferKiBps;
    public static ForgeConfigSpec.IntValue tacticalMapGlobalTransferKiBps;
    public static ForgeConfigSpec.IntValue pointRewardInterval;
    public static ForgeConfigSpec.IntValue pointRewardAmount;
    public static ForgeConfigSpec.IntValue killRewardAmount;
    public static ForgeConfigSpec.IntValue captureRewardAmount;
    public static ForgeConfigSpec.IntValue capturedRewardInterval;
    public static ForgeConfigSpec.IntValue capturedRewardAmount;
    public static ForgeConfigSpec.IntValue capturedRewardDelay;
    public static ForgeConfigSpec.IntValue friendlyFirePenalty;
    public static ForgeConfigSpec.BooleanValue enableFriendlyFirePenalty;
    public static ForgeConfigSpec.BooleanValue enableOperationMode;
    public static ForgeConfigSpec.DoubleValue lowReinforcementThreshold;
    private static final ForgeConfigSpec.Builder BUILDER;
    public static final ForgeConfigSpec SPEC;

    static {
        BUILDER = new ForgeConfigSpec.Builder();
        BUILDER.push("HUD Settings");
        enableHUD = BUILDER.comment("\u542f\u7528HUD\u663e\u793a").define("enableHUD", true);
        enableCarousel = BUILDER.comment("\u542f\u7528\u636e\u70b9\u4fe1\u606f\u8f6e\u64ad").define("enableCarousel", true);
        BUILDER.pop();
        BUILDER.push("Team Settings");
        enableTeams = BUILDER.comment("\u517c\u5bb9\u65e7\u914d\u7f6e\uff1aHCRpoints \u73b0\u5728\u56fa\u5b9a\u4f7f\u7528 Espetro \u653b\u9632\u9635\u8425\u961f\u4f0d").define("enableTeams", true);
        enableTeamIndicator = BUILDER.comment("\u5728\u73a9\u5bb6\u5934\u9876\u663e\u793a\u654c\u6211\u6807\u8bc6\uff08\u53cb\u519b\u7eff\u8272\u7bad\u5934/\u654c\u519b\u7ea2\u8272\u5012\u4e09\u89d2\uff09\uff0c\u9700\u8981\u961f\u4f0d\u673a\u5236\u540c\u65f6\u542f\u7528").define("enableTeamIndicator", true);
        BUILDER.pop();
        BUILDER.push("Performance Settings");
        checkInterval = BUILDER.comment("\u636e\u70b9\u68c0\u67e5\u95f4\u9694\uff08tick\uff09").defineInRange("checkInterval", 5, 1, 100);
        tacticalMapServerMemoryMiB = BUILDER.comment("\u6218\u672f\u5730\u56fe\u670d\u52a1\u7aef\u7f16\u7801\u74e6\u7247\u5185\u5b58\u7f13\u5b58\uff08MiB\uff09").defineInRange("tacticalMapServerMemoryMiB", 32, 8, 512);
        tacticalMapDiskCacheMiB = BUILDER.comment("config/espoints/cache/tactical-map \u78c1\u76d8\u9884\u7b97\uff08MiB\uff09").defineInRange("tacticalMapDiskCacheMiB", 512, 64, 4096);
        tacticalMapPlayerTransferKiBps = BUILDER.comment("\u6bcf\u540d\u73a9\u5bb6\u6218\u672f\u5730\u56fe\u74e6\u7247\u4f20\u8f93\u9884\u7b97\uff08KiB/s\uff09").defineInRange("tacticalMapPlayerTransferKiBps", 512, 32, 4096);
        tacticalMapGlobalTransferKiBps = BUILDER.comment("\u5168\u670d\u6218\u672f\u5730\u56fe\u74e6\u7247\u4f20\u8f93\u9884\u7b97\uff08KiB/s\uff09").defineInRange("tacticalMapGlobalTransferKiBps", 4096, 256, 65536);
        BUILDER.pop();
        BUILDER.push("Reward Settings");
        pointRewardInterval = BUILDER.comment("\u636e\u70b9\u5185\u83b7\u5f97\u70b9\u6570\u7684\u65f6\u95f4\u95f4\u9694\uff08\u79d2\uff09").defineInRange("pointRewardInterval", 60, 1, 3600);
        pointRewardAmount = BUILDER.comment("\u636e\u70b9\u5185\u6bcf\u6b21\u83b7\u5f97\u7684\u70b9\u6570").defineInRange("pointRewardAmount", 5, 1, 1000);
        killRewardAmount = BUILDER.comment("\u51fb\u6740\u73a9\u5bb6\u83b7\u5f97\u7684\u70b9\u6570").defineInRange("killRewardAmount", 50, 1, 1000);
        captureRewardAmount = BUILDER.comment("\u5360\u9886\u636e\u70b9\u83b7\u5f97\u7684\u70b9\u6570").defineInRange("captureRewardAmount", 100, 1, 1000);
        capturedRewardInterval = BUILDER.comment("\u5360\u9886\u636e\u70b9\u540e\uff0c\u83b7\u5f97\u6301\u7eed\u5956\u52b1\u7684\u65f6\u95f4\u95f4\u9694\uff08\u79d2\uff09").defineInRange("capturedRewardInterval", 60, 1, 3600);
        capturedRewardAmount = BUILDER.comment("\u5360\u9886\u636e\u70b9\u540e\uff0c\u6bcf\u6b21\u83b7\u5f97\u7684\u6301\u7eed\u5956\u52b1\u70b9\u6570").defineInRange("capturedRewardAmount", 10, 1, 1000);
        capturedRewardDelay = BUILDER.comment("\u5360\u9886\u636e\u70b9\u540e\uff0c\u5f00\u59cb\u83b7\u5f97\u6301\u7eed\u5956\u52b1\u7684\u5ef6\u8fdf\u65f6\u95f4\uff08\u79d2\uff09").defineInRange("capturedRewardDelay", 5, 1, 3600);
        enableFriendlyFirePenalty = BUILDER.comment("\u542f\u7528\u53cb\u519b\u51fb\u6740\u60e9\u7f5a").define("enableFriendlyFirePenalty", true);
        friendlyFirePenalty = BUILDER.comment("\u53cb\u519b\u51fb\u6740\u6263\u9664\u7684\u70b9\u6570").defineInRange("friendlyFirePenalty", 200, 1, 10000);
        BUILDER.pop();
        BUILDER.push("Operation Settings");
        enableOperationMode = BUILDER.comment("\u517c\u5bb9\u65e7\u914d\u7f6e\uff1aHCRpoints \u73b0\u5728\u56fa\u5b9a\u542f\u7528\u884c\u52a8\u653b\u9632\u673a\u5236").define("enableOperationMode", true);
        lowReinforcementThreshold = BUILDER.comment("\u5f53\u4e00\u65b9\u5175\u529b\u4f4e\u4e8e\u6b64\u767e\u5206\u6bd4\u65f6\u64ad\u653e\u80cc\u6c34\u4e00\u6218\u80cc\u666f\u97f3\u4e50\uff080-100\uff0c\u8bbe\u7f6e\u4e3a0\u5219\u4e0d\u64ad\u653e\uff09").defineInRange("lowReinforcementThreshold", 10.0, 0.0, 100.0);
        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}

