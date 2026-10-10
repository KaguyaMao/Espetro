/*
 * Decompiled with CFR 0.152.
 */
package frontline.combat.fcp.compat.vpb;

public final class ExplosionStats {
    public final float damage;
    public final float radius;
    public final int fireTime;
    public final boolean destroyBlocks;

    public ExplosionStats(float damage, float radius, int fireTime, boolean destroyBlocks) {
        this.damage = damage;
        this.radius = radius;
        this.fireTime = fireTime;
        this.destroyBlocks = destroyBlocks;
    }

    public String toString() {
        return "ExplosionStats{damage=" + this.damage + ", radius=" + this.radius + ", fireTime=" + this.fireTime + ", destroyBlocks=" + this.destroyBlocks + "}";
    }
}

