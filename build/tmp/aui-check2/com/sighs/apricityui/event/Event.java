/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.event;

import com.sighs.apricityui.ApricityUI;
import com.sighs.apricityui.init.Document;
import com.sighs.apricityui.init.Node;
import com.sighs.apricityui.render.ClipboardDataBridge;
import com.sighs.apricityui.util.AuiLog;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Event
implements Cloneable {
    private static final ThreadLocal<Integer> TRUSTED_CONTEXT_DEPTH = ThreadLocal.withInitial(() -> 0);
    public static final short NONE = 0;
    public static final short CAPTURING_PHASE = 1;
    public static final short AT_TARGET = 2;
    public static final short BUBBLING_PHASE = 3;
    public Object target;
    public Object currentTarget;
    public String type;
    public boolean bubbles = true;
    public boolean cancelable = false;
    public boolean defaultPrevented = false;
    public Object detail = null;
    public Object submitter = null;
    public Object formData = null;
    public short eventPhase = 0;
    public boolean cancelBubble = false;
    public boolean returnValue = true;
    public boolean isTrusted = false;
    public ClipboardDataBridge clipboardData = null;
    public double timeStamp = (double)System.nanoTime() / 1000000.0;
    private boolean propagationStopped = false;
    private boolean immediatePropagationStopped = false;
    private ArrayList<Object> composedPath = new ArrayList();

    public Event(Object target, String type) {
        this.target = target;
        this.currentTarget = target;
        this.type = type;
    }

    public Event(Object target, String type, boolean bubbles) {
        this(target, type);
        this.bubbles = bubbles;
    }

    public Event(Object currentTarget, String type, Consumer<Event> listener, boolean useCapture) {
        this(currentTarget, type);
    }

    public Event(Object currentTarget, String type, Consumer<Event> listener, boolean useCapture, boolean internal) {
        this(currentTarget, type);
    }

    public void stopPropagation() {
        this.cancelBubble = true;
        this.propagationStopped = true;
    }

    public void stopImmediatePropagation() {
        this.cancelBubble = true;
        this.immediatePropagationStopped = true;
        this.propagationStopped = true;
    }

    public void preventDefault() {
        if (this.cancelable) {
            this.defaultPrevented = true;
            this.returnValue = false;
        }
    }

    public boolean isPropagationStopped() {
        return this.propagationStopped;
    }

    public boolean isImmediatePropagationStopped() {
        return this.immediatePropagationStopped;
    }

    public void resetForDispatch(Object dispatchTarget) {
        if (dispatchTarget != null) {
            this.target = dispatchTarget;
        }
        this.currentTarget = null;
        this.eventPhase = 0;
        this.defaultPrevented = false;
        this.returnValue = true;
        this.cancelBubble = false;
        this.propagationStopped = false;
        this.immediatePropagationStopped = false;
        this.composedPath = new ArrayList();
    }

    protected void copyTo(Event copy) {
        copy.target = this.target;
        copy.currentTarget = this.currentTarget;
        copy.type = this.type;
        copy.bubbles = this.bubbles;
        copy.cancelable = this.cancelable;
        copy.defaultPrevented = this.defaultPrevented;
        copy.detail = this.detail;
        copy.submitter = this.submitter;
        copy.formData = this.formData;
        copy.eventPhase = this.eventPhase;
        copy.cancelBubble = this.cancelBubble;
        copy.returnValue = this.returnValue;
        copy.isTrusted = this.isTrusted;
        copy.timeStamp = this.timeStamp;
        copy.propagationStopped = this.propagationStopped;
        copy.immediatePropagationStopped = this.immediatePropagationStopped;
        copy.composedPath = new ArrayList<Object>(this.composedPath);
    }

    public Event clone() {
        try {
            Event copy = (Event)super.clone();
            this.copyTo(copy);
            return copy;
        }
        catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public static ArrayList<Node> getRoute(Node element) {
        Node parent;
        ArrayList<Node> result = new ArrayList<Node>();
        Node node = parent = element == null ? null : element.parentNode;
        while (parent != null) {
            result.add(parent);
            parent = parent.parentNode;
        }
        return result;
    }

    public static boolean tiggerEvent(Event targetEvent) {
        Object object = targetEvent.target;
        if (!(object instanceof Node)) {
            return false;
        }
        Node target = (Node)object;
        return Event.runWithEventContext(targetEvent, () -> {
            targetEvent.resetForDispatch(target);
            String type = targetEvent.type;
            ArrayList<Node> route = target.getRouteNodes();
            route.remove(target);
            targetEvent.composedPath = Event.buildComposedPath(route, target);
            AtomicBoolean consumed = new AtomicBoolean(false);
            Collections.reverse(route);
            for (Node node : route) {
                Event.dispatchToNode(node, type, targetEvent, true, (short)1, consumed);
                if (!targetEvent.isPropagationStopped()) continue;
                targetEvent.eventPhase = 0;
                targetEvent.currentTarget = null;
                return consumed.get();
            }
            Event.dispatchAtTarget(target, type, targetEvent, consumed);
            if (targetEvent.isPropagationStopped()) {
                targetEvent.eventPhase = 0;
                targetEvent.currentTarget = null;
                return consumed.get();
            }
            if (targetEvent.bubbles) {
                Collections.reverse(route);
                for (Node node : route) {
                    Event.dispatchToNode(node, type, targetEvent, false, (short)3, consumed);
                    if (!targetEvent.isPropagationStopped()) continue;
                    break;
                }
            }
            targetEvent.eventPhase = 0;
            targetEvent.currentTarget = null;
            return consumed.get();
        });
    }

    public static boolean triggerSingle(Event targetEvent) {
        Object object = targetEvent.target;
        if (!(object instanceof Node)) {
            return false;
        }
        Node target = (Node)object;
        return Event.runWithEventContext(targetEvent, () -> {
            targetEvent.resetForDispatch(target);
            targetEvent.composedPath = new ArrayList<Node>(List.of(target));
            AtomicBoolean consumed = new AtomicBoolean(false);
            Event.dispatchAtTarget(target, targetEvent.type, targetEvent, consumed);
            targetEvent.eventPhase = 0;
            targetEvent.currentTarget = null;
            return consumed.get();
        });
    }

    public List<Object> composedPath() {
        return List.copyOf(this.composedPath);
    }

    public void setTrusted(boolean trusted) {
        this.isTrusted = trusted;
    }

    public static void markTrustedFromCurrentDispatch(Event event) {
        if (event == null) {
            return;
        }
        if (TRUSTED_CONTEXT_DEPTH.get() > 0) {
            event.setTrusted(true);
        }
    }

    public static void runTrustedAction(Runnable action) {
        if (action == null) {
            return;
        }
        Event.runWithTrustedContext(() -> {
            action.run();
            return null;
        });
    }

    public static void runWithEventTrust(Event sourceEvent, Runnable action) {
        if (action == null) {
            return;
        }
        if (sourceEvent != null && sourceEvent.isTrusted) {
            Event.runTrustedAction(action);
            return;
        }
        action.run();
    }

    public void setComposedPath(List<Object> path) {
        this.composedPath = path == null ? new ArrayList() : new ArrayList<Object>(path);
    }

    private static ArrayList<Object> buildComposedPath(ArrayList<Node> route, Node target) {
        ArrayList<Object> path = new ArrayList<Object>();
        path.add(target);
        path.addAll(route);
        return path;
    }

    private static <T> T runWithEventContext(Event event, Supplier<T> action) {
        Document document;
        Object object;
        if (event != null && (object = event.target) instanceof Node) {
            Node target = (Node)object;
            document = target.document;
        } else {
            document = null;
        }
        Document document2 = document;
        Supplier<Object> contextualAction = () -> {
            if (event != null && event.isTrusted) {
                return Event.runWithTrustedContext(action);
            }
            return action.get();
        };
        if (document2 == null) {
            return (T)contextualAction.get();
        }
        try (Document.ContextScope ignored = Document.withContext(document2);){
            Object object2 = contextualAction.get();
            return (T)object2;
        }
    }

    private static <T> T runWithTrustedContext(Supplier<T> action) {
        TRUSTED_CONTEXT_DEPTH.set(TRUSTED_CONTEXT_DEPTH.get() + 1);
        try {
            T t = action.get();
            return t;
        }
        finally {
            int depth = TRUSTED_CONTEXT_DEPTH.get() - 1;
            if (depth <= 0) {
                TRUSTED_CONTEXT_DEPTH.remove();
            } else {
                TRUSTED_CONTEXT_DEPTH.set(depth);
            }
        }
    }

    private static void dispatchAtTarget(Node target, String type, Event event, AtomicBoolean consumed) {
        Event.dispatchToNode(target, type, event, true, (short)2, consumed);
        if (event.isImmediatePropagationStopped()) {
            return;
        }
        Event.dispatchToNode(target, type, event, false, (short)2, consumed);
    }

    private static void dispatchToNode(Node node, String type, Event event, boolean capturePhase, short phase, AtomicBoolean consumed) {
        if (node == null || type == null || event.isImmediatePropagationStopped()) {
            return;
        }
        node.triggerEvent(listenerRecord -> {
            block7: {
                if (event.isImmediatePropagationStopped()) {
                    return;
                }
                if (!Objects.equals(type, listenerRecord.type())) {
                    return;
                }
                if (listenerRecord.useCapture() != capturePhase) {
                    return;
                }
                event.currentTarget = node;
                event.eventPhase = phase;
                if (!listenerRecord.internal()) {
                    consumed.set(true);
                }
                if (listenerRecord.once()) {
                    node.removeEventListener(type, listenerRecord.listener(), listenerRecord.useCapture());
                }
                try {
                    listenerRecord.listener().accept(event);
                }
                catch (RuntimeException exception) {
                    ApricityUI.LOGGER.error("[AUI Event] listener failed type={} phase={} capture={} internal={} target={} document={}", new Object[]{type, phase, capturePhase, listenerRecord.internal(), AuiLog.node(node), node.document == null ? "<unknown>" : AuiLog.source(node.document.getPath()), exception});
                    if (!listenerRecord.internal()) break block7;
                    throw exception;
                }
            }
        });
    }

    public record ListenerRecord(String type, Consumer<Event> listener, boolean useCapture, boolean once, boolean internal) {
    }

    public static class CompositionEvent
    extends Event {
        public String data = "";
        public boolean isComposing = false;

        public CompositionEvent(Object target, String type, boolean bubbles, String data) {
            super(target, type, bubbles);
            this.data = data == null ? "" : data;
        }

        @Override
        public CompositionEvent clone() {
            CompositionEvent copy = new CompositionEvent(this.target, this.type, this.bubbles, this.data);
            this.copyTo(copy);
            copy.data = this.data;
            copy.isComposing = this.isComposing;
            return copy;
        }
    }

    public static class InputEvent
    extends Event {
        public String inputType = "";
        public String data = null;
        public boolean isComposing = false;

        public InputEvent(Object target, String type, boolean bubbles, String inputType, String data) {
            super(target, type, bubbles);
            this.inputType = inputType == null ? "" : inputType;
            this.data = data;
        }

        @Override
        public InputEvent clone() {
            InputEvent copy = new InputEvent(this.target, this.type, this.bubbles, this.inputType, this.data);
            this.copyTo(copy);
            copy.inputType = this.inputType;
            copy.data = this.data;
            copy.isComposing = this.isComposing;
            return copy;
        }
    }

    public static class CustomEvent
    extends Event {
        public CustomEvent(String type, Object detail, boolean bubbles) {
            super(null, type, bubbles);
            this.detail = detail;
        }
    }
}

