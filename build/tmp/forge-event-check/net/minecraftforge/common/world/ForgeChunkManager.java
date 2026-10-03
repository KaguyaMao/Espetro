/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.longs.LongCollection
 *  it.unimi.dsi.fastutil.longs.LongIterator
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  it.unimi.dsi.fastutil.longs.LongSet
 *  javax.annotation.ParametersAreNonnullByDefault
 *  net.minecraft.core.BlockPos
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.NbtUtils
 *  net.minecraft.nbt.Tag
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.TicketType
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.ForcedChunksSavedData
 *  net.minecraftforge.fml.ModList
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraftforge.common.world;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ForcedChunksSavedData;
import net.minecraftforge.fml.ModList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ParametersAreNonnullByDefault
public class ForgeChunkManager {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final TicketType<TicketOwner<BlockPos>> BLOCK = TicketType.m_9462_((String)"forge:block", Comparator.comparing(info -> info));
    private static final TicketType<TicketOwner<BlockPos>> BLOCK_TICKING = TicketType.m_9462_((String)"forge:block_ticking", Comparator.comparing(info -> info));
    private static final TicketType<TicketOwner<UUID>> ENTITY = TicketType.m_9462_((String)"forge:entity", Comparator.comparing(info -> info));
    private static final TicketType<TicketOwner<UUID>> ENTITY_TICKING = TicketType.m_9462_((String)"forge:entity_ticking", Comparator.comparing(info -> info));
    private static final Map<String, LoadingValidationCallback> callbacks = new HashMap<String, LoadingValidationCallback>();

    public static void setForcedChunkLoadingCallback(String modId, LoadingValidationCallback callback) {
        if (ModList.get().isLoaded(modId)) {
            callbacks.put(modId, callback);
        } else {
            LOGGER.warn("A mod attempted to set the forced chunk validation loading callback for an unloaded mod of id: {}", (Object)modId);
        }
    }

    public static boolean hasForcedChunks(ServerLevel level) {
        ForcedChunksSavedData data = (ForcedChunksSavedData)level.m_8895_().m_164858_(ForcedChunksSavedData::m_151483_, "chunks");
        if (data == null) {
            return false;
        }
        return !data.m_46116_().isEmpty() || !data.getBlockForcedChunks().isEmpty() || !data.getEntityForcedChunks().isEmpty();
    }

    public static boolean forceChunk(ServerLevel level, String modId, BlockPos owner, int chunkX, int chunkZ, boolean add, boolean ticking) {
        return ForgeChunkManager.forceChunk(level, modId, owner, chunkX, chunkZ, add, ticking, ticking ? BLOCK_TICKING : BLOCK, ForcedChunksSavedData::getBlockForcedChunks);
    }

    public static boolean forceChunk(ServerLevel level, String modId, Entity owner, int chunkX, int chunkZ, boolean add, boolean ticking) {
        return ForgeChunkManager.forceChunk(level, modId, owner.m_20148_(), chunkX, chunkZ, add, ticking);
    }

    public static boolean forceChunk(ServerLevel level, String modId, UUID owner, int chunkX, int chunkZ, boolean add, boolean ticking) {
        return ForgeChunkManager.forceChunk(level, modId, owner, chunkX, chunkZ, add, ticking, ticking ? ENTITY_TICKING : ENTITY, ForcedChunksSavedData::getEntityForcedChunks);
    }

    private static <T extends Comparable<? super T>> boolean forceChunk(ServerLevel level, String modId, T owner, int chunkX, int chunkZ, boolean add, boolean ticking, TicketType<TicketOwner<T>> type, Function<ForcedChunksSavedData, TicketTracker<T>> ticketGetter) {
        boolean success;
        if (!ModList.get().isLoaded(modId)) {
            LOGGER.warn("A mod attempted to force a chunk for an unloaded mod of id: {}", (Object)modId);
            return false;
        }
        ForcedChunksSavedData saveData = (ForcedChunksSavedData)level.m_8895_().m_164861_(ForcedChunksSavedData::m_151483_, ForcedChunksSavedData::new, "chunks");
        ChunkPos pos = new ChunkPos(chunkX, chunkZ);
        long chunk = pos.m_45588_();
        TicketTracker<T> tickets = ticketGetter.apply(saveData);
        TicketOwner<T> ticketOwner = new TicketOwner<T>(modId, owner);
        if (add) {
            success = tickets.add(ticketOwner, chunk, ticking);
            if (success) {
                level.m_6325_(chunkX, chunkZ);
            }
        } else {
            success = tickets.remove(ticketOwner, chunk, ticking);
        }
        if (success) {
            saveData.m_77760_(true);
            ForgeChunkManager.forceChunk(level, pos, type, ticketOwner, add, ticking);
        }
        return success;
    }

    private static <T extends Comparable<? super T>> void forceChunk(ServerLevel level, ChunkPos pos, TicketType<TicketOwner<T>> type, TicketOwner<T> owner, boolean add, boolean ticking) {
        if (add) {
            level.m_7726_().addRegionTicket(type, pos, 2, owner, ticking);
        } else {
            level.m_7726_().removeRegionTicket(type, pos, 2, owner, ticking);
        }
    }

    public static void reinstatePersistentChunks(ServerLevel level, ForcedChunksSavedData saveData) {
        if (!callbacks.isEmpty()) {
            Map blockTickets = ForgeChunkManager.gatherTicketsByModId(saveData.getBlockForcedChunks());
            Map entityTickets = ForgeChunkManager.gatherTicketsByModId(saveData.getEntityForcedChunks());
            for (Map.Entry<String, LoadingValidationCallback> entry : callbacks.entrySet()) {
                String modId = entry.getKey();
                boolean hasBlockTicket = blockTickets.containsKey(modId);
                boolean hasEntityTicket = entityTickets.containsKey(modId);
                if (!hasBlockTicket && !hasEntityTicket) continue;
                Map<BlockPos, Pair<LongSet, LongSet>> ownedBlockTickets = hasBlockTicket ? Collections.unmodifiableMap(blockTickets.get(modId)) : Collections.emptyMap();
                Map<UUID, Pair<LongSet, LongSet>> ownedEntityTickets = hasEntityTicket ? Collections.unmodifiableMap(entityTickets.get(modId)) : Collections.emptyMap();
                entry.getValue().validateTickets(level, new TicketHelper(saveData, modId, ownedBlockTickets, ownedEntityTickets));
            }
        }
        ForgeChunkManager.reinstatePersistentChunks(level, BLOCK, saveData.getBlockForcedChunks().chunks, false);
        ForgeChunkManager.reinstatePersistentChunks(level, BLOCK_TICKING, saveData.getBlockForcedChunks().tickingChunks, true);
        ForgeChunkManager.reinstatePersistentChunks(level, ENTITY, saveData.getEntityForcedChunks().chunks, false);
        ForgeChunkManager.reinstatePersistentChunks(level, ENTITY_TICKING, saveData.getEntityForcedChunks().tickingChunks, true);
    }

    private static <T extends Comparable<? super T>> Map<String, Map<T, Pair<LongSet, LongSet>>> gatherTicketsByModId(TicketTracker<T> tickets) {
        HashMap<String, Map<T, Pair<LongSet, LongSet>>> modSortedOwnedChunks = new HashMap<String, Map<T, Pair<LongSet, LongSet>>>();
        ForgeChunkManager.gatherTicketsByModId(tickets.chunks, Pair::getFirst, modSortedOwnedChunks);
        ForgeChunkManager.gatherTicketsByModId(tickets.tickingChunks, Pair::getSecond, modSortedOwnedChunks);
        return modSortedOwnedChunks;
    }

    private static <T extends Comparable<? super T>> void gatherTicketsByModId(Map<TicketOwner<T>, LongSet> tickets, Function<Pair<LongSet, LongSet>, LongSet> typeGetter, Map<String, Map<T, Pair<LongSet, LongSet>>> modSortedOwnedChunks) {
        for (Map.Entry<TicketOwner<T>, LongSet> entry : tickets.entrySet()) {
            Pair pair = modSortedOwnedChunks.computeIfAbsent(entry.getKey().modId, modId -> new HashMap()).computeIfAbsent(entry.getKey().owner, owner -> new Pair((Object)new LongOpenHashSet(), (Object)new LongOpenHashSet()));
            typeGetter.apply((Pair<LongSet, LongSet>)pair).addAll((LongCollection)entry.getValue());
        }
    }

    private static <T extends Comparable<? super T>> void reinstatePersistentChunks(ServerLevel level, TicketType<TicketOwner<T>> type, Map<TicketOwner<T>, LongSet> tickets, boolean ticking) {
        for (Map.Entry<TicketOwner<T>, LongSet> entry : tickets.entrySet()) {
            LongIterator longIterator = entry.getValue().iterator();
            while (longIterator.hasNext()) {
                long chunk = (Long)longIterator.next();
                ForgeChunkManager.forceChunk(level, new ChunkPos(chunk), type, entry.getKey(), true, ticking);
            }
        }
    }

    public static void writeForgeForcedChunks(CompoundTag nbt, TicketTracker<BlockPos> blockForcedChunks, TicketTracker<UUID> entityForcedChunks) {
        if (!blockForcedChunks.isEmpty() || !entityForcedChunks.isEmpty()) {
            HashMap<String, Long2ObjectMap<CompoundTag>> forcedEntries = new HashMap<String, Long2ObjectMap<CompoundTag>>();
            ForgeChunkManager.writeForcedChunkOwners(forcedEntries, blockForcedChunks, "Blocks", 10, (T pos, ListTag forcedBlocks) -> forcedBlocks.add((Object)NbtUtils.m_129224_((BlockPos)pos)));
            ForgeChunkManager.writeForcedChunkOwners(forcedEntries, entityForcedChunks, "Entities", 11, (T uuid, ListTag forcedEntities) -> forcedEntities.add((Object)NbtUtils.m_129226_((UUID)uuid)));
            ListTag forcedChunks = new ListTag();
            for (Map.Entry entry : forcedEntries.entrySet()) {
                CompoundTag forcedEntry = new CompoundTag();
                forcedEntry.m_128359_("Mod", (String)entry.getKey());
                ListTag modForced = new ListTag();
                modForced.addAll((Collection)((Long2ObjectMap)entry.getValue()).values());
                forcedEntry.m_128365_("ModForced", (Tag)modForced);
                forcedChunks.add((Object)forcedEntry);
            }
            nbt.m_128365_("ForgeForced", (Tag)forcedChunks);
        }
    }

    private static <T extends Comparable<? super T>> void writeForcedChunkOwners(Map<String, Long2ObjectMap<CompoundTag>> forcedEntries, TicketTracker<T> tracker, String listKey, int listType, BiConsumer<T, ListTag> ownerWriter) {
        ForgeChunkManager.writeForcedChunkOwners(forcedEntries, tracker.chunks, listKey, listType, ownerWriter);
        ForgeChunkManager.writeForcedChunkOwners(forcedEntries, tracker.tickingChunks, "Ticking" + listKey, listType, ownerWriter);
    }

    private static <T extends Comparable<? super T>> void writeForcedChunkOwners(Map<String, Long2ObjectMap<CompoundTag>> forcedEntries, Map<TicketOwner<T>, LongSet> forcedChunks, String listKey, int listType, BiConsumer<T, ListTag> ownerWriter) {
        for (Map.Entry<TicketOwner<T>, LongSet> entry : forcedChunks.entrySet()) {
            Long2ObjectMap modForced = forcedEntries.computeIfAbsent(entry.getKey().modId, modId -> new Long2ObjectOpenHashMap());
            LongIterator longIterator = entry.getValue().iterator();
            while (longIterator.hasNext()) {
                long chunk = (Long)longIterator.next();
                CompoundTag modEntry = (CompoundTag)modForced.computeIfAbsent(chunk, chunkPos -> {
                    CompoundTag baseEntry = new CompoundTag();
                    baseEntry.m_128356_("Chunk", chunkPos);
                    return baseEntry;
                });
                ListTag ownerList = modEntry.m_128437_(listKey, listType);
                ownerWriter.accept(entry.getKey().owner, ownerList);
                modEntry.m_128365_(listKey, (Tag)ownerList);
            }
        }
    }

    public static void readForgeForcedChunks(CompoundTag nbt, TicketTracker<BlockPos> blockForcedChunks, TicketTracker<UUID> entityForcedChunks) {
        ListTag forcedChunks = nbt.m_128437_("ForgeForced", 10);
        for (int i = 0; i < forcedChunks.size(); ++i) {
            CompoundTag forcedEntry = forcedChunks.m_128728_(i);
            String modId = forcedEntry.m_128461_("Mod");
            if (ModList.get().isLoaded(modId)) {
                ListTag modForced = forcedEntry.m_128437_("ModForced", 10);
                for (int j = 0; j < modForced.size(); ++j) {
                    CompoundTag modEntry = modForced.m_128728_(j);
                    long chunkPos = modEntry.m_128454_("Chunk");
                    ForgeChunkManager.readBlockForcedChunks(modId, chunkPos, modEntry, "Blocks", blockForcedChunks.chunks);
                    ForgeChunkManager.readBlockForcedChunks(modId, chunkPos, modEntry, "TickingBlocks", blockForcedChunks.tickingChunks);
                    ForgeChunkManager.readEntityForcedChunks(modId, chunkPos, modEntry, "Entities", entityForcedChunks.chunks);
                    ForgeChunkManager.readEntityForcedChunks(modId, chunkPos, modEntry, "TickingEntities", entityForcedChunks.tickingChunks);
                }
                continue;
            }
            LOGGER.warn("Found chunk loading data for mod {} which is currently not available or active - it will be removed from the level save.", (Object)modId);
        }
    }

    private static void readBlockForcedChunks(String modId, long chunkPos, CompoundTag modEntry, String key, Map<TicketOwner<BlockPos>, LongSet> blockForcedChunks) {
        ListTag forcedBlocks = modEntry.m_128437_(key, 10);
        for (int k = 0; k < forcedBlocks.size(); ++k) {
            blockForcedChunks.computeIfAbsent(new TicketOwner<BlockPos>(modId, NbtUtils.m_129239_((CompoundTag)forcedBlocks.m_128728_(k))), owner -> new LongOpenHashSet()).add(chunkPos);
        }
    }

    private static void readEntityForcedChunks(String modId, long chunkPos, CompoundTag modEntry, String key, Map<TicketOwner<UUID>, LongSet> entityForcedChunks) {
        ListTag forcedEntities = modEntry.m_128437_(key, 11);
        for (Tag uuid : forcedEntities) {
            entityForcedChunks.computeIfAbsent(new TicketOwner<UUID>(modId, NbtUtils.m_129233_((Tag)uuid)), owner -> new LongOpenHashSet()).add(chunkPos);
        }
    }

    public static class TicketTracker<T extends Comparable<? super T>> {
        private final Map<TicketOwner<T>, LongSet> chunks = new HashMap<TicketOwner<T>, LongSet>();
        private final Map<TicketOwner<T>, LongSet> tickingChunks = new HashMap<TicketOwner<T>, LongSet>();

        public Map<TicketOwner<T>, LongSet> getChunks() {
            return Collections.unmodifiableMap(this.chunks);
        }

        public Map<TicketOwner<T>, LongSet> getTickingChunks() {
            return Collections.unmodifiableMap(this.tickingChunks);
        }

        public boolean isEmpty() {
            return this.chunks.isEmpty() && this.tickingChunks.isEmpty();
        }

        private Map<TicketOwner<T>, LongSet> getTickets(boolean ticking) {
            return ticking ? this.tickingChunks : this.chunks;
        }

        private boolean remove(TicketOwner<T> owner, long chunk, boolean ticking) {
            LongSet ticketChunks;
            Map<TicketOwner<T>, LongSet> tickets = this.getTickets(ticking);
            if (tickets.containsKey(owner) && (ticketChunks = tickets.get(owner)).remove(chunk)) {
                if (ticketChunks.isEmpty()) {
                    tickets.remove(owner);
                }
                return true;
            }
            return false;
        }

        private boolean add(TicketOwner<T> owner, long chunk, boolean ticking) {
            return this.getTickets(ticking).computeIfAbsent(owner, o -> new LongOpenHashSet()).add(chunk);
        }
    }

    public static class TicketOwner<T extends Comparable<? super T>>
    implements Comparable<TicketOwner<T>> {
        private final String modId;
        private final T owner;

        private TicketOwner(String modId, T owner) {
            this.modId = modId;
            this.owner = owner;
        }

        @Override
        public int compareTo(TicketOwner<T> other) {
            int res = this.modId.compareTo(other.modId);
            return res == 0 ? this.owner.compareTo(other.owner) : res;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            TicketOwner that = (TicketOwner)o;
            return Objects.equals(this.modId, that.modId) && Objects.equals(this.owner, that.owner);
        }

        public int hashCode() {
            return Objects.hash(this.modId, this.owner);
        }
    }

    @FunctionalInterface
    public static interface LoadingValidationCallback {
        public void validateTickets(ServerLevel var1, TicketHelper var2);
    }

    public static class TicketHelper {
        private final Map<BlockPos, Pair<LongSet, LongSet>> blockTickets;
        private final Map<UUID, Pair<LongSet, LongSet>> entityTickets;
        private final ForcedChunksSavedData saveData;
        private final String modId;

        private TicketHelper(ForcedChunksSavedData saveData, String modId, Map<BlockPos, Pair<LongSet, LongSet>> blockTickets, Map<UUID, Pair<LongSet, LongSet>> entityTickets) {
            this.saveData = saveData;
            this.modId = modId;
            this.blockTickets = blockTickets;
            this.entityTickets = entityTickets;
        }

        public Map<BlockPos, Pair<LongSet, LongSet>> getBlockTickets() {
            return this.blockTickets;
        }

        public Map<UUID, Pair<LongSet, LongSet>> getEntityTickets() {
            return this.entityTickets;
        }

        public void removeAllTickets(BlockPos owner) {
            this.removeAllTickets(this.saveData.getBlockForcedChunks(), owner);
        }

        public void removeAllTickets(UUID owner) {
            this.removeAllTickets(this.saveData.getEntityForcedChunks(), owner);
        }

        private <T extends Comparable<? super T>> void removeAllTickets(TicketTracker<T> tickets, T owner) {
            TicketOwner<T> ticketOwner = new TicketOwner<T>(this.modId, owner);
            if (tickets.chunks.containsKey(ticketOwner) || tickets.tickingChunks.containsKey(ticketOwner)) {
                tickets.chunks.remove(ticketOwner);
                tickets.tickingChunks.remove(ticketOwner);
                this.saveData.m_77760_(true);
            }
        }

        public void removeTicket(BlockPos owner, long chunk, boolean ticking) {
            this.removeTicket(this.saveData.getBlockForcedChunks(), owner, chunk, ticking);
        }

        public void removeTicket(UUID owner, long chunk, boolean ticking) {
            this.removeTicket(this.saveData.getEntityForcedChunks(), owner, chunk, ticking);
        }

        private <T extends Comparable<? super T>> void removeTicket(TicketTracker<T> tickets, T owner, long chunk, boolean ticking) {
            if (tickets.remove(new TicketOwner<T>(this.modId, owner), chunk, ticking)) {
                this.saveData.m_77760_(true);
            }
        }
    }
}

