/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.layout;

import com.sighs.apricityui.ApricityUI;
import com.sighs.apricityui.dom.TextNode;
import com.sighs.apricityui.init.Element;
import com.sighs.apricityui.init.Node;
import com.sighs.apricityui.layout.Align;
import com.sighs.apricityui.layout.Box;
import com.sighs.apricityui.layout.Layout;
import com.sighs.apricityui.layout.LayoutMeasureCache;
import com.sighs.apricityui.layout.Position;
import com.sighs.apricityui.layout.Size;
import com.sighs.apricityui.parser.Color;
import com.sighs.apricityui.style.Style;
import com.sighs.apricityui.style.Text;
import com.sighs.apricityui.util.TextMetrics;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;

public class Flex {
    public KeywordValue flexDirection;
    public KeywordValue flexWrap;
    public KeywordValue alignContent;
    public KeywordValue justifyContent;
    public KeywordValue alignItems;

    public Flex(Style style) {
        this.flexDirection = new KeywordValue(style.flexDirection);
        this.flexWrap = new KeywordValue(style.flexWrap);
        this.alignContent = new KeywordValue(style.alignContent);
        this.justifyContent = new KeywordValue(style.justifyContent);
        this.alignItems = new KeywordValue(style.alignItems);
    }

    public static Flex of(Element element) {
        return new Flex(element.getComputedStyle());
    }

    public static boolean flexWraps(Flex flex) {
        return flex != null && (flex.flexWrap.is("wrap") || flex.flexWrap.is("wrap-reverse"));
    }

    public static int resolveOrder(Element child) {
        if (child == null) {
            return 0;
        }
        String raw = child.getComputedStyle().order;
        if (raw == null || raw.isBlank()) {
            return 0;
        }
        try {
            return Integer.parseInt(raw.trim());
        }
        catch (NumberFormatException ignored) {
            return 0;
        }
    }

    private static List<FlexParticipant> sortParticipantsByOrder(List<FlexParticipant> participants) {
        ArrayList<FlexParticipant> sorted = new ArrayList<FlexParticipant>(participants);
        sorted.sort(Comparator.comparingInt(participant -> Flex.resolveOrder(participant.element())));
        return sorted;
    }

    private static List<Element> sortItemsByOrder(List<Element> items) {
        ArrayList<Element> sorted = new ArrayList<Element>(items);
        sorted.sort(Comparator.comparingInt(Flex::resolveOrder));
        return sorted;
    }

    public static Position computeChildPosition(Element element, Element parent, List<Element> siblings) {
        Box parentBox = Box.of(parent);
        Position position = Flex.getOrComputeLayout(parent).positions().get(element);
        if (position == null) {
            position = new Position(parentBox.offset("left"), parentBox.offset("top"));
        }
        if (Boolean.getBoolean("apricityui.test.logStyles") && Flex.shouldLogFlexParent(parent)) {
            ApricityUI.LOGGER.info("[AUI FlexPos] child={} class={} position={} size={}x{} parentClass={}", new Object[]{element.tagName, element.getClassNames(), position, Size.box(element).width(), Size.box(element).height(), parent.getClassNames()});
        }
        return position;
    }

    public static Size computeContentSize(Element element) {
        boolean natural;
        Flex flex = Flex.of(element);
        boolean flexColumn = flex.flexDirection.contains("column");
        boolean wrappedRow = Flex.flexWraps(flex) && !flexColumn;
        double availableWidth = wrappedRow ? Flex.resolveWrappedRowAvailableWidth(element) : Flex.naturalWidthCacheKey(element, natural);
        Size cached = LayoutMeasureCache.getSize(2, element, availableWidth, Double.NaN, natural = Size.isNaturalMeasurementContext());
        if (cached != null) {
            return cached;
        }
        List<Element> flowItems = Flex.getFlowItems(element.getRenderChildren());
        List<FlexParticipant> participants = Flex.buildParticipants(element, flowItems);
        double gap = Flex.resolveMainAxisGap(element);
        if (wrappedRow) {
            Size result = Flex.computeWrappedRowContentSize(element, flowItems, availableWidth);
            LayoutMeasureCache.putSize(2, element, availableWidth, Double.NaN, natural, result);
            return result;
        }
        double totalWidth = 0.0;
        double totalHeight = 0.0;
        boolean baselineRow = !flexColumn && Flex.isBaselineAligned(participants, flex);
        for (FlexParticipant participant : participants) {
            Size size = participant.size();
            if (flexColumn) {
                totalWidth = Math.max(totalWidth, size.width());
                totalHeight += size.height();
                continue;
            }
            if (!baselineRow) {
                totalHeight = Math.max(totalHeight, size.height());
            }
            totalWidth += size.width();
        }
        if (baselineRow) {
            BaselineMetrics metrics = Flex.computeBaselineMetrics(participants);
            totalHeight = Math.max(totalHeight, metrics.ascent + metrics.descent);
        }
        if (participants.size() > 1) {
            if (flexColumn) {
                totalHeight += gap * (double)(participants.size() - 1);
            } else {
                totalWidth += gap * (double)(participants.size() - 1);
            }
        }
        Size result = new Size(totalWidth, totalHeight);
        LayoutMeasureCache.putSize(2, element, availableWidth, Double.NaN, natural, result);
        return result;
    }

    public static List<DirectTextLayout> computeDirectTextLayouts(Element parent) {
        if (parent == null) {
            return List.of();
        }
        if (!Layout.isFlexDisplay(parent.getComputedStyle().display)) {
            return List.of();
        }
        return Flex.getOrComputeLayout(parent).directTextLayouts();
    }

    private static FlexLayoutResult getOrComputeLayout(Element parent) {
        boolean natural;
        if (parent == null) {
            return FlexLayoutResult.EMPTY;
        }
        Flex flex = Flex.of(parent);
        Box parentBox = Box.of(parent);
        boolean wrappedRow = Flex.flexWraps(flex) && flex.flexDirection.contains("row");
        double availableWidth = wrappedRow ? Flex.resolveWrappedRowAvailableWidth(parent) : Flex.naturalWidthCacheKey(parent, natural);
        FlexLayoutResult cached = (FlexLayoutResult)LayoutMeasureCache.getObject(4, parent, availableWidth, Double.NaN, natural = Size.isNaturalMeasurementContext());
        if (cached != null) {
            return cached;
        }
        List<Element> flowItems = Flex.getFlowItems(parent.getRenderChildren());
        List<FlexParticipant> participants = Flex.sortParticipantsByOrder(Flex.buildParticipants(parent, flowItems));
        if (participants.isEmpty()) {
            LayoutMeasureCache.putObject(4, parent, availableWidth, Double.NaN, natural, FlexLayoutResult.EMPTY);
            return FlexLayoutResult.EMPTY;
        }
        FlexLayoutResult result = wrappedRow ? Flex.computeWrappedRowLayout(parent, parentBox, flowItems, availableWidth) : Flex.computeSingleLineLayout(parent, parentBox, flex, flowItems, participants);
        LayoutMeasureCache.putObject(4, parent, availableWidth, Double.NaN, natural, result);
        return result;
    }

    private static FlexLayoutResult computeSingleLineLayout(Element parent, Box parentBox, Flex flex, List<Element> flowItems, List<FlexParticipant> participants) {
        boolean baselineLine;
        Size parentContentSize = parentBox.innerSize();
        double gap = Flex.resolveMainAxisGap(parent);
        boolean columnMainAxis = flex.flexDirection.contains("column");
        boolean mainReversed = flex.flexDirection.contains("reverse");
        boolean crossReversed = flex.flexWrap.contains("reverse");
        double[] itemMainSizes = Flex.computeAssignedMainSizes(parent, flowItems);
        double totalMain = 0.0;
        for (FlexParticipant participant : participants) {
            double mainSize = Flex.participantMainSize(participant, itemMainSizes, columnMainAxis);
            totalMain += mainSize;
        }
        if (participants.size() > 1) {
            totalMain += gap * (double)(participants.size() - 1);
        }
        double availableMain = Flex.resolveAvailableMainSize(parent, parentBox, flex);
        double offsetTotal = availableMain - totalMain;
        double cursorX = parentBox.offset("left");
        double cursorY = parentBox.offset("top");
        double autoMarginShare = Flex.resolveMainAxisAutoMarginShare(participants, columnMainAxis, offsetTotal);
        double justifyOffsetTotal = autoMarginShare > 0.0 ? 0.0 : offsetTotal;
        FlexLayoutOffset flexOffset = Flex.computeJustifyContentOffset(flex.justifyContent, justifyOffsetTotal, participants.size(), 0);
        if (columnMainAxis) {
            cursorY += flexOffset.offsetStart;
        } else {
            cursorX += flexOffset.offsetStart;
        }
        double[] baselineOffsets = null;
        boolean bl = baselineLine = !columnMainAxis && Flex.isBaselineAligned(participants, flex);
        if (baselineLine) {
            baselineOffsets = Flex.computeBaselineOffsets(participants, parentContentSize.height(), crossReversed);
        }
        IdentityHashMap<Element, Position> positions = new IdentityHashMap<Element, Position>();
        ArrayList<DirectTextLayout> layouts = new ArrayList<DirectTextLayout>();
        for (int i = 0; i < participants.size(); ++i) {
            FlexParticipant participant = participants.get(i);
            double mainSize = Flex.participantMainSize(participant, itemMainSizes, columnMainAxis);
            if (columnMainAxis) {
                cursorY += Flex.mainAxisAutoMarginBefore(participant, true, autoMarginShare);
            } else {
                cursorX += Flex.mainAxisAutoMarginBefore(participant, false, autoMarginShare);
            }
            if (participant.element() != null) {
                Element child = participant.element();
                Size childSize = participant.size();
                double childX = cursorX;
                double childY = cursorY;
                double availableCross = columnMainAxis ? parentContentSize.width() : parentContentSize.height();
                double usedCross = columnMainAxis ? childSize.width() : childSize.height();
                double crossOffset = Flex.resolveCrossAxisOffset(child, parent, availableCross, usedCross);
                if (baselineLine && !Flex.hasCrossAxisAutoMargin(child, columnMainAxis)) {
                    crossOffset = baselineOffsets[i];
                } else if (crossReversed) {
                    crossOffset = Math.max(0.0, availableCross - usedCross - crossOffset);
                }
                if (columnMainAxis) {
                    childX += crossOffset;
                } else {
                    childY += crossOffset;
                }
                if (mainReversed) {
                    double mirrored = Flex.mirrorMainPosition(columnMainAxis ? parentBox.offset("top") : parentBox.offset("left"), availableMain, mainSize, columnMainAxis ? childY : childX);
                    if (columnMainAxis) {
                        childY = mirrored;
                    } else {
                        childX = mirrored;
                    }
                }
                positions.put(child, new Position(childX, childY));
            }
            if (participant.text() != null) {
                Position textPos;
                double crossOffset = 0.0;
                double availableCross = columnMainAxis ? parentContentSize.width() : parentContentSize.height();
                double usedCross = columnMainAxis ? participant.size().width() : participant.size().height();
                crossOffset = baselineLine ? baselineOffsets[i] : Flex.resolveCrossOffset(flex, availableCross, usedCross);
                if (crossReversed && !baselineLine) {
                    crossOffset = Math.max(0.0, availableCross - usedCross - crossOffset);
                }
                Position position = textPos = columnMainAxis ? new Position(cursorX + crossOffset, cursorY) : new Position(cursorX, cursorY + crossOffset);
                if (mainReversed) {
                    double mirrored = Flex.mirrorMainPosition(columnMainAxis ? parentBox.offset("top") : parentBox.offset("left"), availableMain, mainSize, columnMainAxis ? textPos.y : textPos.x);
                    textPos = columnMainAxis ? new Position(textPos.x, mirrored) : new Position(mirrored, textPos.y);
                }
                layouts.add(new DirectTextLayout(participant.text(), textPos));
            }
            if (columnMainAxis) {
                cursorY += mainSize + Flex.mainAxisAutoMarginAfter(participant, true, autoMarginShare);
            } else {
                cursorX += mainSize + Flex.mainAxisAutoMarginAfter(participant, false, autoMarginShare);
            }
            if (i + 1 >= participants.size()) continue;
            if (columnMainAxis) {
                cursorY += gap + flexOffset.offsetInterval;
                continue;
            }
            cursorX += gap + flexOffset.offsetInterval;
        }
        return new FlexLayoutResult(positions, List.copyOf(layouts));
    }

    private static double mirrorMainPosition(double contentStart, double availableMain, double mainSize, double logicalMain) {
        return contentStart + Math.max(0.0, availableMain - mainSize - (logicalMain - contentStart));
    }

    private static FlexLayoutResult computeWrappedRowLayout(Element parent, Box parentBox, List<Element> flowItems, double availableWidth) {
        IdentityHashMap<Element, Position> positions = new IdentityHashMap<Element, Position>();
        double rowGap = Flex.resolveRowGap(parent);
        Flex flex = Flex.of(parent);
        boolean mainReversed = flex.flexDirection.contains("reverse");
        boolean crossReversed = flex.flexWrap.contains("reverse");
        double availableCross = parentBox.innerSize().height();
        List<WrappedRowLine> lines = Flex.buildWrappedRowLines(parent, Flex.sortItemsByOrder(flowItems), availableWidth);
        if (lines.isEmpty()) {
            return new FlexLayoutResult(positions, List.of());
        }
        double totalLinesCross = 0.0;
        for (WrappedRowLine line : lines) {
            totalLinesCross += line.lineHeight();
        }
        if (lines.size() > 1) {
            totalLinesCross += rowGap * (double)(lines.size() - 1);
        }
        AlignContentOffset alignOffset = Flex.computeAlignContentOffset(flex.alignContent, availableCross - totalLinesCross, lines.size());
        double cursorY = alignOffset.offsetStart;
        for (int i = 0; i < lines.size(); ++i) {
            WrappedRowLine line = lines.get(i);
            double lineHeight = line.lineHeight() + alignOffset.extraPerLine;
            double freeSpace = Math.max(0.0, availableWidth - line.lineWidth());
            FlexLayoutOffset lineOffset = Flex.computeJustifyContentOffset(flex.justifyContent, freeSpace, line.items().size(), 0);
            double cursorX = lineOffset.offsetStart;
            for (int index = 0; index < line.items().size(); ++index) {
                Element item = line.items().get(index);
                Size itemSize = Size.box(item);
                double offsetY = Flex.resolveWrappedRowCrossAxisOffset(item, lineHeight, itemSize.height(), parent);
                double logicalY = cursorY + offsetY;
                double physicalX = parentBox.offset("left") + cursorX;
                double physicalY = parentBox.offset("top") + (crossReversed ? Math.max(0.0, availableCross - itemSize.height() - logicalY) : logicalY);
                if (mainReversed) {
                    physicalX = parentBox.offset("left") + Math.max(0.0, availableWidth - itemSize.width() - cursorX);
                }
                positions.put(item, new Position(physicalX, physicalY));
                cursorX += itemSize.width();
                if (index + 1 >= line.items().size()) continue;
                cursorX += line.columnGap() + lineOffset.offsetInterval;
            }
            cursorY += lineHeight + rowGap + (i + 1 < lines.size() ? alignOffset.offsetInterval : 0.0);
        }
        return new FlexLayoutResult(positions, List.of());
    }

    public static List<Element> getFlowItems(List<Element> siblings) {
        ArrayList<Element> flowItems = new ArrayList<Element>();
        for (Element sibling : siblings) {
            if (!Layout.isInFlow(sibling.getComputedStyle())) continue;
            flowItems.add(sibling);
        }
        return flowItems;
    }

    public static double resolveMainAxisGap(Element parent) {
        if (parent == null) {
            return 0.0;
        }
        Style style = parent.getComputedStyle();
        boolean column = Flex.of((Element)parent).flexDirection.contains("column");
        String raw = column ? ("unset".equals(style.rowGap) ? style.gap : style.rowGap) : ("unset".equals(style.columnGap) ? style.gap : style.columnGap);
        double basis = column ? Size.getScaleHeight(parent) : Size.getScaleWidth(parent);
        return Math.max(0.0, Size.resolveLength(raw, basis, 0.0));
    }

    private static double resolveAvailableMainSize(Element parent, Box parentBox, Flex flex) {
        if (parent == null || parentBox == null || flex == null) {
            return 0.0;
        }
        Size parentContentSize = parentBox.innerSize();
        return Math.max(0.0, flex.flexDirection.contains("column") ? parentContentSize.height() : parentContentSize.width());
    }

    public static boolean shouldStretchCrossAxis(Element child, Element parent) {
        boolean hasCrossAxisAutoMargin;
        String effective;
        if (child == null || parent == null) {
            return false;
        }
        Flex flex = Flex.of(parent);
        Style childStyle = child.getComputedStyle();
        String alignSelf = childStyle.alignSelf == null ? "auto" : childStyle.alignSelf.trim().toLowerCase();
        String string = effective = "unset".equals(alignSelf) || "auto".equals(alignSelf) ? flex.alignItems.value : alignSelf;
        if (!"stretch".equals(effective)) {
            return false;
        }
        Box childBox = Box.of(child);
        boolean bl = flex.flexDirection.contains("column") ? childBox.isMarginAuto("left") || childBox.isMarginAuto("right") : (hasCrossAxisAutoMargin = childBox.isMarginAuto("top") || childBox.isMarginAuto("bottom"));
        if (hasCrossAxisAutoMargin) {
            return false;
        }
        Double aspectRatio = Size.parseAspectRatio(childStyle.aspectRatio);
        if (aspectRatio != null && aspectRatio > 0.0) {
            if (flex.flexDirection.contains("column") && Size.parseNumber(childStyle.height) != null) {
                return false;
            }
            if (!flex.flexDirection.contains("column") && Size.parseNumber(childStyle.width) != null) {
                return false;
            }
        }
        return flex.flexDirection.contains("column") ? Size.parseNumber(childStyle.width) == null : Size.parseNumber(childStyle.height) == null;
    }

    public static double resolveFlexGrow(Element child) {
        if (child == null) {
            return 0.0;
        }
        Style style = child.getComputedStyle();
        Double parsed = Size.parseNumber(style.flexGrow);
        return parsed == null ? 0.0 : Math.max(0.0, parsed);
    }

    public static double resolveFlexShrink(Element child) {
        if (child == null) {
            return 1.0;
        }
        Style style = child.getComputedStyle();
        Double parsed = Size.parseNumber(style.flexShrink);
        return parsed == null ? 1.0 : Math.max(0.0, parsed);
    }

    public static ItemUsedSize resolveItemUsedSize(Element element, Box box, double contentWidth, double contentHeight, boolean widthAuto, boolean heightAuto, double horizontalBox, double verticalBox, Double explicitParentHeight, boolean allowMainAxisAdjustment) {
        Element parent;
        Element element2 = parent = element == null ? null : element.parentElement;
        if (parent == null || !Layout.isInFlow(element.getComputedStyle()) || !Layout.isFlexDisplay(parent.getComputedStyle().display)) {
            return new ItemUsedSize(contentWidth, contentHeight, false, false);
        }
        Flex flex = Flex.of(parent);
        boolean parentResolving = Size.isResolving(parent);
        boolean mainSizeAssigned = false;
        boolean crossSizeStretched = false;
        if (allowMainAxisAdjustment) {
            Size parentContentSize;
            Size size = parentContentSize = parentResolving ? Size.ZERO : Box.of(parent).innerSize();
            if (flex.flexDirection.contains("column") && widthAuto && Flex.shouldStretchCrossAxis(element, parent)) {
                double parentCrossWidth = parentContentSize.width() > 0.0 ? parentContentSize.width() : Size.getScaleWidth(element);
                contentWidth = Math.max(0.0, parentCrossWidth - box.getMarginHorizontal() - horizontalBox);
            } else if (flex.flexDirection.contains("row") && heightAuto && Flex.shouldStretchCrossAxis(element, parent) && (!parentResolving || explicitParentHeight != null)) {
                double parentCrossHeight = parentContentSize.height() > 0.0 ? parentContentSize.height() : (explicitParentHeight != null ? explicitParentHeight : Size.getScaleHeight(element));
                contentHeight = Math.max(0.0, parentCrossHeight - box.getMarginVertical() - verticalBox);
                crossSizeStretched = true;
            }
            if (!parentResolving && flex.flexDirection.contains("column") && heightAuto) {
                double outer = Flex.resolveAssignedMainSize(element, parent, contentHeight + verticalBox + box.getMarginVertical());
                contentHeight = Math.max(0.0, outer - box.getMarginVertical() - verticalBox);
                mainSizeAssigned = true;
            } else if (!parentResolving && flex.flexDirection.contains("row") && Size.hasDefiniteAutoResolvedWidth(parent)) {
                double previousWidth = contentWidth;
                double outer = Flex.resolveAssignedMainSize(element, parent, contentWidth + horizontalBox + box.getMarginHorizontal());
                contentWidth = Math.max(0.0, outer - box.getMarginHorizontal() - horizontalBox);
                if (heightAuto && !Flex.shouldStretchCrossAxis(element, parent) && Math.abs(contentWidth - previousWidth) > 1.0E-4) {
                    Size constrained = Size.naturalAtContentWidth(element, contentWidth);
                    contentHeight = Math.max(0.0, constrained.height() - verticalBox);
                }
            }
        }
        if (!parentResolving && Flex.shouldStretchCrossAxis(element, parent)) {
            Size parentInner = Box.of(parent).innerSize();
            if (flex.flexDirection.contains("column")) {
                contentWidth = Math.max(0.0, parentInner.width() - box.getMarginHorizontal() - horizontalBox);
            } else {
                contentHeight = Math.max(0.0, parentInner.height() - box.getMarginVertical() - verticalBox);
                crossSizeStretched = true;
            }
        }
        return new ItemUsedSize(contentWidth, contentHeight, mainSizeAssigned, crossSizeStretched);
    }

    public static double resolveAssignedMainSize(Element child, Element parent, double naturalOuterMainSize) {
        if (child == null || parent == null) {
            return naturalOuterMainSize;
        }
        List<Element> flowItems = Flex.getFlowItems(parent.getRenderChildren());
        int index = Flex.indexOfIdentity(flowItems, child);
        if (index < 0) {
            return naturalOuterMainSize;
        }
        return Flex.computeAssignedMainSizes(parent, flowItems)[index];
    }

    private static double[] computeAssignedMainSizes(Element parent, List<Element> items) {
        boolean natural = Size.isNaturalMeasurementContext();
        AssignedMainSizes cached = (AssignedMainSizes)LayoutMeasureCache.getObject(6, parent, Double.NaN, Double.NaN, natural);
        if (cached != null && cached.matches(items)) {
            return (double[])cached.values().clone();
        }
        Flex flex = Flex.of(parent);
        Box parentBox = Box.of(parent);
        double availableMain = Flex.resolveAvailableMainSize(parent, parentBox, flex);
        double[] assigned = Flex.computeAssignedMainSizes(parent, items, availableMain, flex);
        LayoutMeasureCache.putObject(6, parent, Double.NaN, Double.NaN, natural, new AssignedMainSizes(items, (double[])assigned.clone()));
        return assigned;
    }

    private static double[] computeAssignedMainSizes(Element parent, List<Element> items, double availableMain, Flex flex) {
        double gap = Flex.resolveMainAxisGap(parent);
        double[] assigned = new double[items.size()];
        double[] minMainSizes = new double[items.size()];
        double totalBase = items.size() > 1 ? gap * (double)(items.size() - 1) : 0.0;
        double totalGrow = 0.0;
        double[] shrinkFactors = new double[items.size()];
        for (int i = 0; i < items.size(); ++i) {
            double base;
            Element item = items.get(i);
            Box itemBox = Box.of(item);
            Size naturalElementSize = Flex.measureNaturalFlexItem(parent, item, flex);
            Size naturalItemSize = new Size(naturalElementSize.width() + itemBox.getMarginHorizontal(), naturalElementSize.height() + itemBox.getMarginVertical());
            double naturalOuterMainSize = flex.flexDirection.contains("column") ? naturalItemSize.height() : naturalItemSize.width();
            assigned[i] = base = Flex.resolveFlexBaseMainSize(item, parent, flex.flexDirection.contains("column"), naturalOuterMainSize);
            minMainSizes[i] = Flex.resolveMinMainSize(item, flex.flexDirection.contains("column"), base);
            totalBase += base;
            double grow = Flex.resolveFlexGrow(item);
            double shrink = Flex.resolveFlexShrink(item);
            totalGrow += grow;
            shrinkFactors[i] = Math.max(0.0, shrink);
        }
        double remaining = availableMain - totalBase;
        if (remaining > 0.0 && totalGrow > 0.0) {
            for (int i = 0; i < items.size(); ++i) {
                double grow = Flex.resolveFlexGrow(items.get(i));
                if (grow <= 0.0) continue;
                int n = i;
                assigned[n] = assigned[n] + remaining * (grow / totalGrow);
            }
        } else if (remaining < 0.0) {
            Flex.shrinkToFit(assigned, minMainSizes, shrinkFactors, -remaining);
        }
        if (Boolean.getBoolean("apricityui.test.logStyles") && Flex.shouldLogFlexParent(parent)) {
            StringBuilder builder = new StringBuilder();
            builder.append("[AUI Flex] parent=").append(parent.tagName).append(" class=").append(parent.getClassNames()).append(" availableMain=").append(availableMain).append(" gap=").append(gap).append(" assigned=[");
            for (int i = 0; i < items.size(); ++i) {
                if (i > 0) {
                    builder.append(", ");
                }
                builder.append(items.get((int)i).tagName).append(":").append(items.get(i).getClassNames()).append("=").append(assigned[i]);
            }
            builder.append("]");
            ApricityUI.LOGGER.info(builder.toString());
        }
        return assigned;
    }

    private static Size measureNaturalFlexItem(Element parent, Element item, Flex flex) {
        double parentContentWidth;
        if (parent == null || item == null || flex == null || !flex.flexDirection.contains("column") || !Flex.shouldStretchCrossAxis(item, parent)) {
            return Size.natural(item);
        }
        Double naturalWidth = Size.getNaturalMeasurementWidthContext(parent);
        double d = parentContentWidth = naturalWidth != null ? naturalWidth.doubleValue() : Box.of(parent).innerSize().width();
        if (parentContentWidth <= 0.0) {
            return Size.natural(item);
        }
        Box itemBox = Box.of(item);
        double itemContentWidth = parentContentWidth - itemBox.getMarginHorizontal() - itemBox.getBorderHorizontal() - itemBox.getPaddingHorizontal();
        return Size.naturalAtContentWidth(item, Math.max(0.0, itemContentWidth));
    }

    public static double computeRowCrossSizeAtMainSize(Element parent, double availableMain) {
        if (parent == null) {
            return 0.0;
        }
        Flex flex = Flex.of(parent);
        if (!flex.flexDirection.contains("row")) {
            return 0.0;
        }
        if (Flex.flexWraps(flex)) {
            List<Element> items = Flex.getFlowItems(parent.getRenderChildren());
            return Flex.computeWrappedRowContentSize(parent, items, Math.max(0.0, availableMain)).height();
        }
        List<Element> items = Flex.getFlowItems(parent.getRenderChildren());
        double crossSize = 0.0;
        for (FlexParticipant participant : Flex.buildParticipants(parent, items)) {
            if (participant.element() != null) continue;
            crossSize = Math.max(crossSize, participant.size().height());
        }
        if (items.isEmpty()) {
            return crossSize;
        }
        double[] assigned = Flex.computeAssignedMainSizes(parent, items, Math.max(0.0, availableMain), flex);
        for (int i = 0; i < items.size(); ++i) {
            Size constrained;
            Size natural;
            Element item = items.get(i);
            Box box = Box.of(item);
            double borderBoxWidth = Math.max(0.0, assigned[i] - box.getMarginHorizontal());
            if (borderBoxWidth + 1.0E-4 >= (natural = Size.natural(item)).width()) {
                constrained = natural;
            } else {
                double contentWidth = Math.max(0.0, borderBoxWidth - box.getBorderHorizontal() - box.getPaddingHorizontal());
                constrained = Size.naturalAtContentWidth(item, contentWidth);
            }
            crossSize = Math.max(crossSize, constrained.height() + box.getMarginVertical());
        }
        return crossSize;
    }

    private static boolean shouldLogFlexParent(Element parent) {
        if (parent == null) {
            return false;
        }
        return parent.getClassNames().contains("compact-actions");
    }

    private static double resolveFlexBaseMainSize(Element item, Element parent, boolean columnMainAxis, double naturalOuterMainSize) {
        if (item == null) {
            return Math.max(0.0, naturalOuterMainSize);
        }
        Style style = item.getComputedStyle();
        String flexBasis = style.flexBasis;
        if (flexBasis == null || flexBasis.isBlank() || "auto".equalsIgnoreCase(flexBasis) || "unset".equalsIgnoreCase(flexBasis)) {
            return Math.max(0.0, naturalOuterMainSize);
        }
        Box box = Box.of(item);
        double percentBasis = columnMainAxis ? Size.getScaleHeight(parent) : Size.getScaleWidth(parent);
        double resolved = Size.resolveLength(flexBasis, percentBasis, 0.0);
        double outer = box.isBorderBox() ? resolved : resolved + (columnMainAxis ? box.getBorderVertical() + box.getPaddingVertical() : box.getBorderHorizontal() + box.getPaddingHorizontal());
        return Math.max(0.0, outer += columnMainAxis ? box.getMarginVertical() : box.getMarginHorizontal());
    }

    private static double resolveMinMainSize(Element item, boolean columnMainAxis, double naturalOuterMainSize) {
        if (item == null) {
            return Math.max(0.0, naturalOuterMainSize);
        }
        Style style = item.getComputedStyle();
        String rawMin = columnMainAxis ? style.minHeight : style.minWidth;
        Double parsedMin = Size.parseNumber(rawMin);
        if (parsedMin == null) {
            boolean flexible;
            if (columnMainAxis && Flex.isOverflowVisible(style.overflow)) {
                return Math.max(0.0, naturalOuterMainSize);
            }
            Box box = Box.of(item);
            Element parent = item.parentElement;
            boolean definiteMain = parent == null || (columnMainAxis ? Size.parseNumber(parent.getComputedStyle().height) != null : Size.hasDefiniteAutoResolvedWidth(parent));
            boolean parentWraps = parent != null && Flex.flexWraps(Flex.of(parent));
            boolean bl = flexible = definiteMain && (Flex.resolveFlexShrink(item) > 0.0 || Flex.resolveFlexGrow(item) > 0.0 || style.flexBasis != null && !style.flexBasis.isBlank() && !"auto".equalsIgnoreCase(style.flexBasis) && !"unset".equalsIgnoreCase(style.flexBasis));
            if (flexible && !parentWraps) {
                return Math.max(0.0, columnMainAxis ? box.getBorderVertical() + box.getPaddingVertical() + box.getMarginVertical() : box.getBorderHorizontal() + box.getPaddingHorizontal() + box.getMarginHorizontal());
            }
            return Math.max(0.0, naturalOuterMainSize);
        }
        double basis = columnMainAxis ? Size.getScaleHeight(item) : Size.getScaleWidth(item);
        double resolved = Size.resolveLength(rawMin, basis, parsedMin);
        Box box = Box.of(item);
        boolean borderBox = box.isBorderBox();
        double total = borderBox ? resolved : resolved + (columnMainAxis ? box.getBorderVertical() + box.getPaddingVertical() : box.getBorderHorizontal() + box.getPaddingHorizontal());
        return Math.max(0.0, total += columnMainAxis ? box.getMarginVertical() : box.getMarginHorizontal());
    }

    private static void shrinkToFit(double[] assigned, double[] minMainSizes, double[] shrinkFactors, double deficit) {
        double consumed;
        if (assigned == null || minMainSizes == null || shrinkFactors == null || deficit <= 0.0) {
            return;
        }
        boolean[] frozen = new boolean[assigned.length];
        for (double remainingDeficit = deficit; remainingDeficit > 0.01; remainingDeficit -= consumed) {
            double totalWeight = 0.0;
            for (int i = 0; i < assigned.length; ++i) {
                if (frozen[i]) continue;
                double availableShrink = Math.max(0.0, assigned[i] - minMainSizes[i]);
                if (availableShrink <= 0.0 || shrinkFactors[i] <= 0.0) {
                    frozen[i] = true;
                    continue;
                }
                totalWeight += shrinkFactors[i] * Math.max(0.0, assigned[i]);
            }
            if (totalWeight <= 0.0) break;
            consumed = 0.0;
            for (int i = 0; i < assigned.length; ++i) {
                if (frozen[i]) continue;
                double availableShrink = Math.max(0.0, assigned[i] - minMainSizes[i]);
                if (availableShrink <= 0.0 || shrinkFactors[i] <= 0.0) {
                    frozen[i] = true;
                    continue;
                }
                double weight = shrinkFactors[i] * Math.max(0.0, assigned[i]);
                double cut = remainingDeficit * (weight / totalWeight);
                if (cut >= availableShrink) {
                    assigned[i] = minMainSizes[i];
                    consumed += availableShrink;
                    frozen[i] = true;
                    continue;
                }
                int n = i;
                assigned[n] = assigned[n] - cut;
                consumed += cut;
            }
            if (consumed <= 0.01) break;
        }
    }

    private static boolean isOverflowVisible(String overflow) {
        return overflow == null || overflow.isBlank() || "unset".equalsIgnoreCase(overflow) || "visible".equalsIgnoreCase(overflow);
    }

    private static Size computeWrappedRowContentSize(Element element, List<Element> items, double availableWidth) {
        double rowGap = Flex.resolveRowGap(element);
        double totalHeight = 0.0;
        double maxWidth = 0.0;
        List<WrappedRowLine> lines = Flex.buildWrappedRowLines(element, Flex.sortItemsByOrder(items), availableWidth);
        for (int i = 0; i < lines.size(); ++i) {
            WrappedRowLine line = lines.get(i);
            maxWidth = Math.max(maxWidth, line.lineWidth());
            totalHeight += line.lineHeight();
            if (i + 1 >= lines.size()) continue;
            totalHeight += rowGap;
        }
        return new Size(maxWidth, totalHeight);
    }

    private static List<WrappedRowLine> buildWrappedRowLines(Element parent, List<Element> items) {
        return Flex.buildWrappedRowLines(parent, items, Flex.resolveWrappedRowAvailableWidth(parent));
    }

    private static List<WrappedRowLine> buildWrappedRowLines(Element parent, List<Element> items, double availableWidth) {
        ArrayList<WrappedRowLine> lines = new ArrayList<WrappedRowLine>();
        if (parent == null || items == null || items.isEmpty()) {
            return lines;
        }
        double columnGap = Flex.resolveColumnGap(parent);
        ArrayList<Element> currentItems = new ArrayList<Element>();
        double lineWidth = 0.0;
        double lineHeight = 0.0;
        for (Element item : items) {
            double nextWidth;
            Size itemSize = Size.box(item);
            double itemWidth = itemSize.width();
            double itemHeight = itemSize.height();
            double d = nextWidth = currentItems.isEmpty() ? itemWidth : lineWidth + columnGap + itemWidth;
            if (!currentItems.isEmpty() && availableWidth > 0.0 && nextWidth > availableWidth) {
                lines.add(new WrappedRowLine(List.copyOf(currentItems), lineWidth, lineHeight, columnGap));
                currentItems.clear();
                lineWidth = 0.0;
                lineHeight = 0.0;
                nextWidth = itemWidth;
            }
            currentItems.add(item);
            lineWidth = nextWidth;
            lineHeight = Math.max(lineHeight, itemHeight);
        }
        if (!currentItems.isEmpty()) {
            lines.add(new WrappedRowLine(List.copyOf(currentItems), lineWidth, lineHeight, columnGap));
        }
        return lines;
    }

    private static double resolveWrappedRowAvailableWidth(Element parent) {
        if (parent == null) {
            return 0.0;
        }
        Style style = parent.getComputedStyle();
        Box box = Box.of(parent);
        Double declaredWidth = Size.parseNumber(style.width);
        if (declaredWidth != null) {
            double resolvedWidth = Size.resolveLength(style.width, Size.getScaleWidth(parent), declaredWidth);
            if (box.isBorderBox()) {
                resolvedWidth -= box.getBorderHorizontal() + box.getPaddingHorizontal();
            }
            return Math.max(0.0, resolvedWidth);
        }
        if ("inline-flex".equalsIgnoreCase(style.display)) {
            return 0.0;
        }
        double containingBlockWidth = Size.getScaleWidth(parent);
        double autoContentWidth = containingBlockWidth - box.getMarginHorizontal() - box.getBorderHorizontal() - box.getPaddingHorizontal();
        return Math.max(0.0, autoContentWidth);
    }

    private static double naturalWidthCacheKey(Element element, boolean natural) {
        if (!natural) {
            return Double.NaN;
        }
        Double width = Size.getNaturalMeasurementWidthContext(element);
        return width == null ? Double.NaN : width;
    }

    private static double resolveWrappedRowCrossAxisOffset(Element child, double lineHeight, double itemHeight, Element parent) {
        if (child == null || parent == null) {
            return 0.0;
        }
        return Flex.resolveCrossAxisOffset(child, parent, lineHeight, itemHeight);
    }

    public static String resolveCrossAxisAlignValue(Element child, Element parent) {
        if (parent == null) {
            return "stretch";
        }
        Flex flex = Flex.of(parent);
        if (child == null) {
            return flex.alignItems.value;
        }
        Style childStyle = child.getComputedStyle();
        String alignSelf = childStyle.alignSelf == null ? "auto" : childStyle.alignSelf.trim().toLowerCase();
        return "unset".equals(alignSelf) || "auto".equals(alignSelf) ? flex.alignItems.value : alignSelf;
    }

    private static double resolveColumnGap(Element parent) {
        return Flex.resolveGap(parent, false);
    }

    private static double resolveRowGap(Element parent) {
        return Flex.resolveGap(parent, true);
    }

    private static double resolveGap(Element parent, boolean rowAxis) {
        if (parent == null) {
            return 0.0;
        }
        Style style = parent.getComputedStyle();
        String raw = rowAxis ? ("unset".equals(style.rowGap) ? style.gap : style.rowGap) : ("unset".equals(style.columnGap) ? style.gap : style.columnGap);
        double basis = rowAxis ? Size.getScaleHeight(parent) : Size.getScaleWidth(parent);
        return Math.max(0.0, Size.resolveLength(raw, basis, 0.0));
    }

    private static double resolveCrossOffset(Flex flex, double availableCross, double usedCross) {
        Align align = Align.normalize(flex.alignItems.value(), Align.STRETCH);
        return switch (align) {
            case Align.CENTER -> Math.max(0.0, (availableCross - usedCross) / 2.0);
            case Align.END -> Math.max(0.0, availableCross - usedCross);
            default -> 0.0;
        };
    }

    private static double resolveCrossAxisOffset(Element child, Element parent, double availableCross, double usedCross) {
        if (child == null || parent == null) {
            return 0.0;
        }
        Flex flex = Flex.of(parent);
        Box box = Box.of(child);
        boolean beforeAuto = flex.flexDirection.contains("column") ? box.isMarginAuto("left") : box.isMarginAuto("top");
        boolean afterAuto = flex.flexDirection.contains("column") ? box.isMarginAuto("right") : box.isMarginAuto("bottom");
        double freeSpace = availableCross - usedCross;
        if (beforeAuto || afterAuto) {
            if (freeSpace <= 0.0) {
                return 0.0;
            }
            if (beforeAuto && afterAuto) {
                return freeSpace / 2.0;
            }
            return beforeAuto ? freeSpace : 0.0;
        }
        Align align = Align.normalize(Flex.resolveCrossAxisAlignValue(child, parent), Align.STRETCH);
        return switch (align) {
            case Align.CENTER -> Math.max(0.0, freeSpace / 2.0);
            case Align.END -> Math.max(0.0, freeSpace);
            default -> 0.0;
        };
    }

    private static boolean isBaselineAligned(List<FlexParticipant> participants, Flex flex) {
        if (flex != null && flex.alignItems.is("baseline")) {
            return true;
        }
        if (participants == null) {
            return false;
        }
        for (FlexParticipant participant : participants) {
            String alignSelf;
            Element element = participant.element();
            if (element == null || (alignSelf = element.getComputedStyle().alignSelf) == null || !"baseline".equalsIgnoreCase(alignSelf.trim())) continue;
            return true;
        }
        return false;
    }

    private static BaselineMetrics computeBaselineMetrics(List<FlexParticipant> participants) {
        double maxAscent = 0.0;
        double maxDescent = 0.0;
        if (participants == null) {
            return new BaselineMetrics(0.0, 0.0);
        }
        for (FlexParticipant participant : participants) {
            double baseline = Flex.baselineFromCrossStart(participant);
            double height = participant.size() == null ? 0.0 : participant.size().height();
            maxAscent = Math.max(maxAscent, Math.max(0.0, baseline));
            maxDescent = Math.max(maxDescent, Math.max(0.0, height - baseline));
        }
        return new BaselineMetrics(maxAscent, maxDescent);
    }

    private static double[] computeBaselineOffsets(List<FlexParticipant> participants, double availableCross, boolean crossReversed) {
        int n = participants.size();
        double[] offsets = new double[n];
        BaselineMetrics metrics = Flex.computeBaselineMetrics(participants);
        for (int i = 0; i < n; ++i) {
            double baseline = Flex.baselineFromCrossStart(participants.get(i));
            offsets[i] = crossReversed ? Math.max(0.0, availableCross - metrics.descent - baseline) : Math.max(0.0, metrics.ascent - baseline);
        }
        return offsets;
    }

    private static double baselineFromCrossStart(FlexParticipant participant) {
        if (participant == null) {
            return 0.0;
        }
        if (participant.element() != null) {
            Element element = participant.element();
            Text text = Text.of(element);
            if (text != null && text.content != null && !text.content.isBlank()) {
                Box box = Box.of(element);
                return Math.max(0.0, box.getMarginTop() + box.getBorderTop() + box.getPaddingTop() + Text.baselineOffset(text));
            }
            return participant.size() == null ? 0.0 : participant.size().height();
        }
        Text text = participant.text();
        return text == null ? 0.0 : Text.baselineOffset(text);
    }

    private static boolean hasCrossAxisAutoMargin(Element child, boolean columnMainAxis) {
        if (child == null) {
            return false;
        }
        Box box = Box.of(child);
        return columnMainAxis ? box.isMarginAuto("left") || box.isMarginAuto("right") : box.isMarginAuto("top") || box.isMarginAuto("bottom");
    }

    private static double resolveMainAxisAutoMarginShare(List<FlexParticipant> participants, boolean columnMainAxis, double freeSpace) {
        if (participants == null || participants.isEmpty() || freeSpace <= 0.0) {
            return 0.0;
        }
        int autoMarginCount = 0;
        for (FlexParticipant participant : participants) {
            autoMarginCount += Flex.countMainAxisAutoMargins(participant, columnMainAxis);
        }
        return autoMarginCount <= 0 ? 0.0 : freeSpace / (double)autoMarginCount;
    }

    private static int countMainAxisAutoMargins(FlexParticipant participant, boolean columnMainAxis) {
        Element element;
        Element element2 = element = participant == null ? null : participant.element();
        if (element == null) {
            return 0;
        }
        Box box = Box.of(element);
        int count = 0;
        if (box.isMarginAuto(columnMainAxis ? "top" : "left")) {
            ++count;
        }
        if (box.isMarginAuto(columnMainAxis ? "bottom" : "right")) {
            ++count;
        }
        return count;
    }

    private static double mainAxisAutoMarginBefore(FlexParticipant participant, boolean columnMainAxis, double autoMarginShare) {
        if (autoMarginShare <= 0.0 || participant == null || participant.element() == null) {
            return 0.0;
        }
        Box box = Box.of(participant.element());
        return box.isMarginAuto(columnMainAxis ? "top" : "left") ? autoMarginShare : 0.0;
    }

    private static double mainAxisAutoMarginAfter(FlexParticipant participant, boolean columnMainAxis, double autoMarginShare) {
        if (autoMarginShare <= 0.0 || participant == null || participant.element() == null) {
            return 0.0;
        }
        Box box = Box.of(participant.element());
        return box.isMarginAuto(columnMainAxis ? "bottom" : "right") ? autoMarginShare : 0.0;
    }

    private static double participantMainSize(FlexParticipant participant, double[] itemMainSizes, boolean columnMainAxis) {
        if (participant == null) {
            return 0.0;
        }
        Element element = participant.element();
        if (element == null) {
            return participant.mainSize(columnMainAxis);
        }
        int index = participant.itemIndex();
        if (index < 0 || index >= itemMainSizes.length) {
            return participant.mainSize(columnMainAxis);
        }
        return itemMainSizes[index];
    }

    private static int indexOfIdentity(List<Element> items, Element target) {
        if (items == null || target == null) {
            return -1;
        }
        for (int i = 0; i < items.size(); ++i) {
            if (items.get(i) != target) continue;
            return i;
        }
        return -1;
    }

    private static List<FlexParticipant> buildParticipants(Element parent, List<Element> flowItems) {
        String normalized;
        ArrayList<FlexParticipant> participants = new ArrayList<FlexParticipant>();
        if (parent == null) {
            return participants;
        }
        int flowIndex = 0;
        for (Node child : parent.getRenderChildNodes()) {
            TextNode textNode;
            String normalized2;
            if (child instanceof Element) {
                Element childElement = (Element)child;
                if (flowIndex >= flowItems.size() || flowItems.get(flowIndex) != childElement) continue;
                participants.add(new FlexParticipant(childElement, null, Flex.participantSize(parent, childElement), flowIndex));
                ++flowIndex;
                continue;
            }
            if (!(child instanceof TextNode) || (normalized2 = Text.normalizeWhiteSpaceContent((textNode = (TextNode)child).getTextContent(), Text.getWhiteSpace(parent))) == null || normalized2.isBlank()) continue;
            Text base = Text.of(parent);
            Text text = new Text();
            TextMetrics.copyTextForRun(base, text);
            text.color = base.color == null ? Color.BLACK : base.color;
            text.strokeColor = base.strokeColor == null ? Color.BLACK : base.strokeColor;
            text.content = normalized2;
            text.flexDirect = true;
            Text.WrappedText wrapped = Text.wrap(text, 0.0);
            text.size = new Size(wrapped.width(), wrapped.height(text.lineHeight));
            participants.add(new FlexParticipant(null, text, text.size, -1));
        }
        if (participants.stream().noneMatch(participant -> participant.text() != null) && parent.innerText != null && !parent.innerText.isBlank() && (normalized = Text.normalizeWhiteSpaceContent(parent.innerText, Text.getWhiteSpace(parent))) != null && !normalized.isBlank()) {
            Text base = Text.of(parent);
            Text text = new Text();
            TextMetrics.copyTextForRun(base, text);
            text.color = base.color == null ? Color.BLACK : base.color;
            text.strokeColor = base.strokeColor == null ? Color.BLACK : base.strokeColor;
            text.content = normalized;
            text.flexDirect = true;
            Text.WrappedText wrapped = Text.wrap(text, 0.0);
            text.size = new Size(wrapped.width(), wrapped.height(text.lineHeight));
            participants.add(new FlexParticipant(null, text, text.size, -1));
        }
        return participants;
    }

    private static Size participantSize(Element parent, Element child) {
        if (child == null) {
            return Size.ZERO;
        }
        if (!Size.isNaturalMeasurementContext() && !Size.isResolving(parent)) {
            return Size.box(child);
        }
        Box box = Box.of(child);
        Size naturalSize = Size.natural(child);
        return new Size(naturalSize.width() + box.getMarginHorizontal(), naturalSize.height() + box.getMarginVertical());
    }

    private static FlexLayoutOffset computeJustifyContentOffset(KeywordValue justifyContent, double offsetTotal, int siblingsCount, int index) {
        double offsetStart = 0.0;
        double offsetInterval = 0.0;
        if (offsetTotal < 0.0 && (justifyContent.is("space-around") || justifyContent.is("space-evenly") || justifyContent.is("space-between"))) {
            return new FlexLayoutOffset(0.0, 0.0);
        }
        if (justifyContent.is("center")) {
            offsetStart = offsetTotal / 2.0;
        } else if (justifyContent.is("flex-end")) {
            offsetStart = offsetTotal;
        } else if (justifyContent.is("space-around")) {
            offsetStart = offsetTotal / (double)siblingsCount / 2.0;
            offsetInterval = offsetTotal / (double)siblingsCount;
        } else if (justifyContent.is("space-evenly")) {
            offsetInterval = offsetStart = offsetTotal / (double)(siblingsCount + 1);
        } else if (justifyContent.is("space-between")) {
            offsetStart = 0.0;
            offsetInterval = offsetTotal / (double)Math.max(1, siblingsCount - 1);
        }
        return new FlexLayoutOffset(offsetStart, offsetInterval);
    }

    private static AlignContentOffset computeAlignContentOffset(KeywordValue alignContent, double freeCross, int lineCount) {
        double offsetStart = 0.0;
        double offsetInterval = 0.0;
        double extraPerLine = 0.0;
        int count = Math.max(1, lineCount);
        if (alignContent == null || freeCross <= 0.0) {
            return new AlignContentOffset(0.0, 0.0, 0.0);
        }
        if (alignContent.is("center")) {
            offsetStart = freeCross / 2.0;
        } else if (alignContent.is("flex-end")) {
            offsetStart = freeCross;
        } else if (alignContent.is("space-around")) {
            offsetStart = freeCross / (double)count / 2.0;
            offsetInterval = freeCross / (double)count;
        } else if (alignContent.is("space-evenly")) {
            offsetInterval = offsetStart = freeCross / (double)(count + 1);
        } else if (alignContent.is("space-between")) {
            offsetStart = 0.0;
            offsetInterval = freeCross / (double)Math.max(1, count - 1);
        } else if (alignContent.is("stretch")) {
            extraPerLine = freeCross / (double)count;
        }
        return new AlignContentOffset(offsetStart, offsetInterval, extraPerLine);
    }

    public static final class KeywordValue {
        public final String value;

        public KeywordValue(String value) {
            this.value = value;
        }

        public boolean is(String keyword) {
            return keyword.equals(this.value);
        }

        public boolean contains(String part) {
            return this.value != null && this.value.contains(part);
        }

        public String value() {
            return this.value;
        }
    }

    private record FlexLayoutResult(IdentityHashMap<Element, Position> positions, List<DirectTextLayout> directTextLayouts) {
        private static final FlexLayoutResult EMPTY = new FlexLayoutResult(new IdentityHashMap<Element, Position>(), List.of());
    }

    private record FlexParticipant(Element element, Text text, Size size, int itemIndex) {
        private double mainSize(boolean columnMainAxis) {
            if (this.size == null) {
                return 0.0;
            }
            return columnMainAxis ? this.size.height() : this.size.width();
        }
    }

    private record BaselineMetrics(double ascent, double descent) {
    }

    private record FlexLayoutOffset(double offsetStart, double offsetInterval) {
    }

    public record DirectTextLayout(Text text, Position position) {
    }

    private record WrappedRowLine(List<Element> items, double lineWidth, double lineHeight, double columnGap) {
    }

    private record AlignContentOffset(double offsetStart, double offsetInterval, double extraPerLine) {
    }

    public record ItemUsedSize(double contentWidth, double contentHeight, boolean mainSizeAssigned, boolean crossSizeStretched) {
    }

    private record AssignedMainSizes(List<Element> items, double[] values) {
        private boolean matches(List<Element> current) {
            if (this.items == current) {
                return true;
            }
            if (this.items == null || current == null || this.items.size() != current.size()) {
                return false;
            }
            for (int i = 0; i < this.items.size(); ++i) {
                if (this.items.get(i) == current.get(i)) continue;
                return false;
            }
            return true;
        }
    }
}

