/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  javax.annotation.Nullable
 *  net.minecraftforge.network.PacketDistributor
 */
package org.espetro.logistics.resupply;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.Nullable;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.network.PacketDistributor;
import org.espetro.bastion.BastionData;
import org.espetro.bastion.BastionManager;
import org.espetro.bastion.FobSupplyTracker;
import org.espetro.compat.taczmagazines.MagazineCompat;
import org.espetro.compat.taczmagazines.MagazineCompatProvider;
import org.espetro.logistics.AmmoResupplyPolicy;
import org.espetro.logistics.LogisticsConfig;
import org.espetro.logistics.resupply.ResupplyItemIdentity;
import org.espetro.logistics.resupply.ResupplySourceRef;
import org.espetro.network.NetworkManager;
import org.espetro.network.RadioRadialPacket;
import org.espetro.network.ResupplyCatalogPacket;
import org.espetro.network.ResupplyEntryDeltaPacket;
import org.espetro.network.SelectResupplyEntryPacket;
import org.espetro.network.VehicleSupplyActionPacket;
import org.espetro.team.ClassCountManager;
import org.espetro.team.FactionDataLoader;
import org.espetro.team.FactionDataProvider;

public final class ResupplySessionManager {
    private static final int MAX_ITEMS = 64;
    private static final int RESULT_CACHE_SIZE = 64;
    private static final int RATE_LIMIT_CACHE_SIZE = 8192;
    private static final long SESSION_TIMEOUT_TICKS = 600L;
    private static final AtomicLong NEXT_CATALOGUE_REVISION = new AtomicLong(1L);
    private static final Map<UUID, Session> SESSIONS = new HashMap<UUID, Session>();
    private static final LinkedHashMap<ActionKey, Long> LAST_ACTION_TICKS = new LinkedHashMap<ActionKey, Long>(256, 0.75f, true){

        @Override
        protected boolean removeEldestEntry(Map.Entry<ActionKey, Long> eldest) {
            return this.size() > 8192;
        }
    };
    private static long lastSweepTick = -4611686018427387904L;

    private ResupplySessionManager() {
    }

    public static void open(ServerPlayer player, ResupplySourceRef source) {
        FactionDataLoader.ResupplyData data;
        if (player == null || source == null) {
            return;
        }
        ResolvedSource resolved = ResupplySessionManager.resolveSource(player, source, null);
        if (resolved == null) {
            player.m_5661_(Component.m_237113_("\u00a7c\u8865\u7ed9\u6765\u6e90\u5df2\u5931\u6548\u6216\u4e0d\u5c5e\u4e8e\u5df1\u65b9\u3002"), true);
            return;
        }
        String classId = ClassCountManager.getInstance().getPlayerClass(player.m_20148_());
        String variantId = ClassCountManager.getInstance().getPlayerVariant(player.m_20148_());
        FactionDataLoader loader = FactionDataProvider.getOrCreateLoader();
        FactionDataLoader.ClassKitData kit = classId == null ? null : loader.getClassKit(classId);
        FactionDataLoader.ClassVariantData variant = kit == null ? null : kit.getVariant(variantId);
        FactionDataLoader.ResupplyData resupplyData = data = variant == null ? null : variant.resupply;
        if (data == null || data.items == null || data.items.length == 0) {
            player.m_5661_(Component.m_237113_("\u00a7c\u5f53\u524d\u804c\u4e1a\u53d8\u4f53\u6ca1\u6709\u9010\u9879\u8865\u7ed9\u914d\u7f6e\u3002"), true);
            return;
        }
        ArrayList<EntrySpec> specs = new ArrayList<EntrySpec>(Math.min(64, data.items.length));
        for (int i = 0; i < data.items.length && i < 64; ++i) {
            specs.add(ResupplySessionManager.resolveEntry(i, data.items[i], data));
        }
        UUID token = UUID.randomUUID();
        long catalogue = NEXT_CATALOGUE_REVISION.getAndIncrement();
        long now = player.m_20194_() == null ? 0L : (long)player.m_20194_().m_129921_();
        Session session = new Session(player.m_20148_(), token, catalogue, source, resolved.accountId(), classId, variantId == null ? "" : variantId, List.copyOf(specs), now);
        SESSIONS.put(player.m_20148_(), session);
        ResupplySessionManager.sendCatalogue(player, session, resolved);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void select(ServerPlayer player, SelectResupplyEntryPacket packet) {
        Session session = SESSIONS.get(player.m_20148_());
        if (session == null || !session.token.equals(packet.token())) {
            ResupplySessionManager.sendClosed(player, packet.token(), packet.actionSeq(), "\u8865\u7ed9\u4f1a\u8bdd\u4e0d\u5b58\u5728\u6216\u5df2\u7ecf\u8fc7\u671f\u3002");
            return;
        }
        Session session2 = session;
        synchronized (session2) {
            long now;
            ResupplyEntryDeltaPacket cached = session.results.get(packet.actionSeq());
            if (cached != null) {
                ResupplySessionManager.send(player, cached);
                return;
            }
            if (packet.actionSeq() <= session.highestActionSeq) {
                ResupplySessionManager.send(player, ResupplySessionManager.failure(session, packet.actionSeq(), null, "\u8bf7\u6c42\u5e8f\u53f7\u5df2\u7ecf\u8fc7\u671f\u3002", false, ResupplySessionManager.currentBalance(player, session)));
                return;
            }
            session.highestActionSeq = packet.actionSeq();
            long l = now = player.m_20194_() == null ? 0L : (long)player.m_20194_().m_129921_();
            if (now - session.lastAccessTick > 600L || packet.catalogRevision() != session.catalogRevision || !session.source.equals(packet.source()) || !ResupplySessionManager.sameLoadout(player, session)) {
                SESSIONS.remove(player.m_20148_());
                ResupplyEntryDeltaPacket closed = ResupplySessionManager.failure(session, packet.actionSeq(), null, "\u804c\u4e1a\u3001\u6765\u6e90\u6216\u76ee\u5f55\u5df2\u53d8\u5316\uff0c\u8bf7\u91cd\u65b0\u6253\u5f00\u8865\u7ed9\u83dc\u5355\u3002", true, 0);
                ResupplySessionManager.remember(session, packet.actionSeq(), closed);
                ResupplySessionManager.send(player, closed);
                return;
            }
            session.lastAccessTick = now;
            if (packet.entryIndex() < 0 || packet.entryIndex() >= session.entries.size()) {
                ResupplyEntryDeltaPacket result = ResupplySessionManager.failure(session, packet.actionSeq(), null, "\u8865\u7ed9\u6761\u76ee\u4e0d\u5b58\u5728\u3002", false, ResupplySessionManager.currentBalance(player, session));
                ResupplySessionManager.remember(session, packet.actionSeq(), result);
                ResupplySessionManager.send(player, result);
                return;
            }
            ResolvedSource source = ResupplySessionManager.resolveSource(player, session.source, session.accountId);
            if (source == null) {
                SESSIONS.remove(player.m_20148_());
                ResupplyEntryDeltaPacket closed = ResupplySessionManager.failure(session, packet.actionSeq(), null, "\u8865\u7ed9\u6765\u6e90\u5df2\u5931\u6548\u3001\u8ddd\u79bb\u8fc7\u8fdc\u6216\u4e0d\u518d\u5c5e\u4e8e\u5df1\u65b9\u3002", true, 0);
                ResupplySessionManager.remember(session, packet.actionSeq(), closed);
                ResupplySessionManager.send(player, closed);
                return;
            }
            EntrySpec spec = session.entries.get(packet.entryIndex());
            ActionKey actionKey = new ActionKey(player.m_20148_(), source.accountId(), session.classId, session.variantId, spec.index);
            long previousTick = LAST_ACTION_TICKS.getOrDefault(actionKey, -4611686018427387904L);
            if (now - previousTick < 1L) {
                ResupplyEntryDeltaPacket result = ResupplySessionManager.failure(session, packet.actionSeq(), ResupplySessionManager.view(player, source, spec), "\u64cd\u4f5c\u8fc7\u5feb\uff0c\u8bf7\u7a0d\u540e\u91cd\u8bd5\u3002", false, source.balance());
                ResupplySessionManager.remember(session, packet.actionSeq(), result);
                ResupplySessionManager.send(player, result);
                return;
            }
            LAST_ACTION_TICKS.put(actionKey, now);
            TransactionResult transaction = ResupplySessionManager.transact(player, source, spec);
            if (transaction.success) {
                ++session.stateRevision;
            }
            ResupplyCatalogPacket.Entry entry = ResupplySessionManager.view(player, source, spec);
            ResupplyEntryDeltaPacket result = new ResupplyEntryDeltaPacket(session.token, packet.actionSeq(), session.stateRevision, source.balance(), transaction.success, false, transaction.message, entry);
            ResupplySessionManager.remember(session, packet.actionSeq(), result);
            ResupplySessionManager.send(player, result);
        }
    }

    public static void close(UUID playerId, UUID token) {
        Session session = SESSIONS.get(playerId);
        if (session != null && session.token.equals(token)) {
            SESSIONS.remove(playerId);
        }
    }

    public static void clearPlayer(UUID playerId) {
        if (playerId == null) {
            return;
        }
        SESSIONS.remove(playerId);
        LAST_ACTION_TICKS.keySet().removeIf(key -> playerId.equals(key.playerId));
    }

    public static void clearAll() {
        SESSIONS.clear();
        LAST_ACTION_TICKS.clear();
        lastSweepTick = -4611686018427387904L;
    }

    public static int activeSessionCount() {
        return SESSIONS.size();
    }

    public static void tick(MinecraftServer server) {
        if (server == null || SESSIONS.isEmpty()) {
            return;
        }
        long sweepTick = server.m_129921_();
        if (sweepTick - lastSweepTick < 20L) {
            return;
        }
        lastSweepTick = sweepTick;
        Iterator<Map.Entry<UUID, Session>> iterator = SESSIONS.entrySet().iterator();
        while (iterator.hasNext()) {
            boolean invalid;
            Map.Entry<UUID, Session> entry2 = iterator.next();
            Session session = entry2.getValue();
            ServerPlayer player = server.m_6846_().m_11259_(entry2.getKey());
            long now = sweepTick;
            boolean expired = player == null || now - session.lastAccessTick > 600L;
            boolean bl = invalid = !expired && (!ResupplySessionManager.sameLoadout(player, session) || ResupplySessionManager.resolveSource(player, session.source, session.accountId) == null);
            if (!expired && !invalid) continue;
            iterator.remove();
            if (player == null) continue;
            ResupplySessionManager.send(player, new ResupplyEntryDeltaPacket(session.token, session.highestActionSeq, session.stateRevision, 0, false, true, expired ? "\u8865\u7ed9\u4f1a\u8bdd\u5df2\u8d85\u65f6\u3002" : "\u8865\u7ed9\u6765\u6e90\u6216\u804c\u4e1a\u5df2\u53d8\u5316\u3002", null));
        }
        LAST_ACTION_TICKS.entrySet().removeIf(entry -> sweepTick - (Long)entry.getValue() > 600L);
    }

    private static void sendCatalogue(ServerPlayer player, Session session, ResolvedSource source) {
        ArrayList<ResupplyCatalogPacket.Entry> entries = new ArrayList<ResupplyCatalogPacket.Entry>(session.entries.size());
        for (EntrySpec entry : session.entries) {
            entries.add(ResupplySessionManager.view(player, source, entry));
        }
        NetworkManager.NET.send(PacketDistributor.PLAYER.with(() -> player), (Object)new ResupplyCatalogPacket(session.token, session.catalogRevision, session.stateRevision, session.source, source.balance(), entries));
    }

    private static EntrySpec resolveEntry(int index, FactionDataLoader.ResupplyItem configured, FactionDataLoader.ResupplyData parent) {
        if (configured == null || configured.id == null) {
            return EntrySpec.unavailable(index, "", 1, 1, 1, "\u914d\u7f6e\u4e3a\u7a7a");
        }
        ResupplyItemIdentity.Configured identity = ResupplyItemIdentity.parse(configured.id, configured.nbt);
        String raw = identity.registryId();
        String nbt = identity.nbt();
        boolean exactTag = identity.exactTag();
        int cost = configured.ammoCost != null ? configured.ammoCost : (parent.ammoCost != null ? parent.ammoCost : LogisticsConfig.get().defaultResupplyAmmoCost);
        int count = Math.max(1, configured.count);
        int max = Math.max(count, configured.max);
        ResourceLocation id = ResourceLocation.m_135820_(raw);
        if (id == null) {
            return EntrySpec.unavailable(index, raw, count, max, cost, "\u7269\u54c1ID\u65e0\u6548");
        }
        Optional registered = BuiltInRegistries.f_257033_.m_6612_(id);
        if (registered.isEmpty()) {
            return EntrySpec.unavailable(index, raw, count, max, cost, "\u6240\u9700\u6a21\u7ec4\u6216\u7269\u54c1\u672a\u52a0\u8f7d");
        }
        ItemStack template = new ItemStack((ItemLike)registered.get());
        if (nbt != null && !nbt.isBlank()) {
            try {
                CompoundTag parsed = TagParser.m_129359_(nbt);
                template.m_41751_(parsed);
            }
            catch (CommandSyntaxException error) {
                return EntrySpec.unavailable(index, raw, count, max, cost, "\u7269\u54c1NBT\u65e0\u6548");
            }
        }
        MagazineCompat magazine = MagazineCompatProvider.get();
        Optional<MagazineCompat.Identity> magazineIdentity = magazine.identity(template);
        if ("taczmagazines".equals(id.m_135827_()) && magazineIdentity.isEmpty()) {
            return EntrySpec.unavailable(index, raw, count, max, cost, magazine.available() ? "\u5f39\u5323\u8eab\u4efd\u65e0\u6548" : "TaCZ Magazines\u672a\u52a0\u8f7d\u6216\u7248\u672c\u4e0d\u517c\u5bb9");
        }
        return new EntrySpec(index, raw, template, exactTag, count, max, Math.max(0, cost), magazineIdentity.orElse(null), "");
    }

    private static TransactionResult transact(ServerPlayer player, ResolvedSource source, EntrySpec spec) {
        boolean added;
        if (!spec.unavailableReason.isEmpty()) {
            return TransactionResult.fail(spec.unavailableReason);
        }
        if (source.balance() < spec.ammoCost) {
            return TransactionResult.fail("\u6765\u6e90\u5f39\u836f\u4e0d\u8db3");
        }
        if (spec.magazineIdentity != null) {
            return ResupplySessionManager.transactMagazine(player, source, spec);
        }
        int current = ResupplySessionManager.countNormal(player, spec);
        int wanted = AmmoResupplyPolicy.grantCount(current, spec.max, spec.count);
        int grant = Math.min(wanted, ResupplySessionManager.insertionCapacity(player, spec.template));
        if (grant <= 0) {
            return TransactionResult.fail(current >= spec.max ? "\u5df2\u8fbe\u5230\u8865\u7ed9\u4e0a\u9650" : "\u80cc\u5305\u6ca1\u6709\u7a7a\u95f4");
        }
        List<ItemStack> before = ResupplySessionManager.snapshotInventory(player);
        if (!source.consume(spec.ammoCost)) {
            return TransactionResult.fail("\u6765\u6e90\u5f39\u836f\u521a\u521a\u53d1\u751f\u53d8\u5316");
        }
        ItemStack inserted = spec.template.m_41777_();
        inserted.m_41764_(grant);
        boolean bl = added = player.m_150109_().m_36054_(inserted) && inserted.m_41619_();
        if (!added) {
            ResupplySessionManager.restoreInventory(player, before);
            source.refund(spec.ammoCost);
            return TransactionResult.fail("\u80cc\u5305\u53d8\u5316\u5bfc\u81f4\u4e8b\u52a1\u56de\u6eda");
        }
        ResupplySessionManager.finish(player, source);
        return TransactionResult.ok("\u5df2\u8865\u7ed9 " + spec.template.m_41786_().getString() + " \u00d7" + grant + "\uff0c\u6d88\u8017 " + spec.ammoCost + " \u5f39\u836f");
    }

    private static TransactionResult transactMagazine(ServerPlayer player, ResolvedSource source, EntrySpec spec) {
        boolean committed;
        MagazineCompat compat = MagazineCompatProvider.get();
        List<MagazineSlot> matching = ResupplySessionManager.matchingMagazines(player, spec, compat);
        int full = matching.stream().filter(MagazineSlot::full).mapToInt(slot -> slot.stack.m_41613_()).sum();
        if (full >= spec.max) {
            return TransactionResult.fail("\u6ee1\u5f39\u5323\u5df2\u8fbe\u5230\u4e0a\u9650");
        }
        ItemStack replacement = compat.createFull(spec.template);
        if (replacement.m_41619_()) {
            return TransactionResult.fail("\u65e0\u6cd5\u521b\u5efa\u8be5\u5f39\u5323");
        }
        MagazineSlot partial = matching.stream().filter(slot -> !slot.full).min(Comparator.comparingInt(MagazineSlot::rounds).thenComparingInt(MagazineSlot::slot)).orElse(null);
        if (partial == null && ResupplySessionManager.insertionCapacity(player, replacement) < 1) {
            return TransactionResult.fail("\u80cc\u5305\u6ca1\u6709\u7a7a\u95f4");
        }
        List<ItemStack> before = ResupplySessionManager.snapshotInventory(player);
        if (!source.consume(spec.ammoCost)) {
            return TransactionResult.fail("\u6765\u6e90\u5f39\u836f\u521a\u521a\u53d1\u751f\u53d8\u5316");
        }
        if (partial != null && partial.stack.m_41613_() == 1) {
            player.m_150109_().m_6836_(partial.slot, replacement.m_41777_());
            committed = true;
        } else {
            if (partial != null) {
                player.m_150109_().m_8020_(partial.slot).m_41774_(1);
            }
            ItemStack inserted = replacement.m_41777_();
            boolean bl = committed = player.m_150109_().m_36054_(inserted) && inserted.m_41619_();
        }
        if (!committed) {
            ResupplySessionManager.restoreInventory(player, before);
            source.refund(spec.ammoCost);
            return TransactionResult.fail("\u80cc\u5305\u53d8\u5316\u5bfc\u81f4\u5f39\u5323\u4e8b\u52a1\u56de\u6eda");
        }
        ResupplySessionManager.finish(player, source);
        return TransactionResult.ok(partial == null ? "\u5df2\u9886\u53d61\u4e2a\u6ee1\u5f39\u5323\uff0c\u6d88\u8017 " + spec.ammoCost + " \u5f39\u836f" : "\u5df2\u4e22\u5f03\u6700\u4f4e\u4f59\u5f39\u5e76\u66ff\u6362\u4e3a\u6ee1\u5f39\u5323\uff0c\u6d88\u8017 " + spec.ammoCost + " \u5f39\u836f");
    }

    private static void finish(ServerPlayer player, ResolvedSource source) {
        player.m_150109_().m_6596_();
        player.f_36095_.m_38946_();
        player.f_36096_.m_38946_();
        BastionManager.getInstance().recordResupply(player.m_20148_());
        source.notifyChanged();
    }

    private static ResupplyCatalogPacket.Entry view(ServerPlayer player, ResolvedSource source, EntrySpec spec) {
        int current = spec.magazineIdentity == null ? ResupplySessionManager.countNormal(player, spec) : ResupplySessionManager.countFullMagazines(player, spec, MagazineCompatProvider.get());
        String reason = spec.unavailableReason;
        boolean selectable = reason.isEmpty();
        if (selectable && current >= spec.max) {
            selectable = false;
            String string = reason = spec.magazineIdentity == null ? "\u5df2\u8fbe\u5230\u4e0a\u9650" : "\u6ee1\u5f39\u5323\u5df2\u8fbe\u5230\u4e0a\u9650";
        }
        if (selectable && source.balance() < spec.ammoCost) {
            selectable = false;
            reason = "\u6765\u6e90\u5f39\u836f\u4e0d\u8db3";
        }
        if (selectable) {
            if (spec.magazineIdentity == null && ResupplySessionManager.insertionCapacity(player, spec.template) <= 0) {
                selectable = false;
                reason = "\u80cc\u5305\u6ca1\u6709\u7a7a\u95f4";
            } else if (spec.magazineIdentity != null && !ResupplySessionManager.hasReplaceableMagazineOrSpace(player, spec, MagazineCompatProvider.get())) {
                selectable = false;
                reason = "\u80cc\u5305\u6ca1\u6709\u7a7a\u95f4";
            }
        }
        return new ResupplyCatalogPacket.Entry(spec.index, spec.template, spec.configuredId, spec.count, spec.max, spec.ammoCost, current, selectable, reason);
    }

    private static int countNormal(ServerPlayer player, EntrySpec spec) {
        int count = 0;
        for (ItemStack stack : player.m_150109_().f_35974_) {
            if (stack.m_41619_() || !ResupplyItemIdentity.matchesNormal(spec.exactTag, stack.m_150930_(spec.template.m_41720_()), ItemStack.m_150942_(stack, spec.template))) continue;
            count += stack.m_41613_();
        }
        return count;
    }

    private static int insertionCapacity(ServerPlayer player, ItemStack template) {
        int capacity = 0;
        for (ItemStack stack : player.m_150109_().f_35974_) {
            if (stack.m_41619_()) {
                capacity += template.m_41741_();
            } else if (ItemStack.m_150942_(stack, template)) {
                capacity += Math.max(0, stack.m_41741_() - stack.m_41613_());
            }
            if (capacity < 1000000) continue;
            return 1000000;
        }
        return capacity;
    }

    private static int countFullMagazines(ServerPlayer player, EntrySpec spec, MagazineCompat compat) {
        return ResupplySessionManager.matchingMagazines(player, spec, compat).stream().filter(MagazineSlot::full).mapToInt(slot -> slot.stack.m_41613_()).sum();
    }

    private static boolean hasReplaceableMagazineOrSpace(ServerPlayer player, EntrySpec spec, MagazineCompat compat) {
        List<MagazineSlot> matching = ResupplySessionManager.matchingMagazines(player, spec, compat);
        if (matching.stream().anyMatch(slot -> !slot.full && slot.stack.m_41613_() == 1)) {
            return true;
        }
        return ResupplySessionManager.insertionCapacity(player, compat.createFull(spec.template)) > 0;
    }

    private static List<MagazineSlot> matchingMagazines(ServerPlayer player, EntrySpec spec, MagazineCompat compat) {
        ArrayList<MagazineSlot> matches = new ArrayList<MagazineSlot>();
        for (int slot = 0; slot < player.m_150109_().f_35974_.size(); ++slot) {
            int rounds;
            ItemStack stack = player.m_150109_().f_35974_.get(slot);
            Optional<MagazineCompat.Identity> identity = compat.identity(stack);
            if (identity.isEmpty() || !identity.get().equals(spec.magazineIdentity)) continue;
            matches.add(new MagazineSlot(slot, stack, rounds, (rounds = compat.ammoCount(stack)) >= spec.magazineIdentity.capacity()));
        }
        return matches;
    }

    private static List<ItemStack> snapshotInventory(ServerPlayer player) {
        return player.m_150109_().f_35974_.stream().map(ItemStack::m_41777_).toList();
    }

    private static void restoreInventory(ServerPlayer player, List<ItemStack> snapshot) {
        for (int i = 0; i < snapshot.size(); ++i) {
            player.m_150109_().m_6836_(i, snapshot.get(i).m_41777_());
        }
        player.m_150109_().m_6596_();
    }

    private static boolean sameLoadout(ServerPlayer player, Session session) {
        String classId = ClassCountManager.getInstance().getPlayerClass(player.m_20148_());
        String variantId = ClassCountManager.getInstance().getPlayerVariant(player.m_20148_());
        return session.classId.equals(classId) && session.variantId.equals(variantId == null ? "" : variantId);
    }

    private static int currentBalance(ServerPlayer player, Session session) {
        ResolvedSource source = ResupplySessionManager.resolveSource(player, session.source, session.accountId);
        return source == null ? 0 : source.balance();
    }

    @Nullable
    private static ResolvedSource resolveSource(ServerPlayer player, ResupplySourceRef ref, @Nullable UUID expectedAccount) {
        if (ref.kind() == ResupplySourceRef.Kind.RADIO) {
            BastionData nearby = RadioRadialPacket.findFriendlyRadioNearby(player, ref.blockPos());
            if (nearby == null || expectedAccount != null && !expectedAccount.equals(nearby.getBastionId())) {
                return null;
            }
            return new ResolvedSource(nearby.getBastionId(), nearby::getAmmunitionSupplies, nearby::consumeAmmunitionSupplies, amount -> nearby.addAmmunitionSupplies(amount, LogisticsConfig.get().maxAmmunition), () -> FobSupplyTracker.notifySupplyChanged(nearby));
        }
        VehicleSupplyActionPacket.Interaction interaction = VehicleSupplyActionPacket.resolveInteraction(player, ref.entityId());
        if (interaction == null || expectedAccount != null && !expectedAccount.equals(interaction.vehicleId())) {
            return null;
        }
        return new ResolvedSource(interaction.vehicleId(), interaction.supply()::getAmmo, amount -> interaction.supply().removeAmmo(amount) == amount, amount -> interaction.supply().addAmmo(amount), () -> {});
    }

    private static ResupplyEntryDeltaPacket failure(Session session, long seq, @Nullable ResupplyCatalogPacket.Entry entry, String message, boolean close, int balance) {
        return new ResupplyEntryDeltaPacket(session.token, seq, session.stateRevision, Math.max(0, balance), false, close, message, entry);
    }

    private static void sendClosed(ServerPlayer player, UUID token, long seq, String message) {
        ResupplySessionManager.send(player, new ResupplyEntryDeltaPacket(token, seq, 0L, 0, false, true, message, null));
    }

    private static void remember(Session session, long seq, ResupplyEntryDeltaPacket result) {
        session.results.put(seq, result);
    }

    private static void send(ServerPlayer player, ResupplyEntryDeltaPacket packet) {
        NetworkManager.NET.send(PacketDistributor.PLAYER.with(() -> player), (Object)packet);
    }

    private record ResolvedSource(UUID accountId, IntValue balanceValue, IntAction consumeAction, RefundAction refundAction, Runnable changeNotifier) {
        int balance() {
            return Math.max(0, this.balanceValue.get());
        }

        boolean consume(int amount) {
            return amount == 0 || this.consumeAction.apply(amount);
        }

        void refund(int amount) {
            if (amount > 0) {
                this.refundAction.apply(amount);
            }
        }

        void notifyChanged() {
            this.changeNotifier.run();
        }
    }

    private record EntrySpec(int index, String configuredId, ItemStack template, boolean exactTag, int count, int max, int ammoCost, @Nullable MagazineCompat.Identity magazineIdentity, String unavailableReason) {
        static EntrySpec unavailable(int index, String id, int count, int max, int cost, String reason) {
            return new EntrySpec(index, id, ItemStack.f_41583_, false, count, max, Math.max(0, cost), null, reason);
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
        private final LinkedHashMap<Long, ResupplyEntryDeltaPacket> results = new LinkedHashMap<Long, ResupplyEntryDeltaPacket>(65, 0.75f, true){

            @Override
            protected boolean removeEldestEntry(Map.Entry<Long, ResupplyEntryDeltaPacket> eldest) {
                return this.size() > 64;
            }
        };

        private Session(UUID playerId, UUID token, long catalogRevision, ResupplySourceRef source, UUID accountId, String classId, String variantId, List<EntrySpec> entries, long lastAccessTick) {
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

    private record ActionKey(UUID playerId, UUID accountId, String classId, String variantId, int entryIndex) {
    }

    private record TransactionResult(boolean success, String message) {
        static TransactionResult ok(String message) {
            return new TransactionResult(true, message);
        }

        static TransactionResult fail(String message) {
            return new TransactionResult(false, message);
        }
    }

    private record MagazineSlot(int slot, ItemStack stack, int rounds, boolean full) {
    }

    @FunctionalInterface
    private static interface IntValue {
        public int get();
    }

    @FunctionalInterface
    private static interface IntAction {
        public boolean apply(int var1);
    }

    @FunctionalInterface
    private static interface RefundAction {
        public void apply(int var1);
    }
}

