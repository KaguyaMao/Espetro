/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.phys.Vec3
 */
package frontline.combat.fcp.effects;

import net.minecraft.world.phys.Vec3;

public record GunAxes(Vec3 forward, Vec3 right, Vec3 up) {
    public static GunAxes fromDirection(Vec3 forward) {
        Vec3 right = forward.m_82537_(new Vec3(0.0, 1.0, 0.0));
        if (right.m_82556_() < 1.0E-6) {
            right = forward.m_82537_(new Vec3(1.0, 0.0, 0.0));
        }
        right = right.m_82541_();
        Vec3 up = right.m_82537_(forward).m_82541_();
        return new GunAxes(forward, right, up);
    }

    public Vec3 toWorld(Vec3 local) {
        return this.right.m_82490_(local.f_82479_).m_82549_(this.up.m_82490_(local.f_82480_)).m_82549_(this.forward.m_82490_(local.f_82481_));
    }
}

