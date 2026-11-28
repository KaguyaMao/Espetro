package org.espetro.client.gui;

import org.esradial.client.Actions;
import org.esradial.client.RadialMenuClientApi;
import org.esradial.client.RadialMenuBuilder;
import org.esradial.client.RadialMenuData;
import org.esradial.client.RadialMenuRegistry;
import org.esradial.client.RadialMenuOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.espetro.network.NetworkManager;
import org.espetro.network.RequestResupplyCatalogPacket;
import org.espetro.network.VehicleSupplyActionPacket;
import org.espetro.network.VehicleSupplySyncPacket;
import org.espetro.logistics.resupply.ResupplySourceRef;
import org.espetro.vehicle.SbwVehicleSeatResolver;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.espetro.client.gui.UiTheme;

/** Opens the interaction wheel for the ridden vehicle or the vehicle under the crosshair. */
public final class VehicleWheelController {

    private static final String OWNER = "espetro_vehicle";
    private static final int OPEN_DELAY_TICKS = 6;
    private static final double INTERACT_RANGE = 5.0;
    private static final int WHEEL_INNER = 44;
    private static final int WHEEL_OUTER = 100;

    private static final ResourceLocation ROOT = id("vehicle_root");
    private static final ResourceLocation ACTION_ID = id("vehicle_action");
    private static final ResourceLocation ICON_AMMO_LOAD = vehicleIcon("radial_ammo_load");
    private static final ResourceLocation ICON_AMMO_UNLOAD = vehicleIcon("radial_ammo_unload");
    private static final ResourceLocation ICON_BUILD_LOAD = vehicleIcon("radial_build_load");
    private static final ResourceLocation ICON_BUILD_UNLOAD = vehicleIcon("radial_build_unload");
    private static final ResourceLocation ICON_RESUPPLY = vehicleIcon("radial_resupply");
    private static final ResourceLocation ICON_ANY_ROLE = vehicleIcon("radial_any_role");
    private static final ResourceLocation ICON_CREW_ROLE = vehicleIcon("radial_crew_role");
    private static final String NO_DROP_POINT = "附近没有补给投放点";
    private static final String NO_CONSTRUCTION = "此载具不支持建材补给";
    private static final int REFRESH_TICKS = 20;

    private static final ResourceLocation ICON_RESET_VEHICLE =
        id("textures/gui/vehicle/reset_vehicle.png");

    /** 「重置载具」长按所需 tick（10 秒）。 */
    private static final int RESET_HOLD_TICKS = 200;
    private static final String ACTION_RESET_VEHICLE = "RESET_VEHICLE";

    private static boolean initialized;
    private static boolean keyWasDown;
    private static boolean ownsOverlay;
    private static boolean consumedUntilRelease;
    private static boolean snapshotReady;
    private static int heldTicks;
    private static int refreshTicks;
    private static UUID currentVehicleId;
    private static boolean startedMounted;

    public static boolean isMountedInteraction() { return startedMounted; }

    public static void completeSeatInteraction() {
        consumedUntilRelease=true;
        closeOwnedOverlay();
    }
    private static VehicleSupplySyncPacket cachedSupply;
    private static long themeRevision = -1L;

    private static String holdAction;
    private static int holdProgress;

    /** 当前“重置载具”长按累计 tick；<=0 表示没在按。 */
    private static int resetHoldTicks;
    private static boolean resetSent;

    private VehicleWheelController() {
    }

    public static boolean isWheelActive() {
        return ownsOverlay && RadialMenuClientApi.isActive() && RadialMenuClientApi.isOwnedBy(OWNER);
    }

    static boolean isVehicleRootActive() {
        return isWheelActive() && RadialMenuClientApi.activeMenuId().filter(ROOT::equals).isPresent();
    }

    static int displayRadius(int width, int height) {
        return Math.max(0, Math.min(WHEEL_OUTER, Math.min(width - 32, height - 64) / 2));
    }

    /**
     * True when the wheel is open, no radial slot is hovered, and the cursor
     * is inside the inner radius (center icon zone used by the mount channel).
     */
    public static boolean isCenterHovered() {
        if (!isWheelActive() || !RadialMenuClientApi.activeMenuId().filter(ROOT::equals).isPresent()) {
            return false;
        }
        if (RadialMenuClientApi.hoveredSlotIndex() >= 0) {
            return false;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc == null) {
            return false;
        }
        return RadialMenuClientApi.isCenterHovered();
    }

    public static boolean isInteractHeld() {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null) {
            return false;
        }
        return isInteractKeyDown(mc.getWindow().getWindow());
    }

    public static boolean isHolding() {
        return holdAction != null;
    }

    public static int getHoldProgress() {
        return holdProgress;
    }

    public static int getHoldColor() {
        return holdAction != null && holdAction.contains("CONSTRUCTION")
            ? UiTheme.WARNING : UiTheme.NEGATIVE;
    }

    @Nullable
    public static VehicleSupplySyncPacket getCachedSupply() {
        return cachedSupply;
    }

    public static void initialize() {
        if (initialized) return;
        initialized = true;

        Actions.register(ACTION_ID, params -> {
            String actionName = params.getString("action", "");
            if (actionName.isEmpty() || currentVehicleId == null) return;
            if (ACTION_RESET_VEHICLE.equals(actionName)) {
                // 10 秒长按由 tickResetHold 自己计时发包；这里被脚本触发时什么也不做。
                ownsOverlay = true;
                return;
            }
            if (isTransferAction(actionName)) {
                if (!canTransfer(cachedSupply, actionName)) return;
                holdAction = actionName;
                holdProgress = 0;
                sendSupplyAction(actionName);
                consumedUntilRelease = true;
                ownsOverlay = true;
                return;
            }
            try {
                VehicleSupplyActionPacket.Action action =
                    VehicleSupplyActionPacket.Action.valueOf(actionName);
                if (action == VehicleSupplyActionPacket.Action.RESUPPLY_INFANTRY) {
                    NetworkManager.NET.sendToServer(new RequestResupplyCatalogPacket(
                        ResupplySourceRef.vehicle(currentVehicleId)));
                    consumedUntilRelease = true;
                    ownsOverlay = true;
                    return;
                }
                if (action == VehicleSupplyActionPacket.Action.CHANGE_CLASS) {
                    RadioRadialController.markNextClassListAsVehicle(
                        currentVehicleId);
                }
                NetworkManager.NET.sendToServer(
                    new VehicleSupplyActionPacket(currentVehicleId, action));
            } catch (IllegalArgumentException ignored) {
                return;
            }
            consumedUntilRelease = true;
            ownsOverlay = true;
        });
    }

    static boolean isTransferAction(String action) {
        return action.equals("LOAD_AMMO") || action.equals("UNLOAD_AMMO")
            || action.equals("LOAD_CONSTRUCTION") || action.equals("UNLOAD_CONSTRUCTION");
    }

    private static void sendSupplyAction(String actionName) {
        try {
            NetworkManager.NET.sendToServer(new VehicleSupplyActionPacket(
                currentVehicleId, VehicleSupplyActionPacket.Action.valueOf(actionName)));
        } catch (IllegalArgumentException ignored) {
        }
    }

    private static void publishMenu() {
        if (cachedSupply == null || !cachedSupply.hasAnyAction()) return;
        RadialMenuRegistry.setMenus(OWNER, List.of(buildRootMenu()));
        themeRevision = UiTheme.revision();
    }

    private static org.esradial.client.RadialMenuData buildRootMenu() {
        RadialMenuBuilder builder = new RadialMenuBuilder(ROOT)
            .title(Component.literal("载具交互"))
            .radii(WHEEL_INNER, WHEEL_OUTER)
            .animationSpeed(1.25f)
            .ringColors(RadialGlassStyle.ringColors())
            .progress(() -> {
                // 「重置载具」长按时优先显示它的 10 秒进度，其余动作沿用转交进度。
                if (resetHoldTicks > 0) {
                    float hold = Math.min(1.0F, resetHoldTicks / (float) RESET_HOLD_TICKS);
                    return new RadialMenuData.Progress(null, hold, UiTheme.hex(UiTheme.WARNING));
                }
                return RadialMenuData.Progress.NONE;
            });

        String gold = UiTheme.hex(UiTheme.WORLD_WARNING);
        if (showsTransfers(cachedSupply)) {
            transfer(builder, "LOAD_AMMO", ICON_AMMO_LOAD, "装载弹药", 240);
            transfer(builder, "UNLOAD_AMMO", ICON_AMMO_UNLOAD, "卸载弹药", 180);
        }
        transfer(builder, "LOAD_CONSTRUCTION", ICON_BUILD_LOAD, "装载建材", 60);
        transfer(builder, "UNLOAD_CONSTRUCTION", ICON_BUILD_UNLOAD, "卸载建材", 120);
        if (cachedSupply.canResupplyInfantry()) {
            builder.persistentSlot("espetro.veh.resupply_infantry", ICON_RESUPPLY,
                Actions.script(ACTION_ID, Map.of("action", "RESUPPLY_INFANTRY")),
                Component.literal("补给步兵"), gold, gold).tintLast(UiTheme.WORLD_WARNING).sectorLast(0,60);
        }
        if (cachedSupply.isSupplyVehicle() || cachedSupply.isFightVehicle()) {
            builder.persistentSlot("espetro.veh.change_class",
                isClassChangeCrewOnly() ? ICON_CREW_ROLE : ICON_ANY_ROLE,
                Actions.script(ACTION_ID, Map.of("action", "CHANGE_CLASS")),
                Component.literal("更换职业"), gold, gold).tintLast(UiTheme.WORLD_WARNING).sectorLast(330,30);
        }
        builder.persistentSlot("espetro.veh.reset_vehicle", ICON_RESET_VEHICLE,
            Actions.script(ACTION_ID, Map.of("action", ACTION_RESET_VEHICLE)),
            Component.literal("重置载具 (长按10秒)"), gold, gold)
            .tintLast(UiTheme.WORLD_WARNING).sectorLast(300,30);
        return builder.build();
    }

    private static void transfer(RadialMenuBuilder builder, String action, ResourceLocation icon,
                                 String label, int angle) {
        builder.persistentSlot("espetro.veh." + action.toLowerCase(java.util.Locale.ROOT), icon,
            Actions.script(ACTION_ID, Map.of("action", action)), Component.literal(label),
            UiTheme.hex(UiTheme.WORLD_TEXT), UiTheme.hex(UiTheme.WORLD_TEXT)).sectorLast(angle, 60);
        if (canTransfer(cachedSupply, action)) {
            builder.repeatLast(cachedSupply.getTransferIntervalTicks());
        } else if (action.endsWith("CONSTRUCTION") && !cachedSupply.canCarryConstruction()) {
            builder.disabledLast(Component.literal(NO_CONSTRUCTION)).tintLast(0xFF757575);
        } else {
            builder.disabledLast(Component.literal(NO_DROP_POINT));
        }
    }

    private static boolean showsTransfers(@Nullable VehicleSupplySyncPacket supply) {
        return supply != null && (supply.isSupplyVehicle() || supply.isFightVehicle());
    }

    static boolean canTransfer(@Nullable VehicleSupplySyncPacket supply, String action) {
        return supply != null && (action.endsWith("CONSTRUCTION")
            ? supply.canTransferConstruction() : supply.canTransferAmmo());
    }

    /** Called on the client thread by the packet handler. */
    public static void updateSupply(VehicleSupplySyncPacket packet) {
        if (packet == null || packet.isRequest() || currentVehicleId == null
            || !currentVehicleId.equals(packet.getVehicleId())) return;
        String previousLayout = layoutSignature(cachedSupply);
        cachedSupply = packet;
        snapshotReady = packet.hasAnyAction();
        if (!previousLayout.equals(layoutSignature(packet))) {
            if (RadialMenuClientApi.activeMenuId().filter(ROOT::equals).isPresent()) {
                RadialMenuClientApi.replace(buildRootMenu());
            } else {
                publishMenu();
            }
        }
    }

    static String layoutSignature(@Nullable VehicleSupplySyncPacket packet) {
        if (packet == null) return "";
        return (packet.canTransferAmmo() ? "A" : "-")
            + (packet.canTransferConstruction() ? "C" : "-")
            + (packet.canResupplyInfantry() ? "R" : "-")
            + (packet.isSupplyVehicle() ? "S" : "-")
            + (packet.isFightVehicle() ? "F" : "-")
            + (packet.allowsFullClassChange() ? "K" : "-") + ":" + packet.getTransferIntervalTicks();
    }

    public static void tick(Minecraft minecraft) {
        if (!initialized || minecraft == null || minecraft.player == null) {
            reset();
            return;
        }
        if (cachedSupply != null && themeRevision != UiTheme.revision()) {
            publishMenu();
            if (isWheelActive() && RadialMenuClientApi.activeMenuId().filter(ROOT::equals).isPresent()) {
                RadialMenuClientApi.replace(buildRootMenu());
            }
        }
        if (minecraft.screen != null) {
            closeOwnedOverlay();
            keyWasDown = false;
            heldTicks = 0;
            return;
        }

        long window = minecraft.getWindow().getWindow();
        // Align with SBW「交互」; fall back to F if SBW client classes are absent.
        boolean down = isInteractKeyDown(window);
        if (!down) {
            if (keyWasDown) closeOwnedOverlay();
            keyWasDown = false;
            heldTicks = 0;
            consumedUntilRelease = false;
            holdAction = null;
            holdProgress = 0;
            resetHoldTicks = 0;
            resetSent = false;
            currentVehicleId = null;
            cachedSupply = null;
            snapshotReady = false;
            return;
        }

        if (!keyWasDown) {
            refreshTicks = 0;
            Entity mountedVehicle = SbwVehicleSeatResolver.findMountedVehicle(minecraft.player);
            startedMounted = mountedVehicle != null;
            currentVehicleId = startedMounted ? mountedVehicle.getUUID() : findLookedAtVehicle(minecraft);
            snapshotReady = false;
            cachedSupply = null;
            if (currentVehicleId != null) {
                NetworkManager.NET.sendToServer(VehicleSupplySyncPacket.request(currentVehicleId));
            }
        }
        keyWasDown = true;
        if (currentVehicleId == null) return;
        if (isWheelActive() && RadialMenuClientApi.activeMenuId().filter(ROOT::equals).isPresent()
                && ++refreshTicks >= REFRESH_TICKS) {
            refreshTicks = 0;
            NetworkManager.NET.sendToServer(VehicleSupplySyncPacket.request(currentVehicleId));
        }

        if (consumedUntilRelease) {
            if (ownsOverlay && RadialMenuClientApi.activeMenuId().filter(ROOT::equals).isPresent()) {
                tickHold(window);
                tickResetHold(window);
            }
            return;
        }
        if (ownsOverlay || RadialMenuOverlay.INSTANCE.isActive()) {
            if (ownsOverlay && RadialMenuClientApi.activeMenuId().filter(ROOT::equals).isPresent()) {
                tickResetHold(window);
            }
            return;
        }

        heldTicks++;
        if (snapshotReady && (startedMounted || heldTicks >= OPEN_DELAY_TICKS)) {
            publishMenu();
            var data = RadialMenuRegistry.getRuntimeMenu(ROOT);
            ownsOverlay = data != null && RadialMenuClientApi.open(data,
                new RadialMenuClientApi.OpenOptions(OWNER, () -> isInteractKeyDown(window), false, reason -> { ownsOverlay = false; consumedUntilRelease = true; }));
        }
    }

    /** Match the permission used by the server to populate the class list. */
    static boolean isClassChangeCrewOnly() {
        return cachedSupply == null || !cachedSupply.allowsFullClassChange();
    }

    static List<String> visibleActions(@Nullable VehicleSupplySyncPacket supply) {
        List<String> actions = new ArrayList<>(7);
        if (showsTransfers(supply)) {
            actions.addAll(List.of("LOAD_AMMO", "UNLOAD_AMMO"));
        }
        if (supply != null && !supply.isRequest()) {
            actions.addAll(List.of("LOAD_CONSTRUCTION", "UNLOAD_CONSTRUCTION"));
        }
        if (supply != null && supply.canResupplyInfantry()) {
            actions.add("RESUPPLY_INFANTRY");
        }
        if (supply != null
            && (supply.isSupplyVehicle() || supply.isFightVehicle())) {
            actions.add("CHANGE_CLASS");
        }
        if (supply != null && supply.hasAnyAction()) {
            actions.add(ACTION_RESET_VEHICLE);
        }
        return actions;
    }

    private static List<String> visibleActions() {
        return visibleActions(cachedSupply);
    }

    private static void tickHold(long window) {
        int slot = RadialMenuClientApi.hoveredSlotIndex();
        List<String> actions = visibleActions();
        String action = slot >= 0 && slot < actions.size() ? actions.get(slot) : null;
        boolean leftDown = GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_LEFT)
            == GLFW.GLFW_PRESS;
        if (action == null || !isTransferAction(action) || !canTransfer(cachedSupply, action) || !leftDown) {
            holdAction = null;
            holdProgress = 0;
            return;
        }
        holdAction = action;
        int interval = Math.max(1, cachedSupply == null ? 20 : cachedSupply.getTransferIntervalTicks());
        // EsRadial owns the one and only repeat clock; HUD only reads its progress.
        holdProgress = (int) Math.round(RadialMenuClientApi.repeatProgress() * interval);
    }

    /**
     * 「重置载具」长按计时：鼠标停在 2~3 点槽位并按住左键满 10 秒才发包一次。
     * 松开左键后清零，可以在轮盘不关闭的情况下再来一次。
     */
    private static void tickResetHold(long window) {
        int slot = RadialMenuClientApi.hoveredSlotIndex();
        List<String> actions = visibleActions();
        String action = slot >= 0 && slot < actions.size() ? actions.get(slot) : null;
        boolean leftDown = GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_LEFT)
            == GLFW.GLFW_PRESS;
        if (!leftDown) {
            resetHoldTicks = 0;
            resetSent = false;
            return;
        }
        if (!ACTION_RESET_VEHICLE.equals(action)) {
            resetHoldTicks = 0;
            return;
        }
        if (resetSent) return;
        resetHoldTicks++;
        if (resetHoldTicks < RESET_HOLD_TICKS) return;
        resetHoldTicks = 0;
        resetSent = true;
        if (currentVehicleId == null) return;
        NetworkManager.NET.sendToServer(
            new VehicleSupplyActionPacket(currentVehicleId,
                VehicleSupplyActionPacket.Action.RESET_VEHICLE));
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player != null) {
            minecraft.player.displayClientMessage(
                Component.literal("正在重置载具…")
                    .withStyle(style -> style.withColor(UiTheme.WORLD_WARNING)), true);
        }
    }

    @Nullable
    private static UUID findLookedAtVehicle(Minecraft minecraft) {
        if (minecraft.player == null || minecraft.level == null) return null;
        Vec3 eye = minecraft.player.getEyePosition(1.0F);
        Vec3 look = minecraft.player.getLookAngle();
        Vec3 end = eye.add(look.scale(INTERACT_RANGE));
        EntityHitResult hit = ProjectileUtil.getEntityHitResult(
            minecraft.player, eye, end,
            minecraft.player.getBoundingBox().expandTowards(look.scale(INTERACT_RANGE)).inflate(1.0D),
            entity -> entity != minecraft.player && entity.isPickable(),
            INTERACT_RANGE * INTERACT_RANGE);
        if (hit == null) return null;
        Entity root = hit.getEntity().getRootVehicle();
        return root == null || root == minecraft.player ? null : root.getUUID();
    }

    private static void closeOwnedOverlay() {
        if (ownsOverlay && RadialMenuOverlay.INSTANCE.isActive()) {
            RadialMenuClientApi.close(OWNER);
        }
        ownsOverlay = false;
    }

    /** Replace a currently visible vehicle child menu without close/reopen flicker. */
    public static void replaceRoot() {
        if (cachedSupply == null || !cachedSupply.hasAnyAction()) return;
        if (!RadialMenuClientApi.replace(buildRootMenu())) {
            publishMenu();
            var data = RadialMenuRegistry.getRuntimeMenu(ROOT);
            if (data != null) RadialMenuClientApi.open(data,
                new RadialMenuClientApi.OpenOptions(OWNER,
                    () -> isInteractKeyDown(Minecraft.getInstance().getWindow().getWindow()), false, reason -> { ownsOverlay = false; consumedUntilRelease = true; }));
        }
        ownsOverlay = true;
        consumedUntilRelease = true;
    }

    private static void reset() {
        closeOwnedOverlay();
        keyWasDown = false;
        heldTicks = 0;
        consumedUntilRelease = false;
        snapshotReady = false;
        currentVehicleId = null;
        startedMounted = false;
        cachedSupply = null;
        holdAction = null;
        holdProgress = 0;
        resetHoldTicks = 0;
        resetSent = false;
    }

    private static boolean isInteractKeyDown(long window) {
        try {
            Class<?> keys = Class.forName("com.atsuishio.superbwarfare.init.ModKeyMappings");
            Object mapping = keys.getField("INTERACT").get(null);
            if (mapping instanceof net.minecraft.client.KeyMapping keyMapping) {
                return keyMapping.isDown();
            }
        } catch (Throwable ignored) {
            // fall through
        }
        return GLFW.glfwGetKey(window, GLFW.GLFW_KEY_F) == GLFW.GLFW_PRESS;
    }

    /** 载具交互键的本地化名称（默认 F），用于准心提示文案。 */
    public static String interactKeyName() {
        try {
            Class<?> keys = Class.forName("com.atsuishio.superbwarfare.init.ModKeyMappings");
            Object mapping = keys.getField("INTERACT").get(null);
            if (mapping instanceof net.minecraft.client.KeyMapping keyMapping) {
                String name = keyMapping.getTranslatedKeyMessage().getString();
                if (name != null && !name.isBlank()) {
                    return name;
                }
            }
        } catch (Throwable ignored) {
            // fall through
        }
        return "F";
    }

    private static ResourceLocation vehicleIcon(String name) {
        return id("textures/gui/vehicle/" + name + ".png");
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath("espetro", path);
    }
}
