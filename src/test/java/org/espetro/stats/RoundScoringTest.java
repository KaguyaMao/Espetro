package org.espetro.stats;

import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RoundScoringTest {
    @Test
    void onlyPreviouslyUnworkedProgressPaysOnEachSiteAndDirection() {
        var ranges = new TreeMap<Integer, Integer>();
        assertTrue(RoundScoring.claimWorkRange(ranges, 0, 5));
        assertFalse(RoundScoring.claimWorkRange(ranges, 0, 5));
        assertFalse(RoundScoring.claimWorkRange(ranges, 1, 4));
        assertTrue(RoundScoring.claimWorkRange(ranges, 10, 15));
        assertTrue(RoundScoring.claimWorkRange(ranges, 4, 11));
        assertEquals(Map.of(0, 15), ranges);
        assertFalse(RoundScoring.claimWorkRange(ranges, 5, 10));
    }

    @Test
    void scoresRoundOnceAfterAggregatingDeliveryAndPassengerDistance() {
        assertEquals(27, RoundScoring.pointsForUnits(135, 20, 100));
        assertEquals(1, RoundScoring.distanceUnits(99, 100));
        assertEquals(1, RoundScoring.distanceUnits(100, 100));
        assertEquals(2, RoundScoring.distanceUnits(101, 100));
        assertEquals(3, RoundScoring.pointsForUnits(10 * RoundScoring.distanceUnits(500, 100), 5, 100));
    }
}
