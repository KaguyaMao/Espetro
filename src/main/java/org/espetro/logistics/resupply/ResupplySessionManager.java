package org.espetro.logistics.resupply;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.PacketDistributor;
import org.espetro.Espetro;
import org.espetro.bastion.BastionData;
import org.espetro.bastion.BastionManager;
import org.espetro.bastion.FobSupplyTracker;
import org.espetro.compat.taczmagazines.MagazineCompat;
import org.espetro.compat.taczmagazines.MagazineCompatProvider;
import org.espetro.logistics.LogisticsConfig;
import org.espetro.network.NetworkManager;
import org.espetro.network.ResupplyCatalogPacket;
import org.espetro.network.ResupplyEntryDeltaPacket;
import org.espetro.network.SelectResupplyEntryPacket;
import org.espetro.network.VehicleSupplyActionPacket;
import org.espetro.team.ClassCountManager;
import org.espetro.team.FactionDataLoader;
import org.espetro.team.FactionDataProvider;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Server-owned, short-lived resupply menus (the plan's ResupplyService).
 * Every click resolves the source, class, inventory and balance again; the
 * client never submits an ItemStack, price or maximum.
 */
public final class ResupplySessionManager {
    private static final int MAX_ITEMS = 64;
    private static final int RESULT_CACHE_SIZE = 64;
    /**
     * Sentinel slot id for the offhand.  The player's offhand must be scanned by
     * every "how much of this item do I already own" check, otherwise a player
     * can park the supplied item in the offhand to keep claiming past the cap.
     */
    private static final int OFFHAND_SLOT = -1;
    private static final int RATE_LIMIT_CACHE_SIZE = 8192;
    private static final long SESSION_TIMEOUT_TICKS = 20L * 30L;
    private static final AtomicLong NEXT_CATALOGUE_REVISION = new AtomicLong(1L);
    private static final Map<UUID, Session> SESSIONS = new HashMap<>();
    private static final LinkedHashMap<ActionKey, Long> LAST_ACTION_TICKS =
        new LinkedHashMap<>(256, 0.75F, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<ActionKey, Long> eldest) {
                return size() > RATE_LIMIT_CACHE_SIZE;
            }
        };
    private static long lastSweepTick = Long.MIN_VALUE / 2;

    private ResupplySessionManager() {
    }

    public static void open(ServerPlayer player, ResupplySourceRef source) {
        if (player == null || source == null) return;
        ResolvedSource resolved = resolveSource(player, source, null);
        if (resolved == null) {
            player.displayClientMessage(Component.literal("§c补给来源已失效或不属于己方。"), true);
            return;
        }

        String classId = ClassCountManager.getInstance().getPlayerClass(player.getUUID());
        String variantId = ClassCountManager.getInstance().getPlayerVariant(player.getUUID());
        FactionDataLoader loader = FactionDataProvider.getOrCreateLoader();
        FactionDataLoader.ClassKitData kit = classId == null ? null : loader.getClassKit(classId);
        FactionDataLoader.ClassVariantData variant = kit == null ? null : kit.getVariant(variantId);
        FactionDataLoader.ResupplyData data = variant == null ? null : variant.resupply;
        if (data == null || data.items == null || data.items.length == 0) {
            player.displayClientMessage(Component.literal("§c当前职业变体没有逐项补给配置。"), true);
            return;
        }

        List<EntrySpec> specs = new ArrayList<>(Math.min(MAX_ITEMS, data.items.length));
        for (int i = 0; i < data.items.length && i < MAX_ITEMS; i++) {
            specs.add(resolveEntry(i, data.items[i], data));
        }
        UUID token = UUID.randomUUID();
        long catalogue = NEXT_CATALOGUE_REVISION.getAndIncrement();
        long now = player.getServer() == null ? 0L : player.getServer().getTickCount();
        Session session = new Session(player.getUUID(), token, catalogue, source,
            resolved.accountId(), classId, variantId == null ? "" : variantId,
            List.copyOf(specs), now);
        SESSIONS.put(player.getUUID(), session);
        sendCatalogue(player, session, resolved);
    }

    public static void select(ServerPlayer player, SelectResupplyEntryPacket packet) {
        Session session = SESSIONS.get(player.getUUID());
        if (session == null || !session.token.equals(packet.token())) {
            sendClosed(player, packet.token(), packet.actionSeq(), "补给会话不存在或已经过期。");
            return;
        }
        synchronized (session) {
            ResupplyEntryDeltaPacket cached = session.results.get(packet.actionSeq());
            if (cached != null) {
                send(player, cached);
                return;
            }
            if (packet.actionSeq() <= session.highestActionSeq) {
                send(player, failure(session, packet.actionSeq(), null,
                    "请求序号已经过期。", false, currentBalance(player, session)));
                return;
            }
            session.highestActionSeq = packet.actionSeq();

            long now = player.getServer() == null ? 0L : player.getServer().getTickCount();
            if (now - session.lastAccessTick > SESSION_TIMEOUT_TICKS
                || packet.catalogRevision() != session.catalogRevision
                || !session.source.equals(packet.source())
                || !sameLoadout(player, session)) {
                SESSIONS.remove(player.getUUID());
                ResupplyEntryDeltaPacket closed = failure(session, packet.actionSeq(), null,
                    "职业、来源或目录已变化，请重新打开补给菜单。", true, 0);
                remember(session, packet.actionSeq(), closed);
                send(player, closed);
                return;
            }
            session.lastAccessTick = now;

            if (packet.entryIndex() < 0 || packet.entryIndex() >= session.entries.size()) {
                ResupplyEntryDeltaPacket result = failure(session, packet.actionSeq(), null,
                    "补给条目不存在。", false, currentBalance(player, session));
                remember(session, packet.actionSeq(), result);
                send(player, result);
                return;
            }

            ResolvedSource source = resolveSource(player, session.source, session.accountId);
            if (source == null) {
                SESSIONS.remove(player.getUUID());
                ResupplyEntryDeltaPacket closed = failure(session, packet.actionSeq(), null,
                    "补给来源已失效、距离过远或不再属于己方。", true, 0);
                remember(session, packet.actionSeq(), closed);
                send(player, closed);
                return;
            }

            EntrySpec spec = session.entries.get(packet.entryIndex());
            ActionKey actionKey = new ActionKey(player.getUUID(), source.accountId(),
                session.classId, session.variantId, spec.index);
            long previousTick = LAST_ACTION_TICKS.getOrDefault(actionKey, Long.MIN_VALUE / 2);
            if (now - previousTick < 1L) {
                ResupplyEntryDeltaPacket result = failure(session, packet.actionSeq(),
                    view(player, source, spec), "操作过快，请稍后重试。", false,
                    source.balance());
                remember(session, packet.actionSeq(), result);
                send(player, result);
                return;
            }
            LAST_ACTION_TICKS.put(actionKey, now);

            TransactionResult transaction = transact(player, source, spec);
            if (transaction.success) session.stateRevision++;
            ResupplyCatalogPacket.Entry entry = view(player, source, spec);
            ResupplyEntryDeltaPacket result = new ResupplyEntryDeltaPacket(session.token,
                packet.actionSeq(), session.stateRevision, source.balance(), transaction.success,
                false, transaction.message, entry);
            remember(session, packet.actionSeq(), result);
            send(player, result);
        }
    }

    public static void close(UUID playerId, UUID token) {
        Session session = SESSIONS.get(playerId);
        if (session != null && session.token.equals(token)) SESSIONS.remove(playerId);
    }

    public static void clearPlayer(UUID playerId) {
        if (playerId == null) return;
        SESSIONS.remove(playerId);
        LAST_ACTION_TICKS.keySet().removeIf(key -> playerId.equals(key.playerId));
    }

    public static void clearAll() {
        SESSIONS.clear();
        LAST_ACTION_TICKS.clear();
        lastSweepTick = Long.MIN_VALUE / 2;
    }

    public static int activeSessionCount() {
        return SESSIONS.size();
    }

    /**
     * Bounded server-tick cleanup.  Besides timeout, this actively invalidates
     * menus when the player changes loadout or the backing source disappears.
     */
    public static void tick(MinecraftServer server) {
        if (server == null || SESSIONS.isEmpty()) return;
        long sweepTick = server.getTickCount();
        if (sweepTick - lastSweepTick < 20L) return;
        lastSweepTick = sweepTick;
        Iterator<Map.Entry<UUID, Session>> iterator = SESSIONS.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, Session> entry = iterator.next();
            Session session = entry.getValue();
            ServerPlayer player = server.getPlayerList().getPlayer(entry.getKey());
            long now = sweepTick;
            boolean expired = player == null
                || now - session.lastAccessTick > SESSION_TIMEOUT_TICKS;
            boolean invalid = !expired && (!sameLoadout(player, session)
                || resolveSource(player, session.source, session.accountId) == null);
            if (!expired && !invalid) continue;
            iterator.remove();
            if (player != null) {
                send(player, new ResupplyEntryDeltaPacket(session.token,
                    session.highestActionSeq, session.stateRevision, 0,
                    false, true, expired ? "补给会话已超时。" : "补给来源或职业已变化。", null));
            }
        }
        LAST_ACTION_TICKS.entrySet().removeIf(entry ->
            sweepTick - entry.getValue() > SESSION_TIMEOUT_TICKS);
    }

    private static void sendCatalogue(ServerPlayer player, Session session,
                                      ResolvedSource source) {
        List<ResupplyCatalogPacket.Entry> entries = new ArrayList<>(session.entries.size());
        for (EntrySpec entry : session.entries) entries.add(view(player, source, entry));
        NetworkManager.NET.send(PacketDistributor.PLAYER.with(() -> player),
            new ResupplyCatalogPacket(session.token, session.catalogRevision,
                session.stateRevision, session.source, source.balance(), entries));
    }

    private static EntrySpec resolveEntry(int index, FactionDataLoader.ResupplyItem configured,
                                          FactionDataLoader.ResupplyData parent) {
        if (configured == null || configured.id == null) {
            return EntrySpec.unavailable(index, "", 1, 1, 1, "配置为空");
        }
        ResupplyItemIdentity.Configured identity =
            ResupplyItemIdentity.parse(configured.id, configured.nbt);
        String raw = identity.registryId();
        String nbt = identity.nbt();
        boolean exactTag = identity.exactTag();
        int cost = configured.ammoCost != null ? configured.ammoCost
            : parent.ammoCost != null ? parent.ammoCost
            : LogisticsConfig.get().defaultResupplyAmmoCost;
        int count = Math.max(1, configured.count);
        int max = Math.max(count, configured.max);
        ResourceLocation id = ResourceLocation.tryParse(raw);
        if (id == null) return EntrySpec.unavailable(index, raw, count, max, cost, "物品ID无效");
        Optional<Item> registered = BuiltInRegistries.ITEM.getOptional(id);
        if (registered.isEmpty()) {
            return EntrySpec.unavailable(index, raw, count, max, cost, "所需模组或物品未加载");
        }
        ItemStack template = new ItemStack(registered.get());
        if (nbt != null && !nbt.isBlank()) {
            try {
                CompoundTag parsed = TagParser.parseTag(nbt);
                template.setTag(parsed);
            } catch (CommandSyntaxException error) {
                return EntrySpec.unavailable(index, raw, count, max, cost, "物品NBT无效");
            }
        }
        MagazineCompat magazine = MagazineCompatProvider.get();
        Optional<MagazineCompat.Identity> magazineIdentity = magazine.identity(template);
        if ("taczmagazines".equals(id.getNamespace()) && magazineIdentity.isEmpty()) {
            return EntrySpec.unavailable(index, raw, count, max, cost,
                magazine.available() ? "弹匣身份无效" : "TaCZ Magazines未加载或版本不兼容");
        }
        return new EntrySpec(index, raw, template, exactTag, count, max, Math.max(0, cost),
            magazineIdentity.orElse(null), "");
    }

    private static TransactionResult transact(ServerPlayer player, ResolvedSource source,
                                               EntrySpec spec) {
        if (!spec.unavailableReason.isEmpty()) return TransactionResult.fail(spec.unavailableReason);
        if (source.balance() < spec.ammoCost) return TransactionResult.fail("来源弹药不足");
        if (spec.magazineIdentity != null) return transactMagazine(player, source, spec);
        int current = countNormal(player, spec);
        int wanted = org.espetro.logistics.AmmoResupplyPolicy.grantCount(
            current, spec.max, spec.count);
        int grant = Math.min(wanted, insertionCapacity(player, spec.template));
        if (grant <= 0) return TransactionResult.fail(current >= spec.max ? "已达到补给上限" : "背包没有空间");

        List<ItemStack> before = snapshotInventory(player);
        if (!source.consume(spec.ammoCost)) return TransactionResult.fail("来源弹药刚刚发生变化");
        ItemStack inserted = spec.template.copy();
        inserted.setCount(grant);
        boolean added = player.getInventory().add(inserted) && inserted.isEmpty();
        if (!added) {
            restoreInventory(player, before);
            source.refund(spec.ammoCost);
            return TransactionResult.fail("背包变化导致事务回滚");
        }
        finish(player, source);
        return TransactionResult.ok("已补给 " + spec.template.getHoverName().getString()
            + " ×" + grant + "，消耗 " + spec.ammoCost + " 弹药");
    }

    private static TransactionResult transactMagazine(ServerPlayer player,
                                                       ResolvedSource source, EntrySpec spec) {
        MagazineCompat compat = MagazineCompatProvider.get();
        List<MagazineSlot> matching = matchingMagazines(player, spec, compat);
        int full = matching.stream().filter(MagazineSlot::full)
            .mapToInt(slot -> slot.stack.getCount()).sum();
        if (full >= spec.max) return TransactionResult.fail("满弹匣已达到上限");
        // 补给弹匣 = 把背包里已有的半空弹匣填满，而不是「发一个新弹匣再吃掉旧的」：
        // 旧做法会连弹匣里剩余的子弹一起丢掉，玩家每次补充都要丢一次。
        MagazineSlot partial = matching.stream().filter(slot -> !slot.full)
            .min(Comparator.comparingInt(MagazineSlot::rounds)
                .thenComparingInt(MagazineSlot::slot)).orElse(null);
        if (partial == null) {
            // 没有可填充的弹匣：退回"直接发一个满弹匣"。
            // 打空后的弹匣可能还嵌在枪里（TaCZMag_StoredMagazine）、被拆走或掉在地上，
            // 背包里没有任何可填对象时如果直接拒绝，玩家就彻底补不了弹。
            ItemStack replacement = compat.createFull(spec.template);
            if (replacement.isEmpty()) return TransactionResult.fail("无法创建该弹匣");
            if (insertionCapacity(player, replacement) < 1) {
                return TransactionResult.fail("背包没有空间（枪内的空弹匣请先取出，或清空一个背包格）");
            }
            List<ItemStack> beforeAdd = snapshotInventory(player);
            if (!source.consume(spec.ammoCost)) return TransactionResult.fail("来源弹药刚刚发生变化");
            ItemStack inserted = replacement.copy();
            boolean added = player.getInventory().add(inserted) && inserted.isEmpty();
            if (!added) {
                restoreInventory(player, beforeAdd);
                source.refund(spec.ammoCost);
                return TransactionResult.fail("背包变化导致弹匣事务回滚");
            }
            finish(player, source);
            return TransactionResult.ok("已领取 1 个满弹匣，消耗 " + spec.ammoCost + " 弹药");
        }

        int capacity = spec.magazineIdentity.capacity();
        if (partial.stored) {
            // 枪内弹匣走独立事务（它自己扣一次弹药并写回枪械 NBT），
            // 这里必须先分流，否则会在下面先扣一次、再在事务里扣第二次。
            return transactStoredMagazine(player, source, spec, partial, capacity);
        }
        List<ItemStack> before = snapshotInventory(player);
        if (!source.consume(spec.ammoCost)) return TransactionResult.fail("来源弹药刚刚发生变化");
        ItemStack target = partial.slot == OFFHAND_SLOT
            ? player.getInventory().offhand.get(0)
            : player.getInventory().getItem(partial.slot);
        // TaCZ Magazines stacks identical magazines in one slot.  Filling the whole
        // stack would hand the player several magazines for a single supply unit, so
        // one magazine is split off and filled on its own.
        boolean split = target.getCount() > 1;
        ItemStack filled;
        if (split) {
            filled = target.copy();
            filled.setCount(1);
            target.shrink(1);
        } else {
            filled = target;
        }
        if (!compat.bindAmmo(filled, spec.magazineIdentity.ammoId())
            || !compat.setAmmoCount(filled, capacity)) {
            restoreInventory(player, before);
            source.refund(spec.ammoCost);
            return TransactionResult.fail("无法填充该弹匣");
        }
        if (split) {
            ItemStack returned = filled.copy();
            boolean added = player.getInventory().add(returned) && returned.isEmpty();
            if (!added) {
                restoreInventory(player, before);
                source.refund(spec.ammoCost);
                return TransactionResult.fail("背包没有空间放下填好的弹匣");
            }
        }
        finish(player, source);
        return TransactionResult.ok("已填满 1 个弹匣（" + partial.rounds + " → " + capacity
            + " 发），消耗 " + spec.ammoCost + " 弹药");
    }

    /** 填满枪械 NBT 里的弹匣并写回。 */
    private static TransactionResult transactStoredMagazine(ServerPlayer player,
                                                             ResolvedSource source, EntrySpec spec,
                                                             MagazineSlot partial, int capacity) {
        MagazineCompat compat = MagazineCompatProvider.get();
        ItemStack gun = partial.slot == OFFHAND_SLOT
            ? player.getInventory().offhand.get(0)
            : player.getInventory().getItem(partial.slot);
        if (gun.isEmpty()) return TransactionResult.fail("枪械已不在背包中");
        ItemStack filled = partial.stack.copy();
        if (filled.isEmpty()
            || !compat.bindAmmo(filled, spec.magazineIdentity.ammoId())
            || !compat.setAmmoCount(filled, capacity)) {
            return TransactionResult.fail("无法填充该弹匣");
        }
        if (!source.consume(spec.ammoCost)) return TransactionResult.fail("来源弹药刚刚发生变化");
        if (!compat.setStoredMagazine(gun, filled)) {
            source.refund(spec.ammoCost);
            return TransactionResult.fail("无法写回枪内弹匣");
        }
        finish(player, source);
        return TransactionResult.ok("已填满枪内弹匣（" + partial.rounds + " → " + capacity
            + " 发），消耗 " + spec.ammoCost + " 弹药");
    }

    private static void finish(ServerPlayer player, ResolvedSource source) {
        player.getInventory().setChanged();
        player.inventoryMenu.broadcastChanges();
        player.containerMenu.broadcastChanges();
        BastionManager.getInstance().recordResupply(player.getUUID());
        source.notifyChanged();
    }

    private static ResupplyCatalogPacket.Entry view(ServerPlayer player, ResolvedSource source,
                                                     EntrySpec spec) {
        int current = spec.magazineIdentity == null ? countNormal(player, spec)
            : countFullMagazines(player, spec, MagazineCompatProvider.get());
        String reason = spec.unavailableReason;
        boolean selectable = reason.isEmpty();
        if (selectable && current >= spec.max) {
            selectable = false;
            reason = spec.magazineIdentity == null ? "已达到上限" : "满弹匣已达到上限";
        }
        if (selectable && source.balance() < spec.ammoCost) {
            selectable = false;
            reason = "来源弹药不足";
        }
        if (selectable) {
            if (spec.magazineIdentity == null && insertionCapacity(player, spec.template) <= 0) {
                selectable = false;
                reason = "背包没有空间";
            } else if (spec.magazineIdentity != null
                && !hasFillableMagazine(player, spec, MagazineCompatProvider.get())
                && !canReceiveNewMagazine(player, spec, MagazineCompatProvider.get())) {
                selectable = false;
                reason = "背包没有空间";
            }
        }
        return new ResupplyCatalogPacket.Entry(spec.index, spec.template, spec.configuredId,
            spec.count, spec.max, spec.ammoCost, current, selectable, reason);
    }

    private static int countNormal(ServerPlayer player, EntrySpec spec) {
        int count = 0;
        for (ItemStack stack : player.getInventory().items) {
            if (matchesCountedNormal(stack, spec)) count += stack.getCount();
        }
        ItemStack offhand = player.getInventory().offhand.get(0);
        if (matchesCountedNormal(offhand, spec)) count += offhand.getCount();
        return count;
    }

    private static boolean matchesCountedNormal(ItemStack stack, EntrySpec spec) {
        return !stack.isEmpty() && ResupplyItemIdentity.matchesNormal(spec.exactTag,
            stack.is(spec.template.getItem()),
            ItemStack.isSameItemSameTags(stack, spec.template));
    }

    private static int insertionCapacity(ServerPlayer player, ItemStack template) {
        int capacity = 0;
        for (ItemStack stack : player.getInventory().items) {
            if (stack.isEmpty()) capacity += template.getMaxStackSize();
            else if (ItemStack.isSameItemSameTags(stack, template)) {
                capacity += Math.max(0, stack.getMaxStackSize() - stack.getCount());
            }
            if (capacity >= 1_000_000) return 1_000_000;
        }
        return capacity;
    }

    private static int countFullMagazines(ServerPlayer player, EntrySpec spec,
                                          MagazineCompat compat) {
        return matchingMagazines(player, spec, compat).stream()
            .filter(MagazineSlot::full).mapToInt(slot -> slot.stack.getCount()).sum();
    }

    private static boolean hasFillableMagazine(ServerPlayer player, EntrySpec spec,
                                               MagazineCompat compat) {
        return matchingMagazines(player, spec, compat).stream().anyMatch(slot -> !slot.full);
    }

    /** 背包里没有可填弹匣时，是否还能直接收下一个新的满弹匣。 */
    private static boolean canReceiveNewMagazine(ServerPlayer player, EntrySpec spec,
                                                 MagazineCompat compat) {
        ItemStack replacement = compat.createFull(spec.template);
        return !replacement.isEmpty() && insertionCapacity(player, replacement) > 0;
    }

    private static List<MagazineSlot> matchingMagazines(ServerPlayer player, EntrySpec spec,
                                                        MagazineCompat compat) {
        List<MagazineSlot> matches = new ArrayList<>();
        for (int slot = 0; slot < player.getInventory().items.size(); slot++) {
            ItemStack stack = player.getInventory().items.get(slot);
            collectMagazine(compat, spec, slot, stack, matches);
            collectStoredMagazine(compat, spec, slot, stack, matches);
        }
        ItemStack offhand = player.getInventory().offhand.get(0);
        collectMagazine(compat, spec, OFFHAND_SLOT, offhand, matches);
        collectStoredMagazine(compat, spec, OFFHAND_SLOT, offhand, matches);
        return matches;
    }

    /**
     * 枪械自带的弹匣（打空后仍留在枪里）同样算“可填充对象”。
     * TaCZ Magazines 把弹匣存在枪的 NBT 里，不占背包格；只看背包会得出
     * “没有可填充的弹匣”，玩家把弹打光后就再也补不上。
     */
    private static void collectStoredMagazine(MagazineCompat compat, EntrySpec spec, int slot,
                                              ItemStack gun, List<MagazineSlot> out) {
        if (gun.isEmpty()) return;
        ItemStack stored = compat.storedMagazine(gun);
        if (stored.isEmpty()) return;
        Optional<MagazineCompat.Identity> identity = compat.identity(stored);
        if (identity.isEmpty() || !compat.sameMagazineType(identity.get(), spec.magazineIdentity)) {
            return;
        }
        int rounds = compat.ammoCount(stored);
        out.add(new MagazineSlot(slot, stored, rounds, rounds >= spec.magazineIdentity.capacity(),
            true));
    }

    private static void collectMagazine(MagazineCompat compat, EntrySpec spec, int slot,
                                        ItemStack stack, List<MagazineSlot> out) {
        if (stack.isEmpty()) return;
        Optional<MagazineCompat.Identity> identity = compat.identity(stack);
        if (identity.isEmpty() || !compat.sameMagazineType(identity.get(), spec.magazineIdentity)) {
            return;
        }
        int rounds = compat.ammoCount(stack);
        out.add(new MagazineSlot(slot, stack, rounds, rounds >= spec.magazineIdentity.capacity(),
            false));
    }

    private static List<ItemStack> snapshotInventory(ServerPlayer player) {
        List<ItemStack> snapshot = new ArrayList<>(player.getInventory().items.size() + 1);
        for (ItemStack stack : player.getInventory().items) snapshot.add(stack.copy());
        snapshot.add(player.getInventory().offhand.get(0).copy());
        return snapshot;
    }

    private static void restoreInventory(ServerPlayer player, List<ItemStack> snapshot) {
        int main = player.getInventory().items.size();
        for (int i = 0; i < main && i < snapshot.size(); i++) {
            player.getInventory().setItem(i, snapshot.get(i).copy());
        }
        if (snapshot.size() > main) {
            player.getInventory().offhand.set(0, snapshot.get(main).copy());
        }
        player.getInventory().setChanged();
    }

    private static boolean sameLoadout(ServerPlayer player, Session session) {
        String classId = ClassCountManager.getInstance().getPlayerClass(player.getUUID());
        String variantId = ClassCountManager.getInstance().getPlayerVariant(player.getUUID());
        return session.classId.equals(classId)
            && session.variantId.equals(variantId == null ? "" : variantId);
    }

    private static int currentBalance(ServerPlayer player, Session session) {
        ResolvedSource source = resolveSource(player, session.source, session.accountId);
        return source == null ? 0 : source.balance();
    }

    @Nullable
    private static ResolvedSource resolveSource(ServerPlayer player, ResupplySourceRef ref,
                                                @Nullable UUID expectedAccount) {
        if (ref.kind() == ResupplySourceRef.Kind.MAIN_BASE_AMMO) {
            // 主出生点无限弹药箱：必须仍是部署点旁自动放置的弹药箱（防伪造），
            // 弹药值无限且永不消耗。
            if (!org.espetro.logistics.DeploySupplyStationPlacer
                .isMainBaseAmmoCrate(
                    (net.minecraft.server.level.ServerLevel) player.serverLevel(), ref.blockPos())) {
                return null;
            }
            return new ResolvedSource(new UUID(0L, 1L), () -> Integer.MAX_VALUE,
                amount -> true, amount -> { }, () -> { });
        }
        if (ref.kind() == ResupplySourceRef.Kind.RADIO) {
            // 主基地（原部署点）无限弹药箱：轮盘「补给步兵」从弹药箱发起时
            // source 仍标记为 radio(点击位置)，此处识别为主基地无限源。
            if (org.espetro.logistics.DeploySupplyStationPlacer.isMainBaseAmmoCrate(
                    (net.minecraft.server.level.ServerLevel) player.serverLevel(), ref.blockPos())) {
                return new ResolvedSource(new UUID(0L, 1L), () -> Integer.MAX_VALUE,
                    amount -> true, amount -> { }, () -> { });
            }
            BastionData nearby = org.espetro.network.RadioRadialPacket
                .findFriendlyRadioNearby(player, ref.blockPos());
            if (nearby == null || expectedAccount != null
                && !expectedAccount.equals(nearby.getBastionId())) return null;
            return new ResolvedSource(nearby.getBastionId(), nearby::getAmmunitionSupplies,
                nearby::consumeAmmunitionSupplies,
                amount -> nearby.addAmmunitionSupplies(amount,
                    LogisticsConfig.get().maxAmmunition),
                () -> FobSupplyTracker.notifySupplyChanged(nearby));
        }
        VehicleSupplyActionPacket.Interaction interaction =
            VehicleSupplyActionPacket.resolveInteraction(player, ref.entityId());
        if (interaction == null || expectedAccount != null
            && !expectedAccount.equals(interaction.vehicleId())) return null;
        return new ResolvedSource(interaction.vehicleId(), interaction.supply()::getAmmo,
            amount -> interaction.supply().removeAmmo(amount) == amount,
            amount -> interaction.supply().addAmmo(amount), () -> { });
    }

    private static ResupplyEntryDeltaPacket failure(Session session, long seq,
                                                     @Nullable ResupplyCatalogPacket.Entry entry,
                                                     String message, boolean close, int balance) {
        return new ResupplyEntryDeltaPacket(session.token, seq, session.stateRevision,
            Math.max(0, balance), false, close, message, entry);
    }

    private static void sendClosed(ServerPlayer player, UUID token, long seq, String message) {
        send(player, new ResupplyEntryDeltaPacket(token, seq, 0L, 0,
            false, true, message, null));
    }

    private static void remember(Session session, long seq, ResupplyEntryDeltaPacket result) {
        session.results.put(seq, result);
    }

    private static void send(ServerPlayer player, ResupplyEntryDeltaPacket packet) {
        NetworkManager.NET.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }

    private record EntrySpec(int index, String configuredId, ItemStack template,
                             boolean exactTag, int count, int max, int ammoCost,
                             @Nullable MagazineCompat.Identity magazineIdentity,
                             String unavailableReason) {
        static EntrySpec unavailable(int index, String id, int count, int max, int cost,
                                     String reason) {
            return new EntrySpec(index, id, ItemStack.EMPTY, false, count, max,
                Math.max(0, cost), null, reason);
        }
    }

    /**
     * @param slot   背包槽位（{@link #OFFHAND_SLOT} = 副手）
     * @param stack  弹匣副本
     * @param stored 是否是枪械 NBT 里的弹匣（TaCZMag_StoredMagazine），填满后要写回枪
     */
    private record MagazineSlot(int slot, ItemStack stack, int rounds, boolean full,
                                boolean stored) {
    }

    private record ActionKey(UUID playerId, UUID accountId, String classId,
                             String variantId, int entryIndex) {
    }

    private record TransactionResult(boolean success, String message) {
        static TransactionResult ok(String message) {
            return new TransactionResult(true, message);
        }

        static TransactionResult fail(String message) {
            return new TransactionResult(false, message);
        }
    }

    @FunctionalInterface
    private interface IntValue {
        int get();
    }

    @FunctionalInterface
    private interface IntAction {
        boolean apply(int amount);
    }

    @FunctionalInterface
    private interface RefundAction {
        void apply(int amount);
    }

    private record ResolvedSource(UUID accountId, IntValue balanceValue,
                                  IntAction consumeAction, RefundAction refundAction,
                                  Runnable changeNotifier) {
        int balance() {
            return Math.max(0, balanceValue.get());
        }

        boolean consume(int amount) {
            return amount == 0 || consumeAction.apply(amount);
        }

        void refund(int amount) {
            if (amount > 0) refundAction.apply(amount);
        }

        void notifyChanged() {
            changeNotifier.run();
        }
    }

    private static final class Session {
        private final UUID playerId;
        private final UUID token;
        private final long catalogRevision;
        private long stateRevision = 1L;
        private final ResupplySourceRef source;
        private final UUID accountId;
        private final String classId;
        private final String variantId;
        private final List<EntrySpec> entries;
        private long lastAccessTick;
        private long highestActionSeq;
        private final LinkedHashMap<Long, ResupplyEntryDeltaPacket> results =
            new LinkedHashMap<>(RESULT_CACHE_SIZE + 1, 0.75F, true) {
                @Override
                protected boolean removeEldestEntry(
                        Map.Entry<Long, ResupplyEntryDeltaPacket> eldest) {
                    return size() > RESULT_CACHE_SIZE;
                }
            };

        private Session(UUID playerId, UUID token, long catalogRevision,
                        ResupplySourceRef source, UUID accountId, String classId,
                        String variantId, List<EntrySpec> entries, long lastAccessTick) {
            this.playerId = playerId;
            this.token = token;
            this.catalogRevision = catalogRevision;
            this.source = source;
            this.accountId = accountId;
            this.classId = classId;
            this.variantId = variantId;
            this.entries = entries;
            this.lastAccessTick = lastAccessTick;
        }
    }
}
