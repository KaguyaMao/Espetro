package org.espetro.mixin.client;

import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.components.ChatComponent;
import org.espetro.client.gui.TacticalHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatComponent.class)
abstract class BattleChatLinesMixin {
    // Only the message renderer reads a zero background opacity. The saved game option stays intact.
    @Redirect(method = "render",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/OptionInstance;get()Ljava/lang/Object;"),
        slice = @Slice(
            from = @At(value = "INVOKE", target = "Lnet/minecraft/client/Options;textBackgroundOpacity()Lnet/minecraft/client/OptionInstance;"),
            to = @At(value = "INVOKE", target = "Lnet/minecraft/client/Options;chatLineSpacing()Lnet/minecraft/client/OptionInstance;")),
        require = 1)
    private Object espetro$transparentChatMessages(OptionInstance<Double> option) {
        return 0.0D;
    }

    @Inject(method = "getLinesPerPage", at = @At("RETURN"), cancellable = true, require = 1)
    private void espetro$limitBattleChat(CallbackInfoReturnable<Integer> callback) {
        int original = callback.getReturnValue();
        int limited = TacticalHud.chatLineLimit(original);
        if (limited != original) callback.setReturnValue(limited);
    }
}
