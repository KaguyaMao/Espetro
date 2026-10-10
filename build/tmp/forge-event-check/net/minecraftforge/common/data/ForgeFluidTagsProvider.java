/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.data.PackOutput
 *  net.minecraft.data.tags.FluidTagsProvider
 */
package net.minecraftforge.common.data;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;

public final class ForgeFluidTagsProvider
extends FluidTagsProvider {
    public ForgeFluidTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, "forge", existingFileHelper);
    }

    public void m_6577_(HolderLookup.Provider lookupProvider) {
        this.m_206424_(Tags.Fluids.MILK).m_176839_(ForgeMod.MILK.getId()).m_176839_(ForgeMod.FLOWING_MILK.getId());
    }

    public String m_6055_() {
        return "Forge Fluid Tags";
    }
}

