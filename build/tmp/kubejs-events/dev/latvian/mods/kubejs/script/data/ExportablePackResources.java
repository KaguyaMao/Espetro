/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.packs.PackResources
 */
package dev.latvian.mods.kubejs.script.data;

import java.io.IOException;
import java.nio.file.Path;
import net.minecraft.server.packs.PackResources;

public interface ExportablePackResources
extends PackResources {
    public void export(Path var1) throws IOException;
}

