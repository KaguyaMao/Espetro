/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  dev.latvian.mods.rhino.util.RemapPrefixForJS
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.SoundType
 *  net.minecraft.world.level.block.state.BlockState
 */
package dev.latvian.mods.kubejs.core;

import com.google.common.collect.ImmutableList;
import dev.latvian.mods.kubejs.block.BlockBuilder;
import dev.latvian.mods.kubejs.block.RandomTickCallbackJS;
import dev.latvian.mods.kubejs.core.BlockBuilderProvider;
import dev.latvian.mods.kubejs.core.NoMixinException;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;

@RemapPrefixForJS(value="kjs$")
public interface BlockKJS
extends BlockBuilderProvider {
    default public void kjs$setBlockBuilder(BlockBuilder b) {
        throw new NoMixinException();
    }

    default public ResourceLocation kjs$getIdLocation() {
        return UtilsJS.UNKNOWN_ID;
    }

    default public String kjs$getId() {
        return this.kjs$getIdLocation().toString();
    }

    default public String kjs$getMod() {
        return this.kjs$getIdLocation().m_135827_();
    }

    default public CompoundTag kjs$getTypeData() {
        throw new NoMixinException();
    }

    default public void kjs$setHasCollision(boolean v) {
        throw new NoMixinException();
    }

    default public void kjs$setExplosionResistance(float v) {
        throw new NoMixinException();
    }

    default public void kjs$setIsRandomlyTicking(boolean v) {
        throw new NoMixinException();
    }

    default public void kjs$setRandomTickCallback(Consumer<RandomTickCallbackJS> callback) {
        throw new NoMixinException();
    }

    default public void kjs$setSoundType(SoundType v) {
        throw new NoMixinException();
    }

    default public void kjs$setFriction(float v) {
        throw new NoMixinException();
    }

    default public void kjs$setSpeedFactor(float v) {
        throw new NoMixinException();
    }

    default public void kjs$setJumpFactor(float v) {
        throw new NoMixinException();
    }

    default public void kjs$setNameKey(String key) {
        throw new NoMixinException();
    }

    default public void kjs$setDestroySpeed(float v) {
        for (BlockState state : this.kjs$getBlockStates()) {
            state.kjs$setDestroySpeed(v);
        }
    }

    default public void kjs$setLightEmission(int v) {
        for (BlockState state : this.kjs$getBlockStates()) {
            state.kjs$setLightEmission(v);
        }
    }

    default public void kjs$setRequiresTool(boolean v) {
        for (BlockState state : this.kjs$getBlockStates()) {
            state.kjs$setRequiresTool(v);
        }
    }

    default public List<BlockState> kjs$getBlockStates() {
        ImmutableList immutableList;
        BlockKJS blockKJS = this;
        if (blockKJS instanceof Block) {
            Block block = (Block)blockKJS;
            immutableList = block.m_49965_().m_61056_();
        } else {
            immutableList = List.of();
        }
        return immutableList;
    }
}

