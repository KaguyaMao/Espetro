/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.architectury.hooks.PackRepositoryHooks
 *  dev.architectury.platform.Platform
 *  dev.architectury.registry.menu.MenuRegistry
 *  net.minecraft.Util
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.renderer.texture.TextureAtlas
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.packs.PackResources
 *  net.minecraft.server.packs.repository.PackRepository
 *  net.minecraft.server.packs.repository.RepositorySource
 *  net.minecraft.server.packs.resources.PreparableReloadListener
 *  net.minecraft.util.profiling.InactiveProfiler
 *  net.minecraft.util.profiling.ProfilerFiller
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.flag.FeatureFlagSet
 *  net.minecraft.world.item.CreativeModeTabs
 *  net.minecraft.world.level.material.Fluid
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.client;

import dev.architectury.hooks.PackRepositoryHooks;
import dev.architectury.platform.Platform;
import dev.architectury.registry.menu.MenuRegistry;
import dev.latvian.mods.kubejs.CommonProperties;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.KubeJSCommon;
import dev.latvian.mods.kubejs.KubeJSPaths;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.bindings.event.ClientEvents;
import dev.latvian.mods.kubejs.bindings.event.ItemEvents;
import dev.latvian.mods.kubejs.bindings.event.NetworkEvents;
import dev.latvian.mods.kubejs.client.AtlasSpriteRegistryEventJS;
import dev.latvian.mods.kubejs.client.ClientInitEventJS;
import dev.latvian.mods.kubejs.client.ClientProperties;
import dev.latvian.mods.kubejs.client.KubeJSClientEventHandler;
import dev.latvian.mods.kubejs.client.KubeJSErrorScreen;
import dev.latvian.mods.kubejs.client.KubeJSResourcePackFinder;
import dev.latvian.mods.kubejs.client.painter.Painter;
import dev.latvian.mods.kubejs.fluid.FluidBuilder;
import dev.latvian.mods.kubejs.gui.KubeJSMenu;
import dev.latvian.mods.kubejs.gui.KubeJSScreen;
import dev.latvian.mods.kubejs.item.ItemModelPropertiesEventJS;
import dev.latvian.mods.kubejs.net.NetworkEventJS;
import dev.latvian.mods.kubejs.registry.BuilderBase;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.script.ConsoleLine;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.script.ScriptTypeHolder;
import dev.latvian.mods.kubejs.script.data.ExportablePackResources;
import dev.latvian.mods.kubejs.script.data.GeneratedData;
import dev.latvian.mods.kubejs.util.KubeJSPlugins;
import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.util.profiling.InactiveProfiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

public class KubeJSClient
extends KubeJSCommon {
    @Override
    public void init() {
        if (Minecraft.m_91087_() == null) {
            return;
        }
        KubeJSClient.reloadClientScripts();
        new KubeJSClientEventHandler().init();
        PackRepository list = Minecraft.m_91087_().m_91099_();
        PackRepositoryHooks.addSource((PackRepository)list, (RepositorySource)new KubeJSResourcePackFinder());
        KubeJSPlugins.forEachPlugin(KubeJSPlugin::clientInit);
    }

    @Override
    public void reloadClientInternal() {
        KubeJSClient.reloadClientScripts();
    }

    public static void reloadClientScripts() {
        KubeJSClientEventHandler.staticItemTooltips = null;
        Minecraft mc = Minecraft.m_91087_();
        if (mc != null) {
            KubeJS.getClientScriptManager().reload(mc.m_91098_());
        }
    }

    public static void copyDefaultOptionsFile(File optionsFile) {
        Path defOptions;
        if (!optionsFile.exists() && Files.exists(defOptions = KubeJSPaths.CONFIG.resolve("defaultoptions.txt"), new LinkOption[0])) {
            try {
                KubeJS.LOGGER.info("Loaded default options from kubejs/config/defaultoptions.txt");
                Files.copy(defOptions, optionsFile.toPath(), new CopyOption[0]);
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void clientSetup() {
        if (Platform.isDevelopmentEnvironment()) {
            KubeJS.LOGGER.info("CLIENT SETUP");
        }
        ClientEvents.INIT.post(ScriptType.STARTUP, new ClientInitEventJS());
        ItemEvents.MODEL_PROPERTIES.post(ScriptType.STARTUP, new ItemModelPropertiesEventJS());
        ClientEvents.ATLAS_SPRITE_REGISTRY.listenJava(ScriptType.CLIENT, TextureAtlas.f_118259_, event -> {
            AtlasSpriteRegistryEventJS e = (AtlasSpriteRegistryEventJS)event;
            for (BuilderBase<Fluid> builderBase : RegistryInfo.FLUID) {
                if (!(builderBase instanceof FluidBuilder)) continue;
                FluidBuilder b = (FluidBuilder)builderBase;
                e.register(b.stillTexture);
                e.register(b.flowingTexture);
            }
            return null;
        });
        if (!CommonProperties.get().serverOnly) {
            MenuRegistry.registerScreenFactory(KubeJSMenu.KUBEJS_MENU.get(), KubeJSScreen::new);
        }
    }

    @Override
    public void handleDataFromServerPacket(String channel, @Nullable CompoundTag data) {
        if (NetworkEvents.DATA_RECEIVED.hasListeners(channel)) {
            NetworkEvents.DATA_RECEIVED.post((ScriptTypeHolder)ScriptType.CLIENT, (Object)channel, new NetworkEventJS((Player)Minecraft.m_91087_().f_91074_, channel, data));
        }
    }

    @Override
    @Nullable
    public Player getClientPlayer() {
        return Minecraft.m_91087_().f_91074_;
    }

    @Override
    public void paint(CompoundTag tag) {
        Painter.INSTANCE.paint(tag);
    }

    private void reload(PreparableReloadListener listener) {
        long start = System.currentTimeMillis();
        Minecraft mc = Minecraft.m_91087_();
        mc.m_91098_().m_213713_(GeneratedData.INTERNAL_RELOAD.id());
        listener.m_5540_(CompletableFuture::completedFuture, mc.m_91098_(), (ProfilerFiller)InactiveProfiler.f_18554_, (ProfilerFiller)InactiveProfiler.f_18554_, (Executor)Util.m_183991_(), (Executor)mc).thenAccept(unused -> mc.f_91074_.m_213846_((Component)Component.m_237113_((String)"Done! You still may have to reload all assets with F3 + T")));
    }

    @Override
    public void reloadTextures() {
        this.reload((PreparableReloadListener)Minecraft.m_91087_().m_91097_());
    }

    @Override
    public void reloadLang() {
        KubeJSClient.reloadClientScripts();
        this.reload((PreparableReloadListener)Minecraft.m_91087_().m_91102_());
    }

    @Override
    public void generateTypings(CommandSourceStack source) {
        source.m_288197_(() -> Component.m_237113_((String)"WIP!"), false);
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        ClientProperties.reload();
    }

    @Override
    public void reloadStartupScripts(boolean dedicated) {
        Minecraft mc = Minecraft.m_91087_();
        if (mc.f_91074_ != null) {
            CreativeModeTabs.f_268496_ = null;
            CreativeModeTabs.m_269226_((FeatureFlagSet)mc.f_91074_.f_108617_.m_247016_(), (mc.f_91074_.m_36337_() && (Boolean)mc.f_91066_.m_257871_().m_231551_() != false ? 1 : 0) != 0, (HolderLookup.Provider)mc.f_91073_.m_9598_());
        }
    }

    @Override
    public void export(List<ExportablePackResources> packs) {
        for (PackResources pack : Minecraft.m_91087_().m_91098_().m_7536_().toList()) {
            ExportablePackResources e;
            if (!(pack instanceof ExportablePackResources) || packs.contains(e = (ExportablePackResources)pack)) continue;
            packs.add(e);
        }
    }

    @Override
    public void openErrors(ScriptType type) {
        Minecraft.m_91087_().execute(() -> Minecraft.m_91087_().m_91152_((Screen)new KubeJSErrorScreen(Minecraft.m_91087_().f_91080_, type.console)));
    }

    @Override
    public void openErrors(ScriptType type, List<ConsoleLine> errors, List<ConsoleLine> warnings) {
        Minecraft.m_91087_().execute(() -> Minecraft.m_91087_().m_91152_((Screen)new KubeJSErrorScreen(Minecraft.m_91087_().f_91080_, type, null, errors, warnings)));
    }
}

