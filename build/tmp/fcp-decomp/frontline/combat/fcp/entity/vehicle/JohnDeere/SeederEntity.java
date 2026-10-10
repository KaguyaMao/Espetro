/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.entity.vehicle.base.GeoVehicleEntity
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.Container
 *  net.minecraft.world.Containers
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.MenuProvider
 *  net.minecraft.world.SimpleContainer
 *  net.minecraft.world.SimpleMenuProvider
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.Entity$RemovalReason
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.ChestMenu
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.gameevent.GameEvent
 *  net.minecraft.world.level.gameevent.GameEvent$Context
 *  net.minecraftforge.common.IPlantable
 *  net.minecraftforge.network.NetworkHooks
 *  software.bernie.geckolib.core.animatable.GeoAnimatable
 *  software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache
 *  software.bernie.geckolib.core.animation.AnimatableManager$ControllerRegistrar
 *  software.bernie.geckolib.core.animation.AnimationController
 *  software.bernie.geckolib.core.object.PlayState
 *  software.bernie.geckolib.util.GeckoLibUtil
 */
package frontline.combat.fcp.entity.vehicle.JohnDeere;

import com.atsuishio.superbwarfare.entity.vehicle.base.GeoVehicleEntity;
import frontline.combat.fcp.entity.vehicle.Trailers.AbstractTrailerEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.network.NetworkHooks;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class SeederEntity
extends AbstractTrailerEntity {
    private static final ResourceLocation[] CAMO_TEXTURES = new ResourceLocation[]{new ResourceLocation("fcp", "textures/entity/tractor/john_deere.png"), new ResourceLocation("fcp", "textures/entity/tractor/john_deere_wrecked.png")};
    private static final String[] CAMO_NAMES = new String[]{"John Deere"};
    private static final int INVENTORY_SIZE = 27;
    private static final double ROW_HALF_WIDTH = 9.0;
    private static final double ROW_SPACING = 1.0;
    private static final double ROW_LOCAL_Z = 0.0;
    private static final int SEARCH_DEPTH = 2;
    private static final double PLANT_STEP = 0.5;
    private static final int MAX_STEPS = 8;
    private static final double TELEPORT_DISTANCE = 12.0;
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache((GeoAnimatable)this);
    private final SimpleContainer inventory = new SimpleContainer(27);
    private double lastSeedX = Double.NaN;
    private double lastSeedZ = Double.NaN;

    public SeederEntity(EntityType<SeederEntity> type, Level world) {
        super((EntityType<? extends GeoVehicleEntity>)type, world);
    }

    @Override
    public ResourceLocation[] getCamoTextures() {
        return CAMO_TEXTURES;
    }

    @Override
    public String[] getCamoNames() {
        return CAMO_NAMES;
    }

    public void registerControllers(AnimatableManager.ControllerRegistrar reg) {
        reg.add(new AnimationController[]{new AnimationController((GeoAnimatable)this, "base", 0, state -> PlayState.STOP)});
    }

    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public void m_6075_() {
        double dz;
        super.m_6075_();
        if (this.m_9236_().m_5776_()) {
            return;
        }
        if (!this.isAttached()) {
            this.lastSeedX = Double.NaN;
            this.lastSeedZ = Double.NaN;
            return;
        }
        if (Double.isNaN(this.lastSeedX)) {
            this.lastSeedX = this.m_20185_();
            this.lastSeedZ = this.m_20189_();
            return;
        }
        double dx = this.m_20185_() - this.lastSeedX;
        double dist = Math.sqrt(dx * dx + (dz = this.m_20189_() - this.lastSeedZ) * dz);
        if (dist > 12.0) {
            this.lastSeedX = this.m_20185_();
            this.lastSeedZ = this.m_20189_();
            return;
        }
        if (dist < 0.5) {
            return;
        }
        int steps = (int)Math.min(8.0, Math.ceil(dist / 0.5));
        for (int s = 1; s <= steps; ++s) {
            double t = (double)s / (double)steps;
            this.plantRow(this.lastSeedX + dx * t, this.lastSeedZ + dz * t);
        }
        this.lastSeedX = this.m_20185_();
        this.lastSeedZ = this.m_20189_();
    }

    private void plantRow(double cx, double cz) {
        int count = (int)Math.floor(18.0) + 1;
        double theta = Math.toRadians(this.m_146908_());
        double cos = Math.cos(theta);
        double sin = Math.sin(theta);
        for (int i = 0; i < count; ++i) {
            double lx = -9.0 + (double)i * 1.0;
            double wx = cx + (lx * cos - 0.0 * sin);
            double wz = cz + (lx * sin + 0.0 * cos);
            this.plantAt(wx, this.m_20186_(), wz);
        }
    }

    private void plantAt(double wx, double wy, double wz) {
        Level level = this.m_9236_();
        BlockPos base = BlockPos.m_274561_((double)wx, (double)wy, (double)wz);
        for (int dy = 1; dy >= -2; --dy) {
            BlockPos soil = base.m_7918_(0, dy, 0);
            BlockState soilState = level.m_8055_(soil);
            if (!soilState.m_60713_(Blocks.f_50093_)) continue;
            BlockPos cropPos = soil.m_7494_();
            if (!level.m_8055_(cropPos).m_60795_()) {
                return;
            }
            this.tryPlant(soilState, soil, cropPos);
            return;
        }
    }

    private boolean tryPlant(BlockState soilState, BlockPos soil, BlockPos cropPos) {
        Level level = this.m_9236_();
        for (int slot = 0; slot < this.inventory.m_6643_(); ++slot) {
            BlockState crop;
            IPlantable plantable;
            BlockItem blockItem;
            Block block;
            Item item;
            ItemStack stack = this.inventory.m_8020_(slot);
            if (stack.m_41619_() || !((item = stack.m_41720_()) instanceof BlockItem) || !((block = (blockItem = (BlockItem)item).m_40614_()) instanceof IPlantable) || !soilState.canSustainPlant((BlockGetter)level, soil, Direction.UP, plantable = (IPlantable)block) || !(crop = block.m_49966_()).m_60710_((LevelReader)level, cropPos)) continue;
            level.m_7731_(cropPos, crop, 3);
            level.m_220407_(GameEvent.f_157797_, cropPos, GameEvent.Context.m_223719_((Entity)this, (BlockState)crop));
            level.m_5594_(null, cropPos, SoundEvents.f_11839_, SoundSource.BLOCKS, 0.5f, 1.0f);
            stack.m_41774_(1);
            if (stack.m_41619_()) {
                this.inventory.m_6836_(slot, ItemStack.f_41583_);
            }
            this.inventory.m_6596_();
            return true;
        }
        return false;
    }

    @Override
    public InteractionResult m_6096_(Player player, InteractionHand hand) {
        if (!player.m_21120_(hand).m_41619_()) {
            return super.m_6096_(player, hand);
        }
        if (this.m_9236_().m_5776_()) {
            return InteractionResult.SUCCESS;
        }
        if (player instanceof ServerPlayer) {
            ServerPlayer serverPlayer = (ServerPlayer)player;
            NetworkHooks.openScreen((ServerPlayer)serverPlayer, (MenuProvider)new SimpleMenuProvider((id, playerInv, p) -> ChestMenu.m_39237_((int)id, (Inventory)playerInv, (Container)this.inventory), this.m_5446_()));
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void m_7380_(CompoundTag tag) {
        super.m_7380_(tag);
        tag.m_128365_("SeedInventory", (Tag)this.inventory.m_7927_());
    }

    @Override
    public void m_7378_(CompoundTag tag) {
        super.m_7378_(tag);
        if (tag.m_128425_("SeedInventory", 9)) {
            this.inventory.m_7797_(tag.m_128437_("SeedInventory", 10));
        }
    }

    public void m_142687_(Entity.RemovalReason reason) {
        if (!this.m_9236_().m_5776_() && reason.m_146965_()) {
            Containers.m_18998_((Level)this.m_9236_(), (Entity)this, (Container)this.inventory);
        }
        super.m_142687_(reason);
    }
}

