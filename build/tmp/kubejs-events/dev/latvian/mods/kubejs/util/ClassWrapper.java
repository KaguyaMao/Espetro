/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.latvian.mods.rhino.Context
 *  dev.latvian.mods.rhino.NativeJavaClass
 *  dev.latvian.mods.rhino.Scriptable
 *  dev.latvian.mods.rhino.util.CustomJavaToJsWrapper
 */
package dev.latvian.mods.kubejs.util;

import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.NativeJavaClass;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.util.CustomJavaToJsWrapper;

public record ClassWrapper<T>(Class<T> wrappedClass) implements CustomJavaToJsWrapper
{
    public Scriptable convertJavaToJs(Context cx, Scriptable scope, Class<?> staticType) {
        return new NativeJavaClass(cx, scope, this.wrappedClass);
    }

    @Override
    public String toString() {
        return "ClassWrapper[" + this.wrappedClass.getName() + "]";
    }
}

