/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.editor.ore;

import com.sighs.apricityui.editor.ore.OreEditorDom;
import com.sighs.apricityui.editor.ore.OreEditorHistory;
import com.sighs.apricityui.editor.ore.OreEditorSession;
import com.sighs.apricityui.editor.ore.canvas.OreCanvasHitTester;
import com.sighs.apricityui.editor.ore.canvas.OreCanvasRenderer;
import com.sighs.apricityui.editor.ore.canvas.OreFlexInsertionResolver;
import com.sighs.apricityui.editor.ore.drag.OreDragController;
import com.sighs.apricityui.editor.ore.model.OreAbsoluteConstraints;
import com.sighs.apricityui.editor.ore.model.OreCanvasNode;
import com.sighs.apricityui.editor.ore.model.OreComponentNode;
import com.sighs.apricityui.editor.ore.model.OreContainerNode;
import com.sighs.apricityui.editor.ore.model.OreEditorProject;
import com.sighs.apricityui.editor.ore.palette.OreComponentDefinition;
import com.sighs.apricityui.editor.ore.palette.OreComponentRegistry;
import com.sighs.apricityui.editor.ore.persistence.OreEditorDocumentStore;
import com.sighs.apricityui.editor.ore.persistence.OreEditorHtmlExporter;
import com.sighs.apricityui.editor.ore.persistence.OreEditorHtmlImporter;
import com.sighs.apricityui.editor.ore.persistence.OreEditorProjectCodec;
import com.sighs.apricityui.event.Event;
import com.sighs.apricityui.event.KeyEvent;
import com.sighs.apricityui.event.MouseEvent;
import com.sighs.apricityui.init.Document;
import com.sighs.apricityui.init.Element;
import com.sighs.apricityui.init.Node;
import com.sighs.apricityui.layout.Box;
import com.sighs.apricityui.loader.ClientLoader;
import com.sighs.apricityui.ui.ToastManager;
import com.sighs.apricityui.ui.Tooltip;
import com.sighs.apricityui.ui.UiTranslations;
import java.io.IOException;
import java.lang.invoke.CallSite;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class OreEditorController {
    static final OreEditorController INSTANCE = new OreEditorController();
    static final String PATH = "editor/ore/ore-editor.html";
    private static final double MIN_WIDTH = 360.0;
    private static final double MAX_WIDTH = 560.0;
    private static final List<ThemeToken> THEME_TOKENS = List.of(new ThemeToken("--ore-ink", "#f4f5f7", "ink"), new ThemeToken("--ore-ink-muted", "#b6bac1", "ink_muted"), new ThemeToken("--ore-ink-dark", "#191a1c", "ink_dark"), new ThemeToken("--ore-canvas", "#202124", "canvas"), new ThemeToken("--ore-surface", "#48494a", "surface"), new ThemeToken("--ore-surface-deep", "#313233", "surface_deep"), new ThemeToken("--ore-surface-soft", "#d0d1d4", "surface_soft"), new ThemeToken("--ore-edge", "#1e1e1f", "edge"), new ThemeToken("--ore-edge-light", "#77797c", "edge_light"), new ThemeToken("--ore-green", "#3c8527", "green"), new ThemeToken("--ore-green-hover", "#2a641c", "green_hover"), new ThemeToken("--ore-green-shadow", "#1d4d13", "green_shadow"), new ThemeToken("--ore-purple", "#7345e5", "purple"), new ThemeToken("--ore-purple-hover", "#5d2cc6", "purple_hover"), new ThemeToken("--ore-purple-shadow", "#4a1cac", "purple_shadow"), new ThemeToken("--ore-gold", "#f0b92d", "gold"), new ThemeToken("--ore-gold-shadow", "#936715", "gold_shadow"), new ThemeToken("--ore-red", "#b33b31", "red"), new ThemeToken("--ore-red-hover", "#8b2923", "red_hover"), new ThemeToken("--ore-red-shadow", "#662019", "red_shadow"), new ThemeToken("--ore-blue", "#2d78a8", "blue"), new ThemeToken("--ore-success", "#69ad45", "success"), new ThemeToken("--ore-warning", "#f0b92d", "warning"), new ThemeToken("--ore-danger", "#d45b50", "danger"), new ThemeToken("--ore-info", "#58a6d2", "info"), new ThemeToken("--ore-focus", "#ffffff", "focus"), new ThemeToken("--ore-space-1", "4px", "space_1"), new ThemeToken("--ore-space-2", "8px", "space_2"), new ThemeToken("--ore-space-3", "16px", "space_3"), new ThemeToken("--ore-space-4", "24px", "space_4"), new ThemeToken("--ore-space-5", "32px", "space_5"), new ThemeToken("--ore-font-sm", "13px", "font_sm"), new ThemeToken("--ore-font-md", "16px", "font_md"), new ThemeToken("--ore-font-lg", "20px", "font_lg"), new ThemeToken("--ore-font-xl", "28px", "font_xl"));
    private static final List<ThemeGroup> THEME_GROUPS = List.of(new ThemeGroup("typography", List.of("--ore-ink", "--ore-ink-muted", "--ore-ink-dark", "--ore-font-sm", "--ore-font-md", "--ore-font-lg", "--ore-font-xl")), new ThemeGroup("surfaces", List.of("--ore-canvas", "--ore-surface", "--ore-surface-deep", "--ore-surface-soft", "--ore-edge", "--ore-edge-light", "--ore-focus")), new ThemeGroup("actions", List.of("--ore-green", "--ore-green-hover", "--ore-green-shadow", "--ore-purple", "--ore-purple-hover", "--ore-purple-shadow", "--ore-gold", "--ore-gold-shadow", "--ore-red", "--ore-red-hover", "--ore-red-shadow", "--ore-blue")), new ThemeGroup("feedback", List.of("--ore-success", "--ore-warning", "--ore-danger", "--ore-info")), new ThemeGroup("spacing", List.of("--ore-space-1", "--ore-space-2", "--ore-space-3", "--ore-space-4", "--ore-space-5")));
    private Document document;
    private final OreEditorSession session = new OreEditorSession();
    private OreEditorProject project = new OreEditorProject();
    private final OreEditorHistory history = new OreEditorHistory();
    private final OreEditorProjectCodec projectCodec = new OreEditorProjectCodec();
    private final OreEditorHtmlExporter htmlExporter = new OreEditorHtmlExporter();
    private final OreEditorHtmlImporter htmlImporter = new OreEditorHtmlImporter();
    private final OreEditorDocumentStore documentStore = new OreEditorDocumentStore();
    private final OreDragController drag = new OreDragController();
    private final OreCanvasHitTester hitTester = new OreCanvasHitTester();
    private final OreFlexInsertionResolver insertionResolver = new OreFlexInsertionResolver();
    private OreCanvasRenderer canvasRenderer;
    private Element dragGhost;
    private Element unsavedChangesDialog;
    private Path openedHtmlPath;
    private UUID hoveredNode;
    private OreContainerNode dropTarget;
    private OreFlexInsertionResolver.Insertion dropInsertion;
    private UUID movingNode;
    private UUID absoluteDragNode;
    private double absoluteDragStartX;
    private double absoluteDragStartY;
    private double absoluteDragLeft;
    private double absoluteDragTop;
    private double absoluteDragRight;
    private double absoluteDragBottom;
    private UUID absoluteResizeNode;
    private double absoluteResizeStartX;
    private double absoluteResizeStartY;
    private double absoluteResizeWidth;
    private double absoluteResizeHeight;
    private double absoluteResizeRight;
    private double absoluteResizeBottom;
    private ComponentState absoluteDragBefore;
    private ComponentState absoluteResizeBefore;
    private OreComponentNode.VisualState editingVisualState = OreComponentNode.VisualState.DEFAULT;
    private boolean showingPaletteContainers = true;
    private boolean resizing;

    OreEditorController() {
    }

    synchronized boolean isOpen() {
        return this.document != null && this.document.isActive() && Document.get(PATH).contains(this.document);
    }

    synchronized Document getDocument() {
        return this.isOpen() ? this.document : null;
    }

    synchronized OreEditorSession getSession() {
        return this.session;
    }

    synchronized boolean loadSavedProject() {
        OreEditorDocumentStore.ReadResult result = this.documentStore.readProject();
        if (!result.success()) {
            return false;
        }
        try {
            this.project = this.projectCodec.read(result.content());
            this.openedHtmlPath = null;
            this.session.reset();
            this.session.select(this.project.root().id());
            this.history.reset();
            if (this.isOpen()) {
                this.updateDocumentState();
                this.renderCanvas();
                this.renderMode();
                this.renderBreadcrumb();
                this.updateHistoryControls();
            }
            ToastManager.showTranslation("ore_editor.apricityui.notice.loaded");
            return true;
        }
        catch (IllegalArgumentException ignored) {
            return false;
        }
    }

    synchronized boolean open() {
        if (!this.isOpen()) {
            this.document = Document.create(PATH);
        }
        if (this.document == null) {
            return false;
        }
        this.document.setReloadPersistent(true);
        Element canvas = this.document.querySelector("#editorCanvas");
        if (canvas != null) {
            this.canvasRenderer = new OreCanvasRenderer(this.document, canvas, this::selectNode, this::beginNodeDrag, this::beginAbsoluteResize);
        }
        if (this.session.selectedNode() == null || this.project.find(this.session.selectedNode()) == null) {
            this.session.select(this.project.root().id());
        }
        this.bindShell();
        this.updateDocumentState();
        this.renderMode();
        this.renderCanvas();
        this.renderBreadcrumb();
        this.history.reset();
        this.updateHistoryControls();
        return true;
    }

    synchronized boolean openHtml(Path path, String source) {
        if (this.session.dirty() && this.isOpen()) {
            this.showDiscardConfirmation("ore_editor.apricityui.dialog.open_html.title", "ore_editor.apricityui.dialog.open_html.message", () -> this.openHtmlNow(path, source));
            return false;
        }
        return this.openHtmlNow(path, source);
    }

    private boolean openHtmlNow(Path path, String source) {
        try {
            if (path == null || !Files.isRegularFile(path, new LinkOption[0])) {
                return false;
            }
            this.project = this.htmlImporter.read(source);
            this.openedHtmlPath = path.toAbsolutePath().normalize();
            this.session.reset();
            this.session.select(this.project.root().id());
            this.history.reset();
            return this.open();
        }
        catch (IllegalArgumentException ignored) {
            return false;
        }
    }

    synchronized void close() {
        if (this.isOpen() && this.session.dirty()) {
            this.showDiscardConfirmation("ore_editor.apricityui.dialog.unsaved.title", "ore_editor.apricityui.dialog.unsaved.message", this::closeNow);
            return;
        }
        this.closeNow();
    }

    private void closeNow() {
        Document closing = this.document;
        this.document = null;
        this.unsavedChangesDialog = null;
        this.canvasRenderer = null;
        this.hoveredNode = null;
        this.dropTarget = null;
        this.dropInsertion = null;
        this.movingNode = null;
        this.absoluteDragNode = null;
        this.absoluteResizeNode = null;
        this.openedHtmlPath = null;
        this.removeDragGhost();
        this.drag.cancel();
        this.resizing = false;
        this.showingPaletteContainers = true;
        this.session.reset();
        Tooltip.hide(closing);
        if (closing != null && !closing.isDisposed()) {
            closing.remove();
        }
    }

    synchronized void toggle() {
        if (this.isOpen()) {
            this.close();
        } else {
            this.open();
        }
    }

    private void bindShell() {
        if (this.document == null || this.document.body == null) {
            return;
        }
        this.bindAccessibilityLabels();
        this.bindClick("#closeButton", this::close);
        this.bindClick("#undoButton", this::undo);
        this.bindClick("#redoButton", this::redo);
        this.bindClick("#loadButton", this::requestLoadSavedProject);
        this.bindClick("#saveButton", this::saveProject);
        this.bindClick("#exportButton", this::exportHtml);
        this.bindTooltip("#undoButton");
        this.bindTooltip("#redoButton");
        this.bindTooltip("#loadButton");
        this.bindTooltip("#saveButton");
        this.bindTooltip("#exportButton");
        this.bindTooltip("#closeButton");
        for (Element tab : this.document.querySelectorAll(".editor-tab")) {
            if ("1".equals(tab.getAttribute("data-java-bound"))) continue;
            tab.setAttribute("data-java-bound", "1");
            tab.addEventListener("click", event -> {
                this.session.setMode(this.parseMode(tab.getAttribute("data-editor-mode")));
                this.renderMode();
            });
        }
        Element handle = this.document.querySelector("#editorResizeHandle");
        if (handle != null && !"1".equals(handle.getAttribute("data-java-bound"))) {
            handle.setAttribute("data-java-bound", "1");
            handle.addEventListener("mousedown", event -> {
                this.resizing = true;
                handle.setAttribute("class", "editor-resize-handle dragging");
                event.preventDefault();
            });
        }
        if (!"1".equals(this.document.body.getAttribute("data-editor-pointer-bound"))) {
            this.document.body.setAttribute("data-editor-pointer-bound", "1");
            this.document.body.addEventListener("mousemove", event -> {
                if (!(event instanceof MouseEvent)) {
                    return;
                }
                MouseEvent mouseEvent = (MouseEvent)event;
                this.resizeSidebar(mouseEvent.clientX);
                this.movePaletteDrag(mouseEvent.clientX, mouseEvent.clientY);
                this.moveNodeDrag(mouseEvent.clientX, mouseEvent.clientY);
                this.moveAbsoluteNode(mouseEvent.clientX, mouseEvent.clientY);
                this.moveAbsoluteResize(mouseEvent.clientX, mouseEvent.clientY);
                this.updateCanvasHover(mouseEvent.clientX, mouseEvent.clientY);
            });
            this.document.body.addEventListener("mouseup", event -> {
                if (!(event instanceof MouseEvent)) {
                    return;
                }
                MouseEvent mouseEvent = (MouseEvent)event;
                this.stopResize(handle);
                this.finishPaletteDrag(mouseEvent.clientX, mouseEvent.clientY);
                this.finishNodeDrag(mouseEvent.clientX, mouseEvent.clientY);
                this.finishAbsoluteNode();
                this.finishAbsoluteResize();
            });
        }
        if (!"1".equals(this.document.body.getAttribute("data-editor-key-bound"))) {
            this.document.body.setAttribute("data-editor-key-bound", "1");
            this.document.body.addEventListener("keydown", this::handleEditorShortcut);
        }
    }

    private void bindAccessibilityLabels() {
        for (Element element : this.document.querySelectorAll("[data-aria-label-key]")) {
            String key = element.getAttribute("data-aria-label-key");
            if (key == null || key.isBlank()) continue;
            element.setAttribute("aria-label", UiTranslations.translate(key));
        }
    }

    private void bindClick(String selector, Runnable action) {
        Element element = this.document.querySelector(selector);
        if (element == null || "1".equals(element.getAttribute("data-java-bound"))) {
            return;
        }
        element.setAttribute("data-java-bound", "1");
        element.addEventListener("click", event -> action.run());
    }

    private void handleEditorShortcut(Event event) {
        boolean undo;
        KeyEvent keyEvent;
        block7: {
            block6: {
                if (!(event instanceof KeyEvent)) break block6;
                keyEvent = (KeyEvent)event;
                if ((keyEvent.controlKey || keyEvent.metaKey) && !keyEvent.altKey && !OreEditorController.isTextEntry(keyEvent.target)) break block7;
            }
            return;
        }
        boolean redo = keyEvent.keyCode == 89 || keyEvent.keyCode == 90 && keyEvent.shiftKey;
        boolean bl = undo = keyEvent.keyCode == 90 && !keyEvent.shiftKey;
        if (!undo && !redo) {
            return;
        }
        if (undo) {
            this.undo();
        } else {
            this.redo();
        }
        event.preventDefault();
        event.stopPropagation();
    }

    private static boolean isTextEntry(Object target) {
        if (!(target instanceof Element)) {
            return false;
        }
        Element element = (Element)target;
        return "INPUT".equalsIgnoreCase(element.tagName) || "TEXTAREA".equalsIgnoreCase(element.tagName) || "SELECT".equalsIgnoreCase(element.tagName);
    }

    private void requestLoadSavedProject() {
        if (this.session.dirty()) {
            this.showDiscardConfirmation("ore_editor.apricityui.dialog.load.title", "ore_editor.apricityui.dialog.load.message", this::loadSavedProject);
            return;
        }
        this.loadSavedProject();
    }

    private void showDiscardConfirmation(String titleKey, String messageKey, Runnable discardAction) {
        if (this.document == null || this.document.body == null || this.unsavedChangesDialog != null) {
            return;
        }
        Element overlay = Element.init(this.document.createElement("DIV"));
        overlay.setAttribute("class", "editor-confirm-overlay");
        overlay.setAttribute("data-ore-editor-ui", "unsaved-changes-dialog");
        overlay.setTopLayer(true);
        overlay.addEventListener("click", event -> this.closeUnsavedChangesDialog());
        Element dialog = Element.init(this.document.createElement("DIV"));
        dialog.setAttribute("class", "panel editor-confirm-dialog");
        dialog.addEventListener("click", event -> event.stopPropagation());
        Element header = Element.init(this.document.createElement("DIV"));
        header.setAttribute("class", "panel-header");
        header.appendChild(OreEditorDom.translation(this.document, titleKey, null));
        Element body = Element.init(this.document.createElement("DIV"));
        body.setAttribute("class", "panel-body");
        body.appendChild(OreEditorDom.translation(this.document, messageKey, null));
        Element actions = Element.init(this.document.createElement("DIV"));
        actions.setAttribute("class", "editor-inspector-actions");
        Element cancel = Element.init(this.document.createElement("BUTTON"));
        cancel.setAttribute("class", "button button-secondary button-small");
        cancel.setAttribute("type", "button");
        cancel.appendChild(OreEditorDom.translation(this.document, "ore_editor.apricityui.action.cancel", null));
        cancel.addEventListener("click", event -> this.closeUnsavedChangesDialog());
        Element discard = Element.init(this.document.createElement("BUTTON"));
        discard.setAttribute("class", "button button-danger button-small");
        discard.setAttribute("type", "button");
        discard.appendChild(OreEditorDom.translation(this.document, "ore_editor.apricityui.action.discard_changes", null));
        discard.addEventListener("click", event -> {
            this.closeUnsavedChangesDialog();
            discardAction.run();
        });
        actions.appendChild(cancel);
        actions.appendChild(discard);
        body.appendChild(actions);
        dialog.appendChild(header);
        dialog.appendChild(body);
        overlay.appendChild(dialog);
        this.document.body.appendChild(overlay);
        this.unsavedChangesDialog = overlay;
        this.document.markDirty(this.document.body, 15);
    }

    private void closeUnsavedChangesDialog() {
        if (this.unsavedChangesDialog != null) {
            this.unsavedChangesDialog.remove();
        }
        this.unsavedChangesDialog = null;
        if (this.document != null && this.document.body != null) {
            this.document.markDirty(this.document.body, 15);
        }
    }

    private void bindTooltip(String selector) {
        Element element = this.document.querySelector(selector);
        if (element == null || "1".equals(element.getAttribute("data-tooltip-bound"))) {
            return;
        }
        String key = element.getAttribute("data-tooltip-key");
        if (key == null || key.isBlank()) {
            return;
        }
        element.setAttribute("data-tooltip-bound", "1");
        Tooltip.bindTranslation(element, key);
    }

    private OreEditorSession.Mode parseMode(String raw) {
        if (raw == null) {
            return OreEditorSession.Mode.ADD;
        }
        try {
            return OreEditorSession.Mode.valueOf(raw.trim().toUpperCase());
        }
        catch (IllegalArgumentException ignored) {
            return OreEditorSession.Mode.ADD;
        }
    }

    private void renderMode() {
        String title;
        if (this.document == null) {
            return;
        }
        for (Element element : this.document.querySelectorAll(".editor-tab")) {
            boolean bl = this.parseMode(element.getAttribute("data-editor-mode")) == this.session.mode();
            element.setAttribute("class", bl ? "button button-small editor-tab active" : "button button-small editor-tab");
        }
        Element content = this.document.querySelector("#editorSidebarContent");
        if (content == null) {
            return;
        }
        for (Node node : new ArrayList<Element>(content.children)) {
            node.remove();
        }
        Element element = Element.init(this.document.createElement("DIV"));
        element.setAttribute("class", "panel");
        Element element2 = Element.init(this.document.createElement("DIV"));
        element2.setAttribute("class", "panel-header");
        Element body = Element.init(this.document.createElement("DIV"));
        body.setAttribute("class", "panel-body");
        switch (this.session.mode()) {
            case INSPECT: {
                title = "ore_editor.apricityui.inspector.title";
                String message = "ore_editor.apricityui.empty.inspect";
                break;
            }
            case THEME: {
                title = "ore_editor.apricityui.theme.title";
                String message = "ore_editor.apricityui.empty.theme";
                break;
            }
            default: {
                title = this.showingPaletteContainers ? "ore_editor.apricityui.palette.containers" : "ore_editor.apricityui.palette.components";
                String message = "ore_editor.apricityui.empty.add";
            }
        }
        element2.appendChild(OreEditorDom.translation(this.document, title, null));
        if (this.session.mode() == OreEditorSession.Mode.ADD) {
            this.renderPalette(body);
        } else if (this.session.mode() == OreEditorSession.Mode.INSPECT) {
            this.renderInspector(body);
        } else {
            this.renderTheme(body);
        }
        element.appendChild(element2);
        element.appendChild(body);
        content.appendChild(element);
        this.document.markDirty(content, 15);
    }

    private void selectNode(UUID id) {
        this.session.select(id);
        this.renderCanvas();
        this.renderBreadcrumb();
        if (this.session.mode() == OreEditorSession.Mode.INSPECT) {
            this.renderMode();
        }
    }

    private void renderCanvas() {
        if (this.canvasRenderer != null) {
            this.canvasRenderer.render(this.project, this.session.selectedNode(), this.hoveredNode);
        }
    }

    private void updateCanvasHover(double x, double y) {
        UUID next;
        if (this.drag.active() || this.canvasRenderer == null || this.document == null) {
            return;
        }
        Element canvas = this.document.querySelector("#editorCanvas");
        if (canvas == null) {
            return;
        }
        Element.DOMRect rect = canvas.getBoundingClientRect();
        UUID uUID = next = x < rect.left || x > rect.right || y < rect.top || y > rect.bottom ? null : this.hitTester.hit(this.canvasRenderer.elements(), x, y);
        if (Objects.equals(this.hoveredNode, next)) {
            return;
        }
        this.hoveredNode = next;
        this.renderCanvas();
    }

    private void renderBreadcrumb() {
        if (this.document == null) {
            return;
        }
        Element breadcrumb = this.document.querySelector("#editorBreadcrumb");
        if (breadcrumb == null) {
            return;
        }
        for (Node node : new ArrayList<Element>(breadcrumb.children)) {
            node.remove();
        }
        ArrayList<OreContainerNode> arrayList = new ArrayList<OreContainerNode>();
        for (OreContainerNode node = this.session.selectedNode() == null ? this.project.root() : this.project.find(this.session.selectedNode()); node != null; node = node.parent()) {
            arrayList.add(0, node);
        }
        for (int index = 0; index < arrayList.size(); ++index) {
            OreContainerNode container;
            OreCanvasNode entry;
            if (index > 0) {
                breadcrumb.appendChild(this.document.createTextNode(" / "));
            }
            String key = (entry = (OreCanvasNode)arrayList.get(index)) instanceof OreContainerNode && (container = (OreContainerNode)entry).isRoot() ? "ore_editor.apricityui.breadcrumb.canvas" : (entry instanceof OreContainerNode ? "ore_editor.apricityui.breadcrumb.container" : "ore_editor.apricityui.breadcrumb.component");
            breadcrumb.appendChild(OreEditorDom.translation(this.document, key, null));
        }
        this.document.markDirty(breadcrumb, 15);
    }

    private void updateDocumentState() {
        if (this.document == null) {
            return;
        }
        Element state = this.document.querySelector(".editor-document-state");
        if (state == null) {
            return;
        }
        for (Node node : new ArrayList<Element>(state.children)) {
            node.remove();
        }
        if (this.openedHtmlPath == null) {
            state.appendChild(OreEditorDom.translation(this.document, "ore_editor.apricityui.document.untitled", null));
        } else {
            state.appendChild(this.document.createTextNode(this.openedHtmlPath.getFileName().toString()));
        }
        if (this.session.dirty()) {
            Element dirty = Element.init(this.document.createElement("SPAN"));
            dirty.setAttribute("class", "editor-document-dirty");
            dirty.appendChild(OreEditorDom.translation(this.document, "ore_editor.apricityui.document.modified", null));
            state.appendChild(dirty);
        }
        this.document.markDirty(state, 7);
    }

    private void renderPalette(Element body) {
        Element switcher = Element.init(this.document.createElement("DIV"));
        switcher.setAttribute("class", "editor-segmented editor-palette-switcher");
        switcher.setAttribute("role", "group");
        this.appendPaletteModeButton(switcher, true, "ore_editor.apricityui.palette.containers");
        this.appendPaletteModeButton(switcher, false, "ore_editor.apricityui.palette.components");
        body.appendChild(switcher);
        Element items = Element.init(this.document.createElement("DIV"));
        items.setAttribute("class", "editor-palette-items");
        for (OreComponentDefinition definition : OreComponentRegistry.definitions()) {
            if (definition.container() != this.showingPaletteContainers) continue;
            Element item = Element.init(this.document.createElement("BUTTON"));
            item.setAttribute("class", "button button-secondary button-small editor-palette-item");
            item.setAttribute("type", "button");
            item.appendChild(OreEditorDom.translation(this.document, definition.nameKey(), null));
            Tooltip.bindTranslation(item, definition.descriptionKey());
            item.addEventListener("mousedown", event -> {
                if (!(event instanceof MouseEvent)) {
                    return;
                }
                MouseEvent mouseEvent = (MouseEvent)event;
                this.beginPaletteDrag(definition, mouseEvent.clientX, mouseEvent.clientY);
                event.preventDefault();
                event.stopPropagation();
            });
            items.appendChild(item);
        }
        body.appendChild(items);
    }

    private void appendPaletteModeButton(Element parent, boolean containers, String labelKey) {
        Element button = Element.init(this.document.createElement("BUTTON"));
        boolean active = this.showingPaletteContainers == containers;
        button.setAttribute("class", active ? "button button-primary button-small" : "button button-secondary button-small");
        button.setAttribute("type", "button");
        button.setAttribute("aria-pressed", Boolean.toString(active));
        button.appendChild(OreEditorDom.translation(this.document, labelKey, null));
        button.addEventListener("click", event -> {
            if (this.showingPaletteContainers == containers) {
                return;
            }
            this.showingPaletteContainers = containers;
            this.renderMode();
        });
        parent.appendChild(button);
    }

    private void renderInspector(Element body) {
        OreCanvasNode node = this.project.find(this.session.selectedNode());
        if (node == null) {
            body.appendChild(OreEditorDom.translation(this.document, "ore_editor.apricityui.empty.inspect", null));
            return;
        }
        if (node.locked() && node != this.project.root()) {
            this.appendNodeActions(body, node);
            return;
        }
        if (node instanceof OreContainerNode) {
            OreContainerNode container = (OreContainerNode)node;
            this.renderContainerInspector(body, container);
        } else if (node instanceof OreComponentNode) {
            OreComponentNode component = (OreComponentNode)node;
            this.renderComponentInspector(body, component);
        }
    }

    private void renderTheme(Element body) {
        for (ThemeGroup group : THEME_GROUPS) {
            Element section = Element.init(this.document.createElement("FIELDSET"));
            section.setAttribute("class", "editor-theme-group");
            Element header = Element.init(this.document.createElement("DIV"));
            header.setAttribute("class", "editor-theme-group-header");
            header.appendChild(OreEditorDom.translation(this.document, "ore_editor.apricityui.theme_group." + group.translationSuffix(), null));
            Element resetGroup = Element.init(this.document.createElement("BUTTON"));
            resetGroup.setAttribute("class", "button button-secondary button-small");
            resetGroup.setAttribute("type", "button");
            resetGroup.appendChild(OreEditorDom.translation(this.document, "ore_editor.apricityui.action.reset_group", null));
            resetGroup.addEventListener("click", event -> {
                Map<String, String> before = this.project.theme().overrides();
                for (String token : group.tokens()) {
                    this.project.theme().set(token, null);
                }
                Map<String, String> after = this.project.theme().overrides();
                if (before.equals(after)) {
                    return;
                }
                this.updateProject();
                this.commitHistory(OreEditorHistory.action("ResetThemeGroup", this.session.selectedNode(), this.session.selectedNode(), () -> this.applyTheme(before), () -> this.applyTheme(after)));
                this.renderMode();
            });
            header.appendChild(resetGroup);
            section.appendChild(header);
            for (String name : group.tokens()) {
                ThemeToken token = OreEditorController.themeToken(name);
                if (token == null) continue;
                String current = this.project.theme().get(token.name());
                this.appendThemeTokenInput(section, token, current == null ? token.defaultValue() : current);
            }
            body.appendChild(section);
        }
        Element reset = Element.init(this.document.createElement("BUTTON"));
        reset.setAttribute("class", "button button-secondary button-small");
        reset.setAttribute("type", "button");
        reset.appendChild(OreEditorDom.translation(this.document, "ore_editor.apricityui.action.reset_theme", null));
        reset.addEventListener("click", event -> {
            Map<String, String> before = this.project.theme().overrides();
            this.project.theme().reset();
            if (before.isEmpty()) {
                return;
            }
            this.updateProject();
            this.commitHistory(OreEditorHistory.action("ResetThemeGroup", this.session.selectedNode(), this.session.selectedNode(), () -> this.applyTheme(before), () -> this.project.theme().reset()));
            this.renderMode();
        });
        body.appendChild(reset);
    }

    private void renderContainerInspector(Element body, OreContainerNode container) {
        this.appendSegmented(body, "ore_editor.apricityui.property.flex_direction", container.flex().direction(), value -> this.updateContainer(container, () -> container.flex().setDirection((String)value)), "row", "row-reverse", "column", "column-reverse");
        this.appendSegmented(body, "ore_editor.apricityui.property.flex_wrap", container.flex().wrap(), value -> this.updateContainer(container, () -> container.flex().setWrap((String)value)), "nowrap", "wrap", "wrap-reverse");
        this.appendAlignmentSelect(body, "ore_editor.apricityui.property.justify_content", "justify", container.flex().justifyContent(), value -> this.updateContainer(container, () -> container.flex().setJustifyContent((String)value)), "flex-start", "center", "flex-end", "space-between", "space-around", "space-evenly");
        this.appendAlignmentSelect(body, "ore_editor.apricityui.property.align_items", "items", container.flex().alignItems(), value -> this.updateContainer(container, () -> container.flex().setAlignItems((String)value)), "stretch", "flex-start", "center", "flex-end", "baseline");
        this.appendAlignmentSelect(body, "ore_editor.apricityui.property.align_content", "content", container.flex().alignContent(), value -> this.updateContainer(container, () -> container.flex().setAlignContent((String)value)), "stretch", "flex-start", "center", "flex-end", "space-between", "space-around", "space-evenly");
        this.appendLengthStyleInput(body, container, "ore_editor.apricityui.property.gap", "gap", container.flex().gap(), value -> this.updateContainer(container, () -> container.flex().setGap((String)value)));
        this.appendLengthStyleInput(body, container, "ore_editor.apricityui.property.row_gap", "row-gap", container.flex().rowGap(), value -> this.updateContainer(container, () -> container.flex().setRowGap((String)value)));
        this.appendLengthStyleInput(body, container, "ore_editor.apricityui.property.column_gap", "column-gap", container.flex().columnGap(), value -> this.updateContainer(container, () -> container.flex().setColumnGap((String)value)));
        this.appendLengthStyleInput(body, container, "ore_editor.apricityui.property.width", "width", container.style().get("width"), null);
        this.appendLengthStyleInput(body, container, "ore_editor.apricityui.property.height", "height", container.style().get("height"), null);
        this.appendLengthStyleInput(body, container, "ore_editor.apricityui.property.min_width", "min-width", container.style().get("min-width"), null);
        this.appendLengthStyleInput(body, container, "ore_editor.apricityui.property.min_height", "min-height", container.style().get("min-height"), null);
        this.appendLengthStyleInput(body, container, "ore_editor.apricityui.property.max_width", "max-width", container.style().get("max-width"), null);
        this.appendLengthStyleInput(body, container, "ore_editor.apricityui.property.max_height", "max-height", container.style().get("max-height"), null);
        this.appendBoxModelField(body, container, "padding", "ore_editor.apricityui.property.padding");
        this.appendStyleInput(body, container, "ore_editor.apricityui.property.background", "background", container.style().get("background"), null);
        this.appendSelect(body, "ore_editor.apricityui.property.overflow", container.style().get("overflow"), value -> this.updateNodeStyle(container, "overflow", (String)value), "visible", "hidden", "auto", "scroll");
        this.appendSelect(body, "ore_editor.apricityui.property.overflow_x", container.style().get("overflow-x"), value -> this.updateNodeStyle(container, "overflow-x", (String)value), "visible", "hidden", "auto", "scroll");
        this.appendSelect(body, "ore_editor.apricityui.property.overflow_y", container.style().get("overflow-y"), value -> this.updateNodeStyle(container, "overflow-y", (String)value), "visible", "hidden", "auto", "scroll");
        this.appendNodeActions(body, container);
    }

    private void renderComponentInspector(Element body, OreComponentNode component) {
        this.appendInput(body, "ore_editor.apricityui.property.content", component.content(), value -> this.updateContent(component, (String)value));
        this.appendSelect(body, "ore_editor.apricityui.property.visual_state", this.editingVisualState.name().toLowerCase(), value -> {
            this.editingVisualState = OreComponentNode.VisualState.valueOf(value.toUpperCase());
            this.renderMode();
        }, "default", "hover", "active", "focus", "disabled");
        Element stateStatus = Element.init(this.document.createElement("DIV"));
        boolean overridden = OreEditorController.hasStateOverride(component, this.editingVisualState);
        stateStatus.setAttribute("class", overridden ? "badge badge-purple editor-state-status" : "badge editor-state-status");
        stateStatus.appendChild(OreEditorDom.translation(this.document, this.editingVisualState == OreComponentNode.VisualState.DEFAULT ? "ore_editor.apricityui.state.base" : (overridden ? "ore_editor.apricityui.state.overridden" : "ore_editor.apricityui.state.no_override"), null));
        body.appendChild(stateStatus);
        this.appendNumberStyleInput(body, component, "ore_editor.apricityui.property.order", "order", component.style().get("order"), null, null, 1.0);
        this.appendNumberStyleInput(body, component, "ore_editor.apricityui.property.flex_grow", "flex-grow", component.style().get("flex-grow"), 0.0, null, 0.1);
        this.appendNumberStyleInput(body, component, "ore_editor.apricityui.property.flex_shrink", "flex-shrink", component.style().get("flex-shrink"), 0.0, null, 0.1);
        this.appendLengthStyleInput(body, component, "ore_editor.apricityui.property.flex_basis", "flex-basis", component.style().get("flex-basis"), null);
        this.appendSelect(body, "ore_editor.apricityui.property.align_self", component.style().get("align-self"), value -> this.updateNodeStyle(component, "align-self", (String)value), "auto", "stretch", "flex-start", "center", "flex-end", "baseline");
        this.appendSelect(body, "ore_editor.apricityui.property.position", component.absolute() ? "absolute" : "static", value -> this.toggleAbsolute(component, "absolute".equals(value)), "static", "absolute");
        Element left = this.appendLengthStyleInput(body, component, "ore_editor.apricityui.property.left", "left", component.style().get("left"), value -> this.updateAbsoluteOffset(component, "left", (String)value));
        Element right = this.appendLengthStyleInput(body, component, "ore_editor.apricityui.property.right", "right", component.style().get("right"), value -> this.updateAbsoluteOffset(component, "right", (String)value));
        Element top = this.appendLengthStyleInput(body, component, "ore_editor.apricityui.property.top", "top", component.style().get("top"), value -> this.updateAbsoluteOffset(component, "top", (String)value));
        Element bottom = this.appendLengthStyleInput(body, component, "ore_editor.apricityui.property.bottom", "bottom", component.style().get("bottom"), value -> this.updateAbsoluteOffset(component, "bottom", (String)value));
        if (!component.absolute()) {
            OreEditorController.disableField(left, "ore_editor.apricityui.disabled.absolute_offsets");
            OreEditorController.disableField(right, "ore_editor.apricityui.disabled.absolute_offsets");
            OreEditorController.disableField(top, "ore_editor.apricityui.disabled.absolute_offsets");
            OreEditorController.disableField(bottom, "ore_editor.apricityui.disabled.absolute_offsets");
        }
        this.appendLengthStyleInput(body, component, "ore_editor.apricityui.property.width", "width", component.style().get("width"), null);
        this.appendLengthStyleInput(body, component, "ore_editor.apricityui.property.height", "height", component.style().get("height"), null);
        this.appendLengthStyleInput(body, component, "ore_editor.apricityui.property.min_width", "min-width", component.style().get("min-width"), null);
        this.appendLengthStyleInput(body, component, "ore_editor.apricityui.property.min_height", "min-height", component.style().get("min-height"), null);
        this.appendLengthStyleInput(body, component, "ore_editor.apricityui.property.max_width", "max-width", component.style().get("max-width"), null);
        this.appendLengthStyleInput(body, component, "ore_editor.apricityui.property.max_height", "max-height", component.style().get("max-height"), null);
        this.appendNumberStyleInput(body, component, "ore_editor.apricityui.property.z_index", "z-index", component.style().get("z-index"), null, null, 1.0);
        this.appendBoxModelField(body, component, "margin", "ore_editor.apricityui.property.margin");
        this.appendBoxModelField(body, component, "padding", "ore_editor.apricityui.property.padding");
        this.appendComponentColorField(body, component, "ore_editor.apricityui.property.color", "color");
        this.appendComponentColorField(body, component, "ore_editor.apricityui.property.background", "background");
        this.appendComponentStateStyleInput(body, component, "ore_editor.apricityui.property.border", "border");
        this.appendComponentStateNumberField(body, component, "ore_editor.apricityui.property.opacity", "opacity", 0.0, 1.0, 0.05);
        this.appendComponentStateStyleInput(body, component, "ore_editor.apricityui.property.font_family", "font-family");
        this.appendComponentStateLengthField(body, component, "ore_editor.apricityui.property.font_size", "font-size");
        this.appendComponentStateLengthField(body, component, "ore_editor.apricityui.property.line_height", "line-height");
        this.appendComponentStateNumberField(body, component, "ore_editor.apricityui.property.font_weight", "font-weight", 100.0, 900.0, 100.0);
        this.appendComponentStateStyleSelect(body, component, "ore_editor.apricityui.property.text_align", "text-align", "left", "center", "right", "justify");
        this.appendShadowField(body, component);
        this.appendNodeActions(body, component);
    }

    private void appendNodeActions(Element body, OreCanvasNode node) {
        if (node == this.project.root() || node.parent() == null) {
            return;
        }
        Element actions = Element.init(this.document.createElement("DIV"));
        actions.setAttribute("class", "editor-inspector-actions");
        Element lock = Element.init(this.document.createElement("BUTTON"));
        lock.setAttribute("class", "button button-secondary button-small");
        lock.setAttribute("type", "button");
        lock.appendChild(OreEditorDom.translation(this.document, node.locked() ? "ore_editor.apricityui.action.unlock" : "ore_editor.apricityui.action.lock", null));
        lock.addEventListener("click", event -> {
            boolean before = node.locked();
            node.setLocked(!before);
            this.updateProject();
            this.commitHistory(OreEditorHistory.booleanValue("LockNode", node.id(), node.id(), before, !before, node::setLocked));
            this.renderMode();
        });
        actions.appendChild(lock);
        if (node.locked()) {
            body.appendChild(actions);
            return;
        }
        int index = node.parent().children().indexOf(node);
        Element moveUp = Element.init(this.document.createElement("BUTTON"));
        moveUp.setAttribute("class", "button button-secondary button-small");
        moveUp.setAttribute("type", "button");
        moveUp.setDisabled(index <= 0);
        moveUp.appendChild(OreEditorDom.translation(this.document, "ore_editor.apricityui.action.move_up", null));
        moveUp.addEventListener("click", event -> this.moveNodeSibling(node, -1));
        Element moveDown = Element.init(this.document.createElement("BUTTON"));
        moveDown.setAttribute("class", "button button-secondary button-small");
        moveDown.setAttribute("type", "button");
        moveDown.setDisabled(index < 0 || index + 1 >= node.parent().children().size());
        moveDown.appendChild(OreEditorDom.translation(this.document, "ore_editor.apricityui.action.move_down", null));
        moveDown.addEventListener("click", event -> this.moveNodeSibling(node, 1));
        Element moveOut = Element.init(this.document.createElement("BUTTON"));
        moveOut.setAttribute("class", "button button-secondary button-small");
        moveOut.setAttribute("type", "button");
        moveOut.setDisabled(node.parent().parent() == null);
        moveOut.appendChild(OreEditorDom.translation(this.document, "ore_editor.apricityui.action.move_out", null));
        moveOut.addEventListener("click", event -> this.moveNodeOut(node));
        Element duplicate = Element.init(this.document.createElement("BUTTON"));
        duplicate.setAttribute("class", "button button-secondary button-small");
        duplicate.appendChild(OreEditorDom.translation(this.document, "ore_editor.apricityui.action.duplicate", null));
        duplicate.addEventListener("click", event -> this.duplicateNode(node));
        Element delete = Element.init(this.document.createElement("BUTTON"));
        delete.setAttribute("class", "button button-danger button-small");
        delete.appendChild(OreEditorDom.translation(this.document, "ore_editor.apricityui.action.delete", null));
        delete.addEventListener("click", event -> this.deleteNode(node));
        actions.appendChild(moveUp);
        actions.appendChild(moveDown);
        actions.appendChild(moveOut);
        actions.appendChild(duplicate);
        actions.appendChild(delete);
        body.appendChild(actions);
    }

    static boolean hasStateOverride(OreComponentNode component, OreComponentNode.VisualState state) {
        return component != null && state != null && state != OreComponentNode.VisualState.DEFAULT && component.stateStyles().containsKey((Object)state) && !component.stateStyles().get((Object)state).properties().isEmpty();
    }

    private void duplicateNode(OreCanvasNode node) {
        OreContainerNode parent = node.parent();
        if (node.locked() || parent == null || OreEditorController.structureLocked(parent)) {
            return;
        }
        OreCanvasNode copy = this.copyNode(node);
        int index = parent.children().indexOf(node) + 1;
        parent.insert(index, copy);
        this.session.select(copy.id());
        this.updateProject();
        this.commitHistory(OreEditorHistory.action("DuplicateNode", node.id(), copy.id(), () -> parent.remove(copy), () -> parent.insert(index, copy)));
        this.renderMode();
        this.renderBreadcrumb();
    }

    private void deleteNode(OreCanvasNode node) {
        OreContainerNode parent = node.parent();
        if (node.locked() || parent == null || OreEditorController.structureLocked(parent)) {
            return;
        }
        int index = parent.children().indexOf(node);
        parent.remove(node);
        this.session.select(parent.id());
        this.updateProject();
        this.commitHistory(OreEditorHistory.action("RemoveNode", node.id(), parent.id(), () -> parent.insert(index, node), () -> parent.remove(node)));
        this.renderMode();
        this.renderBreadcrumb();
    }

    private void moveNodeSibling(OreCanvasNode node, int offset) {
        OreContainerNode parent;
        OreContainerNode oreContainerNode = parent = node == null ? null : node.parent();
        if (node == null || node.locked() || parent == null || OreEditorController.structureLocked(parent)) {
            return;
        }
        int beforeIndex = parent.children().indexOf(node);
        int afterIndex = beforeIndex + offset;
        if (beforeIndex < 0 || afterIndex < 0 || afterIndex >= parent.children().size()) {
            return;
        }
        parent.remove(node);
        parent.insert(afterIndex, node);
        this.session.select(node.id());
        this.updateProject();
        this.commitHistory(OreEditorHistory.action("MoveNode", node.id(), node.id(), () -> {
            parent.remove(node);
            parent.insert(beforeIndex, node);
        }, () -> {
            parent.remove(node);
            parent.insert(afterIndex, node);
        }));
        this.renderMode();
        this.renderBreadcrumb();
    }

    private void moveNodeOut(OreCanvasNode node) {
        OreContainerNode target;
        OreContainerNode source = node == null ? null : node.parent();
        OreContainerNode oreContainerNode = target = source == null ? null : source.parent();
        if (node == null || node.locked() || source == null || OreEditorController.structureLocked(source) || target == null || OreEditorController.structureLocked(target)) {
            return;
        }
        int sourceIndex = source.children().indexOf(node);
        int targetIndex = target.children().indexOf(source) + 1;
        if (sourceIndex < 0 || targetIndex <= 0) {
            return;
        }
        source.remove(node);
        target.insert(targetIndex, node);
        this.session.select(node.id());
        this.updateProject();
        this.commitHistory(OreEditorHistory.action("ReparentNode", node.id(), node.id(), () -> {
            target.remove(node);
            source.insert(sourceIndex, node);
        }, () -> {
            source.remove(node);
            target.insert(targetIndex, node);
        }));
        this.renderMode();
        this.renderBreadcrumb();
    }

    private OreCanvasNode copyNode(OreCanvasNode node) {
        OreCanvasNode copy;
        if (node instanceof OreContainerNode) {
            OreContainerNode source = (OreContainerNode)node;
            OreContainerNode container = new OreContainerNode(false);
            container.setTag(source.tag());
            container.flex().setDirection(source.flex().direction());
            container.flex().setWrap(source.flex().wrap());
            container.flex().setJustifyContent(source.flex().justifyContent());
            container.flex().setAlignItems(source.flex().alignItems());
            container.flex().setAlignContent(source.flex().alignContent());
            container.flex().setGap(source.flex().gap());
            container.flex().setRowGap(source.flex().rowGap());
            container.flex().setColumnGap(source.flex().columnGap());
            for (OreCanvasNode child : source.children()) {
                container.add(this.copyNode(child));
            }
            copy = container;
        } else if (node instanceof OreComponentNode) {
            OreComponentNode source = (OreComponentNode)node;
            OreComponentNode component = new OreComponentNode(source.type(), source.content());
            if (source.absolute()) {
                component.enterAbsolute(source.flowIndex());
            }
            if (source.hasFlowStyleSnapshot()) {
                component.setFlowStyleSnapshot(source.flowStyleSnapshot());
            }
            source.stateStyles().forEach((state, style) -> style.properties().forEach((key, value) -> component.stateStyle((OreComponentNode.VisualState)((Object)state)).set((String)key, (String)value)));
            copy = component;
        } else {
            throw new IllegalArgumentException("Unknown canvas node");
        }
        node.style().properties().forEach(copy.style()::set);
        node.attributes().forEach(copy::setAttribute);
        copy.setLocked(node.locked());
        return copy;
    }

    private void appendStyleInput(Element body, OreCanvasNode node, String label, String property, String value, Consumer<String> override) {
        this.appendInput(body, label, value, next -> {
            if (override != null) {
                override.accept((String)next);
            } else {
                this.updateNodeStyle(node, property, (String)next);
            }
        }, this::validCssValue);
    }

    private Element appendLengthStyleInput(Element body, OreCanvasNode node, String label, String property, String value, Consumer<String> override) {
        return this.appendLengthField(body, label, value, OreEditorController.allowsAuto(property), next -> {
            if (override != null) {
                override.accept((String)next);
            } else {
                this.updateNodeStyle(node, property, (String)next);
            }
        });
    }

    private void appendNumberStyleInput(Element body, OreCanvasNode node, String label, String property, String value, Double min, Double max, double step) {
        this.appendNumberField(body, label, value, min, max, step, next -> this.updateNodeStyle(node, property, (String)next));
    }

    private void appendComponentStateStyleInput(Element body, OreComponentNode component, String label, String property) {
        this.appendInput(body, label, component.stateStyle(this.editingVisualState).get(property), next -> {
            OreComponentNode.VisualState state = this.editingVisualState;
            String before = component.stateStyle(state).get(property);
            if (OreEditorController.same(before, next)) {
                return;
            }
            component.stateStyle(state).set(property, (String)next);
            this.updateProject();
            this.commitHistory(OreEditorHistory.stringValue("UpdateComponentProperty", this.history.activeMergeKey(), component.id(), component.id(), before, next, value -> component.stateStyle(state).set(property, (String)value)));
        }, this::validCssValue);
    }

    private void appendComponentStateLengthField(Element body, OreComponentNode component, String label, String property) {
        OreComponentNode.VisualState state = this.editingVisualState;
        this.appendLengthField(body, label, component.stateStyle(state).get(property), OreEditorController.allowsAuto(property), next -> this.updateComponentStateStyle(component, state, property, (String)next));
    }

    private void appendComponentStateNumberField(Element body, OreComponentNode component, String label, String property, Double min, Double max, double step) {
        OreComponentNode.VisualState state = this.editingVisualState;
        this.appendNumberField(body, label, component.stateStyle(state).get(property), min, max, step, next -> this.updateComponentStateStyle(component, state, property, (String)next));
    }

    private void appendComponentColorField(Element body, OreComponentNode component, String labelKey, String property) {
        Element group = Element.init(this.document.createElement("DIV"));
        group.setAttribute("class", "form-group editor-form-group");
        Element label = Element.init(this.document.createElement("LABEL"));
        label.setAttribute("class", "form-label");
        label.appendChild(OreEditorDom.translation(this.document, labelKey, null));
        Element controls = Element.init(this.document.createElement("DIV"));
        controls.setAttribute("class", "editor-color-field");
        OreComponentNode.VisualState state = this.editingVisualState;
        String current = component.stateStyle(state).get(property);
        ColorValue initial = ColorValue.parse(current);
        Element input = Element.init(this.document.createElement("INPUT"));
        input.setAttribute("class", "form-input");
        input.setAttribute("type", "text");
        input.setValue(current == null ? "" : current);
        Element color = Element.init(this.document.createElement("INPUT"));
        color.setAttribute("class", "editor-component-color");
        color.setAttribute("type", "color");
        color.setValue(initial.hex());
        Element alpha = Element.init(this.document.createElement("INPUT"));
        alpha.setAttribute("class", "editor-component-alpha");
        alpha.setAttribute("type", "range");
        alpha.setAttribute("min", "0");
        alpha.setAttribute("max", "1");
        alpha.setAttribute("step", "0.01");
        alpha.setValue(OreEditorController.formatNumber(initial.alpha()));
        alpha.setAttribute("data-tooltip-key", "ore_editor.apricityui.property.alpha");
        input.addEventListener("focus", event -> this.history.beginMerge("component-color:" + System.identityHashCode(input)));
        input.addEventListener("blur", event -> this.history.endMerge());
        input.addEventListener("change", event -> {
            String next = input.getValue();
            if (!OreEditorController.validCssColor(next)) {
                input.setAttribute("class", "form-input is-invalid");
                Tooltip.bindTranslation(input, "ore_editor.apricityui.validation.color");
                return;
            }
            input.setAttribute("class", "form-input");
            ColorValue parsed = ColorValue.parse(next);
            color.setValue(parsed.hex());
            alpha.setValue(OreEditorController.formatNumber(parsed.alpha()));
            this.updateComponentStateStyle(component, state, property, next);
        });
        Runnable commitPickerColor = () -> {
            double opacity = OreEditorController.validNumber(alpha.getValue()) ? Double.parseDouble(alpha.getValue()) : 1.0;
            String next = ColorValue.toCss(color.getValue(), opacity);
            input.setValue(next);
            this.updateComponentStateStyle(component, state, property, next);
        };
        color.addEventListener("change", event -> commitPickerColor.run());
        alpha.addEventListener("focus", event -> this.history.beginMerge("component-color:" + System.identityHashCode(input)));
        alpha.addEventListener("blur", event -> this.history.endMerge());
        alpha.addEventListener("change", event -> commitPickerColor.run());
        controls.appendChild(input);
        controls.appendChild(color);
        controls.appendChild(alpha);
        group.appendChild(label);
        group.appendChild(controls);
        body.appendChild(group);
    }

    private void appendShadowField(Element body, OreComponentNode component) {
        OreComponentNode.VisualState state = this.editingVisualState;
        ShadowValue initial = OreEditorController.parseShadow(component.stateStyle(state).get("box-shadow"));
        Element group = Element.init(this.document.createElement("FIELDSET"));
        group.setAttribute("class", "editor-shadow-field");
        Element legend = Element.init(this.document.createElement("LEGEND"));
        legend.setAttribute("class", "form-label");
        legend.appendChild(OreEditorDom.translation(this.document, "ore_editor.apricityui.property.shadow", null));
        group.appendChild(legend);
        Element insetChoice = Element.init(this.document.createElement("LABEL"));
        insetChoice.setAttribute("class", "choice");
        Element inset = Element.init(this.document.createElement("INPUT"));
        inset.setAttribute("type", "checkbox");
        inset.setChecked(initial.inset());
        insetChoice.appendChild(inset);
        insetChoice.appendChild(OreEditorDom.translation(this.document, "ore_editor.apricityui.property.shadow_inset", null));
        group.appendChild(insetChoice);
        Element fields = Element.init(this.document.createElement("DIV"));
        fields.setAttribute("class", "editor-shadow-fields");
        ArrayList<Element> values = new ArrayList<Element>();
        values.add(this.appendShadowInput(fields, "ore_editor.apricityui.property.shadow_offset_x", initial.offsetX()));
        values.add(this.appendShadowInput(fields, "ore_editor.apricityui.property.shadow_offset_y", initial.offsetY()));
        values.add(this.appendShadowInput(fields, "ore_editor.apricityui.property.shadow_blur", initial.blur()));
        values.add(this.appendShadowInput(fields, "ore_editor.apricityui.property.shadow_spread", initial.spread()));
        values.add(this.appendShadowInput(fields, "ore_editor.apricityui.property.shadow_color", initial.color()));
        Runnable commit = () -> {
            String color = ((Element)values.get(4)).getValue();
            if (color == null || color.isBlank()) {
                this.updateComponentStateStyle(component, state, "box-shadow", "");
                return;
            }
            if (!(OreEditorController.validCssColor(color) && this.validCssValue(((Element)values.get(0)).getValue()) && this.validCssValue(((Element)values.get(1)).getValue()) && this.validCssValue(((Element)values.get(2)).getValue()) && this.validCssValue(((Element)values.get(3)).getValue()))) {
                return;
            }
            ShadowValue next = new ShadowValue(inset.isChecked(), ((Element)values.get(0)).getValue(), ((Element)values.get(1)).getValue(), ((Element)values.get(2)).getValue(), ((Element)values.get(3)).getValue(), color);
            this.updateComponentStateStyle(component, state, "box-shadow", next.toCss());
        };
        inset.addEventListener("change", event -> commit.run());
        for (Element input : values) {
            input.addEventListener("change", event -> commit.run());
        }
        group.appendChild(fields);
        body.appendChild(group);
    }

    private Element appendShadowInput(Element parent, String labelKey, String value) {
        Element field = Element.init(this.document.createElement("DIV"));
        field.setAttribute("class", "editor-shadow-input");
        Element label = Element.init(this.document.createElement("LABEL"));
        label.setAttribute("class", "form-label");
        label.appendChild(OreEditorDom.translation(this.document, labelKey, null));
        Element input = Element.init(this.document.createElement("INPUT"));
        input.setAttribute("class", "form-input");
        input.setAttribute("type", "text");
        input.setValue(value);
        field.appendChild(label);
        field.appendChild(input);
        parent.appendChild(field);
        return input;
    }

    private void updateComponentStateStyle(OreComponentNode component, OreComponentNode.VisualState state, String property, String value) {
        String before = component.stateStyle(state).get(property);
        if (OreEditorController.same(before, value)) {
            return;
        }
        component.stateStyle(state).set(property, value);
        this.updateProject();
        this.commitHistory(OreEditorHistory.stringValue("UpdateComponentProperty", this.history.activeMergeKey(), component.id(), component.id(), before, value, next -> component.stateStyle(state).set(property, (String)next)));
    }

    private void appendComponentStateStyleSelect(Element body, OreComponentNode component, String label, String property, String ... values) {
        OreComponentNode.VisualState state = this.editingVisualState;
        this.appendSelect(body, label, component.stateStyle(state).get(property), value -> {
            String before = component.stateStyle(state).get(property);
            if (OreEditorController.same(before, value)) {
                return;
            }
            component.stateStyle(state).set(property, (String)value);
            this.updateProject();
            this.commitHistory(OreEditorHistory.stringValue("UpdateComponentProperty", this.history.activeMergeKey(), component.id(), component.id(), before, value, next -> component.stateStyle(state).set(property, (String)next)));
        }, values);
    }

    private void appendBoxModelField(Element body, OreCanvasNode node, String property, String labelKey) {
        BoxValue initial = BoxValue.of(node, property);
        Element group = Element.init(this.document.createElement("FIELDSET"));
        group.setAttribute("class", "editor-box-field");
        Element legend = Element.init(this.document.createElement("LEGEND"));
        legend.setAttribute("class", "form-label");
        legend.appendChild(OreEditorDom.translation(this.document, labelKey, null));
        group.appendChild(legend);
        Element linkedChoice = Element.init(this.document.createElement("LABEL"));
        linkedChoice.setAttribute("class", "choice editor-box-link");
        Element linked = Element.init(this.document.createElement("INPUT"));
        linked.setAttribute("type", "checkbox");
        linked.setChecked(false);
        linkedChoice.appendChild(linked);
        linkedChoice.appendChild(OreEditorDom.translation(this.document, "ore_editor.apricityui.property.box_link", null));
        group.appendChild(linkedChoice);
        Element fields = Element.init(this.document.createElement("DIV"));
        fields.setAttribute("class", "editor-box-fields");
        String[] sides = new String[]{"top", "right", "bottom", "left"};
        String[] values = new String[]{initial.top(), initial.right(), initial.bottom(), initial.left()};
        for (int index = 0; index < sides.length; ++index) {
            String side = sides[index];
            Element field = Element.init(this.document.createElement("DIV"));
            field.setAttribute("class", "editor-box-input");
            field.setAttribute("data-editor-box-side", side);
            Element label = Element.init(this.document.createElement("LABEL"));
            label.setAttribute("class", "form-label");
            label.appendChild(OreEditorDom.translation(this.document, "ore_editor.apricityui.property.box_" + side, null));
            field.appendChild(label);
            this.appendLengthControls(field, values[index], "margin".equals(property), next -> {
                if (linked.isChecked()) {
                    this.updateBoxModelSides(node, property, (String)next);
                } else {
                    this.updateNodeStyle(node, property + "-" + side, (String)next);
                }
            }, "box:" + node.id() + ":" + side);
            fields.appendChild(field);
        }
        group.appendChild(fields);
        body.appendChild(group);
    }

    private void updateBoxModelSides(OreCanvasNode node, String property, String value) {
        LinkedHashMap<CallSite, String> before = new LinkedHashMap<CallSite, String>();
        LinkedHashMap<String, String> after = new LinkedHashMap<String, String>();
        for (String side : List.of("top", "right", "bottom", "left")) {
            String key = property + "-" + side;
            before.put((CallSite)((Object)key), node.style().get(key));
            after.put(key, value);
        }
        if (before.equals(after)) {
            return;
        }
        OreEditorController.applyNodeStyles(node, after);
        this.updateProject();
        this.commitHistory(OreEditorHistory.action("UpdateComponentProperty", this.history.activeMergeKey(), node.id(), node.id(), () -> OreEditorController.applyNodeStyles(node, before), () -> OreEditorController.applyNodeStyles(node, after)));
    }

    private static void applyNodeStyles(OreCanvasNode node, Map<String, String> styles) {
        styles.forEach(node.style()::set);
    }

    private void appendInput(Element body, String labelKey, String value, Consumer<String> changed) {
        this.appendInput(body, labelKey, value, changed, ignored -> true);
    }

    private Element appendLengthField(Element body, String labelKey, String value, boolean autoAllowed, Consumer<String> changed) {
        Element group = Element.init(this.document.createElement("DIV"));
        group.setAttribute("class", "form-group editor-form-group");
        Element label = Element.init(this.document.createElement("LABEL"));
        label.setAttribute("class", "form-label");
        label.appendChild(OreEditorDom.translation(this.document, labelKey, null));
        group.appendChild(label);
        this.appendLengthControls(group, value, autoAllowed, changed, "length:" + System.identityHashCode(group));
        body.appendChild(group);
        return group;
    }

    private static void disableField(Element group, String tooltipKey) {
        if (group == null) {
            return;
        }
        for (Element control : group.querySelectorAll("input, select")) {
            control.setDisabled(true);
            Tooltip.bindTranslation(control, tooltipKey);
        }
    }

    private Element appendLengthControls(Element parent, String value, boolean autoAllowed, Consumer<String> changed, String mergeKey) {
        LengthValue initial = LengthValue.parse(value);
        Element controls = Element.init(this.document.createElement("DIV"));
        controls.setAttribute("class", autoAllowed ? "editor-length-field" : "editor-length-field editor-length-no-auto");
        Element input = Element.init(this.document.createElement("INPUT"));
        input.setAttribute("class", "form-input");
        input.setAttribute("type", "number");
        input.setAttribute("step", "any");
        input.setValue(initial.number());
        Element unit = Element.init(this.document.createElement("SELECT"));
        unit.setAttribute("class", "form-select editor-length-unit");
        for (String candidate : LengthValue.UNITS) {
            Element option = Element.init(this.document.createElement("OPTION"));
            option.setAttribute("value", candidate);
            option.setTextContent(candidate);
            unit.appendChild(option);
        }
        unit.setValue(initial.unit());
        Element mode = null;
        if (autoAllowed) {
            mode = Element.init(this.document.createElement("SELECT"));
            mode.setAttribute("class", "form-select editor-length-mode");
            this.appendLengthModeOption(mode, "value", "ore_editor.apricityui.value.value");
            this.appendLengthModeOption(mode, "auto", "ore_editor.apricityui.value.auto");
            mode.setValue(initial.auto() ? "auto" : "value");
        }
        Element modeControl = mode;
        Runnable commit = () -> {
            boolean auto = modeControl != null && "auto".equals(modeControl.getValue());
            input.setDisabled(auto);
            unit.setDisabled(auto);
            if (auto) {
                input.setAttribute("class", "form-input");
                changed.accept("auto");
                return;
            }
            String number = input.getValue();
            if (number == null || number.isBlank()) {
                input.setAttribute("class", "form-input");
                changed.accept("");
                return;
            }
            if (!OreEditorController.validNumber(number)) {
                input.setAttribute("class", "form-input is-invalid");
                Tooltip.bindTranslation(input, "ore_editor.apricityui.validation.css");
                return;
            }
            input.setAttribute("class", "form-input");
            changed.accept(number.trim() + unit.getValue());
        };
        input.setDisabled(initial.auto());
        unit.setDisabled(initial.auto());
        input.addEventListener("focus", event -> this.history.beginMerge(mergeKey));
        input.addEventListener("blur", event -> this.history.endMerge());
        input.addEventListener("keydown", event -> {
            if (event instanceof KeyEvent) {
                KeyEvent keyEvent = (KeyEvent)event;
                if (keyEvent.keyCode == 257) {
                    this.history.endMerge();
                }
            }
        });
        input.addEventListener("change", event -> commit.run());
        unit.addEventListener("change", event -> commit.run());
        if (mode != null) {
            mode.addEventListener("change", event -> commit.run());
        }
        controls.appendChild(input);
        controls.appendChild(unit);
        if (mode != null) {
            controls.appendChild(mode);
        }
        parent.appendChild(controls);
        return input;
    }

    private void appendLengthModeOption(Element mode, String value, String key) {
        Element option = Element.init(this.document.createElement("OPTION"));
        option.setAttribute("value", value);
        option.appendChild(OreEditorDom.translation(this.document, key, null));
        mode.appendChild(option);
    }

    private void appendNumberField(Element body, String labelKey, String value, Double min, Double max, double step, Consumer<String> changed) {
        Element group = Element.init(this.document.createElement("DIV"));
        group.setAttribute("class", "form-group editor-form-group");
        Element label = Element.init(this.document.createElement("LABEL"));
        label.setAttribute("class", "form-label");
        label.appendChild(OreEditorDom.translation(this.document, labelKey, null));
        Element input = Element.init(this.document.createElement("INPUT"));
        input.setAttribute("class", "form-input");
        input.setAttribute("type", "number");
        input.setAttribute("step", OreEditorController.formatNumber(step));
        if (min != null) {
            input.setAttribute("min", OreEditorController.formatNumber(min));
        }
        if (max != null) {
            input.setAttribute("max", OreEditorController.formatNumber(max));
        }
        input.setValue(value == null ? "" : value);
        String mergeKey = "number:" + System.identityHashCode(input);
        input.addEventListener("focus", event -> this.history.beginMerge(mergeKey));
        input.addEventListener("blur", event -> this.history.endMerge());
        input.addEventListener("keydown", event -> {
            if (event instanceof KeyEvent) {
                KeyEvent keyEvent = (KeyEvent)event;
                if (keyEvent.keyCode == 257) {
                    this.history.endMerge();
                }
            }
        });
        input.addEventListener("change", event -> {
            String next = input.getValue();
            if (next == null || next.isBlank()) {
                input.setAttribute("class", "form-input");
                changed.accept("");
                return;
            }
            if (!OreEditorController.validNumber(next) || min != null && Double.parseDouble(next) < min || max != null && Double.parseDouble(next) > max) {
                input.setAttribute("class", "form-input is-invalid");
                Tooltip.bindTranslation(input, "ore_editor.apricityui.validation.css");
                return;
            }
            input.setAttribute("class", "form-input");
            changed.accept(next.trim());
        });
        group.appendChild(label);
        group.appendChild(input);
        body.appendChild(group);
    }

    private static boolean validNumber(String value) {
        try {
            return value != null && !value.isBlank() && Double.isFinite(Double.parseDouble(value.trim()));
        }
        catch (NumberFormatException ignored) {
            return false;
        }
    }

    private static String formatNumber(double value) {
        return value == Math.rint(value) ? Long.toString((long)value) : Double.toString(value);
    }

    private static boolean allowsAuto(String property) {
        return "width".equals(property) || "height".equals(property) || "flex-basis".equals(property) || "left".equals(property) || "right".equals(property) || "top".equals(property) || "bottom".equals(property);
    }

    private void appendInput(Element body, String labelKey, String value, Consumer<String> changed, Predicate<String> valid) {
        Element group = Element.init(this.document.createElement("DIV"));
        group.setAttribute("class", "form-group editor-form-group");
        Element label = Element.init(this.document.createElement("LABEL"));
        label.setAttribute("class", "form-label");
        label.appendChild(OreEditorDom.translation(this.document, labelKey, null));
        Element input = Element.init(this.document.createElement("INPUT"));
        input.setAttribute("class", "form-input");
        input.setAttribute("type", "text");
        input.setValue(value == null ? "" : value);
        String mergeKey = "input:" + System.identityHashCode(input);
        input.addEventListener("focus", event -> this.history.beginMerge(mergeKey));
        input.addEventListener("blur", event -> this.history.endMerge());
        input.addEventListener("keydown", event -> {
            if (event instanceof KeyEvent) {
                KeyEvent keyEvent = (KeyEvent)event;
                if (keyEvent.keyCode == 257) {
                    this.history.endMerge();
                }
            }
        });
        input.addEventListener("change", event -> {
            String next = input.getValue();
            if (!valid.test(next)) {
                input.setAttribute("class", "form-input is-invalid");
                Tooltip.bindTranslation(input, "ore_editor.apricityui.validation.css");
                return;
            }
            input.setAttribute("class", "form-input");
            changed.accept(next);
        });
        group.appendChild(label);
        group.appendChild(input);
        body.appendChild(group);
    }

    private void appendSelect(Element body, String labelKey, String selected, Consumer<String> changed, String ... values) {
        Element group = Element.init(this.document.createElement("DIV"));
        group.setAttribute("class", "form-group editor-form-group");
        Element label = Element.init(this.document.createElement("LABEL"));
        label.setAttribute("class", "form-label");
        label.appendChild(OreEditorDom.translation(this.document, labelKey, null));
        Element select = Element.init(this.document.createElement("SELECT"));
        select.setAttribute("class", "form-select");
        for (String value : values) {
            Element option = Element.init(this.document.createElement("OPTION"));
            option.setAttribute("value", value);
            option.appendChild(OreEditorDom.translation(this.document, "ore_editor.apricityui.value." + value.replace('-', '_'), null));
            select.appendChild(option);
        }
        select.setValue(selected == null || selected.isBlank() ? values[0] : selected);
        select.addEventListener("change", event -> changed.accept(select.getValue()));
        group.appendChild(label);
        group.appendChild(select);
        body.appendChild(group);
    }

    private void appendAlignmentSelect(Element body, String labelKey, String axis, String selected, Consumer<String> changed, String ... values) {
        Element group = Element.init(this.document.createElement("DIV"));
        group.setAttribute("class", "form-group editor-form-group editor-alignment-field");
        Element label = Element.init(this.document.createElement("LABEL"));
        label.setAttribute("class", "form-label");
        label.appendChild(OreEditorDom.translation(this.document, labelKey, null));
        Element select = Element.init(this.document.createElement("SELECT"));
        select.setAttribute("class", "form-select");
        for (String value : values) {
            Element option = Element.init(this.document.createElement("OPTION"));
            option.setAttribute("value", value);
            option.appendChild(OreEditorDom.translation(this.document, "ore_editor.apricityui.value." + value.replace('-', '_'), null));
            select.appendChild(option);
        }
        String current = selected == null || selected.isBlank() ? values[0] : selected;
        select.setValue(current);
        Element preview = this.alignmentPreview(axis, current);
        select.addEventListener("change", event -> {
            String next = select.getValue();
            preview.setAttribute("class", OreEditorController.alignmentPreviewClass(axis, next));
            changed.accept(next);
        });
        group.appendChild(label);
        group.appendChild(select);
        group.appendChild(preview);
        body.appendChild(group);
    }

    private Element alignmentPreview(String axis, String value) {
        Element preview = Element.init(this.document.createElement("DIV"));
        preview.setAttribute("class", OreEditorController.alignmentPreviewClass(axis, value));
        preview.setAttribute("aria-hidden", "true");
        int markers = "content".equals(axis) ? 4 : 3;
        for (int index = 0; index < markers; ++index) {
            Element marker = Element.init(this.document.createElement("SPAN"));
            marker.setAttribute("class", "editor-alignment-marker");
            preview.appendChild(marker);
        }
        return preview;
    }

    private static String alignmentPreviewClass(String axis, String value) {
        String normalized = value == null || value.isBlank() ? "flex-start" : value;
        return "editor-alignment-preview editor-alignment-" + axis + " editor-alignment-value-" + normalized;
    }

    private void appendSegmented(Element body, String labelKey, String selected, Consumer<String> changed, String ... values) {
        Element group = Element.init(this.document.createElement("DIV"));
        group.setAttribute("class", "form-group editor-form-group");
        Element label = Element.init(this.document.createElement("LABEL"));
        label.setAttribute("class", "form-label");
        label.appendChild(OreEditorDom.translation(this.document, labelKey, null));
        Element options = Element.init(this.document.createElement("DIV"));
        options.setAttribute("class", "editor-segmented");
        options.setAttribute("role", "group");
        ArrayList<Element> buttons = new ArrayList<Element>();
        String current = selected == null || selected.isBlank() ? values[0] : selected;
        for (String value : values) {
            Element button = Element.init(this.document.createElement("BUTTON"));
            button.setAttribute("type", "button");
            button.setAttribute("data-editor-segmented-value", value);
            button.appendChild(OreEditorDom.translation(this.document, "ore_editor.apricityui.value." + value.replace('-', '_'), null));
            buttons.add(button);
            button.addEventListener("click", event -> {
                Iterator iterator = buttons.iterator();
                while (iterator.hasNext()) {
                    Element candidate;
                    boolean active = value.equals((candidate = (Element)iterator.next()).getAttribute("data-editor-segmented-value"));
                    candidate.setAttribute("class", active ? "button button-primary button-small" : "button button-secondary button-small");
                    candidate.setAttribute("aria-pressed", Boolean.toString(active));
                }
                changed.accept(value);
            });
            options.appendChild(button);
        }
        Iterator iterator = buttons.iterator();
        while (iterator.hasNext()) {
            Element button;
            boolean active = current.equals((button = (Element)iterator.next()).getAttribute("data-editor-segmented-value"));
            button.setAttribute("class", active ? "button button-primary button-small" : "button button-secondary button-small");
            button.setAttribute("aria-pressed", Boolean.toString(active));
        }
        group.appendChild(label);
        group.appendChild(options);
        body.appendChild(group);
    }

    private void updateContainer(OreContainerNode container, Runnable change) {
        FlexState before = FlexState.of(container);
        change.run();
        FlexState after = FlexState.of(container);
        if (before.equals(after)) {
            return;
        }
        this.updateProject();
        this.commitHistory(OreEditorHistory.action("UpdateContainerFlex", this.history.activeMergeKey(), container.id(), container.id(), () -> before.apply(container), () -> after.apply(container)));
    }

    private void updateContent(OreComponentNode component, String value) {
        String before = component.content();
        if (OreEditorController.same(before, value)) {
            return;
        }
        component.setContent(value);
        this.updateProject();
        this.commitHistory(OreEditorHistory.stringValue("UpdateContent", this.history.activeMergeKey(), component.id(), component.id(), before, value, component::setContent));
    }

    private void updateNodeStyle(OreCanvasNode node, String property, String value) {
        String before = node.style().get(property);
        if (OreEditorController.same(before, value)) {
            return;
        }
        node.style().set(property, value);
        this.updateProject();
        this.commitHistory(OreEditorHistory.stringValue("UpdateComponentProperty", this.history.activeMergeKey(), node.id(), node.id(), before, value, next -> node.style().set(property, (String)next)));
    }

    private static boolean same(String left, String right) {
        String normalizedLeft = left == null || left.isBlank() ? null : left.trim();
        String normalizedRight = right == null || right.isBlank() ? null : right.trim();
        return Objects.equals(normalizedLeft, normalizedRight);
    }

    private boolean validCssValue(String value) {
        if (value == null || value.isBlank()) {
            return true;
        }
        if (value.length() > 256) {
            return false;
        }
        return value.indexOf(59) < 0 && value.indexOf(123) < 0 && value.indexOf(125) < 0 && value.indexOf(10) < 0 && value.indexOf(13) < 0;
    }

    private void toggleAbsolute(OreComponentNode component, boolean absolute) {
        Element parentElement;
        if (component.locked() || component.absolute() == absolute) {
            return;
        }
        ComponentState before = ComponentState.of(component);
        OreContainerNode parent = component.parent();
        Element target = this.canvasRenderer == null ? null : this.canvasRenderer.elementFor(component.id());
        Element element = parentElement = parent == null || this.canvasRenderer == null ? null : this.canvasRenderer.elementFor(parent.id());
        if (absolute && parent != null && target != null && parentElement != null) {
            Element.DOMRect childRect = target.getBoundingClientRect();
            Element.DOMRect parentRect = parentElement.getBoundingClientRect();
            Box parentBox = Box.of(parentElement);
            component.captureFlowStyleSnapshot();
            component.enterAbsolute(parent.children().indexOf(component));
            component.style().set("position", "absolute");
            component.style().set("left", OreEditorController.px(childRect.left - parentRect.left - parentBox.getBorderLeft()));
            component.style().set("top", OreEditorController.px(childRect.top - parentRect.top - parentBox.getBorderTop()));
            component.style().set("width", OreEditorController.px(childRect.width));
            component.style().set("height", OreEditorController.px(childRect.height));
        } else if (!absolute && parent != null) {
            component.leaveAbsolute();
            component.restoreFlowStyleSnapshot();
            parent.insert(component.flowIndex(), component);
        }
        this.updateProject();
        ComponentState after = ComponentState.of(component);
        this.commitHistory(OreEditorHistory.action("ToggleAbsolute", component.id(), component.id(), () -> before.apply(component), () -> after.apply(component)));
        this.renderMode();
    }

    private void updateAbsoluteOffset(OreComponentNode component, String property, String value) {
        if (component.locked()) {
            return;
        }
        ComponentState before = ComponentState.of(component);
        OreAbsoluteConstraints.setOffset(component, property, value);
        ComponentState after = ComponentState.of(component);
        if (before.equals(after)) {
            return;
        }
        this.updateProject();
        this.commitHistory(OreEditorHistory.action("UpdateAbsolutePosition", component.id(), component.id(), () -> before.apply(component), () -> after.apply(component)));
    }

    private void beginAbsoluteMove(OreComponentNode component, MouseEvent event) {
        if (component.locked()) {
            return;
        }
        this.absoluteDragBefore = ComponentState.of(component);
        this.absoluteDragNode = component.id();
        this.absoluteDragStartX = event.clientX;
        this.absoluteDragStartY = event.clientY;
        this.absoluteDragLeft = OreEditorController.number(component.style().get("left"));
        this.absoluteDragTop = OreEditorController.number(component.style().get("top"));
        this.absoluteDragRight = OreEditorController.number(component.style().get("right"));
        this.absoluteDragBottom = OreEditorController.number(component.style().get("bottom"));
        event.preventDefault();
        event.stopPropagation();
    }

    private void moveAbsoluteNode(double x, double y) {
        OreComponentNode component;
        if (this.absoluteDragNode == null) {
            return;
        }
        OreCanvasNode node = this.project.find(this.absoluteDragNode);
        if (!(node instanceof OreComponentNode) || (component = (OreComponentNode)node).locked()) {
            return;
        }
        double dx = x - this.absoluteDragStartX;
        double dy = y - this.absoluteDragStartY;
        if (OreEditorController.hasPixelOffset(component, "right")) {
            component.style().set("right", OreEditorController.px(this.absoluteDragRight - dx));
        } else {
            component.style().set("left", OreEditorController.px(this.absoluteDragLeft + dx));
        }
        if (OreEditorController.hasPixelOffset(component, "bottom")) {
            component.style().set("bottom", OreEditorController.px(this.absoluteDragBottom - dy));
        } else {
            component.style().set("top", OreEditorController.px(this.absoluteDragTop + dy));
        }
        this.renderCanvas();
    }

    private void finishAbsoluteNode() {
        if (this.absoluteDragNode == null) {
            return;
        }
        OreCanvasNode node = this.project.find(this.absoluteDragNode);
        this.absoluteDragNode = null;
        if (node instanceof OreComponentNode) {
            ComponentState after;
            OreComponentNode component = (OreComponentNode)node;
            if (this.absoluteDragBefore != null && !this.absoluteDragBefore.equals(after = ComponentState.of(component))) {
                ComponentState before = this.absoluteDragBefore;
                this.commitHistory(OreEditorHistory.action("UpdateAbsolutePosition", component.id(), component.id(), () -> before.apply(component), () -> after.apply(component)));
            }
        }
        this.absoluteDragBefore = null;
        this.setDirty(true);
    }

    private void beginAbsoluteResize(UUID id, MouseEvent event) {
        OreComponentNode component;
        Element element;
        OreCanvasNode node = this.project.find(id);
        Element element2 = element = this.canvasRenderer == null ? null : this.canvasRenderer.elementFor(id);
        if (!(node instanceof OreComponentNode) || (component = (OreComponentNode)node).locked() || !component.absolute() || event == null || element == null) {
            return;
        }
        Element.DOMRect bounds = element.getBoundingClientRect();
        this.absoluteResizeNode = id;
        this.absoluteResizeStartX = event.clientX;
        this.absoluteResizeStartY = event.clientY;
        this.absoluteResizeBefore = ComponentState.of(component);
        this.absoluteResizeWidth = bounds.width;
        this.absoluteResizeHeight = bounds.height;
        this.absoluteResizeRight = OreEditorController.number(component.style().get("right"));
        this.absoluteResizeBottom = OreEditorController.number(component.style().get("bottom"));
        event.preventDefault();
        event.stopPropagation();
    }

    private void moveAbsoluteResize(double x, double y) {
        OreComponentNode component;
        if (this.absoluteResizeNode == null) {
            return;
        }
        OreCanvasNode node = this.project.find(this.absoluteResizeNode);
        if (!(node instanceof OreComponentNode) || (component = (OreComponentNode)node).locked()) {
            return;
        }
        double dx = x - this.absoluteResizeStartX;
        double dy = y - this.absoluteResizeStartY;
        component.style().set("width", OreEditorController.px(Math.max(16.0, this.absoluteResizeWidth + dx)));
        component.style().set("height", OreEditorController.px(Math.max(16.0, this.absoluteResizeHeight + dy)));
        if (OreEditorController.hasPixelOffset(component, "right")) {
            component.style().set("right", OreEditorController.px(this.absoluteResizeRight - dx));
        }
        if (OreEditorController.hasPixelOffset(component, "bottom")) {
            component.style().set("bottom", OreEditorController.px(this.absoluteResizeBottom - dy));
        }
        this.renderCanvas();
    }

    private void finishAbsoluteResize() {
        if (this.absoluteResizeNode == null) {
            return;
        }
        OreCanvasNode node = this.project.find(this.absoluteResizeNode);
        this.absoluteResizeNode = null;
        if (node instanceof OreComponentNode) {
            ComponentState after;
            OreComponentNode component = (OreComponentNode)node;
            if (this.absoluteResizeBefore != null && !this.absoluteResizeBefore.equals(after = ComponentState.of(component))) {
                ComponentState before = this.absoluteResizeBefore;
                this.commitHistory(OreEditorHistory.action("UpdateAbsolutePosition", component.id(), component.id(), () -> before.apply(component), () -> after.apply(component)));
            }
        }
        this.absoluteResizeBefore = null;
        this.setDirty(true);
    }

    private static double number(String value) {
        if (value == null) {
            return 0.0;
        }
        try {
            return Double.parseDouble(value.replace("px", "").trim());
        }
        catch (NumberFormatException ignored) {
            return 0.0;
        }
    }

    private static boolean hasPixelOffset(OreComponentNode component, String property) {
        String value = component.style().get(property);
        return value != null && value.trim().endsWith("px");
    }

    private static String px(double value) {
        return (double)Math.round(value * 100.0) / 100.0 + "px";
    }

    private void updateProject() {
        this.setDirty(true);
        this.renderCanvas();
    }

    private void undo() {
        this.applyHistory(this.history.undo());
    }

    private void redo() {
        this.applyHistory(this.history.redo());
    }

    private void applyHistory(OreEditorHistory.Result result) {
        if (!result.changed()) {
            return;
        }
        this.setDirty(!this.history.isAtSavedRevision());
        this.session.select(result.selection() == null ? this.project.root().id() : result.selection());
        this.renderCanvas();
        this.renderMode();
        this.renderBreadcrumb();
        this.updateHistoryControls();
    }

    private void commitHistory(OreEditorHistory.Command command) {
        this.history.recordExecuted(command);
        this.updateHistoryControls();
    }

    private void updateHistoryControls() {
        if (this.document == null) {
            return;
        }
        Element undo = this.document.querySelector("#undoButton");
        Element redo = this.document.querySelector("#redoButton");
        if (undo != null) {
            undo.setDisabled(!this.history.canUndo());
        }
        if (redo != null) {
            redo.setDisabled(!this.history.canRedo());
        }
    }

    private void saveProject() {
        boolean saved;
        boolean bl = saved = this.openedHtmlPath == null ? this.documentStore.saveProject(this.projectCodec.write(this.project)).success() : this.saveOpenedHtml();
        if (saved) {
            this.history.markSaved();
            this.setDirty(false);
            ToastManager.showTranslation("ore_editor.apricityui.notice.saved");
        } else {
            ToastManager.showTranslation("ore_editor.apricityui.notice.save_failed");
        }
    }

    private boolean saveOpenedHtml() {
        if (this.openedHtmlPath == null || !Files.isRegularFile(this.openedHtmlPath, new LinkOption[0])) {
            return false;
        }
        Path target = this.openedHtmlPath;
        try {
            Path parent = target.getParent();
            if (parent == null) {
                return false;
            }
            Path temporary = Files.createTempFile(parent, target.getFileName().toString(), ".tmp", new FileAttribute[0]);
            Files.writeString(temporary, (CharSequence)this.htmlExporter.export(this.project), StandardCharsets.UTF_8, new OpenOption[0]);
            try {
                Files.move(temporary, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            }
            catch (IOException ignored) {
                Files.move(temporary, target, StandardCopyOption.REPLACE_EXISTING);
            }
            ClientLoader.invalidateStaticResourceCache();
            return true;
        }
        catch (IOException | RuntimeException ignored) {
            return false;
        }
    }

    private void setDirty(boolean dirty) {
        this.session.setDirty(dirty);
        this.updateDocumentState();
    }

    private void exportHtml() {
        OreEditorDocumentStore.Result result = this.documentStore.exportHtml(this.htmlExporter.export(this.project));
        ToastManager.showTranslation(result.success() ? "ore_editor.apricityui.notice.exported" : "ore_editor.apricityui.notice.export_failed");
    }

    private static ThemeToken themeToken(String name) {
        for (ThemeToken token : THEME_TOKENS) {
            if (!token.name().equals(name)) continue;
            return token;
        }
        return null;
    }

    private void appendThemeTokenInput(Element body, ThemeToken token, String value) {
        Element group = Element.init(this.document.createElement("DIV"));
        group.setAttribute("class", "form-group editor-form-group editor-theme-token");
        Element label = Element.init(this.document.createElement("LABEL"));
        label.setAttribute("class", "form-label");
        label.appendChild(OreEditorDom.translation(this.document, "ore_editor.apricityui.theme." + token.translationSuffix(), null));
        Element controls = Element.init(this.document.createElement("DIV"));
        controls.setAttribute("class", "editor-theme-token-controls");
        Element input = Element.init(this.document.createElement("INPUT"));
        input.setAttribute("class", "form-input");
        input.setAttribute("type", "text");
        input.setValue(value == null ? "" : value);
        input.addEventListener("focus", event -> this.history.beginMerge("theme-token:" + token.name()));
        input.addEventListener("blur", event -> this.history.endMerge());
        input.addEventListener("change", event -> {
            String next = input.getValue();
            if (!this.validCssValue(next)) {
                input.setAttribute("class", "form-input is-invalid");
                Tooltip.bindTranslation(input, "ore_editor.apricityui.validation.css");
                return;
            }
            input.setAttribute("class", "form-input");
            ColorValue parsed = ColorValue.parse(next);
            Element colorInput = controls.querySelector(".editor-theme-color");
            Element alphaInput = controls.querySelector(".editor-theme-alpha");
            if (colorInput != null) {
                colorInput.setValue(parsed.hex());
            }
            if (alphaInput != null) {
                alphaInput.setValue(OreEditorController.formatNumber(parsed.alpha()));
            }
            this.updateThemeToken(token, next);
            Element resetButton = controls.querySelector(".editor-theme-token-reset");
            if (resetButton != null) {
                resetButton.setDisabled(false);
            }
        });
        if (OreEditorController.isHexColor(token.defaultValue())) {
            ColorValue initial = ColorValue.parse(value);
            Element color = Element.init(this.document.createElement("INPUT"));
            color.setAttribute("class", "editor-theme-color");
            color.setAttribute("type", "color");
            color.setValue(initial.hex());
            Element alpha = Element.init(this.document.createElement("INPUT"));
            alpha.setAttribute("class", "editor-theme-alpha");
            alpha.setAttribute("type", "range");
            alpha.setAttribute("min", "0");
            alpha.setAttribute("max", "1");
            alpha.setAttribute("step", "0.01");
            alpha.setAttribute("data-tooltip-key", "ore_editor.apricityui.property.alpha");
            alpha.setValue(OreEditorController.formatNumber(initial.alpha()));
            Runnable commitPickerColor = () -> {
                double opacity = OreEditorController.validNumber(alpha.getValue()) ? Double.parseDouble(alpha.getValue()) : 1.0;
                String next = ColorValue.toCss(color.getValue(), opacity);
                input.setValue(next);
                this.updateThemeToken(token, next);
                Element resetButton = controls.querySelector(".editor-theme-token-reset");
                if (resetButton != null) {
                    resetButton.setDisabled(false);
                }
            };
            color.addEventListener("change", event -> commitPickerColor.run());
            alpha.addEventListener("focus", event -> this.history.beginMerge("theme-token:" + token.name()));
            alpha.addEventListener("blur", event -> this.history.endMerge());
            alpha.addEventListener("change", event -> commitPickerColor.run());
            controls.appendChild(color);
            controls.appendChild(alpha);
        }
        controls.appendChild(input);
        Element reset = Element.init(this.document.createElement("BUTTON"));
        reset.setAttribute("class", "button button-secondary button-small editor-theme-token-reset");
        reset.setAttribute("type", "button");
        reset.setDisabled(this.project.theme().get(token.name()) == null);
        reset.appendChild(OreEditorDom.translation(this.document, "ore_editor.apricityui.action.reset_token", null));
        reset.addEventListener("click", event -> {
            if (this.project.theme().get(token.name()) == null) {
                return;
            }
            this.updateThemeToken(token, null);
            this.renderMode();
        });
        controls.appendChild(reset);
        group.appendChild(label);
        group.appendChild(controls);
        body.appendChild(group);
    }

    private void updateThemeToken(ThemeToken token, String value) {
        String before = this.project.theme().get(token.name());
        if (OreEditorController.same(before, value)) {
            return;
        }
        this.project.theme().set(token.name(), value);
        this.updateProject();
        this.commitHistory(OreEditorHistory.stringValue("UpdateThemeVariable", this.history.activeMergeKey(), this.session.selectedNode(), this.session.selectedNode(), before, value, next -> this.project.theme().set(token.name(), (String)next)));
    }

    private void applyTheme(Map<String, String> values) {
        this.project.theme().reset();
        if (values != null) {
            values.forEach(this.project.theme()::set);
        }
    }

    private static boolean isHexColor(String value) {
        return value != null && value.matches("#[0-9a-fA-F]{6}");
    }

    static boolean validCssColor(String value) {
        if (value == null || value.isBlank()) {
            return true;
        }
        String color = value.trim();
        return color.matches("#[0-9a-fA-F]{3,8}") || color.matches("(?i)(transparent|currentcolor|[a-z]+)") || color.matches("(?i)(rgb|rgba|hsl|hsla|lab|lch|oklab|oklch|color|var)\\([^;{}\\r\\n]+\\)");
    }

    static ShadowValue parseShadow(String value) {
        String[] tokens;
        if (value == null || value.isBlank()) {
            return new ShadowValue(false, "0px", "0px", "0px", "0px", "#000000");
        }
        String source = value.trim();
        boolean inset = source.startsWith("inset ");
        if (inset) {
            source = source.substring("inset ".length()).trim();
        }
        if ((tokens = source.split("\\s+", 3)).length < 3 || !OreEditorController.isLength(tokens[0]) || !OreEditorController.isLength(tokens[1])) {
            return new ShadowValue(inset, "0px", "0px", "0px", "0px", "#000000");
        }
        String remainder = tokens[2];
        ArrayList<String> lengths = new ArrayList<String>(List.of(tokens[0], tokens[1]));
        while (lengths.size() < 4) {
            String candidate;
            int split = remainder.indexOf(32);
            String string = candidate = split < 0 ? remainder : remainder.substring(0, split);
            if (!OreEditorController.isLength(candidate)) break;
            lengths.add(candidate);
            remainder = split < 0 ? "" : remainder.substring(split + 1).trim();
        }
        if (!OreEditorController.validCssColor(remainder)) {
            return new ShadowValue(inset, "0px", "0px", "0px", "0px", "#000000");
        }
        while (lengths.size() < 4) {
            lengths.add("0px");
        }
        return new ShadowValue(inset, (String)lengths.get(0), (String)lengths.get(1), (String)lengths.get(2), (String)lengths.get(3), remainder);
    }

    private static boolean isLength(String value) {
        return value != null && value.matches("(?i)-?(?:\\d+|\\d*\\.\\d+)(?:px|em|rem|%|vh|vw|vmin|vmax|pt|cm|mm|in|pc|ch|ex)?");
    }

    private void beginPaletteDrag(OreComponentDefinition definition, double x, double y) {
        this.drag.begin(definition, x, y);
        if (!this.drag.active() || this.document == null) {
            return;
        }
        this.removeDragGhost();
        this.dragGhost = Element.init(this.document.createElement("DIV"));
        this.dragGhost.setAttribute("class", "panel editor-drag-ghost");
        this.dragGhost.appendChild(OreEditorDom.translation(this.document, definition.nameKey(), null));
        this.document.body.appendChild(this.dragGhost);
        this.positionDragGhost(x, y);
    }

    private void movePaletteDrag(double x, double y) {
        if (!this.drag.active()) {
            return;
        }
        this.drag.move(x, y);
        this.positionDragGhost(x, y);
        this.updateDropFeedback(x, y);
    }

    private void beginNodeDrag(UUID id, MouseEvent event) {
        OreComponentNode component;
        OreCanvasNode node = this.project.find(id);
        if (node == null || node.locked() || node == this.project.root() || event == null) {
            return;
        }
        if (node instanceof OreComponentNode && (component = (OreComponentNode)node).absolute()) {
            this.beginAbsoluteMove(component, event);
            return;
        }
        this.movingNode = id;
        this.updateDropFeedback(event.clientX, event.clientY);
        event.preventDefault();
        event.stopPropagation();
    }

    private void moveNodeDrag(double x, double y) {
        if (this.movingNode != null) {
            this.updateDropFeedback(x, y);
        }
    }

    private void finishNodeDrag(double x, double y) {
        if (this.movingNode == null) {
            return;
        }
        this.updateDropFeedback(x, y);
        OreCanvasNode node = this.project.find(this.movingNode);
        OreContainerNode target = this.dropTarget;
        OreFlexInsertionResolver.Insertion insertion = this.dropInsertion;
        this.movingNode = null;
        this.clearDropFeedback();
        if (node == null || node.locked() || node.parent() == null || target == null || OreEditorController.structureLocked(node.parent()) || OreEditorController.structureLocked(target)) {
            return;
        }
        OreContainerNode source = node.parent();
        int index = this.insertionIndex(target, insertion);
        int sourceIndex = source.children().indexOf(node);
        if (source == target && sourceIndex >= 0 && sourceIndex < index) {
            --index;
        }
        target.insert(index, node);
        int targetIndex = target.children().indexOf(node);
        this.session.select(node.id());
        this.setDirty(true);
        this.renderCanvas();
        this.renderBreadcrumb();
        this.commitHistory(OreEditorHistory.action(source == target ? "MoveNode" : "ReparentNode", node.id(), node.id(), () -> {
            target.remove(node);
            source.insert(sourceIndex, node);
        }, () -> {
            source.remove(node);
            target.insert(targetIndex, node);
        }));
    }

    private void positionDragGhost(double x, double y) {
        if (this.dragGhost == null) {
            return;
        }
        this.dragGhost.setAttribute("style", "left:" + (x + 12.0) + "px;top:" + (y + 12.0) + "px;");
        this.document.markDirty(this.dragGhost, 13);
    }

    private void finishPaletteDrag(double x, double y) {
        if (!this.drag.active()) {
            return;
        }
        this.updateDropFeedback(x, y);
        OreContainerNode target = this.dropTarget;
        OreFlexInsertionResolver.Insertion insertion = this.dropInsertion;
        this.drag.end((definition, point) -> this.addPaletteNode((OreComponentDefinition)definition, target, insertion));
        this.removeDragGhost();
        this.clearDropFeedback();
    }

    private void addPaletteNode(OreComponentDefinition definition, OreContainerNode target, OreFlexInsertionResolver.Insertion insertion) {
        if (target == null || OreEditorController.structureLocked(target)) {
            return;
        }
        OreCanvasNode node = definition.createNode();
        int index = this.insertionIndex(target, insertion);
        target.insert(index, node);
        this.session.select(node.id());
        this.setDirty(true);
        this.renderCanvas();
        this.renderBreadcrumb();
        this.commitHistory(OreEditorHistory.action("AddNode", target.id(), node.id(), () -> target.remove(node), () -> target.insert(index, node)));
    }

    private void updateDropFeedback(double x, double y) {
        this.dropTarget = this.dropContainerAt(x, y);
        if (this.movingNode != null && !this.canMoveTo(this.movingNode, this.dropTarget)) {
            this.dropTarget = null;
        }
        this.dropInsertion = this.resolveInsertion(this.dropTarget, x, y);
        if (this.canvasRenderer != null) {
            this.canvasRenderer.renderInsertion(this.dropInsertion);
        }
    }

    private void clearDropFeedback() {
        this.dropTarget = null;
        this.dropInsertion = null;
        if (this.canvasRenderer != null) {
            this.canvasRenderer.renderInsertion(null);
        }
    }

    private OreFlexInsertionResolver.Insertion resolveInsertion(OreContainerNode target, double x, double y) {
        if (target == null || this.canvasRenderer == null) {
            return null;
        }
        ArrayList<OreFlexInsertionResolver.Item> items = new ArrayList<OreFlexInsertionResolver.Item>();
        for (OreCanvasNode child : target.children()) {
            Element element = this.canvasRenderer.elementFor(child.id());
            if (element == null) continue;
            Element.DOMRect bounds = element.getBoundingClientRect();
            items.add(new OreFlexInsertionResolver.Item(child.id(), new OreFlexInsertionResolver.Bounds(bounds.left, bounds.top, bounds.width, bounds.height), "absolute".equals(element.getComputedStyle().position)));
        }
        return this.insertionResolver.resolve(target.flex().direction(), target.flex().wrap(), items, x, y);
    }

    private int insertionIndex(OreContainerNode target, OreFlexInsertionResolver.Insertion insertion) {
        if (insertion == null || insertion.beforeId() == null) {
            return target.children().size();
        }
        List<OreCanvasNode> children = target.children();
        for (int index = 0; index < children.size(); ++index) {
            if (!insertion.beforeId().equals(children.get(index).id())) continue;
            return index;
        }
        return children.size();
    }

    private OreContainerNode dropContainerAt(double x, double y) {
        OreCanvasNode oreCanvasNode;
        OreCanvasNode container;
        OreCanvasNode node;
        if (this.canvasRenderer == null || this.document == null) {
            return null;
        }
        Element canvas = this.document.querySelector("#editorCanvas");
        if (canvas == null) {
            return null;
        }
        Element.DOMRect rect = canvas.getBoundingClientRect();
        if (x < rect.left || x > rect.right || y < rect.top || y > rect.bottom) {
            return null;
        }
        UUID hit = this.hitTester.hit(this.canvasRenderer.elements(), x, y);
        OreCanvasNode oreCanvasNode2 = node = hit == null ? this.project.root() : this.project.find(hit);
        while (node != null && (!(node instanceof OreContainerNode) || OreEditorController.structureLocked((OreContainerNode)(container = node)))) {
            node = node.parent();
        }
        if (node instanceof OreContainerNode) {
            container = (OreContainerNode)node;
            oreCanvasNode = container;
        } else {
            oreCanvasNode = this.project.root();
        }
        return oreCanvasNode;
    }

    private boolean canMoveTo(UUID nodeId, OreContainerNode target) {
        OreCanvasNode node = this.project.find(nodeId);
        if (node == null || node.locked() || node == this.project.root() || node.parent() == null || OreEditorController.structureLocked(node.parent()) || target == null || OreEditorController.structureLocked(target)) {
            return false;
        }
        for (OreContainerNode current = target; current != null; current = current.parent()) {
            if (current != node) continue;
            return false;
        }
        return true;
    }

    private static boolean structureLocked(OreContainerNode container) {
        return container != null && !container.acceptsStructuralChildren();
    }

    private void removeDragGhost() {
        if (this.dragGhost != null) {
            this.dragGhost.remove();
        }
        this.dragGhost = null;
    }

    private void resizeSidebar(double pointerX) {
        if (!this.resizing || this.document == null) {
            return;
        }
        Element sidebar = this.document.querySelector("#editorSidebar");
        if (sidebar == null) {
            return;
        }
        double width = Math.max(360.0, Math.min(560.0, (double)this.document.getViewport().layoutWidth() - pointerX));
        sidebar.setAttribute("style", "flex-basis:" + width + "px;width:" + width + "px;");
        this.document.markDirty(sidebar, 15);
    }

    private void stopResize(Element handle) {
        if (!this.resizing) {
            return;
        }
        this.resizing = false;
        handle.setAttribute("class", "editor-resize-handle");
        this.document.markDirty(handle, 9);
    }

    private record ThemeGroup(String translationSuffix, List<String> tokens) {
    }

    private record ThemeToken(String name, String defaultValue, String translationSuffix) {
    }

    record ColorValue(String hex, double alpha) {
        static ColorValue parse(String value) {
            if (value == null || value.isBlank()) {
                return new ColorValue("#000000", 1.0);
            }
            String source = value.trim();
            if (source.matches("#[0-9a-fA-F]{3,4}")) {
                String red = source.substring(1, 2);
                String green = source.substring(2, 3);
                String blue = source.substring(3, 4);
                double alpha = source.length() == 5 ? (double)Integer.parseInt(source.substring(4, 5) + source.substring(4, 5), 16) / 255.0 : 1.0;
                return new ColorValue("#" + red + red + green + green + blue + blue, alpha);
            }
            if (source.matches("#[0-9a-fA-F]{6}(?:[0-9a-fA-F]{2})?")) {
                double alpha = source.length() == 9 ? (double)Integer.parseInt(source.substring(7, 9), 16) / 255.0 : 1.0;
                return new ColorValue(source.substring(0, 7), alpha);
            }
            Matcher matcher = Pattern.compile("(?i)^rgba?\\(\\s*(\\d{1,3})\\s*,\\s*(\\d{1,3})\\s*,\\s*(\\d{1,3})(?:\\s*,\\s*(0(?:\\.\\d+)?|1(?:\\.0+)?))?\\s*\\)$").matcher(source);
            if (!matcher.matches()) {
                return new ColorValue("#000000", 1.0);
            }
            int red = Integer.parseInt(matcher.group(1));
            int green = Integer.parseInt(matcher.group(2));
            int blue = Integer.parseInt(matcher.group(3));
            if (red > 255 || green > 255 || blue > 255) {
                return new ColorValue("#000000", 1.0);
            }
            double alpha = matcher.group(4) == null ? 1.0 : Double.parseDouble(matcher.group(4));
            return new ColorValue(String.format(Locale.ROOT, "#%02x%02x%02x", red, green, blue), alpha);
        }

        static String toCss(String hex, double alpha) {
            String normalized = OreEditorController.isHexColor(hex) ? hex.toLowerCase(Locale.ROOT) : "#000000";
            double clamped = Math.max(0.0, Math.min(1.0, alpha));
            if (clamped >= 1.0) {
                return normalized;
            }
            int red = Integer.parseInt(normalized.substring(1, 3), 16);
            int green = Integer.parseInt(normalized.substring(3, 5), 16);
            int blue = Integer.parseInt(normalized.substring(5, 7), 16);
            return "rgba(" + red + ", " + green + ", " + blue + ", " + OreEditorController.formatNumber(clamped) + ")";
        }
    }

    record ShadowValue(boolean inset, String offsetX, String offsetY, String blur, String spread, String color) {
        String toCss() {
            String prefix = this.inset ? "inset " : "";
            return prefix + this.offsetX + " " + this.offsetY + " " + this.blur + " " + this.spread + " " + this.color;
        }
    }

    record BoxValue(String top, String right, String bottom, String left) {
        static BoxValue of(OreCanvasNode node, String property) {
            String shorthand = node == null ? null : node.style().get(property);
            BoxValue parsed = BoxValue.parse(shorthand);
            if (node == null) {
                return parsed;
            }
            return new BoxValue(BoxValue.valueOr(node.style().get(property + "-top"), parsed.top), BoxValue.valueOr(node.style().get(property + "-right"), parsed.right), BoxValue.valueOr(node.style().get(property + "-bottom"), parsed.bottom), BoxValue.valueOr(node.style().get(property + "-left"), parsed.left));
        }

        static BoxValue parse(String shorthand) {
            if (shorthand == null || shorthand.isBlank()) {
                return new BoxValue("", "", "", "");
            }
            String[] values = shorthand.trim().split("\\s+");
            if (values.length < 1 || values.length > 4) {
                return new BoxValue("", "", "", "");
            }
            return switch (values.length) {
                case 1 -> new BoxValue(values[0], values[0], values[0], values[0]);
                case 2 -> new BoxValue(values[0], values[1], values[0], values[1]);
                case 3 -> new BoxValue(values[0], values[1], values[2], values[1]);
                default -> new BoxValue(values[0], values[1], values[2], values[3]);
            };
        }

        private static String valueOr(String value, String fallback) {
            return value == null || value.isBlank() ? fallback : value;
        }
    }

    record LengthValue(String number, String unit, boolean auto) {
        private static final List<String> UNITS = List.of("px", "rem", "em", "%", "vw", "vh");

        static LengthValue parse(String value) {
            if (value != null && "auto".equalsIgnoreCase(value.trim())) {
                return new LengthValue("", "px", true);
            }
            if (value == null || value.isBlank()) {
                return new LengthValue("", "px", false);
            }
            Matcher matcher = Pattern.compile("^([+-]?(?:\\d+(?:\\.\\d+)?|\\.\\d+))(px|rem|em|%|vw|vh)?$", 2).matcher(value.trim());
            if (!matcher.matches()) {
                return new LengthValue("", "px", false);
            }
            String unit = matcher.group(2);
            return new LengthValue(matcher.group(1), unit == null ? "px" : unit.toLowerCase(Locale.ROOT), false);
        }
    }

    private record FlexState(String direction, String wrap, String justifyContent, String alignItems, String alignContent, String gap, String rowGap, String columnGap) {
        static FlexState of(OreContainerNode container) {
            return new FlexState(container.flex().direction(), container.flex().wrap(), container.flex().justifyContent(), container.flex().alignItems(), container.flex().alignContent(), container.flex().gap(), container.flex().rowGap(), container.flex().columnGap());
        }

        void apply(OreContainerNode container) {
            container.flex().setDirection(this.direction);
            container.flex().setWrap(this.wrap);
            container.flex().setJustifyContent(this.justifyContent);
            container.flex().setAlignItems(this.alignItems);
            container.flex().setAlignContent(this.alignContent);
            container.flex().setGap(this.gap);
            container.flex().setRowGap(this.rowGap);
            container.flex().setColumnGap(this.columnGap);
        }
    }

    private record ComponentState(boolean absolute, int flowIndex, Map<String, String> styles, Map<String, String> flowSnapshot) {
        static ComponentState of(OreComponentNode component) {
            return new ComponentState(component.absolute(), component.flowIndex(), component.style().properties(), component.hasFlowStyleSnapshot() ? component.flowStyleSnapshot() : Map.of());
        }

        void apply(OreComponentNode component) {
            for (String property : new ArrayList<String>(component.style().properties().keySet())) {
                component.style().set(property, null);
            }
            this.styles.forEach(component.style()::set);
            if (this.absolute) {
                component.enterAbsolute(this.flowIndex);
            } else {
                component.leaveAbsolute();
            }
            component.setFlowStyleSnapshot(this.flowSnapshot);
        }
    }
}

