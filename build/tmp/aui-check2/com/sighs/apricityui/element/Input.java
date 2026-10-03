/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  org.lwjgl.PointerBuffer
 *  org.lwjgl.system.MemoryStack
 *  org.lwjgl.util.tinyfd.TinyFileDialogs
 */
package com.sighs.apricityui.element;

import com.mojang.blaze3d.vertex.PoseStack;
import com.sighs.apricityui.element.AbstractText;
import com.sighs.apricityui.event.Event;
import com.sighs.apricityui.event.MouseEvent;
import com.sighs.apricityui.init.Document;
import com.sighs.apricityui.init.Element;
import com.sighs.apricityui.layout.Box;
import com.sighs.apricityui.layout.Position;
import com.sighs.apricityui.layout.Size;
import com.sighs.apricityui.parser.Color;
import com.sighs.apricityui.registry.annotation.ElementRegister;
import com.sighs.apricityui.render.Base;
import com.sighs.apricityui.render.FontDrawer;
import com.sighs.apricityui.render.Graph;
import com.sighs.apricityui.render.Rect;
import com.sighs.apricityui.style.Background;
import com.sighs.apricityui.style.Text;
import com.sighs.apricityui.ui.ColorPicker;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

@ElementRegister(value="INPUT")
public class Input
extends AbstractText {
    public static final String TAG_NAME = "INPUT";
    private static final float CONTENT_DEPTH_OFFSET = 0.16f;
    private static final float DETAIL_DEPTH_OFFSET = 0.24f;
    private static final float MARK_DEPTH_OFFSET = 0.2f;
    private boolean rangeDragging;
    private boolean rangeValueChanged;

    public Input(Document document) {
        super(document, TAG_NAME);
        this.addInternalEventListener("mousedown", event -> {
            MouseEvent mouse;
            block8: {
                block7: {
                    if (!(event instanceof MouseEvent)) break block7;
                    mouse = (MouseEvent)event;
                    if (!this.isDisabled()) break block8;
                }
                return;
            }
            if (this.getMode() == Mode.RANGE) {
                if (mouse.button != -1 && mouse.button != 0) {
                    return;
                }
                this.rangeDragging = true;
                this.rangeValueChanged = this.setRangeValueFromPointer(mouse);
                if (this.rangeValueChanged) {
                    this.triggerInputEvent();
                }
                return;
            }
            if (this.getMode() == Mode.NUMBER) {
                this.handleNumberSpinner(mouse);
            }
        });
        this.addInternalEventListener("mousemove", event -> {
            MouseEvent mouse;
            block5: {
                block4: {
                    if (!(event instanceof MouseEvent)) break block4;
                    mouse = (MouseEvent)event;
                    if (this.getMode() == Mode.RANGE && this.rangeDragging && !this.isDisabled()) break block5;
                }
                return;
            }
            if (this.setRangeValueFromPointer(mouse)) {
                this.rangeValueChanged = true;
                this.triggerInputEvent();
            }
        });
        this.addInternalEventListener("mouseup", event -> {
            if (!(event instanceof MouseEvent) || this.getMode() != Mode.RANGE || !this.rangeDragging) {
                return;
            }
            this.rangeDragging = false;
            if (this.rangeValueChanged) {
                this.rangeValueChanged = false;
                this.triggerChangeOnlyEvent();
            }
        });
        this.addInternalEventListener("blur", event -> {
            this.rangeDragging = false;
            this.rangeValueChanged = false;
        });
        this.addInternalEventListener("wheel", event -> {
            if (!(event instanceof MouseEvent)) {
                return;
            }
            MouseEvent mouse = (MouseEvent)event;
            if (this.handleNumberWheel(mouse)) {
                event.preventDefault();
                mouse.consumeNative();
            }
        });
    }

    @Override
    public String getValue() {
        if (this.getMode() == Mode.RANGE && !this.hasAttribute("value") && (this.value == null || this.value.isEmpty())) {
            double min = this.parseNumberAttribute("min", 0.0);
            double max = this.parseNumberAttribute("max", 100.0);
            return Double.toString(min + (max - min) * 0.5);
        }
        if ("color".equalsIgnoreCase(this.getType()) && !this.hasAttribute("value") && (this.value == null || this.value.isEmpty())) {
            return "#000000";
        }
        return super.getValue();
    }

    private Mode getMode() {
        String type = this.getType();
        if (type == null || type.isBlank()) {
            return Mode.TEXT;
        }
        return switch (type.toLowerCase(Locale.ROOT)) {
            case "button", "submit", "reset", "image" -> Mode.BUTTON;
            case "checkbox" -> Mode.CHECKBOX;
            case "radio" -> Mode.RADIO;
            case "file" -> Mode.FILE;
            case "range" -> Mode.RANGE;
            case "number" -> Mode.NUMBER;
            case "color" -> Mode.COLOR;
            case "hidden" -> Mode.HIDDEN;
            default -> Mode.TEXT;
        };
    }

    @Override
    public boolean canEditText() {
        return this.getMode() == Mode.TEXT || this.getMode() == Mode.NUMBER;
    }

    @Override
    public boolean canFocus() {
        return this.getMode() != Mode.HIDDEN && super.canFocus();
    }

    @Override
    public void handleClickDefault() {
        if (this.isDisabled()) {
            return;
        }
        Mode mode = this.getMode();
        if (mode == Mode.HIDDEN) {
            return;
        }
        if (mode == Mode.CHECKBOX) {
            this.setChecked(!this.isChecked());
            this.triggerChangeEvent();
        } else if (mode == Mode.RADIO) {
            if (!this.isChecked()) {
                this.setChecked(true);
                this.triggerChangeEvent();
            }
        } else if (mode == Mode.FILE) {
            this.openFileDialog();
        } else if (mode == Mode.COLOR) {
            this.openColorPicker();
        } else if (mode != Mode.RANGE) {
            Element form;
            if (mode == Mode.BUTTON && ("submit".equalsIgnoreCase(this.getAttribute("type")) || "image".equalsIgnoreCase(this.getAttribute("type")))) {
                this.submitEnclosingForm();
            } else if (mode == Mode.BUTTON && "reset".equalsIgnoreCase(this.getAttribute("type")) && (form = this.getFormOwner()) != null) {
                form.reset();
            }
        }
    }

    @Override
    public boolean canSelectText() {
        return (this.getMode() == Mode.TEXT || this.getMode() == Mode.NUMBER) && super.canSelectText();
    }

    private void triggerChangeEvent() {
        this.triggerInputEvent();
        this.triggerChangeOnlyEvent();
    }

    private void triggerInputEvent() {
        Event inputEvent = new Event(this, "input", true);
        Event.markTrustedFromCurrentDispatch(inputEvent);
        Event.tiggerEvent(inputEvent);
    }

    private void triggerChangeOnlyEvent() {
        Event changeEvent = new Event(this, "change", true);
        Event.markTrustedFromCurrentDispatch(changeEvent);
        Event.tiggerEvent(changeEvent);
    }

    private void openFileDialog() {
        String accept = this.getAttribute("accept");
        String pattern = Input.resolveFilePattern(accept);
        String description = "*.html".equals(pattern) ? "HTML files" : "Files";
        try (MemoryStack stack = MemoryStack.stackPush();){
            PointerBuffer filters = stack.mallocPointer(1);
            filters.put(stack.UTF8((CharSequence)pattern)).flip();
            String selected = TinyFileDialogs.tinyfd_openFileDialog((CharSequence)"Choose file", (CharSequence)"", (PointerBuffer)filters, (CharSequence)description, (boolean)this.isMultiple());
            if (selected == null || selected.isBlank()) {
                return;
            }
            ArrayList<String> files = new ArrayList<String>();
            for (String path : selected.split("[\\r\\n;]+")) {
                if (path.isBlank() || !Input.acceptsFile(path.trim(), accept)) continue;
                files.add(path.trim());
            }
            if (files.isEmpty()) {
                return;
            }
            if (!this.isMultiple() && files.size() > 1) {
                files.subList(1, files.size()).clear();
            }
            this.setFileList(files);
            this.triggerChangeEvent();
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    private static String resolveFilePattern(String accept) {
        if (accept != null && accept.toLowerCase(Locale.ROOT).contains("html")) {
            return "*.html";
        }
        if (accept != null && accept.trim().startsWith(".")) {
            String extension = accept.trim().split("[, ]", 2)[0];
            return "*" + extension;
        }
        return "*.*";
    }

    private static boolean acceptsFile(String path, String accept) {
        if (accept == null || accept.isBlank()) {
            return true;
        }
        String lowerPath = path.toLowerCase(Locale.ROOT);
        for (String token : accept.split(",")) {
            String candidate = token.trim().toLowerCase(Locale.ROOT);
            int parameter = candidate.indexOf(59);
            if (parameter >= 0) {
                candidate = candidate.substring(0, parameter).trim();
            }
            if (candidate.isEmpty()) continue;
            if (candidate.startsWith(".")) {
                if (!lowerPath.endsWith(candidate)) continue;
                return true;
            }
            if (candidate.endsWith("/*")) {
                String media = candidate.substring(0, candidate.length() - 2);
                String mime = Input.mimeForPath(lowerPath);
                if (mime.isEmpty() || !mime.startsWith(media + "/")) continue;
                return true;
            }
            if (!candidate.contains("/") || !candidate.equals(Input.mimeForPath(lowerPath))) continue;
            return true;
        }
        return false;
    }

    private static String mimeForPath(String lowerPath) {
        if (lowerPath == null) {
            return "";
        }
        int dot = lowerPath.lastIndexOf(46);
        if (dot < 0 || dot + 1 >= lowerPath.length()) {
            return "";
        }
        return switch (lowerPath.substring(dot + 1)) {
            case "html", "htm" -> "text/html";
            case "txt", "text" -> "text/plain";
            case "csv" -> "text/csv";
            case "css" -> "text/css";
            case "js", "mjs" -> "text/javascript";
            case "json" -> "application/json";
            case "xml" -> "application/xml";
            case "pdf" -> "application/pdf";
            case "zip" -> "application/zip";
            case "doc" -> "application/msword";
            case "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "xls" -> "application/vnd.ms-excel";
            case "xlsx" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case "png" -> "image/png";
            case "jpg", "jpeg" -> "image/jpeg";
            case "gif" -> "image/gif";
            case "bmp" -> "image/bmp";
            case "webp" -> "image/webp";
            case "svg" -> "image/svg+xml";
            case "mp3", "wav", "ogg" -> "audio/*";
            case "mp4", "webm", "mov" -> "video/*";
            default -> "";
        };
    }

    public boolean handleSpaceKey() {
        if (this.isDisabled()) {
            return false;
        }
        Mode mode = this.getMode();
        if (mode == Mode.HIDDEN) {
            return false;
        }
        if (mode == Mode.CHECKBOX) {
            this.setChecked(!this.isChecked());
            this.triggerChangeEvent();
            return true;
        }
        if (mode == Mode.RADIO) {
            if (!this.isChecked()) {
                this.setChecked(true);
                this.triggerChangeEvent();
            }
            return true;
        }
        if (mode == Mode.RANGE) {
            return true;
        }
        if (mode == Mode.COLOR) {
            this.handleClickDefault();
            return true;
        }
        return false;
    }

    @Override
    public void drawPhase(PoseStack poseStack, Base.RenderPhase phase) {
        Mode mode;
        Rect rectRenderer = Rect.of(this);
        if (phase == Base.RenderPhase.SHADOW) {
            rectRenderer.drawShadow(poseStack);
        }
        if ((mode = this.getMode()) == Mode.HIDDEN) {
            return;
        }
        if (mode == Mode.CHECKBOX || mode == Mode.RADIO) {
            if (phase == Base.RenderPhase.BODY) {
                this.drawCheckableInput(poseStack, rectRenderer, mode);
            }
            return;
        }
        if (phase == Base.RenderPhase.BORDER) {
            rectRenderer.drawBorder(poseStack);
        }
        if (phase != Base.RenderPhase.BODY) {
            return;
        }
        rectRenderer.drawBody(poseStack);
        Base.offsetPaintDepth(poseStack, 0.16f);
        if (mode == Mode.RANGE) {
            this.drawRangeInput(poseStack, rectRenderer);
            return;
        }
        if (mode == Mode.COLOR) {
            this.drawColorInput(poseStack, rectRenderer);
            return;
        }
        if (mode == Mode.BUTTON) {
            this.drawButtonInput(poseStack, rectRenderer);
            return;
        }
        if (mode == Mode.NUMBER) {
            this.drawNumberInput(poseStack, rectRenderer);
            return;
        }
        this.drawTextInput(poseStack, rectRenderer);
    }

    private void drawButtonInput(PoseStack poseStack, Rect rectRenderer) {
        String label;
        String string = label = this.value == null || this.value.isBlank() ? this.getAttribute("value") : this.value;
        if (label == null || label.isBlank()) {
            label = "button";
        }
        Text text = Text.of(this);
        text.content = label;
        text.color = new Color(Text.getFontColor(this));
        Position contentPos = rectRenderer.getContentPosition();
        FontDrawer.drawFont(poseStack, text, new Position(contentPos.x, this.singleLineDrawY(rectRenderer, text)));
    }

    private void drawCheckableInput(PoseStack poseStack, Rect rectRenderer, Mode mode) {
        int backgroundColor;
        Box box = rectRenderer.box;
        Background background = rectRenderer.background;
        float outerX = (float)(rectRenderer.position.x + box.getMarginLeft());
        float outerY = (float)(rectRenderer.position.y + box.getMarginTop());
        float outerW = (float)box.elementSize().width();
        float outerH = (float)box.elementSize().height();
        float controlSize = Math.max(0.0f, Math.min(outerW, outerH));
        float x = outerX + (outerW - controlSize) * 0.5f;
        float y = outerY + (outerH - controlSize) * 0.5f;
        float borderWidth = Math.max(1.0f, Math.min(2.0f, controlSize / 8.0f));
        float radius = mode == Mode.RADIO ? controlSize * 0.5f : Math.min(controlSize * 0.25f, 4.0f);
        boolean checked = this.isChecked();
        int accentColor = this.resolveAccentColor();
        int n = checked ? accentColor : (backgroundColor = new Color(this.isDisabled() ? "#F2F2F2" : "#FFFFFF").getValue());
        int borderColor = checked ? accentColor : new Color(this.isDisabled() ? "#B7B7B7" : "#767676").getValue();
        Graph.beginBatch();
        Graph.drawUnifiedRoundedRect(poseStack.m_85850_().m_252922_(), x, y, controlSize, controlSize, this.uniformRadii(radius), backgroundColor);
        Base.offsetPaintDepth(poseStack, 0.24f);
        Graph.drawComplexRoundedBorder(poseStack.m_85850_().m_252922_(), x, y, controlSize, controlSize, this.uniformRadii(radius), new float[]{borderWidth, borderWidth, borderWidth, borderWidth}, new int[]{borderColor, borderColor, borderColor, borderColor});
        if (checked) {
            Base.offsetPaintDepth(poseStack, 0.2f);
            int indicatorColor = new Color(this.isDisabled() ? "#F7F7F7" : "#FFFFFF").getValue();
            if (mode == Mode.RADIO) {
                float dotSize = Math.max(4.0f, controlSize * 0.42f);
                float dotX = x + (controlSize - dotSize) * 0.5f;
                float dotY = y + (controlSize - dotSize) * 0.5f;
                Graph.drawUnifiedRoundedRect(poseStack.m_85850_().m_252922_(), dotX, dotY, dotSize, dotSize, this.uniformRadii(dotSize * 0.5f), indicatorColor);
            } else {
                this.drawCheckboxMark(poseStack, x, y, controlSize, indicatorColor);
            }
        }
    }

    private void drawCheckboxMark(PoseStack poseStack, float x, float y, float size, int color) {
        float py;
        float px;
        int i;
        float pixel = Math.max(1.5f, size * 0.11f);
        for (i = 0; i <= 4; ++i) {
            px = x + size * 0.2f + (float)i * size * 0.055f;
            py = y + size * 0.48f + (float)i * size * 0.055f;
            Graph.drawFillRect(poseStack.m_85850_().m_252922_(), px, py, px + pixel, py + pixel, color);
        }
        for (i = 0; i <= 8; ++i) {
            px = x + size * 0.42f + (float)i * size * 0.045f;
            py = y + size * 0.7f - (float)i * size * 0.055f;
            Graph.drawFillRect(poseStack.m_85850_().m_252922_(), px, py, px + pixel, py + pixel, color);
        }
    }

    private int resolveCheckableBorderColor(Box box) {
        Box.SideBorder top = box.getBorderTopSide();
        if (top.size() > 0.0) {
            return top.color().getValue();
        }
        return new Color(Text.getFontColor(this)).getValue();
    }

    private float[] uniformRadii(float radius) {
        return new float[]{radius, radius, radius, radius};
    }

    private void drawTextInput(PoseStack poseStack, Rect rectRenderer) {
        String renderContent;
        String textToShow = this.getRenderText();
        if ("password".equalsIgnoreCase(this.getAttribute("type")) && !textToShow.isEmpty()) {
            textToShow = "*".repeat(textToShow.length());
        }
        boolean isPlaceholder = textToShow.isEmpty() && !this.placeholder.isEmpty();
        String string = renderContent = isPlaceholder ? this.placeholder : textToShow;
        if (renderContent.isEmpty() && !Element.isElementFocusing(this)) {
            return;
        }
        Text text = Text.of(this);
        text.content = renderContent;
        text.color = isPlaceholder ? new Color("#888888") : new Color(Text.getFontColor(this));
        Position contentPos = rectRenderer.getContentPosition();
        float drawX = (float)(contentPos.x + this.resolveTextAlignX(renderContent) - this.scrollLeft);
        float drawY = (float)this.singleLineDrawY(rectRenderer, text);
        if (!isPlaceholder) {
            Base.offsetPaintDepth(poseStack, 0.04f);
            this.drawSingleLineSelection(poseStack, rectRenderer, textToShow, drawY, text.lineHeight);
            Base.offsetPaintDepth(poseStack, 0.1f);
        }
        if (!isPlaceholder && this.hasSelection() && this.canSelectText()) {
            int min = Math.max(0, Math.min(this.selMin(), textToShow.length()));
            int max = Math.max(0, Math.min(this.selMax(), textToShow.length()));
            String before = textToShow.substring(0, min);
            String selected = textToShow.substring(min, max);
            String after = textToShow.substring(max);
            float segmentX = drawX;
            if (!before.isEmpty()) {
                text.content = before;
                text.color = new Color(Text.getFontColor(this));
                FontDrawer.drawFont(poseStack, text, new Position(segmentX, drawY));
                segmentX += (float)Size.measureText(this, before);
            }
            if (!selected.isEmpty()) {
                text.content = selected;
                text.color = new Color("#FFFFFF");
                FontDrawer.drawFont(poseStack, text, new Position(segmentX, drawY));
                segmentX += (float)Size.measureText(this, selected);
            }
            if (!after.isEmpty()) {
                text.content = after;
                text.color = new Color(Text.getFontColor(this));
                FontDrawer.drawFont(poseStack, text, new Position(segmentX, drawY));
            }
        } else {
            FontDrawer.drawFont(poseStack, text, new Position(drawX, drawY));
        }
        Base.offsetPaintDepth(poseStack, 0.24f);
        this.drawSingleLineCursor(poseStack, textToShow, drawX, drawY, (float)text.lineHeight);
    }

    private void drawNumberInput(PoseStack poseStack, Rect rectRenderer) {
        boolean spinnerDisabled;
        this.drawTextInput(poseStack, rectRenderer);
        Base.offsetPaintDepth(poseStack, 0.24f);
        Box box = rectRenderer.box;
        Position contentPos = rectRenderer.getContentPosition();
        double width = Math.max(0.0, box.innerSize().width());
        double height = Math.max(0.0, box.innerSize().height());
        double spinnerWidth = Input.numberSpinnerWidth(width);
        if (width <= 0.0 || height <= 0.0 || spinnerWidth <= 0.0) {
            return;
        }
        float left = (float)(contentPos.x + width - spinnerWidth);
        float top = (float)contentPos.y;
        float right = (float)(contentPos.x + width);
        float bottom = (float)(contentPos.y + height);
        boolean bl = spinnerDisabled = this.isDisabled() || this.hasAttribute("readonly");
        int background = new Color(spinnerDisabled ? "#D5D5D5" : (this.isHover ? "#E8E8E8" : "#F4F4F4")).getValue();
        int separator = new Color(spinnerDisabled ? "#B7B7B7" : "#C8C8C8").getValue();
        int arrow = new Color(spinnerDisabled ? "#929292" : "#4F4F4F").getValue();
        Graph.beginBatch();
        Graph.drawFillRect(poseStack.m_85850_().m_252922_(), left, top, right, bottom, background);
        Graph.drawFillRect(poseStack.m_85850_().m_252922_(), left, (float)((double)top + height / 2.0 - 0.5), right, (float)((double)top + height / 2.0 + 0.5), separator);
        Input.drawSpinnerTriangle(poseStack, left + (float)spinnerWidth / 2.0f, top + (float)height / 4.0f, true, arrow);
        Input.drawSpinnerTriangle(poseStack, left + (float)spinnerWidth / 2.0f, top + (float)(height * 3.0 / 4.0), false, arrow);
    }

    private void drawColorInput(PoseStack poseStack, Rect rectRenderer) {
        Box box = rectRenderer.box;
        Position contentPos = rectRenderer.getContentPosition();
        double width = Math.max(0.0, box.innerSize().width());
        double height = Math.max(0.0, box.innerSize().height());
        if (width <= 0.0 || height <= 0.0) {
            return;
        }
        float left = (float)contentPos.x;
        float top = (float)contentPos.y;
        float right = (float)(contentPos.x + width);
        float bottom = (float)(contentPos.y + height);
        int color = new Color(this.getValue()).getValue();
        int alpha = color >>> 24 & 0xFF;
        Graph.beginBatch();
        if (alpha < 255) {
            int light = new Color("#E6E6E6").getValue();
            int dark = new Color("#BDBDBD").getValue();
            float tile = Math.max(3.0f, Math.min(6.0f, (float)Math.min(width, height) / 3.0f));
            for (float y = top; y < bottom; y += tile) {
                for (float x = left; x < right; x += tile) {
                    boolean alternate = ((int)Math.floor((x - left) / tile) + (int)Math.floor((y - top) / tile)) % 2 == 0;
                    Graph.drawFillRect(poseStack.m_85850_().m_252922_(), x, y, Math.min(right, x + tile), Math.min(bottom, y + tile), alternate ? light : dark);
                }
            }
        }
        Base.offsetPaintDepth(poseStack, 0.24f);
        Graph.drawFillRect(poseStack.m_85850_().m_252922_(), left, top, right, bottom, color);
    }

    private static void drawSpinnerTriangle(PoseStack poseStack, float centerX, float centerY, boolean up, int color) {
        for (int row = 0; row < 5; ++row) {
            int distance = up ? row : 4 - row;
            float halfWidth = 1.0f + (float)distance * 0.85f;
            float y = centerY - 2.0f + (float)row;
            Graph.drawFillRect(poseStack.m_85850_().m_252922_(), centerX - halfWidth, y, centerX + halfWidth + 1.0f, y + 1.0f, color);
        }
    }

    private static double numberSpinnerWidth(double width) {
        return Math.max(0.0, Math.min(width, Math.max(14.0, Math.min(18.0, width * 0.25))));
    }

    private int numberSpinnerDirection(MouseEvent event) {
        if (event == null || this.getMode() != Mode.NUMBER || this.isDisabled() || this.hasAttribute("readonly")) {
            return 0;
        }
        Element.DOMRect rect = this.getBoundingClientRect();
        if (rect == null || rect.width <= 0.0 || rect.height <= 0.0) {
            return 0;
        }
        Box box = Box.of(this);
        double contentLeft = rect.x + box.getBorderLeft() + box.getPaddingLeft();
        double contentTop = rect.y + box.getBorderTop() + box.getPaddingTop();
        double contentWidth = Math.max(0.0, rect.width - box.getBorderHorizontal() - box.getPaddingHorizontal() - this.getVerticalScrollbarGutter());
        double contentHeight = Math.max(0.0, rect.height - box.getBorderVertical() - box.getPaddingVertical() - this.getHorizontalScrollbarGutter());
        double spinnerWidth = Input.numberSpinnerWidth(contentWidth);
        double spinnerRight = contentLeft + contentWidth;
        double spinnerBottom = contentTop + contentHeight;
        if (contentWidth <= 0.0 || contentHeight <= 0.0 || event.clientX < spinnerRight - spinnerWidth || event.clientX > spinnerRight || event.clientY < contentTop || event.clientY > spinnerBottom) {
            return 0;
        }
        return event.clientY < contentTop + contentHeight / 2.0 ? 1 : -1;
    }

    public boolean handleNumberSpinner(MouseEvent event) {
        int direction = this.numberSpinnerDirection(event);
        return direction != 0 && this.adjustNumber(direction);
    }

    public boolean handleNumberWheel(MouseEvent event) {
        if (event == null || this.getMode() != Mode.NUMBER || this.isDisabled() || this.hasAttribute("readonly")) {
            return false;
        }
        double delta = event.deltaY;
        if (!Double.isFinite(delta) || Math.abs(delta) < 1.0E-6) {
            delta = event.scrollDelta;
        }
        if (!Double.isFinite(delta) || Math.abs(delta) < 1.0E-6) {
            return false;
        }
        return this.adjustNumber(delta < 0.0 ? 1 : -1);
    }

    private boolean adjustNumber(int direction) {
        String after;
        double max;
        double min;
        double clamped;
        if (this.getMode() != Mode.NUMBER || direction == 0 || this.isDisabled() || this.hasAttribute("readonly")) {
            return false;
        }
        String before = this.getValue();
        if (direction > 0) {
            this.stepUp(1);
        } else {
            this.stepDown(1);
        }
        double next = this.getValueAsNumber();
        if (Double.isFinite(next) && Double.compare(next, clamped = Math.max(min = this.parseNumberAttribute("min", Double.NEGATIVE_INFINITY), Math.min(max = this.parseNumberAttribute("max", Double.POSITIVE_INFINITY), next))) != 0) {
            this.setValueAsNumber(clamped);
        }
        if (Objects.equals(before, after = this.getValue())) {
            return false;
        }
        this.triggerChangeEvent();
        return true;
    }

    private void openColorPicker() {
        if (this.document == null) {
            return;
        }
        ColorPicker.pick(this.getValue()).thenAccept(selected -> selected.ifPresent(next -> Document.runWithContext(this.document, () -> Event.runTrustedAction(() -> {
            if (Objects.equals(this.getValue(), next)) {
                return;
            }
            this.setValue((String)next);
            this.triggerChangeEvent();
        }))));
    }

    private void drawRangeInput(PoseStack poseStack, Rect rectRenderer) {
        double width = rectRenderer.box.innerSize().width();
        double centerY = rectRenderer.getContentPosition().y + rectRenderer.box.innerSize().height() / 2.0;
        double left = rectRenderer.getContentPosition().x;
        double right = left + Math.max(0.0, width);
        double fraction = this.rangeFraction();
        int track = new Color(this.isDisabled() ? "#A5A5A5" : "#777777").getValue();
        int accent = this.resolveAccentColor();
        Graph.drawFillRect(poseStack.m_85850_().m_252922_(), (float)left, (float)centerY - 1.0f, (float)right, (float)centerY + 1.0f, track);
        Graph.drawFillRect(poseStack.m_85850_().m_252922_(), (float)left, (float)centerY - 1.0f, (float)(left + (right - left) * fraction), (float)centerY + 1.0f, accent);
        Base.offsetPaintDepth(poseStack, 0.24f);
        float knobX = (float)(left + (right - left) * fraction - 5.0);
        Graph.drawUnifiedRoundedRect(poseStack.m_85850_().m_252922_(), knobX, (float)centerY - 5.0f, 10.0f, 10.0f, this.uniformRadii(5.0f), this.isDisabled() ? new Color("#B7B7B7").getValue() : accent);
    }

    public boolean handleRangeKey(int key) {
        String type;
        if (this.getMode() == Mode.NUMBER) {
            boolean up;
            if (this.isDisabled() || this.hasAttribute("readonly")) {
                return false;
            }
            boolean down = key == 263 || key == 264;
            boolean bl = up = key == 262 || key == 265;
            if (!down && !up) {
                return false;
            }
            this.adjustNumber(up ? 1 : -1);
            return true;
        }
        if (this.getMode() != Mode.RANGE || this.isDisabled()) {
            return false;
        }
        String string = key == 263 || key == 264 ? "down" : (type = key == 262 || key == 265 ? "up" : "");
        if (type.isEmpty()) {
            return false;
        }
        double current = this.getValueAsNumber();
        if (!Double.isFinite(current)) {
            current = this.rangeMin();
        }
        double step = this.rangeStep();
        this.setValueAsNumber(current + ("up".equals(type) ? step : -step));
        this.triggerChangeEvent();
        return true;
    }

    public void setValueFromPointer(double fraction) {
        this.setRangeValueFromFraction(fraction);
    }

    private boolean setRangeValueFromPointer(MouseEvent event) {
        double offset;
        if (event == null) {
            return false;
        }
        Element.DOMRect rect = this.getBoundingClientRect();
        double width = rect != null && Double.isFinite(rect.width) && rect.width > 0.0 ? rect.width : Math.max(1.0, Box.of(this).innerSize().width());
        double d = offset = rect == null ? event.offsetX : event.clientX - rect.x;
        if (!Double.isFinite(offset)) {
            offset = event.offsetX;
        }
        return this.setRangeValueFromFraction(offset / width);
    }

    private boolean setRangeValueFromFraction(double fraction) {
        double min = this.rangeMin();
        double max = this.rangeMax();
        double value = min + Math.max(0.0, Math.min(1.0, fraction)) * (max - min);
        double step = this.rangeStep();
        if (step > 0.0) {
            value = min + (double)Math.round((value - min) / step) * step;
        }
        double next = Math.max(min, Math.min(max, value));
        double previous = this.getValueAsNumber();
        this.setValueAsNumber(next);
        double current = this.getValueAsNumber();
        if (Double.isFinite(previous) && Double.isFinite(current)) {
            return Double.compare(previous, current) != 0;
        }
        return !Objects.equals(Double.toString(previous), Double.toString(current));
    }

    private double rangeMin() {
        return this.parseNumberAttribute("min", 0.0);
    }

    private double rangeMax() {
        return this.parseNumberAttribute("max", 100.0);
    }

    private double rangeStep() {
        return this.parseNumberAttribute("step", 1.0);
    }

    private double parseNumberAttribute(String name, double fallback) {
        try {
            double value = Double.parseDouble(this.getAttribute(name));
            return Double.isFinite(value) ? value : fallback;
        }
        catch (Exception ignored) {
            return fallback;
        }
    }

    private double rangeFraction() {
        double min = this.rangeMin();
        double max = this.rangeMax();
        double value = this.getValueAsNumber();
        if (!Double.isFinite(value) || max <= min) {
            return 0.5;
        }
        return Math.max(0.0, Math.min(1.0, (value - min) / (max - min)));
    }

    private int resolveAccentColor() {
        String accent = this.getComputedStyle().accentColor;
        if (accent == null || accent.isBlank() || "unset".equalsIgnoreCase(accent) || "auto".equalsIgnoreCase(accent)) {
            accent = "#0075FF";
        }
        return new Color(accent).getValue();
    }

    private double singleLineDrawY(Rect rectRenderer, Text text) {
        if (rectRenderer == null || text == null) {
            return 0.0;
        }
        Position contentPos = rectRenderer.getContentPosition();
        double contentHeight = Math.max(0.0, rectRenderer.box.innerSize().height());
        return contentPos.y + (contentHeight - text.lineHeight) / 2.0;
    }

    private static enum Mode {
        TEXT,
        NUMBER,
        COLOR,
        BUTTON,
        CHECKBOX,
        RADIO,
        FILE,
        RANGE,
        HIDDEN;

    }
}

