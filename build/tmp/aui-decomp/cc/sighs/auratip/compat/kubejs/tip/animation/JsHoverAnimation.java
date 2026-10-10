/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.latvian.mods.rhino.Context
 *  dev.latvian.mods.rhino.Function
 *  dev.latvian.mods.rhino.Scriptable
 */
package cc.sighs.auratip.compat.kubejs.tip.animation;

import cc.sighs.auratip.api.animation.HoverAnimation;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Function;
import dev.latvian.mods.rhino.Scriptable;

public class JsHoverAnimation
implements HoverAnimation {
    private final Scriptable jsObj;

    public JsHoverAnimation(Scriptable jsObj) {
        this.jsObj = jsObj;
    }

    @Override
    public int offsetX(long now, long start, int w, int h, float speed) {
        return this.callInt(Context.enter(), "offsetX", now, start, w, h, Float.valueOf(speed));
    }

    @Override
    public int offsetY(long now, long start, int w, int h, float speed) {
        return this.callInt(Context.enter(), "offsetY", now, start, w, h, Float.valueOf(speed));
    }

    private int callInt(Context context, String name, Object ... args) {
        int n;
        Object fn = this.jsObj.get(context, name, this.jsObj);
        if (!(fn instanceof Function)) {
            return 0;
        }
        Function f = (Function)fn;
        Object result = f.call(context, this.jsObj, this.jsObj, args);
        if (result instanceof Number) {
            Number n2 = (Number)result;
            n = n2.intValue();
        } else {
            n = 0;
        }
        return n;
    }
}

