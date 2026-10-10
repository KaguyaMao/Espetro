/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 */
package com.sighs.apricityui.network.handler;

import com.sighs.apricityui.container.bind.ContainerBindType;
import com.sighs.apricityui.element.ContainerDeclaration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;

public final class BindingBuilder {
    private final List<ContainerDeclaration> declarations = new ArrayList<ContainerDeclaration>();
    private final Map<String, Map<String, String>> argsById = new LinkedHashMap<String, Map<String, String>>();
    private boolean primarySet = false;

    public BindingBuilder player() {
        this.declarations.add(new ContainerDeclaration("player", ContainerBindType.PLAYER, 36, false));
        return this;
    }

    public BindingBuilder saveddata() {
        return this.saveddata("apricityui_data", 9);
    }

    public BindingBuilder saveddata(String dataName) {
        return this.saveddata(dataName, 9);
    }

    public BindingBuilder saveddata(String dataName, int capacity) {
        boolean primary;
        String id = "saved_data";
        boolean bl = primary = !this.primarySet;
        if (primary) {
            this.primarySet = true;
        }
        this.declarations.add(new ContainerDeclaration(id, ContainerBindType.SAVED_DATA, capacity, primary));
        this.argsById.put(id, Map.of("data_name", dataName));
        return this;
    }

    public BindingBuilder blockEntity(BlockPos pos) {
        return this.blockEntity(pos, 0);
    }

    public BindingBuilder blockEntity(BlockPos pos, int capacity) {
        boolean primary;
        String id = "block_entity";
        boolean bl = primary = !this.primarySet;
        if (primary) {
            this.primarySet = true;
        }
        this.declarations.add(new ContainerDeclaration(id, ContainerBindType.BLOCK_ENTITY, capacity, primary));
        this.argsById.put(id, Map.of("x", String.valueOf(pos.m_123341_()), "y", String.valueOf(pos.m_123342_()), "z", String.valueOf(pos.m_123343_())));
        return this;
    }

    public BindingBuilder entity(int entityId) {
        return this.entity(entityId, 0);
    }

    public BindingBuilder entity(int entityId, int capacity) {
        boolean primary;
        String id = "entity";
        boolean bl = primary = !this.primarySet;
        if (primary) {
            this.primarySet = true;
        }
        this.declarations.add(new ContainerDeclaration(id, ContainerBindType.ENTITY, capacity, primary));
        this.argsById.put(id, Map.of("entity_id", String.valueOf(entityId)));
        return this;
    }

    List<ContainerDeclaration> declarations() {
        return this.declarations;
    }

    Map<String, Map<String, String>> argsById() {
        return this.argsById;
    }
}

