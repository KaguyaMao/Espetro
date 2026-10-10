/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  net.minecraft.client.Minecraft
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.commands.Commands
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.client.event.RegisterClientCommandsEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 */
package com.sighs.apricityui.client;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.sighs.apricityui.event.Event;
import com.sighs.apricityui.init.Document;
import com.sighs.apricityui.init.Element;
import com.sighs.apricityui.world.WorldWindow;
import com.sighs.apricityui.world.WorldWindowDisplayPrecision;
import java.util.Locale;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="apricityui", value={Dist.CLIENT})
public final class ApricityUIClientCommands {
    private static final String TEST_DOCUMENT_PATH = "tests/world-window-command.html";
    private static final int TEST_MAX_DISTANCE = 32;
    private static final double TEST_FORWARD_DISTANCE = 2.0;
    private static final float DEFAULT_NEAR_DEPTH_STEP = 3.5E-4f;
    private static final float DEFAULT_FAR_DEPTH_STEP = 0.003f;
    private static final float DEFAULT_DEPTH_NEAR_DISTANCE = 2.0f;
    private static final float DEFAULT_FOLLOW_FACTOR = 0.3f;
    private static WorldWindow testWindow;
    private static Document boundDocument;
    private static final Consumer<Event> DEBUG_EVENT_LISTENER;
    private static final Consumer<Event> DEBUG_LIFECYCLE_LISTENER;

    private ApricityUIClientCommands() {
    }

    @SubscribeEvent
    public static void register(RegisterClientCommandsEvent event) {
        CommandDispatcher dispatcher = event.getDispatcher();
        dispatcher.register((LiteralArgumentBuilder)Commands.m_82127_((String)"aui").then(Commands.m_82127_((String)"worldwindow").executes(context -> ApricityUIClientCommands.spawnTestWindow((CommandSourceStack)context.getSource()))));
    }

    private static int spawnTestWindow(CommandSourceStack source) {
        Minecraft minecraft = Minecraft.m_91087_();
        if (minecraft == null || minecraft.f_91073_ == null || minecraft.f_91074_ == null) {
            source.m_81352_((Component)Component.m_237113_((String)"AUI worldwindow requires an active client world."));
            return 0;
        }
        if (testWindow != null) {
            WorldWindow.removeWindow(testWindow);
            testWindow = null;
            boundDocument = null;
        }
        Vec3 player = minecraft.f_91074_.m_20182_();
        Vec3 position = player.m_82520_(0.0, (double)minecraft.f_91074_.m_20192_(), 0.0).m_82549_(minecraft.f_91074_.m_20154_().m_82490_(2.0));
        testWindow = new WorldWindow(TEST_DOCUMENT_PATH, position, 32);
        WorldWindow.addWindow(testWindow);
        ApricityUIClientCommands.bindDebugger(ApricityUIClientCommands.testWindow.document);
        source.m_288197_(() -> Component.m_237113_((String)"Spawned AUI test worldwindow."), false);
        return 1;
    }

    private static void bindDebugger(Document document) {
        if (document == null || document.body == null) {
            return;
        }
        if (boundDocument != document) {
            document.body.addEventListener("input", DEBUG_EVENT_LISTENER);
            document.body.addEventListener("change", DEBUG_EVENT_LISTENER);
            document.body.addEventListener("click", DEBUG_EVENT_LISTENER);
            document.addEventListener("DOMContentLoaded", DEBUG_LIFECYCLE_LISTENER);
            boundDocument = document;
        }
        ApricityUIClientCommands.syncControls();
    }

    private static void handleDebugEvent(Event event) {
        Element target;
        if (testWindow == null || event == null) {
            return;
        }
        if ("DOMContentLoaded".equals(event.type)) {
            ApricityUIClientCommands.syncControls();
            return;
        }
        Object object = event.target;
        if (object instanceof Element) {
            Element element = (Element)object;
            v0 = ApricityUIClientCommands.eventControl(element);
        } else {
            v0 = target = null;
        }
        if (target != null && "click".equals(event.type) && "reset-settings".equals(target.getAttribute("id"))) {
            ApricityUIClientCommands.resetSettings();
            return;
        }
        if (target == null || !"input".equals(event.type) && !"change".equals(event.type)) {
            return;
        }
        String id = target.getAttribute("id");
        if (id == null || id.isBlank()) {
            return;
        }
        switch (id) {
            case "max-display-distance": {
                Element mode = ApricityUIClientCommands.control("display-distance-mode");
                if (mode != null && !"instance".equalsIgnoreCase(ApricityUIClientCommands.value(mode, "global"))) {
                    mode.setValue("instance");
                }
                ApricityUIClientCommands.applyDisplayDistance();
                break;
            }
            case "display-distance-mode": {
                ApricityUIClientCommands.applyDisplayDistance();
                break;
            }
            case "max-distance": {
                testWindow.setMaxDistance(ApricityUIClientCommands.readInt(target, testWindow.getMaxDistance()));
                break;
            }
            case "depth-test": {
                testWindow.setDepthTest(target.isChecked());
                break;
            }
            case "lod-policy": 
            case "lod-custom": 
            case "lod-full-distance": 
            case "lod-reduced-distance": {
                ApricityUIClientCommands.applyLodSettings();
                break;
            }
            case "scale-mode": 
            case "world-scale": {
                ApricityUIClientCommands.applyScaleSettings();
                break;
            }
            case "near-depth-step": 
            case "far-depth-step": 
            case "depth-near-distance": 
            case "depth-far-distance": {
                ApricityUIClientCommands.applyDepthSettings();
                break;
            }
            case "follow-enabled": {
                testWindow.setFollow(target.isChecked());
                break;
            }
            case "facing-enabled": {
                testWindow.setFacing(target.isChecked());
                break;
            }
            case "follow-factor": {
                testWindow.setFollowFactor(ApricityUIClientCommands.readFloat(target, testWindow.getFollowFactor()));
                break;
            }
            default: {
                return;
            }
        }
        ApricityUIClientCommands.syncControls();
    }

    private static void applyDisplayDistance() {
        String mode = ApricityUIClientCommands.value(ApricityUIClientCommands.control("display-distance-mode"), "global").toLowerCase(Locale.ROOT);
        if ("global".equals(mode)) {
            testWindow.clearMaxDisplayDistanceOverride();
        } else if ("unlimited".equals(mode)) {
            testWindow.setMaxDisplayDistance(Integer.MAX_VALUE);
        } else {
            Element distance = ApricityUIClientCommands.control("max-display-distance");
            testWindow.setMaxDisplayDistance(ApricityUIClientCommands.readInt(distance, testWindow.getMaxDisplayDistance()));
        }
    }

    private static void applyLodSettings() {
        Element policyControl = ApricityUIClientCommands.control("lod-policy");
        String policy = ApricityUIClientCommands.value(policyControl, "auto").toLowerCase(Locale.ROOT);
        if (!"auto".equals(policy)) {
            testWindow.setDisplayPrecision(policy);
            return;
        }
        Element customControl = ApricityUIClientCommands.control("lod-custom");
        if (customControl != null && customControl.isChecked()) {
            testWindow.setDisplayPrecisionDistances(ApricityUIClientCommands.readInt(ApricityUIClientCommands.control("lod-full-distance"), testWindow.getFullDetailDistance()), ApricityUIClientCommands.readInt(ApricityUIClientCommands.control("lod-reduced-distance"), testWindow.getReducedDetailDistance()));
        } else {
            testWindow.setDisplayPrecision(WorldWindowDisplayPrecision.AUTO);
        }
    }

    private static void applyScaleSettings() {
        String mode = ApricityUIClientCommands.value(ApricityUIClientCommands.control("scale-mode"), "auto").toLowerCase(Locale.ROOT);
        if ("manual".equals(mode)) {
            testWindow.setScale(ApricityUIClientCommands.readFloat(ApricityUIClientCommands.control("world-scale"), testWindow.getScale()));
        } else {
            testWindow.clearScaleOverride();
        }
    }

    private static void applyDepthSettings() {
        testWindow.setDynamicDepthStep(ApricityUIClientCommands.readFloat(ApricityUIClientCommands.control("near-depth-step"), testWindow.getNearDepthStep()), ApricityUIClientCommands.readFloat(ApricityUIClientCommands.control("far-depth-step"), testWindow.getFarDepthStep()), ApricityUIClientCommands.readFloat(ApricityUIClientCommands.control("depth-near-distance"), testWindow.getDepthNearDistance()), ApricityUIClientCommands.readFloat(ApricityUIClientCommands.control("depth-far-distance"), testWindow.getDepthFarDistance()));
    }

    private static void resetSettings() {
        if (testWindow == null) {
            return;
        }
        testWindow.setMaxDistance(32);
        testWindow.clearMaxDisplayDistanceOverride();
        testWindow.setDepthTest(true);
        testWindow.setFollow(false);
        testWindow.setFollowFactor(0.3f);
        testWindow.setFacing(false);
        testWindow.setDisplayPrecision(WorldWindowDisplayPrecision.AUTO);
        testWindow.clearScaleOverride();
        testWindow.setDynamicDepthStep(3.5E-4f, 0.003f, 2.0f, 32.0f);
        ApricityUIClientCommands.syncControls();
    }

    private static void syncControls() {
        if (testWindow == null || boundDocument == null || ApricityUIClientCommands.boundDocument.body == null) {
            return;
        }
        int effectiveDisplayDistance = testWindow.getMaxDisplayDistance();
        ApricityUIClientCommands.setValue("max-display-distance", Integer.toString(Math.min(256, Math.max(4, effectiveDisplayDistance))));
        String displayMode = !testWindow.hasMaxDisplayDistanceOverride() ? "global" : (effectiveDisplayDistance == Integer.MAX_VALUE ? "unlimited" : "instance");
        ApricityUIClientCommands.setValue("display-distance-mode", displayMode);
        ApricityUIClientCommands.setDisabled("max-display-distance", false);
        ApricityUIClientCommands.setValue("max-distance", Integer.toString(testWindow.getMaxDistance()));
        ApricityUIClientCommands.setChecked("depth-test", testWindow.isDepthTestEnabled());
        ApricityUIClientCommands.setText("display-distance-value", (String)(effectiveDisplayDistance == Integer.MAX_VALUE ? "UNLIMITED" : effectiveDisplayDistance + " BLOCKS"));
        ApricityUIClientCommands.setText("max-distance-value", testWindow.getMaxDistance() + " BLOCKS");
        WorldWindowDisplayPrecision policy = testWindow.getDisplayPrecision();
        ApricityUIClientCommands.setValue("lod-policy", policy.toString());
        boolean customLod = policy == WorldWindowDisplayPrecision.AUTO && testWindow.hasDisplayPrecisionOverride();
        ApricityUIClientCommands.setChecked("lod-custom", customLod);
        ApricityUIClientCommands.setDisabled("lod-custom", policy != WorldWindowDisplayPrecision.AUTO);
        ApricityUIClientCommands.setDisabled("lod-full-distance", !customLod);
        ApricityUIClientCommands.setDisabled("lod-reduced-distance", !customLod);
        ApricityUIClientCommands.setValue("lod-full-distance", Integer.toString(testWindow.getFullDetailDistance()));
        ApricityUIClientCommands.setValue("lod-reduced-distance", Integer.toString(testWindow.getReducedDetailDistance()));
        ApricityUIClientCommands.setText("lod-full-value", testWindow.getFullDetailDistance() + " BLOCKS");
        ApricityUIClientCommands.setText("lod-reduced-value", testWindow.getReducedDetailDistance() + " BLOCKS");
        ApricityUIClientCommands.setValue("scale-mode", testWindow.hasScaleOverride() ? "manual" : "auto");
        ApricityUIClientCommands.setDisabled("world-scale", !testWindow.hasScaleOverride());
        ApricityUIClientCommands.setValue("world-scale", ApricityUIClientCommands.formatDecimal(testWindow.getScale(), 5));
        ApricityUIClientCommands.setText("world-scale-value", ApricityUIClientCommands.formatDecimal(testWindow.getScale(), 4));
        ApricityUIClientCommands.setValue("near-depth-step", ApricityUIClientCommands.formatDecimal(testWindow.getNearDepthStep(), 5));
        ApricityUIClientCommands.setValue("far-depth-step", ApricityUIClientCommands.formatDecimal(testWindow.getFarDepthStep(), 5));
        ApricityUIClientCommands.setValue("depth-near-distance", ApricityUIClientCommands.formatDecimal(testWindow.getDepthNearDistance(), 2));
        ApricityUIClientCommands.setValue("depth-far-distance", ApricityUIClientCommands.formatDecimal(testWindow.getDepthFarDistance(), 2));
        ApricityUIClientCommands.setText("near-depth-value", ApricityUIClientCommands.formatDecimal(testWindow.getNearDepthStep(), 5));
        ApricityUIClientCommands.setText("far-depth-value", ApricityUIClientCommands.formatDecimal(testWindow.getFarDepthStep(), 5));
        ApricityUIClientCommands.setText("depth-near-value", ApricityUIClientCommands.formatDecimal(testWindow.getDepthNearDistance(), 1) + " BLOCKS");
        ApricityUIClientCommands.setText("depth-far-value", ApricityUIClientCommands.formatDecimal(testWindow.getDepthFarDistance(), 1) + " BLOCKS");
        ApricityUIClientCommands.setChecked("follow-enabled", testWindow.isFollowEnabled());
        ApricityUIClientCommands.setValue("follow-factor", ApricityUIClientCommands.formatDecimal(testWindow.getFollowFactor(), 2));
        ApricityUIClientCommands.setDisabled("follow-factor", !testWindow.isFollowEnabled());
        ApricityUIClientCommands.setText("follow-factor-value", ApricityUIClientCommands.formatDecimal(testWindow.getFollowFactor(), 2));
        ApricityUIClientCommands.setChecked("facing-enabled", testWindow.isFacingEnabled());
        ApricityUIClientCommands.setText("window-status", "LIVE / RAY " + testWindow.getMaxDistance() + " BLOCKS");
        ApricityUIClientCommands.setText("display-status", (String)(effectiveDisplayDistance == Integer.MAX_VALUE ? "DISPLAY UNLIMITED" : "DISPLAY " + effectiveDisplayDistance + " BLOCKS"));
        ApricityUIClientCommands.setText("lod-status", "EFFECTIVE LOD: " + testWindow.getEffectiveDisplayPrecision().toString().toUpperCase(Locale.ROOT));
        ApricityUIClientCommands.setText("scale-status", "SCALE " + ApricityUIClientCommands.formatDecimal(testWindow.getScale(), 4) + (testWindow.hasScaleOverride() ? " / MANUAL" : " / AUTO"));
    }

    private static Element control(String id) {
        return boundDocument == null ? null : boundDocument.getElementById(id);
    }

    private static Element eventControl(Element target) {
        Element current = target;
        while (current != null) {
            if (current.getAttribute("id") != null && !current.getAttribute("id").isBlank()) {
                return current;
            }
            current = current.parentElement;
        }
        return target;
    }

    private static void setValue(String id, String value) {
        Element element = ApricityUIClientCommands.control(id);
        if (element != null) {
            element.setValue(value);
        }
    }

    private static void setChecked(String id, boolean checked) {
        Element element = ApricityUIClientCommands.control(id);
        if (element != null) {
            element.setChecked(checked);
        }
    }

    private static void setDisabled(String id, boolean disabled) {
        Element element = ApricityUIClientCommands.control(id);
        if (element != null) {
            element.setDisabled(disabled);
        }
    }

    private static void setText(String id, String value) {
        Element element = ApricityUIClientCommands.control(id);
        if (element != null) {
            element.setTextContent(value);
        }
    }

    private static String value(Element element, String fallback) {
        if (element == null) {
            return fallback;
        }
        String value = element.getValue();
        return value == null || value.isBlank() ? fallback : value.trim();
    }

    private static int readInt(Element element, int fallback) {
        try {
            return Integer.parseInt(ApricityUIClientCommands.value(element, Integer.toString(fallback)));
        }
        catch (NumberFormatException ignored) {
            return fallback;
        }
    }

    private static float readFloat(Element element, float fallback) {
        try {
            float parsed = Float.parseFloat(ApricityUIClientCommands.value(element, Float.toString(fallback)));
            return Float.isFinite(parsed) ? parsed : fallback;
        }
        catch (NumberFormatException ignored) {
            return fallback;
        }
    }

    private static String formatDecimal(float value, int precision) {
        return String.format(Locale.ROOT, "%." + precision + "f", Float.valueOf(value));
    }

    static {
        DEBUG_EVENT_LISTENER = ApricityUIClientCommands::handleDebugEvent;
        DEBUG_LIFECYCLE_LISTENER = ignored -> ApricityUIClientCommands.syncControls();
    }
}

