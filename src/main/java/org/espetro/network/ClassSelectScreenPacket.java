package org.espetro.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * 编制选择界面数据包
 * 服务端将可选编制（Faction）列表一起发送，客户端无需自行加载
 * 同时携带对手队伍已确定的编制信息（如果已定）
 */
public class ClassSelectScreenPacket {

    private final String team;
    private final boolean isCommander;
    private final List<FactionInfo> factions;
    private final int timeRemaining;
    // 对手编制信息
    private final String opponentTeamName;
    private final String opponentFaction;
    // 对手阶段剩余时间（秒），-1 表示无对手阶段
    private final int opponentTimeRemaining;
    private final String selectedFactionId;

    /** 编制详情里的一行载具：显示名 + 刷新时间（分钟）+ 该型载具总数量。 */
    public static final class VehicleLine {
        public final String name;
        public final int respawnMinutes;
        /** 该型载具可同时存在的总数量（实体槽位数 × 每槽上限）。 */
        public final int count;

        public VehicleLine(String name, int respawnMinutes, int count) {
            this.name = name != null ? name : "";
            this.respawnMinutes = Math.max(0, respawnMinutes);
            this.count = Math.max(0, count);
        }
    }

    /** 编制详情里的一行专属工事：显示名 + 可部署数量上限。 */
    public static final class FortLine {
        public final String name;
        public final int limit;

        public FortLine(String name, int limit) {
            this.name = name != null ? name : "";
            this.limit = Math.max(0, limit);
        }
    }

    public static class FactionInfo {
        public final String id;
        public final String name;
        public final String selectionImage;
        public final int voteCount;
        /** 服务端从 EsFactions/ 读取的图片数据（可为 null） */
        public final byte[] imageData;
        /** 所属阵营展示名（faction.show_name，回退 name）。 */
        public final String showName;
        /** 编制性质（faction.description）。 */
        public final String description;
        /** 载具列表（显示名 + 刷新分钟），无配置时为空表。 */
        public final List<VehicleLine> vehicles;
        /** 专属工事列表（显示名 + 可部署数量），无配置时为空表。 */
        public final List<FortLine> forts;

        public FactionInfo(String id, String name, String selectionImage, int voteCount) {
            this(id, name, selectionImage, voteCount, null);
        }

        public FactionInfo(String id, String name, String selectionImage, int voteCount, byte[] imageData) {
            this(id, name, selectionImage, voteCount, imageData, "", "", List.of(), List.of());
        }

        public FactionInfo(String id, String name, String selectionImage, int voteCount, byte[] imageData,
                           String showName, String description,
                           List<VehicleLine> vehicles, List<FortLine> forts) {
            this.id = id;
            this.name = name;
            this.selectionImage = selectionImage != null ? selectionImage : "";
            this.voteCount = voteCount;
            this.imageData = imageData;
            this.showName = showName != null ? showName : "";
            this.description = description != null ? description : "";
            this.vehicles = vehicles == null ? List.of() : List.copyOf(vehicles);
            this.forts = forts == null ? List.of() : List.copyOf(forts);
        }
    }

    public ClassSelectScreenPacket(String team, boolean isCommander, List<FactionInfo> factions,
                                    int timeRemaining, String opponentTeamName, String opponentFaction,
                                    int opponentTimeRemaining, String selectedFactionId) {
        this.team = team;
        this.isCommander = isCommander;
        this.factions = factions;
        this.timeRemaining = timeRemaining;
        this.opponentTeamName = opponentTeamName;
        this.opponentFaction = opponentFaction;
        this.opponentTimeRemaining = opponentTimeRemaining;
        this.selectedFactionId = selectedFactionId != null ? selectedFactionId : "";
    }

    public String getTeam() { return team; }
    public boolean isCommander() { return isCommander; }
    public List<FactionInfo> getFactions() { return factions; }
    public int getTimeRemaining() { return timeRemaining; }
    public String getOpponentTeamName() { return opponentTeamName; }
    public String getOpponentFaction() { return opponentFaction; }
    public int getOpponentTimeRemaining() { return opponentTimeRemaining; }
    public String getSelectedFactionId() { return selectedFactionId; }

    public static ClassSelectScreenPacket read(FriendlyByteBuf buf) {
        String team = buf.readUtf();
        boolean isCommander = buf.readBoolean();
        int timeRemaining = buf.readVarInt();
        int count = buf.readVarInt();
        List<FactionInfo> factions = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            String id = buf.readUtf();
            String name = buf.readUtf();
            String selectionImage = buf.readUtf();
            int voteCount = buf.readVarInt();
            byte[] imageData = buf.readBoolean() ? buf.readByteArray() : null;
            String showName = buf.readUtf();
            String description = buf.readUtf();
            int vehicleCount = buf.readVarInt();
            List<VehicleLine> vehicles = new ArrayList<>(Math.max(0, vehicleCount));
            for (int j = 0; j < vehicleCount; j++) {
                vehicles.add(new VehicleLine(buf.readUtf(), buf.readVarInt(), buf.readVarInt()));
            }
            int fortCount = buf.readVarInt();
            List<FortLine> forts = new ArrayList<>(Math.max(0, fortCount));
            for (int j = 0; j < fortCount; j++) {
                forts.add(new FortLine(buf.readUtf(), buf.readVarInt()));
            }
            factions.add(new FactionInfo(id, name, selectionImage, voteCount, imageData,
                showName, description, vehicles, forts));
        }
        String opponentTeamName = buf.readUtf();
        String opponentFaction = buf.readUtf();
        int opponentTimeRemaining = buf.readInt();
        String selectedFactionId = buf.readUtf();
        return new ClassSelectScreenPacket(team, isCommander, factions, timeRemaining,
            opponentTeamName, opponentFaction, opponentTimeRemaining, selectedFactionId);
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(team);
        buf.writeBoolean(isCommander);
        buf.writeVarInt(timeRemaining);
        buf.writeVarInt(factions.size());
        for (FactionInfo info : factions) {
            buf.writeUtf(info.id);
            buf.writeUtf(info.name);
            buf.writeUtf(info.selectionImage);
            buf.writeVarInt(info.voteCount);
            boolean hasImage = info.imageData != null;
            buf.writeBoolean(hasImage);
            if (hasImage) buf.writeByteArray(info.imageData);
            buf.writeUtf(info.showName != null ? info.showName : "");
            buf.writeUtf(info.description != null ? info.description : "");
            buf.writeVarInt(info.vehicles.size());
            for (VehicleLine line : info.vehicles) {
                buf.writeUtf(line.name);
                buf.writeVarInt(line.respawnMinutes);
                buf.writeVarInt(line.count);
            }
            buf.writeVarInt(info.forts.size());
            for (FortLine line : info.forts) {
                buf.writeUtf(line.name);
                buf.writeVarInt(line.limit);
            }
        }
        buf.writeUtf(opponentTeamName != null ? opponentTeamName : "");
        buf.writeUtf(opponentFaction != null ? opponentFaction : "");
        buf.writeInt(opponentTimeRemaining);
        buf.writeUtf(selectedFactionId);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            try {
                Class.forName("org.espetro.client.ClientPacketHandlers")
                    .getMethod("handleClassSelectScreen", ClassSelectScreenPacket.class)
                    .invoke(null, this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
