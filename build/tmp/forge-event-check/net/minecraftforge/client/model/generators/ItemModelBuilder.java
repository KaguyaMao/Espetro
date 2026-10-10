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
package net.minecraftforge.client.model.generators;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ItemModelBuilder
extends ModelBuilder<ItemModelBuilder> {
    protected List<OverrideBuilder> overrides = new ArrayList<OverrideBuilder>();

    public ItemModelBuilder(ResourceLocation outputLocation, ExistingFileHelper existingFileHelper) {
        super(outputLocation, existingFileHelper);
    }

    public OverrideBuilder override() {
        OverrideBuilder ret = new OverrideBuilder();
        this.overrides.add(ret);
        return ret;
    }

    public OverrideBuilder override(int index) {
        Preconditions.checkElementIndex((int)index, (int)this.overrides.size(), (String)"override");
        return this.overrides.get(index);
    }

    @Override
    public JsonObject toJson() {
        JsonObject root = super.toJson();
        if (!this.overrides.isEmpty()) {
            JsonArray overridesJson = new JsonArray();
            this.overrides.stream().map(OverrideBuilder::toJson).forEach(arg_0 -> ((JsonArray)overridesJson).add(arg_0));
            root.add("overrides", (JsonElement)overridesJson);
        }
        return root;
    }

    public class OverrideBuilder {
        private ModelFile model;
        private final Map<ResourceLocation, Float> predicates = new LinkedHashMap<ResourceLocation, Float>();

        public OverrideBuilder model(ModelFile model) {
            this.model = model;
            model.assertExistence();
            return this;
        }

        public OverrideBuilder predicate(ResourceLocation key, float value) {
            this.predicates.put(key, Float.valueOf(value));
            return this;
        }

        public ItemModelBuilder end() {
            return ItemModelBuilder.this;
        }

        JsonObject toJson() {
            JsonObject ret = new JsonObject();
            JsonObject predicatesJson = new JsonObject();
            this.predicates.forEach((key, val) -> predicatesJson.addProperty(key.toString(), (Number)val));
            ret.add("predicate", (JsonElement)predicatesJson);
            ret.addProperty("model", this.model.getLocation().toString());
            return ret;
        }
    }
}

