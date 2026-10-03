/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.latvian.mods.rhino.util.RemapPrefixForJS
 *  net.minecraft.world.level.GameRules
 *  net.minecraft.world.level.GameRules$GameRuleTypeVisitor
 *  net.minecraft.world.level.GameRules$Key
 *  net.minecraft.world.level.GameRules$Type
 *  net.minecraft.world.level.GameRules$Value
 *  org.jetbrains.annotations.Nullable
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 */
package dev.latvian.mods.kubejs.core.mixin.common;

import dev.latvian.mods.kubejs.core.GameRulesKJS;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.world.level.GameRules;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@RemapPrefixForJS(value="kjs$")
@Mixin(value={GameRules.class})
public abstract class GameRulesMixin
implements GameRulesKJS {
    private Map<String, GameRules.Key<?>> kjs$keyCache;

    @Shadow
    public abstract <T extends GameRules.Value<T>> T m_46170_(GameRules.Key<T> var1);

    @Nullable
    private GameRules.Key<?> getKey(String rule) {
        if (this.kjs$keyCache == null) {
            this.kjs$keyCache = new HashMap();
            GameRules.m_46164_((GameRules.GameRuleTypeVisitor)new GameRules.GameRuleTypeVisitor(){

                public <T extends GameRules.Value<T>> void m_6889_(GameRules.Key<T> key, GameRules.Type<T> type) {
                    GameRulesMixin.this.kjs$keyCache.put(key.toString(), key);
                }
            });
        }
        return this.kjs$keyCache.get(rule);
    }

    @Override
    @Nullable
    public GameRules.Value<?> kjs$get(String rule) {
        GameRules.Key<?> key = this.getKey(rule);
        return key == null ? null : (GameRules.Value<?>)this.m_46170_(key);
    }

    @Override
    public void kjs$set(String rule, String value) {
        Object r;
        GameRules.Key<?> key = this.getKey(rule);
        Object v0 = r = key == null ? null : this.m_46170_(key);
        if (r != null) {
            r.m_7377_(value);
            if (UtilsJS.staticServer != null) {
                r.m_46368_(UtilsJS.staticServer);
            }
        }
    }
}

