/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraftforge.network.NetworkEvent$Context
 */
package tech.vvp.vvp.network.message;

import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkEvent;
import tech.vvp.vvp.entity.vehicle.PantsirS1Entity;

public class PantsirLockRequestMessage {
    public static final int ACTION_START_LOCK = 0;
    public static final int ACTION_CANCEL_LOCK = 1;
    public static final int ACTION_NEXT_TARGET = 2;
    public static final int ACTION_PREV_TARGET = 3;
    public final int action;

    public PantsirLockRequestMessage(int action) {
        this.action = action;
    }

    public static void encode(PantsirLockRequestMessage message, FriendlyByteBuf buffer) {
        buffer.writeInt(message.action);
    }

    public static PantsirLockRequestMessage decode(FriendlyByteBuf buffer) {
        return new PantsirLockRequestMessage(buffer.readInt());
    }

    public static void handler(PantsirLockRequestMessage message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ((NetworkEvent.Context)ctx.get()).getSender();
            if (player == null) {
                return;
            }
            Entity vehicle = player.m_20202_();
            if (!(vehicle instanceof PantsirS1Entity)) {
                return;
            }
            PantsirS1Entity pantsir = (PantsirS1Entity)vehicle;
            int seatIndex = pantsir.getSeatIndex((Entity)player);
            if (seatIndex != 1) {
                return;
            }
            switch (message.action) {
                case 0: {
                    pantsir.requestLock((LivingEntity)player);
                    break;
                }
                case 1: {
                    pantsir.cancelLock((LivingEntity)player);
                    break;
                }
                case 2: {
                    pantsir.selectNextTarget();
                    break;
                }
                case 3: {
                    pantsir.selectPrevTarget();
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}

