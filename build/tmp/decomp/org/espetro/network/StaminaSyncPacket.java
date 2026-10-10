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

public class StaminaSyncPacket {
    private final boolean enabled;
    private final int stamina;
    private final int maxStamina;
    private final int jumpStaminaCost;

    public StaminaSyncPacket(boolean enabled, int stamina, int maxStamina, int jumpStaminaCost) {
        this.enabled = enabled;
        this.stamina = stamina;
        this.maxStamina = maxStamina;
        this.jumpStaminaCost = jumpStaminaCost;
    }

    public static StaminaSyncPacket read(FriendlyByteBuf buf) {
        return new StaminaSyncPacket(buf.readBoolean(), buf.m_130242_(), buf.m_130242_(), buf.m_130242_());
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeBoolean(this.enabled);
        buf.m_130130_(this.stamina);
        buf.m_130130_(this.maxStamina);
        buf.m_130130_(this.jumpStaminaCost);
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public int getStamina() {
        return this.stamina;
    }

    public int getMaxStamina() {
        return this.maxStamina;
    }

    public int getJumpStaminaCost() {
        return this.jumpStaminaCost;
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            try {
                Class.forName("org.espetro.client.ClientPacketHandlers").getMethod("handleStamina", StaminaSyncPacket.class).invoke(null, this);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        });
        ctx.get().setPacketHandled(true);
    }
}

