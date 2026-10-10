/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.HashMultimap
 *  com.google.common.collect.Multimap
 *  net.minecraft.client.resources.ClientPackSource
 *  net.minecraft.client.resources.IndexedAssetSource
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.packs.FilePackResources
 *  net.minecraft.server.packs.PackType
 *  net.minecraft.server.packs.PathPackResources
 *  net.minecraft.server.packs.repository.ServerPacksSource
 *  net.minecraft.server.packs.resources.MultiPackResourceManager
 *  net.minecraft.server.packs.resources.Resource
 *  net.minecraft.server.packs.resources.ResourceManager
 *  net.minecraftforge.fml.ModList
 *  net.minecraftforge.forgespi.language.IModFileInfo
 *  org.jetbrains.annotations.Nullable
 *  org.jetbrains.annotations.VisibleForTesting
 */
package net.minecraftforge.common.data;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import net.minecraft.client.resources.ClientPackSource;
import net.minecraft.client.resources.IndexedAssetSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.FilePackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.ServerPacksSource;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModFileInfo;
import net.minecraftforge.resource.ResourcePackLoader;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;

public class ExistingFileHelper {
    private final MultiPackResourceManager clientResources;
    private final MultiPackResourceManager serverData;
    private final boolean enable;
    private final Multimap<PackType, ResourceLocation> generated = HashMultimap.create();

    public ExistingFileHelper(Collection<Path> existingPacks, Set<String> existingMods, boolean enable, @Nullable String assetIndex, @Nullable File assetsDir) {
        Object pack;
        ArrayList<Object> candidateClientResources = new ArrayList<Object>();
        ArrayList<Object> candidateServerResources = new ArrayList<Object>();
        if (assetIndex != null && assetsDir != null && assetsDir.exists()) {
            candidateClientResources.add(ClientPackSource.m_246691_((Path)IndexedAssetSource.m_245793_((Path)assetsDir.toPath(), (String)assetIndex)));
        }
        candidateServerResources.add(ServerPacksSource.m_246173_());
        for (Path existing : existingPacks) {
            File file = existing.toFile();
            pack = file.isDirectory() ? new PathPackResources(file.getName(), file.toPath(), false) : new FilePackResources(file.getName(), file, false);
            candidateClientResources.add(pack);
            candidateServerResources.add(pack);
        }
        for (String existingMod : existingMods) {
            IModFileInfo modFileInfo = ModList.get().getModFileById(existingMod);
            if (modFileInfo == null) continue;
            pack = ResourcePackLoader.createPackForMod(modFileInfo);
            candidateClientResources.add(pack);
            candidateServerResources.add(pack);
        }
        this.clientResources = new MultiPackResourceManager(PackType.CLIENT_RESOURCES, candidateClientResources);
        this.serverData = new MultiPackResourceManager(PackType.SERVER_DATA, candidateServerResources);
        this.enable = enable;
    }

    private ResourceManager getManager(PackType packType) {
        return packType == PackType.CLIENT_RESOURCES ? this.clientResources : this.serverData;
    }

    private ResourceLocation getLocation(ResourceLocation base, String suffix, String prefix) {
        return new ResourceLocation(base.m_135827_(), prefix + "/" + base.m_135815_() + suffix);
    }

    public boolean exists(ResourceLocation loc, PackType packType) {
        if (!this.enable) {
            return true;
        }
        return this.generated.get((Object)packType).contains(loc) || this.getManager(packType).m_213713_(loc).isPresent();
    }

    public boolean exists(ResourceLocation loc, IResourceType type) {
        return this.exists(this.getLocation(loc, type.getSuffix(), type.getPrefix()), type.getPackType());
    }

    public boolean exists(ResourceLocation loc, PackType packType, String pathSuffix, String pathPrefix) {
        return this.exists(this.getLocation(loc, pathSuffix, pathPrefix), packType);
    }

    public void trackGenerated(ResourceLocation loc, IResourceType type) {
        this.generated.put((Object)type.getPackType(), (Object)this.getLocation(loc, type.getSuffix(), type.getPrefix()));
    }

    public void trackGenerated(ResourceLocation loc, PackType packType, String pathSuffix, String pathPrefix) {
        this.generated.put((Object)packType, (Object)this.getLocation(loc, pathSuffix, pathPrefix));
    }

    @VisibleForTesting
    public Resource getResource(ResourceLocation loc, PackType packType, String pathSuffix, String pathPrefix) throws FileNotFoundException {
        return this.getResource(this.getLocation(loc, pathSuffix, pathPrefix), packType);
    }

    @VisibleForTesting
    public Resource getResource(ResourceLocation loc, PackType packType) throws FileNotFoundException {
        return this.getManager(packType).m_215593_(loc);
    }

    @VisibleForTesting
    public List<Resource> getResourceStack(ResourceLocation loc, PackType packType) {
        return this.getManager(packType).m_213829_(loc);
    }

    public boolean isEnabled() {
        return this.enable;
    }

    public static interface IResourceType {
        public PackType getPackType();

        public String getSuffix();

        public String getPrefix();
    }

    public static class ResourceType
    implements IResourceType {
        final PackType packType;
        final String suffix;
        final String prefix;

        public ResourceType(PackType type, String suffix, String prefix) {
            this.packType = type;
            this.suffix = suffix;
            this.prefix = prefix;
        }

        @Override
        public PackType getPackType() {
            return this.packType;
        }

        @Override
        public String getSuffix() {
            return this.suffix;
        }

        @Override
        public String getPrefix() {
            return this.prefix;
        }
    }
}

