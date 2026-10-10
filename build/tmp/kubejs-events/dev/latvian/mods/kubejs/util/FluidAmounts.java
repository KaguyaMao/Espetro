/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.architectury.hooks.fluid.FluidStackHooks
 */
package dev.latvian.mods.kubejs.util;

import dev.architectury.hooks.fluid.FluidStackHooks;
import dev.latvian.mods.kubejs.platform.MiscPlatformHelper;

public interface FluidAmounts {
    public static final long BUCKET = FluidStackHooks.bucketAmount();
    public static final long MILLIBUCKET = BUCKET / 1000L;
    public static final long B = BUCKET;
    public static final long MB = MILLIBUCKET;
    public static final long INGOT = MiscPlatformHelper.get().ingotFluidAmount();
    public static final long NUGGET = INGOT / 9L;
    public static final long METAL_BLOCK = INGOT * 9L;
    public static final long BOTTLE = MiscPlatformHelper.get().bottleFluidAmount();
}

