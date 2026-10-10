/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.Window
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraftforge.eventbus.api.Cancelable
 *  net.minecraftforge.eventbus.api.Event
 *  org.jetbrains.annotations.ApiStatus$Internal
 */
package net.minecraftforge.client.event;

import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.client.gui.overlay.NamedGuiOverlay;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.ApiStatus;

public abstract class RenderGuiOverlayEvent
extends Event {
    private final Window window;
    private final GuiGraphics guiGraphics;
    private final float partialTick;
    private final NamedGuiOverlay overlay;

    @ApiStatus.Internal
    protected RenderGuiOverlayEvent(Window window, GuiGraphics guiGraphics, float partialTick, NamedGuiOverlay overlay) {
        this.window = window;
        this.guiGraphics = guiGraphics;
        this.partialTick = partialTick;
        this.overlay = overlay;
    }

    public Window getWindow() {
        return this.window;
    }

    public GuiGraphics getGuiGraphics() {
        return this.guiGraphics;
    }

    public float getPartialTick() {
        return this.partialTick;
    }

    public NamedGuiOverlay getOverlay() {
        return this.overlay;
    }

    public static class Post
    extends RenderGuiOverlayEvent {
        @ApiStatus.Internal
        public Post(Window window, GuiGraphics guiGraphics, float partialTick, NamedGuiOverlay overlay) {
            super(window, guiGraphics, partialTick, overlay);
        }
    }

    @Cancelable
    public static class Pre
    extends RenderGuiOverlayEvent {
        @ApiStatus.Internal
        public Pre(Window window, GuiGraphics guiGraphics, float partialTick, NamedGuiOverlay overlay) {
            super(window, guiGraphics, partialTick, overlay);
        }
    }
}

