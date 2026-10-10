package org.espetro.client.gui;

import java.util.Locale;

/** Directory assignment uses catalogue names/icons; it never invents deployable entries. */
public enum FortificationMenuCategory {
    FOUNDATION("功能性建筑", "radialtechstructuresicon"),
    DEFENSE("防御工事", "radialfortificationsicon"),
    WEAPONS("武器", "radialemplacementsicon");
    public final String title, icon;
    FortificationMenuCategory(String title, String icon) { this.title = title; this.icon = icon; }
    public static FortificationMenuCategory classify(String id, String label, String icon) {
        String key = (id + " " + label + " " + icon).toLowerCase(Locale.ROOT);
        if ("espetro:hab".equals(id) || "espetro:ammo_crate".equals(id)
                || "espetro:vehicle_supply_station".equals(id)) return FOUNDATION;
        if (contains(key, "mortar", "machinegun", "machine_gun", "hmg", "gmg", "anti_tank", "antiair", "anti_air", "cannon", "rocket", "迫击", "机枪", "反坦克", "防空", "火炮")) return WEAPONS;
        if (contains(key, "sandbag", "hesco", "razor", "bunker", "camonet", "wall", "shelter", "roadblock", "沙袋", "防爆", "铁丝", "掩体", "围墙", "路障")) return DEFENSE;
        return DEFENSE;
    }
    private static boolean contains(String text, String... words) {
        for (String word : words) if (text.contains(word)) return true;
        return false;
    }
}
