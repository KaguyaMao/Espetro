/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.mojang.math.Transformation
 *  net.minecraft.client.renderer.block.model.BlockElement
 *  net.minecraft.client.renderer.block.model.BlockElement$Deserializer
 *  net.minecraft.client.renderer.block.model.BlockElementFace
 *  net.minecraft.client.renderer.block.model.BlockElementFace$Deserializer
 *  net.minecraft.client.renderer.block.model.BlockFaceUV
 *  net.minecraft.client.renderer.block.model.BlockFaceUV$Deserializer
 *  net.minecraft.client.renderer.block.model.BlockModel
 *  net.minecraft.client.renderer.block.model.BlockModel$Deserializer
 *  net.minecraft.client.renderer.block.model.ItemOverride
 *  net.minecraft.client.renderer.block.model.ItemOverride$Deserializer
 *  net.minecraft.client.renderer.block.model.ItemTransform
 *  net.minecraft.client.renderer.block.model.ItemTransform$Deserializer
 *  net.minecraft.client.renderer.block.model.ItemTransforms
 *  net.minecraft.client.renderer.block.model.ItemTransforms$Deserializer
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.GsonHelper
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.client.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.math.Transformation;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.client.model.geometry.GeometryLoaderManager;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;
import net.minecraftforge.common.util.TransformationHelper;
import org.jetbrains.annotations.Nullable;

public class ExtendedBlockModelDeserializer
extends BlockModel.Deserializer {
    public static final Gson INSTANCE = new GsonBuilder().registerTypeAdapter(BlockModel.class, (Object)new ExtendedBlockModelDeserializer()).registerTypeAdapter(BlockElement.class, (Object)new BlockElement.Deserializer()).registerTypeAdapter(BlockElementFace.class, (Object)new BlockElementFace.Deserializer()).registerTypeAdapter(BlockFaceUV.class, (Object)new BlockFaceUV.Deserializer()).registerTypeAdapter(ItemTransform.class, (Object)new ItemTransform.Deserializer()).registerTypeAdapter(ItemTransforms.class, (Object)new ItemTransforms.Deserializer()).registerTypeAdapter(ItemOverride.class, (Object)new ItemOverride.Deserializer()).registerTypeAdapter(Transformation.class, (Object)new TransformationHelper.Deserializer()).create();

    public BlockModel deserialize(JsonElement element, Type targetType, JsonDeserializationContext deserializationContext) throws JsonParseException {
        String renderTypeHintName;
        BlockModel model = super.deserialize(element, targetType, deserializationContext);
        JsonObject jsonobject = element.getAsJsonObject();
        IUnbakedGeometry<?> geometry = ExtendedBlockModelDeserializer.deserializeGeometry(deserializationContext, jsonobject);
        List elements = model.m_111436_();
        if (geometry != null) {
            elements.clear();
            model.customData.setCustomGeometry(geometry);
        }
        if (jsonobject.has("transform")) {
            JsonElement transform = jsonobject.get("transform");
            model.customData.setRootTransform((Transformation)deserializationContext.deserialize(transform, Transformation.class));
        }
        if (jsonobject.has("render_type")) {
            renderTypeHintName = GsonHelper.m_13906_((JsonObject)jsonobject, (String)"render_type");
            model.customData.setRenderTypeHint(new ResourceLocation(renderTypeHintName));
        }
        if (jsonobject.has("render_type_fast")) {
            renderTypeHintName = GsonHelper.m_13906_((JsonObject)jsonobject, (String)"render_type_fast");
            model.customData.setRenderTypeFastHint(new ResourceLocation(renderTypeHintName));
        }
        if (jsonobject.has("visibility")) {
            JsonObject visibility = GsonHelper.m_13930_((JsonObject)jsonobject, (String)"visibility");
            for (Map.Entry part : visibility.entrySet()) {
                model.customData.visibilityData.setVisibilityState((String)part.getKey(), ((JsonElement)part.getValue()).getAsBoolean());
            }
        }
        return model;
    }

    @Nullable
    public static IUnbakedGeometry<?> deserializeGeometry(JsonDeserializationContext deserializationContext, JsonObject object) throws JsonParseException {
        if (!object.has("loader")) {
            return null;
        }
        ResourceLocation name = new ResourceLocation(GsonHelper.m_13906_((JsonObject)object, (String)"loader"));
        IGeometryLoader<?> loader = GeometryLoaderManager.get(name);
        if (loader == null) {
            throw new JsonParseException(String.format(Locale.ENGLISH, "Model loader '%s' not found. Registered loaders: %s", name, GeometryLoaderManager.getLoaderList()));
        }
        return loader.read(object, deserializationContext);
    }
}

