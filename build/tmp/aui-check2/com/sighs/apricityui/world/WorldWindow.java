/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.Minecraft
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.ClipContext
 *  net.minecraft.world.level.ClipContext$Block
 *  net.minecraft.world.level.ClipContext$Fluid
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult$Type
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Matrix3fc
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 *  org.joml.Vector4f
 */
package com.sighs.apricityui.world;

import com.mojang.blaze3d.vertex.PoseStack;
import com.sighs.apricityui.dev.resource.ResourcePreviewDialog;
import com.sighs.apricityui.init.Document;
import com.sighs.apricityui.layout.Position;
import com.sighs.apricityui.render.Base;
import com.sighs.apricityui.render.Mask;
import com.sighs.apricityui.render.WorldPaintDepth;
import com.sighs.apricityui.render.WorldWindowRenderContext;
import com.sighs.apricityui.spi.AuiServices;
import com.sighs.apricityui.world.WorldWindowDisplayPrecision;
import com.sighs.apricityui.world.WorldWindowVisibility;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3fc;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class WorldWindow {
    public static final List<WorldWindow> windows = new ArrayList<WorldWindow>();
    private static final float DEFAULT_NEAR_DEPTH_STEP = 3.5E-4f;
    private static final float DEFAULT_FAR_DEPTH_STEP = 0.003f;
    private static final float DEFAULT_DEPTH_NEAR_DISTANCE = 2.0f;
    private static final float DEFAULT_DEPTH_OFFSET_SCALE = 5.0E-4f;
    private static final float POLYGON_OFFSET = -1.0f;
    private static final float DEFAULT_VIEWPORT_FILL = 0.8f;
    private static final float FALLBACK_WORLD_SCALE = 0.02f;
    private static final double OCCLUSION_DISTANCE_EPSILON = 1.0E-4;
    private static final float DEFAULT_FOLLOW_FACTOR = 0.3f;
    private static final float ITEM_MODEL_DEPTH_FRACTION = 0.25f;
    private static final float ITEM_DECORATION_DEPTH_FRACTION = 0.5f;
    private static final float[] VIEWPORT_CLIP_RADIUS = new float[]{0.0f, 0.0f, 0.0f, 0.0f};
    public Document document;
    private Vec3 position;
    private final Quaternionf rotation;
    private Float scaleOverride;
    private float resolvedScale = 0.02f;
    private long resolvedViewportVersion = Long.MIN_VALUE;
    private boolean depthTest = true;
    private boolean followEnabled;
    private boolean facingEnabled;
    private float followFactor = 0.3f;
    private Float widthOverride;
    private Float heightOverride;
    private int maxDistance;
    private Integer maxDisplayDistanceOverride;
    private WorldWindowDisplayPrecision displayPrecision = WorldWindowDisplayPrecision.AUTO;
    private boolean displayPrecisionOverride;
    private Integer fullDetailDistanceOverride;
    private Integer reducedDetailDistanceOverride;
    private float nearDepthStep = 3.5E-4f;
    private float farDepthStep = 0.003f;
    private float depthNearDistance = 2.0f;
    private float depthFarDistance;
    private Matrix4f interactionClipMatrix;
    private Matrix4f interactionWorldMatrix;
    private Vec3 interactionPosition;

    public WorldWindow(String documentPath, Vec3 position, int maxDistance) {
        this.document = Document.createInWorld(documentPath);
        this.position = position;
        this.widthOverride = null;
        this.heightOverride = null;
        this.rotation = new Quaternionf().rotationY((float)Math.toRadians(180.0));
        this.scaleOverride = null;
        this.maxDistance = WorldWindow.sanitizeDistance(maxDistance);
        this.depthFarDistance = Math.max(3.0f, (float)this.maxDistance);
    }

    public WorldWindow(String documentPath, double x, double y, double z, int maxDistance) {
        this(documentPath, new Vec3(x, y, z), maxDistance);
    }

    public WorldWindow(String documentPath, Vec3 position, int maxDistance, float yaw, float pitch) {
        this(documentPath, position, maxDistance);
        this.setRotation(yaw, pitch);
    }

    public WorldWindow(String documentPath, Vec3 position, int maxDistance, float yaw, float pitch, float roll) {
        this(documentPath, position, maxDistance);
        this.setRotation(yaw, pitch, roll);
    }

    public WorldWindow(String documentPath, Vec3 position, int maxDistance, Vec3 eulerDegrees) {
        this(documentPath, position, maxDistance);
        this.setRotation(eulerDegrees);
    }

    public WorldWindow(String documentPath, Vec3 position, int maxDistance, Quaternionf orientation) {
        this(documentPath, position, maxDistance);
        this.setOrientation(orientation);
    }

    @Deprecated
    public WorldWindow(String documentPath, Vec3 position, float width, float height, int maxDistance) {
        this(documentPath, position, maxDistance);
        this.widthOverride = Float.valueOf(WorldWindow.sanitizeDimension(width));
        this.heightOverride = Float.valueOf(WorldWindow.sanitizeDimension(height));
        this.scaleOverride = Float.valueOf(0.02f);
    }

    public void setPosition(Vec3 position) {
        this.position = position;
        if (this.scaleOverride == null) {
            this.resolvedViewportVersion = Long.MIN_VALUE;
        }
    }

    protected Vec3 getPosition() {
        return this.position;
    }

    public void setRotation(float yRot, float xRot) {
        this.setRotation(yRot, xRot, 0.0f);
    }

    public void setRotation(float yaw, float pitch, float roll) {
        float safeYaw = Float.isFinite(yaw) ? yaw : 0.0f;
        float safePitch = Float.isFinite(pitch) ? pitch : 0.0f;
        float safeRoll = Float.isFinite(roll) ? roll : 0.0f;
        this.rotation.identity().rotateY((float)Math.toRadians(180.0f - safeYaw)).rotateX((float)Math.toRadians(safePitch)).rotateZ((float)Math.toRadians(safeRoll));
    }

    public void setRotation(Vec3 eulerDegrees) {
        if (eulerDegrees == null) {
            this.setRotation(0.0f, 0.0f, 0.0f);
            return;
        }
        this.setRotation((float)eulerDegrees.f_82480_, (float)eulerDegrees.f_82479_, (float)eulerDegrees.f_82481_);
    }

    public void setOrientation(Quaternionf orientation) {
        if (orientation == null) {
            this.setRotation(0.0f, 0.0f, 0.0f);
            return;
        }
        this.rotation.set((Quaternionfc)orientation);
    }

    public Quaternionf getOrientation() {
        return new Quaternionf((Quaternionfc)this.rotation);
    }

    public void setFollow(boolean follow) {
        if (this.followEnabled != follow) {
            this.resolvedViewportVersion = Long.MIN_VALUE;
        }
        this.followEnabled = follow;
    }

    public boolean isFollowEnabled() {
        return this.followEnabled;
    }

    public void setFollowEnabled(boolean follow) {
        this.setFollow(follow);
    }

    public boolean isFollow() {
        return this.isFollowEnabled();
    }

    public void setFacing(boolean facing) {
        this.facingEnabled = facing;
    }

    public boolean isFacingEnabled() {
        return this.facingEnabled;
    }

    public void setFacingEnabled(boolean facing) {
        this.setFacing(facing);
    }

    public boolean isFacing() {
        return this.isFacingEnabled();
    }

    public void setFollowFactor(float followFactor) {
        this.followFactor = WorldWindow.sanitizeFollowFactor(followFactor);
    }

    public float getFollowFactor() {
        return this.followFactor;
    }

    private Vec3 resolveRenderPosition(Vec3 cameraPosition, Vector3f lookVector) {
        if (this.position == null || !this.followEnabled || cameraPosition == null) {
            return this.position;
        }
        Vec3 look = new Vec3((double)lookVector.x, (double)lookVector.y, (double)lookVector.z);
        return WorldWindow.resolveFollowPosition(this.position, cameraPosition, look, this.followFactor);
    }

    private Quaternionf resolveRenderRotation(Vec3 cameraPosition, Vec3 renderPosition) {
        if (!this.facingEnabled || cameraPosition == null || renderPosition == null) {
            return new Quaternionf((Quaternionfc)this.rotation);
        }
        return this.faceCamera(cameraPosition, renderPosition);
    }

    private Quaternionf faceCamera(Vec3 cameraPosition, Vec3 windowPosition) {
        if (cameraPosition == null || windowPosition == null) {
            return new Quaternionf((Quaternionfc)this.rotation);
        }
        Vec3 toCamera = cameraPosition.m_82546_(windowPosition);
        double horizontal = Math.sqrt(toCamera.f_82479_ * toCamera.f_82479_ + toCamera.f_82481_ * toCamera.f_82481_);
        float yaw = (float)(Math.toDegrees(Math.atan2(toCamera.f_82481_, toCamera.f_82479_)) - 90.0);
        float pitch = (float)Math.toDegrees(Math.atan2(toCamera.f_82480_, horizontal));
        return new Quaternionf().rotateY((float)Math.toRadians(-yaw)).rotateX((float)Math.toRadians(-pitch));
    }

    static Vec3 resolveFollowPosition(Vec3 basePosition, Vec3 cameraPosition, Vec3 lookVector, float followFactor) {
        if (basePosition == null || cameraPosition == null || lookVector == null) {
            return basePosition;
        }
        double lookLength = Math.sqrt(lookVector.f_82479_ * lookVector.f_82479_ + lookVector.f_82480_ * lookVector.f_82480_ + lookVector.f_82481_ * lookVector.f_82481_);
        if (!(lookLength > 1.0E-8) || !Double.isFinite(lookLength)) {
            return basePosition;
        }
        Vec3 normalizedLook = lookVector.m_82490_(1.0 / lookLength);
        Vec3 toBase = basePosition.m_82546_(cameraPosition);
        double depth = toBase.m_82526_(normalizedLook);
        if (!(depth > 0.0) || !Double.isFinite(depth)) {
            return basePosition;
        }
        Vec3 targetPosition = cameraPosition.m_82549_(normalizedLook.m_82490_(depth));
        float factor = WorldWindow.sanitizeFollowFactor(followFactor);
        return basePosition.m_82549_(targetPosition.m_82546_(basePosition).m_82490_((double)factor));
    }

    public void setScale(float scale) {
        if (Float.isFinite(scale) && scale > 0.0f) {
            this.scaleOverride = Float.valueOf(scale);
            this.resolvedScale = scale;
        }
    }

    public boolean hasScaleOverride() {
        return this.scaleOverride != null;
    }

    public float getScale() {
        return this.resolvedScale;
    }

    public void clearScaleOverride() {
        this.scaleOverride = null;
        this.resolvedViewportVersion = Long.MIN_VALUE;
    }

    public void setDepthTest(boolean depthTest) {
        this.depthTest = depthTest;
    }

    public boolean isDepthTestEnabled() {
        return this.depthTest;
    }

    public int getMaxDistance() {
        return this.maxDistance;
    }

    public void setMaxDistance(int maxDistance) {
        this.maxDistance = WorldWindow.sanitizeDistance(maxDistance);
        this.depthFarDistance = Math.max(this.depthNearDistance + 0.001f, (float)this.maxDistance);
    }

    public int getMaxDisplayDistance() {
        return WorldWindowVisibility.resolveDisplayDistance(AuiServices.config().worldWindowMaxDisplayDistance(), this.maxDisplayDistanceOverride);
    }

    public void setMaxDisplayDistance(int maxDisplayDistance) {
        this.maxDisplayDistanceOverride = WorldWindow.sanitizeDistance(maxDisplayDistance);
    }

    public boolean hasMaxDisplayDistanceOverride() {
        return this.maxDisplayDistanceOverride != null;
    }

    public void clearMaxDisplayDistanceOverride() {
        this.maxDisplayDistanceOverride = null;
    }

    public WorldWindowDisplayPrecision getDisplayPrecision() {
        return this.displayPrecision;
    }

    public boolean hasDisplayPrecisionOverride() {
        return this.displayPrecisionOverride;
    }

    public void setDisplayPrecision(WorldWindowDisplayPrecision displayPrecision) {
        WorldWindowDisplayPrecision mode;
        this.displayPrecision = mode = displayPrecision == null ? WorldWindowDisplayPrecision.AUTO : displayPrecision;
        boolean bl = this.displayPrecisionOverride = mode != WorldWindowDisplayPrecision.AUTO;
        if (mode == WorldWindowDisplayPrecision.AUTO) {
            this.fullDetailDistanceOverride = null;
            this.reducedDetailDistanceOverride = null;
        }
    }

    public void setDisplayPrecision(String displayPrecision) {
        this.setDisplayPrecision(WorldWindowDisplayPrecision.parse(displayPrecision));
    }

    public WorldWindowDisplayPrecision getEffectiveDisplayPrecision() {
        Vec3 renderPosition;
        Minecraft minecraft = Minecraft.m_91087_();
        if (minecraft == null || minecraft.f_91063_ == null) {
            return WorldWindowDisplayPrecision.MINIMAL;
        }
        Vec3 cameraPosition = AuiServices.client().getCameraPosition();
        if (!this.isWithinDisplayDistance(cameraPosition, renderPosition = this.resolveRenderPosition(cameraPosition, AuiServices.client().getCameraLookVector()))) {
            return WorldWindowDisplayPrecision.MINIMAL;
        }
        return this.resolveDisplayPrecision(cameraPosition, renderPosition);
    }

    public void setDisplayPrecisionDistances(int fullDetailDistance, int reducedDetailDistance) {
        int full = WorldWindow.sanitizeDistance(fullDetailDistance);
        int reduced = WorldWindow.sanitizeDistance(reducedDetailDistance);
        if (reduced < full) {
            reduced = full;
        }
        this.fullDetailDistanceOverride = full;
        this.reducedDetailDistanceOverride = reduced;
        this.displayPrecisionOverride = true;
        this.displayPrecision = WorldWindowDisplayPrecision.AUTO;
    }

    public int getFullDetailDistance() {
        return this.fullDetailDistanceOverride != null ? this.fullDetailDistanceOverride.intValue() : AuiServices.config().worldWindowFullDetailDistance();
    }

    public int getReducedDetailDistance() {
        return this.reducedDetailDistanceOverride != null ? this.reducedDetailDistanceOverride.intValue() : AuiServices.config().worldWindowReducedDetailDistance();
    }

    public void setDynamicDepthStep(float nearDepthStep, float farDepthStep, float nearDistance, float farDistance) {
        this.nearDepthStep = Math.max(0.0f, nearDepthStep);
        this.farDepthStep = Math.max(this.nearDepthStep, farDepthStep);
        this.depthNearDistance = Math.max(0.0f, nearDistance);
        this.depthFarDistance = Math.max(this.depthNearDistance + 0.001f, farDistance);
    }

    public float getNearDepthStep() {
        return this.nearDepthStep;
    }

    public float getFarDepthStep() {
        return this.farDepthStep;
    }

    public float getDepthNearDistance() {
        return this.depthNearDistance;
    }

    public float getDepthFarDistance() {
        return this.depthFarDistance;
    }

    public float getWidth() {
        if (this.widthOverride != null) {
            return this.widthOverride.floatValue();
        }
        return this.documentViewportWidth();
    }

    public float getHeight() {
        if (this.heightOverride != null) {
            return this.heightOverride.floatValue();
        }
        return this.documentViewportHeight();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void render(PoseStack poseStack, Matrix4f projectionMatrix, float partialTick) {
        this.clearInteractionTransform();
        Vec3 cameraPos = AuiServices.client().getCameraPosition();
        Vec3 renderPosition = this.resolveRenderPosition(cameraPos, AuiServices.client().getCameraLookVector());
        if (!this.isWithinDisplayDistance(cameraPos, renderPosition)) {
            return;
        }
        WorldWindowDisplayPrecision precision = this.resolveDisplayPrecision(cameraPos, renderPosition);
        float documentScale = this.worldDocumentScale();
        float worldScale = this.resolveRenderScale(cameraPos, projectionMatrix, renderPosition);
        float renderScale = worldScale * documentScale;
        float viewportWidth = this.getWidth();
        float viewportHeight = this.getHeight();
        this.resolvedScale = worldScale;
        Quaternionf renderRotation = this.resolveRenderRotation(cameraPos, renderPosition);
        poseStack.m_85836_();
        poseStack.m_85837_(renderPosition.f_82479_ - cameraPos.f_82479_, renderPosition.f_82480_ - cameraPos.f_82480_, renderPosition.f_82481_ - cameraPos.f_82481_);
        poseStack.m_252781_(new Quaternionf((Quaternionfc)renderRotation));
        poseStack.m_85841_(renderScale, -renderScale, renderScale);
        if (!WorldWindow.isQuadVisible(poseStack.m_85850_().m_252922_(), projectionMatrix, viewportWidth, viewportHeight)) {
            poseStack.m_85849_();
            return;
        }
        poseStack.m_252880_(-viewportWidth / 2.0f, -viewportHeight / 2.0f, 0.0f);
        poseStack.m_85850_().m_252922_().set((Matrix4fc)poseStack.m_85850_().m_252922_());
        poseStack.m_85850_().m_252943_().set((Matrix3fc)poseStack.m_85850_().m_252943_());
        boolean previousDepthTest = AuiServices.render().isDepthTestEnabled();
        boolean previousDepthMask = AuiServices.render().isDepthMaskEnabled();
        if (this.depthTest) {
            AuiServices.render().enableDepthTest();
            AuiServices.render().setDepthFunc(515);
            AuiServices.render().setDepthMask(true);
        } else {
            AuiServices.render().disableDepthTest();
            AuiServices.render().setDepthMask(false);
        }
        AuiServices.render().enableBlend();
        AuiServices.render().setBlendFuncSeparate(770, 771, 1, 771);
        AuiServices.render().enablePolygonOffset();
        AuiServices.render().polygonOffset(-1.0f, -1.0f);
        float documentDepthBudget = this.computeDepthStep(cameraPos, renderPosition);
        float safeScale = Math.max(1.0E-4f, renderScale);
        int paintNodeCount = this.document == null ? 1 : Math.max(1, this.document.getPaintList().size());
        float localStep = documentDepthBudget / (float)paintNodeCount / safeScale;
        if (localStep > 0.2f) {
            localStep = 0.2f;
        }
        Base.pushDepthStep(localStep);
        Base.pushDepthMode(true);
        Base.pushDepthTest(this.depthTest);
        Base.pushGuiItemZ(localStep * 0.25f, localStep * 0.5f);
        float documentZOffset = documentDepthBudget / safeScale;
        Base.pushDocumentZOffset(documentZOffset);
        WorldPaintDepth.pushFlatTransforms(true);
        Mask.resetDepth(viewportWidth, viewportHeight);
        Mask.pushForceStencil();
        Mask.pushMask(poseStack, 0.0f, 0.0f, viewportWidth, viewportHeight, VIEWPORT_CLIP_RADIUS, true);
        try {
            this.captureInteractionTransform(projectionMatrix, poseStack.m_85850_().m_252922_(), renderScale, documentZOffset, renderPosition, renderRotation);
            try (WorldWindowRenderContext.Scope ignored = WorldWindowRenderContext.push(precision);){
                Base.drawDocument(poseStack, this.document);
                ResourcePreviewDialog.drawInWorld(poseStack, this.document);
            }
        }
        finally {
            Mask.popMask(poseStack, 0.0f, 0.0f, viewportWidth, viewportHeight, VIEWPORT_CLIP_RADIUS);
            Mask.popForceStencil();
            WorldPaintDepth.popFlatTransforms();
            Base.popGuiItemZ();
            Base.popDepthTest();
            Base.popDepthMode();
            Base.popDepthStep();
            Base.popDocumentZOffset();
        }
        AuiServices.render().flushSharedBuffers();
        AuiServices.render().polygonOffset(0.0f, 0.0f);
        AuiServices.render().disablePolygonOffset();
        if (previousDepthTest) {
            AuiServices.render().enableDepthTest();
        } else {
            AuiServices.render().disableDepthTest();
        }
        AuiServices.render().setDepthMask(previousDepthMask);
        poseStack.m_85849_();
    }

    private float computeDepthStep(Vec3 cameraPos, Vec3 renderPosition) {
        double distance = cameraPos.m_82554_(renderPosition);
        double t = Mth.m_14112_((double)distance, (double)this.depthNearDistance, (double)this.depthFarDistance);
        float depth = (float)Mth.m_14085_((double)this.nearDepthStep, (double)this.farDepthStep, (double)t);
        return depth * AuiServices.config().worldWindowDepthOffsetScale() / 5.0E-4f;
    }

    public static void addWindow(WorldWindow window) {
        windows.add(window);
    }

    public static void removeWindow(WorldWindow window) {
        window.document.remove();
        windows.remove(window);
    }

    public static void clear() {
        for (WorldWindow window : new ArrayList<WorldWindow>(windows)) {
            WorldWindow.removeWindow(window);
        }
    }

    public Position getDocumentPositionAtScreen(Position screenPosition) {
        Vec3 renderPosition;
        if (screenPosition == null || !Double.isFinite(screenPosition.x) || !Double.isFinite(screenPosition.y) || this.interactionClipMatrix == null || this.interactionWorldMatrix == null) {
            return null;
        }
        Minecraft minecraft = Minecraft.m_91087_();
        if (minecraft == null || minecraft.f_91063_ == null) {
            return null;
        }
        Vec3 rayOrigin = AuiServices.client().getCameraPosition();
        Vec3 vec3 = renderPosition = this.interactionPosition == null ? this.position : this.interactionPosition;
        if (!this.isWithinDisplayDistance(rayOrigin, renderPosition)) {
            return null;
        }
        Position localHit = this.unprojectToDocument(screenPosition);
        if (localHit == null) {
            return null;
        }
        double width = this.getWidth();
        double height = this.getHeight();
        if (localHit.x < 0.0 || localHit.x > width || localHit.y < 0.0 || localHit.y > height) {
            return null;
        }
        Vector4f worldHit = this.interactionWorldMatrix.transform(new Vector4f((float)localHit.x, (float)localHit.y, 0.0f, 1.0f));
        if (!(Float.isFinite(worldHit.x) && Float.isFinite(worldHit.y) && Float.isFinite(worldHit.z))) {
            return null;
        }
        Vec3 intersection = new Vec3((double)worldHit.x, (double)worldHit.y, (double)worldHit.z);
        double windowDistance = rayOrigin.m_82554_(intersection);
        if (!Double.isFinite(windowDistance) || windowDistance > (double)this.maxDistance) {
            return null;
        }
        if (this.depthTest && this.isOccluded(minecraft, rayOrigin, intersection, windowDistance)) {
            return null;
        }
        return localHit;
    }

    public Position projectDocumentPosition(Position documentPosition) {
        if (documentPosition == null || !Double.isFinite(documentPosition.x) || !Double.isFinite(documentPosition.y) || this.interactionClipMatrix == null) {
            return null;
        }
        Vector4f clip = this.interactionClipMatrix.transform(new Vector4f((float)documentPosition.x, (float)documentPosition.y, 0.0f, 1.0f));
        if (!Float.isFinite(clip.x) || !Float.isFinite(clip.y) || !Float.isFinite(clip.w) || clip.w <= 1.0E-6f) {
            return null;
        }
        double ndcX = clip.x / clip.w;
        double ndcY = clip.y / clip.w;
        if (!Double.isFinite(ndcX) || !Double.isFinite(ndcY)) {
            return null;
        }
        Minecraft minecraft = Minecraft.m_91087_();
        if (minecraft == null || minecraft.m_91268_() == null) {
            return null;
        }
        double guiWidth = Math.max(1.0, (double)minecraft.m_91268_().m_85445_());
        double guiHeight = Math.max(1.0, (double)minecraft.m_91268_().m_85446_());
        return new Position((ndcX + 1.0) * 0.5 * guiWidth, (1.0 - ndcY) * 0.5 * guiHeight);
    }

    public ScreenRect projectDocumentRect(double x, double y, double width, double height) {
        if (!Double.isFinite(x) || !Double.isFinite(y) || !Double.isFinite(width) || !Double.isFinite(height) || width <= 0.0 || height <= 0.0) {
            return null;
        }
        Position[] corners = new Position[]{this.projectDocumentPosition(new Position(x, y)), this.projectDocumentPosition(new Position(x + width, y)), this.projectDocumentPosition(new Position(x + width, y + height)), this.projectDocumentPosition(new Position(x, y + height))};
        double left = Double.POSITIVE_INFINITY;
        double top = Double.POSITIVE_INFINITY;
        double right = Double.NEGATIVE_INFINITY;
        double bottom = Double.NEGATIVE_INFINITY;
        for (Position corner : corners) {
            if (corner == null) {
                return null;
            }
            left = Math.min(left, corner.x);
            top = Math.min(top, corner.y);
            right = Math.max(right, corner.x);
            bottom = Math.max(bottom, corner.y);
        }
        return new ScreenRect(left, top, Math.max(0.0, right - left), Math.max(0.0, bottom - top));
    }

    public static WorldWindow findByDocument(Document document) {
        if (document == null) {
            return null;
        }
        for (WorldWindow window : windows) {
            if (window == null || window.document != document) continue;
            return window;
        }
        return null;
    }

    public Position getRealPos() {
        return this.getRealPos(AuiServices.client().getMousePositionForWorldInteraction());
    }

    public Position getRealPos(Position screenPosition) {
        Position documentPosition = this.getDocumentPositionAtScreen(screenPosition);
        return documentPosition == null || this.document == null ? null : this.document.documentToScreenPosition(documentPosition);
    }

    private Position unprojectToDocument(Position screenPosition) {
        Matrix4f inverse;
        Minecraft minecraft = Minecraft.m_91087_();
        if (minecraft == null || minecraft.m_91268_() == null) {
            return null;
        }
        double guiWidth = Math.max(1.0, (double)minecraft.m_91268_().m_85445_());
        double guiHeight = Math.max(1.0, (double)minecraft.m_91268_().m_85446_());
        float ndcX = (float)(screenPosition.x / guiWidth * 2.0 - 1.0);
        float ndcY = (float)(1.0 - screenPosition.y / guiHeight * 2.0);
        try {
            inverse = new Matrix4f((Matrix4fc)this.interactionClipMatrix).invert();
        }
        catch (RuntimeException invalidMatrix) {
            return null;
        }
        Vector4f near = inverse.transform(new Vector4f(ndcX, ndcY, -1.0f, 1.0f));
        Vector4f far = inverse.transform(new Vector4f(ndcX, ndcY, 1.0f, 1.0f));
        if (!WorldWindow.perspectiveDivide(near) || !WorldWindow.perspectiveDivide(far)) {
            return null;
        }
        double dz = far.z - near.z;
        if (!Double.isFinite(dz) || Math.abs(dz) < 1.0E-7) {
            return null;
        }
        double amount = (double)(-near.z) / dz;
        if (!Double.isFinite(amount) || amount < 0.0 || amount > 1.0) {
            return null;
        }
        return new Position((double)near.x + (double)(far.x - near.x) * amount, (double)near.y + (double)(far.y - near.y) * amount);
    }

    private static boolean perspectiveDivide(Vector4f point) {
        if (point == null || !Float.isFinite(point.w) || Math.abs(point.w) < 1.0E-6f) {
            return false;
        }
        point.x /= point.w;
        point.y /= point.w;
        point.z /= point.w;
        point.w = 1.0f;
        return Float.isFinite(point.x) && Float.isFinite(point.y) && Float.isFinite(point.z);
    }

    private void captureInteractionTransform(Matrix4f projectionMatrix, Matrix4f modelViewMatrix, float renderScale, float documentZOffset, Vec3 renderPosition, Quaternionf renderRotation) {
        if (projectionMatrix == null || modelViewMatrix == null || renderPosition == null || renderRotation == null) {
            return;
        }
        Matrix4f documentModelView = new Matrix4f((Matrix4fc)modelViewMatrix).translate(0.0f, 0.0f, documentZOffset);
        this.interactionClipMatrix = new Matrix4f((Matrix4fc)projectionMatrix).mul((Matrix4fc)documentModelView);
        this.interactionPosition = renderPosition;
        this.interactionWorldMatrix = new Matrix4f().translate((float)renderPosition.f_82479_, (float)renderPosition.f_82480_, (float)renderPosition.f_82481_).rotate((Quaternionfc)renderRotation).scale(renderScale, -renderScale, renderScale).translate(-this.getWidth() / 2.0f, -this.getHeight() / 2.0f, 0.0f).translate(0.0f, 0.0f, documentZOffset);
    }

    private void clearInteractionTransform() {
        this.interactionClipMatrix = null;
        this.interactionWorldMatrix = null;
        this.interactionPosition = null;
    }

    private boolean isOccluded(Minecraft minecraft, Vec3 rayOrigin, Vec3 intersection, double windowDistance) {
        if (minecraft.f_91073_ == null || minecraft.f_91074_ == null) {
            return true;
        }
        BlockHitResult blockHit = minecraft.f_91073_.m_45547_(new ClipContext(rayOrigin, intersection, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, (Entity)minecraft.f_91074_));
        if (blockHit.m_6662_() == HitResult.Type.MISS) {
            return false;
        }
        double blockDistance = rayOrigin.m_82554_(blockHit.m_82450_());
        return blockDistance + 1.0E-4 < windowDistance;
    }

    private boolean isWithinDisplayDistance(Vec3 cameraPosition) {
        return this.isWithinDisplayDistance(cameraPosition, this.position);
    }

    private boolean isWithinDisplayDistance(Vec3 cameraPosition, Vec3 renderPosition) {
        if (cameraPosition == null || renderPosition == null) {
            return false;
        }
        return WorldWindowVisibility.isWithinDisplayDistance(cameraPosition.m_82557_(renderPosition), this.getMaxDisplayDistance());
    }

    private WorldWindowDisplayPrecision resolveDisplayPrecision(Vec3 cameraPosition) {
        return this.resolveDisplayPrecision(cameraPosition, this.position);
    }

    private WorldWindowDisplayPrecision resolveDisplayPrecision(Vec3 cameraPosition, Vec3 renderPosition) {
        if (cameraPosition == null || renderPosition == null) {
            return WorldWindowDisplayPrecision.MINIMAL;
        }
        return WorldWindowVisibility.resolveDisplayPrecision(cameraPosition.m_82557_(renderPosition), this.displayPrecision, this.displayPrecisionOverride || AuiServices.config().worldWindowLodEnabled(), this.getFullDetailDistance(), this.getReducedDetailDistance());
    }

    private float documentViewportWidth() {
        if (this.document == null || this.document.getViewport() == null) {
            return 1.0f;
        }
        return Math.max(1.0f, (float)this.document.getViewport().layoutWidth());
    }

    private float documentViewportHeight() {
        if (this.document == null || this.document.getViewport() == null) {
            return 1.0f;
        }
        return Math.max(1.0f, (float)this.document.getViewport().layoutHeight());
    }

    private float resolveRenderScale(Vec3 cameraPos, Matrix4f projectionMatrix, Vec3 renderPosition) {
        long viewportVersion;
        if (this.scaleOverride != null) {
            return this.scaleOverride.floatValue();
        }
        long l = viewportVersion = this.document == null ? 0L : this.document.getViewportVersion();
        if (this.resolvedViewportVersion == viewportVersion) {
            return this.resolvedScale;
        }
        double viewDepth = cameraPos.m_82554_(renderPosition);
        float fittedScale = WorldWindow.computeViewportScale(this.getWidth() * this.worldDocumentScale(), this.getHeight() * this.worldDocumentScale(), viewDepth, projectionMatrix.m00(), projectionMatrix.m11(), this.resolvedScale);
        if (viewDepth > 0.0 && Double.isFinite(viewDepth)) {
            this.resolvedViewportVersion = viewportVersion;
        }
        return fittedScale;
    }

    private float documentRenderScale() {
        if (this.document == null || this.document.getViewport() == null) {
            return 1.0f;
        }
        float scale = this.document.getViewport().renderScale();
        return Float.isFinite(scale) && scale > 0.0f ? scale : 1.0f;
    }

    private float worldDocumentScale() {
        if (this.scaleOverride != null) {
            return 1.0f;
        }
        Minecraft minecraft = Minecraft.m_91087_();
        double guiScale = minecraft.m_91268_().m_85449_();
        if (!(guiScale > 0.0) || !Double.isFinite(guiScale)) {
            guiScale = 1.0;
        }
        return (float)Math.max(1.0E-4, (double)this.documentRenderScale() * guiScale);
    }

    static float computeViewportScale(float viewportWidth, float viewportHeight, double viewDepth, float projectionX, float projectionY, float fallbackScale) {
        double visibleWorldHeight;
        if (!(viewportWidth > 0.0f && viewportHeight > 0.0f && viewDepth > 0.0 && Double.isFinite(viewDepth) && Float.isFinite(projectionX) && Float.isFinite(projectionY) && !(Math.abs(projectionX) < 1.0E-6f) && !(Math.abs(projectionY) < 1.0E-6f))) {
            return WorldWindow.sanitizeScale(fallbackScale);
        }
        double visibleWorldWidth = 2.0 * viewDepth / (double)Math.abs(projectionX);
        double fittedScale = Math.min(visibleWorldWidth * (double)0.8f / (double)viewportWidth, (visibleWorldHeight = 2.0 * viewDepth / (double)Math.abs(projectionY)) * (double)0.8f / (double)viewportHeight);
        if (!(fittedScale > 0.0) || !Double.isFinite(fittedScale)) {
            return WorldWindow.sanitizeScale(fallbackScale);
        }
        return (float)fittedScale;
    }

    static boolean isQuadVisible(Matrix4f modelViewMatrix, Matrix4f projectionMatrix, float width, float height) {
        if (modelViewMatrix == null || projectionMatrix == null || !Float.isFinite(width) || !Float.isFinite(height) || width <= 0.0f || height <= 0.0f) {
            return true;
        }
        Matrix4f clip = new Matrix4f((Matrix4fc)projectionMatrix).mul((Matrix4fc)modelViewMatrix);
        float halfWidth = width * 0.5f;
        float halfHeight = height * 0.5f;
        float centerX = clip.m30();
        float centerY = clip.m31();
        float centerZ = clip.m32();
        float centerW = clip.m33();
        float xAxisX = clip.m00() * halfWidth;
        float xAxisY = clip.m01() * halfWidth;
        float xAxisZ = clip.m02() * halfWidth;
        float xAxisW = clip.m03() * halfWidth;
        float yAxisX = clip.m10() * halfHeight;
        float yAxisY = clip.m11() * halfHeight;
        float yAxisZ = clip.m12() * halfHeight;
        float yAxisW = clip.m13() * halfHeight;
        if (centerW + Math.abs(xAxisW) + Math.abs(yAxisW) <= 0.0f) {
            return false;
        }
        if (WorldWindow.outsideLeft(centerX, centerW, xAxisX, xAxisW, yAxisX, yAxisW)) {
            return false;
        }
        if (WorldWindow.outsideRight(centerX, centerW, xAxisX, xAxisW, yAxisX, yAxisW)) {
            return false;
        }
        if (WorldWindow.outsideLeft(centerY, centerW, xAxisY, xAxisW, yAxisY, yAxisW)) {
            return false;
        }
        if (WorldWindow.outsideRight(centerY, centerW, xAxisY, xAxisW, yAxisY, yAxisW)) {
            return false;
        }
        if (WorldWindow.outsideLeft(centerZ, centerW, xAxisZ, xAxisW, yAxisZ, yAxisW)) {
            return false;
        }
        return !WorldWindow.outsideRight(centerZ, centerW, xAxisZ, xAxisW, yAxisZ, yAxisW);
    }

    private static boolean outsideLeft(float valueCenter, float wCenter, float xAxis, float wAxis, float yAxis, float yWAxis) {
        float center = valueCenter + wCenter;
        float radius = Math.abs(xAxis + wAxis) + Math.abs(yAxis + yWAxis);
        return center + radius < 0.0f;
    }

    private static boolean outsideRight(float valueCenter, float wCenter, float xAxis, float wAxis, float yAxis, float yWAxis) {
        float center = valueCenter - wCenter;
        float radius = Math.abs(xAxis - wAxis) + Math.abs(yAxis - yWAxis);
        return center - radius > 0.0f;
    }

    private static float sanitizeScale(float value) {
        return Float.isFinite(value) && value > 0.0f ? value : 0.02f;
    }

    private static float sanitizeFollowFactor(float value) {
        return Float.isFinite(value) ? Mth.m_14036_((float)value, (float)0.0f, (float)1.0f) : 0.3f;
    }

    private static float sanitizeDimension(float value) {
        return Float.isFinite(value) && value > 0.0f ? value : 1.0f;
    }

    private static int sanitizeDistance(int value) {
        return Math.max(0, value);
    }

    public record ScreenRect(double x, double y, double width, double height) {
    }
}

