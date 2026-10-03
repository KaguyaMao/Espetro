/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.element;

import com.sighs.apricityui.container.bind.ContainerBindType;

public record ContainerDeclaration(String id, ContainerBindType bindType, int capacity, boolean primary) {
    public ContainerDeclaration {
        id = id == null ? "" : id.trim();
        bindType = bindType == null ? ContainerBindType.PLAYER : bindType;
        capacity = Math.max(0, capacity);
    }
}

