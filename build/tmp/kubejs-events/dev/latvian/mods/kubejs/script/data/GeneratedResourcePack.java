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
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.KubeJSPaths;
import dev.latvian.mods.kubejs.script.data.ExportablePackResources;
import dev.latvian.mods.kubejs.script.data.GeneratedData;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.kubejs.util.Lazy;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.AbstractPackResources;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.resources.IoSupplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class GeneratedResourcePack
implements ExportablePackResources {
    private final PackType packType;
    private Map<ResourceLocation, GeneratedData> generated;
    private Set<String> generatedNamespaces;

    private static Stream<Path> tryWalk(Path path) {
        try {
            return Files.walk(path, new FileVisitOption[0]);
        }
        catch (Exception exception) {
            return Stream.empty();
        }
    }

    public static void scanForInvalidFiles(String pathName, Path path) throws IOException {
        block2: for (Path p : Files.list(path).filter(x$0 -> Files.isDirectory(x$0, new LinkOption[0])).flatMap(GeneratedResourcePack::tryWalk).filter(x$0 -> Files.isRegularFile(x$0, new LinkOption[0])).filter(Files::isReadable).toList()) {
            try {
                char[] chars;
                String fileName = p.getFileName().toString();
                String fileNameLC = fileName.toLowerCase();
                if (fileNameLC.endsWith(".zip") || fileNameLC.equals(".ds_store") || fileNameLC.equals("thumbs.db") || fileNameLC.equals("desktop.ini")) continue;
                if (Files.isHidden(path)) {
                    ConsoleJS.STARTUP.error("Invisible file found: " + pathName + path.relativize(p).toString().replace('\\', '/')).withExternalFile(p);
                    continue;
                }
                for (char c : chars = fileName.toCharArray()) {
                    if (c >= 'A' && c <= 'Z') {
                        ConsoleJS.STARTUP.error("Invalid file name: Uppercase '" + c + "' in " + pathName + path.relativize(p).toString().replace('\\', '/')).withExternalFile(p);
                        continue block2;
                    }
                    if (c == '_' || c == '-' || c >= 'a' && c <= 'z' || c >= '0' && c <= '9' || c == '/' || c == '.') continue;
                    ConsoleJS.STARTUP.error("Invalid file name: Invalid character '" + c + "' in " + pathName + path.relativize(p).toString().replace('\\', '/')).withExternalFile(p);
                    continue block2;
                }
            }
            catch (Exception ex) {
                ConsoleJS.STARTUP.error("Invalid file name: " + pathName + path.relativize(p).toString().replace('\\', '/'), ex).withExternalFile(p);
            }
        }
    }

    public GeneratedResourcePack(PackType t) {
        this.packType = t;
    }

    @Nullable
    public GeneratedData getRootResource(String ... path) {
        return switch (path.length == 1 ? path[0] : "") {
            case "pack.mcmeta" -> GeneratedData.PACK_META;
            case "pack.png" -> GeneratedData.PACK_ICON;
            default -> null;
        };
    }

    public Map<ResourceLocation, GeneratedData> getGenerated() {
        if (this.generated == null) {
            this.generated = new HashMap<ResourceLocation, GeneratedData>();
            this.generate(this.generated);
            boolean debug = DevProperties.get().logGeneratedData || DevProperties.get().debugInfo;
            try {
                Path root = KubeJSPaths.get(this.packType);
                for (Path dir : Files.list(root).filter(x$0 -> Files.isDirectory(x$0, new LinkOption[0])).toList()) {
                    String ns = dir.getFileName().toString();
                    if (debug) {
                        KubeJS.LOGGER.info("# Walking namespace '" + ns + "'");
                    }
                    for (Path path : Files.walk(dir, new FileVisitOption[0]).filter(x$0 -> Files.isRegularFile(x$0, new LinkOption[0])).filter(Files::isReadable).toList()) {
                        String pathStr = dir.relativize(path).toString().replace('\\', '/').toLowerCase();
                        int sindex = pathStr.lastIndexOf(47);
                        String fileNameLC = sindex == -1 ? pathStr : pathStr.substring(sindex + 1);
                        if (fileNameLC.endsWith(".zip") || fileNameLC.equals(".ds_store") || fileNameLC.equals("thumbs.db") || fileNameLC.equals("desktop.ini") || Files.isHidden(path)) continue;
                        GeneratedData data = new GeneratedData(new ResourceLocation(ns, pathStr), Lazy.of(() -> {
                            try {
                                return Files.readAllBytes(path);
                            }
                            catch (Exception ex) {
                                ex.printStackTrace();
                                return new byte[0];
                            }
                        }), this.forgetFile(pathStr));
                        if (debug) {
                            KubeJS.LOGGER.info("- File found: '" + String.valueOf(data.id()) + "' (" + data.data().get().length + " bytes)");
                        }
                        if (this.skipFile(data)) {
                            if (!debug) continue;
                            KubeJS.LOGGER.info("- Skipping '" + String.valueOf(data.id()) + "'");
                            continue;
                        }
                        this.generated.put(data.id(), data);
                    }
                }
            }
            catch (Exception ex) {
                KubeJS.LOGGER.error("Failed to load files from kubejs/" + this.packType.m_10305_(), (Throwable)ex);
            }
            this.generated.put(GeneratedData.INTERNAL_RELOAD.id(), GeneratedData.INTERNAL_RELOAD);
            this.generated = Map.copyOf(this.generated);
            if (debug) {
                KubeJS.LOGGER.info("Generated " + String.valueOf(this.packType) + " data (" + this.generated.size() + " files)");
            }
        }
        return this.generated;
    }

    protected boolean forgetFile(String path) {
        return true;
    }

    protected boolean skipFile(GeneratedData data) {
        return false;
    }

    @Nullable
    public IoSupplier<InputStream> m_214146_(PackType type, ResourceLocation location) {
        GeneratedData r;
        GeneratedData generatedData = r = type == this.packType ? this.getGenerated().get(location) : null;
        if (r == GeneratedData.INTERNAL_RELOAD) {
            this.close();
        }
        return r;
    }

    public void generate(Map<ResourceLocation, GeneratedData> map) {
    }

    public void m_8031_(PackType type, String namespace, String path, PackResources.ResourceOutput visitor) {
        if (type == this.packType) {
            if (!((String)path).endsWith("/")) {
                path = (String)path + "/";
            }
            for (Map.Entry<ResourceLocation, GeneratedData> r : this.getGenerated().entrySet()) {
                if (!r.getKey().m_135827_().equals(namespace) || !r.getKey().m_135815_().startsWith((String)path)) continue;
                visitor.accept((Object)r.getKey(), (Object)r.getValue());
            }
        }
    }

    @NotNull
    public Set<String> m_5698_(PackType type) {
        if (type == this.packType) {
            if (this.generatedNamespaces == null) {
                this.generatedNamespaces = new HashSet<String>();
                for (Map.Entry<ResourceLocation, GeneratedData> s : this.getGenerated().entrySet()) {
                    this.generatedNamespaces.add(s.getKey().m_135827_());
                }
            }
            return this.generatedNamespaces;
        }
        return Collections.emptySet();
    }

    @Nullable
    public <T> T m_5550_(MetadataSectionSerializer<T> serializer) throws IOException {
        GeneratedData inputSupplier = this.getRootResource("pack.mcmeta");
        if (inputSupplier != null) {
            try (InputStream input = inputSupplier.get();){
                Object object = AbstractPackResources.m_10214_(serializer, (InputStream)input);
                return (T)object;
            }
        }
        return null;
    }

    @NotNull
    public String m_5542_() {
        return "KubeJS Resource Pack [" + this.packType.m_10305_() + "]";
    }

    public void close() {
        this.generated = null;
        this.generatedNamespaces = null;
    }

    public boolean m_246538_() {
        return true;
    }

    @Override
    public void export(Path root) throws IOException {
        for (Map.Entry<ResourceLocation, GeneratedData> file : this.getGenerated().entrySet()) {
            Path path = root.resolve(this.packType.m_10305_() + "/" + file.getKey().m_135827_() + "/" + file.getKey().m_135815_());
            Files.createDirectories(path.getParent(), new FileAttribute[0]);
            Files.write(path, file.getValue().data().get(), new OpenOption[0]);
        }
        Files.write(root.resolve("pack.mcmeta"), GeneratedData.PACK_META.data().get(), new OpenOption[0]);
        Files.write(root.resolve("pack.png"), GeneratedData.PACK_ICON.data().get(), new OpenOption[0]);
    }
}

