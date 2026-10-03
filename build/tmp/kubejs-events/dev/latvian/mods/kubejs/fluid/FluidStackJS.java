/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  dev.architectury.fluid.FluidStack
 *  dev.latvian.mods.rhino.mod.util.NBTUtils
 *  dev.latvian.mods.rhino.util.HideFromJS
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.level.material.Fluid
 *  net.minecraft.world.level.material.Fluids
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.fluid;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.architectury.fluid.FluidStack;
import dev.latvian.mods.kubejs.fluid.BoundFluidStackJS;
import dev.latvian.mods.kubejs.fluid.EmptyFluidStackJS;
import dev.latvian.mods.kubejs.fluid.FluidLike;
import dev.latvian.mods.kubejs.fluid.InputFluid;
import dev.latvian.mods.kubejs.fluid.OutputFluid;
import dev.latvian.mods.kubejs.fluid.UnboundFluidStackJS;
import dev.latvian.mods.kubejs.item.ingredient.TagContext;
import dev.latvian.mods.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.util.MapJS;
import dev.latvian.mods.kubejs.util.Tags;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.kubejs.util.WrappedJS;
import dev.latvian.mods.rhino.mod.util.NBTUtils;
import dev.latvian.mods.rhino.util.HideFromJS;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

public abstract class FluidStackJS
implements WrappedJS,
InputFluid,
OutputFluid {
    private double chance = Double.NaN;

    public static FluidStackJS of(@Nullable Object o) {
        if (o == null) {
            return EmptyFluidStackJS.INSTANCE;
        }
        if (o instanceof FluidStackJS) {
            FluidStackJS js = (FluidStackJS)o;
            return js;
        }
        if (o instanceof FluidStack) {
            FluidStack fluidStack = (FluidStack)o;
            return new BoundFluidStackJS(fluidStack);
        }
        if (o instanceof Fluid) {
            Fluid fluid = (Fluid)o;
            UnboundFluidStackJS f = new UnboundFluidStackJS(RegistryInfo.FLUID.getId(fluid));
            return f.kjs$isEmpty() ? EmptyFluidStackJS.INSTANCE : f;
        }
        if (o instanceof JsonElement) {
            JsonElement json = (JsonElement)o;
            return FluidStackJS.fromJson(json);
        }
        if (o instanceof CharSequence || o instanceof ResourceLocation) {
            String s = o.toString();
            if (s.isEmpty() || s.equals("-") || s.equals("empty") || s.equals("minecraft:empty")) {
                return EmptyFluidStackJS.INSTANCE;
            }
            String[] s1 = s.split(" ", 2);
            return new UnboundFluidStackJS(new ResourceLocation(s1[0])).withAmount(UtilsJS.parseLong(s1.length == 2 ? s1[1] : "", FluidStack.bucketAmount()));
        }
        Map<?, ?> map = MapJS.of(o);
        if (map != null && map.containsKey("fluid")) {
            UnboundFluidStackJS stack = new UnboundFluidStackJS(new ResourceLocation(map.get("fluid").toString()));
            Object obj = map.get("amount");
            if (obj instanceof Number) {
                Number num = (Number)obj;
                ((FluidStackJS)stack).setAmount(num.longValue());
            }
            if (map.containsKey("nbt")) {
                ((FluidStackJS)stack).setNbt(NBTUtils.toTagCompound(map.get("nbt")));
            }
            return stack;
        }
        return EmptyFluidStackJS.INSTANCE;
    }

    public static FluidStackJS of(@Nullable Object o, long amount, @Nullable CompoundTag nbt) {
        FluidStackJS stack = FluidStackJS.of(o);
        stack.setAmount(amount);
        stack.setNbt(nbt);
        return stack;
    }

    public static FluidStackJS fromJson(JsonElement e) {
        if (!e.isJsonObject()) {
            return FluidStackJS.of(e.getAsString());
        }
        JsonObject json = e.getAsJsonObject();
        FluidStackJS fluid = FluidStackJS.of(json.get("fluid").getAsString());
        if (fluid.kjs$isEmpty()) {
            throw new RecipeExceptionJS(String.valueOf(json) + " is not a valid fluid!");
        }
        long amount = FluidStack.bucketAmount();
        CompoundTag nbt = null;
        if (json.has("amount")) {
            amount = json.get("amount").getAsInt();
        } else if (json.has("count")) {
            amount = json.get("count").getAsInt();
        }
        if (json.has("nbt")) {
            nbt = json.get("nbt").isJsonObject() ? NBTUtils.toTagCompound((Object)json.get("nbt")) : NBTUtils.toTagCompound((Object)json.get("nbt").getAsString());
        }
        return FluidStackJS.of(fluid, amount, nbt);
    }

    public abstract String getId();

    public Collection<ResourceLocation> getTags() {
        return Tags.byFluid(this.getFluid()).map(TagKey::f_203868_).collect(Collectors.toSet());
    }

    public boolean hasTag(ResourceLocation tag) {
        return ((TagContext)TagContext.INSTANCE.getValue()).contains(Tags.fluid(tag), this.getFluid());
    }

    public Fluid getFluid() {
        Fluid f = RegistryInfo.FLUID.getValue(new ResourceLocation(this.getId()));
        return f == null ? Fluids.f_76191_ : f;
    }

    public abstract FluidStack getFluidStack();

    @Override
    public abstract long kjs$getAmount();

    @HideFromJS
    public final long getAmount() {
        return this.kjs$getAmount();
    }

    public abstract void setAmount(long var1);

    public final FluidStackJS withAmount(long amount) {
        if (amount <= 0L) {
            return EmptyFluidStackJS.INSTANCE;
        }
        FluidStackJS fs = this.copy();
        fs.setAmount(amount);
        return fs;
    }

    @Nullable
    public abstract CompoundTag getNbt();

    public abstract void setNbt(@Nullable CompoundTag var1);

    public final FluidStackJS withNBT(@Nullable CompoundTag nbt) {
        FluidStackJS fs = this.copy();
        fs.setNbt(nbt);
        return fs;
    }

    public FluidStackJS copy() {
        return this.kjs$copy(this.kjs$getAmount());
    }

    @Override
    public abstract FluidStackJS kjs$copy(long var1);

    public boolean hasChance() {
        return !Double.isNaN(this.chance);
    }

    public void removeChance() {
        this.setChance(Double.NaN);
    }

    public void setChance(double c) {
        this.chance = c;
    }

    public double getChance() {
        return this.chance;
    }

    public final FluidStackJS withChance(double c) {
        if (Double.isNaN(this.chance) && Double.isNaN(c) || this.chance == c) {
            return this;
        }
        FluidStackJS is = this.copy();
        is.setChance(c);
        return is;
    }

    public int hashCode() {
        return Objects.hash(this.getFluid(), this.getNbt());
    }

    public boolean equals(Object o) {
        if (o instanceof CharSequence) {
            return this.getId().equals(o.toString());
        }
        FluidStackJS f = FluidStackJS.of(o);
        if (f.kjs$isEmpty()) {
            return false;
        }
        return this.getFluid() == f.getFluid() && Objects.equals(this.getNbt(), f.getNbt());
    }

    public boolean strongEquals(Object o) {
        FluidStackJS f = FluidStackJS.of(o);
        if (f.kjs$isEmpty()) {
            return false;
        }
        return this.kjs$getAmount() == f.kjs$getAmount() && this.getFluid() == f.getFluid() && Objects.equals(this.getNbt(), f.getNbt());
    }

    public String toString() {
        long amount = this.kjs$getAmount();
        CompoundTag nbt = this.getNbt();
        StringBuilder builder = new StringBuilder();
        builder.append("Fluid.of('");
        builder.append(this.getId());
        if (amount != FluidStack.bucketAmount()) {
            builder.append(", ");
            builder.append(amount);
        }
        if (nbt != null) {
            builder.append(", ");
            NBTUtils.quoteAndEscapeForJS((StringBuilder)builder, (String)nbt.toString());
        }
        builder.append("')");
        if (this.hasChance()) {
            builder.append(".withChance(");
            builder.append(this.getChance());
            builder.append(')');
        }
        return builder.toString();
    }

    public JsonObject toJson() {
        JsonObject o = new JsonObject();
        o.addProperty("fluid", this.getId());
        o.addProperty("amount", (Number)this.kjs$getAmount());
        if (this.getNbt() != null) {
            o.addProperty("nbt", this.getNbt().toString());
        }
        if (this.hasChance()) {
            o.addProperty("chance", (Number)this.getChance());
        }
        return o;
    }

    public CompoundTag toNBT() {
        CompoundTag tag = new CompoundTag();
        this.getFluidStack().write(tag);
        return tag;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public boolean matches(FluidLike other) {
        if (!(other instanceof FluidStackJS)) return false;
        FluidStackJS fs = (FluidStackJS)other;
        if (this.getFluid() != fs.getFluid()) return false;
        if (!Objects.equals(this.getNbt(), fs.getNbt())) return false;
        return true;
    }
}

