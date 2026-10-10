/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.layout;

import com.sighs.apricityui.init.Element;
import com.sighs.apricityui.layout.Align;
import com.sighs.apricityui.layout.Box;
import com.sighs.apricityui.layout.Layout;
import com.sighs.apricityui.layout.LayoutMeasureCache;
import com.sighs.apricityui.layout.Position;
import com.sighs.apricityui.layout.Size;
import com.sighs.apricityui.style.Background;
import com.sighs.apricityui.style.Interaction;
import com.sighs.apricityui.style.Style;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public final class Grid {
    private static final ThreadLocal<Set<Element>> RESOLVING = ThreadLocal.withInitial(() -> Collections.newSetFromMap(new IdentityHashMap()));

    private Grid() {
    }

    public static Position computeChildPosition(Element element, Element parent, List<Element> siblings) {
        double cellH;
        Box parentBox = Box.of(parent);
        GridLayout layout = Grid.getOrComputeLayout(parent, siblings);
        int idx = layout.flow.indexOf(element);
        if (idx < 0) {
            return new Position(parentBox.offset("left"), parentBox.offset("top"));
        }
        Placement p = layout.placements.get(idx);
        double baseX = parentBox.offset("left") + Grid.prefixSum(layout.colW, p.col) + (double)p.col * (double)layout.gaps.colGap;
        double baseY = parentBox.offset("top") + Grid.prefixSum(layout.rowH, p.row) + (double)p.row * (double)layout.gaps.rowGap;
        double cellW = Grid.spanSum(layout.colW, p.col, p.colSpan) + (double)(p.colSpan - 1) * (double)layout.gaps.colGap;
        Size assignedSize = Grid.resolveAssignedSize(element, parent, layout, p, cellW, cellH = Grid.spanSum(layout.rowH, p.row, p.rowSpan) + (double)(p.rowSpan - 1) * (double)layout.gaps.rowGap);
        Size itemSize = assignedSize != null ? assignedSize : Size.box(element);
        Style ps = parent.getComputedStyle();
        Style es = element.getComputedStyle();
        double dx = Grid.computeAlignmentOffset(ps.justifyItems, es.justifySelf, cellW, itemSize.width());
        double dy = Grid.computeAlignmentOffset(ps.alignItems, es.alignSelf, cellH, itemSize.height());
        return new Position(baseX + dx, baseY + dy);
    }

    public static Size resolveAssignedSize(Element element) {
        if (element == null || element.parentElement == null) {
            return null;
        }
        Element parent = element.parentElement;
        if (!Layout.isGridDisplay(parent.getComputedStyle().display) || RESOLVING.get().contains(parent)) {
            return null;
        }
        GridLayout layout = Grid.getOrComputeLayout(parent, parent.getRenderChildren());
        int index = layout.flow.indexOf(element);
        if (index < 0) {
            return null;
        }
        Placement placement = layout.placements.get(index);
        double cellW = Grid.spanSum(layout.colW, placement.col, placement.colSpan) + (double)Math.max(0, placement.colSpan - 1) * (double)layout.gaps.colGap;
        double cellH = Grid.spanSum(layout.rowH, placement.row, placement.rowSpan) + (double)Math.max(0, placement.rowSpan - 1) * (double)layout.gaps.rowGap;
        return Grid.resolveAssignedSize(element, parent, layout, placement, cellW, cellH);
    }

    private static Size resolveAssignedSize(Element element, Element parent, GridLayout layout, Placement placement, double cellW, double cellH) {
        double targetH;
        boolean hasExplicitHeight;
        Style parentStyle = parent.getComputedStyle();
        Style selfStyle = element.getComputedStyle();
        boolean stretchW = Grid.isGridStretch(parentStyle.justifyItems, selfStyle.justifySelf);
        boolean stretchH = Grid.isGridStretch(parentStyle.alignItems, selfStyle.alignSelf);
        if (!stretchW && !stretchH) {
            return null;
        }
        boolean hasExplicitWidth = Size.parseNumber(selfStyle.width) != null;
        boolean bl = hasExplicitHeight = Size.parseNumber(selfStyle.height) != null;
        if (stretchW && hasExplicitWidth) {
            stretchW = false;
        }
        if (stretchH && hasExplicitHeight) {
            stretchH = false;
        }
        if (!stretchW && !stretchH) {
            return null;
        }
        Size current = Size.natural(element);
        Box box = Box.of(element);
        double targetW = stretchW ? Math.max(0.0, cellW - box.getMarginHorizontal()) : current.width();
        double d = targetH = stretchH ? Math.max(0.0, cellH - box.getMarginVertical()) : current.height();
        if (stretchW && Grid.hasContentBasedAutomaticMinimum(selfStyle, layout.cols, placement.col, placement.colSpan, true)) {
            targetW = Math.max(targetW, current.width());
        }
        if (stretchH && Grid.hasContentBasedAutomaticMinimum(selfStyle, layout.rows, placement.row, placement.rowSpan, false)) {
            targetH = Math.max(targetH, current.height());
        }
        double finalW = stretchW ? Math.max(0.0, targetW) : current.width();
        double finalH = stretchH ? Math.max(0.0, targetH) : current.height();
        return new Size(finalW, finalH);
    }

    private static boolean hasContentBasedAutomaticMinimum(Style style, List<Track> tracks, int start, int span, boolean horizontal) {
        String overflow;
        String minimum;
        String string = minimum = horizontal ? style.minWidth : style.minHeight;
        if (Size.tryResolveLength(minimum, 0.0) != null) {
            return false;
        }
        String string2 = overflow = horizontal ? Interaction.resolveOverflowX(style) : Interaction.resolveOverflowY(style);
        if (!"visible".equals(Interaction.normalizeOverflow(overflow))) {
            return false;
        }
        boolean spansAutoMinimum = false;
        boolean spansFlexible = false;
        int resolvedSpan = Math.max(1, span);
        int end = Math.min(tracks.size(), start + resolvedSpan);
        for (int i = Math.max(0, start); i < end; ++i) {
            Track track = tracks.get(i);
            spansAutoMinimum |= Grid.hasAutoMinimum(track);
            spansFlexible |= Grid.frWeight(track) > 0.0;
        }
        return spansAutoMinimum && (resolvedSpan <= 1 || !spansFlexible);
    }

    private static boolean hasAutoMinimum(Track track) {
        if (track == null) {
            return false;
        }
        return switch (track.type) {
            default -> throw new IncompatibleClassChangeError();
            case TrackType.AUTO, TrackType.FR -> true;
            case TrackType.FIXED -> false;
            case TrackType.MINMAX -> track.minTrack != null && track.minTrack.type == TrackType.AUTO;
        };
    }

    private static boolean isGridStretch(String containerValue, String selfValue) {
        Align container = Align.normalize(containerValue, Align.STRETCH);
        Align self = Align.normalize(selfValue, container);
        return self == Align.STRETCH;
    }

    public static Size computeContentSize(Element gridContainer) {
        GridLayout layout = Grid.getOrComputeLayout(gridContainer, gridContainer.getRenderChildren());
        if (layout.flow.isEmpty()) {
            return Size.ZERO;
        }
        double gridW = Grid.sum(layout.colW) + (double)layout.gaps.colGap * (double)Math.max(0, layout.colW.length - 1);
        double gridH = Grid.sum(layout.rowH) + (double)layout.gaps.rowGap * (double)Math.max(0, layout.rowH.length - 1);
        return new Size(gridW, gridH);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static GridLayout getOrComputeLayout(Element gridContainer, List<Element> siblings) {
        Size available = Grid.resolveAvailableTrackSpace(gridContainer);
        boolean natural = Size.isNaturalMeasurementContext();
        GridLayout cached = (GridLayout)LayoutMeasureCache.getObject(8, gridContainer, available.width(), available.height(), natural);
        if (cached != null) {
            return cached;
        }
        Set<Element> resolving = RESOLVING.get();
        if (!resolving.add(gridContainer)) {
            return new GridLayout(List.of(), List.of(), List.of(), List.of(), new double[]{0.0}, new double[]{0.0}, new Gaps(0, 0));
        }
        try {
            GridLayout result = Grid.computeLayout(gridContainer, siblings, available);
            LayoutMeasureCache.putObject(8, gridContainer, available.width(), available.height(), natural, result);
            GridLayout gridLayout = result;
            return gridLayout;
        }
        finally {
            resolving.remove(gridContainer);
            if (resolving.isEmpty()) {
                RESOLVING.remove();
            }
        }
    }

    private static GridLayout computeLayout(Element gridContainer, List<Element> siblings, Size availableSize) {
        Style ps = gridContainer.getComputedStyle();
        Gaps gaps = Grid.parseGaps(ps);
        List<Element> flow = Grid.collectFlowChildren(siblings);
        ParsedTracks parsedCols = Grid.parseTracks(ps.gridTemplateColumns, 1, availableSize.width(), gaps.colGap);
        ParsedTracks parsedRows = Grid.parseTracks(ps.gridTemplateRows, 0, availableSize.height(), gaps.rowGap);
        if (flow.isEmpty()) {
            List<Track> cols0 = parsedCols.tracks().isEmpty() ? Grid.makeAutoTracks(1) : parsedCols.tracks();
            List<Track> rows0 = parsedRows.tracks().isEmpty() ? Grid.makeAutoTracks(1) : parsedRows.tracks();
            return new GridLayout(flow, List.of(), cols0, rows0, new double[]{0.0}, new double[]{0.0}, gaps);
        }
        ArrayList<Track> cols = new ArrayList<Track>(parsedCols.tracks());
        List<Track> rows = new ArrayList<Track>(parsedRows.tracks());
        ArrayList<ItemSpec> items = new ArrayList<ItemSpec>();
        int requiredCols = Math.max(1, cols.size());
        for (Element e : flow) {
            Style es = e.getComputedStyle();
            SpanSpec col = Grid.parseSpanSpec(es.gridColumn);
            SpanSpec row = Grid.parseSpanSpec(es.gridRow);
            requiredCols = Math.max(requiredCols, Grid.spanRequirement(col));
            if (col.start >= 0) {
                requiredCols = Math.max(requiredCols, col.start + col.span);
            }
            items.add(new ItemSpec(col, row, e));
        }
        while (cols.size() < requiredCols) {
            cols.add(Track.auto());
        }
        int colCount = cols.size();
        Occupancy occ = new Occupancy(colCount);
        ArrayList<Placement> placements = new ArrayList<Placement>(items.size());
        int cursorRow = 0;
        int cursorCol = 0;
        for (ItemSpec itemSpec : items) {
            int placedRow;
            int placedCol;
            boolean hasRow;
            SpanSpec c = itemSpec.col;
            SpanSpec r = itemSpec.row;
            int colSpan = Math.max(1, c.span);
            int rowSpan = Math.max(1, r.span);
            if (colSpan > colCount) {
                int add = colSpan - colCount;
                for (int i = 0; i < add; ++i) {
                    cols.add(Track.auto());
                }
                colCount = cols.size();
                occ = occ.resize(colCount);
            }
            boolean hasCol = c.start >= 0;
            boolean bl = hasRow = r.start >= 0;
            if (hasCol && hasRow) {
                placedCol = c.start;
                placedRow = r.start;
                occ.ensureRows(placedRow + rowSpan);
                occ.mark(placedRow, placedCol, rowSpan, colSpan);
            } else if (hasRow) {
                rc = Grid.findFirstFit(occ, r.start, 0, rowSpan, colSpan);
                placedRow = rc[0];
                placedCol = rc[1];
                occ.mark(placedRow, placedCol, rowSpan, colSpan);
            } else if (hasCol) {
                rc = Grid.findFirstFitAtCol(occ, 0, c.start, rowSpan, colSpan);
                placedRow = rc[0];
                placedCol = rc[1];
                occ.mark(placedRow, placedCol, rowSpan, colSpan);
            } else {
                rc = Grid.findFirstFit(occ, cursorRow, cursorCol, rowSpan, colSpan);
                placedRow = rc[0];
                placedCol = rc[1];
                occ.mark(placedRow, placedCol, rowSpan, colSpan);
                cursorRow = placedRow;
                cursorCol = placedCol + colSpan;
                if (cursorCol >= colCount) {
                    ++cursorRow;
                    cursorCol = 0;
                }
            }
            placements.add(new Placement(placedCol, placedRow, colSpan, rowSpan));
        }
        int requiredRows = 1;
        for (Placement p : placements) {
            requiredRows = Math.max(requiredRows, p.row + p.rowSpan);
        }
        if (rows.isEmpty()) {
            rows = Grid.makeAutoTracks(requiredRows);
        } else {
            while (rows.size() < requiredRows) {
                rows.add(Track.auto());
            }
        }
        double[] dArray = Grid.computeTrackSizes(cols, placements, flow, gaps.colGap, availableSize.width(), true, null, 0);
        double[] rowH = Grid.computeTrackSizes(rows, placements, flow, gaps.rowGap, availableSize.height(), false, dArray, gaps.colGap);
        return new GridLayout(flow, placements, cols, rows, dArray, rowH, gaps);
    }

    private static int spanRequirement(SpanSpec spec) {
        return spec.start < 0 ? spec.span : 0;
    }

    private static SpanSpec parseSpanSpec(String raw) {
        if (raw == null) {
            return SpanSpec.auto();
        }
        if ((raw = raw.trim().toLowerCase(Locale.ROOT)).isBlank() || "unset".equals(raw) || "auto".equals(raw)) {
            return SpanSpec.auto();
        }
        String[] parts = raw.split("/");
        String a = parts[0].trim();
        Integer start = null;
        Integer span = null;
        if (a.startsWith("span")) {
            span = Grid.parsePositiveInt(a.substring(4).trim(), 1);
        } else {
            start = "auto".equals(a) ? Integer.valueOf(-1) : (a.matches("^\\d+$") ? Integer.valueOf(Math.max(1, Integer.parseInt(a)) - 1) : Integer.valueOf(-1));
        }
        if (parts.length >= 2) {
            String b = parts[1].trim();
            if (b.startsWith("span")) {
                span = Grid.parsePositiveInt(b.substring(4).trim(), 1);
            } else if (b.matches("^\\d+$") && start != null && start >= 0) {
                int endLine = Integer.parseInt(b);
                int startLine = start + 1;
                span = Math.max(1, endLine - startLine);
            }
        }
        int s = start == null ? -1 : start;
        int sp = span == null ? 1 : Math.max(1, span);
        return new SpanSpec(s, sp);
    }

    private static int parsePositiveInt(String s, int fallback) {
        if (s == null) {
            return fallback;
        }
        if ((s = s.trim()).isEmpty()) {
            return fallback;
        }
        StringBuilder num = new StringBuilder();
        for (char c : s.toCharArray()) {
            if (!Character.isDigit(c)) break;
            num.append(c);
        }
        if (num.isEmpty()) {
            return fallback;
        }
        try {
            int v = Integer.parseInt(num.toString());
            return v > 0 ? v : fallback;
        }
        catch (NumberFormatException ignored) {
            return fallback;
        }
    }

    private static double[] computeTrackSizes(List<Track> tracks, List<Placement> placements, List<Element> flow, int gap, double availableSpace, boolean columnAxis, double[] resolvedColumns, int columnGap) {
        int count = tracks.size();
        double[] resolved = new double[count];
        boolean[] growable = new boolean[count];
        double totalFr = 0.0;
        for (int i = 0; i < count; ++i) {
            Track track = tracks.get(i);
            resolved[i] = Grid.minimumTrackSize(track);
            if (Grid.canGrowForItemContribution(track)) {
                growable[i] = true;
            }
            totalFr += Grid.frWeight(track);
        }
        for (int idx = 0; idx < flow.size(); ++idx) {
            int i;
            Element el = flow.get(idx);
            Placement p = placements.get(idx);
            int start = columnAxis ? p.col : p.row;
            int span = Math.max(1, columnAxis ? p.colSpan : p.rowSpan);
            int internalGaps = Math.max(0, span - 1) * gap;
            Size naturalSize = columnAxis || resolvedColumns == null ? Size.natural(el) : Grid.measureAtGridAreaWidth(el, p, resolvedColumns, columnGap);
            Box itemBox = Box.of(el);
            double outerContribution = columnAxis ? naturalSize.width() + itemBox.getMarginHorizontal() : naturalSize.height() + itemBox.getMarginVertical();
            double desired = Math.max(0.0, outerContribution - (double)internalGaps);
            double current = 0.0;
            int growableCount = 0;
            for (int i2 = start; i2 < start + span && i2 < count; ++i2) {
                current += resolved[i2];
                if (!growable[i2]) continue;
                ++growableCount;
            }
            if (desired <= current || growableCount <= 0) continue;
            double extra = desired - current;
            double spanFr = 0.0;
            for (i = start; i < start + span && i < count; ++i) {
                spanFr += Grid.frWeight(tracks.get(i));
            }
            for (i = start; i < start + span && i < count; ++i) {
                if (!growable[i]) continue;
                double weight = Grid.frWeight(tracks.get(i));
                double add = spanFr > 0.0 && weight > 0.0 ? extra * (weight / spanFr) : extra / (double)growableCount;
                resolved[i] = Grid.applyGrowthCap(tracks.get(i), resolved[i] + Math.max(0.0, add));
            }
        }
        double base = Grid.sum(resolved);
        double availableTracks = Math.max(0.0, availableSpace - (double)gap * (double)Math.max(0, count - 1));
        if (availableTracks > base && totalFr > 0.0) {
            double remaining = availableTracks - base;
            Grid.distributeWeightedGrowth(tracks, resolved, remaining, totalFr);
        }
        return resolved;
    }

    private static Size measureAtGridAreaWidth(Element element, Placement placement, double[] resolvedColumns, int columnGap) {
        double areaWidth = Grid.spanSum(resolvedColumns, placement.col, placement.colSpan) + (double)Math.max(0, placement.colSpan - 1) * (double)columnGap;
        Box box = Box.of(element);
        double contentWidth = areaWidth - box.getBorderHorizontal() - box.getPaddingHorizontal();
        return Size.naturalAtContentWidth(element, Math.max(0.0, contentWidth));
    }

    private static void distributeWeightedGrowth(List<Track> tracks, double[] resolved, double remaining, double totalFr) {
        if (remaining <= 0.0 || totalFr <= 0.0) {
            return;
        }
        double assigned = 0.0;
        int lastFlexible = -1;
        for (int i = 0; i < tracks.size(); ++i) {
            double weight = Grid.frWeight(tracks.get(i));
            if (weight <= 0.0) continue;
            lastFlexible = i;
            double add = remaining * (weight / totalFr);
            resolved[i] = Grid.applyGrowthCap(tracks.get(i), resolved[i] + Math.max(0.0, add));
            assigned += Math.max(0.0, add);
        }
        double leftover = remaining - assigned;
        if (leftover > 1.0E-6 && lastFlexible >= 0) {
            resolved[lastFlexible] = Grid.applyGrowthCap(tracks.get(lastFlexible), resolved[lastFlexible] + leftover);
        }
    }

    private static int minimumTrackSize(Track track) {
        return switch (track.type) {
            default -> throw new IncompatibleClassChangeError();
            case TrackType.FIXED -> track.px;
            case TrackType.AUTO, TrackType.FR -> 0;
            case TrackType.MINMAX -> Grid.minimumTrackSize(track.minTrack);
        };
    }

    private static boolean canGrow(Track track) {
        return switch (track.type) {
            default -> throw new IncompatibleClassChangeError();
            case TrackType.AUTO -> true;
            case TrackType.FR -> {
                if (track.fr > 0.0) {
                    yield true;
                }
                yield false;
            }
            case TrackType.FIXED -> false;
            case TrackType.MINMAX -> Grid.canGrowBeyondMinimum(track.maxTrack);
        };
    }

    private static boolean canGrowForItemContribution(Track track) {
        return switch (track.type) {
            default -> throw new IncompatibleClassChangeError();
            case TrackType.AUTO -> true;
            case TrackType.FR -> {
                if (track.fr > 0.0) {
                    yield true;
                }
                yield false;
            }
            case TrackType.FIXED -> false;
            case TrackType.MINMAX -> track.minTrack != null && (track.minTrack.type != TrackType.FIXED || track.minTrack.px != 0) && Grid.canGrow(track.minTrack);
        };
    }

    private static boolean canGrowBeyondMinimum(Track track) {
        return switch (track.type) {
            default -> throw new IncompatibleClassChangeError();
            case TrackType.AUTO, TrackType.FR -> true;
            case TrackType.FIXED -> false;
            case TrackType.MINMAX -> Grid.canGrowBeyondMinimum(track.maxTrack);
        };
    }

    private static double frWeight(Track track) {
        return switch (track.type) {
            case TrackType.FR -> Math.max(0.0, track.fr);
            case TrackType.MINMAX -> Grid.frWeight(track.maxTrack);
            default -> 0.0;
        };
    }

    private static double applyGrowthCap(Track track, double candidate) {
        return switch (track.type) {
            default -> throw new IncompatibleClassChangeError();
            case TrackType.FIXED -> track.px;
            case TrackType.AUTO, TrackType.FR -> Math.max(0.0, candidate);
            case TrackType.MINMAX -> {
                double min = Grid.minimumTrackSize(track.minTrack);
                double capped = Math.max(min, candidate);
                if (track.maxTrack != null && track.maxTrack.type == TrackType.FIXED) {
                    capped = Math.min(capped, (double)track.maxTrack.px);
                }
                yield capped;
            }
        };
    }

    private static double computeAlignmentOffset(String containerRaw, String selfRaw, double cellExtent, double itemExtent) {
        Align container = Align.normalize(containerRaw, Align.START);
        Align self = Align.normalize(selfRaw, container);
        return switch (self) {
            default -> throw new IncompatibleClassChangeError();
            case Align.CENTER -> (cellExtent - itemExtent) / 2.0;
            case Align.END -> cellExtent - itemExtent;
            case Align.STRETCH, Align.START -> 0.0;
        };
    }

    private static List<Element> collectFlowChildren(List<Element> siblings) {
        ArrayList<Element> flow = new ArrayList<Element>();
        for (Element c : siblings) {
            Style cs = c.getComputedStyle();
            if ("none".equals(cs.display) || "absolute".equals(cs.position) || "fixed".equals(cs.position)) continue;
            flow.add(c);
        }
        return flow;
    }

    private static Gaps parseGaps(Style s) {
        int b;
        int row = s.rowGap != null && !"unset".equals(s.rowGap) ? Size.parse(s.rowGap) : -1;
        int col = s.columnGap != null && !"unset".equals(s.columnGap) ? Size.parse(s.columnGap) : -1;
        String gap = s.gap == null ? "0px" : s.gap.trim();
        String[] parts = gap.split("\\s+");
        int a = parts.length > 0 ? Size.parse(parts[0]) : 0;
        int n = b = parts.length > 1 ? Size.parse(parts[1]) : a;
        if (row < 0) {
            row = Math.max(0, a);
        }
        if (col < 0) {
            col = Math.max(0, b);
        }
        return new Gaps(row, col);
    }

    private static ParsedTracks parseTracks(String raw, int fallbackCount, double availableSpace, int gap) {
        String string = raw = raw == null ? "unset" : raw.trim().toLowerCase(Locale.ROOT);
        if (raw.isBlank() || "unset".equals(raw)) {
            return new ParsedTracks(Grid.makeAutoTracks(Math.max(1, fallbackCount)));
        }
        if (raw.matches("^\\d+$")) {
            int n = Integer.parseInt(raw);
            return new ParsedTracks(Grid.makeAutoTracks(Math.max(1, n)));
        }
        List<String> tokens = Grid.splitTopLevelWhitespace(raw);
        ArrayList<Track> out = new ArrayList<Track>();
        for (String token : tokens) {
            Grid.expandTrackToken(token, out, availableSpace, gap);
        }
        if (out.isEmpty()) {
            return new ParsedTracks(Grid.makeAutoTracks(Math.max(1, fallbackCount)));
        }
        return new ParsedTracks(out);
    }

    private static void expandTrackToken(String token, List<Track> out, double availableSpace, int gap) {
        String inner;
        List<String> args;
        if (token == null) {
            return;
        }
        String value = token.trim();
        if (value.isEmpty()) {
            return;
        }
        if (value.startsWith("repeat(") && value.endsWith(")") && (args = Background.splitTopLevelComma(inner = value.substring(7, value.length() - 1).trim())).size() == 2) {
            String repeatCount = args.get(0).trim();
            List<String> repeated = Grid.splitTopLevelWhitespace(args.get(1));
            if ("auto-fill".equals(repeatCount) || "auto-fit".equals(repeatCount)) {
                int resolved = Grid.resolveAutoRepeatCount(repeated, availableSpace, gap);
                for (int i = 0; i < resolved; ++i) {
                    for (String repeatedToken : repeated) {
                        Grid.expandTrackToken(repeatedToken, out, availableSpace, gap);
                    }
                }
                return;
            }
            Integer count = Grid.parsePositiveIntObject(repeatCount);
            if (count != null) {
                for (int i = 0; i < count; ++i) {
                    for (String repeatedToken : repeated) {
                        Grid.expandTrackToken(repeatedToken, out, availableSpace, gap);
                    }
                }
                return;
            }
        }
        out.add(Grid.parseSingleTrack(value));
    }

    private static Track parseSingleTrack(String token) {
        if ("auto".equals(token)) {
            return Track.auto();
        }
        if (token.startsWith("minmax(") && token.endsWith(")")) {
            String inner = token.substring(7, token.length() - 1).trim();
            List<String> args = Background.splitTopLevelComma(inner);
            if (args.size() == 2) {
                Track minTrack = Grid.parseSingleTrack(args.get(0).trim());
                Track maxTrack = Grid.parseSingleTrack(args.get(1).trim());
                return Track.minmax(minTrack, maxTrack);
            }
            return Track.auto();
        }
        if (token.endsWith("fr")) {
            Double number = Size.parseNumber(token);
            return Track.fr(number == null ? 1.0 : number);
        }
        int px = Size.parse(token);
        if (px >= 0) {
            return Track.fixed(px);
        }
        return Track.auto();
    }

    private static int resolveAutoRepeatCount(List<String> repeated, double availableSpace, int gap) {
        if (repeated == null || repeated.isEmpty()) {
            return 1;
        }
        int baseSize = 0;
        for (String token : repeated) {
            Track track = Grid.parseSingleTrack(token);
            baseSize += (switch (track.type) {
                case TrackType.FIXED -> track.px;
                case TrackType.MINMAX -> Grid.minimumTrackSize(track);
                default -> 0;
            });
        }
        if (baseSize <= 0 || availableSpace <= 0.0) {
            return 1;
        }
        return Math.max(1, (int)Math.floor((availableSpace + (double)gap) / (double)(baseSize + gap)));
    }

    private static Integer parsePositiveIntObject(String raw) {
        if (raw == null) {
            return null;
        }
        if (!(raw = raw.trim()).matches("^\\d+$")) {
            return null;
        }
        try {
            int value = Integer.parseInt(raw);
            return value > 0 ? Integer.valueOf(value) : null;
        }
        catch (NumberFormatException ignored) {
            return null;
        }
    }

    private static List<Track> makeAutoTracks(int n) {
        ArrayList<Track> out = new ArrayList<Track>();
        for (int i = 0; i < n; ++i) {
            out.add(Track.auto());
        }
        return out;
    }

    private static Size resolveAvailableTrackSpace(Element gridContainer) {
        Style style = gridContainer.getComputedStyle();
        Box box = Box.of(gridContainer);
        boolean borderBox = box.isBorderBox();
        double widthBasis = Size.getScaleWidth(gridContainer);
        double width = Grid.resolveAvailableAxisSize(style.width, widthBasis, box.getBorderHorizontal() + box.getPaddingHorizontal(), borderBox);
        Double explicitParentHeight = Size.getExplicitContainingBlockHeight(gridContainer);
        double heightBasis = explicitParentHeight != null ? explicitParentHeight : 0.0;
        double height = Grid.resolveAvailableAxisSize(style.height, heightBasis, box.getBorderVertical() + box.getPaddingVertical(), borderBox);
        return new Size(width, height);
    }

    private static double resolveAvailableAxisSize(String raw, double percentBasis, double boxExtent, boolean borderBox) {
        Double parsed = Size.parseNumber(raw);
        if (parsed == null) {
            return Math.max(0.0, percentBasis);
        }
        if (Size.isPercent(raw) && percentBasis <= 0.0) {
            return 0.0;
        }
        double resolved = Size.resolveLength(raw, percentBasis, parsed);
        return Math.max(0.0, borderBox ? resolved - boxExtent : resolved);
    }

    private static List<String> splitTopLevelWhitespace(String value) {
        return Layout.splitTopLevelWhitespace(value);
    }

    private static int[] findFirstFit(Occupancy occ, int startRow, int startCol, int rowSpan, int colSpan) {
        int row = Math.max(0, startRow);
        int col0 = Math.max(0, startCol);
        while (true) {
            occ.ensureRows(row + rowSpan);
            for (int col = col0; col <= occ.cols - colSpan; ++col) {
                if (!occ.fits(row, col, rowSpan, colSpan)) continue;
                return new int[]{row, col};
            }
            ++row;
            col0 = 0;
        }
    }

    private static int[] findFirstFitAtCol(Occupancy occ, int startRow, int fixedCol, int rowSpan, int colSpan) {
        int row = Math.max(0, startRow);
        int col = Math.max(0, fixedCol);
        while (!occ.fits(row, col, rowSpan, colSpan)) {
            ++row;
        }
        return new int[]{row, col};
    }

    private static double sum(double[] arr) {
        double s = 0.0;
        for (double v : arr) {
            s += v;
        }
        return s;
    }

    private static double prefixSum(double[] arr, int count) {
        double s = 0.0;
        for (int i = 0; i < count && i < arr.length; ++i) {
            s += arr[i];
        }
        return s;
    }

    private static double spanSum(double[] arr, int start, int span) {
        double s = 0.0;
        int end = Math.min(arr.length, start + span);
        for (int i = Math.max(0, start); i < end; ++i) {
            s += arr[i];
        }
        return s;
    }

    private record GridLayout(List<Element> flow, List<Placement> placements, List<Track> cols, List<Track> rows, double[] colW, double[] rowH, Gaps gaps) {
    }

    private record Placement(int col, int row, int colSpan, int rowSpan) {
    }

    private record Gaps(int rowGap, int colGap) {
    }

    private record Track(TrackType type, int px, double fr, Track minTrack, Track maxTrack) {
        static Track fixed(int px) {
            return new Track(TrackType.FIXED, Math.max(0, px), 0.0, null, null);
        }

        static Track auto() {
            return new Track(TrackType.AUTO, 0, 0.0, null, null);
        }

        static Track fr(double fr) {
            return new Track(TrackType.FR, 0, Math.max(0.0, fr), null, null);
        }

        static Track minmax(Track minTrack, Track maxTrack) {
            return new Track(TrackType.MINMAX, 0, 0.0, minTrack, maxTrack);
        }
    }

    private static enum TrackType {
        FIXED,
        AUTO,
        FR,
        MINMAX;

    }

    private record ParsedTracks(List<Track> tracks) {
    }

    private record SpanSpec(int start, int span) {
        static SpanSpec auto() {
            return new SpanSpec(-1, 1);
        }
    }

    private record ItemSpec(SpanSpec col, SpanSpec row, Element el) {
    }

    private static final class Occupancy {
        private final int cols;
        private final List<boolean[]> rows = new ArrayList<boolean[]>();

        Occupancy(int cols) {
            this.cols = Math.max(1, cols);
        }

        Occupancy resize(int newCols) {
            Occupancy n = new Occupancy(newCols);
            for (boolean[] r : this.rows) {
                boolean[] nr = new boolean[newCols];
                int copy = Math.min(r.length, nr.length);
                System.arraycopy(r, 0, nr, 0, copy);
                n.rows.add(nr);
            }
            return n;
        }

        void ensureRows(int count) {
            while (this.rows.size() < count) {
                this.rows.add(new boolean[this.cols]);
            }
        }

        boolean fits(int row, int col, int rowSpan, int colSpan) {
            if (col < 0 || row < 0) {
                return false;
            }
            if (col + colSpan > this.cols) {
                return false;
            }
            this.ensureRows(row + rowSpan);
            for (int r = row; r < row + rowSpan; ++r) {
                boolean[] rr = this.rows.get(r);
                for (int c = col; c < col + colSpan; ++c) {
                    if (!rr[c]) continue;
                    return false;
                }
            }
            return true;
        }

        void mark(int row, int col, int rowSpan, int colSpan) {
            this.ensureRows(row + rowSpan);
            int c0 = Math.max(0, col);
            int c1 = Math.min(this.cols, col + colSpan);
            for (int r = row; r < row + rowSpan; ++r) {
                boolean[] rr = this.rows.get(r);
                for (int c = c0; c < c1; ++c) {
                    rr[c] = true;
                }
            }
        }
    }
}

