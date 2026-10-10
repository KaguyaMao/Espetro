/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.architectury.networking.NetworkManager$PacketContext
 *  dev.architectury.networking.simple.BaseC2SMessage
 *  dev.architectury.networking.simple.MessageType
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 */
package dev.latvian.mods.kubejs.net;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseC2SMessage;
import dev.architectury.networking.simple.MessageType;
import dev.latvian.mods.kubejs.bindings.event.ItemEvents;
import dev.latvian.mods.kubejs.item.ItemClickedEventJS;
import dev.latvian.mods.kubejs.net.KubeJSNet;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.script.ScriptTypeHolder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class FirstClickMessage
extends BaseC2SMessage {
    private final int type;

    public FirstClickMessage(int t) {
        this.type = t;
    }

    FirstClickMessage(FriendlyByteBuf buf) {
        this.type = buf.readByte();
    }

    public MessageType getType() {
        return KubeJSNet.FIRST_CLICK;
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeByte(this.type);
    }

    public void handle(NetworkManager.PacketContext context) {
        InteractionHand[] interactionHandArray = context.getPlayer();
        if (interactionHandArray instanceof ServerPlayer) {
            ServerPlayer serverPlayer = (ServerPlayer)interactionHandArray;
            if (this.type == 0 && ItemEvents.FIRST_LEFT_CLICKED.hasListeners()) {
                ItemStack stack = serverPlayer.m_21120_(InteractionHand.MAIN_HAND);
                ItemEvents.FIRST_LEFT_CLICKED.post((ScriptTypeHolder)ScriptType.SERVER, (Object)stack.m_41720_(), new ItemClickedEventJS((Player)serverPlayer, InteractionHand.MAIN_HAND, stack));
            } else if (this.type == 1 && ItemEvents.FIRST_RIGHT_CLICKED.hasListeners()) {
                for (InteractionHand hand : InteractionHand.values()) {
                    ItemStack stack = serverPlayer.m_21120_(hand);
                    ItemEvents.FIRST_RIGHT_CLICKED.post((ScriptTypeHolder)ScriptType.SERVER, (Object)stack.m_41720_(), new ItemClickedEventJS((Player)serverPlayer, hand, stack));
                }
            }
        }
    }
}

