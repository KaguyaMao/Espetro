/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class SimpleJsonParser {
    private final String source;
    private int index = 0;

    public SimpleJsonParser(String source) {
        this.source = source == null ? "" : source;
    }

    public Object parse() {
        this.skipWhitespace();
        Object value = this.parseValue();
        this.skipWhitespace();
        if (this.index != this.source.length()) {
            throw new IllegalArgumentException("Unexpected trailing JSON content at index " + this.index);
        }
        return value;
    }

    private Object parseValue() {
        this.skipWhitespace();
        if (this.index >= this.source.length()) {
            throw new IllegalArgumentException("Unexpected end of JSON input");
        }
        char c = this.source.charAt(this.index);
        return switch (c) {
            case '{' -> this.parseObject();
            case '[' -> this.parseArray();
            case '\"' -> this.parseString();
            case 't' -> this.parseLiteral("true", Boolean.TRUE);
            case 'f' -> this.parseLiteral("false", Boolean.FALSE);
            case 'n' -> this.parseLiteral("null", null);
            default -> this.parseNumber();
        };
    }

    private Map<String, Object> parseObject() {
        LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>();
        ++this.index;
        this.skipWhitespace();
        if (this.peek('}')) {
            ++this.index;
            return result;
        }
        while (true) {
            this.skipWhitespace();
            String key = this.parseString();
            this.skipWhitespace();
            this.expect(':');
            Object value = this.parseValue();
            result.put(key, value);
            this.skipWhitespace();
            if (this.peek('}')) {
                ++this.index;
                return result;
            }
            this.expect(',');
        }
    }

    private List<Object> parseArray() {
        ArrayList<Object> result = new ArrayList<Object>();
        ++this.index;
        this.skipWhitespace();
        if (this.peek(']')) {
            ++this.index;
            return result;
        }
        while (true) {
            result.add(this.parseValue());
            this.skipWhitespace();
            if (this.peek(']')) {
                ++this.index;
                return result;
            }
            this.expect(',');
        }
    }

    private String parseString() {
        this.expect('\"');
        StringBuilder builder = new StringBuilder();
        block9: while (this.index < this.source.length()) {
            char c;
            if ((c = this.source.charAt(this.index++)) == '\"') {
                return builder.toString();
            }
            if (c != '\\') {
                builder.append(c);
                continue;
            }
            if (this.index >= this.source.length()) {
                throw new IllegalArgumentException("Unexpected end of JSON string escape");
            }
            char escaped = this.source.charAt(this.index++);
            switch (escaped) {
                case '\"': 
                case '/': 
                case '\\': {
                    builder.append(escaped);
                    continue block9;
                }
                case 'b': {
                    builder.append('\b');
                    continue block9;
                }
                case 'f': {
                    builder.append('\f');
                    continue block9;
                }
                case 'n': {
                    builder.append('\n');
                    continue block9;
                }
                case 'r': {
                    builder.append('\r');
                    continue block9;
                }
                case 't': {
                    builder.append('\t');
                    continue block9;
                }
                case 'u': {
                    if (this.index + 4 > this.source.length()) {
                        throw new IllegalArgumentException("Invalid unicode escape in JSON string");
                    }
                    String hex = this.source.substring(this.index, this.index + 4);
                    builder.append((char)Integer.parseInt(hex, 16));
                    this.index += 4;
                    continue block9;
                }
            }
            throw new IllegalArgumentException("Unsupported JSON escape: \\" + escaped);
        }
        throw new IllegalArgumentException("Unterminated JSON string");
    }

    private Object parseLiteral(String literal, Object value) {
        if (!this.source.startsWith(literal, this.index)) {
            throw new IllegalArgumentException("Invalid JSON literal at index " + this.index);
        }
        this.index += literal.length();
        return value;
    }

    private Double parseNumber() {
        String token;
        int start = this.index++;
        if (this.peek('-')) {
            // empty if block
        }
        while (this.index < this.source.length() && Character.isDigit(this.source.charAt(this.index))) {
            ++this.index;
        }
        if (this.peek('.')) {
            ++this.index;
            while (this.index < this.source.length() && Character.isDigit(this.source.charAt(this.index))) {
                ++this.index;
            }
        }
        if (this.peek('e') || this.peek('E')) {
            ++this.index;
            if (this.peek('+') || this.peek('-')) {
                ++this.index;
            }
            while (this.index < this.source.length() && Character.isDigit(this.source.charAt(this.index))) {
                ++this.index;
            }
        }
        if ((token = this.source.substring(start, this.index)).isEmpty() || "-".equals(token)) {
            throw new IllegalArgumentException("Invalid JSON number at index " + start);
        }
        return Double.parseDouble(token);
    }

    private void skipWhitespace() {
        while (this.index < this.source.length() && Character.isWhitespace(this.source.charAt(this.index))) {
            ++this.index;
        }
    }

    private void expect(char expected) {
        this.skipWhitespace();
        if (!this.peek(expected)) {
            throw new IllegalArgumentException("Expected '" + expected + "' at index " + this.index);
        }
        ++this.index;
    }

    private boolean peek(char expected) {
        return this.index < this.source.length() && this.source.charAt(this.index) == expected;
    }
}

