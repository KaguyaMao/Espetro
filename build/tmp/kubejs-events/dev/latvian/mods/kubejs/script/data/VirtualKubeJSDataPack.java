/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.packs.AbstractPackResources
 *  net.minecraft.server.packs.PackResources$ResourceOutput
 *  net.minecraft.server.packs.PackType
 *  net.minecraft.server.packs.metadata.MetadataSectionSerializer
 *  net.minecraft.server.packs.resources.IoSupplier
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.script.data;

import dev.latvian.mods.kubejs.DevProperties;
import dev.latvian.mods.kubejs.script.data.ExportablePackResources;
import dev.latvian.mods.kubejs.script.data.GeneratedData;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.AbstractPackResources;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.resources.IoSupplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VirtualKubeJSDataPack
extends AbstractPackResources
implements ExportablePackResources {
    public final boolean high;
    private final Map<ResourceLocation, String> locationToData;
    private final Map<String, String> pathToData;
    private final Set<String> namespaces;

    public VirtualKubeJSDataPack(boolean h) {
        super("dummy", false);
        this.high = h;
        this.locationToData = new HashMap<ResourceLocation, String>();
        this.pathToData = new HashMap<String, String>();
        this.namespaces = new HashSet<String>();
    }

    public void addData(ResourceLocation id, String data) {
        this.locationToData.put(id, data);
        this.pathToData.put("data/" + id.m_135827_() + "/" + id.m_135815_(), data);
        this.namespaces.add(id.m_135827_());
        if (DevProperties.get().dataPackOutput) {
            ConsoleJS.SERVER.info("Registered virtual file [" + (this.high ? "high" : "low") + " priority] '" + String.valueOf(id) + "': " + data);
        }
    }

    @Nullable
    public IoSupplier<InputStream> m_8017_(String ... path) {
        return switch (path.length == 1 ? path[0] : "") {
            case "pack.mcmeta" -> GeneratedData.PACK_META;
            case "pack.png" -> GeneratedData.PACK_ICON;
            default -> null;
        };
    }

    @Nullable
    public IoSupplier<InputStream> m_214146_(PackType type, ResourceLocation location) {
        if (type != PackType.SERVER_DATA) {
            return null;
        }
        String s = this.locationToData.get(location);
        if (s != null) {
            if (DevProperties.get().dataPackOutput) {
                ConsoleJS.SERVER.info("Served virtual file [" + (this.high ? "high" : "low") + " priority] '" + String.valueOf(location) + "': " + s);
            }
            return () -> new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8));
        }
        return null;
    }

    public void m_8031_(PackType packType, String namespace, String path, PackResources.ResourceOutput visitor) {
        if (!((String)path).endsWith("/")) {
            path = (String)path + "/";
        }
        for (ResourceLocation r : this.locationToData.keySet()) {
            if (r.m_135815_().endsWith(".mcmeta") || !r.m_135827_().equals(namespace) || !r.m_135815_().startsWith((String)path)) continue;
            visitor.accept((Object)r, this.m_214146_(packType, r));
        }
    }

    public Set<String> m_5698_(PackType type) {
        return new HashSet<String>(this.namespaces);
    }

    @Nullable
    public <T> T m_5550_(MetadataSectionSerializer<T> serializer) {
        return null;
    }

    public String toString() {
        return this.m_5542_();
    }

    @NotNull
    public String m_5542_() {
        return "KubeJS Virtual Data Pack [" + (this.high ? "high" : "low") + " priority]";
    }

    @Override
    public void export(Path root) throws IOException {
        for (Map.Entry<String, String> file : this.pathToData.entrySet()) {
            Path path = root.resolve(file.getKey());
            Files.createDirectories(path.getParent(), new FileAttribute[0]);
            Files.writeString(path, (CharSequence)file.getValue(), StandardCharsets.UTF_8, new OpenOption[0]);
        }
    }

    public void close() {
    }

    public boolean hasNamespace(String key) {
        return this.namespaces.contains(key);
    }
}

