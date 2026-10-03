/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.latvian.mods.rhino.BaseFunction
 *  dev.latvian.mods.rhino.Context
 *  dev.latvian.mods.rhino.Scriptable
 *  dev.latvian.mods.rhino.Symbol
 */
package dev.latvian.mods.kubejs.util;

import dev.latvian.mods.kubejs.util.MutedError;
import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.Symbol;

public class LegacyCodeHandler
extends BaseFunction {
    public final String code;

    public LegacyCodeHandler(String code) {
        this.code = code;
    }

    public LegacyError makeError(Context cx) {
        int[] linep = new int[]{0};
        Context.getSourcePositionFromStack((Context)cx, (int[])linep);
        return new LegacyError("Line " + linep[0] + ": '" + this.code + "' is no longer supported! Read more on wiki: https://kubejs.com/kjs6");
    }

    public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        throw this.makeError(cx);
    }

    public Scriptable construct(Context cx, Scriptable scope, Object[] args) {
        throw this.makeError(cx);
    }

    public void put(Context cx, String name, Scriptable start, Object value) {
        throw this.makeError(cx);
    }

    public void put(Context cx, int index, Scriptable start, Object value) {
        throw this.makeError(cx);
    }

    public void put(Context cx, Symbol key, Scriptable start, Object value) {
        throw this.makeError(cx);
    }

    public Object get(Context cx, String name, Scriptable start) {
        throw this.makeError(cx);
    }

    public Object get(Context cx, int index, Scriptable start) {
        throw this.makeError(cx);
    }

    public Object get(Context cx, Symbol key, Scriptable start) {
        throw this.makeError(cx);
    }

    public static class LegacyError
    extends RuntimeException
    implements MutedError {
        public LegacyError(String message) {
            super(message);
        }

        @Override
        public String toString() {
            return this.getLocalizedMessage();
        }
    }
}

