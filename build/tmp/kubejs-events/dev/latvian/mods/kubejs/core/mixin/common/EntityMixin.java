/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.latvian.mods.rhino.util.HideFromJS
 *  dev.latvian.mods.rhino.util.RemapForJS
 *  dev.latvian.mods.rhino.util.RemapPrefixForJS
 *  net.minecraft.core.Direction
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.level.Level
 *  org.jetbrains.annotations.Nullable
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package dev.latvian.mods.kubejs.core.mixin.common;

import dev.latvian.mods.kubejs.core.EntityKJS;
import dev.latvian.mods.rhino.util.HideFromJS;
import dev.latvian.mods.rhino.util.RemapForJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@RemapPrefixForJS(value="kjs$")
@Mixin(value={Entity.class})
public abstract class EntityMixin
implements EntityKJS {
    private CompoundTag kjs$persistentData;
    @Shadow
    @RemapForJS(value="stepHeight")
    public float f_19793_;
    @Shadow
    @RemapForJS(value="age")
    public int f_19797_;

    @Shadow
    public abstract boolean m_20137_(String var1);

    @Override
    public CompoundTag kjs$getPersistentData() {
        if (this.kjs$persistentData == null) {
            this.kjs$persistentData = new CompoundTag();
        }
        return this.kjs$persistentData;
    }

    @Inject(method={"saveWithoutId"}, at={@At(value="RETURN")})
    private void saveKJS(CompoundTag tag, CallbackInfoReturnable<CompoundTag> ci) {
        if (this.kjs$persistentData != null && !this.kjs$persistentData.m_128456_()) {
            tag.m_128365_("KubeJSPersistentData", (Tag)this.kjs$persistentData);
        }
    }

    @Inject(method={"load"}, at={@At(value="RETURN")})
    private void loadKJS(CompoundTag tag, CallbackInfo ci) {
        this.kjs$persistentData = tag.m_128441_("KubeJSPersistentData") ? tag.m_128469_("KubeJSPersistentData") : null;
    }

    @Override
    @HideFromJS
    @Nullable
    public CompoundTag kjs$getRawPersistentData() {
        return this.kjs$persistentData;
    }

    @Override
    @HideFromJS
    public void kjs$setRawPersistentData(@Nullable CompoundTag tag) {
        this.kjs$persistentData = tag;
    }

    @Shadow
    @RemapForJS(value="getUuid")
    public abstract UUID m_20148_();

    @Shadow
    @RemapForJS(value="getStringUuid")
    public abstract String m_20149_();

    @Shadow
    @RemapForJS(value="getUsername")
    public abstract String m_6302_();

    @Shadow
    @RemapForJS(value="isGlowing")
    public abstract boolean m_142038_();

    @Shadow
    @RemapForJS(value="setGlowing")
    public abstract void m_146915_(boolean var1);

    @Shadow
    @RemapForJS(value="getYaw")
    public abstract float m_146908_();

    @Shadow
    @RemapForJS(value="setYaw")
    public abstract void m_146922_(float var1);

    @Shadow
    @RemapForJS(value="getPitch")
    public abstract float m_146909_();

    @Shadow
    @RemapForJS(value="setPitch")
    public abstract void m_146926_(float var1);

    @Shadow
    @RemapForJS(value="setMotion")
    public abstract void m_20334_(double var1, double var3, double var5);

    @Shadow
    @RemapForJS(value="setPositionAndRotation")
    public abstract void m_7678_(double var1, double var3, double var5, float var7, float var8);

    @Shadow
    @RemapForJS(value="addMotion")
    public abstract void m_5997_(double var1, double var3, double var5);

    @Shadow
    @HideFromJS
    public abstract List<Entity> m_20197_();

    @Shadow
    @RemapForJS(value="isOnSameTeam")
    public abstract boolean m_7307_(Entity var1);

    @Shadow
    @RemapForJS(value="getHorizontalFacing")
    public abstract Direction m_6350_();

    @Shadow
    @RemapForJS(value="extinguish")
    public abstract void m_20095_();

    @Shadow
    @RemapForJS(value="attack")
    public abstract boolean m_6469_(DamageSource var1, float var2);

    @Shadow
    @RemapForJS(value="getDistanceSq")
    public abstract double m_20275_(double var1, double var3, double var5);

    @Shadow
    @RemapForJS(value="getEntityType")
    public abstract EntityType<?> m_6095_();

    @Shadow
    @RemapForJS(value="distanceToEntitySqr")
    public abstract double m_20280_(Entity var1);

    @Shadow
    @RemapForJS(value="distanceToEntity")
    public abstract float m_20270_(Entity var1);

    @Shadow
    @HideFromJS
    public abstract Level m_9236_();
}

