/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  javax.annotation.Nullable
 *  net.minecraftforge.event.AddPackFindersEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 */
package org.espetro.dimension;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.espetro.Espetro;
import org.espetro.mapconfig.ActiveMapConfig;
import org.espetro.mapconfig.ExternalConfigBootstrap;

@Mod.EventBusSubscriber(modid="espetro", bus=Mod.EventBusSubscriber.Bus.MOD)
public final class DimensionPackBootstrap {
    public static final String PACK_ID = "espetro_dimensions_runtime";

    private DimensionPackBootstrap() {
    }

    @SubscribeEvent
    public static void onAddPackFinders(AddPackFindersEvent event) {
        if (event.getPackType() != PackType.SERVER_DATA) {
            return;
        }
        ExternalConfigBootstrap.bootstrapIfNeeded();
        LinkedHashMap<String, byte[]> dimensionResources = new LinkedHashMap<String, byte[]>();
        for (ActiveMapConfig map : ExternalConfigBootstrap.getUsableMaps()) {
            String path = "data/" + map.dimensionId.m_135827_() + "/dimension/" + map.dimensionId.m_135815_() + ".json";
            dimensionResources.put(path.toLowerCase(Locale.ROOT), map.dimensionJson.getBytes(StandardCharsets.UTF_8));
        }
        int registeredDimensions = dimensionResources.size();
        dimensionResources.putAll(DimensionPackBootstrap.loadBuiltinStructures());
        if (registeredDimensions == 0) {
            Espetro.LOGGER.warn("\u65e0\u53ef\u7528\u5730\u56fe\u7ef4\u5ea6\u53ef\u6ce8\u518c\uff1b/espetro prestart \u5c06\u4e0d\u53ef\u7528\u76f4\u5230\u914d\u7f6e\u6709\u6548\u5730\u56fe");
        }
        event.addRepositorySource(consumer -> {
            MemoryDimensionPackResources resources = new MemoryDimensionPackResources(dimensionResources);
            Pack pack = Pack.m_245429_(PACK_ID, Component.m_237113_("Espetro Dimensions"), true, resources, PackType.SERVER_DATA, Pack.Position.TOP, PackSource.f_10528_);
            if (pack != null) {
                consumer.accept(pack);
                Espetro.LOGGER.info("\u5df2\u6dfb\u52a0\u5185\u5b58 SERVER_DATA \u5305: {} ({} \u9879\u8d44\u6e90)", (Object)PACK_ID, (Object)dimensionResources.size());
            } else {
                Espetro.LOGGER.error("\u65e0\u6cd5\u521b\u5efa Espetro \u7ef4\u5ea6\u6570\u636e\u5305\uff08readMetaAndCreate \u8fd4\u56de null\uff09");
            }
        });
    }

    private static Map<String, byte[]> loadBuiltinStructures() {
        LinkedHashMap<String, byte[]> result = new LinkedHashMap<String, byte[]>();
        for (String name : Set.of("radio", "hab_attack", "hab_defend", "ammo_crate", "sandbag_wall", "vehicle_supply_station_fallback")) {
            String source = "/data/espetro/structure_sources/fortifications/" + name + ".snbt";
            try {
                InputStream input = DimensionPackBootstrap.class.getResourceAsStream(source);
                try {
                    if (input == null) {
                        throw new IllegalStateException("\u7f3a\u5c11 " + source);
                    }
                    String snbt = new String(input.readAllBytes(), StandardCharsets.UTF_8);
                    CompoundTag tag = TagParser.m_129359_(snbt);
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    NbtIo.m_128947_(tag, bytes);
                    result.put("data/espetro/structures/fortifications/" + name + ".nbt", bytes.toByteArray());
                }
                finally {
                    if (input == null) continue;
                    input.close();
                }
            }
            catch (Exception e) {
                Espetro.LOGGER.error("\u65e0\u6cd5\u7f16\u8bd1\u5185\u7f6e Structure NBT {}", (Object)source, (Object)e);
            }
        }
        return result;
    }

    public static final class MemoryDimensionPackResources
    implements Pack.ResourcesSupplier {
        private final Map<String, byte[]> dimensionResources;

        public MemoryDimensionPackResources(Map<String, byte[]> dimensionResources) {
            this.dimensionResources = Map.copyOf(dimensionResources);
        }

        @Override
        public PackResources m_247679_(String id) {
            return new MemoryPackResources(id, this.dimensionResources);
        }
    }

    private static final class MemoryPackResources
    implements PackResources {
        private static final String PACK_MCMETA = "{\n  \"pack\": {\n    \"description\": \"Espetro runtime dimensions\",\n    \"pack_format\": 15\n  }\n}\n";
        private final String packId;
        private final Map<String, byte[]> dimensionResources;
        private boolean closed;

        private MemoryPackResources(String packId, Map<String, byte[]> dimensionResources) {
            this.packId = packId;
            this.dimensionResources = dimensionResources;
        }

        @Override
        @Nullable
        public IoSupplier<InputStream> m_8017_(String ... pathParts) {
            String joined = String.join((CharSequence)"/", pathParts);
            if ("pack.mcmeta".equals(joined)) {
                return () -> new ByteArrayInputStream(PACK_MCMETA.getBytes(StandardCharsets.UTF_8));
            }
            return null;
        }

        @Override
        @Nullable
        public IoSupplier<InputStream> m_214146_(PackType type, ResourceLocation location) {
            if (type != PackType.SERVER_DATA) {
                return null;
            }
            String path = "data/" + location.m_135827_() + "/" + location.m_135815_();
            byte[] content = this.dimensionResources.get(path.toLowerCase(Locale.ROOT));
            if (content != null) {
                return () -> new ByteArrayInputStream(content);
            }
            return null;
        }

        @Override
        public void m_8031_(PackType type, String namespace, String path, PackResources.ResourceOutput output) {
            if (type != PackType.SERVER_DATA) {
                return;
            }
            String prefix = "data/" + namespace + "/" + path;
            for (Map.Entry<String, byte[]> entry : this.dimensionResources.entrySet()) {
                String withoutData;
                int slash;
                String dimPath = entry.getKey();
                if (!dimPath.startsWith(prefix.toLowerCase(Locale.ROOT)) && !dimPath.startsWith(prefix) || (slash = (withoutData = dimPath.substring("data/".length())).indexOf(47)) < 0) continue;
                String ns = withoutData.substring(0, slash);
                String rest = withoutData.substring(slash + 1);
                ResourceLocation rl = ResourceLocation.m_135820_(ns + ":" + rest.replace(".json", ""));
                rl = ResourceLocation.fromNamespaceAndPath((String)ns, (String)rest);
                if (rl == null || !ns.equals(namespace) || !rest.startsWith(path.isEmpty() ? "" : path)) continue;
                byte[] content = entry.getValue();
                output.accept(rl, () -> new ByteArrayInputStream(content));
            }
        }

        @Override
        public Set<String> m_5698_(PackType type) {
            if (type != PackType.SERVER_DATA) {
                return Set.of();
            }
            LinkedHashSet<String> ns = new LinkedHashSet<String>();
            for (String p : this.dimensionResources.keySet()) {
                String[] parts = p.split("/");
                if (parts.length < 2) continue;
                ns.add(parts[1]);
            }
            return ns;
        }

        @Override
        @Nullable
        public <T> T m_5550_(MetadataSectionSerializer<T> deserializer) {
            if ("pack".equals(deserializer.m_7991_())) {
                try {
                    JsonObject obj = JsonParser.parseString((String)PACK_MCMETA).getAsJsonObject();
                    return deserializer.m_6322_(obj.getAsJsonObject("pack"));
                }
                catch (Exception e) {
                    return null;
                }
            }
            return null;
        }

        @Override
        public String m_5542_() {
            return this.packId;
        }

        @Override
        public void close() {
            this.closed = true;
        }
    }
}

