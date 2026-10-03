/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.github.mcmodderanchor.simplebedrockmodel.v1.common.animation.BedrockAnimation
 *  com.github.mcmodderanchor.simplebedrockmodel.v1.common.resource.GsonUtil
 *  com.github.mcmodderanchor.simplebedrockmodel.v1.common.resource.pojo.BedrockAnimationFile
 *  com.github.mcmodderanchor.simplebedrockmodel.v1.common.resource.pojo.BedrockModelPOJO
 *  com.google.gson.Gson
 *  net.minecraft.resources.FileToIdConverter
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.packs.resources.Resource
 *  net.minecraft.server.packs.resources.ResourceManager
 *  net.minecraft.server.packs.resources.SimplePreparableReloadListener
 *  net.minecraft.util.GsonHelper
 *  net.minecraft.util.profiling.ProfilerFiller
 */
package com.redabysslucia.dragonrise_reforge.resource.model;

import com.github.mcmodderanchor.simplebedrockmodel.v1.common.animation.BedrockAnimation;
import com.github.mcmodderanchor.simplebedrockmodel.v1.common.resource.GsonUtil;
import com.github.mcmodderanchor.simplebedrockmodel.v1.common.resource.pojo.BedrockAnimationFile;
import com.github.mcmodderanchor.simplebedrockmodel.v1.common.resource.pojo.BedrockModelPOJO;
import com.google.gson.Gson;
import com.redabysslucia.dragonrise_reforge.Dragonrise_reforge;
import java.io.BufferedReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;

public abstract class BedrockModelReloadListener<T>
extends SimplePreparableReloadListener<Map<ResourceLocation, BedrockModelPOJO>> {
    protected final String modelPath;
    protected final String animPath;
    protected final Gson gson;
    protected final Map<ResourceLocation, T> models = new HashMap<ResourceLocation, T>();
    protected final Map<ResourceLocation, BedrockAnimationFile> animFiles = new HashMap<ResourceLocation, BedrockAnimationFile>();
    protected final Map<ResourceLocation, List<BedrockAnimation>> animations = new HashMap<ResourceLocation, List<BedrockAnimation>>();
    protected final Map<ResourceLocation, ResourceLocation> idToModelPaths = new HashMap<ResourceLocation, ResourceLocation>();
    protected final Map<ResourceLocation, ResourceLocation> animPathToIds = new HashMap<ResourceLocation, ResourceLocation>();

    public BedrockModelReloadListener(String modelPath) {
        this(modelPath, "");
    }

    public BedrockModelReloadListener(String modelPath, String animPath) {
        this.modelPath = modelPath;
        this.animPath = animPath;
        this.gson = GsonUtil.CLIENT_GSON;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected Map<ResourceLocation, BedrockModelPOJO> prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
        HashMap<ResourceLocation, BedrockModelPOJO> map = new HashMap<ResourceLocation, BedrockModelPOJO>();
        FileToIdConverter modelConverter = FileToIdConverter.m_246568_((String)this.modelPath);
        for (Map.Entry entry : modelConverter.m_247457_(resourceManager).entrySet()) {
            ResourceLocation location = (ResourceLocation)entry.getKey();
            ResourceLocation id = modelConverter.m_245273_(location);
            id = new ResourceLocation(id.m_135827_(), id.m_135815_().replace(".geo", ""));
            try (BufferedReader reader = ((Resource)entry.getValue()).m_215508_();){
                BedrockModelPOJO pojo = (BedrockModelPOJO)GsonHelper.m_13776_((Gson)this.gson, (Reader)reader, BedrockModelPOJO.class);
                BedrockModelPOJO existed = map.put(location, pojo);
                this.idToModelPaths.put(id, location);
                if (existed == null) continue;
                throw new IllegalStateException("Duplicate model resource " + location);
            }
            catch (Exception e) {
                Dragonrise_reforge.LOGGER.error("Error while reading model {}", (Object)location, (Object)e);
            }
        }
        if (!this.animPath.isEmpty()) {
            FileToIdConverter animConverter = FileToIdConverter.m_246568_((String)this.animPath);
            for (Map.Entry entry : animConverter.m_247457_(resourceManager).entrySet()) {
                ResourceLocation location = (ResourceLocation)entry.getKey();
                ResourceLocation id = animConverter.m_245273_(location);
                id = new ResourceLocation(id.m_135827_(), id.m_135815_().replace(".animation", ""));
                try (BufferedReader reader = ((Resource)entry.getValue()).m_215508_();){
                    BedrockAnimationFile file = (BedrockAnimationFile)GsonHelper.m_13776_((Gson)this.gson, (Reader)reader, BedrockAnimationFile.class);
                    BedrockAnimationFile existed = this.animFiles.put(location, file);
                    this.animPathToIds.put(location, id);
                    if (existed == null) continue;
                    throw new IllegalStateException("Duplicate animation resource " + location);
                }
                catch (Exception e) {
                    Dragonrise_reforge.LOGGER.error("Error while reading animation {}", (Object)location, (Object)e);
                }
            }
        }
        return map;
    }

    protected void apply(Map<ResourceLocation, BedrockModelPOJO> map, ResourceManager resourceManager, ProfilerFiller profiler) {
        this.models.clear();
        this.animations.clear();
    }

    public T getModel(ResourceLocation path) {
        return this.models.get(path);
    }

    public List<BedrockAnimation> getAnimation(ResourceLocation path) {
        return this.animations.get(path);
    }
}

