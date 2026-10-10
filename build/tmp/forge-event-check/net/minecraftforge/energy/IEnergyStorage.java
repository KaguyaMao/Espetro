/*
 * Decompiled with CFR 0.152.
 */
package net.minecraftforge.energy;

import net.minecraftforge.common.capabilities.AutoRegisterCapability;

@AutoRegisterCapability
public interface IEnergyStorage {
    public int receiveEnergy(int var1, boolean var2);

    public int extractEnergy(int var1, boolean var2);

    public int getEnergyStored();

    public int getMaxEnergyStored();

    public boolean canExtract();

    public boolean canReceive();
}

