/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraftforge.network.NetworkDirection
 *  net.minecraftforge.network.NetworkRegistry
 *  net.minecraftforge.network.simple.SimpleChannel
 */
package com.example.espoints.network;

import com.example.espoints.network.OpenArtillerySupportMapMessage;
import com.example.espoints.network.PlaceTacticalMarkerMessage;
import com.example.espoints.network.PlayLowReinforcementAudioMessage;
import com.example.espoints.network.RemoveTacticalMarkerMessage;
import com.example.espoints.network.RequestCapturePointOverviewMessage;
import com.example.espoints.network.RequestTacticalMapTileMessage;
import com.example.espoints.network.RequestTacticalMarkersMessage;
import com.example.espoints.network.SelectArtillerySupportTargetMessage;
import com.example.espoints.network.ShowMessagePopupMessage;
import com.example.espoints.network.SyncBastionsMessage;
import com.example.espoints.network.SyncCapturePointOverviewMessage;
import com.example.espoints.network.SyncCapturePointsMessage;
import com.example.espoints.network.SyncConfigMessage;
import com.example.espoints.network.SyncMapPlayerDisplayMessage;
import com.example.espoints.network.SyncOperationModeMessage;
import com.example.espoints.network.SyncPlayerIdentityMessage;
import com.example.espoints.network.SyncPlayerPositionsMessage;
import com.example.espoints.network.SyncTacticalMapBackgroundMessage;
import com.example.espoints.network.SyncTacticalMapConfigMessage;
import com.example.espoints.network.SyncTacticalMapTileMessage;
import com.example.espoints.network.SyncTacticalMarkersMessage;
import com.example.espoints.network.TacticalMapSubscriptionMessage;
import com.example.espoints.network.TacticalMarkerDeltaMessage;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class NetworkHandler {
    public static final String PROTOCOL_VERSION = "15";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel((ResourceLocation)ResourceLocation.fromNamespaceAndPath((String)"espoints", (String)"main"), () -> "15", "15"::equals, "15"::equals);
    private static int packetId = 0;

    private static int nextPacketId() {
        return packetId++;
    }

    public static void registerMessages() {
        INSTANCE.messageBuilder(SyncCapturePointsMessage.class, NetworkHandler.nextPacketId(), NetworkDirection.PLAY_TO_CLIENT).encoder(SyncCapturePointsMessage::encode).decoder(SyncCapturePointsMessage::decode).consumerMainThread(SyncCapturePointsMessage::handle).add();
        INSTANCE.messageBuilder(SyncConfigMessage.class, NetworkHandler.nextPacketId(), NetworkDirection.PLAY_TO_CLIENT).encoder(SyncConfigMessage::encode).decoder(SyncConfigMessage::decode).consumerMainThread(SyncConfigMessage::handle).add();
        INSTANCE.messageBuilder(ShowMessagePopupMessage.class, NetworkHandler.nextPacketId(), NetworkDirection.PLAY_TO_CLIENT).encoder(ShowMessagePopupMessage::encode).decoder(ShowMessagePopupMessage::decode).consumerMainThread(ShowMessagePopupMessage::handle).add();
        INSTANCE.messageBuilder(SyncOperationModeMessage.class, NetworkHandler.nextPacketId(), NetworkDirection.PLAY_TO_CLIENT).encoder(SyncOperationModeMessage::encode).decoder(SyncOperationModeMessage::decode).consumerMainThread(SyncOperationModeMessage::handle).add();
        INSTANCE.messageBuilder(SyncPlayerPositionsMessage.class, NetworkHandler.nextPacketId(), NetworkDirection.PLAY_TO_CLIENT).encoder(SyncPlayerPositionsMessage::encode).decoder(SyncPlayerPositionsMessage::decode).consumerMainThread(SyncPlayerPositionsMessage::handle).add();
        INSTANCE.messageBuilder(SyncPlayerIdentityMessage.class, NetworkHandler.nextPacketId(), NetworkDirection.PLAY_TO_CLIENT).encoder(SyncPlayerIdentityMessage::encode).decoder(SyncPlayerIdentityMessage::decode).consumerMainThread(SyncPlayerIdentityMessage::handle).add();
        INSTANCE.messageBuilder(PlayLowReinforcementAudioMessage.class, NetworkHandler.nextPacketId(), NetworkDirection.PLAY_TO_CLIENT).encoder(PlayLowReinforcementAudioMessage::encode).decoder(PlayLowReinforcementAudioMessage::decode).consumerMainThread(PlayLowReinforcementAudioMessage::handle).add();
        INSTANCE.messageBuilder(SyncMapPlayerDisplayMessage.class, NetworkHandler.nextPacketId(), NetworkDirection.PLAY_TO_CLIENT).encoder(SyncMapPlayerDisplayMessage::encode).decoder(SyncMapPlayerDisplayMessage::decode).consumerMainThread(SyncMapPlayerDisplayMessage::handle).add();
        INSTANCE.messageBuilder(SyncTacticalMapConfigMessage.class, NetworkHandler.nextPacketId(), NetworkDirection.PLAY_TO_CLIENT).encoder(SyncTacticalMapConfigMessage::encode).decoder(SyncTacticalMapConfigMessage::decode).consumerMainThread(SyncTacticalMapConfigMessage::handle).add();
        INSTANCE.messageBuilder(SyncTacticalMapBackgroundMessage.class, NetworkHandler.nextPacketId(), NetworkDirection.PLAY_TO_CLIENT).encoder(SyncTacticalMapBackgroundMessage::encode).decoder(SyncTacticalMapBackgroundMessage::decode).consumerMainThread(SyncTacticalMapBackgroundMessage::handle).add();
        INSTANCE.messageBuilder(SyncBastionsMessage.class, NetworkHandler.nextPacketId(), NetworkDirection.PLAY_TO_CLIENT).encoder(SyncBastionsMessage::encode).decoder(SyncBastionsMessage::decode).consumerMainThread(SyncBastionsMessage::handle).add();
        INSTANCE.messageBuilder(SyncCapturePointOverviewMessage.class, NetworkHandler.nextPacketId(), NetworkDirection.PLAY_TO_CLIENT).encoder(SyncCapturePointOverviewMessage::encode).decoder(SyncCapturePointOverviewMessage::decode).consumerMainThread(SyncCapturePointOverviewMessage::handle).add();
        INSTANCE.messageBuilder(RequestCapturePointOverviewMessage.class, NetworkHandler.nextPacketId(), NetworkDirection.PLAY_TO_SERVER).encoder(RequestCapturePointOverviewMessage::encode).decoder(RequestCapturePointOverviewMessage::decode).consumerMainThread(RequestCapturePointOverviewMessage::handle).add();
        INSTANCE.messageBuilder(PlaceTacticalMarkerMessage.class, NetworkHandler.nextPacketId(), NetworkDirection.PLAY_TO_SERVER).encoder(PlaceTacticalMarkerMessage::encode).decoder(PlaceTacticalMarkerMessage::decode).consumerMainThread(PlaceTacticalMarkerMessage::handle).add();
        INSTANCE.messageBuilder(RequestTacticalMarkersMessage.class, NetworkHandler.nextPacketId(), NetworkDirection.PLAY_TO_SERVER).encoder(RequestTacticalMarkersMessage::encode).decoder(RequestTacticalMarkersMessage::decode).consumerMainThread(RequestTacticalMarkersMessage::handle).add();
        INSTANCE.messageBuilder(SyncTacticalMarkersMessage.class, NetworkHandler.nextPacketId(), NetworkDirection.PLAY_TO_CLIENT).encoder(SyncTacticalMarkersMessage::encode).decoder(SyncTacticalMarkersMessage::decode).consumerMainThread(SyncTacticalMarkersMessage::handle).add();
        INSTANCE.messageBuilder(TacticalMarkerDeltaMessage.class, NetworkHandler.nextPacketId(), NetworkDirection.PLAY_TO_CLIENT).encoder(TacticalMarkerDeltaMessage::encode).decoder(TacticalMarkerDeltaMessage::decode).consumerMainThread(TacticalMarkerDeltaMessage::handle).add();
        INSTANCE.messageBuilder(RemoveTacticalMarkerMessage.class, NetworkHandler.nextPacketId(), NetworkDirection.PLAY_TO_SERVER).encoder(RemoveTacticalMarkerMessage::encode).decoder(RemoveTacticalMarkerMessage::decode).consumerMainThread(RemoveTacticalMarkerMessage::handle).add();
        INSTANCE.messageBuilder(OpenArtillerySupportMapMessage.class, NetworkHandler.nextPacketId(), NetworkDirection.PLAY_TO_CLIENT).encoder(OpenArtillerySupportMapMessage::encode).decoder(OpenArtillerySupportMapMessage::decode).consumerMainThread(OpenArtillerySupportMapMessage::handle).add();
        INSTANCE.messageBuilder(SelectArtillerySupportTargetMessage.class, NetworkHandler.nextPacketId(), NetworkDirection.PLAY_TO_SERVER).encoder(SelectArtillerySupportTargetMessage::encode).decoder(SelectArtillerySupportTargetMessage::decode).consumerMainThread(SelectArtillerySupportTargetMessage::handle).add();
        INSTANCE.messageBuilder(TacticalMapSubscriptionMessage.class, NetworkHandler.nextPacketId(), NetworkDirection.PLAY_TO_SERVER).encoder(TacticalMapSubscriptionMessage::encode).decoder(TacticalMapSubscriptionMessage::decode).consumerMainThread(TacticalMapSubscriptionMessage::handle).add();
        INSTANCE.messageBuilder(RequestTacticalMapTileMessage.class, NetworkHandler.nextPacketId(), NetworkDirection.PLAY_TO_SERVER).encoder(RequestTacticalMapTileMessage::encode).decoder(RequestTacticalMapTileMessage::decode).consumerMainThread(RequestTacticalMapTileMessage::handle).add();
        INSTANCE.messageBuilder(SyncTacticalMapTileMessage.class, NetworkHandler.nextPacketId(), NetworkDirection.PLAY_TO_CLIENT).encoder(SyncTacticalMapTileMessage::encode).decoder(SyncTacticalMapTileMessage::decode).consumerMainThread(SyncTacticalMapTileMessage::handle).add();
    }
}

