/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Quaternionf
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package com.redabysslucia.dragonrise_reforge.utils.IK.Constraint;

import com.redabysslucia.dragonrise_reforge.utils.IK.Constraint.RotationConstraint;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class ConeConstraint
extends RotationConstraint {
    private final Vector3f axis;
    private final double maxAngle;

    public ConeConstraint(Vector3fc axis, double maxAngleDegrees) {
        this.axis = new Vector3f(axis).normalize();
        this.maxAngle = Math.toRadians(maxAngleDegrees);
    }

    @Override
    public Vector3f apply(Vector3f desiredDirection, Vector3fc parentPos, Vector3fc currentPos) {
        Vector3f currentDir = new Vector3f(currentPos).sub(parentPos).normalize();
        Vector3f targetDir = new Vector3f((Vector3fc)desiredDirection).normalize();
        double currentAngle = Math.acos(this.axis.dot((Vector3fc)currentDir));
        double targetAngle = Math.acos(this.axis.dot((Vector3fc)targetDir));
        if (targetAngle > this.maxAngle) {
            Vector3f rotationAxis = new Vector3f((Vector3fc)this.axis).cross((Vector3fc)currentDir);
            if ((double)rotationAxis.lengthSquared() < 1.0E-10) {
                rotationAxis = this.findPerpendicular(this.axis);
            }
            rotationAxis.normalize();
            double angleToRotate = this.maxAngle - currentAngle;
            Quaternionf rotation = new Quaternionf().fromAxisAngleRad((Vector3fc)rotationAxis, (float)angleToRotate);
            targetDir = rotation.transform(currentDir);
        }
        return targetDir.normalize();
    }
}

