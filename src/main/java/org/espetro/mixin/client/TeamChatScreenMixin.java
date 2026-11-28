package org.espetro.mixin.client;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatScreen.class)
abstract class TeamChatScreenMixin extends Screen {
    @Unique private static boolean espetro$teamChat;
    @Unique private boolean espetro$restoreInputFocus;
    @Shadow protected EditBox input;
    protected TeamChatScreenMixin(Component title){super(title);}

    @Inject(method="init",at=@At("TAIL"),require=1)
    private void espetro$addTeamToggle(CallbackInfo ci){
        addRenderableWidget(Button.builder(espetro$caption(),button -> {
            espetro$teamChat=!espetro$teamChat;
            button.setMessage(espetro$caption());
            espetro$restoreInputFocus=true;
            setFocused(input);
            input.setFocused(true);
        }).bounds(4,height-34,88,18).build());
    }
    @Inject(method="mouseClicked",at=@At("RETURN"),require=1)
    private void espetro$keepTyping(double x,double y,int button,CallbackInfoReturnable<Boolean> cir){
        if(!espetro$restoreInputFocus)return;
        espetro$restoreInputFocus=false;
        setFocused(input);
        input.setFocused(true);
    }

    @Unique private static Component espetro$caption(){
        return Component.literal("队内交流："+(espetro$teamChat?"开":"关"));
    }
    @ModifyVariable(method="handleChatInput",at=@At("HEAD"),argsOnly=true,ordinal=0,require=1)
    private String espetro$routeTeamMessage(String text){
        return !espetro$teamChat || text.isBlank() || text.stripLeading().startsWith("/")
            ? text : "/teammsg "+text.stripLeading();
    }
}
