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
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.ApiStatus;

public abstract class RenderGuiEvent
extends Event {
    private final Window window;
    private final GuiGraphics guiGraphics;
    private final float partialTick;

    @ApiStatus.Internal
    protected RenderGuiEvent(Window window, GuiGraphics guiGraphics, float partialTick) {
        this.window = window;
        this.guiGraphics = guiGraphics;
        this.partialTick = partialTick;
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

    public static class Post
    extends RenderGuiEvent {
        @ApiStatus.Internal
        public Post(Window window, GuiGraphics guiGraphics, float partialTick) {
            super(window, guiGraphics, partialTick);
        }
    }

    @Cancelable
    public static class Pre
    extends RenderGuiEvent {
        @ApiStatus.Internal
        public Pre(Window window, GuiGraphics guiGraphics, float partialTick) {
            super(window, guiGraphics, partialTick);
        }
    }
}

