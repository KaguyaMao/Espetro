/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.network.NetworkEvent$Context
 *  net.minecraftforge.network.PacketDistributor
 */
package org.espetro.network;

import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import org.espetro.mapconfig.BattlefieldContext;
import org.espetro.network.GameStateResponsePacket;
import org.espetro.network.NetworkManager;
import org.espetro.team.ClassCountManager;
import org.espetro.team.ClassSelectManager;
import org.espetro.team.GamePhase;
import org.espetro.team.GameStateManager;
import org.espetro.team.VoteManager;

public class RequestGameStatePacket {
    public static RequestGameStatePacket read(FriendlyByteBuf buf) {
        return new RequestGameStatePacket();
    }

    public void write(FriendlyByteBuf buf) {
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (((NetworkEvent.Context)ctx.get()).getDirection().getReceptionSide().isServer()) {
                ServerPlayer player = ((NetworkEvent.Context)ctx.get()).getSender();
                if (player == null) {
                    return;
                }
                GameStateManager gsm = GameStateManager.getInstance();
                GamePhase phase = gsm.getCurrentPhase();
                ClassCountManager ccm = ClassCountManager.getInstance();
                String playerTeam = ccm.getPlayerTeam(player.m_20148_());
                String playerFaction = ccm.getPlayerFaction(player.m_20148_());
                int timeRemaining = 0;
                switch (phase) {
                    case DEFEND_COMMANDER_VOTE: 
                    case ATTACK_COMMANDER_VOTE: {
                        timeRemaining = VoteManager.getInstance().getRemainingSeconds();
                        break;
                    }
                    case DEFEND_FACTION_SELECT: 
                    case ATTACK_FACTION_SELECT: {
                        timeRemaining = ClassSelectManager.getInstance().getRemainingSeconds();
                        break;
                    }
                    case DEPLOYING: {
                        timeRemaining = gsm.getDeployTimeRemainingSeconds();
                        break;
                    }
                    default: {
                        timeRemaining = 0;
                    }
                }
                String activeTeam = phase.getActiveTeam();
                GameStateResponsePacket response = new GameStateResponsePacket(phase.name(), playerTeam, playerFaction, activeTeam, timeRemaining, gsm.getCurrentMapFolder(), BattlefieldContext.getObjectiveMode());
                NetworkManager.NET.send(PacketDistributor.PLAYER.with(() -> player), (Object)response);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}

