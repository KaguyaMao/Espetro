/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Direction
 *  net.minecraft.core.dispenser.DispenseItemBehavior
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.MobSpawnType
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.SpawnEggItem
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.block.DispenserBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.gameevent.GameEvent
 *  net.minecraft.world.level.gameevent.GameEvent$Context
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.eventbus.api.EventPriority
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.common;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.jetbrains.annotations.Nullable;

public class ForgeSpawnEggItem
extends SpawnEggItem {
    private static final List<ForgeSpawnEggItem> MOD_EGGS = new ArrayList<ForgeSpawnEggItem>();
    private static final Map<EntityType<? extends Mob>, ForgeSpawnEggItem> TYPE_MAP = new IdentityHashMap<EntityType<? extends Mob>, ForgeSpawnEggItem>();
    private final Supplier<? extends EntityType<? extends Mob>> typeSupplier;
    private static final DispenseItemBehavior DEFAULT_DISPENSE_BEHAVIOR = (source, stack) -> {
        Direction face = (Direction)source.m_6414_().m_61143_((Property)DispenserBlock.f_52659_);
        EntityType type = ((SpawnEggItem)stack.m_41720_()).m_43228_(stack.m_41783_());
        try {
            type.m_20592_(source.m_7727_(), stack, null, source.m_7961_().m_121945_(face), MobSpawnType.DISPENSER, face != Direction.UP, false);
        }
        catch (Exception exception) {
            DispenseItemBehavior.f_181892_.error("Error while dispensing spawn egg from dispenser at {}", (Object)source.m_7961_(), (Object)exception);
            return ItemStack.f_41583_;
        }
        stack.m_41774_(1);
        source.m_7727_().m_220407_(GameEvent.f_157810_, source.m_7961_(), GameEvent.Context.m_223722_((BlockState)source.m_6414_()));
        return stack;
    };

    public ForgeSpawnEggItem(Supplier<? extends EntityType<? extends Mob>> type, int backgroundColor, int highlightColor, Item.Properties props) {
        super((EntityType)null, backgroundColor, highlightColor, props);
        this.typeSupplier = type;
        MOD_EGGS.add(this);
    }

    public EntityType<?> m_43228_(@Nullable CompoundTag tag) {
        EntityType<? extends Mob> type = super.m_43228_(tag);
        return type != null ? type : this.typeSupplier.get();
    }

    @Nullable
    protected DispenseItemBehavior createDispenseBehavior() {
        return DEFAULT_DISPENSE_BEHAVIOR;
    }

    @Nullable
    public static SpawnEggItem fromEntityType(@Nullable EntityType<?> type) {
        SpawnEggItem ret = TYPE_MAP.get(type);
        return ret != null ? ret : SpawnEggItem.m_43213_(type);
    }

    protected EntityType<?> getDefaultType() {
        return this.typeSupplier.get();
    }

    @Mod.EventBusSubscriber(value={Dist.CLIENT}, modid="forge", bus=Mod.EventBusSubscriber.Bus.MOD)
    private static class ColorRegisterHandler {
        private ColorRegisterHandler() {
        }

        @SubscribeEvent(priority=EventPriority.HIGHEST)
        public static void registerSpawnEggColors(RegisterColorHandlersEvent.Item event) {
            MOD_EGGS.forEach(egg -> event.getItemColors().m_92689_((stack, layer) -> egg.m_43211_(layer), new ItemLike[]{egg}));
        }
    }

    @Mod.EventBusSubscriber(modid="forge", bus=Mod.EventBusSubscriber.Bus.MOD)
    private static class CommonHandler {
        private CommonHandler() {
        }

        @SubscribeEvent
        public static void onCommonSetup(FMLCommonSetupEvent event) {
            MOD_EGGS.forEach(egg -> {
                DispenseItemBehavior dispenseBehavior = egg.createDispenseBehavior();
                if (dispenseBehavior != null) {
                    DispenserBlock.m_52672_((ItemLike)egg, (DispenseItemBehavior)dispenseBehavior);
                }
                TYPE_MAP.put(egg.typeSupplier.get(), (ForgeSpawnEggItem)((Object)egg));
            });
        }
    }
}

