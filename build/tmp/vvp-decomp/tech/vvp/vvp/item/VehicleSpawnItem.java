/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.core.Direction
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.TooltipFlag
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.ServerLevelAccessor
 */
package tech.vvp.vvp.item;

import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

public class VehicleSpawnItem
extends Item {
    private final Supplier<EntityType<?>> entityTypeSupplier;

    public VehicleSpawnItem(Supplier<EntityType<?>> entityTypeSupplier, Item.Properties properties) {
        super(properties);
        this.entityTypeSupplier = entityTypeSupplier;
    }

    public InteractionResult m_6225_(UseOnContext context) {
        Level level = context.m_43725_();
        if (level instanceof ServerLevelAccessor) {
            ServerLevelAccessor serverLevel = (ServerLevelAccessor)level;
            EntityType<?> entityType = this.entityTypeSupplier.get();
            Entity entity = entityType.m_20615_((Level)serverLevel.m_6018_());
            if (entity != null) {
                entity.m_6034_(context.m_43720_().f_82479_, context.m_43720_().f_82480_, context.m_43720_().f_82481_);
                Direction direction = context.m_8125_();
                float yaw = direction.m_122435_();
                entity.m_146922_(yaw);
                entity.m_146926_(0.0f);
                entity.f_19859_ = yaw;
                entity.f_19860_ = 0.0f;
                serverLevel.m_6018_().m_7967_(entity);
                if (context.m_43723_() != null && !context.m_43723_().m_150110_().f_35937_) {
                    context.m_43722_().m_41774_(1);
                }
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    public void m_7373_(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.m_7373_(stack, level, tooltip, flag);
    }
}

