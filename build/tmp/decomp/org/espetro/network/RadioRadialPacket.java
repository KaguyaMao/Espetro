/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.network.NetworkEvent$Context
 *  net.minecraftforge.network.PacketDistributor
 */
package org.espetro.network;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import org.espetro.Espetro;
import org.espetro.bastion.BastionData;
import org.espetro.bastion.BastionManager;
import org.espetro.logistics.LogisticsConfig;
import org.espetro.logistics.resupply.ResupplySessionManager;
import org.espetro.logistics.resupply.ResupplySourceRef;
import org.espetro.network.NetworkManager;
import org.espetro.team.ClassCountManager;
import org.espetro.team.FactionDataLoader;
import org.espetro.team.FactionDataProvider;
import org.espetro.team.SquadManager;

public class RadioRadialPacket {
    private final Kind kind;
    private final BlockPos pos;
    private final List<ClassEntry> classes;

    private RadioRadialPacket(Kind kind, BlockPos pos, List<ClassEntry> classes) {
        this.kind = kind;
        this.pos = pos;
        this.classes = classes != null ? classes : List.of();
    }

    public static RadioRadialPacket openRequest(BlockPos pos) {
        return new RadioRadialPacket(Kind.OPEN_REQUEST, pos, List.of());
    }

    public static RadioRadialPacket resupply(BlockPos pos) {
        return new RadioRadialPacket(Kind.RESUPPLY, pos, List.of());
    }

    public static RadioRadialPacket classList(List<ClassEntry> classes) {
        return RadioRadialPacket.classList(BlockPos.f_121853_, classes);
    }

    public static RadioRadialPacket classList(BlockPos pos, List<ClassEntry> classes) {
        return new RadioRadialPacket(Kind.CLASS_LIST, pos != null ? pos : BlockPos.f_121853_, classes);
    }

    public static void openClassMenuAt(ServerPlayer player, BlockPos sourcePos) {
        new RadioRadialPacket(Kind.OPEN_REQUEST, sourcePos != null ? sourcePos : BlockPos.f_121853_, List.of()).handleOpen(player);
    }

    public static RadioRadialPacket read(FriendlyByteBuf buf) {
        Kind kind;
        try {
            kind = Kind.valueOf(buf.m_130277_());
        }
        catch (Exception e) {
            kind = Kind.OPEN_REQUEST;
        }
        BlockPos pos = buf.m_130135_();
        int n = buf.m_130242_();
        ArrayList<ClassEntry> list = new ArrayList<ClassEntry>(n);
        for (int i = 0; i < n; ++i) {
            String classId = buf.m_130277_();
            String name = buf.m_130277_();
            String icon = buf.m_130277_();
            String iconImage = buf.m_130277_();
            String defaultVariantId = buf.m_130277_();
            int currentCount = buf.m_130242_();
            int maxCount = buf.m_130242_();
            boolean showCount = buf.readBoolean();
            boolean enabled = buf.readBoolean();
            boolean cooldownBlocked = buf.readBoolean();
            String denialMessage = buf.m_130277_();
            int variantCount = buf.m_130242_();
            ArrayList<VariantEntry> variants = new ArrayList<VariantEntry>(variantCount);
            for (int v = 0; v < variantCount; ++v) {
                variants.add(new VariantEntry(buf.m_130277_(), buf.m_130277_(), buf.m_130242_(), buf.m_130242_(), buf.readBoolean(), buf.readBoolean(), buf.m_130277_()));
            }
            list.add(new ClassEntry(classId, name, icon, iconImage, defaultVariantId, currentCount, maxCount, showCount, enabled, cooldownBlocked, denialMessage, variants));
        }
        return new RadioRadialPacket(kind, pos, list);
    }

    public void write(FriendlyByteBuf buf) {
        buf.m_130070_(this.kind.name());
        buf.m_130064_(this.pos != null ? this.pos : BlockPos.f_121853_);
        buf.m_130130_(this.classes.size());
        for (ClassEntry e : this.classes) {
            buf.m_130070_(e.classId);
            buf.m_130070_(e.name);
            buf.m_130070_(e.icon);
            buf.m_130070_(e.iconImage);
            buf.m_130070_(e.defaultVariantId);
            buf.m_130130_(e.currentCount);
            buf.m_130130_(e.maxCount);
            buf.writeBoolean(e.showCount);
            buf.writeBoolean(e.enabled);
            buf.writeBoolean(e.cooldownBlocked);
            buf.m_130070_(e.denialMessage);
            buf.m_130130_(e.variants.size());
            for (VariantEntry variant : e.variants) {
                buf.m_130070_(variant.variantId);
                buf.m_130070_(variant.name);
                buf.m_130130_(variant.currentCount);
                buf.m_130130_(variant.maxCount);
                buf.writeBoolean(variant.strictCount);
                buf.writeBoolean(variant.enabled);
                buf.m_130070_(variant.denialMessage);
            }
        }
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        if (context.getDirection().getReceptionSide().isClient()) {
            context.enqueueWork(() -> {
                if (this.kind == Kind.CLASS_LIST) {
                    try {
                        Class.forName("org.espetro.client.gui.RadioRadialController").getMethod("onClassList", BlockPos.class, List.class).invoke(null, this.pos, this.classes);
                    }
                    catch (Throwable t) {
                        InvocationTargetException ite;
                        Throwable cause = t instanceof InvocationTargetException && (ite = (InvocationTargetException)t).getCause() != null ? ite.getCause() : t;
                        Espetro.LOGGER.warn("RadioRadial client open failed: {}", (Object)cause.toString(), (Object)cause);
                    }
                }
            });
            context.setPacketHandled(true);
            return;
        }
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) {
                return;
            }
            switch (this.kind) {
                case OPEN_REQUEST: {
                    this.handleOpen(player);
                    break;
                }
                case RESUPPLY: {
                    this.handleResupply(player);
                    break;
                }
            }
        });
        context.setPacketHandled(true);
    }

    private void handleOpen(ServerPlayer player) {
        if (!RadioRadialPacket.isFriendlyRadioNearby(player, this.pos)) {
            player.m_213846_(Component.m_237113_("\u00a7c\u9644\u8fd1\u6ca1\u6709\u5df1\u65b9 Radio\u3002"));
            return;
        }
        String factionId = ClassCountManager.getInstance().getPlayerFaction(player.m_20148_());
        if (factionId == null) {
            player.m_213846_(Component.m_237113_("\u00a7c\u4f60\u5c1a\u672a\u9009\u62e9\u7f16\u5236\u3002"));
            return;
        }
        FactionDataLoader loader = FactionDataProvider.getOrCreateLoader();
        FactionDataLoader.ClassKitData[] kits = loader.getClassesForFaction(factionId);
        ArrayList<ClassEntry> list = new ArrayList<ClassEntry>();
        ClassCountManager counts = ClassCountManager.getInstance();
        String team = counts.getEffectivePlayerTeam(player.m_20148_());
        int squadId = SquadManager.getInstance().getPlayerSquadId(player.m_20148_());
        boolean inSquad = squadId != -1;
        int squadSize = inSquad ? SquadManager.getInstance().getSquadMemberUuids(team, squadId).size() : 0;
        int cooldown = counts.getClassSwitchCooldownRemaining(player.m_20148_());
        if (kits != null) {
            for (FactionDataLoader.ClassKitData kit : kits) {
                boolean cooldownBlocked;
                if (kit == null) continue;
                int squadCount = counts.getSquadClassCountForViewer(player.m_20148_(), team, kit.id);
                int maxCount = kit.teamCount ? Math.max(1, kit.maxPlayers) : (kit.maxPerSquad > 0 ? kit.maxPerSquad : Math.max(1, kit.maxPlayers));
                int teamCount = counts.getCount(team, kit.id);
                Object denial = "";
                boolean bl = cooldownBlocked = cooldown > 0;
                if (cooldownBlocked) {
                    denial = "\u804c\u4e1a\u5207\u6362\u51b7\u5374\u4e2d\uff0c\u8fd8\u9700\u7b49\u5f85 " + cooldown + " \u79d2\u3002";
                } else if (!inSquad) {
                    denial = "\u8bf7\u5148\u52a0\u5165\u73ed\u7ec4\u5c0f\u961f\u540e\u518d\u9009\u62e9\u804c\u4e1a\u3002";
                } else if (kit.teammatesNeed > 0 && squadSize < kit.teammatesNeed) {
                    denial = "\u5c0f\u961f\u8fbe\u5230 " + kit.teammatesNeed + " \u4eba\u540e\u624d\u80fd\u9009\u62e9\u8be5\u804c\u4e1a\u3002";
                } else if (kit.teamCount && squadCount >= kit.maxPlayers) {
                    denial = "\u672c\u5c0f\u961f\u8be5\u804c\u4e1a\u4eba\u6570\u5df2\u6ee1\uff08" + squadCount + "/" + kit.maxPlayers + "\uff09\u3002";
                } else if (!kit.teamCount && teamCount >= kit.maxPlayers) {
                    denial = "\u8be5\u804c\u4e1a\u5168\u961f\u4eba\u6570\u5df2\u6ee1\uff08" + teamCount + "/" + kit.maxPlayers + "\uff09\u3002";
                } else if (!kit.teamCount && kit.maxPerSquad > 0 && squadCount >= kit.maxPerSquad) {
                    denial = "\u672c\u5c0f\u961f\u8be5\u804c\u4e1a\u4eba\u6570\u5df2\u6ee1\uff08" + squadCount + "/" + kit.maxPerSquad + "\uff09\u3002";
                }
                boolean enabled = ((String)denial).isEmpty();
                ArrayList<VariantEntry> variants = new ArrayList<VariantEntry>();
                String defaultVariantId = "";
                if (kit.variants != null) {
                    FactionDataLoader.ClassVariantData defaultVariant = kit.variants.get("default");
                    if (defaultVariant != null) {
                        defaultVariantId = defaultVariant.id;
                    } else if (!kit.variants.isEmpty()) {
                        defaultVariantId = kit.variants.values().iterator().next().id;
                    }
                    for (FactionDataLoader.ClassVariantData variant : kit.variants.values()) {
                        int variantCount = kit.teamCount ? counts.countVariantInSquad(team, squadId, kit.id, variant.id) : counts.getVariantCount(team, kit.id, variant.id);
                        boolean variantEnabled = enabled && (!kit.strictCount || variantCount < variant.maxPlayers);
                        Object variantDenial = denial;
                        if (enabled && !variantEnabled) {
                            variantDenial = "\u8be5\u88c5\u5907\u53d8\u4f53\u4eba\u6570\u5df2\u6ee1\uff08" + variantCount + "/" + variant.maxPlayers + "\uff09\u3002";
                        }
                        variants.add(new VariantEntry(variant.id, variant.name, variantCount, variant.maxPlayers, kit.strictCount, variantEnabled, (String)variantDenial));
                    }
                }
                list.add(new ClassEntry(kit.id, kit.name, kit.icon != null ? kit.icon : "", kit.iconImage != null ? kit.iconImage : "", defaultVariantId, squadCount, maxCount, inSquad, enabled, cooldownBlocked, (String)denial, variants));
            }
        }
        NetworkManager.NET.send(PacketDistributor.PLAYER.with(() -> player), (Object)RadioRadialPacket.classList(this.pos, list));
    }

    private void handleResupply(ServerPlayer player) {
        ResupplySessionManager.open(player, ResupplySourceRef.radio(this.pos));
    }

    public static boolean isFriendlyRadioNearby(ServerPlayer player, BlockPos clickPos) {
        return RadioRadialPacket.findFriendlyRadioNearby(player, clickPos) != null;
    }

    public static BastionData findFriendlyRadioNearby(ServerPlayer player, BlockPos clickPos) {
        String team = Espetro.getPlayerTeam(player);
        if (team == null) {
            return null;
        }
        double radius = LogisticsConfig.get().depositRadius + 2.0;
        BlockPos playerPos = player.m_20183_();
        BlockPos anchor = clickPos != null ? clickPos : playerPos;
        BastionData atClick = BastionManager.getInstance().findRadioByBlockPos(anchor);
        if (atClick != null && team.equals(atClick.getTeam()) && atClick.isActive()) {
            return playerPos.m_123314_(atClick.getPosition(), radius) || playerPos.m_123314_(anchor, radius) ? atClick : null;
        }
        BastionData nearest = BastionManager.getInstance().findNearestRadio(player.m_284548_(), anchor, team, radius);
        if (nearest == null) {
            return null;
        }
        BlockPos radioPos = nearest.getPosition();
        return radioPos != null && playerPos.m_123314_(radioPos, radius) ? nearest : null;
    }

    public static enum Kind {
        OPEN_REQUEST,
        RESUPPLY,
        CLASS_LIST;

    }

    public static class VariantEntry {
        public final String variantId;
        public final String name;
        public final int currentCount;
        public final int maxCount;
        public final boolean strictCount;
        public final boolean enabled;
        public final String denialMessage;

        public VariantEntry(String variantId, String name, int currentCount, int maxCount, boolean strictCount, boolean enabled, String denialMessage) {
            this.variantId = variantId == null ? "" : variantId;
            this.name = name == null ? "" : name;
            this.currentCount = Math.max(0, currentCount);
            this.maxCount = Math.max(0, maxCount);
            this.strictCount = strictCount;
            this.enabled = enabled;
            this.denialMessage = denialMessage == null ? "" : denialMessage;
        }
    }

    public static class ClassEntry {
        public final String classId;
        public final String name;
        public final String icon;
        public final String iconImage;
        public final String defaultVariantId;
        public final int currentCount;
        public final int maxCount;
        public final boolean showCount;
        public final boolean enabled;
        public final boolean cooldownBlocked;
        public final String denialMessage;
        public final List<VariantEntry> variants;

        public ClassEntry(String classId, String name, String icon) {
            this(classId, name, icon, "", "", 0, 0, false, false, false, "", List.of());
        }

        public ClassEntry(String classId, String name, String icon, String iconImage, String defaultVariantId, int currentCount, int maxCount, boolean showCount, boolean enabled, boolean cooldownBlocked, String denialMessage, List<VariantEntry> variants) {
            this.classId = classId == null ? "" : classId;
            this.name = name == null ? "" : name;
            this.icon = icon == null ? "" : icon;
            this.iconImage = iconImage == null ? "" : iconImage;
            this.defaultVariantId = defaultVariantId == null ? "" : defaultVariantId;
            this.currentCount = Math.max(0, currentCount);
            this.maxCount = Math.max(0, maxCount);
            this.showCount = showCount;
            this.enabled = enabled;
            this.cooldownBlocked = cooldownBlocked;
            this.denialMessage = denialMessage == null ? "" : denialMessage;
            this.variants = variants != null ? List.copyOf(variants) : List.of();
        }
    }
}

