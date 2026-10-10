/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraftforge.network.NetworkEvent$Context
 *  org.espetro.api.EspetroAPI
 */
package com.example.espoints.network;

import com.example.espoints.config.TacticalMapJsonConfig;
import com.example.espoints.network.PacketValidation;
import com.example.espoints.tactical.TacticalMarkerManager;
import com.example.espoints.tactical.TacticalMarkerType;
import com.example.espoints.util.EspetroTeamBridge;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import org.espetro.api.EspetroAPI;

public class SelectArtillerySupportTargetMessage {
    private final double x;
    private final double z;

    public SelectArtillerySupportTargetMessage(double x, double z) {
        this.x = x;
        this.z = z;
    }

    public static void encode(SelectArtillerySupportTargetMessage message, FriendlyByteBuf buf) {
        buf.writeDouble(message.x);
        buf.writeDouble(message.z);
    }

    public static SelectArtillerySupportTargetMessage decode(FriendlyByteBuf buf) {
        return new SelectArtillerySupportTargetMessage(PacketValidation.checkedCoordinate(buf.readDouble(), "artillery x"), PacketValidation.checkedCoordinate(buf.readDouble(), "artillery z"));
    }

    public static void handle(SelectArtillerySupportTargetMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        ServerPlayer sender = context.getSender();
        context.enqueueWork(() -> {
            if (sender == null) {
                return;
            }
            SelectArtillerySupportTargetMessage.handleServer(sender, message.x, message.z);
        });
        context.setPacketHandled(true);
    }

    private static void handleServer(ServerPlayer sender, double x, double z) {
        if (!Double.isFinite(x) || !Double.isFinite(z)) {
            return;
        }
        if (!EspetroAPI.isActiveBattlefield((ServerLevel)sender.m_284548_())) {
            sender.m_213846_((Component)Component.m_237113_((String)"\u00a7c\u8bf7\u8fdb\u5165\u5f53\u524d\u6218\u573a\u540e\u518d\u9009\u62e9\u76ee\u6807\u3002"));
            return;
        }
        TacticalMapJsonConfig.TacticalMapBounds bounds = TacticalMapJsonConfig.getInstance().getBounds();
        if (!bounds.contains(x, z)) {
            sender.m_213846_((Component)Component.m_237113_((String)"\u00a7c155\u706b\u70ae\u652f\u63f4\u5750\u6807\u8d85\u51fa\u6218\u672f\u5730\u56fe\u5141\u8bb8\u8303\u56f4\u3002"));
            return;
        }
        if (!EspetroTeamBridge.submitArtillerySupportTarget(sender, x, z)) {
            sender.m_213846_((Component)Component.m_237113_((String)"\u00a7c155\u706b\u70ae\u652f\u63f4\u5750\u6807\u63d0\u4ea4\u5931\u8d25\u3002"));
            return;
        }
        TacticalMarkerManager.place(sender, TacticalMarkerType.ARTILLERY_TARGET, x, z);
    }
}

