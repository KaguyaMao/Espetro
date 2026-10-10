/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.ui;

import com.sighs.apricityui.event.Event;
import com.sighs.apricityui.event.MouseEvent;
import com.sighs.apricityui.init.Document;
import com.sighs.apricityui.init.Element;
import java.util.Locale;

public final class DialogWindow {
    private final Document document;
    private final Options options;
    private final Runnable onClose;
    private Element overlay;
    private Element window;
    private Element content;
    private Element maximizeButton;
    private double x;
    private double y;
    private double width;
    private double height;
    private double startX;
    private double startY;
    private double startWidth;
    private double startHeight;
    private double startLeft;
    private double startTop;
    private double restoredX;
    private double restoredY;
    private double restoredWidth;
    private double restoredHeight;
    private boolean maximized;
    private Mode mode = Mode.NONE;

    private DialogWindow(Document document, Options options, Runnable onClose) {
        this.document = document;
        this.options = options;
        this.onClose = onClose;
    }

    public static DialogWindow open(Document document, Options options, Runnable onClose) {
        DialogWindow result = new DialogWindow(document, options, onClose);
        result.create();
        return result;
    }

    public Element content() {
        return this.content;
    }

    public Element window() {
        return this.window;
    }

    public boolean isOpen() {
        return this.overlay != null && this.overlay.isConnected();
    }

    public void close() {
        if (this.overlay != null) {
            this.overlay.remove();
        }
        this.content = null;
        this.window = null;
        this.overlay = null;
        this.maximizeButton = null;
        this.maximized = false;
        if (this.onClose != null) {
            this.onClose.run();
        }
    }

    private void create() {
        double vw = this.document.getViewport().layoutWidth();
        double vh = this.document.getViewport().layoutHeight();
        this.width = this.options.width() > 0.0 ? this.options.width() : Math.min(720.0, vw - 48.0);
        this.height = this.options.height() > 0.0 ? this.options.height() : 0.0;
        this.x = Math.max(12.0, (vw - this.width) / 2.0);
        this.y = this.height > 0.0 ? Math.max(12.0, (vh - this.height) / 2.0) : 48.0;
        this.overlay = this.el("DIV", this.options.overlayClass());
        this.overlay.setTopLayer(true);
        this.overlay.setAttribute("style", "position:fixed;inset:0;z-index:9000;");
        this.window = this.el("DIV", this.options.windowClass());
        this.applyBounds();
        Element heading = this.el("DIV", this.options.headingClass());
        heading.setAttribute("style", "cursor:move;user-select:none;");
        Element title = this.el("DIV", this.options.titleClass());
        title.setAttribute("style", "user-select:none;");
        if (this.options.titleIconClass() != null && !this.options.titleIconClass().isBlank()) {
            title.append(this.el("DIV", this.options.titleIconClass()));
        }
        Element titleText = this.el("SPAN", "aui-dialog-title-text");
        titleText.setTextContent(this.options.title());
        title.append(titleText);
        Element controls = this.el("DIV", "aui-dialog-controls");
        controls.setAttribute("style", "display:flex;align-items:center;gap:6px;position:relative;z-index:1;flex-shrink:0;");
        if (this.options.maximizable()) {
            this.maximizeButton = this.el("BUTTON", "dialog-maximize");
            this.maximizeButton.setAttribute("type", "button");
            this.maximizeButton.addEventListener("mousedown", e -> e.stopPropagation());
            this.maximizeButton.addEventListener("click", e -> {
                e.stopPropagation();
                this.toggleMaximized();
            });
            this.updateMaximizeButton();
            controls.append(this.maximizeButton);
        }
        Element close = this.el("BUTTON", this.options.closeClass());
        close.setTextContent("\u2715");
        close.setAttribute("type", "button");
        close.addEventListener("mousedown", e -> e.stopPropagation());
        close.addEventListener("click", e -> {
            e.stopPropagation();
            this.close();
        });
        controls.append(close);
        heading.addEventListener("mousedown", e -> this.begin((Event)e, Mode.MOVE));
        heading.append(title);
        heading.append(controls);
        this.window.append(heading);
        this.content = this.el("DIV", this.options.contentClass());
        this.content.setAttribute("style", this.height > 0.0 ? "position:relative;flex:1;min-height:0;" : "position:relative;");
        this.window.append(this.content);
        if (this.options.resizable()) {
            for (Mode resize : new Mode[]{Mode.N, Mode.NE, Mode.E, Mode.SE, Mode.S, Mode.SW, Mode.W, Mode.NW}) {
                this.handle(resize);
            }
        }
        this.overlay.addEventListener("mousemove", this::move);
        this.overlay.addEventListener("mouseup", e -> {
            this.mode = Mode.NONE;
        });
        this.overlay.append(this.window);
        this.document.body.append(this.overlay);
        this.dirty();
    }

    private void handle(Mode mode) {
        Element e = this.el("DIV", "aui-dialog-resize");
        e.setAttribute("style", "position:absolute;z-index:2;" + mode.handleStyle());
        e.addEventListener("mousedown", v -> this.begin((Event)v, mode));
        this.window.append(e);
    }

    private void begin(Event event, Mode next) {
        if (this.maximized || !(event instanceof MouseEvent)) {
            return;
        }
        MouseEvent e = (MouseEvent)event;
        this.mode = next;
        this.startX = e.clientX;
        this.startY = e.clientY;
        this.startLeft = this.x;
        this.startTop = this.y;
        this.startWidth = this.width;
        this.startHeight = this.height;
        event.stopPropagation();
    }

    private void move(Event event) {
        if (this.maximized || this.mode == Mode.NONE || !(event instanceof MouseEvent)) {
            return;
        }
        MouseEvent e = (MouseEvent)event;
        double dx = e.clientX - this.startX;
        double dy = e.clientY - this.startY;
        if (this.mode.move) {
            this.x = this.startLeft + dx;
            this.y = this.startTop + dy;
        }
        if (this.mode.e) {
            this.width = Math.max(360.0, this.startWidth + dx);
        }
        if (this.mode.s) {
            this.height = Math.max(240.0, this.startHeight + dy);
        }
        if (this.mode.w) {
            this.width = Math.max(360.0, this.startWidth - dx);
            this.x = this.startLeft + this.startWidth - this.width;
        }
        if (this.mode.n) {
            this.height = Math.max(240.0, this.startHeight - dy);
            this.y = this.startTop + this.startHeight - this.height;
        }
        this.applyBounds();
        this.dirty();
        event.stopPropagation();
    }

    private void toggleMaximized() {
        if (!this.options.maximizable()) {
            return;
        }
        if (this.maximized) {
            this.x = this.restoredX;
            this.y = this.restoredY;
            this.width = this.restoredWidth;
            this.height = this.restoredHeight;
            this.maximized = false;
        } else {
            this.restoredX = this.x;
            this.restoredY = this.y;
            this.restoredWidth = this.width;
            this.restoredHeight = this.height;
            this.x = 0.0;
            this.y = 0.0;
            this.width = Math.max(0.0, (double)this.document.getViewport().layoutWidth());
            this.height = Math.max(0.0, (double)this.document.getViewport().layoutHeight());
            this.maximized = true;
        }
        this.mode = Mode.NONE;
        this.applyBounds();
        this.updateMaximizeButton();
        this.dirty();
    }

    private void updateMaximizeButton() {
        if (this.maximizeButton == null) {
            return;
        }
        this.maximizeButton.setTextContent(this.maximized ? "\u25a3" : "\u25a1");
        this.maximizeButton.setAttribute("class", this.maximized ? "dialog-maximize is-maximized" : "dialog-maximize");
        this.maximizeButton.setAttribute("aria-label", this.maximized ? "Restore window" : "Maximize window");
        this.maximizeButton.setAttribute("title", this.maximized ? "Restore window" : "Maximize window");
    }

    private void applyBounds() {
        String style = "position:absolute;left:" + DialogWindow.px(this.x) + ";top:" + DialogWindow.px(this.y) + ";width:" + DialogWindow.px(this.width) + ";pointer-events:auto;";
        if (this.height > 0.0) {
            style = style + "height:" + DialogWindow.px(this.height) + ";display:flex;flex-direction:column;";
        }
        this.window.setAttribute("class", this.options.windowClass() + (this.maximized ? " maximized" : ""));
        this.window.setAttribute("style", style);
    }

    private Element el(String tag, String cls) {
        Element e = Element.init(this.document.createElement(tag));
        e.setAttribute("class", cls);
        return e;
    }

    private void dirty() {
        if (this.document.body != null) {
            this.document.markDirty(this.document.body, 7);
        }
    }

    private static String px(double n) {
        return String.format(Locale.ROOT, "%.2fpx", n);
    }

    private static enum Mode {
        NONE(false, false, false, false, false, ""),
        MOVE(false, false, false, false, true, ""),
        N(true, false, false, false, false, "top:-5px;left:8px;right:8px;height:10px;cursor:n-resize;"),
        NE(true, true, false, false, false, "top:-5px;right:-5px;width:12px;height:12px;cursor:ne-resize;"),
        E(false, true, false, false, false, "top:8px;right:-5px;bottom:8px;width:10px;cursor:e-resize;"),
        SE(false, true, false, true, false, "right:-5px;bottom:-5px;width:12px;height:12px;cursor:se-resize;"),
        S(false, false, false, true, false, "left:8px;right:8px;bottom:-5px;height:10px;cursor:s-resize;"),
        SW(false, false, true, true, false, "left:-5px;bottom:-5px;width:12px;height:12px;cursor:sw-resize;"),
        W(false, false, true, false, false, "top:8px;left:-5px;bottom:8px;width:10px;cursor:w-resize;"),
        NW(true, false, true, false, false, "top:-5px;left:-5px;width:12px;height:12px;cursor:nw-resize;");

        final boolean n;
        final boolean e;
        final boolean w;
        final boolean s;
        final boolean move;
        final String h;

        private Mode(boolean n2, boolean e, boolean w, boolean s, boolean move, String h) {
            this.n = n2;
            this.e = e;
            this.w = w;
            this.s = s;
            this.move = move;
            this.h = h;
        }

        String handleStyle() {
            return this.h;
        }
    }

    public record Options(String title, double width, double height, boolean resizable, String overlayClass, String windowClass, String headingClass, String titleClass, String closeClass, String contentClass, String titleIconClass, boolean maximizable) {
        public Options(String title, double width, double height, boolean resizable, String overlayClass, String windowClass, String headingClass, String titleClass, String closeClass) {
            this(title, width, height, resizable, overlayClass, windowClass, headingClass, titleClass, closeClass, "aui-dialog-content", "", false);
        }

        public Options(String title, double width, double height, boolean resizable, String overlayClass, String windowClass, String headingClass, String titleClass, String closeClass, String contentClass, String titleIconClass) {
            this(title, width, height, resizable, overlayClass, windowClass, headingClass, titleClass, closeClass, contentClass, titleIconClass, false);
        }

        public static Options of(String title, double width, double height, boolean resizable) {
            return new Options(title, width, height, resizable, "aui-dialog-overlay", "aui-dialog-window", "aui-dialog-heading", "aui-dialog-title", "aui-dialog-close", "aui-dialog-content", "aui-dialog-title-icon", false);
        }
    }
}

