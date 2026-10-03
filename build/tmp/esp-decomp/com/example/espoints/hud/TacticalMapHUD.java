/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.math.Axis
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.player.AbstractClientPlayer
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.client.renderer.GameRenderer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.saveddata.maps.MapDecoration$Type
 *  net.minecraftforge.client.event.InputEvent$MouseScrollingEvent
 *  net.minecraftforge.client.event.ScreenEvent$MouseButtonPressed$Pre
 *  net.minecraftforge.client.event.ScreenEvent$MouseScrolled$Pre
 *  net.minecraftforge.client.gui.overlay.ForgeGui
 *  net.minecraftforge.client.gui.overlay.IGuiOverlay
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.eventbus.api.EventPriority
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  org.lwjgl.glfw.GLFW
 */
package com.example.espoints.hud;

import com.example.espoints.capturepoint.CapturePoint;
import com.example.espoints.capturepoint.DisplayState;
import com.example.espoints.client.ClientBattleState;
import com.example.espoints.client.ClientPlayerIdentityState;
import com.example.espoints.client.ClientTacticalMapTileCache;
import com.example.espoints.config.MapImageQuality;
import com.example.espoints.config.MapPlayerDisplayConfig;
import com.example.espoints.config.TacticalMapConfig;
import com.example.espoints.config.TacticalMapJsonConfig;
import com.example.espoints.hud.MapDisplayMode;
import com.example.espoints.network.NetworkHandler;
import com.example.espoints.network.PlaceTacticalMarkerMessage;
import com.example.espoints.network.RemoveTacticalMarkerMessage;
import com.example.espoints.network.RequestTacticalMarkersMessage;
import com.example.espoints.network.SelectArtillerySupportTargetMessage;
import com.example.espoints.network.SyncBastionsMessage;
import com.example.espoints.network.SyncPlayerPositionsMessage;
import com.example.espoints.network.TacticalMapSubscriptionMessage;
import com.example.espoints.tactical.TacticalMarker;
import com.example.espoints.tactical.TacticalMarkerIcons;
import com.example.espoints.tactical.TacticalMarkerType;
import com.example.espoints.tile.TacticalMapCircleMesh;
import com.example.espoints.tile.TacticalMapLabelLayout;
import com.example.espoints.tile.TacticalMapLayerPicker;
import com.example.espoints.tile.TacticalMapLodPlanner;
import com.example.espoints.tile.TacticalMapPyramidLayout;
import com.example.espoints.tile.TacticalMapStaticProjection;
import com.example.espoints.tile.TacticalMapTileScreenMath;
import com.example.espoints.tile.TacticalMapViewportQuantizer;
import com.example.espoints.util.EspetroTeamBridge;
import com.example.espoints.util.ModLogger;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;

public class TacticalMapHUD
implements IGuiOverlay {
    private static final double ZOOM_FACTOR = 1.25;
    private static final int MAP_TITLE_HEIGHT = 20;
    private static final int EMBEDDED_MAP_TITLE_HEIGHT = 14;
    private static final float EMBEDDED_TEXT_SCALE = 0.68f;
    private static final ResourceLocation VANILLA_MAP_ICONS = ResourceLocation.withDefaultNamespace((String)"textures/map/map_icons.png");
    private static final ResourceLocation SQUAD_HAB_ICON = ResourceLocation.fromNamespaceAndPath((String)"espoints", (String)"textures/gui/squad/hab.png");
    private static final ResourceLocation SQUAD_RADIO_ICON = ResourceLocation.fromNamespaceAndPath((String)"espoints", (String)"textures/gui/squad/radio.png");
    private static final ResourceLocation SQUAD_RALLY_ICON = ResourceLocation.fromNamespaceAndPath((String)"espoints", (String)"textures/gui/squad/rally.png");
    private static final ResourceLocation MAP_SOLDIER = ResourceLocation.fromNamespaceAndPath((String)"espoints", (String)"textures/gui/map/soldier.png");
    private static final ResourceLocation MAP_EN_SOLDIER = ResourceLocation.fromNamespaceAndPath((String)"espoints", (String)"textures/gui/map/en_soldier.png");
    private static final ResourceLocation MAP_TANK = ResourceLocation.fromNamespaceAndPath((String)"espoints", (String)"textures/gui/map/tank.png");
    private static final ResourceLocation MAP_HELI = ResourceLocation.fromNamespaceAndPath((String)"espoints", (String)"textures/gui/map/heli.png");
    private static final ResourceLocation MAP_EN_HELI = ResourceLocation.fromNamespaceAndPath((String)"espoints", (String)"textures/gui/map/en_heli.png");
    private static final ResourceLocation MAP_TRUCK = ResourceLocation.fromNamespaceAndPath((String)"espoints", (String)"textures/gui/map/truck.png");
    private static final ResourceLocation MAP_IFV = ResourceLocation.fromNamespaceAndPath((String)"espoints", (String)"textures/gui/map/map_ifv.png");
    private static final ResourceLocation MAP_FOB = ResourceLocation.fromNamespaceAndPath((String)"espoints", (String)"textures/gui/map/fob.png");
    private static final ResourceLocation MAP_EN_FOB = ResourceLocation.fromNamespaceAndPath((String)"espoints", (String)"textures/gui/map/en_fob.png");
    private static final ResourceLocation MAP_RADIO = ResourceLocation.fromNamespaceAndPath((String)"espoints", (String)"textures/gui/map/radio.png");
    private static final ResourceLocation MAP_HAB = ResourceLocation.fromNamespaceAndPath((String)"espoints", (String)"textures/gui/map/hab.png");
    private static final ResourceLocation MAP_HAB_ACTIVATED = ResourceLocation.fromNamespaceAndPath((String)"espoints", (String)"textures/gui/map/hab_activated.png");
    private static final ResourceLocation MAP_MAINSPAWN = ResourceLocation.fromNamespaceAndPath((String)"espoints", (String)"textures/gui/map/mainspawn.png");
    private static final ResourceLocation MAP_MARK_ATTACK = ResourceLocation.fromNamespaceAndPath((String)"espoints", (String)"textures/gui/map/mark_attack.png");
    private static final ResourceLocation MAP_MARK_DEFEND = ResourceLocation.fromNamespaceAndPath((String)"espoints", (String)"textures/gui/map/mark_defend.png");
    private static final ResourceLocation MAP_RALLY = ResourceLocation.fromNamespaceAndPath((String)"espoints", (String)"textures/gui/map/rally.png");
    private static final ResourceLocation MAP_EN_RALLY = ResourceLocation.fromNamespaceAndPath((String)"espoints", (String)"textures/gui/map/en_rally.png");
    private static final ResourceLocation MAP_ACP = ResourceLocation.fromNamespaceAndPath((String)"espoints", (String)"textures/gui/map/acp.png");
    private static final ResourceLocation MAP_EN_ACP = ResourceLocation.fromNamespaceAndPath((String)"espoints", (String)"textures/gui/map/en_acp.png");
    private static final ResourceLocation MAP_VEHICLE_SUPPLY = ResourceLocation.fromNamespaceAndPath((String)"espoints", (String)"textures/gui/map/vehicle_supply.png");
    private static final int COLOR_FRIENDLY_WHITE = -1;
    private static final int COLOR_ENEMY_RED = -43691;
    private static final int MAP_BACKGROUND_FALLBACK_COLOR = -1440931557;
    private static final int LOCAL_PLAYER_MARKER_SIZE = 7;
    private static final int TEAMMATE_MARKER_SIZE = 6;
    private static final int CAPTURE_POINT_MARKER_SIZE = 3;
    private static final int BASTION_MARKER_SIZE = 12;
    private static final int VEHICLE_SUPPLY_STATION_MARKER_SIZE = 6;
    private static final int BASE_MARKER_SIZE = 7;
    private static final int TACTICAL_MARKER_SIZE = 10;
    private static final int SAME_BATCH_ROUTE_COLOR = -855649946;
    private static final int SELECTED_DEPLOYMENT_FRAME_SIZE = 15;
    private static final int SELECTED_DEPLOYMENT_FRAME_MAX_SIZE = 24;
    private static final long SELECTED_DEPLOYMENT_ANIMATION_MS = 180L;
    private static final double SELECTED_DEPLOYMENT_MATCH_DISTANCE_SQUARED = 64.0;
    private static final long MIN_PLAYER_INTERPOLATION_MS = 50L;
    private static final long MAX_PLAYER_INTERPOLATION_MS = 500L;
    private static final long PLAYER_INTERPOLATION_BUFFER_MS = 50L;
    private static final long MAX_PLAYER_EXTRAPOLATION_MS = 250L;
    private static final double PLAYER_TELEPORT_DISTANCE_SQUARED = 65536.0;
    private static final long SUBSCRIPTION_HEARTBEAT_INTERVAL_MS = 4000L;
    private double lastViewMinX;
    private double lastViewMinY = 0.0;
    private double lastViewMaxX = 1.0;
    private double lastViewMaxY = 1.0;
    private int lastViewScreenWidth = 256;
    private int lastViewScreenHeight = 256;
    private static final int MARKER_MENU_WIDTH = 124;
    private static final int MARKER_MENU_HEADER_HEIGHT = 15;
    private static final int MARKER_MENU_ROW_HEIGHT = 16;
    private static final int[] ROUTE_COLORS = new int[]{-43691, -11141291, -11167233, -171, -43521, -11141121, -21931};
    private boolean isMapVisible = false;
    private double visibleWorldSpan = -1.0;
    private List<CapturePoint> allPoints = List.of();
    private List<SyncBastionsMessage.BastionInfo> visibleBastions = List.of();
    private List<SyncBastionsMessage.BaseInfo> visibleBases = List.of();
    private List<SyncBastionsMessage.VehicleSupplyStationInfo> visibleVehicleSupplyStations = List.of();
    private List<TacticalMarker> visibleTacticalMarkers = List.of();
    private final Map<UUID, SyncPlayerPositionsMessage.PlayerPosition> syncedPlayerPositions = new HashMap<UUID, SyncPlayerPositionsMessage.PlayerPosition>();
    private final Map<UUID, SmoothedPlayerMarker> smoothedPlayerMarkers = new HashMap<UUID, SmoothedPlayerMarker>();
    private final Map<UUID, Integer> cachedPlayerColors = new HashMap<UUID, Integer>();
    private long cachedPlayerIdentityRevision = Long.MIN_VALUE;
    private float prevYaw = 0.0f;
    private float prevPitch = 0.0f;
    private float smoothShakeX = 0.0f;
    private float smoothShakeY = 0.0f;
    private static final float SMOOTH_FACTOR = 0.1f;
    private static final int SHAKE_INTENSITY = 3;
    private boolean draggingMap = false;
    private double dragStartMouseX;
    private double dragStartMouseY;
    private double dragStartCenterX;
    private double dragStartCenterZ;
    private double draggedCenterX;
    private double draggedCenterZ;
    private boolean customMapCenter = false;
    private int lastEmbeddedMapLeft = Integer.MIN_VALUE;
    private int lastEmbeddedMapTop = Integer.MIN_VALUE;
    private int lastEmbeddedMapWidth;
    private int lastEmbeddedMapHeight;
    private Object lastEmbeddedMapScreen;
    private long lastEmbeddedMapRenderMs;
    private boolean compactEmbeddedRendering;
    private int lastRecenterButtonLeft = Integer.MIN_VALUE;
    private int lastRecenterButtonTop = Integer.MIN_VALUE;
    private int lastRecenterButtonWidth;
    private int lastRecenterButtonHeight;
    private MapViewport lastInteractiveViewport;
    private Object lastMarkerRequestScreen;
    private boolean markerMenuVisible;
    private int markerMenuX;
    private int markerMenuY;
    private double pendingMarkerWorldX;
    private double pendingMarkerWorldZ;
    private boolean artillerySelectionMode;
    private boolean suppressMapDragUntilRelease;
    private HoveredMapMarker hoveredMapMarker;
    private double hoveredMapMarkerDistanceSquared = Double.MAX_VALUE;
    private double markerHoverMouseX;
    private double markerHoverMouseY;
    private boolean markerHoverEnabled;
    private boolean hasSelectedDeploymentPoint;
    private double selectedDeploymentX;
    private double selectedDeploymentZ;
    private long selectedDeploymentAnimationStartedAt;
    private long lastSubscriptionHeartbeatMs;
    private final TacticalMapLodPlanner mapLodPlanner = new TacticalMapLodPlanner();
    private final TacticalMapLabelLayout mapLabelLayout = new TacticalMapLabelLayout();
    private final TacticalMapStaticProjection staticProjection = new TacticalMapStaticProjection();
    private long staticMapRevision;
    private Object lastLabelFrameKey;
    private long lastLabelLayoutMs;

    public TacticalMapHUD() {
        MinecraftForge.EVENT_BUS.register((Object)this);
    }

    public void syncVisibleCapturePointsFromServer(List<CapturePoint> syncedPoints) {
        List<CapturePoint> next = List.copyOf(syncedPoints);
        if (!this.allPoints.equals(next)) {
            this.allPoints = next;
            ++this.staticMapRevision;
        }
    }

    public void syncBastionsFromServer(List<SyncBastionsMessage.BastionInfo> bastions) {
        this.visibleBastions = List.copyOf(bastions);
        this.visibleBases = List.of();
        this.visibleVehicleSupplyStations = List.of();
        ++this.staticMapRevision;
    }

    public void syncBastionsFromServer(List<SyncBastionsMessage.BastionInfo> bastions, List<SyncBastionsMessage.BaseInfo> bases) {
        this.syncBastionsFromServer(bastions, bases, List.of());
    }

    public void syncBastionsFromServer(List<SyncBastionsMessage.BastionInfo> bastions, List<SyncBastionsMessage.BaseInfo> bases, List<SyncBastionsMessage.VehicleSupplyStationInfo> vehicleSupplyStations) {
        this.visibleBastions = List.copyOf(bastions);
        this.visibleBases = List.copyOf(bases);
        this.visibleVehicleSupplyStations = vehicleSupplyStations == null ? List.of() : List.copyOf(vehicleSupplyStations);
        ++this.staticMapRevision;
    }

    public void syncTacticalMarkersFromServer(List<TacticalMarker> markers) {
        this.visibleTacticalMarkers = markers == null ? List.of() : List.copyOf(markers);
        ++this.staticMapRevision;
    }

    public void toggleMapVisibility() {
        this.isMapVisible = !this.isMapVisible;
        this.draggingMap = false;
        Minecraft mc = Minecraft.m_91087_();
        if (this.isMapVisible) {
            TacticalMapJsonConfig config = TacticalMapJsonConfig.getInstance();
            this.resetVisibleSpan(config);
            this.customMapCenter = false;
            this.ensureTacticalMapSubscription();
        } else {
            ClientTacticalMapTileCache.get().suspendRequests();
            this.sendTacticalMapSubscription(false);
        }
    }

    public void cycleDisplayMode() {
    }

    public MapDisplayMode getDisplayMode() {
        return MapDisplayMode.TOGGLE_KEY;
    }

    public boolean isMapVisible() {
        return this.isMapVisible;
    }

    public void increaseRenderRange() {
        TacticalMapJsonConfig config = TacticalMapJsonConfig.getInstance();
        TacticalMapJsonConfig.TacticalMapBounds bounds = this.getCurrentBounds(config);
        this.ensureVisibleSpan(config, bounds);
        this.visibleWorldSpan = Math.min(bounds.size(), this.visibleWorldSpan * 1.25);
        this.preserveCustomViewportCenter(bounds);
    }

    public void decreaseRenderRange() {
        TacticalMapJsonConfig config = TacticalMapJsonConfig.getInstance();
        TacticalMapJsonConfig.TacticalMapBounds bounds = this.getCurrentBounds(config);
        this.ensureVisibleSpan(config, bounds);
        this.visibleWorldSpan = Math.max(config.getMinimumRange(bounds), this.visibleWorldSpan / 1.25);
        this.preserveCustomViewportCenter(bounds);
    }

    public void zoomFromMouseWheel(double delta) {
        if (delta > 0.0) {
            this.decreaseRenderRange();
        } else if (delta < 0.0) {
            this.increaseRenderRange();
        }
    }

    public void recenterOnPlayer() {
        TacticalMapJsonConfig config = TacticalMapJsonConfig.getInstance();
        TacticalMapJsonConfig.TacticalMapBounds bounds = this.getCurrentBounds(config);
        this.ensureVisibleSpan(config, bounds);
        LocalPlayer player = Minecraft.m_91087_().f_91074_;
        if (player != null) {
            double[] clamped = this.clampViewportCenter(player.m_20185_(), player.m_20189_(), this.visibleWorldSpan, bounds);
            this.draggedCenterX = clamped[0];
            this.draggedCenterZ = clamped[1];
        }
        this.draggingMap = false;
        this.customMapCenter = false;
    }

    public void onTacticalMapConfigSynced() {
        TacticalMapJsonConfig config = TacticalMapJsonConfig.getInstance();
        this.resetVisibleSpan(config);
        this.draggingMap = false;
        this.customMapCenter = false;
    }

    public void clearServerSyncedBackgroundState() {
        this.isMapVisible = false;
        this.visibleWorldSpan = -1.0;
        this.draggingMap = false;
        this.customMapCenter = false;
        this.lastSubscriptionHeartbeatMs = 0L;
        this.syncedPlayerPositions.clear();
        this.smoothedPlayerMarkers.clear();
        this.cachedPlayerColors.clear();
        this.cachedPlayerIdentityRevision = Long.MIN_VALUE;
        this.visibleBastions = List.of();
        this.visibleBases = List.of();
        this.visibleVehicleSupplyStations = List.of();
        this.visibleTacticalMarkers = List.of();
        this.allPoints = List.of();
        ClientTacticalMapTileCache.get().clear();
        this.mapLodPlanner.reset();
        this.mapLabelLayout.reset();
        ++this.staticMapRevision;
    }

    private void ensureTacticalMapSubscription() {
        long now = System.currentTimeMillis();
        if (this.lastSubscriptionHeartbeatMs > 0L && now - this.lastSubscriptionHeartbeatMs < 4000L) {
            return;
        }
        this.sendTacticalMapSubscription(true);
    }

    private void sendTacticalMapSubscription(boolean active) {
        Minecraft minecraft = Minecraft.m_91087_();
        if (minecraft.m_91403_() == null || minecraft.f_91074_ == null) {
            if (!active) {
                this.lastSubscriptionHeartbeatMs = 0L;
            }
            return;
        }
        if (active) {
            NetworkHandler.INSTANCE.sendToServer((Object)new TacticalMapSubscriptionMessage(true, this.lastViewMinX, this.lastViewMinY, this.lastViewMaxX, this.lastViewMaxY, this.lastViewScreenWidth, this.lastViewScreenHeight));
        } else {
            NetworkHandler.INSTANCE.sendToServer((Object)new TacticalMapSubscriptionMessage(false));
        }
        this.lastSubscriptionHeartbeatMs = active ? System.currentTimeMillis() : 0L;
    }

    public void setSelectedDeploymentPoint(double x, double z) {
        if (!Double.isFinite(x) || !Double.isFinite(z)) {
            this.clearSelectedDeploymentPoint();
            return;
        }
        if (!this.hasSelectedDeploymentPoint || Math.abs(this.selectedDeploymentX - x) > 0.01 || Math.abs(this.selectedDeploymentZ - z) > 0.01) {
            this.selectedDeploymentAnimationStartedAt = System.currentTimeMillis();
        }
        this.selectedDeploymentX = x;
        this.selectedDeploymentZ = z;
        this.hasSelectedDeploymentPoint = true;
    }

    public void clearSelectedDeploymentPoint() {
        this.hasSelectedDeploymentPoint = false;
    }

    public void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        if (!this.isMapVisible) {
            return;
        }
        this.ensureTacticalMapSubscription();
        int margin = 8;
        int mapWidth = Mth.m_14045_((int)((int)((double)screenWidth * 0.32)), (int)220, (int)420);
        int mapHeight = Mth.m_14045_((int)((int)((double)screenHeight * 0.34)), (int)160, (int)300);
        int mapLeft = screenWidth - mapWidth - margin;
        int mapTop = margin;
        this.renderMapArea(guiGraphics, mapLeft, mapTop, mapWidth, mapHeight, "", false, false, partialTick);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void renderEmbeddedMap(GuiGraphics guiGraphics, int mapLeft, int mapTop, int mapWidth, int mapHeight, float partialTick) {
        this.ensureTacticalMapSubscription();
        Minecraft minecraft = Minecraft.m_91087_();
        Screen currentScreen = minecraft.f_91080_;
        if (currentScreen != this.lastEmbeddedMapScreen) {
            this.markerMenuVisible = false;
            this.lastInteractiveViewport = null;
        }
        if (currentScreen != null && currentScreen != this.lastMarkerRequestScreen && minecraft.m_91403_() != null) {
            NetworkHandler.INSTANCE.sendToServer((Object)new RequestTacticalMarkersMessage());
            this.lastMarkerRequestScreen = currentScreen;
        }
        this.lastEmbeddedMapLeft = mapLeft;
        this.lastEmbeddedMapTop = mapTop;
        this.lastEmbeddedMapWidth = mapWidth;
        this.lastEmbeddedMapHeight = mapHeight;
        this.lastEmbeddedMapScreen = currentScreen;
        this.lastEmbeddedMapRenderMs = System.currentTimeMillis();
        this.compactEmbeddedRendering = true;
        try {
            this.renderMapArea(guiGraphics, mapLeft, mapTop, mapWidth, mapHeight, "\u6218\u672f\u5730\u56fe " + this.getRangeText() + " \u9f20\u6807\u6eda\u8f6e\u7f29\u653e", true, partialTick);
        }
        finally {
            this.compactEmbeddedRendering = false;
        }
    }

    public void beginArtillerySelection() {
        this.artillerySelectionMode = true;
        this.markerMenuVisible = false;
        this.suppressMapDragUntilRelease = false;
    }

    public void endArtillerySelection() {
        this.artillerySelectionMode = false;
        this.markerMenuVisible = false;
        this.suppressMapDragUntilRelease = false;
        this.draggingMap = false;
        this.lastInteractiveViewport = null;
        this.clearRecenterButton();
    }

    public void renderArtillerySelectionMap(GuiGraphics guiGraphics, int mapLeft, int mapTop, int mapWidth, int mapHeight, float partialTick) {
        this.ensureTacticalMapSubscription();
        Minecraft minecraft = Minecraft.m_91087_();
        Screen currentScreen = minecraft.f_91080_;
        if (currentScreen != this.lastEmbeddedMapScreen) {
            this.markerMenuVisible = false;
            this.lastInteractiveViewport = null;
        }
        if (currentScreen != null && currentScreen != this.lastMarkerRequestScreen && minecraft.m_91403_() != null) {
            NetworkHandler.INSTANCE.sendToServer((Object)new RequestTacticalMarkersMessage());
            this.lastMarkerRequestScreen = currentScreen;
        }
        this.lastEmbeddedMapLeft = mapLeft;
        this.lastEmbeddedMapTop = mapTop;
        this.lastEmbeddedMapWidth = mapWidth;
        this.lastEmbeddedMapHeight = mapHeight;
        this.lastEmbeddedMapScreen = currentScreen;
        this.lastEmbeddedMapRenderMs = System.currentTimeMillis();
        this.renderMapArea(guiGraphics, mapLeft, mapTop, mapWidth, mapHeight, "", true, false, partialTick);
    }

    public boolean zoomArtillerySelectionMap(double mouseX, double mouseY, double scrollDelta) {
        if (!this.artillerySelectionMode || !this.isInsideLastEmbeddedMap(mouseX, mouseY)) {
            return false;
        }
        this.zoomFromMouseWheel(scrollDelta);
        return true;
    }

    public boolean submitArtillerySelectionTarget(double mouseX, double mouseY) {
        if (!(this.artillerySelectionMode && this.isInsideLastEmbeddedMap(mouseX, mouseY) && this.lastInteractiveViewport != null && this.lastInteractiveViewport.containsScreen(mouseX, mouseY))) {
            return false;
        }
        double worldX = this.lastInteractiveViewport.worldX(mouseX);
        double worldZ = this.lastInteractiveViewport.worldZ(mouseY);
        NetworkHandler.INSTANCE.sendToServer((Object)new SelectArtillerySupportTargetMessage(worldX, worldZ));
        Minecraft.m_91087_().m_91152_(null);
        return true;
    }

    @SubscribeEvent(priority=EventPriority.HIGHEST)
    public void onHudMouseScrolled(InputEvent.MouseScrollingEvent event) {
        Minecraft mc = Minecraft.m_91087_();
        if (mc.f_91074_ == null || mc.f_91080_ != null || mc.f_91066_.f_92062_) {
            return;
        }
        if (!this.isMapVisible) {
            return;
        }
        this.zoomFromMouseWheel(event.getScrollDelta());
        event.setCanceled(true);
    }

    @SubscribeEvent
    public void onScreenMouseScrolled(ScreenEvent.MouseScrolled.Pre event) {
        if (this.isInsideLastEmbeddedMap(event.getMouseX(), event.getMouseY())) {
            this.zoomFromMouseWheel(event.getScrollDelta());
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onScreenMouseClicked(ScreenEvent.MouseButtonPressed.Pre event) {
        TacticalMarker clickedMarker;
        boolean insideEmbeddedMap = this.isInsideLastEmbeddedMap(event.getMouseX(), event.getMouseY());
        if (!insideEmbeddedMap && !this.markerMenuVisible) {
            return;
        }
        if (this.markerMenuVisible) {
            if (event.getButton() == 0) {
                TacticalMarkerType selected = this.markerTypeAt(event.getMouseX(), event.getMouseY());
                this.markerMenuVisible = false;
                this.suppressMapDragUntilRelease = true;
                if (selected != null) {
                    NetworkHandler.INSTANCE.sendToServer((Object)new PlaceTacticalMarkerMessage(selected, this.pendingMarkerWorldX, this.pendingMarkerWorldZ));
                }
                event.setCanceled(true);
                return;
            }
            if (event.getButton() == 1) {
                this.markerMenuVisible = false;
                if (this.lastInteractiveViewport == null || !this.lastInteractiveViewport.containsScreen(event.getMouseX(), event.getMouseY())) {
                    event.setCanceled(true);
                    return;
                }
            } else {
                this.markerMenuVisible = false;
                event.setCanceled(true);
                return;
            }
        }
        if (this.artillerySelectionMode && event.getButton() == 1) {
            this.markerMenuVisible = false;
            if (insideEmbeddedMap && this.lastInteractiveViewport != null && this.lastInteractiveViewport.containsScreen(event.getMouseX(), event.getMouseY())) {
                double worldX = this.lastInteractiveViewport.worldX(event.getMouseX());
                double worldZ = this.lastInteractiveViewport.worldZ(event.getMouseY());
                NetworkHandler.INSTANCE.sendToServer((Object)new SelectArtillerySupportTargetMessage(worldX, worldZ));
                Minecraft.m_91087_().m_91152_(null);
            }
            event.setCanceled(true);
            return;
        }
        if (event.getButton() == 0 && this.isInsideLastRecenterButton(event.getMouseX(), event.getMouseY())) {
            this.recenterOnPlayer();
            event.setCanceled(true);
            return;
        }
        if (event.getButton() == 0 && !this.artillerySelectionMode && this.lastInteractiveViewport != null && this.lastInteractiveViewport.containsScreen(event.getMouseX(), event.getMouseY()) && (clickedMarker = this.findTacticalMarkerAt(event.getMouseX(), event.getMouseY())) != null) {
            LocalPlayer player = Minecraft.m_91087_().f_91074_;
            if (player != null && player.m_20148_().equals(clickedMarker.ownerId())) {
                NetworkHandler.INSTANCE.sendToServer((Object)new RemoveTacticalMarkerMessage(clickedMarker.id()));
            } else if (player != null) {
                player.m_5661_((Component)Component.m_237113_((String)"\u00a7c\u53ea\u80fd\u53d6\u6d88\u81ea\u5df1\u653e\u7f6e\u7684\u6218\u672f\u6807\u70b9\u3002"), true);
            }
            this.suppressMapDragUntilRelease = true;
            this.draggingMap = false;
            event.setCanceled(true);
            return;
        }
        if (event.getButton() == 1 && !this.artillerySelectionMode && this.lastInteractiveViewport != null && this.lastInteractiveViewport.containsScreen(event.getMouseX(), event.getMouseY())) {
            this.pendingMarkerWorldX = this.lastInteractiveViewport.worldX(event.getMouseX());
            this.pendingMarkerWorldZ = this.lastInteractiveViewport.worldZ(event.getMouseY());
            this.openMarkerMenu((int)event.getMouseX(), (int)event.getMouseY());
            event.setCanceled(true);
        }
    }

    private void renderMapArea(GuiGraphics guiGraphics, int mapLeft, int mapTop, int mapWidth, int mapHeight, String title, boolean allowMouseDrag, float partialTick) {
        this.renderMapArea(guiGraphics, mapLeft, mapTop, mapWidth, mapHeight, title, allowMouseDrag, true, partialTick);
    }

    private void renderMapArea(GuiGraphics guiGraphics, int mapLeft, int mapTop, int mapWidth, int mapHeight, String title, boolean allowMouseDrag, boolean showInteractionChrome, float partialTick) {
        if (mapWidth <= 0 || mapHeight <= 0) {
            return;
        }
        int bgColor = -870309856;
        guiGraphics.m_280509_(mapLeft, mapTop, mapLeft + mapWidth, mapTop + mapHeight, bgColor);
        int borderColor = -16777216;
        guiGraphics.m_280509_(mapLeft, mapTop, mapLeft + mapWidth, mapTop + 1, borderColor);
        guiGraphics.m_280509_(mapLeft, mapTop, mapLeft + 1, mapTop + mapHeight, borderColor);
        guiGraphics.m_280509_(mapLeft + mapWidth - 1, mapTop, mapLeft + mapWidth, mapTop + mapHeight, borderColor);
        guiGraphics.m_280509_(mapLeft, mapTop + mapHeight - 1, mapLeft + mapWidth, mapTop + mapHeight, borderColor);
        if (showInteractionChrome && title != null && !title.isBlank()) {
            this.drawMapText(guiGraphics, title, mapLeft + 4, mapTop + (this.compactEmbeddedRendering ? 3 : 5), 0xFFFFFF);
        }
        if (allowMouseDrag && showInteractionChrome) {
            this.renderRecenterButton(guiGraphics, mapLeft, mapTop, mapWidth);
        } else {
            this.clearRecenterButton();
        }
        this.renderBirdsEyeView(guiGraphics, mapLeft, mapTop, mapWidth, mapHeight, allowMouseDrag, showInteractionChrome, partialTick);
        if (allowMouseDrag && showInteractionChrome && !this.artillerySelectionMode) {
            this.renderMarkerMenu(guiGraphics);
        }
    }

    private void renderMarkerMenu(GuiGraphics guiGraphics) {
        if (!this.markerMenuVisible) {
            return;
        }
        Minecraft minecraft = Minecraft.m_91087_();
        double mouseX = this.getGuiMouseX(minecraft);
        double mouseY = this.getGuiMouseY(minecraft);
        TacticalMarkerType[] types = TacticalMarkerType.selectableValues();
        int menuHeight = 15 + types.length * 16 + 4;
        guiGraphics.m_280509_(this.markerMenuX, this.markerMenuY, this.markerMenuX + 124, this.markerMenuY + menuHeight, -266856416);
        guiGraphics.m_280637_(this.markerMenuX, this.markerMenuY, 124, menuHeight, -8947832);
        guiGraphics.m_280056_(minecraft.f_91062_, "\u9009\u62e9\u6218\u672f\u6807\u70b9", this.markerMenuX + 5, this.markerMenuY + 4, -1644826, false);
        for (int i = 0; i < types.length; ++i) {
            TacticalMarkerType type = types[i];
            int rowY = this.markerMenuY + 15 + i * 16;
            boolean hovered = mouseX >= (double)(this.markerMenuX + 2) && mouseX < (double)(this.markerMenuX + 124 - 2) && mouseY >= (double)rowY && mouseY < (double)(rowY + 16);
            guiGraphics.m_280509_(this.markerMenuX + 2, rowY, this.markerMenuX + 124 - 2, rowY + 16, hovered ? -532396971 : -1608244179);
            this.renderTacticalMarkerIcon(guiGraphics, this.markerMenuX + 10, rowY + 8, type);
            guiGraphics.m_280056_(minecraft.f_91062_, type.getDisplayName(), this.markerMenuX + 20, rowY + 4, type.getColor(), false);
        }
    }

    private void openMarkerMenu(int clickX, int clickY) {
        int menuHeight = 15 + TacticalMarkerType.selectableValues().length * 16 + 4;
        int minX = this.lastEmbeddedMapLeft + 2;
        int maxX = this.lastEmbeddedMapLeft + this.lastEmbeddedMapWidth - 124 - 2;
        int minY = this.lastEmbeddedMapTop + 2;
        int maxY = this.lastEmbeddedMapTop + this.lastEmbeddedMapHeight - menuHeight - 2;
        this.markerMenuX = clickX + 8;
        if (this.markerMenuX + 124 > this.lastEmbeddedMapLeft + this.lastEmbeddedMapWidth - 2) {
            this.markerMenuX = clickX - 124 - 8;
        }
        this.markerMenuX = Mth.m_14045_((int)this.markerMenuX, (int)minX, (int)Math.max(minX, maxX));
        this.markerMenuY = Mth.m_14045_((int)(clickY - 6), (int)minY, (int)Math.max(minY, maxY));
        this.markerMenuVisible = true;
    }

    private TacticalMarkerType markerTypeAt(double mouseX, double mouseY) {
        if (mouseX < (double)(this.markerMenuX + 2) || mouseX >= (double)(this.markerMenuX + 124 - 2)) {
            return null;
        }
        int relativeY = (int)mouseY - this.markerMenuY - 15;
        if (relativeY < 0) {
            return null;
        }
        int index = relativeY / 16;
        TacticalMarkerType[] values = TacticalMarkerType.selectableValues();
        return index >= 0 && index < values.length ? values[index] : null;
    }

    private TacticalMarker findTacticalMarkerAt(double mouseX, double mouseY) {
        if (this.lastInteractiveViewport == null) {
            return null;
        }
        LocalPlayer player = Minecraft.m_91087_().f_91074_;
        String localTeam = EspetroTeamBridge.getPlayerTeam((Player)player);
        TacticalMarker nearest = null;
        double nearestDistanceSquared = 49.0;
        for (TacticalMarker marker : this.visibleTacticalMarkers) {
            double dy;
            double dx;
            double distanceSquared;
            if (!EspetroTeamBridge.isSameTeam(localTeam, marker.team()) || this.getTacticalMarkerOpacity(marker) <= 0.0f || !this.lastInteractiveViewport.containsWorld(marker.x(), marker.z()) || !((distanceSquared = (dx = mouseX - this.lastInteractiveViewport.screenXd(marker.x())) * dx + (dy = mouseY - this.lastInteractiveViewport.screenYd(marker.z())) * dy) <= nearestDistanceSquared)) continue;
            nearest = marker;
            nearestDistanceSquared = distanceSquared;
        }
        return nearest;
    }

    private void renderRecenterButton(GuiGraphics guiGraphics, int mapLeft, int mapTop, int mapWidth) {
        String label = "\u5f52\u4e2d";
        float textScale = this.getMapTextScale();
        int labelWidth = Math.round((float)Minecraft.m_91087_().f_91062_.m_92895_(label) * textScale);
        int buttonWidth = this.compactEmbeddedRendering ? Math.max(25, labelWidth + 7) : Math.max(34, labelWidth + 12);
        int buttonHeight = this.compactEmbeddedRendering ? 10 : 14;
        int buttonLeft = mapLeft + mapWidth - buttonWidth - (this.compactEmbeddedRendering ? 4 : 6);
        int buttonTop = mapTop + (this.compactEmbeddedRendering ? 2 : 4);
        this.lastRecenterButtonLeft = buttonLeft;
        this.lastRecenterButtonTop = buttonTop;
        this.lastRecenterButtonWidth = buttonWidth;
        this.lastRecenterButtonHeight = buttonHeight;
        guiGraphics.m_280509_(buttonLeft, buttonTop, buttonLeft + buttonWidth, buttonTop + buttonHeight, -1441787888);
        guiGraphics.m_280637_(buttonLeft, buttonTop, buttonWidth, buttonHeight, -1427709846);
        Objects.requireNonNull(Minecraft.m_91087_().f_91062_);
        int textHeight = Math.max(1, Math.round(9.0f * textScale));
        this.drawMapText(guiGraphics, label, buttonLeft + Math.max(0, (buttonWidth - labelWidth) / 2), buttonTop + Math.max(0, (buttonHeight - textHeight) / 2), -171);
    }

    private void clearRecenterButton() {
        this.lastRecenterButtonLeft = Integer.MIN_VALUE;
        this.lastRecenterButtonTop = Integer.MIN_VALUE;
        this.lastRecenterButtonWidth = 0;
        this.lastRecenterButtonHeight = 0;
    }

    private void renderReinforcementsDisplay(GuiGraphics guiGraphics, int mapLeft, int mapTop, int mapWidth, int mapHeight, boolean isMiniMap) {
        ClientBattleState manager = ClientBattleState.get();
        String attackerTeam = manager.attackerTeam();
        String defenderTeam = manager.defenderTeam();
        if (attackerTeam == null || defenderTeam == null) {
            return;
        }
        int attackerReinforcements = manager.reinforcements(attackerTeam);
        int defenderReinforcements = manager.reinforcements(defenderTeam);
        int barHeight = 10;
        int barTop = isMiniMap ? mapTop + 5 : mapTop + 20;
        int barLeft = mapLeft + 5;
        int barRight = mapLeft + mapWidth - 5;
        int barWidth = barRight - barLeft;
        int halfBarWidth = barWidth / 2;
        String defenderHexColor = (String)TacticalMapConfig.defenderProgressBarColor.get();
        String attackerHexColor = (String)TacticalMapConfig.attackerProgressBarColor.get();
        int defenderColor = ModLogger.hexToColor(defenderHexColor, -16755201);
        int attackerColor = ModLogger.hexToColor(attackerHexColor, -43776);
        int defenderInitialReinforcements = manager.initialReinforcements(defenderTeam);
        int defenderBarWidth = 0;
        if (defenderInitialReinforcements > 0) {
            defenderBarWidth = (int)((double)defenderReinforcements / (double)defenderInitialReinforcements * (double)halfBarWidth);
        }
        guiGraphics.m_280509_(barLeft, barTop, barLeft + halfBarWidth, barTop + barHeight, 0x44000000);
        guiGraphics.m_280509_(barLeft, barTop, barLeft + defenderBarWidth, barTop + barHeight, defenderColor);
        int attackerInitialReinforcements = manager.initialReinforcements(attackerTeam);
        int attackerBarWidth = 0;
        if (attackerInitialReinforcements > 0) {
            attackerBarWidth = (int)((double)attackerReinforcements / (double)attackerInitialReinforcements * (double)halfBarWidth);
        }
        int attackerBarLeft = barLeft + halfBarWidth;
        guiGraphics.m_280509_(attackerBarLeft, barTop, attackerBarLeft + halfBarWidth, barTop + barHeight, 0x44000000);
        guiGraphics.m_280509_(attackerBarLeft + halfBarWidth - attackerBarWidth, barTop, attackerBarLeft + halfBarWidth, barTop + barHeight, attackerColor);
        Minecraft minecraft = Minecraft.m_91087_();
        int textY = isMiniMap ? barTop - 10 : barTop + barHeight + 2;
        String defenderText = defenderTeam + ": " + defenderReinforcements;
        guiGraphics.m_280056_(minecraft.f_91062_, defenderText, barLeft, textY, 0xFFFFFF, false);
        String attackerText = attackerTeam + ": " + attackerReinforcements;
        guiGraphics.m_280056_(minecraft.f_91062_, attackerText, barRight - minecraft.f_91062_.m_92895_(attackerText), textY, 0xFFFFFF, false);
    }

    public void syncPlayerPositionsFromServer(Map<UUID, SyncPlayerPositionsMessage.PlayerPosition> playerPositions) {
        this.syncedPlayerPositions.keySet().retainAll(playerPositions.keySet());
        this.smoothedPlayerMarkers.keySet().retainAll(playerPositions.keySet());
        long now = System.currentTimeMillis();
        for (Map.Entry<UUID, SyncPlayerPositionsMessage.PlayerPosition> entry : playerPositions.entrySet()) {
            SyncPlayerPositionsMessage.PlayerPosition position;
            SyncPlayerPositionsMessage.PlayerPosition mergedPosition = position = entry.getValue();
            this.syncedPlayerPositions.put(entry.getKey(), mergedPosition);
            this.smoothedPlayerMarkers.compute(entry.getKey(), (uuid, marker) -> {
                if (marker == null) {
                    return new SmoothedPlayerMarker(mergedPosition, now);
                }
                marker.updateTarget(mergedPosition, now);
                return marker;
            });
        }
    }

    private void renderOtherPlayersOnMap(GuiGraphics guiGraphics, LocalPlayer localPlayer, MapViewport viewport, float partialTick) {
        boolean showPlayerLocations = MapPlayerDisplayConfig.getInstance().isShowPlayerLocations();
        if (!showPlayerLocations) {
            return;
        }
        String localTeam = EspetroTeamBridge.getPlayerTeam((Player)localPlayer);
        long renderTime = System.currentTimeMillis();
        this.refreshPlayerIdentityCache();
        for (Map.Entry<UUID, SyncPlayerPositionsMessage.PlayerPosition> entry : this.syncedPlayerPositions.entrySet()) {
            float otherPlayerYaw;
            UUID playerUUID = entry.getKey();
            SyncPlayerPositionsMessage.PlayerPosition pos = entry.getValue();
            if (playerUUID.equals(localPlayer.m_20148_())) continue;
            SmoothedPlayerMarker smoothedMarker = this.smoothedPlayerMarkers.get(playerUUID);
            if (smoothedMarker != null) {
                smoothedMarker.sample(renderTime);
            }
            double otherPlayerX = smoothedMarker == null ? pos.getX() : smoothedMarker.renderX;
            double otherPlayerZ = smoothedMarker == null ? pos.getZ() : smoothedMarker.renderZ;
            float f = otherPlayerYaw = smoothedMarker == null ? pos.getYaw() : smoothedMarker.renderYaw;
            if (!viewport.containsWorld(otherPlayerX, otherPlayerZ) || !EspetroTeamBridge.isSameTeam(localTeam, pos.getTeamName())) continue;
            double mapPosX = viewport.screenXd(otherPlayerX);
            double mapPosY = viewport.screenYd(otherPlayerZ);
            int teammateColor = this.cachedPlayerColors.computeIfAbsent(playerUUID, ignored -> EspetroTeamBridge.getMapPlayerColor(pos.getName(), pos.getSquadId(), pos.isSquadLeader(), pos.isCommander()));
            this.renderMapPlayerIcon(guiGraphics, mapPosX, mapPosY, otherPlayerYaw, 6, teammateColor);
            this.considerMarkerHover(mapPosX, mapPosY, 6, otherPlayerX, otherPlayerZ);
        }
        if (this.syncedPlayerPositions.isEmpty()) {
            showPlayerLocations = MapPlayerDisplayConfig.getInstance().isShowPlayerLocations();
            if (!showPlayerLocations) {
                return;
            }
            Minecraft minecraft = Minecraft.m_91087_();
            if (minecraft.f_91073_ != null) {
                for (Player otherPlayer : minecraft.f_91073_.m_6907_()) {
                    double otherPlayerZ;
                    Entity otherBody;
                    double otherPlayerX;
                    if (otherPlayer == localPlayer || !EspetroTeamBridge.isPlayerVisibleOnTacticalMap(otherPlayer) || !viewport.containsWorld(otherPlayerX = Mth.m_14139_((double)partialTick, (double)otherBody.f_19854_, (double)(otherBody = TacticalMapHUD.getMapRenderBody((Entity)otherPlayer)).m_20185_()), otherPlayerZ = Mth.m_14139_((double)partialTick, (double)otherBody.f_19856_, (double)otherBody.m_20189_())) || !EspetroTeamBridge.isSameTeam(localTeam, EspetroTeamBridge.getPlayerTeam(otherPlayer))) continue;
                    double mapPosX = viewport.screenXd(otherPlayerX);
                    double mapPosY = viewport.screenYd(otherPlayerZ);
                    int teammateColor = EspetroTeamBridge.getMapPlayerColor(otherPlayer.m_7755_().getString());
                    this.renderMapPlayerIcon(guiGraphics, mapPosX, mapPosY, Mth.m_14189_((float)partialTick, (float)otherPlayer.f_19859_, (float)otherPlayer.m_146908_()), 6, teammateColor);
                    this.considerMarkerHover(mapPosX, mapPosY, 6, otherPlayerX, otherPlayerZ);
                }
            }
        }
    }

    private void refreshPlayerIdentityCache() {
        long revision = ClientPlayerIdentityState.get().revision();
        if (revision != this.cachedPlayerIdentityRevision) {
            this.cachedPlayerColors.clear();
            this.cachedPlayerIdentityRevision = revision;
        }
    }

    private void renderBirdsEyeView(GuiGraphics guiGraphics, int mapLeft, int mapTop, int mapWidth, int mapHeight, boolean allowMouseDrag, boolean showInteractionChrome, float partialTick) {
        Minecraft mc = Minecraft.m_91087_();
        LocalPlayer player = mc.f_91074_;
        if (player == null) {
            return;
        }
        TacticalMapJsonConfig config = TacticalMapJsonConfig.getInstance();
        TacticalMapJsonConfig.TacticalMapBounds bounds = config.getBounds();
        this.ensureVisibleSpan(config, bounds);
        Entity localBody = TacticalMapHUD.getMapRenderBody((Entity)player);
        double playerRenderX = Mth.m_14139_((double)partialTick, (double)localBody.f_19854_, (double)localBody.m_20185_());
        double playerRenderZ = Mth.m_14139_((double)partialTick, (double)localBody.f_19856_, (double)localBody.m_20189_());
        float playerRenderYaw = Mth.m_14189_((float)partialTick, (float)player.f_19859_, (float)player.m_146908_());
        MapViewport content = this.createViewport(config, bounds, playerRenderX, playerRenderZ, mapLeft, mapTop, mapWidth, mapHeight, this.getMapTitleHeight(showInteractionChrome));
        if (allowMouseDrag) {
            this.handleMapDrag(content, bounds);
            this.lastInteractiveViewport = content = this.createViewport(config, bounds, playerRenderX, playerRenderZ, mapLeft, mapTop, mapWidth, mapHeight, this.getMapTitleHeight(showInteractionChrome));
        } else {
            this.draggingMap = false;
            this.customMapCenter = false;
            this.lastInteractiveViewport = null;
        }
        this.beginMarkerHover(allowMouseDrag && showInteractionChrome, content);
        Object overlayKey = this.labelFrameKey(content, config);
        this.mapLabelLayout.begin(overlayKey, new TacticalMapLabelLayout.Bounds(content.left, content.top, content.right(), content.bottom()));
        this.staticProjection.begin(overlayKey);
        guiGraphics.m_280588_(content.left, content.top, content.right(), content.bottom());
        this.renderMapBackground(guiGraphics, content);
        if (config.showGrid) {
            this.renderViewportGrid(guiGraphics, content);
        }
        String localTeam = EspetroTeamBridge.getPlayerTeam((Player)player);
        this.renderBasesOnMap(guiGraphics, content, player, false, localTeam);
        this.renderFobRadiiOnMap(guiGraphics, content, localTeam);
        this.renderCapturePointsOnMap(guiGraphics, content, player, true);
        this.renderBastionsOnMap(guiGraphics, content, player, false, localTeam);
        this.renderVehicleSupplyStationsOnMap(guiGraphics, content, false, localTeam);
        this.renderTacticalMarkersOnMap(guiGraphics, content, false, localTeam);
        this.renderOtherPlayersOnMap(guiGraphics, player, content, partialTick);
        if (EspetroTeamBridge.isPlayerVisibleOnTacticalMap((Player)player) && content.containsWorld(playerRenderX, playerRenderZ)) {
            this.renderMapPlayerIcon(guiGraphics, content.screenXd(playerRenderX), content.screenYd(playerRenderZ), playerRenderYaw, 7, -1);
            this.considerMarkerHover(content.screenXd(playerRenderX), content.screenYd(playerRenderZ), 7, playerRenderX, playerRenderZ);
        }
        this.renderSelectedDeploymentFrame(guiGraphics, content);
        guiGraphics.m_280618_();
        guiGraphics.m_280637_(content.left, content.top, content.width, content.height, -872415232);
        if (showInteractionChrome) {
            this.renderHoveredMarkerCoordinates(guiGraphics, content);
        }
    }

    private void renderBasesOnMap(GuiGraphics guiGraphics, MapViewport viewport, LocalPlayer player, boolean showLabels, String localTeam) {
        if (this.visibleBases.isEmpty()) {
            return;
        }
        for (SyncBastionsMessage.BaseInfo base : this.visibleBases) {
            BlockPos pos;
            if (!this.isVisibleTeamMarker(localTeam, base.getTeam()) || !viewport.containsWorld((pos = base.getPos()).m_123341_(), pos.m_123343_())) continue;
            TacticalMapStaticProjection.ScreenPoint projected = this.staticProjection.point("base:" + base.getTeam() + ":" + pos.m_121878_(), viewport.screenXd(pos.m_123341_()), viewport.screenYd(pos.m_123343_()));
            double mapPosX = projected.x();
            double mapPosY = projected.y();
            int size = Math.round(7.0f * this.getSelectedDeploymentScale(pos.m_123341_(), pos.m_123343_()));
            int baseColor = this.getBastionColor(localTeam, base.getTeam());
            this.renderBaseMarker(guiGraphics, mapPosX, mapPosY, size, baseColor, base.getYaw());
            this.considerMarkerHover(mapPosX, mapPosY, size, pos.m_123341_(), pos.m_123343_());
            if (!showLabels) continue;
            String name = base.getName() == null || base.getName().isEmpty() ? "\u4e3b\u57fa\u5730" : base.getName();
            this.renderMapLabel(guiGraphics, "base:" + base.getTeam() + ":" + pos.m_121878_(), 2, name, mapPosX, mapPosY, size + 3, -size / 2, 0xFFFFFF);
        }
    }

    private void renderBastionsOnMap(GuiGraphics guiGraphics, MapViewport viewport, LocalPlayer player, boolean showLabels, String localTeam) {
        if (this.visibleBastions.isEmpty()) {
            return;
        }
        for (SyncBastionsMessage.BastionInfo bastion : this.visibleBastions) {
            Object name;
            BlockPos pos;
            if (!this.isVisibleTeamMarker(localTeam, bastion.getTeam()) || !viewport.containsWorld((pos = bastion.getPos()).m_123341_(), pos.m_123343_())) continue;
            TacticalMapStaticProjection.ScreenPoint projected = this.staticProjection.point("bastion:" + bastion.getTeam() + ":" + pos.m_121878_(), viewport.screenXd(pos.m_123341_()), viewport.screenYd(pos.m_123343_()));
            double mapPosX = projected.x();
            double mapPosY = projected.y();
            int size = Math.round(12.0f * this.getSelectedDeploymentScale(pos.m_123341_(), pos.m_123343_()));
            int bastionColor = this.getBastionColor(localTeam, bastion.getTeam());
            this.renderBastionMarker(guiGraphics, mapPosX, mapPosY, size, bastionColor, bastion);
            this.considerMarkerHover(mapPosX, mapPosY, size, pos.m_123341_(), pos.m_123343_());
            if (!showLabels) continue;
            String fallback = bastion.isRally() ? "Rally" : (bastion.isHab() ? "HAB" : (bastion.isRadio() ? "Radio" : "FOB"));
            Object object = name = bastion.getName() == null || bastion.getName().isEmpty() ? fallback : bastion.getName();
            if (bastion.isRally()) {
                name = (String)name + " \u00b7 " + bastion.getNextWaveSeconds() + "s";
            } else if (bastion.isRadio()) {
                name = (String)name + " \u00b7 \u5efa\u6750 " + bastion.getConstruction() + " / \u5f39\u836f " + bastion.getAmmunition();
            } else if (bastion.isHab()) {
                if (!bastion.isOperational()) {
                    name = (String)name + " \u00b7 \u4e0d\u53ef\u7528";
                }
            } else {
                name = (String)name + " \u00b7 \u5efa\u6750 " + bastion.getConstruction() + " / \u5f39\u836f " + bastion.getAmmunition();
                if (!bastion.isOperational()) {
                    name = (String)name + " \u00b7 HAB\u4e0d\u53ef\u7528";
                }
            }
            this.renderMapLabel(guiGraphics, "bastion:" + bastion.getTeam() + ":" + pos.m_121878_(), 3, (String)name, mapPosX, mapPosY, size + 3, -size / 2, 0xFFFFFF);
        }
    }

    private void renderVehicleSupplyStationsOnMap(GuiGraphics guiGraphics, MapViewport viewport, boolean showLabels, String localTeam) {
        if (this.visibleVehicleSupplyStations.isEmpty()) {
            return;
        }
        for (SyncBastionsMessage.VehicleSupplyStationInfo station : this.visibleVehicleSupplyStations) {
            BlockPos pos;
            if (!this.isVisibleTeamMarker(localTeam, station.getTeam()) || !viewport.containsWorld((pos = station.getPos()).m_123341_(), pos.m_123343_())) continue;
            TacticalMapStaticProjection.ScreenPoint projected = this.staticProjection.point("station:" + station.getTeam() + ":" + pos.m_121878_(), viewport.screenXd(pos.m_123341_()), viewport.screenYd(pos.m_123343_()));
            double mapPosX = projected.x();
            double mapPosY = projected.y();
            int size = Math.round(6.0f * this.getSelectedDeploymentScale(pos.m_123341_(), pos.m_123343_()));
            int color = this.getVehicleSupplyStationColor(localTeam, station.getTeam());
            this.renderVehicleSupplyStationMarker(guiGraphics, mapPosX, mapPosY, size, color);
            this.considerMarkerHover(mapPosX, mapPosY, size, pos.m_123341_(), pos.m_123343_());
            if (!showLabels) continue;
            String name = station.getName() == null || station.getName().isEmpty() ? "\u8f7d\u5177\u8865\u7ed9\u7ad9" : station.getName();
            this.renderMapLabel(guiGraphics, "station:" + station.getTeam() + ":" + pos.m_121878_(), 3, name, mapPosX, mapPosY, size + 4, -size / 2, 0xFFFFFF);
        }
    }

    private void renderFobRadiiOnMap(GuiGraphics guiGraphics, MapViewport viewport, String localTeam) {
        for (SyncBastionsMessage.BastionInfo bastion : this.visibleBastions) {
            if (bastion.isRally() || bastion.isHab() || !this.isVisibleTeamMarker(localTeam, bastion.getTeam()) || !bastion.isRadio() && bastion.getBuildRadius() <= 0.0 && bastion.getExclusionRadius() <= 0.0) continue;
            BlockPos pos = bastion.getPos();
            if (bastion.getExclusionRadius() > 0.0) {
                this.renderWorldCircle(guiGraphics, viewport, pos.m_123341_(), pos.m_123343_(), bastion.getExclusionRadius(), 1430209610);
            }
            if (!(bastion.getBuildRadius() > 0.0)) continue;
            this.renderWorldCircle(guiGraphics, viewport, pos.m_123341_(), pos.m_123343_(), bastion.getBuildRadius(), -1722437633);
        }
    }

    private void renderWorldCircle(GuiGraphics guiGraphics, MapViewport viewport, double centerX, double centerZ, double radius, int color) {
        double pixelRadius = Math.max(radius * viewport.scaleX, radius * viewport.scaleZ);
        int segments = TacticalMapCircleMesh.segments(pixelRadius);
        double[] unit = TacticalMapCircleMesh.vertices(segments);
        double previousX = viewport.screenXd(centerX + unit[0] * radius);
        double previousY = viewport.screenYd(centerZ + unit[1] * radius);
        for (int index = 1; index <= segments; ++index) {
            double currentX = viewport.screenXd(centerX + unit[index * 2] * radius);
            double currentY = viewport.screenYd(centerZ + unit[index * 2 + 1] * radius);
            this.drawClippedLine(guiGraphics, previousX, previousY, currentX, currentY, color, viewport);
            previousX = currentX;
            previousY = currentY;
        }
    }

    private void renderBastionMarker(GuiGraphics guiGraphics, double x, double y, int size, int color, SyncBastionsMessage.BastionInfo bastion) {
        ResourceLocation icon;
        boolean enemy = TacticalMapHUD.isEnemyTint(color);
        if (bastion.isRally()) {
            icon = enemy ? MAP_EN_RALLY : MAP_RALLY;
        } else if (bastion.isHab()) {
            BlockPos pos = bastion.getPos();
            boolean selected = this.hasSelectedDeploymentPoint && this.matchesSelectedDeployment(pos.m_123341_(), pos.m_123343_());
            icon = selected ? MAP_HAB_ACTIVATED : MAP_HAB;
        } else {
            icon = bastion.isRadio() ? (enemy ? MAP_EN_FOB : MAP_RADIO) : (enemy ? MAP_EN_FOB : MAP_FOB);
        }
        int drawColor = enemy ? color : -1;
        this.blitMapIcon(guiGraphics, x, y, size, icon, drawColor);
    }

    private boolean matchesSelectedDeployment(double worldX, double worldZ) {
        if (!this.hasSelectedDeploymentPoint) {
            return false;
        }
        double dx = this.selectedDeploymentX - worldX;
        double dz = this.selectedDeploymentZ - worldZ;
        return dx * dx + dz * dz <= 64.0;
    }

    private static boolean isEnemyTint(int color) {
        int r = color >> 16 & 0xFF;
        int g = color >> 8 & 0xFF;
        int b = color & 0xFF;
        return r > 180 && g < 120 && b < 120;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void blitMapIcon(GuiGraphics guiGraphics, double x, double y, int size, ResourceLocation icon, int color) {
        guiGraphics.m_280168_().m_85836_();
        guiGraphics.m_280168_().m_85837_(x, y, 0.0);
        try {
            int half = Math.max(4, size / 2);
            float red = (float)(color >> 16 & 0xFF) / 255.0f;
            float green = (float)(color >> 8 & 0xFF) / 255.0f;
            float blue = (float)(color & 0xFF) / 255.0f;
            float alpha = (float)(color >>> 24 & 0xFF) / 255.0f;
            if (alpha <= 0.01f) {
                alpha = 1.0f;
            }
            RenderSystem.setShaderColor((float)red, (float)green, (float)blue, (float)alpha);
            int draw = half * 2;
            guiGraphics.m_280411_(icon, -half, -half, draw, draw, 0.0f, 0.0f, 128, 128, 128, 128);
        }
        finally {
            RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            guiGraphics.m_280168_().m_85849_();
        }
    }

    private void renderVehicleSupplyStationMarker(GuiGraphics guiGraphics, double x, double y, int size, int color) {
        ResourceLocation icon = TacticalMapHUD.isEnemyTint(color) ? MAP_EN_ACP : MAP_VEHICLE_SUPPLY;
        int drawColor = TacticalMapHUD.isEnemyTint(color) ? color : -1;
        this.blitMapIcon(guiGraphics, x, y, Math.max(size, 8), icon, drawColor);
    }

    private void renderBaseMarker(GuiGraphics guiGraphics, double x, double y, int size, int color, float yaw) {
        boolean enemy = TacticalMapHUD.isEnemyTint(color);
        ResourceLocation icon = enemy ? MAP_EN_FOB : MAP_MAINSPAWN;
        this.blitMapIcon(guiGraphics, x, y, Math.max(size, 10), icon, enemy ? color : -1);
    }

    private void renderBaseMarkerAtOrigin(GuiGraphics guiGraphics, int size, int color, float yaw) {
        int half = Math.max(4, size / 2);
        int inner = Math.max(2, half / 2);
        guiGraphics.m_280509_(-half, -half, half + 1, half + 1, -586149872);
        guiGraphics.m_280509_(-half + 1, -half + 1, half, half, color);
        guiGraphics.m_280509_(-inner, -inner, inner + 1, inner + 1, -300937200);
        double radians = Math.toRadians(yaw);
        int tipX = -((int)Math.round(Math.sin(radians) * (double)half));
        int tipY = (int)Math.round(Math.cos(radians) * (double)half);
        int pointerHalf = Math.max(1, size / 8);
        guiGraphics.m_280509_(tipX - pointerHalf, tipY - pointerHalf, tipX + pointerHalf + 1, tipY + pointerHalf + 1, -1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void renderPointMarker(GuiGraphics guiGraphics, double x, double y, int size, int color) {
        guiGraphics.m_280168_().m_85836_();
        guiGraphics.m_280168_().m_85837_(x, y, 0.0);
        try {
            this.renderPointMarkerAtOrigin(guiGraphics, size, color);
        }
        finally {
            guiGraphics.m_280168_().m_85849_();
        }
    }

    private void renderPointMarkerAtOrigin(GuiGraphics guiGraphics, int size, int color) {
        int half = Math.max(1, size / 2);
        guiGraphics.m_280509_(-half, -half, half + 1, half + 1, -586149872);
        guiGraphics.m_280509_(-half + 1, -half + 1, half, half, color);
    }

    private void renderMapLabel(GuiGraphics guiGraphics, String id, int priority, String text, double x, double y, int offsetX, int offsetY, int color) {
        if (id == null || !id.startsWith("capture:") || text == null || text.isBlank()) {
            return;
        }
        Font font = Minecraft.m_91087_().f_91062_;
        float scale = this.getMapTextScale();
        String abbreviated = TacticalMapHUD.abbreviateMapLabel(text);
        int fullWidth = Math.max(1, Math.round((float)font.m_92895_(text) * scale));
        int abbreviatedWidth = Math.max(1, Math.round((float)font.m_92895_(abbreviated) * scale));
        Objects.requireNonNull(font);
        int height = Math.max(1, Math.round(9.0f * scale));
        TacticalMapLabelLayout.Placement placement = this.mapLabelLayout.place(id, priority, (int)Math.round(x), (int)Math.round(y), offsetX, offsetY, fullWidth, abbreviatedWidth, height);
        if (placement.mode() == TacticalMapLabelLayout.Mode.ICON_ONLY) {
            return;
        }
        String drawn = placement.mode() == TacticalMapLabelLayout.Mode.ABBREVIATED ? abbreviated : text;
        this.drawMapText(guiGraphics, drawn, placement.x(), placement.y(), color);
    }

    private static String abbreviateMapLabel(String text) {
        if (text == null || text.length() <= 10) {
            return text == null ? "" : text;
        }
        return text.substring(0, 9) + "\u2026";
    }

    private int getMapTitleHeight(boolean showInteractionChrome) {
        if (!showInteractionChrome) {
            return 0;
        }
        return this.compactEmbeddedRendering ? 14 : 20;
    }

    private float getMapTextScale() {
        return this.compactEmbeddedRendering ? 0.68f : 1.0f;
    }

    private void drawMapText(GuiGraphics guiGraphics, String text, int x, int y, int color) {
        float scale = this.getMapTextScale();
        guiGraphics.m_280168_().m_85836_();
        guiGraphics.m_280168_().m_85841_(scale, scale, 1.0f);
        guiGraphics.m_280056_(Minecraft.m_91087_().f_91062_, text, Math.round((float)x / scale), Math.round((float)y / scale), color, false);
        guiGraphics.m_280168_().m_85849_();
    }

    private int getTeamRelativeColor(String localTeam, String markerTeam) {
        String canonical = EspetroTeamBridge.canonicalizeTeamName(markerTeam);
        if (canonical == null || EspetroTeamBridge.isSameTeam(localTeam, markerTeam)) {
            return -1;
        }
        return -43691;
    }

    private int getBastionColor(String team) {
        return -1;
    }

    private int getBastionColor(String localTeam, String markerTeam) {
        return this.getTeamRelativeColor(localTeam, markerTeam);
    }

    private int getVehicleSupplyStationColor(String team) {
        return -1;
    }

    private int getVehicleSupplyStationColor(String localTeam, String markerTeam) {
        return this.getTeamRelativeColor(localTeam, markerTeam);
    }

    private void renderTacticalMarkersOnMap(GuiGraphics guiGraphics, MapViewport viewport, boolean showLabels, String localTeam) {
        for (TacticalMarker marker : this.visibleTacticalMarkers) {
            float opacity;
            if (!EspetroTeamBridge.isSameTeam(localTeam, marker.team()) || (opacity = this.getTacticalMarkerOpacity(marker)) <= 0.0f || !viewport.containsWorld(marker.x(), marker.z())) continue;
            double mapX = viewport.screenXd(marker.x());
            double mapY = viewport.screenYd(marker.z());
            int baseColor = switch (marker.type()) {
                case TacticalMarkerType.ATTACK_HERE -> TacticalMarkerType.ATTACK_HERE.getColor();
                case TacticalMarkerType.DEFEND_HERE -> TacticalMarkerType.DEFEND_HERE.getColor();
                case TacticalMarkerType.ENEMY_INFANTRY, TacticalMarkerType.ENEMY_TANK, TacticalMarkerType.ENEMY_IFV, TacticalMarkerType.ENEMY_LIGHT_VEHICLE, TacticalMarkerType.ENEMY_HELICOPTER -> -1;
                default -> -43691;
            };
            int fadedColor = this.withOpacity(baseColor, opacity);
            this.renderTacticalMarkerIcon(guiGraphics, mapX, mapY, marker.type(), fadedColor);
            this.considerMarkerHover(mapX, mapY, 10, marker.x(), marker.z());
            this.renderTacticalMarkerAnnotation(guiGraphics, marker, mapX, mapY, fadedColor, opacity);
        }
    }

    private void renderTacticalMarkerAnnotation(GuiGraphics guiGraphics, TacticalMarker marker, double mapX, double mapY, int fadedColor, float opacity) {
        TacticalMarkerType type = marker.type();
        if (type != TacticalMarkerType.ATTACK_HERE && type != TacticalMarkerType.DEFEND_HERE) {
            return;
        }
        int offset = 8;
        if (marker.ownerCommander()) {
            int gold = this.withOpacity(-14490, opacity);
            int r = Math.max(1, Math.round(1.5f * this.getMapTextScale()));
            int cx = (int)Math.round(mapX + (double)offset);
            int cy = (int)Math.round(mapY - 1.0);
            guiGraphics.m_280509_(cx - r, cy - r, cx + r + 1, cy + r + 1, gold);
            return;
        }
        if (marker.ownerSquadId() > 0) {
            this.renderMapLabel(guiGraphics, "tactical:" + String.valueOf(marker.id()), 4, String.valueOf(marker.ownerSquadId()), mapX, mapY, offset, -3, fadedColor);
        }
    }

    private float getTacticalMarkerOpacity(TacticalMarker marker) {
        if (marker.type().isPersistentUntilRemoved()) {
            return 1.0f;
        }
        TacticalMapJsonConfig config = TacticalMapJsonConfig.getInstance();
        long duration = config.getTacticalMarkerDurationMillis();
        long remaining = duration - Math.max(0L, System.currentTimeMillis() - marker.createdAtMillis());
        if (remaining <= 0L) {
            return 0.0f;
        }
        long fadeDuration = config.getTacticalMarkerFadeMillis();
        if (remaining >= fadeDuration) {
            return 1.0f;
        }
        return Mth.m_14036_((float)((float)remaining / (float)Math.max(1L, fadeDuration)), (float)0.0f, (float)1.0f);
    }

    private int withOpacity(int color, float opacity) {
        int originalAlpha = color >>> 24 & 0xFF;
        int alpha = Mth.m_14045_((int)Math.round((float)originalAlpha * opacity), (int)0, (int)255);
        return color & 0xFFFFFF | alpha << 24;
    }

    private void renderCapturePointsOnMap(GuiGraphics guiGraphics, MapViewport viewport, LocalPlayer player, boolean showLabels) {
        int markerSize = 3;
        int labelOffset = markerSize / 2 + 3;
        for (CapturePoint point : this.allPoints) {
            double pointCenterX = (double)(point.getPos1().m_123341_() + point.getPos2().m_123341_()) / 2.0;
            double pointCenterZ = (double)(point.getPos1().m_123343_() + point.getPos2().m_123343_()) / 2.0;
            TacticalMapStaticProjection.ScreenPoint projected = this.staticProjection.point("capture:" + point.getName(), viewport.screenXd(pointCenterX), viewport.screenYd(pointCenterZ));
            double mapPosX = projected.x();
            double mapPosY = projected.y();
            int pointColor = this.getStatusColor(point);
            this.renderPointMarker(guiGraphics, mapPosX, mapPosY, markerSize, pointColor);
            this.considerMarkerHover(mapPosX, mapPosY, markerSize, pointCenterX, pointCenterZ);
            if (!showLabels) {
                this.renderPointBoundary(guiGraphics, point, viewport);
                continue;
            }
            this.renderMapLabel(guiGraphics, "capture:" + point.getName(), 2, point.getName(), mapPosX, mapPosY, labelOffset, -6, 0xFFFFFF);
            this.renderPointBoundary(guiGraphics, point, viewport);
        }
        this.renderBatchRoutes(guiGraphics, viewport);
    }

    private void renderBatchRoutes(GuiGraphics guiGraphics, MapViewport viewport) {
        CapturePoint previous = null;
        for (CapturePoint point : this.allPoints) {
            if (previous != null) {
                boolean newBatch = previous.getBatch() != point.getBatch();
                int color = newBatch ? ROUTE_COLORS[0] : -855649946;
                this.drawRouteLine(guiGraphics, previous, point, color, viewport);
            }
            previous = point;
        }
    }

    private void drawRouteLine(GuiGraphics guiGraphics, CapturePoint from, CapturePoint to, int color, MapViewport viewport) {
        double fromX = (double)(from.getPos1().m_123341_() + from.getPos2().m_123341_()) / 2.0;
        double fromZ = (double)(from.getPos1().m_123343_() + from.getPos2().m_123343_()) / 2.0;
        double toX = (double)(to.getPos1().m_123341_() + to.getPos2().m_123341_()) / 2.0;
        double toZ = (double)(to.getPos1().m_123343_() + to.getPos2().m_123343_()) / 2.0;
        this.drawClippedLine(guiGraphics, viewport.screenXd(fromX), viewport.screenYd(fromZ), viewport.screenXd(toX), viewport.screenYd(toZ), color, viewport);
    }

    private void renderPointBoundary(GuiGraphics guiGraphics, CapturePoint point, MapViewport viewport) {
        int tmp;
        int minX = Math.min(point.getPos1().m_123341_(), point.getPos2().m_123341_());
        int maxX = Math.max(point.getPos1().m_123341_(), point.getPos2().m_123341_());
        int minZ = Math.min(point.getPos1().m_123343_(), point.getPos2().m_123343_());
        int maxZ = Math.max(point.getPos1().m_123343_(), point.getPos2().m_123343_());
        if ((double)maxX < viewport.viewMinX || (double)minX > viewport.viewMaxX || (double)maxZ < viewport.viewMinZ || (double)minZ > viewport.viewMaxZ) {
            return;
        }
        int leftX = Mth.m_14045_((int)viewport.screenX(minX), (int)viewport.left, (int)(viewport.right() - 1));
        int rightX = Mth.m_14045_((int)viewport.screenX(maxX), (int)viewport.left, (int)(viewport.right() - 1));
        int topY = Mth.m_14045_((int)viewport.screenY(minZ), (int)viewport.top, (int)(viewport.bottom() - 1));
        int bottomY = Mth.m_14045_((int)viewport.screenY(maxZ), (int)viewport.top, (int)(viewport.bottom() - 1));
        int boundaryColor = this.getStatusColor(point) & 0x80FFFFFF;
        if (leftX > rightX) {
            tmp = leftX;
            leftX = rightX;
            rightX = tmp;
        }
        if (topY > bottomY) {
            tmp = topY;
            topY = bottomY;
            bottomY = tmp;
        }
        if (leftX < rightX) {
            guiGraphics.m_280509_(leftX, topY, rightX + 1, topY + 1, boundaryColor);
            guiGraphics.m_280509_(leftX, bottomY, rightX + 1, bottomY + 1, boundaryColor);
        }
        if (topY < bottomY) {
            guiGraphics.m_280509_(leftX, topY, leftX + 1, bottomY + 1, boundaryColor);
            guiGraphics.m_280509_(rightX, topY, rightX + 1, bottomY + 1, boundaryColor);
        }
    }

    private MapViewport createViewport(TacticalMapJsonConfig config, TacticalMapJsonConfig.TacticalMapBounds bounds, double playerX, double playerZ, int mapLeft, int mapTop, int mapWidth, int mapHeight, int titleHeight) {
        double centerZ;
        double centerX;
        int safeTitleHeight = Mth.m_14045_((int)titleHeight, (int)0, (int)Math.max(0, mapHeight - 1));
        int availableHeight = Math.max(1, mapHeight - safeTitleHeight);
        double displayAspectRatio = this.getMapDisplayAspectRatio(bounds);
        int width = Math.max(1, mapWidth);
        int height = Math.max(1, (int)Math.round((double)width / displayAspectRatio));
        if (height > availableHeight) {
            height = availableHeight;
            width = Math.max(1, (int)Math.round((double)height * displayAspectRatio));
        }
        int left = mapLeft + (mapWidth - width) / 2;
        int top = mapTop + safeTitleHeight + (availableHeight - height) / 2;
        this.ensureVisibleSpan(config, bounds);
        double[] span = this.getViewportSpan(bounds);
        if (this.draggingMap || this.customMapCenter) {
            centerX = this.draggedCenterX;
            centerZ = this.draggedCenterZ;
        } else {
            double[] autoCenter = this.chooseAutoViewportCenter(playerX, playerZ, bounds, span[0], span[1]);
            centerX = autoCenter[0];
            centerZ = autoCenter[1];
        }
        double[] clamped = this.clampViewportCenter(centerX, centerZ, this.visibleWorldSpan, bounds);
        if (this.draggingMap || this.customMapCenter) {
            this.draggedCenterX = clamped[0];
            this.draggedCenterZ = clamped[1];
        }
        return new MapViewport(left, top, width, height, clamped[0], clamped[1], span[0], span[1], bounds);
    }

    private TacticalMapJsonConfig.TacticalMapBounds getEffectiveBounds(TacticalMapJsonConfig config, TacticalMapJsonConfig.TacticalMapBounds configuredBounds, LocalPlayer player) {
        return configuredBounds;
    }

    private double[] chooseAutoViewportCenter(double playerX, double playerZ, TacticalMapJsonConfig.TacticalMapBounds bounds, double spanX, double spanZ) {
        double[] playerCenter = this.clampViewportCenter(playerX, playerZ, this.visibleWorldSpan, bounds);
        return playerCenter;
    }

    private boolean containsAnyMarker(double centerX, double centerZ, double spanX, double spanZ, TacticalMapJsonConfig.TacticalMapBounds bounds, List<MarkerCenter> markers) {
        double minX = centerX - spanX / 2.0;
        double maxX = centerX + spanX / 2.0;
        double minZ = centerZ - spanZ / 2.0;
        double maxZ = centerZ + spanZ / 2.0;
        for (MarkerCenter marker : markers) {
            if (!bounds.contains(marker.x, marker.z) || !(marker.x >= minX) || !(marker.x <= maxX) || !(marker.z >= minZ) || !(marker.z <= maxZ)) continue;
            return true;
        }
        return false;
    }

    private MarkerCenter findNearestMarker(double x, double z, TacticalMapJsonConfig.TacticalMapBounds bounds, List<MarkerCenter> markers) {
        MarkerCenter nearest = null;
        double nearestDistance = Double.MAX_VALUE;
        for (MarkerCenter marker : markers) {
            double dz;
            double dx;
            double distance;
            if (!bounds.contains(marker.x, marker.z) || !((distance = (dx = marker.x - x) * dx + (dz = marker.z - z) * dz) < nearestDistance)) continue;
            nearest = marker;
            nearestDistance = distance;
        }
        return nearest;
    }

    private List<MarkerCenter> collectSyncedMarkerCenters(LocalPlayer player, boolean includePlayers) {
        Object pos;
        ArrayList<MarkerCenter> markers = new ArrayList<MarkerCenter>();
        String localTeam = EspetroTeamBridge.getPlayerTeam((Player)player);
        for (SyncBastionsMessage.BaseInfo baseInfo : this.visibleBases) {
            if (!this.isVisibleTeamMarker(localTeam, baseInfo.getTeam())) continue;
            pos = baseInfo.getPos();
            this.addMarkerCenter(markers, pos.m_123341_(), pos.m_123343_());
        }
        for (SyncBastionsMessage.BastionInfo bastionInfo : this.visibleBastions) {
            if (!this.isVisibleTeamMarker(localTeam, bastionInfo.getTeam())) continue;
            pos = bastionInfo.getPos();
            this.addMarkerCenter(markers, pos.m_123341_(), pos.m_123343_());
        }
        for (CapturePoint capturePoint : this.allPoints) {
            this.addMarkerCenter(markers, (double)(capturePoint.getPos1().m_123341_() + capturePoint.getPos2().m_123341_()) / 2.0, (double)(capturePoint.getPos1().m_123343_() + capturePoint.getPos2().m_123343_()) / 2.0);
        }
        for (TacticalMarker tacticalMarker : this.visibleTacticalMarkers) {
            if (!EspetroTeamBridge.isSameTeam(localTeam, tacticalMarker.team()) || !(this.getTacticalMarkerOpacity(tacticalMarker) > 0.0f)) continue;
            this.addMarkerCenter(markers, tacticalMarker.x(), tacticalMarker.z());
        }
        if (!includePlayers) {
            return markers;
        }
        for (Map.Entry entry : this.syncedPlayerPositions.entrySet()) {
            if (((UUID)entry.getKey()).equals(player.m_20148_()) || !EspetroTeamBridge.isSameTeam(localTeam, ((SyncPlayerPositionsMessage.PlayerPosition)(pos = (SyncPlayerPositionsMessage.PlayerPosition)entry.getValue())).getTeamName())) continue;
            this.addMarkerCenter(markers, ((SyncPlayerPositionsMessage.PlayerPosition)pos).getX(), ((SyncPlayerPositionsMessage.PlayerPosition)pos).getZ());
        }
        return markers;
    }

    private boolean isVisibleTeamMarker(String localTeam, String markerTeam) {
        String canonicalMarkerTeam = EspetroTeamBridge.canonicalizeTeamName(markerTeam);
        if (canonicalMarkerTeam == null) {
            return true;
        }
        return EspetroTeamBridge.isSameTeam(localTeam, canonicalMarkerTeam);
    }

    private void addMarkerCenter(List<MarkerCenter> markers, double x, double z) {
        if (Double.isFinite(x) && Double.isFinite(z)) {
            markers.add(new MarkerCenter(x, z));
        }
    }

    private double[] getViewportSpan(TacticalMapJsonConfig.TacticalMapBounds bounds) {
        return this.getViewportSpan(bounds, this.visibleWorldSpan);
    }

    private double[] getViewportSpan(TacticalMapJsonConfig.TacticalMapBounds bounds, double span) {
        double aspectRatio = bounds.aspectRatio();
        if (bounds.width() >= bounds.height()) {
            return new double[]{span, span / aspectRatio};
        }
        return new double[]{span * aspectRatio, span};
    }

    private double getMapDisplayAspectRatio(TacticalMapJsonConfig.TacticalMapBounds bounds) {
        return Math.max(0.1, Math.min(10.0, bounds.aspectRatio()));
    }

    private void handleMapDrag(MapViewport viewport, TacticalMapJsonConfig.TacticalMapBounds bounds) {
        Minecraft mc = Minecraft.m_91087_();
        long window = mc.m_91268_().m_85439_();
        boolean leftPressed = GLFW.glfwGetMouseButton((long)window, (int)0) == 1;
        double mouseX = this.getGuiMouseX(mc);
        double mouseY = this.getGuiMouseY(mc);
        boolean insideMap = viewport.containsScreen(mouseX, mouseY);
        boolean insideRecenterButton = this.isInsideLastRecenterButton(mouseX, mouseY);
        if (this.suppressMapDragUntilRelease) {
            this.draggingMap = false;
            if (!leftPressed) {
                this.suppressMapDragUntilRelease = false;
            }
            return;
        }
        if (leftPressed && insideRecenterButton && !this.draggingMap) {
            return;
        }
        if (leftPressed && insideMap && !this.draggingMap) {
            this.draggingMap = true;
            this.dragStartMouseX = mouseX;
            this.dragStartMouseY = mouseY;
            this.dragStartCenterX = viewport.centerX;
            this.dragStartCenterZ = viewport.centerZ;
            this.draggedCenterX = viewport.centerX;
            this.draggedCenterZ = viewport.centerZ;
            this.customMapCenter = true;
        }
        if (!leftPressed) {
            this.draggingMap = false;
            return;
        }
        if (this.draggingMap) {
            double dx = mouseX - this.dragStartMouseX;
            double dy = mouseY - this.dragStartMouseY;
            double[] clamped = this.clampViewportCenter(this.dragStartCenterX - dx / viewport.scaleX, this.dragStartCenterZ - dy / viewport.scaleZ, this.visibleWorldSpan, bounds);
            this.draggedCenterX = clamped[0];
            this.draggedCenterZ = clamped[1];
        }
    }

    private double getGuiMouseX(Minecraft mc) {
        return mc.f_91067_.m_91589_() * (double)mc.m_91268_().m_85445_() / (double)mc.m_91268_().m_85443_();
    }

    private double getGuiMouseY(Minecraft mc) {
        return mc.f_91067_.m_91594_() * (double)mc.m_91268_().m_85446_() / (double)mc.m_91268_().m_85444_();
    }

    private boolean isInsideLastEmbeddedMap(double mouseX, double mouseY) {
        return this.lastEmbeddedMapLeft != Integer.MIN_VALUE && Minecraft.m_91087_().f_91080_ == this.lastEmbeddedMapScreen && System.currentTimeMillis() - this.lastEmbeddedMapRenderMs <= 1000L && this.lastEmbeddedMapWidth > 0 && this.lastEmbeddedMapHeight > 0 && mouseX >= (double)this.lastEmbeddedMapLeft && mouseX <= (double)(this.lastEmbeddedMapLeft + this.lastEmbeddedMapWidth) && mouseY >= (double)this.lastEmbeddedMapTop && mouseY <= (double)(this.lastEmbeddedMapTop + this.lastEmbeddedMapHeight);
    }

    private boolean isInsideLastRecenterButton(double mouseX, double mouseY) {
        return this.lastRecenterButtonLeft != Integer.MIN_VALUE && this.isInsideLastEmbeddedMap(mouseX, mouseY) && this.lastRecenterButtonWidth > 0 && this.lastRecenterButtonHeight > 0 && mouseX >= (double)this.lastRecenterButtonLeft && mouseX <= (double)(this.lastRecenterButtonLeft + this.lastRecenterButtonWidth) && mouseY >= (double)this.lastRecenterButtonTop && mouseY <= (double)(this.lastRecenterButtonTop + this.lastRecenterButtonHeight);
    }

    private double[] clampViewportCenter(double centerX, double centerZ, double span, TacticalMapJsonConfig.TacticalMapBounds bounds) {
        double[] viewportSpan = this.getViewportSpan(bounds, span);
        double spanX = viewportSpan[0];
        double spanZ = viewportSpan[1];
        double halfX = spanX / 2.0;
        double halfZ = spanZ / 2.0;
        double minCenterX = bounds.minX + halfX;
        double maxCenterX = bounds.maxX - halfX;
        double minCenterZ = bounds.minZ + halfZ;
        double maxCenterZ = bounds.maxZ - halfZ;
        centerX = minCenterX > maxCenterX ? bounds.centerX() : Mth.m_14008_((double)centerX, (double)minCenterX, (double)maxCenterX);
        centerZ = minCenterZ > maxCenterZ ? bounds.centerZ() : Mth.m_14008_((double)centerZ, (double)minCenterZ, (double)maxCenterZ);
        return new double[]{centerX, centerZ};
    }

    private void renderMapBackground(GuiGraphics guiGraphics, MapViewport viewport) {
        this.renderConfiguredBackground(guiGraphics, viewport);
        guiGraphics.m_280509_(viewport.left, viewport.top, viewport.right(), viewport.bottom(), 0x22000000);
    }

    private void renderConfiguredBackground(GuiGraphics guiGraphics, MapViewport viewport) {
        ClientTacticalMapTileCache cache = ClientTacticalMapTileCache.get();
        cache.drainUploadQueue(4, 6000000L);
        TacticalMapPyramidLayout layout = cache.layout();
        TacticalMapHUD.resetMapBlitColor(guiGraphics);
        guiGraphics.m_280509_(viewport.left, viewport.top, viewport.right(), viewport.bottom(), -1440931557);
        if (layout == null) {
            this.mapLodPlanner.reset();
            cache.requestCurrentPreview();
            return;
        }
        double minX = (viewport.viewMinX - viewport.bounds.minX) / viewport.bounds.width();
        double maxX = (viewport.viewMaxX - viewport.bounds.minX) / viewport.bounds.width();
        double minY = (viewport.viewMinZ - viewport.bounds.minZ) / viewport.bounds.height();
        double maxY = (viewport.viewMaxZ - viewport.bounds.minZ) / viewport.bounds.height();
        this.lastViewMinX = minX;
        this.lastViewMinY = minY;
        this.lastViewMaxX = maxX;
        this.lastViewMaxY = maxY;
        this.lastViewScreenWidth = viewport.width;
        this.lastViewScreenHeight = viewport.height;
        TacticalMapLodPlanner.Plan plan = this.mapLodPlanner.planCached(layout, (MapImageQuality)((Object)TacticalMapConfig.mapImageQuality.get()), new TacticalMapLodPlanner.Viewport(minX, minY, maxX, maxY, viewport.width, viewport.height), System.currentTimeMillis(), cache.textureBudgetBytes(), cache.descriptor().session(), cache.readinessRevision(), cache::tileState);
        cache.updateDesired(plan.desiredTiles());
        TacticalMapLodPlanner.Layer finestCovering = TacticalMapLayerPicker.finestCovering(layout, plan.layers(), cache::hasAll, minX, minY, maxX, maxY);
        if (finestCovering != null && this.blitLayer(guiGraphics, viewport, cache, layout, finestCovering)) {
            return;
        }
        int previewLevel = layout.maxLevel();
        ClientTacticalMapTileCache.TextureEntry preview = cache.texture(previewLevel, 0, 0);
        if (preview == null) {
            cache.requestCurrentPreview();
        } else {
            this.renderTile(guiGraphics, viewport, layout, previewLevel, 0, 0, preview);
        }
    }

    private boolean blitLayer(GuiGraphics graphics, MapViewport viewport, ClientTacticalMapTileCache cache, TacticalMapPyramidLayout layout, TacticalMapLodPlanner.Layer layer) {
        ClientTacticalMapTileCache.LayerAtlas atlas = cache.composeLayer(layer.level(), layer.visibleTiles());
        if (atlas != null) {
            TacticalMapTileScreenMath.IntRect dest = this.projectPixels(viewport, layout, layer.level(), atlas.spec().pixels());
            if (dest.isEmpty()) {
                return false;
            }
            this.blitTexture(graphics, atlas.texture(), dest, layer.level(), layout.maxLevel());
            return true;
        }
        for (TacticalMapPyramidLayout.TileCoordinate tile : layer.visibleTiles()) {
            ClientTacticalMapTileCache.TextureEntry texture = cache.texture(tile.level(), tile.x(), tile.y());
            if (texture == null) {
                return false;
            }
            this.renderTile(graphics, viewport, layout, tile.level(), tile.x(), tile.y(), texture);
        }
        return true;
    }

    private void renderTile(GuiGraphics graphics, MapViewport viewport, TacticalMapPyramidLayout layout, int level, int tileX, int tileY, ClientTacticalMapTileCache.TextureEntry texture) {
        if (!layout.isValid(level, tileX, tileY) || texture.width() != layout.tileWidth(level, tileX) || texture.height() != layout.tileHeight(level, tileY)) {
            return;
        }
        TacticalMapTileScreenMath.IntRect dest = this.projectPixels(viewport, layout, level, TacticalMapTileScreenMath.tilePixels(layout, level, tileX, tileY));
        if (dest.isEmpty()) {
            return;
        }
        this.blitTexture(graphics, texture, dest, level, layout.maxLevel());
    }

    private TacticalMapTileScreenMath.IntRect projectPixels(MapViewport viewport, TacticalMapPyramidLayout layout, int level, TacticalMapTileScreenMath.PixelRect pixels) {
        return TacticalMapTileScreenMath.project(pixels, layout.levelWidth(level), layout.levelHeight(level), viewport.bounds.minX, viewport.bounds.minZ, viewport.bounds.width(), viewport.bounds.height(), viewport.viewMinX, viewport.viewMinZ, viewport.scaleX, viewport.scaleZ, viewport.left, viewport.top);
    }

    private void blitTexture(GuiGraphics graphics, ClientTacticalMapTileCache.TextureEntry texture, TacticalMapTileScreenMath.IntRect dest, int level, int maximumLevel) {
        TacticalMapHUD.resetMapBlitColor(graphics);
        RenderSystem.setShader(GameRenderer::m_172817_);
        RenderSystem.setShaderTexture((int)0, (int)texture.gpuId());
        texture.prepareFiltering(level, maximumLevel, (double)dest.width() / (double)texture.width(), (double)dest.height() / (double)texture.height());
        TacticalMapTileScreenMath.BlitUv uv = TacticalMapTileScreenMath.insetUv(texture.width(), texture.height());
        graphics.m_280411_(texture.location(), dest.left(), dest.top(), dest.width(), dest.height(), uv.uOffset(), uv.vOffset(), uv.uWidth(), uv.vHeight(), uv.textureWidth(), uv.textureHeight());
        TacticalMapHUD.resetMapBlitColor(graphics);
    }

    private static void resetMapBlitColor(GuiGraphics graphics) {
        graphics.m_280246_(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
    }

    private void renderViewportGrid(GuiGraphics guiGraphics, MapViewport viewport) {
        double firstZ;
        double firstX;
        double gridStep = this.chooseGridStep(Math.max(viewport.spanX, viewport.spanZ));
        int gridColor = 0x33FFFFFF;
        for (double x = firstX = Math.ceil(viewport.viewMinX / gridStep) * gridStep; x <= viewport.viewMaxX; x += gridStep) {
            int screenX = viewport.screenX(x);
            guiGraphics.m_280509_(screenX, viewport.top, screenX + 1, viewport.bottom(), gridColor);
        }
        for (double z = firstZ = Math.ceil(viewport.viewMinZ / gridStep) * gridStep; z <= viewport.viewMaxZ; z += gridStep) {
            int screenY = viewport.screenY(z);
            guiGraphics.m_280509_(viewport.left, screenY, viewport.right(), screenY + 1, gridColor);
        }
    }

    private double chooseGridStep(double span) {
        double step;
        double target = span / 8.0;
        for (step = 16.0; step < target; step *= 2.0) {
        }
        return step;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void renderMapPlayerIcon(GuiGraphics guiGraphics, double centerX, double centerY, float yaw, int size, int color) {
        MapDecoration.Type type = MapDecoration.Type.PLAYER;
        byte icon = type.m_77853_();
        int u = icon % 16 * 8;
        int v = icon / 16 * 8;
        float scale = (float)Mth.m_14045_((int)size, (int)6, (int)28) / 8.0f;
        int argb = color;
        if ((argb >>> 24 & 0xFF) < 8) {
            argb |= 0xFF000000;
        }
        float red = (float)(argb >> 16 & 0xFF) / 255.0f;
        float green = (float)(argb >> 8 & 0xFF) / 255.0f;
        float blue = (float)(argb & 0xFF) / 255.0f;
        float alpha = (float)(argb >>> 24 & 0xFF) / 255.0f;
        guiGraphics.m_280168_().m_85836_();
        try {
            guiGraphics.m_280168_().m_85837_(centerX, centerY, 200.0);
            guiGraphics.m_280168_().m_252781_(Axis.f_252403_.m_252977_(yaw + 180.0f));
            guiGraphics.m_280168_().m_85841_(scale, scale, 1.0f);
            RenderSystem.setShaderColor((float)red, (float)green, (float)blue, (float)alpha);
            guiGraphics.m_280411_(VANILLA_MAP_ICONS, -4, -4, 8, 8, (float)u, (float)v, 8, 8, 128, 128);
        }
        finally {
            RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            guiGraphics.m_280168_().m_85849_();
        }
    }

    private void renderTacticalMarkerIcon(GuiGraphics guiGraphics, double x, double y, TacticalMarkerType type) {
        this.renderTacticalMarkerIcon(guiGraphics, x, y, type, type.getColor());
    }

    private void renderTacticalMarkerIcon(GuiGraphics guiGraphics, double x, double y, TacticalMarkerType type, int color) {
        this.blitMapIcon(guiGraphics, x, y, 10, TacticalMapHUD.mapTextureForMarkerType(type), color);
    }

    private static ResourceLocation mapTextureForMarkerType(TacticalMarkerType type) {
        return TacticalMarkerIcons.textureFor(type);
    }

    private void drawClippedLine(GuiGraphics guiGraphics, double x1, double y1, double x2, double y2, int color, MapViewport viewport) {
        double minX = viewport.left;
        double minY = viewport.top;
        double maxX = viewport.right() - 1;
        double maxY = viewport.bottom() - 1;
        int out1 = this.computeOutCode(x1, y1, minX, minY, maxX, maxY);
        int out2 = this.computeOutCode(x2, y2, minX, minY, maxX, maxY);
        int guard = 0;
        while (guard++ < 8) {
            if ((out1 | out2) == 0) {
                this.drawSmoothLine(guiGraphics, x1, y1, x2, y2, color);
                return;
            }
            if ((out1 & out2) != 0) {
                return;
            }
            int out = out1 != 0 ? out1 : out2;
            double x = 0.0;
            double y = 0.0;
            if ((out & 8) != 0) {
                if (y2 == y1) {
                    return;
                }
                x = x1 + (x2 - x1) * (maxY - y1) / (y2 - y1);
                y = maxY;
            } else if ((out & 4) != 0) {
                if (y2 == y1) {
                    return;
                }
                x = x1 + (x2 - x1) * (minY - y1) / (y2 - y1);
                y = minY;
            } else if ((out & 2) != 0) {
                if (x2 == x1) {
                    return;
                }
                y = y1 + (y2 - y1) * (maxX - x1) / (x2 - x1);
                x = maxX;
            } else if ((out & 1) != 0) {
                if (x2 == x1) {
                    return;
                }
                y = y1 + (y2 - y1) * (minX - x1) / (x2 - x1);
                x = minX;
            }
            if (out == out1) {
                x1 = x;
                y1 = y;
                out1 = this.computeOutCode(x1, y1, minX, minY, maxX, maxY);
                continue;
            }
            x2 = x;
            y2 = y;
            out2 = this.computeOutCode(x2, y2, minX, minY, maxX, maxY);
        }
    }

    private int computeOutCode(double x, double y, double minX, double minY, double maxX, double maxY) {
        int code = 0;
        if (x < minX) {
            code |= 1;
        } else if (x > maxX) {
            code |= 2;
        }
        if (y < minY) {
            code |= 4;
        } else if (y > maxY) {
            code |= 8;
        }
        return code;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void drawSmoothLine(GuiGraphics guiGraphics, double x1, double y1, double x2, double y2, int color) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        double length = Math.hypot(dx, dy);
        if (length < 0.5) {
            int x = (int)Math.round(x1);
            int y = (int)Math.round(y1);
            guiGraphics.m_280509_(x, y, x + 1, y + 1, color);
            return;
        }
        guiGraphics.m_280168_().m_85836_();
        try {
            guiGraphics.m_280168_().m_85837_(x1, y1, 0.0);
            guiGraphics.m_280168_().m_252781_(Axis.f_252403_.m_252961_((float)Math.atan2(dy, dx)));
            guiGraphics.m_280509_(0, 0, Math.max(1, (int)Math.ceil(length)), 1, color);
        }
        finally {
            guiGraphics.m_280168_().m_85849_();
        }
    }

    private void resetVisibleSpan(TacticalMapJsonConfig config) {
        TacticalMapJsonConfig.TacticalMapBounds bounds = this.getCurrentBounds(config);
        this.visibleWorldSpan = config.getInitialRange(bounds);
    }

    private void ensureVisibleSpan(TacticalMapJsonConfig config, TacticalMapJsonConfig.TacticalMapBounds bounds) {
        double min = config.getMinimumRange(bounds);
        if (this.visibleWorldSpan <= 0.0 || Double.isNaN(this.visibleWorldSpan)) {
            this.visibleWorldSpan = config.getInitialRange(bounds);
        }
        this.visibleWorldSpan = Mth.m_14008_((double)this.visibleWorldSpan, (double)min, (double)bounds.size());
    }

    private void preserveCustomViewportCenter(TacticalMapJsonConfig.TacticalMapBounds bounds) {
        if (!this.customMapCenter && !this.draggingMap) {
            return;
        }
        double[] clamped = this.clampViewportCenter(this.draggedCenterX, this.draggedCenterZ, this.visibleWorldSpan, bounds);
        this.draggedCenterX = clamped[0];
        this.draggedCenterZ = clamped[1];
        this.customMapCenter = true;
    }

    private String getRangeText() {
        TacticalMapJsonConfig config = TacticalMapJsonConfig.getInstance();
        TacticalMapJsonConfig.TacticalMapBounds bounds = this.getCurrentBounds(config);
        this.ensureVisibleSpan(config, bounds);
        return "\u8303\u56f4:" + Math.round(this.visibleWorldSpan);
    }

    private TacticalMapJsonConfig.TacticalMapBounds getCurrentBounds(TacticalMapJsonConfig config) {
        return config.getBounds();
    }

    private void beginMarkerHover(boolean allowMouseInteraction, MapViewport viewport) {
        this.hoveredMapMarker = null;
        this.hoveredMapMarkerDistanceSquared = Double.MAX_VALUE;
        this.markerHoverEnabled = false;
        if (!allowMouseInteraction) {
            return;
        }
        Minecraft minecraft = Minecraft.m_91087_();
        this.markerHoverMouseX = this.getGuiMouseX(minecraft);
        this.markerHoverMouseY = this.getGuiMouseY(minecraft);
        this.markerHoverEnabled = viewport.containsScreen(this.markerHoverMouseX, this.markerHoverMouseY);
    }

    private void considerMarkerHover(double screenX, double screenY, int markerSize, double worldX, double worldZ) {
        if (!this.markerHoverEnabled) {
            return;
        }
        double dx = this.markerHoverMouseX - screenX;
        double dy = this.markerHoverMouseY - screenY;
        double distanceSquared = dx * dx + dy * dy;
        int hitRadius = Math.max(6, markerSize / 2 + 3);
        if (distanceSquared > (double)(hitRadius * hitRadius) || distanceSquared >= this.hoveredMapMarkerDistanceSquared) {
            return;
        }
        this.hoveredMapMarkerDistanceSquared = distanceSquared;
        this.hoveredMapMarker = new HoveredMapMarker(Mth.m_14107_((double)worldX), Mth.m_14107_((double)worldZ));
    }

    private void renderHoveredMarkerCoordinates(GuiGraphics guiGraphics, MapViewport viewport) {
        if (this.hoveredMapMarker == null) {
            return;
        }
        String text = "X: " + this.hoveredMapMarker.x + "  Z: " + this.hoveredMapMarker.z;
        Font font = Minecraft.m_91087_().f_91062_;
        float textScale = this.getMapTextScale();
        int padding = this.compactEmbeddedRendering ? 2 : 4;
        int tooltipWidth = Math.round((float)font.m_92895_(text) * textScale) + padding * 2;
        Objects.requireNonNull(font);
        int tooltipHeight = Math.round(9.0f * textScale) + padding * 2;
        int minX = viewport.left;
        int maxX = Math.max(minX, viewport.right() - tooltipWidth);
        int minY = viewport.top;
        int maxY = Math.max(minY, viewport.bottom() - tooltipHeight);
        int tooltipX = Mth.m_14045_((int)((int)Math.round(this.markerHoverMouseX) + 10), (int)minX, (int)maxX);
        int tooltipY = Mth.m_14045_((int)((int)Math.round(this.markerHoverMouseY) + 10), (int)minY, (int)maxY);
        guiGraphics.m_280509_(tooltipX, tooltipY, tooltipX + tooltipWidth, tooltipY + tooltipHeight, -300937192);
        guiGraphics.m_280637_(tooltipX, tooltipY, tooltipWidth, tooltipHeight, -2039584);
        this.drawMapText(guiGraphics, text, tooltipX + padding, tooltipY + padding, -1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void renderSelectedDeploymentFrame(GuiGraphics guiGraphics, MapViewport viewport) {
        if (!this.hasSelectedDeploymentPoint || !viewport.containsWorld(this.selectedDeploymentX, this.selectedDeploymentZ)) {
            return;
        }
        double x = viewport.screenXd(this.selectedDeploymentX);
        double y = viewport.screenYd(this.selectedDeploymentZ);
        float progress = this.getSelectedDeploymentAnimationProgress();
        int frameSize = Math.round(Mth.m_14179_((float)progress, (float)15.0f, (float)24.0f));
        int half = frameSize / 2;
        guiGraphics.m_280168_().m_85836_();
        guiGraphics.m_280168_().m_85837_(x, y, 0.0);
        try {
            guiGraphics.m_280637_(-half, -half, frameSize, frameSize, -1);
            guiGraphics.m_280637_(-half + 1, -half + 1, frameSize - 2, frameSize - 2, -1);
        }
        finally {
            guiGraphics.m_280168_().m_85849_();
        }
        this.considerMarkerHover(x, y, frameSize, this.selectedDeploymentX, this.selectedDeploymentZ);
    }

    private float getSelectedDeploymentScale(double worldX, double worldZ) {
        if (!this.hasSelectedDeploymentPoint) {
            return 1.0f;
        }
        double dx = worldX - this.selectedDeploymentX;
        double dz = worldZ - this.selectedDeploymentZ;
        if (dx * dx + dz * dz > 64.0) {
            return 1.0f;
        }
        return 1.0f + 0.35f * this.getSelectedDeploymentAnimationProgress();
    }

    private float getSelectedDeploymentAnimationProgress() {
        long now = System.currentTimeMillis();
        float progress = this.selectedDeploymentAnimationStartedAt <= 0L ? 1.0f : Mth.m_14036_((float)((float)(now - this.selectedDeploymentAnimationStartedAt) / 180.0f), (float)0.0f, (float)1.0f);
        return progress * progress * (3.0f - 2.0f * progress);
    }

    private static Entity getMapRenderBody(Entity entity) {
        Entity root = entity.m_20201_();
        return root != null ? root : entity;
    }

    private Object labelFrameKey(MapViewport content, TacticalMapJsonConfig config) {
        TacticalMapViewportQuantizer.LabelKey exact = TacticalMapViewportQuantizer.labelKey(this.staticMapRevision, content.viewMinX, content.viewMinZ, content.viewMaxX, content.viewMaxZ, content.spanX, content.spanZ, content.width, content.height, this.compactEmbeddedRendering, config.showLabels);
        long now = System.currentTimeMillis();
        if (this.draggingMap && this.lastLabelFrameKey != null && now - this.lastLabelLayoutMs < 50L) {
            return this.lastLabelFrameKey;
        }
        this.lastLabelFrameKey = exact;
        this.lastLabelLayoutMs = now;
        return exact;
    }

    private int getStatusColor(CapturePoint point) {
        DisplayState displayState = point.getDisplayState();
        int baseColor = 0;
        switch (displayState) {
            case NEUTRAL: {
                baseColor = -5592406;
                break;
            }
            case CAPTURING_FLAG_SINGLE: {
                baseColor = -256;
                break;
            }
            case CAPTURING_CONTESTED_MULTI: {
                baseColor = Short.MIN_VALUE;
                break;
            }
            case CONTESTED_MULTI: {
                baseColor = Short.MIN_VALUE;
                break;
            }
            case CAPTURING_DOWN: {
                baseColor = -65536;
                break;
            }
            case CAPTURED: {
                if (this.isFriendlyCapture(point)) {
                    baseColor = -11141291;
                    break;
                }
                baseColor = -43691;
                break;
            }
            default: {
                baseColor = -5592406;
            }
        }
        return baseColor;
    }

    private void renderTeamPlayers(GuiGraphics guiGraphics, int mapLeft, int mapTop, int mapWidth, int mapHeight, MapDisplayMode displayMode) {
        int textX;
        boolean isLeftSide;
        Minecraft minecraft = Minecraft.m_91087_();
        LocalPlayer localPlayer = minecraft.f_91074_;
        if (localPlayer == null) {
            return;
        }
        String localTeam = EspetroTeamBridge.getPlayerTeam((Player)localPlayer);
        if (localTeam == null) {
            return;
        }
        List allPlayers = minecraft.f_91073_.m_6907_();
        if (allPlayers.isEmpty()) {
            return;
        }
        ArrayList<PlayerDistanceInfo> playerDistanceInfos = new ArrayList<PlayerDistanceInfo>();
        for (AbstractClientPlayer player : allPlayers) {
            if (!EspetroTeamBridge.isPlayerVisibleOnTacticalMap((Player)player) || !EspetroTeamBridge.isSameTeam(localTeam, EspetroTeamBridge.getPlayerTeam((Player)player))) continue;
            double distance = localPlayer.m_20270_((Entity)player);
            playerDistanceInfos.add(new PlayerDistanceInfo(player, distance));
        }
        playerDistanceInfos.sort((a, b) -> {
            if (a.player == localPlayer) {
                return -1;
            }
            if (b.player == localPlayer) {
                return 1;
            }
            return Double.compare(a.distance, b.distance);
        });
        List selectedPlayers = playerDistanceInfos.subList(0, Math.min(4, playerDistanceInfos.size()));
        boolean bl = isLeftSide = displayMode == MapDisplayMode.ALWAYS_VISIBLE_BOTTOM_LEFT || displayMode == MapDisplayMode.ALWAYS_VISIBLE_TOP_LEFT;
        if (isLeftSide) {
            textX = mapLeft + mapWidth + 5;
        } else {
            int maxNameWidth = 0;
            for (PlayerDistanceInfo info : selectedPlayers) {
                int nameWidth = minecraft.f_91062_.m_92895_(info.player.m_7755_().getString());
                if (nameWidth <= maxNameWidth) continue;
                maxNameWidth = nameWidth;
            }
            textX = mapLeft - maxNameWidth - 25;
        }
        int totalHeight = selectedPlayers.size() * 12;
        int textY = mapTop + (mapHeight - totalHeight) / 2;
        ClientBattleState manager = ClientBattleState.get();
        String attackerTeam = manager.attackerTeam();
        String defenderTeam = manager.defenderTeam();
        String defenderHexColor = (String)TacticalMapConfig.defenderProgressBarColor.get();
        String attackerHexColor = (String)TacticalMapConfig.attackerProgressBarColor.get();
        int defenderColor = ModLogger.hexToColor(defenderHexColor, -16755201);
        int attackerColor = ModLogger.hexToColor(attackerHexColor, -43776);
        for (PlayerDistanceInfo info : selectedPlayers) {
            float maxHealth;
            float health = info.player.m_21223_();
            float healthPercentage = health / (maxHealth = info.player.m_21233_());
            int playerColor = healthPercentage >= 0.6f ? -16711936 : (healthPercentage >= 0.25f ? -256 : -65536);
            guiGraphics.m_280509_(textX, textY + 2, textX + 8, textY + 10, playerColor);
            guiGraphics.m_280056_(minecraft.f_91062_, info.player.m_7755_().getString(), textX + 12, textY, -1, false);
            textY += 12;
        }
    }

    private void renderPlayerArrow(GuiGraphics guiGraphics, LocalPlayer player, int centerX, int centerY) {
        int playerColor = -65536;
        float playerYaw = player.m_146908_();
        int arrowLength = 8;
        int arrowWidth = 3;
        int tipX = centerX - (int)(Math.sin(Math.toRadians(playerYaw)) * (double)arrowLength);
        int tipY = centerY + (int)(Math.cos(Math.toRadians(playerYaw)) * (double)arrowLength);
        float tailAngle = playerYaw + 180.0f;
        int leftX = centerX - (int)(Math.sin(Math.toRadians(tailAngle + 30.0f)) * (double)arrowWidth);
        int leftY = centerY + (int)(Math.cos(Math.toRadians(tailAngle + 30.0f)) * (double)arrowWidth);
        int rightX = centerX - (int)(Math.sin(Math.toRadians(tailAngle - 30.0f)) * (double)arrowWidth);
        int rightY = centerY + (int)(Math.cos(Math.toRadians(tailAngle - 30.0f)) * (double)arrowWidth);
        this.drawTriangle(guiGraphics, tipX, tipY, leftX, leftY, rightX, rightY, playerColor);
    }

    private void drawTriangle(GuiGraphics guiGraphics, int x1, int y1, int x2, int y2, int x3, int y3, int color) {
        this.drawLine(guiGraphics, x1, y1, x2, y2, color);
        this.drawLine(guiGraphics, x2, y2, x3, y3, color);
        this.drawLine(guiGraphics, x3, y3, x1, y1, color);
        int minY = Math.min(y1, Math.min(y2, y3));
        int maxY = Math.max(y1, Math.max(y2, y3));
        for (int y = minY; y <= maxY; ++y) {
            int x;
            int left = Integer.MAX_VALUE;
            int right = Integer.MIN_VALUE;
            if (y1 <= y && y < y2 || y2 <= y && y < y1) {
                x = this.interpolate(x1, y1, x2, y2, y);
                left = Math.min(left, x);
                right = Math.max(right, x);
            }
            if (y2 <= y && y < y3 || y3 <= y && y < y2) {
                x = this.interpolate(x2, y2, x3, y3, y);
                left = Math.min(left, x);
                right = Math.max(right, x);
            }
            if (y3 <= y && y < y1 || y1 <= y && y < y3) {
                x = this.interpolate(x3, y3, x1, y1, y);
                left = Math.min(left, x);
                right = Math.max(right, x);
            }
            if (left > right) continue;
            guiGraphics.m_280509_(left, y, right + 1, y + 1, color);
        }
    }

    private int interpolate(int x1, int y1, int x2, int y2, int y) {
        if (y1 == y2) {
            return x1;
        }
        return x1 + (x2 - x1) * (y - y1) / (y2 - y1);
    }

    private void drawLine(GuiGraphics guiGraphics, int x1, int y1, int x2, int y2, int color) {
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = x1 < x2 ? 1 : -1;
        int sy = y1 < y2 ? 1 : -1;
        int err = dx - dy;
        int x = x1;
        int y = y1;
        while (true) {
            guiGraphics.m_280509_(x, y, x + 1, y + 1, color);
            if (x == x2 && y == y2) break;
            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x += sx;
            }
            if (e2 >= dx) continue;
            err += dx;
            y += sy;
        }
    }

    private boolean isFriendlyCapture(CapturePoint point) {
        Minecraft mc = Minecraft.m_91087_();
        if (mc.f_91074_ == null) {
            return false;
        }
        String captorName = point.getCaptorName();
        if (captorName.isEmpty()) {
            return false;
        }
        return EspetroTeamBridge.isSameTeam(EspetroTeamBridge.getPlayerTeam((Player)mc.f_91074_), captorName);
    }

    private float calculateAngleDelta(float previous, float current) {
        float delta = current - previous;
        if (delta > 180.0f) {
            delta -= 360.0f;
        } else if (delta < -180.0f) {
            delta += 360.0f;
        }
        return delta;
    }

    public static TacticalMapHUD getInstance() {
        return InstanceHolder.INSTANCE;
    }

    private static final class MapViewport {
        private final int left;
        private final int top;
        private final int width;
        private final int height;
        private final double centerX;
        private final double centerZ;
        private final double spanX;
        private final double spanZ;
        private final double viewMinX;
        private final double viewMinZ;
        private final double viewMaxX;
        private final double viewMaxZ;
        private final double scaleX;
        private final double scaleZ;
        private final TacticalMapJsonConfig.TacticalMapBounds bounds;

        private MapViewport(int left, int top, int width, int height, double centerX, double centerZ, double spanX, double spanZ, TacticalMapJsonConfig.TacticalMapBounds bounds) {
            this.left = left;
            this.top = top;
            this.width = width;
            this.height = height;
            this.centerX = centerX;
            this.centerZ = centerZ;
            this.spanX = spanX;
            this.spanZ = spanZ;
            this.bounds = bounds;
            this.viewMinX = centerX - spanX / 2.0;
            this.viewMinZ = centerZ - spanZ / 2.0;
            this.viewMaxX = centerX + spanX / 2.0;
            this.viewMaxZ = centerZ + spanZ / 2.0;
            this.scaleX = (double)width / spanX;
            this.scaleZ = (double)height / spanZ;
        }

        private int right() {
            return this.left + this.width;
        }

        private int bottom() {
            return this.top + this.height;
        }

        private boolean containsWorld(double x, double z) {
            return x >= this.viewMinX && x <= this.viewMaxX && z >= this.viewMinZ && z <= this.viewMaxZ;
        }

        private boolean containsScreen(double x, double y) {
            return x >= (double)this.left && x <= (double)this.right() && y >= (double)this.top && y <= (double)this.bottom();
        }

        private int screenX(double worldX) {
            return (int)Math.round(this.screenXd(worldX));
        }

        private int screenY(double worldZ) {
            return (int)Math.round(this.screenYd(worldZ));
        }

        private double screenXd(double worldX) {
            return (double)this.left + (worldX - this.viewMinX) * this.scaleX;
        }

        private double screenYd(double worldZ) {
            return (double)this.top + (worldZ - this.viewMinZ) * this.scaleZ;
        }

        private double worldX(double screenX) {
            return Mth.m_14008_((double)(this.viewMinX + (screenX - (double)this.left) / this.scaleX), (double)this.viewMinX, (double)this.viewMaxX);
        }

        private double worldZ(double screenY) {
            return Mth.m_14008_((double)(this.viewMinZ + (screenY - (double)this.top) / this.scaleZ), (double)this.viewMinZ, (double)this.viewMaxZ);
        }
    }

    private static final class SmoothedPlayerMarker {
        private double fromX;
        private double fromZ;
        private float fromYaw;
        private double targetX;
        private double targetZ;
        private float targetYaw;
        private double renderX;
        private double renderZ;
        private float renderYaw;
        private double velocityX;
        private double velocityZ;
        private long interpolationStartedAt;
        private long interpolationDurationMs;
        private long lastUpdateAt;

        private SmoothedPlayerMarker(SyncPlayerPositionsMessage.PlayerPosition position, long now) {
            this.snapTo(position, now);
        }

        private void updateTarget(SyncPlayerPositionsMessage.PlayerPosition position, long now) {
            long intervalMs;
            this.sample(now);
            double dx = position.getX() - this.renderX;
            double dz = position.getZ() - this.renderZ;
            if (dx * dx + dz * dz >= 65536.0) {
                this.snapTo(position, now);
                return;
            }
            long elapsedMs = Math.max(1L, now - this.lastUpdateAt);
            double sampleDx = position.getX() - this.targetX;
            double sampleDz = position.getZ() - this.targetZ;
            this.velocityX = sampleDx / (double)elapsedMs;
            this.velocityZ = sampleDz / (double)elapsedMs;
            this.fromX = this.renderX;
            this.fromZ = this.renderZ;
            this.fromYaw = this.renderYaw;
            this.targetX = position.getX();
            this.targetZ = position.getZ();
            this.targetYaw = position.getYaw();
            this.interpolationStartedAt = now;
            this.interpolationDurationMs = intervalMs = Math.max(50L, Math.min(500L, elapsedMs + 50L));
            this.lastUpdateAt = now;
        }

        private void sample(long now) {
            if (this.interpolationDurationMs <= 0L) {
                this.renderX = this.targetX;
                this.renderZ = this.targetZ;
                this.renderYaw = this.targetYaw;
                return;
            }
            long elapsed = now - this.interpolationStartedAt;
            if (elapsed <= this.interpolationDurationMs) {
                double progress = Mth.m_14008_((double)((double)elapsed / (double)this.interpolationDurationMs), (double)0.0, (double)1.0);
                this.renderX = Mth.m_14139_((double)progress, (double)this.fromX, (double)this.targetX);
                this.renderZ = Mth.m_14139_((double)progress, (double)this.fromZ, (double)this.targetZ);
                this.renderYaw = this.fromYaw + (float)progress * Mth.m_14177_((float)(this.targetYaw - this.fromYaw));
                return;
            }
            long overshootMs = elapsed - this.interpolationDurationMs;
            if (overshootMs > 250L || this.velocityX == 0.0 && this.velocityZ == 0.0) {
                this.renderX = this.targetX;
                this.renderZ = this.targetZ;
                this.renderYaw = this.targetYaw;
                return;
            }
            double integratedMs = (double)overshootMs - (double)(overshootMs * overshootMs) / 500.0;
            this.renderX = this.targetX + this.velocityX * integratedMs;
            this.renderZ = this.targetZ + this.velocityZ * integratedMs;
            this.renderYaw = this.targetYaw;
        }

        private void snapTo(SyncPlayerPositionsMessage.PlayerPosition position, long now) {
            this.targetX = this.renderX = position.getX();
            this.fromX = this.renderX;
            this.targetZ = this.renderZ = position.getZ();
            this.fromZ = this.renderZ;
            this.targetYaw = this.renderYaw = position.getYaw();
            this.fromYaw = this.renderYaw;
            this.velocityX = 0.0;
            this.velocityZ = 0.0;
            this.interpolationStartedAt = now;
            this.interpolationDurationMs = 0L;
            this.lastUpdateAt = now;
        }
    }

    private static final class MarkerCenter {
        private final double x;
        private final double z;

        private MarkerCenter(double x, double z) {
            this.x = x;
            this.z = z;
        }
    }

    private static final class HoveredMapMarker {
        private final int x;
        private final int z;

        private HoveredMapMarker(int x, int z) {
            this.x = x;
            this.z = z;
        }
    }

    private static class PlayerDistanceInfo {
        private final AbstractClientPlayer player;
        private final double distance;

        public PlayerDistanceInfo(AbstractClientPlayer player, double distance) {
            this.player = player;
            this.distance = distance;
        }
    }

    private static class InstanceHolder {
        private static final TacticalMapHUD INSTANCE = new TacticalMapHUD();

        private InstanceHolder() {
        }
    }
}

