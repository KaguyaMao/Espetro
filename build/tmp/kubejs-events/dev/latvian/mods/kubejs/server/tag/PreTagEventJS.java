/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 */
package dev.latvian.mods.kubejs.server.tag;

import dev.latvian.mods.kubejs.DevProperties;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.bindings.event.ServerEvents;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.script.ScriptTypeHolder;
import dev.latvian.mods.kubejs.server.tag.PreTagWrapper;
import dev.latvian.mods.kubejs.server.tag.TagEventJS;
import dev.latvian.mods.kubejs.server.tag.TagWrapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class PreTagEventJS
extends TagEventJS {
    public final Map<ResourceLocation, PreTagWrapper> tags = new ConcurrentHashMap<ResourceLocation, PreTagWrapper>();
    public final List<Consumer<TagEventJS>> actions = new ArrayList<Consumer<TagEventJS>>();
    public boolean invalid;

    public static void handle(Map<ResourceKey<?>, PreTagEventJS> tagEventHolders) {
        tagEventHolders.clear();
        if (ServerEvents.TAGS.hasListeners()) {
            for (Object id : ServerEvents.TAGS.findUniqueExtraIds(ScriptType.SERVER)) {
                PreTagEventJS e;
                block4: {
                    e = new PreTagEventJS(RegistryInfo.of((ResourceKey)id));
                    try {
                        ServerEvents.TAGS.post((ScriptTypeHolder)ScriptType.SERVER, id, e);
                    }
                    catch (Exception ex) {
                        e.invalid = true;
                        if (!DevProperties.get().debugInfo) break block4;
                        KubeJS.LOGGER.warn("Pre Tag event for " + String.valueOf(e.registry) + " failed:");
                        ex.printStackTrace();
                    }
                }
                if (e.invalid) continue;
                tagEventHolders.put(e.registry.key, e);
            }
        }
    }

    public PreTagEventJS(RegistryInfo registry) {
        super(registry, null);
    }

    @Override
    protected TagWrapper createTagWrapper(ResourceLocation id) {
        return new PreTagWrapper(this, id);
    }

    @Override
    public void removeAllTagsFrom(Object ... ignored) {
        this.actions.add(new RemoveAllTagsFromAction(ignored));
    }

    @Override
    public Set<ResourceLocation> getElementIds() {
        return Set.of();
    }

    public record RemoveAllTagsFromAction(Object[] ignored) implements Consumer<TagEventJS>
    {
        @Override
        public void accept(TagEventJS e) {
            e.removeAllTagsFrom(this.ignored);
        }
    }
}

