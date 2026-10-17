package org.espetro.support;

import static org.espetro.support.FireRequestBoard.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

class FireRequestBoardTest {
    FireRequestBoard board;
    final Actor leader = actor("ATTACK", true, false),
            commander = actor("ATTACK", false, true),
            member = actor("ATTACK", false, false),
            enemy = actor("DEFEND", true, true);
    final Bounds bounds = new Bounds(-512, -512, 512, 512);

    static Actor actor(String team, boolean leader, boolean commander) {
        return new Actor(UUID.randomUUID(), "测试", team, 1, leader, commander, true);
    }

    @BeforeEach
    void reset() {
        board = new FireRequestBoard(new Rules(4, 0, 100, 1500));
        board.reset(42);
    }

    Request create() {
        return board.create(leader, "minecraft:overworld", bounds, true, 10, 20, 30, 40, "压制", 0);
    }

    void denied(Runnable action) {
        assertThrows(Denied.class, action::run);
    }

    @Test
    void onlyDeployedLeadersMayRequest() {
        denied(() -> board.create(member, "world", bounds, false, 0, 0, 0, 0, "", 0));
        var a = new Actor(leader.id(), "测试", "ATTACK", 1, true, false, false);
        denied(() -> board.create(a, "world", bounds, false, 0, 0, 0, 0, "", 0));
    }

    @Test
    void teamsAreIsolated() {
        Request r = create();
        assertTrue(board.visible(enemy).isEmpty());
        denied(() -> board.approve(r.id(), enemy, 1, v -> true));
        denied(() -> board.cancel(r.id(), enemy, 1));
    }

    @Test
    void invalidTargetsCannotEnterBoard() {
        for (double x : new double[] {Double.NaN, Double.POSITIVE_INFINITY, -513, 512})
            denied(() -> board.create(leader, "world", bounds, false, x, 0, 0, 0, "", 0));
        assertTrue(board.visible(leader).isEmpty());
    }

    @Test
    void coordinateLabelsUseFixedMapOrigin() {
        assertEquals("(A,1,1,1)", bounds.label(-512, -512));
        assertEquals("(B,1,1,1)", bounds.label(-362, -512));
        assertEquals("(A,1,5,1)", bounds.label(-462, -462));
        assertEquals("", bounds.label(512, 0));
    }

    @Test
    void invalidLinesAreRejected() {
        denied(() -> board.create(leader, "world", bounds, true, 0, 0, 0, 0, "", 0));
        denied(() -> board.create(leader, "world", bounds, true, 0, 0, Double.NaN, 0, "", 0));
        denied(() -> board.create(leader, "world", bounds, true, 0, 0, 512, 0, "", 0));
        assertTrue(board.create(leader, "world", bounds, true, 0, 0, 100, 0, "", 0).line());
    }

    @Test
    void lineLengthIsBounded() {
        board = new FireRequestBoard(new Rules(4, 0, 100, 50));
        denied(() -> board.create(leader, "world", bounds, true, 0, 0, 51, 0, "", 0));
    }

    @Test
    void leaderCannotApprove() {
        Request r = create();
        denied(() -> board.approve(r.id(), leader, 1, v -> true));
    }

    @Test
    void duplicateApprovalExecutesOnce() {
        Request r = create();
        AtomicInteger n = new AtomicInteger();
        assertTrue(
                board.approve(
                        r.id(),
                        commander,
                        1,
                        v -> {
                            n.incrementAndGet();
                            return true;
                        }));
        denied(
                () ->
                        board.approve(
                                r.id(),
                                commander,
                                2,
                                v -> {
                                    n.incrementAndGet();
                                    return true;
                                }));
        assertEquals(1, n.get());
    }

    @Test
    void reentrantApprovalIsRejected() {
        Request r = create();
        assertTrue(
                board.approve(
                        r.id(),
                        commander,
                        1,
                        v -> {
                            denied(() -> board.approve(r.id(), commander, 1, q -> true));
                            return true;
                        }));
    }

    @Test
    void cooldownFailureAllowsLaterApproval() {
        Request r = create();
        assertFalse(board.approve(r.id(), commander, 1, v -> false));
        assertEquals(State.PENDING, board.get(r.id()).state());
        assertTrue(board.approve(r.id(), commander, 2, v -> true));
    }

    @Test
    void callbackExceptionCannotBeRetried() {
        Request r = create();
        denied(
                () ->
                        board.approve(
                                r.id(),
                                commander,
                                1,
                                v -> {
                                    throw new Exception("effects may be scheduled");
                                }));
        assertEquals(State.REJECTED, board.get(r.id()).state());
        denied(() -> board.approve(r.id(), commander, 2, v -> true));
    }

    @Test
    void rejectAndCancelPreventExecution() {
        Request r = create();
        board.reject(r.id(), commander, 1);
        denied(() -> board.approve(r.id(), commander, 2, v -> true));
        Request c = create();
        board.cancel(c.id(), leader, 1);
        denied(() -> board.approve(c.id(), commander, 2, v -> true));
    }

    @Test
    void strangersCannotCancel() {
        Request r = create();
        denied(() -> board.cancel(r.id(), member, 1));
    }

    @Test
    void expiryIsCheckedBeforeCallback() {
        Request r = create();
        denied(
                () ->
                        board.approve(
                                r.id(),
                                commander,
                                100,
                                v -> {
                                    fail("must not execute");
                                    return true;
                                }));
        assertEquals(State.EXPIRED, board.get(r.id()).state());
    }

    @Test
    void roundEndClosesRequestsAndResetErasesOldIds() {
        Request r = create();
        board.closeRound(1);
        assertEquals(State.CANCELLED, board.get(r.id()).state());
        board.reset(43);
        assertNull(board.get(r.id()));
        denied(() -> board.approve(r.id(), commander, 2, v -> true));
    }

    @Test
    void maximumAndHistoryAreBounded() {
        for (int i = 0; i < 4; i++) create();
        denied(this::create);
        for (Request r : board.visible(leader)) board.cancel(r.id(), leader, 1);
        create();
        assertEquals(4, board.visible(leader).size());
    }

    @Test
    void rateLimitSurvivesMaintenanceUntilItsDeadline() {
        board = new FireRequestBoard(new Rules(48, 20, 100, 1500));
        create();
        board.maintain(19);
        denied(() -> board.create(leader, "world", bounds, false, 0, 0, 0, 0, "", 19));
        board.maintain(20);
        assertNotNull(board.create(leader, "world", bounds, false, 0, 0, 0, 0, "", 20));
    }

    @Test
    void notesAreBoundedAndCannotInjectChatFormatting() {
        Request r =
                board.create(
                        leader, "world", bounds, false, 0, 0, 0, 0, "§a\n" + "测".repeat(200), 0);
        assertEquals(96, r.note().length());
        assertFalse(r.note().contains("§"));
        assertFalse(r.note().contains("\n"));
    }
}
