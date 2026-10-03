/*
 * Decompiled with CFR 0.152.
 */
package com.redabysslucia.dragonrise_reforge.resource.model;

import com.redabysslucia.dragonrise_reforge.resource.model.BasicModelReloadListener;

public class BlockModelReloadListener
extends BasicModelReloadListener {
    public static final BlockModelReloadListener INSTANCE = new BlockModelReloadListener();

    private BlockModelReloadListener() {
        super("block");
    }
}

