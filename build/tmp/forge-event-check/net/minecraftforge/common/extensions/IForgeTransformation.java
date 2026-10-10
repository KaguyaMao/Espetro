/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.math.Transformation
 *  net.minecraft.core.Direction
 *  org.joml.Matrix3fc
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Vector3f
 *  org.joml.Vector4f
 */
package net.minecraftforge.common.extensions;

import com.mojang.math.Transformation;
import net.minecraft.core.Direction;
import org.joml.Matrix3fc;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector4f;

public interface IForgeTransformation {
    private Transformation self() {
        return (Transformation)this;
    }

    default public boolean isIdentity() {
        return this.self().equals((Object)Transformation.m_121093_());
    }

    default public void transformPosition(Vector4f position) {
        position.mul((Matrix4fc)this.self().m_252783_());
    }

    default public void transformNormal(Vector3f normal) {
        normal.mul((Matrix3fc)this.self().getNormalMatrix());
        normal.normalize();
    }

    default public Direction rotateTransform(Direction facing) {
        return Direction.m_252919_((Matrix4f)this.self().m_252783_(), (Direction)facing);
    }

    default public Transformation blockCenterToCorner() {
        return this.applyOrigin(new Vector3f(0.5f, 0.5f, 0.5f));
    }

    default public Transformation blockCornerToCenter() {
        return this.applyOrigin(new Vector3f(-0.5f, -0.5f, -0.5f));
    }

    default public Transformation applyOrigin(Vector3f origin) {
        Transformation transform = this.self();
        if (transform.isIdentity()) {
            return Transformation.m_121093_();
        }
        Matrix4f ret = transform.m_252783_();
        Matrix4f tmp = new Matrix4f().translation(origin.x(), origin.y(), origin.z());
        tmp.mul((Matrix4fc)ret, ret);
        tmp.translation(-origin.x(), -origin.y(), -origin.z());
        ret.mul((Matrix4fc)tmp);
        return new Transformation(ret);
    }
}

