/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.item.crafting.Ingredient
 */
package dev.latvian.mods.kubejs.item;

import dev.latvian.mods.kubejs.bindings.TextWrapper;
import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.kubejs.util.ListJS;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

@Info(value="Invoked when registering handlers for item tooltips.\n\n`text` can be a component or a list of components.\n")
public class ItemTooltipEventJS
extends EventJS {
    private final Map<Item, List<StaticTooltipHandler>> map;

    public ItemTooltipEventJS(Map<Item, List<StaticTooltipHandler>> m) {
        this.map = m;
    }

    @Info(value="Adds text to all items matching the ingredient.")
    public void add(Ingredient item, Object text) {
        if (item.kjs$isWildcard()) {
            this.addToAll(text);
            return;
        }
        StaticTooltipHandlerFromLines l = new StaticTooltipHandlerFromLines(text);
        if (!l.lines.isEmpty()) {
            for (Item i : item.kjs$getItemTypes()) {
                if (i == Items.f_41852_) continue;
                this.map.computeIfAbsent(i, k -> new ArrayList()).add(l);
            }
        }
    }

    @Info(value="Adds text to all items.")
    public void addToAll(Object text) {
        StaticTooltipHandlerFromLines l = new StaticTooltipHandlerFromLines(text);
        if (!l.lines.isEmpty()) {
            this.map.computeIfAbsent(Items.f_41852_, k -> new ArrayList()).add(l);
        }
    }

    @Info(value="Adds a dynamic tooltip handler to all items matching the ingredient.")
    public void addAdvanced(Ingredient item, StaticTooltipHandlerFromJS handler) {
        if (item.kjs$isWildcard()) {
            this.addAdvancedToAll(handler);
            return;
        }
        StaticTooltipHandlerFromJSWrapper l = new StaticTooltipHandlerFromJSWrapper(handler);
        for (Item i : item.kjs$getItemTypes()) {
            if (i == Items.f_41852_) continue;
            this.map.computeIfAbsent(i, k -> new ArrayList()).add(l);
        }
    }

    @Info(value="Adds a dynamic tooltip handler to all items.")
    public void addAdvancedToAll(StaticTooltipHandlerFromJS handler) {
        this.map.computeIfAbsent(Items.f_41852_, k -> new ArrayList()).add(new StaticTooltipHandlerFromJSWrapper(handler));
    }

    @Info(value="Is shift key pressed.")
    public boolean isShift() {
        return Screen.m_96638_();
    }

    @Info(value="Is control key pressed.")
    public boolean isCtrl() {
        return Screen.m_96637_();
    }

    @Info(value="Is alt key pressed.")
    public boolean isAlt() {
        return Screen.m_96639_();
    }

    public static class StaticTooltipHandlerFromLines
    implements StaticTooltipHandler {
        public final List<Component> lines;

        public StaticTooltipHandlerFromLines(List<Component> l) {
            this.lines = l;
        }

        public StaticTooltipHandlerFromLines(Object o) {
            this.lines = new ArrayList<Component>();
            for (Object o1 : ListJS.orSelf(o)) {
                this.lines.add((Component)TextWrapper.of(o1));
            }
        }

        @Override
        public void tooltip(ItemStack stack, boolean advanced, List<Component> components) {
            if (!stack.m_41619_()) {
                components.addAll(this.lines);
            }
        }
    }

    @FunctionalInterface
    public static interface StaticTooltipHandlerFromJS {
        public void accept(ItemStack var1, boolean var2, List<Object> var3);
    }

    public static class StaticTooltipHandlerFromJSWrapper
    implements StaticTooltipHandler {
        private final StaticTooltipHandlerFromJS handler;

        public StaticTooltipHandlerFromJSWrapper(StaticTooltipHandlerFromJS h) {
            this.handler = h;
        }

        @Override
        public void tooltip(ItemStack stack, boolean advanced, List<Component> components) {
            if (stack.m_41619_()) {
                return;
            }
            ArrayList<Component> text = new ArrayList<Component>(components);
            try {
                this.handler.accept(stack, advanced, text);
            }
            catch (Exception ex) {
                ConsoleJS.CLIENT.error("Error while gathering tooltip for " + String.valueOf(stack), ex);
            }
            components.clear();
            for (Object e : text) {
                components.add((Component)TextWrapper.of(e));
            }
        }
    }

    @FunctionalInterface
    public static interface StaticTooltipHandler {
        public void tooltip(ItemStack var1, boolean var2, List<Component> var3);
    }
}

