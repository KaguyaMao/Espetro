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
 *  net.minecraft.world.level.levelgen.structure.Structure
 */
package net.minecraftforge.common.world;

import com.mojang.serialization.Codec;
import java.util.function.Function;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraftforge.common.world.ModifiableStructureInfo;
import net.minecraftforge.registries.ForgeRegistries;

public interface StructureModifier {
    public static final Codec<StructureModifier> DIRECT_CODEC = ExtraCodecs.m_184415_(() -> ForgeRegistries.STRUCTURE_MODIFIER_SERIALIZERS.get().getCodec()).dispatch(StructureModifier::codec, Function.identity());
    public static final Codec<Holder<StructureModifier>> REFERENCE_CODEC = RegistryFileCodec.m_135589_(ForgeRegistries.Keys.STRUCTURE_MODIFIERS, DIRECT_CODEC);
    public static final Codec<HolderSet<StructureModifier>> LIST_CODEC = RegistryCodecs.m_206279_(ForgeRegistries.Keys.STRUCTURE_MODIFIERS, DIRECT_CODEC);

    public void modify(Holder<Structure> var1, Phase var2, ModifiableStructureInfo.StructureInfo.Builder var3);

    public Codec<? extends StructureModifier> codec();

    public static enum Phase {
        BEFORE_EVERYTHING,
        ADD,
        REMOVE,
        MODIFY,
        AFTER_EVERYTHING;

    }
}

