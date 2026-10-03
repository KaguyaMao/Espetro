/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.common.ForgeConfigSpec
 *  net.minecraftforge.common.ForgeConfigSpec$BooleanValue
 *  net.minecraftforge.common.ForgeConfigSpec$Builder
 *  net.minecraftforge.common.ForgeConfigSpec$DoubleValue
 *  net.minecraftforge.common.ForgeConfigSpec$IntValue
 *  net.minecraftforge.fml.loading.FMLEnvironment
 */
package com.sighs.apricityui.config;

import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.loading.FMLEnvironment;

public final class ApricityUIConfig {
    public static final ForgeConfigSpec CLIENT_SPEC;
    public static final Client CLIENT;
    private static final AtomicBoolean CLIENT_RELOAD_PENDING;

    private ApricityUIConfig() {
    }

    public static void markClientReloadPending() {
        CLIENT_RELOAD_PENDING.set(true);
    }

    public static boolean consumeClientReloadPending() {
        return CLIENT_RELOAD_PENDING.compareAndSet(true, false);
    }

    static {
        CLIENT_RELOAD_PENDING = new AtomicBoolean();
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        CLIENT = new Client(builder);
        CLIENT_SPEC = builder.build();
    }

    public static final class Client {
        public final ForgeConfigSpec.BooleanValue debugAutoReload;
        public final ForgeConfigSpec.BooleanValue aiAutoScreenshot;
        public final ForgeConfigSpec.BooleanValue frameTimingHud;
        public final ForgeConfigSpec.BooleanValue remoteDebug;
        public final ForgeConfigSpec.BooleanValue resourceManagerWorldWindow;
        public final ForgeConfigSpec.BooleanValue viewportZoomPassThrough;
        public final ForgeConfigSpec.DoubleValue worldWindowDepthOffsetScale;
        public final ForgeConfigSpec.IntValue worldWindowMaxDisplayDistance;
        public final ForgeConfigSpec.BooleanValue worldWindowLodEnabled;
        public final ForgeConfigSpec.IntValue worldWindowFullDetailDistance;
        public final ForgeConfigSpec.IntValue worldWindowReducedDetailDistance;

        private Client(ForgeConfigSpec.Builder builder) {
            builder.push("debug");
            this.debugAutoReload = builder.comment("Enable dev auto-reload when local files change.").define("autoReload", false);
            this.aiAutoScreenshot = builder.comment("Enable AI helper screenshots (1 per second, keep latest 3) under screenshots/aui.").define("aiAutoScreenshot", false);
            this.frameTimingHud = builder.comment("Show the AUI per-frame timing monitor in the top-left corner.").define("frameTimingHud", false);
            this.remoteDebug = builder.comment("Enable the loopback-only Apricity external debugger on port 25321.").define("remoteDebug", !FMLEnvironment.production);
            this.resourceManagerWorldWindow = builder.comment("Open the debug resource manager as a world window while in-game.").define("resourceManagerWorldWindow", false);
            builder.pop();
            builder.push("input");
            this.viewportZoomPassThrough = builder.comment("Allow Ctrl+mouse-wheel viewport zoom to pass through persistent overlays that do not intercept mouse events.").define("viewportZoomPassThrough", true);
            builder.pop();
            builder.push("worldWindow");
            this.worldWindowDepthOffsetScale = builder.comment("Scale applied to WorldWindow's distance-based depth offset.").defineInRange("depthOffsetScale", 0.01, 0.0, 1.0);
            this.worldWindowMaxDisplayDistance = builder.comment("Default maximum camera distance for WorldWindow rendering and interaction. Integer.MAX_VALUE means unlimited.").defineInRange("maxDisplayDistance", 128, 0, Integer.MAX_VALUE);
            this.worldWindowLodEnabled = builder.comment("Enable distance-based level-of-detail rendering for WorldWindow by default.").define("lodEnabled", false);
            this.worldWindowFullDetailDistance = builder.comment("WorldWindow distance up to which automatic LOD keeps full detail.").defineInRange("fullDetailDistance", 16, 0, Integer.MAX_VALUE);
            this.worldWindowReducedDetailDistance = builder.comment("WorldWindow distance up to which automatic LOD keeps reduced detail.").defineInRange("reducedDetailDistance", 48, 0, Integer.MAX_VALUE);
            builder.pop();
        }

        public float worldWindowDepthOffsetScale() {
            return ((Double)this.worldWindowDepthOffsetScale.get()).floatValue();
        }

        public int worldWindowMaxDisplayDistance() {
            return (Integer)this.worldWindowMaxDisplayDistance.get();
        }

        public boolean worldWindowLodEnabled() {
            return (Boolean)this.worldWindowLodEnabled.get();
        }

        public int worldWindowFullDetailDistance() {
            return (Integer)this.worldWindowFullDetailDistance.get();
        }

        public int worldWindowReducedDetailDistance() {
            return (Integer)this.worldWindowReducedDetailDistance.get();
        }
    }
}

