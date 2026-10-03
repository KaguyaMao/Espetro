/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.minecraft.FileUtil
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.packs.AbstractPackResources
 *  net.minecraft.server.packs.PackResources$ResourceOutput
 *  net.minecraft.server.packs.PackType
 *  net.minecraft.server.packs.PathPackResources
 *  net.minecraft.server.packs.resources.IoSupplier
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.slf4j.Logger
 */
package net.minecraftforge.resource;

import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.FileUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.AbstractPackResources;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.IoSupplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class PathPackResources
extends AbstractPackResources {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Path source;

    public PathPackResources(String packId, boolean isBuiltin, Path source) {
        super(packId, isBuiltin);
        this.source = source;
    }

    public Path getSource() {
        return this.source;
    }

    protected Path resolve(String ... paths) {
        Path path = this.getSource();
        for (String name : paths) {
            path = path.resolve(name);
        }
        return path;
    }

    @Nullable
    public IoSupplier<InputStream> m_8017_(String ... paths) {
        Path path = this.resolve(paths);
        if (!Files.exists(path, new LinkOption[0])) {
            return null;
        }
        return IoSupplier.m_246697_((Path)path);
    }

    public void m_8031_(PackType type, String namespace, String path, PackResources.ResourceOutput resourceOutput) {
        FileUtil.m_245538_((String)path).get().ifLeft(parts -> net.minecraft.server.packs.PathPackResources.m_246914_((String)namespace, (Path)this.resolve(type.m_10305_(), namespace).toAbsolutePath(), (List)parts, (PackResources.ResourceOutput)resourceOutput)).ifRight(dataResult -> LOGGER.error("Invalid path {}: {}", (Object)path, (Object)dataResult.message()));
    }

    public Set<String> m_5698_(PackType type) {
        return this.getNamespacesFromDisk(type);
    }

    @NotNull
    private Set<String> getNamespacesFromDisk(PackType type) {
        Set<String> set;
        block9: {
            Path root = this.resolve(type.m_10305_());
            Stream<Path> walker = Files.walk(root, 1, new FileVisitOption[0]);
            try {
                set = walker.filter(x$0 -> Files.isDirectory(x$0, new LinkOption[0])).map(root::relativize).filter(p -> p.getNameCount() > 0).map(p -> p.toString().replaceAll("/$", "")).filter(s -> !s.isEmpty()).collect(Collectors.toSet());
                if (walker == null) break block9;
            }
            catch (Throwable throwable) {
                try {
                    if (walker != null) {
                        try {
                            walker.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                catch (IOException e) {
                    if (type == PackType.SERVER_DATA) {
                        return this.m_5698_(PackType.CLIENT_RESOURCES);
                    }
                    return Collections.emptySet();
                }
            }
            walker.close();
        }
        return set;
    }

    public IoSupplier<InputStream> m_214146_(PackType type, ResourceLocation location) {
        return this.m_8017_(PathPackResources.getPathFromLocation(location.m_135815_().startsWith("lang/") ? PackType.CLIENT_RESOURCES : type, location));
    }

    private static String[] getPathFromLocation(PackType type, ResourceLocation location) {
        String[] parts = location.m_135815_().split("/");
        String[] result = new String[parts.length + 2];
        result[0] = type.m_10305_();
        result[1] = location.m_135827_();
        System.arraycopy(parts, 0, result, 2, parts.length);
        return result;
    }

    public void close() {
    }

    public String toString() {
        return String.format(Locale.ROOT, "%s: %s (%s)", ((Object)((Object)this)).getClass().getName(), this.m_5542_(), this.getSource());
    }
}

