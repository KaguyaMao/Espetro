/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.world.level.saveddata.SavedData
 */
package net.minecraftforge.common.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;

public class DummySavedData
extends SavedData {
    public static final DummySavedData DUMMY = new DummySavedData();

    private DummySavedData() {
    }

    public CompoundTag m_7176_(CompoundTag compound) {
        return null;
    }
}

