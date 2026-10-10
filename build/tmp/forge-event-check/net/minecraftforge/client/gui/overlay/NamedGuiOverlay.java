/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 */
package net.minecraftforge.client.gui.overlay;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public record NamedGuiOverlay(ResourceLocation id, IGuiOverlay overlay) {
}

