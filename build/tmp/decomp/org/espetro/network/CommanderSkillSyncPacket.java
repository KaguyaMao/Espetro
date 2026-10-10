/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.network.NetworkEvent$Context
 */
package org.espetro.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.espetro.team.CommanderSkillManager;

public class CommanderSkillSyncPacket {
    private final boolean isCommander;
    private final Map<String, Integer> cooldowns;
    private final List<CommanderSkillManager.SkillView> skills;

    public CommanderSkillSyncPacket(boolean isCommander, Map<String, Integer> cooldowns) {
        this(isCommander, cooldowns, List.of());
    }

    public CommanderSkillSyncPacket(boolean isCommander, Map<String, Integer> cooldowns, List<CommanderSkillManager.SkillView> skills) {
        this.isCommander = isCommander;
        this.cooldowns = cooldowns != null ? cooldowns : new HashMap();
        this.skills = skills != null ? List.copyOf(skills) : List.of();
    }

    public static CommanderSkillSyncPacket read(FriendlyByteBuf buf) {
        boolean isCommander = buf.readBoolean();
        int size = buf.m_130242_();
        HashMap<String, Integer> cooldowns = new HashMap<String, Integer>();
        for (int i = 0; i < size; ++i) {
            String key = buf.m_130277_();
            int value = buf.m_130242_();
            cooldowns.put(key, value);
        }
        int skillCount = buf.m_130242_();
        ArrayList<CommanderSkillManager.SkillView> skills = new ArrayList<CommanderSkillManager.SkillView>(skillCount);
        for (int i = 0; i < skillCount; ++i) {
            skills.add(new CommanderSkillManager.SkillView(buf.m_130136_(128), buf.m_130136_(128), buf.m_130136_(512), buf.m_130136_(512), buf.m_130136_(256)));
        }
        return new CommanderSkillSyncPacket(isCommander, cooldowns, skills);
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeBoolean(this.isCommander);
        buf.m_130130_(this.cooldowns.size());
        for (Map.Entry<String, Integer> entry : this.cooldowns.entrySet()) {
            buf.m_130070_(entry.getKey());
            buf.m_130130_(entry.getValue());
        }
        buf.m_130130_(this.skills.size());
        for (CommanderSkillManager.SkillView skill : this.skills) {
            buf.m_130072_(CommanderSkillSyncPacket.limit(skill.id(), 128), 128);
            buf.m_130072_(CommanderSkillSyncPacket.limit(skill.displayName(), 128), 128);
            buf.m_130072_(CommanderSkillSyncPacket.limit(skill.description(), 512), 512);
            buf.m_130072_(CommanderSkillSyncPacket.limit(skill.stats(), 512), 512);
            buf.m_130072_(CommanderSkillSyncPacket.limit(skill.icon(), 256), 256);
        }
    }

    private static String limit(String value, int maxLength) {
        if (value == null) {
            return "";
        }
        return value.length() <= maxLength ? value : value.substring(0, maxLength);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            try {
                Class.forName("org.espetro.client.ClientPacketHandlers").getMethod("handleCommanderSkillSync", CommanderSkillSyncPacket.class).invoke(null, this);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        });
        ctx.get().setPacketHandled(true);
    }

    public boolean isCommander() {
        return this.isCommander;
    }

    public Map<String, Integer> getCooldowns() {
        return this.cooldowns;
    }

    public List<CommanderSkillManager.SkillView> getSkills() {
        return this.skills;
    }
}

