/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.client.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.espetro.Espetro;
import org.espetro.client.aui.AuiRadial;
import org.espetro.client.aui.AuiRadialSlot;
import org.espetro.client.gui.ClientGameState;
import org.espetro.client.gui.EspetroTipNotifier;
import org.espetro.client.gui.RoleIconResources;
import org.espetro.logistics.resupply.ResupplySourceRef;
import org.espetro.network.ClassSelectPacket;
import org.espetro.network.NetworkManager;
import org.espetro.network.RadioRadialPacket;
import org.espetro.network.RequestResupplyCatalogPacket;

public final class RadioRadialController {
    private static final String ROOT = "radio_root";
    private static final String CLASS_MENU = "radio_classes";
    private static final ResourceLocation ICON_CLASS = ResourceLocation.fromNamespaceAndPath((String)"espetro", (String)"textures/gui/roles/rifleman.png");
    private static final ResourceLocation ICON_RESUPPLY = ResourceLocation.fromNamespaceAndPath((String)"espetro", (String)"textures/gui/squad/ammo_supply.png");
    private static final ResourceLocation ICON_UNAVAILABLE = ResourceLocation.fromNamespaceAndPath((String)"espetro", (String)"textures/gui/commander_skills/unavailable.png");
    private static boolean initialized;
    private static BlockPos lastRadioPos;
    private static UUID pendingVehicleId;
    private static List<RadioRadialPacket.ClassEntry> cachedClasses;

    public static void markNextClassListAsVehicle(UUID vehicleId) {
        pendingVehicleId = vehicleId;
    }

    private RadioRadialController() {
    }

    public static void initialize() {
        if (initialized) {
            return;
        }
        initialized = true;
    }

    public static void requestOpen(BlockPos radioPos) {
        if (!initialized) {
            RadioRadialController.initialize();
        }
        lastRadioPos = radioPos != null ? radioPos.immutable() : BlockPos.ZERO;
        pendingVehicleId = null;
        NetworkManager.sendRadioOpen(lastRadioPos);
    }

    public static void onClassList(BlockPos sourcePos, List<RadioRadialPacket.ClassEntry> classes) {
        boolean vehicleClassMenu;
        List<Object> list = cachedClasses = classes != null ? List.copyOf(classes) : List.of();
        if (sourcePos != null) {
            lastRadioPos = sourcePos.immutable();
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) {
            return;
        }
        boolean bl = vehicleClassMenu = pendingVehicleId != null;
        if (AuiRadial.isOpen()) {
            RadioRadialController.replaceMenu(vehicleClassMenu ? RadioRadialController.buildClassMenu() : RadioRadialController.rootMenu(), CLASS_MENU);
        } else {
            AuiRadial.show(vehicleClassMenu ? RadioRadialController.buildClassMenu() : RadioRadialController.rootMenu(), vehicleClassMenu ? CLASS_MENU : ROOT);
        }
    }

    public static void tick(Minecraft mc) {
    }

    private static void navigate(String target) {
        if ("root".equals(target)) {
            RadioRadialController.replaceRoot();
        } else if ("classes".equals(target)) {
            RadioRadialController.replaceMenu(RadioRadialController.buildClassMenu(), CLASS_MENU);
        } else if (target != null && target.startsWith("variants:")) {
            String classId = target.substring("variants:".length());
            cachedClasses.stream().filter(entry -> entry.classId.equals(classId)).findFirst().ifPresent(entry -> RadioRadialController.replaceMenu(RadioRadialController.buildVariantMenu(entry), "radio_variants"));
        }
    }

    private static void pickClass(String classId, String variantId, boolean enabled, String denial) {
        if (!enabled) {
            String message = denial == null || denial.isBlank() ? "\u5f53\u524d\u65e0\u6cd5\u9009\u62e9\u8be5\u804c\u4e1a\u3002" : denial;
            EspetroTipNotifier.showDenial("\u65e0\u6cd5\u9009\u62e9\u804c\u4e1a", message);
            return;
        }
        if (classId == null || classId.isEmpty()) {
            return;
        }
        String faction = ClientGameState.getPlayerFactionId();
        if (pendingVehicleId != null) {
            NetworkManager.NET.sendToServer((Object)ClassSelectPacket.fromVehicle(faction != null ? faction : "", classId, variantId, pendingVehicleId));
            pendingVehicleId = null;
        } else {
            NetworkManager.sendRadioClassSelect(faction != null ? faction : "", classId, variantId, lastRadioPos);
        }
    }

    private static void doAction(String action) {
        if ("RESUPPLY".equals(action)) {
            NetworkManager.NET.sendToServer((Object)new RequestResupplyCatalogPacket(ResupplySourceRef.radio(lastRadioPos)));
        }
    }

    public static void replaceRoot() {
        RadioRadialController.replaceMenu(RadioRadialController.rootMenu(), ROOT);
    }

    private static void replaceMenu(List<AuiRadialSlot> slots, String pageId) {
        if (AuiRadial.isOpen()) {
            AuiRadial.replace(slots, pageId);
        } else {
            AuiRadial.show(slots, pageId);
        }
    }

    private static List<AuiRadialSlot> rootMenu() {
        ArrayList<AuiRadialSlot> slots = new ArrayList<AuiRadialSlot>();
        slots.add(AuiRadialSlot.texture("espetro.radio.resupply", Component.literal("\u8865\u7ed9\u6b65\u5175"), ICON_RESUPPLY, "#FFFFD54F", () -> RadioRadialController.doAction("RESUPPLY")));
        slots.add(AuiRadialSlot.texture("espetro.radio.change_class", Component.literal("\u66f4\u6362\u804c\u4e1a"), ICON_CLASS, "#FFFFD54F", () -> RadioRadialController.navigate("classes")));
        return slots;
    }

    private static List<AuiRadialSlot> buildClassMenu() {
        ArrayList<AuiRadialSlot> slots = new ArrayList<AuiRadialSlot>();
        slots.add(AuiRadialSlot.glyph("espetro.radio.back", Component.literal("\u21a9"), "\u21a9", "#FF888888", () -> RadioRadialController.navigate("root")));
        if (cachedClasses.isEmpty()) {
            slots.add(AuiRadialSlot.texture("espetro.radio.no_class", Component.literal("\u00a77\u65e0\u53ef\u7528\u804c\u4e1a"), ICON_UNAVAILABLE, "#FF4A3030", () -> RadioRadialController.navigate("root")));
        } else {
            for (RadioRadialPacket.ClassEntry e : cachedClasses) {
                String nameColor;
                String count;
                String displayName;
                ResourceLocation icon = RadioRadialController.resolveClassIcon(e);
                String slotName = "espetro.radio.class." + RadioRadialController.sanitizeSlotId(e.classId);
                String string = displayName = e.name != null && !e.name.isBlank() ? e.name : e.classId;
                Object object = e.showCount ? " " + (e.enabled ? "\u00a7a" : (e.cooldownBlocked ? "\u00a77" : "\u00a7c")) + "[" + e.currentCount + "/" + e.maxCount + "]" : (count = "");
                String string2 = e.enabled ? "\u00a7f" : (nameColor = e.cooldownBlocked ? "\u00a77" : "\u00a7c");
                String highlight = e.enabled ? "#FF8CB4D5" : (e.cooldownBlocked ? "#FF44484D" : "#FF4A3030");
                String classId = e.classId;
                if (e.variants.size() > 1 && e.enabled) {
                    slots.add(AuiRadialSlot.texture(slotName, Component.literal(nameColor + displayName + count), icon, highlight, () -> RadioRadialController.navigate("variants:" + classId)));
                    continue;
                }
                slots.add(AuiRadialSlot.texture(slotName, Component.literal(nameColor + displayName + count), icon, highlight, () -> RadioRadialController.pickClass(classId, e.defaultVariantId, e.enabled, e.denialMessage)));
            }
        }
        return slots;
    }

    private static List<AuiRadialSlot> buildVariantMenu(RadioRadialPacket.ClassEntry entry) {
        ArrayList<AuiRadialSlot> slots = new ArrayList<AuiRadialSlot>();
        slots.add(AuiRadialSlot.glyph("espetro.radio.variant.back", Component.literal("\u21a9"), "\u21a9", "#FF888888", () -> RadioRadialController.navigate("classes")));
        ResourceLocation icon = RadioRadialController.resolveClassIcon(entry);
        for (RadioRadialPacket.VariantEntry variant : entry.variants) {
            String label;
            String string = label = variant.name != null && !variant.name.isBlank() ? variant.name : variant.variantId;
            String count = variant.strictCount ? " " + (variant.enabled ? "\u00a7a" : "\u00a7c") + "[" + variant.currentCount + "/" + variant.maxCount + "]" : " \u00a7a" + variant.currentCount + "\u4eba";
            String nameColor = variant.enabled ? "\u00a7f" : "\u00a7c";
            String classId = entry.classId;
            slots.add(AuiRadialSlot.texture("espetro.radio.variant." + RadioRadialController.sanitizeSlotId(entry.classId) + "." + RadioRadialController.sanitizeSlotId(variant.variantId), Component.literal(nameColor + label + count), icon, variant.enabled ? "#FF8CB4D5" : "#FF4A3030", () -> RadioRadialController.pickClass(classId, variant.variantId, variant.enabled, variant.denialMessage)));
        }
        return slots;
    }

    private static ResourceLocation resolveClassIcon(RadioRadialPacket.ClassEntry e) {
        try {
            ResourceLocation loc = RoleIconResources.resolve(e != null ? e.iconImage : null, e != null ? e.icon : null);
            if (loc != null) {
                return loc;
            }
            if (e != null && (loc = RoleIconResources.resolveForScoreboard(e.iconImage, e.icon, e.classId)) != null) {
                return loc;
            }
        }
        catch (Throwable t) {
            Espetro.LOGGER.debug("RadioRadial icon resolve failed: {}", (Object)t.toString());
        }
        return ICON_CLASS;
    }

    static List<String> rootSlotIds() {
        return List.of("espetro.radio.resupply", "espetro.radio.change_class");
    }

    static List<String> classSlotIds(List<RadioRadialPacket.ClassEntry> classes) {
        ArrayList<String> ids = new ArrayList<String>();
        ids.add("espetro.radio.back");
        if (classes == null || classes.isEmpty()) {
            ids.add("espetro.radio.no_class");
            return ids;
        }
        for (RadioRadialPacket.ClassEntry entry : classes) {
            ids.add("espetro.radio.class." + RadioRadialController.sanitizeSlotId(entry.classId));
        }
        return ids;
    }

    static String sanitizeSlotId(String classId) {
        if (classId == null || classId.isBlank()) {
            return "unknown";
        }
        return classId.replaceAll("[^a-zA-Z0-9_.-]", "_");
    }

    static {
        lastRadioPos = BlockPos.ZERO;
        cachedClasses = List.of();
    }
}

