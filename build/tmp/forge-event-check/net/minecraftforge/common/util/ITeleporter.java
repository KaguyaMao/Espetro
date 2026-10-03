/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.portal.PortalForcer
 *  net.minecraft.world.level.portal.PortalInfo
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.common.util;

import java.util.function.Function;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.portal.PortalForcer;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public interface ITeleporter {
    default public Entity placeEntity(Entity entity, ServerLevel currentWorld, ServerLevel destWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
        return repositionEntity.apply(true);
    }

    @Nullable
    default public PortalInfo getPortalInfo(Entity entity, ServerLevel destWorld, Function<ServerLevel, PortalInfo> defaultPortalInfo) {
        return this.isVanilla() ? defaultPortalInfo.apply(destWorld) : new PortalInfo(entity.m_20182_(), Vec3.f_82478_, entity.m_146908_(), entity.m_146909_());
    }

    default public boolean isVanilla() {
        return this.getClass() == PortalForcer.class;
    }

    default public boolean playTeleportSound(ServerPlayer player, ServerLevel sourceWorld, ServerLevel destWorld) {
        return true;
    }
}

