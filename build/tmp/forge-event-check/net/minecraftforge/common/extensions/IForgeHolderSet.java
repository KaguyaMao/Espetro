/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Holder
 *  net.minecraft.core.HolderSet$ListBacked
 */
package net.minecraftforge.common.extensions;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;

public interface IForgeHolderSet<T> {
    default public void addInvalidationListener(Runnable runnable) {
    }

    default public SerializationType serializationType() {
        SerializationType serializationType;
        IForgeHolderSet iForgeHolderSet = this;
        if (iForgeHolderSet instanceof HolderSet.ListBacked) {
            HolderSet.ListBacked listBacked = (HolderSet.ListBacked)iForgeHolderSet;
            serializationType = (SerializationType)((Object)listBacked.m_203440_().map(tag -> SerializationType.STRING, list -> list.size() == 1 ? (SerializationType)((Object)((Object)((Holder)list.get(0)).m_203439_().map(key -> key == null ? SerializationType.OBJECT : SerializationType.STRING, value -> SerializationType.OBJECT))) : SerializationType.LIST));
        } else {
            serializationType = SerializationType.UNKNOWN;
        }
        return serializationType;
    }

    public static enum SerializationType {
        UNKNOWN,
        STRING,
        LIST,
        OBJECT;

    }
}

