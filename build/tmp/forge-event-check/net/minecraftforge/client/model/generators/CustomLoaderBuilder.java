/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  net.minecraft.resources.ResourceLocation
 */
package net.minecraftforge.client.model.generators;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;

public abstract class CustomLoaderBuilder<T extends ModelBuilder<T>> {
    protected final ResourceLocation loaderId;
    protected final T parent;
    protected final ExistingFileHelper existingFileHelper;
    protected final Map<String, Boolean> visibility = new LinkedHashMap<String, Boolean>();

    protected CustomLoaderBuilder(ResourceLocation loaderId, T parent, ExistingFileHelper existingFileHelper) {
        this.loaderId = loaderId;
        this.parent = parent;
        this.existingFileHelper = existingFileHelper;
    }

    public CustomLoaderBuilder<T> visibility(String partName, boolean show) {
        Preconditions.checkNotNull((Object)partName, (Object)"partName must not be null");
        this.visibility.put(partName, show);
        return this;
    }

    public T end() {
        return this.parent;
    }

    public JsonObject toJson(JsonObject json) {
        json.addProperty("loader", this.loaderId.toString());
        if (this.visibility.size() > 0) {
            JsonObject visibilityObj = new JsonObject();
            for (Map.Entry<String, Boolean> entry : this.visibility.entrySet()) {
                visibilityObj.addProperty(entry.getKey(), entry.getValue());
            }
            json.add("visibility", (JsonElement)visibilityObj);
        }
        return json;
    }
}

