/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.architectury.hooks.level.ExplosionHooks
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.level.Explosion
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.level;

import dev.architectury.hooks.level.ExplosionHooks;
import dev.latvian.mods.kubejs.level.BlockContainerJS;
import dev.latvian.mods.kubejs.level.LevelEventJS;
import dev.latvian.mods.kubejs.player.EntityArrayList;
import dev.latvian.mods.kubejs.typings.Info;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public abstract class ExplosionEventJS
extends LevelEventJS {
    protected final Level level;
    protected final Explosion explosion;

    public ExplosionEventJS(Level level, Explosion explosion) {
        this.level = level;
        this.explosion = explosion;
    }

    @Override
    public Level getLevel() {
        return this.level;
    }

    public Vec3 getPosition() {
        return ExplosionHooks.getPosition((Explosion)this.explosion);
    }

    public double getX() {
        return this.getPosition().f_82479_;
    }

    public double getY() {
        return this.getPosition().f_82480_;
    }

    public double getZ() {
        return this.getPosition().f_82481_;
    }

    public BlockContainerJS getBlock() {
        return new BlockContainerJS(this.level, BlockPos.m_274446_((Position)this.getPosition()));
    }

    @Nullable
    public LivingEntity getExploder() {
        return this.explosion.m_252906_();
    }

    @Info(value="Invoked right after an explosion happens.\n")
    public static class After
    extends ExplosionEventJS {
        private final List<Entity> affectedEntities;

        public After(Level level, Explosion explosion, List<Entity> affectedEntities) {
            super(level, explosion);
            this.affectedEntities = affectedEntities;
        }

        @Info(value="Gets a list of all entities affected by the explosion.")
        public EntityArrayList getAffectedEntities() {
            return new EntityArrayList(this.level, this.affectedEntities);
        }

        @Info(value="Remove an entity from the list of affected entities.")
        public void removeAffectedEntity(Entity entity) {
            this.affectedEntities.remove(entity);
        }

        @Info(value="Remove all entities from the list of affected entities.")
        public void removeAllAffectedEntities() {
            this.affectedEntities.clear();
        }

        @Info(value="Gets a list of all blocks affected by the explosion.")
        public List<BlockContainerJS> getAffectedBlocks() {
            ArrayList<BlockContainerJS> list = new ArrayList<BlockContainerJS>(this.explosion.m_46081_().size());
            for (BlockPos pos : this.explosion.m_46081_()) {
                list.add(new BlockContainerJS(this.level, pos));
            }
            return list;
        }

        @Info(value="Remove a block from the list of affected blocks.")
        public void removeAffectedBlock(BlockContainerJS block) {
            this.explosion.m_46081_().remove(block.getPos());
        }

        @Info(value="Remove all blocks from the list of affected blocks.")
        public void removeAllAffectedBlocks() {
            this.explosion.m_46081_().clear();
        }

        @Info(value="Remove all knockback from all affected *players*.")
        public void removeKnockback() {
            this.explosion.m_46078_().clear();
        }
    }

    @Info(value="Invoked right before an explosion happens.\n")
    public static class Before
    extends ExplosionEventJS {
        public Before(Level level, Explosion explosion) {
            super(level, explosion);
        }

        @Info(value="Returns the size of the explosion.")
        public float getSize() {
            return this.explosion.f_46017_;
        }

        @Info(value="Sets the size of the explosion.")
        public void setSize(float s) {
            this.explosion.f_46017_ = s;
        }
    }
}

