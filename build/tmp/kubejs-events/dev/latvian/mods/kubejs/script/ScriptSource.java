/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.packs.resources.Resource
 */
package dev.latvian.mods.kubejs.script;

import dev.latvian.mods.kubejs.script.ScriptFileInfo;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.packs.resources.Resource;

@FunctionalInterface
public interface ScriptSource {
    public List<String> readSource(ScriptFileInfo var1) throws IOException;

    public static interface FromResource
    extends ScriptSource {
        public Resource getResource(ScriptFileInfo var1) throws IOException;

        @Override
        default public List<String> readSource(ScriptFileInfo info) throws IOException {
            ArrayList<String> list = new ArrayList<String>();
            try (BufferedReader reader = this.getResource(info).m_215508_();){
                String line;
                while ((line = reader.readLine()) != null) {
                    list.add(line);
                }
                ArrayList<String> arrayList = list;
                return arrayList;
            }
        }
    }

    public static interface FromPath
    extends ScriptSource {
        public Path getPath(ScriptFileInfo var1);

        @Override
        default public List<String> readSource(ScriptFileInfo info) throws IOException {
            return Files.readAllLines(this.getPath(info));
        }
    }
}

