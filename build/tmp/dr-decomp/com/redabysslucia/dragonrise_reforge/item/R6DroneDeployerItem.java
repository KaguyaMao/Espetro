/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.item.misc.AbstractDeployerItem
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.level.Level
 */
package com.redabysslucia.dragonrise_reforge.item;

import com.atsuishio.superbwarfare.item.misc.AbstractDeployerItem;
import com.redabysslucia.dragonrise_reforge.entities.special.R6DroneEntity;
import com.redabysslucia.dragonrise_reforge.init.ModEntities;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class R6DroneDeployerItem
extends AbstractDeployerItem {
    public R6DroneDeployerItem(Item.Properties properties) {
        super(properties);
    }

    public Entity spawnDeployedEntity(Level level, Player player) {
        return new R6DroneEntity((EntityType<? extends R6DroneEntity>)((EntityType)ModEntities.R6_DRONE.get()), level);
    }
}

