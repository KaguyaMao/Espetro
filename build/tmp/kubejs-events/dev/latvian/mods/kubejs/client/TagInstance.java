/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.core.Registry
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 */
package dev.latvian.mods.kubejs.client;

import java.util.LinkedHashSet;
import java.util.stream.Collectors;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class TagInstance {
    public final ResourceLocation tag;
    public final LinkedHashSet<ResourceKey<? extends Registry<?>>> registries = new LinkedHashSet();

    public TagInstance(ResourceLocation tag) {
        this.tag = tag;
    }

    public Component toText() {
        String string = " #" + String.valueOf(this.tag) + this.registries.stream().map(ResourceKey::m_135782_).map(id -> {
            if (id.m_135827_().equals("minecraft")) {
                return id.m_135815_();
            }
            return id.toString();
        }).collect(Collectors.joining(" + ", " [", "]"));
        return Component.m_237113_((String)string).m_130940_(ChatFormatting.DARK_GRAY);
    }
}

