/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.latvian.mods.kubejs.KubeJSPlugin
 *  dev.latvian.mods.kubejs.event.EventGroup
 *  dev.latvian.mods.kubejs.event.EventHandler
 *  dev.latvian.mods.kubejs.event.EventJS
 *  dev.latvian.mods.kubejs.script.BindingsEvent
 */
package cc.sighs.auratip.compat.kubejs;

import cc.sighs.auratip.api.radiamenu.icon.ItemIcon;
import cc.sighs.auratip.api.radiamenu.icon.TextureIcon;
import cc.sighs.auratip.compat.kubejs.radiamenu.RadialMenuRegistrationEvent;
import cc.sighs.auratip.compat.kubejs.radiamenu.RadialMenuScriptRegistry;
import cc.sighs.auratip.compat.kubejs.radiamenu.action.ActionsKJS;
import cc.sighs.auratip.compat.kubejs.radiamenu.slot.RadialMenuExtraSlotRegistry;
import cc.sighs.auratip.compat.kubejs.radiamenu.slot.RadialMenusKJS;
import cc.sighs.auratip.compat.kubejs.tip.TipRegistrationEvent;
import cc.sighs.auratip.compat.kubejs.tip.TipScriptRegistry;
import cc.sighs.auratip.compat.kubejs.tip.TipText;
import cc.sighs.auratip.compat.kubejs.tip.TipTriggers;
import cc.sighs.auratip.compat.kubejs.tip.TipVariables;
import cc.sighs.auratip.compat.kubejs.tip.animation.TipAnimations;
import cc.sighs.auratip.data.TipData;
import cc.sighs.auratip.util.ItemStackUtil;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.script.BindingsEvent;
import java.util.List;

public class KJSAuraTipPlugin
extends KubeJSPlugin {
    public static final EventGroup TIP_EVENTS = EventGroup.of((String)"TipEvents");
    public static final EventGroup RADIAL_EVENTS = EventGroup.of((String)"RadialMenuEvents");
    public static final EventHandler REGISTER_TIPS = TIP_EVENTS.client("register", () -> TipRegistrationEvent.class);
    public static final EventHandler REGISTER_RADIAL = RADIAL_EVENTS.client("register", () -> RadialMenuRegistrationEvent.class);

    public void registerEvents() {
        TIP_EVENTS.register();
        RADIAL_EVENTS.register();
    }

    public void registerBindings(BindingsEvent event) {
        event.add("TipVars", TipVariables.class);
        event.add("TipTriggers", TipTriggers.class);
        event.add("TipText", TipText.class);
        event.add("TipAnimations", TipAnimations.class);
        event.add("Actions", ActionsKJS.class);
        event.add("RadialMenus", RadialMenusKJS.class);
        event.add("ItemIcon", ItemIcon.class);
        event.add("TextureIcon", TextureIcon.class);
        event.add("ItemStackUtil", ItemStackUtil.class);
    }

    public void onServerReload() {
        RadialMenuExtraSlotRegistry.clear();
        TipRegistrationEvent tipEvent = new TipRegistrationEvent();
        REGISTER_TIPS.post((EventJS)tipEvent);
        List<TipData> tips = tipEvent.buildAll();
        TipScriptRegistry.setTips(tipEvent.buildAll());
        RadialMenuRegistrationEvent radialEvent = new RadialMenuRegistrationEvent();
        REGISTER_RADIAL.post((EventJS)radialEvent);
        RadialMenuScriptRegistry.setMenus(radialEvent.buildAll());
    }
}

