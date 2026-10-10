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
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import org.espetro.Espetro;

public class UnifiedDeployScreenPacket {
    private final String factionId;
    private final String factionName;
    private final String factionDescription;
    private final String factionIcon;
    private final List<ClassInfo> classes;
    private final Map<String, Integer> classCounts;
    private final boolean hasDeployPoint;
    private final String deployPointPos;
    private final List<BastionItem> bastions;
    private final boolean isCommander;
    private final List<VehicleInfo> vehicles;
    private final List<SquadInfo> squads;
    private final List<SquadCategoryInfo> squadCategories;
    private final int mySquadId;
    private final List<String> commanderNames;
    private final double teammateNameTagDistance;
    private final int deployTimeRemaining;
    private final String team;
    private final boolean waitingForDeploySelection;
    private final int outpostRedeployCooldownRemaining;
    private final int classSwitchCooldownRemaining;
    private final String selectedClassId;
    private final boolean openScreen;

    public UnifiedDeployScreenPacket(String factionId, String factionName, String factionDescription, String factionIcon, List<ClassInfo> classes, Map<String, Integer> classCounts, boolean hasDeployPoint, String deployPointPos, List<BastionItem> bastions, boolean isCommander, List<VehicleInfo> vehicles, List<SquadInfo> squads, int mySquadId, int deployTimeRemaining, String team) {
        this(factionId, factionName, factionDescription, factionIcon, classes, classCounts, hasDeployPoint, deployPointPos, bastions, isCommander, vehicles, squads, mySquadId, deployTimeRemaining, team, new ArrayList<String>(), 10.0, false, 0, new ArrayList<SquadCategoryInfo>(), 0, true);
    }

    public UnifiedDeployScreenPacket(String factionId, String factionName, String factionDescription, String factionIcon, List<ClassInfo> classes, Map<String, Integer> classCounts, boolean hasDeployPoint, String deployPointPos, List<BastionItem> bastions, boolean isCommander, List<VehicleInfo> vehicles, List<SquadInfo> squads, int mySquadId, int deployTimeRemaining, String team, List<String> commanderNames, double teammateNameTagDistance, boolean waitingForDeploySelection, int outpostRedeployCooldownRemaining, List<SquadCategoryInfo> squadCategories) {
        this(factionId, factionName, factionDescription, factionIcon, classes, classCounts, hasDeployPoint, deployPointPos, bastions, isCommander, vehicles, squads, mySquadId, deployTimeRemaining, team, commanderNames, teammateNameTagDistance, waitingForDeploySelection, outpostRedeployCooldownRemaining, squadCategories, 0, true);
    }

    public UnifiedDeployScreenPacket(String factionId, String factionName, String factionDescription, String factionIcon, List<ClassInfo> classes, Map<String, Integer> classCounts, boolean hasDeployPoint, String deployPointPos, List<BastionItem> bastions, boolean isCommander, List<VehicleInfo> vehicles, List<SquadInfo> squads, int mySquadId, int deployTimeRemaining, String team, List<String> commanderNames, double teammateNameTagDistance, boolean waitingForDeploySelection, int outpostRedeployCooldownRemaining, List<SquadCategoryInfo> squadCategories, int classSwitchCooldownRemaining) {
        this(factionId, factionName, factionDescription, factionIcon, classes, classCounts, hasDeployPoint, deployPointPos, bastions, isCommander, vehicles, squads, mySquadId, deployTimeRemaining, team, commanderNames, teammateNameTagDistance, waitingForDeploySelection, outpostRedeployCooldownRemaining, squadCategories, classSwitchCooldownRemaining, true);
    }

    public UnifiedDeployScreenPacket(String factionId, String factionName, String factionDescription, String factionIcon, List<ClassInfo> classes, Map<String, Integer> classCounts, boolean hasDeployPoint, String deployPointPos, List<BastionItem> bastions, boolean isCommander, List<VehicleInfo> vehicles, List<SquadInfo> squads, int mySquadId, int deployTimeRemaining, String team, List<String> commanderNames, double teammateNameTagDistance, boolean waitingForDeploySelection, int outpostRedeployCooldownRemaining, List<SquadCategoryInfo> squadCategories, int classSwitchCooldownRemaining, boolean openScreen) {
        this(factionId, factionName, factionDescription, factionIcon, classes, classCounts, hasDeployPoint, deployPointPos, bastions, isCommander, vehicles, squads, mySquadId, deployTimeRemaining, team, commanderNames, teammateNameTagDistance, waitingForDeploySelection, outpostRedeployCooldownRemaining, squadCategories, classSwitchCooldownRemaining, openScreen, "");
    }

    public UnifiedDeployScreenPacket(String factionId, String factionName, String factionDescription, String factionIcon, List<ClassInfo> classes, Map<String, Integer> classCounts, boolean hasDeployPoint, String deployPointPos, List<BastionItem> bastions, boolean isCommander, List<VehicleInfo> vehicles, List<SquadInfo> squads, int mySquadId, int deployTimeRemaining, String team, List<String> commanderNames, double teammateNameTagDistance, boolean waitingForDeploySelection, int outpostRedeployCooldownRemaining, List<SquadCategoryInfo> squadCategories, int classSwitchCooldownRemaining, boolean openScreen, String selectedClassId) {
        this.factionId = factionId;
        this.factionName = factionName;
        this.factionDescription = factionDescription;
        this.factionIcon = factionIcon;
        this.classes = classes != null ? classes : new ArrayList();
        this.classCounts = classCounts != null ? classCounts : new HashMap();
        this.hasDeployPoint = hasDeployPoint;
        this.deployPointPos = deployPointPos;
        this.bastions = bastions != null ? bastions : new ArrayList();
        this.isCommander = isCommander;
        this.vehicles = vehicles != null ? vehicles : new ArrayList();
        this.squads = squads != null ? squads : new ArrayList();
        this.squadCategories = squadCategories != null ? squadCategories : new ArrayList();
        this.mySquadId = mySquadId;
        this.deployTimeRemaining = deployTimeRemaining;
        this.team = team;
        this.commanderNames = commanderNames != null ? commanderNames : new ArrayList();
        this.teammateNameTagDistance = teammateNameTagDistance;
        this.waitingForDeploySelection = waitingForDeploySelection;
        this.outpostRedeployCooldownRemaining = Math.max(0, outpostRedeployCooldownRemaining);
        this.classSwitchCooldownRemaining = Math.max(0, classSwitchCooldownRemaining);
        this.openScreen = openScreen;
        this.selectedClassId = selectedClassId == null ? "" : selectedClassId;
    }

    public UnifiedDeployScreenPacket(FriendlyByteBuf buf) {
        this.factionId = buf.m_130277_();
        this.factionName = buf.m_130277_();
        this.factionDescription = buf.m_130277_();
        this.factionIcon = buf.m_130277_();
        int classSize = buf.m_130242_();
        this.classes = new ArrayList<ClassInfo>();
        for (int i = 0; i < classSize; ++i) {
            this.classes.add(new ClassInfo(buf));
        }
        int countSize = buf.m_130242_();
        this.classCounts = new HashMap<String, Integer>();
        for (int i = 0; i < countSize; ++i) {
            this.classCounts.put(buf.m_130277_(), buf.m_130242_());
        }
        this.hasDeployPoint = buf.readBoolean();
        this.deployPointPos = buf.m_130277_();
        int bastionSize = buf.m_130242_();
        this.bastions = new ArrayList<BastionItem>();
        for (int i = 0; i < bastionSize; ++i) {
            this.bastions.add(new BastionItem(buf));
        }
        this.isCommander = buf.readBoolean();
        int vehicleSize = buf.m_130242_();
        this.vehicles = new ArrayList<VehicleInfo>();
        for (int i = 0; i < vehicleSize; ++i) {
            this.vehicles.add(new VehicleInfo(buf));
        }
        int squadSize = buf.m_130242_();
        this.squads = new ArrayList<SquadInfo>();
        for (int i = 0; i < squadSize; ++i) {
            this.squads.add(new SquadInfo(buf));
        }
        int categorySize = buf.m_130242_();
        this.squadCategories = new ArrayList<SquadCategoryInfo>();
        for (int i = 0; i < categorySize; ++i) {
            this.squadCategories.add(new SquadCategoryInfo(buf.m_130277_(), buf.m_130277_()));
        }
        this.mySquadId = buf.m_130242_();
        this.deployTimeRemaining = buf.m_130242_();
        this.team = buf.m_130277_();
        int commanderSize = buf.m_130242_();
        this.commanderNames = new ArrayList<String>();
        for (int i = 0; i < commanderSize; ++i) {
            this.commanderNames.add(buf.m_130277_());
        }
        this.teammateNameTagDistance = buf.readDouble();
        this.waitingForDeploySelection = buf.readBoolean();
        this.outpostRedeployCooldownRemaining = buf.m_130242_();
        this.classSwitchCooldownRemaining = buf.m_130242_();
        this.openScreen = buf.readBoolean();
        this.selectedClassId = buf.m_130277_();
    }

    public static UnifiedDeployScreenPacket read(FriendlyByteBuf buf) {
        return new UnifiedDeployScreenPacket(buf);
    }

    public void write(FriendlyByteBuf buf) {
        buf.m_130070_(this.factionId);
        buf.m_130070_(this.factionName);
        buf.m_130070_(this.factionDescription);
        buf.m_130070_(this.factionIcon);
        buf.m_130130_(this.classes.size());
        for (ClassInfo classInfo : this.classes) {
            classInfo.write(buf);
        }
        buf.m_130130_(this.classCounts.size());
        for (Map.Entry entry : this.classCounts.entrySet()) {
            buf.m_130070_((String)entry.getKey());
            buf.m_130130_((Integer)entry.getValue());
        }
        buf.writeBoolean(this.hasDeployPoint);
        buf.m_130070_(this.deployPointPos);
        buf.m_130130_(this.bastions.size());
        for (BastionItem bastionItem : this.bastions) {
            bastionItem.write(buf);
        }
        buf.writeBoolean(this.isCommander);
        buf.m_130130_(this.vehicles.size());
        for (VehicleInfo vehicleInfo : this.vehicles) {
            vehicleInfo.write(buf);
        }
        buf.m_130130_(this.squads.size());
        for (SquadInfo squadInfo : this.squads) {
            squadInfo.write(buf);
        }
        buf.m_130130_(this.squadCategories.size());
        for (SquadCategoryInfo squadCategoryInfo : this.squadCategories) {
            buf.m_130070_(squadCategoryInfo.id);
            buf.m_130070_(squadCategoryInfo.displayName);
        }
        buf.m_130130_(this.mySquadId);
        buf.m_130130_(this.deployTimeRemaining);
        buf.m_130070_(this.team);
        buf.m_130130_(this.commanderNames.size());
        for (String string : this.commanderNames) {
            buf.m_130070_(string);
        }
        buf.writeDouble(this.teammateNameTagDistance);
        buf.writeBoolean(this.waitingForDeploySelection);
        buf.m_130130_(this.outpostRedeployCooldownRemaining);
        buf.m_130130_(this.classSwitchCooldownRemaining);
        buf.writeBoolean(this.openScreen);
        buf.m_130070_(this.selectedClassId);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            try {
                Class.forName("org.espetro.client.ClientPacketHandlers").getMethod("handleUnifiedDeployScreen", UnifiedDeployScreenPacket.class).invoke(null, this);
            }
            catch (Exception e) {
                Espetro.LOGGER.error("Failed to handle UnifiedDeployScreenPacket", (Throwable)e);
            }
        });
        ctx.get().setPacketHandled(true);
    }

    public String getFactionId() {
        return this.factionId;
    }

    public String getFactionName() {
        return this.factionName;
    }

    public String getFactionDescription() {
        return this.factionDescription;
    }

    public String getFactionIcon() {
        return this.factionIcon;
    }

    public List<ClassInfo> getClasses() {
        return this.classes;
    }

    public Map<String, Integer> getClassCounts() {
        return this.classCounts;
    }

    public Map<String, Map<String, Integer>> getVariantCounts() {
        HashMap<String, Map<String, Integer>> result = new HashMap<String, Map<String, Integer>>();
        for (ClassInfo classInfo : this.classes) {
            HashMap<String, Integer> perClass = new HashMap<String, Integer>();
            for (VariantInfo variant : classInfo.variants) {
                perClass.put(variant.variantId, variant.currentCount);
            }
            result.put(classInfo.classId, perClass);
        }
        return result;
    }

    public boolean hasDeployPoint() {
        return this.hasDeployPoint;
    }

    public String getDeployPointPos() {
        return this.deployPointPos;
    }

    public List<BastionItem> getBastions() {
        return this.bastions;
    }

    public boolean isCommander() {
        return this.isCommander;
    }

    public List<VehicleInfo> getVehicles() {
        return this.vehicles;
    }

    public List<SquadInfo> getSquads() {
        return this.squads;
    }

    public List<SquadCategoryInfo> getSquadCategories() {
        return this.squadCategories;
    }

    public int getMySquadId() {
        return this.mySquadId;
    }

    public int getDeployTimeRemaining() {
        return this.deployTimeRemaining;
    }

    public String getTeam() {
        return this.team;
    }

    public List<String> getCommanderNames() {
        return this.commanderNames;
    }

    public double getTeammateNameTagDistance() {
        return this.teammateNameTagDistance;
    }

    public boolean isWaitingForDeploySelection() {
        return this.waitingForDeploySelection;
    }

    public int getOutpostRedeployCooldownRemaining() {
        return this.outpostRedeployCooldownRemaining;
    }

    public int getClassSwitchCooldownRemaining() {
        return this.classSwitchCooldownRemaining;
    }

    public boolean shouldOpenScreen() {
        return this.openScreen;
    }

    public String getSelectedClassId() {
        return this.selectedClassId;
    }

    public static class ClassInfo {
        public final String classId;
        public final String name;
        public final String description;
        public final String role;
        public final String icon;
        public final String iconImage;
        public final int maxPlayers;
        public final boolean strictCount;
        public final int currentCount;
        public final int troopValue;
        public final int healthBonus;
        public final float speedBonus;
        public final boolean teamCount;
        public final int maxPerSquad;
        public final int teammatesNeed;
        public int squadCurrentCount;
        public final List<VariantInfo> variants;
        public final int row;
        public final int unlockPerN;
        public final int unlockMinSquad;
        public final boolean leaderOnly;

        public ClassInfo(String classId, String name, String description, String role, String icon, int maxPlayers, boolean strictCount, int currentCount, int troopValue, int healthBonus, float speedBonus, List<VariantInfo> variants) {
            this(classId, name, description, role, icon, null, maxPlayers, strictCount, currentCount, troopValue, healthBonus, speedBonus, false, 0, 0, 0, variants);
        }

        public ClassInfo(String classId, String name, String description, String role, String icon, int maxPlayers, boolean strictCount, int currentCount, int troopValue, int healthBonus, float speedBonus, boolean teamCount, int maxPerSquad, int squadCurrentCount, List<VariantInfo> variants) {
            this(classId, name, description, role, icon, null, maxPlayers, strictCount, currentCount, troopValue, healthBonus, speedBonus, teamCount, maxPerSquad, squadCurrentCount, 0, variants);
        }

        public ClassInfo(String classId, String name, String description, String role, String icon, String iconImage, int maxPlayers, boolean strictCount, int currentCount, int troopValue, int healthBonus, float speedBonus, boolean teamCount, int maxPerSquad, int squadCurrentCount, int teammatesNeed, List<VariantInfo> variants) {
            this(classId, name, description, role, icon, iconImage, maxPlayers, strictCount, currentCount, troopValue, healthBonus, speedBonus, teamCount, maxPerSquad, squadCurrentCount, teammatesNeed, 0, 0, 0, false, variants);
        }

        public ClassInfo(String classId, String name, String description, String role, String icon, String iconImage, int maxPlayers, boolean strictCount, int currentCount, int troopValue, int healthBonus, float speedBonus, boolean teamCount, int maxPerSquad, int squadCurrentCount, int teammatesNeed, int row, int unlockPerN, int unlockMinSquad, boolean leaderOnly, List<VariantInfo> variants) {
            this.classId = classId;
            this.name = name;
            this.description = description;
            this.role = role;
            this.icon = icon;
            this.iconImage = iconImage == null ? "" : iconImage;
            this.maxPlayers = maxPlayers;
            this.strictCount = strictCount;
            this.currentCount = currentCount;
            this.troopValue = troopValue;
            this.healthBonus = healthBonus;
            this.speedBonus = speedBonus;
            this.teamCount = teamCount;
            this.maxPerSquad = Math.max(0, maxPerSquad);
            this.squadCurrentCount = Math.max(0, squadCurrentCount);
            this.teammatesNeed = Math.max(0, teammatesNeed);
            this.row = row;
            this.unlockPerN = unlockPerN;
            this.unlockMinSquad = unlockMinSquad;
            this.leaderOnly = leaderOnly;
            this.variants = variants != null ? variants : new ArrayList();
        }

        public ClassInfo(FriendlyByteBuf buf) {
            this.classId = buf.m_130277_();
            this.name = buf.m_130277_();
            this.description = buf.m_130277_();
            this.role = buf.m_130277_();
            this.icon = buf.m_130277_();
            this.iconImage = buf.m_130277_();
            this.maxPlayers = buf.m_130242_();
            this.strictCount = buf.readBoolean();
            this.currentCount = buf.m_130242_();
            this.troopValue = buf.m_130242_();
            this.healthBonus = buf.m_130242_();
            this.speedBonus = buf.readFloat();
            this.teamCount = buf.readBoolean();
            this.maxPerSquad = Math.max(0, buf.m_130242_());
            this.squadCurrentCount = Math.max(0, buf.m_130242_());
            this.teammatesNeed = Math.max(0, buf.m_130242_());
            this.row = buf.m_130242_();
            this.unlockPerN = buf.m_130242_();
            this.unlockMinSquad = buf.m_130242_();
            this.leaderOnly = buf.readBoolean();
            int variantCount = buf.m_130242_();
            this.variants = new ArrayList<VariantInfo>(variantCount);
            for (int i = 0; i < variantCount; ++i) {
                this.variants.add(new VariantInfo(buf));
            }
        }

        public void write(FriendlyByteBuf buf) {
            buf.m_130070_(this.classId);
            buf.m_130070_(this.name);
            buf.m_130070_(this.description);
            buf.m_130070_(this.role);
            buf.m_130070_(this.icon == null ? "" : this.icon);
            buf.m_130070_(this.iconImage == null ? "" : this.iconImage);
            buf.m_130130_(this.maxPlayers);
            buf.writeBoolean(this.strictCount);
            buf.m_130130_(this.currentCount);
            buf.m_130130_(this.troopValue);
            buf.m_130130_(this.healthBonus);
            buf.writeFloat(this.speedBonus);
            buf.writeBoolean(this.teamCount);
            buf.m_130130_(this.maxPerSquad);
            buf.m_130130_(this.squadCurrentCount);
            buf.m_130130_(this.teammatesNeed);
            buf.m_130130_(this.row);
            buf.m_130130_(this.unlockPerN);
            buf.m_130130_(this.unlockMinSquad);
            buf.writeBoolean(this.leaderOnly);
            buf.m_130130_(this.variants.size());
            for (VariantInfo variant : this.variants) {
                variant.write(buf);
            }
        }
    }

    public static class BastionItem {
        public static final String TYPE_HAB = "hab";
        public static final String TYPE_RALLY = "rally";
        public static final String TYPE_OUTPOST = "outpost";
        public final UUID id;
        public final String name;
        public final String pos;
        public final String type;
        public final String status;
        public final long nextWaveAtEpochMs;
        public final int waveSeconds;
        public final long habAvailableAtEpochMs;
        public final int habActivationTotalSeconds;

        public BastionItem(UUID id, String name, String pos) {
            this(id, name, pos, id.getMostSignificantBits() == 0L ? TYPE_OUTPOST : TYPE_HAB, "", 0L, 0, 0L, 0);
        }

        public BastionItem(UUID id, String name, String pos, String type, String status) {
            this(id, name, pos, type, status, 0L, 0, 0L, 0);
        }

        public BastionItem(UUID id, String name, String pos, String type, String status, long nextWaveAtEpochMs) {
            this(id, name, pos, type, status, nextWaveAtEpochMs, 0, 0L, 0);
        }

        public BastionItem(UUID id, String name, String pos, String type, String status, long nextWaveAtEpochMs, int waveSeconds) {
            this(id, name, pos, type, status, nextWaveAtEpochMs, waveSeconds, 0L, 0);
        }

        public BastionItem(UUID id, String name, String pos, String type, String status, long nextWaveAtEpochMs, int waveSeconds, long habAvailableAtEpochMs, int habActivationTotalSeconds) {
            this.id = id;
            this.name = name;
            this.pos = pos;
            this.type = type == null ? TYPE_HAB : type;
            this.status = status == null ? "" : status;
            this.nextWaveAtEpochMs = Math.max(0L, nextWaveAtEpochMs);
            this.waveSeconds = Math.max(0, waveSeconds);
            this.habAvailableAtEpochMs = Math.max(0L, habAvailableAtEpochMs);
            this.habActivationTotalSeconds = Math.max(0, habActivationTotalSeconds);
        }

        public BastionItem(FriendlyByteBuf buf) {
            this.id = buf.m_130259_();
            this.name = buf.m_130277_();
            this.pos = buf.m_130277_();
            this.type = buf.m_130277_();
            this.status = buf.m_130277_();
            this.nextWaveAtEpochMs = Math.max(0L, buf.readLong());
            this.waveSeconds = Math.max(0, buf.m_130242_());
            this.habAvailableAtEpochMs = Math.max(0L, buf.readLong());
            this.habActivationTotalSeconds = Math.max(0, buf.m_130242_());
        }

        public void write(FriendlyByteBuf buf) {
            buf.m_130077_(this.id);
            buf.m_130070_(this.name);
            buf.m_130070_(this.pos);
            buf.m_130070_(this.type);
            buf.m_130070_(this.status);
            buf.writeLong(this.nextWaveAtEpochMs);
            buf.m_130130_(this.waveSeconds);
            buf.writeLong(this.habAvailableAtEpochMs);
            buf.m_130130_(this.habActivationTotalSeconds);
        }

        public boolean isOutpost() {
            return TYPE_OUTPOST.equals(this.type) || this.id.getMostSignificantBits() == 0L && this.id.getLeastSignificantBits() > 0L;
        }

        public boolean isRally() {
            return TYPE_RALLY.equals(this.type);
        }

        public int getOutpostIndex() {
            return (int)(this.id.getLeastSignificantBits() - 1L);
        }
    }

    public static class VehicleInfo {
        public final String type;
        public final String displayName;
        public final int max;
        public final int current;
        public final int cooldownRemaining;
        public final int respawnMinutes;

        public VehicleInfo(String type, String displayName, int max, int current, int cooldownRemaining, int respawnMinutes) {
            this.type = type;
            this.displayName = displayName;
            this.max = max;
            this.current = current;
            this.cooldownRemaining = cooldownRemaining;
            this.respawnMinutes = respawnMinutes;
        }

        public VehicleInfo(FriendlyByteBuf buf) {
            this.type = buf.m_130277_();
            this.displayName = buf.m_130277_();
            this.max = buf.m_130242_();
            this.current = buf.m_130242_();
            this.cooldownRemaining = buf.m_130242_();
            this.respawnMinutes = buf.m_130242_();
        }

        public void write(FriendlyByteBuf buf) {
            buf.m_130070_(this.type);
            buf.m_130070_(this.displayName);
            buf.m_130130_(this.max);
            buf.m_130130_(this.current);
            buf.m_130130_(this.cooldownRemaining);
            buf.m_130130_(this.respawnMinutes);
        }
    }

    public static class SquadInfo {
        public final int id;
        public final int displayId;
        public final String name;
        public final int memberCount;
        public final int maxMembers;
        public final boolean isLocked;
        public final String leaderName;
        public final String categoryId;
        public final String categoryDisplayName;
        public final List<SquadMemberInfo> members;

        public SquadInfo(int id, String name, int memberCount, int maxMembers, boolean isLocked) {
            this(id, id, name, memberCount, maxMembers, isLocked, "", "none", "\u65e0", new ArrayList<SquadMemberInfo>());
        }

        public SquadInfo(int id, String name, int memberCount, int maxMembers, boolean isLocked, String leaderName, String categoryId, String categoryDisplayName, List<SquadMemberInfo> members) {
            this(id, id, name, memberCount, maxMembers, isLocked, leaderName, categoryId, categoryDisplayName, members);
        }

        public SquadInfo(int id, int displayId, String name, int memberCount, int maxMembers, boolean isLocked, String leaderName, String categoryId, String categoryDisplayName, List<SquadMemberInfo> members) {
            this.id = id;
            this.displayId = displayId;
            this.name = name;
            this.memberCount = memberCount;
            this.maxMembers = maxMembers;
            this.isLocked = isLocked;
            this.leaderName = leaderName == null ? "" : leaderName;
            this.categoryId = categoryId == null ? "none" : categoryId;
            this.categoryDisplayName = categoryDisplayName == null ? "\u65e0" : categoryDisplayName;
            this.members = members != null ? members : new ArrayList();
        }

        public SquadInfo(FriendlyByteBuf buf) {
            this.id = buf.m_130242_();
            this.displayId = buf.m_130242_();
            this.name = buf.m_130277_();
            this.memberCount = buf.m_130242_();
            this.maxMembers = buf.m_130242_();
            this.isLocked = buf.readBoolean();
            this.leaderName = buf.m_130277_();
            this.categoryId = buf.m_130277_();
            this.categoryDisplayName = buf.m_130277_();
            int memberSize = buf.m_130242_();
            this.members = new ArrayList<SquadMemberInfo>();
            for (int i = 0; i < memberSize; ++i) {
                this.members.add(new SquadMemberInfo(buf));
            }
        }

        public void write(FriendlyByteBuf buf) {
            buf.m_130130_(this.id);
            buf.m_130130_(this.displayId);
            buf.m_130070_(this.name);
            buf.m_130130_(this.memberCount);
            buf.m_130130_(this.maxMembers);
            buf.writeBoolean(this.isLocked);
            buf.m_130070_(this.leaderName);
            buf.m_130070_(this.categoryId);
            buf.m_130070_(this.categoryDisplayName);
            buf.m_130130_(this.members.size());
            for (SquadMemberInfo member : this.members) {
                member.write(buf);
            }
        }

        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof SquadInfo)) {
                return false;
            }
            SquadInfo that = (SquadInfo)other;
            return this.id == that.id && this.displayId == that.displayId && this.memberCount == that.memberCount && this.maxMembers == that.maxMembers && this.isLocked == that.isLocked && Objects.equals(this.name, that.name) && Objects.equals(this.leaderName, that.leaderName) && Objects.equals(this.categoryId, that.categoryId) && Objects.equals(this.categoryDisplayName, that.categoryDisplayName) && Objects.equals(this.members, that.members);
        }

        public int hashCode() {
            return Objects.hash(this.id, this.displayId, this.name, this.memberCount, this.maxMembers, this.isLocked, this.leaderName, this.categoryId, this.categoryDisplayName, this.members);
        }
    }

    public static class SquadCategoryInfo {
        public final String id;
        public final String displayName;

        public SquadCategoryInfo(String id, String displayName) {
            this.id = id == null ? "none" : id;
            this.displayName = displayName == null ? "\u65e0" : displayName;
        }
    }

    public static class VariantInfo {
        public final String variantId;
        public final String name;
        public final String description;
        public final int maxPlayers;
        public int currentCount;
        public final LoadoutPreview preview;

        public VariantInfo(String variantId, String name, String description, int maxPlayers, int currentCount) {
            this(variantId, name, description, maxPlayers, currentCount, LoadoutPreview.empty());
        }

        public VariantInfo(String variantId, String name, String description, int maxPlayers, int currentCount, LoadoutPreview preview) {
            this.variantId = variantId;
            this.name = name;
            this.description = description;
            this.maxPlayers = maxPlayers;
            this.currentCount = currentCount;
            this.preview = preview != null ? preview : LoadoutPreview.empty();
        }

        public VariantInfo(FriendlyByteBuf buf) {
            this.variantId = buf.m_130277_();
            this.name = buf.m_130277_();
            this.description = buf.m_130277_();
            this.maxPlayers = buf.m_130242_();
            this.currentCount = buf.m_130242_();
            this.preview = new LoadoutPreview(buf);
        }

        public void write(FriendlyByteBuf buf) {
            buf.m_130070_(this.variantId);
            buf.m_130070_(this.name != null ? this.name : this.variantId);
            buf.m_130070_(this.description != null ? this.description : "");
            buf.m_130130_(this.maxPlayers);
            buf.m_130130_(this.currentCount);
            this.preview.write(buf);
        }
    }

    public static class SquadMemberInfo {
        public final UUID uuid;
        public final String playerName;
        public final String className;
        public final boolean leader;
        public final boolean commander;
        public final byte fireteam;
        public final boolean fireteamLeader;

        public SquadMemberInfo(String playerName, String className, boolean leader) {
            this(new UUID(0L, 0L), playerName, className, leader, false, 0, leader);
        }

        public SquadMemberInfo(String playerName, String className, boolean leader, boolean commander) {
            this(new UUID(0L, 0L), playerName, className, leader, commander, 0, leader);
        }

        public SquadMemberInfo(UUID uuid, String playerName, String className, boolean leader, boolean commander) {
            this(uuid, playerName, className, leader, commander, 0, leader);
        }

        public SquadMemberInfo(UUID uuid, String playerName, String className, boolean leader, boolean commander, byte fireteam, boolean fireteamLeader) {
            this.uuid = uuid == null ? new UUID(0L, 0L) : uuid;
            this.playerName = playerName == null ? "" : playerName;
            this.className = className == null ? "" : className;
            this.leader = leader;
            this.commander = commander;
            this.fireteam = fireteam;
            this.fireteamLeader = fireteamLeader;
        }

        public SquadMemberInfo(FriendlyByteBuf buf) {
            this.uuid = buf.m_130259_();
            this.playerName = buf.m_130277_();
            this.className = buf.m_130277_();
            this.leader = buf.readBoolean();
            this.commander = buf.readBoolean();
            this.fireteam = buf.readByte();
            this.fireteamLeader = buf.readBoolean();
        }

        public void write(FriendlyByteBuf buf) {
            buf.m_130077_(this.uuid);
            buf.m_130070_(this.playerName);
            buf.m_130070_(this.className);
            buf.writeBoolean(this.leader);
            buf.writeBoolean(this.commander);
            buf.writeByte(this.fireteam);
            buf.writeBoolean(this.fireteamLeader);
        }

        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof SquadMemberInfo)) {
                return false;
            }
            SquadMemberInfo that = (SquadMemberInfo)other;
            return this.uuid.equals(that.uuid) && this.leader == that.leader && this.commander == that.commander && this.fireteam == that.fireteam && this.fireteamLeader == that.fireteamLeader && Objects.equals(this.playerName, that.playerName) && Objects.equals(this.className, that.className);
        }

        public int hashCode() {
            return Objects.hash(this.uuid, this.playerName, this.className, this.leader, this.commander, this.fireteam, this.fireteamLeader);
        }
    }

    public static class LoadoutPreview {
        public final ItemStack head;
        public final ItemStack chest;
        public final ItemStack legs;
        public final ItemStack feet;
        public final ItemStack mainHand;
        public final ItemStack offHand;

        public LoadoutPreview(ItemStack head, ItemStack chest, ItemStack legs, ItemStack feet, ItemStack mainHand, ItemStack offHand) {
            this.head = LoadoutPreview.copySafe(head);
            this.chest = LoadoutPreview.copySafe(chest);
            this.legs = LoadoutPreview.copySafe(legs);
            this.feet = LoadoutPreview.copySafe(feet);
            this.mainHand = LoadoutPreview.copySafe(mainHand);
            this.offHand = LoadoutPreview.copySafe(offHand);
        }

        public static LoadoutPreview empty() {
            return new LoadoutPreview(ItemStack.f_41583_, ItemStack.f_41583_, ItemStack.f_41583_, ItemStack.f_41583_, ItemStack.f_41583_, ItemStack.f_41583_);
        }

        public LoadoutPreview(FriendlyByteBuf buf) {
            this.head = buf.m_130267_();
            this.chest = buf.m_130267_();
            this.legs = buf.m_130267_();
            this.feet = buf.m_130267_();
            this.mainHand = buf.m_130267_();
            this.offHand = buf.m_130267_();
        }

        public void write(FriendlyByteBuf buf) {
            buf.m_130055_(this.head);
            buf.m_130055_(this.chest);
            buf.m_130055_(this.legs);
            buf.m_130055_(this.feet);
            buf.m_130055_(this.mainHand);
            buf.m_130055_(this.offHand);
        }

        public boolean isEmpty() {
            return this.head.m_41619_() && this.chest.m_41619_() && this.legs.m_41619_() && this.feet.m_41619_() && this.mainHand.m_41619_() && this.offHand.m_41619_();
        }

        private static ItemStack copySafe(ItemStack stack) {
            return stack == null ? ItemStack.f_41583_ : stack.m_41777_();
        }
    }
}

