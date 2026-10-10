/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.architectury.fluid.FluidStack
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.level.material.Fluids
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.fluid;

import dev.architectury.fluid.FluidStack;
import dev.latvian.mods.kubejs.fluid.FluidStackJS;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

public class UnboundFluidStackJS
extends FluidStackJS {
    private final ResourceLocation fluidRL;
    private final String fluid;
    private long amount;
    private CompoundTag nbt;
    private FluidStack cached;

    public UnboundFluidStackJS(ResourceLocation f) {
        this.fluidRL = f;
        this.fluid = this.fluidRL.toString();
        this.amount = FluidStack.bucketAmount();
        this.nbt = null;
        this.cached = null;
    }

    @Override
    public String getId() {
        return this.fluid;
    }

    @Override
    public boolean kjs$isEmpty() {
        return super.kjs$isEmpty() || this.getFluid() == Fluids.f_76191_;
    }

    @Override
    public FluidStack getFluidStack() {
        if (this.cached == null) {
            this.cached = FluidStack.create(this::getFluid, (long)this.amount, (CompoundTag)this.nbt);
        }
        return this.cached;
    }

    @Override
    public long kjs$getAmount() {
        return this.amount;
    }

    @Override
    public void setAmount(long a) {
        this.amount = a;
        this.cached = null;
    }

    @Override
    @Nullable
    public CompoundTag getNbt() {
        return this.nbt;
    }

    @Override
    public void setNbt(@Nullable CompoundTag n) {
        this.nbt = n;
        this.cached = null;
    }

    @Override
    public FluidStackJS kjs$copy(long amount) {
        UnboundFluidStackJS fs = new UnboundFluidStackJS(this.fluidRL);
        fs.amount = amount;
        fs.nbt = this.nbt == null ? null : this.nbt.m_6426_();
        return fs;
    }
}

