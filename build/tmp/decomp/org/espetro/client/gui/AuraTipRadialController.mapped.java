/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.client.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.espetro.client.aui.AuiRadial;
import org.espetro.client.aui.AuiRadialSlot;
import org.espetro.client.gui.ClientTacticalState;
import org.espetro.network.FortificationCatalogPacket;
import org.espetro.network.NetworkManager;
import org.espetro.network.RadialActionPacket;
import org.espetro.team.CommanderSkillManager;

public final class AuraTipRadialController {
    private static final String ROOT_MENU = "tactical_root";
    private static final String BUILD_MENU = "tactical_build";
    private static final String SKILLS_MENU = "tactical_skills";
    private static final int OPEN_DELAY_TICKS = 6;
    private static final ResourceLocation RALLY = AuraTipRadialController.id("textures/gui/squad/rally_deploy.png");
    private static final ResourceLocation BUILD_ICON = AuraTipRadialController.id("textures/gui/commander_skills/vehicle_supply_station.png");
    private static final ResourceLocation VEHICLE = AuraTipRadialController.id("textures/gui/squad/vehicle_deploy.png");
    private static final ResourceLocation COMMAND_ICON = AuraTipRadialController.id("textures/gui/commander_skills/command.png");
    private static final ResourceLocation UNAVAILABLE_ICON = AuraTipRadialController.id("textures/gui/commander_skills/unavailable.png");
    private static boolean initialized;
    private static boolean keyWasDown;
    private static boolean ownsOverlay;
    private static boolean submenuActive;
    private static boolean consumedUntilRelease;
    private static int heldTicks;
    private static boolean cachedIsCommander;
    private static boolean hasSkillSnapshot;
    private static final Map<String, Integer> cachedCooldowns;
    private static final List<CommanderSkillManager.SkillView> cachedSkills;
    private static final List<FortificationCatalogPacket.Entry> cachedFortifications;
    private static String lastMenuSignature;
    private static volatile boolean skillsDirty;
    private static volatile boolean pendingIsCommander;
    private static volatile boolean pendingHasSnapshot;
    private static final Map<String, Integer> pendingCooldowns;
    private static final List<CommanderSkillManager.SkillView> pendingSkills;
    private static volatile boolean fortificationsDirty;
    private static volatile List<FortificationCatalogPacket.Entry> pendingFortifications;
    private static boolean pendingRebuild;
    private static List<AuiRadialSlot> rootSlots;
    private static List<AuiRadialSlot> buildSlots;
    private static List<AuiRadialSlot> skillsSlots;

    private AuraTipRadialController() {
    }

    public static void initialize() {
        if (initialized) {
            return;
        }
        initialized = true;
        AuraTipRadialController.rebuildSlots();
    }

    private static void openSubmenu(String menu) {
        switch (menu) {
            case "build": {
                AuiRadial.replace(buildSlots, BUILD_MENU);
                break;
            }
            case "skills": {
                AuiRadial.replace(skillsSlots, SKILLS_MENU);
                break;
            }
            default: {
                return;
            }
        }
        submenuActive = true;
    }

    private static void execute(RadialActionPacket.Action action) {
        NetworkManager.sendRadialAction(action);
        consumedUntilRelease = true;
        ownsOverlay = false;
        submenuActive = false;
        AuiRadial.hide();
    }

    private static void buildFort(String fortId) {
        if (fortId != null && !fortId.isEmpty()) {
            NetworkManager.sendBuildFortification(fortId);
        }
        consumedUntilRelease = true;
        ownsOverlay = false;
        submenuActive = false;
        AuiRadial.hide();
    }

    private static void activateSkill(String skillId) {
        if ("vehicle_supply_station".equals(skillId)) {
            return;
        }
        if (skillId == null || skillId.isEmpty()) {
            return;
        }
        NetworkManager.sendCommanderSkillActivate(skillId);
        consumedUntilRelease = true;
        ownsOverlay = false;
        submenuActive = false;
        AuiRadial.hide();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void updateSkills(boolean isCommander, Map<String, Integer> cooldowns, List<CommanderSkillManager.SkillView> skills) {
        pendingIsCommander = isCommander;
        pendingHasSnapshot = true;
        Object object = pendingCooldowns;
        synchronized (object) {
            pendingCooldowns.clear();
            if (cooldowns != null) {
                pendingCooldowns.putAll(cooldowns);
            }
        }
        object = pendingSkills;
        synchronized (object) {
            pendingSkills.clear();
            if (skills != null) {
                pendingSkills.addAll(skills);
            }
        }
        skillsDirty = true;
    }

    public static void updateFortifications(List<FortificationCatalogPacket.Entry> entries) {
        pendingFortifications = entries == null ? List.of() : List.copyOf(entries);
        fortificationsDirty = true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void flushSkillUpdate() {
        if (!skillsDirty) {
            return;
        }
        skillsDirty = false;
        cachedIsCommander = pendingIsCommander;
        hasSkillSnapshot = pendingHasSnapshot;
        Object object = pendingCooldowns;
        synchronized (object) {
            cachedCooldowns.clear();
            cachedCooldowns.putAll(pendingCooldowns);
        }
        object = pendingSkills;
        synchronized (object) {
            cachedSkills.clear();
            cachedSkills.addAll(pendingSkills);
        }
        String newSig = AuraTipRadialController.computeSignature();
        if (!newSig.equals(lastMenuSignature)) {
            if (AuiRadial.isOpen()) {
                pendingRebuild = true;
            } else {
                AuraTipRadialController.rebuildSlots();
            }
        }
    }

    private static void flushFortificationUpdate() {
        if (!fortificationsDirty) {
            return;
        }
        fortificationsDirty = false;
        cachedFortifications.clear();
        cachedFortifications.addAll(pendingFortifications);
        String newSignature = AuraTipRadialController.computeSignature();
        if (!newSignature.equals(lastMenuSignature)) {
            if (AuiRadial.isOpen()) {
                pendingRebuild = true;
            } else {
                AuraTipRadialController.rebuildSlots();
            }
        }
    }

    private static String computeSignature() {
        StringBuilder sb = new StringBuilder();
        sb.append(cachedIsCommander ? (char)'1' : '0');
        sb.append('|');
        for (CommanderSkillManager.SkillView skill : cachedSkills) {
            sb.append(skill.id()).append(':').append(cachedCooldowns.getOrDefault(skill.id(), 0)).append(',');
        }
        sb.append('|');
        for (FortificationCatalogPacket.Entry fort : cachedFortifications) {
            sb.append(fort.id()).append(':').append(fort.icon()).append(':').append(fort.constructionCost()).append(':').append(fort.ammunitionCost()).append(',');
        }
        return sb.toString();
    }

    private static void rebuildSlots() {
        lastMenuSignature = AuraTipRadialController.computeSignature();
        rootSlots = List.copyOf(AuraTipRadialController.rootMenu());
        buildSlots = List.copyOf(AuraTipRadialController.buildMenu());
        skillsSlots = List.copyOf(AuraTipRadialController.skillsMenu());
    }

    public static void tick(Minecraft minecraft, KeyMapping key) {
        if (!initialized || minecraft == null || key == null || minecraft.player == null) {
            AuraTipRadialController.reset(false);
            return;
        }
        AuraTipRadialController.flushSkillUpdate();
        AuraTipRadialController.flushFortificationUpdate();
        boolean down = key.isDown();
        if (!down) {
            if (keyWasDown) {
                AuraTipRadialController.finishSelection(minecraft);
            }
            keyWasDown = false;
            heldTicks = 0;
            consumedUntilRelease = false;
            AuraTipRadialController.tryApplyPendingRebuild();
            return;
        }
        if (!keyWasDown) {
            NetworkManager.requestCommanderSkillSync();
            NetworkManager.requestFortificationCatalog();
        }
        keyWasDown = true;
        if (!ClientTacticalState.canLocalPlayerOpenTacticalRadial(minecraft.player.getName().getString())) {
            AuraTipRadialController.closeOwnedOverlay();
            heldTicks = 0;
            consumedUntilRelease = false;
            AuraTipRadialController.tryApplyPendingRebuild();
            return;
        }
        if (consumedUntilRelease) {
            return;
        }
        if (minecraft.screen != null) {
            AuraTipRadialController.closeOwnedOverlay();
            heldTicks = 0;
            AuraTipRadialController.tryApplyPendingRebuild();
            return;
        }
        if (ownsOverlay || AuiRadial.isOpen()) {
            return;
        }
        if (++heldTicks >= 6) {
            AuiRadial.show(rootSlots, ROOT_MENU);
            ownsOverlay = true;
            submenuActive = false;
        }
    }

    private static void tryApplyPendingRebuild() {
        if (pendingRebuild && !AuiRadial.isOpen()) {
            pendingRebuild = false;
            AuraTipRadialController.rebuildSlots();
        }
    }

    private static void finishSelection(Minecraft minecraft) {
        if (!ownsOverlay) {
            AuraTipRadialController.reset(false);
            return;
        }
        if (submenuActive && AuiRadial.isOpen()) {
            AuiRadial.confirmHovered();
        } else {
            AuraTipRadialController.closeOwnedOverlay();
        }
        AuraTipRadialController.reset(true);
    }

    private static void closeOwnedOverlay() {
        if (AuiRadial.isOpen()) {
            AuiRadial.hide();
        }
        ownsOverlay = false;
        submenuActive = false;
    }

    private static void reset(boolean keepConsumed) {
        heldTicks = 0;
        ownsOverlay = false;
        submenuActive = false;
        if (!keepConsumed) {
            consumedUntilRelease = false;
        }
    }

    private static List<AuiRadialSlot> rootMenu() {
        ArrayList<AuiRadialSlot> slots = new ArrayList<AuiRadialSlot>();
        if (!cachedFortifications.isEmpty()) {
            slots.add(AuiRadialSlot.texture("espetro.build", Component.literal("\u5efa\u9020\u5de5\u4e8b"), BUILD_ICON, "#FFD5B25C", () -> AuraTipRadialController.openSubmenu("build")));
        }
        if (cachedIsCommander) {
            slots.add(AuiRadialSlot.texture("espetro.vehicle", Component.literal("\u8f7d\u5177\u4fe1\u606f"), VEHICLE, "#FFB0A070", () -> AuraTipRadialController.execute(RadialActionPacket.Action.DEPLOY_VEHICLE)));
        }
        if (cachedIsCommander || hasSkillSnapshot && !cachedSkills.isEmpty()) {
            slots.add(AuiRadialSlot.texture("espetro.skills", Component.translatable("radial.espetro.skills"), COMMAND_ICON, "#FFD5A25C", () -> AuraTipRadialController.openSubmenu("skills")));
        }
        return slots;
    }

    private static List<AuiRadialSlot> buildMenu() {
        ArrayList<AuiRadialSlot> slots = new ArrayList<AuiRadialSlot>();
        slots.add(AuiRadialSlot.texture("espetro.rally", Component.translatable("radial.espetro.rally"), RALLY, "#FF7DAE82", () -> AuraTipRadialController.execute(RadialActionPacket.Action.DEPLOY_RALLY)));
        for (FortificationCatalogPacket.Entry fort : cachedFortifications) {
            if (fort == null || fort.id() == null || fort.id().isBlank()) continue;
            ResourceLocation icon = ResourceLocation.tryParse(fort.icon());
            if (icon == null) {
                icon = UNAVAILABLE_ICON;
            }
            StringBuilder label = new StringBuilder(fort.displayName());
            if (fort.constructionCost() > 0 || fort.ammunitionCost() > 0) {
                label.append(" \u00a77(");
                if (fort.constructionCost() > 0) {
                    label.append("\u5efa\u6750 ").append(fort.constructionCost());
                }
                if (fort.constructionCost() > 0 && fort.ammunitionCost() > 0) {
                    label.append(" / ");
                }
                if (fort.ammunitionCost() > 0) {
                    label.append("\u5f39\u836f ").append(fort.ammunitionCost());
                }
                label.append(')');
            }
            ResourceLocation iconRef = icon;
            String fortId = fort.id();
            slots.add(AuiRadialSlot.texture("espetro.fort." + fort.id(), Component.literal(label.toString()), iconRef, "#FFB0A070", () -> AuraTipRadialController.buildFort(fortId)));
        }
        return slots;
    }

    private static List<AuiRadialSlot> skillsMenu() {
        ArrayList<AuiRadialSlot> slots = new ArrayList<AuiRadialSlot>();
        if (!hasSkillSnapshot) {
            slots.add(AuiRadialSlot.texture("espetro.skills_loading", Component.literal("\u00a77\u52a0\u8f7d\u4e2d\u2026"), UNAVAILABLE_ICON, "#FF4A3030", () -> AuraTipRadialController.execute(RadialActionPacket.Action.FOB_STATUS)));
            return slots;
        }
        if (cachedSkills.isEmpty()) {
            slots.add(AuiRadialSlot.texture("espetro.no_skills", Component.literal("\u00a77\u65e0\u53ef\u7528\u6280\u80fd"), UNAVAILABLE_ICON, "#FF4A3030", () -> AuraTipRadialController.execute(RadialActionPacket.Action.FOB_STATUS)));
            return slots;
        }
        for (CommanderSkillManager.SkillView skill : cachedSkills) {
            if ("vehicle_supply_station".equals(skill.id())) continue;
            int cooldown = cachedCooldowns.getOrDefault(skill.id(), 0);
            boolean onCooldown = cooldown > 0;
            String color = onCooldown ? "#FF4A3030" : "#FFD5B25C";
            String label = onCooldown ? skill.displayName() + " \u00a77(" + cooldown + "s)" : skill.displayName();
            ResourceLocation icon = AuraTipRadialController.resolveSkillIcon(skill);
            String skillId = skill.id();
            slots.add(AuiRadialSlot.texture("espetro.skill." + skill.id(), Component.literal(label), icon, color, () -> AuraTipRadialController.activateSkill(skillId)));
        }
        return slots;
    }

    private static ResourceLocation resolveSkillIcon(CommanderSkillManager.SkillView skill) {
        String raw = skill.icon();
        if (raw == null || raw.isBlank()) {
            return COMMAND_ICON;
        }
        ResourceLocation loc = ResourceLocation.tryParse(raw.trim());
        return loc != null ? loc : COMMAND_ICON;
    }

    static List<String> buildMenuSlotIds(List<FortificationCatalogPacket.Entry> forts) {
        ArrayList<String> ids = new ArrayList<String>();
        ids.add("espetro.rally");
        if (forts == null) {
            return ids;
        }
        for (FortificationCatalogPacket.Entry fort : forts) {
            if (fort == null || fort.id() == null || fort.id().isBlank()) continue;
            ids.add("espetro.fort." + fort.id());
        }
        return ids;
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath((String)"espetro", (String)path);
    }

    static {
        cachedCooldowns = new HashMap<String, Integer>();
        cachedSkills = new ArrayList<CommanderSkillManager.SkillView>();
        cachedFortifications = new ArrayList<FortificationCatalogPacket.Entry>();
        lastMenuSignature = "";
        pendingCooldowns = new HashMap<String, Integer>();
        pendingSkills = new ArrayList<CommanderSkillManager.SkillView>();
        pendingFortifications = List.of();
        rootSlots = List.of();
        buildSlots = List.of();
        skillsSlots = List.of();
    }
}

