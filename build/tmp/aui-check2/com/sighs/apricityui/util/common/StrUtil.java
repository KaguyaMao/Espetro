/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.util.common;

import com.sighs.apricityui.util.common.NameConverter;

public class StrUtil {
    public static String cleanResource(String s) {
        return s.toLowerCase().replaceAll("[^a-z0-9_.\\-/:]", "");
    }

    public static String toSnakeCase(String s) {
        return StrUtil.nameConvert(s, NameConverter.SNAKE_CASE);
    }

    public static String toCamelCase(String s) {
        return StrUtil.nameConvert(s, NameConverter.CAMEL_CASE);
    }

    public static String toPascalCase(String s) {
        return StrUtil.nameConvert(s, NameConverter.PASCAL_CASE);
    }

    private static String nameConvert(String s, NameConverter converter) {
        return converter.joinWords(NameConverter.splitName(s));
    }
}

