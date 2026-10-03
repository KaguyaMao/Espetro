/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  javax.annotation.Nullable
 */
package org.espetro.logistics;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.espetro.Espetro;
import org.espetro.logistics.DeploySupplyStationPlacer;
import org.espetro.logistics.LogisticsConfig;
import org.espetro.logistics.SupplyType;

public final class SupplyManager {
    public static final String NBT_SUPPLY_TYPE = "EspetroSupplyType";
    public static final String NBT_SUPPLY_ID = "EspetroSupplyId";
    public static final String NBT_SUPPLY_POINTS = "EspetroSupplyPoints";
    public static final String NBT_SOURCE_ID = "EspetroSupplySourceId";
    private static final SupplyManager INSTANCE = new SupplyManager();
    private final Map<UUID, Long> pickupCooldowns = new HashMap<UUID, Long>();

    private SupplyManager() {
    }

    public static SupplyManager getInstance() {
        return INSTANCE;
    }

    public void reset() {
        this.pickupCooldowns.clear();
        try {
            MinecraftServer server = Espetro.getServer();
            if (server != null) {
                for (ServerLevel level : server.m_129785_()) {
                    DeploySupplyStationPlacer.clear(level);
                }
            }
        }
        catch (Throwable t) {
            Espetro.LOGGER.warn("\u6e05\u7406\u90e8\u7f72\u70b9\u8865\u7ed9\u7ad9\u5931\u8d25: {}", (Object)t.toString());
        }
    }

    public boolean handleSourceInteraction(ServerPlayer player, ServerLevel level, BlockPos pos) {
        try {
            return this.handleSourceInteractionInner(player, level, pos);
        }
        catch (Throwable t) {
            Espetro.LOGGER.error("handleSourceInteraction \u5931\u8d25 at {}", (Object)pos, (Object)t);
            if (player != null) {
                player.m_213846_(Component.m_237113_("\u00a7c\u8865\u7ed9\u7ad9\u6682\u65f6\u4e0d\u53ef\u7528\uff0c\u8bf7\u91cd\u542f\u6e38\u620f\u540e\u91cd\u8bd5\u3002"));
            }
            return true;
        }
    }

    private boolean handleSourceInteractionInner(ServerPlayer player, ServerLevel level, BlockPos pos) {
        int ammunition;
        LogisticsConfig.SupplySource source = this.findSource(player, level, pos);
        if (source == null) {
            return false;
        }
        long cooldownMillis = (long)LogisticsConfig.get().pickupCooldownSeconds * 1000L;
        long now = System.currentTimeMillis();
        Long lastPickup = this.pickupCooldowns.get(player.m_20148_());
        if (lastPickup != null && now - lastPickup < cooldownMillis) {
            long remaining = Math.max(1L, (cooldownMillis - (now - lastPickup) + 999L) / 1000L);
            player.m_213846_(Component.m_237113_("\u00a7e\u8865\u7ed9\u88c5\u8f7d\u4e2d\uff0c" + remaining + " \u79d2\u540e\u53ef\u518d\u6b21\u9886\u53d6\u3002"));
            return true;
        }
        int construction = this.giveSupplies(player, source, source.construction, SupplyType.CONSTRUCTION);
        if (construction + (ammunition = this.giveSupplies(player, source, source.ammunition, SupplyType.AMMUNITION)) <= 0) {
            player.m_213846_(Component.m_237113_("\u00a7c\u8be5\u8865\u7ed9\u6765\u6e90\u6ca1\u6709\u53ef\u53d1\u653e\u7684\u6709\u6548\u7269\u54c1\u3002"));
            return true;
        }
        this.pickupCooldowns.put(player.m_20148_(), now);
        player.m_150109_().m_6596_();
        player.f_36095_.m_38946_();
        player.m_213846_(Component.m_237113_("\u00a7a\u5df2\u9886\u53d6\u8865\u7ed9 \u00a77| \u00a76\u5efa\u6750 " + construction + " \u00a77| \u00a7b\u5f39\u836f " + ammunition + " \u00a77\uff08\u4f7f\u7528\u8865\u7ed9\u8f7d\u5177\u8fd0\u9001\u81f3 Radio\uff09"));
        return true;
    }

    @Nullable
    public LogisticsConfig.SupplySource findSource(ServerPlayer player, ServerLevel level, BlockPos pos) {
        BlockState state = level.m_8055_(pos);
        BlockEntity blockEntity = level.m_7702_(pos);
        String team = Espetro.getPlayerTeam(player);
        for (LogisticsConfig.SupplySource source : LogisticsConfig.get().sources) {
            if (source.team != null && !source.team.isBlank() && !source.team.equalsIgnoreCase(team) || !this.matchesBlock(source.blocks, state) || !this.matchesLocation(source.locations, level, pos) || !this.matchesSourceId(source.sourceIds, blockEntity) || !this.matchesBlockEntityNbt(source.blockEntityNbt, blockEntity)) continue;
            return source;
        }
        return null;
    }

    private int giveSupplies(ServerPlayer player, LogisticsConfig.SupplySource source, List<LogisticsConfig.SupplyItem> entries, SupplyType type) {
        int points = 0;
        for (LogisticsConfig.SupplyItem entry : entries) {
            ItemStack stack = this.createSupplyStack(source, entry, type);
            if (stack.m_41619_()) continue;
            points += stack.m_41613_() * Math.max(1, entry.pointsPerItem);
            if (player.m_150109_().m_36054_(stack)) continue;
            player.m_36176_(stack, false);
        }
        return points;
    }

    private ItemStack createSupplyStack(LogisticsConfig.SupplySource source, LogisticsConfig.SupplyItem entry, SupplyType type) {
        if (entry.id == null || entry.id.isBlank()) {
            Espetro.LOGGER.warn("\u8865\u7ed9\u6765\u6e90 {} \u542b\u6709\u7f3a\u5931\u7269\u54c1 ID \u7684 {} \u6761\u76ee", (Object)source.id, (Object)type.id());
            return ItemStack.f_41583_;
        }
        ResourceLocation itemId = ResourceLocation.m_135820_(entry.id);
        if (itemId == null || !BuiltInRegistries.f_257033_.m_7804_(itemId)) {
            Espetro.LOGGER.warn("\u65e0\u6548\u8865\u7ed9\u7269\u54c1: {}", (Object)entry.id);
            return ItemStack.f_41583_;
        }
        Item item = BuiltInRegistries.f_257033_.m_7745_(itemId);
        ItemStack stack = new ItemStack(item, Math.max(1, entry.count));
        if (entry.nbt != null && !entry.nbt.isBlank()) {
            try {
                stack.m_41751_(TagParser.m_129359_(entry.nbt));
            }
            catch (CommandSyntaxException e) {
                Espetro.LOGGER.warn("\u8865\u7ed9\u7269\u54c1 NBT \u65e0\u6548: {} {}", (Object)entry.id, (Object)entry.nbt);
                return ItemStack.f_41583_;
            }
        }
        CompoundTag tag = stack.m_41784_();
        tag.m_128359_(NBT_SUPPLY_TYPE, type.id());
        tag.m_128359_(NBT_SUPPLY_ID, entry.supplyId == null || entry.supplyId.isBlank() ? entry.id : entry.supplyId);
        tag.m_128405_(NBT_SUPPLY_POINTS, Math.max(1, entry.pointsPerItem));
        tag.m_128359_(NBT_SOURCE_ID, source.id);
        return stack;
    }

    @Nullable
    public SupplyType getSupplyType(ItemStack stack) {
        CompoundTag tag = stack.m_41783_();
        if (tag == null) {
            return null;
        }
        return SupplyType.fromId(tag.m_128461_(NBT_SUPPLY_TYPE));
    }

    public int getPointsPerItem(ItemStack stack) {
        CompoundTag tag = stack.m_41783_();
        return tag != null && tag.m_128441_(NBT_SUPPLY_POINTS) ? Math.max(1, tag.m_128451_(NBT_SUPPLY_POINTS)) : 1;
    }

    public int countConstructionPoints(ServerPlayer player) {
        int total = 0;
        for (int slot = 0; slot < player.m_150109_().m_6643_(); ++slot) {
            ItemStack stack = player.m_150109_().m_8020_(slot);
            if (stack.m_41619_() || this.getSupplyType(stack) != SupplyType.CONSTRUCTION) continue;
            total += stack.m_41613_() * this.getPointsPerItem(stack);
        }
        return total;
    }

    public boolean consumeConstructionPoints(ServerPlayer player, int points) {
        if (points <= 0) {
            return true;
        }
        if (this.countConstructionPoints(player) < points) {
            return false;
        }
        int remaining = points;
        for (int slot = 0; slot < player.m_150109_().m_6643_() && remaining > 0; ++slot) {
            ItemStack stack = player.m_150109_().m_8020_(slot);
            if (stack.m_41619_() || this.getSupplyType(stack) != SupplyType.CONSTRUCTION) continue;
            int pointsPerItem = this.getPointsPerItem(stack);
            int consume = Math.min(stack.m_41613_(), (remaining + pointsPerItem - 1) / pointsPerItem);
            if (consume <= 0) continue;
            stack.m_41774_(consume);
            remaining -= consume * pointsPerItem;
        }
        player.m_150109_().m_6596_();
        player.f_36095_.m_38946_();
        return true;
    }

    private boolean matchesBlock(List<String> matchers, BlockState state) {
        if (matchers == null || matchers.isEmpty()) {
            return true;
        }
        ResourceLocation blockId = BuiltInRegistries.f_256975_.m_7981_(state.m_60734_());
        for (String matcher : matchers) {
            TagKey tag;
            ResourceLocation tagId;
            if (matcher == null || matcher.isBlank() || !(matcher.startsWith("#") ? (tagId = ResourceLocation.m_135820_(matcher.substring(1))) != null && state.m_204336_(tag = BlockTags.create((ResourceLocation)tagId)) : matcher.equals(blockId.toString()))) continue;
            return true;
        }
        return false;
    }

    private boolean matchesLocation(List<LogisticsConfig.SourceLocation> locations, ServerLevel level, BlockPos pos) {
        if (locations == null || locations.isEmpty()) {
            return true;
        }
        String dimension = level.m_46472_().m_135782_().toString();
        for (LogisticsConfig.SourceLocation location : locations) {
            BlockPos configured;
            if (location.position == null || location.position.length < 3 || location.dimension != null && !location.dimension.isBlank() && !location.dimension.equals(dimension) || !((configured = new BlockPos(location.position[0], location.position[1], location.position[2])).m_123331_(pos) <= SupplyManager.square(Math.max(0.5, location.radius)))) continue;
            return true;
        }
        return false;
    }

    private boolean matchesSourceId(List<String> sourceIds, @Nullable BlockEntity blockEntity) {
        if (sourceIds == null || sourceIds.isEmpty()) {
            return true;
        }
        if (blockEntity == null) {
            return false;
        }
        CompoundTag tag = blockEntity.m_187480_();
        String sourceId = tag.m_128461_("source_id");
        return sourceIds.stream().anyMatch(id -> Objects.equals(id, sourceId));
    }

    private boolean matchesBlockEntityNbt(@Nullable String expectedSnbt, @Nullable BlockEntity blockEntity) {
        if (expectedSnbt == null || expectedSnbt.isBlank()) {
            return true;
        }
        if (blockEntity == null) {
            return false;
        }
        try {
            CompoundTag expected = TagParser.m_129359_(expectedSnbt);
            return NbtUtils.m_129235_(expected, blockEntity.m_187480_(), true);
        }
        catch (CommandSyntaxException e) {
            Espetro.LOGGER.warn("\u8865\u7ed9\u6765\u6e90 block_entity_nbt \u65e0\u6548: {}", (Object)expectedSnbt);
            return false;
        }
    }

    private static double square(double value) {
        return value * value;
    }
}

