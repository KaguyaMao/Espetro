/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 *  net.minecraftforge.registries.RegisterEvent
 */
package org.espetro.bastion;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegisterEvent;
import org.espetro.Espetro;
import org.espetro.bastion.OnBuildingBlock;
import org.espetro.bastion.RadioBlock;

@Mod.EventBusSubscriber(modid="espetro", bus=Mod.EventBusSubscriber.Bus.MOD)
public class BastionItems {
    public static RadioBlock RADIO_BLOCK;
    public static BlockItem RADIO_BLOCK_ITEM;
    public static OnBuildingBlock ON_BUILDING_BLOCK;

    @SubscribeEvent
    public static void registerAll(RegisterEvent event) {
        event.register(Registries.f_256747_, helper -> {
            RADIO_BLOCK = new RadioBlock(BlockBehaviour.Properties.m_284310_().m_284180_(MapColor.f_283784_).m_60913_(6.0f, 30.0f).m_60918_(SoundType.f_56743_).m_60955_());
            helper.register(ResourceLocation.fromNamespaceAndPath((String)"espetro", (String)"radio"), (Object)RADIO_BLOCK);
            ON_BUILDING_BLOCK = new OnBuildingBlock(BlockBehaviour.Properties.m_284310_().m_284180_(MapColor.f_283832_).m_60913_(1.0f, 1.0f).m_60918_(SoundType.f_56736_).m_60955_());
            helper.register(ResourceLocation.fromNamespaceAndPath((String)"espetro", (String)"onbuilding"), (Object)ON_BUILDING_BLOCK);
        });
        event.register(Registries.f_256913_, helper -> {
            RADIO_BLOCK_ITEM = new BlockItem(RADIO_BLOCK, new Item.Properties().m_41487_(1));
            helper.register(ResourceLocation.fromNamespaceAndPath((String)"espetro", (String)"radio"), (Object)RADIO_BLOCK_ITEM);
            Espetro.LOGGER.info("\u6ce8\u518c Radio \u65b9\u5757\uff08\u90e8\u7f72\u8d70 Alt \u8f6e\u76d8\uff09");
        });
    }
}

