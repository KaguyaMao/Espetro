/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.dev.resource;

import com.sighs.apricityui.dev.resource.HtmlMetaEditor;
import com.sighs.apricityui.dev.resource.ResourcePath;
import com.sighs.apricityui.init.Document;
import com.sighs.apricityui.init.Element;
import com.sighs.apricityui.ui.DialogWindow;
import com.sighs.apricityui.ui.ToastManager;
import com.sighs.apricityui.ui.Tooltip;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

public final class ResourceMetaDialog {
    private static final double MIN_ZOOM = 0.01;
    private static final double MAX_ZOOM = 10.0;
    private static final List<Choice> FONT_MODE_CHOICES = List.of(new Choice("NOT SET", "", "tooltip.apricityui.meta.font_mode.not_set"), new Choice("MC", "mc", "tooltip.apricityui.meta.font_mode.mc"), new Choice("WEB", "web", "tooltip.apricityui.meta.font_mode.web"), new Choice("WEB SCALED", "web-scaled", "tooltip.apricityui.meta.font_mode.web_scaled"));
    private static final List<Choice> VIEWPORT_CHOICES = List.of(new Choice("NOT SET", "", "tooltip.apricityui.meta.viewport.not_set"), new Choice("GUI", "mode=gui", "tooltip.apricityui.meta.viewport.gui"), new Choice("BROWSER", "mode=browser", "tooltip.apricityui.meta.viewport.browser"), new Choice("WINDOW", "mode=window", "tooltip.apricityui.meta.viewport.window"), new Choice("FIXED / 427 x 249", "mode=fixed,width=427,height=249", "tooltip.apricityui.meta.viewport.fixed_gui"), new Choice("FIXED / 1920 x 1080 / FIT", "mode=fixed,width=1920,height=1080,scale=fit", "tooltip.apricityui.meta.viewport.fixed_fit"));
    private static final List<Choice> MOUSE_EVENT_CHOICES = List.of(new Choice("NOT SET / PASS THROUGH", "", "tooltip.apricityui.meta.mouse_events.pass_through"), new Choice("INTERCEPT", "intercept", "tooltip.apricityui.meta.mouse_events.intercept"));
    private DialogWindow dialog;
    private Element fontModeSelect;
    private Element viewportSelect;
    private Element mouseEventsSelect;
    private Element zoomInput;
    private Element saveButton;
    private Document document;
    private Path target;
    private Runnable afterSave;
    private Consumer<String> templateSave;
    private Consumer<Double> zoomSave;
    private String charset = "";
    private List<String> preservedMeta = List.of();

    public void open(Document document, String resourcePath, Path target, Runnable afterSave) {
        this.close();
        if (document == null || document.body == null) {
            return;
        }
        HtmlMetaEditor.LoadResult loaded = HtmlMetaEditor.load(target);
        if (!loaded.success()) {
            ToastManager.show(loaded.message());
            return;
        }
        this.openEditor(document, "EDIT META / " + ResourcePath.fileName(resourcePath).toUpperCase(Locale.ROOT), target, afterSave, HtmlMetaEditor.parseSettings(loaded.metaMarkup()), null, Double.NaN, null);
    }

    public void open(Document document, String resourcePath, Path target, Runnable afterSave, double currentZoom, Consumer<Double> onZoomSave) {
        this.close();
        if (document == null || document.body == null) {
            return;
        }
        HtmlMetaEditor.LoadResult loaded = HtmlMetaEditor.load(target);
        if (!loaded.success()) {
            ToastManager.show(loaded.message());
            return;
        }
        this.openEditor(document, "EDIT META / " + ResourcePath.fileName(resourcePath).toUpperCase(Locale.ROOT), target, afterSave, HtmlMetaEditor.parseSettings(loaded.metaMarkup()), null, currentZoom, onZoomSave);
    }

    public void openTemplate(Document document, Consumer<String> onSave) {
        this.close();
        if (document == null || document.body == null) {
            return;
        }
        HtmlMetaEditor.MetaSettings browserDefaults = new HtmlMetaEditor.MetaSettings("UTF-8", "web", "mode=browser", "intercept", List.of());
        this.openEditor(document, "NEW HTML META", null, null, browserDefaults, onSave, Double.NaN, null);
    }

    private void openEditor(Document document, String title, Path target, Runnable afterSave, HtmlMetaEditor.MetaSettings settings, Consumer<String> templateSave, double currentZoom, Consumer<Double> zoomSave) {
        this.document = document;
        this.target = target;
        this.afterSave = afterSave;
        this.templateSave = templateSave;
        this.zoomSave = zoomSave;
        this.dialog = DialogWindow.open(document, new DialogWindow.Options(title, 720.0, 520.0, true, "dialog-overlay show", "dialog", "dialog-header", "dialog-title", "dialog-close", "dialog-body", "dialog-title-icon"), this::clearReferences);
        Element root = this.dialog.content();
        root.setAttribute("style", "position:relative;flex:1;min-height:0;display:flex;flex-direction:column;");
        Element fields = this.element("DIV", "resource-meta-fields");
        fields.setAttribute("style", "margin-top:16px;flex:1;min-height:0;overflow:auto;");
        this.charset = settings.charset();
        this.preservedMeta = settings.preservedMeta();
        this.viewportSelect = this.appendSelectField(fields, "VIEWPORT", "tooltip.apricityui.meta.viewport", VIEWPORT_CHOICES, settings.viewport());
        this.fontModeSelect = this.appendSelectField(fields, "FONT MODE", "tooltip.apricityui.meta.font_mode", FONT_MODE_CHOICES, settings.fontMode());
        this.mouseEventsSelect = this.appendSelectField(fields, "MOUSE EVENTS", "tooltip.apricityui.meta.mouse_events", MOUSE_EVENT_CHOICES, settings.mouseEvents());
        if (zoomSave != null) {
            this.zoomInput = this.appendZoomField(fields, currentZoom);
        }
        root.append(fields);
        Element submitRow = this.element("DIV", "dialog-footer");
        this.saveButton = this.element("BUTTON", "dialog-btn dialog-btn-confirm");
        this.saveButton.append(this.text("SPAN", "SAVE", "dialog-btn-label"));
        this.saveButton.addEventListener("click", event -> this.save());
        submitRow.append(this.saveButton);
        this.dialog.window().append(submitRow);
        this.refreshSaveState();
        this.markDirty();
    }

    public void close() {
        DialogWindow openDialog = this.dialog;
        this.dialog = null;
        if (openDialog != null) {
            openDialog.close();
        }
        this.clearReferences();
    }

    private void save() {
        Double zoom = this.zoomValue();
        if (this.zoomInput != null && zoom == null) {
            ToastManager.show("ZOOM must be a number between 0.01 and 10");
            return;
        }
        String markup = HtmlMetaEditor.toMetaMarkup(new HtmlMetaEditor.MetaSettings(this.charset, ResourceMetaDialog.valueOf(this.fontModeSelect), ResourceMetaDialog.valueOf(this.viewportSelect), ResourceMetaDialog.valueOf(this.mouseEventsSelect), this.preservedMeta));
        Consumer<String> templateCallback = this.templateSave;
        if (templateCallback != null) {
            this.close();
            templateCallback.accept(markup);
            return;
        }
        HtmlMetaEditor.EditResult result = HtmlMetaEditor.save(this.target, markup);
        if (!result.success()) {
            ToastManager.show(result.message());
            return;
        }
        Runnable callback = this.afterSave;
        Consumer<Double> zoomCallback = this.zoomSave;
        String name = this.target == null || this.target.getFileName() == null ? "HTML" : this.target.getFileName().toString();
        this.close();
        ToastManager.show("Updated META in " + name);
        if (zoomCallback != null && zoom != null) {
            zoomCallback.accept(zoom);
        }
        if (callback != null) {
            callback.run();
        }
    }

    private void refreshSaveState() {
        if (this.saveButton == null) {
            return;
        }
        if (this.zoomInput != null && this.zoomValue() == null) {
            this.saveButton.setAttribute("disabled", "disabled");
        } else {
            this.saveButton.removeAttribute("disabled");
        }
        this.markDirty();
    }

    private Element appendZoomField(Element parent, double currentZoom) {
        Element field = this.element("DIV", "dialog-field");
        field.setAttribute("style", "margin:0 0 14px;");
        field.append(this.text("LABEL", "ZOOM", "dialog-label"));
        Element input = this.element("INPUT", "dialog-input");
        input.setAttribute("type", "number");
        input.setAttribute("inputmode", "decimal");
        input.setAttribute("min", Double.toString(0.01));
        input.setAttribute("max", Double.toString(10.0));
        input.setAttribute("step", "0.01");
        input.setValue(ResourceMetaDialog.formatZoom(currentZoom));
        input.addEventListener("input", event -> this.refreshSaveState());
        input.addEventListener("change", event -> this.refreshSaveState());
        field.append(input);
        parent.append(field);
        return input;
    }

    private Double zoomValue() {
        if (this.zoomInput == null) {
            return null;
        }
        String raw = this.zoomInput.getValue();
        if (raw == null || raw.isBlank()) {
            return null;
        }
        try {
            double value = Double.parseDouble(raw.trim());
            return Double.isFinite(value) && value >= 0.01 && value <= 10.0 ? Double.valueOf(value) : null;
        }
        catch (NumberFormatException ignored) {
            return null;
        }
    }

    private static String formatZoom(double value) {
        return Double.isFinite(value) && value > 0.0 ? Double.toString(value) : "1.0";
    }

    private Element appendSelectField(Element parent, String label, String tooltipKey, List<Choice> choices, String currentValue) {
        Element field = this.element("DIV", "dialog-field");
        field.setAttribute("style", "margin:0 0 14px;");
        field.append(this.text("LABEL", label, "dialog-label"));
        Element selectWrap = this.element("DIV", "dialog-select-wrap");
        Element select = this.element("SELECT", "dialog-select resource-meta-select");
        select.setAttribute("data-native-arrow", "false");
        select.setAttribute("data-tooltip-key", tooltipKey);
        Tooltip.bindTranslation(select, tooltipKey);
        String selectedValue = ResourceMetaDialog.canonicalChoiceValue(choices, currentValue);
        ArrayList<Choice> available = new ArrayList<Choice>(choices);
        boolean known = available.stream().anyMatch(choice -> choice.value().equals(selectedValue));
        if (!known && selectedValue != null && !selectedValue.isBlank()) {
            available.add(new Choice("CURRENT / " + ResourceMetaDialog.abbreviate(selectedValue).toUpperCase(Locale.ROOT), selectedValue, "tooltip.apricityui.meta.current"));
        }
        for (Choice choice2 : available) {
            Element option = this.text("OPTION", choice2.label(), "resource-meta-option");
            option.setAttribute("value", choice2.value());
            option.setAttribute("data-tooltip-key", choice2.tooltipKey());
            select.append(option);
        }
        select.setValue(selectedValue == null ? "" : selectedValue);
        select.addEventListener("input", event -> this.refreshSaveState());
        select.addEventListener("change", event -> this.refreshSaveState());
        selectWrap.append(select);
        selectWrap.append(this.text("DIV", "\u25be", "dialog-select-arrow"));
        field.append(selectWrap);
        parent.append(field);
        return select;
    }

    private static String canonicalChoiceValue(List<Choice> choices, String value) {
        String safe;
        String string = safe = value == null ? "" : value.trim();
        if (safe.equalsIgnoreCase("mode=screen") && choices.stream().anyMatch(choice -> "mode=window".equals(choice.value()))) {
            return "mode=window";
        }
        return safe;
    }

    private static String valueOf(Element select) {
        return select == null || select.getValue() == null ? "" : select.getValue();
    }

    private static String abbreviate(String value) {
        String safe = value == null ? "" : value;
        return safe.length() <= 54 ? safe : safe.substring(0, 51) + "...";
    }

    private void clearReferences() {
        this.dialog = null;
        this.fontModeSelect = null;
        this.viewportSelect = null;
        this.mouseEventsSelect = null;
        this.zoomInput = null;
        this.saveButton = null;
        this.document = null;
        this.target = null;
        this.afterSave = null;
        this.templateSave = null;
        this.zoomSave = null;
        this.charset = "";
        this.preservedMeta = List.of();
    }

    private Element element(String tag, String className) {
        Element element = Element.init(this.document.createElement(tag));
        element.setAttribute("class", className);
        return element;
    }

    private Element text(String tag, String value, String className) {
        Element element = this.element(tag, className);
        element.setTextContent(value);
        return element;
    }

    private void markDirty() {
        if (this.document != null && this.document.body != null) {
            this.document.markDirty(this.document.body, 7);
        }
    }

    private record Choice(String label, String value, String tooltipKey) {
    }
}

