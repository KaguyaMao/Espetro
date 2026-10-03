/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraftforge.eventbus.api.Cancelable
 *  net.minecraftforge.eventbus.api.Event
 *  net.minecraftforge.eventbus.api.Event$HasResult
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.client.event;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

public abstract class ScreenEvent
extends Event {
    private final Screen screen;

    @ApiStatus.Internal
    protected ScreenEvent(Screen screen) {
        this.screen = Objects.requireNonNull(screen);
    }

    public Screen getScreen() {
        return this.screen;
    }

    public static class Closing
    extends ScreenEvent {
        @ApiStatus.Internal
        public Closing(Screen screen) {
            super(screen);
        }
    }

    @Cancelable
    public static class Opening
    extends ScreenEvent {
        @Nullable
        private final Screen currentScreen;
        private Screen newScreen;

        @ApiStatus.Internal
        public Opening(@Nullable Screen currentScreen, Screen screen) {
            super(screen);
            this.currentScreen = currentScreen;
            this.newScreen = screen;
        }

        @Nullable
        public Screen getCurrentScreen() {
            return this.currentScreen;
        }

        @Nullable
        public Screen getNewScreen() {
            return this.newScreen;
        }

        public void setNewScreen(Screen newScreen) {
            this.newScreen = newScreen;
        }
    }

    public static class CharacterTyped
    extends ScreenEvent {
        private final char codePoint;
        private final int modifiers;

        @ApiStatus.Internal
        public CharacterTyped(Screen screen, char codePoint, int modifiers) {
            super(screen);
            this.codePoint = codePoint;
            this.modifiers = modifiers;
        }

        public char getCodePoint() {
            return this.codePoint;
        }

        public int getModifiers() {
            return this.modifiers;
        }

        public static class Post
        extends CharacterTyped {
            @ApiStatus.Internal
            public Post(Screen screen, char codePoint, int modifiers) {
                super(screen, codePoint, modifiers);
            }
        }

        @Cancelable
        public static class Pre
        extends CharacterTyped {
            @ApiStatus.Internal
            public Pre(Screen screen, char codePoint, int modifiers) {
                super(screen, codePoint, modifiers);
            }
        }
    }

    public static abstract class KeyReleased
    extends KeyInput {
        @ApiStatus.Internal
        public KeyReleased(Screen screen, int keyCode, int scanCode, int modifiers) {
            super(screen, keyCode, scanCode, modifiers);
        }

        @Cancelable
        public static class Post
        extends KeyReleased {
            @ApiStatus.Internal
            public Post(Screen screen, int keyCode, int scanCode, int modifiers) {
                super(screen, keyCode, scanCode, modifiers);
            }
        }

        @Cancelable
        public static class Pre
        extends KeyReleased {
            @ApiStatus.Internal
            public Pre(Screen screen, int keyCode, int scanCode, int modifiers) {
                super(screen, keyCode, scanCode, modifiers);
            }
        }
    }

    public static abstract class KeyPressed
    extends KeyInput {
        @ApiStatus.Internal
        public KeyPressed(Screen screen, int keyCode, int scanCode, int modifiers) {
            super(screen, keyCode, scanCode, modifiers);
        }

        @Cancelable
        public static class Post
        extends KeyPressed {
            @ApiStatus.Internal
            public Post(Screen screen, int keyCode, int scanCode, int modifiers) {
                super(screen, keyCode, scanCode, modifiers);
            }
        }

        @Cancelable
        public static class Pre
        extends KeyPressed {
            @ApiStatus.Internal
            public Pre(Screen screen, int keyCode, int scanCode, int modifiers) {
                super(screen, keyCode, scanCode, modifiers);
            }
        }
    }

    private static abstract class KeyInput
    extends ScreenEvent {
        private final int keyCode;
        private final int scanCode;
        private final int modifiers;

        @ApiStatus.Internal
        protected KeyInput(Screen screen, int keyCode, int scanCode, int modifiers) {
            super(screen);
            this.keyCode = keyCode;
            this.scanCode = scanCode;
            this.modifiers = modifiers;
        }

        public int getKeyCode() {
            return this.keyCode;
        }

        public int getScanCode() {
            return this.scanCode;
        }

        public int getModifiers() {
            return this.modifiers;
        }
    }

    public static abstract class MouseScrolled
    extends MouseInput {
        private final double scrollDelta;

        @ApiStatus.Internal
        public MouseScrolled(Screen screen, double mouseX, double mouseY, double scrollDelta) {
            super(screen, mouseX, mouseY);
            this.scrollDelta = scrollDelta;
        }

        public double getScrollDelta() {
            return this.scrollDelta;
        }

        public static class Post
        extends MouseScrolled {
            @ApiStatus.Internal
            public Post(Screen screen, double mouseX, double mouseY, double scrollDelta) {
                super(screen, mouseX, mouseY, scrollDelta);
            }
        }

        @Cancelable
        public static class Pre
        extends MouseScrolled {
            @ApiStatus.Internal
            public Pre(Screen screen, double mouseX, double mouseY, double scrollDelta) {
                super(screen, mouseX, mouseY, scrollDelta);
            }
        }
    }

    public static abstract class MouseDragged
    extends MouseInput {
        private final int mouseButton;
        private final double dragX;
        private final double dragY;

        @ApiStatus.Internal
        public MouseDragged(Screen screen, double mouseX, double mouseY, int mouseButton, double dragX, double dragY) {
            super(screen, mouseX, mouseY);
            this.mouseButton = mouseButton;
            this.dragX = dragX;
            this.dragY = dragY;
        }

        public int getMouseButton() {
            return this.mouseButton;
        }

        public double getDragX() {
            return this.dragX;
        }

        public double getDragY() {
            return this.dragY;
        }

        public static class Post
        extends MouseDragged {
            @ApiStatus.Internal
            public Post(Screen screen, double mouseX, double mouseY, int mouseButton, double dragX, double dragY) {
                super(screen, mouseX, mouseY, mouseButton, dragX, dragY);
            }
        }

        @Cancelable
        public static class Pre
        extends MouseDragged {
            @ApiStatus.Internal
            public Pre(Screen screen, double mouseX, double mouseY, int mouseButton, double dragX, double dragY) {
                super(screen, mouseX, mouseY, mouseButton, dragX, dragY);
            }
        }
    }

    public static abstract class MouseButtonReleased
    extends MouseInput {
        private final int button;

        @ApiStatus.Internal
        public MouseButtonReleased(Screen screen, double mouseX, double mouseY, int button) {
            super(screen, mouseX, mouseY);
            this.button = button;
        }

        public int getButton() {
            return this.button;
        }

        @Event.HasResult
        public static class Post
        extends MouseButtonReleased {
            private final boolean handled;

            @ApiStatus.Internal
            public Post(Screen screen, double mouseX, double mouseY, int button, boolean handled) {
                super(screen, mouseX, mouseY, button);
                this.handled = handled;
            }

            public boolean wasHandled() {
                return this.handled;
            }
        }

        @Cancelable
        public static class Pre
        extends MouseButtonReleased {
            @ApiStatus.Internal
            public Pre(Screen screen, double mouseX, double mouseY, int button) {
                super(screen, mouseX, mouseY, button);
            }
        }
    }

    public static abstract class MouseButtonPressed
    extends MouseInput {
        private final int button;

        @ApiStatus.Internal
        public MouseButtonPressed(Screen screen, double mouseX, double mouseY, int button) {
            super(screen, mouseX, mouseY);
            this.button = button;
        }

        public int getButton() {
            return this.button;
        }

        @Event.HasResult
        public static class Post
        extends MouseButtonPressed {
            private final boolean handled;

            @ApiStatus.Internal
            public Post(Screen screen, double mouseX, double mouseY, int button, boolean handled) {
                super(screen, mouseX, mouseY, button);
                this.handled = handled;
            }

            public boolean wasHandled() {
                return this.handled;
            }
        }

        @Cancelable
        public static class Pre
        extends MouseButtonPressed {
            @ApiStatus.Internal
            public Pre(Screen screen, double mouseX, double mouseY, int button) {
                super(screen, mouseX, mouseY, button);
            }
        }
    }

    private static abstract class MouseInput
    extends ScreenEvent {
        private final double mouseX;
        private final double mouseY;

        @ApiStatus.Internal
        protected MouseInput(Screen screen, double mouseX, double mouseY) {
            super(screen);
            this.mouseX = mouseX;
            this.mouseY = mouseY;
        }

        public double getMouseX() {
            return this.mouseX;
        }

        public double getMouseY() {
            return this.mouseY;
        }
    }

    @Cancelable
    public static class RenderInventoryMobEffects
    extends ScreenEvent {
        private final int availableSpace;
        private boolean compact;
        private int horizontalOffset;

        @ApiStatus.Internal
        public RenderInventoryMobEffects(Screen screen, int availableSpace, boolean compact, int horizontalOffset) {
            super(screen);
            this.availableSpace = availableSpace;
            this.compact = compact;
            this.horizontalOffset = horizontalOffset;
        }

        public int getAvailableSpace() {
            return this.availableSpace;
        }

        public boolean isCompact() {
            return this.compact;
        }

        public int getHorizontalOffset() {
            return this.horizontalOffset;
        }

        public void setHorizontalOffset(int offset) {
            this.horizontalOffset = offset;
        }

        public void addHorizontalOffset(int offset) {
            this.horizontalOffset += offset;
        }

        public void setCompact(boolean compact) {
            this.compact = compact;
        }
    }

    public static class BackgroundRendered
    extends ScreenEvent {
        private final GuiGraphics guiGraphics;

        @ApiStatus.Internal
        public BackgroundRendered(Screen screen, GuiGraphics guiGraphics) {
            super(screen);
            this.guiGraphics = guiGraphics;
        }

        public GuiGraphics getGuiGraphics() {
            return this.guiGraphics;
        }
    }

    public static abstract class Render
    extends ScreenEvent {
        private final GuiGraphics guiGraphics;
        private final int mouseX;
        private final int mouseY;
        private final float partialTick;

        @ApiStatus.Internal
        protected Render(Screen screen, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            super(screen);
            this.guiGraphics = guiGraphics;
            this.mouseX = mouseX;
            this.mouseY = mouseY;
            this.partialTick = partialTick;
        }

        public GuiGraphics getGuiGraphics() {
            return this.guiGraphics;
        }

        public int getMouseX() {
            return this.mouseX;
        }

        public int getMouseY() {
            return this.mouseY;
        }

        public float getPartialTick() {
            return this.partialTick;
        }

        public static class Post
        extends Render {
            @ApiStatus.Internal
            public Post(Screen screen, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
                super(screen, guiGraphics, mouseX, mouseY, partialTick);
            }
        }

        @Cancelable
        public static class Pre
        extends Render {
            @ApiStatus.Internal
            public Pre(Screen screen, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
                super(screen, guiGraphics, mouseX, mouseY, partialTick);
            }
        }
    }

    public static abstract class Init
    extends ScreenEvent {
        private final Consumer<GuiEventListener> add;
        private final Consumer<GuiEventListener> remove;
        private final List<GuiEventListener> listenerList;

        @ApiStatus.Internal
        protected Init(Screen screen, List<GuiEventListener> listenerList, Consumer<GuiEventListener> add, Consumer<GuiEventListener> remove) {
            super(screen);
            this.listenerList = Collections.unmodifiableList(listenerList);
            this.add = add;
            this.remove = remove;
        }

        public List<GuiEventListener> getListenersList() {
            return this.listenerList;
        }

        public void addListener(GuiEventListener listener) {
            this.add.accept(listener);
        }

        public void removeListener(GuiEventListener listener) {
            this.remove.accept(listener);
        }

        public static class Post
        extends Init {
            @ApiStatus.Internal
            public Post(Screen screen, List<GuiEventListener> list, Consumer<GuiEventListener> add, Consumer<GuiEventListener> remove) {
                super(screen, list, add, remove);
            }
        }

        @Cancelable
        public static class Pre
        extends Init {
            @ApiStatus.Internal
            public Pre(Screen screen, List<GuiEventListener> list, Consumer<GuiEventListener> add, Consumer<GuiEventListener> remove) {
                super(screen, list, add, remove);
            }
        }
    }
}

