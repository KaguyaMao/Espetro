/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Holder
 *  net.minecraft.core.Registry
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.TagManager
 *  net.minecraft.tags.TagManager$LoadResult
 */
package net.minecraftforge.common.crafting.conditions;

import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagManager;
import net.minecraftforge.common.crafting.conditions.ICondition;

public class ConditionContext
implements ICondition.IContext {
    private final TagManager tagManager;
    private Map<ResourceKey<?>, Map<ResourceLocation, Collection<Holder<?>>>> loadedTags = null;

    public ConditionContext(TagManager tagManager) {
        this.tagManager = tagManager;
    }

    @Override
    public <T> Map<ResourceLocation, Collection<Holder<T>>> getAllTags(ResourceKey<? extends Registry<T>> registry) {
        if (this.loadedTags == null) {
            List tags = this.tagManager.m_203904_();
            if (tags.isEmpty()) {
                throw new IllegalStateException("Tags have not been loaded yet.");
            }
            this.loadedTags = new IdentityHashMap();
            for (TagManager.LoadResult loadResult : tags) {
                Map map = Collections.unmodifiableMap(loadResult.f_203929_());
                this.loadedTags.put(loadResult.f_203928_(), map);
            }
        }
        return this.loadedTags.getOrDefault(registry, Collections.emptyMap());
    }
}

