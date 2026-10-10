/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.network.NetworkEvent$Context
 */
package org.espetro.network;

import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import org.espetro.tutorial.TutorialManager;

public class TutorialActionPacket {
    public static final byte ACTION_NEXT = 0;
    public static final byte ACTION_SKIP_ALL = 1;
    public static final byte ACTION_DISMISS = 2;
    public static final byte ACTION_REOPEN = 3;
    private final byte action;
    private final String stepId;

    public TutorialActionPacket(byte action, String stepId) {
        this.action = action;
        this.stepId = stepId == null ? "" : stepId;
    }

    public static TutorialActionPacket next(String stepId) {
        return new TutorialActionPacket(0, stepId);
    }

    public static TutorialActionPacket skipAll() {
        return new TutorialActionPacket(1, "");
    }

    public static TutorialActionPacket dismiss(String stepId) {
        return new TutorialActionPacket(2, stepId);
    }

    public static TutorialActionPacket reopen() {
        return new TutorialActionPacket(3, "");
    }

    public static TutorialActionPacket read(FriendlyByteBuf buf) {
        byte action = buf.readByte();
        String stepId = buf.m_130277_();
        return new TutorialActionPacket(action, stepId);
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeByte(this.action);
        buf.m_130070_(this.stepId);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ((NetworkEvent.Context)ctx.get()).getSender();
            if (player == null) {
                return;
            }
            if (this.action == 3) {
                TutorialManager.getInstance().reopen(player);
                return;
            }
            TutorialManager.Action mapped = switch (this.action) {
                case 1 -> TutorialManager.Action.SKIP_ALL;
                case 2 -> TutorialManager.Action.DISMISS;
                default -> TutorialManager.Action.NEXT;
            };
            TutorialManager.getInstance().handleAction(player, mapped, this.stepId);
        });
        ctx.get().setPacketHandled(true);
    }
}

