/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.client.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.minecraft.client.gui.screens.Screen;
import org.espetro.client.gui.ClassSelectScreen;
import org.espetro.client.gui.CommanderVoteScreen;
import org.espetro.client.gui.FactionRevealScreen;
import org.espetro.client.gui.HubScreen;
import org.espetro.client.gui.MapVoteScreen;
import org.espetro.client.gui.TeamSelectionScreen;
import org.espetro.network.ClassSelectScreenPacket;
import org.espetro.network.MapVoteStatePacket;
import org.espetro.team.TeamDisplayNames;
import org.espetro.tutorial.TutorialStep;

final class TutorialPreviewFactory {
    private TutorialPreviewFactory() {
    }

    static Screen create(TutorialStep step) {
        if (step == null) {
            return null;
        }
        return switch (step) {
            default -> throw new IncompatibleClassChangeError();
            case TutorialStep.WELCOME, TutorialStep.HUB -> new HubScreen(0, "\u6559\u7a0b\u9884\u89c8 \u00b7 \u4e3b\u57ce");
            case TutorialStep.MAP_VOTE -> TutorialPreviewFactory.mapVotePreview();
            case TutorialStep.MAP_LOADING -> null;
            case TutorialStep.TEAM_SELECT -> new TeamSelectionScreen();
            case TutorialStep.COMMANDER_VOTE -> new CommanderVoteScreen("DEFEND", List.of("Alpha", "Bravo", "Charlie", "Delta"), 20, TeamDisplayNames.displayName("ATTACK"), "", 0);
            case TutorialStep.FACTION_SELECT -> TutorialPreviewFactory.classSelectPreview();
            case TutorialStep.FACTION_REVEAL -> new FactionRevealScreen("\u793a\u8303\u8fdb\u653b\u7f16\u5236", "\u793a\u8303\u9632\u5b88\u7f16\u5236", null, null, 30);
            case TutorialStep.DEPLOY_PANEL, TutorialStep.SQUAD, TutorialStep.CLASS_SELECT, TutorialStep.DEPLOY_POINT -> null;
            case TutorialStep.KEYS_RADIAL, TutorialStep.RADIO_RALLY, TutorialStep.LOGISTICS_FOB, TutorialStep.COMMANDER_SKILLS, TutorialStep.OUTPOST, TutorialStep.BATTLE, TutorialStep.RESPAWN, TutorialStep.SCORE_ROUND, TutorialStep.MID_JOIN -> null;
        };
    }

    private static Screen mapVotePreview() {
        ArrayList<MapVoteStatePacket.Candidate> candidates = new ArrayList<MapVoteStatePacket.Candidate>();
        candidates.add(new MapVoteStatePacket.Candidate("demo_alpha", "\u793a\u8303\u5730\u56fe A", "\u5e73\u5766\u8bad\u7ec3\u573a"));
        candidates.add(new MapVoteStatePacket.Candidate("demo_bravo", "\u793a\u8303\u5730\u56fe B", "\u57ce\u5e02\u5df7\u6218"));
        candidates.add(new MapVoteStatePacket.Candidate("demo_charlie", "\u793a\u8303\u5730\u56fe C", "\u5c71\u5730\u9632\u7ebf"));
        HashMap<String, Integer> tally = new HashMap<String, Integer>();
        tally.put("demo_alpha", 2);
        tally.put("demo_bravo", 1);
        tally.put("demo_charlie", 0);
        MapVoteStatePacket packet = new MapVoteStatePacket(true, 25, 0L, candidates, tally, null, null, null);
        MapVoteScreen.update(packet);
        return new MapVoteScreen();
    }

    private static Screen classSelectPreview() {
        ArrayList<ClassSelectScreenPacket.FactionInfo> factions = new ArrayList<ClassSelectScreenPacket.FactionInfo>();
        factions.add(new ClassSelectScreenPacket.FactionInfo("demo_light", "\u8f7b\u88c5\u793a\u8303\u7f16\u5236", "", 1));
        factions.add(new ClassSelectScreenPacket.FactionInfo("demo_heavy", "\u91cd\u88c5\u793a\u8303\u7f16\u5236", "", 0));
        factions.add(new ClassSelectScreenPacket.FactionInfo("demo_mech", "\u673a\u68b0\u5316\u793a\u8303\u7f16\u5236", "", 0));
        return new ClassSelectScreen("DEFEND", true, factions, 30, TeamDisplayNames.displayName("ATTACK"), "", -1, null);
    }
}

