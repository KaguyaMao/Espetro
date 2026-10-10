/*
 * Decompiled with CFR 0.152.
 */
package com.redabysslucia.dragonrise_reforge.resource.model;

import com.redabysslucia.dragonrise_reforge.resource.model.BasicModelReloadListener;

public class ItemModelReloadListener
extends BasicModelReloadListener {
    public static final ItemModelReloadListener INSTANCE = new ItemModelReloadListener();

    private ItemModelReloadListener() {
        super("item");
    }
}

