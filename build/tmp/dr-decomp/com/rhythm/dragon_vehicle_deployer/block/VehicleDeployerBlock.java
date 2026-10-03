/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.ChatFormatting
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.MenuProvider
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.BaseEntityBlock
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.RenderShape
 *  net.minecraft.world.level.block.SoundType
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityTicker
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.BooleanProperty
 *  net.minecraft.world.level.block.state.properties.DirectionProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.Shapes
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  net.minecraftforge.network.NetworkHooks
 *  net.minecraftforge.registries.ForgeRegistries
 */
package com.rhythm.dragon_vehicle_deployer.block;

import com.rhythm.dragon_vehicle_deployer.DragonVehicleDeployer;
import com.rhythm.dragon_vehicle_deployer.block.entity.VehicleDeployerBlockEntity;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.ForgeRegistries;

public class VehicleDeployerBlock
extends BaseEntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.f_61374_;
    public static final BooleanProperty TRIGGERED = BlockStateProperties.f_61360_;
    private static final VoxelShape SHAPE = Shapes.m_83048_((double)0.0, (double)0.0, (double)0.0, (double)1.0, (double)1.0, (double)1.0);

    public VehicleDeployerBlock() {
        super(BlockBehaviour.Properties.m_284310_().m_60918_(SoundType.f_56743_).m_60913_(3.0f, 3600000.0f).m_60999_());
        this.m_49959_((BlockState)((BlockState)((BlockState)this.f_49792_.m_61090_()).m_61124_((Property)FACING, (Comparable)Direction.NORTH)).m_61124_((Property)TRIGGERED, (Comparable)Boolean.valueOf(false)));
    }

    public VoxelShape m_5940_(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Nullable
    public BlockState m_5573_(BlockPlaceContext context) {
        return (BlockState)this.m_49966_().m_61124_((Property)FACING, (Comparable)context.m_8125_().m_122424_());
    }

    protected void m_7926_(StateDefinition.Builder<Block, BlockState> builder) {
        builder.m_61104_(new Property[]{FACING, TRIGGERED});
    }

    public RenderShape m_7514_(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    public BlockEntity m_142194_(BlockPos pos, BlockState state) {
        return new VehicleDeployerBlockEntity(pos, state);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> m_142354_(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.f_46443_) {
            return null;
        }
        return VehicleDeployerBlock.m_152132_(type, (BlockEntityType)((BlockEntityType)DragonVehicleDeployer.VEHICLE_DEPLOYER_BLOCK_ENTITY.get()), VehicleDeployerBlockEntity::tick);
    }

    public void m_6861_(BlockState state, Level world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        boolean charged = world.m_276867_(pos) || world.m_276867_(pos.m_7494_());
        boolean triggered = (Boolean)state.m_61143_((Property)TRIGGERED);
        if (charged && !triggered) {
            world.m_7731_(pos, (BlockState)state.m_61124_((Property)TRIGGERED, (Comparable)Boolean.TRUE), 4);
            BlockEntity blockEntity = world.m_7702_(pos);
            if (blockEntity instanceof VehicleDeployerBlockEntity) {
                VehicleDeployerBlockEntity blockEntity2 = (VehicleDeployerBlockEntity)blockEntity;
                blockEntity2.deploy(state);
            }
        } else if (!charged && triggered) {
            world.m_7731_(pos, (BlockState)state.m_61124_((Property)TRIGGERED, (Comparable)Boolean.FALSE), 4);
        }
    }

    public InteractionResult m_6227_(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (world.f_46443_) {
            return InteractionResult.SUCCESS;
        }
        BlockEntity blockEntity = world.m_7702_(pos);
        if (!(blockEntity instanceof VehicleDeployerBlockEntity)) {
            return InteractionResult.FAIL;
        }
        VehicleDeployerBlockEntity blockEntity2 = (VehicleDeployerBlockEntity)blockEntity;
        if (player.m_6144_() && player.m_20310_(2)) {
            if (player instanceof ServerPlayer) {
                ServerPlayer serverPlayer = (ServerPlayer)player;
                NetworkHooks.openScreen((ServerPlayer)serverPlayer, (MenuProvider)blockEntity2, (BlockPos)pos);
            }
            return InteractionResult.SUCCESS;
        }
        if (!player.m_7500_()) {
            return InteractionResult.FAIL;
        }
        ItemStack stack = player.m_21120_(hand);
        Item containerItem = (Item)ForgeRegistries.ITEMS.getValue(new ResourceLocation("superbwarfare", "container"));
        if (containerItem == null || stack.m_41720_() != containerItem) {
            player.m_5661_((Component)Component.m_237115_((String)"des.superbwarfare.vehicle_deployer.fail").m_130940_(ChatFormatting.RED), true);
            return InteractionResult.FAIL;
        }
        blockEntity2.writeEntityInfo(stack);
        player.m_5661_((Component)Component.m_237115_((String)"des.superbwarfare.vehicle_deployer.success").m_130940_(ChatFormatting.GREEN), true);
        return InteractionResult.SUCCESS;
    }
}

