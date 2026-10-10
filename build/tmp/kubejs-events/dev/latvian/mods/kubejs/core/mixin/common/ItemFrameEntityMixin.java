/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.latvian.mods.rhino.util.RemapPrefixForJS
 *  net.minecraft.world.entity.decoration.ItemFrame
 *  org.spongepowered.asm.mixin.Mixin
 */
package dev.latvian.mods.kubejs.core.mixin.common;

import dev.latvian.mods.kubejs.core.ItemFrameEntityKJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.world.entity.decoration.ItemFrame;
import org.spongepowered.asm.mixin.Mixin;

@RemapPrefixForJS(value="kjs$")
@Mixin(value={ItemFrame.class})
public abstract class ItemFrameEntityMixin
implements ItemFrameEntityKJS {
}

