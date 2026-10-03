/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.gson.Gson
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.mojang.math.Transformation
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  net.minecraft.client.renderer.block.model.BlockElement
 *  net.minecraft.client.renderer.block.model.BlockElementFace
 *  net.minecraft.client.renderer.block.model.BlockElementRotation
 *  net.minecraft.client.renderer.block.model.BlockFaceUV
 *  net.minecraft.client.renderer.block.model.BlockModel$GuiLight
 *  net.minecraft.client.renderer.block.model.ItemTransform
 *  net.minecraft.client.renderer.block.model.ItemTransform$Deserializer
 *  net.minecraft.client.renderer.texture.MissingTextureAtlasSprite
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.Mth
 *  net.minecraft.world.item.ItemDisplayContext
 *  net.minecraftforge.client.model.generators.ModelBuilder$ElementBuilder.FaceBuilder
 *  net.minecraftforge.client.model.generators.ModelBuilder$ElementBuilder.RotationBuilder
 *  net.minecraftforge.client.model.generators.ModelBuilder$TransformsBuilder.TransformVecBuilder
 *  org.jetbrains.annotations.Nullable
 *  org.jetbrains.annotations.VisibleForTesting
 *  org.joml.Quaternionf
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package net.minecraftforge.client.model.generators;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.math.Transformation;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.BlockElementRotation;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraftforge.client.model.ForgeFaceData;
import net.minecraftforge.client.model.generators.CustomLoaderBuilder;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.util.TransformationHelper;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class ModelBuilder<T extends ModelBuilder<T>>
extends ModelFile {
    @Nullable
    protected ModelFile parent;
    protected final Map<String, String> textures = new LinkedHashMap<String, String>();
    protected final TransformsBuilder transforms = new TransformsBuilder();
    protected final ExistingFileHelper existingFileHelper;
    protected String renderType = null;
    protected String renderTypeFast = null;
    protected boolean ambientOcclusion = true;
    protected BlockModel.GuiLight guiLight = null;
    protected final List<ElementBuilder> elements = new ArrayList<ElementBuilder>();
    protected CustomLoaderBuilder<T> customLoader = null;
    private final RootTransformsBuilder rootTransforms = new RootTransformsBuilder();

    protected ModelBuilder(ResourceLocation outputLocation, ExistingFileHelper existingFileHelper) {
        super(outputLocation);
        this.existingFileHelper = existingFileHelper;
    }

    private T self() {
        return (T)this;
    }

    @Override
    protected boolean exists() {
        return true;
    }

    public T parent(ModelFile parent) {
        Preconditions.checkNotNull((Object)parent, (Object)"Parent must not be null");
        parent.assertExistence();
        this.parent = parent;
        return this.self();
    }

    public T texture(String key, String texture) {
        Preconditions.checkNotNull((Object)key, (Object)"Key must not be null");
        Preconditions.checkNotNull((Object)texture, (Object)"Texture must not be null");
        if (texture.charAt(0) == '#') {
            this.textures.put(key, texture);
            return this.self();
        }
        ResourceLocation asLoc = texture.contains(":") ? new ResourceLocation(texture) : new ResourceLocation(this.getLocation().m_135827_(), texture);
        return this.texture(key, asLoc);
    }

    public T texture(String key, ResourceLocation texture) {
        Preconditions.checkNotNull((Object)key, (Object)"Key must not be null");
        Preconditions.checkNotNull((Object)texture, (Object)"Texture must not be null");
        Preconditions.checkArgument((boolean)this.existingFileHelper.exists(texture, ModelProvider.TEXTURE), (String)"Texture %s does not exist in any known resource pack", (Object)texture);
        this.textures.put(key, texture.toString());
        return this.self();
    }

    public T renderType(String renderType) {
        Preconditions.checkNotNull((Object)renderType, (Object)"Render type must not be null");
        return this.renderType(new ResourceLocation(renderType));
    }

    public T renderType(String renderType, String renderTypeFast) {
        Preconditions.checkNotNull((Object)renderType, (Object)"Render type must not be null");
        Preconditions.checkNotNull((Object)renderTypeFast, (Object)"Render type for fast graphics must not be null");
        return this.renderType(new ResourceLocation(renderType), new ResourceLocation(renderTypeFast));
    }

    public T renderType(ResourceLocation renderType) {
        Preconditions.checkNotNull((Object)renderType, (Object)"Render type must not be null");
        this.renderType = renderType.toString();
        this.renderTypeFast = null;
        return this.self();
    }

    public T renderType(ResourceLocation renderType, ResourceLocation renderTypeFast) {
        Preconditions.checkNotNull((Object)renderType, (Object)"Render type must not be null");
        Preconditions.checkNotNull((Object)renderTypeFast, (Object)"Render type for fast graphics must not be null");
        this.renderType = renderType.toString();
        this.renderTypeFast = renderTypeFast.toString();
        return this.self();
    }

    public TransformsBuilder transforms() {
        return this.transforms;
    }

    public T ao(boolean ao) {
        this.ambientOcclusion = ao;
        return this.self();
    }

    public T guiLight(BlockModel.GuiLight light) {
        this.guiLight = light;
        return this.self();
    }

    public ElementBuilder element() {
        Preconditions.checkState((this.customLoader == null ? 1 : 0) != 0, (Object)"Cannot use elements and custom loaders at the same time");
        ElementBuilder ret = new ElementBuilder();
        this.elements.add(ret);
        return ret;
    }

    public ElementBuilder element(int index) {
        Preconditions.checkState((this.customLoader == null ? 1 : 0) != 0, (Object)"Cannot use elements and custom loaders at the same time");
        Preconditions.checkElementIndex((int)index, (int)this.elements.size(), (String)"Element index");
        return this.elements.get(index);
    }

    public int getElementCount() {
        return this.elements.size();
    }

    public <L extends CustomLoaderBuilder<T>> L customLoader(BiFunction<T, ExistingFileHelper, L> customLoaderFactory) {
        CustomLoaderBuilder customLoader;
        Preconditions.checkState((this.elements.size() == 0 ? 1 : 0) != 0, (Object)"Cannot use elements and custom loaders at the same time");
        Preconditions.checkNotNull(customLoaderFactory, (Object)"customLoaderFactory must not be null");
        this.customLoader = customLoader = (CustomLoaderBuilder)customLoaderFactory.apply(this.self(), this.existingFileHelper);
        return (L)customLoader;
    }

    public RootTransformsBuilder rootTransforms() {
        return this.rootTransforms;
    }

    @VisibleForTesting
    public JsonObject toJson() {
        JsonObject transform;
        Map<ItemDisplayContext, ItemTransform> transforms;
        JsonObject root = new JsonObject();
        if (this.parent != null) {
            root.addProperty("parent", this.parent.getLocation().toString());
        }
        if (!this.ambientOcclusion) {
            root.addProperty("ambientocclusion", Boolean.valueOf(this.ambientOcclusion));
        }
        if (this.guiLight != null) {
            root.addProperty("gui_light", this.guiLight.getSerializedName());
        }
        if (this.renderType != null) {
            root.addProperty("render_type", this.renderType);
        }
        if (this.renderTypeFast != null) {
            root.addProperty("render_type_fast", this.renderTypeFast);
        }
        if (!(transforms = this.transforms.build()).isEmpty()) {
            JsonObject display = new JsonObject();
            for (Map.Entry<ItemDisplayContext, ItemTransform> entry : transforms.entrySet()) {
                boolean hasRightRotation;
                JsonObject transform2 = new JsonObject();
                ItemTransform vec = entry.getValue();
                if (vec.equals((Object)ItemTransform.f_111754_)) continue;
                boolean bl = hasRightRotation = !vec.rightRotation.equals((Object)ItemTransform.Deserializer.f_111769_);
                if (!vec.f_111756_.equals((Object)ItemTransform.Deserializer.f_111770_)) {
                    transform2.add("translation", (JsonElement)this.serializeVector3f(entry.getValue().f_111756_));
                }
                if (!vec.f_111755_.equals((Object)ItemTransform.Deserializer.f_111769_)) {
                    transform2.add(hasRightRotation ? "left_rotation" : "rotation", (JsonElement)this.serializeVector3f(vec.f_111755_));
                }
                if (!vec.f_111757_.equals((Object)ItemTransform.Deserializer.f_111771_)) {
                    transform2.add("scale", (JsonElement)this.serializeVector3f(entry.getValue().f_111757_));
                }
                if (hasRightRotation) {
                    transform2.add("right_rotation", (JsonElement)this.serializeVector3f(vec.rightRotation));
                }
                display.add(entry.getKey().m_7912_(), (JsonElement)transform2);
            }
            root.add("display", (JsonElement)display);
        }
        if (!this.textures.isEmpty()) {
            JsonObject textures = new JsonObject();
            for (Map.Entry<Object, Object> entry : this.textures.entrySet()) {
                textures.addProperty((String)entry.getKey(), this.serializeLocOrKey((String)entry.getValue()));
            }
            root.add("textures", (JsonElement)textures);
        }
        if (!this.elements.isEmpty()) {
            JsonArray elements = new JsonArray();
            this.elements.stream().map(ElementBuilder::build).forEach(part -> {
                JsonObject partObj = new JsonObject();
                partObj.add("from", (JsonElement)this.serializeVector3f(part.f_111308_));
                partObj.add("to", (JsonElement)this.serializeVector3f(part.f_111309_));
                if (part.f_111311_ != null) {
                    JsonObject rotation = new JsonObject();
                    rotation.add("origin", (JsonElement)this.serializeVector3f(part.f_111311_.f_111378_()));
                    rotation.addProperty("axis", part.f_111311_.f_111379_().m_7912_());
                    rotation.addProperty("angle", (Number)Float.valueOf(part.f_111311_.f_111380_()));
                    if (part.f_111311_.f_111381_()) {
                        rotation.addProperty("rescale", Boolean.valueOf(part.f_111311_.f_111381_()));
                    }
                    partObj.add("rotation", (JsonElement)rotation);
                }
                if (!part.f_111312_) {
                    partObj.addProperty("shade", Boolean.valueOf(part.f_111312_));
                }
                JsonObject faces = new JsonObject();
                for (Direction dir : Direction.values()) {
                    BlockElementFace face = (BlockElementFace)part.f_111310_.get(dir);
                    if (face == null) continue;
                    JsonObject faceObj = new JsonObject();
                    faceObj.addProperty("texture", this.serializeLocOrKey(face.f_111356_));
                    if (!Arrays.equals(face.f_111357_.f_111387_, part.m_111320_(dir))) {
                        faceObj.add("uv", new Gson().toJsonTree((Object)face.f_111357_.f_111387_));
                    }
                    if (face.f_111354_ != null) {
                        faceObj.addProperty("cullface", face.f_111354_.m_7912_());
                    }
                    if (face.f_111357_.f_111388_ != 0) {
                        faceObj.addProperty("rotation", (Number)face.f_111357_.f_111388_);
                    }
                    if (face.f_111355_ != -1) {
                        faceObj.addProperty("tintindex", (Number)face.f_111355_);
                    }
                    if (!face.getFaceData().equals(ForgeFaceData.DEFAULT)) {
                        faceObj.add("forge_data", (JsonElement)ForgeFaceData.CODEC.encodeStart((DynamicOps)JsonOps.INSTANCE, (Object)face.getFaceData()).result().get());
                    }
                    faces.add(dir.m_7912_(), (JsonElement)faceObj);
                }
                if (!part.f_111310_.isEmpty()) {
                    partObj.add("faces", (JsonElement)faces);
                }
                elements.add((JsonElement)partObj);
            });
            root.add("elements", (JsonElement)elements);
        }
        if ((transform = this.rootTransforms.toJson()).size() > 0) {
            root.add("transform", (JsonElement)transform);
        }
        if (this.customLoader != null) {
            return this.customLoader.toJson(root);
        }
        return root;
    }

    private String serializeLocOrKey(String tex) {
        if (tex.charAt(0) == '#') {
            return tex;
        }
        return new ResourceLocation(tex).toString();
    }

    private JsonArray serializeVector3f(Vector3f vec) {
        JsonArray ret = new JsonArray();
        ret.add(this.serializeFloat(vec.x()));
        ret.add(this.serializeFloat(vec.y()));
        ret.add(this.serializeFloat(vec.z()));
        return ret;
    }

    private Number serializeFloat(float f) {
        if ((float)((int)f) == f) {
            return (int)f;
        }
        return Float.valueOf(f);
    }

    public class TransformsBuilder {
        private final Map<ItemDisplayContext, net.minecraftforge.client.model.generators.ModelBuilder$TransformsBuilder.TransformVecBuilder> transforms = new LinkedHashMap<ItemDisplayContext, net.minecraftforge.client.model.generators.ModelBuilder$TransformsBuilder.TransformVecBuilder>();

        public net.minecraftforge.client.model.generators.ModelBuilder$TransformsBuilder.TransformVecBuilder transform(ItemDisplayContext type) {
            Preconditions.checkNotNull((Object)type, (Object)"Perspective cannot be null");
            return this.transforms.computeIfAbsent(type, x$0 -> new TransformVecBuilder((ItemDisplayContext)x$0));
        }

        Map<ItemDisplayContext, ItemTransform> build() {
            return this.transforms.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> ((TransformVecBuilder)e.getValue()).build(), (k1, k2) -> {
                throw new IllegalArgumentException();
            }, LinkedHashMap::new));
        }

        public T end() {
            return ModelBuilder.this.self();
        }

        public class TransformVecBuilder {
            private Vector3f rotation = new Vector3f((Vector3fc)ItemTransform.Deserializer.f_111769_);
            private Vector3f translation = new Vector3f((Vector3fc)ItemTransform.Deserializer.f_111770_);
            private Vector3f scale = new Vector3f((Vector3fc)ItemTransform.Deserializer.f_111771_);
            private Vector3f rightRotation = new Vector3f((Vector3fc)ItemTransform.Deserializer.f_111769_);

            TransformVecBuilder(ItemDisplayContext type) {
            }

            public net.minecraftforge.client.model.generators.ModelBuilder$TransformsBuilder.TransformVecBuilder rotation(float x, float y, float z) {
                this.rotation = new Vector3f(x, y, z);
                return this;
            }

            public net.minecraftforge.client.model.generators.ModelBuilder$TransformsBuilder.TransformVecBuilder leftRotation(float x, float y, float z) {
                return this.rotation(x, y, z);
            }

            public net.minecraftforge.client.model.generators.ModelBuilder$TransformsBuilder.TransformVecBuilder translation(float x, float y, float z) {
                this.translation = new Vector3f(x, y, z);
                return this;
            }

            public net.minecraftforge.client.model.generators.ModelBuilder$TransformsBuilder.TransformVecBuilder scale(float sc) {
                return this.scale(sc, sc, sc);
            }

            public net.minecraftforge.client.model.generators.ModelBuilder$TransformsBuilder.TransformVecBuilder scale(float x, float y, float z) {
                this.scale = new Vector3f(x, y, z);
                return this;
            }

            public net.minecraftforge.client.model.generators.ModelBuilder$TransformsBuilder.TransformVecBuilder rightRotation(float x, float y, float z) {
                this.rightRotation = new Vector3f(x, y, z);
                return this;
            }

            ItemTransform build() {
                return new ItemTransform(this.rotation, this.translation, this.scale, this.rightRotation);
            }

            public TransformsBuilder end() {
                return TransformsBuilder.this;
            }
        }
    }

    public class RootTransformsBuilder {
        private static final Vector3f ONE = new Vector3f(1.0f, 1.0f, 1.0f);
        private Vector3f translation = new Vector3f();
        private Quaternionf leftRotation = new Quaternionf();
        private Quaternionf rightRotation = new Quaternionf();
        private Vector3f scale = ONE;
        @Nullable
        private TransformationHelper.TransformOrigin origin;
        @Nullable
        private Vector3f originVec;

        RootTransformsBuilder() {
        }

        public RootTransformsBuilder translation(Vector3f translation) {
            this.translation = (Vector3f)Preconditions.checkNotNull((Object)translation, (Object)"Translation must not be null");
            return this;
        }

        public RootTransformsBuilder translation(float x, float y, float z) {
            return this.translation(new Vector3f(x, y, z));
        }

        public RootTransformsBuilder rotation(Quaternionf rotation) {
            this.leftRotation = (Quaternionf)Preconditions.checkNotNull((Object)rotation, (Object)"Rotation must not be null");
            return this;
        }

        public RootTransformsBuilder rotation(float x, float y, float z, boolean isDegrees) {
            return this.rotation(TransformationHelper.quatFromXYZ(x, y, z, isDegrees));
        }

        public RootTransformsBuilder leftRotation(Quaternionf leftRotation) {
            return this.rotation(leftRotation);
        }

        public RootTransformsBuilder leftRotation(float x, float y, float z, boolean isDegrees) {
            return this.leftRotation(TransformationHelper.quatFromXYZ(x, y, z, isDegrees));
        }

        public RootTransformsBuilder rightRotation(Quaternionf rightRotation) {
            this.rightRotation = (Quaternionf)Preconditions.checkNotNull((Object)rightRotation, (Object)"Rotation must not be null");
            return this;
        }

        public RootTransformsBuilder rightRotation(float x, float y, float z, boolean isDegrees) {
            return this.rightRotation(TransformationHelper.quatFromXYZ(x, y, z, isDegrees));
        }

        public RootTransformsBuilder postRotation(Quaternionf postRotation) {
            return this.rightRotation(postRotation);
        }

        public RootTransformsBuilder postRotation(float x, float y, float z, boolean isDegrees) {
            return this.postRotation(TransformationHelper.quatFromXYZ(x, y, z, isDegrees));
        }

        public RootTransformsBuilder scale(float scale) {
            return this.scale(new Vector3f(scale, scale, scale));
        }

        public RootTransformsBuilder scale(float xScale, float yScale, float zScale) {
            return this.scale(new Vector3f(xScale, yScale, zScale));
        }

        public RootTransformsBuilder scale(Vector3f scale) {
            this.scale = (Vector3f)Preconditions.checkNotNull((Object)scale, (Object)"Scale must not be null");
            return this;
        }

        public RootTransformsBuilder transform(Transformation transformation) {
            Preconditions.checkNotNull((Object)transformation, (Object)"Transformation must not be null");
            this.translation = transformation.m_252829_();
            this.leftRotation = transformation.m_253244_();
            this.rightRotation = transformation.m_252848_();
            this.scale = transformation.m_252900_();
            return this;
        }

        public RootTransformsBuilder origin(Vector3f origin) {
            this.originVec = (Vector3f)Preconditions.checkNotNull((Object)origin, (Object)"Origin must not be null");
            this.origin = null;
            return this;
        }

        public RootTransformsBuilder origin(TransformationHelper.TransformOrigin origin) {
            this.origin = (TransformationHelper.TransformOrigin)((Object)Preconditions.checkNotNull((Object)((Object)origin), (Object)"Origin must not be null"));
            this.originVec = null;
            return this;
        }

        public ModelBuilder<T> end() {
            return ModelBuilder.this;
        }

        public JsonObject toJson() {
            JsonObject transform = new JsonObject();
            if (!this.translation.equals(0.0f, 0.0f, 0.0f)) {
                transform.add("translation", (JsonElement)RootTransformsBuilder.writeVec3(this.translation));
            }
            if (!this.scale.equals((Object)ONE)) {
                transform.add("scale", (JsonElement)RootTransformsBuilder.writeVec3(this.scale));
            }
            if (!this.leftRotation.equals(0.0f, 0.0f, 0.0f, 1.0f)) {
                transform.add("rotation", (JsonElement)RootTransformsBuilder.writeQuaternion(this.leftRotation));
            }
            if (!this.rightRotation.equals(0.0f, 0.0f, 0.0f, 1.0f)) {
                transform.add("post_rotation", (JsonElement)RootTransformsBuilder.writeQuaternion(this.rightRotation));
            }
            if (this.origin != null) {
                transform.addProperty("origin", this.origin.m_7912_());
            } else if (this.originVec != null && !this.originVec.equals(0.0f, 0.0f, 0.0f)) {
                transform.add("origin", (JsonElement)RootTransformsBuilder.writeVec3(this.originVec));
            }
            return transform;
        }

        private static JsonArray writeVec3(Vector3f vector) {
            JsonArray array = new JsonArray();
            array.add((Number)Float.valueOf(vector.x()));
            array.add((Number)Float.valueOf(vector.y()));
            array.add((Number)Float.valueOf(vector.z()));
            return array;
        }

        private static JsonArray writeQuaternion(Quaternionf quaternion) {
            JsonArray array = new JsonArray();
            array.add((Number)Float.valueOf(quaternion.x()));
            array.add((Number)Float.valueOf(quaternion.y()));
            array.add((Number)Float.valueOf(quaternion.z()));
            array.add((Number)Float.valueOf(quaternion.w()));
            return array;
        }
    }

    public class ElementBuilder {
        private Vector3f from = new Vector3f();
        private Vector3f to = new Vector3f(16.0f, 16.0f, 16.0f);
        private final Map<Direction, net.minecraftforge.client.model.generators.ModelBuilder$ElementBuilder.FaceBuilder> faces = new LinkedHashMap<Direction, net.minecraftforge.client.model.generators.ModelBuilder$ElementBuilder.FaceBuilder>();
        private net.minecraftforge.client.model.generators.ModelBuilder$ElementBuilder.RotationBuilder rotation;
        private boolean shade = true;
        private int color = -1;
        private int blockLight = 0;
        private int skyLight = 0;
        private boolean hasAmbientOcclusion = true;
        private boolean calculateNormals = false;

        private void validateCoordinate(float coord, char name) {
            Preconditions.checkArgument((!(coord < -16.0f) && !(coord > 32.0f) ? 1 : 0) != 0, (String)("Position " + name + " out of range, must be within [-16, 32]. Found: %d"), (Object)Float.valueOf(coord));
        }

        private void validatePosition(Vector3f pos) {
            this.validateCoordinate(pos.x(), 'x');
            this.validateCoordinate(pos.y(), 'y');
            this.validateCoordinate(pos.z(), 'z');
        }

        public ElementBuilder from(float x, float y, float z) {
            this.from = new Vector3f(x, y, z);
            this.validatePosition(this.from);
            return this;
        }

        public ElementBuilder to(float x, float y, float z) {
            this.to = new Vector3f(x, y, z);
            this.validatePosition(this.to);
            return this;
        }

        public net.minecraftforge.client.model.generators.ModelBuilder$ElementBuilder.FaceBuilder face(Direction dir) {
            Preconditions.checkNotNull((Object)dir, (Object)"Direction must not be null");
            return this.faces.computeIfAbsent(dir, x$0 -> new FaceBuilder((Direction)x$0));
        }

        public net.minecraftforge.client.model.generators.ModelBuilder$ElementBuilder.RotationBuilder rotation() {
            if (this.rotation == null) {
                this.rotation = new RotationBuilder();
            }
            return this.rotation;
        }

        public ElementBuilder shade(boolean shade) {
            this.shade = shade;
            return this;
        }

        public ElementBuilder allFaces(BiConsumer<Direction, net.minecraftforge.client.model.generators.ModelBuilder$ElementBuilder.FaceBuilder> action) {
            Arrays.stream(Direction.values()).forEach(d -> action.accept((Direction)d, (net.minecraftforge.client.model.generators.ModelBuilder$ElementBuilder.FaceBuilder)this.face((Direction)d)));
            return this;
        }

        public ElementBuilder faces(BiConsumer<Direction, net.minecraftforge.client.model.generators.ModelBuilder$ElementBuilder.FaceBuilder> action) {
            this.faces.entrySet().stream().forEach(e -> action.accept((Direction)e.getKey(), (net.minecraftforge.client.model.generators.ModelBuilder$ElementBuilder.FaceBuilder)((FaceBuilder)e.getValue())));
            return this;
        }

        public ElementBuilder textureAll(String texture) {
            return this.allFaces(this.addTexture(texture));
        }

        public ElementBuilder texture(String texture) {
            return this.faces(this.addTexture(texture));
        }

        public ElementBuilder cube(String texture) {
            return this.allFaces(this.addTexture(texture).andThen((dir, f) -> f.cullface(dir)));
        }

        public ElementBuilder emissivity(int blockLight, int skyLight) {
            this.blockLight = blockLight;
            this.skyLight = skyLight;
            return this;
        }

        public ElementBuilder color(int color) {
            this.color = color;
            return this;
        }

        public ElementBuilder ao(boolean ao) {
            this.hasAmbientOcclusion = ao;
            return this;
        }

        public ElementBuilder calculateNormals(boolean calc) {
            this.calculateNormals = calc;
            return this;
        }

        private BiConsumer<Direction, net.minecraftforge.client.model.generators.ModelBuilder$ElementBuilder.FaceBuilder> addTexture(String texture) {
            return ($, f) -> f.texture(texture);
        }

        BlockElement build() {
            Map faces = this.faces.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> ((FaceBuilder)e.getValue()).build(), (k1, k2) -> {
                throw new IllegalArgumentException();
            }, LinkedHashMap::new));
            return new BlockElement(this.from, this.to, faces, this.rotation == null ? null : this.rotation.build(), this.shade, new ForgeFaceData(this.color, this.blockLight, this.skyLight, this.hasAmbientOcclusion, this.calculateNormals));
        }

        public T end() {
            return ModelBuilder.this.self();
        }

        public class FaceBuilder {
            private Direction cullface;
            private int tintindex = -1;
            private String texture = MissingTextureAtlasSprite.m_118071_().toString();
            private float[] uvs;
            private FaceRotation rotation = FaceRotation.ZERO;
            private int color = -1;
            private int blockLight = 0;
            private int skyLight = 0;
            private boolean hasAmbientOcclusion = true;
            private boolean calculateNormals = false;

            FaceBuilder(Direction dir) {
            }

            public net.minecraftforge.client.model.generators.ModelBuilder$ElementBuilder.FaceBuilder cullface(@Nullable Direction dir) {
                this.cullface = dir;
                return this;
            }

            public net.minecraftforge.client.model.generators.ModelBuilder$ElementBuilder.FaceBuilder tintindex(int index) {
                this.tintindex = index;
                return this;
            }

            public net.minecraftforge.client.model.generators.ModelBuilder$ElementBuilder.FaceBuilder texture(String texture) {
                Preconditions.checkNotNull((Object)texture, (Object)"Texture must not be null");
                this.texture = texture;
                return this;
            }

            public net.minecraftforge.client.model.generators.ModelBuilder$ElementBuilder.FaceBuilder uvs(float u1, float v1, float u2, float v2) {
                this.uvs = new float[]{u1, v1, u2, v2};
                return this;
            }

            public net.minecraftforge.client.model.generators.ModelBuilder$ElementBuilder.FaceBuilder rotation(FaceRotation rot) {
                Preconditions.checkNotNull((Object)((Object)rot), (Object)"Rotation must not be null");
                this.rotation = rot;
                return this;
            }

            public net.minecraftforge.client.model.generators.ModelBuilder$ElementBuilder.FaceBuilder emissivity(int blockLight, int skyLight) {
                this.blockLight = blockLight;
                this.skyLight = skyLight;
                return this;
            }

            public net.minecraftforge.client.model.generators.ModelBuilder$ElementBuilder.FaceBuilder color(int color) {
                this.color = color;
                return this;
            }

            public net.minecraftforge.client.model.generators.ModelBuilder$ElementBuilder.FaceBuilder ao(boolean ao) {
                this.hasAmbientOcclusion = ao;
                return this;
            }

            public net.minecraftforge.client.model.generators.ModelBuilder$ElementBuilder.FaceBuilder calculateNormals(boolean calc) {
                this.calculateNormals = calc;
                return this;
            }

            BlockElementFace build() {
                if (this.texture == null) {
                    throw new IllegalStateException("A model face must have a texture");
                }
                return new BlockElementFace(this.cullface, this.tintindex, this.texture, new BlockFaceUV(this.uvs, this.rotation.rotation), new ForgeFaceData(this.color, this.blockLight, this.skyLight, this.hasAmbientOcclusion, this.calculateNormals));
            }

            public ElementBuilder end() {
                return ElementBuilder.this;
            }
        }

        public class RotationBuilder {
            private Vector3f origin;
            private Direction.Axis axis;
            private float angle;
            private boolean rescale;

            public net.minecraftforge.client.model.generators.ModelBuilder$ElementBuilder.RotationBuilder origin(float x, float y, float z) {
                this.origin = new Vector3f(x, y, z);
                return this;
            }

            public net.minecraftforge.client.model.generators.ModelBuilder$ElementBuilder.RotationBuilder axis(Direction.Axis axis) {
                Preconditions.checkNotNull((Object)axis, (Object)"Axis must not be null");
                this.axis = axis;
                return this;
            }

            public net.minecraftforge.client.model.generators.ModelBuilder$ElementBuilder.RotationBuilder angle(float angle) {
                Preconditions.checkArgument((angle == 0.0f || Mth.m_14154_((float)angle) == 22.5f || Mth.m_14154_((float)angle) == 45.0f ? 1 : 0) != 0, (String)"Invalid rotation %f found, only -45/-22.5/0/22.5/45 allowed", (Object)Float.valueOf(angle));
                this.angle = angle;
                return this;
            }

            public net.minecraftforge.client.model.generators.ModelBuilder$ElementBuilder.RotationBuilder rescale(boolean rescale) {
                this.rescale = rescale;
                return this;
            }

            BlockElementRotation build() {
                return new BlockElementRotation(this.origin, this.axis, this.angle, this.rescale);
            }

            public ElementBuilder end() {
                return ElementBuilder.this;
            }
        }
    }

    public static enum FaceRotation {
        ZERO(0),
        CLOCKWISE_90(90),
        UPSIDE_DOWN(180),
        COUNTERCLOCKWISE_90(270);

        final int rotation;

        private FaceRotation(int rotation) {
            this.rotation = rotation;
        }
    }
}

