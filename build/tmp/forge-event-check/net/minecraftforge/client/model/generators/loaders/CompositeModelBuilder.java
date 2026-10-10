/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  net.minecraft.resources.ResourceLocation
 */
package net.minecraftforge.client.model.generators.loaders;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.CustomLoaderBuilder;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;

public class CompositeModelBuilder<T extends ModelBuilder<T>>
extends CustomLoaderBuilder<T> {
    private final Map<String, T> childModels = new LinkedHashMap<String, T>();
    private final List<String> itemRenderOrder = new ArrayList<String>();

    public static <T extends ModelBuilder<T>> CompositeModelBuilder<T> begin(T parent, ExistingFileHelper existingFileHelper) {
        return new CompositeModelBuilder<T>(parent, existingFileHelper);
    }

    protected CompositeModelBuilder(T parent, ExistingFileHelper existingFileHelper) {
        super(new ResourceLocation("forge:composite"), parent, existingFileHelper);
    }

    public CompositeModelBuilder<T> child(String name, T modelBuilder) {
        Preconditions.checkNotNull((Object)name, (Object)"name must not be null");
        Preconditions.checkNotNull(modelBuilder, (Object)"modelBuilder must not be null");
        this.childModels.put(name, modelBuilder);
        this.itemRenderOrder.add(name);
        return this;
    }

    public CompositeModelBuilder<T> itemRenderOrder(String ... names) {
        Preconditions.checkNotNull((Object)names, (Object)"names must not be null");
        Preconditions.checkArgument((names.length > 0 ? 1 : 0) != 0, (Object)"names must contain at least one element");
        for (String name : names) {
            if (this.childModels.containsKey(name)) continue;
            throw new IllegalArgumentException("names contains \"" + name + "\", which is not a child of this model");
        }
        this.itemRenderOrder.clear();
        this.itemRenderOrder.addAll(Arrays.asList(names));
        return this;
    }

    @Override
    public JsonObject toJson(JsonObject json) {
        json = super.toJson(json);
        JsonObject children = new JsonObject();
        for (Map.Entry<String, T> entry : this.childModels.entrySet()) {
            children.add(entry.getKey(), (JsonElement)((ModelBuilder)entry.getValue()).toJson());
        }
        json.add("children", (JsonElement)children);
        JsonArray itemRenderOrder = new JsonArray();
        for (String name : this.itemRenderOrder) {
            itemRenderOrder.add(name);
        }
        json.add("item_render_order", (JsonElement)itemRenderOrder);
        return json;
    }
}

