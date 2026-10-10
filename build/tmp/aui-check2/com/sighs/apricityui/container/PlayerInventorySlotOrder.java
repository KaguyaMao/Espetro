/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.container;

public final class PlayerInventorySlotOrder {
    private static final int HOTBAR_SLOT_COUNT = 9;

    private PlayerInventorySlotOrder() {
    }

    public static int menuRelativeIndexToPlayerInventoryIndex(int menuRelativeIndex, int capacity) {
        if (!PlayerInventorySlotOrder.isValidIndex(menuRelativeIndex, capacity)) {
            return -1;
        }
        if (capacity <= 9) {
            return menuRelativeIndex;
        }
        int mainInventorySlotCount = capacity - 9;
        return menuRelativeIndex < mainInventorySlotCount ? menuRelativeIndex + 9 : menuRelativeIndex - mainInventorySlotCount;
    }

    public static int playerInventoryIndexToMenuRelativeIndex(int playerInventoryIndex, int capacity) {
        if (!PlayerInventorySlotOrder.isValidIndex(playerInventoryIndex, capacity)) {
            return -1;
        }
        if (capacity <= 9) {
            return playerInventoryIndex;
        }
        int mainInventorySlotCount = capacity - 9;
        return playerInventoryIndex < 9 ? mainInventorySlotCount + playerInventoryIndex : playerInventoryIndex - 9;
    }

    private static boolean isValidIndex(int index, int capacity) {
        return capacity > 0 && index >= 0 && index < capacity;
    }
}

