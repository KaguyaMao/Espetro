package org.espetro.client.gui;

import org.espetro.network.RoundReport;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RoundEndDetailsTest {
    @Test
    void regularDeductionsShareTrendSamplesAndMarkedJumpsKeepBothEndpoints() {
        var vehicle = RoundReport.Reason.of(RoundReport.Cause.VEHICLE, "载具被击毁");
        var report = new RoundReport(3200, 100, 120, List.of(
            new RoundReport.Ticket(20, true, 100, 99, RoundReport.Reason.OTHER),
            new RoundReport.Ticket(40, true, 99, 98, RoundReport.Reason.OTHER),
            new RoundReport.Ticket(100, false, 120, 119, RoundReport.Reason.OTHER),
            new RoundReport.Ticket(300, true, 98, 75, vehicle),
            new RoundReport.Ticket(330, true, 75, 74, RoundReport.Reason.OTHER),
            new RoundReport.Ticket(370, true, 74, 73, RoundReport.Reason.OTHER)
        ), List.of());

        assertEquals(List.of(
            new RoundEndDetails.Point(0, 100),
            new RoundEndDetails.Point(40, 98),
            new RoundEndDetails.Point(300, 98),
            new RoundEndDetails.Point(300, 75),
            new RoundEndDetails.Point(370, 73),
            new RoundEndDetails.Point(3200, 73)
        ), RoundEndDetails.trend(report, true));
        assertEquals(6, report.tickets().size());
    }
}
