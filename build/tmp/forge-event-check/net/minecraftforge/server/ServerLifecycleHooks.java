/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Holder
 *  net.minecraft.core.RegistryAccess$Frozen
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.gametest.framework.GameTestServer
 *  net.minecraft.network.Connection
 *  net.minecraft.network.ConnectionProtocol
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.network.protocol.Packet
 *  net.minecraft.network.protocol.handshake.ClientIntentionPacket
 *  net.minecraft.network.protocol.login.ClientboundLoginDisconnectPacket
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.packs.PackResources
 *  net.minecraft.server.packs.PackType
 *  net.minecraft.server.packs.repository.Pack
 *  net.minecraft.server.packs.repository.Pack$Position
 *  net.minecraft.server.packs.repository.PackSource
 *  net.minecraft.server.packs.repository.RepositorySource
 *  net.minecraft.world.level.biome.Biome
 *  net.minecraft.world.level.levelgen.structure.Structure
 *  net.minecraft.world.level.storage.LevelResource
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.eventbus.api.Event
 *  net.minecraftforge.fml.DistExecutor
 *  net.minecraftforge.fml.Logging
 *  net.minecraftforge.fml.ModLoader
 *  net.minecraftforge.fml.ModLoadingStage
 *  net.minecraftforge.fml.ModLoadingWarning
 *  net.minecraftforge.fml.config.ConfigTracker
 *  net.minecraftforge.fml.config.ModConfig$Type
 *  net.minecraftforge.forgespi.language.IModInfo
 *  net.minecraftforge.forgespi.locating.IModFile
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.apache.logging.log4j.Marker
 *  org.apache.logging.log4j.MarkerManager
 *  org.jetbrains.annotations.ApiStatus$Internal
 */
package net.minecraftforge.server;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestServer;
import net.minecraft.network.Connection;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.handshake.ClientIntentionPacket;
import net.minecraft.network.protocol.login.ClientboundLoginDisconnectPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.LogicalSidedProvider;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.StructureModifier;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.Logging;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.ModLoadingStage;
import net.minecraftforge.fml.ModLoadingWarning;
import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.forgespi.locating.IModFile;
import net.minecraftforge.gametest.ForgeGameTestHooks;
import net.minecraftforge.network.ConnectionType;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.resource.PathPackResources;
import net.minecraftforge.server.LanguageHook;
import net.minecraftforge.server.permission.PermissionAPI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.jetbrains.annotations.ApiStatus;

public class ServerLifecycleHooks {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Marker SERVERHOOKS = MarkerManager.getMarker((String)"SERVERHOOKS");
    private static final LevelResource SERVERCONFIG = new LevelResource("serverconfig");
    private static volatile CountDownLatch exitLatch = null;
    private static MinecraftServer currentServer;
    private static AtomicBoolean allowLogins;

    private static Path getServerConfigPath(MinecraftServer server) {
        Path serverConfig = server.m_129843_(SERVERCONFIG);
        if (!Files.isDirectory(serverConfig, new LinkOption[0])) {
            try {
                Files.createDirectories(serverConfig, new FileAttribute[0]);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return serverConfig;
    }

    public static boolean handleServerAboutToStart(MinecraftServer server) {
        currentServer = server;
        LogicalSidedProvider.setServer(() -> server);
        ConfigTracker.INSTANCE.loadConfigs(ModConfig.Type.SERVER, ServerLifecycleHooks.getServerConfigPath(server));
        ServerLifecycleHooks.runModifiers(server);
        return !MinecraftForge.EVENT_BUS.post((Event)new ServerAboutToStartEvent(server));
    }

    public static boolean handleServerStarting(MinecraftServer server) {
        DistExecutor.runWhenOn((Dist)Dist.DEDICATED_SERVER, () -> () -> {
            LanguageHook.loadLanguagesOnServer(server);
            if (!(server instanceof GameTestServer)) {
                ForgeGameTestHooks.registerGametests();
            }
        });
        PermissionAPI.initializePermissionAPI();
        return !MinecraftForge.EVENT_BUS.post((Event)new ServerStartingEvent(server));
    }

    public static void handleServerStarted(MinecraftServer server) {
        MinecraftForge.EVENT_BUS.post((Event)new ServerStartedEvent(server));
        allowLogins.set(true);
    }

    public static void handleServerStopping(MinecraftServer server) {
        allowLogins.set(false);
        MinecraftForge.EVENT_BUS.post((Event)new ServerStoppingEvent(server));
    }

    public static void expectServerStopped() {
        exitLatch = new CountDownLatch(1);
    }

    public static void handleServerStopped(MinecraftServer server) {
        if (!server.m_6982_()) {
            GameData.revertToFrozen();
        }
        MinecraftForge.EVENT_BUS.post((Event)new ServerStoppedEvent(server));
        currentServer = null;
        LogicalSidedProvider.setServer(null);
        CountDownLatch latch = exitLatch;
        if (latch != null) {
            latch.countDown();
            exitLatch = null;
        }
        ConfigTracker.INSTANCE.unloadConfigs(ModConfig.Type.SERVER, ServerLifecycleHooks.getServerConfigPath(server));
    }

    public static MinecraftServer getCurrentServer() {
        return currentServer;
    }

    public static boolean handleServerLogin(ClientIntentionPacket packet, Connection manager) {
        if (!allowLogins.get()) {
            MutableComponent text = Component.m_237113_((String)"Server is still starting! Please wait before reconnecting.");
            LOGGER.info(SERVERHOOKS, "Disconnecting Player (server is still starting): {}", (Object)text.m_214077_());
            manager.m_129512_((Packet)new ClientboundLoginDisconnectPacket((Component)text));
            manager.m_129507_((Component)text);
            return false;
        }
        if (packet.m_134735_() == ConnectionProtocol.LOGIN) {
            ConnectionType connectionType = ConnectionType.forVersionFlag(packet.getFMLVersion());
            int versionNumber = connectionType.getFMLVersionNumber(packet.getFMLVersion());
            if (connectionType == ConnectionType.MODDED && versionNumber != 3) {
                ServerLifecycleHooks.rejectConnection(manager, connectionType, "This modded server is not impl compatible with your modded client. Please verify your Forge version closely matches the server. Got net version " + versionNumber + " this server is net version 3");
                return false;
            }
            if (connectionType == ConnectionType.VANILLA && !NetworkRegistry.acceptsVanillaClientConnections()) {
                ServerLifecycleHooks.rejectConnection(manager, connectionType, "This server has mods that require Forge to be installed on the client. Contact your server admin for more details.");
                return false;
            }
        }
        if (packet.m_134735_() == ConnectionProtocol.STATUS) {
            return true;
        }
        NetworkHooks.registerServerLoginChannel(manager, packet);
        return true;
    }

    private static void rejectConnection(Connection manager, ConnectionType type, String message) {
        manager.m_129498_(ConnectionProtocol.LOGIN);
        String ip = "local";
        if (manager.m_129523_() != null) {
            ip = manager.m_129523_().toString();
        }
        LOGGER.info(SERVERHOOKS, "[{}] Disconnecting {} connection attempt: {}", (Object)ip, (Object)type, (Object)message);
        MutableComponent text = Component.m_237113_((String)message);
        manager.m_129512_((Packet)new ClientboundLoginDisconnectPacket((Component)text));
        manager.m_129507_((Component)text);
    }

    public static void handleExit(int retVal) {
        System.exit(retVal);
    }

    @ApiStatus.Internal
    public static RepositorySource buildPackFinder(Map<IModFile, ? extends PathPackResources> modResourcePacks) {
        return packAcceptor -> ServerLifecycleHooks.serverPackFinder(modResourcePacks, packAcceptor);
    }

    private static void serverPackFinder(Map<IModFile, ? extends PathPackResources> modResourcePacks, Consumer<Pack> packAcceptor) {
        for (Map.Entry<IModFile, ? extends PathPackResources> e : modResourcePacks.entrySet()) {
            IModInfo mod = (IModInfo)e.getKey().getModInfos().get(0);
            if (Objects.equals(mod.getModId(), "minecraft")) continue;
            String name = "mod:" + mod.getModId();
            Pack modPack = Pack.m_245429_((String)name, (Component)Component.m_237113_((String)e.getValue().m_5542_()), (boolean)false, id -> (PackResources)e.getValue(), (PackType)PackType.SERVER_DATA, (Pack.Position)Pack.Position.BOTTOM, (PackSource)PackSource.f_10527_);
            if (modPack == null) {
                ModLoader.get().addWarning(new ModLoadingWarning(mod, ModLoadingStage.ERROR, "fml.modloading.brokenresources", new Object[]{e.getKey()}));
                continue;
            }
            LOGGER.debug(Logging.CORE, "Generating PackInfo named {} for mod file {}", (Object)name, (Object)e.getKey().getFilePath());
            packAcceptor.accept(modPack);
        }
    }

    private static void runModifiers(MinecraftServer server) {
        RegistryAccess.Frozen registries = server.m_206579_();
        List<BiomeModifier> biomeModifiers = registries.m_175515_(ForgeRegistries.Keys.BIOME_MODIFIERS).m_203611_().map(Holder::m_203334_).toList();
        List<StructureModifier> structureModifiers = registries.m_175515_(ForgeRegistries.Keys.STRUCTURE_MODIFIERS).m_203611_().map(Holder::m_203334_).toList();
        registries.m_175515_(Registries.f_256952_).m_203611_().forEach(biomeHolder -> ((Biome)biomeHolder.m_203334_()).modifiableBiomeInfo().applyBiomeModifiers((Holder<Biome>)biomeHolder, biomeModifiers));
        registries.m_175515_(Registries.f_256944_).m_203611_().forEach(structureHolder -> ((Structure)structureHolder.m_203334_()).modifiableStructureInfo().applyStructureModifiers((Holder<Structure>)structureHolder, structureModifiers));
    }

    static {
        allowLogins = new AtomicBoolean(false);
    }
}

