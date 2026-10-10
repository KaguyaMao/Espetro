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
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import org.espetro.team.PartyManager;

public class PartyActionPacket {
    private final Action action;
    private final UUID partyId;
    private final String password;
    private final UUID targetId;

    public PartyActionPacket(Action action, UUID partyId, String password, UUID targetId) {
        this.action = action;
        this.partyId = partyId;
        this.password = password == null ? "" : password;
        this.targetId = targetId;
    }

    public static PartyActionPacket create(String password) {
        return new PartyActionPacket(Action.CREATE, null, password, null);
    }

    public static PartyActionPacket join(UUID partyId, String password) {
        return new PartyActionPacket(Action.JOIN, partyId, password, null);
    }

    public static PartyActionPacket leave() {
        return new PartyActionPacket(Action.LEAVE, null, null, null);
    }

    public static PartyActionPacket kick(UUID partyId, UUID targetId) {
        return new PartyActionPacket(Action.KICK, partyId, null, targetId);
    }

    public static PartyActionPacket toggleLock(UUID partyId) {
        return new PartyActionPacket(Action.TOGGLE_LOCK, partyId, null, null);
    }

    public static PartyActionPacket disband(UUID partyId) {
        return new PartyActionPacket(Action.DISBAND, partyId, null, null);
    }

    public static PartyActionPacket requestList() {
        return new PartyActionPacket(Action.REQUEST_LIST, null, null, null);
    }

    public static PartyActionPacket read(FriendlyByteBuf buf) {
        Action action = Action.values()[buf.readByte()];
        UUID partyId = buf.readBoolean() ? buf.m_130259_() : null;
        String password = buf.readBoolean() ? buf.m_130136_(64) : null;
        UUID targetId = buf.readBoolean() ? buf.m_130259_() : null;
        return new PartyActionPacket(action, partyId, password, targetId);
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeByte(this.action.ordinal());
        buf.writeBoolean(this.partyId != null);
        if (this.partyId != null) {
            buf.m_130077_(this.partyId);
        }
        buf.writeBoolean(this.password != null && !this.password.isEmpty());
        if (this.password != null && !this.password.isEmpty()) {
            buf.m_130072_(this.password, 64);
        }
        buf.writeBoolean(this.targetId != null);
        if (this.targetId != null) {
            buf.m_130077_(this.targetId);
        }
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ((NetworkEvent.Context)ctx.get()).getSender();
            if (player == null) {
                return;
            }
            PartyManager pm = PartyManager.getInstance();
            switch (this.action) {
                case CREATE: {
                    String err = null;
                    if (pm.isInParty(player.m_20148_())) {
                        err = "\u00a7c\u4f60\u5df2\u5728\u961f\u4f0d\u4e2d\uff0c\u8bf7\u5148\u9000\u51fa\u3002";
                    } else {
                        pm.createParty(player, this.password);
                    }
                    if (err == null) break;
                    player.m_213846_(Component.m_237113_(err));
                    break;
                }
                case JOIN: {
                    String joinErr;
                    if (this.partyId == null || (joinErr = pm.joinParty(this.partyId, player, this.password)) == null) break;
                    player.m_213846_(Component.m_237113_("\u00a7c" + joinErr));
                    break;
                }
                case LEAVE: {
                    pm.leaveParty(player.m_20148_());
                    break;
                }
                case KICK: {
                    String kickErr;
                    if (this.partyId == null || this.targetId == null || (kickErr = pm.kickMember(this.partyId, player.m_20148_(), this.targetId)) == null) break;
                    player.m_213846_(Component.m_237113_("\u00a7c" + kickErr));
                    break;
                }
                case TOGGLE_LOCK: {
                    String lockErr;
                    if (this.partyId == null || (lockErr = pm.toggleLock(this.partyId, player.m_20148_())) == null) break;
                    player.m_213846_(Component.m_237113_("\u00a7c" + lockErr));
                    break;
                }
                case DISBAND: {
                    if (this.partyId == null) break;
                    PartyManager.PartyData p = pm.getParty(this.partyId);
                    if (p != null && p.ownerId.equals(player.m_20148_())) {
                        pm.disbandParty(this.partyId);
                        break;
                    }
                    player.m_213846_(Component.m_237113_("\u00a7c\u53ea\u6709\u961f\u957f\u624d\u80fd\u89e3\u6563\u961f\u4f0d\u3002"));
                    break;
                }
                case REQUEST_LIST: {
                    pm.syncToPlayer(player);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }

    public static enum Action {
        CREATE,
        JOIN,
        LEAVE,
        KICK,
        TOGGLE_LOCK,
        DISBAND,
        REQUEST_LIST;

    }
}

