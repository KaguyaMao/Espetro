/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.editor.ore.model;

public final class OreFlexStyle {
    private String direction = "row";
    private String wrap = "nowrap";
    private String justifyContent = "flex-start";
    private String alignItems = "stretch";
    private String alignContent = "stretch";
    private String gap = "0px";
    private String rowGap = "0px";
    private String columnGap = "0px";

    public String direction() {
        return this.direction;
    }

    public String wrap() {
        return this.wrap;
    }

    public String justifyContent() {
        return this.justifyContent;
    }

    public String alignItems() {
        return this.alignItems;
    }

    public String alignContent() {
        return this.alignContent;
    }

    public String gap() {
        return this.gap;
    }

    public String rowGap() {
        return this.rowGap;
    }

    public String columnGap() {
        return this.columnGap;
    }

    public void setDirection(String value) {
        this.direction = value == null || value.isBlank() ? "row" : value;
    }

    public void setWrap(String value) {
        this.wrap = value == null || value.isBlank() ? "nowrap" : value;
    }

    public void setJustifyContent(String value) {
        this.justifyContent = value == null || value.isBlank() ? "flex-start" : value;
    }

    public void setAlignItems(String value) {
        this.alignItems = value == null || value.isBlank() ? "stretch" : value;
    }

    public void setAlignContent(String value) {
        this.alignContent = value == null || value.isBlank() ? "stretch" : value;
    }

    public void setGap(String value) {
        this.gap = value == null || value.isBlank() ? "0px" : value;
    }

    public void setRowGap(String value) {
        this.rowGap = value == null || value.isBlank() ? "0px" : value;
    }

    public void setColumnGap(String value) {
        this.columnGap = value == null || value.isBlank() ? "0px" : value;
    }
}

