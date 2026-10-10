package org.espetro.client.gui;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FortificationMenuCategoryTest {
    @Test void builtInCatalogueMapsToExpectedDirectories() {
        assertEquals(FortificationMenuCategory.FOUNDATION,FortificationMenuCategory.classify("espetro:radio","电台",""));
        assertEquals(FortificationMenuCategory.FOUNDATION,FortificationMenuCategory.classify("espetro:hab","兵站",""));
        assertEquals(FortificationMenuCategory.DEFENSE,FortificationMenuCategory.classify("espetro:sandbag_wall","沙袋墙",""));
        assertEquals(FortificationMenuCategory.LOGISTICS,FortificationMenuCategory.classify("espetro:vehicle_supply_station","载具补给站",""));
    }
    @Test void customNamesAndIconsAreClassifiedWithoutDiscardingUnknownItems() {
        assertEquals(FortificationMenuCategory.WEAPONS,FortificationMenuCategory.classify("custom:a","x","custom:RadialMortarsIcon"));
        assertEquals(FortificationMenuCategory.DEFENSE,FortificationMenuCategory.classify("custom:b","铁丝网",""));
        assertEquals(FortificationMenuCategory.OTHER,FortificationMenuCategory.classify("custom:unknown","新工事",""));
    }
}
