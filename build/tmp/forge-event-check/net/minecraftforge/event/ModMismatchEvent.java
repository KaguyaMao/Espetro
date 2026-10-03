/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.storage.LevelStorageSource$LevelDirectory
 *  net.minecraftforge.eventbus.api.Event
 *  net.minecraftforge.fml.ModContainer
 *  net.minecraftforge.fml.ModList
 *  net.minecraftforge.fml.ModLoadingContext
 *  net.minecraftforge.fml.event.IModBusEvent
 *  net.minecraftforge.forgespi.language.IModInfo
 *  org.apache.maven.artifact.versioning.ArtifactVersion
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.event;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.forgespi.language.IModInfo;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

public class ModMismatchEvent
extends Event
implements IModBusEvent {
    private final LevelStorageSource.LevelDirectory levelDirectory;
    private final HashMap<String, MismatchedVersionInfo> versionDifferences;
    private final HashMap<String, ModContainer> resolved;

    @ApiStatus.Internal
    public ModMismatchEvent(LevelStorageSource.LevelDirectory levelDirectory, Map<String, ArtifactVersion> previousVersions, Map<String, ArtifactVersion> missingVersions) {
        this.levelDirectory = levelDirectory;
        this.resolved = new HashMap(previousVersions.size());
        this.versionDifferences = new HashMap();
        previousVersions.forEach((modId, version) -> this.versionDifferences.put((String)modId, new MismatchedVersionInfo((ArtifactVersion)version, ModList.get().getModContainerById(modId).map(ModContainer::getModInfo).map(IModInfo::getVersion).orElse(null))));
        missingVersions.forEach((modId, version) -> this.versionDifferences.put((String)modId, new MismatchedVersionInfo((ArtifactVersion)version, null)));
    }

    public LevelStorageSource.LevelDirectory getLevelDirectory() {
        return this.levelDirectory;
    }

    @Nullable
    public ArtifactVersion getPreviousVersion(String modId) {
        if (this.versionDifferences.containsKey(modId)) {
            return this.versionDifferences.get(modId).oldVersion();
        }
        return null;
    }

    @Nullable
    public ArtifactVersion getCurrentVersion(String modid) {
        if (this.versionDifferences.containsKey(modid)) {
            return this.versionDifferences.get(modid).newVersion();
        }
        return null;
    }

    public void markResolved(String modId) {
        ModContainer resolvedBy = ModLoadingContext.get().getActiveContainer();
        this.resolved.putIfAbsent(modId, resolvedBy);
    }

    public boolean wasResolved(String modId) {
        return this.resolved.containsKey(modId);
    }

    public Optional<MismatchedVersionInfo> getVersionDifference(String modid) {
        return Optional.ofNullable(this.versionDifferences.get(modid));
    }

    public Optional<ModContainer> getResolver(String modid) {
        return Optional.ofNullable(this.resolved.get(modid));
    }

    public boolean anyUnresolved() {
        return this.resolved.size() < this.versionDifferences.size();
    }

    public Stream<MismatchResolutionResult> getUnresolved() {
        return this.versionDifferences.keySet().stream().filter(modid -> !this.resolved.containsKey(modid)).map(unresolved -> new MismatchResolutionResult((String)unresolved, this.versionDifferences.get(unresolved), null)).sorted(Comparator.comparing(MismatchResolutionResult::modid));
    }

    public boolean anyResolved() {
        return !this.resolved.isEmpty();
    }

    public Stream<MismatchResolutionResult> getResolved() {
        return this.resolved.keySet().stream().map(modid -> new MismatchResolutionResult((String)modid, this.versionDifferences.get(modid), this.resolved.get(modid))).sorted(Comparator.comparing(MismatchResolutionResult::modid));
    }

    public record MismatchedVersionInfo(ArtifactVersion oldVersion, @Nullable ArtifactVersion newVersion) {
        public boolean isMissing() {
            return this.newVersion == null;
        }

        public boolean wasUpgrade() {
            if (this.newVersion == null) {
                return false;
            }
            return this.newVersion.compareTo((Object)this.oldVersion) > 0;
        }
    }

    public record MismatchResolutionResult(String modid, MismatchedVersionInfo versionDifference, @Nullable ModContainer resolver) {
        public boolean wasSelfResolved() {
            return this.resolver != null && this.resolver.getModId().equals(this.modid);
        }
    }
}

