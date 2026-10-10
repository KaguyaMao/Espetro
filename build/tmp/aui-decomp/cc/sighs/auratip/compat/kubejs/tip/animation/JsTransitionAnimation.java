/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.latvian.mods.rhino.Context
 *  dev.latvian.mods.rhino.Function
 *  dev.latvian.mods.rhino.Scriptable
 */
package cc.sighs.auratip.compat.kubejs.tip.animation;

import cc.sighs.auratip.api.animation.TransitionAnimation;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Function;
import dev.latvian.mods.rhino.Scriptable;

public class JsTransitionAnimation
implements TransitionAnimation {
    private final Scriptable jsObj;

    public JsTransitionAnimation(Scriptable jsObj) {
        this.jsObj = jsObj;
    }

    @Override
    public float easedProgress(long now, long start, boolean closing, int openMs, int closeMs) {
        return this.callFloat(Context.enter(), "easedProgress", now, start, closing, openMs, closeMs);
    }

    @Override
    public int offsetX(float eased, int w, int h) {
        return this.callInt(Context.enter(), "offsetX", Float.valueOf(eased), w, h);
    }

    @Override
    public int offsetY(float eased, int w, int h) {
        return this.callInt(Context.enter(), "offsetY", Float.valueOf(eased), w, h);
    }

    private int callInt(Context context, String name, Object ... args) {
        int n;
        Object fn = this.jsObj.get(context, name, this.jsObj);
        if (!(fn instanceof Function)) {
            return 0;
        }
        Function f = (Function)fn;
        Object result = f.call(context, this.jsObj.getParentScope(), this.jsObj, args);
        if (result instanceof Number) {
            Number n2 = (Number)result;
            n = n2.intValue();
        } else {
            n = 0;
        }
        return n;
    }

    private float callFloat(Context context, String name, Object ... args) {
        float f;
        Object fn = this.jsObj.get(context, name, this.jsObj);
        if (!(fn instanceof Function)) {
            return 0.0f;
        }
        Function f2 = (Function)fn;
        Object result = f2.call(context, this.jsObj.getParentScope(), this.jsObj, args);
        if (result instanceof Number) {
            Number n = (Number)result;
            f = n.floatValue();
        } else {
            f = 0.0f;
        }
        return f;
    }
}

