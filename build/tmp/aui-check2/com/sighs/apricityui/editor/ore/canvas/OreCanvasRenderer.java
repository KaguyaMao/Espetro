/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.editor.ore.canvas;

import com.sighs.apricityui.editor.ore.OreEditorDom;
import com.sighs.apricityui.editor.ore.canvas.OreFlexInsertionResolver;
import com.sighs.apricityui.editor.ore.model.OreCanvasNode;
import com.sighs.apricityui.editor.ore.model.OreComponentNode;
import com.sighs.apricityui.editor.ore.model.OreContainerNode;
import com.sighs.apricityui.editor.ore.model.OreEditorProject;
import com.sighs.apricityui.event.MouseEvent;
import com.sighs.apricityui.init.Document;
import com.sighs.apricityui.init.Element;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public final class OreCanvasRenderer {
    private final Document document;
    private final Element canvas;
    private final Consumer<UUID> selectionConsumer;
    private final BiConsumer<UUID, MouseEvent> dragStartConsumer;
    private final BiConsumer<UUID, MouseEvent> resizeStartConsumer;
    private final Map<UUID, Element> elements = new HashMap<UUID, Element>();
    private final Map<UUID, Element> emptyHints = new HashMap<UUID, Element>();
    private Element selectionOverlay;
    private Element hoverOverlay;
    private Element insertionOverlay;
    private Element resizeHandle;
    private Element flexOverlay;

    public OreCanvasRenderer(Document document, Element canvas, Consumer<UUID> selectionConsumer) {
        this(document, canvas, selectionConsumer, null);
    }

    public OreCanvasRenderer(Document document, Element canvas, Consumer<UUID> selectionConsumer, BiConsumer<UUID, MouseEvent> dragStartConsumer) {
        this(document, canvas, selectionConsumer, dragStartConsumer, null);
    }

    public OreCanvasRenderer(Document document, Element canvas, Consumer<UUID> selectionConsumer, BiConsumer<UUID, MouseEvent> dragStartConsumer, BiConsumer<UUID, MouseEvent> resizeStartConsumer) {
        this.document = document;
        this.canvas = canvas;
        this.selectionConsumer = selectionConsumer == null ? ignored -> {} : selectionConsumer;
        this.dragStartConsumer = dragStartConsumer == null ? (ignored, event) -> {} : dragStartConsumer;
        this.resizeStartConsumer = resizeStartConsumer == null ? (ignored, event) -> {} : resizeStartConsumer;
    }

    public Element elementFor(UUID id) {
        return this.elements.get(id);
    }

    public Map<UUID, Element> elements() {
        return Map.copyOf(this.elements);
    }

    public void renderInsertion(OreFlexInsertionResolver.Insertion insertion) {
        if (insertion == null) {
            if (this.insertionOverlay != null) {
                this.insertionOverlay.remove();
            }
            this.insertionOverlay = null;
            return;
        }
        if (this.insertionOverlay == null) {
            this.insertionOverlay = Element.init(this.document.createElement("DIV"));
            this.insertionOverlay.setAttribute("class", "editor-insertion-overlay");
            this.insertionOverlay.setAttribute("data-ore-editor-ui", "insertion");
        }
        Element.DOMRect canvasRect = this.canvas.getBoundingClientRect();
        String style = insertion.row() ? "left:" + (insertion.coordinate() - canvasRect.x - 1.0) + "px;top:" + (insertion.crossStart() - canvasRect.y) + "px;width:3px;height:" + Math.max(16.0, insertion.crossSize()) + "px;" : "left:" + (insertion.crossStart() - canvasRect.x) + "px;top:" + (insertion.coordinate() - canvasRect.y - 1.0) + "px;width:" + Math.max(16.0, insertion.crossSize()) + "px;height:3px;";
        this.insertionOverlay.setAttribute("style", style);
        this.canvas.appendChild(this.insertionOverlay);
        this.document.markDirty(this.insertionOverlay, 13);
    }

    public void render(OreEditorProject project, UUID selected) {
        this.render(project, selected, null);
    }

    public void render(OreEditorProject project, UUID selected, UUID hovered) {
        if (project == null || this.canvas == null) {
            return;
        }
        this.canvas.setAttribute("style", project.theme().toCss());
        HashSet<UUID> seen = new HashSet<UUID>();
        Element root = this.renderNode(project.root(), seen);
        if (root != null) {
            this.canvas.appendChild(root);
        }
        for (UUID id : new ArrayList<UUID>(this.elements.keySet())) {
            Element hint;
            if (seen.contains(id)) continue;
            Element removed = this.elements.remove(id);
            if (removed != null) {
                removed.remove();
            }
            if ((hint = this.emptyHints.remove(id)) == null) continue;
            hint.remove();
        }
        try {
            this.renderOverlay(selected, "editor-selection-overlay", true);
            this.renderOverlay((UUID)(hovered != null && hovered.equals(selected) ? null : hovered), "editor-hover-overlay", false);
            this.renderFlexOverlay(project, selected);
            this.renderResizeHandle(project, selected);
        }
        catch (LinkageError ignored) {
            this.clearEditorOverlays();
        }
        this.document.markDirty(this.canvas, 15);
    }

    private Element renderNode(OreCanvasNode node, Set<UUID> seen) {
        seen.add(node.id());
        Element element = this.elements.computeIfAbsent(node.id(), ignored -> this.createElement(node));
        element.setAttribute("data-ore-node-id", node.id().toString());
        element.setAttribute("data-ore-node-type", node instanceof OreContainerNode ? "container" : "component");
        element.setAttribute("style", this.styleFor(node, element));
        if (node instanceof OreContainerNode) {
            OreContainerNode container = (OreContainerNode)node;
            if (container.children().isEmpty()) {
                Element hint = this.emptyHints.computeIfAbsent(container.id(), ignored -> this.emptyHint());
                element.appendChild(hint);
            } else {
                Element hint = this.emptyHints.remove(container.id());
                if (hint != null) {
                    hint.remove();
                }
                for (OreCanvasNode child : container.children()) {
                    element.appendChild(this.renderNode(child, seen));
                }
            }
        } else if (node instanceof OreComponentNode) {
            OreComponentNode component = (OreComponentNode)node;
            element.setTextContent(component.content());
        }
        return element;
    }

    private Element emptyHint() {
        Element hint = OreEditorDom.translation(this.document, "ore_editor.apricityui.empty.container", "editor-empty-container");
        hint.setAttribute("data-ore-editor-ui", "empty-container");
        return hint;
    }

    private Element createElement(OreCanvasNode node) {
        String string;
        if (node instanceof OreComponentNode) {
            OreComponentNode component = (OreComponentNode)node;
            string = component.type();
        } else {
            string = ((OreContainerNode)node).tag();
        }
        String tag = string;
        Element element = Element.init(this.document.createElement(tag));
        node.attributes().forEach(element::setAttribute);
        String editorClass = node instanceof OreContainerNode ? "ore-editor-container" : this.componentClass((OreComponentNode)node);
        String rawClass = element.getAttribute("class");
        String sourceClass = rawClass == null ? "" : rawClass.trim();
        element.setAttribute("class", (String)(sourceClass.isEmpty() ? editorClass : sourceClass + " " + editorClass));
        element.addEventListener("click", event -> {
            event.preventDefault();
            event.stopPropagation();
            this.selectionConsumer.accept(node.id());
        });
        element.addEventListener("mousedown", event -> {
            if (event instanceof MouseEvent) {
                MouseEvent mouseEvent = (MouseEvent)event;
                this.dragStartConsumer.accept(node.id(), mouseEvent);
            }
        });
        if (node instanceof OreComponentNode) {
            OreComponentNode component = (OreComponentNode)node;
            this.bindStateRefresh(element, component);
        }
        return element;
    }

    private void bindStateRefresh(Element element, OreComponentNode component) {
        for (String eventName : new String[]{"mouseover", "mouseout", "mousedown", "mouseup", "focus", "blur"}) {
            element.addEventListener(eventName, event -> this.refreshComponentStyle(element, component));
        }
    }

    private void refreshComponentStyle(Element element, OreComponentNode component) {
        element.setAttribute("style", this.styleFor(component, element));
        this.document.markDirty(element, 13);
    }

    private String componentClass(OreComponentNode component) {
        return "button".equals(component.type()) ? "button button-normal ore-editor-component" : "ore-editor-component";
    }

    private String styleFor(OreCanvasNode node, Element element) {
        StringBuilder style = new StringBuilder();
        if (node instanceof OreContainerNode) {
            OreContainerNode container = (OreContainerNode)node;
            style.append("display:flex;position:relative;").append("flex-direction:").append(container.flex().direction()).append(';').append("flex-wrap:").append(container.flex().wrap()).append(';').append("justify-content:").append(container.flex().justifyContent()).append(';').append("align-items:").append(container.flex().alignItems()).append(';').append("align-content:").append(container.flex().alignContent()).append(';').append("gap:").append(container.flex().gap()).append(';').append("row-gap:").append(container.flex().rowGap()).append(';').append("column-gap:").append(container.flex().columnGap()).append(';');
            if (container.isRoot()) {
                style.append("min-height:100%;width:100%;");
            }
        }
        node.style().properties().forEach((key, value) -> style.append((String)key).append(':').append((String)value).append(';'));
        if (node instanceof OreComponentNode) {
            OreComponentNode component = (OreComponentNode)node;
            OreComponentNode.VisualState state = this.stateFor(element);
            component.stateStyle(state).properties().forEach((key, value) -> style.append((String)key).append(':').append((String)value).append(';'));
        }
        return style.toString();
    }

    private OreComponentNode.VisualState stateFor(Element element) {
        if (element.isDisabled()) {
            return OreComponentNode.VisualState.DISABLED;
        }
        if (element.isActive) {
            return OreComponentNode.VisualState.ACTIVE;
        }
        if (element.isFocus) {
            return OreComponentNode.VisualState.FOCUS;
        }
        if (element.isHover) {
            return OreComponentNode.VisualState.HOVER;
        }
        return OreComponentNode.VisualState.DEFAULT;
    }

    private void renderOverlay(UUID id, String className, boolean selection) {
        Element overlay;
        Element element = overlay = selection ? this.selectionOverlay : this.hoverOverlay;
        if (id == null || !this.elements.containsKey(id)) {
            if (overlay != null) {
                overlay.remove();
            }
            if (selection) {
                this.selectionOverlay = null;
            } else {
                this.hoverOverlay = null;
            }
            return;
        }
        if (overlay == null) {
            overlay = Element.init(this.document.createElement("DIV"));
            overlay.setAttribute("class", className);
            if (selection) {
                this.selectionOverlay = overlay;
            } else {
                this.hoverOverlay = overlay;
            }
        }
        Element target = this.elements.get(id);
        Element.DOMRect targetRect = target.getBoundingClientRect();
        Element.DOMRect canvasRect = this.canvas.getBoundingClientRect();
        overlay.setAttribute("style", "left:" + (targetRect.x - canvasRect.x) + "px;top:" + (targetRect.y - canvasRect.y) + "px;width:" + targetRect.width + "px;height:" + targetRect.height + "px;");
        this.canvas.appendChild(overlay);
    }

    private void renderResizeHandle(OreEditorProject project, UUID selected) {
        OreComponentNode component;
        OreCanvasNode node;
        OreCanvasNode oreCanvasNode = node = project == null || selected == null ? null : project.find(selected);
        if (!(node instanceof OreComponentNode && (component = (OreComponentNode)node).absolute() && this.elements.containsKey(selected))) {
            if (this.resizeHandle != null) {
                this.resizeHandle.remove();
            }
            this.resizeHandle = null;
            return;
        }
        if (this.resizeHandle == null) {
            this.resizeHandle = Element.init(this.document.createElement("DIV"));
            this.resizeHandle.setAttribute("class", "editor-absolute-resize-handle");
            this.resizeHandle.setAttribute("data-ore-editor-ui", "absolute-resize");
            this.resizeHandle.addEventListener("mousedown", event -> {
                String id = this.resizeHandle.getAttribute("data-ore-node-id");
                if (event instanceof MouseEvent) {
                    MouseEvent mouseEvent = (MouseEvent)event;
                    if (id != null) {
                        this.resizeStartConsumer.accept(UUID.fromString(id), mouseEvent);
                    }
                }
            });
        }
        Element.DOMRect targetRect = this.elements.get(selected).getBoundingClientRect();
        Element.DOMRect canvasRect = this.canvas.getBoundingClientRect();
        this.resizeHandle.setAttribute("style", "left:" + (targetRect.x - canvasRect.x + targetRect.width - 5.0) + "px;top:" + (targetRect.y - canvasRect.y + targetRect.height - 5.0) + "px;");
        this.resizeHandle.setAttribute("data-ore-node-id", selected.toString());
        this.canvas.appendChild(this.resizeHandle);
    }

    private void clearEditorOverlays() {
        if (this.selectionOverlay != null) {
            this.selectionOverlay.remove();
        }
        if (this.hoverOverlay != null) {
            this.hoverOverlay.remove();
        }
        if (this.insertionOverlay != null) {
            this.insertionOverlay.remove();
        }
        if (this.resizeHandle != null) {
            this.resizeHandle.remove();
        }
        if (this.flexOverlay != null) {
            this.flexOverlay.remove();
        }
        this.flexOverlay = null;
        this.resizeHandle = null;
        this.insertionOverlay = null;
        this.hoverOverlay = null;
        this.selectionOverlay = null;
    }

    private void renderFlexOverlay(OreEditorProject project, UUID selected) {
        OreContainerNode container;
        block7: {
            block6: {
                OreCanvasNode node;
                if (this.flexOverlay != null) {
                    this.flexOverlay.remove();
                }
                this.flexOverlay = null;
                OreCanvasNode oreCanvasNode = node = project == null || selected == null ? null : project.find(selected);
                if (!(node instanceof OreContainerNode)) break block6;
                container = (OreContainerNode)node;
                if (this.elements.containsKey(selected)) break block7;
            }
            return;
        }
        Element target = this.elements.get(selected);
        Element.DOMRect targetRect = target.getBoundingClientRect();
        Element.DOMRect canvasRect = this.canvas.getBoundingClientRect();
        boolean row = !container.flex().direction().startsWith("column");
        this.flexOverlay = Element.init(this.document.createElement("DIV"));
        this.flexOverlay.setAttribute("class", "editor-flex-overlay");
        this.flexOverlay.setAttribute("data-ore-editor-ui", "flex-overlay");
        this.addOverlayPart(row ? "editor-flex-main-axis horizontal" : "editor-flex-main-axis vertical", row ? targetRect.left - canvasRect.x : targetRect.left - canvasRect.x + 4.0, row ? targetRect.top - canvasRect.y + 4.0 : targetRect.top - canvasRect.y, row ? targetRect.width : 1.0, row ? 1.0 : targetRect.height);
        this.addOverlayPart(row ? "editor-flex-cross-axis vertical" : "editor-flex-cross-axis horizontal", row ? targetRect.left - canvasRect.x + 4.0 : targetRect.left - canvasRect.x, row ? targetRect.top - canvasRect.y : targetRect.top - canvasRect.y + 4.0, row ? 1.0 : targetRect.width, row ? targetRect.height : 1.0);
        ArrayList<Element> children = new ArrayList<Element>();
        for (OreCanvasNode oreCanvasNode : container.children()) {
            Element element = this.elements.get(oreCanvasNode.id());
            if (element == null || "absolute".equals(element.getComputedStyle().position)) continue;
            children.add(element);
        }
        for (List list : this.flexLines(children, row)) {
            this.renderFlexLine(list, row, canvasRect);
            this.renderFlexGaps(list, row, canvasRect);
        }
        this.canvas.appendChild(this.flexOverlay);
    }

    private List<List<Element>> flexLines(List<Element> children, boolean row) {
        ArrayList<Element> ordered = new ArrayList<Element>(children);
        ordered.sort(Comparator.comparingDouble(element -> OreCanvasRenderer.crossCenter(element.getBoundingClientRect(), row)));
        ArrayList<List<Element>> lines = new ArrayList<List<Element>>();
        for (Element child : ordered) {
            if (lines.isEmpty() || Math.abs(OreCanvasRenderer.lineCrossCenter((List)lines.get(lines.size() - 1), row) - OreCanvasRenderer.crossCenter(child.getBoundingClientRect(), row)) > 2.0) {
                lines.add(new ArrayList());
            }
            ((List)lines.get(lines.size() - 1)).add(child);
        }
        return lines;
    }

    private void renderFlexLine(List<Element> line, boolean row, Element.DOMRect canvasRect) {
        if (line.isEmpty()) {
            return;
        }
        double left = line.stream().map(Element::getBoundingClientRect).mapToDouble(rect -> rect.left).min().orElse(0.0);
        double top = line.stream().map(Element::getBoundingClientRect).mapToDouble(rect -> rect.top).min().orElse(0.0);
        double right = line.stream().map(Element::getBoundingClientRect).mapToDouble(rect -> rect.right).max().orElse(left);
        double bottom = line.stream().map(Element::getBoundingClientRect).mapToDouble(rect -> rect.bottom).max().orElse(top);
        this.addOverlayPart("editor-flex-line-overlay", left - canvasRect.x, top - canvasRect.y, Math.max(1.0, right - left), Math.max(1.0, bottom - top));
    }

    private void renderFlexGaps(List<Element> line, boolean row, Element.DOMRect canvasRect) {
        ArrayList<Element> ordered = new ArrayList<Element>(line);
        ordered.sort(Comparator.comparingDouble(element -> OreCanvasRenderer.mainStart(element.getBoundingClientRect(), row)));
        for (int index = 1; index < ordered.size(); ++index) {
            Element.DOMRect previous = ((Element)ordered.get(index - 1)).getBoundingClientRect();
            Element.DOMRect next = ((Element)ordered.get(index)).getBoundingClientRect();
            double gap = OreCanvasRenderer.mainStart(next, row) - OreCanvasRenderer.mainEnd(previous, row);
            if (gap <= 1.0) continue;
            if (row) {
                this.addOverlayPart("editor-flex-gap-overlay", previous.right - canvasRect.x, Math.min(previous.top, next.top) - canvasRect.y, gap, Math.max(previous.bottom, next.bottom) - Math.min(previous.top, next.top));
                continue;
            }
            this.addOverlayPart("editor-flex-gap-overlay", Math.min(previous.left, next.left) - canvasRect.x, previous.bottom - canvasRect.y, Math.max(previous.right, next.right) - Math.min(previous.left, next.left), gap);
        }
    }

    private void addOverlayPart(String className, double left, double top, double width, double height) {
        Element part = Element.init(this.document.createElement("DIV"));
        part.setAttribute("class", className);
        part.setAttribute("style", "left:" + left + "px;top:" + top + "px;width:" + width + "px;height:" + height + "px;");
        this.flexOverlay.appendChild(part);
    }

    private static double mainStart(Element.DOMRect rect, boolean row) {
        return row ? rect.left : rect.top;
    }

    private static double mainEnd(Element.DOMRect rect, boolean row) {
        return row ? rect.right : rect.bottom;
    }

    private static double crossCenter(Element.DOMRect rect, boolean row) {
        return row ? rect.top + rect.height / 2.0 : rect.left + rect.width / 2.0;
    }

    private static double lineCrossCenter(List<Element> line, boolean row) {
        return line.stream().map(Element::getBoundingClientRect).mapToDouble(rect -> OreCanvasRenderer.crossCenter(rect, row)).average().orElse(0.0);
    }
}

