/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.team;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.server.level.ServerPlayer;
import org.espetro.network.NetworkManager;

public final class PartyManager {
    private static PartyManager INSTANCE;
    private static int maxPartySize;
    private final Map<UUID, PartyData> partiesByOwner = new LinkedHashMap<UUID, PartyData>();
    private final Map<UUID, UUID> playerPartyMap = new HashMap<UUID, UUID>();

    private PartyManager() {
        INSTANCE = this;
    }

    public static PartyManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PartyManager();
        }
        return INSTANCE;
    }

    public Collection<PartyData> getParties() {
        return Collections.unmodifiableCollection(this.partiesByOwner.values());
    }

    public PartyData getParty(UUID partyId) {
        return this.partiesByOwner.get(partyId);
    }

    public PartyData getPartyByMember(UUID playerId) {
        UUID partyId = this.playerPartyMap.get(playerId);
        return partyId != null ? this.partiesByOwner.get(partyId) : null;
    }

    public boolean isInParty(UUID playerId) {
        return this.playerPartyMap.containsKey(playerId);
    }

    public int getPartySize(UUID partyId) {
        PartyData p = this.partiesByOwner.get(partyId);
        return p != null ? p.members.size() : 0;
    }

    public PartyData createParty(ServerPlayer owner, String password) {
        if (this.isInParty(owner.m_20148_())) {
            return null;
        }
        UUID partyId = UUID.randomUUID();
        PartyData party = new PartyData(partyId, owner.m_20148_(), owner.m_7755_().getString(), password);
        party.members.add(owner.m_20148_());
        this.partiesByOwner.put(partyId, party);
        this.playerPartyMap.put(owner.m_20148_(), partyId);
        this.broadcastPartyList();
        return party;
    }

    public void disbandParty(UUID partyId) {
        PartyData party = this.partiesByOwner.remove(partyId);
        if (party == null) {
            return;
        }
        for (UUID m : party.members) {
            this.playerPartyMap.remove(m);
        }
        this.broadcastPartyList();
    }

    public List<UUID> disbandLargestParty() {
        if (this.partiesByOwner.isEmpty()) {
            return List.of();
        }
        PartyData largest = null;
        for (PartyData p : this.partiesByOwner.values()) {
            if (largest != null && p.members.size() <= largest.members.size()) continue;
            largest = p;
        }
        if (largest == null) {
            return List.of();
        }
        ArrayList<UUID> members = new ArrayList<UUID>(largest.members);
        this.disbandParty(largest.partyId);
        return members;
    }

    public String joinParty(UUID partyId, ServerPlayer player, String password) {
        PartyData party = this.partiesByOwner.get(partyId);
        if (party == null) {
            return "\u8be5\u961f\u4f0d\u4e0d\u5b58\u5728\u3002";
        }
        if (this.isInParty(player.m_20148_())) {
            return "\u4f60\u5df2\u5728\u5176\u4ed6\u961f\u4f0d\u4e2d\uff0c\u8bf7\u5148\u9000\u51fa\u3002";
        }
        if (party.locked) {
            return "\u8be5\u961f\u4f0d\u5df2\u9501\u5b9a\uff0c\u65e0\u6cd5\u52a0\u5165\u3002";
        }
        if (party.members.size() >= maxPartySize) {
            return "\u8be5\u961f\u4f0d\u5df2\u6ee1\u5458\uff08\u4e0a\u9650 " + maxPartySize + " \u4eba\uff09\u3002";
        }
        if (party.password != null && !party.password.isEmpty() && !party.password.equals(password)) {
            return "\u5bc6\u7801\u9519\u8bef\u3002";
        }
        party.members.add(player.m_20148_());
        this.playerPartyMap.put(player.m_20148_(), partyId);
        this.broadcastPartyList();
        return null;
    }

    public boolean leaveParty(UUID playerId) {
        UUID partyId = this.playerPartyMap.remove(playerId);
        if (partyId == null) {
            return false;
        }
        PartyData party = this.partiesByOwner.get(partyId);
        if (party == null) {
            return false;
        }
        party.members.remove(playerId);
        if (party.members.isEmpty()) {
            this.partiesByOwner.remove(partyId);
        }
        this.broadcastPartyList();
        return true;
    }

    public void removePlayerFromAnyParty(UUID playerId) {
        this.leaveParty(playerId);
    }

    public String kickMember(UUID partyId, UUID kickerId, UUID targetId) {
        PartyData party = this.partiesByOwner.get(partyId);
        if (party == null) {
            return "\u961f\u4f0d\u4e0d\u5b58\u5728\u3002";
        }
        if (!party.ownerId.equals(kickerId)) {
            return "\u53ea\u6709\u961f\u957f\u624d\u80fd\u8e22\u4eba\u3002";
        }
        if (kickerId.equals(targetId)) {
            return "\u4e0d\u80fd\u8e22\u81ea\u5df1\uff0c\u8bf7\u4f7f\u7528\u89e3\u6563\u961f\u4f0d\u3002";
        }
        party.members.remove(targetId);
        this.playerPartyMap.remove(targetId);
        if (party.members.isEmpty()) {
            this.partiesByOwner.remove(partyId);
        }
        this.broadcastPartyList();
        return null;
    }

    public String toggleLock(UUID partyId, UUID ownerId) {
        PartyData party = this.partiesByOwner.get(partyId);
        if (party == null) {
            return "\u961f\u4f0d\u4e0d\u5b58\u5728\u3002";
        }
        if (!party.ownerId.equals(ownerId)) {
            return "\u53ea\u6709\u961f\u957f\u624d\u80fd\u64cd\u4f5c\u3002";
        }
        party.locked = !party.locked;
        this.broadcastPartyList();
        return null;
    }

    public void broadcastPartyList() {
        NetworkManager.broadcastPartyList(this);
    }

    public void syncToPlayer(ServerPlayer player) {
        NetworkManager.sendPartyListTo(player);
    }

    public static int getMaxPartySize() {
        return maxPartySize;
    }

    public static void setMaxPartySize(int size) {
        maxPartySize = Math.max(1, size);
    }

    public void clearAll() {
        this.partiesByOwner.clear();
        this.playerPartyMap.clear();
        this.broadcastPartyList();
    }

    public Map<UUID, String> computeTeamAssignment(List<ServerPlayer> allPlayers) {
        LinkedHashMap<UUID, String> result = new LinkedHashMap<UUID, String>();
        ArrayList<UUID> unassigned = new ArrayList<UUID>();
        LinkedHashMap<UUID, List> partyGroups = new LinkedHashMap<UUID, List>();
        HashSet<UUID> handled = new HashSet<UUID>();
        for (ServerPlayer p : allPlayers) {
            UUID uid = p.m_20148_();
            if (handled.contains(uid)) continue;
            PartyData party = this.getPartyByMember(uid);
            if (party != null) {
                partyGroups.computeIfAbsent(party.partyId, k -> new ArrayList()).addAll(party.members);
                handled.addAll(party.members);
                continue;
            }
            unassigned.add(uid);
            handled.add(uid);
        }
        int attack = 0;
        int defend = 0;
        ArrayList sortedGroups = new ArrayList(partyGroups.values());
        sortedGroups.sort((a, b) -> Integer.compare(b.size(), a.size()));
        for (List group : sortedGroups) {
            if (attack <= defend) {
                for (UUID uid : group) {
                    result.put(uid, "ATTACK");
                }
                attack += group.size();
                continue;
            }
            for (UUID uid : group) {
                result.put(uid, "DEFEND");
            }
            defend += group.size();
        }
        while (Math.abs(attack - defend) > 2 && !partyGroups.isEmpty()) {
            String overTeam = attack > defend ? "ATTACK" : "DEFEND";
            PartyData largestParty = null;
            for (UUID pid : partyGroups.keySet()) {
                PartyData p = this.partiesByOwner.get(pid);
                if (p == null) continue;
                boolean onOverTeam = false;
                for (UUID m : p.members) {
                    if (!overTeam.equals(result.get(m))) continue;
                    onOverTeam = true;
                    break;
                }
                if (!onOverTeam || largestParty != null && p.members.size() <= largestParty.members.size()) continue;
                largestParty = p;
            }
            if (largestParty == null) break;
            for (UUID m : largestParty.members) {
                result.remove(m);
                unassigned.add(m);
                if ("ATTACK".equals(result.getOrDefault(m, null))) {
                    --attack;
                    continue;
                }
                if (!"DEFEND".equals(result.getOrDefault(m, null))) continue;
                --defend;
            }
            partyGroups.remove(largestParty.partyId);
        }
        attack = 0;
        defend = 0;
        for (String t : result.values()) {
            if ("ATTACK".equals(t)) {
                ++attack;
                continue;
            }
            ++defend;
        }
        for (UUID uid : unassigned) {
            if (attack <= defend) {
                result.put(uid, "ATTACK");
                ++attack;
                continue;
            }
            result.put(uid, "DEFEND");
            ++defend;
        }
        return result;
    }

    static {
        maxPartySize = 7;
    }

    public static final class PartyData {
        public final UUID partyId;
        public final UUID ownerId;
        public final String ownerName;
        public final Set<UUID> members;
        public String password;
        public boolean locked;

        PartyData(UUID partyId, UUID ownerId, String ownerName, String password) {
            this.partyId = partyId;
            this.ownerId = ownerId;
            this.ownerName = ownerName;
            this.members = new LinkedHashSet<UUID>();
            this.password = password == null || password.isEmpty() ? null : password;
            this.locked = password != null && !password.isEmpty();
        }

        public int size() {
            return this.members.size();
        }

        public boolean hasPassword() {
            return this.password != null && !this.password.isEmpty();
        }
    }
}

