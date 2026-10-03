/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.screens.Screen
 */
package com.sighs.apricityui.dev.devtools;

import com.sighs.apricityui.dev.devtools.DevToolsConfigDialog;
import com.sighs.apricityui.dev.devtools.DevToolsConsole;
import com.sighs.apricityui.dev.devtools.DevToolsCssSerializer;
import com.sighs.apricityui.dev.devtools.DevToolsDocumentStore;
import com.sighs.apricityui.dev.devtools.DevToolsDom;
import com.sighs.apricityui.dev.devtools.DevToolsDomTree;
import com.sighs.apricityui.dev.devtools.DevToolsEditHistory;
import com.sighs.apricityui.dev.devtools.DevToolsHtmlSerializer;
import com.sighs.apricityui.dev.devtools.DevToolsInspector;
import com.sighs.apricityui.dev.devtools.DevToolsSaveDialog;
import com.sighs.apricityui.dev.devtools.DevToolsTranslations;
import com.sighs.apricityui.dev.resource.ResourceMetaDialog;
import com.sighs.apricityui.editor.ore.OreEditor;
import com.sighs.apricityui.element.AbstractText;
import com.sighs.apricityui.event.Event;
import com.sighs.apricityui.event.KeyEvent;
import com.sighs.apricityui.event.MouseEvent;
import com.sighs.apricityui.init.Document;
import com.sighs.apricityui.init.Element;
import com.sighs.apricityui.init.Node;
import com.sighs.apricityui.layout.Box;
import com.sighs.apricityui.layout.Position;
import com.sighs.apricityui.layout.Size;
import com.sighs.apricityui.loader.ClientLoader;
import com.sighs.apricityui.parser.CSS;
import com.sighs.apricityui.parser.HTML;
import com.sighs.apricityui.parser.Selector;
import com.sighs.apricityui.render.Operation;
import com.sighs.apricityui.screen.AuiLinkedScreen;
import com.sighs.apricityui.spi.AuiServices;
import com.sighs.apricityui.style.Cursor;
import com.sighs.apricityui.style.InlineStyleDeclaration;
import com.sighs.apricityui.task.FrameTaskScheduler;
import com.sighs.apricityui.ui.ContextMenu;
import com.sighs.apricityui.ui.DialogWindow;
import com.sighs.apricityui.ui.FilePicker;
import com.sighs.apricityui.ui.Tooltip;
import com.sighs.apricityui.world.WorldWindow;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

public final class DevToolsController {
    public static final String PATH = "devtools/devtools.html";
    private static final String[] BOX_MODEL_REGION_IDS = new String[]{"inspectMarginTop", "inspectMarginRight", "inspectMarginBottom", "inspectMarginLeft", "inspectBorderTop", "inspectBorderRight", "inspectBorderBottom", "inspectBorderLeft", "inspectPaddingTop", "inspectPaddingRight", "inspectPaddingBottom", "inspectPaddingLeft", "inspectContent"};
    private final Set<UUID> expandedNodes = new LinkedHashSet<UUID>();
    private final Map<UUID, LinkedHashMap<String, String>> disabledStyles = new LinkedHashMap<UUID, LinkedHashMap<String, String>>();
    private final Map<UUID, LinkedHashMap<RuleDeclarationKey, CSS.Declaration>> disabledRuleStyles = new LinkedHashMap<UUID, LinkedHashMap<RuleDeclarationKey, CSS.Declaration>>();
    private final DevToolsDomTree tree = new DevToolsDomTree(this);
    private final DevToolsInspector inspector = new DevToolsInspector(this);
    private final DevToolsConsole console = new DevToolsConsole(this);
    private final DevToolsEditHistory editHistory = new DevToolsEditHistory();
    private final DevToolsSaveDialog saveDialog = new DevToolsSaveDialog();
    private final DevToolsConfigDialog configDialog = new DevToolsConfigDialog();
    private final ResourceMetaDialog metaDialog = new ResourceMetaDialog();
    private Document toolDocument;
    private Document targetDocument;
    private Document inspectShellCacheDocument;
    private long inspectShellCacheGeneration = -1L;
    private Element inspectPanelElement;
    private Element inspectHighlightElement;
    private Element inspectHighlightLabelElement;
    private Map<String, Element> inspectBoxRegionElements = Map.of();
    private Document.MutationObserver targetObserver;
    private UUID selectedElementUuid;
    private InspectorTab inspectorTab = InspectorTab.ATTRIBUTES;
    private boolean pickMode;
    private UUID treeHoverElementUuid;
    private boolean consumeInspectMouseUp;
    private boolean draggingPanel;
    private double panelDragOffsetX;
    private boolean resizingInspector;
    private boolean consoleMode;
    private boolean refreshQueued;
    private boolean skipSaveConfirmation;
    private long toastTicket;
    private DialogWindow createElementDialog;
    private Tooltip.Binding consoleTooltipBinding;
    private Element consoleTooltipTarget;
    private String consoleTooltipKey;

    public synchronized boolean isOpen() {
        return this.toolDocument != null && this.toolDocument.isActive() && Document.get(PATH).contains(this.toolDocument);
    }

    public synchronized Document getToolDocument() {
        return this.isOpen() ? this.toolDocument : null;
    }

    public synchronized boolean ensureOpen() {
        if (!this.isOpen()) {
            this.open();
        }
        return this.isOpen();
    }

    public synchronized void toggle() {
        if (this.isOpen()) {
            this.close();
        } else {
            this.open();
        }
    }

    public synchronized boolean selectDocument(Document document) {
        boolean openedWithRequestedTarget;
        if (!this.isDebuggable(document)) {
            return false;
        }
        boolean wasOpen = this.isOpen();
        if (!this.ensureOpen()) {
            return false;
        }
        boolean bl = openedWithRequestedTarget = !wasOpen && this.targetDocument == document;
        if (this.targetDocument != document) {
            this.bindTarget(document);
        } else if (wasOpen) {
            this.resetTreeExpansion();
        }
        this.selectedElementUuid = document.body.uuid;
        this.hideInspectHighlight();
        if (openedWithRequestedTarget) {
            return true;
        }
        this.refresh();
        return true;
    }

    public synchronized boolean selectElement(Element element) {
        if (element == null || !this.isDebuggable(element.document)) {
            return false;
        }
        if (!this.ensureOpen()) {
            return false;
        }
        if (this.targetDocument != element.document) {
            this.bindTarget(element.document);
        }
        this.selectedElementUuid = element.uuid;
        this.revealAncestors(element);
        this.refresh();
        return true;
    }

    public synchronized boolean applyInlineStyle(Element element, String property, String value) {
        if (element == null || !this.isDebuggable(element.document)) {
            return false;
        }
        String normalized = InlineStyleDeclaration.normalizeProperty(property);
        if (normalized.isBlank()) {
            return false;
        }
        DevToolsEditHistory.Snapshot before = this.editSnapshot(element);
        LinkedHashMap<String, String> styles = this.inlineStyles(element);
        styles.put(normalized, value == null ? "" : value.trim());
        this.disabledStyleMap(element).remove(normalized);
        this.applyInlineStyles(element, styles);
        this.afterTargetEdit(element, before, "Style \"" + normalized + "\" updated");
        return true;
    }

    public synchronized boolean handleInspectMouseMove(Position screenPosition) {
        if (!this.isOpen()) {
            this.hideInspectHighlight();
            return false;
        }
        Element treeHover = DevToolsController.findElement(this.targetDocument, this.treeHoverElementUuid);
        if (treeHover != null) {
            this.showInspectHighlight(treeHover);
            return false;
        }
        if (!this.pickMode) {
            this.hideInspectHighlight();
            return false;
        }
        if (!this.isDebuggable(this.targetDocument)) {
            this.pickMode = false;
            this.hideInspectHighlight();
            this.updateShellState();
            return false;
        }
        if (this.isOverToolPanel(screenPosition)) {
            this.hideInspectHighlight();
            return false;
        }
        Cursor.applyCssCursor("crosshair");
        Element hit = this.inspectHit(screenPosition);
        if (hit == null) {
            this.hideInspectHighlight();
            return false;
        }
        this.showInspectHighlight(hit);
        return true;
    }

    public synchronized boolean handleInspectMouseDown(Position screenPosition, int button) {
        if (button != 0 || !this.isOpen() || !this.pickMode || !this.isDebuggable(this.targetDocument) || this.isOverToolPanel(screenPosition)) {
            return false;
        }
        Element hit = this.inspectHit(screenPosition);
        if (hit != null) {
            this.selectedElementUuid = hit.uuid;
            this.revealAncestors(hit);
        }
        this.consumeInspectMouseUp = true;
        this.pickMode = false;
        Cursor.resetToDefault();
        if (hit != null) {
            this.refresh();
            this.scheduleTreeReveal(hit.uuid);
        } else {
            this.updateShellState();
        }
        this.hideInspectHighlight();
        return true;
    }

    public synchronized boolean handleInspectMouseUp(int button) {
        if (button != 0 || !this.consumeInspectMouseUp) {
            return false;
        }
        this.consumeInspectMouseUp = false;
        return true;
    }

    public synchronized void refresh() {
        this.refreshQueued = false;
        if (!this.isOpen()) {
            return;
        }
        if (!this.isDebuggable(this.targetDocument)) {
            this.bindTarget(this.resolvePreferredTarget());
        }
        this.bindShell();
        Element domTree = this.toolDocument.querySelector("#domTree");
        Element nodeCount = this.toolDocument.querySelector("#nodeCount");
        if (domTree == null || nodeCount == null) {
            return;
        }
        Element selected = this.selectedElement();
        if (selected == null && this.targetDocument != null && this.targetDocument.body != null) {
            selected = this.targetDocument.body;
            this.selectedElementUuid = selected.uuid;
        }
        this.tree.render(domTree, nodeCount, this.targetDocument, selected);
        this.inspector.render(this.targetDocument, selected, this.inspectorTab);
        this.updateShellState();
        DevToolsDom.markDirty(this.toolDocument);
    }

    Document toolDocument() {
        return this.toolDocument;
    }

    Document targetDocument() {
        return this.targetDocument;
    }

    Element selectedElement() {
        return DevToolsController.findElement(this.targetDocument, this.selectedElementUuid);
    }

    UUID selectedElementUuid() {
        return this.selectedElementUuid;
    }

    boolean isCollapsed(Element element) {
        return element != null && !this.expandedNodes.contains(element.uuid);
    }

    boolean isPickMode() {
        return this.pickMode;
    }

    boolean isConsoleMode() {
        return this.consoleMode;
    }

    public synchronized void drainExternalLogs() {
        if (!this.isOpen() || !this.consoleMode) {
            return;
        }
        this.console.drainExternalLogs();
    }

    void toggleConsoleMode() {
        boolean bl = this.consoleMode = !this.consoleMode;
        if (this.consoleMode && this.pickMode) {
            this.pickMode = false;
            Cursor.resetToDefault();
            this.hideInspectHighlight();
        }
        if (!this.consoleMode && this.toolDocument != null && this.toolDocument.getFocusedElement() != null) {
            this.clearToolFocus();
        }
        this.updateShellState();
        if (this.consoleMode) {
            Element input;
            this.console.bind();
            Element element = input = this.toolDocument == null ? null : this.toolDocument.querySelector("#consoleInput");
            if (input != null) {
                input.focus();
            }
        }
        DevToolsDom.markDirty(this.toolDocument);
    }

    void togglePickModeFromConsole() {
        if (!this.isDebuggable(this.targetDocument)) {
            this.pickMode = false;
            this.hideInspectHighlight();
            this.showToast(DevToolsTranslations.translate("devtools.apricityui.select_document_first", new Object[0]));
            this.updateShellState();
            return;
        }
        boolean bl = this.pickMode = !this.pickMode;
        if (!this.pickMode) {
            this.hideInspectHighlight();
            Cursor.resetToDefault();
        }
        this.updateShellState();
    }

    void toggleCollapsed(Element element) {
        if (!DevToolsDomTree.hasInspectableChildren(element)) {
            return;
        }
        if (!this.expandedNodes.remove(element.uuid)) {
            this.expandedNodes.add(element.uuid);
        }
        this.refreshTree();
    }

    void selectFromView(Element element) {
        if (element == null || element.document != this.targetDocument) {
            return;
        }
        this.treeHoverElementUuid = null;
        this.selectedElementUuid = element.uuid;
        this.revealAncestors(element);
        this.refresh();
        this.hideInspectHighlight();
    }

    void showElementContextMenu(Element element, MouseEvent event) {
        if (element == null || event == null || element.document != this.targetDocument || !element.isConnected()) {
            return;
        }
        this.selectFromView(element);
        boolean canChangeStructure = this.canChangeStructure(element);
        ArrayList<ContextMenu.Item> items = new ArrayList<ContextMenu.Item>();
        items.add(ContextMenu.Item.header(DevToolsTranslations.translate("devtools.apricityui.element_menu", element.tagName.toLowerCase(Locale.ROOT))));
        items.add(ContextMenu.Item.action(DevToolsTranslations.translate("devtools.apricityui.copy_outer_html", new Object[0]), "<svg viewBox=\"0 0 14 14\" fill=\"currentColor\"><rect x=\"4\" y=\"4\" width=\"8\" height=\"8\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"1.2\"/><path d=\"M2 10V3h6v1H3v6H2z\"/></svg>", "Ctrl+C", () -> this.copyElementOuterHtml(element)));
        items.add(ContextMenu.Item.action(DevToolsTranslations.translate("devtools.apricityui.copy_selector", new Object[0]), "<svg viewBox=\"0 0 14 14\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"1.2\"><path d=\"M5.5 8.5l3-3\"/><path d=\"M4.5 10.5H3a2.5 2.5 0 010-5h2M9.5 3.5H11a2.5 2.5 0 010 5H9\"/></svg>", () -> this.copyElementSelector(element)));
        items.add(ContextMenu.Item.separator());
        items.add(this.structureItem(DevToolsTranslations.translate("devtools.apricityui.add_child_element", new Object[0]), "<svg viewBox=\"0 0 14 14\" fill=\"currentColor\"><path d=\"M3 1h5l3 3v8H3V1zm5 1v3h3M7 6v2H5v1h2v2h1V9h2V8H8V6H7z\"/></svg>", canChangeStructure, () -> this.openCreateElementDialog(element)));
        items.add(this.structureItem(DevToolsTranslations.translate("devtools.apricityui.hide_element", new Object[0]), "<svg viewBox=\"0 0 14 14\" fill=\"currentColor\"><circle cx=\"7\" cy=\"7\" r=\"5\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"1.2\"/><path d=\"M7 4v3.5M7 9v.5\" stroke=\"currentColor\" stroke-width=\"1.5\" stroke-linecap=\"round\"/></svg>", canChangeStructure, () -> this.hideElement(element)));
        items.add(this.structureItem(DevToolsTranslations.translate("devtools.apricityui.duplicate_element", new Object[0]), "<svg viewBox=\"0 0 14 14\" fill=\"currentColor\"><path d=\"M3 1h5l3 3v8H3V1zm5 1v3h3M7 6v2H5v1h2v2h1V9h2V8H8V6H7z\"/></svg>", canChangeStructure, () -> this.duplicateElement(element)));
        items.add(ContextMenu.Item.separator());
        items.add(this.structureItem(DevToolsTranslations.translate("devtools.apricityui.delete_element", new Object[0]), "<svg viewBox=\"0 0 14 14\" fill=\"currentColor\"><path d=\"M4 2h6v1H4V2zM2 4h10v1H2V4zm1 2h8l-1 7H4L3 6zm3 1v5h1V7H6zm2 0v5h1V7H8z\"/></svg>", canChangeStructure, () -> this.deleteElement(element)).dangerous());
        ContextMenu.show(this.toolDocument, new Position(event.clientX, event.clientY), items);
    }

    void hoverFromView(Element element) {
        if (element == null || element.document != this.targetDocument || !element.isConnected()) {
            return;
        }
        this.treeHoverElementUuid = element.uuid;
        this.showInspectHighlight(element);
    }

    private ContextMenu.Item structureItem(String label, String icon, boolean enabled, Runnable action) {
        ContextMenu.Item item = ContextMenu.Item.action(label, icon, action);
        return enabled ? item : item.disabled();
    }

    private void copyElementOuterHtml(Element element) {
        if (!this.isCurrentTarget(element)) {
            return;
        }
        Operation.setClipboardText(DevToolsHtmlSerializer.serializeElement(element));
        this.showToast(DevToolsTranslations.translate("devtools.apricityui.outer_html_copied", new Object[0]));
    }

    private void copyElementSelector(Element element) {
        if (!this.isCurrentTarget(element)) {
            return;
        }
        Operation.setClipboardText(DevToolsController.cssSelector(element));
        this.showToast(DevToolsTranslations.translate("devtools.apricityui.selector_copied", new Object[0]));
    }

    private void hideElement(Element element) {
        if (!this.canChangeStructure(element)) {
            return;
        }
        this.applyInlineStyle(element, "display", "none");
    }

    private void openCreateElementDialog(Element parent) {
        DialogWindow dialog;
        if (!this.canChangeStructure(parent) || this.toolDocument == null) {
            return;
        }
        this.closeCreateElementDialog();
        this.createElementDialog = dialog = DialogWindow.open(this.toolDocument, new DialogWindow.Options(DevToolsTranslations.translate("devtools.apricityui.add_child_element", new Object[0]), 360.0, 0.0, false, "dialog-overlay show", "dialog create-element-dialog", "dialog-header", "dialog-title", "dialog-close", "dialog-body", ""), () -> {
            this.createElementDialog = null;
        });
        Element content = dialog.content();
        Element label = DevToolsDom.text(this.toolDocument, "LABEL", "create-element-label", DevToolsTranslations.translate("devtools.apricityui.tag_name", new Object[0]));
        label.setAttribute("for", "createElementTag");
        content.append(label);
        Element input = DevToolsDom.input(this.toolDocument, "create-element-input", "div", "div");
        input.setAttribute("id", "createElementTag");
        input.addEventListener("keydown", event -> {
            if (this.isCommitKey((Event)event)) {
                this.createElement(parent, DevToolsDom.value(input), dialog);
            }
        });
        content.append(input);
        content.append(DevToolsDom.text(this.toolDocument, "DIV", "create-element-hint", DevToolsTranslations.translate("devtools.apricityui.add_child_hint", new Object[0])));
        Element footer = DevToolsDom.element(this.toolDocument, "DIV", "dialog-footer");
        Element cancel = DevToolsDom.text(this.toolDocument, "BUTTON", "dialog-btn dialog-btn-cancel", DevToolsTranslations.translate("devtools.apricityui.cancel", new Object[0]));
        cancel.addEventListener("click", event -> dialog.close());
        footer.append(cancel);
        Element create = DevToolsDom.text(this.toolDocument, "BUTTON", "dialog-btn dialog-btn-confirm", DevToolsTranslations.translate("devtools.apricityui.create", new Object[0]));
        create.addEventListener("click", event -> this.createElement(parent, DevToolsDom.value(input), dialog));
        footer.append(create);
        dialog.window().append(footer);
        DevToolsDom.markDirty(this.toolDocument);
    }

    private void createElement(Element parent, String tagName, DialogWindow dialog) {
        String tag;
        if (!this.canChangeStructure(parent)) {
            return;
        }
        String string = tag = tagName == null ? "" : tagName.trim().toLowerCase(Locale.ROOT);
        if (!tag.matches("[a-z][a-z0-9-]*")) {
            this.showToast(DevToolsTranslations.translate("devtools.apricityui.invalid_tag_name", new Object[0]));
            return;
        }
        Element child = Element.init(this.targetDocument.createElement(tag));
        parent.append(child);
        this.expandedNodes.add(parent.uuid);
        this.selectedElementUuid = child.uuid;
        this.editHistory.record(this.targetDocument, () -> this.removeElementFromHistory(child), () -> this.appendElementFromHistory(parent, child), DevToolsTranslations.translate("devtools.apricityui.element_created", tag));
        if (dialog != null) {
            dialog.close();
        }
        this.targetDocument.markDirty(this.targetDocument.body, 15);
        this.showToast(DevToolsTranslations.translate("devtools.apricityui.element_created", tag));
        this.refresh();
    }

    private void duplicateElement(Element element) {
        if (!this.canChangeStructure(element)) {
            return;
        }
        Element duplicate = element.cloneNode(true);
        if (duplicate == null) {
            return;
        }
        element.after(duplicate);
        this.selectedElementUuid = duplicate.uuid;
        this.editHistory.record(this.targetDocument, () -> this.removeElementFromHistory(duplicate), () -> this.insertAfterFromHistory(element, duplicate), DevToolsTranslations.translate("devtools.apricityui.element_duplicated", new Object[0]));
        this.targetDocument.markDirty(this.targetDocument.body, 15);
        this.showToast(DevToolsTranslations.translate("devtools.apricityui.element_duplicated", new Object[0]));
        this.refresh();
    }

    private void deleteElement(Element element) {
        if (!this.canChangeStructure(element)) {
            return;
        }
        Element parent = element.parentElement;
        Node nextSibling = element.getNextSibling();
        if (parent == null) {
            return;
        }
        element.remove();
        this.selectedElementUuid = parent.uuid;
        this.editHistory.record(this.targetDocument, () -> this.insertBeforeFromHistory(parent, element, nextSibling), () -> this.removeElementFromHistory(element), DevToolsTranslations.translate("devtools.apricityui.element_deleted", new Object[0]));
        this.targetDocument.markDirty(this.targetDocument.body, 15);
        this.showToast(DevToolsTranslations.translate("devtools.apricityui.element_deleted", new Object[0]));
        this.refresh();
    }

    private boolean removeElementFromHistory(Element element) {
        if (element == null || !element.isConnected()) {
            return false;
        }
        element.remove();
        this.refresh();
        return true;
    }

    private boolean insertAfterFromHistory(Element reference, Element element) {
        if (!this.isCurrentTarget(reference) || element == null || element.isConnected()) {
            return false;
        }
        reference.after(element);
        this.refresh();
        return true;
    }

    private boolean appendElementFromHistory(Element parent, Element element) {
        if (!this.isCurrentTarget(parent) || element == null || element.isConnected()) {
            return false;
        }
        parent.append(element);
        this.expandedNodes.add(parent.uuid);
        this.refresh();
        return true;
    }

    private boolean insertBeforeFromHistory(Element parent, Element element, Node nextSibling) {
        if (!this.isCurrentTarget(parent) || element == null || element.isConnected()) {
            return false;
        }
        if (nextSibling != null && nextSibling.isConnected() && nextSibling.parentNode == parent) {
            parent.insertBefore((Node)element, nextSibling);
        } else {
            parent.append(element);
        }
        this.refresh();
        return true;
    }

    private boolean canChangeStructure(Element element) {
        return this.isCurrentTarget(element) && element != this.targetDocument.documentElement && element.parentElement != null;
    }

    private boolean isCurrentTarget(Element element) {
        return element != null && element.document == this.targetDocument && element.isConnected();
    }

    private static String cssSelector(Element element) {
        if (element.id != null && !element.id.isBlank()) {
            return "#" + DevToolsController.escapeSelectorToken(element.id);
        }
        ArrayList<Object> parts = new ArrayList<Object>();
        Element current = element;
        while (current != null && current.document != null) {
            String tag = current.tagName.toLowerCase(Locale.ROOT);
            if (current.id != null && !current.id.isBlank()) {
                parts.add("#" + DevToolsController.escapeSelectorToken(current.id));
                break;
            }
            StringBuilder part = new StringBuilder(tag);
            for (String className : current.getClassNames()) {
                part.append('.').append(DevToolsController.escapeSelectorToken(className));
            }
            if (current.parentElement != null) {
                int index = 1;
                for (Element sibling : current.parentElement.children) {
                    if (sibling == current) break;
                    if (!tag.equalsIgnoreCase(sibling.tagName)) continue;
                    ++index;
                }
                part.append(":nth-of-type(").append(index).append(')');
            }
            parts.add(part.toString());
            if (current == current.document.documentElement) break;
            current = current.parentElement;
        }
        Collections.reverse(parts);
        return String.join((CharSequence)" > ", parts);
    }

    private static String escapeSelectorToken(String value) {
        return value.replaceAll("[^a-zA-Z0-9_-]", "\\\\$0");
    }

    void clearHoverFromView(Element element) {
        if (element == null || !element.uuid.equals(this.treeHoverElementUuid)) {
            return;
        }
        this.clearTreeHover();
    }

    void updateAttribute(Element target, String name, String value) {
        if (target == null || name == null || name.isBlank()) {
            return;
        }
        DevToolsEditHistory.Snapshot before = this.editSnapshot(target);
        String normalized = name.trim();
        if (value == null || value.isEmpty()) {
            target.removeAttribute(normalized);
        } else {
            target.setAttribute(normalized, value);
        }
        if ("style".equalsIgnoreCase(normalized)) {
            DevToolsController.syncRuntimeInlineStyleCache(target);
        }
        this.afterTargetEdit(target, before, "Attr \"" + normalized + "\" updated");
    }

    void addAttribute(Element target, String name, String value) {
        if (target == null || name == null || name.isBlank()) {
            return;
        }
        DevToolsEditHistory.Snapshot before = this.editSnapshot(target);
        String normalized = name.trim();
        target.setAttribute(normalized, value == null ? "" : value);
        if ("style".equalsIgnoreCase(normalized)) {
            DevToolsController.syncRuntimeInlineStyleCache(target);
        }
        this.afterTargetEdit(target, before, "Attr \"" + normalized + "\" added");
    }

    void deleteAttribute(Element target, String name) {
        if (target == null || name == null || name.isBlank()) {
            return;
        }
        DevToolsEditHistory.Snapshot before = this.editSnapshot(target);
        target.removeAttribute(name);
        if ("style".equalsIgnoreCase(name)) {
            DevToolsController.syncRuntimeInlineStyleCache(target);
        }
        this.afterTargetEdit(target, before, "Attr \"" + name + "\" removed");
    }

    void updateStyle(Element target, String property, String value) {
        this.applyInlineStyle(target, property, value);
    }

    void renameStyle(Element target, String oldProperty, String newProperty) {
        if (target == null) {
            return;
        }
        String oldKey = InlineStyleDeclaration.normalizeProperty(oldProperty);
        String newKey = InlineStyleDeclaration.normalizeProperty(newProperty);
        if (oldKey.isBlank() || newKey.isBlank() || oldKey.equals(newKey)) {
            return;
        }
        DevToolsEditHistory.Snapshot before = this.editSnapshot(target);
        LinkedHashMap<String, String> styles = this.inlineStyles(target);
        String value = (String)styles.remove(oldKey);
        LinkedHashMap<String, String> disabled = this.disabledStyleMap(target);
        if (value == null) {
            value = (String)disabled.remove(oldKey);
        }
        if (value == null) {
            return;
        }
        styles.put(newKey, value);
        this.applyInlineStyles(target, styles);
        this.afterTargetEdit(target, before, "Style renamed to \"" + newKey + "\"");
    }

    void deleteStyle(Element target, String property) {
        if (target == null) {
            return;
        }
        DevToolsEditHistory.Snapshot before = this.editSnapshot(target);
        String key = InlineStyleDeclaration.normalizeProperty(property);
        LinkedHashMap<String, String> styles = this.inlineStyles(target);
        styles.remove(key);
        this.disabledStyleMap(target).remove(key);
        this.applyInlineStyles(target, styles);
        this.afterTargetEdit(target, before, "Style \"" + key + "\" removed");
    }

    void toggleStyle(Element target, String property) {
        if (target == null) {
            return;
        }
        DevToolsEditHistory.Snapshot before = this.editSnapshot(target);
        String key = InlineStyleDeclaration.normalizeProperty(property);
        LinkedHashMap<String, String> styles = this.inlineStyles(target);
        LinkedHashMap<String, String> disabled = this.disabledStyleMap(target);
        if (disabled.containsKey(key)) {
            styles.put(key, (String)disabled.remove(key));
        } else if (styles.containsKey(key)) {
            disabled.put(key, (String)styles.remove(key));
        }
        this.applyInlineStyles(target, styles);
        this.afterTargetEdit(target, before, "Style \"" + key + "\" toggled");
    }

    LinkedHashMap<String, String> inlineStyles(Element target) {
        return InlineStyleDeclaration.parse(target == null ? "" : target.getAttribute("style"));
    }

    LinkedHashMap<String, String> disabledStyleEntries(Element target) {
        return new LinkedHashMap<String, String>(this.disabledStyleMap(target));
    }

    boolean isStyleDisabled(Element target, String property) {
        return target != null && this.disabledStyleMap(target).containsKey(property);
    }

    LinkedHashMap<String, RuleStyle> stylesheetStyles(Selector.DebugStyleBlock block) {
        LinkedHashMap<String, RuleStyle> result = new LinkedHashMap<String, RuleStyle>();
        if (block == null) {
            return result;
        }
        block.declarations().forEach((property, declaration) -> result.put((String)property, new RuleStyle(declaration.value(), declaration.important(), declaration.overridden(), false)));
        if (this.targetDocument == null) {
            return result;
        }
        this.disabledRuleStyleMap(this.targetDocument).forEach((key, declaration) -> {
            if (key.ruleOrder() == block.ruleOrder()) {
                result.putIfAbsent(key.property(), new RuleStyle(declaration.value(), declaration.important(), false, true));
            }
        });
        return result;
    }

    void updateStylesheetStyle(Element target, int ruleOrder, String property, String value) {
        if (!this.canEditRule(target, ruleOrder)) {
            return;
        }
        String key = InlineStyleDeclaration.normalizeProperty(property);
        if (key.isBlank()) {
            return;
        }
        StylesheetSnapshot before = this.stylesheetSnapshot(target.document);
        RuleDeclarationKey declarationKey = new RuleDeclarationKey(ruleOrder, key);
        LinkedHashMap<RuleDeclarationKey, CSS.Declaration> disabled = this.disabledRuleStyleMap(target.document);
        CSS.Declaration declaration = DevToolsController.parseRuleDeclaration(value);
        if (disabled.containsKey(declarationKey)) {
            disabled.put(declarationKey, declaration);
        } else {
            DevToolsController.findDebugRule(target.document, ruleOrder).properties().put(key, declaration);
        }
        this.afterStylesheetEdit(target.document, before, "Rule \"" + key + "\" updated");
    }

    void renameStylesheetStyle(Element target, int ruleOrder, String oldProperty, String newProperty) {
        if (!this.canEditRule(target, ruleOrder)) {
            return;
        }
        String oldKey = InlineStyleDeclaration.normalizeProperty(oldProperty);
        String newKey = InlineStyleDeclaration.normalizeProperty(newProperty);
        if (oldKey.isBlank() || newKey.isBlank() || oldKey.equals(newKey)) {
            return;
        }
        StylesheetSnapshot before = this.stylesheetSnapshot(target.document);
        CSS.DebugRule rule = DevToolsController.findDebugRule(target.document, ruleOrder);
        RuleDeclarationKey oldDeclarationKey = new RuleDeclarationKey(ruleOrder, oldKey);
        RuleDeclarationKey newDeclarationKey = new RuleDeclarationKey(ruleOrder, newKey);
        LinkedHashMap<RuleDeclarationKey, CSS.Declaration> disabled = this.disabledRuleStyleMap(target.document);
        CSS.Declaration declaration = (CSS.Declaration)disabled.remove(oldDeclarationKey);
        if (declaration != null) {
            disabled.put(newDeclarationKey, declaration);
        } else {
            declaration = rule.properties().remove(oldKey);
            if (declaration == null) {
                return;
            }
            rule.properties().put(newKey, declaration);
        }
        this.afterStylesheetEdit(target.document, before, "Rule renamed to \"" + newKey + "\"");
    }

    void deleteStylesheetStyle(Element target, int ruleOrder, String property) {
        if (!this.canEditRule(target, ruleOrder)) {
            return;
        }
        String key = InlineStyleDeclaration.normalizeProperty(property);
        if (key.isBlank()) {
            return;
        }
        StylesheetSnapshot before = this.stylesheetSnapshot(target.document);
        CSS.DebugRule rule = DevToolsController.findDebugRule(target.document, ruleOrder);
        rule.properties().remove(key);
        this.disabledRuleStyleMap(target.document).remove(new RuleDeclarationKey(ruleOrder, key));
        this.afterStylesheetEdit(target.document, before, "Rule \"" + key + "\" removed");
    }

    void toggleStylesheetStyle(Element target, int ruleOrder, String property) {
        if (!this.canEditRule(target, ruleOrder)) {
            return;
        }
        String key = InlineStyleDeclaration.normalizeProperty(property);
        if (key.isBlank()) {
            return;
        }
        StylesheetSnapshot before = this.stylesheetSnapshot(target.document);
        CSS.DebugRule rule = DevToolsController.findDebugRule(target.document, ruleOrder);
        RuleDeclarationKey declarationKey = new RuleDeclarationKey(ruleOrder, key);
        LinkedHashMap<RuleDeclarationKey, CSS.Declaration> disabled = this.disabledRuleStyleMap(target.document);
        CSS.Declaration declaration = (CSS.Declaration)disabled.remove(declarationKey);
        if (declaration != null) {
            rule.properties().put(key, declaration);
        } else {
            declaration = rule.properties().remove(key);
            if (declaration == null) {
                return;
            }
            disabled.put(declarationKey, declaration);
        }
        this.afterStylesheetEdit(target.document, before, "Rule \"" + key + "\" toggled");
    }

    void addStylesheetStyle(Element target, int ruleOrder, String property, String value) {
        this.updateStylesheetStyle(target, ruleOrder, property, value);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    boolean isCommitKey(Event event) {
        if (!(event instanceof KeyEvent)) return false;
        KeyEvent keyEvent = (KeyEvent)event;
        if (!"Enter".equals(keyEvent.key)) return false;
        return true;
    }

    void clearToolFocus() {
        if (this.toolDocument != null) {
            this.toolDocument.clearFocus();
        }
    }

    void showToast(String message) {
        if (this.toolDocument == null) {
            return;
        }
        Element toast = this.toolDocument.querySelector("#toast");
        if (toast == null) {
            return;
        }
        long ticket = ++this.toastTicket;
        toast.setTextContent(message == null ? "" : message);
        toast.setAttribute("class", "toast show");
        DevToolsDom.markDirty(this.toolDocument);
        FrameTaskScheduler.scheduleAfterFrames(32, deadlineNs -> {
            DevToolsController devToolsController = this;
            synchronized (devToolsController) {
                if (ticket == this.toastTicket && toast.isConnected()) {
                    toast.setAttribute("class", "toast");
                    DevToolsDom.markDirty(this.toolDocument);
                }
            }
            return true;
        });
    }

    private void open() {
        this.toolDocument = Document.create(PATH);
        if (this.toolDocument == null) {
            return;
        }
        this.cacheInspectShellElements();
        this.toolDocument.setReloadPersistent(true);
        this.bindTarget(this.resolvePreferredTarget());
        this.refresh();
    }

    private void close() {
        this.disconnectTargetObserver();
        this.saveDialog.close();
        this.configDialog.close();
        this.metaDialog.close();
        this.closeCreateElementDialog();
        Document closing = this.toolDocument;
        Tooltip.hide(closing);
        if (this.consoleTooltipBinding != null) {
            this.consoleTooltipBinding.close();
        }
        this.consoleTooltipBinding = null;
        this.consoleTooltipTarget = null;
        this.consoleTooltipKey = null;
        this.clearInspectShellElementCache();
        this.toolDocument = null;
        this.targetDocument = null;
        this.selectedElementUuid = null;
        this.expandedNodes.clear();
        this.disabledStyles.clear();
        this.disabledRuleStyles.clear();
        this.editHistory.clear();
        this.pickMode = false;
        this.consoleMode = false;
        this.treeHoverElementUuid = null;
        this.consumeInspectMouseUp = false;
        this.draggingPanel = false;
        this.panelDragOffsetX = 0.0;
        this.resizingInspector = false;
        this.refreshQueued = false;
        if (closing != null) {
            closing.remove();
        }
    }

    private void cacheInspectShellElements() {
        if (this.toolDocument == null) {
            this.clearInspectShellElementCache();
            return;
        }
        long generation = this.toolDocument.getRefreshGeneration();
        if (this.inspectShellCacheDocument == this.toolDocument && this.inspectShellCacheGeneration == generation) {
            return;
        }
        this.inspectShellCacheDocument = this.toolDocument;
        this.inspectShellCacheGeneration = generation;
        this.inspectPanelElement = this.toolDocument.querySelector(".side-panel");
        this.inspectHighlightElement = this.toolDocument.getElementById("inspectHighlight");
        this.inspectHighlightLabelElement = this.toolDocument.getElementById("inspectHighlightLabel");
        LinkedHashMap<String, Element> regions = new LinkedHashMap<String, Element>();
        for (String id : BOX_MODEL_REGION_IDS) {
            regions.put(id, this.toolDocument.getElementById(id));
        }
        this.inspectBoxRegionElements = regions;
    }

    private void clearInspectShellElementCache() {
        this.inspectShellCacheDocument = null;
        this.inspectShellCacheGeneration = -1L;
        this.inspectPanelElement = null;
        this.inspectHighlightElement = null;
        this.inspectHighlightLabelElement = null;
        this.inspectBoxRegionElements = Map.of();
    }

    private void closeCreateElementDialog() {
        DialogWindow dialog = this.createElementDialog;
        this.createElementDialog = null;
        if (dialog != null) {
            dialog.close();
        }
    }

    private void bindTarget(Document target) {
        this.metaDialog.close();
        this.disconnectTargetObserver();
        this.pickMode = false;
        this.treeHoverElementUuid = null;
        this.hideInspectHighlight();
        this.targetDocument = this.isDebuggable(target) ? target : null;
        this.resetTreeExpansion();
        if (this.targetDocument == null) {
            this.selectedElementUuid = null;
            return;
        }
        if (DevToolsController.findElement(this.targetDocument, this.selectedElementUuid) == null) {
            this.selectedElementUuid = this.targetDocument.body.uuid;
        }
        this.targetObserver = this.targetDocument.createMutationObserver(records -> this.scheduleRefresh());
        this.targetObserver.observe(this.targetDocument.documentElement, true, true, true, true, false, false, "");
    }

    private void disconnectTargetObserver() {
        if (this.targetObserver != null) {
            this.targetObserver.disconnect();
        }
        this.targetObserver = null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void scheduleRefresh() {
        DevToolsController devToolsController = this;
        synchronized (devToolsController) {
            if (this.refreshQueued || !this.isOpen()) {
                return;
            }
            this.refreshQueued = true;
        }
        FrameTaskScheduler.scheduleAfterFrames(1, deadlineNs -> {
            this.refresh();
            return true;
        });
    }

    private void bindShell() {
        if (this.toolDocument == null || this.toolDocument.body == null) {
            return;
        }
        Element pickButton = this.toolDocument.querySelector("#pickBtn");
        Element saveButton = this.toolDocument.querySelector("#saveBtn");
        Element reloadDocumentButton = this.toolDocument.querySelector("#reloadDocumentBtn");
        Element metaButton = this.toolDocument.querySelector("#metaButton");
        Element consoleButton = this.toolDocument.querySelector(".console-btn");
        Element oreEditorButton = this.toolDocument.querySelector("#oreEditorButton");
        Element settingsButton = this.toolDocument.querySelector("#settingsButton");
        Element dragHandle = this.toolDocument.querySelector("#panelDragHandle");
        Element closeDevToolsButton = this.toolDocument.querySelector("#closeDevToolsBtn");
        Element closeDocumentButton = this.toolDocument.querySelector("#closeDocumentBtn");
        Element documentSelect = this.toolDocument.querySelector("#documentSelect");
        this.localizeAccessibility();
        this.bindOnce(pickButton, event -> {
            if (!this.isDebuggable(this.targetDocument)) {
                this.pickMode = false;
                this.hideInspectHighlight();
                this.updateShellState();
                this.showToast(DevToolsTranslations.translate("devtools.apricityui.select_document_first", new Object[0]));
                return;
            }
            boolean bl = this.pickMode = !this.pickMode;
            if (!this.pickMode) {
                this.hideInspectHighlight();
                Cursor.resetToDefault();
            }
            this.updateShellState();
            this.showToast(DevToolsTranslations.translate(this.pickMode ? "devtools.apricityui.inspect_mode_on" : "devtools.apricityui.inspect_mode_off", new Object[0]));
        });
        this.bindOnce(saveButton, event -> this.requestSave());
        this.bindOnce(reloadDocumentButton, event -> this.reloadTargetDocument());
        this.bindOnce(metaButton, event -> this.openMetaEditor());
        this.bindOnce(consoleButton, event -> this.toggleConsoleMode());
        this.bindOnce(oreEditorButton, event -> this.openOreEditorFilePicker());
        this.bindOnce(settingsButton, event -> this.configDialog.open(this.toolDocument));
        this.bindOnce(closeDevToolsButton, event -> this.close());
        this.bindOnce(closeDocumentButton, event -> this.closeTargetDocument());
        this.bindTooltipOnce(pickButton, "tooltip.apricityui.devtools.inspect");
        this.bindTooltipOnce(saveButton, "tooltip.apricityui.devtools.save");
        this.bindTooltipOnce(reloadDocumentButton, "tooltip.apricityui.devtools.reload_document");
        this.bindTooltipOnce(metaButton, "tooltip.apricityui.devtools.meta");
        this.bindConsoleTooltip(consoleButton);
        this.bindTooltipOnce(oreEditorButton, "tooltip.apricityui.ore_editor.open");
        this.bindTooltipOnce(settingsButton, "tooltip.apricityui.devtools.settings");
        this.bindTooltipOnce(closeDevToolsButton, "tooltip.apricityui.devtools.close");
        this.bindTooltipOnce(closeDocumentButton, "tooltip.apricityui.devtools.close_document");
        this.bindPanelDrag(dragHandle);
        this.bindTooltipOnce(dragHandle, "tooltip.apricityui.devtools.move");
        this.bindDocumentSelector(documentSelect);
        this.bindHistoryShortcuts();
        this.syncDocumentSelector(documentSelect);
        for (Element tab : this.toolDocument.querySelectorAll(".inspector-tab")) {
            this.bindOnce(tab, event -> {
                this.inspectorTab = InspectorTab.parse(tab.getAttribute("data-tab"));
                this.inspector.render(this.targetDocument, this.selectedElement(), this.inspectorTab);
                this.updateShellState();
                DevToolsDom.markDirty(this.toolDocument);
            });
        }
        Element resizeHandle = this.toolDocument.querySelector("#resizeHandle");
        if (resizeHandle != null && !"1".equals(resizeHandle.getAttribute("data-resize-bound"))) {
            resizeHandle.setAttribute("data-resize-bound", "1");
            resizeHandle.addEventListener("mousedown", event -> {
                this.resizingInspector = true;
                resizeHandle.setAttribute("class", "resize-handle dragging");
                event.preventDefault();
            });
            this.toolDocument.body.addEventListener("mousemove", this::resizeInspector);
            this.toolDocument.body.addEventListener("mouseup", event -> {
                if (!this.resizingInspector) {
                    return;
                }
                this.resizingInspector = false;
                resizeHandle.setAttribute("class", "resize-handle");
                DevToolsDom.markDirty(this.toolDocument);
            });
        }
        this.console.bind();
    }

    private void openOreEditorFilePicker() {
        if (OreEditor.isOpen() && OreEditor.getSession().dirty()) {
            this.showToast(DevToolsTranslations.translate("devtools.apricityui.ore_editor.unsaved", new Object[0]));
            return;
        }
        FilePicker.pick(FilePicker.Options.htmlTranslation("devtools.apricityui.ore_editor.select_html", false)).thenAccept(selection -> selection.ifPresent(file -> {
            if (!OreEditor.openHtml(file.localPath())) {
                this.showToast(DevToolsTranslations.translate("devtools.apricityui.ore_editor.open_failed", new Object[0]));
            }
        }));
    }

    private void bindOnce(Element element, Consumer<Event> listener) {
        if (element == null || "1".equals(element.getAttribute("data-java-bound"))) {
            return;
        }
        element.setAttribute("data-java-bound", "1");
        element.addEventListener("click", listener);
    }

    private void localizeAccessibility() {
        Element logo = this.toolDocument.querySelector(".logo");
        if (logo != null) {
            logo.setTextContent(DevToolsTranslations.translate("devtools.apricityui.title", new Object[0]));
        }
        this.setAttribute("#panelDragHandle", "aria-label", "devtools.apricityui.move");
        this.setAttribute("#saveBtn", "aria-label", "devtools.apricityui.save_current_html");
        this.setAttribute("#reloadDocumentBtn", "aria-label", "devtools.apricityui.reload_document");
        this.setAttribute("#metaButton", "aria-label", "devtools.apricityui.edit_meta");
        this.setAttribute("#pickBtn", "aria-label", "devtools.apricityui.inspect_elements");
        this.setAttribute(".console-btn", "aria-label", this.consoleMode ? "devtools.apricityui.inspect_elements" : "devtools.apricityui.console");
        this.setAttribute("#oreEditorButton", "aria-label", "tooltip.apricityui.ore_editor.open");
        this.setAttribute("#settingsButton", "aria-label", "tooltip.apricityui.devtools.settings");
        this.setAttribute("#closeDevToolsBtn", "aria-label", "tooltip.apricityui.devtools.close");
        this.setAttribute("#closeDocumentBtn", "aria-label", "tooltip.apricityui.devtools.close_document");
    }

    private void setAttribute(String selector, String attribute, String key) {
        Element element = this.toolDocument.querySelector(selector);
        if (element != null) {
            element.setAttribute(attribute, DevToolsTranslations.translate(key, new Object[0]));
        }
    }

    private void bindTooltipOnce(Element element, String translationKey) {
        if (element == null || "1".equals(element.getAttribute("data-tooltip-bound"))) {
            return;
        }
        element.setAttribute("data-tooltip-bound", "1");
        Tooltip.bindTranslation(element, translationKey);
    }

    private void bindConsoleTooltip(Element element) {
        String key;
        if (element == null) {
            return;
        }
        String string = key = this.consoleMode ? "tooltip.apricityui.devtools.inspect" : "tooltip.apricityui.devtools.console";
        if (element == this.consoleTooltipTarget && key.equals(this.consoleTooltipKey)) {
            return;
        }
        if (this.consoleTooltipBinding != null) {
            this.consoleTooltipBinding.close();
        }
        element.setAttribute("data-tooltip-key", key);
        element.setAttribute("data-tooltip-bound", "1");
        this.consoleTooltipBinding = Tooltip.bindTranslation(element, key);
        this.consoleTooltipTarget = element;
        this.consoleTooltipKey = key;
    }

    private void bindDocumentSelector(Element select) {
        if (select == null || "1".equals(select.getAttribute("data-document-bound"))) {
            return;
        }
        select.setAttribute("data-document-bound", "1");
        select.addEventListener("click", event -> this.syncDocumentSelector(select));
        select.addEventListener("change", event -> this.selectDocumentByUuid(select.getValue()));
    }

    private synchronized void closeTargetDocument() {
        Document closing = this.targetDocument;
        if (!this.isDebuggable(closing)) {
            this.refresh();
            return;
        }
        this.disconnectTargetObserver();
        this.saveDialog.close();
        this.metaDialog.close();
        this.closeCreateElementDialog();
        Tooltip.hide(this.toolDocument);
        Cursor.resetToDefault();
        this.consumeInspectMouseUp = false;
        closing.remove();
        this.disabledStyles.clear();
        this.disabledRuleStyles.clear();
        this.editHistory.clear();
        this.bindTarget(this.resolvePreferredTarget());
        this.selectedElementUuid = this.targetDocument == null ? null : this.targetDocument.body.uuid;
        this.refresh();
    }

    private synchronized void reloadTargetDocument() {
        Document document = this.targetDocument;
        if (!this.isDebuggable(document)) {
            this.showToast(DevToolsTranslations.translate("devtools.apricityui.select_document_first", new Object[0]));
            return;
        }
        this.disconnectTargetObserver();
        this.saveDialog.close();
        this.metaDialog.close();
        this.closeCreateElementDialog();
        Tooltip.hide(this.toolDocument);
        Cursor.resetToDefault();
        this.pickMode = false;
        this.treeHoverElementUuid = null;
        this.consumeInspectMouseUp = false;
        this.hideInspectHighlight();
        this.expandedNodes.clear();
        this.disabledStyles.clear();
        this.disabledRuleStyles.clear();
        this.editHistory.clear();
        this.selectedElementUuid = null;
        HTML.reload(document.getPath());
        document.refresh();
        if (!this.isOpen()) {
            return;
        }
        this.bindTarget(document);
        this.selectedElementUuid = this.targetDocument == null || this.targetDocument.body == null ? null : this.targetDocument.body.uuid;
        this.refresh();
        this.showToast(DevToolsTranslations.translate("devtools.apricityui.document_reloaded", document.getPath()));
    }

    private void bindHistoryShortcuts() {
        if (this.toolDocument == null || this.toolDocument.body == null || "1".equals(this.toolDocument.body.getAttribute("data-history-bound"))) {
            return;
        }
        this.toolDocument.body.setAttribute("data-history-bound", "1");
        this.toolDocument.body.addEventListener("keydown", event -> {
            KeyEvent keyEvent;
            block9: {
                block8: {
                    if (!(event instanceof KeyEvent)) break block8;
                    keyEvent = (KeyEvent)event;
                    if (keyEvent.controlKey || keyEvent.metaKey) break block9;
                }
                return;
            }
            if (this.toolDocument != null && this.toolDocument.getFocusedElement() instanceof AbstractText) {
                return;
            }
            boolean handled = false;
            if ("KeyZ".equals(keyEvent.code)) {
                handled = keyEvent.shiftKey ? this.redoEdit() : this.undoEdit();
            } else if ("KeyY".equals(keyEvent.code)) {
                handled = this.redoEdit();
            }
            if (handled) {
                event.preventDefault();
                event.stopPropagation();
            }
        });
    }

    private void requestSave() {
        Document document = this.targetDocument;
        Tooltip.hide(this.toolDocument);
        DevToolsDocumentStore.Resolution resolution = DevToolsDocumentStore.resolve(document);
        if (!resolution.writable()) {
            this.showToast(resolution.message());
            return;
        }
        if (this.skipSaveConfirmation) {
            this.saveDocument(document, resolution.target(), false);
            return;
        }
        this.saveDialog.open(this.toolDocument, resolution.target().relativePath(), options -> {
            if (options.skipConfirmation()) {
                this.skipSaveConfirmation = true;
            }
            this.saveDocument(document, resolution.target(), options.saveDomTree());
        });
    }

    private void openMetaEditor() {
        Document document = this.targetDocument;
        if (!this.isDebuggable(document)) {
            this.showToast(DevToolsTranslations.translate("devtools.apricityui.select_document_first", new Object[0]));
            return;
        }
        DevToolsDocumentStore.Resolution resolution = DevToolsDocumentStore.resolve(document);
        if (!resolution.writable()) {
            this.showToast(resolution.message());
            return;
        }
        Tooltip.hide(this.toolDocument);
        this.metaDialog.open(this.toolDocument, document.getPath(), resolution.target().file(), ClientLoader::reload, document.getViewport().zoom(), zoom -> FrameTaskScheduler.scheduleAfterFrames(3, deadlineNs -> {
            if (document.isActive()) {
                document.setViewportZoom((double)zoom);
            }
            return true;
        }));
    }

    private void saveDocument(Document document, DevToolsDocumentStore.SaveTarget target, boolean saveDomTree) {
        if (document == null || target == null || !document.isActive()) {
            this.showToast(DevToolsTranslations.translate("devtools.apricityui.document_unavailable", new Object[0]));
            return;
        }
        String original = DevToolsDocumentStore.read(target);
        if (original == null) {
            this.showToast(DevToolsTranslations.translate("devtools.apricityui.source_read_failed", new Object[0]));
            return;
        }
        DevToolsCssSerializer.Result prepared = DevToolsCssSerializer.prepare(document, original, target, ClientLoader.listFinalStaticResources(), AuiServices.client().isProduction(), saveDomTree);
        if (!prepared.success()) {
            this.showToast(prepared.message());
            return;
        }
        if (prepared.edits().isEmpty()) {
            this.showToast(DevToolsTranslations.translate("devtools.apricityui.no_css_changes", new Object[0]));
            return;
        }
        for (DevToolsCssSerializer.Edit edit : prepared.edits()) {
            DevToolsDocumentStore.SaveResult result = DevToolsDocumentStore.save(edit.target(), edit.content());
            if (result.success()) continue;
            this.showToast(DevToolsTranslations.translate("devtools.apricityui.source_save_failed", edit.target().relativePath()));
            return;
        }
        this.showToast(DevToolsTranslations.translate("devtools.apricityui.saved", target.relativePath()));
    }

    boolean undoEdit() {
        return this.finishHistoryAction(this.editHistory.undo(this.targetDocument), DevToolsTranslations.translate("devtools.apricityui.undo", new Object[0]));
    }

    boolean redoEdit() {
        return this.finishHistoryAction(this.editHistory.redo(this.targetDocument), DevToolsTranslations.translate("devtools.apricityui.redo", new Object[0]));
    }

    private boolean finishHistoryAction(DevToolsEditHistory.Applied applied, String action) {
        if (applied == null || this.targetDocument == null) {
            return false;
        }
        this.showToast(action + " \u00b7 " + applied.description());
        this.refresh();
        return true;
    }

    private boolean restoreElementSnapshot(Document document, UUID elementUuid, DevToolsEditHistory.Snapshot snapshot) {
        if (document == null || snapshot == null || !document.isActive()) {
            return false;
        }
        Element target = DevToolsController.findElement(document, elementUuid);
        if (target == null) {
            return false;
        }
        for (String string : new ArrayList<String>(target.getAttributes().keySet())) {
            target.removeAttribute(string);
        }
        for (Map.Entry entry : snapshot.attributes().entrySet()) {
            target.setAttribute((String)entry.getKey(), (String)entry.getValue());
        }
        LinkedHashMap<String, String> disabled = this.disabledStyleMap(target);
        disabled.clear();
        disabled.putAll(snapshot.disabledStyles());
        DevToolsController.syncRuntimeInlineStyleCache(target);
        target.document.markDirty(target, 15);
        this.selectedElementUuid = target.uuid;
        return true;
    }

    private void bindPanelDrag(Element handle) {
        if (handle == null || this.toolDocument == null || "1".equals(handle.getAttribute("data-panel-drag-bound"))) {
            return;
        }
        handle.setAttribute("data-panel-drag-bound", "1");
        handle.addEventListener("mousedown", event -> {
            MouseEvent mouseEvent;
            block5: {
                block4: {
                    if (!(event instanceof MouseEvent)) break block4;
                    mouseEvent = (MouseEvent)event;
                    if (mouseEvent.button == 0) break block5;
                }
                return;
            }
            Element panel = this.toolDocument.querySelector(".side-panel");
            if (panel == null) {
                return;
            }
            this.draggingPanel = true;
            this.panelDragOffsetX = mouseEvent.clientX - Position.of((Element)panel).x;
            this.toolDocument.setPressedElement(handle);
            Tooltip.hide(this.toolDocument);
            handle.setAttribute("class", "top-btn drag-handle dragging");
            event.preventDefault();
            event.stopPropagation();
        });
        handle.addEventListener("mousemove", this::movePanel);
        handle.addEventListener("mouseup", event -> this.endPanelDrag());
    }

    private void movePanel(Event event) {
        MouseEvent mouseEvent;
        block5: {
            block4: {
                if (!this.draggingPanel || !(event instanceof MouseEvent)) break block4;
                mouseEvent = (MouseEvent)event;
                if (this.toolDocument != null) break block5;
            }
            return;
        }
        Element panel = this.toolDocument.querySelector(".side-panel");
        if (panel == null) {
            return;
        }
        double panelWidth = Size.of(panel).width();
        double viewportWidth = this.toolDocument.getViewport().layoutWidth();
        double maxLeft = Math.max(0.0, viewportWidth - panelWidth);
        double left = Math.max(0.0, Math.min(maxLeft, mouseEvent.clientX - this.panelDragOffsetX));
        panel.setAttribute("style", "left:" + String.format(Locale.ROOT, "%.2fpx", left) + ";right:auto;");
        event.preventDefault();
        event.stopImmediatePropagation();
    }

    private void endPanelDrag() {
        if (!this.draggingPanel || this.toolDocument == null) {
            return;
        }
        this.draggingPanel = false;
        Element handle = this.toolDocument.querySelector("#panelDragHandle");
        if (handle != null) {
            handle.setAttribute("class", "top-btn drag-handle");
        }
    }

    private void syncDocumentSelector(Element select) {
        if (select == null || this.toolDocument == null) {
            return;
        }
        List<Document> documents = this.debuggableDocuments();
        StringBuilder signature = new StringBuilder();
        for (Document document : documents) {
            signature.append(document.getUuid()).append('\n').append(document.getPath()).append('\n');
        }
        String nextSignature = signature.toString();
        if (!nextSignature.equals(select.getAttribute("data-document-signature"))) {
            select.clearChildren();
            for (Document document : documents) {
                Element option = Element.init(this.toolDocument.createElement("OPTION"));
                option.setAttribute("value", document.getUuid().toString());
                option.setTextContent(DevToolsController.documentLabel(document));
                select.append(option);
            }
            select.setAttribute("data-document-signature", nextSignature);
        }
        select.setValue(this.targetDocument == null ? "" : this.targetDocument.getUuid().toString());
    }

    private synchronized void selectDocumentByUuid(String uuid) {
        Document selected;
        Document document = selected = uuid == null || uuid.isBlank() ? null : Document.getByUUID(uuid);
        if (!this.isDebuggable(selected)) {
            this.refresh();
            return;
        }
        if (selected == this.targetDocument) {
            return;
        }
        this.bindTarget(selected);
        this.selectedElementUuid = selected.body.uuid;
        this.refresh();
    }

    private void resizeInspector(Event event) {
        MouseEvent mouseEvent;
        block5: {
            block4: {
                if (!this.resizingInspector || !(event instanceof MouseEvent)) break block4;
                mouseEvent = (MouseEvent)event;
                if (this.toolDocument != null) break block5;
            }
            return;
        }
        Element sidePanel = this.toolDocument.querySelector(".side-panel");
        Element section = this.toolDocument.querySelector("#inspectorSection");
        if (sidePanel == null || section == null) {
            return;
        }
        double panelBottom = Position.of((Element)sidePanel).y + Size.of(sidePanel).height();
        double maxHeight = Math.max(120.0, Size.of(sidePanel).height() - 150.0);
        double height = Math.max(120.0, Math.min(maxHeight, panelBottom - mouseEvent.clientY));
        section.setAttribute("style", "height:" + String.format(Locale.ROOT, "%.2fpx", height) + ";");
        DevToolsDom.markDirty(this.toolDocument);
        event.preventDefault();
    }

    private void updateShellState() {
        boolean active;
        Element closeDocumentButton;
        Element reloadDocumentButton;
        Element metaButton;
        Element saveButton;
        if (this.toolDocument == null) {
            return;
        }
        Element consoleButton = this.toolDocument.querySelector(".console-btn");
        if (consoleButton != null) {
            consoleButton.setAttribute("class", this.consoleMode ? "top-btn console-btn mode-console" : "top-btn console-btn");
            consoleButton.setAttribute("aria-pressed", Boolean.toString(this.consoleMode));
            consoleButton.setAttribute("aria-label", DevToolsTranslations.translate(this.consoleMode ? "devtools.apricityui.inspect_elements" : "devtools.apricityui.console", new Object[0]));
            this.bindConsoleTooltip(consoleButton);
        }
        this.setPanelVisibility(".document-selector-bar", !this.consoleMode);
        this.setPanelVisibility("#domSection", !this.consoleMode);
        this.setPanelVisibility("#inspectorSection", !this.consoleMode);
        this.setPanelVisibility("#consoleContent", this.consoleMode);
        Element pickButton = this.toolDocument.querySelector("#pickBtn");
        if (pickButton != null) {
            pickButton.setAttribute("class", this.pickMode ? "top-btn active" : "top-btn");
        }
        if ((saveButton = this.toolDocument.querySelector("#saveBtn")) != null) {
            DevToolsDocumentStore.Resolution resolution = DevToolsDocumentStore.resolve(this.targetDocument);
            if (resolution.writable()) {
                saveButton.removeAttribute("disabled");
                saveButton.setAttribute("aria-disabled", "false");
            } else {
                saveButton.setAttribute("disabled", "disabled");
                saveButton.setAttribute("aria-disabled", "true");
            }
        }
        if ((metaButton = this.toolDocument.querySelector("#metaButton")) != null) {
            DevToolsDocumentStore.Resolution resolution = DevToolsDocumentStore.resolve(this.targetDocument);
            if (resolution.writable()) {
                metaButton.removeAttribute("disabled");
                metaButton.setAttribute("aria-disabled", "false");
            } else {
                metaButton.setAttribute("disabled", "disabled");
                metaButton.setAttribute("aria-disabled", "true");
            }
        }
        if ((reloadDocumentButton = this.toolDocument.querySelector("#reloadDocumentBtn")) != null) {
            if (this.isDebuggable(this.targetDocument)) {
                reloadDocumentButton.removeAttribute("disabled");
                reloadDocumentButton.setAttribute("aria-disabled", "false");
            } else {
                reloadDocumentButton.setAttribute("disabled", "disabled");
                reloadDocumentButton.setAttribute("aria-disabled", "true");
            }
        }
        if ((closeDocumentButton = this.toolDocument.querySelector("#closeDocumentBtn")) != null) {
            if (this.isDebuggable(this.targetDocument)) {
                closeDocumentButton.removeAttribute("disabled");
                closeDocumentButton.setAttribute("aria-disabled", "false");
            } else {
                closeDocumentButton.setAttribute("disabled", "disabled");
                closeDocumentButton.setAttribute("aria-disabled", "true");
            }
        }
        Iterator<Element> iterator = this.toolDocument.querySelectorAll(".inspector-tab").iterator();
        while (iterator.hasNext()) {
            Element tab;
            active = this.inspectorTab.id.equalsIgnoreCase((tab = iterator.next()).getAttribute("data-tab"));
            tab.setAttribute("class", active ? "inspector-tab active" : "inspector-tab");
        }
        for (Element pane : this.toolDocument.querySelectorAll(".inspector-pane")) {
            active = ("pane-" + this.inspectorTab.id).equals(pane.id);
            pane.setAttribute("class", active ? "inspector-pane active" : "inspector-pane");
        }
    }

    private void setPanelVisibility(String selector, boolean visible) {
        Element element = this.toolDocument.querySelector(selector);
        if (element == null) {
            return;
        }
        String current = element.getAttribute("class");
        String[] tokens = current == null ? new String[]{} : current.trim().split("\\s+");
        LinkedHashSet<String> next = new LinkedHashSet<String>();
        for (String token : tokens) {
            if (token.isBlank() || "hidden".equals(token)) continue;
            next.add(token);
        }
        if (!visible) {
            next.add("hidden");
        }
        element.setAttribute("class", String.join((CharSequence)" ", next));
    }

    private Element inspectHit(Position screenPosition) {
        if (screenPosition == null || !this.isDebuggable(this.targetDocument)) {
            return null;
        }
        if (this.targetDocument.inWorld) {
            WorldWindow worldWindow = WorldWindow.findByDocument(this.targetDocument);
            Position documentPosition = worldWindow == null ? null : worldWindow.getDocumentPositionAtScreen(screenPosition);
            return documentPosition == null ? null : this.targetDocument.hitTest(documentPosition);
        }
        return this.targetDocument.hitTest(this.targetDocument.screenToDocumentPosition(screenPosition));
    }

    private boolean isOverToolPanel(Position screenPosition) {
        if (screenPosition == null || this.toolDocument == null) {
            return false;
        }
        this.cacheInspectShellElements();
        Element panel = this.inspectPanelElement;
        if (panel == null) {
            return false;
        }
        Position local = this.toolDocument.screenToDocumentPosition(screenPosition);
        Element.DOMRect rect = panel.getBoundingClientRect();
        return local.x >= rect.left && local.x <= rect.right && local.y >= rect.top && local.y <= rect.bottom;
    }

    private void showInspectHighlight(Element element) {
        if (element == null || this.toolDocument == null || this.targetDocument == null) {
            this.hideInspectHighlight();
            return;
        }
        this.cacheInspectShellElements();
        Element highlight = this.inspectHighlightElement;
        Element label = this.inspectHighlightLabelElement;
        if (highlight == null || label == null) {
            return;
        }
        Element.DOMRect rect = element.getBoundingClientRect();
        Box box = Box.of(element);
        double marginLeft = Math.max(0.0, box.getMarginLeft());
        double marginTop = Math.max(0.0, box.getMarginTop());
        double marginRight = Math.max(0.0, box.getMarginRight());
        double marginBottom = Math.max(0.0, box.getMarginBottom());
        double borderLeft = Math.max(0.0, box.getBorderLeft());
        double borderTop = Math.max(0.0, box.getBorderTop());
        double borderRight = Math.max(0.0, box.getBorderRight());
        double borderBottom = Math.max(0.0, box.getBorderBottom());
        double paddingLeft = Math.max(0.0, box.getPaddingLeft());
        double paddingTop = Math.max(0.0, box.getPaddingTop());
        double paddingRight = Math.max(0.0, box.getPaddingRight());
        double paddingBottom = Math.max(0.0, box.getPaddingBottom());
        this.setBoxModelBands("inspectMargin", rect.x - marginLeft, rect.y - marginTop, rect.width + marginLeft + marginRight, rect.height + marginTop + marginBottom, marginTop, marginRight, marginBottom, marginLeft);
        this.setBoxModelBands("inspectBorder", rect.x, rect.y, rect.width, rect.height, borderTop, borderRight, borderBottom, borderLeft);
        double paddingBoxX = rect.x + borderLeft;
        double paddingBoxY = rect.y + borderTop;
        double paddingBoxWidth = Math.max(0.0, rect.width - borderLeft - borderRight);
        double paddingBoxHeight = Math.max(0.0, rect.height - borderTop - borderBottom);
        this.setBoxModelBands("inspectPadding", paddingBoxX, paddingBoxY, paddingBoxWidth, paddingBoxHeight, paddingTop, paddingRight, paddingBottom, paddingLeft);
        this.setBoxModelRegion("inspectContent", paddingBoxX + paddingLeft, paddingBoxY + paddingTop, Math.max(0.0, paddingBoxWidth - paddingLeft - paddingRight), Math.max(0.0, paddingBoxHeight - paddingTop - paddingBottom));
        Position outerScreen = this.projectTargetPosition(new Position(rect.x - marginLeft, rect.y - marginTop));
        if (outerScreen == null) {
            this.hideInspectHighlight();
            return;
        }
        Position outerLocal = this.toolDocument.screenToDocumentPosition(outerScreen);
        double labelTop = outerLocal.y < 20.0 ? outerLocal.y : outerLocal.y - 20.0;
        String labelStyle = String.format(Locale.ROOT, "left:%.2fpx;top:%.2fpx;", outerLocal.x, labelTop);
        String labelText = DevToolsController.inspectLabel(element, rect);
        if (!"inspect-highlight show".equals(highlight.getAttribute("class"))) {
            highlight.setAttribute("class", "inspect-highlight show");
        }
        if (!labelStyle.equals(label.getAttribute("style"))) {
            label.setAttribute("style", labelStyle);
        }
        if (!labelText.equals(label.getTextContent())) {
            label.setTextContent(labelText);
        }
    }

    private void setBoxModelBands(String prefix, double x, double y, double width, double height, double top, double right, double bottom, double left) {
        double safeWidth = Math.max(0.0, width);
        double safeHeight = Math.max(0.0, height);
        double safeTop = Math.min(Math.max(0.0, top), safeHeight);
        double safeBottom = Math.min(Math.max(0.0, bottom), Math.max(0.0, safeHeight - safeTop));
        double middleHeight = Math.max(0.0, safeHeight - safeTop - safeBottom);
        double safeLeft = Math.min(Math.max(0.0, left), safeWidth);
        double safeRight = Math.min(Math.max(0.0, right), Math.max(0.0, safeWidth - safeLeft));
        this.setBoxModelRegion(prefix + "Top", x, y, safeWidth, safeTop);
        this.setBoxModelRegion(prefix + "Right", x + safeWidth - safeRight, y + safeTop, safeRight, middleHeight);
        this.setBoxModelRegion(prefix + "Bottom", x, y + safeHeight - safeBottom, safeWidth, safeBottom);
        this.setBoxModelRegion(prefix + "Left", x, y + safeTop, safeLeft, middleHeight);
    }

    private void setBoxModelRegion(String id, double x, double y, double width, double height) {
        String style;
        this.cacheInspectShellElements();
        Element region = this.inspectBoxRegionElements.get(id);
        if (region == null) {
            return;
        }
        if (width <= 0.0 || height <= 0.0) {
            style = "left:0px;top:0px;width:0px;height:0px;";
        } else {
            Position screen;
            WorldWindow.ScreenRect projected;
            WorldWindow worldWindow = this.targetDocument != null && this.targetDocument.inWorld ? WorldWindow.findByDocument(this.targetDocument) : null;
            WorldWindow.ScreenRect screenRect = projected = worldWindow == null ? null : worldWindow.projectDocumentRect(x, y, width, height);
            if (worldWindow != null && projected == null) {
                String style2 = "left:0px;top:0px;width:0px;height:0px;";
                if (!style2.equals(region.getAttribute("style"))) {
                    region.setAttribute("style", style2);
                }
                return;
            }
            Position position = screen = projected == null ? this.projectTargetPosition(new Position(x, y)) : new Position(projected.x(), projected.y());
            if (screen == null) {
                String style3 = "left:0px;top:0px;width:0px;height:0px;";
                if (!style3.equals(region.getAttribute("style"))) {
                    region.setAttribute("style", style3);
                }
                return;
            }
            Position local = this.toolDocument.screenToDocumentPosition(screen);
            double screenWidth = projected == null ? width * this.targetDocument.getViewportScaleX() : projected.width();
            double screenHeight = projected == null ? height * this.targetDocument.getViewportScaleY() : projected.height();
            style = String.format(Locale.ROOT, "left:%.2fpx;top:%.2fpx;width:%.2fpx;height:%.2fpx;", local.x, local.y, screenWidth / this.toolDocument.getViewportScaleX(), screenHeight / this.toolDocument.getViewportScaleY());
        }
        if (!style.equals(region.getAttribute("style"))) {
            region.setAttribute("style", style);
        }
    }

    private Position projectTargetPosition(Position documentPosition) {
        if (this.targetDocument == null || documentPosition == null) {
            return null;
        }
        if (this.targetDocument.inWorld) {
            WorldWindow worldWindow = WorldWindow.findByDocument(this.targetDocument);
            return worldWindow == null ? null : worldWindow.projectDocumentPosition(documentPosition);
        }
        return this.targetDocument.documentToScreenPosition(documentPosition);
    }

    private void hideInspectHighlight() {
        String hiddenStyle;
        if (this.toolDocument == null) {
            return;
        }
        this.cacheInspectShellElements();
        Element highlight = this.inspectHighlightElement;
        if (highlight != null && !"inspect-highlight".equals(highlight.getAttribute("class"))) {
            highlight.setAttribute("class", "inspect-highlight");
        }
        for (String id : BOX_MODEL_REGION_IDS) {
            String hiddenStyle2;
            Element region = this.inspectBoxRegionElements.get(id);
            if (region == null || (hiddenStyle2 = "left:0px;top:0px;width:0px;height:0px;").equals(region.getAttribute("style"))) continue;
            region.setAttribute("style", hiddenStyle2);
        }
        Element label = this.inspectHighlightLabelElement;
        if (label != null && !(hiddenStyle = "left:-10000px;top:-10000px;").equals(label.getAttribute("style"))) {
            label.setAttribute("style", hiddenStyle);
        }
    }

    private void clearTreeHover() {
        this.treeHoverElementUuid = null;
        this.hideInspectHighlight();
    }

    private static String inspectLabel(Element element, Element.DOMRect rect) {
        StringBuilder label = new StringBuilder(element.tagName.toLowerCase(Locale.ROOT));
        if (element.id != null && !element.id.isBlank()) {
            label.append('#').append(element.id);
        }
        for (String className : element.getClassNames()) {
            label.append('.').append(className);
        }
        label.append(' ').append(Math.round(rect.width)).append(" x ").append(Math.round(rect.height));
        return label.toString();
    }

    private DevToolsEditHistory.Snapshot editSnapshot(Element target) {
        return this.editHistory.snapshot(target, target == null ? Map.of() : this.disabledStyleMap(target));
    }

    private void afterTargetEdit(Element target, DevToolsEditHistory.Snapshot before, String toast) {
        if (target == null || target.document == null) {
            return;
        }
        DevToolsController.syncRuntimeInlineStyleCache(target);
        DevToolsEditHistory.Snapshot after = this.editSnapshot(target);
        if (!before.equals(after)) {
            Document document = target.document;
            UUID elementUuid = target.uuid;
            this.editHistory.record(document, () -> this.restoreElementSnapshot(document, elementUuid, before), () -> this.restoreElementSnapshot(document, elementUuid, after), toast);
        }
        target.document.markDirty(target, 15);
        this.showToast(toast);
        this.refresh();
    }

    private void applyInlineStyles(Element target, LinkedHashMap<String, String> styles) {
        String serialized = InlineStyleDeclaration.serialize(styles);
        if (serialized.isBlank()) {
            target.removeAttribute("style");
        } else {
            target.setAttribute("style", serialized);
        }
        DevToolsController.syncRuntimeInlineStyleCache(target);
    }

    private LinkedHashMap<String, String> disabledStyleMap(Element target) {
        if (target == null) {
            return new LinkedHashMap<String, String>();
        }
        return this.disabledStyles.computeIfAbsent(target.uuid, ignored -> new LinkedHashMap());
    }

    private boolean canEditRule(Element target, int ruleOrder) {
        return target != null && target.document != null && target.document == this.targetDocument && DevToolsController.findDebugRule(target.document, ruleOrder) != null;
    }

    private static CSS.DebugRule findDebugRule(Document document, int ruleOrder) {
        if (document == null) {
            return null;
        }
        for (CSS.DebugRule rule : document.CSSDebugRules) {
            if (rule == null || rule.order() != ruleOrder) continue;
            return rule;
        }
        return null;
    }

    private LinkedHashMap<RuleDeclarationKey, CSS.Declaration> disabledRuleStyleMap(Document document) {
        if (document == null) {
            return new LinkedHashMap<RuleDeclarationKey, CSS.Declaration>();
        }
        return this.disabledRuleStyles.computeIfAbsent(document.getUuid(), ignored -> new LinkedHashMap());
    }

    private StylesheetSnapshot stylesheetSnapshot(Document document) {
        ArrayList<CSS.DebugRule> rules = new ArrayList<CSS.DebugRule>();
        if (document != null) {
            for (CSS.DebugRule rule : document.CSSDebugRules) {
                rules.add(DevToolsController.copyDebugRule(rule));
            }
        }
        return new StylesheetSnapshot(List.copyOf(rules), Map.copyOf(document == null ? Map.of() : this.disabledRuleStyleMap(document)));
    }

    private static CSS.DebugRule copyDebugRule(CSS.DebugRule rule) {
        return new CSS.DebugRule(rule.selector(), rule.properties(), rule.sourcePath(), rule.order());
    }

    private void afterStylesheetEdit(Document document, StylesheetSnapshot before, String toast) {
        if (document == null || before == null) {
            return;
        }
        DevToolsController.rebuildStylesheet(document);
        StylesheetSnapshot after = this.stylesheetSnapshot(document);
        if (!before.equals(after)) {
            this.editHistory.record(document, () -> this.restoreStylesheetSnapshot(document, before), () -> this.restoreStylesheetSnapshot(document, after), toast);
        }
        this.showToast(toast);
        this.refresh();
    }

    private boolean restoreStylesheetSnapshot(Document document, StylesheetSnapshot snapshot) {
        if (document == null || snapshot == null || !document.isActive()) {
            return false;
        }
        document.CSSDebugRules.clear();
        for (CSS.DebugRule rule : snapshot.rules()) {
            document.CSSDebugRules.add(DevToolsController.copyDebugRule(rule));
        }
        LinkedHashMap<RuleDeclarationKey, CSS.Declaration> disabled = this.disabledRuleStyleMap(document);
        disabled.clear();
        disabled.putAll(snapshot.disabled());
        DevToolsController.rebuildStylesheet(document);
        return true;
    }

    private static void rebuildStylesheet(Document document) {
        CSS.rebuildCacheFromDebugRules(document.CSSDebugRules, document.CSSCache);
        document.rebuildSelectorIndex();
        document.reapplyStylesFromCache();
    }

    private static CSS.Declaration parseRuleDeclaration(String raw) {
        String value = raw == null ? "" : raw.trim();
        String lower = value.toLowerCase(Locale.ROOT);
        boolean important = lower.endsWith("!important");
        if (important) {
            value = value.substring(0, value.length() - "!important".length()).trim();
        }
        return new CSS.Declaration(value, important);
    }

    private static void syncRuntimeInlineStyleCache(Element target) {
        if (target == null) {
            return;
        }
        String raw = target.getAttribute("style");
        if (target.getRuntimeCache("bound-base-inline-style") != null) {
            target.putRuntimeCache("bound-base-inline-style", raw == null ? "" : raw);
        }
        if (target.getRuntimeCache("bound-last-inline-style") != null) {
            target.putRuntimeCache("bound-last-inline-style", raw == null ? "" : raw);
        }
    }

    private Document resolvePreferredTarget() {
        try {
            AuiLinkedScreen screen;
            Screen screen2;
            Minecraft minecraft = Minecraft.m_91087_();
            if (minecraft != null && (screen2 = minecraft.f_91080_) instanceof AuiLinkedScreen && this.isDebuggable((screen = (AuiLinkedScreen)screen2).getLinkedDocument())) {
                return screen.getLinkedDocument();
            }
        }
        catch (LinkageError | RuntimeException minecraft) {
            // empty catch block
        }
        ArrayList<Document> documents = new ArrayList<Document>(Document.getAll());
        for (int index = documents.size() - 1; index >= 0; --index) {
            Document document = (Document)documents.get(index);
            if (!this.isDebuggable(document)) continue;
            return document;
        }
        return null;
    }

    private List<Document> debuggableDocuments() {
        ArrayList<Document> documents = new ArrayList<Document>();
        for (Document document : Document.getAll()) {
            if (!this.isDebuggable(document)) continue;
            documents.add(document);
        }
        return documents;
    }

    private static String documentLabel(Document document) {
        String uuid = document.getUuid().toString();
        return document.getPath() + " [" + uuid.substring(0, 4) + "]";
    }

    private boolean isDebuggable(Document document) {
        return document != null && document != this.toolDocument && document.isActive() && document.body != null && !PATH.equals(document.getPath()) && !DevToolsController.isInternalCursorOverlay(document);
    }

    private static boolean isInternalCursorOverlay(Document document) {
        if (document == null || document.body == null) {
            return false;
        }
        if (document.body.getClassNames().contains("cursor-overlay-body")) {
            return true;
        }
        return document.querySelector("#baeffect-cursor-layer.cursor-layer") != null;
    }

    private static Element findElement(Document document, UUID uuid) {
        if (document == null || uuid == null) {
            return null;
        }
        if (document.documentElement != null && uuid.equals(document.documentElement.uuid)) {
            return document.documentElement;
        }
        if (document.head != null && uuid.equals(document.head.uuid)) {
            return document.head;
        }
        if (document.body != null && uuid.equals(document.body.uuid)) {
            return document.body;
        }
        for (Element element : document.getElements()) {
            if (element == null || !uuid.equals(element.uuid)) continue;
            return element;
        }
        return null;
    }

    private void revealAncestors(Element element) {
        Element current;
        Element element2 = current = element == null ? null : element.parentElement;
        while (current != null) {
            this.expandedNodes.add(current.uuid);
            current = current.parentElement;
        }
    }

    private void resetTreeExpansion() {
        this.expandedNodes.clear();
        if (this.targetDocument == null) {
            return;
        }
        if (this.targetDocument.documentElement != null) {
            this.expandedNodes.add(this.targetDocument.documentElement.uuid);
        }
        if (this.targetDocument.body != null) {
            this.expandedNodes.add(this.targetDocument.body.uuid);
        }
    }

    private void scheduleTreeReveal(UUID elementUuid) {
        if (elementUuid == null) {
            return;
        }
        FrameTaskScheduler.scheduleAfterFrames(1, deadlineNs -> {
            DevToolsController devToolsController = this;
            synchronized (devToolsController) {
                if (!this.isOpen() || !elementUuid.equals(this.selectedElementUuid)) {
                    return true;
                }
                this.revealTreeRow(elementUuid);
            }
            return true;
        });
    }

    private void revealTreeRow(UUID elementUuid) {
        Element row;
        Element domTree = this.toolDocument == null ? null : this.toolDocument.querySelector("#domTree");
        Element element = row = domTree == null ? null : domTree.querySelector(".dom-node[data-node-id=\"" + elementUuid + "\"]");
        if (domTree == null || row == null) {
            return;
        }
        Element.DOMRect treeRect = domTree.getBoundingClientRect();
        Element.DOMRect rowRect = row.getBoundingClientRect();
        double rowCenter = rowRect.top + rowRect.height / 2.0;
        double viewportCenter = treeRect.top + treeRect.height / 2.0;
        domTree.setScrollTop(domTree.getScrollTop() + rowCenter - viewportCenter);
    }

    private void refreshTree() {
        if (!this.isOpen()) {
            return;
        }
        Element domTree = this.toolDocument.querySelector("#domTree");
        Element nodeCount = this.toolDocument.querySelector("#nodeCount");
        if (domTree == null || nodeCount == null) {
            return;
        }
        this.tree.render(domTree, nodeCount, this.targetDocument, this.selectedElement());
        this.toolDocument.markDirty(domTree, 15);
    }

    static enum InspectorTab {
        ATTRIBUTES("attributes"),
        STYLES("styles"),
        BOXMODEL("boxmodel");

        final String id;

        private InspectorTab(String id) {
            this.id = id;
        }

        static InspectorTab parse(String value) {
            if (value != null) {
                for (InspectorTab tab : InspectorTab.values()) {
                    if (!tab.id.equalsIgnoreCase(value.trim())) continue;
                    return tab;
                }
            }
            return ATTRIBUTES;
        }
    }

    private record StylesheetSnapshot(List<CSS.DebugRule> rules, Map<RuleDeclarationKey, CSS.Declaration> disabled) {
    }

    private record RuleDeclarationKey(int ruleOrder, String property) {
    }

    record RuleStyle(String value, boolean important, boolean overridden, boolean disabled) {
        String displayValue() {
            return this.value + (this.important ? " !important" : "");
        }
    }
}

