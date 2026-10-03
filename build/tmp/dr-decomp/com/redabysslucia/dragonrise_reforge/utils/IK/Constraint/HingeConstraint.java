/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.Mth
 *  org.joml.Quaternionf
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package com.redabysslucia.dragonrise_reforge.utils.IK.Constraint;

import com.redabysslucia.dragonrise_reforge.utils.IK.Constraint.RotationConstraint;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class HingeConstraint
extends RotationConstraint {
    private final Vector3f hingeAxis;
    private final double minAngle;
    private final double maxAngle;

    public HingeConstraint(Vector3fc hingeAxis, double minAngleDegrees, double maxAngleDegrees) {
        this.hingeAxis = new Vector3f(hingeAxis).normalize();
        this.minAngle = Math.toRadians(minAngleDegrees);
        this.maxAngle = Math.toRadians(maxAngleDegrees);
    }

    @Override
    public Vector3f apply(Vector3f desiredDirection, Vector3fc parentPos, Vector3fc currentPos) {
        Vector3f currentDir = new Vector3f(currentPos).sub(parentPos).normalize();
        Vector3f targetDir = new Vector3f((Vector3fc)desiredDirection).normalize();
        Vector3f currentInPlane = this.removeComponent(currentDir, (Vector3fc)this.hingeAxis);
        Vector3f targetInPlane = this.removeComponent(targetDir, (Vector3fc)this.hingeAxis);
        if ((double)currentInPlane.lengthSquared() < 1.0E-10 || (double)targetInPlane.lengthSquared() < 1.0E-10) {
            return currentDir;
        }
        currentInPlane.normalize();
        targetInPlane.normalize();
        double currentAngle = this.calculateAngle(currentInPlane);
        double targetAngle = this.calculateAngle(targetInPlane);
        double clampedAngle = Mth.m_14008_((double)targetAngle, (double)this.minAngle, (double)this.maxAngle);
        Quaternionf rotation = new Quaternionf().fromAxisAngleRad((Vector3fc)this.hingeAxis, (float)(clampedAngle - currentAngle));
        return rotation.transform(currentDir).normalize();
    }

    private Vector3f removeComponent(Vector3f vector, Vector3fc component) {
        double dot = vector.dot(component);
        return new Vector3f((Vector3fc)vector).sub((Vector3fc)new Vector3f(component).mul((float)dot));
    }

    private double calculateAngle(Vector3f vector) {
        Vector3f reference = this.findPerpendicular(this.hingeAxis);
        double dot = reference.dot((Vector3fc)vector);
        double cross = new Vector3f((Vector3fc)reference).cross((Vector3fc)vector).dot((Vector3fc)this.hingeAxis);
        return Math.atan2(cross, dot);
    }
}

