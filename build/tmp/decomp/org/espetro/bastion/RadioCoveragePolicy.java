/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.bastion;

import java.util.Objects;

public final class RadioCoveragePolicy {
    private RadioCoveragePolicy() {
    }

    public static double minimumCenterDistance(double buildRadius, double exclusionRadius) {
        double safeBuildRadius = Math.max(0.0, buildRadius);
        double safeExclusionRadius = Math.max(0.0, exclusionRadius);
        return Math.max(safeExclusionRadius, safeBuildRadius * 2.0);
    }

    public static boolean overlaps(double distanceSquared, double minimumCenterDistance) {
        if (distanceSquared < 0.0 || minimumCenterDistance <= 0.0) {
            return false;
        }
        return distanceSquared < minimumCenterDistance * minimumCenterDistance;
    }

    public static boolean blocksPlacement(String existingTeam, String placingTeam, double distanceSquared, double minimumCenterDistance) {
        return Objects.equals(existingTeam, placingTeam) && RadioCoveragePolicy.overlaps(distanceSquared, minimumCenterDistance);
    }
}

