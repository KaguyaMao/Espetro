/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  net.minecraft.ChatFormatting
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.ItemStack
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.event.entity.player.ItemTooltipEvent
 *  net.minecraftforge.eventbus.api.IEventBus
 *  net.minecraftforge.fml.common.Mod
 *  net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
 *  net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext
 *  net.minecraftforge.network.simple.SimpleChannel
 *  org.slf4j.Logger
 */
package frontline.combat.fcp;

import com.mojang.logging.LogUtils;
import frontline.combat.fcp.init.ModEntities;
import frontline.combat.fcp.init.ModItems;
import frontline.combat.fcp.init.ModParticleTypes;
import frontline.combat.fcp.init.ModSounds;
import frontline.combat.fcp.init.ModTabs;
import frontline.combat.fcp.network.FCPNetwork;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.simple.SimpleChannel;
import org.slf4j.Logger;

@Mod(value="fcp")
public class FCP {
    public static final String MODID = "fcp";
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final SimpleChannel PACKET_HANDLER = FCPNetwork.FCP_HANDLER;

    public FCP() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModEntities.register(modEventBus);
        ModItems.REGISTRY.register(modEventBus);
        ModParticleTypes.PARTICLE_TYPES.register(modEventBus);
        ModSounds.REGISTRY.register(modEventBus);
        ModTabs.TABS.register(modEventBus);
        modEventBus.addListener(this::setup);
        MinecraftForge.EVENT_BUS.register((Object)this);
        MinecraftForge.EVENT_BUS.addListener(this::onItemTooltip);
    }

    private void setup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> FCPNetwork.register());
    }

    private void onItemTooltip(ItemTooltipEvent event) {
        String entityType;
        CompoundTag tag;
        if (event.getItemStack().m_41720_() instanceof BlockItem && event.getItemStack().m_41782_() && (tag = BlockItem.m_186336_((ItemStack)event.getItemStack())) != null && tag.m_128441_("EntityType") && (entityType = tag.m_128461_("EntityType")).startsWith("fcp:vdv_")) {
            event.getToolTip().add(Component.m_237115_((String)"tooltip.fcp.usage_restriction").m_130940_(ChatFormatting.RED));
        }
    }

    public static ResourceLocation loc(String path) {
        return new ResourceLocation(MODID, path);
    }
}

