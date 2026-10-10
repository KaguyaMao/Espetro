/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.KeyMapping
 *  net.minecraft.world.InteractionHand
 *  net.minecraftforge.eventbus.api.Cancelable
 *  net.minecraftforge.eventbus.api.Event
 *  org.jetbrains.annotations.ApiStatus$Internal
 */
package net.minecraftforge.client.event;

import net.minecraft.client.KeyMapping;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.ApiStatus;

public abstract class InputEvent
extends Event {
    @ApiStatus.Internal
    protected InputEvent() {
    }

    @Cancelable
    public static class InteractionKeyMappingTriggered
    extends InputEvent {
        private final int button;
        private final KeyMapping keyMapping;
        private final InteractionHand hand;
        private boolean handSwing = true;

        @ApiStatus.Internal
        public InteractionKeyMappingTriggered(int button, KeyMapping keyMapping, InteractionHand hand) {
            this.button = button;
            this.keyMapping = keyMapping;
            this.hand = hand;
        }

        public void setSwingHand(boolean value) {
            this.handSwing = value;
        }

        public boolean shouldSwingHand() {
            return this.handSwing;
        }

        public InteractionHand getHand() {
            return this.hand;
        }

        public boolean isAttack() {
            return this.button == 0;
        }

        public boolean isUseItem() {
            return this.button == 1;
        }

        public boolean isPickBlock() {
            return this.button == 2;
        }

        public KeyMapping getKeyMapping() {
            return this.keyMapping;
        }
    }

    public static class Key
    extends InputEvent {
        private final int key;
        private final int scanCode;
        private final int action;
        private final int modifiers;

        @ApiStatus.Internal
        public Key(int key, int scanCode, int action, int modifiers) {
            this.key = key;
            this.scanCode = scanCode;
            this.action = action;
            this.modifiers = modifiers;
        }

        public int getKey() {
            return this.key;
        }

        public int getScanCode() {
            return this.scanCode;
        }

        public int getAction() {
            return this.action;
        }

        public int getModifiers() {
            return this.modifiers;
        }
    }

    @Cancelable
    public static class MouseScrollingEvent
    extends InputEvent {
        private final double scrollDelta;
        private final double mouseX;
        private final double mouseY;
        private final boolean leftDown;
        private final boolean middleDown;
        private final boolean rightDown;

        @ApiStatus.Internal
        public MouseScrollingEvent(double scrollDelta, boolean leftDown, boolean middleDown, boolean rightDown, double mouseX, double mouseY) {
            this.scrollDelta = scrollDelta;
            this.leftDown = leftDown;
            this.middleDown = middleDown;
            this.rightDown = rightDown;
            this.mouseX = mouseX;
            this.mouseY = mouseY;
        }

        public double getScrollDelta() {
            return this.scrollDelta;
        }

        public boolean isLeftDown() {
            return this.leftDown;
        }

        public boolean isRightDown() {
            return this.rightDown;
        }

        public boolean isMiddleDown() {
            return this.middleDown;
        }

        public double getMouseX() {
            return this.mouseX;
        }

        public double getMouseY() {
            return this.mouseY;
        }
    }

    public static abstract class MouseButton
    extends InputEvent {
        private final int button;
        private final int action;
        private final int modifiers;

        @ApiStatus.Internal
        protected MouseButton(int button, int action, int modifiers) {
            this.button = button;
            this.action = action;
            this.modifiers = modifiers;
        }

        public int getButton() {
            return this.button;
        }

        public int getAction() {
            return this.action;
        }

        public int getModifiers() {
            return this.modifiers;
        }

        public static class Post
        extends MouseButton {
            @ApiStatus.Internal
            public Post(int button, int action, int modifiers) {
                super(button, action, modifiers);
            }
        }

        @Cancelable
        public static class Pre
        extends MouseButton {
            @ApiStatus.Internal
            public Pre(int button, int action, int modifiers) {
                super(button, action, modifiers);
            }
        }
    }
}

