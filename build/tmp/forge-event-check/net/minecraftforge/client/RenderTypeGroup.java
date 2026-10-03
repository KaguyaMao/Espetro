/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.RenderType
 */
package net.minecraftforge.client;

import net.minecraft.client.renderer.RenderType;

public record RenderTypeGroup(RenderType block, RenderType entity, RenderType entityFabulous) {
    public static RenderTypeGroup EMPTY = new RenderTypeGroup(null, null, null);

    public RenderTypeGroup {
        if (block == null != (entity == null) || block == null != (entityFabulous == null)) {
            throw new IllegalArgumentException("The render types in a group must either be all null, or all non-null.");
        }
    }

    public RenderTypeGroup(RenderType block, RenderType entity) {
        this(block, entity, entity);
    }

    public boolean isEmpty() {
        return this.block == null;
    }
}

