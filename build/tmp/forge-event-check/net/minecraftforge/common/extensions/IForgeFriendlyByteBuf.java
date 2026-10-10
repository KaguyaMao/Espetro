/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.resources.ResourceLocation
 *  org.jetbrains.annotations.NotNull
 */
package net.minecraftforge.common.extensions;

import com.google.common.base.Preconditions;
import java.util.Objects;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryManager;
import org.jetbrains.annotations.NotNull;

public interface IForgeFriendlyByteBuf {
    private FriendlyByteBuf self() {
        return (FriendlyByteBuf)this;
    }

    default public <T> void writeRegistryIdUnsafe(@NotNull IForgeRegistry<T> registry, @NotNull T entry) {
        ForgeRegistry forgeRegistry = (ForgeRegistry)registry;
        int id = forgeRegistry.getID(entry);
        this.self().m_130130_(id);
    }

    default public void writeRegistryIdUnsafe(@NotNull IForgeRegistry<?> registry, @NotNull ResourceLocation entryKey) {
        ForgeRegistry forgeRegistry = (ForgeRegistry)registry;
        int id = forgeRegistry.getID(entryKey);
        this.self().m_130130_(id);
    }

    default public <T> T readRegistryIdUnsafe(@NotNull IForgeRegistry<T> registry) {
        ForgeRegistry forgeRegistry = (ForgeRegistry)registry;
        int id = this.self().m_130242_();
        return (T)forgeRegistry.getValue(id);
    }

    default public <T> void writeRegistryId(@NotNull IForgeRegistry<T> registry, @NotNull T entry) {
        Objects.requireNonNull(registry, "Cannot write a null registry key!");
        Objects.requireNonNull(entry, "Cannot write a null registry entry!");
        ResourceLocation name = registry.getRegistryName();
        Preconditions.checkArgument((boolean)registry.containsValue(entry), (String)"Cannot find %s in %s", registry.getKey(entry) != null ? registry.getKey(entry) : entry, (Object)name);
        ForgeRegistry reg = (ForgeRegistry)registry;
        this.self().m_130085_(name);
        this.self().m_130130_(reg.getID(entry));
    }

    default public <T> T readRegistryId() {
        ResourceLocation location = this.self().m_130281_();
        ForgeRegistry registry = RegistryManager.ACTIVE.getRegistry(location);
        return (T)registry.getValue(this.self().m_130242_());
    }

    default public <T> T readRegistryIdSafe(Class<? super T> registrySuperType) {
        T value = this.readRegistryId();
        if (!registrySuperType.isAssignableFrom(value.getClass())) {
            throw new IllegalArgumentException("Attempted to read an registryValue of the wrong type from the Buffer!");
        }
        return value;
    }

    default public void writeFluidStack(FluidStack stack) {
        if (stack.isEmpty()) {
            this.self().writeBoolean(false);
        } else {
            this.self().writeBoolean(true);
            stack.writeToPacket(this.self());
        }
    }

    default public FluidStack readFluidStack() {
        return !this.self().readBoolean() ? FluidStack.EMPTY : FluidStack.readFromPacket(this.self());
    }
}

