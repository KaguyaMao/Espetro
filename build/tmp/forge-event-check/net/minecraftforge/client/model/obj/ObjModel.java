/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.math.Transformation
 *  joptsimple.internal.Strings
 *  net.minecraft.client.renderer.LightTexture
 *  net.minecraft.client.renderer.block.model.BakedQuad
 *  net.minecraft.client.renderer.texture.TextureAtlasSprite
 *  net.minecraft.client.resources.model.Material
 *  net.minecraft.client.resources.model.ModelBaker
 *  net.minecraft.client.resources.model.ModelState
 *  net.minecraft.client.resources.model.UnbakedModel
 *  net.minecraft.core.Direction
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.Mth
 *  net.minecraft.world.phys.Vec2
 *  org.apache.commons.lang3.tuple.Pair
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 *  org.joml.Vector4f
 */
package net.minecraftforge.client.model.obj;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.math.Transformation;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import joptsimple.internal.Strings;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;
import net.minecraftforge.client.model.IModelBuilder;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.SimpleUnbakedGeometry;
import net.minecraftforge.client.model.geometry.UnbakedGeometryHelper;
import net.minecraftforge.client.model.obj.ObjLoader;
import net.minecraftforge.client.model.obj.ObjMaterialLibrary;
import net.minecraftforge.client.model.obj.ObjTokenizer;
import net.minecraftforge.client.model.pipeline.QuadBakingVertexConsumer;
import net.minecraftforge.client.model.renderable.CompositeRenderable;
import net.minecraftforge.client.textures.UnitTextureAtlasSprite;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;

public class ObjModel
extends SimpleUnbakedGeometry<ObjModel> {
    private static final Vector4f COLOR_WHITE = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
    private static final Vec2[] DEFAULT_COORDS = new Vec2[]{new Vec2(0.0f, 0.0f), new Vec2(0.0f, 1.0f), new Vec2(1.0f, 1.0f), new Vec2(1.0f, 0.0f)};
    private final Map<String, ModelGroup> parts = Maps.newLinkedHashMap();
    private final Set<String> rootComponentNames = Collections.unmodifiableSet(this.parts.keySet());
    private Set<String> allComponentNames;
    private final List<Vector3f> positions = Lists.newArrayList();
    private final List<Vec2> texCoords = Lists.newArrayList();
    private final List<Vector3f> normals = Lists.newArrayList();
    private final List<Vector4f> colors = Lists.newArrayList();
    public final boolean automaticCulling;
    public final boolean shadeQuads;
    public final boolean flipV;
    public final boolean emissiveAmbient;
    @Nullable
    public final String mtlOverride;
    public final ResourceLocation modelLocation;

    private ObjModel(ModelSettings settings) {
        this.modelLocation = settings.modelLocation;
        this.automaticCulling = settings.automaticCulling;
        this.shadeQuads = settings.shadeQuads;
        this.flipV = settings.flipV;
        this.emissiveAmbient = settings.emissiveAmbient;
        this.mtlOverride = settings.mtlOverride;
    }

    public static ObjModel parse(ObjTokenizer tokenizer, ModelSettings settings) throws IOException {
        String[] line;
        ResourceLocation modelLocation = settings.modelLocation;
        String materialLibraryOverrideLocation = settings.mtlOverride;
        ObjModel model = new ObjModel(settings);
        String modelDomain = modelLocation.m_135827_();
        String modelPath = modelLocation.m_135815_();
        int lastSlash = modelPath.lastIndexOf(47);
        modelPath = lastSlash >= 0 ? modelPath.substring(0, lastSlash + 1) : "";
        ObjMaterialLibrary mtllib = ObjMaterialLibrary.EMPTY;
        ObjMaterialLibrary.Material currentMat = null;
        String currentSmoothingGroup = null;
        ModelObject currentGroup = null;
        ModelObject currentObject = null;
        ModelMesh currentMesh = null;
        boolean objAboveGroup = false;
        if (materialLibraryOverrideLocation != null) {
            String lib = materialLibraryOverrideLocation;
            mtllib = lib.contains(":") ? ObjLoader.INSTANCE.loadMaterialLibrary(new ResourceLocation(lib)) : ObjLoader.INSTANCE.loadMaterialLibrary(new ResourceLocation(modelDomain, modelPath + lib));
        }
        while ((line = tokenizer.readAndSplitLine(true)) != null) {
            switch (line[0]) {
                case "mtllib": {
                    if (materialLibraryOverrideLocation != null) break;
                    String lib = line[1];
                    if (lib.contains(":")) {
                        mtllib = ObjLoader.INSTANCE.loadMaterialLibrary(new ResourceLocation(lib));
                        break;
                    }
                    mtllib = ObjLoader.INSTANCE.loadMaterialLibrary(new ResourceLocation(modelDomain, modelPath + lib));
                    break;
                }
                case "usemtl": {
                    String mat = Strings.join((String[])Arrays.copyOfRange(line, 1, line.length), (String)" ");
                    ObjMaterialLibrary.Material newMat = mtllib.getMaterial(mat);
                    if (Objects.equals(newMat, currentMat)) break;
                    currentMat = newMat;
                    if (currentMesh != null && currentMesh.mat == null && currentMesh.faces.size() == 0) {
                        currentMesh.mat = currentMat;
                        break;
                    }
                    currentMesh = null;
                    break;
                }
                case "v": {
                    model.positions.add(ObjModel.parseVector4To3(line));
                    break;
                }
                case "vt": {
                    model.texCoords.add(ObjModel.parseVector2(line));
                    break;
                }
                case "vn": {
                    model.normals.add(ObjModel.parseVector3(line));
                    break;
                }
                case "vc": {
                    model.colors.add(ObjModel.parseVector4(line));
                    break;
                }
                case "f": {
                    if (currentMesh == null) {
                        ObjModel objModel = model;
                        Objects.requireNonNull(objModel);
                        currentMesh = objModel.new ModelMesh(currentMat, currentSmoothingGroup);
                        if (currentObject != null) {
                            currentObject.meshes.add(currentMesh);
                        } else {
                            if (currentGroup == null) {
                                ObjModel objModel2 = model;
                                Objects.requireNonNull(objModel2);
                                currentGroup = objModel2.new ModelGroup("");
                                model.parts.put("", (ModelGroup)currentGroup);
                            }
                            ((ModelGroup)currentGroup).meshes.add(currentMesh);
                        }
                    }
                    int[][] vertices = new int[line.length - 1][];
                    for (int i = 0; i < vertices.length; ++i) {
                        String vertexData = line[i + 1];
                        String[] vertexParts = vertexData.split("/");
                        int[] vertex = Arrays.stream(vertexParts).mapToInt(num -> Strings.isNullOrEmpty((String)num) ? 0 : Integer.parseInt(num)).toArray();
                        vertex[0] = vertex[0] < 0 ? model.positions.size() + vertex[0] : vertex[0] - 1;
                        if (vertex.length > 1) {
                            vertex[1] = vertex[1] < 0 ? model.texCoords.size() + vertex[1] : vertex[1] - 1;
                            if (vertex.length > 2) {
                                vertex[2] = vertex[2] < 0 ? model.normals.size() + vertex[2] : vertex[2] - 1;
                                if (vertex.length > 3) {
                                    vertex[3] = vertex[3] < 0 ? model.colors.size() + vertex[3] : vertex[3] - 1;
                                }
                            }
                        }
                        vertices[i] = vertex;
                    }
                    currentMesh.faces.add(vertices);
                    break;
                }
                case "s": {
                    String smoothingGroup;
                    String string = smoothingGroup = "off".equals(line[1]) ? null : line[1];
                    if (Objects.equals(currentSmoothingGroup, smoothingGroup)) break;
                    currentSmoothingGroup = smoothingGroup;
                    if (currentMesh != null && currentMesh.smoothingGroup == null && currentMesh.faces.size() == 0) {
                        currentMesh.smoothingGroup = currentSmoothingGroup;
                        break;
                    }
                    currentMesh = null;
                    break;
                }
                case "g": {
                    String name = line[1];
                    if (objAboveGroup) {
                        ObjModel objModel = model;
                        Objects.requireNonNull(objModel);
                        currentObject = objModel.new ModelObject(currentGroup.name() + "/" + name);
                        ((ModelGroup)currentGroup).parts.put(name, currentObject);
                    } else {
                        ObjModel objModel = model;
                        Objects.requireNonNull(objModel);
                        currentGroup = objModel.new ModelGroup(name);
                        model.parts.put(name, (ModelGroup)currentGroup);
                        currentObject = null;
                    }
                    currentMesh = null;
                    break;
                }
                case "o": {
                    String name = line[1];
                    if (objAboveGroup || currentGroup == null) {
                        objAboveGroup = true;
                        ObjModel objModel = model;
                        Objects.requireNonNull(objModel);
                        currentGroup = objModel.new ModelGroup(name);
                        model.parts.put(name, (ModelGroup)currentGroup);
                        currentObject = null;
                    } else {
                        ObjModel objModel = model;
                        Objects.requireNonNull(objModel);
                        currentObject = objModel.new ModelObject(currentGroup.name() + "/" + name);
                        ((ModelGroup)currentGroup).parts.put(name, currentObject);
                    }
                    currentMesh = null;
                    break;
                }
            }
        }
        return model;
    }

    private static Vector3f parseVector4To3(String[] line) {
        Vector4f vec4 = ObjModel.parseVector4(line);
        return new Vector3f(vec4.x() / vec4.w(), vec4.y() / vec4.w(), vec4.z() / vec4.w());
    }

    private static Vec2 parseVector2(String[] line) {
        return switch (line.length) {
            case 1 -> new Vec2(0.0f, 0.0f);
            case 2 -> new Vec2(Float.parseFloat(line[1]), 0.0f);
            default -> new Vec2(Float.parseFloat(line[1]), Float.parseFloat(line[2]));
        };
    }

    private static Vector3f parseVector3(String[] line) {
        return switch (line.length) {
            case 1 -> new Vector3f();
            case 2 -> new Vector3f(Float.parseFloat(line[1]), 0.0f, 0.0f);
            case 3 -> new Vector3f(Float.parseFloat(line[1]), Float.parseFloat(line[2]), 0.0f);
            default -> new Vector3f(Float.parseFloat(line[1]), Float.parseFloat(line[2]), Float.parseFloat(line[3]));
        };
    }

    static Vector4f parseVector4(String[] line) {
        return switch (line.length) {
            case 1 -> new Vector4f();
            case 2 -> new Vector4f(Float.parseFloat(line[1]), 0.0f, 0.0f, 1.0f);
            case 3 -> new Vector4f(Float.parseFloat(line[1]), Float.parseFloat(line[2]), 0.0f, 1.0f);
            case 4 -> new Vector4f(Float.parseFloat(line[1]), Float.parseFloat(line[2]), Float.parseFloat(line[3]), 1.0f);
            default -> new Vector4f(Float.parseFloat(line[1]), Float.parseFloat(line[2]), Float.parseFloat(line[3]), Float.parseFloat(line[4]));
        };
    }

    @Override
    protected void addQuads(IGeometryBakingContext owner, IModelBuilder<?> modelBuilder, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ResourceLocation modelLocation) {
        this.parts.values().stream().filter(part -> owner.isComponentVisible(part.name(), true)).forEach(part -> part.addQuads(owner, modelBuilder, baker, spriteGetter, modelTransform, modelLocation));
    }

    public Set<String> getRootComponentNames() {
        return this.rootComponentNames;
    }

    @Override
    public Set<String> getConfigurableComponentNames() {
        if (this.allComponentNames != null) {
            return this.allComponentNames;
        }
        HashSet<String> names = new HashSet<String>();
        for (ModelGroup group : this.parts.values()) {
            group.addNamesRecursively(names);
        }
        this.allComponentNames = Collections.unmodifiableSet(names);
        return this.allComponentNames;
    }

    private Pair<BakedQuad, Direction> makeQuad(int[][] indices, int tintIndex, Vector4f colorTint, Vector4f ambientColor, TextureAtlasSprite texture, Transformation transform) {
        boolean needsNormalRecalculation = false;
        for (int[] ints : indices) {
            needsNormalRecalculation |= ints.length < 3;
        }
        Vector3f faceNormal = new Vector3f();
        if (needsNormalRecalculation) {
            Vector3f a = this.positions.get(indices[0][0]);
            Vector3f ab = this.positions.get(indices[1][0]);
            Vector3f ac = this.positions.get(indices[2][0]);
            Vector3f abs = new Vector3f((Vector3fc)ab);
            abs.sub((Vector3fc)a);
            Vector3f acs = new Vector3f((Vector3fc)ac);
            acs.sub((Vector3fc)a);
            abs.cross((Vector3fc)acs);
            abs.normalize();
            faceNormal = abs;
        }
        QuadBakingVertexConsumer.Buffered quadBaker = new QuadBakingVertexConsumer.Buffered();
        quadBaker.setSprite(texture);
        quadBaker.setTintIndex(tintIndex);
        int uv2 = 0;
        if (this.emissiveAmbient) {
            int fakeLight = (int)((ambientColor.x() + ambientColor.y() + ambientColor.z()) * 15.0f / 3.0f);
            uv2 = LightTexture.m_109885_((int)fakeLight, (int)fakeLight);
            quadBaker.setShade(fakeLight == 0 && this.shadeQuads);
        } else {
            quadBaker.setShade(this.shadeQuads);
        }
        boolean hasTransform = !transform.isIdentity();
        Transformation transformation = hasTransform ? transform.blockCenterToCorner() : transform;
        Vector4f[] pos = new Vector4f[4];
        Vector3f[] norm = new Vector3f[4];
        for (int i = 0; i < 4; ++i) {
            Vector4f color;
            Vector3f norm0;
            int[] index = indices[Math.min(i, indices.length - 1)];
            Vector4f position = new Vector4f((Vector3fc)this.positions.get(index[0]), 1.0f);
            Vec2 texCoord = index.length >= 2 && this.texCoords.size() > 0 ? this.texCoords.get(index[1]) : DEFAULT_COORDS[i];
            Vector3f normal = norm0 = !needsNormalRecalculation && index.length >= 3 && this.normals.size() > 0 ? this.normals.get(index[2]) : faceNormal;
            Vector4f vector4f = color = index.length >= 4 && this.colors.size() > 0 ? this.colors.get(index[3]) : COLOR_WHITE;
            if (hasTransform) {
                normal = new Vector3f((Vector3fc)norm0);
                transformation.transformPosition(position);
                transformation.transformNormal(normal);
            }
            Vector4f tintedColor = new Vector4f(color.x() * colorTint.x(), color.y() * colorTint.y(), color.z() * colorTint.z(), color.w() * colorTint.w());
            quadBaker.m_5483_(position.x(), position.y(), position.z());
            quadBaker.m_85950_(tintedColor.x(), tintedColor.y(), tintedColor.z(), tintedColor.w());
            quadBaker.m_7421_(texture.m_118367_((double)(texCoord.f_82470_ * 16.0f)), texture.m_118393_((double)((this.flipV ? 1.0f - texCoord.f_82471_ : texCoord.f_82471_) * 16.0f)));
            quadBaker.m_85969_(uv2);
            quadBaker.m_5601_(normal.x(), normal.y(), normal.z());
            if (i == 0) {
                quadBaker.setDirection(Direction.m_122372_((float)normal.x(), (float)normal.y(), (float)normal.z()));
            }
            quadBaker.m_5752_();
            pos[i] = position;
            norm[i] = normal;
        }
        Direction cull = null;
        if (this.automaticCulling) {
            if (Mth.m_14033_((float)pos[0].x(), (float)0.0f) && Mth.m_14033_((float)pos[1].x(), (float)0.0f) && Mth.m_14033_((float)pos[2].x(), (float)0.0f) && Mth.m_14033_((float)pos[3].x(), (float)0.0f) && norm[0].x() < 0.0f) {
                cull = Direction.WEST;
            } else if (Mth.m_14033_((float)pos[0].x(), (float)1.0f) && Mth.m_14033_((float)pos[1].x(), (float)1.0f) && Mth.m_14033_((float)pos[2].x(), (float)1.0f) && Mth.m_14033_((float)pos[3].x(), (float)1.0f) && norm[0].x() > 0.0f) {
                cull = Direction.EAST;
            } else if (Mth.m_14033_((float)pos[0].z(), (float)0.0f) && Mth.m_14033_((float)pos[1].z(), (float)0.0f) && Mth.m_14033_((float)pos[2].z(), (float)0.0f) && Mth.m_14033_((float)pos[3].z(), (float)0.0f) && norm[0].z() < 0.0f) {
                cull = Direction.NORTH;
            } else if (Mth.m_14033_((float)pos[0].z(), (float)1.0f) && Mth.m_14033_((float)pos[1].z(), (float)1.0f) && Mth.m_14033_((float)pos[2].z(), (float)1.0f) && Mth.m_14033_((float)pos[3].z(), (float)1.0f) && norm[0].z() > 0.0f) {
                cull = Direction.SOUTH;
            } else if (Mth.m_14033_((float)pos[0].y(), (float)0.0f) && Mth.m_14033_((float)pos[1].y(), (float)0.0f) && Mth.m_14033_((float)pos[2].y(), (float)0.0f) && Mth.m_14033_((float)pos[3].y(), (float)0.0f) && norm[0].y() < 0.0f) {
                cull = Direction.DOWN;
            } else if (Mth.m_14033_((float)pos[0].y(), (float)1.0f) && Mth.m_14033_((float)pos[1].y(), (float)1.0f) && Mth.m_14033_((float)pos[2].y(), (float)1.0f) && Mth.m_14033_((float)pos[3].y(), (float)1.0f) && norm[0].y() > 0.0f) {
                cull = Direction.UP;
            }
        }
        return Pair.of((Object)quadBaker.getQuad(), cull);
    }

    public CompositeRenderable bakeRenderable(IGeometryBakingContext configuration) {
        CompositeRenderable.Builder builder = CompositeRenderable.builder();
        for (Map.Entry<String, ModelGroup> entry : this.parts.entrySet()) {
            String name = entry.getKey();
            ModelGroup part = entry.getValue();
            part.bake(builder.child(name), configuration);
        }
        return builder.get();
    }

    public record ModelSettings(@NotNull ResourceLocation modelLocation, boolean automaticCulling, boolean shadeQuads, boolean flipV, boolean emissiveAmbient, @Nullable String mtlOverride) {
    }

    private class ModelMesh {
        @Nullable
        public ObjMaterialLibrary.Material mat;
        @Nullable
        public String smoothingGroup;
        public final List<int[][]> faces = Lists.newArrayList();

        public ModelMesh(@Nullable ObjMaterialLibrary.Material currentMat, String currentSmoothingGroup) {
            this.mat = currentMat;
            this.smoothingGroup = currentSmoothingGroup;
        }

        public void addQuads(IGeometryBakingContext owner, IModelBuilder<?> modelBuilder, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform) {
            if (this.mat == null) {
                return;
            }
            TextureAtlasSprite texture = spriteGetter.apply(UnbakedGeometryHelper.resolveDirtyMaterial(this.mat.diffuseColorMap, owner));
            int tintIndex = this.mat.diffuseTintIndex;
            Vector4f colorTint = this.mat.diffuseColor;
            Transformation rootTransform = owner.getRootTransform();
            Transformation transform = rootTransform.isIdentity() ? modelTransform.m_6189_() : modelTransform.m_6189_().m_121096_(rootTransform);
            for (int[][] face : this.faces) {
                Pair<BakedQuad, Direction> quad = ObjModel.this.makeQuad(face, tintIndex, colorTint, this.mat.ambientColor, texture, transform);
                if (quad.getRight() == null) {
                    modelBuilder.addUnculledFace((BakedQuad)quad.getLeft());
                    continue;
                }
                modelBuilder.addCulledFace((Direction)quad.getRight(), (BakedQuad)quad.getLeft());
            }
        }

        public void bake(CompositeRenderable.PartBuilder<?> builder, IGeometryBakingContext configuration) {
            ObjMaterialLibrary.Material mat = this.mat;
            if (mat == null) {
                return;
            }
            int tintIndex = mat.diffuseTintIndex;
            Vector4f colorTint = mat.diffuseColor;
            ArrayList<BakedQuad> quads = new ArrayList<BakedQuad>();
            for (int[][] face : this.faces) {
                Pair<BakedQuad, Direction> pair = ObjModel.this.makeQuad(face, tintIndex, colorTint, mat.ambientColor, UnitTextureAtlasSprite.INSTANCE, Transformation.m_121093_());
                quads.add((BakedQuad)pair.getLeft());
            }
            ResourceLocation textureLocation = UnbakedGeometryHelper.resolveDirtyMaterial(mat.diffuseColorMap, configuration).m_119203_();
            ResourceLocation texturePath = new ResourceLocation(textureLocation.m_135827_(), "textures/" + textureLocation.m_135815_() + ".png");
            builder.addMesh(texturePath, quads);
        }
    }

    public class ModelObject {
        public final String name;
        List<ModelMesh> meshes = Lists.newArrayList();

        ModelObject(String name) {
            this.name = name;
        }

        public String name() {
            return this.name;
        }

        public void addQuads(IGeometryBakingContext owner, IModelBuilder<?> modelBuilder, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ResourceLocation modelLocation) {
            for (ModelMesh mesh : this.meshes) {
                mesh.addQuads(owner, modelBuilder, spriteGetter, modelTransform);
            }
        }

        public void bake(CompositeRenderable.PartBuilder<?> builder, IGeometryBakingContext configuration) {
            for (ModelMesh mesh : this.meshes) {
                mesh.bake(builder, configuration);
            }
        }

        public Collection<Material> getTextures(IGeometryBakingContext owner, Function<ResourceLocation, UnbakedModel> modelGetter, Set<com.mojang.datafixers.util.Pair<String, String>> missingTextureErrors) {
            return this.meshes.stream().flatMap(mesh -> mesh.mat != null ? Stream.of(UnbakedGeometryHelper.resolveDirtyMaterial(mesh.mat.diffuseColorMap, owner)) : Stream.of(new Material[0])).collect(Collectors.toSet());
        }

        protected void addNamesRecursively(Set<String> names) {
            names.add(this.name());
        }
    }

    public class ModelGroup
    extends ModelObject {
        final Map<String, ModelObject> parts;

        ModelGroup(String name) {
            super(name);
            this.parts = Maps.newLinkedHashMap();
        }

        @Override
        public void addQuads(IGeometryBakingContext owner, IModelBuilder<?> modelBuilder, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ResourceLocation modelLocation) {
            super.addQuads(owner, modelBuilder, baker, spriteGetter, modelTransform, modelLocation);
            this.parts.values().stream().filter(part -> owner.isComponentVisible(part.name(), true)).forEach(part -> part.addQuads(owner, modelBuilder, baker, spriteGetter, modelTransform, modelLocation));
        }

        @Override
        public void bake(CompositeRenderable.PartBuilder<?> builder, IGeometryBakingContext configuration) {
            super.bake(builder, configuration);
            for (Map.Entry<String, ModelObject> entry : this.parts.entrySet()) {
                String name = entry.getKey();
                ModelObject part = entry.getValue();
                part.bake(builder.child(name), configuration);
            }
        }

        @Override
        public Collection<Material> getTextures(IGeometryBakingContext owner, Function<ResourceLocation, UnbakedModel> modelGetter, Set<com.mojang.datafixers.util.Pair<String, String>> missingTextureErrors) {
            HashSet combined = Sets.newHashSet();
            combined.addAll(super.getTextures(owner, modelGetter, missingTextureErrors));
            for (ModelObject part : this.parts.values()) {
                combined.addAll(part.getTextures(owner, modelGetter, missingTextureErrors));
            }
            return combined;
        }

        @Override
        protected void addNamesRecursively(Set<String> names) {
            super.addNamesRecursively(names);
            for (ModelObject object : this.parts.values()) {
                object.addNamesRecursively(names);
            }
        }
    }
}

