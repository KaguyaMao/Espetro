/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.gson.JsonObject
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.packs.PackType
 */
package net.minecraftforge.client.model.generators.loaders;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraftforge.client.model.generators.CustomLoaderBuilder;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ObjModelBuilder<T extends ModelBuilder<T>>
extends CustomLoaderBuilder<T> {
    private ResourceLocation modelLocation;
    private Boolean automaticCulling;
    private Boolean shadeQuads;
    private Boolean flipV;
    private Boolean emissiveAmbient;
    private ResourceLocation mtlOverride;

    public static <T extends ModelBuilder<T>> ObjModelBuilder<T> begin(T parent, ExistingFileHelper existingFileHelper) {
        return new ObjModelBuilder<T>(parent, existingFileHelper);
    }

    protected ObjModelBuilder(T parent, ExistingFileHelper existingFileHelper) {
        super(new ResourceLocation("forge:obj"), parent, existingFileHelper);
    }

    public ObjModelBuilder<T> modelLocation(ResourceLocation modelLocation) {
        Preconditions.checkNotNull((Object)modelLocation, (Object)"modelLocation must not be null");
        Preconditions.checkArgument((boolean)this.existingFileHelper.exists(modelLocation, PackType.CLIENT_RESOURCES), (String)"OBJ Model %s does not exist in any known resource pack", (Object)modelLocation);
        this.modelLocation = modelLocation;
        return this;
    }

    public ObjModelBuilder<T> automaticCulling(boolean automaticCulling) {
        this.automaticCulling = automaticCulling;
        return this;
    }

    public ObjModelBuilder<T> shadeQuads(boolean shadeQuads) {
        this.shadeQuads = shadeQuads;
        return this;
    }

    public ObjModelBuilder<T> flipV(boolean flipV) {
        this.flipV = flipV;
        return this;
    }

    public ObjModelBuilder<T> emissiveAmbient(boolean ambientEmissive) {
        this.emissiveAmbient = ambientEmissive;
        return this;
    }

    public ObjModelBuilder<T> overrideMaterialLibrary(ResourceLocation mtlOverride) {
        Preconditions.checkNotNull((Object)mtlOverride, (Object)"mtlOverride must not be null");
        Preconditions.checkArgument((boolean)this.existingFileHelper.exists(mtlOverride, PackType.CLIENT_RESOURCES), (String)"OBJ Model %s does not exist in any known resource pack", (Object)mtlOverride);
        this.mtlOverride = mtlOverride;
        return this;
    }

    @Override
    public JsonObject toJson(JsonObject json) {
        json = super.toJson(json);
        Preconditions.checkNotNull((Object)this.modelLocation, (Object)"modelLocation must not be null");
        json.addProperty("model", this.modelLocation.toString());
        if (this.automaticCulling != null) {
            json.addProperty("automatic_culling", this.automaticCulling);
        }
        if (this.shadeQuads != null) {
            json.addProperty("shade_quads", this.shadeQuads);
        }
        if (this.flipV != null) {
            json.addProperty("flip_v", this.flipV);
        }
        if (this.emissiveAmbient != null) {
            json.addProperty("emissive_ambient", this.emissiveAmbient);
        }
        if (this.mtlOverride != null) {
            json.addProperty("mtl_override", this.mtlOverride.toString());
        }
        return json;
    }
}

