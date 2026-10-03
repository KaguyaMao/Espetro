/*
 * Decompiled with CFR 0.152.
 */
package cc.sighs.auratip.client.render;

final class RadialMenuInputPolicy {
    private RadialMenuInputPolicy() {
    }

    static boolean isCloseKey(int keyCode, int customCloseKeyCode) {
        return keyCode == 256 || customCloseKeyCode >= 0 && keyCode == customCloseKeyCode;
    }

    static boolean isOutsideClick(int hoveredIndex, int slotCount) {
        return hoveredIndex < 0 || hoveredIndex >= slotCount;
    }
}

