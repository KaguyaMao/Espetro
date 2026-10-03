/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.latvian.mods.rhino.util.RemapPrefixForJS
 *  net.minecraft.world.entity.item.ItemEntity
 *  org.spongepowered.asm.mixin.Mixin
 */
package dev.latvian.mods.kubejs.core.mixin.common;

import dev.latvian.mods.kubejs.core.ItemEntityKJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;

@RemapPrefixForJS(value="kjs$")
@Mixin(value={ItemEntity.class})
public abstract class ItemEntityMixin
implements ItemEntityKJS {
}

