/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraftforge.network.PacketDistributor
 */
package org.espetro.bastion;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import org.espetro.Espetro;
import org.espetro.bastion.BastionData;
import org.espetro.bastion.BastionItems;
import org.espetro.bastion.BastionManager;
import org.espetro.bastion.FobSupplyTracker;
import org.espetro.bastion.FortificationConfig;
import org.espetro.bastion.FortificationProgressPolicy;
import org.espetro.bastion.FortificationSpatialIndex;
import org.espetro.bastion.FortificationTemplateCompiler;
import org.espetro.bastion.FortificationTransform;
import org.espetro.bastion.OnBuildingBlock;
import org.espetro.bastion.RadioCoveragePolicy;
import org.espetro.bastion.RadioLossPolicy;
import org.espetro.dimension.BattlefieldWorldManager;
import org.espetro.logistics.LogisticsConfig;
import org.espetro.mapconfig.BattlefieldContext;
import org.espetro.network.FortificationPreviewPacket;
import org.espetro.network.FortificationProgressPacket;
import org.espetro.network.NetworkManager;
import org.espetro.team.ClassCountManager;
import org.espetro.team.FactionDataLoader;
import org.espetro.team.FactionDataProvider;
import org.espetro.team.GamePhase;
import org.espetro.team.GameStateManager;
import org.espetro.team.SquadManager;
import org.espetro.team.VoteManager;
import org.espetro.vehicle.VehicleManager;

public final class FortificationManager {
    public static final String BUILTIN_RADIO = "builtin_radio";
    public static final String BUILTIN_HAB = "builtin_hab";
    private static final double PLACE_REACH = 6.0;
    private static final long PREVIEW_LIFETIME_TICKS = 1200L;
    private static final long WORK_INTERVAL_TICKS = 5L;
    private static final FortificationManager INSTANCE = new FortificationManager();
    private final Map<UUID, Construction> constructions = new HashMap<UUID, Construction>();
    private final Map<String, UUID> positionIndex = new HashMap<String, UUID>();
    private final Map<String, PlacedFort> placed = new HashMap<String, PlacedFort>();
    private final Map<UUID, UUID> entityIndex = new HashMap<UUID, UUID>();
    private final Map<UUID, UUID> bastionIndex = new HashMap<UUID, UUID>();
    private final Map<UUID, Boolean> damageableEntityIndex = new HashMap<UUID, Boolean>();
    private final Map<UUID, PreviewSession> previews = new HashMap<UUID, PreviewSession>();
    private final Map<UUID, Long> lastWorkTick = new HashMap<UUID, Long>();
    private final FortificationSpatialIndex spatialIndex = new FortificationSpatialIndex();

    private FortificationManager() {
    }

    public static FortificationManager getInstance() {
        return INSTANCE;
    }

    public void reset() {
        for (PlacedFort fort : new ArrayList<PlacedFort>(this.placed.values())) {
            VehicleManager.getInstance().unregisterMappedSupplyStation(fort.mapId());
        }
        this.constructions.clear();
        this.positionIndex.clear();
        this.placed.clear();
        this.entityIndex.clear();
        this.damageableEntityIndex.clear();
        this.bastionIndex.clear();
        this.previews.clear();
        this.lastWorkTick.clear();
        this.spatialIndex.clear();
    }

    public void clearPlayer(UUID playerId) {
        if (playerId == null) {
            return;
        }
        this.previews.remove(playerId);
        this.lastWorkTick.remove(playerId);
    }

    @Nullable
    public String beginPreview(ServerPlayer player, String fortId) {
        String common = this.validateCommon(player);
        if (common != null) {
            return common;
        }
        String team = FortificationManager.normalizeTeam(Espetro.getPlayerTeam(player));
        if (team == null) {
            return "\u00a7c\u65e0\u6cd5\u786e\u5b9a\u961f\u4f0d\u3002";
        }
        Blueprint blueprint = this.createBlueprint(fortId, team);
        if (blueprint == null) {
            return "\u00a7c\u5de5\u4e8b\u914d\u7f6e\u65e0\u6548\u6216\u7f3a\u5c11\u6240\u9700\u6a21\u7ec4\u3002";
        }
        String roleError = this.validateSelectionRole(player, blueprint);
        if (roleError != null) {
            return roleError;
        }
        UUID token = UUID.randomUUID();
        String dimension = player.m_9236_().m_46472_().m_135782_().toString();
        this.previews.put(player.m_20148_(), new PreviewSession(token, dimension, blueprint, team, player.m_284548_().m_46467_() + 1200L));
        List<FortificationPreviewPacket.Offset> offsets = blueprint.slots.stream().map(slot -> new FortificationPreviewPacket.Offset(slot.offset.m_123341_(), slot.offset.m_123342_(), slot.offset.m_123343_())).toList();
        NetworkManager.NET.send(PacketDistributor.PLAYER.with(() -> player), (Object)new FortificationPreviewPacket(token, blueprint.id, blueprint.displayName, offsets));
        return null;
    }

    @Nullable
    public String cancelPreview(ServerPlayer player, UUID token) {
        PreviewSession preview = this.previews.get(player.m_20148_());
        if (preview != null && preview.token.equals(token)) {
            this.previews.remove(player.m_20148_());
        }
        return null;
    }

    @Nullable
    public String confirmPreview(ServerPlayer player, UUID token, BlockPos anchor, Direction facing) {
        PreviewSession preview = this.previews.get(player.m_20148_());
        if (preview == null || !preview.token.equals(token)) {
            return "\u00a7c\u5de5\u4e8b\u9884\u89c8\u5df2\u5931\u6548\uff0c\u8bf7\u91cd\u65b0\u9009\u62e9\u3002";
        }
        if (preview.expiresAt < player.m_284548_().m_46467_()) {
            this.previews.remove(player.m_20148_());
            return "\u00a7c\u5de5\u4e8b\u9884\u89c8\u5df2\u8d85\u65f6\uff0c\u8bf7\u91cd\u65b0\u9009\u62e9\u3002";
        }
        if (!preview.dimension.equals(player.m_9236_().m_46472_().m_135782_().toString())) {
            this.previews.remove(player.m_20148_());
            return "\u00a7c\u4f60\u5df2\u79bb\u5f00\u9884\u89c8\u6240\u5728\u533a\u57df\u3002";
        }
        String common = this.validateCommon(player);
        if (common != null) {
            return common;
        }
        if (facing == null || !facing.m_122434_().m_122479_()) {
            return "\u00a7c\u65e0\u6548\u7684\u5de5\u4e8b\u65b9\u5411\u3002";
        }
        if (facing != player.m_6350_()) {
            return "\u00a7c\u5de5\u4e8b\u65b9\u5411\u5df2\u53d8\u5316\uff0c\u8bf7\u91cd\u65b0\u5bf9\u51c6\u540e\u786e\u8ba4\u3002";
        }
        BlockPos serverTarget = FortificationManager.raycastPlacePos(player);
        if (serverTarget == null || !serverTarget.equals(anchor) || player.m_146892_().m_82557_(Vec3.m_82512_(anchor)) > 64.0) {
            return "\u00a7c\u653e\u7f6e\u70b9\u5df2\u53d8\u5316\uff0c\u8bf7\u91cd\u65b0\u5bf9\u51c6\u540e\u786e\u8ba4\u3002";
        }
        String roleError = this.validateSelectionRole(player, preview.blueprint);
        if (roleError != null) {
            return roleError;
        }
        List<WorldSlot> finalSlots = FortificationManager.transform(preview.blueprint.slots, anchor, facing);
        List<BlockPos> footprint = FortificationManager.footprint(finalSlots);
        if (!FortificationManager.spaceIsClear(player.m_284548_(), finalSlots, player)) {
            return "\u00a7c\u7ea2\u8272\u8303\u56f4\u5185\u5b58\u5728\u65b9\u5757\u6216\u5b9e\u4f53\u3002";
        }
        for (WorldSlot slot : finalSlots) {
            if (!this.positionIndex.containsKey(FortificationManager.posKey(player.m_284548_(), slot.pos))) continue;
            return "\u00a7c\u8be5\u7a7a\u95f4\u5df2\u88ab\u5176\u4ed6\u5de5\u4e8b\u5360\u7528\u3002";
        }
        PlacementBacking backing = this.validateBacking(player, preview, anchor);
        if (backing.error != null) {
            return backing.error;
        }
        if (!this.debitBacking(player, preview.blueprint, backing, anchor)) {
            return "\u00a7c\u5efa\u9020\u8d44\u6e90\u4e0d\u8db3\u3002";
        }
        Construction construction = new Construction(preview.blueprint, preview.team, preview.dimension, anchor, facing, finalSlots, footprint, backing.radioId);
        if (!FortificationManager.placeFoundations(player.m_284548_(), construction, 0)) {
            this.refundBacking(player, preview.blueprint, backing);
            return "\u00a7c\u65bd\u5de5\u5e95\u5ea7\u653e\u7f6e\u5931\u8d25\u3002";
        }
        this.registerConstruction(player.m_284548_(), construction);
        this.previews.remove(player.m_20148_());
        FortificationManager.sendProgress(player, construction, true);
        player.m_213846_(Component.m_237113_("\u00a7a\u65bd\u5de5\u8303\u56f4\u5df2\u786e\u8ba4\uff0c\u6309\u4f4f\u5de5\u5175\u94f2\u5de6\u952e\u5f00\u59cb\u4fee\u5efa\u3002"));
        return null;
    }

    public void work(ServerPlayer player, BlockPos target, boolean build) {
        Construction construction;
        ServerLevel level;
        block5: {
            block4: {
                Level level2;
                if (player == null || target == null || player.m_5833_() || !player.m_6084_() || player.m_21205_().m_41720_() != Items.f_42384_ || !((level2 = player.m_9236_()) instanceof ServerLevel)) break block4;
                level = (ServerLevel)level2;
                if (!(player.m_146892_().m_82557_(Vec3.m_82512_(target)) > 49.0) && FortificationManager.isLookingAt(player, target)) break block5;
            }
            return;
        }
        UUID constructionId = this.positionIndex.get(FortificationManager.posKey(level, target));
        Construction construction2 = construction = constructionId == null ? null : this.constructions.get(constructionId);
        if (construction == null) {
            return;
        }
        this.applyWork(player, level, construction, build);
    }

    public void workEntity(ServerPlayer player, UUID target, boolean build) {
        Level level;
        if (player == null || target == null || player.m_5833_() || !player.m_6084_() || player.m_21205_().m_41720_() != Items.f_42384_ || !((level = player.m_9236_()) instanceof ServerLevel)) {
            return;
        }
        ServerLevel level2 = (ServerLevel)level;
        UUID constructionId = this.entityIndex.get(target);
        Construction construction = constructionId == null ? null : this.constructions.get(constructionId);
        Entity entity = level2.m_8791_(target);
        if (construction == null || entity == null || player.m_20280_(entity) > 49.0 || !player.m_142582_(entity)) {
            return;
        }
        this.applyWork(player, level2, construction, build);
    }

    private void applyWork(ServerPlayer player, ServerLevel level, Construction construction, boolean build) {
        long previous;
        long now = level.m_46467_();
        if (now - (previous = this.lastWorkTick.getOrDefault(player.m_20148_(), -4611686018427387904L).longValue()) < 5L) {
            return;
        }
        this.lastWorkTick.put(player.m_20148_(), now);
        if (construction.complete) {
            if (build) {
                construction.structuralValue = Math.min(construction.blueprint.definition.durability.structuralValue, construction.structuralValue + construction.blueprint.definition.durability.repairPerHit);
                FortificationManager.restoreProportional(level, construction);
            } else {
                construction.structuralValue = Math.max(0, construction.structuralValue - construction.blueprint.profile.removePerHit);
                if (construction.structuralValue == 0) {
                    this.destroy(level, construction, player, true, true, true);
                }
            }
        } else if (build) {
            int old = construction.progress;
            construction.progress = Math.min(construction.required(), old + construction.blueprint.profile.buildPerHit);
            FortificationManager.restoreProportional(level, construction);
            if (!construction.complete) {
                FortificationManager.updateFoundationStage(level, construction);
            }
            if (!construction.complete && construction.progress >= construction.required() && !this.complete(level, construction, player)) {
                construction.progress = construction.required();
                player.m_5661_(Component.m_237113_("\u00a7e\u6700\u7ec8\u7a7a\u95f4\u88ab\u5360\u7528\uff0c\u6e05\u7a7a\u540e\u518d\u6b21\u94f2\u51fb\u3002"), true);
            }
        } else {
            construction.progress = Math.max(0, construction.progress - construction.blueprint.profile.removePerHit);
            if (!construction.complete) {
                FortificationManager.updateFoundationStage(level, construction);
            }
            if (construction.progress == 0) {
                this.destroy(level, construction, player, true, true, true);
            }
        }
        if (this.constructions.containsKey(construction.id)) {
            FortificationManager.sendProgress(player, construction, build);
        } else {
            NetworkManager.NET.send(PacketDistributor.PLAYER.with(() -> player), (Object)new FortificationProgressPacket(construction.blueprint.displayName, 0, construction.required(), build));
        }
        if (construction.blueprint.definition.behaviorType == FortificationConfig.Behavior.RADIO && this.constructions.containsKey(construction.id)) {
            FobSupplyTracker.notifyConstructionProgressChanged(level, construction.anchor, construction.team);
        }
    }

    public void removeAt(ServerLevel level, BlockPos pos) {
        this.damageAt(level, pos, null);
    }

    public void damageNearby(ServerLevel level, Vec3 center, float radius, @Nullable Entity attacker) {
        if (level == null || center == null) {
            return;
        }
        double checkRadius = Math.max(0.0, (double)radius) + 1.5;
        double radiusSq = Math.max(0.0, (double)radius) * Math.max(0.0, (double)radius);
        this.damageExplosionParts(level, center, checkRadius, attacker, pos -> Vec3.m_82512_(pos).m_82557_(center) <= radiusSq);
    }

    public void damageExplosion(ServerLevel level, Vec3 center, float radius, Collection<BlockPos> affectedBlocks, @Nullable Entity attacker) {
        if (level == null || center == null || affectedBlocks == null || affectedBlocks.isEmpty()) {
            return;
        }
        HashSet<BlockPos> affected = new HashSet<BlockPos>();
        for (BlockPos pos : affectedBlocks) {
            if (pos == null) continue;
            affected.add(pos.m_7949_());
        }
        if (affected.isEmpty()) {
            return;
        }
        this.damageExplosionParts(level, center, Math.max(0.0, (double)radius) + 1.5, attacker, affected::contains);
    }

    private void damageExplosionParts(ServerLevel level, Vec3 center, double checkRadius, @Nullable Entity attacker, Predicate<BlockPos> hitPart) {
        String dimension = level.m_46472_().m_135782_().toString();
        AABB query = new AABB(center.f_82479_ - checkRadius, center.f_82480_ - checkRadius, center.f_82481_ - checkRadius, center.f_82479_ + checkRadius, center.f_82480_ + checkRadius, center.f_82481_ + checkRadius);
        for (UUID id : this.spatialIndex.query(dimension, query)) {
            Construction construction = this.constructions.get(id);
            if (construction == null) continue;
            List<BlockPos> parts = construction.complete ? construction.finalSlots.stream().filter(slot -> slot.touch == FortificationTemplateCompiler.Touch.BLOCK).map(WorldSlot::pos).toList() : construction.footprint;
            boolean changed = false;
            for (BlockPos pos : parts) {
                if (!hitPart.test(pos) || !construction.missing.add(pos.m_7949_())) continue;
                if (FortificationManager.isCompletedRadioCore(construction, pos)) {
                    this.destroy(level, construction, attacker, true);
                    break;
                }
                changed = true;
                this.damageConstruction(level, construction, pos, attacker, DamageKind.EXPLOSION);
                if (this.constructions.containsKey(construction.id)) continue;
                break;
            }
            if (!changed || construction.blueprint.definition.behaviorType != FortificationConfig.Behavior.RADIO || !this.constructions.containsKey(construction.id)) continue;
            FobSupplyTracker.notifyConstructionProgressChanged(level, construction.anchor, construction.team);
        }
    }

    public void damageAt(ServerLevel level, BlockPos pos, @Nullable Entity attacker) {
        this.damageAt(level, pos, attacker, DamageKind.DIRECT_BREAK);
    }

    public void damageAt(ServerLevel level, BlockPos pos, @Nullable Entity attacker, float damageRatio) {
        this.damageAt(level, pos, attacker, damageRatio <= 0.2f ? DamageKind.EXPLOSION : DamageKind.DIRECT_BREAK);
    }

    public void damageAt(ServerLevel level, BlockPos pos, @Nullable Entity attacker, DamageKind kind) {
        Construction construction;
        UUID id = this.positionIndex.get(FortificationManager.posKey(level, pos));
        Construction construction2 = construction = id == null ? null : this.constructions.get(id);
        if (construction == null) {
            return;
        }
        WorldSlot part = construction.finalSlots.stream().filter(slot -> slot.pos.equals(pos) && slot.touch == FortificationTemplateCompiler.Touch.BLOCK).findFirst().orElse(null);
        if (part == null || !construction.missing.add(pos.m_7949_())) {
            return;
        }
        this.damageConstruction(level, construction, pos, attacker, kind);
        if (construction.blueprint.definition.behaviorType == FortificationConfig.Behavior.RADIO && this.constructions.containsKey(construction.id)) {
            FobSupplyTracker.notifyConstructionProgressChanged(level, construction.anchor, construction.team);
        }
    }

    public void damageEntity(ServerLevel level, UUID entityId, @Nullable Entity attacker) {
        Construction construction;
        if (level == null || entityId == null) {
            return;
        }
        UUID constructionId = this.entityIndex.get(entityId);
        Construction construction2 = construction = constructionId == null ? null : this.constructions.get(constructionId);
        if (construction == null || !Boolean.TRUE.equals(this.damageableEntityIndex.get(entityId)) || construction.blueprint.kind == Kind.ENTITY || !construction.settledEntityParts.add(entityId)) {
            return;
        }
        this.damageConstruction(level, construction, null, attacker, DamageKind.PROJECTILE);
    }

    public void removeEntity(UUID entityId) {
        Construction construction;
        UUID id = this.entityIndex.get(entityId);
        Construction construction2 = construction = id == null ? null : this.constructions.get(id);
        if (construction == null) {
            return;
        }
        ServerLevel level = this.levelFor(construction);
        if (!Boolean.TRUE.equals(this.damageableEntityIndex.get(entityId))) {
            this.entityIndex.remove(entityId);
            construction.spawnedEntities.remove(entityId);
            return;
        }
        if (!construction.settledEntityParts.add(entityId)) {
            return;
        }
        if (level == null) {
            this.unregisterConstruction(construction);
        } else if (construction.blueprint.kind == Kind.ENTITY && !construction.fallbackMode) {
            construction.structuralValue = 0;
            this.destroy(level, construction, null, true);
        } else {
            this.damageConstruction(level, construction, null, null, DamageKind.DIRECT_BREAK);
        }
    }

    private static boolean isCompletedRadioCore(Construction construction, @Nullable BlockPos pos) {
        return pos != null && construction.complete && construction.blueprint.definition.behaviorType == FortificationConfig.Behavior.RADIO && pos.equals(construction.anchor);
    }

    private void damageConstruction(ServerLevel level, Construction construction, @Nullable BlockPos part, @Nullable Entity attacker, DamageKind kind) {
        int damage;
        int maximum = construction.complete ? construction.blueprint.definition.durability.structuralValue : construction.required();
        int parts = construction.complete ? FortificationManager.damageablePartCount(construction) : Math.max(1, construction.footprint.size());
        int baseDamage = Math.max(1, (int)Math.ceil((double)maximum / (double)parts));
        double reduction = construction.blueprint.definition.durability.damageReduction.forKind(kind);
        int n = damage = reduction >= 1.0 ? 0 : Math.max(1, (int)Math.ceil((double)baseDamage * (1.0 - reduction)));
        if (damage <= 0) {
            return;
        }
        if (construction.complete) {
            construction.structuralValue = Math.max(0, construction.structuralValue - damage);
            if (construction.structuralValue == 0) {
                this.destroy(level, construction, attacker, true);
            }
        } else {
            construction.progress = Math.max(0, construction.progress - damage);
            if (construction.progress == 0) {
                this.destroy(level, construction, attacker, true);
            } else {
                FortificationManager.updateFoundationStage(level, construction);
            }
        }
    }

    private static int damageablePartCount(Construction construction) {
        if (construction.blueprint.kind == Kind.ENTITY && !construction.fallbackMode) {
            return 1;
        }
        return construction.blueprint.template == null ? 1 : Math.max(1, construction.blueprint.template.damageablePartCount());
    }

    public boolean contains(ServerLevel level, BlockPos pos) {
        return this.positionIndex.containsKey(FortificationManager.posKey(level, pos));
    }

    public boolean containsEntity(UUID entityId) {
        return entityId != null && this.entityIndex.containsKey(entityId);
    }

    public boolean isFoundation(ServerLevel level, BlockPos pos) {
        UUID id = this.positionIndex.get(FortificationManager.posKey(level, pos));
        Construction c = id == null ? null : this.constructions.get(id);
        return c != null && !c.complete && c.footprint.contains(pos);
    }

    public boolean isAmmoCrateAt(ServerLevel level, BlockPos pos, String team) {
        PlacedFort fort = this.placed.get(FortificationManager.posKey(level, pos));
        if (fort != null) {
            return FortificationManager.behaviorOf(level, fort.fortId()) == FortificationConfig.Behavior.AMMO_CRATE && team != null && team.equals(fort.team());
        }
        BastionData radio = BastionManager.getInstance().findBastionByShulkerPos(pos);
        return radio != null && team != null && team.equals(radio.getTeam()) && radio.isAmmoCrateBuilt();
    }

    @Nullable
    public BastionData findRadioForAmmoCrate(ServerLevel level, BlockPos cratePos, String team) {
        PlacedFort fort = this.placed.get(FortificationManager.posKey(level, cratePos));
        if (fort != null && FortificationManager.behaviorOf(level, fort.fortId()) == FortificationConfig.Behavior.AMMO_CRATE && team != null && team.equals(fort.team())) {
            BastionData radio;
            BastionData bastionData = radio = fort.radioId() == null ? null : BastionManager.getInstance().getBastion(fort.radioId());
            if (radio != null && radio.isActive() && radio.isRadio()) {
                return radio;
            }
        }
        return null;
    }

    @Nullable
    public BastionData findVehicleServiceRadio(ServerLevel level, BlockPos vehiclePos, String team) {
        double radiusSq = Math.pow(FortificationConfig.vehicleService().stationRadius, 2.0);
        for (PlacedFort fort : this.placed.values()) {
            BastionData radio;
            if (FortificationManager.behaviorOf(level, fort.fortId()) != FortificationConfig.Behavior.VEHICLE_SUPPLY_STATION || !fort.dimension().equals(level.m_46472_().m_135782_().toString()) || !fort.team().equals(team) || fort.pos().m_123331_(vehiclePos) > radiusSq || (radio = fort.radioId() == null ? null : BastionManager.getInstance().getBastion(fort.radioId())) == null || !radio.isActive() || !radio.isRadio() || !team.equals(radio.getTeam()) || radio.getLevel() != level) continue;
            return radio;
        }
        return null;
    }

    @Nullable
    private static FortificationConfig.Behavior behaviorOf(ServerLevel level, String id) {
        FortificationConfig.FortificationDef def = FortificationConfig.get(level.m_46472_().m_135782_(), id);
        return def == null ? null : def.behaviorType;
    }

    @Nullable
    public String place(ServerPlayer player, String fortId) {
        return this.beginPreview(player, fortId);
    }

    public static boolean canUse(ServerPlayer player, FortificationConfig.FortificationDef def) {
        UUID uuid = player.m_20148_();
        for (String string : def.usableBy == null ? List.of() : def.usableBy) {
            String role = string.toLowerCase(Locale.ROOT);
            if ("commander".equals(role) && VoteManager.getInstance().isCommander(uuid)) {
                return true;
            }
            if ("squad_leader".equals(role) && SquadManager.getInstance().isSquadLeader(uuid)) {
                return true;
            }
            if (!"fireteam_leader".equals(role) || !SquadManager.getInstance().isFireteamLeader(uuid)) continue;
            return true;
        }
        return false;
    }

    public static boolean canOpenBuildMenu(ServerPlayer player) {
        return FortificationConfig.list().stream().anyMatch(def -> FortificationManager.canUse(player, def));
    }

    @Nullable
    public RadioConstructionProgress getRadioConstructionProgress(ServerLevel level, BlockPos pos, String team) {
        if (level == null || pos == null || team == null) {
            return null;
        }
        double radius = LogisticsConfig.get().radioBuildRadius;
        double radiusSq = radius * radius;
        String dimension = level.m_46472_().m_135782_().toString();
        Construction best = null;
        double bestDistance = Double.MAX_VALUE;
        for (Construction c : this.constructions.values()) {
            double distance;
            if (c.blueprint.definition.behaviorType != FortificationConfig.Behavior.RADIO || !team.equals(c.team) || !dimension.equals(c.dimension) || !((distance = c.anchor.m_123331_(pos)) <= radiusSq) || !(distance < bestDistance)) continue;
            best = c;
            bestDistance = distance;
        }
        return best == null ? null : new RadioConstructionProgress(best.progress, best.required());
    }

    private Blueprint createBlueprint(String id, String team) {
        FortificationConfig.FortificationDef def;
        ResourceLocation dimension = BattlefieldContext.getActiveDimensionKey().map(ResourceKey::m_135782_).orElse(null);
        FortificationConfig.FortificationDef fortificationDef = def = dimension == null ? FortificationConfig.get(id) : FortificationConfig.get(dimension, id);
        if (def == null) {
            return null;
        }
        FortificationTemplateCompiler.CompiledTemplate template = def.templateFor(team);
        ArrayList<Slot> slots = new ArrayList<Slot>();
        if (template != null) {
            for (FortificationTemplateCompiler.OrientedBlock block : template.oriented(Direction.NORTH).blocks()) {
                slots.add(new Slot(block.templateIndex(), block.relativePos(), block.state(), block.blockEntityNbt(), block.touch()));
            }
        }
        if (slots.isEmpty() && "structure".equals(def.placement.type)) {
            return null;
        }
        if (slots.isEmpty()) {
            slots.add(new Slot(-1, BlockPos.f_121853_, null, null, FortificationTemplateCompiler.Touch.EXPLICIT_AIR));
        }
        return new Blueprint("entity".equals(def.placement.type) ? Kind.ENTITY : Kind.STRUCTURE, def.id, def.displayName, new FortificationConfig.ConstructionProfile(def.construction.requiredProgress, def.construction.buildPerHit, def.construction.removePerHit), List.copyOf(slots), def, template);
    }

    @Nullable
    private String validateCommon(ServerPlayer player) {
        ServerLevel level;
        if (player == null || player.m_5833_() || !player.m_6084_()) {
            return "\u00a7c\u5f53\u524d\u72b6\u6001\u65e0\u6cd5\u5efa\u9020\u3002";
        }
        if (!BattlefieldWorldManager.getInstance().isStartupReady() || !FortificationConfig.isFrozenReady()) {
            return "\u00a7c\u6218\u573a\u542f\u52a8\u95e8\u7981\u672a\u5c31\u7eea\uff0c\u672c\u6b21\u4f1a\u8bdd\u65e0\u6cd5\u5efa\u9020\u5de5\u4e8b\u3002";
        }
        GamePhase phase = GameStateManager.getInstance().getCurrentPhase();
        if (phase != GamePhase.DEPLOYING && phase != GamePhase.BATTLE) {
            return "\u00a7c\u5f53\u524d\u9636\u6bb5\u65e0\u6cd5\u5efa\u9020\u5de5\u4e8b\u3002";
        }
        Level level2 = player.m_9236_();
        if (!(level2 instanceof ServerLevel) || !BattlefieldContext.isActiveBattlefield(level = (ServerLevel)level2)) {
            return "\u00a7c\u53ea\u80fd\u5728\u5f53\u524d\u6218\u573a\u5efa\u9020\u5de5\u4e8b\u3002";
        }
        return null;
    }

    @Nullable
    private String validateSelectionRole(ServerPlayer player, Blueprint blueprint) {
        return FortificationManager.canUse(player, blueprint.definition) ? null : "\u00a7c\u4f60\u6ca1\u6709\u6743\u9650\u5efa\u9020\u8be5\u5de5\u4e8b\u3002";
    }

    private PlacementBacking validateBacking(ServerPlayer player, PreviewSession preview, BlockPos anchor) {
        Blueprint blueprint = preview.blueprint;
        ServerLevel level = player.m_284548_();
        FortificationConfig.Behavior behavior = blueprint.definition.behaviorType;
        if (behavior == FortificationConfig.Behavior.RADIO) {
            GamePhase phase;
            LogisticsConfig.RadioPlacementSettings cfg = LogisticsConfig.get().getRadio();
            if (!cfg.allowsPhase((phase = GameStateManager.getInstance().getCurrentPhase()).name())) {
                return new PlacementBacking(null, null, "\u00a7c\u5f53\u524d\u9636\u6bb5\u4e0d\u80fd\u90e8\u7f72 Radio\u3002");
            }
            BastionManager manager = BastionManager.getInstance();
            String cooldown = manager.canBuildBastion(player.m_20148_(), manager.getEffectiveRadioCooldownSeconds());
            if (cooldown != null) {
                return new PlacementBacking(null, null, cooldown);
            }
            long pending = this.constructions.values().stream().filter(c -> c.blueprint.definition.behaviorType == FortificationConfig.Behavior.RADIO && c.team.equals(preview.team)).count();
            if ((long)manager.getActiveBastionCount(preview.team) + pending >= (long)manager.getBastionLimitPerTeam()) {
                return new PlacementBacking(null, null, "\u00a7c\u672c\u65b9 Radio \u6570\u91cf\u5df2\u8fbe\u5230\u4e0a\u9650\u3002");
            }
            if (manager.wouldRadioCoverageOverlap(level, anchor, preview.team) || this.pendingRadioOverlap(level, anchor, preview.team)) {
                return new PlacementBacking(null, null, "\u00a7cRadio \u4f5c\u7528\u8303\u56f4\u4e0d\u80fd\u4e0e\u5176\u4ed6 Radio \u91cd\u53e0\u3002");
            }
            if (cfg.teammateCount > 0 && FortificationManager.countNearbyTeammates(player, preview.team, anchor, cfg.teammateRadius) < cfg.teammateCount) {
                return new PlacementBacking(null, null, "\u00a7c\u90e8\u7f72\u70b9\u9644\u8fd1\u961f\u53cb\u6570\u91cf\u4e0d\u8db3\u3002");
            }
            return new PlacementBacking(null, null, null);
        }
        if (behavior == FortificationConfig.Behavior.HAB) {
            List<BastionData> radios = BastionManager.getInstance().findCoveringRadios(level, anchor, preview.team);
            if (radios.isEmpty()) {
                return new PlacementBacking(null, null, "\u00a7c\u5175\u7ad9\u5fc5\u987b\u4f4d\u4e8e\u5df1\u65b9 Radio \u8303\u56f4\u5185\u3002");
            }
            BastionData radio = radios.get(0);
            if (this.habCountForRadio(radio, preview.team) >= FortificationManager.maxHabsFor(player)) {
                return new PlacementBacking(radio, radio.getBastionId(), "\u00a7c\u8be5 Radio \u8303\u56f4\u5185\u5175\u7ad9\u5df2\u8fbe\u5230\u4e0a\u9650\u3002");
            }
            if (BastionManager.getInstance().sumConstructionInCoveringRadios(level, anchor, preview.team) < blueprint.definition.cost.construction) {
                return new PlacementBacking(radio, radio.getBastionId(), "\u00a7c\u8986\u76d6 Radio \u7684\u5efa\u6750\u4e0d\u8db3\u3002");
            }
            return new PlacementBacking(radio, radio.getBastionId(), null);
        }
        FortificationConfig.FortificationDef def = blueprint.definition;
        BastionData radio = null;
        if (def.requirements.requireRadioRange) {
            List<BastionData> radios = BastionManager.getInstance().findCoveringRadios(level, anchor, preview.team);
            if (radios.isEmpty()) {
                return new PlacementBacking(null, null, "\u00a7c\u5fc5\u987b\u5728\u5df1\u65b9 Radio \u8303\u56f4\u5185\u5efa\u9020\u3002");
            }
            radio = radios.get(0);
        }
        if (radio == null && (def.cost.construction > 0 || def.cost.ammunition > 0)) {
            return new PlacementBacking(null, null, "\u00a7c\u8be5\u5de5\u4e8b\u9700\u8981 Radio \u5e93\u5b58\u3002");
        }
        if (radio != null && (radio.getConstructionSupplies() < def.cost.construction || radio.getAmmunitionSupplies() < def.cost.ammunition)) {
            return new PlacementBacking(radio, radio.getBastionId(), "\u00a7c\u5efa\u9020\u8d44\u6e90\u4e0d\u8db3\u3002");
        }
        return new PlacementBacking(radio, radio == null ? null : radio.getBastionId(), null);
    }

    private boolean debitBacking(ServerPlayer player, Blueprint blueprint, PlacementBacking backing, BlockPos anchor) {
        if (blueprint.definition.behaviorType == FortificationConfig.Behavior.RADIO) {
            return true;
        }
        if (blueprint.definition.behaviorType == FortificationConfig.Behavior.HAB) {
            int cost = blueprint.definition.cost.construction;
            return BastionManager.getInstance().tryDebitConstructionFromCoveringRadios(player.m_284548_(), anchor, Espetro.getPlayerTeam(player), cost);
        }
        return FortificationManager.debit(backing.radio, blueprint.definition.cost.construction, blueprint.definition.cost.ammunition);
    }

    private void refundBacking(ServerPlayer player, Blueprint blueprint, PlacementBacking backing) {
        if (blueprint.definition.behaviorType == FortificationConfig.Behavior.HAB && backing.radio != null) {
            backing.radio.addConstructionSupplies(blueprint.definition.cost.construction, LogisticsConfig.get().maxConstruction);
            FobSupplyTracker.notifySupplyChanged(backing.radio);
        } else if (blueprint.definition.behaviorType != FortificationConfig.Behavior.RADIO) {
            FortificationManager.refund(backing.radio, blueprint.definition.cost.construction, blueprint.definition.cost.ammunition);
        }
    }

    private boolean complete(ServerLevel level, Construction c, @Nullable Entity actor) {
        boolean success;
        if (!FortificationManager.completionSpaceAvailable(level, c)) {
            return false;
        }
        FortificationManager.removeFoundations(level, c);
        boolean bl = success = c.blueprint.kind == Kind.ENTITY ? this.completeEntity(level, c) : this.placeFinalBlocks(level, c);
        if (!success) {
            FortificationManager.placeFoundations(level, c, 6);
            return false;
        }
        FortificationConfig.Behavior behavior = c.blueprint.definition.behaviorType;
        if (behavior == FortificationConfig.Behavior.RADIO) {
            UUID uUID;
            String name = FortificationManager.nextBastionName(c.team, true);
            BastionData radio = BastionManager.getInstance().createRadio(level, c.anchor, c.team, name);
            if (radio == null) {
                this.clearFinalBlocks(level, c);
                FortificationManager.placeFoundations(level, c, 6);
                return false;
            }
            c.bastionId = radio.getBastionId();
            this.bastionIndex.put(c.bastionId, c.id);
            BastionManager bastionManager = BastionManager.getInstance();
            if (actor instanceof ServerPlayer) {
                ServerPlayer p = (ServerPlayer)actor;
                uUID = p.m_20148_();
            } else {
                uUID = UUID.randomUUID();
            }
            bastionManager.setBastionCooldown(uUID);
            Espetro.broadcastToTeam(c.team, "\u00a76[Radio] \u00a7a" + name + " \u00a7a\u5df2\u5efa\u6210\u3002");
        } else if (behavior == FortificationConfig.Behavior.HAB) {
            String name = FortificationManager.nextBastionName(c.team, false);
            BastionData hab = BastionManager.getInstance().createHab(level, c.anchor, c.team, name);
            if (hab == null) {
                this.clearFinalBlocks(level, c);
                FortificationManager.placeFoundations(level, c, 6);
                return false;
            }
            c.bastionId = hab.getBastionId();
            this.bastionIndex.put(c.bastionId, c.id);
            Espetro.broadcastToTeam(c.team, "\u00a76[\u5175\u7ad9] \u00a7a" + name + " \u00a7a\u5df2\u5efa\u6210\u3002");
        } else {
            this.registerCompletedConfig(c);
        }
        c.complete = true;
        c.structuralValue = c.blueprint.definition.durability.structuralValue;
        c.missing.clear();
        return true;
    }

    private void registerCompletedConfig(Construction c) {
        BastionData radio;
        FortificationConfig.FortificationDef def = c.blueprint.definition;
        PlacedFort fort = new PlacedFort(def.id, c.team, c.radioId, c.dimension, c.anchor, c.entityId, c.entityId == null ? c.mapId : c.entityId);
        this.placed.put(FortificationManager.posKey(c.dimension, c.anchor), fort);
        if (c.entityId != null) {
            this.entityIndex.put(c.entityId, c.id);
        }
        BastionData bastionData = radio = c.radioId == null ? null : BastionManager.getInstance().getBastion(c.radioId);
        if (def.behaviorType == FortificationConfig.Behavior.AMMO_CRATE && radio != null) {
            radio.setShulkerPos(c.anchor);
            radio.setAmmoCrateBuilt(true);
        }
        if (def.behaviorType == FortificationConfig.Behavior.VEHICLE_SUPPLY_STATION) {
            VehicleManager.getInstance().registerMappedSupplyStation(fort.mapId(), def.displayName, c.team, c.dimension, c.anchor);
        }
        FobSupplyTracker.notifySupplyChanged(radio);
    }

    public void onBastionDestroyed(UUID bastionId, @Nullable ServerLevel knownLevel, @Nullable Entity attacker) {
        ServerLevel level;
        Construction construction;
        UUID constructionId = this.bastionIndex.remove(bastionId);
        Construction construction2 = construction = constructionId == null ? null : this.constructions.get(constructionId);
        if (construction == null) {
            return;
        }
        ServerLevel serverLevel = level = knownLevel != null ? knownLevel : this.levelFor(construction);
        if (level == null) {
            this.unregisterConstruction(construction);
        } else {
            this.destroy(level, construction, attacker, true, false);
        }
    }

    private void destroy(ServerLevel level, Construction c, @Nullable Entity actor, boolean removeWorld) {
        this.destroy(level, c, actor, removeWorld, true, false);
    }

    private void destroy(ServerLevel level, Construction c, @Nullable Entity actor, boolean removeWorld, boolean destroyBastionRecord) {
        this.destroy(level, c, actor, removeWorld, destroyBastionRecord, false);
    }

    private void destroy(ServerLevel level, Construction c, @Nullable Entity actor, boolean removeWorld, boolean destroyBastionRecord, boolean shovelDismantle) {
        PlacedFort fort;
        BastionData bastion;
        this.unregisterConstruction(c);
        if (removeWorld) {
            Entity entity;
            for (BlockPos pos : c.footprint) {
                if (!level.m_46805_(pos) || !level.m_8055_(pos).m_60713_(BastionItems.ON_BUILDING_BLOCK)) continue;
                level.m_7731_(pos, Blocks.f_50016_.m_49966_(), 3);
            }
            if (c.complete) {
                this.clearFinalBlocks(level, c);
            }
            if (c.entityId != null && (entity = level.m_8791_(c.entityId)) != null) {
                entity.m_146870_();
            }
        }
        if (destroyBastionRecord && c.bastionId != null && (bastion = BastionManager.getInstance().getBastion(c.bastionId)) != null) {
            ServerPlayer player;
            boolean friendlyShovel;
            boolean radio = c.blueprint.definition.behaviorType == FortificationConfig.Behavior.RADIO;
            boolean bl = friendlyShovel = shovelDismantle && actor instanceof ServerPlayer && c.team.equals(FortificationManager.normalizeTeam(Espetro.getPlayerTeam(player = (ServerPlayer)actor)));
            if (radio) {
                BastionManager.getInstance().destroyBastionWithManpower(bastion, actor, RadioLossPolicy.deductManpower(true, friendlyShovel, false));
            } else {
                BastionManager.getInstance().destroyBastion(bastion, actor);
            }
        }
        if ((fort = this.placed.remove(FortificationManager.posKey(c.dimension, c.anchor))) != null) {
            BastionData radio;
            VehicleManager.getInstance().unregisterMappedSupplyStation(fort.mapId());
            FortificationConfig.FortificationDef def = FortificationConfig.get(fort.fortId());
            if (def != null && def.behaviorType == FortificationConfig.Behavior.AMMO_CRATE && fort.radioId() != null && (radio = BastionManager.getInstance().getBastion(fort.radioId())) != null && c.anchor.equals(radio.getShulkerPos())) {
                radio.setAmmoCrateBuilt(false);
                radio.setShulkerPos(null);
                FobSupplyTracker.notifySupplyChanged(radio);
            }
        }
    }

    private void registerConstruction(ServerLevel level, Construction c) {
        this.constructions.put(c.id, c);
        for (WorldSlot slot : c.finalSlots) {
            this.positionIndex.put(FortificationManager.posKey(level, slot.pos), c.id);
        }
        AABB bounds = FortificationManager.boundsOf(c.finalSlots, c.anchor);
        this.spatialIndex.put(c.id, c.dimension, bounds);
    }

    private void unregisterConstruction(Construction c) {
        this.constructions.remove(c.id);
        for (WorldSlot slot : c.finalSlots) {
            this.positionIndex.remove(FortificationManager.posKey(c.dimension, slot.pos), c.id);
        }
        for (UUID entityId : c.spawnedEntities) {
            this.entityIndex.remove(entityId);
            this.damageableEntityIndex.remove(entityId);
        }
        if (c.entityId != null) {
            this.entityIndex.remove(c.entityId);
            this.damageableEntityIndex.remove(c.entityId);
        }
        if (c.bastionId != null) {
            this.bastionIndex.remove(c.bastionId, c.id);
        }
        this.spatialIndex.remove(c.id);
    }

    private static List<WorldSlot> transform(List<Slot> slots, BlockPos anchor, Direction facing) {
        ArrayList<WorldSlot> result = new ArrayList<WorldSlot>(slots.size());
        for (Slot slot : slots) {
            BlockPos relative = FortificationTransform.rotate(slot.offset, facing);
            BlockState state = slot.state == null ? null : slot.state.m_60717_(FortificationTransform.rotation(facing));
            result.add(new WorldSlot(slot.templateIndex, anchor.m_121955_(relative), state, slot.blockEntityNbt == null ? null : slot.blockEntityNbt.m_6426_(), slot.touch));
        }
        return result;
    }

    private static List<BlockPos> footprint(List<WorldSlot> slots) {
        int minY = slots.stream().filter(slot -> slot.touch == FortificationTemplateCompiler.Touch.BLOCK).mapToInt(slot -> slot.pos.m_123342_()).min().orElse(slots.stream().mapToInt(slot -> slot.pos.m_123342_()).min().orElse(0));
        LinkedHashSet<BlockPos> result = new LinkedHashSet<BlockPos>();
        for (WorldSlot slot2 : slots) {
            if (slot2.pos.m_123342_() != minY || slot2.touch != FortificationTemplateCompiler.Touch.BLOCK && !result.isEmpty()) continue;
            result.add(slot2.pos.m_7949_());
        }
        return List.copyOf(result);
    }

    private static boolean spaceIsClear(ServerLevel level, List<WorldSlot> slots, ServerPlayer placer) {
        for (WorldSlot slot : slots) {
            BlockState state = level.m_8055_(slot.pos);
            if (!FortificationManager.isReplaceable(state)) {
                return false;
            }
            if (level.m_6249_(null, new AABB(slot.pos), entity -> entity != placer && entity instanceof LivingEntity && entity.m_6084_()).isEmpty()) continue;
            return false;
        }
        return true;
    }

    private static boolean completionSpaceAvailable(ServerLevel level, Construction c) {
        for (WorldSlot slot : c.finalSlots) {
            boolean foundation;
            BlockState state = level.m_8055_(slot.pos);
            boolean bl = foundation = c.footprint.contains(slot.pos) && state.m_60713_(BastionItems.ON_BUILDING_BLOCK);
            if (!foundation && !FortificationManager.isReplaceable(state)) {
                return false;
            }
            if (level.m_6249_(null, new AABB(slot.pos), entity -> entity instanceof LivingEntity && entity.m_6084_()).isEmpty()) continue;
            return false;
        }
        return true;
    }

    private static boolean isReplaceable(BlockState state) {
        return state.m_60795_() || state.m_60713_(Blocks.f_50125_) || state.m_247087_();
    }

    private static AABB boundsOf(List<WorldSlot> slots, BlockPos fallback) {
        AABB box = null;
        for (WorldSlot slot : slots) {
            AABB cell = new AABB(slot.pos);
            box = box == null ? cell : box.m_82367_(cell);
        }
        return box == null ? new AABB(fallback) : box;
    }

    private static boolean placeFoundations(ServerLevel level, Construction c, int stage) {
        if (BastionItems.ON_BUILDING_BLOCK == null) {
            return false;
        }
        ArrayList<BlockSnapshot> snapshots = new ArrayList<BlockSnapshot>();
        BlockState state = (BlockState)BastionItems.ON_BUILDING_BLOCK.m_49966_().m_61124_(OnBuildingBlock.STAGE, Math.max(0, Math.min(6, stage)));
        for (BlockPos pos : c.footprint) {
            BlockState old = level.m_8055_(pos);
            if (!FortificationManager.isReplaceable(old)) {
                FortificationManager.restore(level, snapshots);
                return false;
            }
            snapshots.add(FortificationManager.snapshot(level, pos));
            if (level.m_7731_(pos, state, 3)) continue;
            FortificationManager.restore(level, snapshots);
            return false;
        }
        return true;
    }

    private static void removeFoundations(ServerLevel level, Construction c) {
        for (BlockPos pos : c.footprint) {
            if (!level.m_8055_(pos).m_60713_(BastionItems.ON_BUILDING_BLOCK)) continue;
            level.m_7731_(pos, Blocks.f_50016_.m_49966_(), 3);
        }
    }

    private static void updateFoundationStage(ServerLevel level, Construction c) {
        int stage = FortificationProgressPolicy.stage(c.progress, c.required());
        for (BlockPos pos : c.footprint) {
            BlockState state = level.m_8055_(pos);
            if (!state.m_60713_(BastionItems.ON_BUILDING_BLOCK) || state.m_61143_(OnBuildingBlock.STAGE) == stage) continue;
            level.m_7731_(pos, (BlockState)state.m_61124_(OnBuildingBlock.STAGE, stage), 2);
        }
    }

    private boolean placeFinalBlocks(ServerLevel level, Construction c) {
        ArrayList<BlockSnapshot> snapshots = new ArrayList<BlockSnapshot>();
        ArrayList<UUID> spawned = new ArrayList<UUID>();
        try {
            for (WorldSlot slot : c.finalSlots) {
                BlockState state;
                if (slot.touch == FortificationTemplateCompiler.Touch.IGNORE) continue;
                snapshots.add(FortificationManager.snapshot(level, slot.pos));
                BlockState blockState = state = slot.touch == FortificationTemplateCompiler.Touch.EXPLICIT_AIR ? Blocks.f_50016_.m_49966_() : slot.state;
                if (state == null || !level.m_7731_(slot.pos, state, 3)) {
                    throw new IllegalStateException("setBlock \u8fd4\u56de false: " + slot.pos);
                }
                if (slot.blockEntityNbt == null) continue;
                BlockEntity target = level.m_7702_(slot.pos);
                if (target == null) {
                    throw new IllegalStateException("\u65b9\u5757\u5b9e\u4f53\u672a\u521b\u5efa: " + slot.pos);
                }
                CompoundTag clean = slot.blockEntityNbt.m_6426_();
                clean.m_128405_("x", slot.pos.m_123341_());
                clean.m_128405_("y", slot.pos.m_123342_());
                clean.m_128405_("z", slot.pos.m_123343_());
                target.m_142466_(clean);
                target.m_6596_();
            }
            if (c.blueprint.template != null) {
                for (FortificationTemplateCompiler.OrientedEntity info : c.blueprint.template.oriented(c.facing).entities()) {
                    CompoundTag tag = info.visualNbt().m_6426_();
                    Entity entity = EntityType.m_20645_(tag, level, loaded -> {
                        Vec3 relative = info.relativePosition();
                        loaded.m_6034_((double)c.anchor.m_123341_() + relative.f_82479_, (double)c.anchor.m_123342_() + relative.f_82480_, (double)c.anchor.m_123343_() + relative.f_82481_);
                        loaded.m_146922_(c.facing.m_122435_());
                        return loaded;
                    });
                    if (entity == null || !level.m_7967_(entity)) {
                        throw new IllegalStateException("\u7ed3\u6784\u5b9e\u4f53\u751f\u6210\u5931\u8d25: " + info.type());
                    }
                    spawned.add(entity.m_20148_());
                    c.spawnedEntities.add(entity.m_20148_());
                    this.entityIndex.put(entity.m_20148_(), c.id);
                    this.damageableEntityIndex.put(entity.m_20148_(), info.damageable());
                }
            }
            return true;
        }
        catch (Exception e) {
            Espetro.LOGGER.error("\u5de5\u4e8b\u7ed3\u6784\u4e8b\u52a1\u653e\u7f6e\u5931\u8d25 {}\uff0c\u6b63\u5728\u56de\u6eda", (Object)c.blueprint.id, (Object)e);
            for (UUID id : spawned) {
                Entity entity = level.m_8791_(id);
                if (entity != null) {
                    entity.m_146870_();
                }
                c.spawnedEntities.remove(id);
                this.entityIndex.remove(id);
                this.damageableEntityIndex.remove(id);
            }
            FortificationManager.restore(level, snapshots);
            return false;
        }
    }

    private void clearFinalBlocks(ServerLevel level, Construction c) {
        for (WorldSlot slot : c.finalSlots) {
            if (slot.touch != FortificationTemplateCompiler.Touch.BLOCK || c.missing.contains(slot.pos) || !level.m_46805_(slot.pos) || slot.state == null || !level.m_8055_(slot.pos).equals(slot.state)) continue;
            level.m_7731_(slot.pos, Blocks.f_50016_.m_49966_(), 3);
        }
        for (UUID id : new ArrayList<UUID>(c.spawnedEntities)) {
            Entity entity = level.m_8791_(id);
            if (entity != null) {
                entity.m_146870_();
            }
            this.entityIndex.remove(id);
            this.damageableEntityIndex.remove(id);
        }
        c.spawnedEntities.clear();
        c.entityId = null;
    }

    private boolean completeEntity(ServerLevel level, Construction c) {
        Entity entity;
        CompoundTag tag;
        FortificationConfig.FortificationDef def = c.blueprint.definition;
        ResourceLocation id = ResourceLocation.m_135820_(def.placement.entityId);
        EntityType type = id == null ? null : (EntityType)BuiltInRegistries.f_256780_.m_6612_(id).orElse(null);
        CompoundTag compoundTag = tag = def.placement.sanitizedEntityNbt == null ? new CompoundTag() : def.placement.sanitizedEntityNbt.m_6426_();
        if (id != null) {
            tag.m_128359_("id", id.toString());
        }
        Entity entity2 = entity = type == null ? null : EntityType.m_20645_(tag, level, loaded -> loaded);
        if (entity != null) {
            double[] offset = def.placement.spawnOffset;
            double ox = offset != null && offset.length == 3 ? offset[0] : 0.5;
            double oy = offset != null && offset.length == 3 ? offset[1] : 0.0;
            double oz = offset != null && offset.length == 3 ? offset[2] : 0.5;
            entity.m_6034_((double)c.anchor.m_123341_() + ox, (double)c.anchor.m_123342_() + oy, (double)c.anchor.m_123343_() + oz);
            entity.m_146922_(c.facing.m_122435_());
            entity.m_6593_(Component.m_237113_(def.displayName));
            if (def.behaviorType == FortificationConfig.Behavior.VEHICLE_SUPPLY_STATION) {
                VehicleManager.applySupplyStationMapTags(entity, c.team, "fort_" + entity.m_20148_());
            } else {
                entity.m_20049_("espetro_fortification_" + def.id);
                entity.m_20049_("espetro_team_" + c.team);
            }
            if (level.m_7967_(entity)) {
                c.entityId = entity.m_20148_();
                c.spawnedEntities.add(entity.m_20148_());
                this.entityIndex.put(entity.m_20148_(), c.id);
                this.damageableEntityIndex.put(entity.m_20148_(), true);
                return true;
            }
        }
        c.fallbackMode = true;
        return c.blueprint.template != null && this.placeFinalBlocks(level, c);
    }

    private static BlockSnapshot snapshot(ServerLevel level, BlockPos pos) {
        BlockEntity blockEntity = level.m_7702_(pos);
        return new BlockSnapshot(pos.m_7949_(), level.m_8055_(pos), blockEntity == null ? null : blockEntity.m_187480_());
    }

    private static void restore(ServerLevel level, List<BlockSnapshot> snapshots) {
        for (int i = snapshots.size() - 1; i >= 0; --i) {
            BlockEntity restored;
            BlockSnapshot snapshot = snapshots.get(i);
            level.m_7731_(snapshot.pos, snapshot.state, 3);
            if (snapshot.blockEntityNbt == null || (restored = level.m_7702_(snapshot.pos)) == null) continue;
            restored.m_142466_(snapshot.blockEntityNbt.m_6426_());
            restored.m_6596_();
        }
    }

    private static void restoreProportional(ServerLevel level, Construction c) {
        int present;
        int total;
        if (c.missing.isEmpty()) {
            return;
        }
        int desired = FortificationProgressPolicy.desiredPresentParts(c.complete ? c.structuralValue : c.progress, c.complete ? c.blueprint.definition.durability.structuralValue : c.required(), total = c.complete ? (int)c.finalSlots.stream().filter(slot -> slot.touch == FortificationTemplateCompiler.Touch.BLOCK).count() : c.footprint.size());
        if (desired <= (present = total - c.missing.size())) {
            return;
        }
        if (c.complete) {
            for (WorldSlot slot2 : c.finalSlots) {
                if (present < desired) {
                    BlockEntity blockEntity;
                    BlockState current = level.m_8055_(slot2.pos);
                    if (slot2.touch != FortificationTemplateCompiler.Touch.BLOCK || !c.missing.contains(slot2.pos) || slot2.state == null || !FortificationManager.isReplaceable(current) || !level.m_7731_(slot2.pos, slot2.state, 3)) continue;
                    if (slot2.blockEntityNbt != null && (blockEntity = level.m_7702_(slot2.pos)) != null) {
                        CompoundTag clean = slot2.blockEntityNbt.m_6426_();
                        clean.m_128405_("x", slot2.pos.m_123341_());
                        clean.m_128405_("y", slot2.pos.m_123342_());
                        clean.m_128405_("z", slot2.pos.m_123343_());
                        blockEntity.m_142466_(clean);
                        blockEntity.m_6596_();
                    }
                    c.missing.remove(slot2.pos);
                    ++present;
                    continue;
                }
                break;
            }
        } else {
            int stage = FortificationProgressPolicy.stage(c.progress, c.required());
            BlockState marker = (BlockState)BastionItems.ON_BUILDING_BLOCK.m_49966_().m_61124_(OnBuildingBlock.STAGE, stage);
            for (BlockPos pos : c.footprint) {
                if (present < desired) {
                    BlockState current = level.m_8055_(pos);
                    if (!c.missing.contains(pos) || !current.m_60795_() && !current.m_60713_(Blocks.f_50125_) || !level.m_7731_(pos, marker, 3)) continue;
                    c.missing.remove(pos);
                    ++present;
                    continue;
                }
                break;
            }
        }
    }

    private static boolean debit(@Nullable BastionData radio, int construction, int ammunition) {
        if (radio == null) {
            return construction == 0 && ammunition == 0;
        }
        if (construction > 0 && !radio.consumeConstructionSupplies(construction)) {
            return false;
        }
        if (ammunition > 0 && !radio.consumeAmmunitionSupplies(ammunition)) {
            if (construction > 0) {
                radio.addConstructionSupplies(construction, LogisticsConfig.get().maxConstruction);
            }
            return false;
        }
        FobSupplyTracker.notifySupplyChanged(radio);
        return true;
    }

    private static void refund(@Nullable BastionData radio, int construction, int ammunition) {
        if (radio == null) {
            return;
        }
        if (construction > 0) {
            radio.addConstructionSupplies(construction, LogisticsConfig.get().maxConstruction);
        }
        if (ammunition > 0) {
            radio.addAmmunitionSupplies(ammunition, LogisticsConfig.get().maxAmmunition);
        }
        FobSupplyTracker.notifySupplyChanged(radio);
    }

    @Nullable
    private static BlockState resolveBlock(@Nullable String rawId) {
        ResourceLocation id = ResourceLocation.m_135820_(rawId);
        if (id == null || !BuiltInRegistries.f_256975_.m_7804_(id)) {
            return null;
        }
        Block block = BuiltInRegistries.f_256975_.m_7745_(id);
        return block == Blocks.f_50016_ ? null : block.m_49966_();
    }

    @Nullable
    private static BlockPos raycastPlacePos(ServerPlayer player) {
        Vec3 eye = player.m_20299_(1.0f);
        Vec3 end = eye.m_82549_(player.m_20154_().m_82490_(6.0));
        BlockHitResult hit = player.m_9236_().m_45547_(new ClipContext(eye, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
        if (hit.m_6662_() == HitResult.Type.MISS) {
            return null;
        }
        return player.m_9236_().m_8055_(hit.m_82425_()).m_60713_(Blocks.f_50125_) ? hit.m_82425_() : hit.m_82425_().m_121945_(hit.m_82434_());
    }

    private static boolean isLookingAt(ServerPlayer player, BlockPos target) {
        Vec3 eye = player.m_20299_(1.0f);
        Vec3 end = eye.m_82549_(player.m_20154_().m_82490_(7.0));
        BlockHitResult hit = player.m_9236_().m_45547_(new ClipContext(eye, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
        return hit.m_6662_() == HitResult.Type.BLOCK && hit.m_82425_().equals(target);
    }

    private boolean pendingRadioOverlap(ServerLevel level, BlockPos anchor, String team) {
        double min = BastionManager.getInstance().getMinimumRadioCenterDistance();
        String dimension = level.m_46472_().m_135782_().toString();
        return this.constructions.values().stream().anyMatch(c -> c.blueprint.definition.behaviorType == FortificationConfig.Behavior.RADIO && c.dimension.equals(dimension) && RadioCoveragePolicy.blocksPlacement(c.team, team, c.anchor.m_123331_(anchor), min));
    }

    private int habCountForRadio(BastionData radio, String team) {
        double radiusSq = Math.pow(LogisticsConfig.get().radioBuildRadius, 2.0);
        int count = 0;
        for (BastionData b : BastionManager.getInstance().getAllBastions()) {
            if (!b.isActive() || !b.isHab() || !team.equals(b.getTeam()) || !(b.getPosition().m_123331_(radio.getPosition()) <= radiusSq)) continue;
            ++count;
        }
        for (Construction c : this.constructions.values()) {
            if (c.blueprint.definition.behaviorType != FortificationConfig.Behavior.HAB || !radio.getBastionId().equals(c.radioId)) continue;
            ++count;
        }
        return count;
    }

    private static int maxHabsFor(ServerPlayer player) {
        String factionId = ClassCountManager.getInstance().getPlayerFaction(player.m_20148_());
        FactionDataLoader.FactionData faction = factionId == null ? null : FactionDataProvider.getOrCreateLoader().getFaction(factionId);
        return faction == null ? 2 : Math.max(0, faction.maxHabsPerRadio);
    }

    private static int countNearbyTeammates(ServerPlayer player, String team, BlockPos center, double radius) {
        int count = 0;
        double radiusSq = radius * radius;
        for (ServerPlayer other : player.m_284548_().m_6907_()) {
            if (other == player || !other.m_6084_() || other.m_5833_() || !team.equals(Espetro.getPlayerTeam(other)) || !(other.m_20183_().m_123331_(center) <= radiusSq)) continue;
            ++count;
        }
        return count;
    }

    private static String nextBastionName(String team, boolean radio) {
        int number = 1;
        for (BastionData data : BastionManager.getInstance().getAllBastions()) {
            if (!data.isActive() || !team.equals(data.getTeam()) || !(radio ? data.isRadio() : data.isHab())) continue;
            ++number;
        }
        return ("ATTACK".equals(team) ? "\u8fdb\u653b" : "\u9632\u5b88") + (radio ? "Radio-" : "\u5175\u7ad9-") + number;
    }

    private static void sendProgress(ServerPlayer player, Construction c, boolean building) {
        int value = c.complete ? c.structuralValue : c.progress;
        int maximum = c.complete ? c.blueprint.definition.durability.structuralValue : c.required();
        NetworkManager.NET.send(PacketDistributor.PLAYER.with(() -> player), (Object)new FortificationProgressPacket(c.blueprint.displayName, value, maximum, building));
    }

    @Nullable
    private ServerLevel levelFor(Construction construction) {
        if (Espetro.getServer() == null) {
            return null;
        }
        for (ServerLevel level : Espetro.getServer().m_129785_()) {
            if (!level.m_46472_().m_135782_().toString().equals(construction.dimension)) continue;
            return level;
        }
        return null;
    }

    private static UUID stableMapId(String dimension, BlockPos pos) {
        return UUID.nameUUIDFromBytes(("espetro-fort|" + dimension + "|" + pos.m_121878_()).getBytes(StandardCharsets.UTF_8));
    }

    private static String posKey(ServerLevel level, BlockPos pos) {
        return FortificationManager.posKey(level.m_46472_().m_135782_().toString(), pos);
    }

    private static String posKey(String dimension, BlockPos pos) {
        return dimension + "|" + pos.m_123341_() + "," + pos.m_123342_() + "," + pos.m_123343_();
    }

    @Nullable
    private static String normalizeTeam(@Nullable String team) {
        if (team == null) {
            return null;
        }
        String normalized = team.trim().toUpperCase(Locale.ROOT);
        return "ATTACK".equals(normalized) || "DEFEND".equals(normalized) ? normalized : null;
    }

    public record PlacedFort(String fortId, String team, @Nullable UUID radioId, String dimension, BlockPos pos, @Nullable UUID entityId, UUID mapId) {
    }

    private record Blueprint(Kind kind, String id, String displayName, FortificationConfig.ConstructionProfile profile, List<Slot> slots, FortificationConfig.FortificationDef definition, @Nullable FortificationTemplateCompiler.CompiledTemplate template) {
    }

    private record PreviewSession(UUID token, String dimension, Blueprint blueprint, String team, long expiresAt) {
    }

    private record WorldSlot(int templateIndex, BlockPos pos, @Nullable BlockState state, @Nullable CompoundTag blockEntityNbt, FortificationTemplateCompiler.Touch touch) {
    }

    private record PlacementBacking(@Nullable BastionData radio, @Nullable UUID radioId, @Nullable String error) {
    }

    private static final class Construction {
        final UUID id = UUID.randomUUID();
        final Blueprint blueprint;
        final String team;
        final String dimension;
        final BlockPos anchor;
        final Direction facing;
        final List<WorldSlot> finalSlots;
        final List<BlockPos> footprint;
        final Set<BlockPos> missing = new HashSet<BlockPos>();
        final Set<UUID> spawnedEntities = new HashSet<UUID>();
        final Set<UUID> settledEntityParts = new HashSet<UUID>();
        final UUID radioId;
        final UUID mapId;
        int progress;
        int structuralValue;
        boolean complete;
        boolean fallbackMode;
        UUID entityId;
        UUID bastionId;

        Construction(Blueprint blueprint, String team, String dimension, BlockPos anchor, Direction facing, List<WorldSlot> finalSlots, List<BlockPos> footprint, @Nullable UUID radioId) {
            this.blueprint = blueprint;
            this.team = team;
            this.dimension = dimension;
            this.anchor = anchor.m_7949_();
            this.facing = facing;
            this.finalSlots = List.copyOf(finalSlots);
            this.footprint = List.copyOf(footprint);
            this.radioId = radioId;
            this.mapId = FortificationManager.stableMapId(dimension, anchor);
        }

        int required() {
            return this.blueprint.profile.requiredProgress;
        }
    }

    public static enum DamageKind {
        EXPLOSION,
        PROJECTILE,
        DIRECT_BREAK;

    }

    private static enum Kind {
        STRUCTURE,
        ENTITY;

    }

    public record RadioConstructionProgress(int progress, int required) {
    }

    private record Slot(int templateIndex, BlockPos offset, @Nullable BlockState state, @Nullable CompoundTag blockEntityNbt, FortificationTemplateCompiler.Touch touch) {
    }

    private record BlockSnapshot(BlockPos pos, BlockState state, @Nullable CompoundTag blockEntityNbt) {
    }
}

