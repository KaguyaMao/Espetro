/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.Util
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.data.registries.VanillaRegistries
 *  net.minecraft.server.Bootstrap
 *  net.minecraftforge.fml.ModContainer
 *  net.minecraftforge.fml.ModLoader
 *  net.minecraftforge.fml.ModWorkManager
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraftforge.data.loading;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.server.Bootstrap;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.ModWorkManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DatagenModLoader {
    private static final Logger LOGGER = LogManager.getLogger();
    private static GatherDataEvent.DataGeneratorConfig dataGeneratorConfig;
    private static ExistingFileHelper existingFileHelper;
    private static boolean runningDataGen;

    public static boolean isRunningDataGen() {
        return runningDataGen;
    }

    public static void begin(Set<String> mods, Path path, Collection<Path> inputs, Collection<Path> existingPacks, Set<String> existingMods, boolean serverGenerators, boolean clientGenerators, boolean devToolGenerators, boolean reportsGenerator, boolean structureValidator, boolean flat, String assetIndex, File assetsDir) {
        if (mods.contains("minecraft") && mods.size() == 1) {
            return;
        }
        LOGGER.info("Initializing Data Gatherer for mods {}", mods);
        runningDataGen = true;
        Bootstrap.m_135870_();
        ModLoader.get().gatherAndInitializeMods(ModWorkManager.syncExecutor(), ModWorkManager.parallelExecutor(), () -> {});
        CompletableFuture<HolderLookup.Provider> lookupProvider = CompletableFuture.supplyAsync(VanillaRegistries::m_255371_, Util.m_183991_());
        dataGeneratorConfig = new GatherDataEvent.DataGeneratorConfig(mods, path, inputs, lookupProvider, serverGenerators, clientGenerators, devToolGenerators, reportsGenerator, structureValidator, flat);
        if (!mods.contains("forge")) {
            existingMods.add("forge");
        }
        existingFileHelper = new ExistingFileHelper(existingPacks, existingMods, structureValidator, assetIndex, assetsDir);
        ModLoader.get().runEventGenerator(mc -> new GatherDataEvent((ModContainer)mc, dataGeneratorConfig.makeGenerator(p -> dataGeneratorConfig.isFlat() ? p : p.resolve(mc.getModId()), dataGeneratorConfig.getMods().contains(mc.getModId())), dataGeneratorConfig, existingFileHelper));
        dataGeneratorConfig.runAll();
    }
}

