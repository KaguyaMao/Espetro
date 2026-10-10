/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.behavior;

import com.sighs.apricityui.init.Element;
import java.util.ArrayList;
import java.util.List;

public final class SelectModel {
    private SelectModel() {
    }

    public static List<Element> getOptionChildren(Element select) {
        if (!"SELECT".equalsIgnoreCase(select.tagName)) {
            return List.of();
        }
        ArrayList<Element> options = new ArrayList<Element>();
        SelectModel.collectOptionChildren(select, options);
        return options;
    }

    private static void collectOptionChildren(Element parent, List<Element> result) {
        if (parent == null) {
            return;
        }
        for (Element child : parent.children) {
            if (child == null) continue;
            if ("OPTION".equalsIgnoreCase(child.tagName)) {
                result.add(child);
                continue;
            }
            if (!"OPTGROUP".equalsIgnoreCase(child.tagName)) continue;
            SelectModel.collectOptionChildren(child, result);
        }
    }

    public static String getOptionValue(Element option) {
        if (!"OPTION".equalsIgnoreCase(option.tagName)) {
            return option.getValue();
        }
        if (option.hasAttribute("value")) {
            return option.getAttribute("value");
        }
        return SelectModel.normalizeOptionText(option.getTextContent());
    }

    public static String getOptionLabel(Element option) {
        if (!"OPTION".equalsIgnoreCase(option.tagName)) {
            return option.getTextContent();
        }
        if (option.hasAttribute("label")) {
            return option.getAttribute("label");
        }
        return SelectModel.normalizeOptionText(option.getTextContent());
    }

    public static void setOptionLabel(Element option, String label) {
        if ("OPTION".equalsIgnoreCase(option.tagName)) {
            option.setAttribute("label", label == null ? "" : label);
        }
    }

    public static String getOptionText(Element option) {
        return "OPTION".equalsIgnoreCase(option.tagName) ? SelectModel.normalizeOptionText(option.getTextContent()) : "";
    }

    public static void setOptionText(Element option, String text) {
        if ("OPTION".equalsIgnoreCase(option.tagName)) {
            option.setTextContent(text == null ? "" : text);
        }
    }

    public static int getOptionIndex(Element option) {
        Element select = SelectModel.getOwnerSelect(option);
        return select == null ? -1 : SelectModel.getOptionChildren(select).indexOf(option);
    }

    public static int getSelectLength(Element select) {
        return "SELECT".equalsIgnoreCase(select.tagName) ? SelectModel.getOptionChildren(select).size() : 0;
    }

    public static int getSelectSize(Element select) {
        return "SELECT".equalsIgnoreCase(select.tagName) ? SelectModel.getSelectDisplaySize(select) : 0;
    }

    public static void setSelectSize(Element select, int size) {
        if (!"SELECT".equalsIgnoreCase(select.tagName)) {
            return;
        }
        if (size <= 0) {
            select.removeAttribute("size");
        } else {
            select.setAttribute("size", Integer.toString(size));
        }
    }

    public static Element getOwnerSelect(Element option) {
        if (!"OPTION".equalsIgnoreCase(option.tagName)) {
            return null;
        }
        Element current = option.parentElement;
        if (current != null && "OPTGROUP".equalsIgnoreCase(current.tagName)) {
            current = current.parentElement;
        }
        return current != null && "SELECT".equalsIgnoreCase(current.tagName) ? current : null;
    }

    public static boolean isOptionEffectivelyDisabled(Element option) {
        if (!"OPTION".equalsIgnoreCase(option.tagName)) {
            return option.isDisabled();
        }
        if (option.isDisabled()) {
            return true;
        }
        Element select = SelectModel.getOwnerSelect(option);
        if (select != null && select.isDisabled()) {
            return true;
        }
        return option.parentElement != null && "OPTGROUP".equalsIgnoreCase(option.parentElement.tagName) && option.parentElement.isDisabled();
    }

    public static boolean currentSelectedness(Element option) {
        return option.selectedState != null ? option.selectedState.booleanValue() : option.hasRawBooleanAttribute("selected");
    }

    public static String normalizeOptionText(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        return text.trim().replaceAll("[\\t\\n\\f\\r ]+", " ");
    }

    public static void normalizeSelectSelection(Element select, boolean allowDefaultSelection) {
        if (!"SELECT".equalsIgnoreCase(select.tagName)) {
            return;
        }
        List<Element> options = SelectModel.getOptionChildren(select);
        if (options.isEmpty()) {
            return;
        }
        for (Element option : options) {
            if (option.selectedState != null) continue;
            option.selectedState = option.hasRawBooleanAttribute("selected");
        }
        if (select.isMultiple()) {
            return;
        }
        Element winner = null;
        for (Element option : options) {
            if (!SelectModel.currentSelectedness(option)) continue;
            winner = option;
        }
        if (winner == null && allowDefaultSelection && SelectModel.getSelectDisplaySize(select) <= 1) {
            winner = options.get(0);
        }
        if (winner != null) {
            for (Element option : options) {
                boolean bl = option == winner;
                option.selectedState = bl;
            }
        }
    }

    public static int getSelectDisplaySize(Element select) {
        String raw = select.getAttribute("size");
        if (raw == null || raw.isBlank()) {
            return select.isMultiple() ? 4 : 1;
        }
        try {
            int parsed = Integer.parseInt(raw.trim());
            return parsed > 0 ? parsed : (select.isMultiple() ? 4 : 1);
        }
        catch (NumberFormatException ignored) {
            return select.isMultiple() ? 4 : 1;
        }
    }

    public static void invalidateSelectPresentation(Element select) {
        select.getRenderer().text.clear();
        select.getRenderer().wrappedText.clear();
        select.invalidateStyle();
    }
}

