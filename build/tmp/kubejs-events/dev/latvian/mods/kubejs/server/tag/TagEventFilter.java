/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.TagEntry
 *  net.minecraft.tags.TagLoader$EntryWithSource
 *  net.minecraft.util.ExtraCodecs$TagOrElementLocation
 */
package dev.latvian.mods.kubejs.server.tag;

import dev.latvian.mods.kubejs.DevProperties;
import dev.latvian.mods.kubejs.server.tag.EmptyTagTargetException;
import dev.latvian.mods.kubejs.server.tag.TagEventJS;
import dev.latvian.mods.kubejs.server.tag.TagWrapper;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.kubejs.util.UtilsJS;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagLoader;
import net.minecraft.util.ExtraCodecs;

public interface TagEventFilter {
    public static TagEventFilter of(TagEventJS event, Object o) {
        if (o instanceof TagEventFilter) {
            TagEventFilter f2 = (TagEventFilter)o;
            return f2;
        }
        if (o instanceof Collection) {
            Collection list = (Collection)o;
            List<TagEventFilter> filters = list.stream().map(o1 -> TagEventFilter.of(event, o1)).flatMap(TagEventFilter::unwrap).filter(f -> f != Empty.INSTANCE).toList();
            return filters.isEmpty() ? Empty.INSTANCE : (filters.size() == 1 ? filters.get(0) : new Or(filters));
        }
        Pattern regex = UtilsJS.parseRegex(o);
        if (regex != null) {
            return new RegEx(regex);
        }
        String s = o.toString().trim();
        if (!s.isEmpty()) {
            return switch (s.charAt(0)) {
                case '#' -> new Tag(event.get(new ResourceLocation(s.substring(1))));
                case '@' -> new Namespace(s.substring(1));
                default -> new ID(new ResourceLocation(s));
            };
        }
        return Empty.INSTANCE;
    }

    public static TagEventFilter unwrap(TagEventJS event, Object[] array) {
        TagEventFilter filter = array.length == 1 ? TagEventFilter.of(event, array[0]) : TagEventFilter.of(event, Arrays.asList(array));
        return filter;
    }

    public boolean testElementId(ResourceLocation var1);

    default public boolean testTagOrElementLocation(ExtraCodecs.TagOrElementLocation element) {
        return !element.f_216196_() && this.testElementId(element.f_216195_());
    }

    default public Stream<TagEventFilter> unwrap() {
        return Stream.of(this);
    }

    default public int add(TagWrapper wrapper) {
        int count = 0;
        for (ResourceLocation id : wrapper.event.getElementIds()) {
            if (!this.testElementId(id)) continue;
            wrapper.entries.add(new TagLoader.EntryWithSource(TagEntry.m_215925_((ResourceLocation)id), "KubeJS Custom Tags"));
            ++count;
        }
        return count;
    }

    default public int remove(TagWrapper wrapper) {
        int count = 0;
        Iterator<TagLoader.EntryWithSource> itr = wrapper.entries.iterator();
        while (itr.hasNext()) {
            TagLoader.EntryWithSource it = itr.next();
            if (it.f_216042_().f_215914_ || !this.testElementId(it.f_216042_().f_215913_)) continue;
            itr.remove();
            ++count;
        }
        return count;
    }

    public static class Empty
    implements TagEventFilter {
        public static final Empty INSTANCE = new Empty();

        @Override
        public boolean testElementId(ResourceLocation resourceLocation) {
            return false;
        }

        @Override
        public boolean testTagOrElementLocation(ExtraCodecs.TagOrElementLocation element) {
            return false;
        }

        @Override
        public int add(TagWrapper wrapper) {
            return 0;
        }

        @Override
        public int remove(TagWrapper wrapper) {
            return 0;
        }
    }

    public record Or(List<TagEventFilter> filters) implements TagEventFilter
    {
        @Override
        public boolean testElementId(ResourceLocation resourceLocation) {
            for (TagEventFilter filter : this.filters) {
                if (!filter.testElementId(resourceLocation)) continue;
                return true;
            }
            return false;
        }

        @Override
        public boolean testTagOrElementLocation(ExtraCodecs.TagOrElementLocation element) {
            for (TagEventFilter filter : this.filters) {
                if (!filter.testTagOrElementLocation(element)) continue;
                return true;
            }
            return false;
        }

        @Override
        public Stream<TagEventFilter> unwrap() {
            return this.filters.stream();
        }

        @Override
        public int add(TagWrapper wrapper) {
            int count = 0;
            for (TagEventFilter filter : this.filters) {
                count += filter.add(wrapper);
            }
            return count;
        }

        @Override
        public int remove(TagWrapper wrapper) {
            int count = 0;
            for (TagEventFilter filter : this.filters) {
                count += filter.remove(wrapper);
            }
            return count;
        }
    }

    public record RegEx(Pattern pattern) implements TagEventFilter
    {
        @Override
        public boolean testElementId(ResourceLocation id) {
            return this.pattern.matcher(id.toString()).find();
        }
    }

    public record Tag(TagWrapper tag) implements TagEventFilter
    {
        @Override
        public boolean testElementId(ResourceLocation id) {
            return false;
        }

        @Override
        public boolean testTagOrElementLocation(ExtraCodecs.TagOrElementLocation element) {
            return element.f_216196_() && this.tag.id.equals((Object)element.f_216195_());
        }

        @Override
        public int add(TagWrapper wrapper) {
            wrapper.entries.add(new TagLoader.EntryWithSource(TagEntry.m_215949_((ResourceLocation)this.tag.id), "KubeJS Custom Tags"));
            return 1;
        }

        @Override
        public int remove(TagWrapper wrapper) {
            int count = 0;
            Iterator<TagLoader.EntryWithSource> itr = wrapper.entries.iterator();
            while (itr.hasNext()) {
                TagLoader.EntryWithSource it = itr.next();
                if (!it.f_216042_().f_215914_ || !it.f_216042_().f_215913_.equals((Object)this.tag.id)) continue;
                itr.remove();
                ++count;
            }
            return count;
        }
    }

    public record Namespace(String namespace) implements TagEventFilter
    {
        @Override
        public boolean testElementId(ResourceLocation id) {
            return id.m_135827_().equals(this.namespace);
        }
    }

    public record ID(ResourceLocation id) implements TagEventFilter
    {
        @Override
        public boolean testElementId(ResourceLocation id) {
            return this.id.equals((Object)id);
        }

        @Override
        public int add(TagWrapper wrapper) {
            if (wrapper.event.getElementIds().contains(this.id)) {
                wrapper.entries.add(new TagLoader.EntryWithSource(TagEntry.m_215925_((ResourceLocation)this.id), "KubeJS Custom Tags"));
                return 1;
            }
            String msg = "No such element %s in registry %s".formatted(this.id, wrapper.event.registry);
            if (DevProperties.get().strictTags) {
                throw new EmptyTagTargetException(msg);
            }
            if (DevProperties.get().logSkippedTags) {
                ConsoleJS.SERVER.warn(msg);
            }
            return 0;
        }
    }
}

