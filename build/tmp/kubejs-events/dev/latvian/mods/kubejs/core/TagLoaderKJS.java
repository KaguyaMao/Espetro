/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  net.minecraft.core.Registry
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.TagLoader$EntryWithSource
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.core;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import dev.latvian.mods.kubejs.bindings.event.ServerEvents;
import dev.latvian.mods.kubejs.item.ingredient.TagContext;
import dev.latvian.mods.kubejs.registry.BuilderBase;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.server.DataExport;
import dev.latvian.mods.kubejs.server.ServerScriptManager;
import dev.latvian.mods.kubejs.server.tag.PreTagEventJS;
import dev.latvian.mods.kubejs.server.tag.TagEventFilter;
import dev.latvian.mods.kubejs.server.tag.TagEventJS;
import dev.latvian.mods.kubejs.server.tag.TagWrapper;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagLoader;
import org.jetbrains.annotations.Nullable;

public interface TagLoaderKJS<T> {
    default public void kjs$customTags(ServerScriptManager ssm, Map<ResourceLocation, List<TagLoader.EntryWithSource>> map) {
        TagContext.INSTANCE.setValue((Object)TagContext.EMPTY);
        Registry<T> reg = this.kjs$getRegistry();
        if (reg == null) {
            return;
        }
        RegistryInfo<?> regInfo = RegistryInfo.of(reg.m_123023_());
        if (regInfo.hasDefaultTags || ServerEvents.TAGS.hasListeners(reg.m_123023_())) {
            PreTagEventJS preEvent = ssm.preTagEvents.get(reg.m_123023_());
            TagEventJS event = new TagEventJS(regInfo, reg);
            for (Map.Entry<ResourceLocation, List<TagLoader.EntryWithSource>> entry : map.entrySet()) {
                Iterator<ResourceLocation> w = new TagWrapper(event, entry.getKey(), entry.getValue());
                event.tags.put(((TagWrapper)((Object)w)).id, (TagWrapper)((Object)w));
                if (!ConsoleJS.SERVER.shouldPrintDebug()) continue;
                ConsoleJS.SERVER.debug("Tags %s/#%s; %d".formatted(regInfo, ((TagWrapper)((Object)w)).id, ((TagWrapper)((Object)w)).entries.size()));
            }
            for (BuilderBase builderBase : regInfo.objects.values()) {
                for (ResourceLocation s : builderBase.defaultTags) {
                    event.add(s, new TagEventFilter.ID(builderBase.id));
                }
            }
            if (preEvent == null) {
                ServerEvents.TAGS.post(event, regInfo.key, TagEventJS.TAG_EVENT_HANDLER);
            } else {
                for (Consumer consumer : preEvent.actions) {
                    consumer.accept(event);
                }
            }
            map.clear();
            for (Map.Entry entry : event.tags.entrySet()) {
                map.put((ResourceLocation)entry.getKey(), ((TagWrapper)entry.getValue()).entries);
            }
            if (event.totalAdded > 0 || event.totalRemoved > 0 || ConsoleJS.SERVER.shouldPrintDebug()) {
                ConsoleJS.SERVER.info("[%s] Found %d tags, added %d objects, removed %d objects".formatted(regInfo, event.tags.size(), event.totalAdded, event.totalRemoved));
            }
        }
        if (DataExport.export != null) {
            String loc = "tags/" + String.valueOf(regInfo) + "/";
            for (Map.Entry<ResourceLocation, List<TagLoader.EntryWithSource>> entry : map.entrySet()) {
                ArrayList<String> arrayList = new ArrayList<String>();
                for (TagLoader.EntryWithSource e : entry.getValue()) {
                    arrayList.add(e.f_216042_().toString());
                }
                arrayList.sort(String.CASE_INSENSITIVE_ORDER);
                JsonArray arr = new JsonArray();
                for (String e : arrayList) {
                    arr.add(e);
                }
                DataExport.export.addJson(loc + String.valueOf(entry.getKey()) + ".json", (JsonElement)arr);
            }
        }
    }

    public void kjs$setRegistry(Registry<T> var1);

    @Nullable
    public Registry<T> kjs$getRegistry();
}

