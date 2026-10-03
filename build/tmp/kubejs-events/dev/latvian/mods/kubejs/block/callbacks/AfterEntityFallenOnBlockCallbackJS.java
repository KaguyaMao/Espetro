/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.latvian.mods.rhino.util.HideFromJS
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.phys.Vec3
 */
package dev.latvian.mods.kubejs.block.callbacks;

import dev.latvian.mods.kubejs.block.callbacks.EntitySteppedOnBlockCallbackJS;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.Vec3;

public class AfterEntityFallenOnBlockCallbackJS
extends EntitySteppedOnBlockCallbackJS {
    private boolean hasChangedVelocity = false;

    public AfterEntityFallenOnBlockCallbackJS(BlockGetter blockGetter, Entity entity) {
        super(entity.m_9236_(), entity, entity.m_20097_(), blockGetter.m_8055_(entity.m_20097_()));
    }

    @Info(value="Bounce the entity upwards by bounciness * their fall velocity.\nDo not make bounciness negative, as that is a recipe for a long and laggy trip to the void\n")
    public void bounce(float bounciness) {
        Vec3 deltaMovement = this.entity.m_20184_();
        if (!this.isSuppressingBounce() && deltaMovement.f_82480_ < 0.0) {
            this.entity.m_20334_(deltaMovement.f_82479_, -deltaMovement.f_82480_ * (double)bounciness, deltaMovement.f_82481_);
            this.hasChangedVelocity = true;
        }
    }

    @Info(value="Returns the Vec3 of the entity's velocity. Use .x, .y and .z to get the respective components of that")
    public Vec3 getVelocity() {
        return this.entity.m_20184_();
    }

    @Info(value="Sets the entity's velocity")
    public void setVelocity(Vec3 vec) {
        this.entity.m_20256_(vec);
        this.hasChangedVelocity = true;
    }

    @Info(value="Sets the entity's velocity")
    public void setVelocity(float x, float y, float z) {
        this.setVelocity(new Vec3((double)x, (double)y, (double)z));
    }

    @HideFromJS
    public boolean hasChangedVelocity() {
        return this.hasChangedVelocity;
    }
}

