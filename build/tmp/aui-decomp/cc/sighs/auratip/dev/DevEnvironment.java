/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.fml.loading.FMLEnvironment
 */
package cc.sighs.auratip.dev;

import net.minecraftforge.fml.loading.FMLEnvironment;

public final class DevEnvironment {
    private DevEnvironment() {
    }

    public static boolean isDev() {
        return FMLEnvironment.production;
    }
}

