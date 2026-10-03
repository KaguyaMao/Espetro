/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.packs.repository.Pack
 *  net.minecraft.server.packs.repository.RepositorySource
 */
package dev.latvian.mods.kubejs.client;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.KubeJSPaths;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.function.Consumer;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.RepositorySource;

public class KubeJSResourcePackFinder
implements RepositorySource {
    public void m_7686_(Consumer<Pack> nameToPackMap) {
        if (KubeJSPaths.FIRST_RUN.getValue().booleanValue()) {
            OutputStream out;
            InputStream in;
            Path blockTextures = KubeJSPaths.dir(KubeJSPaths.ASSETS.resolve("kubejs/textures/block"));
            Path itemTextures = KubeJSPaths.dir(KubeJSPaths.ASSETS.resolve("kubejs/textures/item"));
            try {
                in = Files.newInputStream((Path)KubeJS.thisMod.findResource(new String[]{"data", "kubejs", "example_block_texture.png"}).get(), new OpenOption[0]);
                try {
                    out = Files.newOutputStream(blockTextures.resolve("example_block.png"), new OpenOption[0]);
                    try {
                        in.transferTo(out);
                    }
                    finally {
                        if (out != null) {
                            out.close();
                        }
                    }
                }
                finally {
                    if (in != null) {
                        in.close();
                    }
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
            try {
                in = Files.newInputStream((Path)KubeJS.thisMod.findResource(new String[]{"data", "kubejs", "example_item_texture.png"}).get(), new OpenOption[0]);
                try {
                    out = Files.newOutputStream(itemTextures.resolve("example_item.png"), new OpenOption[0]);
                    try {
                        in.transferTo(out);
                    }
                    finally {
                        if (out != null) {
                            out.close();
                        }
                    }
                }
                finally {
                    if (in != null) {
                        in.close();
                    }
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}

