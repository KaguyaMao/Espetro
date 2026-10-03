/*
 * Decompiled with CFR 0.152.
 */
package dev.latvian.mods.kubejs.script;

import dev.latvian.mods.kubejs.script.ScriptType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public interface ScriptTypePredicate
extends Predicate<ScriptType> {
    public static final ScriptTypePredicate ALL = type -> true;
    public static final ScriptTypePredicate COMMON = type -> type != ScriptType.STARTUP;
    public static final ScriptTypePredicate STARTUP_OR_CLIENT = type -> type != ScriptType.SERVER;
    public static final ScriptTypePredicate STARTUP_OR_SERVER = type -> type != ScriptType.CLIENT;

    @Override
    public boolean test(ScriptType var1);

    default public List<ScriptType> getValidTypes() {
        if (this == ALL) {
            return Arrays.asList(ScriptType.VALUES);
        }
        if (this == COMMON) {
            return List.of(ScriptType.SERVER, ScriptType.CLIENT);
        }
        if (this == STARTUP_OR_CLIENT) {
            return List.of(ScriptType.STARTUP, ScriptType.CLIENT);
        }
        if (this == STARTUP_OR_SERVER) {
            return List.of(ScriptType.STARTUP, ScriptType.SERVER);
        }
        ArrayList<ScriptType> list = new ArrayList<ScriptType>(2);
        for (ScriptType type : ScriptType.VALUES) {
            if (!this.test(type)) continue;
            list.add(type);
        }
        return list;
    }
}

