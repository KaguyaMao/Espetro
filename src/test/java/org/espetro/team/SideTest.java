package org.espetro.team;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class SideTest {
    @Test
    void idsMatchLegacyStrings() {
        assertEquals("ATTACK", Side.ATTACK.id());
        assertEquals("DEFEND", Side.DEFEND.id());
        assertEquals(Side.ATTACK_ID, Side.ATTACK.id());
        assertEquals(Side.DEFEND_ID, Side.DEFEND.id());
    }

    @Test
    void ofIsStrictButIgnoresCaseAndWhitespace() {
        assertEquals(Side.ATTACK, Side.of("ATTACK"));
        assertEquals(Side.ATTACK, Side.of(" attack "));
        assertEquals(Side.DEFEND, Side.of("Defend"));
        assertNull(Side.of(null));
        assertNull(Side.of(""));
        assertNull(Side.of("ESPETRO_ATTACK"));
        assertNull(Side.of("attacker"));
        assertNull(Side.of("进攻"));
    }

    @Test
    void parseAlsoAcceptsScoreboardTeamNames() {
        assertEquals(Side.ATTACK, Side.parse("ESPETRO_ATTACK"));
        assertEquals(Side.DEFEND, Side.parse("espetro_defend"));
        assertEquals(Side.ATTACK, Side.parse("attack"));
        assertNull(Side.parse("ESPETRO_"));
        assertNull(Side.parse(null));
    }

    @Test
    void guessMatchesKeywordsWithAttackFirst() {
        assertEquals(Side.ATTACK, Side.guess("attacker"));
        assertEquals(Side.ATTACK, Side.guess("Team_Attack"));
        assertEquals(Side.ATTACK, Side.guess("进攻方"));
        assertEquals(Side.ATTACK, Side.guess("攻方"));
        assertEquals(Side.DEFEND, Side.guess("defender"));
        assertEquals(Side.DEFEND, Side.guess("防守方"));
        assertEquals(Side.DEFEND, Side.guess("守方"));
        assertNull(Side.guess("   "));
        assertNull(Side.guess(null));
        assertNull(Side.guess("spectator"));
    }

    @Test
    void normalizeIdReturnsCanonicalStringOrNull() {
        assertEquals("ATTACK", Side.normalizeId(" attack"));
        assertEquals("DEFEND", Side.normalizeId("DEFEND"));
        assertNull(Side.normalizeId("UNKNOWN"));
        assertNull(Side.normalizeId(null));
    }

    @Test
    void oppositeIdKeepsLegacyTernarySemantics() {
        assertEquals("DEFEND", Side.oppositeId("ATTACK"));
        assertEquals("ATTACK", Side.oppositeId("DEFEND"));
        // 与历史写法 "ATTACK".equals(x) ? "DEFEND" : "ATTACK" 一致
        assertEquals("ATTACK", Side.oppositeId(null));
        assertEquals("ATTACK", Side.oppositeId("attack"));
    }

    @Test
    void oppositeFlipsSide() {
        assertEquals(Side.DEFEND, Side.ATTACK.opposite());
        assertEquals(Side.ATTACK, Side.DEFEND.opposite());
    }
}
