package org.espetro.network;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SquadInfoPacketTest {

    @Test
    void preservesInternalAndFactionDisplayIdsSeparately() {
        var original = new UnifiedDeployScreenPacket.SquadInfo(
            9, 3, "Alpha", 1, 9, false,
            "Leader", "infantry", "Infantry", List.of());
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        try {
            original.write(buffer);
            var decoded = new UnifiedDeployScreenPacket.SquadInfo(buffer);

            assertEquals(9, decoded.id);
            assertEquals(3, decoded.displayId);
        } finally {
            buffer.release();
        }
    }
}
