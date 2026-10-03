/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.ui;

import com.sighs.apricityui.event.Event;
import com.sighs.apricityui.event.MouseEvent;
import com.sighs.apricityui.init.Document;
import com.sighs.apricityui.init.Element;
import com.sighs.apricityui.render.Operation;
import com.sighs.apricityui.ui.ToastManager;
import com.sighs.apricityui.ui.Tooltip;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ColorPicker {
    private static final String PATH = "devtools/color-picker.html";
    private static final double WIDTH = 288.0;
    private static final double HEIGHT = 476.0;
    private static final double GAP = 8.0;
    private static ColorPicker active;
    private final Document document;
    private final boolean ownsDocument;
    private final CompletableFuture<Optional<String>> result = new CompletableFuture();
    private final ColorState color;
    private String format = "hex";
    private Element root;
    private Element svPanel;
    private Element hueSlider;
    private Element alphaSlider;
    private Element previewFill;
    private Element previewValue;
    private Element alphaFill;
    private Element svHandle;
    private Element hueHandle;
    private Element alphaHandle;
    private Element inputs;
    private Element dragTarget;

    private ColorPicker(Document document, boolean ownsDocument, String initialColor) {
        this.document = document;
        this.ownsDocument = ownsDocument;
        this.color = ColorState.parse(initialColor);
    }

    public static synchronized CompletableFuture<Optional<String>> pick(String initialColor) {
        Document document = Document.create(PATH);
        if (document == null || document.body == null) {
            return CompletableFuture.completedFuture(Optional.empty());
        }
        document.setReloadPersistent(true);
        return ColorPicker.open(document, null, initialColor, true);
    }

    public static synchronized CompletableFuture<Optional<String>> pickIn(Document document, Element anchor, String initialColor) {
        return ColorPicker.open(document, anchor, initialColor, false);
    }

    public static synchronized boolean isOpen() {
        return active != null && ColorPicker.active.root != null && ColorPicker.active.root.isConnected();
    }

    public static synchronized void closeActive() {
        if (active != null) {
            active.finish(Optional.empty());
        }
    }

    private static CompletableFuture<Optional<String>> open(Document document, Element anchor, String initialColor, boolean ownsDocument) {
        if (document == null || document.body == null) {
            return CompletableFuture.completedFuture(Optional.empty());
        }
        Tooltip.hide();
        if (active != null) {
            active.finish(Optional.empty());
        }
        active = new ColorPicker(document, ownsDocument, initialColor);
        active.render(anchor);
        return ColorPicker.active.result;
    }

    private void render(Element anchor) {
        this.root = this.el("DIV", "color-picker");
        this.root.setTopLayer(true);
        this.root.setAttribute("data-aui-color-picker", "true");
        Element header = this.el("DIV", "cp-header");
        header.append(this.text("DIV", "cp-header-left", "COLOR"));
        Element close = this.button("cp-close", "x");
        close.addEventListener("click", event -> this.finish(Optional.empty()));
        header.append(close);
        this.root.append(header);
        Element body = this.el("DIV", "cp-body");
        this.svPanel = this.el("DIV", "cp-sv-panel");
        this.svPanel.append(this.el("DIV", "cp-sv-white"));
        this.svPanel.append(this.el("DIV", "cp-sv-black"));
        this.svHandle = this.el("DIV", "cp-sv-handle");
        this.svPanel.append(this.svHandle);
        body.append(this.svPanel);
        this.hueSlider = this.slider("cp-slider cp-slider-hue");
        this.hueHandle = this.el("DIV", "cp-slider-handle");
        this.hueSlider.append(this.hueHandle);
        body.append(this.sliderRow("H", this.hueSlider));
        this.alphaSlider = this.slider("cp-slider cp-slider-alpha");
        this.alphaFill = this.el("DIV", "cp-slider-alpha-fill");
        this.alphaHandle = this.el("DIV", "cp-slider-handle");
        this.alphaSlider.append(this.alphaFill);
        this.alphaSlider.append(this.alphaHandle);
        body.append(this.sliderRow("A", this.alphaSlider));
        Element preview = this.el("DIV", "cp-current-preview");
        this.previewFill = this.el("DIV", "cp-current-preview-fill");
        this.previewValue = this.text("DIV", "cp-current-value", "");
        preview.append(this.previewFill);
        preview.append(this.previewValue);
        body.append(preview);
        Element tabs = this.el("DIV", "cp-format-tabs");
        for (String value : new String[]{"hex", "rgb", "hsl"}) {
            Element tab = this.button("cp-format-tab" + (value.equals(this.format) ? " active" : ""), value.toUpperCase(Locale.ROOT));
            tab.setAttribute("data-format", value);
            tab.addEventListener("click", event -> {
                this.format = value;
                this.updateFormatTabs();
                this.renderInputs();
                this.update();
            });
            tabs.append(tab);
        }
        body.append(tabs);
        this.inputs = this.el("DIV", "cp-inputs");
        body.append(this.inputs);
        Element actions = this.el("DIV", "cp-actions");
        Element left = this.el("DIV", "cp-actions-left");
        Element eye = this.button("cp-icon-btn", "");
        eye.setInnerHTML("<svg viewBox=\"0 0 14 14\" fill=\"currentColor\"><path d=\"M12.5 1.5l-2-2a1 1 0 0 0-1.4 0L7.6 1l-.7-.7a1 1 0 0 0-1.4 0L4 1.8a1 1 0 0 0 0 1.4l.7.7-4 4a1 1 0 0 0 0 1.4l3 3a1 1 0 0 0 1.4 0l4-4 .7.7a1 1 0 0 0 1.4 0l1.5-1.5a1 1 0 0 0 0-1.4l-.7-.7 1.5-1.5a1 1 0 0 0 0-1.4z\"/></svg>");
        eye.setAttribute("title", "Pick from screen");
        eye.addEventListener("click", event -> ToastManager.show("EYEDROPPER NOT SUPPORTED"));
        Element copy = this.button("cp-icon-btn", "");
        copy.setInnerHTML("<svg viewBox=\"0 0 14 14\" fill=\"currentColor\"><rect x=\"4\" y=\"4\" width=\"8\" height=\"8\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"1.2\"/><path d=\"M2 10V3h6v1H3v6H2z\"/></svg>");
        copy.setAttribute("title", "Copy color value");
        copy.addEventListener("click", event -> Operation.setClipboardText(this.value()));
        left.append(eye);
        left.append(copy);
        actions.append(left);
        actions.append(this.el("DIV", "cp-actions-spacer"));
        Element right = this.el("DIV", "cp-actions-right");
        Element cancel = this.button("cp-btn", "CANCEL");
        cancel.addEventListener("click", event -> this.finish(Optional.empty()));
        Element apply = this.button("cp-btn primary", "APPLY");
        apply.addEventListener("click", event -> this.finish(Optional.of(this.value())));
        right.append(cancel);
        right.append(apply);
        actions.append(right);
        body.append(actions);
        this.root.append(body);
        this.document.body.append(this.root);
        this.bindDrag(this.svPanel);
        this.bindDrag(this.hueSlider);
        this.bindDrag(this.alphaSlider);
        this.root.addEventListener("mousedown", Event::stopPropagation);
        this.document.addEventListener("mousedown", event -> {
            Element target;
            Object patt6295$temp = event.target;
            if (patt6295$temp instanceof Element && !this.root.contains(target = (Element)patt6295$temp)) {
                this.finish(Optional.empty());
            }
        });
        this.document.addEventListener("mousemove", this::drag);
        this.document.addEventListener("mouseup", event -> {
            this.dragTarget = null;
        });
        this.position(anchor);
        this.renderInputs();
        this.update();
        this.dirty();
    }

    private void bindDrag(Element element) {
        element.addEventListener("mousedown", event -> {
            if (event instanceof MouseEvent) {
                MouseEvent mouse = (MouseEvent)event;
                this.dragTarget = element;
                this.move(mouse);
                event.preventDefault();
            }
        });
    }

    private void drag(Event event) {
        if (this.dragTarget != null && event instanceof MouseEvent) {
            MouseEvent mouse = (MouseEvent)event;
            this.move(mouse);
        }
    }

    private void move(MouseEvent event) {
        Element.DOMRect rect = this.dragTarget.getBoundingClientRect();
        double x = ColorPicker.clamp((event.clientX - rect.left) / Math.max(1.0, rect.width), 0.0, 1.0);
        if (this.dragTarget == this.svPanel) {
            this.color.setS(x * 100.0).setV((1.0 - ColorPicker.clamp((event.clientY - rect.top) / Math.max(1.0, rect.height), 0.0, 1.0)) * 100.0);
        } else if (this.dragTarget == this.hueSlider) {
            this.color.setH(x * 360.0);
        } else {
            this.color.setA((double)Math.round(x * 100.0) / 100.0);
        }
        this.update();
    }

    private void renderInputs() {
        new ArrayList<Element>(this.inputs.children).forEach(Element::remove);
        if ("hex".equals(this.format)) {
            this.addInput("HEX", "cp-hex", this.hex().toUpperCase(Locale.ROOT), true, this::fromHex);
            this.addInput("A %", "cp-hexa", Integer.toString((int)Math.round(this.color.a * 100.0)), false, value -> this.color.setA(ColorPicker.clamp(ColorPicker.number(value, 0.0) / 100.0, 0.0, 1.0)));
        } else if ("rgb".equals(this.format)) {
            Rgb rgb = this.color.rgb();
            this.addInput("R", "cp-r", Integer.toString(rgb.r), false, ignored -> this.fromRgb());
            this.addInput("G", "cp-g", Integer.toString(rgb.g), false, ignored -> this.fromRgb());
            this.addInput("B", "cp-b", Integer.toString(rgb.b), false, ignored -> this.fromRgb());
            this.addInput("A", "cp-rgba", ColorPicker.decimal(this.color.a), false, value -> this.color.setA(ColorPicker.clamp(ColorPicker.number(value, 0.0), 0.0, 1.0)));
        } else {
            Hsl hsl = this.color.hsl();
            this.addInput("H", "cp-h", Integer.toString(hsl.h), false, ignored -> this.fromHsl());
            this.addInput("S", "cp-s", Integer.toString(hsl.s), false, ignored -> this.fromHsl());
            this.addInput("L", "cp-l", Integer.toString(hsl.l), false, ignored -> this.fromHsl());
            this.addInput("A", "cp-hsla", ColorPicker.decimal(this.color.a), false, value -> this.color.setA(ColorPicker.clamp(ColorPicker.number(value, 0.0), 0.0, 1.0)));
        }
    }

    private void addInput(String label, String id, String initial, boolean hex, Consumer<String> change) {
        Element group = this.el("DIV", "cp-input-group" + (hex ? " hex" : ""));
        group.append(this.text("DIV", "cp-input-label", label));
        Element input = this.el("INPUT", "cp-input");
        input.setAttribute("id", id);
        input.setAttribute("type", "text");
        input.setValue(initial);
        input.addEventListener("input", event -> {
            change.accept(input.getValue());
            this.update();
        });
        group.append(input);
        this.inputs.append(group);
    }

    private void updateFormatTabs() {
        for (Element tab : this.root.querySelectorAll(".cp-format-tab")) {
            tab.setClassName("cp-format-tab" + (this.format.equals(tab.getAttribute("data-format")) ? " active" : ""));
        }
    }

    private void fromHex(String raw) {
        ColorState parsed = ColorState.parse(raw);
        this.color.copy(parsed);
    }

    private void fromRgb() {
        this.color.setRgb((int)ColorPicker.number(this.input("#cp-r"), 0.0), (int)ColorPicker.number(this.input("#cp-g"), 0.0), (int)ColorPicker.number(this.input("#cp-b"), 0.0));
    }

    private void fromHsl() {
        this.color.setHsl(ColorPicker.number(this.input("#cp-h"), 0.0), ColorPicker.number(this.input("#cp-s"), 0.0), ColorPicker.number(this.input("#cp-l"), 0.0));
    }

    private String input(String selector) {
        Element input = this.root.querySelector(selector);
        return input == null ? "0" : input.getValue();
    }

    private void update() {
        Rgb rgb = this.color.rgb();
        String rgbText = "rgb(" + rgb.r + "," + rgb.g + "," + rgb.b + ")";
        this.svPanel.setAttribute("style", "background:hsl(" + ColorPicker.number(this.color.h) + ",100%,50%);");
        this.svHandle.setAttribute("style", "left:" + this.color.s + "%;top:" + (100.0 - this.color.v) + "%;");
        this.hueHandle.setAttribute("style", "left:" + this.color.h / 3.6 + "%;");
        this.alphaHandle.setAttribute("style", "left:" + this.color.a * 100.0 + "%;");
        this.alphaFill.setAttribute("style", "background:linear-gradient(to right,rgba(" + rgb.r + "," + rgb.g + "," + rgb.b + ",0)," + rgbText + ");");
        this.previewFill.setAttribute("style", "background:" + this.rgba() + ";");
        this.previewValue.setTextContent(this.value().toUpperCase(Locale.ROOT));
        this.dirty();
    }

    private void position(Element anchor) {
        double x = ((double)this.document.getViewport().layoutWidth() - 288.0) / 2.0;
        double y = ((double)this.document.getViewport().layoutHeight() - 476.0) / 2.0;
        if (anchor != null) {
            Element.DOMRect r = anchor.getBoundingClientRect();
            x = r.right + 10.0;
            y = r.top - 40.0;
            if (x + 288.0 > (double)this.document.getViewport().layoutWidth() - 8.0) {
                x = r.left - 288.0 - 10.0;
            }
        }
        x = ColorPicker.clamp(x, 8.0, Math.max(8.0, (double)this.document.getViewport().layoutWidth() - 288.0 - 8.0));
        y = ColorPicker.clamp(y, 8.0, Math.max(8.0, (double)this.document.getViewport().layoutHeight() - 476.0 - 8.0));
        this.root.setAttribute("style", "left:" + ColorPicker.number(x) + "px;top:" + ColorPicker.number(y) + "px;");
    }

    private String hex() {
        Rgb c = this.color.rgb();
        return String.format(Locale.ROOT, "#%02x%02x%02x", c.r, c.g, c.b);
    }

    private String rgba() {
        Rgb c = this.color.rgb();
        return "rgba(" + c.r + "," + c.g + "," + c.b + "," + ColorPicker.decimal(this.color.a) + ")";
    }

    private String value() {
        Rgb c = this.color.rgb();
        if ("hex".equals(this.format)) {
            return this.color.a < 0.999 ? this.hex() + String.format(Locale.ROOT, "%02x", Math.round(this.color.a * 255.0)) : this.hex();
        }
        if ("rgb".equals(this.format)) {
            return this.color.a < 0.999 ? this.rgba() : "rgb(" + c.r + ", " + c.g + ", " + c.b + ")";
        }
        Hsl h = this.color.hsl();
        return this.color.a < 0.999 ? "hsla(" + h.h + ", " + h.s + "%, " + h.l + "%, " + ColorPicker.decimal(this.color.a) + ")" : "hsl(" + h.h + ", " + h.s + "%, " + h.l + "%)";
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void finish(Optional<String> value) {
        if (this.root != null) {
            this.root.remove();
        }
        this.dragTarget = null;
        if (!this.result.isDone()) {
            this.result.complete(value);
        }
        Class<ColorPicker> clazz = ColorPicker.class;
        synchronized (ColorPicker.class) {
            if (active == this) {
                active = null;
            }
            // ** MonitorExit[var2_2] (shouldn't be in output)
            if (this.ownsDocument) {
                this.document.remove();
            } else {
                this.dirty();
            }
            return;
        }
    }

    private Element el(String tag, String cls) {
        Element e = Element.init(this.document.createElement(tag));
        e.setAttribute("class", cls);
        return e;
    }

    private Element text(String tag, String cls, String value) {
        Element e = this.el(tag, cls);
        e.setTextContent(value);
        return e;
    }

    private Element button(String cls, String value) {
        Element e = this.text("BUTTON", cls, value);
        e.setAttribute("type", "button");
        return e;
    }

    private Element slider(String cls) {
        return this.el("DIV", cls);
    }

    private Element sliderRow(String label, Element slider) {
        Element row = this.el("DIV", "cp-slider-row");
        row.append(this.text("DIV", "cp-slider-label", label));
        row.append(slider);
        return row;
    }

    private void dirty() {
        if (this.document.body != null) {
            this.document.markDirty(this.document.body, 15);
        }
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, Double.isFinite(value) ? value : min));
    }

    private static double number(String value, double fallback) {
        try {
            return Double.parseDouble(value == null ? "" : value.trim());
        }
        catch (NumberFormatException ignored) {
            return fallback;
        }
    }

    private static String number(double value) {
        return Math.abs(value - Math.rint(value)) < 0.01 ? Long.toString(Math.round(value)) : String.format(Locale.ROOT, "%.2f", value);
    }

    private static String decimal(double value) {
        return String.format(Locale.ROOT, "%.2f", value);
    }

    private static final class ColorState {
        double h;
        double s;
        double v;
        double a = 1.0;

        private ColorState() {
        }

        ColorState setH(double h) {
            this.h = ColorPicker.clamp(h, 0.0, 360.0);
            return this;
        }

        ColorState setS(double s) {
            this.s = ColorPicker.clamp(s, 0.0, 100.0);
            return this;
        }

        ColorState setV(double v) {
            this.v = ColorPicker.clamp(v, 0.0, 100.0);
            return this;
        }

        ColorState setA(double a) {
            this.a = ColorPicker.clamp(a, 0.0, 1.0);
            return this;
        }

        void copy(ColorState other) {
            this.h = other.h;
            this.s = other.s;
            this.v = other.v;
            this.a = other.a;
        }

        static ColorState parse(String value) {
            ColorState out = new ColorState();
            String s = value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
            try {
                Matcher m;
                if (s.startsWith("#")) {
                    Object hex = s.substring(1);
                    if (((String)hex).length() == 3 || ((String)hex).length() == 4) {
                        hex = "" + ((String)hex).charAt(0) + ((String)hex).charAt(0) + ((String)hex).charAt(1) + ((String)hex).charAt(1) + ((String)hex).charAt(2) + ((String)hex).charAt(2) + (String)(((String)hex).length() == 4 ? "" + ((String)hex).charAt(3) + ((String)hex).charAt(3) : "");
                    }
                    if (((String)hex).length() == 6 || ((String)hex).length() == 8) {
                        out.setRgb(Integer.parseInt(((String)hex).substring(0, 2), 16), Integer.parseInt(((String)hex).substring(2, 4), 16), Integer.parseInt(((String)hex).substring(4, 6), 16));
                        if (((String)hex).length() == 8) {
                            out.a = (double)Integer.parseInt(((String)hex).substring(6, 8), 16) / 255.0;
                        }
                        return out;
                    }
                }
                if ((m = Pattern.compile("rgba?\\(([^)]+)\\)").matcher(s)).matches()) {
                    String[] p = m.group(1).split(",");
                    out.setRgb((int)ColorPicker.number(p[0], 0.0), (int)ColorPicker.number(p[1], 0.0), (int)ColorPicker.number(p[2], 0.0));
                    if (p.length > 3) {
                        out.a = ColorPicker.clamp(ColorPicker.number(p[3], 1.0), 0.0, 1.0);
                    }
                    return out;
                }
                m = Pattern.compile("hsla?\\(([^)]+)\\)").matcher(s);
                if (m.matches()) {
                    String[] p = m.group(1).replace("%", "").split(",");
                    out.setHsl(ColorPicker.number(p[0], 0.0), ColorPicker.number(p[1], 0.0), ColorPicker.number(p[2], 0.0));
                    if (p.length > 3) {
                        out.a = ColorPicker.clamp(ColorPicker.number(p[3], 1.0), 0.0, 1.0);
                    }
                    return out;
                }
            }
            catch (RuntimeException runtimeException) {
                // empty catch block
            }
            return out;
        }

        ColorState setRgb(int r, int g, int b) {
            double rr = ColorPicker.clamp(r, 0.0, 255.0) / 255.0;
            double gg = ColorPicker.clamp(g, 0.0, 255.0) / 255.0;
            double bb = ColorPicker.clamp(b, 0.0, 255.0) / 255.0;
            double max = Math.max(rr, Math.max(gg, bb));
            double min = Math.min(rr, Math.min(gg, bb));
            double d = max - min;
            this.v = max * 100.0;
            double d2 = this.s = max == 0.0 ? 0.0 : d / max * 100.0;
            this.h = d == 0.0 ? 0.0 : (max == rr ? 60.0 * ((gg - bb) / d + (double)(gg < bb ? 6 : 0)) : (max == gg ? 60.0 * ((bb - rr) / d + 2.0) : 60.0 * ((rr - gg) / d + 4.0)));
            return this;
        }

        Rgb rgb() {
            double hh = this.h % 360.0 / 60.0;
            double ss = this.s / 100.0;
            double vv = this.v / 100.0;
            double c = vv * ss;
            double x = c * (1.0 - Math.abs(hh % 2.0 - 1.0));
            double m = vv - c;
            double r = 0.0;
            double g = 0.0;
            double b = 0.0;
            if (hh < 1.0) {
                r = c;
                g = x;
            } else if (hh < 2.0) {
                r = x;
                g = c;
            } else if (hh < 3.0) {
                g = c;
                b = x;
            } else if (hh < 4.0) {
                g = x;
                b = c;
            } else if (hh < 5.0) {
                r = x;
                b = c;
            } else {
                r = c;
                b = x;
            }
            return new Rgb((int)Math.round((r + m) * 255.0), (int)Math.round((g + m) * 255.0), (int)Math.round((b + m) * 255.0));
        }

        Hsl hsl() {
            double ss;
            Rgb c = this.rgb();
            double r = (double)c.r / 255.0;
            double g = (double)c.g / 255.0;
            double b = (double)c.b / 255.0;
            double max = Math.max(r, Math.max(g, b));
            double min = Math.min(r, Math.min(g, b));
            double l = (max + min) / 2.0;
            double d = max - min;
            double d2 = ss = d == 0.0 ? 0.0 : d / (1.0 - Math.abs(2.0 * l - 1.0));
            double hh = d == 0.0 ? 0.0 : (max == r ? 60.0 * ((g - b) / d + (double)(g < b ? 6 : 0)) : (max == g ? 60.0 * ((b - r) / d + 2.0) : 60.0 * ((r - g) / d + 4.0)));
            return new Hsl((int)Math.round(hh), (int)Math.round(ss * 100.0), (int)Math.round(l * 100.0));
        }

        ColorState setHsl(double h, double s, double l) {
            double hh = (h % 360.0 + 360.0) % 360.0 / 360.0;
            double ss = ColorPicker.clamp(s, 0.0, 100.0) / 100.0;
            double ll = ColorPicker.clamp(l, 0.0, 100.0) / 100.0;
            double q = ll < 0.5 ? ll * (1.0 + ss) : ll + ss - ll * ss;
            double p = 2.0 * ll - q;
            return this.setRgb((int)Math.round(ColorState.hue(p, q, hh + 0.3333333333333333) * 255.0), (int)Math.round(ColorState.hue(p, q, hh) * 255.0), (int)Math.round(ColorState.hue(p, q, hh - 0.3333333333333333) * 255.0));
        }

        private static double hue(double p, double q, double t) {
            if (t < 0.0) {
                t += 1.0;
            }
            if (t > 1.0) {
                t -= 1.0;
            }
            if (t < 0.16666666666666666) {
                return p + (q - p) * 6.0 * t;
            }
            if (t < 0.5) {
                return q;
            }
            if (t < 0.6666666666666666) {
                return p + (q - p) * (0.6666666666666666 - t) * 6.0;
            }
            return p;
        }
    }

    private record Rgb(int r, int g, int b) {
    }

    private record Hsl(int h, int s, int l) {
    }
}

