/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Options
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package dev.latvian.mods.kubejs.core.mixin.common;

import dev.latvian.mods.kubejs.client.KubeJSClient;
import java.io.File;
import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={Options.class})
public class OptionsMixin {
    @Shadow
    @Final
    private File f_92110_;

    @Inject(method={"load"}, at={@At(value="HEAD")})
    private void loadKJS(CallbackInfo ci) {
        KubeJSClient.copyDefaultOptionsFile(this.f_92110_);
    }
}

