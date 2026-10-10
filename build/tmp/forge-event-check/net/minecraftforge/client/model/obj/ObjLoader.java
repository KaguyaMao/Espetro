/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  net.minecraft.client.Minecraft
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.packs.resources.Resource
 *  net.minecraft.server.packs.resources.ResourceManager
 *  net.minecraft.server.packs.resources.ResourceManagerReloadListener
 *  net.minecraft.util.GsonHelper
 */
package net.minecraftforge.client.model.obj;

import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.FileNotFoundException;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.obj.ObjMaterialLibrary;
import net.minecraftforge.client.model.obj.ObjModel;
import net.minecraftforge.client.model.obj.ObjTokenizer;

public class ObjLoader
implements IGeometryLoader<ObjModel>,
ResourceManagerReloadListener {
    public static ObjLoader INSTANCE = new ObjLoader();
    private final Map<ObjModel.ModelSettings, ObjModel> modelCache = Maps.newConcurrentMap();
    private final Map<ResourceLocation, ObjMaterialLibrary> materialCache = Maps.newConcurrentMap();
    private ResourceManager manager = Minecraft.m_91087_().m_91098_();

    public void m_6213_(ResourceManager resourceManager) {
        this.modelCache.clear();
        this.materialCache.clear();
        this.manager = resourceManager;
    }

    @Override
    public ObjModel read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) {
        if (!jsonObject.has("model")) {
            throw new JsonParseException("OBJ Loader requires a 'model' key that points to a valid .OBJ model.");
        }
        String modelLocation = jsonObject.get("model").getAsString();
        boolean automaticCulling = GsonHelper.m_13855_((JsonObject)jsonObject, (String)"automatic_culling", (boolean)true);
        boolean shadeQuads = GsonHelper.m_13855_((JsonObject)jsonObject, (String)"shade_quads", (boolean)true);
        boolean flipV = GsonHelper.m_13855_((JsonObject)jsonObject, (String)"flip_v", (boolean)false);
        boolean emissiveAmbient = GsonHelper.m_13855_((JsonObject)jsonObject, (String)"emissive_ambient", (boolean)true);
        String mtlOverride = GsonHelper.m_13851_((JsonObject)jsonObject, (String)"mtl_override", null);
        return this.loadModel(new ObjModel.ModelSettings(new ResourceLocation(modelLocation), automaticCulling, shadeQuads, flipV, emissiveAmbient, mtlOverride));
    }

    public ObjModel loadModel(ObjModel.ModelSettings settings) {
        return this.modelCache.computeIfAbsent(settings, data -> {
            ObjModel objModel;
            Resource resource = (Resource)this.manager.m_213713_(settings.modelLocation()).orElseThrow();
            ObjTokenizer tokenizer = new ObjTokenizer(resource.m_215507_());
            try {
                objModel = ObjModel.parse(tokenizer, settings);
            }
            catch (Throwable throwable) {
                try {
                    try {
                        tokenizer.close();
                    }
                    catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                    throw throwable;
                }
                catch (FileNotFoundException e) {
                    throw new RuntimeException("Could not find OBJ model", e);
                }
                catch (Exception e) {
                    throw new RuntimeException("Could not read OBJ model", e);
                }
            }
            tokenizer.close();
            return objModel;
        });
    }

    public ObjMaterialLibrary loadMaterialLibrary(ResourceLocation materialLocation) {
        return this.materialCache.computeIfAbsent(materialLocation, location -> {
            ObjMaterialLibrary objMaterialLibrary;
            Resource resource = (Resource)this.manager.m_213713_(location).orElseThrow();
            ObjTokenizer rdr = new ObjTokenizer(resource.m_215507_());
            try {
                objMaterialLibrary = new ObjMaterialLibrary(rdr);
            }
            catch (Throwable throwable) {
                try {
                    try {
                        rdr.close();
                    }
                    catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                    throw throwable;
                }
                catch (FileNotFoundException e) {
                    throw new RuntimeException("Could not find OBJ material library", e);
                }
                catch (Exception e) {
                    throw new RuntimeException("Could not read OBJ material library", e);
                }
            }
            rdr.close();
            return objMaterialLibrary;
        });
    }
}

