/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.latvian.mods.rhino.util.HideFromJS
 *  net.minecraft.core.BlockPos
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.protocol.Packet
 *  net.minecraft.network.protocol.game.ClientGamePacketListener
 *  net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.block.entity;

import dev.latvian.mods.kubejs.block.entity.BlockEntityAttachment;
import dev.latvian.mods.kubejs.block.entity.BlockEntityEventCallback;
import dev.latvian.mods.kubejs.block.entity.BlockEntityInfo;
import dev.latvian.mods.kubejs.core.InventoryKJS;
import dev.latvian.mods.kubejs.level.BlockContainerJS;
import dev.latvian.mods.rhino.util.HideFromJS;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class BlockEntityJS
extends BlockEntity {
    public final BlockEntityInfo info;
    protected BlockContainerJS block;
    public final int x;
    public final int y;
    public final int z;
    public int tick;
    public int cycle;
    public CompoundTag data;
    public final BlockEntityAttachment[] attachments;
    public InventoryKJS inventory;
    public UUID placerId;

    public BlockEntityJS(BlockPos blockPos, BlockState blockState, BlockEntityInfo entityInfo) {
        super(entityInfo.entityType, blockPos, blockState);
        this.info = entityInfo;
        this.x = blockPos.m_123341_();
        this.y = blockPos.m_123342_();
        this.z = blockPos.m_123343_();
        this.tick = 0;
        this.cycle = 0;
        this.data = this.info.initialData.m_6426_();
        if (entityInfo.attachments != null) {
            this.attachments = new BlockEntityAttachment[entityInfo.attachments.size()];
            for (int i = 0; i < this.attachments.length; ++i) {
                InventoryKJS inv;
                BlockEntityAttachment blockEntityAttachment;
                this.attachments[i] = entityInfo.attachments.get(i).factory().create(this);
                if (this.inventory != null || !((blockEntityAttachment = this.attachments[i]) instanceof InventoryKJS)) continue;
                this.inventory = inv = (InventoryKJS)((Object)blockEntityAttachment);
            }
        } else {
            this.attachments = BlockEntityAttachment.EMPTY_ARRAY;
        }
    }

    protected void m_183515_(CompoundTag tag) {
        super.m_183515_(tag);
        tag.m_128365_("data", (Tag)this.data);
        if (this.tick > 0) {
            tag.m_128405_("tick", this.tick);
        }
        if (this.cycle > 0) {
            tag.m_128405_("cycle", this.cycle);
        }
        if (this.placerId != null) {
            tag.m_128362_("placer", this.placerId);
        }
        if (this.attachments.length > 0) {
            ListTag list = new ListTag();
            for (BlockEntityAttachment att : this.attachments) {
                list.add((Object)att.writeAttachment());
            }
            tag.m_128365_("attachments", (Tag)list);
        }
    }

    public void m_142466_(CompoundTag tag) {
        block4: {
            super.m_142466_(tag);
            this.data = tag.m_128469_("data");
            this.tick = tag.m_128451_("tick");
            this.cycle = tag.m_128451_("cycle");
            UUID uUID = this.placerId = tag.m_128441_("placer") ? tag.m_128342_("placer") : null;
            if (this.attachments.length <= 0) break block4;
            ListTag list = tag.m_128437_("attachments", 10);
            if (this.attachments.length == list.size()) {
                for (int i = 0; i < this.attachments.length; ++i) {
                    this.attachments[i].readAttachment(list.m_128728_(i));
                }
            } else {
                for (BlockEntityAttachment att : this.attachments) {
                    att.readAttachment(new CompoundTag());
                }
            }
        }
    }

    public CompoundTag m_5995_() {
        CompoundTag tag = new CompoundTag();
        if (this.info.sync && !this.data.m_128456_()) {
            tag.m_128365_("data", (Tag)this.data);
        }
        if (this.tick > 0) {
            tag.m_128405_("tick", this.tick);
        }
        if (this.cycle > 0) {
            tag.m_128405_("cycle", this.cycle);
        }
        return tag;
    }

    @Nullable
    public Packet<ClientGamePacketListener> m_58483_() {
        return ClientboundBlockEntityDataPacket.m_195640_((BlockEntity)this);
    }

    public void save() {
        if (this.f_58857_ != null) {
            this.f_58857_.m_151543_(this.f_58858_);
        }
    }

    public void sync() {
        if (this.f_58857_ != null) {
            this.save();
            this.f_58857_.m_7260_(this.f_58858_, this.m_58900_(), this.m_58900_(), 11);
        }
    }

    public void sendEvent(int eventId, int data) {
        this.f_58857_.m_7696_(this.f_58858_, this.m_58900_().m_60734_(), eventId, data);
    }

    public boolean m_7531_(int eventId, int data) {
        BlockEntityEventCallback e;
        if (this.info.eventHandlers != null && (e = (BlockEntityEventCallback)this.info.eventHandlers.get(eventId)) != null) {
            e.accept(this, data);
            return true;
        }
        return false;
    }

    @HideFromJS
    public void postTick(boolean c) {
        ++this.tick;
        if (c) {
            ++this.cycle;
        }
    }

    public BlockContainerJS getBlock() {
        if (this.block == null) {
            this.block = new BlockContainerJS(this.f_58857_, this.f_58858_);
            this.block.cachedEntity = this;
        }
        return this.block;
    }
}

