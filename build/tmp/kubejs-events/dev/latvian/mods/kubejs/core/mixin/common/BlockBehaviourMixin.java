/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.latvian.mods.rhino.util.RemapPrefixForJS
 *  net.minecraft.core.BlockPos
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.SoundType
 *  net.minecraft.world.level.block.state.BlockBehaviour
 *  net.minecraft.world.level.block.state.BlockState
 *  org.jetbrains.annotations.Nullable
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Mutable
 *  org.spongepowered.asm.mixin.gen.Accessor
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package dev.latvian.mods.kubejs.core.mixin.common;

import dev.latvian.mods.kubejs.block.BlockBuilder;
import dev.latvian.mods.kubejs.block.RandomTickCallbackJS;
import dev.latvian.mods.kubejs.core.BlockKJS;
import dev.latvian.mods.kubejs.level.BlockContainerJS;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@RemapPrefixForJS(value="kjs$")
@Mixin(value={BlockBehaviour.class})
public abstract class BlockBehaviourMixin
implements BlockKJS {
    private BlockBuilder kjs$blockBuilder;
    private CompoundTag kjs$typeData;
    private ResourceLocation kjs$id;
    private String kjs$idString;
    private Consumer<RandomTickCallbackJS> kjs$randomTickCallback;

    @Override
    public ResourceLocation kjs$getIdLocation() {
        if (this.kjs$id == null) {
            Block block;
            ResourceLocation id;
            BlockBehaviourMixin blockBehaviourMixin = this;
            this.kjs$id = blockBehaviourMixin instanceof Block ? ((id = RegistryInfo.BLOCK.getId(block = (Block)blockBehaviourMixin)) == null ? UtilsJS.UNKNOWN_ID : id) : UtilsJS.UNKNOWN_ID;
        }
        return this.kjs$id;
    }

    @Override
    public String kjs$getId() {
        if (this.kjs$idString == null) {
            this.kjs$idString = this.kjs$getIdLocation().toString();
        }
        return this.kjs$idString;
    }

    @Override
    @Nullable
    public BlockBuilder kjs$getBlockBuilder() {
        return this.kjs$blockBuilder;
    }

    @Override
    public void kjs$setBlockBuilder(BlockBuilder b) {
        this.kjs$blockBuilder = b;
    }

    @Override
    public CompoundTag kjs$getTypeData() {
        if (this.kjs$typeData == null) {
            this.kjs$typeData = new CompoundTag();
        }
        return this.kjs$typeData;
    }

    @Override
    public void kjs$setRandomTickCallback(Consumer<RandomTickCallbackJS> callback) {
        this.kjs$setIsRandomlyTicking(true);
        this.kjs$randomTickCallback = callback;
    }

    @Inject(method={"randomTick"}, at={@At(value="HEAD")}, cancellable=true)
    private void onRandomTick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource randomSource, CallbackInfo ci) {
        if (this.kjs$randomTickCallback != null) {
            this.kjs$randomTickCallback.accept(new RandomTickCallbackJS(new BlockContainerJS((Level)serverLevel, blockPos), randomSource));
            ci.cancel();
        }
    }

    @Override
    @Accessor(value="hasCollision")
    @Mutable
    public abstract void kjs$setHasCollision(boolean var1);

    @Override
    @Accessor(value="explosionResistance")
    @Mutable
    public abstract void kjs$setExplosionResistance(float var1);

    @Override
    @Accessor(value="isRandomlyTicking")
    @Mutable
    public abstract void kjs$setIsRandomlyTicking(boolean var1);

    @Override
    @Accessor(value="soundType")
    @Mutable
    public abstract void kjs$setSoundType(SoundType var1);

    @Override
    @Accessor(value="friction")
    @Mutable
    public abstract void kjs$setFriction(float var1);

    @Override
    @Accessor(value="speedFactor")
    @Mutable
    public abstract void kjs$setSpeedFactor(float var1);

    @Override
    @Accessor(value="jumpFactor")
    @Mutable
    public abstract void kjs$setJumpFactor(float var1);
}

