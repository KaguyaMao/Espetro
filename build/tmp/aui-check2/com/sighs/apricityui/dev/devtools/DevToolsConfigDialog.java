/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.dev.devtools;

import com.sighs.apricityui.dev.devtools.DevToolsDom;
import com.sighs.apricityui.dev.devtools.DevToolsTranslations;
import com.sighs.apricityui.init.Document;
import com.sighs.apricityui.init.Element;
import com.sighs.apricityui.spi.AuiServices;
import com.sighs.apricityui.ui.DialogWindow;
import com.sighs.apricityui.ui.ToastManager;
import java.util.LinkedHashMap;
import java.util.Map;

final class DevToolsConfigDialog {
    private final Map<String, Element> booleanInputs = new LinkedHashMap<String, Element>();
    private final Map<String, Element> numberInputs = new LinkedHashMap<String, Element>();
    private DialogWindow dialog;
    private Document document;

    DevToolsConfigDialog() {
    }

    void open(Document document) {
        this.close();
        if (document == null || document.body == null) {
            return;
        }
        this.document = document;
        this.dialog = DialogWindow.open(document, new DialogWindow.Options(DevToolsTranslations.translate("devtools.apricityui.settings.title", new Object[0]), 720.0, 620.0, true, "dialog-overlay show", "dialog settings-dialog", "dialog-header", "dialog-title", "dialog-close", "dialog-body", "settings-dialog-title-icon"), this::clearReferences);
        Element root = this.dialog.content();
        root.setAttribute("style", "position:relative;flex:1;min-height:0;display:flex;flex-direction:column;");
        Element scroll = DevToolsDom.element(document, "DIV", "settings-scroll");
        scroll.append(DevToolsDom.text(document, "DIV", "settings-note", DevToolsTranslations.translate("devtools.apricityui.settings.description", new Object[0])));
        Element debug = this.appendSection(scroll, "devtools.apricityui.settings.section.debug");
        Element debugGrid = this.appendGrid(debug);
        this.appendBooleanField(debugGrid, "debugAutoReload", "devtools.apricityui.settings.debug_auto_reload", "devtools.apricityui.settings.debug_auto_reload.description", AuiServices.config().debugAutoReload());
        this.appendBooleanField(debugGrid, "aiAutoScreenshot", "devtools.apricityui.settings.ai_auto_screenshot", "devtools.apricityui.settings.ai_auto_screenshot.description", AuiServices.config().aiAutoScreenshot());
        this.appendBooleanField(debugGrid, "frameTimingHud", "devtools.apricityui.settings.frame_timing_hud", "devtools.apricityui.settings.frame_timing_hud.description", AuiServices.config().frameTimingHud());
        this.appendBooleanField(debugGrid, "remoteDebug", "devtools.apricityui.settings.remote_debug", "devtools.apricityui.settings.remote_debug.description", AuiServices.config().remoteDebug());
        this.appendBooleanField(debugGrid, "resourceManagerWorldWindow", "devtools.apricityui.settings.resource_manager_world_window", "devtools.apricityui.settings.resource_manager_world_window.description", AuiServices.config().resourceManagerWorldWindow());
        Element input = this.appendSection(scroll, "devtools.apricityui.settings.section.input");
        Element inputGrid = this.appendGrid(input);
        this.appendBooleanField(inputGrid, "viewportZoomPassThrough", "devtools.apricityui.settings.viewport_zoom_pass_through", "devtools.apricityui.settings.viewport_zoom_pass_through.description", AuiServices.config().viewportZoomPassThrough());
        Element worldWindow = this.appendSection(scroll, "devtools.apricityui.settings.section.world_window");
        Element worldGrid = this.appendGrid(worldWindow);
        this.appendNumberField(worldGrid, "worldWindowDepthOffsetScale", "devtools.apricityui.settings.world_window_depth_offset_scale", "devtools.apricityui.settings.world_window_depth_offset_scale.description", Double.toString(AuiServices.config().worldWindowDepthOffsetScale()), "0", "1", "0.01");
        this.appendNumberField(worldGrid, "worldWindowMaxDisplayDistance", "devtools.apricityui.settings.world_window_max_display_distance", "devtools.apricityui.settings.world_window_max_display_distance.description", Integer.toString(AuiServices.config().worldWindowMaxDisplayDistance()), "0", Integer.toString(Integer.MAX_VALUE), "1");
        this.appendBooleanField(worldGrid, "worldWindowLodEnabled", "devtools.apricityui.settings.world_window_lod_enabled", "devtools.apricityui.settings.world_window_lod_enabled.description", AuiServices.config().worldWindowLodEnabled());
        this.appendNumberField(worldGrid, "worldWindowFullDetailDistance", "devtools.apricityui.settings.world_window_full_detail_distance", "devtools.apricityui.settings.world_window_full_detail_distance.description", Integer.toString(AuiServices.config().worldWindowFullDetailDistance()), "0", Integer.toString(Integer.MAX_VALUE), "1");
        this.appendNumberField(worldGrid, "worldWindowReducedDetailDistance", "devtools.apricityui.settings.world_window_reduced_detail_distance", "devtools.apricityui.settings.world_window_reduced_detail_distance.description", Integer.toString(AuiServices.config().worldWindowReducedDetailDistance()), "0", Integer.toString(Integer.MAX_VALUE), "1");
        root.append(scroll);
        Element footer = DevToolsDom.element(document, "DIV", "dialog-footer");
        Element cancel = this.button(document, "devtools.apricityui.cancel", "dialog-btn dialog-btn-cancel");
        cancel.addEventListener("click", event -> this.close());
        Element save = this.button(document, "devtools.apricityui.settings.save", "dialog-btn dialog-btn-confirm");
        save.addEventListener("click", event -> this.save());
        footer.append(cancel);
        footer.append(save);
        this.dialog.window().append(footer);
        DevToolsDom.markDirty(document);
    }

    void close() {
        DialogWindow current = this.dialog;
        this.dialog = null;
        if (current != null && current.isOpen()) {
            current.close();
        }
        this.clearReferences();
    }

    private Element appendSection(Element parent, String titleKey) {
        Element section = DevToolsDom.element(this.document, "SECTION", "settings-section");
        section.append(DevToolsDom.text(this.document, "H2", "settings-section-title", DevToolsTranslations.translate(titleKey, new Object[0])));
        parent.append(section);
        return section;
    }

    private Element appendGrid(Element section) {
        Element grid = DevToolsDom.element(this.document, "DIV", "settings-section-grid");
        section.append(grid);
        return grid;
    }

    private void appendBooleanField(Element parent, String key, String labelKey, String descriptionKey, boolean currentValue) {
        Element field = DevToolsDom.element(this.document, "LABEL", "settings-checkbox-field");
        Element input = DevToolsDom.element(this.document, "INPUT", "settings-checkbox-input");
        input.setAttribute("id", "auiSetting-" + key);
        input.setAttribute("type", "checkbox");
        input.setChecked(currentValue);
        Element checkmark = DevToolsDom.element(this.document, "SPAN", "settings-checkbox-mark");
        Element copy = DevToolsDom.element(this.document, "SPAN", "settings-checkbox-copy");
        copy.append(DevToolsDom.text(this.document, "SPAN", "settings-field-label", DevToolsTranslations.translate(labelKey, new Object[0])));
        copy.append(DevToolsDom.text(this.document, "SPAN", "settings-field-description", DevToolsTranslations.translate(descriptionKey, new Object[0])));
        field.append(input);
        field.append(checkmark);
        field.append(copy);
        input.addEventListener("change", event -> DevToolsDom.markDirty(this.document));
        field.addEventListener("click", event -> {
            if (event.target != input) {
                input.setChecked(!input.isChecked());
                event.preventDefault();
                DevToolsDom.markDirty(this.document);
            }
        });
        parent.append(field);
        this.booleanInputs.put(key, input);
    }

    private void appendNumberField(Element parent, String key, String labelKey, String descriptionKey, String currentValue, String min, String max, String step) {
        Element field = DevToolsDom.element(this.document, "LABEL", "settings-number-field");
        Element copy = DevToolsDom.element(this.document, "SPAN", "settings-number-copy");
        copy.append(DevToolsDom.text(this.document, "SPAN", "settings-field-label", DevToolsTranslations.translate(labelKey, new Object[0])));
        copy.append(DevToolsDom.text(this.document, "SPAN", "settings-field-description", DevToolsTranslations.translate(descriptionKey, new Object[0])));
        Element input = DevToolsDom.element(this.document, "INPUT", "settings-number-input");
        input.setAttribute("id", "auiSetting-" + key);
        input.setAttribute("type", "number");
        input.setAttribute("min", min);
        input.setAttribute("max", max);
        input.setAttribute("step", step);
        input.setValue(currentValue);
        input.addEventListener("input", event -> DevToolsDom.markDirty(this.document));
        field.append(copy);
        field.append(input);
        parent.append(field);
        this.numberInputs.put(key, input);
    }

    private Element button(Document document, String labelKey, String className) {
        Element button = DevToolsDom.element(document, "BUTTON", className);
        button.append(DevToolsDom.text(document, "SPAN", "dialog-btn-label", DevToolsTranslations.translate(labelKey, new Object[0])));
        return button;
    }

    private void save() {
        Double depthOffsetScale = this.readDouble("worldWindowDepthOffsetScale");
        Integer maxDisplayDistance = this.readInteger("worldWindowMaxDisplayDistance");
        Integer fullDetailDistance = this.readInteger("worldWindowFullDetailDistance");
        Integer reducedDetailDistance = this.readInteger("worldWindowReducedDetailDistance");
        if (depthOffsetScale == null || maxDisplayDistance == null || fullDetailDistance == null || reducedDetailDistance == null) {
            ToastManager.show(DevToolsTranslations.translate("devtools.apricityui.settings.invalid_number", new Object[0]));
            return;
        }
        if (depthOffsetScale < 0.0 || depthOffsetScale > 1.0) {
            ToastManager.show(DevToolsTranslations.translate("devtools.apricityui.settings.depth_range", new Object[0]));
            return;
        }
        if (maxDisplayDistance < 0 || fullDetailDistance < 0 || reducedDetailDistance < 0) {
            ToastManager.show(DevToolsTranslations.translate("devtools.apricityui.settings.distance_range", new Object[0]));
            return;
        }
        if (reducedDetailDistance < fullDetailDistance) {
            ToastManager.show(DevToolsTranslations.translate("devtools.apricityui.settings.distance_order", new Object[0]));
            return;
        }
        try {
            AuiServices.config().setDebugAutoReload(this.isChecked("debugAutoReload"));
            AuiServices.config().setAiAutoScreenshot(this.isChecked("aiAutoScreenshot"));
            AuiServices.config().setFrameTimingHud(this.isChecked("frameTimingHud"));
            AuiServices.config().setRemoteDebug(this.isChecked("remoteDebug"));
            AuiServices.config().setResourceManagerWorldWindow(this.isChecked("resourceManagerWorldWindow"));
            AuiServices.config().setViewportZoomPassThrough(this.isChecked("viewportZoomPassThrough"));
            AuiServices.config().setWorldWindowDepthOffsetScale(depthOffsetScale);
            AuiServices.config().setWorldWindowMaxDisplayDistance(maxDisplayDistance);
            AuiServices.config().setWorldWindowLodEnabled(this.isChecked("worldWindowLodEnabled"));
            AuiServices.config().setWorldWindowFullDetailDistance(fullDetailDistance);
            AuiServices.config().setWorldWindowReducedDetailDistance(reducedDetailDistance);
            AuiServices.config().save();
        }
        catch (RuntimeException exception) {
            ToastManager.show(DevToolsTranslations.translate("devtools.apricityui.settings.save_failed", new Object[0]));
            return;
        }
        AuiServices.config().markClientReloadPending();
        this.close();
        ToastManager.show(DevToolsTranslations.translate("devtools.apricityui.settings.saved", new Object[0]));
    }

    private boolean isChecked(String key) {
        Element input = this.booleanInputs.get(key);
        return input != null && input.isChecked();
    }

    private Double readDouble(String key) {
        String raw = DevToolsConfigDialog.valueOf(this.numberInputs.get(key));
        if (raw.isBlank()) {
            return null;
        }
        try {
            double value = Double.parseDouble(raw);
            return Double.isFinite(value) ? Double.valueOf(value) : null;
        }
        catch (NumberFormatException ignored) {
            return null;
        }
    }

    private Integer readInteger(String key) {
        String raw = DevToolsConfigDialog.valueOf(this.numberInputs.get(key));
        if (raw.isBlank()) {
            return null;
        }
        try {
            return Integer.valueOf(raw);
        }
        catch (NumberFormatException ignored) {
            return null;
        }
    }

    private static String valueOf(Element input) {
        return input == null || input.getValue() == null ? "" : input.getValue().trim();
    }

    private void clearReferences() {
        this.dialog = null;
        this.document = null;
        this.booleanInputs.clear();
        this.numberInputs.clear();
    }
}

