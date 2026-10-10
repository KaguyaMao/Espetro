/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.architectury.hooks.level.entity.PlayerHooks
 *  dev.latvian.mods.rhino.util.SpecialEquality
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LightningBolt
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.LightLayer
 *  net.minecraft.world.level.biome.Biomes
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.AABB
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.level;

import dev.architectury.hooks.level.entity.PlayerHooks;
import dev.latvian.mods.kubejs.core.InventoryKJS;
import dev.latvian.mods.kubejs.level.ExplosionJS;
import dev.latvian.mods.kubejs.level.FireworksJS;
import dev.latvian.mods.kubejs.platform.LevelPlatformHelper;
import dev.latvian.mods.kubejs.player.EntityArrayList;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.util.Tags;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.util.SpecialEquality;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

public class BlockContainerJS
implements SpecialEquality {
    private static final ResourceLocation AIR_ID = new ResourceLocation("minecraft:air");
    public final Level minecraftLevel;
    private final BlockPos pos;
    public transient BlockState cachedState;
    public transient BlockEntity cachedEntity;

    public BlockContainerJS(Level w, BlockPos p) {
        this.minecraftLevel = w;
        this.pos = p;
    }

    public BlockContainerJS(BlockEntity blockEntity) {
        this.minecraftLevel = blockEntity.m_58904_();
        this.pos = blockEntity.m_58899_();
        this.cachedEntity = blockEntity;
    }

    public void clearCache() {
        this.cachedState = null;
        this.cachedEntity = null;
    }

    public Level getLevel() {
        return this.minecraftLevel;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public ResourceLocation getDimension() {
        return this.minecraftLevel.m_46472_().m_135782_();
    }

    public int getX() {
        return this.getPos().m_123341_();
    }

    public int getY() {
        return this.getPos().m_123342_();
    }

    public int getZ() {
        return this.getPos().m_123343_();
    }

    public BlockContainerJS offset(Direction f, int d) {
        return new BlockContainerJS(this.minecraftLevel, this.getPos().m_5484_(f, d));
    }

    public BlockContainerJS offset(Direction f) {
        return this.offset(f, 1);
    }

    public BlockContainerJS offset(int x, int y, int z) {
        return new BlockContainerJS(this.minecraftLevel, this.getPos().m_7918_(x, y, z));
    }

    public BlockContainerJS getDown() {
        return this.offset(Direction.DOWN);
    }

    public BlockContainerJS getUp() {
        return this.offset(Direction.UP);
    }

    public BlockContainerJS getNorth() {
        return this.offset(Direction.NORTH);
    }

    public BlockContainerJS getSouth() {
        return this.offset(Direction.SOUTH);
    }

    public BlockContainerJS getWest() {
        return this.offset(Direction.WEST);
    }

    public BlockContainerJS getEast() {
        return this.offset(Direction.EAST);
    }

    public BlockState getBlockState() {
        if (this.cachedState == null) {
            this.cachedState = this.minecraftLevel.m_8055_(this.getPos());
        }
        return this.cachedState;
    }

    public void setBlockState(BlockState state, int flags) {
        this.minecraftLevel.m_7731_(this.getPos(), state, flags);
        this.clearCache();
        this.cachedState = state;
    }

    public String getId() {
        return RegistryInfo.BLOCK.getId(this.getBlockState().m_60734_()).toString();
    }

    public Collection<ResourceLocation> getTags() {
        return Tags.byBlockState(this.getBlockState()).map(TagKey::f_203868_).collect(Collectors.toSet());
    }

    public boolean hasTag(ResourceLocation tag) {
        return this.getBlockState().m_204336_(Tags.block(tag));
    }

    public void set(ResourceLocation id, Map<?, ?> properties, int flags) {
        Block block = RegistryInfo.BLOCK.getValue(id);
        BlockState state = block.m_49966_();
        if (!properties.isEmpty() && state.m_60734_() != Blocks.f_50016_) {
            HashMap<String, Property> pmap = new HashMap<String, Property>();
            for (Property property : state.m_61147_()) {
                pmap.put(property.m_61708_(), property);
            }
            for (Map.Entry entry : properties.entrySet()) {
                Property property = (Property)pmap.get(String.valueOf(entry.getKey()));
                if (property == null) continue;
                state = (BlockState)state.m_61124_(property, (Comparable)UtilsJS.cast(property.m_6215_(String.valueOf(entry.getValue())).orElseThrow()));
            }
        }
        this.setBlockState(state, flags);
    }

    public void set(ResourceLocation id, Map<?, ?> properties) {
        this.set(id, properties, 3);
    }

    public void set(ResourceLocation id) {
        this.set(id, Collections.emptyMap());
    }

    public Map<String, String> getProperties() {
        HashMap<String, String> map = new HashMap<String, String>();
        BlockState state = this.getBlockState();
        for (Property property : state.m_61147_()) {
            map.put(property.m_61708_(), property.m_6940_(state.m_61143_(property)));
        }
        return map;
    }

    @Nullable
    public BlockEntity getEntity() {
        if (this.cachedEntity == null || this.cachedEntity.m_58901_()) {
            this.cachedEntity = this.minecraftLevel.m_7702_(this.pos);
        }
        return this.cachedEntity;
    }

    public String getEntityId() {
        BlockEntity entity = this.getEntity();
        return entity == null ? "minecraft:air" : RegistryInfo.BLOCK_ENTITY_TYPE.getId(entity.m_58903_()).toString();
    }

    @Nullable
    public CompoundTag getEntityData() {
        BlockEntity entity = this.getEntity();
        if (entity != null) {
            return entity.m_187480_();
        }
        return null;
    }

    public void setEntityData(@Nullable CompoundTag tag) {
        BlockEntity entity;
        if (tag != null && (entity = this.getEntity()) != null) {
            entity.m_142466_(tag);
        }
    }

    public void mergeEntityData(@Nullable CompoundTag tag) {
        CompoundTag t = this.getEntityData();
        if (t == null) {
            this.setEntityData(tag);
        } else if (tag != null && !tag.m_128456_()) {
            for (String s : tag.m_128431_()) {
                t.m_128365_(s, tag.m_128423_(s));
            }
        }
        this.setEntityData(t);
    }

    public int getLight() {
        return this.minecraftLevel.m_46803_(this.pos);
    }

    public int getSkyLight() {
        return this.minecraftLevel.m_45517_(LightLayer.SKY, this.pos) - this.minecraftLevel.m_7445_();
    }

    public int getBlockLight() {
        return this.minecraftLevel.m_45517_(LightLayer.BLOCK, this.pos);
    }

    public boolean getCanSeeSky() {
        return this.minecraftLevel.m_45527_(this.pos);
    }

    public boolean canSeeSkyFromBelowWater() {
        return this.minecraftLevel.m_46861_(this.pos);
    }

    public String toString() {
        String id = this.getId();
        Map<String, String> properties = this.getProperties();
        if (properties.isEmpty()) {
            return id;
        }
        StringBuilder builder = new StringBuilder(id);
        builder.append('[');
        boolean first = true;
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            if (first) {
                first = false;
            } else {
                builder.append(',');
            }
            builder.append(entry.getKey());
            builder.append('=');
            builder.append(entry.getValue());
        }
        builder.append(']');
        return builder.toString();
    }

    public ExplosionJS createExplosion() {
        return new ExplosionJS((LevelAccessor)this.minecraftLevel, (double)this.getX() + 0.5, (double)this.getY() + 0.5, (double)this.getZ() + 0.5);
    }

    @Nullable
    public Entity createEntity(EntityType<?> type) {
        Entity entity = this.getLevel().kjs$createEntity(type);
        if (entity != null) {
            entity.kjs$setPosition(this);
        }
        return entity;
    }

    public void spawnLightning(boolean effectOnly, @Nullable ServerPlayer player) {
        if (this.minecraftLevel instanceof ServerLevel) {
            LightningBolt e = (LightningBolt)EntityType.f_20465_.m_20615_(this.minecraftLevel);
            e.m_6027_((double)this.getX() + 0.5, (double)this.getY() + 0.5, (double)this.getZ() + 0.5);
            e.m_20879_(player);
            e.m_20874_(effectOnly);
            this.minecraftLevel.m_7967_((Entity)e);
        }
    }

    public void spawnLightning(boolean effectOnly) {
        this.spawnLightning(effectOnly, null);
    }

    public void spawnLightning() {
        this.spawnLightning(false);
    }

    public void spawnFireworks(FireworksJS fireworks) {
        this.minecraftLevel.m_7967_((Entity)fireworks.createFireworkRocket(this.minecraftLevel, (double)this.getX() + 0.5, (double)this.getY() + 0.5, (double)this.getZ() + 0.5));
    }

    @Nullable
    public InventoryKJS getInventory() {
        return this.getInventory(Direction.UP);
    }

    @Nullable
    public InventoryKJS getInventory(Direction facing) {
        BlockEntity entity = this.getEntity();
        if (entity != null) {
            InventoryKJS c = LevelPlatformHelper.get().getInventoryFromBlockEntity(entity, facing);
            if (c != null) {
                return c;
            }
            if (entity instanceof InventoryKJS) {
                InventoryKJS inv = (InventoryKJS)entity;
                return inv;
            }
        }
        return null;
    }

    public ItemStack getItem() {
        BlockState state = this.getBlockState();
        return state.m_60734_().m_7397_((BlockGetter)this.minecraftLevel, this.pos, state);
    }

    public List<ItemStack> getDrops() {
        return this.getDrops(null, ItemStack.f_41583_);
    }

    public List<ItemStack> getDrops(@Nullable Entity entity, ItemStack heldItem) {
        Level level = this.minecraftLevel;
        if (level instanceof ServerLevel) {
            ServerLevel s = (ServerLevel)level;
            return Block.m_49874_((BlockState)this.getBlockState(), (ServerLevel)s, (BlockPos)this.pos, (BlockEntity)this.getEntity(), (Entity)entity, (ItemStack)heldItem);
        }
        return null;
    }

    public void popItem(ItemStack item) {
        Block.m_49840_((Level)this.minecraftLevel, (BlockPos)this.pos, (ItemStack)item);
    }

    public void popItemFromFace(ItemStack item, Direction dir) {
        Block.m_152435_((Level)this.minecraftLevel, (BlockPos)this.pos, (Direction)dir, (ItemStack)item);
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof CharSequence || obj instanceof ResourceLocation) {
            return this.getId().equals(obj.toString());
        }
        return super.equals(obj);
    }

    private static boolean isReal(Player p) {
        return !PlayerHooks.isFake((Player)p);
    }

    public EntityArrayList getPlayersInRadius(double radius) {
        return new EntityArrayList(this.minecraftLevel, this.minecraftLevel.m_6443_(Player.class, new AABB((double)this.pos.m_123341_() - radius, (double)this.pos.m_123342_() - radius, (double)this.pos.m_123343_() - radius, (double)this.pos.m_123341_() + 1.0 + radius, (double)this.pos.m_123342_() + 1.0 + radius, (double)this.pos.m_123343_() + 1.0 + radius), BlockContainerJS::isReal));
    }

    public EntityArrayList getPlayersInRadius() {
        return this.getPlayersInRadius(8.0);
    }

    public ResourceLocation getBiomeId() {
        return this.minecraftLevel.m_204166_(this.pos).m_203543_().orElse(Biomes.f_48202_).m_135782_();
    }

    public boolean specialEquals(Object o, boolean shallow) {
        if (o instanceof CharSequence || o instanceof ResourceLocation) {
            return this.getId().equals(o.toString());
        }
        return this.equals(o);
    }

    public CompoundTag getTypeData() {
        return this.getBlockState().m_60734_().kjs$getTypeData();
    }
}

