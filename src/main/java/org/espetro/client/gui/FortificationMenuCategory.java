package org.espetro.client.gui;

import java.util.Locale;

/** Directory assignment uses catalogue names/icons; it never invents deployable entries. */
public enum FortificationMenuCategory {
    FOUNDATION("基础设施", "radialradiohubicon"),
    DEFENSE("防御工事", "radialfortificationsicon"),
    WEAPONS("固定武器", "radialemplacementsicon"),
    LOGISTICS("后勤设施", "radialtechstructuresicon"),
    OTHER("其他工事", "radialdeployablesicon");
    public final String title, icon;
    FortificationMenuCategory(String title, String icon) { this.title = title; this.icon = icon; }
    public static FortificationMenuCategory classify(String id, String label, String icon) {
        String key = (id + " " + label + " " + icon).toLowerCase(Locale.ROOT);
        if (contains(key, "repair", "vehicle_supply", "helipad", "维修", "补给站", "停机坪")) return LOGISTICS;
        if (contains(key, "mortar", "machinegun", "machine_gun", "hmg", "gmg", "anti_tank", "antiair", "anti_air", "cannon", "rocket", "迫击", "机枪", "反坦克", "防空", "火炮")) return WEAPONS;
        if (contains(key, "sandbag", "hesco", "razor", "bunker", "camonet", "wall", "shelter", "roadblock", "沙袋", "防爆", "铁丝", "掩体", "围墙", "路障")) return DEFENSE;
        if (contains(key, "radio", "hab", "fob", "ammo", "rally", "电台", "兵站", "弹药箱", "集结")) return FOUNDATION;
        return OTHER;
    }
    private static boolean contains(String text, String... words) {
        for (String word : words) if (text.contains(word)) return true;
        return false;
    }
}
