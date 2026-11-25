package org.espetro.network;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RoundReportTest {
    @Test
    void roundEndPacketKeepsOrderedChangesAndVehicleOccupants() {
        var crew = List.of("Driver", "Gunner", "Passenger");
        var vehicle = new RoundReport.Reason(RoundReport.Cause.VEHICLE, "步兵战车被击毁",
            "ifv|map_ifv.png", "2号小队 · 装甲支援", crew);
        var capture = RoundReport.Reason.of(RoundReport.Cause.CAPTURE, "占领据点");
        var report = new RoundReport(900, 400, 350,
            List.of(new RoundReport.Ticket(250, true, 400, 365, vehicle),
                new RoundReport.Ticket(250, false, 350, 375, capture),
                new RoundReport.Ticket(900, false, 375, 0,
                    RoundReport.Reason.of(RoundReport.Cause.CAPTURE, "全部据点失守"))),
            List.of(new RoundReport.Player(UUID.randomUUID(), "Driver", true, 2, "装甲支援",
                (byte) 0, true, true, "车组长", "crewman", "", "crew", 9, 2)));
        var packet = new RoundEndPacket("ATTACK", RoundReport.DISPLAY_SECONDS, "进攻方", "防守方",
            365, 0, 3, false, report);
        var buffer = new FriendlyByteBuf(Unpooled.buffer());
        try {
            packet.write(buffer);
            var decoded = RoundEndPacket.read(buffer);
            assertEquals(report, decoded.report);
            assertEquals(30, decoded.displaySeconds);
            assertEquals(365, decoded.attackTickets);
            assertEquals(0, buffer.readableBytes());
        } finally {
            buffer.release();
        }
    }
}
