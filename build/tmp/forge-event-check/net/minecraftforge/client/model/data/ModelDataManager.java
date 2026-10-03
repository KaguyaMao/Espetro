/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.client.model.data;

import com.google.common.base.Preconditions;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Mod.EventBusSubscriber(modid="forge", bus=Mod.EventBusSubscriber.Bus.FORGE, value={Dist.CLIENT})
@ApiStatus.Internal
public class ModelDataManager {
    private final Level level;
    private final Map<ChunkPos, Set<BlockPos>> needModelDataRefresh = new ConcurrentHashMap<ChunkPos, Set<BlockPos>>();
    private final Map<ChunkPos, Map<BlockPos, ModelData>> modelDataCache = new ConcurrentHashMap<ChunkPos, Map<BlockPos, ModelData>>();

    public ModelDataManager(Level level) {
        this.level = level;
    }

    public void requestRefresh(@NotNull BlockEntity blockEntity) {
        Preconditions.checkNotNull((Object)blockEntity, (Object)"Block entity must not be null");
        this.needModelDataRefresh.computeIfAbsent(new ChunkPos(blockEntity.m_58899_()), $ -> Collections.synchronizedSet(new HashSet())).add(blockEntity.m_58899_());
    }

    private void refreshAt(ChunkPos chunk) {
        Set<BlockPos> needUpdate = this.needModelDataRefresh.remove(chunk);
        if (needUpdate != null) {
            Map data = this.modelDataCache.computeIfAbsent(chunk, $ -> new ConcurrentHashMap());
            for (BlockPos pos : needUpdate) {
                BlockEntity toUpdate = this.level.m_7702_(pos);
                if (toUpdate != null && !toUpdate.m_58901_()) {
                    data.put(pos, toUpdate.getModelData());
                    continue;
                }
                data.remove(pos);
            }
        }
    }

    @Nullable
    public ModelData getAt(BlockPos pos) {
        return this.getAt(new ChunkPos(pos)).get(pos);
    }

    public Map<BlockPos, ModelData> getAt(ChunkPos pos) {
        Preconditions.checkArgument((boolean)this.level.f_46443_, (Object)"Cannot request model data for server level");
        this.refreshAt(pos);
        return this.modelDataCache.getOrDefault(pos, Collections.emptyMap());
    }

    @SubscribeEvent
    public static void onChunkUnload(ChunkEvent.Unload event) {
        LevelAccessor level = event.getChunk().getWorldForge();
        if (level == null) {
            return;
        }
        ModelDataManager modelDataManager = level.getModelDataManager();
        if (modelDataManager == null) {
            return;
        }
        ChunkPos chunk = event.getChunk().m_7697_();
        modelDataManager.needModelDataRefresh.remove(chunk);
        modelDataManager.modelDataCache.remove(chunk);
    }
}

