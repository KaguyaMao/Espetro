/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.layout;

import com.sighs.apricityui.ApricityUI;
import com.sighs.apricityui.dom.TextNode;
import com.sighs.apricityui.element.AbstractText;
import com.sighs.apricityui.element.Select;
import com.sighs.apricityui.init.Document;
import com.sighs.apricityui.init.Element;
import com.sighs.apricityui.init.Node;
import com.sighs.apricityui.layout.Box;
import com.sighs.apricityui.layout.Flex;
import com.sighs.apricityui.layout.Grid;
import com.sighs.apricityui.layout.Layout;
import com.sighs.apricityui.layout.LayoutMeasureCache;
import com.sighs.apricityui.layout.Position;
import com.sighs.apricityui.parser.CSS;
import com.sighs.apricityui.spi.AuiServices;
import com.sighs.apricityui.style.Style;
import com.sighs.apricityui.style.Text;
import com.sighs.apricityui.viewport.ApricityViewport;
import java.awt.Canvas;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public record Size(double width, double height) {
    public static final double DEFAULT_LINE_HEIGHT = 16.0;
    public static final Size ZERO = new Size(0.0, 0.0);
    private static final ThreadLocal<Set<Element>> RESOLVING = ThreadLocal.withInitial(HashSet::new);
    private static final ThreadLocal<Integer> NATURAL_MEASURE_DEPTH = ThreadLocal.withInitial(() -> 0);
    private static final ThreadLocal<Map<Element, Double>> NATURAL_CONTENT_WIDTHS = ThreadLocal.withInitial(IdentityHashMap::new);
    private static final int NUMBER_CACHE_LIMIT = 4096;
    private static final Map<String, Double> NUMBER_CACHE = Collections.synchronizedMap(new LinkedHashMap<String, Double>(128, 0.75f, true){

        @Override
        protected boolean removeEldestEntry(Map.Entry<String, Double> eldest) {
            return this.size() > 4096;
        }
    });
    private static volatile Size viewportOverride;
    private static volatile Double rootFontOverride;
    private static final Canvas METRICS_CANVAS;

    public Size add(Size size) {
        return new Size(this.width + size.width, this.height + size.height);
    }

    public static Size getWindowSize() {
        Size override = viewportOverride;
        if (override != null) {
            return override;
        }
        Document context = Document.getContextDocument();
        if (context != null && context.isActive()) {
            ApricityViewport viewport = context.getViewport();
            return new Size(viewport.layoutWidth(), viewport.layoutHeight());
        }
        String widthOverride = System.getProperty("aui.test.viewport.width");
        String heightOverride = System.getProperty("aui.test.viewport.height");
        if (widthOverride != null || heightOverride != null) {
            Double parsedWidth = Size.parseNumber(widthOverride);
            Double parsedHeight = Size.parseNumber(heightOverride);
            double width = parsedWidth == null ? 1920.0 : parsedWidth;
            double height = parsedHeight == null ? 1080.0 : parsedHeight;
            return new Size(width, height);
        }
        try {
            return AuiServices.client().getWindowSize();
        }
        catch (Exception | NoClassDefFoundError ignored) {
            return new Size(1920.0, 1080.0);
        }
    }

    public static Size getHeadlessWindowSize() {
        Size override = viewportOverride;
        if (override != null) {
            return override;
        }
        String widthOverride = System.getProperty("aui.test.viewport.width");
        String heightOverride = System.getProperty("aui.test.viewport.height");
        if (widthOverride != null || heightOverride != null) {
            Double parsedWidth = Size.parseNumber(widthOverride);
            Double parsedHeight = Size.parseNumber(heightOverride);
            return new Size(parsedWidth == null ? 1920.0 : parsedWidth, parsedHeight == null ? 1080.0 : parsedHeight);
        }
        return new Size(1920.0, 1080.0);
    }

    public static double getWindowWidth() {
        Size override = viewportOverride;
        if (override != null) {
            return override.width;
        }
        Document context = Document.getContextDocument();
        if (context != null && context.isActive()) {
            return context.getViewport().layoutWidth();
        }
        String widthOverride = System.getProperty("aui.test.viewport.width");
        if (widthOverride != null) {
            Double parsedWidth = Size.parseNumber(widthOverride);
            return parsedWidth == null ? 1920.0 : parsedWidth;
        }
        try {
            return AuiServices.client().getWindowSize().width();
        }
        catch (Exception | NoClassDefFoundError ignored) {
            return 1920.0;
        }
    }

    public static double getWindowHeight() {
        Size override = viewportOverride;
        if (override != null) {
            return override.height;
        }
        Document context = Document.getContextDocument();
        if (context != null && context.isActive()) {
            return context.getViewport().layoutHeight();
        }
        String heightOverride = System.getProperty("aui.test.viewport.height");
        if (heightOverride != null) {
            Double parsedHeight = Size.parseNumber(heightOverride);
            return parsedHeight == null ? 1080.0 : parsedHeight;
        }
        try {
            return AuiServices.client().getWindowSize().height();
        }
        catch (Exception | NoClassDefFoundError ignored) {
            return 1080.0;
        }
    }

    public static void setViewportOverride(double width, double height) {
        viewportOverride = new Size(Math.max(0.0, width), Math.max(0.0, height));
    }

    public static void clearViewportOverride() {
        viewportOverride = null;
    }

    public static void setRootFontOverride(Double rootFontSize) {
        if (rootFontSize == null || rootFontSize <= 0.0) {
            rootFontOverride = null;
            return;
        }
        rootFontOverride = rootFontSize;
    }

    public static void clearRootFontOverride() {
        rootFontOverride = null;
    }

    public static int parse(String str) {
        if (str == null || str.isBlank()) {
            return -1;
        }
        Double number = Size.parseNumber(str);
        if (number == null) {
            return -1;
        }
        return (int)Math.round(number);
    }

    public static Double parseNumber(String str) {
        int i;
        if (str == null) {
            return null;
        }
        Double cached = NUMBER_CACHE.get(str);
        if (cached != null) {
            return cached;
        }
        int len = str.length();
        for (i = 0; i < len && Character.isWhitespace(str.charAt(i)); ++i) {
        }
        if (i >= len) {
            return null;
        }
        int start = i;
        char first = str.charAt(i);
        if (first == '+' || first == '-') {
            ++i;
        }
        boolean hasDigit = false;
        boolean hasDot = false;
        while (i < len) {
            char c = str.charAt(i);
            if (c >= '0' && c <= '9') {
                hasDigit = true;
                ++i;
                continue;
            }
            if (c != '.' || hasDot) break;
            hasDot = true;
            ++i;
        }
        if (!hasDigit) {
            return null;
        }
        try {
            Double parsed = Double.parseDouble(str.substring(start, i));
            NUMBER_CACHE.put(str, parsed);
            return parsed;
        }
        catch (NumberFormatException ignored) {
            return null;
        }
    }

    public static boolean isPercent(String value) {
        if (value == null) {
            return false;
        }
        return value.trim().endsWith("%");
    }

    public static double resolveLength(String value, double percentBasis, double fallback) {
        if (value == null || value.isBlank() || value.equals("unset")) {
            return fallback;
        }
        Double resolved = Size.tryResolveLength(value, percentBasis);
        return resolved == null ? fallback : resolved;
    }

    public static Double tryResolveLength(String value, double percentBasis) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty() || "unset".equalsIgnoreCase(trimmed) || "auto".equalsIgnoreCase(trimmed)) {
            return null;
        }
        if (Size.isMathFunction(trimmed)) {
            return Size.resolveMathFunction(trimmed, percentBasis, Size.getRootFontSize());
        }
        if (trimmed.regionMatches(true, 0, "calc(", 0, 5) && trimmed.endsWith(")")) {
            return Size.resolveCalc(trimmed.substring(5, trimmed.length() - 1), percentBasis, Size.getRootFontSize());
        }
        return Size.resolveSingleLength(trimmed, percentBasis, Size.getRootFontSize());
    }

    public static Double tryResolveLength(String value, double percentBasis, double emBasis) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty() || "unset".equalsIgnoreCase(trimmed) || "auto".equalsIgnoreCase(trimmed)) {
            return null;
        }
        if (Size.isMathFunction(trimmed)) {
            return Size.resolveMathFunction(trimmed, percentBasis, emBasis);
        }
        if (trimmed.regionMatches(true, 0, "calc(", 0, 5) && trimmed.endsWith(")")) {
            return Size.resolveCalc(trimmed.substring(5, trimmed.length() - 1), percentBasis, emBasis);
        }
        return Size.resolveSingleLength(trimmed, percentBasis, emBasis);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Size of(Element element) {
        Size cache = element.getRenderer().size.get();
        if (cache != null) {
            return cache;
        }
        Set<Element> resolving = RESOLVING.get();
        boolean firstVisit = resolving.add(element);
        try {
            Size size = Size.computeSize(element, firstVisit);
            return size;
        }
        finally {
            if (firstVisit) {
                resolving.remove(element);
                if (resolving.isEmpty()) {
                    RESOLVING.remove();
                }
            }
        }
    }

    public static Size natural(Element element) {
        Double contextWidth = Size.getNaturalMeasurementWidthContext(element);
        return Size.measureNatural(element, 1, contextWidth == null ? Double.NaN : contextWidth);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Size naturalAtContentWidth(Element element, double contentWidth) {
        if (element == null) {
            return ZERO;
        }
        double constrainedWidth = Math.max(0.0, contentWidth);
        Map<Element, Double> constraints = NATURAL_CONTENT_WIDTHS.get();
        boolean hadPrevious = constraints.containsKey(element);
        Double previous = constraints.put(element, constrainedWidth);
        try {
            Size size = Size.measureNatural(element, 7, constrainedWidth);
            return size;
        }
        finally {
            if (hadPrevious) {
                constraints.put(element, previous);
            } else {
                constraints.remove(element);
            }
            if (constraints.isEmpty()) {
                NATURAL_CONTENT_WIDTHS.remove();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static Size measureNatural(Element element, int cacheMode, double availableWidth) {
        if (element == null) {
            return ZERO;
        }
        Size cached = LayoutMeasureCache.getSize(cacheMode, element, availableWidth, Double.NaN, true);
        if (cached != null) {
            return cached;
        }
        int depth = NATURAL_MEASURE_DEPTH.get();
        NATURAL_MEASURE_DEPTH.set(depth + 1);
        try {
            Size result = Size.computeSize(element, false);
            LayoutMeasureCache.putSize(cacheMode, element, availableWidth, Double.NaN, true, result);
            Size size = result;
            return size;
        }
        finally {
            int next = NATURAL_MEASURE_DEPTH.get() - 1;
            if (next <= 0) {
                NATURAL_MEASURE_DEPTH.remove();
            } else {
                NATURAL_MEASURE_DEPTH.set(next);
            }
        }
    }

    public static boolean isNaturalMeasurementContext() {
        return NATURAL_MEASURE_DEPTH.get() > 0;
    }

    public static boolean hasNaturalWidthConstraint(Element element) {
        if (element == null) {
            return false;
        }
        Map<Element, Double> constraints = NATURAL_CONTENT_WIDTHS.get();
        Element current = element;
        while (current != null) {
            if (constraints.containsKey(current)) {
                return true;
            }
            current = current.parentElement;
        }
        return false;
    }

    public static Double getNaturalContentWidthConstraint(Element element) {
        if (element == null) {
            return null;
        }
        return NATURAL_CONTENT_WIDTHS.get().get(element);
    }

    static Double getNaturalMeasurementWidthContext(Element element) {
        Map<Element, Double> constraints = NATURAL_CONTENT_WIDTHS.get();
        Element current = element;
        while (current != null) {
            Double width = constraints.get(current);
            if (width != null) {
                return width;
            }
            current = current.parentElement;
        }
        return null;
    }

    public static boolean isResolving(Element element) {
        return element != null && RESOLVING.get().contains(element);
    }

    private static Size computeSize(Element element, boolean allowFlexAdjustments) {
        boolean parentAssignsColumnMainSize;
        Double aspectRatio;
        double resolved;
        Double explicitParentHeight;
        AbstractText textControl;
        Size contentSize;
        boolean isText;
        Size gridUsedSize;
        Size naturalCache;
        Size cache;
        boolean intrinsicMeasurement = Size.isNaturalMeasurementContext();
        Size size = cache = intrinsicMeasurement ? null : element.getRenderer().size.get();
        if (cache != null) {
            return cache;
        }
        if (intrinsicMeasurement && !allowFlexAdjustments && (naturalCache = Size.getNaturalMeasurementCache(element)) != null) {
            return naturalCache;
        }
        Style style = element.getComputedStyle();
        if ("none".equals(style.display)) {
            return ZERO;
        }
        Size size2 = gridUsedSize = intrinsicMeasurement ? null : Grid.resolveAssignedSize(element);
        if (gridUsedSize != null && element.parentElement != null && Layout.isGridDisplay(element.parentElement.getComputedStyle().display) && Layout.isInFlow(style)) {
            element.getRenderer().size.set(gridUsedSize);
            return gridUsedSize;
        }
        boolean bl = isText = element instanceof AbstractText || (!element.innerText.isEmpty() || Size.hasDirectTextNodeChildren(element)) && element.getRenderChildren().isEmpty();
        if (element instanceof com.sighs.apricityui.element.Canvas) {
            com.sighs.apricityui.element.Canvas canvas = (com.sighs.apricityui.element.Canvas)element;
            contentSize = canvas.getIntrinsicSize();
        } else if (element instanceof Select) {
            Select select = (Select)element;
            contentSize = select.getIntrinsicSize();
        } else {
            Size size3 = contentSize = isText ? Size.getTextSize(element) : Size.getContentSize(element);
        }
        if (isText && element instanceof AbstractText && !(textControl = (AbstractText)element).isMultiline() && Size.usesNormalLineHeight(element)) {
            Text text = Text.of(element);
            contentSize = new Size(contentSize.width(), Math.round(Text.calculateLineHeight(text.fontSize, "normal")));
        }
        Box box = Box.of(element);
        double horizontalBox = box.getBorderHorizontal() + box.getPaddingHorizontal();
        double verticalBox = box.getBorderVertical() + box.getPaddingVertical();
        boolean borderBox = box.isBorderBox();
        boolean fixedPositioned = "fixed".equals(style.position);
        boolean absolutePositioned = "absolute".equals(style.position) || fixedPositioned;
        double contentWidth = contentSize.width;
        double contentHeight = contentSize.height;
        Double cachedParentWidth = absolutePositioned ? Size.getContainingBlockPaddingBoxWidth(element) : null;
        Double explicitParentWidth = absolutePositioned ? Size.getExplicitContainingBlockPaddingBoxWidth(element) : null;
        Double cachedParentHeight = absolutePositioned ? Size.getContainingBlockPaddingBoxHeight(element) : Size.getCachedContainingBlockContentHeight(element);
        Double d = explicitParentHeight = absolutePositioned ? Size.getExplicitContainingBlockPaddingBoxHeight(element) : Size.getExplicitContainingBlockHeight(element);
        if (fixedPositioned) {
            explicitParentWidth = cachedParentWidth = Double.valueOf(Math.max(0.0, Size.getWindowWidth()));
            explicitParentHeight = cachedParentHeight = Double.valueOf(Math.max(0.0, Size.getWindowHeight()));
        }
        Double definiteParentWidth = cachedParentWidth != null ? cachedParentWidth : explicitParentWidth;
        double parentWidth = absolutePositioned && definiteParentWidth != null ? definiteParentWidth : Size.getScaleWidth(element);
        Double definiteParentHeight = cachedParentHeight != null ? cachedParentHeight : explicitParentHeight;
        double parentHeight = definiteParentHeight != null ? definiteParentHeight : 0.0;
        boolean unsetWidth = Size.tryResolveLength(style.width, parentWidth) == null;
        boolean unsetHeight = Size.tryResolveLength(style.height, parentHeight) == null;
        boolean flexMainHeightAssigned = false;
        boolean flexCrossHeightStretched = false;
        Double naturalWidthConstraint = NATURAL_CONTENT_WIDTHS.get().get(element);
        boolean hasLeft = Size.isInsetSet(style.left);
        boolean hasRight = Size.isInsetSet(style.right);
        boolean hasTop = Size.isInsetSet(style.top);
        boolean hasBottom = Size.isInsetSet(style.bottom);
        boolean insetResolvedHeight = false;
        if (absolutePositioned && unsetWidth && hasLeft && hasRight) {
            double left = Size.resolveLength(style.left, parentWidth, 0.0);
            double right = Size.resolveLength(style.right, parentWidth, 0.0);
            contentWidth = Math.max(0.0, parentWidth - left - right - horizontalBox);
        }
        if (absolutePositioned && unsetHeight && hasTop && hasBottom && definiteParentHeight != null) {
            double top = Size.resolveLength(style.top, parentHeight, 0.0);
            double bottom = Size.resolveLength(style.bottom, parentHeight, 0.0);
            contentHeight = Math.max(0.0, parentHeight - top - bottom - verticalBox);
            insetResolvedHeight = true;
        }
        if (unsetWidth && Size.shouldFillAvailableBlockWidth(element, style) && !Size.shouldUseContentBasedAutoWidthInNaturalFlexMeasurement(element, allowFlexAdjustments) && !Size.shouldUseContentBasedAutoWidthForWrappedFlex(element)) {
            double availableOuterWidth = Math.max(0.0, parentWidth - box.getMarginHorizontal());
            contentWidth = Math.max(0.0, availableOuterWidth - horizontalBox);
        }
        if (!unsetWidth) {
            resolved = Size.resolveLength(style.width, parentWidth, contentWidth);
            contentWidth = borderBox ? Math.max(0.0, resolved - horizontalBox) : Math.max(0.0, resolved);
        } else if (naturalWidthConstraint != null) {
            contentWidth = Math.max(0.0, naturalWidthConstraint);
        }
        if (!(unsetHeight || Size.isPercent(style.height) && definiteParentHeight == null)) {
            resolved = Size.resolveLength(style.height, parentHeight, contentHeight);
            double d2 = contentHeight = borderBox ? Math.max(0.0, resolved - verticalBox) : Math.max(0.0, resolved);
        }
        if ((aspectRatio = Size.parseAspectRatio(style.aspectRatio)) != null && aspectRatio > 0.0) {
            if ((!unsetWidth || naturalWidthConstraint != null) && unsetHeight) {
                contentHeight = Size.aspectHeightFromWidth(contentWidth, aspectRatio, borderBox, horizontalBox, verticalBox);
            } else if (unsetWidth && !unsetHeight) {
                contentWidth = Size.aspectWidthFromHeight(contentHeight, aspectRatio, borderBox, horizontalBox, verticalBox);
            }
        }
        Flex.ItemUsedSize flexItemSize = Flex.resolveItemUsedSize(element, box, contentWidth, contentHeight, unsetWidth, unsetHeight, horizontalBox, verticalBox, explicitParentHeight, allowFlexAdjustments);
        contentWidth = flexItemSize.contentWidth();
        contentHeight = flexItemSize.contentHeight();
        flexMainHeightAssigned = flexItemSize.mainSizeAssigned();
        flexCrossHeightStretched = flexItemSize.crossSizeStretched();
        boolean bl2 = parentAssignsColumnMainSize = element.parentElement != null && Layout.isInFlow(style) && Layout.isFlexDisplay(element.parentElement.getComputedStyle().display) && Flex.of((Element)element.parentElement).flexDirection.contains("column");
        if (!(!unsetHeight || insetResolvedHeight || flexMainHeightAssigned || flexCrossHeightStretched || parentAssignsColumnMainSize || intrinsicMeasurement && naturalWidthConstraint == null || element instanceof AbstractText || !Layout.isFlexDisplay(style.display))) {
            Flex ownFlex = Flex.of(element);
            if (ownFlex.flexDirection.contains("row") && !ownFlex.flexWrap.is("wrap")) {
                contentHeight = Flex.computeRowCrossSizeAtMainSize(element, contentWidth);
            }
        }
        double constrainedContentWidth = Size.clampContentExtent(contentWidth, horizontalBox, style.minWidth, style.maxWidth, parentWidth, true);
        double constrainedContentHeight = Size.clampContentExtent(contentHeight, verticalBox, style.minHeight, style.maxHeight, parentHeight, definiteParentHeight != null);
        if (aspectRatio != null && aspectRatio > 0.0) {
            if ((!unsetWidth || naturalWidthConstraint != null) && unsetHeight) {
                constrainedContentHeight = Size.aspectHeightFromWidth(constrainedContentWidth, aspectRatio, borderBox, horizontalBox, verticalBox);
                constrainedContentHeight = Size.clampContentExtent(constrainedContentHeight, verticalBox, style.minHeight, style.maxHeight, parentHeight, definiteParentHeight != null);
            } else if (unsetWidth && !unsetHeight) {
                constrainedContentWidth = Size.aspectWidthFromHeight(constrainedContentHeight, aspectRatio, borderBox, horizontalBox, verticalBox);
                constrainedContentWidth = Size.clampContentExtent(constrainedContentWidth, horizontalBox, style.minWidth, style.maxWidth, parentWidth, true);
            }
        }
        contentWidth = constrainedContentWidth;
        contentHeight = constrainedContentHeight;
        double totalWidth = contentWidth + horizontalBox;
        double totalHeight = contentHeight + verticalBox;
        Size resultSize = new Size(totalWidth, totalHeight);
        if (Boolean.getBoolean("apricityui.test.logStyles") && element.parentElement != null && element.parentElement.getClassNames().contains("compact-actions")) {
            ApricityUI.LOGGER.info("[AUI Size] tag={} class={} total={}x{} content={}x{} unsetWidth={} unsetHeight={} flex={} grow={} shrink={} basis={}", new Object[]{element.tagName, element.getClassNames(), totalWidth, totalHeight, contentWidth, contentHeight, unsetWidth, unsetHeight, style.flex, style.flexGrow, style.flexShrink, style.flexBasis});
        }
        if (!intrinsicMeasurement) {
            element.getRenderer().size.set(resultSize);
        }
        return resultSize;
    }

    private static double aspectHeightFromWidth(double contentWidth, double ratio, boolean borderBox, double horizontalBox, double verticalBox) {
        double ratioWidth = borderBox ? contentWidth + horizontalBox : contentWidth;
        double ratioHeight = ratioWidth / ratio;
        return Math.max(0.0, borderBox ? ratioHeight - verticalBox : ratioHeight);
    }

    private static double aspectWidthFromHeight(double contentHeight, double ratio, boolean borderBox, double horizontalBox, double verticalBox) {
        double ratioHeight = borderBox ? contentHeight + verticalBox : contentHeight;
        double ratioWidth = ratioHeight * ratio;
        return Math.max(0.0, borderBox ? ratioWidth - horizontalBox : ratioWidth);
    }

    private static double clampContentExtent(double contentExtent, double boxExtent, String minValue, String maxValue, double percentBasis, boolean allowPercentResolution) {
        Double maxParsed;
        double result = contentExtent;
        Double minParsed = Size.parseNumber(minValue);
        if (minParsed != null && (!Size.isPercent(minValue) || allowPercentResolution)) {
            double minTotal = Size.resolveLength(minValue, percentBasis, minParsed);
            result = Math.max(result, Math.max(0.0, minTotal - boxExtent));
        }
        if ((maxParsed = Size.parseNumber(maxValue)) != null && (!Size.isPercent(maxValue) || allowPercentResolution)) {
            double maxTotal = Size.resolveLength(maxValue, percentBasis, maxParsed);
            result = Math.min(result, Math.max(0.0, maxTotal - boxExtent));
        }
        return Math.max(0.0, result);
    }

    private static boolean isInsetSet(String value) {
        if (value == null || value.isBlank()) {
            return false;
        }
        String normalized = value.trim().toLowerCase(Locale.ROOT);
        return !"unset".equals(normalized) && !"auto".equals(normalized);
    }

    public static Size getTextSize(Element element) {
        Text text = Text.of(element);
        return Text.measureSize(element, text);
    }

    private static boolean hasDirectTextNodeChildren(Element element) {
        if (element == null) {
            return false;
        }
        for (Node child : element.getRenderChildNodes()) {
            TextNode textNode;
            if (!(child instanceof TextNode) || (textNode = (TextNode)child).getTextContent().isEmpty()) continue;
            return true;
        }
        return false;
    }

    public static Size getContentSize(Element element) {
        return Layout.computeContentSize(element);
    }

    public static Size box(Element element) {
        return Box.of(element).size();
    }

    public static double getScaleWidth(Element element) {
        double scaleWidth;
        Element[] route;
        if (element == null) {
            return Size.getWindowWidth();
        }
        Map<Element, Double> constraints = NATURAL_CONTENT_WIDTHS.get();
        for (Element current : route = element.getRouteArray()) {
            Double constrainedWidth = constraints.get(current);
            if (constrainedWidth == null) continue;
            return Math.max(0.0, constrainedWidth);
        }
        double nearestScaleWidth = scaleWidth = Size.getWindowWidth();
        for (int index = route.length - 1; index > 0; --index) {
            double innerWidth;
            Element current = route[index];
            Size cachedSize = current.getRenderer().size.get();
            boolean hasUsableSize = false;
            if (cachedSize != null && (innerWidth = Box.of(current).innerSize().width()) > 0.0) {
                scaleWidth = innerWidth;
                hasUsableSize = true;
            }
            if (!hasUsableSize) {
                Style currentStyle = current.getRawComputedStyle();
                Double resolved = Size.tryResolveLength(currentStyle.width, scaleWidth);
                if (resolved != null) {
                    double resolvedWidth = resolved;
                    if ("border-box".equals(Box.normalizeBoxSizing(currentStyle.boxSizing))) {
                        Box currentBox = Box.of(current);
                        resolvedWidth -= currentBox.getBorderHorizontal() + currentBox.getPaddingHorizontal();
                    }
                    scaleWidth = Math.max(0.0, resolvedWidth);
                    hasUsableSize = true;
                }
            }
            if (!hasUsableSize) continue;
            nearestScaleWidth = scaleWidth;
        }
        return nearestScaleWidth;
    }

    private static Size getNaturalMeasurementCache(Element element) {
        Map<Element, Double> constraints = NATURAL_CONTENT_WIDTHS.get();
        Double availableWidth = Size.getNaturalMeasurementWidthContext(element);
        int cacheMode = constraints.containsKey(element) ? 7 : 1;
        return LayoutMeasureCache.getSize(cacheMode, element, availableWidth == null ? Double.NaN : availableWidth, Double.NaN, true);
    }

    public static double getScaleHeight(Element element) {
        Element parent = element.parentElement;
        if (parent != null) {
            double innerHeight;
            Size cachedParentSize = parent.getRenderer().size.get();
            if (cachedParentSize != null && (innerHeight = Box.of(parent).innerSize().height()) > 0.0) {
                return innerHeight;
            }
            Style parentStyle = parent.getRawComputedStyle();
            if (Size.tryResolveLength(parentStyle.height, Size.getScaleHeight(parent)) != null) {
                double resolvedHeight = Size.resolveLength(parentStyle.height, Size.getScaleHeight(parent), 0.0);
                if ("border-box".equals(Box.normalizeBoxSizing(parentStyle.boxSizing))) {
                    Box parentBox = Box.of(parent);
                    resolvedHeight -= parentBox.getBorderVertical() + parentBox.getPaddingVertical();
                }
                return Math.max(0.0, resolvedHeight);
            }
            return Size.getScaleHeight(parent);
        }
        return Size.getWindowHeight();
    }

    public static Double getExplicitContainingBlockHeight(Element element) {
        Element parent = element.parentElement;
        if (parent == null) {
            return Math.max(0.0, Size.getWindowHeight());
        }
        Double parentOwnHeight = Size.resolveOwnExplicitContentHeight(parent);
        if (parentOwnHeight == null) {
            return null;
        }
        return Math.max(0.0, parentOwnHeight);
    }

    private static boolean usesNormalLineHeight(Element element) {
        if (element == null) {
            return true;
        }
        for (Element current : element.getRouteArray()) {
            String lineHeight = current.getComputedStyle().lineHeight;
            if (lineHeight == null || lineHeight.isBlank() || "unset".equalsIgnoreCase(lineHeight)) continue;
            return "normal".equalsIgnoreCase(lineHeight);
        }
        return true;
    }

    private static Double containingBlockPaddingBoxExtent(Element element, boolean horizontal, boolean explicit) {
        Element cb = Position.findContainingBlock(element);
        if (cb == null) {
            Size viewport = Position.viewportContainingBlockSize(element);
            return Math.max(0.0, horizontal ? viewport.width() : viewport.height());
        }
        Box cbBox = Box.of(cb);
        if (explicit) {
            Double content;
            Double d = content = horizontal ? Size.resolveOwnExplicitContentWidth(cb) : Size.resolveOwnExplicitContentHeight(cb);
            if (content == null) {
                return null;
            }
            return Math.max(0.0, content + (horizontal ? cbBox.getPaddingHorizontal() : cbBox.getPaddingVertical()));
        }
        Size cbSize = cb.getRenderer().size.get();
        if (cbSize == null) {
            if (Size.isResolving(cb)) {
                return null;
            }
            cbSize = Size.of(cb);
        }
        return Math.max(0.0, horizontal ? cbSize.width() - cbBox.getBorderHorizontal() : cbSize.height() - cbBox.getBorderVertical());
    }

    public static Double getContainingBlockPaddingBoxHeight(Element element) {
        return Size.containingBlockPaddingBoxExtent(element, false, false);
    }

    private static Double getCachedContainingBlockContentHeight(Element element) {
        Element parent;
        Element element2 = parent = element == null ? null : element.parentElement;
        if (parent == null) {
            return Math.max(0.0, Size.getWindowHeight());
        }
        Size parentSize = parent.getRenderer().size.get();
        if (parentSize == null) {
            return null;
        }
        Box parentBox = Box.of(parent);
        return Math.max(0.0, parentSize.height() - parentBox.getBorderVertical() - parentBox.getPaddingVertical());
    }

    public static Double getContainingBlockPaddingBoxWidth(Element element) {
        return Size.containingBlockPaddingBoxExtent(element, true, false);
    }

    private static Double getExplicitContainingBlockPaddingBoxWidth(Element element) {
        return Size.containingBlockPaddingBoxExtent(element, true, true);
    }

    private static Double getExplicitContainingBlockPaddingBoxHeight(Element element) {
        return Size.containingBlockPaddingBoxExtent(element, false, true);
    }

    private static Double resolveOwnExplicitContentHeight(Element element) {
        Double widthBasis;
        if (element == null) {
            return null;
        }
        Style style = element.getRawComputedStyle();
        Double containingBlockHeight = element.parentElement == null ? Double.valueOf(Math.max(0.0, Size.getWindowHeight())) : Size.getExplicitContainingBlockHeight(element);
        Double resolvedHeight = Size.tryResolveLength(style.height, containingBlockHeight == null ? 0.0 : containingBlockHeight);
        if (!(resolvedHeight == null || Size.isPercent(style.height) && containingBlockHeight == null)) {
            double contentHeight = resolvedHeight;
            Box box = Box.of(element);
            if ("border-box".equals(Box.normalizeBoxSizing(style.boxSizing))) {
                contentHeight -= box.getBorderVertical() + box.getPaddingVertical();
            }
            double parentHeight = containingBlockHeight == null ? 0.0 : containingBlockHeight;
            return Size.clampContentExtent(contentHeight, box.getBorderVertical() + box.getPaddingVertical(), style.minHeight, style.maxHeight, parentHeight, containingBlockHeight != null);
        }
        Double aspectRatio = Size.parseAspectRatio(style.aspectRatio);
        if (aspectRatio != null && aspectRatio > 0.0 && (widthBasis = Size.resolveOwnExplicitContentWidth(element)) != null) {
            return Math.max(0.0, widthBasis / aspectRatio);
        }
        return null;
    }

    private static Double resolveOwnExplicitContentWidth(Element element) {
        if (element == null) {
            return null;
        }
        Style style = element.getRawComputedStyle();
        Double containingBlockWidth = element.parentElement == null ? Size.getWindowWidth() : Size.getScaleWidth(element);
        Double resolvedWidth = Size.tryResolveLength(style.width, containingBlockWidth == null ? 0.0 : containingBlockWidth);
        if (!(resolvedWidth == null || Size.isPercent(style.width) && containingBlockWidth == null)) {
            Box box = Box.of(element);
            double horizontalBox = box.getBorderHorizontal() + box.getPaddingHorizontal();
            double parentWidth = element.parentElement == null ? Size.getWindowWidth() : Size.getScaleWidth(element);
            double contentWidth = box.isBorderBox() ? Math.max(0.0, resolvedWidth - horizontalBox) : resolvedWidth;
            return Size.clampContentExtent(contentWidth, horizontalBox, style.minWidth, style.maxWidth, parentWidth, true);
        }
        return null;
    }

    private static boolean shouldFillAvailableBlockWidth(Element element, Style style) {
        String display;
        Style parentStyle;
        String position;
        if (element == null || style == null) {
            return false;
        }
        if (element.parentElement == null) {
            return true;
        }
        if (!Layout.isInFlow(style)) {
            return false;
        }
        String string = position = style.position == null ? "static" : style.position.trim().toLowerCase(Locale.ROOT);
        if ("absolute".equals(position) || "fixed".equals(position)) {
            return false;
        }
        Element parent = element.parentElement;
        if (parent != null && Size.isAutoWidthPositionedContainer(parent, parentStyle = parent.getRawComputedStyle())) {
            return parent.getRenderer().size.get() != null && !Size.isResolving(parent);
        }
        String string2 = display = style.display == null ? "" : style.display.trim().toLowerCase(Locale.ROOT);
        if ("inline".equals(display) || "inline-block".equals(display) || "inline-flex".equals(display) || "inline-grid".equals(display)) {
            return false;
        }
        return !Layout.isFlexDisplay(element.parentElement.getComputedStyle().display);
    }

    private static boolean isAutoWidthPositionedContainer(Element element, Style style) {
        String position;
        if (element == null || style == null) {
            return false;
        }
        String string = position = style.position == null ? "static" : style.position.trim().toLowerCase(Locale.ROOT);
        if (!"absolute".equals(position) && !"fixed".equals(position)) {
            return false;
        }
        return Size.tryResolveLength(style.width, Size.getScaleWidth(element)) == null;
    }

    public static boolean hasDefiniteAutoResolvedWidth(Element element) {
        return Size.hasDefiniteAutoResolvedWidthInternal(element);
    }

    private static boolean shouldUseContentBasedAutoWidthInNaturalFlexMeasurement(Element element, boolean allowFlexAdjustments) {
        if (element == null || allowFlexAdjustments || !Size.isNaturalMeasurementContext()) {
            return false;
        }
        Element current = element;
        while (current != null) {
            Element parent = current.parentElement;
            if (parent == null) {
                return false;
            }
            if (Layout.isFlexDisplay(parent.getComputedStyle().display) && Flex.of((Element)parent).flexDirection.contains("row")) {
                return true;
            }
            current = parent;
        }
        return false;
    }

    private static boolean shouldUseContentBasedAutoWidthForWrappedFlex(Element element) {
        if (element == null) {
            return false;
        }
        Style style = element.getComputedStyle();
        return "inline-flex".equalsIgnoreCase(style.display) && Flex.of((Element)element).flexDirection.contains("row") && Flex.flexWraps(Flex.of(element)) && Size.parseNumber(style.width) == null;
    }

    private static boolean hasDefiniteAutoResolvedWidthInternal(Element element) {
        if (element == null) {
            return false;
        }
        if (Size.resolveOwnExplicitContentWidth(element) != null) {
            return true;
        }
        if (element.parentElement == null) {
            return true;
        }
        Element parent = element.parentElement;
        if (!Layout.isFlexDisplay(parent.getComputedStyle().display) && Size.shouldFillAvailableBlockWidth(element, element.getComputedStyle())) {
            return true;
        }
        if (Layout.isFlexDisplay(parent.getComputedStyle().display)) {
            Flex parentFlex = Flex.of(parent);
            if (parentFlex.flexDirection.contains("column") && Flex.shouldStretchCrossAxis(element, parent)) {
                return true;
            }
        }
        return false;
    }

    public static double lerp(double current, double target) {
        return current + (target - current) * 0.2;
    }

    private static Double resolveCalc(String expression, double percentBasis) {
        return Size.resolveCalc(expression, percentBasis, Size.getRootFontSize());
    }

    private static Double resolveCalc(String expression, double percentBasis, double emBasis) {
        String expr;
        String string = expr = expression == null ? "" : expression.trim();
        if (expr.isEmpty()) {
            return null;
        }
        double result = 0.0;
        int sign = 1;
        int start = 0;
        for (int i = 0; i <= expr.length(); ++i) {
            char c;
            boolean boundary;
            boolean bl = boundary = i == expr.length();
            if (!(boundary || (c = expr.charAt(i)) != '+' && c != '-' || i <= start)) {
                boundary = true;
            }
            if (!boundary) continue;
            String term = expr.substring(start, i).trim();
            if (!term.isEmpty()) {
                if (term.charAt(0) == '+') {
                    term = term.substring(1).trim();
                } else if (term.charAt(0) == '-') {
                    sign *= -1;
                    term = term.substring(1).trim();
                }
                Double resolved = Size.resolveSingleLength(term, percentBasis, emBasis);
                if (resolved == null) {
                    return null;
                }
                result += (double)sign * resolved;
            }
            if (i < expr.length()) {
                sign = expr.charAt(i) == '-' ? -1 : 1;
            }
            start = i + 1;
        }
        return result;
    }

    private static Double resolveSingleLength(String token, double percentBasis) {
        return Size.resolveSingleLength(token, percentBasis, Size.getRootFontSize());
    }

    private static Double resolveSingleLength(String token, double percentBasis, double emBasis) {
        if (token == null) {
            return null;
        }
        String value = token.trim().toLowerCase(Locale.ROOT);
        if (value.isEmpty()) {
            return null;
        }
        Double number = Size.parseNumber(value);
        if (number == null) {
            return null;
        }
        if (value.endsWith("%")) {
            return percentBasis * (number / 100.0);
        }
        if (value.endsWith("rem")) {
            return number * Size.getRootFontSize();
        }
        if (value.endsWith("em")) {
            return number * emBasis;
        }
        if (value.endsWith("vw")) {
            return Size.getWindowWidth() * (number / 100.0);
        }
        if (value.endsWith("vh")) {
            return Size.getWindowHeight() * (number / 100.0);
        }
        return number;
    }

    public static double getRootFontSize() {
        return Size.getRootFontSize(Document.getContextDocument());
    }

    private static boolean isMathFunction(String value) {
        return (value.regionMatches(true, 0, "min(", 0, 4) || value.regionMatches(true, 0, "max(", 0, 4) || value.regionMatches(true, 0, "clamp(", 0, 6)) && value.endsWith(")");
    }

    private static Double resolveMathFunction(String value, double percentBasis, double emBasis) {
        int opening = value.indexOf(40);
        if (opening < 0 || value.length() <= opening + 1) {
            return null;
        }
        String name = value.substring(0, opening).trim().toLowerCase(Locale.ROOT);
        String[] arguments = Size.splitFunctionArguments(value.substring(opening + 1, value.length() - 1));
        if (arguments == null || arguments.length == 0) {
            return null;
        }
        double[] resolved = new double[arguments.length];
        for (int index = 0; index < arguments.length; ++index) {
            Double length = Size.tryResolveLength(arguments[index], percentBasis, emBasis);
            if (length == null) {
                return null;
            }
            resolved[index] = length;
        }
        return switch (name) {
            case "min" -> {
                double result = resolved[0];
                for (int index = 1; index < resolved.length; ++index) {
                    result = Math.min(result, resolved[index]);
                }
                yield result;
            }
            case "max" -> {
                double result = resolved[0];
                for (int index = 1; index < resolved.length; ++index) {
                    result = Math.max(result, resolved[index]);
                }
                yield result;
            }
            case "clamp" -> {
                if (resolved.length == 3) {
                    yield Math.max(resolved[0], Math.min(resolved[1], resolved[2]));
                }
                yield null;
            }
            default -> null;
        };
    }

    private static String[] splitFunctionArguments(String value) {
        ArrayList<String> arguments = new ArrayList<String>();
        int depth = 0;
        int start = 0;
        for (int index = 0; index < value.length(); ++index) {
            char character = value.charAt(index);
            if (character == '(') {
                ++depth;
                continue;
            }
            if (character == ')') {
                if (depth-- != 0) continue;
                return null;
            }
            if (character != ',' || depth != 0) continue;
            String argument = value.substring(start, index).trim();
            if (argument.isEmpty()) {
                return null;
            }
            arguments.add(argument);
            start = index + 1;
        }
        if (depth != 0) {
            return null;
        }
        String argument = value.substring(start).trim();
        if (argument.isEmpty()) {
            return null;
        }
        arguments.add(argument);
        return (String[])arguments.toArray(String[]::new);
    }

    public static double getRootFontSize(Document preferredDocument) {
        if (preferredDocument != null) {
            Double parsed = Size.resolveDocumentRootFontSize(preferredDocument);
            if (parsed != null && parsed > 0.0) {
                return parsed;
            }
            return preferredDocument.getFontMode().defaultFontSize();
        }
        Double override = rootFontOverride;
        if (override != null && override > 0.0) {
            return override;
        }
        for (Document document : Document.getAll()) {
            Double parsed;
            if (document == null || !document.isActive() || (parsed = Size.resolveDocumentRootFontSize(document)) == null || !(parsed > 0.0)) continue;
            return parsed;
        }
        return 16.0;
    }

    private static Double resolveDocumentRootFontSize(Document document) {
        CSS.Declaration declared;
        if (document == null || document.documentElement == null) {
            return null;
        }
        double defaultFontSize = document.getFontMode().defaultFontSize();
        document.documentElement.getComputedStyle();
        String fontSize = document.documentElement.getInlineStylePropertyValue("font-size");
        if (fontSize == null || fontSize.isBlank() || fontSize.equals("unset")) {
            declared = (CSS.Declaration)document.documentElement.cssCache.get("font-size");
            String string = fontSize = declared == null ? null : declared.value();
        }
        if (fontSize == null || fontSize.equals("unset")) {
            declared = (CSS.Declaration)document.documentElement.cssCache.get("fontSize");
            fontSize = declared == null ? null : declared.value();
        }
        return Size.tryResolveLength(fontSize, defaultFontSize, defaultFontSize);
    }

    static Double parseAspectRatio(String raw) {
        if (raw == null) {
            return null;
        }
        String value = raw.trim();
        if (value.isEmpty() || "auto".equalsIgnoreCase(value) || "none".equalsIgnoreCase(value) || "unset".equalsIgnoreCase(value)) {
            return null;
        }
        int slash = value.indexOf(47);
        if (slash >= 0) {
            Double numerator = Size.parseNumber(value.substring(0, slash).trim());
            Double denominator = Size.parseNumber(value.substring(slash + 1).trim());
            if (numerator == null || denominator == null || denominator == 0.0) {
                return null;
            }
            return numerator / denominator;
        }
        Double direct = Size.parseNumber(value);
        if (direct == null || direct <= 0.0) {
            return null;
        }
        return direct;
    }

    public static double measureText(Element element, String text) {
        if (text == null || text.isEmpty()) {
            return 0.0;
        }
        Text base = Text.of(element);
        Text measuring = new Text();
        measuring.fontSize = base.fontSize;
        measuring.fontWeight = base.fontWeight;
        measuring.oblique = base.oblique;
        measuring.strokeWidth = base.strokeWidth;
        measuring.strokeColor = base.strokeColor;
        measuring.color = base.color;
        measuring.fontFamily = base.fontFamily;
        measuring.lineHeight = base.lineHeight;
        measuring.direction = base.direction;
        measuring.textAlign = base.textAlign;
        measuring.verticalAlign = base.verticalAlign;
        measuring.whiteSpace = base.whiteSpace;
        measuring.fontMode = base.fontMode;
        measuring.textIndent = 0.0;
        measuring.letterSpacing = base.letterSpacing;
        measuring.content = text;
        return Text.measureText(measuring);
    }

    static {
        METRICS_CANVAS = new Canvas();
    }
}

