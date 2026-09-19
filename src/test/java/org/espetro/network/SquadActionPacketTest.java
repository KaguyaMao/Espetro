package org.espetro.network;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import org.espetro.team.Fireteam;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SquadActionPacketTest {

    @Test
    void appointFireteamLeaderCarriesTargetAndFireteam() {
        UUID target = UUID.randomUUID();
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        try {
            SquadActionPacket.appointFireteamLeader(target, Fireteam.C).write(buffer);

            assertEquals("APPOINT_FIRETEAM_LEADER", buffer.readUtf());
            buffer.readVarInt();
            buffer.readUtf();
            assertTrue(buffer.readBoolean());
            assertEquals(target, buffer.readUUID());
            assertEquals(Fireteam.C.toNetwork(), buffer.readByte());
        } finally {
            buffer.release();
        }
    }
}
