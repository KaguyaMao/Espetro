/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraftforge.network.NetworkEvent$Context
 */
package org.espetro.network;

import java.util.UUID;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import org.espetro.bastion.FortificationManager;

public record FortificationWorkPacket(@Nullable BlockPos blockTarget, @Nullable UUID entityTarget, boolean build) {
    public static FortificationWorkPacket block(BlockPos target, boolean build) {
        return new FortificationWorkPacket(target, null, build);
    }

    public static FortificationWorkPacket entity(UUID target, boolean build) {
        return new FortificationWorkPacket(null, target, build);
    }

    public static FortificationWorkPacket read(FriendlyByteBuf buf) {
        boolean entity = buf.readBoolean();
        UUID entityTarget = entity ? buf.m_130259_() : null;
        BlockPos blockTarget = entity ? null : buf.m_130135_();
        return new FortificationWorkPacket(blockTarget, entityTarget, buf.readBoolean());
    }

    public void write(FriendlyByteBuf buf) {
        boolean entity = this.entityTarget != null;
        buf.writeBoolean(entity);
        if (entity) {
            buf.m_130077_(this.entityTarget);
        } else {
            buf.m_130064_(this.blockTarget == null ? BlockPos.f_121853_ : this.blockTarget);
        }
        buf.writeBoolean(this.build);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) {
                return;
            }
            if (this.entityTarget != null) {
                FortificationManager.getInstance().workEntity(player, this.entityTarget, this.build);
            } else if (this.blockTarget != null) {
                FortificationManager.getInstance().work(player, this.blockTarget, this.build);
            }
        });
        context.setPacketHandled(true);
    }
}

