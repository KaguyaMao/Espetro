/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Registry
 *  net.minecraft.core.RegistryAccess
 *  net.minecraft.core.RegistrySetBuilder
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.data.PackOutput
 *  net.minecraft.data.registries.RegistriesDatapackGenerator
 */
package net.minecraftforge.common.data;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.registries.RegistriesDatapackGenerator;
import net.minecraftforge.registries.DataPackRegistriesHooks;

public class DatapackBuiltinEntriesProvider
extends RegistriesDatapackGenerator {
    public DatapackBuiltinEntriesProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, Set<String> modIds) {
        super(output, registries, modIds);
    }

    public DatapackBuiltinEntriesProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, RegistrySetBuilder datapackEntriesBuilder, Set<String> modIds) {
        this(output, (CompletableFuture<HolderLookup.Provider>)registries.thenApply(r -> DatapackBuiltinEntriesProvider.constructRegistries(r, datapackEntriesBuilder)), modIds);
    }

    private static HolderLookup.Provider constructRegistries(HolderLookup.Provider original, RegistrySetBuilder datapackEntriesBuilder) {
        HashSet builderKeys = new HashSet(datapackEntriesBuilder.getEntryKeys());
        DataPackRegistriesHooks.getDataPackRegistriesWithDimensions().filter(data -> !builderKeys.contains(data.f_243794_())).forEach(data -> datapackEntriesBuilder.m_254916_(data.f_243794_(), context -> {}));
        return datapackEntriesBuilder.m_254929_((RegistryAccess)RegistryAccess.m_206165_((Registry)BuiltInRegistries.f_257047_), original);
    }
}

