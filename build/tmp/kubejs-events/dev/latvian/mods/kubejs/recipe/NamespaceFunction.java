/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.latvian.mods.rhino.BaseFunction
 *  dev.latvian.mods.rhino.Context
 *  dev.latvian.mods.rhino.Scriptable
 */
package dev.latvian.mods.kubejs.recipe;

import dev.latvian.mods.kubejs.recipe.RecipeTypeFunction;
import dev.latvian.mods.kubejs.recipe.schema.RecipeNamespace;
import dev.latvian.mods.kubejs.util.WrappedJS;
import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Scriptable;
import java.util.Map;

public class NamespaceFunction
extends BaseFunction
implements WrappedJS {
    private final RecipeNamespace namespace;
    private final Map<String, RecipeTypeFunction> map;

    public NamespaceFunction(RecipeNamespace namespace, Map<String, RecipeTypeFunction> map) {
        this.namespace = namespace;
        this.map = map;
    }

    public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args0) {
        return this.namespace.keySet();
    }

    public Object get(Context cx, String name, Scriptable start) {
        return this.map.get(name);
    }

    public String toString() {
        return this.namespace.name;
    }

    public String getMod() {
        return this.namespace.name;
    }

    public int hashCode() {
        return this.namespace.name.hashCode();
    }

    public boolean equals(Object obj) {
        return this.namespace.name.equals(obj.toString());
    }
}

