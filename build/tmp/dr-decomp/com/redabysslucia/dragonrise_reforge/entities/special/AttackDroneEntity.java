/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.entity.projectile.ProjectileEntity
 *  com.atsuishio.superbwarfare.init.ModEntities
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 */
package com.redabysslucia.dragonrise_reforge.entities.special;

import com.atsuishio.superbwarfare.entity.projectile.ProjectileEntity;
import com.atsuishio.superbwarfare.init.ModEntities;
import com.redabysslucia.dragonrise_reforge.entities.special.R6DroneEntity;
import com.redabysslucia.dragonrise_reforge.init.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class AttackDroneEntity
extends R6DroneEntity {
    private static final int SHOOT_COOLDOWN = 80;
    private static final float PROJECTILE_VELOCITY = 30.0f;
    private static final float PROJECTILE_DAMAGE = 5.0f;
    private static final float PROJECTILE_SPREAD = 0.5f;
    private static final int PROJECTILE_LIFE = 4;
    private int shootCooldown;
    private boolean firing;

    public AttackDroneEntity(EntityType<? extends AttackDroneEntity> type, Level level) {
        super(type, level);
    }

    @Override
    public void m_8119_() {
        Player controller;
        super.m_8119_();
        if (this.m_9236_().m_5776_()) {
            return;
        }
        if (this.shootCooldown > 0) {
            --this.shootCooldown;
        }
        if ((controller = this.getController()) == null || !this.isMonitorControlling(controller)) {
            this.firing = false;
            return;
        }
        if (this.firing && this.shootCooldown <= 0) {
            this.shoot();
            this.shootCooldown = 80;
        }
    }

    private void shoot() {
        Player controller = this.getController();
        if (controller == null) {
            return;
        }
        ProjectileEntity projectile = new ProjectileEntity((EntityType)ModEntities.PROJECTILE.get(), this.m_9236_());
        projectile.setDamage(5.0f);
        projectile.setVelocity(30.0f);
        projectile.setCustomGravity(0.0f);
        projectile.setLife(4);
        projectile.shooter((Entity)this);
        Vec3 pos = this.m_20182_().m_82549_(this.m_20154_().m_82490_(0.5)).m_82520_(0.0, 0.1, 0.0);
        projectile.m_6034_(pos.f_82479_, pos.f_82480_, pos.f_82481_);
        Vec3 dir = controller.m_20154_();
        projectile.m_6686_(dir.f_82479_, dir.f_82480_, dir.f_82481_, 30.0f, 0.5f);
        this.m_9236_().m_7967_((Entity)projectile);
        SoundEvent fireSound = SoundEvent.m_262824_((ResourceLocation)new ResourceLocation("superbwarfare", "awm_fire_1p_s"));
        this.playDroneSound(fireSound, 1.0f);
    }

    @Override
    protected Item getDeployItem() {
        return (Item)ModItems.ATTACK_DRONE.get();
    }

    @Override
    public void processInput(short keys) {
        super.processInput(keys);
        this.firing = (keys & 0x20) > 0;
    }
}

