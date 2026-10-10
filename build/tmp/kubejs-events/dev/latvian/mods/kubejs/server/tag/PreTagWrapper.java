/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 */
package dev.latvian.mods.kubejs.server.tag;

import dev.latvian.mods.kubejs.server.tag.PreTagEventJS;
import dev.latvian.mods.kubejs.server.tag.TagEventJS;
import dev.latvian.mods.kubejs.server.tag.TagWrapper;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.resources.ResourceLocation;

public class PreTagWrapper
extends TagWrapper {
    public final PreTagEventJS preEvent;
    public final ResourceLocation id;

    public PreTagWrapper(PreTagEventJS e, ResourceLocation i) {
        super(e, i, null);
        this.preEvent = e;
        this.id = i;
    }

    @Override
    public TagWrapper add(Object ... filters) {
        this.preEvent.actions.add(new AddAction(this.id, filters));
        return this;
    }

    @Override
    public TagWrapper remove(Object ... filters) {
        this.preEvent.actions.add(new RemoveAction(this.id, filters));
        return this;
    }

    @Override
    public TagWrapper removeAll() {
        this.preEvent.actions.add(new RemoveAllAction(this.id));
        return this;
    }

    @Override
    public List<ResourceLocation> getObjectIds() {
        this.preEvent.invalid = true;
        return new ArrayList<ResourceLocation>(0);
    }

    public record AddAction(ResourceLocation tag, Object[] filters) implements Consumer<TagEventJS>
    {
        @Override
        public void accept(TagEventJS e) {
            e.add(this.tag, this.filters);
        }
    }

    public record RemoveAction(ResourceLocation tag, Object[] filters) implements Consumer<TagEventJS>
    {
        @Override
        public void accept(TagEventJS e) {
            e.remove(this.tag, this.filters);
        }
    }

    public record RemoveAllAction(ResourceLocation tag) implements Consumer<TagEventJS>
    {
        @Override
        public void accept(TagEventJS e) {
            e.removeAll(this.tag);
        }
    }
}

