/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.style;

import com.sighs.apricityui.dom.TextNode;
import com.sighs.apricityui.dom.TextTransform;
import com.sighs.apricityui.element.AbstractText;
import com.sighs.apricityui.element.Translation;
import com.sighs.apricityui.init.Document;
import com.sighs.apricityui.init.Element;
import com.sighs.apricityui.init.Node;
import com.sighs.apricityui.layout.Box;
import com.sighs.apricityui.layout.Size;
import com.sighs.apricityui.parser.CSS;
import com.sighs.apricityui.parser.Color;
import com.sighs.apricityui.parser.CssString;
import com.sighs.apricityui.resource.Font;
import com.sighs.apricityui.spi.AuiServices;
import com.sighs.apricityui.style.Style;
import java.awt.Canvas;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Text {
    private static final Canvas METRICS_CANVAS = new Canvas();
    private static final FontRenderContext BROWSER_FONT_RENDER_CONTEXT = new FontRenderContext(new AffineTransform(), true, true);
    private static final double BROWSER_NORMAL_LINE_HEIGHT_LEADING = 1.125;
    private static final double BROWSER_NORMAL_LINE_HEIGHT_MAX = 1.45;
    private static final int LINE_WIDTH_CACHE_LIMIT = 2048;
    private static final Map<LineMeasureKey, Double> LINE_WIDTH_CACHE = Collections.synchronizedMap(new LinkedHashMap<LineMeasureKey, Double>(64, 0.75f, true){

        @Override
        protected boolean removeEldestEntry(Map.Entry<LineMeasureKey, Double> eldest) {
            return this.size() > 2048;
        }
    });
    private String cachedKey = null;
    private int cachedKeyHash = 0;
    public double fontSize = -1.0;
    public int fontWeight = -1;
    public boolean oblique = false;
    public double strokeWidth = 0.0;
    public Color strokeColor = null;
    public Color color = null;
    public String textDecoration = "none";
    public String fontFamily = "unset";
    public String content = "";
    public double lineHeight = -1.0;
    public String direction = "ltr";
    public String textAlign = "start";
    public String verticalAlign = "baseline";
    public String whiteSpace = "normal";
    public double textIndent = 0.0;
    public double letterSpacing = 0.0;
    public Document.FontMode fontMode = Document.FontMode.WEB_SCALED;
    public Size size = null;
    public String rasterBackgroundColor = "unset";
    public boolean flexDirect = false;

    public static void warmUpFontMetrics() {
        Text.warmUpFontFamily("sans-serif");
    }

    public static void warmUpFontFamily(String fontFamily) {
        if (fontFamily == null || fontFamily.isBlank() || fontFamily.contains("var(")) {
            return;
        }
        String sample = "AUI \u4e2d\u6587";
        List<Font.FontRun> runs = com.sighs.apricityui.resource.Font.planFontRuns(fontFamily, 0, com.sighs.apricityui.resource.Font.getBaseFontSize(), sample);
        for (Font.FontRun run : runs) {
            if (run == null || run.font() == null) continue;
            FontMetrics metrics = METRICS_CANVAS.getFontMetrics(run.font());
            metrics.stringWidth(run.text());
            run.font().getStringBounds(run.text(), BROWSER_FONT_RENDER_CONTEXT).getWidth();
        }
    }

    public static double getFontSize(Element element) {
        Document.FontMode fontMode = Text.getFontMode(element);
        double fontSize = fontMode.defaultFontSize();
        for (Element e : element.getRouteArray()) {
            e.getComputedStyle();
            String f = Text.getDeclaredFontSize(e);
            if (f.equals("unset")) continue;
            Double parsed = Size.tryResolveLength(f, fontSize, Size.getRootFontSize(element == null ? null : element.document));
            if (parsed == null) break;
            fontSize = parsed;
            break;
        }
        return fontSize;
    }

    public static String getFontFamily(Element element) {
        String fontFamily = "unset";
        for (Element e : element.getRouteArray()) {
            String f = e.getComputedStyle().fontFamily;
            if (f.equals("unset")) continue;
            fontFamily = f;
            break;
        }
        return fontFamily;
    }

    public static int getFontWeight(Element element) {
        int fontWeight = 400;
        for (Element e : element.getRouteArray()) {
            String f = e.getComputedStyle().fontWeight;
            if (f.equals("unset")) continue;
            fontWeight = Text.parseFontWeight(f);
            break;
        }
        return fontWeight;
    }

    public static boolean isOblique(Element element) {
        for (Element e : element.getRouteArray()) {
            String f = e.getComputedStyle().fontStyle;
            if (f.equals("unset")) continue;
            return Text.isObliqueValue(f);
        }
        return false;
    }

    public static int parseFontWeight(String raw) {
        if (raw == null || raw.isBlank()) {
            return 400;
        }
        String value = raw.trim().toLowerCase(Locale.ROOT);
        if (value.equals("unset") || value.equals("normal")) {
            return 400;
        }
        if (value.equals("bold") || value.equals("bolder")) {
            return 700;
        }
        if (value.equals("lighter")) {
            return 300;
        }
        try {
            int parsed = Integer.parseInt(value);
            if (parsed < 1) {
                return 1;
            }
            return Math.min(parsed, 1000);
        }
        catch (NumberFormatException numberFormatException) {
            return 400;
        }
    }

    public static boolean isObliqueValue(String raw) {
        if (raw == null || raw.isBlank()) {
            return false;
        }
        String value = raw.trim().toLowerCase(Locale.ROOT);
        return value.equals("oblique");
    }

    public static Style.TextStroke parseTextStroke(String raw) {
        if (raw == null || raw.isBlank()) {
            return Style.TextStroke.NONE;
        }
        String value = raw.trim();
        String lower = value.toLowerCase(Locale.ROOT);
        if (lower.equals("unset") || lower.equals("none")) {
            return Style.TextStroke.NONE;
        }
        double width = 0.0;
        String colorPart = value;
        int pxIndex = lower.indexOf("px");
        if (pxIndex > 0) {
            int start;
            for (start = pxIndex - 1; start >= 0 && Character.isDigit(lower.charAt(start)); --start) {
            }
            String number = lower.substring(start + 1, pxIndex).trim();
            Double parsed = Size.parseNumber(number);
            if (parsed != null) {
                width = Math.max(0.0, parsed);
            }
            colorPart = (value.substring(0, Math.max(0, start + 1)) + " " + value.substring(pxIndex + 2)).trim();
        }
        int color = Color.parse(colorPart.isBlank() ? "#000" : colorPart);
        if (width <= 0.0) {
            return Style.TextStroke.NONE;
        }
        return new Style.TextStroke(width, color);
    }

    public static Style.TextStroke getTextStroke(Element element) {
        for (Element e : element.getRouteArray()) {
            String s = e.getComputedStyle().textStroke;
            if (s.equals("unset")) continue;
            return Text.parseTextStroke(s);
        }
        return Style.TextStroke.NONE;
    }

    public static String getTextDirection(Element element) {
        for (Element e : element.getRouteArray()) {
            String value = e.getComputedStyle().direction;
            if (value.equals("unset")) continue;
            return value.trim().toLowerCase(Locale.ROOT);
        }
        return "ltr";
    }

    public static String getTextAlign(Element element) {
        for (Element e : element.getRouteArray()) {
            String value = e.getComputedStyle().textAlign;
            if (value.equals("unset")) continue;
            return value.trim().toLowerCase(Locale.ROOT);
        }
        return "start";
    }

    public static String getVerticalAlign(Element element) {
        for (Element e : element.getRouteArray()) {
            String value = e.getComputedStyle().verticalAlign;
            if (value.equals("unset")) continue;
            return value.trim().toLowerCase(Locale.ROOT);
        }
        return "top";
    }

    public static String getWhiteSpace(Element element) {
        for (Element e : element.getRouteArray()) {
            String value = e.getComputedStyle().whiteSpace;
            if (value.equals("unset")) continue;
            return value.trim().toLowerCase(Locale.ROOT);
        }
        return "normal";
    }

    public static double getTextIndent(Element element) {
        for (Element e : element.getRouteArray()) {
            String value = e.getComputedStyle().textIndent;
            if (value.equals("unset")) continue;
            Double indent = Size.tryResolveLength(value, Size.getScaleWidth(element));
            return indent == null ? 0.0 : indent;
        }
        return 0.0;
    }

    public static double getLetterSpacing(Element element) {
        for (Element e : element.getRouteArray()) {
            String value = e.getComputedStyle().letterSpacing;
            if (value.equals("unset")) continue;
            String normalized = value.trim().toLowerCase(Locale.ROOT);
            if (normalized.equals("normal")) {
                return 0.0;
            }
            Double spacing = Size.tryResolveLength(value, Text.getFontSize(element));
            return spacing == null ? 0.0 : spacing;
        }
        return 0.0;
    }

    public static int getFontColor(Element element) {
        String styleColor = element.getComputedStyle().color;
        if (styleColor.equals("unset")) {
            Element parent = element.parentElement;
            while (parent != null) {
                String parentColor = parent.getComputedStyle().color;
                if (!parentColor.equals("unset")) {
                    styleColor = parentColor;
                    break;
                }
                parent = parent.parentElement;
            }
        }
        if (styleColor.equals("unset")) {
            styleColor = "#000";
        }
        return Color.parse(styleColor);
    }

    public static int getSelectionColor(Element element) {
        String selection = element.getComputedStyle().selectionColor;
        if (selection.equals("unset")) {
            Element parent = element.parentElement;
            while (parent != null) {
                String parentSelection = parent.getComputedStyle().selectionColor;
                if (!parentSelection.equals("unset")) {
                    selection = parentSelection;
                    break;
                }
                parent = parent.parentElement;
            }
        }
        if (selection.equals("unset")) {
            selection = "#0078D7";
        }
        return Color.parse(selection);
    }

    public static Text of(Element element) {
        Text cache;
        boolean naturalMeasurement = Size.isNaturalMeasurementContext();
        Text text = cache = naturalMeasurement ? null : element.getRenderer().text.get();
        if (cache != null) {
            return cache;
        }
        Text text2 = new Text();
        text2.fontMode = Text.getFontMode(element);
        text2.content = Text.resolveElementTextContent(element);
        if (element.tagName.equals("INPUT")) {
            text2.content = element.value;
        }
        if (element.tagName.equals("TEXTAREA")) {
            text2.content = element.value;
        }
        text2.content = TextTransform.apply(text2.content, element);
        ResolveState state = new ResolveState();
        for (Element e : element.getRouteArray()) {
            Style style = e.getComputedStyle();
            boolean unresolved = false;
            unresolved |= Text.resolveFontFamily(text2, style);
            unresolved |= Text.resolveFontSize(text2, style, e, element);
            unresolved |= Text.resolveFontWeight(text2, style);
            unresolved |= Text.resolveFontStyle(text2, style, state);
            unresolved |= Text.resolveTextStroke(text2, style, state);
            unresolved |= Text.resolveColor(text2, style);
            unresolved |= Text.resolveLineHeight(state, style);
            unresolved |= Text.resolveTextDecoration(text2, style, state);
            unresolved |= Text.resolveDirection(text2, style, state);
            unresolved |= Text.resolveTextAlign(text2, style, state);
            unresolved |= Text.resolveVerticalAlign(text2, style, state);
            unresolved |= Text.resolveWhiteSpace(text2, style, state);
            unresolved |= Text.resolveTextIndent(text2, style, element, state);
            if (!(unresolved |= Text.resolveLetterSpacing(text2, style, state))) break;
        }
        if (text2.fontSize == -1.0) {
            text2.fontSize = text2.fontMode.defaultFontSize();
        }
        if (text2.fontWeight == -1) {
            text2.fontWeight = 400;
        }
        if (text2.color == null) {
            text2.color = Color.BLACK;
        }
        if (text2.strokeColor == null) {
            text2.strokeColor = Color.BLACK;
        }
        if (!state.whiteSpace) {
            if (element.tagName.equals("PRE")) {
                text2.whiteSpace = "pre";
            } else if (element.tagName.equals("TEXTAREA")) {
                text2.whiteSpace = "pre-wrap";
            }
        }
        if (!(element instanceof AbstractText)) {
            text2.content = Text.normalizeWhiteSpaceContent(text2.content, text2.whiteSpace);
        }
        text2.rasterBackgroundColor = Text.resolveRasterBackgroundColor(element);
        if (text2.lineHeight == -1.0) {
            text2.lineHeight = Text.calculateLineHeight(text2, state.lineHeightRaw);
        }
        text2.size = Text.measureSize(element, text2);
        if (!naturalMeasurement) {
            element.getRenderer().text.set(text2);
        }
        return text2;
    }

    private static boolean resolveFontFamily(Text text, Style style) {
        if (!text.fontFamily.equals("unset")) {
            return false;
        }
        if (!style.fontFamily.equals("unset")) {
            text.fontFamily = style.fontFamily;
        }
        return true;
    }

    private static boolean resolveFontSize(Text text, Style style, Element ancestor, Element root) {
        Double parsed;
        if (text.fontSize != -1.0) {
            return false;
        }
        String declaredFontSize = Text.getDeclaredFontSize(ancestor);
        if (!declaredFontSize.equals("unset") && (parsed = Size.tryResolveLength(declaredFontSize, text.fontMode.defaultFontSize(), Size.getRootFontSize(root.document))) != null) {
            text.fontSize = parsed;
        }
        return true;
    }

    private static boolean resolveFontWeight(Text text, Style style) {
        if (text.fontWeight != -1) {
            return false;
        }
        if (!style.fontWeight.equals("unset")) {
            text.fontWeight = Text.parseFontWeight(style.fontWeight);
        }
        return true;
    }

    private static boolean resolveFontStyle(Text text, Style style, ResolveState state) {
        if (state.fontStyle) {
            return false;
        }
        if (!style.fontStyle.equals("unset")) {
            text.oblique = Text.isObliqueValue(style.fontStyle);
            state.fontStyle = true;
        }
        return true;
    }

    private static boolean resolveTextStroke(Text text, Style style, ResolveState state) {
        if (state.textStroke) {
            return false;
        }
        if (!style.textStroke.equals("unset")) {
            Style.TextStroke stroke = Text.parseTextStroke(style.textStroke);
            text.strokeWidth = stroke.width();
            text.strokeColor = new Color(stroke.color());
            state.textStroke = true;
        }
        return true;
    }

    private static boolean resolveColor(Text text, Style style) {
        if (text.color != null) {
            return false;
        }
        if (!style.color.equals("unset")) {
            text.color = new Color(style.color);
        }
        return true;
    }

    private static boolean resolveLineHeight(ResolveState state, Style style) {
        if (state.lineHeight) {
            return false;
        }
        if (!style.lineHeight.equals("unset")) {
            state.lineHeightRaw = style.lineHeight;
            state.lineHeight = true;
        }
        return true;
    }

    private static boolean resolveTextDecoration(Text text, Style style, ResolveState state) {
        if (state.textDecoration) {
            return false;
        }
        if (!style.textDecoration.equals("unset")) {
            text.textDecoration = CssString.normalizeTextDecoration(style.textDecoration);
            state.textDecoration = true;
        }
        return true;
    }

    private static boolean resolveDirection(Text text, Style style, ResolveState state) {
        if (state.direction) {
            return false;
        }
        if (!style.direction.equals("unset")) {
            text.direction = CssString.normalizeDirection(style.direction);
            state.direction = true;
        }
        return true;
    }

    private static boolean resolveTextAlign(Text text, Style style, ResolveState state) {
        if (state.textAlign) {
            return false;
        }
        if (!style.textAlign.equals("unset")) {
            text.textAlign = CssString.normalizeTextAlign(style.textAlign);
            state.textAlign = true;
        }
        return true;
    }

    private static boolean resolveVerticalAlign(Text text, Style style, ResolveState state) {
        if (state.verticalAlign) {
            return false;
        }
        if (!style.verticalAlign.equals("unset")) {
            text.verticalAlign = CssString.normalizeVerticalAlign(style.verticalAlign);
            state.verticalAlign = true;
        }
        return true;
    }

    private static boolean resolveWhiteSpace(Text text, Style style, ResolveState state) {
        if (state.whiteSpace) {
            return false;
        }
        if (!style.whiteSpace.equals("unset")) {
            text.whiteSpace = CssString.normalizeWhiteSpace(style.whiteSpace);
            state.whiteSpace = true;
        }
        return true;
    }

    private static boolean resolveTextIndent(Text text, Style style, Element root, ResolveState state) {
        if (state.textIndent) {
            return false;
        }
        if (!style.textIndent.equals("unset")) {
            Double indent = Size.tryResolveLength(style.textIndent, Size.getScaleWidth(root));
            text.textIndent = indent == null ? 0.0 : indent;
            state.textIndent = true;
        }
        return true;
    }

    private static boolean resolveLetterSpacing(Text text, Style style, ResolveState state) {
        if (state.letterSpacing) {
            return false;
        }
        if (!style.letterSpacing.equals("unset")) {
            text.letterSpacing = Text.parseLetterSpacing(style.letterSpacing);
            state.letterSpacing = true;
        }
        return true;
    }

    public static Size measureSize(Element element, Text text) {
        Size measured;
        if (text == null) {
            return Size.ZERO;
        }
        WrappedText wrapped = Text.wrap(element, text);
        int lineClamp = Text.resolveLineClamp(element);
        int measuredLines = lineClamp > 0 ? Math.min(lineClamp, wrapped.lines().size()) : wrapped.lines().size();
        text.size = measured = new Size(wrapped.width(), Math.max(text.lineHeight, (double)measuredLines * text.lineHeight));
        return measured;
    }

    private static String resolveElementTextContent(Element element) {
        if (element == null) {
            return "";
        }
        if (element instanceof Translation) {
            Translation translation = (Translation)element;
            return translation.getTranslatedText();
        }
        if (element.childNodes.isEmpty()) {
            return element.innerText == null ? "" : element.innerText;
        }
        StringBuilder builder = new StringBuilder();
        for (Node child : element.childNodes) {
            if (!(child instanceof TextNode)) continue;
            TextNode textNode = (TextNode)child;
            builder.append(textNode.getTextContent());
        }
        if (builder.isEmpty()) {
            return element.innerText == null ? "" : element.innerText;
        }
        return builder.toString();
    }

    public static double calculateLineHeight(double fontSize, String lh) {
        if (lh == null || lh.isEmpty() || lh.equals("normal") || lh.equals("unset")) {
            return Text.normalLineHeight(fontSize);
        }
        if (lh.endsWith("%")) {
            Double percent = Size.parseNumber(lh);
            if (percent == null) {
                return Text.normalLineHeight(fontSize);
            }
            return fontSize * (percent / 100.0);
        }
        try {
            double multiplier = Double.parseDouble(lh);
            return fontSize * multiplier;
        }
        catch (NumberFormatException e) {
            Double val = Size.tryResolveLength(lh, fontSize);
            return val != null ? val : Text.normalLineHeight(fontSize);
        }
    }

    public static double calculateLineHeight(Text text, String lh) {
        if (text == null) {
            return Text.calculateLineHeight(16.0, lh);
        }
        if (lh == null || lh.isEmpty() || lh.equals("normal") || lh.equals("unset")) {
            return Text.normalLineHeight(text);
        }
        return Text.calculateLineHeight(text.fontSize, lh);
    }

    private static double normalLineHeight(double fontSize) {
        return fontSize * 1.2;
    }

    private static double normalLineHeight(Text text) {
        Font base;
        if (text == null) {
            return Text.normalLineHeight(16.0);
        }
        if (text.fontFamily == null || text.fontFamily.equals("unset")) {
            return Text.normalLineHeight(text.fontSize);
        }
        int fontStyle = 0;
        if (text.isBold()) {
            fontStyle |= 1;
        }
        if (text.isOblique()) {
            fontStyle |= 2;
        }
        if ((base = com.sighs.apricityui.resource.Font.resolveBaseFont(text.fontFamily)) == null) {
            return Text.normalLineHeight(text.fontSize);
        }
        Font measured = base.deriveFont(fontStyle, com.sighs.apricityui.resource.Font.getBaseFontSize());
        FontMetrics metrics = METRICS_CANVAS.getFontMetrics(measured);
        double scaled = (double)metrics.getHeight() * 1.125 * (text.fontSize / (double)com.sighs.apricityui.resource.Font.getBaseFontSize());
        double capped = Math.min(scaled, text.fontSize * 1.45);
        return Math.max(Text.normalLineHeight(text.fontSize), capped);
    }

    public static double baselineOffset(Text text) {
        if (text == null) {
            return 0.0;
        }
        double ascent = text.fontSize * 0.8;
        if (text.fontFamily != null && !text.fontFamily.equals("unset")) {
            Font base;
            int fontStyle = 0;
            if (text.isBold()) {
                fontStyle |= 1;
            }
            if (text.isOblique()) {
                fontStyle |= 2;
            }
            if ((base = com.sighs.apricityui.resource.Font.resolveBaseFont(text.fontFamily)) != null) {
                Font measured = base.deriveFont(fontStyle, (float)text.fontSize);
                ascent = METRICS_CANVAS.getFontMetrics(measured).getAscent();
            }
        }
        double halfLeading = (text.lineHeight - text.fontSize) / 2.0;
        return Math.floor(Math.max(0.0, halfLeading + ascent) + 1.0E-6);
    }

    public static double renderedAscent(Text text) {
        Font base;
        if (text == null) {
            return 0.0;
        }
        double rendered = text.renderedFontSize();
        if (text.fontFamily == null || text.fontFamily.equals("unset")) {
            return rendered * 0.8;
        }
        int fontStyle = 0;
        if (text.isBold()) {
            fontStyle |= 1;
        }
        if (text.isOblique()) {
            fontStyle |= 2;
        }
        if ((base = com.sighs.apricityui.resource.Font.resolveBaseFont(text.fontFamily)) == null) {
            return rendered * 0.8;
        }
        double baseSize = com.sighs.apricityui.resource.Font.getBaseFontSize();
        if (baseSize <= 0.0) {
            return rendered * 0.8;
        }
        Font measured = base.deriveFont(fontStyle, (float)baseSize);
        return (double)METRICS_CANVAS.getFontMetrics(measured).getAscent() * (rendered / baseSize);
    }

    public static double renderedBaselineOffset(Text text) {
        if (text == null) {
            return 0.0;
        }
        double halfLeading = Math.max(0.0, (text.lineHeight - text.fontSize) / 2.0);
        return halfLeading + Text.renderedAscent(text);
    }

    public static double measureText(Element element, String content) {
        Text text = Text.of(element);
        text.content = content;
        return Text.measureText(text);
    }

    public static double measureText(Text text) {
        if (text.content == null || text.content.isEmpty()) {
            return 0.0;
        }
        List<String> lines = Text.splitLines(text.content);
        double maxLine = 0.0;
        for (String line : lines) {
            maxLine = Math.max(maxLine, Text.measureLine(text, line));
        }
        return maxLine;
    }

    public static double measureLine(Text text, String line) {
        if (text == null) {
            return 0.0;
        }
        if (line == null || line.isEmpty()) {
            return 0.0;
        }
        LineMeasureKey cacheKey = new LineMeasureKey(com.sighs.apricityui.resource.Font.getMetricsRevision(), text.fontSize, text.fontWeight, text.oblique, text.strokeWidth, text.letterSpacing, text.fontMode, text.fontFamily, line);
        Double cached = LINE_WIDTH_CACHE.get(cacheKey);
        if (cached != null) {
            return cached;
        }
        double measured = Text.measureLineUncached(text, line);
        LINE_WIDTH_CACHE.put(cacheKey, measured);
        return measured;
    }

    private static double measureLineUncached(Text text, String line) {
        List<Font.FontRun> runs;
        double letterSpacingWidth;
        if (text == null) {
            return 0.0;
        }
        if (line == null || line.isEmpty()) {
            return 0.0;
        }
        int glyphCount = line.codePointCount(0, line.length());
        double d = letterSpacingWidth = glyphCount > 0 ? text.letterSpacing * (double)glyphCount : 0.0;
        if (text.fontFamily.equals("unset")) {
            return (double)AuiServices.client().getDefaultFontWidth(line, text.isBold(), text.isOblique(), 0.0) * text.defaultFontScale() + text.strokeWidth * 2.0 + letterSpacingWidth;
        }
        int fontStyle = 0;
        if (text.isBold()) {
            fontStyle |= 1;
        }
        if (text.isOblique()) {
            fontStyle |= 2;
        }
        if ((runs = com.sighs.apricityui.resource.Font.planFontRuns(text.fontFamily, fontStyle, com.sighs.apricityui.resource.Font.getBaseFontSize(), line)).isEmpty()) {
            return 0.0;
        }
        float currentSize = (float)text.renderedFontSize();
        float scale = currentSize / com.sighs.apricityui.resource.Font.getBaseFontSize();
        if (scale <= 0.0f || !Float.isFinite(scale)) {
            return letterSpacingWidth + text.strokeWidth * 2.0;
        }
        double baseLetterSpacing = text.letterSpacing / (double)scale;
        double baseWidth = com.sighs.apricityui.resource.Font.measureFontRuns(runs, BROWSER_FONT_RENDER_CONTEXT, baseLetterSpacing, true);
        return baseWidth * (double)scale + text.strokeWidth * 2.0;
    }

    public String toKey() {
        int h = 1;
        h = 31 * h + (int)Math.round(this.fontSize * 1000.0);
        h = 31 * h + this.fontWeight;
        h = 31 * h + (this.oblique ? 1 : 0);
        h = 31 * h + (int)Math.round(this.strokeWidth * 1000.0);
        h = 31 * h + (this.strokeColor == null ? 0 : this.strokeColor.getValue());
        h = 31 * h + (this.color == null ? 0 : this.color.getValue());
        h = 31 * h + (this.textDecoration == null ? 0 : this.textDecoration.hashCode());
        h = 31 * h + (this.fontFamily == null ? 0 : this.fontFamily.hashCode());
        h = 31 * h + (this.content == null ? 0 : this.content.hashCode());
        h = 31 * h + (this.direction == null ? 0 : this.direction.hashCode());
        h = 31 * h + (this.textAlign == null ? 0 : this.textAlign.hashCode());
        h = 31 * h + (this.verticalAlign == null ? 0 : this.verticalAlign.hashCode());
        h = 31 * h + (this.whiteSpace == null ? 0 : this.whiteSpace.hashCode());
        h = 31 * h + (int)Math.round(this.textIndent * 1000.0);
        h = 31 * h + (int)Math.round(this.letterSpacing * 1000.0);
        h = 31 * h + (this.fontMode == null ? 0 : this.fontMode.hashCode());
        h = 31 * h + (this.rasterBackgroundColor == null ? 0 : this.rasterBackgroundColor.hashCode());
        if (this.cachedKey != null && this.cachedKeyHash == h) {
            return this.cachedKey;
        }
        StringBuilder sb = new StringBuilder(64);
        sb.append(this.fontSize).append('/').append(this.fontWeight).append('/').append(this.oblique).append('/').append(this.strokeWidth).append('/').append(this.strokeColor == null ? 0 : this.strokeColor.getValue()).append('/').append(this.color == null ? 0 : this.color.getValue()).append('/').append(this.textDecoration == null ? "" : this.textDecoration).append('/').append(this.fontFamily == null ? "" : this.fontFamily).append('/').append(this.content == null ? "" : this.content).append('/').append(this.direction == null ? "" : this.direction).append('/').append(this.textAlign == null ? "" : this.textAlign).append('/').append(this.verticalAlign == null ? "" : this.verticalAlign).append('/').append(this.whiteSpace == null ? "" : this.whiteSpace).append('/').append(this.textIndent).append('/').append(this.letterSpacing).append('/').append(this.fontMode == null ? "" : this.fontMode.value()).append('/').append(this.rasterBackgroundColor == null ? "" : this.rasterBackgroundColor);
        this.cachedKey = sb.toString();
        this.cachedKeyHash = h;
        return this.cachedKey;
    }

    public boolean isUnderlined() {
        return this.hasDecorationLine("underline");
    }

    public boolean isStrikethrough() {
        return this.hasDecorationLine("line-through");
    }

    private boolean hasDecorationLine(String line) {
        if (this.textDecoration == null || this.textDecoration.isBlank()) {
            return false;
        }
        for (String token : this.textDecoration.trim().toLowerCase(Locale.ROOT).split("\\s+")) {
            if (token.equals("none")) {
                return false;
            }
            if (!token.equals(line)) continue;
            return true;
        }
        return false;
    }

    public boolean isBold() {
        return this.fontWeight >= 600;
    }

    public boolean isOblique() {
        return this.oblique;
    }

    public boolean hasStroke() {
        return this.strokeWidth > 0.0;
    }

    public boolean isRtl() {
        return "rtl".equals(this.direction);
    }

    public double defaultFontScale() {
        Document.FontMode mode = this.fontMode == null ? Document.FontMode.WEB_SCALED : this.fontMode;
        return this.fontSize / mode.defaultFontScaleBase();
    }

    public double renderedFontSize() {
        Document.FontMode mode = this.fontMode == null ? Document.FontMode.WEB_SCALED : this.fontMode;
        return this.fontSize * 9.0 / mode.defaultFontScaleBase();
    }

    private static Document.FontMode getFontMode(Element element) {
        if (element == null || element.document == null) {
            return Document.FontMode.WEB_SCALED;
        }
        return element.document.getFontMode();
    }

    private static String resolveRasterBackgroundColor(Element element) {
        Element current = element;
        while (current != null) {
            String color;
            Style style = current.getComputedStyle();
            String string = color = style == null ? null : style.backgroundColor;
            if (!(color == null || color.isBlank() || "unset".equalsIgnoreCase(color) || "transparent".equalsIgnoreCase(color))) {
                return color;
            }
            current = current.parentElement;
        }
        return "unset";
    }

    private static String getDeclaredFontSize(Element element) {
        Style computed;
        if (element == null) {
            return "unset";
        }
        String declared = element.getInlineStylePropertyValue("font-size");
        if (declared == null || declared.isBlank() || declared.equals("unset")) {
            CSS.Declaration declaredCss = element.cssCache.get("font-size");
            String string = declared = declaredCss == null ? null : declaredCss.value();
            if (declared == null) {
                CSS.Declaration fontSizeCss = element.cssCache.get("fontSize");
                String string2 = declared = fontSizeCss == null ? null : fontSizeCss.value();
            }
        }
        if (declared == null || declared.isBlank() || declared.equals("unset")) {
            return "unset";
        }
        if (declared.contains("var(") && (computed = element.getComputedStyle()) != null && computed.fontSize != null && !computed.fontSize.equals("unset")) {
            return computed.fontSize;
        }
        return declared;
    }

    public static List<String> splitLines(String content) {
        return List.of((content == null ? "" : content).split("\n", -1));
    }

    public static WrappedText wrap(Element element) {
        return Text.wrap(element, Text.of(element));
    }

    public static WrappedText wrap(Element element, Text text) {
        if (element == null || text == null) {
            return Text.wrap(text, 0.0);
        }
        return Text.wrapCachedInternal(element, text, Text.resolveWrapWidth(element, text));
    }

    public static WrappedText wrapCached(Element element, Text text) {
        if (element == null || text == null) {
            return Text.wrap(text, 0.0);
        }
        double wrapWidth = Text.resolveWrapWidth(element, text);
        return Text.wrapCachedInternal(element, text, wrapWidth);
    }

    private static WrappedText wrapCachedInternal(Element element, Text text, double wrapWidth) {
        if (Size.isNaturalMeasurementContext()) {
            return Text.wrap(text, wrapWidth);
        }
        long wrapWidthBits = Double.doubleToLongBits(wrapWidth);
        int metricsHash = Text.wrapMetricsHash(text);
        String content = text.content == null ? "" : text.content;
        int contentHash = content.hashCode();
        int contentLen = content.length();
        WrappedTextCache cache = element.getRenderer().wrappedText.get();
        if (cache != null && cache.wrapWidthBits == wrapWidthBits && cache.metricsHash == metricsHash && cache.contentHash == contentHash && cache.contentLen == contentLen) {
            return cache.wrapped;
        }
        WrappedText wrapped = Text.wrap(text, wrapWidth);
        element.getRenderer().wrappedText.set(new WrappedTextCache(metricsHash, contentHash, contentLen, wrapWidthBits, wrapped));
        return wrapped;
    }

    private static int wrapMetricsHash(Text text) {
        if (text == null) {
            return 0;
        }
        int h = 1;
        long fontRevision = com.sighs.apricityui.resource.Font.getMetricsRevision();
        h = 31 * h + (int)(fontRevision ^ fontRevision >>> 32);
        h = 31 * h + (int)Math.round(text.fontSize * 1000.0);
        h = 31 * h + text.fontWeight;
        h = 31 * h + (text.oblique ? 1 : 0);
        h = 31 * h + (int)Math.round(text.strokeWidth * 1000.0);
        h = 31 * h + (text.fontFamily == null ? 0 : text.fontFamily.hashCode());
        h = 31 * h + (text.whiteSpace == null ? 0 : text.whiteSpace.hashCode());
        h = 31 * h + (text.direction == null ? 0 : text.direction.hashCode());
        h = 31 * h + (int)Math.round(text.textIndent * 1000.0);
        h = 31 * h + (int)Math.round(text.letterSpacing * 1000.0);
        h = 31 * h + (int)Math.round(text.lineHeight * 1000.0);
        h = 31 * h + (text.fontMode == null ? 0 : text.fontMode.hashCode());
        return h;
    }

    public static WrappedText wrap(Text text, double wrapWidth) {
        String content = text == null || text.content == null ? "" : text.content;
        List<String> hardLines = Text.splitLines(content);
        ArrayList<String> lines = new ArrayList<String>();
        ArrayList<Integer> starts = new ArrayList<Integer>();
        double maxWidth = 0.0;
        boolean allowsSoftWrap = Text.allowsSoftWrap(text == null ? null : text.whiteSpace) && wrapWidth > 0.0;
        int cursor = 0;
        for (String hardLine : hardLines) {
            if (!allowsSoftWrap) {
                lines.add(hardLine);
                starts.add(cursor);
                maxWidth = Math.max(maxWidth, Text.measureLine(text, hardLine));
            } else {
                Text.wrapHardLine(text, hardLine, cursor, wrapWidth, lines, starts);
            }
            cursor += hardLine.length() + 1;
        }
        if (lines.isEmpty()) {
            lines.add("");
            starts.add(0);
        }
        for (int i = 0; i < lines.size(); ++i) {
            maxWidth = Math.max(maxWidth, Text.measureLine(text, (String)lines.get(i)));
        }
        if (wrapWidth > 0.0 && Text.allowsSoftWrap(text == null ? null : text.whiteSpace)) {
            maxWidth = Math.min(maxWidth, wrapWidth);
        }
        int[] startArray = new int[starts.size()];
        for (int i = 0; i < starts.size(); ++i) {
            startArray[i] = (Integer)starts.get(i);
        }
        return new WrappedText(lines, startArray, maxWidth);
    }

    private static void wrapHardLine(Text text, String hardLine, int baseIndex, double wrapWidth, List<String> lines, List<Integer> starts) {
        if (hardLine.isEmpty()) {
            lines.add("");
            starts.add(baseIndex);
            return;
        }
        int lineStart = 0;
        HashMap<Integer, Double> codePointWidthCache = new HashMap<Integer, Double>();
        while (lineStart < hardLine.length()) {
            int lineEnd;
            int charCount;
            double width = 0.0;
            int lastBreak = -1;
            boolean firstGlyph = true;
            for (lineEnd = lineStart; lineEnd < hardLine.length(); lineEnd += charCount) {
                int codePoint = hardLine.codePointAt(lineEnd);
                charCount = Character.charCount(codePoint);
                char c = hardLine.charAt(lineEnd);
                double charWidth = codePointWidthCache.computeIfAbsent(codePoint, key -> Text.measureLine(text, new String(Character.toChars(key))));
                if (!firstGlyph && width + charWidth > wrapWidth) break;
                width += charWidth;
                if (Text.isPreferredBreakChar(text == null ? null : text.whiteSpace, c)) {
                    lastBreak = lineEnd;
                }
                firstGlyph = false;
            }
            if (lineEnd >= hardLine.length()) {
                lines.add(hardLine.substring(lineStart));
                starts.add(baseIndex + lineStart);
                return;
            }
            if (lineEnd == lineStart) {
                lineEnd += Character.charCount(hardLine.codePointAt(lineStart));
            }
            if (lastBreak >= lineStart && Text.consumesBreakChar(text == null ? null : text.whiteSpace, hardLine.charAt(lastBreak))) {
                lines.add(hardLine.substring(lineStart, lastBreak));
                starts.add(baseIndex + lineStart);
                lineStart = lastBreak + 1;
                continue;
            }
            int resolvedEnd = lastBreak >= lineStart ? lastBreak + 1 : lineEnd;
            lines.add(hardLine.substring(lineStart, resolvedEnd));
            starts.add(baseIndex + lineStart);
            lineStart = resolvedEnd;
        }
    }

    private static boolean isPreferredBreakChar(String whiteSpace, char c) {
        boolean collapsesSpaces;
        String value = whiteSpace == null ? "normal" : whiteSpace;
        boolean bl = collapsesSpaces = "normal".equals(value) || "pre-line".equals(value);
        boolean whitespaceBreak = collapsesSpaces ? c == ' ' : c == ' ' || c == '\t';
        return whitespaceBreak || Text.isHyphenBreakOpportunity(c);
    }

    private static boolean isHyphenBreakOpportunity(char c) {
        return c == '-' || c == '\u2010';
    }

    private static boolean consumesBreakChar(String whiteSpace, char c) {
        String value;
        String string = value = whiteSpace == null ? "normal" : whiteSpace;
        if ("normal".equals(value) || "pre-line".equals(value)) {
            return c == ' ';
        }
        return false;
    }

    public static boolean allowsSoftWrap(String whiteSpace) {
        String value;
        return switch (value = whiteSpace == null ? "normal" : whiteSpace) {
            case "normal", "pre-wrap", "pre-line", "break-spaces" -> true;
            default -> false;
        };
    }

    private static double resolveWrapWidth(Element element, Text text) {
        double resolved;
        AbstractText input;
        if (element == null || text == null || !Text.allowsSoftWrap(text.whiteSpace)) {
            return 0.0;
        }
        if (element instanceof AbstractText && !(input = (AbstractText)element).isMultiline()) {
            return 0.0;
        }
        Style style = element.getRawComputedStyle();
        Double explicitWidth = Size.parseNumber(style.width);
        if (explicitWidth == null && Size.isNaturalMeasurementContext() && !Size.hasNaturalWidthConstraint(element)) {
            String display;
            String string = display = style.display == null ? "block" : style.display.trim().toLowerCase(Locale.ROOT);
            if (!"inline".equals(display) && !"inline-block".equals(display)) {
                return 0.0;
            }
        }
        Box box = Box.of(element);
        Double naturalContentWidth = Size.getNaturalContentWidthConstraint(element);
        if (naturalContentWidth != null) {
            resolved = naturalContentWidth;
        } else if (explicitWidth != null) {
            resolved = Size.resolveLength(style.width, Size.getScaleWidth(element), explicitWidth);
            if ("border-box".equals(Box.normalizeBoxSizing(style.boxSizing))) {
                resolved -= box.getBorderHorizontal() + box.getPaddingHorizontal();
            }
        } else {
            String display;
            String string = display = style.display == null ? "block" : style.display.trim().toLowerCase(Locale.ROOT);
            if ("inline".equals(display) || "inline-block".equals(display)) {
                return 0.0;
            }
            resolved = Size.getScaleWidth(element) - box.getBorderHorizontal() - box.getPaddingHorizontal();
        }
        if (Math.abs(text.textIndent) > 1.0E-4) {
            resolved -= Math.abs(text.textIndent);
        }
        return Math.max(0.0, resolved);
    }

    private static double parseLetterSpacing(String raw) {
        if (raw == null || raw.isBlank()) {
            return 0.0;
        }
        String value = raw.trim().toLowerCase(Locale.ROOT);
        if (value.equals("normal") || value.equals("unset")) {
            return 0.0;
        }
        Double parsed = Size.tryResolveLength(raw, 16.0, Size.getRootFontSize());
        return parsed == null ? 0.0 : parsed;
    }

    public static String normalizeWhiteSpaceContent(String content, String whiteSpace) {
        String value;
        if (content == null || content.isEmpty()) {
            return "";
        }
        return switch (value = whiteSpace == null ? "normal" : whiteSpace) {
            case "pre", "pre-wrap", "break-spaces" -> content.replace("\r\n", "\n").replace('\r', '\n');
            case "pre-line" -> Text.collapseSpacesPreserveNewlines(content);
            case "nowrap", "normal" -> Text.collapseToSingleLine(content);
            default -> Text.collapseToSingleLine(content);
        };
    }

    private static String collapseToSingleLine(String content) {
        StringBuilder sb = new StringBuilder(content.length());
        boolean pendingSpace = false;
        boolean emitted = false;
        for (int i = 0; i < content.length(); ++i) {
            char c = content.charAt(i);
            if (c == '\r') {
                if (i + 1 < content.length() && content.charAt(i + 1) == '\n') {
                    ++i;
                }
                pendingSpace = true;
                continue;
            }
            if (c == '\n' || Text.isCollapsibleSpace(c)) {
                pendingSpace = true;
                continue;
            }
            if (pendingSpace && emitted) {
                sb.append(' ');
            }
            sb.append(c);
            pendingSpace = false;
            emitted = true;
        }
        return sb.toString();
    }

    public static int resolveLineClamp(Element element) {
        if (element == null) {
            return 0;
        }
        String raw = element.getComputedStyle().lineClamp;
        if (raw == null) {
            return 0;
        }
        String value = raw.trim().toLowerCase(Locale.ROOT);
        if (value.isEmpty() || "none".equals(value) || "unset".equals(value)) {
            return 0;
        }
        int separator = value.indexOf(32);
        if (separator >= 0) {
            value = value.substring(0, separator);
        }
        try {
            return Math.max(0, Integer.parseInt(value));
        }
        catch (NumberFormatException ignored) {
            return 0;
        }
    }

    private static String collapseSpacesPreserveNewlines(String content) {
        StringBuilder sb = new StringBuilder(content.length());
        StringBuilder line = new StringBuilder();
        for (int i = 0; i <= content.length(); ++i) {
            char c;
            boolean end = i >= content.length();
            char c2 = c = end ? (char)'\n' : (char)content.charAt(i);
            if (c == '\r') {
                if (i + 1 < content.length() && content.charAt(i + 1) == '\n') {
                    ++i;
                }
                Text.appendCollapsedLine(sb, line);
                line.setLength(0);
                if (end) continue;
                sb.append('\n');
                continue;
            }
            if (c == '\n') {
                Text.appendCollapsedLine(sb, line);
                line.setLength(0);
                if (end) continue;
                sb.append('\n');
                continue;
            }
            line.append(c);
        }
        return sb.toString();
    }

    private static void appendCollapsedLine(StringBuilder target, CharSequence line) {
        boolean pendingSpace = false;
        boolean emitted = false;
        for (int i = 0; i < line.length(); ++i) {
            char c = line.charAt(i);
            if (Text.isCollapsibleSpace(c)) {
                pendingSpace = true;
                continue;
            }
            if (pendingSpace && emitted) {
                target.append(' ');
            }
            target.append(c);
            pendingSpace = false;
            emitted = true;
        }
    }

    private static boolean isCollapsibleSpace(char c) {
        return c == ' ' || c == '\t' || c == '\u000b' || c == '\f';
    }

    private static final class ResolveState {
        boolean fontStyle;
        boolean lineHeight;
        String lineHeightRaw;
        boolean textStroke;
        boolean textDecoration;
        boolean direction;
        boolean textAlign;
        boolean verticalAlign;
        boolean whiteSpace;
        boolean textIndent;
        boolean letterSpacing;

        private ResolveState() {
        }
    }

    public record WrappedText(List<String> lines, int[] starts, double width) {
        public double height(double lineHeight) {
            return Math.max(lineHeight, (double)this.lines.size() * lineHeight);
        }
    }

    private record LineMeasureKey(long fontRevision, double fontSize, int fontWeight, boolean oblique, double strokeWidth, double letterSpacing, Document.FontMode fontMode, String fontFamily, String line) {
    }

    public record WrappedTextCache(int metricsHash, int contentHash, int contentLen, long wrapWidthBits, WrappedText wrapped) {
    }
}

