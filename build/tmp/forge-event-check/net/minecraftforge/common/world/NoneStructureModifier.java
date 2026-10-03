/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.minecraft.core.Holder
 *  net.minecraft.world.level.levelgen.structure.Structure
 */
package net.minecraftforge.common.world;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.world.ModifiableStructureInfo;
import net.minecraftforge.common.world.StructureModifier;

public class NoneStructureModifier
implements StructureModifier {
    public static final NoneStructureModifier INSTANCE = new NoneStructureModifier();

    @Override
    public void modify(Holder<Structure> structure, StructureModifier.Phase phase, ModifiableStructureInfo.StructureInfo.Builder builder) {
    }

    @Override
    public Codec<? extends StructureModifier> codec() {
        return ForgeMod.NONE_STRUCTURE_MODIFIER_TYPE.get();
    }
}

