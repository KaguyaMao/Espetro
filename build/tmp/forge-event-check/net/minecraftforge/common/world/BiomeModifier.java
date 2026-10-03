/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  net.minecraft.core.Holder
 *  net.minecraft.core.HolderSet
 *  net.minecraft.core.RegistryCodecs
 *  net.minecraft.resources.RegistryFileCodec
 *  net.minecraft.util.ExtraCodecs
 *  net.minecraft.world.level.biome.Biome
 */
package net.minecraftforge.common.world;

import com.mojang.serialization.Codec;
import java.util.function.Function;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.common.world.ModifiableBiomeInfo;
import net.minecraftforge.registries.ForgeRegistries;

public interface BiomeModifier {
    public static final Codec<BiomeModifier> DIRECT_CODEC = ExtraCodecs.m_184415_(() -> ForgeRegistries.BIOME_MODIFIER_SERIALIZERS.get().getCodec()).dispatch(BiomeModifier::codec, Function.identity());
    public static final Codec<Holder<BiomeModifier>> REFERENCE_CODEC = RegistryFileCodec.m_135589_(ForgeRegistries.Keys.BIOME_MODIFIERS, DIRECT_CODEC);
    public static final Codec<HolderSet<BiomeModifier>> LIST_CODEC = RegistryCodecs.m_206279_(ForgeRegistries.Keys.BIOME_MODIFIERS, DIRECT_CODEC);

    public void modify(Holder<Biome> var1, Phase var2, ModifiableBiomeInfo.BiomeInfo.Builder var3);

    public Codec<? extends BiomeModifier> codec();

    public static enum Phase {
        BEFORE_EVERYTHING,
        ADD,
        REMOVE,
        MODIFY,
        AFTER_EVERYTHING;

    }
}

