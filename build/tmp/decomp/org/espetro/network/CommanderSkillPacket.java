/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.network.NetworkEvent$Context
 */
package org.espetro.network;

import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import org.espetro.network.NetworkManager;
import org.espetro.team.CommanderSkillManager;
import org.espetro.team.CommanderSkillType;

public class CommanderSkillPacket {
    private final String skillId;

    public CommanderSkillPacket(String skillId) {
        this.skillId = skillId != null ? skillId : "";
    }

    public static CommanderSkillPacket query() {
        return new CommanderSkillPacket("");
    }

    public static CommanderSkillPacket activate(CommanderSkillType type) {
        return new CommanderSkillPacket(type.getId());
    }

    public static CommanderSkillPacket read(FriendlyByteBuf buf) {
        String skillId = buf.m_130136_(128);
        return new CommanderSkillPacket(skillId);
    }

    public void write(FriendlyByteBuf buf) {
        buf.m_130072_(this.skillId.length() <= 128 ? this.skillId : this.skillId.substring(0, 128), 128);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ((NetworkEvent.Context)ctx.get()).getSender();
            if (player == null) {
                return;
            }
            if (this.skillId.isEmpty()) {
                NetworkManager.sendCommanderSkillSync(player);
                return;
            }
            CommanderSkillManager.getInstance().activateSkill(player, this.skillId);
        });
        ctx.get().setPacketHandled(true);
    }
}

