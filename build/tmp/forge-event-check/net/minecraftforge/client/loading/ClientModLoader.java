/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.SharedConstants
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.packs.PackResources
 *  net.minecraft.server.packs.PackType
 *  net.minecraft.server.packs.metadata.pack.PackMetadataSection
 *  net.minecraft.server.packs.repository.Pack
 *  net.minecraft.server.packs.repository.Pack$Position
 *  net.minecraft.server.packs.repository.PackRepository
 *  net.minecraft.server.packs.repository.PackSource
 *  net.minecraft.server.packs.repository.RepositorySource
 *  net.minecraft.server.packs.resources.PreparableReloadListener
 *  net.minecraft.server.packs.resources.PreparableReloadListener$PreparationBarrier
 *  net.minecraft.server.packs.resources.ReloadableResourceManager
 *  net.minecraft.server.packs.resources.ResourceManager
 *  net.minecraft.util.profiling.ProfilerFiller
 *  net.minecraft.world.level.DataPackConfig
 *  net.minecraftforge.eventbus.api.Event
 *  net.minecraftforge.fml.LoadingFailedException
 *  net.minecraftforge.fml.Logging
 *  net.minecraftforge.fml.ModList
 *  net.minecraftforge.fml.ModLoader
 *  net.minecraftforge.fml.ModLoadingStage
 *  net.minecraftforge.fml.ModLoadingWarning
 *  net.minecraftforge.fml.ModWorkManager
 *  net.minecraftforge.fml.ModWorkManager$DrivenExecutor
 *  net.minecraftforge.fml.VersionChecker
 *  net.minecraftforge.fml.VersionChecker$CheckResult
 *  net.minecraftforge.fml.VersionChecker$Status
 *  net.minecraftforge.fml.loading.ImmediateWindowHandler
 *  net.minecraftforge.forgespi.language.IModInfo
 *  net.minecraftforge.forgespi.locating.IModFile
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraftforge.client.loading;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.DataPackConfig;
import net.minecraftforge.client.gui.LoadingErrorScreen;
import net.minecraftforge.common.ForgeConfig;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.LogicalSidedProvider;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.LoadingFailedException;
import net.minecraftforge.fml.Logging;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.ModLoadingStage;
import net.minecraftforge.fml.ModLoadingWarning;
import net.minecraftforge.fml.ModWorkManager;
import net.minecraftforge.fml.VersionChecker;
import net.minecraftforge.fml.loading.ImmediateWindowHandler;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.forgespi.locating.IModFile;
import net.minecraftforge.internal.BrandingControl;
import net.minecraftforge.logging.CrashReportExtender;
import net.minecraftforge.resource.DelegatingPackResources;
import net.minecraftforge.resource.PathPackResources;
import net.minecraftforge.resource.ResourcePackLoader;
import net.minecraftforge.server.LanguageHook;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClientModLoader {
    private static final Logger LOGGER = LogManager.getLogger();
    private static boolean loading;
    private static Minecraft mc;
    private static boolean loadingComplete;
    private static LoadingFailedException error;

    public static void begin(Minecraft minecraft, PackRepository defaultResourcePacks, ReloadableResourceManager mcResourceManager) {
        Runtime.getRuntime().addShutdownHook(new Thread(LogManager::shutdown));
        ImmediateWindowHandler.updateProgress((String)"Loading mods");
        loading = true;
        mc = minecraft;
        LogicalSidedProvider.setClient(() -> minecraft);
        LanguageHook.loadForgeAndMCLangs();
        ClientModLoader.createRunnableWithCatch(() -> ModLoader.get().gatherAndInitializeMods(ModWorkManager.syncExecutor(), ModWorkManager.parallelExecutor(), ImmediateWindowHandler::renderTick)).run();
        if (error == null) {
            ResourcePackLoader.loadResourcePacks(defaultResourcePacks, ClientModLoader::buildPackFinder);
            ModLoader.get().postEvent((Event)new AddPackFindersEvent(PackType.CLIENT_RESOURCES, arg_0 -> ((PackRepository)defaultResourcePacks).addPackFinder(arg_0)));
            DataPackConfig.f_45842_.addModPacks(ResourcePackLoader.getPackNames());
            mcResourceManager.m_7217_(ClientModLoader::onResourceReload);
            mcResourceManager.m_7217_((PreparableReloadListener)BrandingControl.resourceManagerReloadListener());
        }
    }

    private static CompletableFuture<Void> onResourceReload(PreparableReloadListener.PreparationBarrier stage, ResourceManager resourceManager, ProfilerFiller prepareProfiler, ProfilerFiller executeProfiler, Executor asyncExecutor, Executor syncExecutor) {
        return ((CompletableFuture)CompletableFuture.runAsync(ClientModLoader.createRunnableWithCatch(() -> ClientModLoader.startModLoading(ModWorkManager.wrappedExecutor((Executor)syncExecutor), asyncExecutor)), ModWorkManager.parallelExecutor()).thenCompose(arg_0 -> ((PreparableReloadListener.PreparationBarrier)stage).m_6769_(arg_0))).thenRunAsync(() -> ClientModLoader.finishModLoading(ModWorkManager.wrappedExecutor((Executor)syncExecutor), asyncExecutor), ModWorkManager.parallelExecutor());
    }

    private static Runnable createRunnableWithCatch(Runnable r) {
        return () -> {
            block3: {
                if (loadingComplete) {
                    return;
                }
                try {
                    r.run();
                }
                catch (LoadingFailedException e) {
                    if (error != null) break block3;
                    error = e;
                }
            }
        };
    }

    private static void startModLoading(ModWorkManager.DrivenExecutor syncExecutor, Executor parallelExecutor) {
        ClientModLoader.createRunnableWithCatch(() -> ModLoader.get().loadMods(syncExecutor, parallelExecutor, ImmediateWindowHandler::renderTick)).run();
    }

    private static void finishModLoading(ModWorkManager.DrivenExecutor syncExecutor, Executor parallelExecutor) {
        ClientModLoader.createRunnableWithCatch(() -> ModLoader.get().finishMods(syncExecutor, parallelExecutor, ImmediateWindowHandler::renderTick)).run();
        loading = false;
        loadingComplete = true;
        syncExecutor.execute(() -> ClientModLoader.mc.f_91066_.load(true));
    }

    public static VersionChecker.Status checkForUpdates() {
        boolean anyOutdated = ModList.get().getMods().stream().map(VersionChecker::getResult).map(VersionChecker.CheckResult::status).anyMatch(VersionChecker.Status::isOutdated);
        return anyOutdated ? VersionChecker.Status.OUTDATED : null;
    }

    public static boolean completeModLoading() {
        List<ModLoadingWarning> warnings = ModLoader.get().getWarnings();
        boolean showWarnings = ForgeConfig.CLIENT.showLoadWarnings();
        if (!showWarnings) {
            if (!warnings.isEmpty()) {
                LOGGER.warn(Logging.LOADING, "Mods loaded with {} warning(s)", (Object)warnings.size());
                warnings.forEach(warning -> LOGGER.warn(Logging.LOADING, warning.formatToString()));
            }
            warnings = Collections.emptyList();
        }
        File dumpedLocation = null;
        if (error == null) {
            MinecraftForge.EVENT_BUS.start();
        } else {
            LanguageHook.loadForgeAndMCLangs();
            dumpedLocation = CrashReportExtender.dumpModLoadingCrashReport(LOGGER, error, ClientModLoader.mc.f_91069_);
        }
        if (error != null || !warnings.isEmpty()) {
            mc.m_91152_((Screen)new LoadingErrorScreen(error, warnings, dumpedLocation));
            return true;
        }
        return false;
    }

    public static boolean isLoading() {
        return loading;
    }

    private static RepositorySource buildPackFinder(Map<IModFile, ? extends PathPackResources> modResourcePacks) {
        return packAcceptor -> ClientModLoader.clientPackFinder(modResourcePacks, packAcceptor);
    }

    private static void clientPackFinder(Map<IModFile, ? extends PathPackResources> modResourcePacks, Consumer<Pack> packAcceptor) {
        ArrayList<PathPackResources> hiddenPacks = new ArrayList<PathPackResources>();
        for (Map.Entry<IModFile, ? extends PathPackResources> e : modResourcePacks.entrySet()) {
            IModInfo mod = (IModInfo)e.getKey().getModInfos().get(0);
            String name = "mod:" + mod.getModId();
            Pack modPack = Pack.m_245429_((String)name, (Component)Component.m_237113_((String)e.getValue().m_5542_()), (boolean)false, id -> (PackResources)e.getValue(), (PackType)PackType.CLIENT_RESOURCES, (Pack.Position)Pack.Position.BOTTOM, (PackSource)PackSource.f_10527_);
            if (modPack == null) {
                ModLoader.get().addWarning(new ModLoadingWarning(mod, ModLoadingStage.ERROR, "fml.modloading.brokenresources", new Object[]{e.getKey()}));
                continue;
            }
            LOGGER.debug(Logging.CORE, "Generating PackInfo named {} for mod file {}", (Object)name, (Object)e.getKey().getFilePath());
            if (mod.getOwningFile().showAsResourcePack()) {
                packAcceptor.accept(modPack);
                continue;
            }
            hiddenPacks.add(e.getValue());
        }
        Pack modResourcesPack = Pack.m_245429_((String)"mod_resources", (Component)Component.m_237113_((String)"Mod Resources"), (boolean)true, id -> new DelegatingPackResources(id, false, new PackMetadataSection((Component)Component.m_237110_((String)"fml.resources.modresources", (Object[])new Object[]{hiddenPacks.size()}), SharedConstants.m_183709_().m_264084_(PackType.CLIENT_RESOURCES)), hiddenPacks), (PackType)PackType.CLIENT_RESOURCES, (Pack.Position)Pack.Position.BOTTOM, (PackSource)PackSource.f_10527_);
        packAcceptor.accept(modResourcesPack);
    }
}

