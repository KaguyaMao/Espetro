package org.espetro.mixin;

import net.minecraft.world.item.ItemStack;
import org.espetro.compat.OptionalExplosiveInteractions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
abstract class OptionalExplosiveUseDurationMixin {
    @Inject(method="getUseDuration",at=@At("HEAD"),cancellable=true,require=1)
    private void espetro$holdDuration(CallbackInfoReturnable<Integer> cir){
        if(OptionalExplosiveInteractions.usesHoldDuration((ItemStack)(Object)this))
            cir.setReturnValue(OptionalExplosiveInteractions.HOLD_TICKS);
    }
}
