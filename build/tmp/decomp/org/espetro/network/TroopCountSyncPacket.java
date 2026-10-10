/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.network.NetworkEvent$Context
 */
package org.espetro.network;

import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class TroopCountSyncPacket {
    private final int attackTroops;
    private final int defendTroops;

    public TroopCountSyncPacket(int attackTroops, int defendTroops) {
        this.attackTroops = attackTroops;
        this.defendTroops = defendTroops;
    }

    public static TroopCountSyncPacket read(FriendlyByteBuf buf) {
        int attack = buf.readInt();
        int defend = buf.readInt();
        return new TroopCountSyncPacket(attack, defend);
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeInt(this.attackTroops);
        buf.writeInt(this.defendTroops);
    }

    public int getAttackTroops() {
        return this.attackTroops;
    }

    public int getDefendTroops() {
        return this.defendTroops;
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            try {
                Class.forName("org.espetro.client.ClientPacketHandlers").getMethod("handleTroopCount", TroopCountSyncPacket.class).invoke(null, this);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        });
        ctx.get().setPacketHandled(true);
    }
}

