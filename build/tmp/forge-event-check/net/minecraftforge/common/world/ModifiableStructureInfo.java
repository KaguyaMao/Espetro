/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Holder
 *  net.minecraft.world.level.levelgen.structure.Structure
 *  net.minecraft.world.level.levelgen.structure.Structure$StructureSettings
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.common.world;

import java.util.List;
import java.util.Locale;
import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraftforge.common.world.StructureModifier;
import net.minecraftforge.common.world.StructureSettingsBuilder;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ModifiableStructureInfo {
    @NotNull
    private final StructureInfo originalStructureInfo;
    @Nullable
    private StructureInfo modifiedStructureInfo = null;

    public ModifiableStructureInfo(@NotNull StructureInfo originalStructureInfo) {
        this.originalStructureInfo = originalStructureInfo;
    }

    @NotNull
    public StructureInfo get() {
        return this.modifiedStructureInfo == null ? this.originalStructureInfo : this.modifiedStructureInfo;
    }

    @NotNull
    public StructureInfo getOriginalStructureInfo() {
        return this.originalStructureInfo;
    }

    @Nullable
    public StructureInfo getModifiedStructureInfo() {
        return this.modifiedStructureInfo;
    }

    @ApiStatus.Internal
    public void applyStructureModifiers(Holder<Structure> structure, List<StructureModifier> structureModifiers) {
        if (this.modifiedStructureInfo != null) {
            throw new IllegalStateException(String.format(Locale.ENGLISH, "Structure %s already modified", structure));
        }
        StructureInfo original = this.getOriginalStructureInfo();
        StructureInfo.Builder builder = StructureInfo.Builder.copyOf(original);
        for (StructureModifier.Phase phase : StructureModifier.Phase.values()) {
            for (StructureModifier modifier : structureModifiers) {
                modifier.modify(structure, phase, builder);
            }
        }
        this.modifiedStructureInfo = builder.build();
    }

    public record StructureInfo(Structure.StructureSettings structureSettings) {

        public static class Builder {
            private StructureSettingsBuilder structureSettings;

            public static Builder copyOf(StructureInfo original) {
                StructureSettingsBuilder structureBuilder = StructureSettingsBuilder.copyOf(original.structureSettings());
                return new Builder(structureBuilder);
            }

            private Builder(StructureSettingsBuilder structureSettings) {
                this.structureSettings = structureSettings;
            }

            public StructureInfo build() {
                return new StructureInfo(this.structureSettings.build());
            }

            public StructureSettingsBuilder getStructureSettings() {
                return this.structureSettings;
            }
        }
    }
}

