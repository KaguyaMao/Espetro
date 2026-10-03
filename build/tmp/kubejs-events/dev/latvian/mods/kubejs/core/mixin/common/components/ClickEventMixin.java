/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  dev.latvian.mods.rhino.mod.util.JsonSerializable
 *  dev.latvian.mods.rhino.util.RemapForJS
 *  net.minecraft.Util
 *  net.minecraft.network.chat.ClickEvent
 *  net.minecraft.network.chat.ClickEvent$Action
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 */
package dev.latvian.mods.kubejs.core.mixin.common.components;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.mods.rhino.mod.util.JsonSerializable;
import dev.latvian.mods.rhino.util.RemapForJS;
import net.minecraft.Util;
import net.minecraft.network.chat.ClickEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value={ClickEvent.class})
public abstract class ClickEventMixin
implements JsonSerializable {
    @Shadow
    public abstract ClickEvent.Action m_130622_();

    @Shadow
    public abstract String m_130623_();

    @RemapForJS(value="toJson")
    public JsonElement toJsonJS() {
        return (JsonElement)Util.m_137469_((Object)new JsonObject(), json -> {
            json.addProperty("action", this.m_130622_().m_130649_());
            json.addProperty("value", this.m_130623_());
        });
    }
}

