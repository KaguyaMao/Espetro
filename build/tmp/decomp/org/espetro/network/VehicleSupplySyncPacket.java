/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.network.NetworkEvent$Context
 *  net.minecraftforge.network.PacketDistributor
 */
package org.espetro.network;

import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import org.espetro.Espetro;
import org.espetro.network.NetworkManager;
import org.espetro.network.VehicleSupplyActionPacket;

public final class VehicleSupplySyncPacket {
    private final boolean request;
    private final UUID vehicleId;
    private final int ammo;
    private final int construction;
    private final int maxCapacity;
    private final boolean supplyVehicle;
    private final boolean fightVehicle;
    private final boolean transferAmmo;
    private final boolean transferConstruction;
    private final int transferIntervalTicks;

    private VehicleSupplySyncPacket(boolean request, UUID vehicleId, int ammo, int construction, int maxCapacity, boolean supplyVehicle, boolean fightVehicle, boolean transferAmmo, boolean transferConstruction, int transferIntervalTicks) {
        this.request = request;
        this.vehicleId = vehicleId;
        this.ammo = ammo;
        this.construction = construction;
        this.maxCapacity = maxCapacity;
        this.supplyVehicle = supplyVehicle;
        this.fightVehicle = fightVehicle;
        this.transferAmmo = transferAmmo;
        this.transferConstruction = transferConstruction;
        this.transferIntervalTicks = Math.max(1, transferIntervalTicks);
    }

    public static VehicleSupplySyncPacket request(UUID vehicleId) {
        return new VehicleSupplySyncPacket(true, vehicleId, 0, 0, 0, false, false, false, false, 20);
    }

    public static VehicleSupplySyncPacket state(UUID vehicleId, int ammo, int construction, int maxCapacity, boolean supplyVehicle, boolean fightVehicle, boolean transferAmmo, boolean transferConstruction, int transferIntervalTicks) {
        return new VehicleSupplySyncPacket(false, vehicleId, ammo, construction, maxCapacity, supplyVehicle, fightVehicle, transferAmmo, transferConstruction, transferIntervalTicks);
    }

    public UUID getVehicleId() {
        return this.vehicleId;
    }

    public int getAmmo() {
        return this.ammo;
    }

    public int getConstruction() {
        return this.construction;
    }

    public int getMaxCapacity() {
        return this.maxCapacity;
    }

    public boolean isSupplyVehicle() {
        return this.supplyVehicle;
    }

    public boolean isFightVehicle() {
        return this.fightVehicle;
    }

    public boolean canTransferAmmo() {
        return this.transferAmmo;
    }

    public boolean canTransferConstruction() {
        return this.transferConstruction;
    }

    public boolean canResupplyInfantry() {
        return !this.request;
    }

    public int getTransferIntervalTicks() {
        return this.transferIntervalTicks;
    }

    public boolean canCarryConstruction() {
        return this.supplyVehicle;
    }

    public boolean isRequest() {
        return this.request;
    }

    public boolean hasAnyAction() {
        return this.transferAmmo || this.transferConstruction || this.canResupplyInfantry();
    }

    public static VehicleSupplySyncPacket read(FriendlyByteBuf buf) {
        boolean request = buf.readBoolean();
        UUID id = buf.m_130259_();
        if (request) {
            return VehicleSupplySyncPacket.request(id);
        }
        return VehicleSupplySyncPacket.state(id, buf.m_130242_(), buf.m_130242_(), buf.m_130242_(), buf.readBoolean(), buf.readBoolean(), buf.readBoolean(), buf.readBoolean(), buf.m_130242_());
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeBoolean(this.request);
        buf.m_130077_(this.vehicleId);
        if (this.request) {
            return;
        }
        buf.m_130130_(this.ammo);
        buf.m_130130_(this.construction);
        buf.m_130130_(this.maxCapacity);
        buf.writeBoolean(this.supplyVehicle);
        buf.writeBoolean(this.fightVehicle);
        buf.writeBoolean(this.transferAmmo);
        buf.writeBoolean(this.transferConstruction);
        buf.m_130130_(this.transferIntervalTicks);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer sender = context.getSender();
            if (sender != null) {
                VehicleSupplySyncPacket response = VehicleSupplyActionPacket.createSyncResponse(sender, this.vehicleId);
                if (response != null) {
                    NetworkManager.NET.send(PacketDistributor.PLAYER.with(() -> sender), (Object)response);
                }
            } else {
                try {
                    Class.forName("org.espetro.client.ClientPacketHandlers").getMethod("handleVehicleSupplySync", VehicleSupplySyncPacket.class).invoke(null, this);
                }
                catch (ReflectiveOperationException e) {
                    Espetro.LOGGER.error("\u5904\u7406\u8f7d\u5177\u8865\u7ed9\u540c\u6b65\u5931\u8d25", (Throwable)e);
                }
            }
        });
        context.setPacketHandled(true);
    }
}

