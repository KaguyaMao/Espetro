/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.ParametersAreNonnullByDefault
 *  net.minecraft.MethodsReturnNonnullByDefault
 *  net.minecraft.core.Direction
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.resources.ResourceLocation
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.common.capabilities;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.Nullable;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class CapabilityDispatcher
implements INBTSerializable<CompoundTag>,
ICapabilityProvider {
    private final ICapabilityProvider[] caps;
    private final INBTSerializable<Tag>[] writers;
    private final String[] names;
    private final List<Runnable> listeners;

    public CapabilityDispatcher(Map<ResourceLocation, ICapabilityProvider> list, List<Runnable> listeners) {
        this(list, listeners, null);
    }

    public CapabilityDispatcher(Map<ResourceLocation, ICapabilityProvider> list, List<Runnable> listeners, @Nullable ICapabilityProvider parent) {
        ArrayList<ICapabilityProvider> lstCaps = new ArrayList<ICapabilityProvider>();
        ArrayList<INBTSerializable> lstWriters = new ArrayList<INBTSerializable>();
        ArrayList<String> lstNames = new ArrayList<String>();
        this.listeners = listeners;
        if (parent != null) {
            lstCaps.add(parent);
            if (parent instanceof INBTSerializable) {
                lstWriters.add((INBTSerializable)((Object)parent));
                lstNames.add("Parent");
            }
        }
        for (Map.Entry<ResourceLocation, ICapabilityProvider> entry : list.entrySet()) {
            ICapabilityProvider prov = entry.getValue();
            lstCaps.add(prov);
            if (!(prov instanceof INBTSerializable)) continue;
            lstWriters.add((INBTSerializable)((Object)prov));
            lstNames.add(entry.getKey().toString());
        }
        this.caps = lstCaps.toArray(new ICapabilityProvider[lstCaps.size()]);
        this.writers = lstWriters.toArray(new INBTSerializable[lstWriters.size()]);
        this.names = lstNames.toArray(new String[lstNames.size()]);
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        for (ICapabilityProvider c : this.caps) {
            LazyOptional<T> ret = c.getCapability(cap, side);
            if (ret == null) {
                throw new RuntimeException(String.format(Locale.ENGLISH, "Provider %s.getCapability() returned null; return LazyOptional.empty() instead!", c.getClass().getTypeName()));
            }
            if (!ret.isPresent()) continue;
            return ret;
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        for (int x = 0; x < this.writers.length; ++x) {
            nbt.m_128365_(this.names[x], this.writers[x].serializeNBT());
        }
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        for (int x = 0; x < this.writers.length; ++x) {
            if (!nbt.m_128441_(this.names[x])) continue;
            this.writers[x].deserializeNBT(nbt.m_128423_(this.names[x]));
        }
    }

    public boolean areCompatible(@Nullable CapabilityDispatcher other) {
        if (other == null) {
            return this.writers.length == 0;
        }
        if (this.writers.length == 0) {
            return other.writers.length == 0;
        }
        return this.serializeNBT().equals((Object)other.serializeNBT());
    }

    public void invalidate() {
        this.listeners.forEach(Runnable::run);
    }
}

