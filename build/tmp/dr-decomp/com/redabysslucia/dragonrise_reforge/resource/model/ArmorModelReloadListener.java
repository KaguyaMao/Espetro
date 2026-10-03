/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.github.mcmodderanchor.simplebedrockmodel.v1.client.model.BedrockArmorModel
 *  com.github.mcmodderanchor.simplebedrockmodel.v1.common.BoneIndexProvider
 *  com.github.mcmodderanchor.simplebedrockmodel.v1.common.animation.BedrockAnimation
 *  com.github.mcmodderanchor.simplebedrockmodel.v1.common.resource.pojo.BedrockAnimationFile
 *  com.github.mcmodderanchor.simplebedrockmodel.v1.common.resource.pojo.BedrockModelPOJO
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.packs.resources.ResourceManager
 *  net.minecraft.util.profiling.ProfilerFiller
 */
package com.redabysslucia.dragonrise_reforge.resource.model;

import com.github.mcmodderanchor.simplebedrockmodel.v1.client.model.BedrockArmorModel;
import com.github.mcmodderanchor.simplebedrockmodel.v1.common.BoneIndexProvider;
import com.github.mcmodderanchor.simplebedrockmodel.v1.common.animation.BedrockAnimation;
import com.github.mcmodderanchor.simplebedrockmodel.v1.common.resource.pojo.BedrockAnimationFile;
import com.github.mcmodderanchor.simplebedrockmodel.v1.common.resource.pojo.BedrockModelPOJO;
import com.redabysslucia.dragonrise_reforge.resource.model.BedrockModelReloadListener;
import java.util.List;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

public class ArmorModelReloadListener
extends BedrockModelReloadListener<BedrockArmorModel> {
    public static final ArmorModelReloadListener INSTANCE = new ArmorModelReloadListener();

    private ArmorModelReloadListener() {
        super("models/bedrock/armor", "animations/bedrock/armor");
    }

    @Override
    protected void apply(Map<ResourceLocation, BedrockModelPOJO> map, ResourceManager resourceManager, ProfilerFiller profiler) {
        super.apply(map, resourceManager, profiler);
        for (Map.Entry<ResourceLocation, BedrockModelPOJO> entry : map.entrySet()) {
            this.models.put(entry.getKey(), new BedrockArmorModel(entry.getValue()));
        }
        for (Map.Entry<Object, Object> entry : this.animFiles.entrySet()) {
            BedrockArmorModel model;
            ResourceLocation path;
            ResourceLocation id = (ResourceLocation)this.animPathToIds.get(entry.getKey());
            if (id == null || (path = (ResourceLocation)this.idToModelPaths.get(id)) == null || (model = (BedrockArmorModel)this.models.get(path)) == null) continue;
            List animations = BedrockAnimation.createAnimation((BedrockAnimationFile)((BedrockAnimationFile)entry.getValue()), (BoneIndexProvider)model);
            this.animations.put((ResourceLocation)entry.getKey(), animations);
        }
        this.animFiles.clear();
    }
}

