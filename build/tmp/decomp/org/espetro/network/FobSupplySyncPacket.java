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

public class FobSupplySyncPacket {
    private final int construction;
    private final int ammunition;
    private final int maxConstruction;
    private final int maxAmmunition;
    private final boolean inRange;
    private final int radioHealth;
    private final int radioMaxHealth;

    public FobSupplySyncPacket(boolean inRange, int construction, int ammunition, int maxConstruction, int maxAmmunition, int radioHealth, int radioMaxHealth) {
        this.inRange = inRange;
        this.construction = construction;
        this.ammunition = ammunition;
        this.maxConstruction = maxConstruction;
        this.maxAmmunition = maxAmmunition;
        this.radioHealth = Math.max(0, radioHealth);
        this.radioMaxHealth = Math.max(1, radioMaxHealth);
    }

    public static FobSupplySyncPacket outOfRange() {
        return new FobSupplySyncPacket(false, 0, 0, 0, 0, 0, 1);
    }

    public static FobSupplySyncPacket read(FriendlyByteBuf buf) {
        boolean inRange = buf.readBoolean();
        if (!inRange) {
            return FobSupplySyncPacket.outOfRange();
        }
        return new FobSupplySyncPacket(true, buf.m_130242_(), buf.m_130242_(), buf.m_130242_(), buf.m_130242_(), buf.m_130242_(), buf.m_130242_());
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeBoolean(this.inRange);
        if (this.inRange) {
            buf.m_130130_(this.construction);
            buf.m_130130_(this.ammunition);
            buf.m_130130_(this.maxConstruction);
            buf.m_130130_(this.maxAmmunition);
            buf.m_130130_(this.radioHealth);
            buf.m_130130_(this.radioMaxHealth);
        }
    }

    public boolean isInRange() {
        return this.inRange;
    }

    public int getConstruction() {
        return this.construction;
    }

    public int getAmmunition() {
        return this.ammunition;
    }

    public int getMaxConstruction() {
        return this.maxConstruction;
    }

    public int getMaxAmmunition() {
        return this.maxAmmunition;
    }

    public int getRadioHealth() {
        return this.radioHealth;
    }

    public int getRadioMaxHealth() {
        return this.radioMaxHealth;
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            try {
                Class.forName("org.espetro.client.ClientPacketHandlers").getMethod("handleFobSupplySync", FobSupplySyncPacket.class).invoke(null, this);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        });
        ctx.get().setPacketHandled(true);
    }
}

