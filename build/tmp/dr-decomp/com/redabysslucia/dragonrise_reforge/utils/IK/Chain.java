/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Vector3fc
 */
package com.redabysslucia.dragonrise_reforge.utils.IK;

import com.redabysslucia.dragonrise_reforge.utils.IK.Constraint.IConstraint;
import com.redabysslucia.dragonrise_reforge.utils.IK.FABRIK;
import com.redabysslucia.dragonrise_reforge.utils.IK.Joint;
import java.util.ArrayList;
import java.util.List;
import org.joml.Vector3fc;

public class Chain {
    private final List<Joint> joints = new ArrayList<Joint>();

    public Chain addJoint(Vector3fc position) {
        Joint joint = new Joint(position);
        this.joints.add(joint);
        return this;
    }

    public Chain addJoint(Vector3fc position, Vector3fc restDirection) {
        Joint joint = new Joint(position, restDirection);
        this.joints.add(joint);
        return this;
    }

    public Chain addConstraint(int jointIndex, IConstraint constraint) {
        if (jointIndex >= 0 && jointIndex < this.joints.size()) {
            this.joints.get(jointIndex).addConstraint(constraint);
        }
        return this;
    }

    public FABRIK build(FABRIK.Config config) {
        return new FABRIK(this.joints, config);
    }
}

