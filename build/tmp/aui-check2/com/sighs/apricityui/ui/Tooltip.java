/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.ui;

import com.sighs.apricityui.event.Event;
import com.sighs.apricityui.event.MouseEvent;
import com.sighs.apricityui.init.Document;
import com.sighs.apricityui.init.Element;
import com.sighs.apricityui.layout.Position;
import com.sighs.apricityui.layout.Size;
import com.sighs.apricityui.task.FrameTaskScheduler;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class Tooltip {
    private static final double VIEWPORT_GAP = 6.0;
    private static final int Z_INDEX = 11000;
    private static final String BASE_STYLE = "position:fixed;z-index:11000;display:inline-block;box-sizing:border-box;pointer-events:none;padding:8px 10px;min-width:40px;background:#ffffff;color:#1a1a1a;border:2px solid #1a1a1a;border-left:3px solid #8b5cf6;box-shadow:4px 4px 0 rgba(139,92,246,0.25);font-family:'Microsoft YaHei',sans-serif;font-size:11px;line-height:15px;font-weight:600;letter-spacing:0;white-space:normal;overflow-wrap:break-word;";
    private static Tooltip activeTooltip;
    private final Document document;
    private final Options options;
    private final Element owner;
    private final String text;
    private final String translationKey;
    private final Size measuredSize;
    private Element element;
    private Position pointer;
    private boolean closed;

    private Tooltip(Document document, Element owner, Position pointer, String text, String translationKey, Options options) {
        this.document = document;
        this.owner = owner;
        this.pointer = pointer == null ? Position.ZERO : pointer;
        this.options = (options == null ? Options.defaults() : options).normalize();
        this.text = text == null ? "" : text;
        this.translationKey = translationKey == null ? "" : translationKey;
        this.measuredSize = Tooltip.estimateSize(this.translationKey.isBlank() ? this.text : this.translationKey, this.options.maxWidth());
        this.mount();
    }

    public static Tooltip show(Document document, Position pointer, String text) {
        return Tooltip.show(document, pointer, text, Options.defaults());
    }

    public static synchronized Tooltip show(Document document, Position pointer, String text, Options options) {
        return Tooltip.replace(document, null, pointer, text, null, options);
    }

    public static synchronized Tooltip showTranslation(Document document, Position pointer, String translationKey, Options options) {
        return Tooltip.replace(document, null, pointer, null, translationKey, options);
    }

    public static Binding bind(Element target, String text) {
        return Tooltip.bind(target, () -> text, Options.defaults());
    }

    public static Binding bind(Element target, Supplier<String> text) {
        return Tooltip.bind(target, text, Options.defaults());
    }

    public static Binding bind(Element target, Supplier<String> text, Options options) {
        if (target == null) {
            throw new IllegalArgumentException("Tooltip target cannot be null");
        }
        Supplier<String> safeText = text == null ? () -> "" : text;
        return new Binding(target, safeText, options == null ? Options.defaults() : options);
    }

    public static Binding bindTranslation(Element target, String translationKey) {
        return Tooltip.bindTranslation(target, translationKey, Options.defaults());
    }

    public static Binding bindTranslation(Element target, String translationKey, Options options) {
        String key;
        String string = key = translationKey == null ? "" : translationKey;
        if (target == null) {
            throw new IllegalArgumentException("Tooltip target cannot be null");
        }
        return new Binding(target, key, options == null ? Options.defaults() : options);
    }

    public static synchronized void hide() {
        if (activeTooltip != null) {
            activeTooltip.close();
        }
    }

    public static synchronized void hide(Document document) {
        if (activeTooltip != null && Tooltip.activeTooltip.document == document) {
            activeTooltip.close();
        }
    }

    public static synchronized void moveActive(Position pointer) {
        if (activeTooltip != null) {
            activeTooltip.move(pointer);
        }
    }

    public static synchronized void moveActiveFromScreen(Position screenPointer) {
        if (activeTooltip == null || screenPointer == null) {
            return;
        }
        Position documentPointer = Tooltip.activeTooltip.document == null ? screenPointer : Tooltip.activeTooltip.document.screenToDocumentPosition(screenPointer);
        activeTooltip.move(documentPointer);
    }

    public boolean isVisible() {
        return !this.closed && this.element != null && this.element.isConnected();
    }

    public void move(Position nextPointer) {
        if (!this.isVisible() || nextPointer == null) {
            return;
        }
        if (Double.compare(this.pointer.x, nextPointer.x) == 0 && Double.compare(this.pointer.y, nextPointer.y) == 0) {
            return;
        }
        this.pointer = nextPointer;
        this.position();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void close() {
        if (this.closed) {
            return;
        }
        this.closed = true;
        if (this.element != null) {
            this.element.remove();
        }
        this.element = null;
        Class<Tooltip> clazz = Tooltip.class;
        synchronized (Tooltip.class) {
            if (activeTooltip == this) {
                activeTooltip = null;
            }
            // ** MonitorExit[var1_1] (shouldn't be in output)
            Tooltip.markDirty(this.document == null ? null : this.document.body);
            return;
        }
    }

    private void mount() {
        if (this.document == null || this.document.body == null || this.text.isBlank() && this.translationKey.isBlank()) {
            this.closed = true;
            return;
        }
        this.element = Element.init(this.document.createElement("DIV"));
        this.element.setTopLayer(true);
        this.element.setAttribute("class", this.options.className());
        this.element.setAttribute("role", "tooltip");
        if (this.translationKey.isBlank()) {
            this.element.setTextContent(this.text);
        } else {
            Element translation = Element.init(this.document.createElement("TRANSLATION"));
            translation.setTextContent(this.translationKey);
            this.element.appendChild(translation);
        }
        this.document.body.append(this.element);
        this.applyStyle(this.pointer.x + this.options.offsetX(), this.pointer.y + this.options.offsetY());
        this.position();
        Tooltip.markDirty(this.element);
        FrameTaskScheduler.scheduleAfterFrames(1, deadlineNs -> {
            if (this.isVisible()) {
                this.position();
            }
            return true;
        });
    }

    private void position() {
        if (this.element == null) {
            return;
        }
        Size measured = this.measuredSize;
        double viewportWidth = this.document.getViewport().layoutWidth();
        double viewportHeight = this.document.getViewport().layoutHeight();
        if (viewportWidth <= 1.0) {
            viewportWidth = 1920.0;
        }
        if (viewportHeight <= 1.0) {
            viewportHeight = 1080.0;
        }
        double width = Math.min(Math.max(0.0, measured.width()), this.options.maxWidth());
        double height = Math.max(0.0, measured.height());
        double left = this.pointer.x + this.options.offsetX();
        double top = this.pointer.y + this.options.offsetY();
        if (left + width + 6.0 > viewportWidth) {
            left = this.pointer.x - width - this.options.offsetX();
        }
        if (top + height + 6.0 > viewportHeight) {
            top = this.pointer.y - height - this.options.offsetY();
        }
        left = Tooltip.clamp(left, 6.0, Math.max(6.0, viewportWidth - width - 6.0));
        top = Tooltip.clamp(top, 6.0, Math.max(6.0, viewportHeight - height - 6.0));
        this.applyStyle(left, top);
        Tooltip.markDirty(this.element);
    }

    private void applyStyle(double left, double top) {
        this.element.setAttribute("style", "position:fixed;z-index:11000;display:inline-block;box-sizing:border-box;pointer-events:none;padding:8px 10px;min-width:40px;background:#ffffff;color:#1a1a1a;border:2px solid #1a1a1a;border-left:3px solid #8b5cf6;box-shadow:4px 4px 0 rgba(139,92,246,0.25);font-family:'Microsoft YaHei',sans-serif;font-size:11px;line-height:15px;font-weight:600;letter-spacing:0;white-space:normal;overflow-wrap:break-word;width:" + Tooltip.px(this.measuredSize.width()) + ";max-width:" + Tooltip.px(this.options.maxWidth()) + ";left:" + Tooltip.px(left) + ";top:" + Tooltip.px(top) + ";" + this.options.style());
    }

    private static Size estimateSize(String text, double maxWidth) {
        int codePoint;
        double contentLimit = Math.max(20.0, maxWidth - 24.0);
        double totalWidth = 0.0;
        double longestLine = 0.0;
        int wrappedLines = 0;
        for (int offset = 0; offset < text.length(); offset += Character.charCount(codePoint)) {
            codePoint = text.codePointAt(offset);
            if (codePoint == 10) {
                longestLine = Math.max(longestLine, totalWidth);
                wrappedLines += Math.max(1, (int)Math.ceil(totalWidth / contentLimit));
                totalWidth = 0.0;
                continue;
            }
            totalWidth += Tooltip.isWideCodePoint(codePoint) ? 11.0 : 6.5;
        }
        longestLine = Math.max(longestLine, totalWidth);
        double width = Math.min(maxWidth, Math.max(40.0, longestLine + 24.0));
        return new Size(width, (wrappedLines += Math.max(1, (int)Math.ceil(totalWidth / contentLimit))) * 15 + 20);
    }

    private static boolean isWideCodePoint(int codePoint) {
        Character.UnicodeScript script = Character.UnicodeScript.of(codePoint);
        return script == Character.UnicodeScript.HAN || script == Character.UnicodeScript.HIRAGANA || script == Character.UnicodeScript.KATAKANA || script == Character.UnicodeScript.HANGUL;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void showForEvent(Element owner, Supplier<String> supplier, String translationKey, Options options, Event event) {
        MouseEvent mouseEvent;
        block11: {
            block10: {
                if (!(event instanceof MouseEvent)) break block10;
                mouseEvent = (MouseEvent)event;
                if (owner != null && owner.document != null) break block11;
            }
            return;
        }
        String text = "";
        if (supplier != null) {
            try {
                text = supplier.get();
            }
            catch (RuntimeException ignored) {
                text = "";
            }
        }
        Position pointer = new Position(mouseEvent.clientX, mouseEvent.clientY);
        Class<Tooltip> clazz = Tooltip.class;
        synchronized (Tooltip.class) {
            if (activeTooltip != null && Tooltip.activeTooltip.owner == owner && activeTooltip.isVisible()) {
                activeTooltip.move(pointer);
                // ** MonitorExit[var8_9] (shouldn't be in output)
                return;
            }
            Tooltip.replace(owner.document, owner, pointer, text, translationKey, options);
            // ** MonitorExit[var8_9] (shouldn't be in output)
            return;
        }
    }

    private static synchronized Tooltip replace(Document document, Element owner, Position pointer, String text, String translationKey, Options options) {
        Tooltip tooltip;
        if (activeTooltip != null) {
            activeTooltip.close();
        }
        if ((tooltip = new Tooltip(document, owner, pointer, text, translationKey, options)).isVisible()) {
            activeTooltip = tooltip;
        }
        return tooltip;
    }

    private static synchronized void hideOwnedBy(Element owner) {
        if (activeTooltip != null && Tooltip.activeTooltip.owner == owner) {
            activeTooltip.close();
        }
    }

    private static void markDirty(Element target) {
        if (target != null && target.document != null) {
            target.document.markDirty(target, 15);
        }
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(value, max));
    }

    private static String px(double value) {
        return String.format(Locale.ROOT, "%.2fpx", value);
    }

    public record Options(String className, String style, double offsetX, double offsetY, double maxWidth) {
        public static Options defaults() {
            return new Options("aui-tooltip", "", 14.0, 18.0, 320.0);
        }

        private Options normalize() {
            Options defaults = Options.defaults();
            return new Options(this.className == null || this.className.isBlank() ? defaults.className() : this.className.trim(), this.style == null ? "" : this.style, Double.isFinite(this.offsetX) ? this.offsetX : defaults.offsetX(), Double.isFinite(this.offsetY) ? this.offsetY : defaults.offsetY(), Double.isFinite(this.maxWidth) && this.maxWidth > 0.0 ? this.maxWidth : defaults.maxWidth());
        }
    }

    public static final class Binding
    implements AutoCloseable {
        private final Element target;
        private final Consumer<Event> enterListener;
        private final Consumer<Event> moveListener;
        private final Consumer<Event> leaveListener;
        private boolean closed;

        private Binding(Element target, Supplier<String> text, Options options) {
            this.target = target;
            this.enterListener = event -> Tooltip.showForEvent(target, text, null, options, event);
            this.moveListener = event -> Tooltip.showForEvent(target, text, null, options, event);
            this.leaveListener = event -> Tooltip.hideOwnedBy(target);
            target.addEventListener("mouseenter", this.enterListener);
            target.addEventListener("mousemove", this.moveListener);
            target.addEventListener("mouseleave", this.leaveListener);
        }

        private Binding(Element target, String translationKey, Options options) {
            this.target = target;
            this.enterListener = event -> Tooltip.showForEvent(target, null, translationKey, options, event);
            this.moveListener = event -> Tooltip.showForEvent(target, null, translationKey, options, event);
            this.leaveListener = event -> Tooltip.hideOwnedBy(target);
            target.addEventListener("mouseenter", this.enterListener);
            target.addEventListener("mousemove", this.moveListener);
            target.addEventListener("mouseleave", this.leaveListener);
        }

        @Override
        public void close() {
            if (this.closed) {
                return;
            }
            this.closed = true;
            this.target.removeEventListener("mouseenter", this.enterListener);
            this.target.removeEventListener("mousemove", this.moveListener);
            this.target.removeEventListener("mouseleave", this.leaveListener);
            Tooltip.hideOwnedBy(this.target);
        }
    }
}

