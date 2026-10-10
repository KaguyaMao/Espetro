/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package com.redabysslucia.dragonrise_reforge.utils.IK.Constraint;

import com.redabysslucia.dragonrise_reforge.utils.IK.Constraint.IConstraint;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public abstract class RotationConstraint
implements IConstraint {
    public abstract Vector3f apply(Vector3f var1, Vector3fc var2, Vector3fc var3);

    public Vector3f findPerpendicular(Vector3f axis) {
        Vector3f temp = new Vector3f((Vector3fc)axis);
        Vector3f perpendicular = new Vector3f(1.0f, 0.0f, 0.0f).cross((Vector3fc)temp);
        if ((double)perpendicular.lengthSquared() < 1.0E-10) {
            perpendicular = new Vector3f(0.0f, 0.0f, 1.0f).cross((Vector3fc)temp);
        }
        return perpendicular.normalize();
    }
}

