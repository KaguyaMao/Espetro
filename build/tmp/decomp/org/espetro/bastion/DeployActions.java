/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package org.espetro.bastion;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.espetro.bastion.BastionData;
import org.espetro.bastion.BastionItems;
import org.espetro.bastion.BastionManager;
import org.espetro.bastion.FortificationManager;
import org.espetro.logistics.LogisticsConfig;
import org.espetro.network.NetworkManager;
import org.espetro.team.ClassCountManager;
import org.espetro.team.FactionDataLoader;
import org.espetro.team.FactionDataProvider;
import org.espetro.team.SquadManager;
import org.espetro.team.VoteManager;

public final class DeployActions {
    private DeployActions() {
    }

    public static void giveRadioItem(ServerPlayer serverPlayer) {
        DeployActions.beginConstructionPreview(serverPlayer, "builtin_radio");
    }

    public static void startHabChannel(ServerPlayer serverPlayer) {
        DeployActions.beginConstructionPreview(serverPlayer, "builtin_hab");
    }

    private static void beginConstructionPreview(ServerPlayer player, String id) {
        String error = FortificationManager.getInstance().beginPreview(player, id);
        player.m_213846_(Component.m_237113_(error == null ? "\u00a7e\u5de6\u952e\u786e\u8ba4\u65bd\u5de5\u8303\u56f4\uff0c\u53f3\u952e\u53d6\u6d88\u3002" : error));
    }

    @Nullable
    static String checkHabPerRadioLimit(ServerPlayer player, String team) {
        Level level = player.m_9236_();
        if (!(level instanceof ServerLevel)) {
            return null;
        }
        ServerLevel level2 = (ServerLevel)level;
        List<BastionData> radios = BastionManager.getInstance().findCoveringRadios(level2, player.m_20183_(), team);
        if (radios.isEmpty()) {
            return "\u00a7c\u5fc5\u987b\u5728\u5df1\u65b9 Radio \u4f5c\u7528\u8303\u56f4\u5185\u90e8\u7f72\u5175\u7ad9\u3002";
        }
        BastionData radio = radios.get(0);
        int max = DeployActions.getMaxHabsPerRadio(player);
        int count = DeployActions.countHabsCoveredBy(radio, team);
        if (count >= max) {
            return "\u00a7c\u8be5 Radio \u8303\u56f4\u5185\u5175\u7ad9\u5df2\u8fbe\u4e0a\u9650 (" + count + "/" + max + ")\u3002";
        }
        return null;
    }

    private static int getMaxHabsPerRadio(ServerPlayer player) {
        String factionId = ClassCountManager.getInstance().getPlayerFaction(player.m_20148_());
        if (factionId == null) {
            return 2;
        }
        FactionDataLoader loader = FactionDataProvider.getOrCreateLoader();
        FactionDataLoader.FactionData faction = loader.getFaction(factionId);
        if (faction == null) {
            return 2;
        }
        return Math.max(0, faction.maxHabsPerRadio);
    }

    private static int countHabsCoveredBy(BastionData radio, String team) {
        if (radio == null) {
            return 0;
        }
        int n = 0;
        for (BastionData b : BastionManager.getInstance().getAllBastions()) {
            if (!b.isActive() || !b.isHab() || !team.equals(b.getTeam()) || !BastionManager.getInstance().isCoveredByFriendlyRadio(b) || !DeployActions.isWithinRadioBuildRadius(radio, b.getPosition())) continue;
            ++n;
        }
        return n;
    }

    private static boolean isWithinRadioBuildRadius(BastionData radio, BlockPos pos) {
        double dz;
        double dy;
        if (radio == null || pos == null) {
            return false;
        }
        double r = LogisticsConfig.get().radioBuildRadius;
        double dx = radio.getPosition().m_123341_() - pos.m_123341_();
        return dx * dx + (dy = (double)(radio.getPosition().m_123342_() - pos.m_123342_())) * dy + (dz = (double)(radio.getPosition().m_123343_() - pos.m_123343_())) * dz <= r * r;
    }

    public static void openVehicleDeploy(ServerPlayer player) {
        String factionId = ClassCountManager.getInstance().getPlayerFaction(player.m_20148_());
        if (factionId == null) {
            player.m_213846_(Component.m_237113_("\u00a7c\u4f60\u8fd8\u6ca1\u6709\u9009\u62e9\u7f16\u5236\uff01"));
            return;
        }
        NetworkManager.sendVehicleDeployScreen(player, factionId);
    }

    public static boolean hasRadioItem(ServerPlayer player) {
        if (BastionItems.RADIO_BLOCK_ITEM == null) {
            return false;
        }
        for (ItemStack stack : player.m_150109_().f_35974_) {
            if (stack.m_41720_() != BastionItems.RADIO_BLOCK_ITEM) continue;
            return true;
        }
        for (ItemStack stack : player.m_150109_().f_35976_) {
            if (stack.m_41720_() != BastionItems.RADIO_BLOCK_ITEM) continue;
            return true;
        }
        return false;
    }

    static boolean checkCommanderOrLeader(ServerPlayer serverPlayer, LogisticsConfig.RadioPlacementSettings radio, String what) {
        boolean commander = VoteManager.getInstance().isCommander(serverPlayer.m_20148_());
        boolean squadLeader = SquadManager.getInstance().isSquadLeader(serverPlayer.m_20148_());
        if (radio.requireCommander && !commander) {
            serverPlayer.m_213846_(Component.m_237113_("\u00a7c\u53ea\u6709\u6307\u6325\u5b98\u624d\u80fd\u90e8\u7f72" + what + "\uff01"));
            return false;
        }
        if (!(commander || radio.allowSquadLeader && squadLeader)) {
            serverPlayer.m_213846_(Component.m_237113_(radio.allowSquadLeader ? "\u00a7c\u53ea\u6709\u5c0f\u961f\u957f\u6216\u6307\u6325\u5b98\u624d\u80fd\u90e8\u7f72" + what + "\uff01" : "\u00a7c\u53ea\u6709\u6307\u6325\u5b98\u624d\u80fd\u90e8\u7f72" + what + "\uff01"));
            return false;
        }
        return true;
    }
}

