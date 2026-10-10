/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
 *  net.minecraftforge.eventbus.api.Event
 *  org.jetbrains.annotations.ApiStatus$Internal
 */
package net.minecraftforge.client.event;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.ApiStatus;

public abstract class ContainerScreenEvent
extends Event {
    private final AbstractContainerScreen<?> containerScreen;

    @ApiStatus.Internal
    protected ContainerScreenEvent(AbstractContainerScreen<?> containerScreen) {
        this.containerScreen = containerScreen;
    }

    public AbstractContainerScreen<?> getContainerScreen() {
        return this.containerScreen;
    }

    public static abstract class Render
    extends ContainerScreenEvent {
        private final GuiGraphics guiGraphics;
        private final int mouseX;
        private final int mouseY;

        @ApiStatus.Internal
        protected Render(AbstractContainerScreen<?> guiContainer, GuiGraphics guiGraphics, int mouseX, int mouseY) {
            super(guiContainer);
            this.guiGraphics = guiGraphics;
            this.mouseX = mouseX;
            this.mouseY = mouseY;
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

        public static class Background
        extends Render {
            @ApiStatus.Internal
            public Background(AbstractContainerScreen<?> guiContainer, GuiGraphics guiGraphics, int mouseX, int mouseY) {
                super(guiContainer, guiGraphics, mouseX, mouseY);
            }
        }

        public static class Foreground
        extends Render {
            @ApiStatus.Internal
            public Foreground(AbstractContainerScreen<?> guiContainer, GuiGraphics guiGraphics, int mouseX, int mouseY) {
                super(guiContainer, guiGraphics, mouseX, mouseY);
            }
        }
    }
}

