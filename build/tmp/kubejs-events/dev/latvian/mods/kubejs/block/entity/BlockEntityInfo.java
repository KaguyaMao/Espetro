/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.latvian.mods.rhino.util.HideFromJS
 *  it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  net.minecraft.core.BlockPos
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityTicker
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 */
package dev.latvian.mods.kubejs.block.entity;

import dev.latvian.mods.kubejs.block.BlockBuilder;
import dev.latvian.mods.kubejs.block.entity.BlockEntityAttachmentHolder;
import dev.latvian.mods.kubejs.block.entity.BlockEntityAttachmentType;
import dev.latvian.mods.kubejs.block.entity.BlockEntityCallback;
import dev.latvian.mods.kubejs.block.entity.BlockEntityEventCallback;
import dev.latvian.mods.kubejs.block.entity.BlockEntityJS;
import dev.latvian.mods.kubejs.block.entity.BlockEntityJSTicker;
import dev.latvian.mods.kubejs.core.ServerPlayerKJS;
import dev.latvian.mods.kubejs.platform.LevelPlatformHelper;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.rhino.util.HideFromJS;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class BlockEntityInfo {
    public final transient BlockBuilder blockBuilder;
    public transient BlockEntityType<?> entityType;
    public transient CompoundTag initialData;
    public transient BlockEntityJSTicker serverTicker;
    public transient BlockEntityJSTicker clientTicker;
    public transient boolean sync;
    public transient List<BlockEntityAttachmentHolder> attachments;
    public transient Int2ObjectMap<BlockEntityEventCallback> eventHandlers;

    public BlockEntityInfo(BlockBuilder blockBuilder) {
        this.blockBuilder = blockBuilder;
        this.initialData = new CompoundTag();
        this.sync = false;
        this.attachments = new ArrayList<BlockEntityAttachmentHolder>(1);
        this.eventHandlers = new Int2ObjectArrayMap(0);
    }

    public void initialData(CompoundTag data) {
        this.initialData = data;
    }

    public void serverTick(int frequency, int offset, BlockEntityCallback callback) {
        this.serverTicker = new BlockEntityJSTicker(this, Math.max(1, frequency), Math.max(0, offset), callback, true);
    }

    public void serverTick(BlockEntityCallback callback) {
        this.serverTick(1, 0, callback);
    }

    public void clientTick(int frequency, int offset, BlockEntityCallback callback) {
        this.clientTicker = new BlockEntityJSTicker(this, Math.max(1, frequency), Math.max(0, offset), callback, false);
    }

    public void clientTick(BlockEntityCallback callback) {
        this.clientTick(1, 0, callback);
    }

    public void tick(int frequency, int offset, BlockEntityCallback callback) {
        this.serverTick(frequency, offset, callback);
        this.clientTick(frequency, offset, callback);
    }

    public void tick(BlockEntityCallback callback) {
        this.serverTick(callback);
        this.clientTick(callback);
    }

    public void enableSync() {
        this.sync = true;
    }

    public void attach(String type, Map<String, Object> args) {
        BlockEntityAttachmentType att = BlockEntityAttachmentType.ALL.get().get(type);
        if (att != null) {
            try {
                this.attachments.add(new BlockEntityAttachmentHolder(this.attachments.size(), att.factory().apply(args)));
            }
            catch (Exception ex) {
                ConsoleJS.STARTUP.error("Error while creating BlockEntity attachment '" + type + "'", ex);
            }
        } else {
            ConsoleJS.STARTUP.error("BlockEntity attachment '" + type + "' not found!");
        }
    }

    public void inventory(int width, int height) {
        this.attach("inventory", Map.of("width", width, "height", height));
    }

    public void inventory(int width, int height, Ingredient inputFilter) {
        this.attach("inventory", Map.of("width", width, "height", height, "inputFilter", inputFilter));
    }

    public void eventHandler(int eventId, BlockEntityEventCallback callback) {
        this.eventHandlers.put(eventId, (Object)callback);
    }

    public void rightClickOpensInventory() {
        this.blockBuilder.rightClick = e -> {
            BlockEntity patt3545$temp = e.getBlock().getEntity();
            if (patt3545$temp instanceof BlockEntityJS) {
                BlockEntityJS entity = (BlockEntityJS)patt3545$temp;
                if (entity.inventory != null) {
                    ((ServerPlayerKJS)e.getPlayer()).kjs$openInventoryGUI(entity.inventory, (Component)((Block)this.blockBuilder.get()).m_49954_());
                }
            }
        };
    }

    @HideFromJS
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return LevelPlatformHelper.get().createBlockEntity(pos, state, this);
    }

    @HideFromJS
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level) {
        return level.m_5776_() ? this.clientTicker : this.serverTicker;
    }

    public String toString() {
        return "BlockEntityInfo[" + String.valueOf(this.blockBuilder.id) + "]";
    }
}

