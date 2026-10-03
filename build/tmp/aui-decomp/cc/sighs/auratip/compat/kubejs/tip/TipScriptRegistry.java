/*
 * Decompiled with CFR 0.152.
 */
package cc.sighs.auratip.compat.kubejs.tip;

import cc.sighs.auratip.api.tip.TipRegistry;
import cc.sighs.auratip.data.TipData;
import java.util.List;

public final class TipScriptRegistry {
    private TipScriptRegistry() {
    }

    public static List<TipData> getTips() {
        return TipRegistry.getTips(TipRegistry.ownerKubejs());
    }

    public static void setTips(List<TipData> newTips) {
        TipRegistry.setTips(TipRegistry.ownerKubejs(), newTips);
    }
}

