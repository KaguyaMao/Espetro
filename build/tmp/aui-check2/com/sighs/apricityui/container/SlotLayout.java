/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.FriendlyByteBuf
 */
package com.sighs.apricityui.container;

import com.sighs.apricityui.container.bind.ContainerBindType;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.FriendlyByteBuf;

public record SlotLayout(String templatePath, List<ContainerEntry> containers) {
    public SlotLayout {
        templatePath = templatePath == null ? "" : templatePath;
        containers = containers == null ? List.of() : List.copyOf(containers);
    }

    public static SlotLayout createUiOnly(String templatePath) {
        return new SlotLayout(templatePath, List.of());
    }

    public boolean isUiOnly() {
        return this.containers.isEmpty();
    }

    public String primaryContainerId() {
        for (ContainerEntry entry : this.containers) {
            if (!entry.primary()) continue;
            return entry.id();
        }
        return this.containers.isEmpty() ? "" : this.containers.get(0).id();
    }

    public ContainerEntry findContainer(String containerId) {
        if (containerId == null || containerId.isBlank()) {
            return null;
        }
        for (ContainerEntry entry : this.containers) {
            if (!containerId.equals(entry.id())) continue;
            return entry;
        }
        return null;
    }

    public void write(FriendlyByteBuf buf) {
        buf.m_130070_(this.templatePath);
        buf.m_130130_(this.containers.size());
        for (ContainerEntry entry : this.containers) {
            buf.m_130070_(entry.id());
            buf.m_130070_(entry.bindType().id());
            buf.m_130130_(entry.baseIndex());
            buf.m_130130_(entry.capacity());
            buf.writeBoolean(entry.primary());
        }
    }

    public static SlotLayout read(FriendlyByteBuf buf) {
        String templatePath = buf.m_130277_();
        int count = buf.m_130242_();
        ArrayList<ContainerEntry> entries = new ArrayList<ContainerEntry>(count);
        for (int i = 0; i < count; ++i) {
            String id = buf.m_130277_();
            String bindTypeId = buf.m_130277_();
            ContainerBindType bindType = ContainerBindType.fromRaw(bindTypeId);
            if (bindType == null) {
                bindType = ContainerBindType.PLAYER;
            }
            int baseIndex = buf.m_130242_();
            int capacity = buf.m_130242_();
            boolean primary = buf.readBoolean();
            entries.add(new ContainerEntry(id, bindType, baseIndex, capacity, primary));
        }
        return new SlotLayout(templatePath, entries);
    }

    public record ContainerEntry(String id, ContainerBindType bindType, int baseIndex, int capacity, boolean primary) {
        public ContainerEntry {
            id = id == null ? "" : id;
            bindType = bindType == null ? ContainerBindType.PLAYER : bindType;
            baseIndex = Math.max(0, baseIndex);
            capacity = Math.max(0, capacity);
        }

        public Integer resolveGlobalSlotIndex(int localSlotIndex) {
            if (localSlotIndex < 0 || localSlotIndex >= this.capacity) {
                return null;
            }
            return this.baseIndex + localSlotIndex;
        }
    }
}

