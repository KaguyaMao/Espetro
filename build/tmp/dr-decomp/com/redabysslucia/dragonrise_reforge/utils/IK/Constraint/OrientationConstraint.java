/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package com.redabysslucia.dragonrise_reforge.utils.IK.Constraint;

import com.redabysslucia.dragonrise_reforge.utils.IK.Constraint.IConstraint;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class OrientationConstraint
implements IConstraint {
    private final Vector3f localForward;
    private Quaternionf desiredRotation;

    public OrientationConstraint(Vector3fc localForward) {
        this.localForward = new Vector3f(localForward).normalize();
    }

    public void apply(Quaternionfc targetOrientation, Vector3fc parentPos, Vector3fc currentPos) {
        this.desiredRotation = new Quaternionf(targetOrientation);
    }

    public Quaternionf getDesiredRotation() {
        return new Quaternionf((Quaternionfc)this.desiredRotation);
    }
}

