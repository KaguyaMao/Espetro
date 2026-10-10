/*
 * Decompiled with CFR 0.152.
 */
package dev.latvian.mods.kubejs.block.entity;

import dev.latvian.mods.kubejs.block.entity.BlockEntityAttachment;

public record BlockEntityAttachmentHolder(int index, BlockEntityAttachment.Factory factory) {
    @Override
    public String toString() {
        return "attachment_" + this.index;
    }
}

