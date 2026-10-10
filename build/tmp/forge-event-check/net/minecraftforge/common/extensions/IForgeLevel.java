/*
 * Decompiled with CFR 0.152.
 */
package net.minecraftforge.common.extensions;

import java.util.Collection;
import java.util.Collections;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.entity.PartEntity;

public interface IForgeLevel
extends ICapabilityProvider {
    public double getMaxEntityRadius();

    public double increaseMaxEntityRadius(double var1);

    default public Collection<PartEntity<?>> getPartEntities() {
        return Collections.emptyList();
    }
}

