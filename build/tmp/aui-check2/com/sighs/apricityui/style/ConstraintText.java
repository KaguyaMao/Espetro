/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.style;

import com.sighs.apricityui.init.Element;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.time.temporal.IsoFields;
import java.util.Locale;

public final class ConstraintText {
    private ConstraintText() {
    }

    public static String normalizeNumericText(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }
        int len = value.length();
        int i = 0;
        if (value.charAt(0) == '-') {
            if (len == 1) {
                return value;
            }
            i = 1;
        }
        boolean allDigits = true;
        for (int j = i; j < len - 2; ++j) {
            char c = value.charAt(j);
            if (c >= '0' && c <= '9') continue;
            allDigits = false;
            break;
        }
        if (allDigits && len >= i + 3 && value.charAt(len - 2) == '.' && value.charAt(len - 1) == '0') {
            return value.substring(0, len - 2);
        }
        return value;
    }

    public static String fileName(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }
        int slash = Math.max(value.lastIndexOf(47), value.lastIndexOf(92));
        return slash < 0 ? value : value.substring(slash + 1);
    }

    public static String normalizedInputType(Element control) {
        String type = control.getType();
        return type == null || type.isBlank() ? "text" : type.trim().toLowerCase(Locale.ROOT);
    }

    public static boolean isSubmitButton(Element element) {
        if (element == null) {
            return false;
        }
        if ("BUTTON".equalsIgnoreCase(element.tagName)) {
            String type = element.getAttribute("type");
            return type == null || type.isBlank() || "submit".equalsIgnoreCase(type) || "image".equalsIgnoreCase(type);
        }
        if (!"INPUT".equalsIgnoreCase(element.tagName)) {
            return false;
        }
        String type = ConstraintText.normalizedInputType(element);
        return "submit".equals(type) || "image".equals(type);
    }

    public static String serializeNumberValue(double number) {
        if (number == 0.0) {
            return "0";
        }
        return BigDecimal.valueOf(number).stripTrailingZeros().toPlainString();
    }

    public static boolean isSimpleEmail(String value) {
        return value != null && value.matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+");
    }

    public static boolean isNumericType(String type) {
        return switch (type) {
            case "number", "range", "date", "month", "week", "time", "datetime-local" -> true;
            default -> false;
        };
    }

    public static Double parseConstraintNumber(String type, String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        try {
            return switch (type) {
                case "number", "range" -> Double.parseDouble(raw);
                case "date" -> LocalDate.parse(raw).toEpochDay();
                case "month" -> {
                    YearMonth month = YearMonth.parse(raw);
                    yield (long)month.getYear() * 12L + (long)month.getMonthValue() - 1L;
                }
                case "time" -> ConstraintText.parseTimeSeconds(raw);
                case "datetime-local" -> (double)LocalDateTime.parse(raw).toInstant(ZoneOffset.UTC).toEpochMilli() / 1000.0;
                case "week" -> {
                    if (!raw.matches("\\d{4}-W\\d{2}")) {
                        yield null;
                    }
                    String[] parts = raw.split("-W");
                    int year = Integer.parseInt(parts[0]);
                    int week = Integer.parseInt(parts[1]);
                    yield LocalDate.of(year, 1, 4).with(IsoFields.WEEK_OF_WEEK_BASED_YEAR, week).with(DayOfWeek.MONDAY).toEpochDay();
                }
                default -> null;
            };
        }
        catch (RuntimeException ignored) {
            return null;
        }
    }

    public static double parseTimeSeconds(String raw) {
        LocalTime time = LocalTime.parse(raw);
        return (double)time.toNanoOfDay() / 1.0E9;
    }

    public static String formatTime(double seconds) {
        long rounded = Math.max(0L, Math.round(seconds));
        return LocalTime.ofSecondOfDay(rounded % 86400L).toString();
    }

    public static double defaultStepBase(String type) {
        return switch (type) {
            case "date" -> 0.0;
            case "month" -> 0.0;
            case "datetime-local", "time" -> 0.0;
            case "week" -> LocalDate.of(1969, 12, 29).toEpochDay();
            default -> 0.0;
        };
    }

    public static String defaultStepText(String type) {
        return switch (type) {
            case "time", "datetime-local" -> "60";
            case "week" -> "7";
            default -> "1";
        };
    }

    public static int parseNonNegativeInt(String raw) {
        if (raw == null || raw.isBlank()) {
            return -1;
        }
        try {
            int value = Integer.parseInt(raw);
            return value < 0 ? -1 : value;
        }
        catch (NumberFormatException ignored) {
            return -1;
        }
    }
}

