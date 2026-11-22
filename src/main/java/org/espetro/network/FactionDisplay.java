package org.espetro.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import org.espetro.Espetro;
import org.espetro.config.GameConfig;
import org.espetro.mapconfig.BattlefieldContext;
import org.espetro.team.*;
import org.espetro.network.core.PacketRegistrar;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * 阵营 / 队伍的显示名与编制选择图片查询（服务端）。
 */
public final class FactionDisplay {

    private FactionDisplay() {
    }

    static String getFactionSelectionImage(String factionId) {
        return getFactionSelectionImageStatic(factionId);
    }

    /** 公开版本，供 GameStateManager 等直接调用。 */
    public static String getFactionSelectionImageStatic(String factionId) {
        if (factionId == null || factionId.isEmpty()) return null;
        org.espetro.team.FactionDataLoader loader =
            org.espetro.team.FactionDataProvider.getOrCreateLoader();
        if (loader != null) {
            org.espetro.team.FactionDataLoader.FactionData faction =
                loader.getFaction(factionId);
            if (faction != null && faction.selectionImage != null && !faction.selectionImage.isEmpty()) {
                return faction.selectionImage;
            }
        }
        return null;
    }

    /**
     * 获取对手队伍的已定编制显示名称。
     * 如果对方编制尚未确定，返回 null。
     */
    static String getOpponentFactionDisplayName(String myTeam) {
        org.espetro.team.ClassSelectManager selectManager =
            org.espetro.team.ClassSelectManager.getInstance();

        String opponentFactionId;
        if ("ATTACK".equals(myTeam)) {
            opponentFactionId = selectManager.getFinalDefendClass();
        } else {
            opponentFactionId = selectManager.getFinalAttackClass();
        }

        return getFactionDisplayName(opponentFactionId);
    }

    static String getFactionDisplayName(String factionId) {
        if (factionId == null || factionId.isEmpty()) return null;

        org.espetro.team.FactionDataLoader loader =
            org.espetro.team.FactionDataProvider.getOrCreateLoader();
        if (loader != null) {
            org.espetro.team.FactionDataLoader.FactionData faction =
                loader.getFaction(factionId);
            if (faction != null && faction.name != null) {
                return faction.name;
            }
        }
        return factionId;
    }

    static String oppositeTeam(String team) {
        return "ATTACK".equals(team) ? "DEFEND" : "ATTACK";
    }

    static String teamDisplayName(String team) {
        return TeamDisplayNames.displayName(team);
    }
}
