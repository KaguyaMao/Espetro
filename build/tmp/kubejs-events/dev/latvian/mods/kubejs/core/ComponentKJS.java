/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Iterators
 *  com.google.gson.JsonElement
 *  dev.latvian.mods.rhino.mod.util.JsonSerializable
 *  dev.latvian.mods.rhino.mod.util.color.Color
 *  dev.latvian.mods.rhino.util.RemapForJS
 *  dev.latvian.mods.rhino.util.RemapPrefixForJS
 *  net.minecraft.ChatFormatting
 *  net.minecraft.network.chat.ClickEvent
 *  net.minecraft.network.chat.ClickEvent$Action
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.Component$Serializer
 *  net.minecraft.network.chat.HoverEvent
 *  net.minecraft.network.chat.HoverEvent$Action
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.network.chat.TextColor
 *  net.minecraft.resources.ResourceLocation
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.core;

import com.google.common.collect.Iterators;
import com.google.gson.JsonElement;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.bindings.TextWrapper;
import dev.latvian.mods.kubejs.util.WrappedJS;
import dev.latvian.mods.rhino.mod.util.JsonSerializable;
import dev.latvian.mods.rhino.mod.util.color.Color;
import dev.latvian.mods.rhino.util.RemapForJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@RemapPrefixForJS(value="kjs$")
public interface ComponentKJS
extends Component,
JsonSerializable,
WrappedJS {
    default public Iterable<Component> kjs$asIterable() {
        return new Iterable<Component>(){

            @Override
            @NotNull
            public Iterator<Component> iterator() {
                if (!ComponentKJS.this.kjs$hasSiblings()) {
                    return Iterators.forArray((Object[])new Component[]{ComponentKJS.this.kjs$self()});
                }
                LinkedList<Object> list = new LinkedList<Object>();
                list.add(ComponentKJS.this.kjs$self());
                for (Component child : ComponentKJS.this.m_7360_()) {
                    if (child instanceof ComponentKJS) {
                        ComponentKJS wrapped = (ComponentKJS)child;
                        wrapped.forEach(list::add);
                        continue;
                    }
                    list.add(child);
                }
                return list.iterator();
            }
        };
    }

    default public MutableComponent kjs$self() {
        return (MutableComponent)this;
    }

    @RemapForJS(value="toJson")
    default public JsonElement toJsonJS() {
        return Component.Serializer.m_130716_((Component)this.kjs$self());
    }

    default public boolean kjs$hasStyle() {
        return this.m_7383_() != null && !this.m_7383_().m_131179_();
    }

    default public boolean kjs$hasSiblings() {
        return !this.m_7360_().isEmpty();
    }

    default public void forEach(Consumer<? super Component> action) {
        this.kjs$asIterable().forEach(action);
    }

    default public MutableComponent kjs$black() {
        return this.kjs$self().m_130940_(ChatFormatting.BLACK);
    }

    default public MutableComponent kjs$darkBlue() {
        return this.kjs$self().m_130940_(ChatFormatting.DARK_BLUE);
    }

    default public MutableComponent kjs$darkGreen() {
        return this.kjs$self().m_130940_(ChatFormatting.DARK_GREEN);
    }

    default public MutableComponent kjs$darkAqua() {
        return this.kjs$self().m_130940_(ChatFormatting.DARK_AQUA);
    }

    default public MutableComponent kjs$darkRed() {
        return this.kjs$self().m_130940_(ChatFormatting.DARK_RED);
    }

    default public MutableComponent kjs$darkPurple() {
        return this.kjs$self().m_130940_(ChatFormatting.DARK_PURPLE);
    }

    default public MutableComponent kjs$gold() {
        return this.kjs$self().m_130940_(ChatFormatting.GOLD);
    }

    default public MutableComponent kjs$gray() {
        return this.kjs$self().m_130940_(ChatFormatting.GRAY);
    }

    default public MutableComponent kjs$darkGray() {
        return this.kjs$self().m_130940_(ChatFormatting.DARK_GRAY);
    }

    default public MutableComponent kjs$blue() {
        return this.kjs$self().m_130940_(ChatFormatting.BLUE);
    }

    default public MutableComponent kjs$green() {
        return this.kjs$self().m_130940_(ChatFormatting.GREEN);
    }

    default public MutableComponent kjs$aqua() {
        return this.kjs$self().m_130940_(ChatFormatting.AQUA);
    }

    default public MutableComponent kjs$red() {
        return this.kjs$self().m_130940_(ChatFormatting.RED);
    }

    default public MutableComponent kjs$lightPurple() {
        return this.kjs$self().m_130940_(ChatFormatting.LIGHT_PURPLE);
    }

    default public MutableComponent kjs$yellow() {
        return this.kjs$self().m_130940_(ChatFormatting.YELLOW);
    }

    default public MutableComponent kjs$white() {
        return this.kjs$self().m_130940_(ChatFormatting.WHITE);
    }

    default public MutableComponent kjs$color(@Nullable Color c) {
        TextColor col = c == null ? null : c.createTextColorJS();
        return this.kjs$self().m_6270_(this.m_7383_().m_131148_(col));
    }

    default public MutableComponent kjs$noColor() {
        return this.kjs$color(null);
    }

    default public MutableComponent kjs$bold(@Nullable Boolean value) {
        return this.kjs$self().m_6270_(this.m_7383_().m_131136_(value));
    }

    default public MutableComponent kjs$bold() {
        return this.kjs$bold(true);
    }

    default public MutableComponent kjs$italic(@Nullable Boolean value) {
        return this.kjs$self().m_6270_(this.m_7383_().m_131155_(value));
    }

    default public MutableComponent kjs$italic() {
        return this.kjs$italic(true);
    }

    default public MutableComponent kjs$underlined(@Nullable Boolean value) {
        return this.kjs$self().m_6270_(this.m_7383_().m_131162_(value));
    }

    default public MutableComponent kjs$underlined() {
        return this.kjs$underlined(true);
    }

    default public MutableComponent kjs$strikethrough(@Nullable Boolean value) {
        return this.kjs$self().m_6270_(this.m_7383_().m_178522_(value));
    }

    default public MutableComponent kjs$strikethrough() {
        return this.kjs$strikethrough(true);
    }

    default public MutableComponent kjs$obfuscated(@Nullable Boolean value) {
        return this.kjs$self().m_6270_(this.m_7383_().m_178524_(value));
    }

    default public MutableComponent kjs$obfuscated() {
        return this.kjs$obfuscated(true);
    }

    default public MutableComponent kjs$insertion(@Nullable String s) {
        return this.kjs$self().m_6270_(this.m_7383_().m_131138_(s));
    }

    default public MutableComponent kjs$font(@Nullable ResourceLocation s) {
        return this.kjs$self().m_6270_(this.m_7383_().m_131150_(s));
    }

    default public MutableComponent kjs$click(@Nullable ClickEvent s) {
        return this.kjs$self().m_6270_(this.m_7383_().m_131142_(s));
    }

    default public MutableComponent kjs$clickRunCommand(String command) {
        return this.kjs$click(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
    }

    default public MutableComponent kjs$clickSuggestCommand(String command) {
        return this.kjs$click(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command));
    }

    default public MutableComponent kjs$clickCopy(String text) {
        return this.kjs$click(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, text));
    }

    default public MutableComponent kjs$clickChangePage(String page) {
        return this.kjs$click(new ClickEvent(ClickEvent.Action.CHANGE_PAGE, page));
    }

    default public MutableComponent kjs$clickOpenUrl(String url) {
        return this.kjs$click(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
    }

    default public MutableComponent kjs$clickOpenFile(String path) {
        return this.kjs$click(new ClickEvent(ClickEvent.Action.OPEN_FILE, path));
    }

    default public MutableComponent kjs$hover(@Nullable Component s) {
        return this.kjs$self().m_6270_(this.m_7383_().m_131144_(s == null ? null : new HoverEvent(HoverEvent.Action.f_130831_, (Object)s)));
    }

    default public boolean kjs$isEmpty() {
        return TextWrapper.isEmpty((Component)this.kjs$self());
    }

    @Deprecated(forRemoval=true)
    default public MutableComponent kjs$rawComponent() {
        KubeJS.LOGGER.warn("Using rawComponent() is deprecated, since components no longer need to be wrapped to Text! You can safely remove this method.");
        return this.kjs$self();
    }

    @Deprecated(forRemoval=true)
    default public MutableComponent kjs$rawCopy() {
        KubeJS.LOGGER.warn("Using rawCopy() is deprecated, since components no longer need to be wrapped to Text! Use copy() instead.");
        return this.m_6881_();
    }

    @Deprecated(forRemoval=true)
    default public Component kjs$component() {
        KubeJS.LOGGER.warn("Using component() is deprecated, since components no longer need to be wrapped to Text! You can safely remove this method.");
        return this.kjs$self();
    }
}

