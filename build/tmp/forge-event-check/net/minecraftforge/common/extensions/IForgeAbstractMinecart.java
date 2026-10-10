/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.tags.BlockTags
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.vehicle.AbstractMinecart
 *  net.minecraft.world.entity.vehicle.AbstractMinecart$Type
 */
package net.minecraftforge.common.extensions;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.AbstractMinecart;

public interface IForgeAbstractMinecart {
    public static final float DEFAULT_MAX_SPEED_AIR_LATERAL = 0.4f;
    public static final float DEFAULT_MAX_SPEED_AIR_VERTICAL = -1.0f;
    public static final double DEFAULT_AIR_DRAG = (double)0.95f;

    private AbstractMinecart self() {
        return (AbstractMinecart)this;
    }

    default public BlockPos getCurrentRailPosition() {
        int x = Mth.m_14107_((double)this.self().m_20185_());
        int y = Mth.m_14107_((double)this.self().m_20186_());
        int z = Mth.m_14107_((double)this.self().m_20189_());
        BlockPos pos = new BlockPos(x, y, z);
        if (this.self().m_9236_().m_8055_(pos.m_7495_()).m_204336_(BlockTags.f_13034_)) {
            pos = pos.m_7495_();
        }
        return pos;
    }

    public double getMaxSpeedWithRail();

    public void moveMinecartOnRail(BlockPos var1);

    public boolean canUseRail();

    public void setCanUseRail(boolean var1);

    default public boolean shouldDoRailFunctions() {
        return true;
    }

    default public boolean isPoweredCart() {
        return this.self().m_6064_() == AbstractMinecart.Type.FURNACE;
    }

    default public boolean canBeRidden() {
        return this.self().m_6064_() == AbstractMinecart.Type.RIDEABLE;
    }

    default public float getMaxCartSpeedOnRail() {
        return 1.2f;
    }

    public float getCurrentCartSpeedCapOnRail();

    public void setCurrentCartSpeedCapOnRail(float var1);

    public float getMaxSpeedAirLateral();

    public void setMaxSpeedAirLateral(float var1);

    public float getMaxSpeedAirVertical();

    public void setMaxSpeedAirVertical(float var1);

    public double getDragAir();

    public void setDragAir(double var1);

    default public double getSlopeAdjustment() {
        return 0.0078125;
    }

    default public int getComparatorLevel() {
        return -1;
    }
}

