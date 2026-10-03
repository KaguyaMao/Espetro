/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.components.AbstractSelectionList
 *  net.minecraft.client.gui.components.AbstractSelectionList$Entry
 *  org.spongepowered.asm.mixin.Mixin
 */
package dev.latvian.mods.kubejs.core.mixin.common;

import net.minecraft.client.gui.components.AbstractSelectionList;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value={AbstractSelectionList.class})
public abstract class AbstractSelectionListMixin<E extends AbstractSelectionList.Entry<E>> {
}

