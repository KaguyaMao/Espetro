/*
 * Decompiled with CFR 0.152.
 */
package com.redabysslucia.dragonrise_reforge.resource.model;

import com.redabysslucia.dragonrise_reforge.resource.model.BasicModelReloadListener;

public class ProjectileModelReloadListener
extends BasicModelReloadListener {
    public static final ProjectileModelReloadListener INSTANCE = new ProjectileModelReloadListener();

    private ProjectileModelReloadListener() {
        super("projectile");
    }
}

