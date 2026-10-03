/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.MobCategory
 *  net.minecraft.world.level.biome.MobSpawnSettings
 *  net.minecraft.world.level.biome.MobSpawnSettings$Builder
 *  net.minecraft.world.level.biome.MobSpawnSettings$MobSpawnCost
 *  net.minecraft.world.level.biome.MobSpawnSettings$SpawnerData
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.common.world;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;
import org.jetbrains.annotations.Nullable;

public class MobSpawnSettingsBuilder
extends MobSpawnSettings.Builder {
    private final Set<MobCategory> typesView;
    private final Set<EntityType<?>> costView;

    public MobSpawnSettingsBuilder(MobSpawnSettings orig) {
        this.typesView = Collections.unmodifiableSet(this.f_48362_.keySet());
        this.costView = Collections.unmodifiableSet(this.f_48363_.keySet());
        orig.getSpawnerTypes().forEach(k -> {
            ((List)this.f_48362_.get(k)).clear();
            ((List)this.f_48362_.get(k)).addAll(orig.m_151798_(k).m_146338_());
        });
        orig.getEntityTypes().forEach(k -> this.f_48363_.put(k, orig.m_48345_(k)));
        this.f_48364_ = orig.m_48344_();
    }

    public Set<MobCategory> getSpawnerTypes() {
        return this.typesView;
    }

    public List<MobSpawnSettings.SpawnerData> getSpawner(MobCategory type) {
        return (List)this.f_48362_.get(type);
    }

    public Set<EntityType<?>> getEntityTypes() {
        return this.costView;
    }

    @Nullable
    public MobSpawnSettings.MobSpawnCost getCost(EntityType<?> type) {
        return (MobSpawnSettings.MobSpawnCost)this.f_48363_.get(type);
    }

    public float getProbability() {
        return this.f_48364_;
    }

    public MobSpawnSettingsBuilder disablePlayerSpawn() {
        return this;
    }
}

