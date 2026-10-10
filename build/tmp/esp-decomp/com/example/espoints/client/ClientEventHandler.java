/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.KeyMapping
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.api.distmarker.OnlyIn
 *  net.minecraftforge.client.event.ClientPlayerNetworkEvent$LoggingOut
 *  net.minecraftforge.event.TickEvent$ClientTickEvent
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
 *  org.espetro.Espetro
 */
package com.example.espoints.client;

import com.example.espoints.client.AudioManager;
import com.example.espoints.client.ClientBattleState;
import com.example.espoints.client.ClientPlayerIdentityState;
import com.example.espoints.client.ClientTacticalMapTileCache;
import com.example.espoints.client.PingWheelMarkerBridge;
import com.example.espoints.client.TacticalMarkRadialController;
import com.example.espoints.client.gui.MDRenderScreen;
import com.example.espoints.client.gui.TacticalMapConfigScreen;
import com.example.espoints.config.TacticalMapJsonConfig;
import com.example.espoints.hud.TacticalMapHUD;
import com.example.espoints.network.NetworkHandler;
import com.example.espoints.network.RequestCapturePointOverviewMessage;
import com.example.espoints.tactical.ClientTacticalMarkerState;
import com.example.espoints.util.ModLogger;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.espetro.Espetro;

@OnlyIn(value=Dist.CLIENT)
@Mod.EventBusSubscriber(modid="espoints", value={Dist.CLIENT})
public class ClientEventHandler {
    private static final String KEY_CATEGORY = "key.categories.espoints";
    private static final String KEY_OPEN_GUI = "key.espoints.open_gui";
    private static final String KEY_TACTICAL_MAP = "key.espoints.tactical_map";
    private static final String KEY_MAP_CONFIG = "key.espoints.map_config";
    private static final String KEY_OPEN_MD_READER = "key.espoints.open_md_reader";
    public static final KeyMapping OPEN_GUI_KEY = new KeyMapping("key.espoints.open_gui", -1, "key.categories.espoints");
    public static final KeyMapping TACTICAL_MAP_KEY = new KeyMapping("key.espoints.tactical_map", 86, "key.categories.espoints");
    public static final KeyMapping MAP_CONFIG_KEY = new KeyMapping("key.espoints.map_config", 88, "key.categories.espoints");
    public static final KeyMapping OPEN_MD_READER_KEY = new KeyMapping("key.espoints.open_md_reader", -1, "key.categories.espoints");
    private static boolean wasGuiKeyPressed = false;
    private static boolean wasTacticalMapKeyPressed = false;
    private static boolean wasMapRangeIncreasePressed = false;
    private static boolean wasMapRangeDecreasePressed = false;
    private static boolean wasMapConfigKeyPressed = false;
    private static boolean wasMdReaderKeyPressed = false;
    private static int tacticalMarkerVisibilityTicks;

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        AudioManager.getAudioFilePath();
        ModLogger.info("\u5df2\u68c0\u67e5\u5e76\u786e\u4fddfightBGM\u6587\u4ef6\u5939\u5b58\u5728");
        event.enqueueWork(TacticalMarkRadialController::initialize);
    }

    @SubscribeEvent
    public static void onClientLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        TacticalMapHUD.getInstance().clearServerSyncedBackgroundState();
        TacticalMapJsonConfig.apply(TacticalMapJsonConfig.createDefault(), "client disconnected");
        ClientBattleState.get().clear();
        ClientPlayerIdentityState.get().clear();
        ClientTacticalMarkerState.clear();
        tacticalMarkerVisibilityTicks = 0;
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Minecraft mc = Minecraft.m_91087_();
            ClientTacticalMapTileCache.get().tickRequests();
            ClientTacticalMapTileCache.get().drainUploadQueue(8, 8000000L);
            boolean isGuiKeyPressed = OPEN_GUI_KEY.m_90857_();
            if (isGuiKeyPressed && !wasGuiKeyPressed && mc.f_91074_ != null && mc.f_91080_ == null && !ClientEventHandler.conflictsWithEspetroKey(OPEN_GUI_KEY)) {
                NetworkHandler.INSTANCE.sendToServer((Object)new RequestCapturePointOverviewMessage());
            }
            wasGuiKeyPressed = isGuiKeyPressed;
            boolean isTacticalMapPressed = TACTICAL_MAP_KEY.m_90857_();
            if (isTacticalMapPressed && !wasTacticalMapKeyPressed && mc.f_91074_ != null) {
                TacticalMapHUD.getInstance().toggleMapVisibility();
            }
            wasTacticalMapKeyPressed = isTacticalMapPressed;
            wasMapRangeIncreasePressed = false;
            wasMapRangeDecreasePressed = false;
            boolean isMapConfigPressed = MAP_CONFIG_KEY.m_90857_();
            if (isMapConfigPressed && !wasMapConfigKeyPressed && mc.f_91074_ != null && mc.f_91080_ == null && !ClientEventHandler.conflictsWithEspetroKey(MAP_CONFIG_KEY)) {
                mc.m_91152_((Screen)new TacticalMapConfigScreen(null));
            }
            wasMapConfigKeyPressed = isMapConfigPressed;
            boolean isMdReaderPressed = OPEN_MD_READER_KEY.m_90857_();
            if (isMdReaderPressed && !wasMdReaderKeyPressed && mc.f_91074_ != null && mc.f_91080_ == null && !ClientEventHandler.conflictsWithEspetroKey(OPEN_MD_READER_KEY)) {
                mc.m_91152_((Screen)new MDRenderScreen());
            }
            wasMdReaderKeyPressed = isMdReaderPressed;
            TacticalMarkRadialController.tick(mc);
            if (++tacticalMarkerVisibilityTicks >= 20) {
                tacticalMarkerVisibilityTicks = 0;
                PingWheelMarkerBridge.refreshVisibility(ClientTacticalMarkerState.getMarkers());
            }
        }
    }

    private static boolean conflictsWithEspetroKey(KeyMapping espointsKey) {
        return ClientEventHandler.sameKey(espointsKey, Espetro.KEY_TEAM) || ClientEventHandler.sameKey(espointsKey, Espetro.KEY_CLASS) || ClientEventHandler.sameKey(espointsKey, Espetro.KEY_SKILL) || ClientEventHandler.sameKey(espointsKey, Espetro.KEY_RADIAL);
    }

    private static boolean sameKey(KeyMapping espointsKey, Object espetroKey) {
        KeyMapping key;
        return espetroKey instanceof KeyMapping && espointsKey.m_90850_(key = (KeyMapping)espetroKey);
    }
}

