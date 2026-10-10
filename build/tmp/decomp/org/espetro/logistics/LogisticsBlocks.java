/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 *  net.minecraftforge.registries.RegisterEvent
 */
package org.espetro.logistics;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegisterEvent;
import org.espetro.logistics.ConstructionMaterialItem;
import org.espetro.logistics.SupplySourceBlock;
import org.espetro.logistics.SupplySourceBlockEntity;

@Mod.EventBusSubscriber(modid="espetro", bus=Mod.EventBusSubscriber.Bus.MOD)
public final class LogisticsBlocks {
    public static final ResourceLocation SUPPLY_SOURCE_ID = ResourceLocation.fromNamespaceAndPath((String)"espetro", (String)"supply_source");
    public static final ResourceLocation CONSTRUCTION_MATERIAL_ID = ResourceLocation.fromNamespaceAndPath((String)"espetro", (String)"construction_material");
    public static SupplySourceBlock SUPPLY_SOURCE;
    public static BlockEntityType<SupplySourceBlockEntity> SUPPLY_SOURCE_BLOCK_ENTITY;
    public static ConstructionMaterialItem CONSTRUCTION_MATERIAL;

    private LogisticsBlocks() {
    }

    @SubscribeEvent
    public static void register(RegisterEvent event) {
        event.register(Registries.f_256747_, helper -> {
            SUPPLY_SOURCE = new SupplySourceBlock(BlockBehaviour.Properties.m_284310_().m_284180_(MapColor.f_283906_).m_60978_(3.5f).m_60918_(SoundType.f_56743_));
            helper.register(SUPPLY_SOURCE_ID, (Object)SUPPLY_SOURCE);
        });
        event.register(Registries.f_256913_, helper -> {
            helper.register(SUPPLY_SOURCE_ID, (Object)new BlockItem(SUPPLY_SOURCE, new Item.Properties()));
            CONSTRUCTION_MATERIAL = new ConstructionMaterialItem(new Item.Properties().m_41487_(64));
            helper.register(CONSTRUCTION_MATERIAL_ID, (Object)CONSTRUCTION_MATERIAL);
        });
        event.register(Registries.f_256922_, helper -> {
            SUPPLY_SOURCE_BLOCK_ENTITY = BlockEntityType.Builder.m_155273_(SupplySourceBlockEntity::new, SUPPLY_SOURCE).m_58966_(null);
            helper.register(SUPPLY_SOURCE_ID, SUPPLY_SOURCE_BLOCK_ENTITY);
        });
    }
}

