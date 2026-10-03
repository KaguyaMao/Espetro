/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.packs.PackResources
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.common.extensions;

import java.util.Collection;
import net.minecraft.server.packs.PackResources;
import org.jetbrains.annotations.Nullable;

public interface IForgePackResources {
    default public boolean isHidden() {
        return false;
    }

    @Nullable
    default public Collection<PackResources> getChildren() {
        return null;
    }
}

