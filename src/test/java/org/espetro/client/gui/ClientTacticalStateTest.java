package org.espetro.client.gui;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ClientTacticalStateTest {

    @Test
    void ordinarySquadMemberCannotOpenLeaderTacticalRadial() {
        var ordinary = new ClientTacticalState.MarkerInfo(4, false, false, false, (byte) 0);
        var fireteamLeader = new ClientTacticalState.MarkerInfo(4, false, true, false, (byte) 1);

        assertFalse(ClientTacticalState.hasSquadLeaderAccess(4, ordinary));
        assertFalse(ClientTacticalState.hasSquadLeaderAccess(4, fireteamLeader));
        assertFalse(ClientTacticalState.hasSquadLeaderAccess(4, null));
    }

    @Test
    void onlyLeaderOfTheLocalSquadCanOpenTacticalRadial() {
        var squadLeader = new ClientTacticalState.MarkerInfo(4, true, false, false, (byte) 0);
        var otherSquadLeader = new ClientTacticalState.MarkerInfo(5, true, false, false, (byte) 0);

        assertTrue(ClientTacticalState.hasSquadLeaderAccess(4, squadLeader));
        assertFalse(ClientTacticalState.hasSquadLeaderAccess(4, otherSquadLeader));
        assertFalse(ClientTacticalState.hasSquadLeaderAccess(-1, squadLeader));
    }
}
