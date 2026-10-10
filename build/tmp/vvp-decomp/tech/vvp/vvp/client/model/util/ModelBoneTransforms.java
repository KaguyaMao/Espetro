/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.bernie.geckolib.core.animatable.model.CoreGeoBone
 */
package tech.vvp.vvp.client.model.util;

import software.bernie.geckolib.core.animatable.model.CoreGeoBone;

public final class ModelBoneTransforms {
    private ModelBoneTransforms() {
    }

    public static void resetForVehicleRender(CoreGeoBone bone) {
        bone.setHidden(false);
        bone.setPosX(0.0f);
        bone.setPosY(0.0f);
        bone.setPosZ(0.0f);
        bone.setRotX(0.0f);
        bone.setRotY(0.0f);
        bone.setRotZ(0.0f);
        bone.setScaleX(1.0f);
        bone.setScaleY(1.0f);
        bone.setScaleZ(1.0f);
    }

    public static void clearRecoilOffsets(CoreGeoBone bone) {
        bone.setPosZ(0.0f);
        bone.setRotX(0.0f);
    }
}

