/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package com.redabysslucia.dragonrise_reforge.utils.IK;

import com.redabysslucia.dragonrise_reforge.utils.IK.Constraint.IConstraint;
import com.redabysslucia.dragonrise_reforge.utils.IK.Constraint.OrientationConstraint;
import com.redabysslucia.dragonrise_reforge.utils.IK.Constraint.RotationConstraint;
import java.util.ArrayList;
import java.util.List;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class Joint {
    private final Vector3f position;
    private final Vector3f restDirection;
    private final List<IConstraint> constraints;
    private Quaternionf localRotation;

    public Joint(Vector3fc position) {
        this.position = new Vector3f(position);
        this.restDirection = new Vector3f(0.0f, 1.0f, 0.0f);
        this.constraints = new ArrayList<IConstraint>();
        this.localRotation = new Quaternionf();
    }

    public Joint(Vector3fc position, Vector3fc restDirection) {
        this.position = new Vector3f(position);
        this.restDirection = new Vector3f(restDirection).normalize();
        this.constraints = new ArrayList<IConstraint>();
        this.localRotation = new Quaternionf();
    }

    public Vector3f getPosition() {
        return new Vector3f((Vector3fc)this.position);
    }

    public Vector3f getRestDirection() {
        return new Vector3f((Vector3fc)this.restDirection);
    }

    public void addConstraint(IConstraint constraint) {
        this.constraints.add(constraint);
    }

    public boolean hasConstraints() {
        return !this.constraints.isEmpty();
    }

    public boolean hasOrientationConstraint() {
        return this.constraints.stream().anyMatch(c -> c instanceof OrientationConstraint);
    }

    public Vector3f applyConstraint(Vector3f desiredDirection, Vector3fc parentPos, Vector3fc currentPos) {
        Vector3f constrainedDir = new Vector3f((Vector3fc)desiredDirection);
        for (IConstraint constraint : this.constraints) {
            if (!(constraint instanceof RotationConstraint)) continue;
            constrainedDir = ((RotationConstraint)constraint).apply(constrainedDir, parentPos, currentPos);
        }
        return constrainedDir;
    }

    public void applyOrientationConstraint(Quaternionfc targetOrientation, Vector3fc parentPos, Vector3fc currentPos) {
        for (IConstraint constraint : this.constraints) {
            if (!(constraint instanceof OrientationConstraint)) continue;
            ((OrientationConstraint)constraint).apply(targetOrientation, parentPos, currentPos);
        }
    }

    public Quaternionf getLocalRotation() {
        return new Quaternionf((Quaternionfc)this.localRotation);
    }

    public void setLocalRotation(Quaternionfc rotation) {
        this.localRotation.set(rotation);
    }
}

