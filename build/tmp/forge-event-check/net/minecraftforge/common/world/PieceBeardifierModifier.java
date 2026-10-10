/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.levelgen.structure.BoundingBox
 *  net.minecraft.world.level.levelgen.structure.TerrainAdjustment
 */
package net.minecraftforge.common.world;

import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;

public interface PieceBeardifierModifier {
    public BoundingBox getBeardifierBox();

    public TerrainAdjustment getTerrainAdjustment();

    public int getGroundLevelDelta();
}

