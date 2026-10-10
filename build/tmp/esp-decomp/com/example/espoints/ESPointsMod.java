/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.server.MinecraftServer
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.api.distmarker.OnlyIn
 *  net.minecraftforge.client.ConfigScreenHandler$ConfigScreenFactory
 *  net.minecraftforge.client.event.RegisterGuiOverlaysEvent
 *  net.minecraftforge.client.gui.overlay.IGuiOverlay
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.event.OnDatapackSyncEvent
 *  net.minecraftforge.event.RegisterCommandsEvent
 *  net.minecraftforge.event.server.ServerStartedEvent
 *  net.minecraftforge.eventbus.api.IEventBus
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.ModLoadingContext
 *  net.minecraftforge.fml.common.Mod
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 *  net.minecraftforge.fml.config.IConfigSpec
 *  net.minecraftforge.fml.config.ModConfig$Type
 *  net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
 *  net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
 *  net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext
 *  net.minecraftforge.server.ServerLifecycleHooks
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.example.espoints;

import com.example.espoints.capturepoint.CapturePointManager;
import com.example.espoints.client.gui.ServerConfigScreen;
import com.example.espoints.command.HCRCommand;
import com.example.espoints.config.ModConfig;
import com.example.espoints.config.TacticalMapConfig;
import com.example.espoints.config.TeamfightJsonConfig;
import com.example.espoints.hud.AreaInfoHUD;
import com.example.espoints.hud.CapturePointHUD;
import com.example.espoints.hud.CurrentCapturePointHUD;
import com.example.espoints.hud.MessagePopup;
import com.example.espoints.hud.ReinforcementsHUD;
import com.example.espoints.hud.TacticalMapHUD;
import com.example.espoints.integration.EspetroBattlefieldIntegration;
import com.example.espoints.network.NetworkHandler;
import com.example.espoints.network.RequestRateLimiter;
import com.example.espoints.network.RequestTacticalMapTileMessage;
import com.example.espoints.network.SyncTacticalMapBackgroundMessage;
import com.example.espoints.network.SyncTacticalMapConfigMessage;
import com.example.espoints.tactical.TacticalMarkerManager;
import com.example.espoints.tile.TacticalMapTileService;
import com.example.espoints.util.TutorialManager;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.IConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(value="espoints")
public class ESPointsMod {
    public static final String MOD_ID = "espoints";
    public static final Logger LOGGER = LogManager.getLogger();
    private static ESPointsMod INSTANCE;
    @OnlyIn(value=Dist.CLIENT)
    private static CapturePointHUD capturePointHUD;
    @OnlyIn(value=Dist.CLIENT)
    private static AreaInfoHUD areaInfoHUD;
    @OnlyIn(value=Dist.CLIENT)
    private static CurrentCapturePointHUD currentCapturePointHUD;
    @OnlyIn(value=Dist.CLIENT)
    private static ReinforcementsHUD reinforcementsHUD;
    @OnlyIn(value=Dist.CLIENT)
    private static MessagePopup messagePopup;

    public ESPointsMod(FMLJavaModLoadingContext context) {
        INSTANCE = this;
        IEventBus modEventBus = context.getModEventBus();
        context.registerConfig(ModConfig.Type.COMMON, (IConfigSpec)ModConfig.SPEC);
        context.registerConfig(ModConfig.Type.CLIENT, (IConfigSpec)TacticalMapConfig.SPEC);
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);
        MinecraftForge.EVENT_BUS.register((Object)this);
        MinecraftForge.EVENT_BUS.register((Object)CapturePointManager.getInstance());
        MinecraftForge.EVENT_BUS.register((Object)new EspetroBattlefieldIntegration());
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        NetworkHandler.registerMessages();
    }

    private void clientSetup(FMLClientSetupEvent event) {
        capturePointHUD = new CapturePointHUD();
        areaInfoHUD = new AreaInfoHUD();
        currentCapturePointHUD = new CurrentCapturePointHUD();
        reinforcementsHUD = new ReinforcementsHUD();
        messagePopup = MessagePopup.getInstance();
        LOGGER.info("HCR Points Mod\u5ba2\u6237\u7aef\u521d\u59cb\u5316\u5b8c\u6210\uff01");
        TutorialManager.generateTutorialFiles();
        this.registerConfigScreens();
    }

    @OnlyIn(value=Dist.CLIENT)
    private void registerConfigScreens() {
        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () -> new ConfigScreenHandler.ConfigScreenFactory((mc, parent) -> new ServerConfigScreen((Screen)parent)));
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        HCRCommand.register((CommandDispatcher<CommandSourceStack>)event.getDispatcher());
    }

    @SubscribeEvent
    public void onServerStarted(ServerStartedEvent event) {
        TacticalMarkerManager.reset();
        CapturePointManager.getInstance().resetTransientSyncCaches();
        TeamfightJsonConfig.clearFrozenSnapshot();
        TacticalMapTileService.get().clear();
        RequestTacticalMapTileMessage.clearAll();
        RequestRateLimiter.clearAll();
    }

    @SubscribeEvent
    public void onDatapackSync(OnDatapackSyncEvent event) {
        if (event.getPlayer() != null) {
            SyncTacticalMapConfigMessage.sendToPlayer(event.getPlayer());
            SyncTacticalMapBackgroundMessage.sendToPlayer(event.getPlayer());
        } else {
            SyncTacticalMapConfigMessage.broadcastToAll();
            SyncTacticalMapBackgroundMessage.broadcastToAll();
        }
    }

    public static MinecraftServer getServer() {
        return ServerLifecycleHooks.getCurrentServer();
    }

    public static boolean isServerRunning() {
        return ESPointsMod.getServer() != null;
    }

    public static boolean isClientRunning() {
        return Minecraft.m_91087_() != null;
    }

    @OnlyIn(value=Dist.CLIENT)
    @Mod.EventBusSubscriber(modid="espoints", value={Dist.CLIENT}, bus=Mod.EventBusSubscriber.Bus.MOD)
    private static class ClientModEvents {
        private ClientModEvents() {
        }

        @SubscribeEvent
        public static void registerOverlays(RegisterGuiOverlaysEvent event) {
            if (capturePointHUD == null) {
                capturePointHUD = new CapturePointHUD();
                areaInfoHUD = new AreaInfoHUD();
                currentCapturePointHUD = new CurrentCapturePointHUD();
                reinforcementsHUD = new ReinforcementsHUD();
                messagePopup = MessagePopup.getInstance();
            }
            event.registerBelowAll("capture_point_hud", (IGuiOverlay)capturePointHUD);
            event.registerBelowAll("area_info_hud", (IGuiOverlay)areaInfoHUD);
            event.registerBelowAll("current_capture_point_hud", (IGuiOverlay)currentCapturePointHUD);
            event.registerBelowAll("tactical_map_hud", (IGuiOverlay)TacticalMapHUD.getInstance());
            event.registerBelowAll("reinforcements_hud", (IGuiOverlay)reinforcementsHUD);
            event.registerBelowAll("message_popup", (IGuiOverlay)messagePopup);
        }
    }
}

