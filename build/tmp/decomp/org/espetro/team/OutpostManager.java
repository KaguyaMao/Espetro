/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 */
package org.espetro.team;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameType;
import org.espetro.Espetro;
import org.espetro.bastion.BastionManager;
import org.espetro.config.GameConfig;
import org.espetro.mapconfig.BattlefieldContext;
import org.espetro.team.GamePhase;
import org.espetro.team.GameStateManager;
import org.espetro.team.TeamDisplayNames;
import org.espetro.team.TeamPackManager;

public class OutpostManager {
    private static final Gson GSON = new Gson();
    private static OutpostManager INSTANCE;
    private final List<Outpost> outposts = new ArrayList<Outpost>();
    private final Map<UUID, Long> redeployCooldowns = new HashMap<UUID, Long>();
    private int redeployCooldownSeconds = 60;
    private boolean active = false;

    private OutpostManager() {
        INSTANCE = this;
    }

    public static OutpostManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new OutpostManager();
        }
        return INSTANCE;
    }

    public static void init() {
        INSTANCE = new OutpostManager();
    }

    public void loadConfig(MinecraftServer server) {
    }

    private void parseAndApply(String json) {
        JsonObject root = (JsonObject)GSON.fromJson(json, JsonObject.class);
        if (root == null) {
            return;
        }
        if (root.has("redeploy_cooldown_seconds")) {
            this.redeployCooldownSeconds = Math.max(0, root.get("redeploy_cooldown_seconds").getAsInt());
        }
        if (!root.has("outposts")) {
            return;
        }
        JsonArray arr = root.getAsJsonArray("outposts");
        for (JsonElement elem : arr) {
            if (!elem.isJsonObject()) continue;
            JsonObject obj = elem.getAsJsonObject();
            String name = obj.has("name") ? obj.get("name").getAsString() : "\u524d\u54e8";
            double x = obj.has("x") ? obj.get("x").getAsDouble() : 0.0;
            double y = obj.has("y") ? obj.get("y").getAsDouble() : 64.0;
            double z = obj.has("z") ? obj.get("z").getAsDouble() : 0.0;
            float yaw = obj.has("yaw") ? (float)obj.get("yaw").getAsDouble() : 0.0f;
            this.outposts.add(new Outpost(name, x, y, z, yaw));
        }
    }

    public void applyExternalJson(String json) {
        this.outposts.clear();
        this.redeployCooldownSeconds = 60;
        this.parseAndApply(json);
        this.active = false;
        this.redeployCooldowns.clear();
    }

    public List<Outpost> getOutposts() {
        return new ArrayList<Outpost>(this.outposts);
    }

    public boolean isAvailable() {
        return this.active && !TeamDisplayNames.isSymmetricMode() && GameStateManager.getInstance().getCurrentPhase() == GamePhase.DEPLOYING && !this.outposts.isEmpty();
    }

    public boolean canListFor(String team) {
        return this.isAvailable() && "DEFEND".equals(team);
    }

    public void activate() {
        this.active = true;
        Espetro.LOGGER.info("\u524d\u54e8\u57fa\u5730\u5df2\u6fc0\u6d3b: {} \u4e2a", (Object)this.outposts.size());
    }

    public void deactivate() {
        if (this.active) {
            this.active = false;
            Espetro.LOGGER.info("\u524d\u54e8\u57fa\u5730\u5df2\u505c\u7528\uff08\u6218\u6597\u5f00\u59cb\uff09");
        }
    }

    public String tryDeploy(ServerPlayer player, int outpostIndex) {
        if (TeamDisplayNames.isSymmetricMode()) {
            return "\u00a7c\u5f53\u524d\u6a21\u5f0f\u53cc\u65b9\u90fd\u4e0d\u53ef\u4f7f\u7528\u524d\u54e8\u57fa\u5730\uff01";
        }
        if (!this.isAvailable()) {
            return "\u00a7c\u524d\u54e8\u57fa\u5730\u5df2\u5931\u6548\uff01";
        }
        String team = Espetro.getPlayerTeam(player);
        if (!"DEFEND".equals(team)) {
            return "\u00a7c\u53ea\u6709\u9632\u5b88\u65b9\u53ef\u4ee5\u4f7f\u7528\u524d\u54e8\u57fa\u5730\uff01";
        }
        if (!BastionManager.getInstance().isWaitingForBastion(player.m_20148_())) {
            return "\u00a7c\u53ea\u6709\u9635\u4ea1\u6216\u4f7f\u7528\u201c\u91cd\u65b0\u90e8\u7f72\u201d\u540e\u624d\u80fd\u9009\u62e9\u524d\u54e8\u57fa\u5730\uff01";
        }
        if (outpostIndex < 0 || outpostIndex >= this.outposts.size()) {
            return "\u00a7c\u524d\u54e8\u57fa\u5730\u4e0d\u5b58\u5728\uff01";
        }
        Outpost outpost = this.outposts.get(outpostIndex);
        ServerLevel battlefield = BattlefieldContext.requireBattlefield(player.f_8924_);
        TeamPackManager.getInstance().cancelPendingRespawn(player.m_20148_());
        BastionManager.getInstance().clearWaiting(player.m_20148_());
        player.m_8999_(battlefield, outpost.x, outpost.y, outpost.z, outpost.yaw, 0.0f);
        player.m_143403_(GameType.SURVIVAL);
        player.m_21219_();
        player.m_7292_(new MobEffectInstance(MobEffects.f_19606_, GameConfig.getRespawnInvincibilityTicks(), 127, false, false, false));
        GameStateManager.getInstance().applyBattlefieldMiningRestriction(player);
        player.m_213846_(Component.m_237113_("\u00a7a\u5df2\u4f20\u9001\u5230\u524d\u54e8\u57fa\u5730: \u00a7f" + outpost.name));
        return null;
    }

    public String tryStartRedeploy(ServerPlayer player) {
        if (TeamDisplayNames.isSymmetricMode()) {
            return "\u00a7c\u5f53\u524d\u6a21\u5f0f\u53cc\u65b9\u90fd\u4e0d\u53ef\u4f7f\u7528\u524d\u54e8\u57fa\u5730\uff01";
        }
        if (!this.isAvailable()) {
            return "\u00a7c\u53ea\u80fd\u5728\u5e03\u9632\u9636\u6bb5\u91cd\u65b0\u90e8\u7f72\uff01";
        }
        if (!"DEFEND".equals(Espetro.getPlayerTeam(player))) {
            return "\u00a7c\u53ea\u6709\u9632\u5b88\u65b9\u53ef\u4ee5\u4f7f\u7528\u524d\u54e8\u91cd\u65b0\u90e8\u7f72\uff01";
        }
        if (BastionManager.getInstance().isWaitingForBastion(player.m_20148_())) {
            return "\u00a7c\u4f60\u5df2\u5728\u7b49\u5f85\u9009\u62e9\u90e8\u7f72\u70b9\uff01";
        }
        int remaining = this.getRedeployCooldownRemaining(player.m_20148_());
        if (remaining > 0) {
            return "\u00a7c\u91cd\u65b0\u90e8\u7f72\u51b7\u5374\u4e2d\uff0c\u8bf7\u7b49\u5f85 " + remaining + " \u79d2\uff01";
        }
        this.redeployCooldowns.put(player.m_20148_(), System.currentTimeMillis());
        this.prepareDeployTargets(BattlefieldContext.requireBattlefield(player.f_8924_));
        player.m_213846_(Component.m_237113_("\u00a7e\u6b63\u5728\u91cd\u65b0\u90e8\u7f72\uff0c\u8bf7\u5728\u590d\u6d3b\u540e\u9009\u62e9\u90e8\u7f72\u70b9\u3002"));
        player.m_6074_();
        return null;
    }

    public void prepareDeployTargets(ServerLevel level) {
        if (!this.isAvailable() || level == null) {
            return;
        }
        for (Outpost outpost : this.outposts) {
            BlockPos target = BlockPos.m_274561_(outpost.x, outpost.y, outpost.z);
            level.m_7726_().m_8387_(TicketType.f_9447_, new ChunkPos(target), 3, target);
        }
    }

    public int getRedeployCooldownRemaining(UUID playerId) {
        Long lastUse = this.redeployCooldowns.get(playerId);
        if (lastUse == null || this.redeployCooldownSeconds <= 0) {
            return 0;
        }
        long remainingMillis = (long)this.redeployCooldownSeconds * 1000L - (System.currentTimeMillis() - lastUse);
        return remainingMillis <= 0L ? 0 : (int)((remainingMillis + 999L) / 1000L);
    }

    public int getRedeployCooldownSeconds() {
        return this.redeployCooldownSeconds;
    }

    public boolean isPlayerNearAvailableOutpost(ServerPlayer player, double radius) {
        if (!this.isAvailable() || !"DEFEND".equals(Espetro.getPlayerTeam(player))) {
            return false;
        }
        BlockPos playerPos = player.m_20183_();
        for (Outpost outpost : this.outposts) {
            BlockPos outpostPos = BlockPos.m_274561_(outpost.x, outpost.y, outpost.z);
            if (!playerPos.m_123314_(outpostPos, radius)) continue;
            return true;
        }
        return false;
    }

    public void reset() {
        this.active = false;
        this.redeployCooldowns.clear();
    }

    public static class Outpost {
        public final String name;
        public final double x;
        public final double y;
        public final double z;
        public final float yaw;

        public Outpost(String name, double x, double y, double z, float yaw) {
            this.name = name;
            this.x = x;
            this.y = y;
            this.z = z;
            this.yaw = yaw;
        }

        public String getPosString() {
            return (int)this.x + ", " + (int)this.y + ", " + (int)this.z;
        }
    }
}

