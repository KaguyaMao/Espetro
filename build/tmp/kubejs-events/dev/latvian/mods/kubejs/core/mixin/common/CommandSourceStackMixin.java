/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.latvian.mods.rhino.util.HideFromJS
 *  dev.latvian.mods.rhino.util.RemapPrefixForJS
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.network.chat.Component
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 */
package dev.latvian.mods.kubejs.core.mixin.common;

import dev.latvian.mods.kubejs.core.LazyComponentKJS;
import dev.latvian.mods.rhino.util.HideFromJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import java.util.function.Supplier;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@RemapPrefixForJS(value="kjs$")
@Mixin(value={CommandSourceStack.class})
public abstract class CommandSourceStackMixin {
    @Shadow
    @HideFromJS
    public abstract void m_288197_(Supplier<Component> var1, boolean var2);

    @Unique
    public void kjs$sendSuccess(Component component, boolean broadcastToAdmins) {
        this.kjs$sendSuccessLazy(() -> component, broadcastToAdmins);
    }

    @Unique
    public void kjs$sendSuccessLazy(LazyComponentKJS component, boolean broadcastToAdmins) {
        this.m_288197_(component, broadcastToAdmins);
    }
}

