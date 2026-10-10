/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Streams
 *  org.apache.commons.lang3.StringUtils
 */
package net.minecraftforge.common.util;

import com.google.common.collect.Streams;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

public class TextTable {
    private final List<Column> columns;
    private final List<Row> rows = new ArrayList<Row>();

    public static Column column(String header) {
        return new Column(header);
    }

    public static Column column(String header, Alignment alignment) {
        return new Column(header, alignment);
    }

    public TextTable(List<Column> columns) {
        this.columns = columns;
    }

    public String build(String lineEnding) {
        StringBuilder destination = new StringBuilder();
        this.append(destination, lineEnding);
        return destination.toString();
    }

    public void append(StringBuilder destination, String lineEnding) {
        List<String> headers = this.columns.stream().map(c -> c.formatHeader(" ")).collect(Collectors.toList());
        this.printRow(destination, headers);
        destination.append(lineEnding);
        this.printSeparators(destination);
        for (Row row : this.rows) {
            destination.append(lineEnding);
            this.printRow(destination, row.format(this.columns, " "));
        }
    }

    private void printSeparators(StringBuilder destination) {
        destination.append('|');
        for (Column column : this.columns) {
            destination.append(column.alignment != Alignment.RIGHT ? (char)':' : ' ');
            destination.append(column.getSeparator('-'));
            destination.append(column.alignment != Alignment.LEFT ? (char)':' : ' ');
            destination.append('|');
        }
    }

    private void printRow(StringBuilder destination, List<String> values) {
        destination.append('|');
        for (String value : values) {
            destination.append(' ');
            destination.append(value);
            destination.append(' ');
            destination.append('|');
        }
    }

    public void add(Object ... values) {
        if (values.length != this.columns.size()) {
            throw new IllegalArgumentException("Received wrong amount of values for table row, expected " + this.columns.size() + ", received " + this.columns.size() + ".");
        }
        Row row = new Row();
        for (int i = 0; i < values.length; ++i) {
            String value = Objects.toString(values[i]);
            row.values.add(value);
            this.columns.get(i).fit(value);
        }
        this.rows.add(row);
    }

    public void clear() {
        for (Column column : this.columns) {
            column.resetWidth();
        }
        this.rows.clear();
    }

    public List<Column> getColumns() {
        return Collections.unmodifiableList(this.columns);
    }

    public static class Column {
        private String header;
        private int width;
        private Alignment alignment;

        public Column(String header) {
            this(header, Alignment.LEFT);
        }

        public Column(String header, Alignment alignment) {
            this.header = header;
            this.width = header.length();
            this.alignment = alignment;
        }

        public String formatHeader(String padding) {
            return this.format(this.header, padding);
        }

        public String format(String value, String padding) {
            switch (this.alignment) {
                case LEFT: {
                    return StringUtils.rightPad((String)value, (int)this.width, (String)padding);
                }
                case RIGHT: {
                    return StringUtils.leftPad((String)value, (int)this.width, (String)padding);
                }
            }
            int length = value.length();
            int left = (this.width - length) / 2;
            int leftWidth = left + length;
            return StringUtils.rightPad((String)StringUtils.leftPad((String)value, (int)leftWidth, (String)padding), (int)this.width, (String)padding);
        }

        public String getSeparator(char character) {
            return StringUtils.leftPad((String)"", (int)this.width, (char)character);
        }

        public void fit(String value) {
            if (value.length() > this.width) {
                this.width = value.length();
            }
        }

        public void resetWidth() {
            this.width = this.header.length();
        }

        public int getWidth() {
            return this.width;
        }
    }

    public static enum Alignment {
        LEFT,
        CENTER,
        RIGHT;

    }

    public static class Row {
        private final ArrayList<String> values = new ArrayList();

        public List<String> format(List<Column> columns, String padding) {
            if (columns.size() != this.values.size()) {
                throw new IllegalArgumentException("Received wrong amount of columns for table row, expected " + columns.size() + ", received " + columns.size() + ".");
            }
            return Streams.zip(this.values.stream(), columns.stream(), (v, c) -> c.format((String)v, padding)).collect(Collectors.toList());
        }
    }
}

