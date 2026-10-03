/*
 * Decompiled with CFR 0.152.
 */
package com.redabysslucia.dragonrise_reforge.resource.model;

import com.redabysslucia.dragonrise_reforge.resource.model.BasicModelReloadListener;

public class EntityModelReloadListener
extends BasicModelReloadListener {
    public static final EntityModelReloadListener INSTANCE = new EntityModelReloadListener();

    private EntityModelReloadListener() {
        super("entity");
    }
}

