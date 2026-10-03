/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.SpawnPlacements$SpawnPredicate
 *  net.minecraft.world.entity.SpawnPlacements$Type
 *  net.minecraft.world.level.levelgen.Heightmap$Types
 *  net.minecraftforge.eventbus.api.Event
 *  net.minecraftforge.fml.event.IModBusEvent
 *  org.jetbrains.annotations.ApiStatus$Internal
 */
package net.minecraftforge.event.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.ApiStatus;

public class SpawnPlacementRegisterEvent
extends Event
implements IModBusEvent {
    private final Map<EntityType<?>, MergedSpawnPredicate<?>> map;

    @ApiStatus.Internal
    public SpawnPlacementRegisterEvent(Map<EntityType<?>, MergedSpawnPredicate<?>> map) {
        this.map = map;
    }

    public <T extends Entity> void register(EntityType<T> entityType, SpawnPlacements.SpawnPredicate<T> predicate) {
        this.register(entityType, null, null, predicate, Operation.OR);
    }

    public <T extends Entity> void register(EntityType<T> entityType, SpawnPlacements.SpawnPredicate<T> predicate, Operation operation) {
        this.register(entityType, null, null, predicate, operation);
    }

    public <T extends Entity> void register(EntityType<T> entityType, @Nullable SpawnPlacements.Type placementType, @Nullable Heightmap.Types heightmap, SpawnPlacements.SpawnPredicate<T> predicate, Operation operation) {
        if (!this.map.containsKey(entityType)) {
            if (placementType == null) {
                throw new NullPointerException("Registering a new Spawn Predicate requires a nonnull placement type! Entity Type: " + String.valueOf(ForgeRegistries.ENTITY_TYPES.getKey(entityType)));
            }
            if (heightmap == null) {
                throw new NullPointerException("Registering a new Spawn Predicate requires a nonnull heightmap type! Entity Type: " + String.valueOf(ForgeRegistries.ENTITY_TYPES.getKey(entityType)));
            }
            this.map.put(entityType, new MergedSpawnPredicate<T>(predicate, placementType, heightmap));
        } else {
            if (operation != Operation.REPLACE && (heightmap != null || placementType != null)) {
                throw new IllegalStateException("Nonnull heightmap types or spawn placement types may only be used with the REPLACE operation. Entity Type: " + String.valueOf(ForgeRegistries.ENTITY_TYPES.getKey(entityType)));
            }
            this.map.get(entityType).merge(operation, predicate, placementType, heightmap);
        }
    }

    public static enum Operation {
        AND,
        OR,
        REPLACE;

    }

    public static class MergedSpawnPredicate<T extends Entity> {
        private final SpawnPlacements.SpawnPredicate<T> originalPredicate;
        private final List<SpawnPlacements.SpawnPredicate<T>> orPredicates;
        private final List<SpawnPlacements.SpawnPredicate<T>> andPredicates;
        @Nullable
        private SpawnPlacements.SpawnPredicate<T> replacementPredicate;
        private SpawnPlacements.Type spawnType;
        private Heightmap.Types heightmapType;

        public MergedSpawnPredicate(SpawnPlacements.SpawnPredicate<T> originalPredicate, SpawnPlacements.Type spawnType, Heightmap.Types heightmapType) {
            this.originalPredicate = originalPredicate;
            this.orPredicates = new ArrayList<SpawnPlacements.SpawnPredicate<T>>();
            this.andPredicates = new ArrayList<SpawnPlacements.SpawnPredicate<T>>();
            this.replacementPredicate = null;
            this.spawnType = spawnType;
            this.heightmapType = heightmapType;
        }

        public SpawnPlacements.Type getSpawnType() {
            return this.spawnType;
        }

        public Heightmap.Types getHeightmapType() {
            return this.heightmapType;
        }

        private void merge(Operation operation, SpawnPlacements.SpawnPredicate<T> predicate, @Nullable SpawnPlacements.Type spawnType, @Nullable Heightmap.Types heightmapType) {
            if (operation == Operation.AND) {
                this.andPredicates.add(predicate);
            } else if (operation == Operation.OR) {
                this.orPredicates.add(predicate);
            } else if (operation == Operation.REPLACE) {
                this.replacementPredicate = predicate;
                if (spawnType != null) {
                    this.spawnType = spawnType;
                }
                if (heightmapType != null) {
                    this.heightmapType = heightmapType;
                }
            }
        }

        @ApiStatus.Internal
        public SpawnPlacements.SpawnPredicate<T> build() {
            if (this.replacementPredicate != null) {
                return this.replacementPredicate;
            }
            SpawnPlacements.SpawnPredicate<T> original = this.originalPredicate;
            SpawnPlacements.SpawnPredicate compiledOrPredicate = (entityType, level, spawnType, pos, random) -> {
                if (original.m_217080_(entityType, level, spawnType, pos, random)) {
                    return true;
                }
                for (SpawnPlacements.SpawnPredicate<T> predicate : this.orPredicates) {
                    if (!predicate.m_217080_(entityType, level, spawnType, pos, random)) continue;
                    return true;
                }
                return false;
            };
            SpawnPlacements.SpawnPredicate compiledAndPredicate = (entityType, level, spawnType, pos, random) -> {
                if (!compiledOrPredicate.m_217080_(entityType, level, spawnType, pos, random)) {
                    return false;
                }
                for (SpawnPlacements.SpawnPredicate<T> predicate : this.andPredicates) {
                    if (predicate.m_217080_(entityType, level, spawnType, pos, random)) continue;
                    return false;
                }
                return true;
            };
            return compiledAndPredicate;
        }
    }
}

