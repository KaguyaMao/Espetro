/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.architectury.event.Event
 *  dev.architectury.event.EventFactory
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.crafting.Recipe
 *  net.minecraft.world.item.crafting.RecipeSerializer
 */
package dev.latvian.mods.kubejs.recipe.special;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.event.EventResult;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class SpecialRecipeSerializerManager
extends EventJS {
    public static final SpecialRecipeSerializerManager INSTANCE = new SpecialRecipeSerializerManager();
    public static final Event<Runnable> EVENT = EventFactory.createLoop((Object[])new Runnable[0]);
    private final Map<ResourceLocation, Boolean> data = new HashMap<ResourceLocation, Boolean>();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void reset() {
        Map<ResourceLocation, Boolean> map = this.data;
        synchronized (map) {
            this.data.clear();
        }
    }

    @Override
    protected void afterPosted(EventResult result) {
        ((Runnable)EVENT.invoker()).run();
    }

    public boolean isSpecial(Recipe<?> recipe) {
        return this.data.getOrDefault(RegistryInfo.RECIPE_SERIALIZER.getId(recipe.m_7707_()), recipe.m_5598_());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void ignoreSpecialFlag(ResourceLocation id) {
        Map<ResourceLocation, Boolean> map = this.data;
        synchronized (map) {
            this.data.put(id, false);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addSpecialFlag(ResourceLocation id) {
        Map<ResourceLocation, Boolean> map = this.data;
        synchronized (map) {
            this.data.put(id, true);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void ignoreSpecialMod(String modid) {
        Map<ResourceLocation, Boolean> map = this.data;
        synchronized (map) {
            for (Map.Entry<ResourceKey<RecipeSerializer>, RecipeSerializer> entry : RegistryInfo.RECIPE_SERIALIZER.entrySet()) {
                if (!entry.getKey().m_135782_().m_135827_().equals(modid)) continue;
                this.data.put(entry.getKey().m_135782_(), false);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addSpecialMod(String modid) {
        Map<ResourceLocation, Boolean> map = this.data;
        synchronized (map) {
            for (Map.Entry<ResourceKey<RecipeSerializer>, RecipeSerializer> entry : RegistryInfo.RECIPE_SERIALIZER.entrySet()) {
                if (!entry.getKey().m_135782_().m_135827_().equals(modid)) continue;
                this.data.put(entry.getKey().m_135782_(), true);
            }
        }
    }
}

