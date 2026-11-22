package org.espetro.client.gui;

import org.espetro.network.GovernanceStatePacket;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DeploySnapshotsTest {

    @AfterEach
    void resetFreeUnlock() {
        DeploySnapshots.applyFreeUnlock(false);
    }

    @Test
    void cooldownIsMinutesAndZeroPaddedSeconds() {
        assertEquals("0:00", DeploySnapshots.formatCooldown(0));
        assertEquals("0:09", DeploySnapshots.formatCooldown(9));
        assertEquals("1:05", DeploySnapshots.formatCooldown(65));
        assertEquals("12:00", DeploySnapshots.formatCooldown(720));
        assertEquals("0:00", DeploySnapshots.formatCooldown(-30));
    }

    @Test
    void deploymentCoordinatesKeepXAndZ() {
        assertArrayEquals(new double[] {12.5, -40.0},
            DeploySnapshots.parseDeploymentCoordinates("坐标 12.5, 64, -40"));
        assertArrayEquals(new double[] {1.0, 3.0},
            DeploySnapshots.parseDeploymentCoordinates("(1 2 3 4)"));
    }

    @Test
    void deploymentCoordinatesNeedThreeNumbers() {
        assertNull(DeploySnapshots.parseDeploymentCoordinates(null));
        assertNull(DeploySnapshots.parseDeploymentCoordinates("  "));
        assertNull(DeploySnapshots.parseDeploymentCoordinates("12, 64"));
        assertNull(DeploySnapshots.parseDeploymentCoordinates("无坐标"));
    }

    @Test
    void insideIsHalfOpen() {
        assertTrue(DeploySnapshots.inside(10, 10, 10, 10, 5, 5));
        assertTrue(DeploySnapshots.inside(14, 14, 10, 10, 5, 5));
        assertFalse(DeploySnapshots.inside(15, 10, 10, 10, 5, 5));
        assertFalse(DeploySnapshots.inside(10, 15, 10, 10, 5, 5));
        assertFalse(DeploySnapshots.inside(9, 10, 10, 10, 5, 5));
    }

    @Test
    void blankSelectedClassIdMeansNoSelection() {
        assertNull(DeploySnapshots.normalizeSelectedClassId(null));
        assertNull(DeploySnapshots.normalizeSelectedClassId(""));
        assertNull(DeploySnapshots.normalizeSelectedClassId("  "));
        assertEquals("rifleman", DeploySnapshots.normalizeSelectedClassId("rifleman"));
    }

    @Test
    void variantCountsCompareByValue() {
        Map<String, Map<String, Integer>> a = Map.of("rifleman", Map.of("default", 2));
        Map<String, Map<String, Integer>> b = Map.of("rifleman", Map.of("default", 2));
        Map<String, Map<String, Integer>> c = Map.of("rifleman", Map.of("default", 3));
        assertTrue(DeploySnapshots.variantCountsEqual(a, b));
        assertFalse(DeploySnapshots.variantCountsEqual(a, c));
    }

    @Test
    void freeUnlockReportsOnlyRealChanges() {
        assertFalse(DeploySnapshots.isFreeUnlockMode());
        assertTrue(DeploySnapshots.applyFreeUnlock(true));
        assertTrue(DeploySnapshots.isFreeUnlockMode());
        assertFalse(DeploySnapshots.applyFreeUnlock(true));
        assertTrue(DeploySnapshots.applyFreeUnlock(false));
        assertFalse(DeploySnapshots.isFreeUnlockMode());
    }

    @Test
    void impeachmentVoteLabelsCommanderAndChallenger() {
        UUID commander = UUID.randomUUID();
        UUID challenger = UUID.randomUUID();
        UUID volunteer = UUID.randomUUID();
        GovernanceStatePacket.TeamState impeachment = new GovernanceStatePacket.TeamState(
            "ATTACK", "IMPEACHMENT_VOTE", commander, challenger, 30, 0L, Map.of(), List.of(), null);
        assertEquals("原指挥官", DeploySnapshots.votePrefixFor(impeachment, commander));
        assertEquals("挑战者", DeploySnapshots.votePrefixFor(impeachment, challenger));
        assertEquals("志愿者", DeploySnapshots.votePrefixFor(impeachment, volunteer));

        GovernanceStatePacket.TeamState election = new GovernanceStatePacket.TeamState(
            "ATTACK", "ELECTION", null, null, 30, 0L, Map.of(), List.of(volunteer), null);
        assertEquals("志愿者", DeploySnapshots.votePrefixFor(election, commander));
        assertEquals("候选人", DeploySnapshots.votePrefixFor(null, commander));
        assertEquals("候选人", DeploySnapshots.votePrefixFor(election, null));
    }
}
