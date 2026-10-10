/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package com.redabysslucia.dragonrise_reforge.utils.IK.Constraint;

import com.redabysslucia.dragonrise_reforge.utils.IK.Constraint.RotationConstraint;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class PlaneConstraint
extends RotationConstraint {
    private final Vector3f planeNormal;

    public PlaneConstraint(Vector3fc planeNormal) {
        this.planeNormal = new Vector3f(planeNormal).normalize();
    }

    @Override
    public Vector3f apply(Vector3f desiredDirection, Vector3fc parentPos, Vector3fc currentPos) {
        Vector3f projected = new Vector3f((Vector3fc)desiredDirection);
        double dot = projected.dot((Vector3fc)this.planeNormal);
        projected.sub((Vector3fc)this.planeNormal.mul((float)dot, new Vector3f()));
        return projected.normalize();
    }
}

