/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cc.sighs.auratip.api.action.Actions
 *  cc.sighs.auratip.api.client.RadialMenuClientApi
 *  cc.sighs.auratip.api.radiamenu.RadialMenuBuilder
 *  cc.sighs.auratip.api.radiamenu.RadialMenuRegistry
 *  cc.sighs.auratip.client.render.RadialMenuOverlay
 *  cc.sighs.auratip.data.RadialMenuData
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.HitResult
 *  net.minecraft.world.phys.HitResult$Type
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.api.distmarker.OnlyIn
 *  nx.pingwheel.common.config.ClientConfig
 *  nx.pingwheel.common.core.PingController
 *  nx.pingwheel.common.math.Raycast
 *  nx.pingwheel.common.util.InputUtils
 *  org.espetro.client.gui.ClientGameState
 *  org.espetro.team.GamePhase
 */
package com.example.espoints.client;

import cc.sighs.auratip.api.action.Actions;
import cc.sighs.auratip.api.client.RadialMenuClientApi;
import cc.sighs.auratip.api.radiamenu.RadialMenuBuilder;
import cc.sighs.auratip.api.radiamenu.RadialMenuRegistry;
import cc.sighs.auratip.client.render.RadialMenuOverlay;
import cc.sighs.auratip.data.RadialMenuData;
import com.example.espoints.network.NetworkHandler;
import com.example.espoints.network.PlaceTacticalMarkerMessage;
import com.example.espoints.network.RequestTacticalMarkersMessage;
import com.example.espoints.tactical.ClientTacticalMarkerState;
import com.example.espoints.tactical.TacticalMarkerIcons;
import com.example.espoints.tactical.TacticalMarkerType;
import com.example.espoints.util.EspetroTeamBridge;
import com.example.espoints.util.ModLogger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import nx.pingwheel.common.config.ClientConfig;
import nx.pingwheel.common.core.PingController;
import nx.pingwheel.common.math.Raycast;
import nx.pingwheel.common.util.InputUtils;
import org.espetro.client.gui.ClientGameState;
import org.espetro.team.GamePhase;

@OnlyIn(value=Dist.CLIENT)
public final class TacticalMarkRadialController {
    private static final int OPEN_DELAY_TICKS = 4;
    private static final String OWNER = "espoints_mark";
    private static final ResourceLocation MENU_ID = ResourceLocation.fromNamespaceAndPath((String)"espoints", (String)"tactical_mark");
    private static final ResourceLocation PLACE_ACTION = ResourceLocation.fromNamespaceAndPath((String)"espoints", (String)"place_tactical_mark");
    private static boolean initialized;
    private static boolean actionsRegistered;
    private static boolean keyWasDown;
    private static boolean ownsOverlay;
    private static boolean consumedUntilRelease;
    private static int heldTicks;
    private static long lastRequestMarkersMs;

    private TacticalMarkRadialController() {
    }

    public static void initialize() {
        if (initialized) {
            return;
        }
        initialized = true;
        TacticalMarkRadialController.registerActionsOnce();
        TacticalMarkRadialController.publishMenu();
        ModLogger.info("\u6218\u672f\u6807\u70b9 AuraTip \u8f6e\u76d8\u5df2\u6ce8\u518c (menu=" + String.valueOf(MENU_ID) + ")");
    }

    private static void registerActionsOnce() {
        if (actionsRegistered) {
            return;
        }
        actionsRegistered = true;
        Actions.register((ResourceLocation)PLACE_ACTION, params -> {
            try {
                TacticalMarkRadialController.placeAtLook(TacticalMarkerType.valueOf(params.getString("type", "")));
            }
            catch (IllegalArgumentException ignored) {
                return;
            }
            consumedUntilRelease = true;
            ownsOverlay = false;
        });
    }

    private static void publishMenu() {
        RadialMenuRegistry.setMenus((String)OWNER, List.of(TacticalMarkRadialController.buildMenuData()));
    }

    static RadialMenuData buildMenuData() {
        RadialMenuBuilder builder = new RadialMenuBuilder(MENU_ID).radii(44, 96).animationSpeed(1.25f).ringColors(List.of("#E6141719", "#F02A2D2F"));
        for (TacticalMarkerType type : TacticalMarkerType.selectableValues()) {
            String color = switch (type) {
                case TacticalMarkerType.ATTACK_HERE -> "#FFFFB52E";
                case TacticalMarkerType.DEFEND_HERE -> "#FF4D9DFF";
                case TacticalMarkerType.ENEMY_INFANTRY, TacticalMarkerType.ENEMY_TANK, TacticalMarkerType.ENEMY_IFV, TacticalMarkerType.ENEMY_LIGHT_VEHICLE, TacticalMarkerType.ENEMY_HELICOPTER -> "#FFE05252";
                default -> "#FFE05252";
            };
            builder = builder.slot("espoints.mark." + type.name(), TacticalMarkerIcons.textureFor(type), Actions.script((ResourceLocation)PLACE_ACTION, Map.of("type", type.name())), (Component)Component.m_237113_((String)type.getDisplayName()), color, true);
        }
        return builder.build();
    }

    public static void tick(Minecraft mc) {
        if (!initialized) {
            TacticalMarkRadialController.initialize();
        }
        if (mc == null || mc.f_91074_ == null) {
            TacticalMarkRadialController.reset(false);
            return;
        }
        if (!TacticalMarkRadialController.isActiveBattlefield(mc)) {
            TacticalMarkRadialController.reset(false);
            keyWasDown = false;
            return;
        }
        PingController.revokePingAction();
        boolean down = InputUtils.KEY_BINDING_PING.m_90857_();
        if (!down) {
            if (keyWasDown) {
                TacticalMarkRadialController.finishSelection(mc);
            }
            keyWasDown = false;
            heldTicks = 0;
            consumedUntilRelease = false;
            return;
        }
        keyWasDown = true;
        if (consumedUntilRelease || mc.f_91080_ != null) {
            return;
        }
        if (ownsOverlay) {
            return;
        }
        if (++heldTicks < 4) {
            return;
        }
        if (!TacticalMarkRadialController.canLocalPlace()) {
            mc.f_91074_.m_5661_((Component)Component.m_237113_((String)"\u00a7c\u5f53\u524d\u8eab\u4efd\u4e0d\u80fd\u653e\u7f6e\u6218\u672f\u6807\u70b9\u3002"), true);
            consumedUntilRelease = true;
            return;
        }
        TacticalMarkRadialController.requestMarkersIfStale();
        if (TacticalMarkRadialController.openAuraMenu()) {
            ownsOverlay = true;
        } else {
            mc.f_91074_.m_5661_((Component)Component.m_237113_((String)"\u00a7c\u65e0\u6cd5\u6253\u5f00\u6807\u70b9\u8f6e\u76d8\uff08\u83dc\u5355\u672a\u6ce8\u518c\u6216 AuraTip \u5f02\u5e38\uff09\u3002"), true);
            consumedUntilRelease = true;
        }
    }

    private static void requestMarkersIfStale() {
        long now = System.currentTimeMillis();
        if (now - lastRequestMarkersMs < 1500L) {
            return;
        }
        if (ClientTacticalMarkerState.getMarkers().isEmpty()) {
            lastRequestMarkersMs = now;
            NetworkHandler.INSTANCE.sendToServer((Object)new RequestTacticalMarkersMessage());
        }
    }

    private static boolean canLocalPlace() {
        LocalPlayer p = Minecraft.m_91087_().f_91074_;
        if (p == null) {
            return false;
        }
        return EspetroTeamBridge.canPlaceTacticalMarkerClientHint((Player)p);
    }

    public static boolean shouldSuppressDefaultPing() {
        return TacticalMarkRadialController.isActiveBattlefield(Minecraft.m_91087_());
    }

    static List<String> menuSlotIds() {
        ArrayList<String> ids = new ArrayList<String>();
        for (TacticalMarkerType type : TacticalMarkerType.selectableValues()) {
            ids.add("espoints.mark." + type.name());
        }
        return ids;
    }

    private static boolean isActiveBattlefield(Minecraft mc) {
        if (mc == null || mc.f_91074_ == null || mc.f_91073_ == null) {
            return false;
        }
        GamePhase phase = ClientGameState.getCurrentPhase();
        return (phase == GamePhase.DEPLOYING || phase == GamePhase.BATTLE) && !Level.f_46428_.equals((Object)mc.f_91073_.m_46472_());
    }

    private static void finishSelection(Minecraft mc) {
        if (!ownsOverlay) {
            TacticalMarkRadialController.reset(false);
            return;
        }
        if (RadialMenuOverlay.INSTANCE.isActive()) {
            double mouseX = mc.f_91067_.m_91589_() * (double)mc.m_91268_().m_85445_() / (double)mc.m_91268_().m_85443_();
            double mouseY = mc.f_91067_.m_91594_() * (double)mc.m_91268_().m_85446_() / (double)mc.m_91268_().m_85444_();
            RadialMenuOverlay.INSTANCE.mouseClicked(mouseX, mouseY, 0);
        }
        TacticalMarkRadialController.reset(true);
    }

    private static void reset(boolean keepConsumed) {
        heldTicks = 0;
        ownsOverlay = false;
        if (!keepConsumed) {
            consumedUntilRelease = false;
        }
    }

    private static boolean openAuraMenu() {
        if (!TacticalMarkRadialController.ensureMenusRegistered()) {
            return false;
        }
        try {
            RadialMenuClientApi.open((ResourceLocation)MENU_ID);
            return true;
        }
        catch (Throwable t) {
            ModLogger.warn("\u6253\u5f00\u6807\u70b9\u8f6e\u76d8\u5931\u8d25: " + String.valueOf(t));
            return false;
        }
    }

    private static boolean ensureMenusRegistered() {
        if (RadialMenuRegistry.getRuntimeMenu((ResourceLocation)MENU_ID) != null) {
            return true;
        }
        TacticalMarkRadialController.publishMenu();
        return RadialMenuRegistry.getRuntimeMenu((ResourceLocation)MENU_ID) != null;
    }

    private static void placeAtLook(TacticalMarkerType type) {
        ClientConfig pingConfig;
        double maxReach;
        float pt;
        Minecraft mc = Minecraft.m_91087_();
        if (mc.f_91074_ == null || mc.f_91073_ == null || type == null) {
            return;
        }
        Object cam = mc.m_91288_() != null ? mc.m_91288_() : mc.f_91074_;
        Vec3 look = cam.m_20252_(pt = mc.m_91296_());
        HitResult hit = Raycast.traceDirectional((Vec3)look, (float)pt, (double)(maxReach = Math.min(256.0, (double)Math.min((pingConfig = (ClientConfig)ClientConfig.HANDLER.getConfig()).getRaycastDistance(), pingConfig.getPingDistance()))), (boolean)cam.m_6047_());
        if (hit == null || hit.m_6662_() == HitResult.Type.MISS) {
            mc.f_91074_.m_5661_((Component)Component.m_237113_((String)"\u00a77\u51c6\u661f\u6ca1\u6709\u6307\u5411\u53ef\u6807\u8bb0\u4f4d\u7f6e\u3002"), true);
            return;
        }
        Vec3 pos = hit.m_82450_().m_82520_(0.0, 0.25, 0.0);
        NetworkHandler.INSTANCE.sendToServer((Object)new PlaceTacticalMarkerMessage(type, pos.f_82479_, pos.f_82480_, pos.f_82481_));
    }
}

