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
import org.espetro.Espetro;

public final class OutpostSupplySyncPacket {
    private final boolean inRange;
    private final int radioHealth;
    private final int radioMaxHealth;
    private final int ammunition;
    private final int construction;
    private final boolean habEnabled;

    public OutpostSupplySyncPacket(boolean inRange, int radioHealth, int radioMaxHealth, int ammunition, int construction, boolean habEnabled) {
        this.inRange = inRange;
        this.radioHealth = Math.max(0, radioHealth);
        this.radioMaxHealth = Math.max(1, radioMaxHealth);
        this.ammunition = Math.max(0, ammunition);
        this.construction = Math.max(0, construction);
        this.habEnabled = habEnabled;
    }

    public static OutpostSupplySyncPacket outOfRange() {
        return new OutpostSupplySyncPacket(false, 0, 1, 0, 0, false);
    }

    public static OutpostSupplySyncPacket read(FriendlyByteBuf buf) {
        boolean inRange = buf.readBoolean();
        if (!inRange) {
            return OutpostSupplySyncPacket.outOfRange();
        }
        return new OutpostSupplySyncPacket(true, buf.m_130242_(), buf.m_130242_(), buf.m_130242_(), buf.m_130242_(), buf.readBoolean());
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeBoolean(this.inRange);
        if (!this.inRange) {
            return;
        }
        buf.m_130130_(this.radioHealth);
        buf.m_130130_(this.radioMaxHealth);
        buf.m_130130_(this.ammunition);
        buf.m_130130_(this.construction);
        buf.writeBoolean(this.habEnabled);
    }

    public boolean isInRange() {
        return this.inRange;
    }

    public int getRadioHealth() {
        return this.radioHealth;
    }

    public int getRadioMaxHealth() {
        return this.radioMaxHealth;
    }

    public int getAmmunition() {
        return this.ammunition;
    }

    public int getConstruction() {
        return this.construction;
    }

    public boolean isHabEnabled() {
        return this.habEnabled;
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            try {
                Class.forName("org.espetro.client.ClientPacketHandlers").getMethod("handleOutpostSupplySync", OutpostSupplySyncPacket.class).invoke(null, this);
            }
            catch (Exception e) {
                Espetro.LOGGER.error("\u5904\u7406\u524d\u54e8\u8865\u7ed9\u540c\u6b65\u5931\u8d25", (Throwable)e);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}

