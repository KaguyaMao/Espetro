/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.behavior.richtext;

import com.sighs.apricityui.behavior.SelectionUnits;
import com.sighs.apricityui.dom.CommentNode;
import com.sighs.apricityui.dom.TextNode;
import com.sighs.apricityui.element.AbstractText;
import com.sighs.apricityui.init.Element;
import com.sighs.apricityui.init.Node;
import com.sighs.apricityui.layout.Box;
import com.sighs.apricityui.layout.Layout;
import com.sighs.apricityui.layout.NormalFlow;
import com.sighs.apricityui.layout.Position;
import com.sighs.apricityui.layout.Size;
import com.sighs.apricityui.render.Rect;
import com.sighs.apricityui.style.Text;
import com.sighs.apricityui.util.TextMetrics;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class RichTextNavigation {
    private RichTextNavigation() {
    }

    public static List<VisualLine> linesOf(Element unit) {
        ArrayList<VisualLine> result = new ArrayList<VisualLine>();
        if (unit == null) {
            return result;
        }
        List<NormalFlow.TextRunLayout> runs = NormalFlow.computeTextRuns(unit);
        ArrayList<RunSegment> objectRows = new ArrayList<RunSegment>();
        RichTextNavigation.collectObjectSegments(unit, unit, objectRows);
        if (runs.isEmpty() && objectRows.isEmpty()) {
            return result;
        }
        Rect rect = Rect.of(unit);
        Position contentPos = rect.getContentPosition();
        boolean alignDirect = RichTextNavigation.shouldAlignDirect(unit);
        double contentWidth = alignDirect ? Box.of(unit).innerSize().width() : 0.0;
        ArrayList<RunSegment> rows = new ArrayList<RunSegment>();
        for (NormalFlow.TextRunLayout run : runs) {
            if (run == null || run.text() == null || run.lines() == null) continue;
            Text runText = run.text();
            double lineHeight = runText.lineHeight;
            Node node = run.node() != null ? run.node() : run.owner();
            int runBase = SelectionUnits.baseOffsetOfDescendant(unit, node);
            for (int i = 0; i < run.lines().size(); ++i) {
                String line = run.lines().get(i);
                double lineWidth = Text.measureLine(runText, line == null ? "" : line);
                double alignOffset = alignDirect && run.owner() == unit ? TextMetrics.computeAlignedX(runText, contentWidth, lineWidth, i == 0) : 0.0;
                double y0 = contentPos.y + run.y() + (double)i * lineHeight;
                double x0 = contentPos.x + (i == 0 ? run.x() : 0.0) + alignOffset - unit.scrollLeft;
                int lineStartNorm = runBase + SelectionUnits.runLineStart(run, i);
                rows.add(new RunSegment(runText, line == null ? "" : line, lineStartNorm, x0, y0, lineWidth));
            }
        }
        rows.sort(Comparator.comparingDouble(RunSegment::y0).thenComparingDouble(RunSegment::x0));
        for (int i = 0; i < rows.size(); ++i) {
            RunSegment next;
            int j;
            RunSegment first = (RunSegment)rows.get(i);
            ArrayList<RunSegment> group = new ArrayList<RunSegment>();
            group.add(first);
            int startNorm = first.startNorm();
            int endNorm = first.startNorm() + first.content().length();
            double y0 = first.y0();
            double lineHeight = first.text().lineHeight;
            for (j = i + 1; j < rows.size() && !(Math.abs(y0 - (next = (RunSegment)rows.get(j)).y0()) > lineHeight * 0.5); ++j) {
                group.add(next);
                startNorm = Math.min(startNorm, next.startNorm());
                endNorm = Math.max(endNorm, next.startNorm() + next.content().length());
            }
            group.sort(Comparator.comparingDouble(RunSegment::x0));
            result.add(new VisualLine(startNorm, endNorm, y0, lineHeight, group));
            i = j - 1;
        }
        for (RunSegment object : objectRows) {
            int targetIndex = -1;
            for (int i = 0; i < result.size(); ++i) {
                VisualLine line = (VisualLine)result.get(i);
                if (object.startNorm() < line.startNorm() || object.startNorm() > line.endNorm()) continue;
                targetIndex = i;
                break;
            }
            if (targetIndex < 0) {
                result.add(new VisualLine(object.startNorm(), object.startNorm() + 1, object.y0(), object.text().lineHeight, List.of(object)));
                continue;
            }
            VisualLine line = (VisualLine)result.get(targetIndex);
            ArrayList<RunSegment> merged = new ArrayList<RunSegment>(line.segments());
            merged.add(object);
            merged.sort(Comparator.comparingDouble(RunSegment::x0));
            int startNorm = Math.min(line.startNorm(), object.startNorm());
            int endNorm = Math.max(line.endNorm(), object.startNorm() + 1);
            result.set(targetIndex, new VisualLine(startNorm, endNorm, line.y0(), line.lineHeight(), merged));
        }
        return result;
    }

    public static VisualLine locateLine(Element unit, int normOffset) {
        List<VisualLine> lines = RichTextNavigation.linesOf(unit);
        if (lines.isEmpty()) {
            return null;
        }
        for (VisualLine line : lines) {
            if (normOffset < line.startNorm() || normOffset > line.endNorm()) continue;
            return line;
        }
        VisualLine best = null;
        int bestDistance = Integer.MAX_VALUE;
        for (VisualLine line : lines) {
            int distance = normOffset < line.startNorm() ? line.startNorm() - normOffset : normOffset - line.endNorm();
            if (distance >= bestDistance) continue;
            bestDistance = distance;
            best = line;
        }
        return best;
    }

    public static int lineStartOffset(Element unit, int normOffset) {
        VisualLine line = RichTextNavigation.locateLine(unit, normOffset);
        return line == null ? 0 : line.startNorm();
    }

    public static int lineEndOffset(Element unit, int normOffset) {
        VisualLine line = RichTextNavigation.locateLine(unit, normOffset);
        return line == null ? normOffset : line.endNorm();
    }

    public static int lineMoveOffset(Element unit, int normOffset, int delta) {
        int target;
        if (delta == 0) {
            return normOffset;
        }
        List<VisualLine> lines = RichTextNavigation.linesOf(unit);
        if (lines.isEmpty()) {
            return normOffset;
        }
        int current = -1;
        for (int i = 0; i < lines.size(); ++i) {
            if (normOffset < lines.get(i).startNorm() || normOffset > lines.get(i).endNorm()) continue;
            current = i;
            break;
        }
        if (current < 0) {
            VisualLine nearest = RichTextNavigation.locateLine(unit, normOffset);
            int n = current = nearest == null ? 0 : lines.indexOf(nearest);
        }
        if ((target = Math.max(0, Math.min(current + delta, lines.size() - 1))) == current) {
            return normOffset;
        }
        return lines.get(target).offsetForX(lines.get(current).xForOffset(normOffset));
    }

    public static Caret caretPosition(Element unit, int normOffset) {
        VisualLine line = RichTextNavigation.locateLine(unit, normOffset);
        if (line == null) {
            return RichTextNavigation.measuredCaret(unit, normOffset);
        }
        return new Caret(line.xForOffset(normOffset), line.y0(), line.lineHeight());
    }

    private static Caret measuredCaret(Element unit, int normOffset) {
        if (unit == null) {
            return new Caret(0.0, 0.0, 16.0);
        }
        Element target = unit;
        Rect rect = target.getRenderer().getCommittedRect();
        if (rect == null && unit.parentElement != null) {
            target = unit.parentElement;
            rect = target.getRenderer().getCommittedRect();
        }
        if (rect == null) {
            return new Caret(0.0, 0.0, 16.0);
        }
        String flat = SelectionUnits.flattenedSelectableText(unit);
        String prefix = flat == null ? "" : flat.substring(0, Math.min(normOffset, flat.length()));
        double x = rect.position.x + Size.measureText(unit, prefix);
        double y = rect.position.y;
        double h = 16.0;
        return new Caret(x, y, h);
    }

    private static boolean shouldAlignDirect(Element unit) {
        if (unit == null) {
            return false;
        }
        boolean hasText = false;
        for (Node child : unit.getRenderChildNodes()) {
            Element element;
            if (child instanceof CommentNode) continue;
            if (child instanceof TextNode) {
                TextNode textNode = (TextNode)child;
                hasText |= textNode.getTextContent() != null && !textNode.getTextContent().isEmpty();
                continue;
            }
            if (child instanceof Element && !Layout.isInFlow((element = (Element)child).getComputedStyle())) continue;
            return false;
        }
        return hasText;
    }

    private static void collectObjectSegments(Element unit, Element current, List<RunSegment> out) {
        for (Node child : current.getRenderChildNodes()) {
            if (!(child instanceof Element)) continue;
            Element childElement = (Element)child;
            if (SelectionUnits.isAtomicObject(childElement)) {
                Element.DOMRect rect = childElement.getBoundingClientRect();
                double x0 = rect != null ? rect.x : 0.0;
                double y0 = rect != null ? rect.y : 0.0;
                double width = rect != null && rect.width > 0.0 ? rect.width : 0.0;
                int startNorm = SelectionUnits.baseOffsetOfDescendant(unit, childElement);
                out.add(new RunSegment(Text.of(unit), "", startNorm, x0, y0, width));
                continue;
            }
            if (childElement instanceof AbstractText || SelectionUnits.isLineBreak(childElement)) continue;
            RichTextNavigation.collectObjectSegments(unit, childElement, out);
        }
    }

    public record RunSegment(Text text, String content, int startNorm, double x0, double y0, double width) {
    }

    public record VisualLine(int startNorm, int endNorm, double y0, double lineHeight, List<RunSegment> segments) {
        public double xForOffset(int normOffset) {
            double x = 0.0;
            for (RunSegment segment : this.segments) {
                int segmentEnd = segment.startNorm() + segment.content().length();
                if (segment.content().isEmpty()) {
                    segmentEnd = segment.startNorm() + 1;
                }
                if (normOffset <= segment.startNorm()) break;
                if (normOffset >= segmentEnd) {
                    x += segment.width();
                    continue;
                }
                if (segment.content().isEmpty()) break;
                x += Text.measureLine(segment.text(), segment.content().substring(0, normOffset - segment.startNorm()));
                break;
            }
            return this.segments.isEmpty() ? 0.0 : this.segments.get(0).x0() + x;
        }

        public int offsetForX(double x) {
            if (this.segments.isEmpty()) {
                return this.startNorm;
            }
            double x0 = this.segments.get(0).x0();
            double relative = x - x0;
            double acc = 0.0;
            for (RunSegment segment : this.segments) {
                if (segment.content().isEmpty()) {
                    if (relative <= acc + segment.width()) {
                        return segment.startNorm();
                    }
                    acc += segment.width();
                    continue;
                }
                double segmentWidth = segment.width();
                if (relative <= acc + segmentWidth) {
                    double local = relative - acc;
                    int best = 0;
                    double bestDistance = Double.MAX_VALUE;
                    double current = 0.0;
                    for (int i = 0; i <= segment.content().length(); ++i) {
                        double distance = Math.abs(current - local);
                        if (distance < bestDistance) {
                            bestDistance = distance;
                            best = i;
                        }
                        if (i >= segment.content().length()) continue;
                        current += Text.measureLine(segment.text(), segment.content().substring(i, i + 1));
                    }
                    return segment.startNorm() + best;
                }
                acc += segmentWidth;
            }
            return this.endNorm;
        }
    }

    public record Caret(double x, double y, double lineHeight) {
    }
}

