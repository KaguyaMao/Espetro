/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.packs.AbstractPackResources
 *  net.minecraft.server.packs.PackResources
 *  net.minecraft.server.packs.PackResources$ResourceOutput
 *  net.minecraft.server.packs.PackType
 *  net.minecraft.server.packs.metadata.MetadataSectionSerializer
 *  net.minecraft.server.packs.metadata.pack.PackMetadataSection
 *  net.minecraft.server.packs.resources.IoSupplier
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.resource;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.AbstractPackResources;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.resources.IoSupplier;
import org.jetbrains.annotations.Nullable;

public class DelegatingPackResources
extends AbstractPackResources {
    private final PackMetadataSection packMeta;
    private final List<PackResources> delegates;
    private final Map<String, List<PackResources>> namespacesAssets;
    private final Map<String, List<PackResources>> namespacesData;

    public DelegatingPackResources(String packId, boolean isBuiltin, PackMetadataSection packMeta, List<? extends PackResources> packs) {
        super(packId, isBuiltin);
        this.packMeta = packMeta;
        this.delegates = ImmutableList.copyOf(packs);
        this.namespacesAssets = this.buildNamespaceMap(PackType.CLIENT_RESOURCES, this.delegates);
        this.namespacesData = this.buildNamespaceMap(PackType.SERVER_DATA, this.delegates);
    }

    private Map<String, List<PackResources>> buildNamespaceMap(PackType type, List<PackResources> packList) {
        HashMap<String, List> map = new HashMap<String, List>();
        for (PackResources pack : packList) {
            for (String namespace : pack.m_5698_(type)) {
                map.computeIfAbsent(namespace, k -> new ArrayList()).add(pack);
            }
        }
        map.replaceAll((k, list) -> ImmutableList.copyOf((Collection)list));
        return ImmutableMap.copyOf(map);
    }

    @Nullable
    public <T> T m_5550_(MetadataSectionSerializer<T> deserializer) throws IOException {
        return (T)(deserializer.m_7991_().equals("pack") ? this.packMeta : null);
    }

    public void m_8031_(PackType type, String resourceNamespace, String paths, PackResources.ResourceOutput resourceOutput) {
        for (PackResources delegate : this.delegates) {
            delegate.m_8031_(type, resourceNamespace, paths, resourceOutput);
        }
    }

    public Set<String> m_5698_(PackType type) {
        return type == PackType.CLIENT_RESOURCES ? this.namespacesAssets.keySet() : this.namespacesData.keySet();
    }

    public void close() {
        for (PackResources pack : this.delegates) {
            pack.close();
        }
    }

    @Nullable
    public IoSupplier<InputStream> m_8017_(String ... paths) {
        return null;
    }

    @Nullable
    public IoSupplier<InputStream> m_214146_(PackType type, ResourceLocation location) {
        for (PackResources pack : this.getCandidatePacks(type, location)) {
            IoSupplier ioSupplier = pack.m_214146_(type, location);
            if (ioSupplier == null) continue;
            return ioSupplier;
        }
        return null;
    }

    @Nullable
    public Collection<PackResources> getChildren() {
        return this.delegates;
    }

    private List<PackResources> getCandidatePacks(PackType type, ResourceLocation location) {
        Map<String, List<PackResources>> map = type == PackType.CLIENT_RESOURCES ? this.namespacesAssets : this.namespacesData;
        List<PackResources> packsWithNamespace = map.get(location.m_135827_());
        return packsWithNamespace == null ? Collections.emptyList() : packsWithNamespace;
    }
}

