/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.level.material.Fluid
 */
package dev.latvian.mods.kubejs.fluid;

import dev.latvian.mods.kubejs.fluid.EmptyFluidStackJS;
import dev.latvian.mods.kubejs.fluid.FluidStackJS;
import dev.latvian.mods.kubejs.fluid.UnboundFluidStackJS;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;

public class FluidWrapper {
    public static final ResourceLocation WATER_ID = new ResourceLocation("minecraft:water");
    public static final ResourceLocation LAVA_ID = new ResourceLocation("minecraft:lava");

    public static FluidStackJS of(FluidStackJS o) {
        return FluidStackJS.of(o);
    }

    public static FluidStackJS of(FluidStackJS o, int amount) {
        o.setAmount(amount);
        return o;
    }

    public static FluidStackJS of(FluidStackJS o, CompoundTag nbt) {
        o.setNbt(nbt);
        return o;
    }

    public static FluidStackJS of(FluidStackJS o, int amount, CompoundTag nbt) {
        o.setAmount(amount);
        o.setNbt(nbt);
        return o;
    }

    public static FluidStackJS water() {
        return new UnboundFluidStackJS(WATER_ID);
    }

    public static FluidStackJS lava() {
        return new UnboundFluidStackJS(LAVA_ID);
    }

    public static FluidStackJS water(int amount) {
        UnboundFluidStackJS fs = new UnboundFluidStackJS(WATER_ID);
        ((FluidStackJS)fs).setAmount(amount);
        return fs;
    }

    public static FluidStackJS lava(int amount) {
        UnboundFluidStackJS fs = new UnboundFluidStackJS(LAVA_ID);
        ((FluidStackJS)fs).setAmount(amount);
        return fs;
    }

    public static Fluid getType(ResourceLocation id) {
        return RegistryInfo.FLUID.getValue(id);
    }

    public static List<String> getTypes() {
        ArrayList<String> types = new ArrayList<String>();
        for (Map.Entry<ResourceKey<Fluid>, Fluid> entry : RegistryInfo.FLUID.entrySet()) {
            types.add(entry.getKey().m_135782_().toString());
        }
        return types;
    }

    public static FluidStackJS getEmpty() {
        return EmptyFluidStackJS.INSTANCE;
    }

    public static boolean exists(ResourceLocation id) {
        return RegistryInfo.FLUID.hasValue(id);
    }

    public static ResourceLocation getId(Fluid fluid) {
        return RegistryInfo.FLUID.getId(fluid);
    }
}

