/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 */
package cc.sighs.auratip.client.render;

import java.util.Optional;
import net.minecraft.resources.ResourceLocation;

final class RadialMenuOverlayState {
    private long generation;
    private ResourceLocation menuId;
    private int slotCount;
    private int hoveredIndex = -1;
    private int activeIndex = -1;
    private int pressedIndex = -1;
    private boolean active;
    private boolean closing;

    RadialMenuOverlayState() {
    }

    void open(ResourceLocation id, int newSlotCount) {
        ++this.generation;
        this.menuId = id;
        this.slotCount = Math.max(0, newSlotCount);
        this.hoveredIndex = -1;
        this.activeIndex = -1;
        this.pressedIndex = -1;
        this.active = true;
        this.closing = false;
    }

    boolean replace(ResourceLocation id, int newSlotCount) {
        if (!this.active) {
            return false;
        }
        ++this.generation;
        this.menuId = id;
        this.slotCount = Math.max(0, newSlotCount);
        this.hoveredIndex = this.retainIfPresent(this.hoveredIndex);
        this.activeIndex = this.retainIfPresent(this.activeIndex);
        this.pressedIndex = this.retainIfPresent(this.pressedIndex);
        this.closing = false;
        return true;
    }

    void beginClose() {
        if (this.active) {
            this.closing = true;
        }
    }

    void finishClose() {
        this.menuId = null;
        this.slotCount = 0;
        this.hoveredIndex = -1;
        this.activeIndex = -1;
        this.pressedIndex = -1;
        this.active = false;
        this.closing = false;
    }

    boolean shouldCloseAfterAction(boolean closeAfterAction, long actionGeneration) {
        return closeAfterAction && this.active && this.generation == actionGeneration;
    }

    long generation() {
        return this.generation;
    }

    boolean isActive() {
        return this.active;
    }

    boolean isClosing() {
        return this.closing;
    }

    Optional<ResourceLocation> activeMenuId() {
        return Optional.ofNullable(this.menuId);
    }

    int hoveredIndex() {
        return this.hoveredIndex;
    }

    void hoveredIndex(int index) {
        this.hoveredIndex = this.normalize(index);
    }

    int activeIndex() {
        return this.activeIndex;
    }

    void activeIndex(int index) {
        this.activeIndex = this.normalize(index);
    }

    int pressedIndex() {
        return this.pressedIndex;
    }

    void press(int index) {
        this.pressedIndex = this.normalize(index);
    }

    void release() {
        this.pressedIndex = -1;
    }

    private int retainIfPresent(int index) {
        return index >= 0 && index < this.slotCount ? index : -1;
    }

    private int normalize(int index) {
        return index >= 0 && index < this.slotCount ? index : -1;
    }
}

