/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.network.NetworkEvent$Context
 */
package org.espetro.network;

import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import org.espetro.governance.CommanderGovernanceManager;

public class GovernanceActionPacket {
    private final Action action;
    private final UUID candidate;

    public GovernanceActionPacket(Action action, UUID candidate) {
        this.action = action;
        this.candidate = candidate;
    }

    public static GovernanceActionPacket read(FriendlyByteBuf buf) {
        Action a;
        try {
            a = Action.valueOf(buf.m_130277_());
        }
        catch (Exception e) {
            a = Action.START_IMPEACHMENT;
        }
        UUID c = buf.readBoolean() ? buf.m_130259_() : null;
        return new GovernanceActionPacket(a, c);
    }

    public void write(FriendlyByteBuf buf) {
        buf.m_130070_(this.action.name());
        buf.writeBoolean(this.candidate != null);
        if (this.candidate != null) {
            buf.m_130077_(this.candidate);
        }
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ((NetworkEvent.Context)ctx.get()).getSender();
            if (player == null) {
                return;
            }
            CommanderGovernanceManager mgr = CommanderGovernanceManager.getInstance();
            switch (this.action) {
                case START_IMPEACHMENT: {
                    mgr.tryStartImpeachment(player);
                    break;
                }
                case VOTE_IMPEACHMENT: {
                    if (this.candidate == null) break;
                    mgr.castImpeachmentVote(player, this.candidate);
                    break;
                }
                case VOLUNTEER_VACANCY: {
                    mgr.volunteerForVacancy(player);
                    break;
                }
                case VOTE_VACANCY: {
                    if (this.candidate == null) break;
                    mgr.castVacancyVote(player, this.candidate);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }

    public static enum Action {
        START_IMPEACHMENT,
        VOTE_IMPEACHMENT,
        VOLUNTEER_VACANCY,
        VOTE_VACANCY;

    }
}

