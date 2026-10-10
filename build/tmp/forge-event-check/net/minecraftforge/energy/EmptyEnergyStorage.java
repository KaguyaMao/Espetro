/*
 * Decompiled with CFR 0.152.
 */
package net.minecraftforge.energy;

import net.minecraftforge.energy.IEnergyStorage;

public class EmptyEnergyStorage
implements IEnergyStorage {
    public static final EmptyEnergyStorage INSTANCE = new EmptyEnergyStorage();

    protected EmptyEnergyStorage() {
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return 0;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return 0;
    }

    @Override
    public int getEnergyStored() {
        return 0;
    }

    @Override
    public int getMaxEnergyStored() {
        return 0;
    }

    @Override
    public boolean canExtract() {
        return false;
    }

    @Override
    public boolean canReceive() {
        return false;
    }
}

