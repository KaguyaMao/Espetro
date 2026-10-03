/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.minecraft.core.Holder
 *  net.minecraft.world.level.biome.Biome
 */
package net.minecraftforge.common.world;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ModifiableBiomeInfo;

public class NoneBiomeModifier
implements BiomeModifier {
    public static final NoneBiomeModifier INSTANCE = new NoneBiomeModifier();

    @Override
    public void modify(Holder<Biome> biome, BiomeModifier.Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
    }

    @Override
    public Codec<? extends BiomeModifier> codec() {
        return ForgeMod.NONE_BIOME_MODIFIER_TYPE.get();
    }
}

