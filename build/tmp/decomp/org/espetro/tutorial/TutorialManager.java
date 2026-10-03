/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.tutorial;

import java.util.ArrayDeque;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.espetro.Espetro;
import org.espetro.config.GameConfig;
import org.espetro.network.NetworkManager;
import org.espetro.network.TutorialSyncPacket;
import org.espetro.team.GamePhase;
import org.espetro.tutorial.TutorialStep;

public final class TutorialManager {
    private static final TutorialManager INSTANCE = new TutorialManager();
    private final Map<UUID, Session> sessions = new HashMap<UUID, Session>();

    private TutorialManager() {
    }

    public static TutorialManager getInstance() {
        return INSTANCE;
    }

    public void onPlayerJoin(ServerPlayer player, boolean midGame) {
        if (player == null) {
            return;
        }
        this.sessions.computeIfAbsent(player.m_20148_(), id -> new Session());
    }

    public void onPlayerLeave(UUID uuid) {
        this.sessions.remove(uuid);
    }

    public void onPhaseChanged(GamePhase phase) {
    }

    public boolean tryShow(ServerPlayer player, TutorialStep step) {
        return this.tryShow(player, step, false);
    }

    public boolean tryShow(ServerPlayer player, TutorialStep step, boolean force) {
        if (player == null || step == null || !GameConfig.isTutorialEnabled()) {
            return false;
        }
        Session session = this.sessions.computeIfAbsent(player.m_20148_(), id -> new Session());
        if (session.skipped && !force) {
            return false;
        }
        if (force) {
            session.skipped = false;
            session.shownSteps.remove((Object)step);
            session.pending.remove((Object)step);
            this.display(player, session, step);
            return true;
        }
        if (!this.enqueue(player, step)) {
            return false;
        }
        this.flushDisplay(player);
        return true;
    }

    public void handleAction(ServerPlayer player, Action action, String stepId) {
        if (player == null || action == null) {
            return;
        }
        if (!GameConfig.isTutorialEnabled()) {
            this.clearClient(player);
            return;
        }
        Session session = this.sessions.computeIfAbsent(player.m_20148_(), id -> new Session());
        TutorialStep step = TutorialStep.byId(stepId);
        if (step == null) {
            step = session.currentStep;
        }
        switch (action) {
            case SKIP_ALL: {
                if (!GameConfig.isTutorialAllowSkip()) {
                    player.m_213846_(Component.m_237115_("tutorial.msg.skip_disabled"));
                    return;
                }
                session.skipped = true;
                session.currentStep = null;
                session.pending.clear();
                this.clearClient(player);
                player.m_213846_(Component.m_237115_("tutorial.msg.skipped"));
                break;
            }
            case DISMISS: {
                if (step != null) {
                    session.shownSteps.add(step);
                }
                session.currentStep = null;
                if (this.flushDisplay(player)) break;
                this.clearClient(player);
                break;
            }
            case NEXT: {
                if (step != null) {
                    session.shownSteps.add(step);
                }
                session.currentStep = null;
                if (this.flushDisplay(player)) {
                    return;
                }
                this.clearClient(player);
                player.m_213846_(Component.m_237115_("tutorial.msg.complete"));
            }
        }
    }

    public void reopen(ServerPlayer player) {
        if (!GameConfig.isTutorialEnabled()) {
            player.m_213846_(Component.m_237115_("tutorial.msg.disabled"));
            return;
        }
        Session session = this.sessions.computeIfAbsent(player.m_20148_(), id -> new Session());
        session.skipped = false;
        session.shownSteps.clear();
        session.pending.clear();
        session.currentStep = null;
        this.clearClient(player);
        for (TutorialStep step : TutorialStep.values()) {
            session.pending.add(step);
        }
        player.m_213846_(Component.m_237115_("tutorial.msg.started"));
        this.flushDisplay(player);
    }

    public void skipAll(ServerPlayer player) {
        this.handleAction(player, Action.SKIP_ALL, null);
    }

    public String statusLine(ServerPlayer player) {
        boolean enabled = GameConfig.isTutorialEnabled();
        Session session = this.sessions.get(player.m_20148_());
        boolean skipped = session != null && session.skipped;
        String current = session != null && session.currentStep != null ? session.currentStep.getId() : "-";
        int shown = session != null ? session.shownSteps.size() : 0;
        int pending = session != null ? session.pending.size() : 0;
        return "enabled=" + enabled + " show_on_join=" + GameConfig.isTutorialShowOnJoin() + " allow_skip=" + GameConfig.isTutorialAllowSkip() + " skipped=" + skipped + " current=" + current + " pending=" + pending + " shown=" + shown + "/" + TutorialStep.totalCount();
    }

    public void onConfigReloaded() {
        if (GameConfig.isTutorialEnabled()) {
            return;
        }
        MinecraftServer server = Espetro.getServer();
        if (server == null) {
            this.sessions.clear();
            return;
        }
        for (ServerPlayer player : server.m_6846_().m_11314_()) {
            this.clearClient(player);
        }
        this.sessions.clear();
    }

    public void clearClient(ServerPlayer player) {
        NetworkManager.sendToPlayer(player, TutorialSyncPacket.clear());
    }

    private boolean enqueue(ServerPlayer player, TutorialStep step) {
        if (player == null || step == null || !GameConfig.isTutorialEnabled()) {
            return false;
        }
        Session session = this.sessions.computeIfAbsent(player.m_20148_(), id -> new Session());
        if (session.skipped) {
            return false;
        }
        if (session.shownSteps.contains((Object)step) || session.currentStep == step || session.pending.contains((Object)step)) {
            return false;
        }
        session.pending.add(step);
        return true;
    }

    private boolean flushDisplay(ServerPlayer player) {
        Session session = this.sessions.get(player.m_20148_());
        if (session == null || session.skipped || session.currentStep != null) {
            return session != null && session.currentStep != null;
        }
        while (!session.pending.isEmpty()) {
            TutorialStep next = session.pending.poll();
            if (next == null || session.shownSteps.contains((Object)next)) continue;
            this.display(player, session, next);
            return true;
        }
        return false;
    }

    private void display(ServerPlayer player, Session session, TutorialStep step) {
        session.currentStep = step;
        session.pending.remove((Object)step);
        NetworkManager.sendToPlayer(player, TutorialSyncPacket.show(step.getId(), step.ordinalIndex(), TutorialStep.totalCount(), GameConfig.isTutorialAllowSkip()));
    }

    private static final class Session {
        private boolean skipped;
        private TutorialStep currentStep;
        private final EnumSet<TutorialStep> shownSteps = EnumSet.noneOf(TutorialStep.class);
        private final Queue<TutorialStep> pending = new ArrayDeque<TutorialStep>();

        private Session() {
        }
    }

    public static enum Action {
        NEXT,
        SKIP_ALL,
        DISMISS;

    }
}

