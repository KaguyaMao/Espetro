/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.editor.ore.canvas;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public final class OreFlexInsertionResolver {
    private static final double LINE_TOLERANCE = 2.0;

    public Insertion resolve(String direction, String wrap, List<Item> candidates, double x, double y) {
        List<Item> items;
        boolean row = !"column".equals(direction) && !"column-reverse".equals(direction);
        boolean reverse = "row-reverse".equals(direction) || "column-reverse".equals(direction);
        List<Item> list = items = candidates == null ? List.of() : candidates.stream().filter(item -> item != null && item.id() != null && item.bounds() != null && !item.absolute()).toList();
        if (items.isEmpty()) {
            return new Insertion(null, row, row ? x : y, row ? y : x, 0.0);
        }
        List<List<Item>> lines = this.lines(items, row);
        double crossPointer = row ? y : x;
        List<Item> line = lines.stream().min(Comparator.comparingDouble(value -> Math.abs(crossPointer - this.lineCrossCenter((List<Item>)value, row)))).orElse(items);
        line = new ArrayList<Item>(line);
        line.sort(Comparator.comparingDouble(item -> this.mainCenter((Item)item, row)));
        if (reverse) {
            line.sort(Comparator.comparingDouble(item -> this.mainCenter((Item)item, row)).reversed());
        }
        double pointer = row ? x : y;
        for (Item item2 : line) {
            if (!(pointer <= this.mainCenter(item2, row))) continue;
            return this.insertionBefore(item2, row, reverse);
        }
        return this.insertionAfter(line.get(line.size() - 1), row, reverse);
    }

    private List<List<Item>> lines(List<Item> items, boolean row) {
        ArrayList<Item> ordered = new ArrayList<Item>(items);
        ordered.sort(Comparator.comparingDouble(item -> this.crossCenter((Item)item, row)));
        ArrayList<List<Item>> lines = new ArrayList<List<Item>>();
        for (Item item2 : ordered) {
            if (lines.isEmpty() || Math.abs(this.lineCrossCenter((List)lines.get(lines.size() - 1), row) - this.crossCenter(item2, row)) > 2.0) {
                lines.add(new ArrayList());
            }
            ((List)lines.get(lines.size() - 1)).add(item2);
        }
        return lines;
    }

    private Insertion insertionBefore(Item item, boolean row, boolean reverse) {
        Bounds bounds = item.bounds();
        double coordinate = row ? (reverse ? bounds.right() : bounds.left()) : (reverse ? bounds.bottom() : bounds.top());
        return new Insertion(item.id(), row, coordinate, row ? bounds.top() : bounds.left(), row ? bounds.height() : bounds.width());
    }

    private Insertion insertionAfter(Item item, boolean row, boolean reverse) {
        Bounds bounds = item.bounds();
        double coordinate = row ? (reverse ? bounds.left() : bounds.right()) : (reverse ? bounds.top() : bounds.bottom());
        return new Insertion(null, row, coordinate, row ? bounds.top() : bounds.left(), row ? bounds.height() : bounds.width());
    }

    private double mainCenter(Item item, boolean row) {
        return row ? item.bounds().centerX() : item.bounds().centerY();
    }

    private double crossCenter(Item item, boolean row) {
        return row ? item.bounds().centerY() : item.bounds().centerX();
    }

    private double lineCrossCenter(List<Item> line, boolean row) {
        return line.stream().mapToDouble(item -> this.crossCenter((Item)item, row)).average().orElse(0.0);
    }

    public record Insertion(UUID beforeId, boolean row, double coordinate, double crossStart, double crossSize) {
    }

    public record Item(UUID id, Bounds bounds, boolean absolute) {
    }

    public record Bounds(double left, double top, double width, double height) {
        public double right() {
            return this.left + this.width;
        }

        public double bottom() {
            return this.top + this.height;
        }

        public double centerX() {
            return this.left + this.width / 2.0;
        }

        public double centerY() {
            return this.top + this.height / 2.0;
        }
    }
}

