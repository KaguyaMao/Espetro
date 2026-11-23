package org.espetro.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * 己方 Radio 覆盖范围内的建材/弹药库存同步（S→C）。
 * construction &lt; 0 表示离开覆盖范围，客户端隐藏 HUD。
 */
public class FobSupplySyncPacket {
    public enum HabStatus { UNKNOWN, INACTIVE, ACTIVE }

    private final int construction;
    private final int ammunition;
    private final int maxConstruction;
    private final int maxAmmunition;
    private final boolean inRange;
    private final int radioHealth;
    private final int radioMaxHealth;
    private final HabStatus habStatus;

    public FobSupplySyncPacket(boolean inRange, int construction, int ammunition,
                               int maxConstruction, int maxAmmunition,
                               int radioHealth, int radioMaxHealth) {
        this(inRange, construction, ammunition, maxConstruction, maxAmmunition,
            radioHealth, radioMaxHealth, HabStatus.UNKNOWN);
    }

    public FobSupplySyncPacket(boolean inRange, int construction, int ammunition,
                               int maxConstruction, int maxAmmunition,
                               int radioHealth, int radioMaxHealth, HabStatus habStatus) {
        this.inRange = inRange;
        this.construction = construction;
        this.ammunition = ammunition;
        this.maxConstruction = maxConstruction;
        this.maxAmmunition = maxAmmunition;
        this.radioHealth = Math.max(0, radioHealth);
        this.radioMaxHealth = Math.max(1, radioMaxHealth);
        this.habStatus = habStatus;
    }

    public static FobSupplySyncPacket outOfRange() {
        return new FobSupplySyncPacket(false, 0, 0, 0, 0, 0, 1);
    }

    public static FobSupplySyncPacket read(FriendlyByteBuf buf) {
        boolean inRange = buf.readBoolean();
        if (!inRange) {
            return outOfRange();
        }
        int construction = buf.readVarInt();
        int ammunition = buf.readVarInt();
        int maxConstruction = buf.readVarInt();
        int maxAmmunition = buf.readVarInt();
        int radioHealth = buf.readVarInt();
        int radioMaxHealth = buf.readVarInt();
        // The existing fields stay at the same offsets for older clients/servers.
        HabStatus hab = !buf.isReadable() ? HabStatus.UNKNOWN : switch (buf.readUnsignedByte()) {
            case 1 -> HabStatus.INACTIVE;
            case 2 -> HabStatus.ACTIVE;
            default -> HabStatus.UNKNOWN;
        };
        return new FobSupplySyncPacket(true, construction, ammunition, maxConstruction,
            maxAmmunition, radioHealth, radioMaxHealth, hab);
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeBoolean(inRange);
        if (inRange) {
            buf.writeVarInt(construction);
            buf.writeVarInt(ammunition);
            buf.writeVarInt(maxConstruction);
            buf.writeVarInt(maxAmmunition);
            buf.writeVarInt(radioHealth);
            buf.writeVarInt(radioMaxHealth);
            buf.writeByte(switch (habStatus) {
                case UNKNOWN -> 0;
                case INACTIVE -> 1;
                case ACTIVE -> 2;
            });
        }
    }

    public boolean isInRange() { return inRange; }
    public int getConstruction() { return construction; }
    public int getAmmunition() { return ammunition; }
    public int getMaxConstruction() { return maxConstruction; }
    public int getMaxAmmunition() { return maxAmmunition; }
    public int getRadioHealth() { return radioHealth; }
    public int getRadioMaxHealth() { return radioMaxHealth; }
    public HabStatus getHabStatus() { return habStatus; }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            try {
                Class.forName("org.espetro.client.ClientPacketHandlers")
                    .getMethod("handleFobSupplySync", FobSupplySyncPacket.class)
                    .invoke(null, this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
