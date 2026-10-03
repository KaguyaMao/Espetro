/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.KeyboardHandler
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.sighs.apricityui.mixin;

import com.sighs.apricityui.render.Operation;
import com.sighs.apricityui.spi.AuiServices;
import net.minecraft.client.KeyboardHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={KeyboardHandler.class})
public abstract class CommonKeyboardHandlerMixin {
    @Inject(method={"charTyped"}, at={@At(value="HEAD")}, cancellable=true)
    private void apricityui$dispatchCharTyped(long window, int codePoint, int modifiers, CallbackInfo ci) {
        boolean allowed;
        long mainWindow = AuiServices.client().getWindowHandle();
        if (mainWindow == 0L || window != mainWindow) {
            return;
        }
        boolean bl = allowed = Character.isValidCodePoint(codePoint) && codePoint >= 32 && codePoint != 127 && codePoint != 167;
        if (allowed && Operation.onCharTyped(codePoint)) {
            ci.cancel();
        }
    }
}

