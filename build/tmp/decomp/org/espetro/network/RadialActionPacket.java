/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.network.NetworkEvent$Context
 */
package org.espetro.network;

import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import org.espetro.Espetro;
import org.espetro.bastion.BastionData;
import org.espetro.bastion.BastionManager;
import org.espetro.bastion.DeployActions;
import org.espetro.bastion.FortificationManager;
import org.espetro.logistics.LogisticsConfig;
import org.espetro.team.TeamPackManager;

public record RadialActionPacket(Action action) {
    public static void write(RadialActionPacket packet, FriendlyByteBuf buffer) {
        buffer.m_130068_(packet.action);
    }

    public static RadialActionPacket read(FriendlyByteBuf buffer) {
        return new RadialActionPacket(buffer.m_130066_(Action.class));
    }

    public static void handle(RadialActionPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        ServerPlayer player = context.getSender();
        if (player != null) {
            context.enqueueWork(() -> RadialActionPacket.execute(player, packet.action));
        }
        context.setPacketHandled(true);
    }

    private static void execute(ServerPlayer player, Action action) {
        switch (action) {
            case DEPLOY_RADIO: {
                RadialActionPacket.beginFortification(player, "builtin_radio");
                break;
            }
            case DEPLOY_HAB: {
                RadialActionPacket.beginFortification(player, "builtin_hab");
                break;
            }
            case DEPLOY_VEHICLE: {
                DeployActions.openVehicleDeploy(player);
                break;
            }
            case DEPLOY_RALLY: {
                RadialActionPacket.deployRally(player);
                break;
            }
            case FOB_STATUS: {
                RadialActionPacket.showStatus(player);
            }
        }
    }

    private static void beginFortification(ServerPlayer player, String id) {
        String error = FortificationManager.getInstance().beginPreview(player, id);
        if (error != null) {
            player.m_213846_(Component.m_237113_(error));
        } else {
            player.m_213846_(Component.m_237113_("\u00a7e\u5de6\u952e\u786e\u8ba4\u65bd\u5de5\u8303\u56f4\uff0c\u53f3\u952e\u53d6\u6d88\u3002"));
        }
    }

    private static void deployRally(ServerPlayer player) {
        String error = TeamPackManager.getInstance().giveRallyItem(player);
        player.m_213846_(Component.m_237113_(error != null ? error : "\u00a7a\u5df2\u9886\u53d6 Rally \u90e8\u7f72\u5305\uff0c\u627e\u5230\u5408\u9002\u4f4d\u7f6e\u653e\u7f6e\u3002"));
    }

    private static void showStatus(ServerPlayer player) {
        BastionData bastion = RadialActionPacket.nearestFob(player);
        if (bastion == null) {
            player.m_213846_(Component.m_237113_("\u00a7c\u9644\u8fd1\u6ca1\u6709\u5df1\u65b9 Radio\u3002"));
            return;
        }
        player.m_213846_(Component.m_237113_("\u00a76[Radio] \u00a7f" + bastion.getName() + " \u00a77| \u5efa\u6750 \u00a76" + bastion.getConstructionSupplies() + " \u00a77| \u5f39\u836f \u00a7b" + bastion.getAmmunitionSupplies() + " \u00a77| " + BastionManager.getInstance().getFobStatus(bastion)));
    }

    private static BastionData nearestFob(ServerPlayer player) {
        String team = Espetro.getPlayerTeam(player);
        return BastionManager.getInstance().findNearestRadio(player.m_284548_(), player.m_20183_(), team, LogisticsConfig.get().depositRadius);
    }

    public static enum Action {
        DEPLOY_RADIO,
        DEPLOY_RALLY,
        FOB_STATUS,
        DEPLOY_HAB,
        DEPLOY_VEHICLE;

    }
}

