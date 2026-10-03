/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.network.NetworkEvent$Context
 */
package org.espetro.network;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.espetro.team.FactionDataLoader;

public class OpenClassSelectionPacket {
    private final String factionId;
    private final String factionName;
    private final String factionDescription;
    private final String factionIcon;
    private final List<ClassInfo> classes;

    public OpenClassSelectionPacket(String factionId, FactionDataLoader loader) {
        this.factionId = factionId;
        FactionDataLoader.FactionData faction = loader.getFaction(factionId);
        if (faction != null) {
            this.factionName = faction.name;
            this.factionDescription = faction.description;
            this.factionIcon = faction.icon;
        } else {
            this.factionName = "\u672a\u77e5\u7f16\u5236";
            this.factionDescription = "";
            this.factionIcon = "?";
        }
        FactionDataLoader.ClassKitData[] kits = loader.getClassesForFaction(factionId);
        this.classes = new ArrayList<ClassInfo>(kits.length);
        for (FactionDataLoader.ClassKitData kit : kits) {
            ArrayList<VariantInfo> variants = new ArrayList<VariantInfo>();
            if (kit.variants != null) {
                for (FactionDataLoader.ClassVariantData variant : kit.variants.values()) {
                    variants.add(new VariantInfo(variant.id, variant.name, variant.description, variant.maxPlayers));
                }
            }
            this.classes.add(new ClassInfo(kit.id, kit.name, kit.description, kit.role, kit.icon, kit.maxPlayers, kit.strictCount, kit.troopValue, kit.healthBonus, kit.speedBonus, variants));
        }
    }

    public static OpenClassSelectionPacket read(FriendlyByteBuf buf) {
        String factionId = buf.m_130277_();
        String factionName = buf.m_130277_();
        String factionDescription = buf.m_130277_();
        String factionIcon = buf.m_130277_();
        int count = buf.m_130242_();
        ArrayList<ClassInfo> classes = new ArrayList<ClassInfo>(count);
        for (int i = 0; i < count; ++i) {
            String classId = buf.m_130277_();
            String name = buf.m_130277_();
            String description = buf.m_130277_();
            String role = buf.m_130277_();
            String icon = buf.m_130277_();
            int maxPlayers = buf.m_130242_();
            boolean strictCount = buf.readBoolean();
            int troopValue = buf.m_130242_();
            int healthBonus = buf.m_130242_();
            float speedBonus = buf.readFloat();
            int variantCount = buf.m_130242_();
            ArrayList<VariantInfo> variants = new ArrayList<VariantInfo>(variantCount);
            for (int variantIndex = 0; variantIndex < variantCount; ++variantIndex) {
                variants.add(new VariantInfo(buf.m_130277_(), buf.m_130277_(), buf.m_130277_(), buf.m_130242_()));
            }
            classes.add(new ClassInfo(classId, name, description, role, icon, maxPlayers, strictCount, troopValue, healthBonus, speedBonus, variants));
        }
        return new OpenClassSelectionPacket(factionId, factionName, factionDescription, factionIcon, classes);
    }

    private OpenClassSelectionPacket(String factionId, String factionName, String factionDescription, String factionIcon, List<ClassInfo> classes) {
        this.factionId = factionId;
        this.factionName = factionName;
        this.factionDescription = factionDescription;
        this.factionIcon = factionIcon;
        this.classes = classes;
    }

    public void write(FriendlyByteBuf buf) {
        buf.m_130070_(this.factionId);
        buf.m_130070_(this.factionName);
        buf.m_130070_(this.factionDescription);
        buf.m_130070_(this.factionIcon);
        buf.m_130130_(this.classes.size());
        for (ClassInfo ci : this.classes) {
            buf.m_130070_(ci.classId);
            buf.m_130070_(ci.name);
            buf.m_130070_(ci.description);
            buf.m_130070_(ci.role);
            buf.m_130070_(ci.icon == null ? "" : ci.icon);
            buf.m_130130_(ci.maxPlayers);
            buf.writeBoolean(ci.strictCount);
            buf.m_130130_(ci.troopValue);
            buf.m_130130_(ci.healthBonus);
            buf.writeFloat(ci.speedBonus);
            buf.m_130130_(ci.variants.size());
            for (VariantInfo variant : ci.variants) {
                buf.m_130070_(variant.variantId);
                buf.m_130070_(variant.name != null ? variant.name : variant.variantId);
                buf.m_130070_(variant.description != null ? variant.description : "");
                buf.m_130130_(variant.maxPlayers);
            }
        }
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            try {
                Class.forName("org.espetro.client.ClientPacketHandlers").getMethod("handleOpenClassSelection", OpenClassSelectionPacket.class).invoke(null, this);
            }
            catch (Exception e) {
                e.printStackTrace();
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

    public static class VariantInfo {
        public final String variantId;
        public final String name;
        public final String description;
        public final int maxPlayers;

        public VariantInfo(String variantId, String name, String description, int maxPlayers) {
            this.variantId = variantId;
            this.name = name;
            this.description = description;
            this.maxPlayers = maxPlayers;
        }
    }

    public static class ClassInfo {
        public final String classId;
        public final String name;
        public final String description;
        public final String role;
        public final String icon;
        public final int maxPlayers;
        public final boolean strictCount;
        public final int troopValue;
        public final int healthBonus;
        public final float speedBonus;
        public final List<VariantInfo> variants;

        public ClassInfo(String classId, String name, String description, String role, String icon, int maxPlayers, boolean strictCount, int troopValue, int healthBonus, float speedBonus, List<VariantInfo> variants) {
            this.classId = classId;
            this.name = name;
            this.description = description;
            this.role = role;
            this.icon = icon;
            this.maxPlayers = maxPlayers;
            this.strictCount = strictCount;
            this.troopValue = troopValue;
            this.healthBonus = healthBonus;
            this.speedBonus = speedBonus;
            this.variants = variants != null ? variants : new ArrayList();
        }
    }
}

