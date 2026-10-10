/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 *  org.lwjgl.glfw.GLFW
 */
package org.espetro.client.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.espetro.client.aui.AuiRadial;
import org.espetro.client.aui.AuiRadialSlot;
import org.espetro.client.gui.RadioRadialController;
import org.espetro.logistics.resupply.ResupplySourceRef;
import org.espetro.network.NetworkManager;
import org.espetro.network.RequestResupplyCatalogPacket;
import org.espetro.network.VehicleSupplyActionPacket;
import org.espetro.network.VehicleSupplySyncPacket;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

public final class VehicleWheelController {
    private static final int OPEN_DELAY_TICKS = 6;
    private static final double INTERACT_RANGE = 5.0;
    private static final int WHEEL_INNER = 44;
    private static final String ROOT = "vehicle_root";
    private static final ResourceLocation ICON_AMMO_WHITE = VehicleWheelController.id("textures/gui/squad/ammo_supply_white.png");
    private static final ResourceLocation ICON_AMMO_RED = VehicleWheelController.id("textures/gui/squad/ammo_supply.png");
    private static final ResourceLocation ICON_CONSTRUCTION_WHITE = VehicleWheelController.id("textures/gui/squad/vehicle_supply_load.png");
    private static final ResourceLocation ICON_CONSTRUCTION_RED = VehicleWheelController.id("textures/gui/squad/vehicle_supply_unload.png");
    private static final ResourceLocation ICON_RESUPPLY = VehicleWheelController.id("textures/gui/squad/ammo_crate.png");
    private static final String COLOR_LOAD = "#FFFFFFFF";
    private static final String COLOR_UNLOAD = "#FFFF4A4A";
    private static boolean initialized;
    private static boolean keyWasDown;
    private static boolean ownsOverlay;
    private static boolean consumedUntilRelease;
    private static boolean snapshotReady;
    private static int heldTicks;
    private static UUID currentVehicleId;
    private static VehicleSupplySyncPacket cachedSupply;
    private static String holdAction;
    private static int holdProgress;

    private VehicleWheelController() {
    }

    public static boolean isWheelActive() {
        return ownsOverlay;
    }

    public static boolean isCenterHovered() {
        double cy;
        double dy;
        if (!ownsOverlay || !AuiRadial.isOpen()) {
            return false;
        }
        if (AuiRadial.hoveredIndex() >= 0) {
            return false;
        }
        Minecraft mc = Minecraft.m_91087_();
        if (mc == null) {
            return false;
        }
        double mx = mc.f_91067_.m_91589_() * (double)mc.m_91268_().m_85445_() / (double)mc.m_91268_().m_85443_();
        double my = mc.f_91067_.m_91594_() * (double)mc.m_91268_().m_85446_() / (double)mc.m_91268_().m_85444_();
        double cx = (double)mc.m_91268_().m_85445_() * 0.5;
        double dx = mx - cx;
        return dx * dx + (dy = my - (cy = (double)mc.m_91268_().m_85446_() * 0.5)) * dy <= 1936.0;
    }

    public static boolean isInteractHeld() {
        Minecraft mc = Minecraft.m_91087_();
        if (mc == null) {
            return false;
        }
        return VehicleWheelController.isInteractKeyDown(mc.m_91268_().m_85439_());
    }

    public static boolean isHolding() {
        return holdAction != null;
    }

    public static int getHoldProgress() {
        return holdProgress;
    }

    public static int getHoldColor() {
        return holdAction != null && holdAction.contains("CONSTRUCTION") ? -3364352 : -3390396;
    }

    @Nullable
    public static VehicleSupplySyncPacket getCachedSupply() {
        return cachedSupply;
    }

    public static void initialize() {
        if (initialized) {
            return;
        }
        initialized = true;
    }

    private static void handleAction(String actionName) {
        if (actionName == null || actionName.isEmpty() || currentVehicleId == null) {
            return;
        }
        if (VehicleWheelController.isTransferAction(actionName)) {
            holdAction = actionName;
            holdProgress = 0;
            VehicleWheelController.sendSupplyAction(actionName);
            consumedUntilRelease = true;
            ownsOverlay = true;
            return;
        }
        try {
            VehicleSupplyActionPacket.Action action = VehicleSupplyActionPacket.Action.valueOf(actionName);
            if (action == VehicleSupplyActionPacket.Action.RESUPPLY_INFANTRY) {
                NetworkManager.NET.sendToServer((Object)new RequestResupplyCatalogPacket(ResupplySourceRef.vehicle(currentVehicleId)));
                consumedUntilRelease = true;
                ownsOverlay = true;
                return;
            }
            if (action == VehicleSupplyActionPacket.Action.CHANGE_CLASS) {
                RadioRadialController.markNextClassListAsVehicle(currentVehicleId);
            }
            NetworkManager.NET.sendToServer((Object)new VehicleSupplyActionPacket(currentVehicleId, action));
        }
        catch (IllegalArgumentException ignored) {
            return;
        }
        consumedUntilRelease = true;
        ownsOverlay = true;
    }

    static boolean isTransferAction(String action) {
        return action.equals("LOAD_AMMO") || action.equals("UNLOAD_AMMO") || action.equals("LOAD_CONSTRUCTION") || action.equals("UNLOAD_CONSTRUCTION");
    }

    private static void sendSupplyAction(String actionName) {
        try {
            NetworkManager.NET.sendToServer((Object)new VehicleSupplyActionPacket(currentVehicleId, VehicleSupplyActionPacket.Action.valueOf(actionName)));
        }
        catch (IllegalArgumentException illegalArgumentException) {
            // empty catch block
        }
    }

    private static List<AuiRadialSlot> buildRootMenu() {
        ArrayList<AuiRadialSlot> slots = new ArrayList<AuiRadialSlot>();
        if (cachedSupply == null || !cachedSupply.hasAnyAction()) {
            return slots;
        }
        if (cachedSupply.canTransferAmmo()) {
            slots.add(VehicleWheelController.actionSlot("espetro.veh.load_ammo", ICON_AMMO_WHITE, "\u88c5\u8f7d\u5f39\u836f", COLOR_LOAD, "#FF3D4650", "LOAD_AMMO"));
            slots.add(VehicleWheelController.actionSlot("espetro.veh.unload_ammo", ICON_AMMO_RED, "\u5378\u4e0b\u5f39\u836f", COLOR_UNLOAD, "#FF5C2525", "UNLOAD_AMMO"));
        }
        if (cachedSupply.canTransferConstruction()) {
            slots.add(VehicleWheelController.actionSlot("espetro.veh.load_construction", ICON_CONSTRUCTION_WHITE, "\u88c5\u8f7d\u5efa\u6750", COLOR_LOAD, "#FF3D4650", "LOAD_CONSTRUCTION"));
            slots.add(VehicleWheelController.actionSlot("espetro.veh.unload_construction", ICON_CONSTRUCTION_RED, "\u5378\u4e0b\u5efa\u6750", COLOR_UNLOAD, "#FF5C2525", "UNLOAD_CONSTRUCTION"));
        }
        if (cachedSupply.canResupplyInfantry()) {
            slots.add(VehicleWheelController.actionSlot("espetro.veh.resupply_infantry", ICON_RESUPPLY, "\u8865\u7ed9\u6b65\u5175", COLOR_LOAD, "#FF725E19", "RESUPPLY_INFANTRY"));
        }
        if (cachedSupply.isSupplyVehicle() || cachedSupply.isFightVehicle()) {
            slots.add(VehicleWheelController.actionSlot("espetro.veh.change_class", ICON_AMMO_WHITE, "\u66f4\u6362\u804c\u4e1a", COLOR_LOAD, "#FF3D4650", "CHANGE_CLASS"));
        }
        return slots;
    }

    private static AuiRadialSlot actionSlot(String id, ResourceLocation icon, String label, String color, String accent, String actionName) {
        return AuiRadialSlot.texture(id, Component.m_237113_(label), icon, accent, () -> VehicleWheelController.handleAction(actionName));
    }

    public static void updateSupply(VehicleSupplySyncPacket packet) {
        if (packet == null || packet.isRequest() || currentVehicleId == null || !currentVehicleId.equals(packet.getVehicleId())) {
            return;
        }
        String previousLayout = VehicleWheelController.layoutSignature(cachedSupply);
        cachedSupply = packet;
        snapshotReady = packet.hasAnyAction();
        if (!previousLayout.equals(VehicleWheelController.layoutSignature(packet)) && AuiRadial.isOpen() && AuiRadial.isPage(ROOT)) {
            AuiRadial.replace(VehicleWheelController.buildRootMenu(), ROOT);
        }
    }

    static String layoutSignature(@Nullable VehicleSupplySyncPacket packet) {
        if (packet == null) {
            return "";
        }
        return (packet.canTransferAmmo() ? "A" : "-") + (packet.canTransferConstruction() ? "C" : "-") + (packet.canResupplyInfantry() ? "R" : "-") + (packet.isSupplyVehicle() ? "S" : "-");
    }

    public static void tick(Minecraft minecraft) {
        if (!initialized || minecraft == null || minecraft.f_91074_ == null) {
            VehicleWheelController.reset();
            return;
        }
        if (minecraft.f_91080_ != null) {
            VehicleWheelController.closeOwnedOverlay();
            keyWasDown = false;
            heldTicks = 0;
            return;
        }
        long window = minecraft.m_91268_().m_85439_();
        boolean down = VehicleWheelController.isInteractKeyDown(window);
        if (!down) {
            if (keyWasDown) {
                VehicleWheelController.closeOwnedOverlay();
            }
            keyWasDown = false;
            heldTicks = 0;
            consumedUntilRelease = false;
            holdAction = null;
            holdProgress = 0;
            currentVehicleId = null;
            cachedSupply = null;
            snapshotReady = false;
            return;
        }
        if (!keyWasDown) {
            currentVehicleId = VehicleWheelController.findLookedAtVehicle(minecraft);
            snapshotReady = false;
            cachedSupply = null;
            if (currentVehicleId != null) {
                NetworkManager.NET.sendToServer((Object)VehicleSupplySyncPacket.request(currentVehicleId));
            }
        }
        keyWasDown = true;
        if (currentVehicleId == null) {
            return;
        }
        if (consumedUntilRelease) {
            if (ownsOverlay && AuiRadial.isOpen() && AuiRadial.isPage(ROOT)) {
                VehicleWheelController.tickHold(minecraft);
            }
            return;
        }
        if (ownsOverlay || AuiRadial.isOpen()) {
            return;
        }
        if (++heldTicks >= 6 && snapshotReady) {
            AuiRadial.show(VehicleWheelController.buildRootMenu(), ROOT);
            ownsOverlay = true;
        }
    }

    static List<String> visibleActions(@Nullable VehicleSupplySyncPacket supply) {
        ArrayList<String> actions = new ArrayList<String>(5);
        if (supply != null && supply.canTransferAmmo()) {
            actions.add("LOAD_AMMO");
            actions.add("UNLOAD_AMMO");
        }
        if (supply != null && supply.canTransferConstruction()) {
            actions.add("LOAD_CONSTRUCTION");
            actions.add("UNLOAD_CONSTRUCTION");
        }
        if (supply != null && supply.canResupplyInfantry()) {
            actions.add("RESUPPLY_INFANTRY");
        }
        if (supply != null && (supply.isSupplyVehicle() || supply.isFightVehicle())) {
            actions.add("CHANGE_CLASS");
        }
        return actions;
    }

    private static void tickHold(Minecraft minecraft) {
        boolean leftDown;
        AuiRadial.tickHover(minecraft);
        int slot = AuiRadial.hoveredIndex();
        List<String> actions = VehicleWheelController.visibleActions(cachedSupply);
        String action = slot >= 0 && slot < actions.size() ? actions.get(slot) : null;
        long window = minecraft.m_91268_().m_85439_();
        boolean bl = leftDown = GLFW.glfwGetMouseButton((long)window, (int)0) == 1;
        if (action == null || !VehicleWheelController.isTransferAction(action) || !leftDown) {
            holdAction = null;
            holdProgress = 0;
            return;
        }
        if (!action.equals(holdAction)) {
            holdProgress = 0;
        }
        holdAction = action;
        int interval = Math.max(1, cachedSupply == null ? 20 : cachedSupply.getTransferIntervalTicks());
        if (++holdProgress >= interval) {
            holdProgress = 0;
            VehicleWheelController.sendSupplyAction(action);
        }
    }

    @Nullable
    private static UUID findLookedAtVehicle(Minecraft minecraft) {
        Vec3 look;
        Vec3 end;
        if (minecraft.f_91074_ == null || minecraft.f_91073_ == null) {
            return null;
        }
        Vec3 eye = minecraft.f_91074_.m_20299_(1.0f);
        EntityHitResult hit = ProjectileUtil.m_37287_(minecraft.f_91074_, eye, end = eye.m_82549_((look = minecraft.f_91074_.m_20154_()).m_82490_(5.0)), minecraft.f_91074_.m_20191_().m_82369_(look.m_82490_(5.0)).m_82400_(1.0), entity -> entity != minecraft.f_91074_ && entity.m_6087_(), 25.0);
        if (hit == null) {
            return null;
        }
        Entity root = hit.m_82443_().m_20201_();
        return root == null || root == minecraft.f_91074_ ? null : root.m_20148_();
    }

    private static void closeOwnedOverlay() {
        if (AuiRadial.isOpen()) {
            AuiRadial.hide();
        }
        ownsOverlay = false;
    }

    public static void replaceRoot() {
        if (cachedSupply == null || !cachedSupply.hasAnyAction()) {
            return;
        }
        List<AuiRadialSlot> slots = VehicleWheelController.buildRootMenu();
        if (AuiRadial.isOpen()) {
            AuiRadial.replace(slots, ROOT);
        } else {
            AuiRadial.show(slots, ROOT);
        }
        ownsOverlay = true;
        consumedUntilRelease = true;
    }

    private static void reset() {
        VehicleWheelController.closeOwnedOverlay();
        keyWasDown = false;
        heldTicks = 0;
        consumedUntilRelease = false;
        snapshotReady = false;
        currentVehicleId = null;
        cachedSupply = null;
        holdAction = null;
        holdProgress = 0;
    }

    private static boolean isInteractKeyDown(long window) {
        try {
            Class<?> keys = Class.forName("com.atsuishio.superbwarfare.init.ModKeyMappings");
            Object mapping = keys.getField("INTERACT").get(null);
            if (mapping instanceof KeyMapping) {
                KeyMapping keyMapping = (KeyMapping)mapping;
                return keyMapping.m_90857_();
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return GLFW.glfwGetKey((long)window, (int)70) == 1;
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath((String)"espetro", (String)path);
    }
}

