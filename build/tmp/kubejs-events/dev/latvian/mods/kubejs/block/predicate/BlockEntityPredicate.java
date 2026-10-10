/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.level.block.entity.BlockEntity
 */
package dev.latvian.mods.kubejs.block.predicate;

import dev.latvian.mods.kubejs.block.predicate.BlockEntityPredicateDataCheck;
import dev.latvian.mods.kubejs.block.predicate.BlockPredicate;
import dev.latvian.mods.kubejs.level.BlockContainerJS;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;

public class BlockEntityPredicate
implements BlockPredicate {
    private final ResourceLocation id;
    private BlockEntityPredicateDataCheck checkData;

    public BlockEntityPredicate(ResourceLocation i) {
        this.id = i;
    }

    public BlockEntityPredicate data(BlockEntityPredicateDataCheck cd) {
        this.checkData = cd;
        return this;
    }

    @Override
    public boolean check(BlockContainerJS block) {
        BlockEntity tileEntity = block.getEntity();
        return tileEntity != null && this.id.equals((Object)RegistryInfo.BLOCK_ENTITY_TYPE.getId(tileEntity.m_58903_())) && (this.checkData == null || this.checkData.checkData(block.getEntityData()));
    }

    public String toString() {
        return "{entity=" + String.valueOf(this.id) + "}";
    }
}

