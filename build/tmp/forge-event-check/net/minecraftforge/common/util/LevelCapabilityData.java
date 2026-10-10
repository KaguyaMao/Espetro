/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.world.level.saveddata.SavedData
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.common.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;

public class LevelCapabilityData
extends SavedData {
    public static final String ID = "capabilities";
    private INBTSerializable<CompoundTag> serializable;
    private CompoundTag capNBT = null;

    public LevelCapabilityData(@Nullable INBTSerializable<CompoundTag> serializable) {
        this.serializable = serializable;
    }

    public static LevelCapabilityData load(CompoundTag tag, @Nullable INBTSerializable<CompoundTag> serializable) {
        LevelCapabilityData data = new LevelCapabilityData(serializable);
        data.read(tag);
        return data;
    }

    public void read(CompoundTag nbt) {
        this.capNBT = nbt;
        if (this.serializable != null) {
            this.serializable.deserializeNBT(this.capNBT);
            this.capNBT = null;
        }
    }

    public CompoundTag m_7176_(CompoundTag nbt) {
        if (this.serializable != null) {
            nbt = this.serializable.serializeNBT();
        }
        return nbt;
    }

    public boolean m_77764_() {
        return true;
    }

    public void setCapabilities(INBTSerializable<CompoundTag> capabilities) {
        this.serializable = capabilities;
        if (this.capNBT != null && this.serializable != null) {
            this.serializable.deserializeNBT(this.capNBT);
            this.capNBT = null;
        }
    }
}

