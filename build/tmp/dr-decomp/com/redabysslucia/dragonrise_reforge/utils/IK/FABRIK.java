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

import com.redabysslucia.dragonrise_reforge.utils.IK.Joint;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class FABRIK {
    private final Config config;
    private final List<Joint> joints;
    private final Vector3f[] positions;
    private final double[] lengths;

    public FABRIK(List<Joint> joints, Config config) {
        this.joints = new ArrayList<Joint>(joints);
        this.config = config;
        this.positions = new Vector3f[joints.size()];
        this.lengths = new double[joints.size() - 1];
        this.initialize();
    }

    private void initialize() {
        int i;
        for (i = 0; i < this.joints.size(); ++i) {
            this.positions[i] = new Vector3f((Vector3fc)this.joints.get(i).getPosition());
        }
        for (i = 0; i < this.joints.size() - 1; ++i) {
            this.lengths[i] = this.positions[i].distance((Vector3fc)this.positions[i + 1]);
        }
    }

    public boolean solve(Vector3fc target) {
        return this.solve(target, null);
    }

    public boolean solve(Vector3fc target, Quaternionfc targetOrientation) {
        double distanceToTarget;
        Vector3f basePosition = new Vector3f((Vector3fc)this.positions[0]);
        double totalLength = Arrays.stream(this.lengths).sum();
        if (!this.config.allowStretching && (distanceToTarget = (double)basePosition.distance(target)) > totalLength * this.config.stretchingLimit) {
            this.stretchToTarget(target);
            return false;
        }
        for (int iteration = 0; iteration < this.config.maxIterations; ++iteration) {
            this.forwardPass(target, targetOrientation);
            this.backwardPass((Vector3fc)basePosition);
            if (!this.checkConvergence(target)) continue;
            return true;
        }
        return false;
    }

    private void forwardPass(Vector3fc target, Quaternionfc targetOrientation) {
        this.positions[this.positions.length - 1].set(target);
        for (int i = this.positions.length - 2; i >= 0; --i) {
            Vector3f direction = new Vector3f();
            this.positions[i + 1].sub((Vector3fc)this.positions[i], direction);
            if (this.config.useConstraints && i < this.joints.size() - 1) {
                Joint joint = this.joints.get(i + 1);
                direction = this.applyConstraints(direction, joint, i, false);
            }
            direction.normalize();
            direction.mul((float)this.lengths[i]);
            this.positions[i].set((Vector3fc)this.positions[i + 1]).sub((Vector3fc)direction);
        }
        if (this.config.useConstraints && targetOrientation != null) {
            this.applyEndEffectorConstraint(targetOrientation);
        }
    }

    private void backwardPass(Vector3fc basePosition) {
        this.positions[0].set(basePosition);
        for (int i = 0; i < this.positions.length - 1; ++i) {
            Vector3f direction = new Vector3f();
            this.positions[i + 1].sub((Vector3fc)this.positions[i], direction);
            if (this.config.useConstraints) {
                Joint joint = this.joints.get(i + 1);
                direction = this.applyConstraints(direction, joint, i, true);
            }
            direction.normalize();
            direction.mul((float)this.lengths[i]);
            this.positions[i + 1].set((Vector3fc)this.positions[i]).add((Vector3fc)direction);
        }
    }

    private Vector3f applyConstraints(Vector3f direction, Joint joint, int segmentIndex, boolean isBackward) {
        if (!joint.hasConstraints()) {
            return direction;
        }
        Vector3f parentPos = isBackward ? this.positions[segmentIndex] : this.positions[segmentIndex + 1];
        Vector3f currentPos = isBackward ? this.positions[segmentIndex + 1] : this.positions[segmentIndex];
        return joint.applyConstraint(direction, (Vector3fc)parentPos, (Vector3fc)currentPos);
    }

    private void applyEndEffectorConstraint(Quaternionfc targetOrientation) {
        Joint endJoint;
        int lastIndex = this.positions.length - 1;
        if (lastIndex > 0 && (endJoint = this.joints.get(lastIndex)).hasOrientationConstraint()) {
            endJoint.applyOrientationConstraint(targetOrientation, (Vector3fc)this.positions[lastIndex - 1], (Vector3fc)this.positions[lastIndex]);
        }
    }

    private void stretchToTarget(Vector3fc target) {
        Vector3f direction = new Vector3f(target).sub((Vector3fc)this.positions[0]);
        direction.normalize();
        for (int i = 1; i < this.positions.length; ++i) {
            direction.mul((float)this.lengths[i - 1]);
            this.positions[i].set((Vector3fc)this.positions[i - 1]).add((Vector3fc)direction);
            direction.normalize();
        }
    }

    private boolean checkConvergence(Vector3fc target) {
        Vector3f endPos = this.positions[this.positions.length - 1];
        return (double)endPos.distanceSquared(target) < this.config.tolerance * this.config.tolerance;
    }

    public List<Vector3f> getJointPositions() {
        ArrayList<Vector3f> result = new ArrayList<Vector3f>();
        for (Vector3f pos : this.positions) {
            result.add(new Vector3f((Vector3fc)pos));
        }
        return result;
    }

    public List<Quaternionf> getJointRotations() {
        ArrayList<Quaternionf> rotations = new ArrayList<Quaternionf>();
        for (int i = 0; i < this.joints.size(); ++i) {
            if (i == 0) {
                rotations.add(new Quaternionf());
                continue;
            }
            Vector3f parentPos = this.positions[i - 1];
            Vector3f currentPos = this.positions[i];
            Vector3f restDirection = this.joints.get(i).getRestDirection();
            Quaternionf rotation = this.calculateRotation((Vector3fc)restDirection, (Vector3fc)parentPos, (Vector3fc)currentPos);
            rotations.add(rotation);
        }
        return rotations;
    }

    private Quaternionf calculateRotation(Vector3fc restDirection, Vector3fc parentPos, Vector3fc currentPos) {
        Vector3f currentDirection = new Vector3f(currentPos).sub(parentPos).normalize();
        Vector3f axis = new Vector3f(restDirection).cross((Vector3fc)currentDirection);
        if ((double)axis.lengthSquared() < 1.0E-10) {
            return new Quaternionf();
        }
        axis.normalize();
        double angle = Math.acos(restDirection.dot((Vector3fc)currentDirection));
        return new Quaternionf().fromAxisAngleRad((Vector3fc)axis, (float)angle);
    }

    public static class Config {
        private double tolerance = 1.0E-5;
        private int maxIterations = 100;
        private boolean useConstraints = true;
        private boolean allowStretching = false;
        private double stretchingLimit = 1.5;

        public Config tolerance(double tolerance) {
            this.tolerance = tolerance;
            return this;
        }

        public Config maxIterations(int maxIterations) {
            this.maxIterations = maxIterations;
            return this;
        }

        public Config useConstraints(boolean useConstraints) {
            this.useConstraints = useConstraints;
            return this;
        }

        public Config allowStretching(boolean allowStretching) {
            this.allowStretching = allowStretching;
            return this;
        }

        public Config stretchingLimit(double stretchingLimit) {
            this.stretchingLimit = stretchingLimit;
            return this;
        }
    }
}

