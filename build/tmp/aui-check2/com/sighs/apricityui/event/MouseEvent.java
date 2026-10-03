/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.event;

import com.sighs.apricityui.event.Event;
import com.sighs.apricityui.init.Document;
import com.sighs.apricityui.init.Element;
import com.sighs.apricityui.layout.Box;
import com.sighs.apricityui.layout.Position;
import com.sighs.apricityui.layout.Size;
import com.sighs.apricityui.render.DocumentLayerOrder;
import com.sighs.apricityui.render.GeometryQueryScope;
import com.sighs.apricityui.render.Operation;
import com.sighs.apricityui.render.Rect;
import com.sighs.apricityui.render.RenderNode;
import com.sighs.apricityui.style.Cursor;
import com.sighs.apricityui.style.Interaction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicBoolean;

public class MouseEvent
extends Event
implements Cloneable {
    public static final int DOM_DELTA_PIXEL = 0;
    public static final int PRIMARY_POINTER_ID = 1;
    private static final long DOUBLE_CLICK_WINDOW_NS = 500000000L;
    private NativeDispatchState nativeDispatchState = new NativeDispatchState();
    public double clientX = 0.0;
    public double clientY = 0.0;
    public double pageX = 0.0;
    public double pageY = 0.0;
    public double offsetX = 0.0;
    public double offsetY = 0.0;
    public double movementX = 0.0;
    public double movementY = 0.0;
    public boolean altKey;
    public boolean shiftKey;
    public boolean controlKey;
    public double deltaX = 0.0;
    public double deltaY = 0.0;
    public int deltaMode = 0;
    public double scrollDelta = 0.0;
    public int button = -1;
    public int buttons = 0;
    public int pointerId = 1;
    public String pointerType = "mouse";
    public boolean isPrimary = true;
    public int clickCount = 0;
    public boolean activeElementRedirect = false;

    public boolean getCtrlKey() {
        return this.controlKey;
    }

    public MouseEvent(String type, Position mousePosition) {
        this(type, mousePosition, -1);
    }

    public MouseEvent(String type, Position mousePosition, int button) {
        this(type, mousePosition, button, true);
    }

    public MouseEvent(String type, Position mousePosition, int button, boolean readEnvironmentState) {
        super(null, type, true);
        if (mousePosition == null) {
            mousePosition = Position.ZERO;
        }
        this.clientX = mousePosition.x;
        this.clientY = mousePosition.y;
        this.pageX = this.clientX;
        this.pageY = this.clientY;
        if (readEnvironmentState) {
            this.altKey = MouseEvent.isModifierPressed("key.keyboard.left.alt") || MouseEvent.isModifierPressed("key.keyboard.right.alt");
            this.shiftKey = MouseEvent.isModifierPressed("key.keyboard.left.shift") || MouseEvent.isModifierPressed("key.keyboard.right.shift");
            this.controlKey = MouseEvent.isModifierPressed("key.keyboard.left.control") || MouseEvent.isModifierPressed("key.keyboard.right.control");
            this.buttons = MouseEvent.resolveButtons();
        } else {
            this.altKey = false;
            this.shiftKey = false;
            this.controlKey = false;
            this.buttons = 0;
        }
        this.button = button;
    }

    public void consumeNative() {
        this.nativeDispatchState.consumed = true;
    }

    public boolean isNativeConsumed() {
        return this.nativeDispatchState.consumed;
    }

    public static boolean tiggerEvent(MouseEvent event) {
        try (GeometryQueryScope geometryScope = GeometryQueryScope.open();){
            Cursor.refreshFromDocuments(new Position(event.clientX, event.clientY));
            List<Document> docs = DocumentLayerOrder.frontToBack(Document.getAll());
            if (docs == null || docs.isEmpty()) {
                boolean bl = false;
                return bl;
            }
            for (Document document : docs) {
                if (document == null || document.inWorld || document.isManuallyRendered()) continue;
                boolean passThroughWheel = "wheel".equals(event.type) && !document.interceptsMouseEvents();
                MouseEvent documentEvent = passThroughWheel ? event.clone() : event;
                boolean consumed = MouseEvent.tiggerEvent(documentEvent, document);
                if (documentEvent.isNativeConsumed()) {
                    boolean bl = true;
                    return bl;
                }
                if (consumed && !passThroughWheel) {
                    boolean bl = true;
                    return bl;
                }
                if (!document.interceptsMouseEventsAt(new Position(event.clientX, event.clientY))) continue;
                boolean bl = true;
                return bl;
            }
            boolean bl = false;
            return bl;
        }
    }

    public static boolean tiggerEvent(MouseEvent event, Document document) {
        try (GeometryQueryScope geometryScope = GeometryQueryScope.open();){
            Document.ContextScope ignored = Document.withContext(document);
            try {
                double originalClientX = event == null ? 0.0 : event.clientX;
                double originalClientY = event == null ? 0.0 : event.clientY;
                event = MouseEvent.adaptToDocumentViewport(event, document);
                Element activeElement = document.getPressedElement();
                Position detectionPos = new Position(event.clientX, event.clientY);
                Element target = document.hitTest(detectionPos);
                boolean consumed = MouseEvent.triggerResolvedEvent(event, document, target, activeElement, true);
                if (document.interceptsMouseEventsAt(new Position(originalClientX, originalClientY))) {
                    event.consumeNative();
                }
                boolean bl = consumed;
                if (ignored != null) {
                    ignored.close();
                }
                return bl;
            }
            catch (Throwable throwable) {
                if (ignored != null) {
                    try {
                        ignored.close();
                    }
                    catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                }
                throw throwable;
            }
        }
    }

    private static MouseEvent adaptToDocumentViewport(MouseEvent event, Document document) {
        if (event == null || document == null) {
            return event;
        }
        if (Math.abs(document.getViewportScaleX() - 1.0) < 1.0E-6 && Math.abs(document.getViewportScaleY() - 1.0) < 1.0E-6) {
            return event;
        }
        MouseEvent adapted = event.clone();
        Position documentPosition = document.screenToDocumentPosition(new Position(event.clientX, event.clientY));
        adapted.clientX = documentPosition.x;
        adapted.clientY = documentPosition.y;
        adapted.pageX = documentPosition.x;
        adapted.pageY = documentPosition.y;
        adapted.movementX = event.movementX / document.getViewportScaleX();
        adapted.movementY = event.movementY / document.getViewportScaleY();
        adapted.deltaX = event.deltaX / document.getViewportScaleX();
        adapted.deltaY = event.deltaY / document.getViewportScaleY();
        adapted.scrollDelta = event.scrollDelta / document.getViewportScaleY();
        return adapted;
    }

    public static boolean dispatchToTarget(MouseEvent event, Document document, Element target) {
        try (GeometryQueryScope geometryScope = GeometryQueryScope.open();){
            Document.ContextScope ignored = Document.withContext(document);
            try {
                boolean bl = MouseEvent.triggerResolvedEvent(event, document, target, document == null ? null : document.getPressedElement(), false);
                if (ignored != null) {
                    ignored.close();
                }
                return bl;
            }
            catch (Throwable throwable) {
                if (ignored != null) {
                    try {
                        ignored.close();
                    }
                    catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                }
                throw throwable;
            }
        }
    }

    private static void clearGlobalFocusExcept(Document keepFocusDoc) {
        Document.getAll().forEach(doc -> {
            if (doc != keepFocusDoc) {
                doc.clearFocus();
            }
        });
    }

    private static void clearGlobalSelectionsOnMouseDown(Document activeDoc, Element clickedTarget) {
        for (Document doc : Document.getAll()) {
            if (doc == null || doc == activeDoc) continue;
            doc.clearAllTextSelections();
        }
        if (clickedTarget == null && activeDoc != null) {
            activeDoc.clearAllTextSelections();
        }
    }

    private static void handleHoverChange(MouseEvent originalEvent, Element newTarget, Document document) {
        Element previousCursorElement = document.getPreviousCursorElement();
        if (previousCursorElement == newTarget) {
            return;
        }
        List<Element> oldChain = previousCursorElement != null ? previousCursorElement.getRoute() : Collections.emptyList();
        List newChain = newTarget != null ? newTarget.getRoute() : Collections.emptyList();
        for (Element element : oldChain) {
            if (newChain.contains(element)) continue;
            element.setHover(false);
            MouseEvent out = originalEvent.clone();
            out.type = "mouseout";
            out.target = element;
            Event.triggerSingle(out);
            MouseEvent.dispatchPointerCompatEvent(out, element, true);
            MouseEvent leave = originalEvent.clone();
            leave.type = "mouseleave";
            leave.target = element;
            Event.triggerSingle(leave);
            MouseEvent.dispatchPointerCompatEvent(leave, element, true);
        }
        for (int i = newChain.size() - 1; i >= 0; --i) {
            Element element;
            element = (Element)newChain.get(i);
            element.setHover(true);
            if (oldChain.contains(element)) continue;
            MouseEvent over = originalEvent.clone();
            over.type = "mouseover";
            over.target = element;
            Event.triggerSingle(over);
            MouseEvent.dispatchPointerCompatEvent(over, element, true);
            MouseEvent enter = originalEvent.clone();
            enter.type = "mouseenter";
            enter.target = element;
            Event.triggerSingle(enter);
            MouseEvent.dispatchPointerCompatEvent(enter, element, true);
        }
        document.setPreviousCursorElement(newTarget);
    }

    private static void scroll(MouseEvent event) {
        Element target = MouseEvent.resolveScrollTarget(event);
        if (target == null) {
            return;
        }
        if (event.shiftKey) {
            if (target.canScrollHorizontally()) {
                target.setScrollLeft(target.getTargetScrollLeft() + event.scrollDelta);
            } else {
                target.setScrollTop(target.getTargetScrollTop() + event.scrollDelta);
            }
        } else {
            target.setScrollTop(target.getTargetScrollTop() + event.scrollDelta);
        }
        if (target.document != null) {
            target.document.markDirty(target, 1);
        }
    }

    private static Element resolveScrollTarget(MouseEvent event) {
        Object object;
        if (event == null || !((object = event.target) instanceof Element)) {
            return null;
        }
        Element targetElement = (Element)object;
        ArrayList<Element> route = targetElement.getRoute();
        if (event.shiftKey) {
            Element horizontalEligible = null;
            Element verticalFallback = null;
            for (Element element : route) {
                if (element.hasHorizontalScrollRange()) {
                    return element;
                }
                if (horizontalEligible == null && element.canScrollHorizontally()) {
                    horizontalEligible = element;
                }
                if (verticalFallback != null || !element.hasVerticalScrollRange()) continue;
                verticalFallback = element;
            }
            if (horizontalEligible != null) {
                return horizontalEligible;
            }
            return verticalFallback;
        }
        Element eligible = null;
        for (Element element : route) {
            if (element.hasVerticalScrollRange()) {
                return element;
            }
            if (eligible != null || !element.canScrollVertically()) continue;
            eligible = element;
        }
        return eligible;
    }

    private static boolean applyScrollDefault(MouseEvent event) {
        Element target = MouseEvent.resolveScrollTarget(event);
        if (target == null) {
            return false;
        }
        double beforeLeft = target.getTargetScrollLeft();
        double beforeTop = target.getTargetScrollTop();
        MouseEvent.scroll(event);
        boolean changed = Double.compare(beforeLeft, target.getTargetScrollLeft()) != 0 || Double.compare(beforeTop, target.getTargetScrollTop()) != 0;
        target.dispatchScrollEventIfChanged(beforeLeft, beforeTop);
        return changed;
    }

    private static boolean triggerResolvedEvent(MouseEvent event, Document document, Element target, Element activeElement, boolean resolveGeometry) {
        boolean consumed = false;
        if (resolveGeometry && target != null) {
            Position targetPosition = MouseEvent.resolveHitBoxPosition(target);
            event.offsetX = event.clientX - targetPosition.x;
            event.offsetY = event.clientY - targetPosition.y;
        }
        event.target = target;
        if (event.type.equals("mousemove")) {
            MouseEvent.handleHoverChange(event, target, document);
        }
        if (event.type.equals("mousedown") && target != null && target.handleScrollbarMouseDown(event)) {
            document.setPressedElement(target);
            return true;
        }
        if (event.type.equals("mousemove") && activeElement != null && activeElement.isScrollbarInteractionActive() && activeElement.handleScrollbarMouseMove(event)) {
            return true;
        }
        if (event.type.equals("mouseup") && activeElement != null && activeElement.isScrollbarInteractionActive() && activeElement.handleScrollbarMouseUp(event)) {
            document.setPressedElement(null);
            return true;
        }
        if (event.type.equals("mousedown")) {
            MouseEvent.clearGlobalSelectionsOnMouseDown(document, target);
            event.clickCount = document.advanceClickSequence(target, event.button, event.clientX, event.clientY, System.nanoTime(), 500000000L);
            Event.runWithEventTrust(event, () -> {
                if (target != null) {
                    document.setPressedElement(target);
                    if (target.canFocus()) {
                        MouseEvent.clearGlobalFocusExcept(document);
                        document.setFocusedElement(target);
                    } else {
                        document.setFocusedElement(null);
                    }
                }
            });
        }
        if (target != null) {
            consumed |= Event.tiggerEvent(event);
            consumed |= MouseEvent.dispatchPointerCompatEvent(event, target, false);
        }
        if (target != null && event.type.equals("wheel") && !event.defaultPrevented) {
            AtomicBoolean scrollConsumed = new AtomicBoolean(false);
            Event.runWithEventTrust(event, () -> scrollConsumed.set(MouseEvent.applyScrollDefault(event)));
            consumed |= scrollConsumed.get();
        }
        if ((event.type.equals("mousemove") || event.type.equals("mouseup")) && activeElement != null && activeElement != target) {
            MouseEvent activeEvent = event.clone();
            activeEvent.target = activeElement;
            activeEvent.activeElementRedirect = true;
            if (resolveGeometry) {
                Position activePosition = MouseEvent.resolveHitBoxPosition(activeElement);
                activeEvent.offsetX = activeEvent.clientX - activePosition.x;
                activeEvent.offsetY = activeEvent.clientY - activePosition.y;
            }
            consumed |= Event.triggerSingle(activeEvent);
        }
        if (event.type.equals("mouseup")) {
            AtomicBoolean followupConsumed = new AtomicBoolean(false);
            Event.runWithEventTrust(event, () -> {
                followupConsumed.set(MouseEvent.dispatchMouseUpFollowupEvents(document, event, target, activeElement));
                document.setPressedElement(null);
            });
            consumed |= followupConsumed.get();
        }
        return consumed;
    }

    private static boolean dispatchMouseUpFollowupEvents(Document document, MouseEvent originalEvent, Element target, Element activeElement) {
        if (document == null || target == null || activeElement == null) {
            return false;
        }
        Element activationTarget = MouseEvent.nearestCommonInclusiveAncestor(activeElement, target);
        if (activationTarget == null || activeElement.isDisabled() || target.isDisabled() || activationTarget.isDisabled()) {
            return false;
        }
        boolean consumed = false;
        if (originalEvent.button == 0) {
            Element defaultActionTarget;
            MouseEvent click = originalEvent.clone();
            click.type = "click";
            click.clickCount = document.getClickCount();
            click.target = activationTarget;
            click.cancelable = true;
            consumed |= Event.tiggerEvent(click);
            if (!click.defaultPrevented && (defaultActionTarget = activationTarget.resolveClickActivationTarget()) != null && !defaultActionTarget.isDisabled()) {
                defaultActionTarget.handleClickDefault();
                consumed = true;
            }
            if (document.registerClickAndCheckDoubleClick(activationTarget, originalEvent.button, System.nanoTime(), 500000000L)) {
                MouseEvent dblclick = originalEvent.clone();
                dblclick.type = "dblclick";
                dblclick.clickCount = 2;
                dblclick.target = activationTarget;
                dblclick.cancelable = true;
                consumed |= Event.tiggerEvent(dblclick);
            }
        } else if (originalEvent.button == 1) {
            MouseEvent contextmenu = originalEvent.clone();
            contextmenu.type = "contextmenu";
            contextmenu.target = activationTarget;
            contextmenu.cancelable = true;
            consumed |= Event.tiggerEvent(contextmenu);
        }
        return consumed;
    }

    private static Element nearestCommonInclusiveAncestor(Element first, Element second) {
        if (first == null || second == null) {
            return null;
        }
        Element candidate = first;
        while (candidate != null) {
            if (candidate.contains(second)) {
                return candidate;
            }
            candidate = candidate.parentElement;
        }
        return null;
    }

    private static boolean dispatchPointerCompatEvent(MouseEvent source, Element target, boolean singleTargetOnly) {
        String compatType;
        if (source == null || target == null) {
            return false;
        }
        switch (source.type) {
            case "mousedown": {
                String string = "pointerdown";
                break;
            }
            case "mouseup": {
                String string = "pointerup";
                break;
            }
            case "mousemove": {
                String string = "pointermove";
                break;
            }
            case "mouseover": {
                String string = "pointerover";
                break;
            }
            case "mouseout": {
                String string = "pointerout";
                break;
            }
            case "mouseenter": {
                String string = "pointerenter";
                break;
            }
            case "mouseleave": {
                String string = "pointerleave";
                break;
            }
            default: {
                String string = compatType = null;
            }
        }
        if (compatType == null) {
            return false;
        }
        MouseEvent pointerEvent = source.clone();
        pointerEvent.type = compatType;
        pointerEvent.target = target;
        if ("pointerenter".equals(compatType) || "pointerleave".equals(compatType)) {
            pointerEvent.bubbles = false;
            singleTargetOnly = true;
        }
        return singleTargetOnly ? Event.triggerSingle(pointerEvent) : Event.tiggerEvent(pointerEvent);
    }

    public static boolean checkCursor(Element element, Position mousePos) {
        try (GeometryQueryScope geometryScope = GeometryQueryScope.open();){
            boolean bl = MouseEvent.checkCursorInScope(element, mousePos);
            return bl;
        }
    }

    private static boolean checkCursorInScope(Element element, Position mousePos) {
        if (mousePos == null) {
            return false;
        }
        Position hitBoxPosition = MouseEvent.resolveHitBoxPosition(element);
        Size hitBoxSize = MouseEvent.resolveHitBoxSize(element);
        return mousePos.x >= hitBoxPosition.x && mousePos.x <= hitBoxPosition.x + hitBoxSize.width() && mousePos.y >= hitBoxPosition.y && mousePos.y <= hitBoxPosition.y + hitBoxSize.height();
    }

    private static Position resolveHitBoxPosition(Element element) {
        if (element == null) {
            return Position.ZERO;
        }
        Rect committed = element.getRenderer().getCommittedRect();
        if (committed != null) {
            if (MouseEvent.usesBodyHitBox(element)) {
                return committed.getBodyRectPosition();
            }
            return new Position(committed.position.x + committed.box.getMarginLeft(), committed.position.y + committed.box.getMarginTop());
        }
        if (MouseEvent.usesBodyHitBox(element)) {
            return Rect.of(element).getBodyRectPosition();
        }
        Position position = Position.of(element);
        Box box = Box.of(element);
        return new Position(position.x + box.getMarginLeft(), position.y + box.getMarginTop());
    }

    private static Size resolveHitBoxSize(Element element) {
        if (element == null) {
            return Size.ZERO;
        }
        Rect committed = element.getRenderer().getCommittedRect();
        if (committed != null) {
            return MouseEvent.usesBodyHitBox(element) ? committed.getBodyRectSize() : committed.getElementSize();
        }
        if (MouseEvent.usesBodyHitBox(element)) {
            return Rect.of(element).getBodyRectSize();
        }
        return Size.of(element);
    }

    private static boolean usesBodyHitBox(Element element) {
        return element != null && "IMG".equals(element.tagName);
    }

    public static Element hitTest(List<RenderNode> paintOrder, Position cursorPosition) {
        try (GeometryQueryScope geometryScope = GeometryQueryScope.open();){
            Element element = MouseEvent.hitTestInScope(paintOrder, cursorPosition);
            return element;
        }
    }

    private static Element hitTestInScope(List<RenderNode> paintOrder, Position cursorPosition) {
        if (paintOrder == null || paintOrder.isEmpty()) {
            return null;
        }
        Stack<Element> clipStack = new Stack<Element>();
        for (int i = paintOrder.size() - 1; i >= 0; --i) {
            RenderNode.ElementPhaseNode phaseNode;
            Element element;
            RenderNode node = paintOrder.get(i);
            if (node instanceof RenderNode.MaskPopNode) {
                RenderNode.MaskPopNode popNode = (RenderNode.MaskPopNode)node;
                clipStack.push(popNode.target());
                continue;
            }
            if (node instanceof RenderNode.MaskPushNode) {
                RenderNode.MaskPushNode pushNode = (RenderNode.MaskPushNode)node;
                if (clipStack.isEmpty() || clipStack.peek() != pushNode.target()) continue;
                clipStack.pop();
                continue;
            }
            if (!(node instanceof RenderNode.ElementPhaseNode) || !Interaction.isDisplayed(element = (phaseNode = (RenderNode.ElementPhaseNode)node).target()) || !element.isVisible || !element.isPointerEnabled || !MouseEvent.checkCursorInScope(element, cursorPosition)) continue;
            boolean isClipped = false;
            for (Element mask : clipStack) {
                if (MouseEvent.checkCursorInScope(mask, cursorPosition)) continue;
                isClipped = true;
                break;
            }
            if (isClipped) continue;
            return element;
        }
        return null;
    }

    @Override
    public MouseEvent clone() {
        MouseEvent copy = new MouseEvent(this.type, new Position(this.clientX, this.clientY), this.button, false);
        this.copyTo(copy);
        copy.clientX = this.clientX;
        copy.clientY = this.clientY;
        copy.pageX = this.pageX;
        copy.pageY = this.pageY;
        copy.offsetX = this.offsetX;
        copy.offsetY = this.offsetY;
        copy.movementX = this.movementX;
        copy.movementY = this.movementY;
        copy.altKey = this.altKey;
        copy.shiftKey = this.shiftKey;
        copy.controlKey = this.controlKey;
        copy.deltaX = this.deltaX;
        copy.deltaY = this.deltaY;
        copy.deltaMode = this.deltaMode;
        copy.scrollDelta = this.scrollDelta;
        copy.button = this.button;
        copy.buttons = this.buttons;
        copy.pointerId = this.pointerId;
        copy.pointerType = this.pointerType;
        copy.isPrimary = this.isPrimary;
        copy.clickCount = this.clickCount;
        copy.activeElementRedirect = this.activeElementRedirect;
        copy.nativeDispatchState = this.nativeDispatchState;
        return copy;
    }

    private static boolean isModifierPressed(String key) {
        try {
            return Operation.isKeyPressed(key);
        }
        catch (Throwable ignored) {
            return false;
        }
    }

    private static int resolveButtons() {
        try {
            return Operation.getMouseButtons();
        }
        catch (Throwable ignored) {
            return 0;
        }
    }

    private static final class NativeDispatchState {
        private boolean consumed;

        private NativeDispatchState() {
        }
    }
}

