/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.ui;

import com.sighs.apricityui.dev.resource.ResourceFileWriter;
import com.sighs.apricityui.dev.resource.ResourcePath;
import com.sighs.apricityui.event.Event;
import com.sighs.apricityui.init.Document;
import com.sighs.apricityui.init.Element;
import com.sighs.apricityui.loader.ClientLoader;
import com.sighs.apricityui.loader.Loader;
import com.sighs.apricityui.ui.Tooltip;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public final class FilePicker {
    private static final String PATH = "devtools/file-picker.html";
    private static final String DEFAULT_TITLE_KEY = "file_picker.apricityui.title";
    private static FilePicker active;
    private final Document document;
    private final Options options;
    private final CompletableFuture<Optional<Selection>> result = new CompletableFuture();
    private List<Loader.StaticResourceEntry> entries;
    private String currentPath = "";
    private Loader.StaticResourceEntry selected;
    private boolean creatingHtml;
    private Element root;
    private final Set<String> expandedPaths = new LinkedHashSet<String>();

    private FilePicker(Document document, Options options, List<Loader.StaticResourceEntry> entries) {
        this.document = document;
        this.options = options == null ? Options.any(null, true) : options;
        this.entries = this.filter(entries);
    }

    public static synchronized CompletableFuture<Optional<Selection>> pick(Options options) {
        Document document = Document.create(PATH);
        if (document == null || document.body == null) {
            return CompletableFuture.completedFuture(Optional.empty());
        }
        document.setReloadPersistent(true);
        return FilePicker.open(document, options, ClientLoader.listFinalStaticResources());
    }

    public static synchronized CompletableFuture<Optional<Selection>> pickIn(Document document, Options options, List<Loader.StaticResourceEntry> entries) {
        return FilePicker.open(document, options, entries);
    }

    public static synchronized boolean isOpen() {
        return active != null && FilePicker.active.root != null && FilePicker.active.root.isConnected();
    }

    public static synchronized void closeActive() {
        if (active != null) {
            active.finish(Optional.empty());
        }
    }

    private static CompletableFuture<Optional<Selection>> open(Document document, Options options, List<Loader.StaticResourceEntry> entries) {
        if (document == null || document.body == null) {
            return CompletableFuture.completedFuture(Optional.empty());
        }
        Tooltip.hide();
        if (active != null) {
            active.finish(Optional.empty());
        }
        active = new FilePicker(document, options, entries);
        active.render();
        return FilePicker.active.result;
    }

    private void render() {
        Element input;
        Element template = this.document.querySelector("#dialogOverlay");
        if (template == null) {
            this.renderEmbeddedFallback();
            return;
        }
        this.root = template;
        this.root.setAttribute("class", "dialog-overlay show");
        this.root.setTopLayer(true);
        this.bindTemplate(this.root, "click", event -> {
            if (event.target == this.root) {
                this.finish(Optional.empty());
            }
        });
        Element panel = this.document.querySelector("#dialogPanel");
        this.bindTemplate(panel, "click", event -> event.stopPropagation());
        this.setTemplateText("#dialogTitle", this.options.title(), this.options.titleKey());
        this.setTemplateText("#dlgCloseButton", "X", null);
        this.setTemplateText("#dlgQuickTitle", "", "file_picker.apricityui.paths");
        this.setTemplateText("#dlgFileNameLabel", "", "file_picker.apricityui.select_file");
        this.setTemplateText("#dlgCancelButton", "", "file_picker.apricityui.action.cancel");
        this.setTemplateText("#dlgConfirmBtn", "", this.selected == null && this.options.allowsHtmlCreation() ? "file_picker.apricityui.action.create" : "file_picker.apricityui.action.select");
        this.bindTemplate(this.document.querySelector("#dlgCloseButton"), "click", event -> this.finish(Optional.empty()));
        this.bindTemplate(this.document.querySelector("#dlgCancelButton"), "click", event -> this.finish(Optional.empty()));
        this.bindTemplate(this.document.querySelector("#dlgUpBtn"), "click", event -> this.navigateTo(ResourcePath.parent(this.currentPath)));
        Element back = this.document.querySelector("#dlgBackBtn");
        if (back != null) {
            back.setDisabled(true);
        }
        if ((input = this.document.querySelector("#dlgFileName")) != null) {
            input.setAttribute("placeholder", "new-file.html");
            input.setValue(this.selected == null ? "" : this.selected.path());
        }
        this.populateTemplateFilter();
        this.renderTemplateAddress();
        this.renderTemplatePaths();
        this.renderTemplateFiles();
        Element confirm = this.document.querySelector("#dlgConfirmBtn");
        if (confirm != null) {
            confirm.setDisabled(this.selected == null && !this.options.allowsHtmlCreation());
            this.bindTemplate(confirm, "click", event -> this.confirmTemplate(input == null ? "" : input.getValue()));
        }
        this.dirty();
    }

    private void renderEmbeddedFallback() {
        if (this.root != null) {
            this.root.remove();
        }
        this.root = this.element("DIV", "aui-file-picker-overlay");
        this.root.setTopLayer(true);
        this.root.addEventListener("click", event -> {
            if (event.target == this.root) {
                this.finish(Optional.empty());
            }
        });
        Element panel = this.element("DIV", "aui-file-picker");
        Element heading = this.element("DIV", "header aui-file-picker-heading");
        heading.appendChild(this.options.titleKey() == null ? this.text("DIV", this.options.title(), "logo aui-file-picker-title") : this.translation("DIV", this.options.titleKey(), "logo aui-file-picker-title"));
        Element cancel = this.element("BUTTON", "action-btn aui-file-picker-cancel");
        cancel.setAttribute("type", "button");
        cancel.appendChild(this.translation("SPAN", "file_picker.apricityui.action.cancel", null));
        cancel.addEventListener("click", event -> this.finish(Optional.empty()));
        heading.appendChild(cancel);
        panel.appendChild(heading);
        Element body = this.element("DIV", "main aui-file-picker-body");
        body.appendChild(this.renderPaths());
        body.appendChild(this.renderDetails());
        panel.appendChild(body);
        this.root.appendChild(panel);
        this.document.body.appendChild(this.root);
        this.dirty();
    }

    private void renderTemplateAddress() {
        Element address = this.document.querySelector("#dlgAddress");
        if (address == null) {
            return;
        }
        address.clearChildren();
        address.appendChild(this.templatePathItem("file_picker.apricityui.root", "", true, this.currentPath.isBlank()));
        StringBuilder path = new StringBuilder();
        for (String part : this.currentPath.split("/")) {
            if (part.isBlank()) continue;
            if (!path.isEmpty()) {
                path.append('/');
            }
            path.append(part);
            Element separator = this.element("SPAN", "dialog-address-sep");
            separator.setTextContent(">");
            address.appendChild(separator);
            address.appendChild(this.templatePathItem(part, path.toString(), false, path.toString().equals(this.currentPath)));
        }
    }

    private Element templatePathItem(String value, String path, boolean localized, boolean current) {
        Element item = this.element("SPAN", current ? "dialog-address-crumb current" : "dialog-address-crumb");
        if (localized) {
            item.appendChild(this.translation("SPAN", value, null));
        } else {
            item.setTextContent(value);
        }
        item.addEventListener("click", event -> this.navigateTo(path));
        return item;
    }

    private void renderTemplatePaths() {
        Element treeContainer = this.document.querySelector("#dlgQuickAccess");
        if (treeContainer == null) {
            return;
        }
        treeContainer.clearChildren();
        this.appendTemplateTreeChildren(treeContainer, this.buildFolderTree(), 0);
    }

    private void appendTemplateTreeChildren(Element parent, FolderNode folder, int depth) {
        for (FolderNode child : folder.sortedFolders()) {
            boolean expanded = this.expandedPaths.contains(child.path);
            boolean hasChildren = !child.folders.isEmpty() || !child.files.isEmpty();
            Element item = this.element("DIV", this.currentPath.equals(child.path) ? "tree-item selected" : "tree-item");
            item.setAttribute("style", "padding-left:" + (24 + depth * 16) + "px;");
            item.setAttribute("data-path", child.path);
            item.addEventListener("click", event -> this.navigateTo(child.path));
            Element toggle = this.text("DIV", "\u25be", hasChildren ? (expanded ? "tree-toggle" : "tree-toggle collapsed") : "tree-toggle empty");
            toggle.addEventListener("click", event -> {
                event.stopPropagation();
                if (!hasChildren) {
                    return;
                }
                if (this.expandedPaths.contains(child.path)) {
                    this.expandedPaths.remove(child.path);
                } else {
                    this.expandedPaths.add(child.path);
                }
                this.renderTemplatePaths();
                this.dirty();
            });
            item.appendChild(toggle);
            item.appendChild(this.templateTreeFolderIcon());
            item.appendChild(this.text("SPAN", child.name.toUpperCase(Locale.ROOT), null));
            parent.appendChild(item);
            if (!hasChildren) continue;
            Element wrapper = this.element("DIV", expanded ? "tree-children-wrapper expanded" : "tree-children-wrapper");
            Element inner = this.element("DIV", "tree-children-inner");
            if (expanded) {
                this.appendTemplateTreeChildren(inner, child, depth + 1);
            }
            wrapper.appendChild(inner);
            parent.appendChild(wrapper);
        }
        for (Loader.StaticResourceEntry entry : folder.sortedFiles()) {
            boolean active = this.selected != null && this.selected.path().equals(entry.path()) && this.selected.layer() == entry.layer();
            Element item = this.element("DIV", active ? "tree-item selected" : "tree-item");
            item.setAttribute("style", "padding-left:" + (24 + depth * 16) + "px;");
            item.setAttribute("data-path", entry.path());
            item.addEventListener("click", event -> {
                this.selected = entry;
                this.render();
            });
            item.appendChild(this.text("DIV", "\u25be", "tree-toggle empty"));
            item.appendChild(this.templateTreeFileIcon());
            item.appendChild(this.text("SPAN", FilePicker.fileName(entry.path()).toUpperCase(Locale.ROOT), null));
            parent.appendChild(item);
        }
    }

    private Element templateTreeFolderIcon() {
        Element icon = this.element("DIV", "tree-icon");
        Element svg = Element.init(this.document.createElement("SVG"));
        svg.setAttribute("viewBox", "0 0 40 40");
        svg.setAttribute("fill", "none");
        this.appendSvg(svg, "RECT", "x", "4", "y", "12", "width", "32", "height", "22", "fill", "#8b5cf6");
        this.appendSvg(svg, "RECT", "x", "4", "y", "8", "width", "14", "height", "6", "fill", "#6d28d9");
        this.appendSvg(svg, "RECT", "x", "4", "y", "14", "width", "32", "height", "2", "fill", "#6d28d9");
        icon.appendChild(svg);
        return icon;
    }

    private Element templateTreeFileIcon() {
        Element icon = this.element("DIV", "tree-icon");
        Element svg = Element.init(this.document.createElement("SVG"));
        svg.setAttribute("viewBox", "0 0 40 40");
        svg.setAttribute("fill", "none");
        this.appendSvg(svg, "RECT", "x", "6", "y", "4", "width", "28", "height", "32", "fill", "none", "stroke", "#1a1a1a", "stroke-width", "2");
        this.appendSvg(svg, "RECT", "x", "10", "y", "12", "width", "20", "height", "2", "fill", "#8b5cf6");
        this.appendSvg(svg, "RECT", "x", "10", "y", "18", "width", "16", "height", "2", "fill", "#8b5cf6");
        this.appendSvg(svg, "RECT", "x", "10", "y", "24", "width", "20", "height", "2", "fill", "#8b5cf6");
        this.appendSvg(svg, "RECT", "x", "10", "y", "30", "width", "12", "height", "2", "fill", "#8b5cf6");
        icon.appendChild(svg);
        return icon;
    }

    private void renderTemplateFiles() {
        Element grid = this.document.querySelector("#dlgFileGrid");
        if (grid == null) {
            return;
        }
        grid.clearChildren();
        for (String folder : this.directFolders()) {
            grid.appendChild(this.templateCard(FilePicker.fileName(folder), true, null));
        }
        for (Loader.StaticResourceEntry entry : this.directFiles()) {
            grid.appendChild(this.templateCard(FilePicker.fileName(entry.path()), false, entry));
        }
        if (grid.children.isEmpty()) {
            grid.appendChild(this.translation("DIV", "file_picker.apricityui.empty", "dialog-empty"));
        }
    }

    private Element templateCard(String name, boolean folder, Loader.StaticResourceEntry entry) {
        boolean activeSelection = entry != null && this.selected != null && this.selected.path().equals(entry.path()) && this.selected.layer() == entry.layer();
        Element card = this.element("DIV", activeSelection ? "dialog-file-card selected" : "dialog-file-card");
        card.appendChild(this.templateIcon(folder ? "folder" : "data", false));
        card.appendChild(this.text("DIV", (String)(folder ? name + "/" : name), "dialog-file-name"));
        if (folder) {
            card.appendChild(this.text("DIV", "FOLDER", "dialog-file-meta"));
        } else if (entry != null) {
            String sourceKey = entry.layer() == Loader.ResourceLayer.RESOURCE_PACK ? "file_picker.apricityui.source.pack" : "file_picker.apricityui.source.local";
            card.appendChild(this.translation("DIV", sourceKey, "dialog-file-meta"));
        }
        card.addEventListener("click", event -> {
            if (folder) {
                this.navigateTo(this.resolveFolder(name));
            } else {
                this.selected = entry;
                this.render();
            }
        });
        if (!folder) {
            card.addEventListener("dblclick", event -> {
                this.selected = entry;
                this.confirm();
            });
        }
        return card;
    }

    private Element templateIcon(String type, boolean compact) {
        Element icon = this.element("DIV", "dialog-file-icon");
        Element svg = Element.init(this.document.createElement("SVG"));
        svg.setAttribute("viewBox", compact ? "0 0 14 14" : "0 0 40 40");
        svg.setAttribute("fill", "none");
        if (compact) {
            this.appendQuickIcon(svg, type);
        } else if ("folder".equals(type)) {
            this.appendSvg(svg, "RECT", "x", "4", "y", "12", "width", "32", "height", "22", "fill", "#8b5cf6");
            this.appendSvg(svg, "RECT", "x", "4", "y", "8", "width", "14", "height", "6", "fill", "#6d28d9");
            this.appendSvg(svg, "RECT", "x", "4", "y", "14", "width", "32", "height", "2", "fill", "#6d28d9");
        } else {
            this.appendSvg(svg, "RECT", "x", "6", "y", "4", "width", "28", "height", "32", "fill", "none", "stroke", "#1a1a1a", "stroke-width", "2");
            this.appendSvg(svg, "RECT", "x", "10", "y", "12", "width", "20", "height", "2", "fill", "#8b5cf6");
            this.appendSvg(svg, "RECT", "x", "10", "y", "18", "width", "16", "height", "2", "fill", "#8b5cf6");
            this.appendSvg(svg, "RECT", "x", "10", "y", "24", "width", "20", "height", "2", "fill", "#8b5cf6");
            this.appendSvg(svg, "RECT", "x", "10", "y", "30", "width", "12", "height", "2", "fill", "#8b5cf6");
        }
        icon.appendChild(svg);
        return icon;
    }

    private void appendQuickIcon(Element svg, String type) {
        if ("root".equals(type)) {
            this.appendSvg(svg, "PATH", "d", "M7 1L1 6h2v6h4V9h0v3h4V6h2L7 1z", "fill", "currentColor");
        } else if ("worlds".equals(type)) {
            this.appendSvg(svg, "CIRCLE", "cx", "7", "cy", "7", "r", "5", "stroke", "currentColor", "stroke-width", "1.2");
            this.appendSvg(svg, "PATH", "d", "M2 7h10M7 2c-2 2-2 8 0 10M7 2c2 2 2 8 0 10", "stroke", "currentColor", "stroke-width", "0.8");
        } else if ("mods".equals(type)) {
            this.appendSvg(svg, "RECT", "x", "2", "y", "2", "width", "10", "height", "10", "fill", "none", "stroke", "currentColor", "stroke-width", "1.2");
            this.appendSvg(svg, "PATH", "d", "M2 7h10M7 2v10", "stroke", "currentColor", "stroke-width", "0.8");
        } else if ("packs".equals(type)) {
            this.appendSvg(svg, "PATH", "d", "M7 1L1 4v6l6 3 6-3V4L7 1z", "stroke", "currentColor", "stroke-width", "1.2");
            this.appendSvg(svg, "PATH", "d", "M1 4l6 3 6-3M7 7v6", "stroke", "currentColor", "stroke-width", "0.8");
        } else {
            this.appendSvg(svg, "CIRCLE", "cx", "7", "cy", "7", "r", "4", "stroke", "currentColor", "stroke-width", "1.2");
            this.appendSvg(svg, "RECT", "x", "6", "y", "2", "width", "2", "height", "2", "fill", "currentColor");
            this.appendSvg(svg, "RECT", "x", "6", "y", "10", "width", "2", "height", "2", "fill", "currentColor");
        }
    }

    private void appendSvg(Element parent, String tag, String ... attributes) {
        Element child = Element.init(this.document.createElement(tag));
        int index = 0;
        while (index + 1 < attributes.length) {
            child.setAttribute(attributes[index], attributes[index + 1]);
            index += 2;
        }
        parent.appendChild(child);
    }

    private String resolveFolder(String name) {
        return this.currentPath.isBlank() ? name : this.currentPath + "/" + name;
    }

    private void navigateTo(String path) {
        this.currentPath = path == null ? "" : path;
        this.expandAncestors(this.currentPath);
        this.selected = null;
        this.creatingHtml = false;
        this.render();
    }

    private void populateTemplateFilter() {
        Element filter = this.document.querySelector("#dlgFilter");
        if (filter == null) {
            return;
        }
        filter.clearChildren();
        Element option = this.element("OPTION", "");
        option.setAttribute("value", "active-filter");
        option.setTextContent(this.options.extensions().isEmpty() ? "*.*" : String.join((CharSequence)", ", this.options.extensions()));
        filter.appendChild(option);
        filter.setDisabled(true);
    }

    private void confirmTemplate(String requestedPath) {
        if (this.selected != null) {
            this.confirm();
            return;
        }
        if (!this.options.allowsHtmlCreation()) {
            return;
        }
        this.createHtml(requestedPath);
        if (this.selected != null) {
            this.confirm();
        }
    }

    private void setTemplateText(String selector, String literal, String key) {
        Element target = this.document.querySelector(selector);
        if (target == null) {
            return;
        }
        target.clearChildren();
        if (key == null || key.isBlank()) {
            target.setTextContent(literal == null ? "" : literal);
        } else {
            target.appendChild(this.translation("SPAN", key, null));
        }
    }

    private void bindTemplate(Element element, String type, Consumer<Event> listener) {
        if (element == null) {
            return;
        }
        String marker = "data-file-picker-bound-" + type;
        if ("1".equals(element.getAttribute(marker))) {
            return;
        }
        element.setAttribute(marker, "1");
        element.addEventListener(type, listener);
    }

    private Element renderPaths() {
        Element paths = this.element("DIV", "sidebar aui-file-picker-paths");
        paths.appendChild(this.translation("DIV", "file_picker.apricityui.paths", "sidebar-title aui-file-picker-section-label"));
        Element rootItem = this.pathItem("file_picker.apricityui.root", "", 0, true);
        paths.appendChild(rootItem);
        for (String folder : this.folders()) {
            int depth = folder.split("/").length;
            paths.appendChild(this.pathItem(FilePicker.fileName(folder), folder, depth, false));
        }
        return paths;
    }

    private Element pathItem(String label, String path, int depth, boolean localizedLabel) {
        boolean activePath = this.currentPath.equals(path);
        Element item = this.element("BUTTON", activePath ? "tree-item selected aui-file-picker-path" : "tree-item aui-file-picker-path");
        item.setAttribute("type", "button");
        item.setAttribute("style", "padding-left:" + (12 + depth * 14) + "px;");
        if (localizedLabel) {
            item.appendChild(this.translation("SPAN", label, null));
        } else {
            item.setTextContent(label);
        }
        item.addEventListener("click", event -> {
            this.currentPath = path;
            this.selected = null;
            this.creatingHtml = false;
            this.render();
        });
        return item;
    }

    private Element renderDetails() {
        Element details = this.element("DIV", "content aui-file-picker-details");
        Element toolbar = this.element("DIV", "content-header aui-file-picker-toolbar");
        toolbar.appendChild(this.currentPath.isBlank() ? this.translation("DIV", "file_picker.apricityui.root", "content-title aui-file-picker-current-path") : this.text("DIV", this.currentPath, "content-title aui-file-picker-current-path"));
        if (this.options.allowsHtmlCreation()) {
            Element create = this.element("BUTTON", "action-btn aui-file-picker-create");
            create.setAttribute("type", "button");
            create.appendChild(this.translation("SPAN", "file_picker.apricityui.action.new_html", null));
            create.addEventListener("click", event -> {
                this.creatingHtml = !this.creatingHtml;
                this.render();
            });
            toolbar.appendChild(create);
        }
        details.appendChild(toolbar);
        if (this.creatingHtml) {
            details.appendChild(this.renderCreateHtml());
        }
        Element list = this.element("DIV", "file-grid aui-file-picker-list");
        for (String folder : this.directFolders()) {
            Element item = this.element("BUTTON", "file-card aui-file-picker-folder");
            item.setAttribute("type", "button");
            item.appendChild(this.text("SPAN", FilePicker.fileName(folder) + "/", "file-name"));
            item.addEventListener("click", event -> {
                this.currentPath = folder;
                this.selected = null;
                this.render();
            });
            list.appendChild(item);
        }
        for (Loader.StaticResourceEntry entry : this.directFiles()) {
            list.appendChild(this.fileItem(entry));
        }
        if (list.children.isEmpty()) {
            list.appendChild(this.translation("DIV", "file_picker.apricityui.empty", "aui-file-picker-empty"));
        }
        details.appendChild(list);
        Element footer = this.element("DIV", "aui-file-picker-footer");
        footer.appendChild(this.selected == null ? this.translation("DIV", "file_picker.apricityui.select_file", "aui-file-picker-selection") : this.text("DIV", this.selected.path(), "aui-file-picker-selection"));
        Element select = this.element("BUTTON", "action-btn aui-file-picker-select");
        select.setAttribute("type", "button");
        select.setDisabled(this.selected == null);
        select.appendChild(this.translation("SPAN", "file_picker.apricityui.action.select", null));
        select.addEventListener("click", event -> this.confirm());
        footer.appendChild(select);
        details.appendChild(footer);
        return details;
    }

    private Element renderCreateHtml() {
        Element form = this.element("DIV", "aui-file-picker-create-form");
        Element input = this.element("INPUT", "dialog-input aui-file-picker-create-input");
        input.setAttribute("type", "text");
        input.setAttribute("placeholder", "new-file.html");
        input.setValue((String)(this.currentPath.isBlank() ? "new-file.html" : this.currentPath + "/new-file.html"));
        Element submit = this.element("BUTTON", "action-btn aui-file-picker-create-submit");
        submit.setAttribute("type", "button");
        submit.appendChild(this.translation("SPAN", "file_picker.apricityui.action.create", null));
        submit.addEventListener("click", event -> this.createHtml(input.getValue()));
        form.appendChild(input);
        form.appendChild(submit);
        return form;
    }

    private Element fileItem(Loader.StaticResourceEntry entry) {
        boolean activeSelection = this.selected != null && this.selected.path().equals(entry.path()) && this.selected.layer() == entry.layer();
        Element item = this.element("BUTTON", activeSelection ? "file-card selected aui-file-picker-file" : "file-card aui-file-picker-file");
        item.setAttribute("type", "button");
        item.appendChild(this.text("SPAN", FilePicker.fileName(entry.path()), "file-name aui-file-picker-file-name"));
        String sourceKey = entry.layer() == Loader.ResourceLayer.RESOURCE_PACK ? "file_picker.apricityui.source.pack" : "file_picker.apricityui.source.local";
        item.appendChild(this.translation("SPAN", sourceKey, "file-meta aui-file-picker-file-source"));
        item.addEventListener("click", event -> {
            this.selected = entry;
            this.render();
        });
        item.addEventListener("dblclick", event -> {
            this.selected = entry;
            this.confirm();
        });
        return item;
    }

    private void createHtml(String requestedPath) {
        String path = ResourceFileWriter.validateHtmlPath(requestedPath);
        if (path.isBlank()) {
            return;
        }
        ResourceFileWriter.WriteResult write = ResourceFileWriter.writeHtml(path, "<!DOCTYPE html>\n<html><head><meta charset=\"UTF-8\"></head><body></body></html>\n");
        if (!write.success()) {
            return;
        }
        ClientLoader.invalidateStaticResourceCache();
        this.entries = this.filter(ClientLoader.listFinalStaticResources());
        this.currentPath = ResourcePath.parent(path);
        this.selected = this.entries.stream().filter((? super T entry) -> path.equals(entry.path())).findFirst().orElse(null);
        this.creatingHtml = false;
        this.render();
    }

    private void confirm() {
        if (this.selected == null) {
            return;
        }
        this.finish(Optional.of(new Selection(this.selected.path(), this.selected.layer(), FilePicker.resolveLocalPath(this.selected))));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private synchronized void finish(Optional<Selection> value) {
        if (this.root != null) {
            this.root.remove();
        }
        this.root = null;
        if (!this.result.isDone()) {
            this.result.complete(value == null ? Optional.empty() : value);
        }
        Class<FilePicker> clazz = FilePicker.class;
        synchronized (FilePicker.class) {
            if (active == this) {
                active = null;
            }
            // ** MonitorExit[var2_2] (shouldn't be in output)
            if (this.document != null && this.document.getPath().equals(PATH) && !this.document.isDisposed()) {
                this.document.remove();
            }
            return;
        }
    }

    private List<Loader.StaticResourceEntry> filter(List<Loader.StaticResourceEntry> source) {
        if (source == null) {
            return List.of();
        }
        return source.stream().filter(this.options::accepts).sorted(Comparator.comparing(Loader.StaticResourceEntry::path)).toList();
    }

    private List<String> folders() {
        LinkedHashSet<String> result = new LinkedHashSet<String>();
        for (Loader.StaticResourceEntry entry : this.entries) {
            String[] parts = ResourcePath.normalize(entry.path()).split("/");
            StringBuilder path = new StringBuilder();
            for (int index = 0; index < parts.length - 1; ++index) {
                if (!path.isEmpty()) {
                    path.append('/');
                }
                path.append(parts[index]);
                result.add(path.toString());
            }
        }
        return result.stream().sorted(String.CASE_INSENSITIVE_ORDER).toList();
    }

    private FolderNode buildFolderTree() {
        FolderNode treeRoot = new FolderNode("ROOT", "");
        for (Loader.StaticResourceEntry entry : this.entries) {
            if (entry == null) continue;
            String[] parts = ResourcePath.normalize(entry.path()).split("/");
            FolderNode cursor = treeRoot;
            StringBuilder folderPath = new StringBuilder();
            for (int index = 0; index < parts.length - 1; ++index) {
                String name = parts[index];
                if (name.isBlank()) continue;
                if (!folderPath.isEmpty()) {
                    folderPath.append('/');
                }
                folderPath.append(name);
                String path = folderPath.toString();
                cursor = cursor.folders.computeIfAbsent(name, ignored -> new FolderNode(name, path));
            }
            cursor.files.add(entry);
        }
        return treeRoot;
    }

    private void expandAncestors(String path) {
        StringBuilder ancestor = new StringBuilder();
        for (String part : ResourcePath.normalize(path).split("/")) {
            if (part.isBlank()) continue;
            if (!ancestor.isEmpty()) {
                ancestor.append('/');
            }
            ancestor.append(part);
            this.expandedPaths.add(ancestor.toString());
        }
    }

    private List<String> directFolders() {
        String prefix = this.currentPath.isBlank() ? "" : this.currentPath + "/";
        return this.folders().stream().filter((? super T folder) -> ResourcePath.parent(folder).equals(this.currentPath)).filter((? super T folder) -> folder.startsWith(prefix)).toList();
    }

    private List<Loader.StaticResourceEntry> directFiles() {
        return this.entries.stream().filter((? super T entry) -> ResourcePath.parent(entry.path()).equals(this.currentPath)).toList();
    }

    private static Path resolveLocalPath(Loader.StaticResourceEntry entry) {
        if (entry == null || entry.layer() == Loader.ResourceLayer.RESOURCE_PACK || entry.sourceRoot() == null || entry.sourceRoot().isBlank()) {
            return null;
        }
        try {
            Path root = Path.of(entry.sourceRoot(), new String[0]).toAbsolutePath().normalize();
            if (!Files.exists(root, new LinkOption[0])) {
                return null;
            }
            Path resolved = root.resolve(ResourcePath.normalize(entry.path())).normalize();
            return resolved.startsWith(root) ? resolved : null;
        }
        catch (RuntimeException ignored) {
            return null;
        }
    }

    private Element element(String tag, String className) {
        Element element = Element.init(this.document.createElement(tag));
        element.setAttribute("class", className);
        return element;
    }

    private Element text(String tag, String value, String className) {
        Element element = this.element(tag, className);
        element.setTextContent(value == null ? "" : value);
        return element;
    }

    private Element translation(String tag, String key, String className) {
        Element element = this.element(tag, className);
        element.appendChild(Element.init(this.document.createElement("TRANSLATION")));
        element.children.get(0).setTextContent(key == null ? "" : key);
        return element;
    }

    private void dirty() {
        if (this.document != null && this.document.body != null) {
            this.document.markDirty(this.document.body, 7);
        }
    }

    private static String fileName(String value) {
        String normalized = ResourcePath.normalize(value);
        int index = normalized.lastIndexOf(47);
        return index < 0 ? normalized : normalized.substring(index + 1);
    }

    private static String normalizeExtension(String value) {
        String normalized = value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
        return normalized.startsWith(".") ? normalized.substring(1) : normalized;
    }

    public record Options(String title, String titleKey, Set<String> extensions, boolean includeResourcePackFiles) {
        public Options(String title, Set<String> extensions, boolean includeResourcePackFiles) {
            this(title, null, extensions, includeResourcePackFiles);
        }

        public Options {
            boolean missingTitle = title == null || title.isBlank();
            String string = title = missingTitle ? "" : title.trim();
            titleKey = titleKey == null || titleKey.isBlank() ? (missingTitle ? FilePicker.DEFAULT_TITLE_KEY : null) : titleKey.trim();
            LinkedHashSet<String> normalized = new LinkedHashSet<String>();
            if (extensions != null) {
                for (String extension : extensions) {
                    String value = FilePicker.normalizeExtension(extension);
                    if (value.isBlank()) continue;
                    normalized.add(value);
                }
            }
            extensions = Set.copyOf(normalized);
        }

        public static Options html(String title, boolean includeResourcePackFiles) {
            return new Options(title, Set.of("html"), includeResourcePackFiles);
        }

        public static Options htmlTranslation(String titleKey, boolean includeResourcePackFiles) {
            return new Options(null, titleKey, Set.of("html"), includeResourcePackFiles);
        }

        public static Options any(String title, boolean includeResourcePackFiles) {
            return new Options(title, Set.of(), includeResourcePackFiles);
        }

        boolean accepts(Loader.StaticResourceEntry entry) {
            if (entry == null) {
                return false;
            }
            if (!this.includeResourcePackFiles && entry.layer() == Loader.ResourceLayer.RESOURCE_PACK) {
                return false;
            }
            return this.extensions.isEmpty() || this.extensions.contains(FilePicker.normalizeExtension(entry.extension()));
        }

        boolean allowsHtmlCreation() {
            return this.extensions.isEmpty() || this.extensions.contains("html");
        }
    }

    private static final class FolderNode {
        private final String name;
        private final String path;
        private final Map<String, FolderNode> folders = new LinkedHashMap<String, FolderNode>();
        private final List<Loader.StaticResourceEntry> files = new ArrayList<Loader.StaticResourceEntry>();

        private FolderNode(String name, String path) {
            this.name = name == null ? "" : name;
            this.path = ResourcePath.normalize(path);
        }

        private List<FolderNode> sortedFolders() {
            return this.folders.values().stream().sorted(Comparator.comparing(folder -> folder.name.toLowerCase(Locale.ROOT))).toList();
        }

        private List<Loader.StaticResourceEntry> sortedFiles() {
            return this.files.stream().sorted(Comparator.comparing(entry -> FilePicker.fileName(entry.path()).toLowerCase(Locale.ROOT))).toList();
        }
    }

    public record Selection(String path, Loader.ResourceLayer layer, Path localPath) {
    }
}

