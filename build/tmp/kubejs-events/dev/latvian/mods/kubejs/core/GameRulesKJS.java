/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.GameRules$BooleanValue
 *  net.minecraft.world.level.GameRules$IntegerValue
 *  net.minecraft.world.level.GameRules$Value
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.core;

import net.minecraft.world.level.GameRules;
import org.jetbrains.annotations.Nullable;

public interface GameRulesKJS {
    @Nullable
    public GameRules.Value<?> kjs$get(String var1);

    public void kjs$set(String var1, String var2);

    default public String kjs$getString(String rule) {
        GameRules.Value<?> o = this.kjs$get(rule);
        return o == null ? "" : o.m_5831_();
    }

    default public boolean kjs$getBoolean(String rule) {
        GameRules.BooleanValue v;
        GameRules.Value<?> o = this.kjs$get(rule);
        return o instanceof GameRules.BooleanValue && (v = (GameRules.BooleanValue)o).m_46223_();
    }

    default public int kjs$getInt(String rule) {
        int n;
        GameRules.Value<?> o = this.kjs$get(rule);
        if (o instanceof GameRules.IntegerValue) {
            GameRules.IntegerValue v = (GameRules.IntegerValue)o;
            n = v.m_46288_();
        } else {
            n = 0;
        }
        return n;
    }
}

