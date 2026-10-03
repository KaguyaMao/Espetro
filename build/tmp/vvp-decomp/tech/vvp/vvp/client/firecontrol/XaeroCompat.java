/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.fml.ModList
 */
package tech.vvp.vvp.client.firecontrol;

import net.minecraftforge.fml.ModList;

public final class XaeroCompat {
    public static final String WORLD_MAP_MOD_ID = "xaeroworldmap";

    private XaeroCompat() {
    }

    public static boolean isWorldMapLoaded() {
        return ModList.get().isLoaded(WORLD_MAP_MOD_ID);
    }
}

