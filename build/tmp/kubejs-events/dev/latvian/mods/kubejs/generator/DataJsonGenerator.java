/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 */
package dev.latvian.mods.kubejs.generator;

import dev.latvian.mods.kubejs.generator.ResourceGenerator;
import dev.latvian.mods.kubejs.script.data.GeneratedData;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;

public class DataJsonGenerator
extends ResourceGenerator {
    public DataJsonGenerator(Map<ResourceLocation, GeneratedData> m) {
        super(ConsoleJS.SERVER, m);
    }
}

