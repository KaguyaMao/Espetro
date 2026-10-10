/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.layout;

import com.sighs.apricityui.init.Element;
import com.sighs.apricityui.layout.Layout;
import com.sighs.apricityui.layout.Size;
import com.sighs.apricityui.parser.Color;
import com.sighs.apricityui.parser.Gradient;
import com.sighs.apricityui.style.Background;
import com.sighs.apricityui.style.Style;
import com.sighs.apricityui.style.Transition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.IntFunction;

public class Box {
    public static final List<String> SIDE = List.of("top", "bottom", "left", "right");
    public static final String BOX_SIZING_CONTENT_BOX = "content-box";
    public static final String BOX_SIZING_BORDER_BOX = "border-box";
    private SideBorder borderTop = SideBorder.getDefault();
    private SideBorder borderRight = SideBorder.getDefault();
    private SideBorder borderBottom = SideBorder.getDefault();
    private SideBorder borderLeft = SideBorder.getDefault();
    private double marginTop = 0.0;
    private double marginRight = 0.0;
    private double marginBottom = 0.0;
    private double marginLeft = 0.0;
    private boolean autoMarginTop = false;
    private boolean autoMarginRight = false;
    private boolean autoMarginBottom = false;
    private boolean autoMarginLeft = false;
    private double paddingTop = 0.0;
    private double paddingRight = 0.0;
    private double paddingBottom = 0.0;
    private double paddingLeft = 0.0;
    private final String[] borderRadiusH = new String[]{"0", "0", "0", "0"};
    private final String[] borderRadiusV = new String[]{"0", "0", "0", "0"};
    public final List<Shadow> shadows = new ArrayList<Shadow>();
    public Shadow shadow = null;
    public BorderImage borderImage = null;
    public Element element;
    private Size cachedRawElementSize;
    private Size cachedRawInnerSize;
    private Size cachedInnerRawSize;
    private Size cachedInnerSize;
    private Size cachedSizeElementSize;
    private Size cachedSize;
    private double cachedSizeMarginHorizontal = Double.NaN;
    private double cachedSizeMarginVertical = Double.NaN;
    private double cachedRawBorderHorizontal = Double.NaN;
    private double cachedRawBorderVertical = Double.NaN;
    private double cachedRawPaddingHorizontal = Double.NaN;
    private double cachedRawPaddingVertical = Double.NaN;
    private double cachedInnerVerticalGutter = Double.NaN;
    private double cachedInnerHorizontalGutter = Double.NaN;
    private static final int[][] FOUR_SIDE_MAP = new int[][]{{0, 0, 0, 0}, {0, 1, 0, 1}, {0, 1, 2, 1}, {0, 1, 2, 3}};

    public void applyBorder(String side, String value) {
        SideBorder sideBorder = Box.parseSideBorder(value);
        this.setBorder(side, sideBorder);
    }

    public void applyBorderAll(String value) {
        SIDE.forEach(side -> this.applyBorder((String)side, value));
    }

    public void applyMargin(String side, String value) {
        boolean auto = Box.isAuto(value);
        this.setAutoMargin(side, auto);
        this.setMargin(side, auto ? 0.0 : this.resolveBoxLength(value));
    }

    public void applyMarginAll(String value) {
        BoxLength[] values = this.parseFourSideBoxLengths(value);
        this.applyParsedMargin("top", values[0]);
        this.applyParsedMargin("right", values[1]);
        this.applyParsedMargin("bottom", values[2]);
        this.applyParsedMargin("left", values[3]);
    }

    public void applyPadding(String side, String value) {
        this.setPadding(side, this.resolveBoxLength(value));
    }

    public void applyPaddingAll(String value) {
        double[] values = this.parseFourSideLengths(value);
        this.paddingTop = values[0];
        this.paddingRight = values[1];
        this.paddingBottom = values[2];
        this.paddingLeft = values[3];
    }

    private static boolean valid(String s) {
        return !s.equals("unset");
    }

    private static boolean isAuto(String value) {
        return value != null && "auto".equalsIgnoreCase(value.trim());
    }

    private void applyParsedMargin(String side, BoxLength value) {
        if (value == null) {
            value = BoxLength.zero();
        }
        this.setAutoMargin(side, value.auto());
        this.setMargin(side, value.length());
    }

    public static Box of(Element element) {
        String radiusStr;
        Box cache = element.getRenderer().box.get();
        if (cache != null) {
            return cache;
        }
        Box resultBox = new Box();
        resultBox.element = element;
        Style style = element.getComputedStyle();
        if (Box.valid(style.border)) {
            resultBox.applyBorderAll(style.border);
        }
        if (Box.valid(style.borderTop)) {
            resultBox.applyBorder("top", style.borderTop);
        }
        if (Box.valid(style.borderBottom)) {
            resultBox.applyBorder("bottom", style.borderBottom);
        }
        if (Box.valid(style.borderLeft)) {
            resultBox.applyBorder("left", style.borderLeft);
        }
        if (Box.valid(style.borderRight)) {
            resultBox.applyBorder("right", style.borderRight);
        }
        if (Box.valid(style.margin)) {
            resultBox.applyMarginAll(style.margin);
        }
        if (Box.valid(style.marginTop)) {
            resultBox.applyMargin("top", style.marginTop);
        }
        if (Box.valid(style.marginBottom)) {
            resultBox.applyMargin("bottom", style.marginBottom);
        }
        if (Box.valid(style.marginLeft)) {
            resultBox.applyMargin("left", style.marginLeft);
        }
        if (Box.valid(style.marginRight)) {
            resultBox.applyMargin("right", style.marginRight);
        }
        if (Box.valid(style.padding)) {
            resultBox.applyPaddingAll(style.padding);
        }
        if (Box.valid(style.paddingTop)) {
            resultBox.applyPadding("top", style.paddingTop);
        }
        if (Box.valid(style.paddingBottom)) {
            resultBox.applyPadding("bottom", style.paddingBottom);
        }
        if (Box.valid(style.paddingLeft)) {
            resultBox.applyPadding("left", style.paddingLeft);
        }
        if (Box.valid(style.paddingRight)) {
            resultBox.applyPadding("right", style.paddingRight);
        }
        if (!(radiusStr = style.borderRadius).equals("unset")) {
            Box.parseBorderRadius(radiusStr, resultBox.borderRadiusH, resultBox.borderRadiusV);
        }
        resultBox.shadows.clear();
        resultBox.shadows.addAll(Box.parseShadowList(style.boxShadow));
        resultBox.shadow = resultBox.shadows.isEmpty() ? Shadow.getDefault() : resultBox.shadows.get(0);
        resultBox.borderImage = Box.parseBorderImage(style);
        if (resultBox.borderImage != null && Box.isZero(resultBox.borderImage.width)) {
            resultBox.borderImage.width = new int[]{(int)resultBox.getBorderTop(), (int)resultBox.getBorderRight(), (int)resultBox.getBorderBottom(), (int)resultBox.getBorderLeft()};
        }
        element.getRenderer().box.set(resultBox);
        return resultBox;
    }

    private static boolean isZero(int[] arr) {
        if (arr == null) {
            return true;
        }
        for (int i : arr) {
            if (i <= 0) continue;
            return false;
        }
        return true;
    }

    public Size size() {
        Size elementSize = Size.of(this.element);
        double marginHorizontal = this.getMarginHorizontal();
        double marginVertical = this.getMarginVertical();
        if (this.cachedSize != null && this.cachedSizeElementSize == elementSize && Double.compare(this.cachedSizeMarginHorizontal, marginHorizontal) == 0 && Double.compare(this.cachedSizeMarginVertical, marginVertical) == 0) {
            return this.cachedSize;
        }
        double resultWidth = elementSize.width() + marginHorizontal;
        double resultHeight = elementSize.height() + marginVertical;
        this.cachedSizeElementSize = elementSize;
        this.cachedSizeMarginHorizontal = marginHorizontal;
        this.cachedSizeMarginVertical = marginVertical;
        this.cachedSize = new Size(resultWidth, resultHeight);
        return this.cachedSize;
    }

    public Size innerSize() {
        Size raw = this.rawInnerSize();
        double verticalGutter = this.element.getVerticalScrollbarGutter();
        double horizontalGutter = this.element.getHorizontalScrollbarGutter();
        if (this.cachedInnerSize != null && this.cachedInnerRawSize == raw && Double.compare(this.cachedInnerVerticalGutter, verticalGutter) == 0 && Double.compare(this.cachedInnerHorizontalGutter, horizontalGutter) == 0) {
            return this.cachedInnerSize;
        }
        double resultWidth = Math.max(0.0, raw.width() - verticalGutter);
        double resultHeight = Math.max(0.0, raw.height() - horizontalGutter);
        this.cachedInnerRawSize = raw;
        this.cachedInnerVerticalGutter = verticalGutter;
        this.cachedInnerHorizontalGutter = horizontalGutter;
        this.cachedInnerSize = new Size(resultWidth, resultHeight);
        return this.cachedInnerSize;
    }

    public Size rawInnerSize() {
        Size elementSize = Size.of(this.element);
        double borderHorizontal = this.getBorderHorizontal();
        double borderVertical = this.getBorderVertical();
        double paddingHorizontal = this.getPaddingHorizontal();
        double paddingVertical = this.getPaddingVertical();
        if (this.cachedRawInnerSize != null && this.cachedRawElementSize.equals(elementSize) && Double.compare(this.cachedRawBorderHorizontal, borderHorizontal) == 0 && Double.compare(this.cachedRawBorderVertical, borderVertical) == 0 && Double.compare(this.cachedRawPaddingHorizontal, paddingHorizontal) == 0 && Double.compare(this.cachedRawPaddingVertical, paddingVertical) == 0) {
            return this.cachedRawInnerSize;
        }
        double resultWidth = elementSize.width() - borderHorizontal - paddingHorizontal;
        double resultHeight = elementSize.height() - borderVertical - paddingVertical;
        this.cachedRawElementSize = elementSize;
        this.cachedRawBorderHorizontal = borderHorizontal;
        this.cachedRawBorderVertical = borderVertical;
        this.cachedRawPaddingHorizontal = paddingHorizontal;
        this.cachedRawPaddingVertical = paddingVertical;
        this.cachedRawInnerSize = new Size(resultWidth, resultHeight);
        this.cachedInnerRawSize = null;
        this.cachedInnerSize = null;
        return this.cachedRawInnerSize;
    }

    public Size elementSize() {
        return Size.of(this.element);
    }

    public String getBoxSizing() {
        if (this.element == null) {
            return BOX_SIZING_CONTENT_BOX;
        }
        return Box.normalizeBoxSizing(this.element.getComputedStyle().boxSizing);
    }

    public boolean isBorderBox() {
        return BOX_SIZING_BORDER_BOX.equals(this.getBoxSizing());
    }

    public static String normalizeBoxSizing(String raw) {
        if (raw == null) {
            return BOX_SIZING_CONTENT_BOX;
        }
        String value = raw.trim().toLowerCase(Locale.ROOT);
        if (BOX_SIZING_BORDER_BOX.equals(value)) {
            return BOX_SIZING_BORDER_BOX;
        }
        return BOX_SIZING_CONTENT_BOX;
    }

    private double resolveBoxLength(String value) {
        if (this.element == null) {
            return Math.max(0.0, Size.resolveLength(value, 0.0, 0.0));
        }
        double basis = Size.isPercent(value) ? Size.getScaleWidth(this.element) : 0.0;
        return Math.max(0.0, Size.resolveLength(value, basis, 0.0));
    }

    private static <T> T[] expandFourSideShorthand(List<T> values, IntFunction<T[]> allocator, T zero) {
        Object[] out = allocator.apply(4);
        int count = values.size();
        if (count < 1 || count > 4) {
            Arrays.fill(out, zero);
            return out;
        }
        int[] map = FOUR_SIDE_MAP[count - 1];
        for (int i = 0; i < 4; ++i) {
            out[i] = values.get(map[i]);
        }
        return out;
    }

    private <T> T[] parseFourSideShorthand(String raw, Function<String, T> mapper, IntFunction<T[]> allocator, T zero) {
        if (raw == null || raw.isBlank() || "unset".equals(raw)) {
            Object[] out = allocator.apply(4);
            Arrays.fill(out, zero);
            return out;
        }
        String[] parts = raw.trim().split("\\s+");
        ArrayList<T> values = new ArrayList<T>(4);
        for (int i = 0; i < Math.min(parts.length, 4); ++i) {
            values.add(mapper.apply(parts[i]));
        }
        return Box.expandFourSideShorthand(values, allocator, zero);
    }

    private double[] parseFourSideLengths(String raw) {
        Double[] expanded = this.parseFourSideShorthand(raw, this::resolveBoxLength, Double[]::new, 0.0);
        return new double[]{expanded[0], expanded[1], expanded[2], expanded[3]};
    }

    private BoxLength[] parseFourSideBoxLengths(String raw) {
        return this.parseFourSideShorthand(raw, token -> Box.isAuto(token) ? BoxLength.autoValue() : new BoxLength(this.resolveBoxLength((String)token), false), BoxLength[]::new, BoxLength.zero());
    }

    public double offset(String side) {
        return this.getBorder((String)side).size + this.getPadding(side);
    }

    public double getMarginHorizontal() {
        return this.getMarginLeft() + this.getMarginRight();
    }

    public double getMarginVertical() {
        return this.getMarginTop() + this.getMarginBottom();
    }

    public double getMarginLeft() {
        return this.marginLeft;
    }

    public double getMarginTop() {
        return this.marginTop;
    }

    public double getMarginRight() {
        return this.marginRight;
    }

    public double getMarginBottom() {
        return this.marginBottom;
    }

    public boolean isMarginAuto(String side) {
        return switch (Box.normalizeSide(side)) {
            case "top" -> this.autoMarginTop;
            case "right" -> this.autoMarginRight;
            case "bottom" -> this.autoMarginBottom;
            case "left" -> this.autoMarginLeft;
            default -> false;
        };
    }

    public double getBorderHorizontal() {
        return this.getBorderLeft() + this.getBorderRight();
    }

    public double getBorderVertical() {
        return this.getBorderTop() + this.getBorderBottom();
    }

    public double getBorderLeft() {
        return this.borderLeft.size;
    }

    public double getBorderRight() {
        return this.borderRight.size;
    }

    public double getBorderTop() {
        return this.borderTop.size;
    }

    public double getBorderBottom() {
        return this.borderBottom.size;
    }

    public double getPaddingHorizontal() {
        return this.getPaddingLeft() + this.getPaddingRight();
    }

    public double getPaddingVertical() {
        return this.getPaddingTop() + this.getPaddingBottom();
    }

    public double getPaddingLeft() {
        return this.paddingLeft;
    }

    public double getPaddingRight() {
        return this.paddingRight;
    }

    public double getPaddingTop() {
        return this.paddingTop;
    }

    public double getPaddingBottom() {
        return this.paddingBottom;
    }

    public SideBorder getBorderSide(String side) {
        return this.getBorder(side);
    }

    public SideBorder getBorderTopSide() {
        return this.borderTop;
    }

    public SideBorder getBorderRightSide() {
        return this.borderRight;
    }

    public SideBorder getBorderBottomSide() {
        return this.borderBottom;
    }

    public SideBorder getBorderLeftSide() {
        return this.borderLeft;
    }

    private SideBorder getBorder(String side) {
        return switch (Box.normalizeSide(side)) {
            case "top" -> this.borderTop;
            case "right" -> this.borderRight;
            case "bottom" -> this.borderBottom;
            case "left" -> this.borderLeft;
            default -> SideBorder.getDefault();
        };
    }

    private double getMargin(String side) {
        return switch (Box.normalizeSide(side)) {
            case "top" -> this.marginTop;
            case "right" -> this.marginRight;
            case "bottom" -> this.marginBottom;
            case "left" -> this.marginLeft;
            default -> 0.0;
        };
    }

    private double getPadding(String side) {
        return switch (Box.normalizeSide(side)) {
            case "top" -> this.paddingTop;
            case "right" -> this.paddingRight;
            case "bottom" -> this.paddingBottom;
            case "left" -> this.paddingLeft;
            default -> 0.0;
        };
    }

    private void setBorder(String side, SideBorder value) {
        SideBorder border = value == null ? SideBorder.getDefault() : value;
        switch (Box.normalizeSide(side)) {
            case "top": {
                this.borderTop = border;
                break;
            }
            case "right": {
                this.borderRight = border;
                break;
            }
            case "bottom": {
                this.borderBottom = border;
                break;
            }
            case "left": {
                this.borderLeft = border;
            }
        }
    }

    private void setMargin(String side, double value) {
        switch (Box.normalizeSide(side)) {
            case "top": {
                this.marginTop = value;
                break;
            }
            case "right": {
                this.marginRight = value;
                break;
            }
            case "bottom": {
                this.marginBottom = value;
                break;
            }
            case "left": {
                this.marginLeft = value;
            }
        }
        this.cachedSize = null;
        this.cachedSizeElementSize = null;
    }

    private void setAutoMargin(String side, boolean value) {
        switch (Box.normalizeSide(side)) {
            case "top": {
                this.autoMarginTop = value;
                break;
            }
            case "right": {
                this.autoMarginRight = value;
                break;
            }
            case "bottom": {
                this.autoMarginBottom = value;
                break;
            }
            case "left": {
                this.autoMarginLeft = value;
            }
        }
    }

    private void setPadding(String side, double value) {
        switch (Box.normalizeSide(side)) {
            case "top": {
                this.paddingTop = value;
                break;
            }
            case "right": {
                this.paddingRight = value;
                break;
            }
            case "bottom": {
                this.paddingBottom = value;
                break;
            }
            case "left": {
                this.paddingLeft = value;
            }
        }
    }

    private static String normalizeSide(String side) {
        return side == null ? "" : side.trim().toLowerCase(Locale.ROOT);
    }

    public static SideBorder parseSideBorder(String string) {
        String[] res = Box.splitWhitespace(string, 3);
        if (res.length != 3) {
            return SideBorder.getDefault();
        }
        Double width = Size.parseNumber(res[0]);
        if (width == null) {
            return SideBorder.getDefault();
        }
        return new SideBorder(Math.max(0.0, width), res[1], new Color(res[2]));
    }

    public static Shadow parseShadow(String string) {
        List<Shadow> parsed = Box.parseShadowList(string);
        return parsed.isEmpty() ? Shadow.getDefault() : parsed.get(0);
    }

    public static List<Shadow> parseShadowList(String string) {
        ArrayList<Shadow> result = new ArrayList<Shadow>();
        if (string == null || string.isBlank() || "unset".equals(string) || "none".equals(string)) {
            return result;
        }
        for (String shadowToken : Background.splitTopLevelComma(string)) {
            Double parsedSpread;
            Double parsedBlur;
            String[] res = Box.splitWhitespace(shadowToken, 8);
            if (res.length < 2) continue;
            boolean inset = false;
            int valueStart = 0;
            if ("inset".equalsIgnoreCase(res[0])) {
                inset = true;
                valueStart = 1;
            } else if (res.length > 2 && "inset".equalsIgnoreCase(res[res.length - 1])) {
                inset = true;
            }
            if (res.length <= valueStart + 1) continue;
            Double x = Size.parseNumber(res[valueStart]);
            Double y = Size.parseNumber(res[valueStart + 1]);
            if (x == null || y == null) continue;
            double blur = 0.0;
            double spread = 0.0;
            int colorIndex = valueStart + 2;
            if (res.length > colorIndex && (parsedBlur = Size.parseNumber(res[colorIndex])) != null) {
                blur = Math.max(0.0, parsedBlur);
                ++colorIndex;
            }
            if (res.length > colorIndex && (parsedSpread = Size.parseNumber(res[colorIndex])) != null) {
                spread = parsedSpread;
                ++colorIndex;
            }
            String color = res.length > colorIndex ? res[colorIndex] : "#000";
            result.add(new Shadow(x, y, blur, spread, new Color(color), inset));
        }
        return result;
    }

    private static String[] splitWhitespace(String value, int maxTokens) {
        if (maxTokens <= 0) {
            return new String[0];
        }
        List<String> tokens = Layout.splitTopLevelWhitespace(value);
        if (tokens.size() > maxTokens) {
            tokens = tokens.subList(0, maxTokens);
        }
        return (String[])tokens.toArray(String[]::new);
    }

    public static BorderImage parseBorderImage(Style style) {
        String[] sections;
        String[] repeats;
        BorderImage bi = new BorderImage();
        if (Box.valid(style.borderImageSource)) {
            bi.source = Box.extractUrl(style.borderImageSource);
        } else if (style.borderImage.contains("url(")) {
            bi.source = Box.extractUrl(style.borderImage);
        }
        String mainPart = style.borderImage.replaceAll("url\\(.*?\\)", "").trim();
        for (String r : repeats = new String[]{"stretch", "repeat", "round", "space"}) {
            if (!mainPart.contains(r)) continue;
            bi.repeat = r;
            mainPart = mainPart.replace(r, "");
            break;
        }
        if ((sections = (mainPart = mainPart.trim()).split("/")).length > 0 && !sections[0].isBlank()) {
            String sliceStr = sections[0].trim();
            if (sliceStr.contains("fill")) {
                bi.fill = true;
                sliceStr = sliceStr.replace("fill", "").trim();
            }
            if (!sliceStr.isEmpty()) {
                bi.slice = Box.parse4Values(sliceStr);
            }
        }
        if (sections.length > 1 && !sections[1].isBlank()) {
            bi.width = Box.parse4Values(sections[1].trim());
        }
        if (sections.length > 2 && !sections[2].isBlank()) {
            bi.outset = Box.parse4Values(sections[2].trim());
        }
        if (Box.valid(style.borderImageSlice)) {
            bi.slice = Box.parse4Values(style.borderImageSlice);
        }
        if (Box.valid(style.borderImageWidth)) {
            bi.width = Box.parse4Values(style.borderImageWidth);
        }
        if (Box.valid(style.borderImageOutset)) {
            bi.outset = Box.parse4Values(style.borderImageOutset);
        }
        if (Box.valid(style.borderImageRepeat)) {
            bi.repeat = style.borderImageRepeat;
        }
        if (style.borderImage.startsWith("linear-gradient")) {
            bi.gradient = Gradient.parse(style.borderImage);
        }
        return bi.isEmpty() ? null : bi;
    }

    private static String extractUrl(String input) {
        if (input == null || !input.contains("url(")) {
            return null;
        }
        return input.substring(input.indexOf("url(") + 4, input.lastIndexOf(")")).replace("\"", "").replace("'", "");
    }

    private static int[] parse4Values(String input) {
        try {
            List<Integer> vals = new ArrayList();
            for (String p : input.trim().split("\\s+")) {
                if (p.equals("fill") || p.isEmpty()) continue;
                int v = Size.parse(p);
                vals.add(v == -1 ? 0 : v);
            }
            if (vals.size() > 4) {
                vals = vals.subList(0, 4);
            }
            Integer[] expanded = Box.expandFourSideShorthand(vals, Integer[]::new, 0);
            return new int[]{expanded[0], expanded[1], expanded[2], expanded[3]};
        }
        catch (Exception e) {
            return new int[]{0, 0, 0, 0};
        }
    }

    static void parseBorderRadius(String value, String[] hOut, String[] vOut) {
        String horizontal = value;
        String vertical = null;
        int slash = value.indexOf(47);
        if (slash >= 0) {
            horizontal = value.substring(0, slash);
            vertical = value.substring(slash + 1);
        }
        String[] h = Box.expandRadiusTokens(Box.tokenizeRadius(horizontal));
        String[] v = vertical == null ? h : Box.expandRadiusTokens(Box.tokenizeRadius(vertical));
        for (int i = 0; i < 4; ++i) {
            hOut[i] = Box.sanitizeRadiusToken(h[i]);
            vOut[i] = Box.sanitizeRadiusToken(v[i]);
        }
    }

    private static List<String> tokenizeRadius(String s) {
        return Layout.splitTopLevelWhitespace(s);
    }

    private static String[] expandRadiusTokens(List<String> tokens) {
        return Box.expandFourSideShorthand(tokens, String[]::new, "0");
    }

    private static String sanitizeRadiusToken(String token) {
        if (token == null || token.isBlank()) {
            return "0";
        }
        Double resolved = Size.tryResolveLength(token.trim(), 100.0);
        return resolved == null ? "0" : token.trim();
    }

    private static double resolveRadius(String token, double basis) {
        Double resolved = Size.tryResolveLength(token, basis);
        return resolved == null ? 0.0 : Math.max(0.0, resolved);
    }

    public float[] getCalculatedRadii(float w, float h, float offset) {
        return Box.calculateRadii(this.borderRadiusH, this.borderRadiusV, w, h, offset);
    }

    static float[] calculateRadii(String[] hTokens, String[] vTokens, float w, float h, float offset) {
        float[] r = new float[8];
        for (int i = 0; i < 4; ++i) {
            r[i * 2] = (float)Math.max(0.0, Box.resolveRadius(hTokens[i], w) - (double)offset);
            r[i * 2 + 1] = (float)Math.max(0.0, Box.resolveRadius(vTokens[i], h) - (double)offset);
        }
        float tlH = r[0];
        float tlV = r[1];
        float trH = r[2];
        float trV = r[3];
        float brH = r[4];
        float brV = r[5];
        float blH = r[6];
        float blV = r[7];
        float scale = 1.0f;
        if (tlH + trH > 0.0f) {
            scale = Math.min(scale, w / (tlH + trH));
        }
        if (trV + brV > 0.0f) {
            scale = Math.min(scale, h / (trV + brV));
        }
        if (brH + blH > 0.0f) {
            scale = Math.min(scale, w / (brH + blH));
        }
        if (blV + tlV > 0.0f) {
            scale = Math.min(scale, h / (blV + tlV));
        }
        if (scale < 0.0f || Float.isNaN(scale)) {
            scale = 0.0f;
        }
        if (scale != 1.0f) {
            int i = 0;
            while (i < 8) {
                int n = i++;
                r[n] = r[n] * scale;
            }
        }
        return r;
    }

    public static boolean matchStyleName(String name) {
        return Set.of("margin", "padding", "border-width").contains(name);
    }

    public static void createShadowTransition(Style startStyle, Style endStyle, List<Transition> result, double duration, double delay) {
        List<Shadow> start = Box.parseShadowList(startStyle.boxShadow);
        List<Shadow> end = Box.parseShadowList(endStyle.boxShadow);
        int count = Math.max(start.size(), end.size());
        for (int i = 0; i < count; ++i) {
            Shadow startShadow = i < start.size() ? start.get(i) : Box.transparentShadow(end.get(i));
            Shadow endShadow = i < end.size() ? end.get(i) : Box.transparentShadow(start.get(i));
            Box.addShadowTransition(result, i, "x", startShadow.x(), endShadow.x(), duration, delay);
            Box.addShadowTransition(result, i, "y", startShadow.y(), endShadow.y(), duration, delay);
            Box.addShadowTransition(result, i, "blur", startShadow.size(), endShadow.size(), duration, delay);
            Box.addShadowTransition(result, i, "spread", startShadow.spread(), endShadow.spread(), duration, delay);
            Box.addShadowTransition(result, i, "color", startShadow.color().getValue(), endShadow.color().getValue(), duration, delay);
        }
    }

    public static void interpolateShadow(List<Transition.Change> changes, String startValue, String endValue, double progress) {
        List<Shadow> start = Box.parseShadowList(startValue);
        List<Shadow> end = Box.parseShadowList(endValue);
        int count = Math.max(start.size(), end.size());
        for (int i = 0; i < count; ++i) {
            Shadow startShadow = i < start.size() ? start.get(i) : Box.transparentShadow(end.get(i));
            Shadow endShadow = i < end.size() ? end.get(i) : Box.transparentShadow(start.get(i));
            Transition.addChange(changes, Box.shadowTransitionName(i, "x"), Transition.getOffset("x", startShadow.x(), endShadow.x(), progress));
            Transition.addChange(changes, Box.shadowTransitionName(i, "y"), Transition.getOffset("y", startShadow.y(), endShadow.y(), progress));
            Transition.addChange(changes, Box.shadowTransitionName(i, "blur"), Transition.getOffset("blur", startShadow.size(), endShadow.size(), progress));
            Transition.addChange(changes, Box.shadowTransitionName(i, "spread"), Transition.getOffset("spread", startShadow.spread(), endShadow.spread(), progress));
            Transition.addChange(changes, Box.shadowTransitionName(i, "color"), Transition.getOffset("color", startShadow.color().getValue(), endShadow.color().getValue(), progress));
        }
    }

    public static void readShadowTransition(List<Transition.Change> changes, Style style) {
        if (changes == null || changes.isEmpty() || style == null) {
            return;
        }
        HashMap<Integer, ShadowComponents> animated = new HashMap<Integer, ShadowComponents>();
        Iterator<Transition.Change> iterator = changes.iterator();
        while (iterator.hasNext()) {
            Transition.Change change = iterator.next();
            ShadowComponent component = Box.parseShadowComponent(change.name());
            if (component == null) continue;
            animated.computeIfAbsent(component.index(), ignored -> new ShadowComponents()).set(component.name(), change.value());
            iterator.remove();
        }
        if (animated.isEmpty()) {
            return;
        }
        ArrayList<Shadow> target = new ArrayList<Shadow>(Box.parseShadowList(style.boxShadow));
        int count = Math.max(target.size(), animated.keySet().stream().mapToInt(Integer::intValue).max().orElse(-1) + 1);
        while (target.size() < count) {
            target.add(new Shadow(0.0, 0.0, 0.0, 0.0, new Color(0), false));
        }
        for (Map.Entry entry : animated.entrySet()) {
            int index = (Integer)entry.getKey();
            Shadow base = (Shadow)target.get(index);
            target.set(index, ((ShadowComponents)entry.getValue()).apply(base));
        }
        style.boxShadow = Box.serializeShadows(target);
    }

    private static void addShadowTransition(List<Transition> result, int index, String component, double start, double end, double duration, double delay) {
        if (Math.abs(start - end) <= 1.0E-4) {
            return;
        }
        result.add(new Transition(Box.shadowTransitionName(index, component), start, end, duration, delay, System.currentTimeMillis()));
    }

    private static String shadowTransitionName(int index, String component) {
        return "box-shadow-" + index + "-" + component;
    }

    private static Shadow transparentShadow(Shadow reference) {
        int transparentColor = reference == null ? 0 : reference.color().getValue() & 0xFFFFFF;
        return new Shadow(0.0, 0.0, 0.0, 0.0, new Color(transparentColor), reference != null && reference.inset());
    }

    private static ShadowComponent parseShadowComponent(String name) {
        String prefix = "box-shadow-";
        if (name == null || !name.startsWith(prefix)) {
            return null;
        }
        int separator = name.indexOf(45, prefix.length());
        if (separator < 0) {
            return null;
        }
        try {
            int index = Integer.parseInt(name.substring(prefix.length(), separator));
            String component = name.substring(separator + 1);
            if (!Set.of("x", "y", "blur", "spread", "color").contains(component)) {
                return null;
            }
            return new ShadowComponent(index, component);
        }
        catch (NumberFormatException ignored) {
            return null;
        }
    }

    private static String serializeShadows(List<Shadow> shadows) {
        if (shadows == null || shadows.isEmpty()) {
            return "none";
        }
        ArrayList<String> values = new ArrayList<String>(shadows.size());
        for (Shadow shadow : shadows) {
            Color color = shadow.color();
            values.add(String.format(Locale.ROOT, "%s%.3fpx %.3fpx %.3fpx %.3fpx rgba(%d,%d,%d,%.3f)", shadow.inset() ? "inset " : "", shadow.x(), shadow.y(), shadow.size(), shadow.spread(), color.getR(), color.getG(), color.getB(), (double)color.getA() / 255.0));
        }
        return String.join((CharSequence)", ", values);
    }

    public static void createTransition(Style sS, Style eS, List<Transition> res, String name, double dur, double del) {
        String[] sides;
        for (String side : sides = new String[]{"-top", "-right", "-bottom", "-left"}) {
            double e;
            double s;
            String subProp = name + side;
            if (name.equals("border-width")) {
                subProp = "border" + side + "-width";
            }
            if (!(Math.abs((s = Transition.parseStyle(subProp, sS.get(subProp))) - (e = Transition.parseStyle(subProp, eS.get(subProp)))) > 1.0E-4)) continue;
            res.add(new Transition(subProp, s, e, dur, del, System.currentTimeMillis()));
        }
    }

    public record SideBorder(double size, String type, Color color) {
        private static final SideBorder DEFAULT = new SideBorder(0.0, "solid", Color.BLACK);

        public static SideBorder getDefault() {
            return DEFAULT;
        }

        @Override
        public String toString() {
            return this.size + "px " + this.type + " " + this.color.toHexString();
        }
    }

    public record Shadow(double x, double y, double size, double spread, Color color, boolean inset) {
        private static final Shadow DEFAULT = new Shadow(0.0, 0.0, 0.0, 0.0, Color.BLACK, false);

        public static Shadow getDefault() {
            return DEFAULT;
        }

        @Override
        public String toString() {
            return this.x + "px " + this.y + "px " + this.size + "px " + this.color.toHexString();
        }
    }

    public static class BorderImage {
        public String source = null;
        public int[] slice = new int[]{0, 0, 0, 0};
        public int[] width = new int[]{0, 0, 0, 0};
        public int[] outset = new int[]{0, 0, 0, 0};
        public String repeat = "stretch";
        public boolean fill = false;
        public Gradient gradient = null;

        public boolean isEmpty() {
            return this.source == null || this.source.equals("none");
        }
    }

    private record BoxLength(double length, boolean auto) {
        private static BoxLength zero() {
            return new BoxLength(0.0, false);
        }

        private static BoxLength autoValue() {
            return new BoxLength(0.0, true);
        }
    }

    private record ShadowComponent(int index, String name) {
    }

    private static final class ShadowComponents {
        private Double x;
        private Double y;
        private Double blur;
        private Double spread;
        private Double color;

        private ShadowComponents() {
        }

        private void set(String name, double value) {
            switch (name) {
                case "x": {
                    this.x = value;
                    break;
                }
                case "y": {
                    this.y = value;
                    break;
                }
                case "blur": {
                    this.blur = value;
                    break;
                }
                case "spread": {
                    this.spread = value;
                    break;
                }
                case "color": {
                    this.color = value;
                }
            }
        }

        private Shadow apply(Shadow base) {
            return new Shadow(this.x == null ? base.x() : this.x.doubleValue(), this.y == null ? base.y() : this.y.doubleValue(), Math.max(0.0, this.blur == null ? base.size() : this.blur.doubleValue()), this.spread == null ? base.spread() : this.spread.doubleValue(), this.color == null ? base.color() : new Color(this.color.intValue()), base.inset());
        }
    }
}

