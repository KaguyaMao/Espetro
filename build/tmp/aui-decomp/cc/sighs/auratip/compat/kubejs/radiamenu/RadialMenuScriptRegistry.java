/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package cc.sighs.auratip.compat.kubejs.radiamenu;

import cc.sighs.auratip.api.radiamenu.RadialMenuRegistry;
import cc.sighs.auratip.data.RadialMenuData;
import java.util.List;
import javax.annotation.Nullable;

public final class RadialMenuScriptRegistry {
    private RadialMenuScriptRegistry() {
    }

    public static List<RadialMenuData> getMenus() {
        return List.copyOf(RadialMenuRegistry.getMenus(RadialMenuRegistry.ownerKubejs()));
    }

    public static void setMenus(@Nullable List<RadialMenuData> menus) {
        RadialMenuRegistry.setMenus(RadialMenuRegistry.ownerKubejs(), menus);
    }
}

