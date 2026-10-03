/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  net.minecraft.core.Holder$Reference
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.material.Fluid
 *  net.minecraft.world.level.material.Fluids
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jetbrains.annotations.NotNull
 */
package net.minecraftforge.fluids;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class FluidStack {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final FluidStack EMPTY = new FluidStack(Fluids.f_76191_, 0);
    public static final Codec<FluidStack> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)BuiltInRegistries.f_257020_.m_194605_().fieldOf("FluidName").forGetter(FluidStack::getFluid), (App)Codec.INT.fieldOf("Amount").forGetter(FluidStack::getAmount), (App)CompoundTag.f_128325_.optionalFieldOf("Tag").forGetter(stack -> Optional.ofNullable(stack.getTag()))).apply((Applicative)instance, (fluid, amount, tag) -> {
        FluidStack stack = new FluidStack((Fluid)fluid, (int)amount);
        tag.ifPresent(stack::setTag);
        return stack;
    }));
    private boolean isEmpty;
    private int amount;
    private CompoundTag tag;
    private Holder.Reference<Fluid> fluidDelegate;

    public FluidStack(Fluid fluid, int amount) {
        if (fluid == null) {
            LOGGER.fatal("Null fluid supplied to fluidstack. Did you try and create a stack for an unregistered fluid?");
            throw new IllegalArgumentException("Cannot create a fluidstack from a null fluid");
        }
        if (ForgeRegistries.FLUIDS.getKey(fluid) == null) {
            LOGGER.fatal("Failed attempt to create a FluidStack for an unregistered Fluid {} (type {})", (Object)ForgeRegistries.FLUIDS.getKey(fluid), (Object)fluid.getClass().getName());
            throw new IllegalArgumentException("Cannot create a fluidstack from an unregistered fluid");
        }
        this.fluidDelegate = ForgeRegistries.FLUIDS.getDelegateOrThrow(fluid);
        this.amount = amount;
        this.updateEmpty();
    }

    public FluidStack(Fluid fluid, int amount, CompoundTag nbt) {
        this(fluid, amount);
        if (nbt != null) {
            this.tag = nbt.m_6426_();
        }
    }

    public FluidStack(FluidStack stack, int amount) {
        this(stack.getFluid(), amount, stack.tag);
    }

    public static FluidStack loadFluidStackFromNBT(CompoundTag nbt) {
        if (nbt == null) {
            return EMPTY;
        }
        if (!nbt.m_128425_("FluidName", 8)) {
            return EMPTY;
        }
        ResourceLocation fluidName = new ResourceLocation(nbt.m_128461_("FluidName"));
        Fluid fluid = ForgeRegistries.FLUIDS.getValue(fluidName);
        if (fluid == null) {
            return EMPTY;
        }
        FluidStack stack = new FluidStack(fluid, nbt.m_128451_("Amount"));
        if (nbt.m_128425_("Tag", 10)) {
            stack.tag = nbt.m_128469_("Tag");
        }
        return stack;
    }

    public CompoundTag writeToNBT(CompoundTag nbt) {
        nbt.m_128359_("FluidName", ForgeRegistries.FLUIDS.getKey(this.getFluid()).toString());
        nbt.m_128405_("Amount", this.amount);
        if (this.tag != null) {
            nbt.m_128365_("Tag", (Tag)this.tag);
        }
        return nbt;
    }

    public void writeToPacket(FriendlyByteBuf buf) {
        buf.writeRegistryId(ForgeRegistries.FLUIDS, (Object)this.getFluid());
        buf.m_130130_(this.getAmount());
        buf.m_130079_(this.tag);
    }

    public static FluidStack readFromPacket(FriendlyByteBuf buf) {
        Fluid fluid = (Fluid)buf.readRegistryId();
        int amount = buf.m_130242_();
        CompoundTag tag = buf.m_130260_();
        if (fluid == Fluids.f_76191_) {
            return EMPTY;
        }
        return new FluidStack(fluid, amount, tag);
    }

    public final Fluid getFluid() {
        return this.isEmpty ? Fluids.f_76191_ : (Fluid)this.fluidDelegate.get();
    }

    public final Fluid getRawFluid() {
        return (Fluid)this.fluidDelegate.get();
    }

    public boolean isEmpty() {
        return this.isEmpty;
    }

    protected void updateEmpty() {
        this.isEmpty = this.getRawFluid() == Fluids.f_76191_ || this.amount <= 0;
    }

    public int getAmount() {
        return this.isEmpty ? 0 : this.amount;
    }

    public void setAmount(int amount) {
        if (this.getRawFluid() == Fluids.f_76191_) {
            throw new IllegalStateException("Can't modify the empty stack.");
        }
        this.amount = amount;
        this.updateEmpty();
    }

    public void grow(int amount) {
        this.setAmount(this.amount + amount);
    }

    public void shrink(int amount) {
        this.setAmount(this.amount - amount);
    }

    public boolean hasTag() {
        return this.tag != null;
    }

    public CompoundTag getTag() {
        return this.tag;
    }

    public void setTag(CompoundTag tag) {
        if (this.getRawFluid() == Fluids.f_76191_) {
            throw new IllegalStateException("Can't modify the empty stack.");
        }
        this.tag = tag;
    }

    public CompoundTag getOrCreateTag() {
        if (this.tag == null) {
            this.setTag(new CompoundTag());
        }
        return this.tag;
    }

    public CompoundTag getChildTag(String childName) {
        if (this.tag == null) {
            return null;
        }
        return this.tag.m_128469_(childName);
    }

    public CompoundTag getOrCreateChildTag(String childName) {
        this.getOrCreateTag();
        CompoundTag child = this.tag.m_128469_(childName);
        if (!this.tag.m_128425_(childName, 10)) {
            this.tag.m_128365_(childName, (Tag)child);
        }
        return child;
    }

    public void removeChildTag(String childName) {
        if (this.tag != null) {
            this.tag.m_128473_(childName);
        }
    }

    public Component getDisplayName() {
        return this.getFluid().getFluidType().getDescription(this);
    }

    public String getTranslationKey() {
        return this.getFluid().getFluidType().getDescriptionId(this);
    }

    public FluidStack copy() {
        return new FluidStack(this.getFluid(), this.amount, this.tag);
    }

    public boolean isFluidEqual(@NotNull FluidStack other) {
        return this.getFluid() == other.getFluid() && this.isFluidStackTagEqual(other);
    }

    private boolean isFluidStackTagEqual(FluidStack other) {
        return this.tag == null ? other.tag == null : other.tag != null && this.tag.equals((Object)other.tag);
    }

    public static boolean areFluidStackTagsEqual(@NotNull FluidStack stack1, @NotNull FluidStack stack2) {
        return stack1.isFluidStackTagEqual(stack2);
    }

    public boolean containsFluid(@NotNull FluidStack other) {
        return this.isFluidEqual(other) && this.amount >= other.amount;
    }

    public boolean isFluidStackIdentical(FluidStack other) {
        return this.isFluidEqual(other) && this.amount == other.amount;
    }

    public boolean isFluidEqual(@NotNull ItemStack other) {
        return FluidUtil.getFluidContained(other).map(this::isFluidEqual).orElse(false);
    }

    public final int hashCode() {
        int code = 1;
        code = 31 * code + this.getFluid().hashCode();
        if (this.tag != null) {
            code = 31 * code + this.tag.hashCode();
        }
        return code;
    }

    public final boolean equals(Object o) {
        if (!(o instanceof FluidStack)) {
            return false;
        }
        return this.isFluidEqual((FluidStack)o);
    }
}

