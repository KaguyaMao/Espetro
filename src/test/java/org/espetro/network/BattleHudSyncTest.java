package org.espetro.network;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class BattleHudSyncTest {
    @Test
    void legacyFobPacketsDoNotClaimRespawnIsAvailable() {
        FriendlyByteBuf wire = new FriendlyByteBuf(Unpooled.buffer());
        try {
            wire.writeBoolean(true);
            for (int value : new int[]{63, 745, 1000, 1000, 82, 100}) wire.writeVarInt(value);
            FobSupplySyncPacket packet = FobSupplySyncPacket.read(wire);
            assertEquals(745, packet.getAmmunition());
            assertEquals(82, packet.getRadioHealth());
            assertEquals(FobSupplySyncPacket.HabStatus.UNKNOWN, packet.getHabStatus());
        } finally {
            wire.release();
        }
    }

    @Test
    void modernFobPacketsKeepLegacyFieldsAndRoundTripHabStatus() {
        FriendlyByteBuf wire = new FriendlyByteBuf(Unpooled.buffer());
        try {
            new FobSupplySyncPacket(true, 63, 745, 1000, 1000, 82, 100,
                FobSupplySyncPacket.HabStatus.ACTIVE).write(wire);
            wire.markReaderIndex();
            assertTrue(wire.readBoolean());
            for (int value : new int[]{63, 745, 1000, 1000, 82, 100}) assertEquals(value, wire.readVarInt());
            wire.resetReaderIndex();
            assertEquals(FobSupplySyncPacket.HabStatus.ACTIVE, FobSupplySyncPacket.read(wire).getHabStatus());
            assertFalse(wire.isReadable());
        } finally {
            wire.release();
        }
    }

    @Test
    void scoreboardKeepsVisibleSquadNumberSeparateFromInternalId() {
        FriendlyByteBuf wire = new FriendlyByteBuf(Unpooled.buffer());
        UUID id = UUID.randomUUID();
        try {
            MatchStatsSyncPacket original = new MatchStatsSyncPacket(List.of(
                new MatchStatsSyncPacket.Row(id, "Player", "ATTACK", 0, 0, "medic", "medic", true, 42, "Alpha")));
            original.write(wire);
            wire.markReaderIndex();
            assertNull(MatchStatsSyncPacket.read(wire).squadNumber(id));
            wire.writeVarInt(7);
            wire.resetReaderIndex();
            MatchStatsSyncPacket modern = MatchStatsSyncPacket.read(wire);
            assertEquals(42, modern.rows.get(0).squadId);
            assertEquals(7, modern.squadNumber(id));
            wire.clear();
            modern.write(wire);
            assertEquals(7, MatchStatsSyncPacket.read(wire).squadNumber(id));
        } finally {
            wire.release();
        }
    }
}
