/*
 * Decompiled with CFR 0.152.
 */
package net.minecraftforge.common.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public class TablePrinter<T> {
    private final List<Header<T>> headers = new ArrayList<Header<T>>();
    private final List<T> rows = new ArrayList<T>();

    public TablePrinter<T> header(String name, Function<T, String> supplier) {
        return this.header(name, supplier, false);
    }

    public TablePrinter<T> header(String name, Function<T, String> supplier, boolean right) {
        this.headers.add(new Header<T>(name, supplier, right));
        return this;
    }

    public void clearRows() {
        this.rows.clear();
    }

    public TablePrinter<T> add(T row) {
        this.rows.add(row);
        return this;
    }

    public TablePrinter<T> add(T row, T ... more) {
        this.add(row);
        for (T t : more) {
            this.add(t);
        }
        return this;
    }

    public TablePrinter<T> add(Collection<? extends T> rows) {
        rows.forEach(this::add);
        return this;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        this.build(buf);
        return buf.toString();
    }

    public void build(StringBuilder buf) {
        int x;
        int x2;
        int[] count = new int[this.headers.size()];
        int width = 1;
        for (int x3 = 0; x3 < count.length; ++x3) {
            count[x3] = this.headers.get((int)x3).name.length() + 2;
        }
        for (T row : this.rows) {
            for (x2 = 0; x2 < count.length; ++x2) {
                count[x2] = Math.max(count[x2], this.headers.get((int)x2).supplier.apply(row).length() + 2);
            }
        }
        for (x = 0; x < count.length; ++x) {
            width += count[x] + 3;
        }
        TablePrinter.line(buf, width);
        for (x = 0; x < count.length; ++x) {
            Header<T> header = this.headers.get(x);
            int left = (count[x] - header.name.length()) / 2;
            int right = count[x] - header.name.length() - left;
            buf.append('|').append(' ');
            TablePrinter.pad(buf, left);
            buf.append(header.name);
            TablePrinter.pad(buf, right);
            buf.append(' ');
        }
        buf.append('|').append('\n');
        TablePrinter.line(buf, width);
        for (T row : this.rows) {
            for (x2 = 0; x2 < count.length; ++x2) {
                Header<T> header = this.headers.get(x2);
                String data = header.supplier.apply(row);
                int padding = count[x2] - data.length();
                buf.append('|').append(' ');
                if (header.right) {
                    TablePrinter.pad(buf, padding);
                }
                buf.append(data);
                if (!header.right) {
                    TablePrinter.pad(buf, padding);
                }
                buf.append(' ');
            }
            buf.append('|').append('\n');
        }
        TablePrinter.line(buf, width);
    }

    private static void line(StringBuilder buf, int size) {
        buf.append('|');
        for (int x = 0; x < size - 2; ++x) {
            buf.append('-');
        }
        buf.append('|').append('\n');
    }

    private static void pad(StringBuilder buf, int size) {
        for (int y = 0; y < size; ++y) {
            buf.append(' ');
        }
    }

    private static class Header<T> {
        private final String name;
        private final Function<T, String> supplier;
        private final boolean right;

        private Header(String name, Function<T, String> supplier, boolean right) {
            this.name = name;
            this.supplier = supplier;
            this.right = right;
        }
    }
}

