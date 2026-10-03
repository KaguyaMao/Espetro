/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.forgespi.locating.ForgeFeature$Bound
 *  org.apache.maven.artifact.versioning.ArtifactVersion
 *  org.apache.maven.artifact.versioning.Restriction
 *  org.apache.maven.artifact.versioning.VersionRange
 */
package net.minecraftforge.common.util;

import java.util.Objects;
import java.util.stream.Collectors;
import net.minecraftforge.common.ForgeI18n;
import net.minecraftforge.forgespi.locating.ForgeFeature;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.Restriction;
import org.apache.maven.artifact.versioning.VersionRange;

public class MavenVersionStringHelper {
    public static String artifactVersionToString(ArtifactVersion artifactVersion) {
        return artifactVersion.toString();
    }

    public static String versionRangeToString(VersionRange range) {
        return range.getRestrictions().stream().map(MavenVersionStringHelper::restrictionToString).collect(Collectors.joining(", "));
    }

    public static String restrictionToString(Restriction restriction) {
        if (restriction.getLowerBound() == null && restriction.getUpperBound() == null) {
            return ForgeI18n.parseMessage("fml.messages.version.restriction.any", new Object[0]);
        }
        if (restriction.getLowerBound() != null && restriction.getUpperBound() != null) {
            if (Objects.equals(MavenVersionStringHelper.artifactVersionToString(restriction.getLowerBound()), MavenVersionStringHelper.artifactVersionToString(restriction.getUpperBound()))) {
                return MavenVersionStringHelper.artifactVersionToString(restriction.getLowerBound());
            }
            if (restriction.isLowerBoundInclusive() && restriction.isUpperBoundInclusive()) {
                return ForgeI18n.parseMessage("fml.messages.version.restriction.bounded.inclusive", restriction.getLowerBound(), restriction.getUpperBound());
            }
            if (restriction.isLowerBoundInclusive()) {
                return ForgeI18n.parseMessage("fml.messages.version.restriction.bounded.upperexclusive", restriction.getLowerBound(), restriction.getUpperBound());
            }
            if (restriction.isUpperBoundInclusive()) {
                return ForgeI18n.parseMessage("fml.messages.version.restriction.bounded.lowerexclusive", restriction.getLowerBound(), restriction.getUpperBound());
            }
            return ForgeI18n.parseMessage("fml.messages.version.restriction.bounded.exclusive", restriction.getLowerBound(), restriction.getUpperBound());
        }
        if (restriction.getLowerBound() != null) {
            if (restriction.isLowerBoundInclusive()) {
                return ForgeI18n.parseMessage("fml.messages.version.restriction.lower.inclusive", restriction.getLowerBound());
            }
            return ForgeI18n.parseMessage("fml.messages.version.restriction.lower.exclusive", restriction.getLowerBound());
        }
        if (restriction.isUpperBoundInclusive()) {
            return ForgeI18n.parseMessage("fml.messages.version.restriction.upper.inclusive", restriction.getUpperBound());
        }
        return ForgeI18n.parseMessage("fml.messages.version.restriction.upper.exclusive", restriction.getUpperBound());
    }

    public static void parseVersionRange(StringBuffer stringBuffer, Object range) {
        stringBuffer.append(MavenVersionStringHelper.versionRangeToString((VersionRange)range));
    }

    public static void parseFeatureBoundValue(StringBuffer stringBuffer, Object range) {
        if (range instanceof ForgeFeature.Bound) {
            ForgeFeature.Bound bound = (ForgeFeature.Bound)range;
            stringBuffer.append(bound.featureName());
            Object object = bound.bound();
            if (object instanceof Boolean) {
                Boolean b = (Boolean)object;
                stringBuffer.append("=").append(b);
            } else {
                object = bound.bound();
                if (object instanceof VersionRange) {
                    VersionRange vr = (VersionRange)object;
                    stringBuffer.append(" ").append(MavenVersionStringHelper.versionRangeToString(vr));
                } else {
                    stringBuffer.append("=\"").append(bound.featureBound()).append("\"");
                }
            }
        }
    }
}

