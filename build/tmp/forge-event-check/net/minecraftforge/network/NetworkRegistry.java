/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.resources.ResourceLocation
 *  org.apache.commons.lang3.tuple.Pair
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.apache.logging.log4j.Marker
 *  org.apache.logging.log4j.MarkerManager
 */
package net.minecraftforge.network;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkInstance;
import net.minecraftforge.network.ServerStatusPing;
import net.minecraftforge.network.event.EventNetworkChannel;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.registries.DataPackRegistriesHooks;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class NetworkRegistry {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Marker NETREGISTRY = MarkerManager.getMarker((String)"NETREGISTRY");
    private static Map<ResourceLocation, NetworkInstance> instances = Collections.synchronizedMap(new HashMap());
    public static ServerStatusPing.ChannelData ABSENT = new ServerStatusPing.ChannelData(new ResourceLocation("absent"), "ABSENT \ud83e\udd14", false);
    public static String ACCEPTVANILLA = new String("ALLOWVANILLA \ud83d\udc93\ud83d\udc93\ud83d\udc93");
    private static boolean lock = false;

    public static Predicate<String> acceptMissingOr(String protocolVersion) {
        return NetworkRegistry.acceptMissingOr(protocolVersion::equals);
    }

    public static Predicate<String> acceptMissingOr(Predicate<String> versionCheck) {
        return versionCheck.or(ABSENT.version()::equals).or(ACCEPTVANILLA::equals);
    }

    public static List<String> getServerNonVanillaNetworkMods() {
        return NetworkRegistry.listRejectedVanillaMods(NetworkInstance::tryClientVersionOnServer);
    }

    public static List<String> getClientNonVanillaNetworkMods() {
        return NetworkRegistry.listRejectedVanillaMods(NetworkInstance::tryServerVersionOnClient);
    }

    public static boolean acceptsVanillaClientConnections() {
        return (instances.isEmpty() || NetworkRegistry.getServerNonVanillaNetworkMods().isEmpty()) && DataPackRegistriesHooks.getSyncedCustomRegistries().isEmpty();
    }

    public static boolean canConnectToVanillaServer() {
        return instances.isEmpty() || NetworkRegistry.getClientNonVanillaNetworkMods().isEmpty();
    }

    public static SimpleChannel newSimpleChannel(ResourceLocation name, Supplier<String> networkProtocolVersion, Predicate<String> clientAcceptedVersions, Predicate<String> serverAcceptedVersions) {
        return new SimpleChannel(NetworkRegistry.createInstance(name, networkProtocolVersion, clientAcceptedVersions, serverAcceptedVersions));
    }

    public static EventNetworkChannel newEventChannel(ResourceLocation name, Supplier<String> networkProtocolVersion, Predicate<String> clientAcceptedVersions, Predicate<String> serverAcceptedVersions) {
        return new EventNetworkChannel(NetworkRegistry.createInstance(name, networkProtocolVersion, clientAcceptedVersions, serverAcceptedVersions));
    }

    private static NetworkInstance createInstance(ResourceLocation name, Supplier<String> networkProtocolVersion, Predicate<String> clientAcceptedVersions, Predicate<String> serverAcceptedVersions) {
        if (lock) {
            LOGGER.error(NETREGISTRY, "Attempted to register channel {} even though registry phase is over", (Object)name);
            throw new IllegalArgumentException("Registration of impl channels is locked");
        }
        if (instances.containsKey(name)) {
            LOGGER.error(NETREGISTRY, "NetworkDirection channel {} already registered.", (Object)name);
            throw new IllegalArgumentException("NetworkDirection Channel {" + String.valueOf(name) + "} already registered");
        }
        NetworkInstance networkInstance = new NetworkInstance(name, networkProtocolVersion, clientAcceptedVersions, serverAcceptedVersions);
        instances.put(name, networkInstance);
        return networkInstance;
    }

    static Optional<NetworkInstance> findTarget(ResourceLocation resourceLocation) {
        return Optional.ofNullable(instances.get(resourceLocation));
    }

    static Map<ResourceLocation, String> buildChannelVersions() {
        return instances.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> ((NetworkInstance)e.getValue()).getNetworkProtocolVersion()));
    }

    static Map<ResourceLocation, ServerStatusPing.ChannelData> buildChannelVersionsForListPing() {
        return instances.entrySet().stream().filter(p -> !((ResourceLocation)p.getKey()).m_135827_().equals("fml")).collect(Collectors.toMap(Map.Entry::getKey, val -> new ServerStatusPing.ChannelData((ResourceLocation)val.getKey(), ((NetworkInstance)val.getValue()).getNetworkProtocolVersion(), ((NetworkInstance)val.getValue()).tryClientVersionOnServer(ABSENT.version()))));
    }

    static List<String> listRejectedVanillaMods(BiFunction<NetworkInstance, String, Boolean> testFunction) {
        List<Pair> results = instances.values().stream().map(ni -> {
            String incomingVersion = ACCEPTVANILLA;
            boolean test = (Boolean)testFunction.apply((NetworkInstance)ni, incomingVersion);
            LOGGER.debug(NETREGISTRY, "Channel '{}' : Vanilla acceptance test: {}", (Object)ni.getChannelName(), (Object)(test ? "ACCEPTED" : "REJECTED"));
            return Pair.of((Object)ni.getChannelName(), (Object)test);
        }).filter(p -> (Boolean)p.getRight() == false).toList();
        if (!results.isEmpty()) {
            LOGGER.error(NETREGISTRY, "Channels [{}] rejected vanilla connections", (Object)results.stream().map(Pair::getLeft).map(Object::toString).collect(Collectors.joining(",")));
            return results.stream().map(Pair::getLeft).map(Object::toString).collect(Collectors.toList());
        }
        LOGGER.debug(NETREGISTRY, "Accepting channel list from vanilla");
        return Collections.emptyList();
    }

    static Map<ResourceLocation, String> validateClientChannels(Map<ResourceLocation, String> channels) {
        return NetworkRegistry.validateChannels(channels, "server", NetworkInstance::tryServerVersionOnClient);
    }

    static Map<ResourceLocation, String> validateServerChannels(Map<ResourceLocation, String> channels) {
        return NetworkRegistry.validateChannels(channels, "client", NetworkInstance::tryClientVersionOnServer);
    }

    private static Map<ResourceLocation, String> validateChannels(Map<ResourceLocation, String> incoming, String originName, BiFunction<NetworkInstance, String, Boolean> testFunction) {
        Map<ResourceLocation, String> results = instances.values().stream().map(ni -> {
            String incomingVersion = incoming.getOrDefault(ni.getChannelName(), ABSENT.version());
            boolean test = (Boolean)testFunction.apply((NetworkInstance)ni, incomingVersion);
            LOGGER.debug(NETREGISTRY, "Channel '{}' : Version test of '{}' from {} : {}", (Object)ni.getChannelName(), (Object)incomingVersion, (Object)originName, (Object)(test ? "ACCEPTED" : "REJECTED"));
            return Pair.of((Object)Pair.of((Object)ni.getChannelName(), (Object)incomingVersion), (Object)test);
        }).filter(p -> (Boolean)p.getRight() == false).map(Pair::getLeft).collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
        if (!results.isEmpty()) {
            LOGGER.error(NETREGISTRY, "Channels [{}] rejected their {} side version number", (Object)results.keySet().stream().map(Object::toString).collect(Collectors.joining(",")), (Object)originName);
            return results;
        }
        LOGGER.debug(NETREGISTRY, "Accepting channel list from {}", (Object)originName);
        return results;
    }

    static List<LoginPayload> gatherLoginPayloads(NetworkDirection direction, boolean isLocal) {
        if (direction != NetworkDirection.LOGIN_TO_CLIENT) {
            return Collections.emptyList();
        }
        ArrayList<LoginPayload> gatheredPayloads = new ArrayList<LoginPayload>();
        instances.values().forEach(ni -> ni.dispatchGatherLogin(gatheredPayloads, isLocal));
        return gatheredPayloads;
    }

    public static boolean checkListPingCompatibilityForClient(Map<ResourceLocation, ServerStatusPing.ChannelData> incoming) {
        HashSet handled = new HashSet();
        List results = instances.values().stream().filter(p -> !p.getChannelName().m_135827_().equals("fml")).map(ni -> {
            ServerStatusPing.ChannelData incomingVersion = incoming.getOrDefault(ni.getChannelName(), ABSENT);
            boolean test = ni.tryServerVersionOnClient(incomingVersion.version());
            handled.add(ni.getChannelName());
            LOGGER.debug(NETREGISTRY, "Channel '{}' : Version test of '{}' during listping : {}", (Object)ni.getChannelName(), (Object)incomingVersion, (Object)(test ? "ACCEPTED" : "REJECTED"));
            return Pair.of((Object)ni.getChannelName(), (Object)test);
        }).filter(p -> (Boolean)p.getRight() == false).collect(Collectors.toList());
        List missingButRequired = incoming.entrySet().stream().filter(p -> !((ResourceLocation)p.getKey()).m_135827_().equals("fml")).filter(p -> !((ServerStatusPing.ChannelData)p.getValue()).required()).filter(p -> !handled.contains(p.getKey())).map(Map.Entry::getKey).collect(Collectors.toList());
        if (!results.isEmpty()) {
            LOGGER.error(NETREGISTRY, "Channels [{}] rejected their server side version number during listping", (Object)results.stream().map(Pair::getLeft).map(Object::toString).collect(Collectors.joining(",")));
            return false;
        }
        if (!missingButRequired.isEmpty()) {
            LOGGER.error(NETREGISTRY, "The server is likely to require channel [{}] to be present, yet we don't have it", missingButRequired);
            return false;
        }
        LOGGER.debug(NETREGISTRY, "Accepting channel list during listping");
        return true;
    }

    public boolean isLocked() {
        return lock;
    }

    public static void lock() {
        lock = true;
    }

    public static class ChannelBuilder {
        private ResourceLocation channelName;
        private Supplier<String> networkProtocolVersion;
        private Predicate<String> clientAcceptedVersions;
        private Predicate<String> serverAcceptedVersions;

        public static ChannelBuilder named(ResourceLocation channelName) {
            ChannelBuilder builder = new ChannelBuilder();
            builder.channelName = channelName;
            return builder;
        }

        public ChannelBuilder networkProtocolVersion(Supplier<String> networkProtocolVersion) {
            this.networkProtocolVersion = networkProtocolVersion;
            return this;
        }

        public ChannelBuilder clientAcceptedVersions(Predicate<String> clientAcceptedVersions) {
            this.clientAcceptedVersions = clientAcceptedVersions;
            return this;
        }

        public ChannelBuilder serverAcceptedVersions(Predicate<String> serverAcceptedVersions) {
            this.serverAcceptedVersions = serverAcceptedVersions;
            return this;
        }

        private NetworkInstance createNetworkInstance() {
            return NetworkRegistry.createInstance(this.channelName, this.networkProtocolVersion, this.clientAcceptedVersions, this.serverAcceptedVersions);
        }

        public SimpleChannel simpleChannel() {
            return new SimpleChannel(this.createNetworkInstance());
        }

        public EventNetworkChannel eventNetworkChannel() {
            return new EventNetworkChannel(this.createNetworkInstance());
        }
    }

    public static class LoginPayload {
        private final FriendlyByteBuf data;
        private final ResourceLocation channelName;
        private final String messageContext;
        private final boolean needsResponse;

        public LoginPayload(FriendlyByteBuf buffer, ResourceLocation channelName, String messageContext) {
            this(buffer, channelName, messageContext, true);
        }

        public LoginPayload(FriendlyByteBuf buffer, ResourceLocation channelName, String messageContext, boolean needsResponse) {
            this.data = buffer;
            this.channelName = channelName;
            this.messageContext = messageContext;
            this.needsResponse = needsResponse;
        }

        public FriendlyByteBuf getData() {
            return this.data;
        }

        public ResourceLocation getChannelName() {
            return this.channelName;
        }

        public String getMessageContext() {
            return this.messageContext;
        }

        public boolean needsResponse() {
            return this.needsResponse;
        }
    }
}

