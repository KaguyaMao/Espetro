/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.common.capabilities;

import net.minecraftforge.common.capabilities.CapabilityDispatcher;
import net.minecraftforge.common.capabilities.CapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public interface ICapabilityProviderImpl<B extends ICapabilityProviderImpl<B>>
extends ICapabilityProvider {
    public boolean areCapsCompatible(CapabilityProvider<B> var1);

    public boolean areCapsCompatible(@Nullable CapabilityDispatcher var1);

    public void invalidateCaps();

    public void reviveCaps();
}

