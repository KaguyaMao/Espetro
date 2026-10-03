/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  net.minecraft.Util
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.tags.FluidTags
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.entity.vehicle.Boat
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Rarity
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.material.FluidState
 *  net.minecraft.world.level.pathfinder.BlockPathTypes
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.fml.loading.FMLEnvironment
 *  net.minecraftforge.fml.loading.FMLLoader
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.fluids;

import com.google.common.collect.ImmutableMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.SoundAction;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

public class FluidType {
    public static final int BUCKET_VOLUME = 1000;
    public static final Lazy<Integer> SIZE = Lazy.of(() -> ForgeRegistries.FLUID_TYPES.get().getKeys().size());
    private String descriptionId;
    private final double motionScale;
    private final boolean canPushEntity;
    private final boolean canSwim;
    private final boolean canDrown;
    private final float fallDistanceModifier;
    private final boolean canExtinguish;
    private final boolean canConvertToSource;
    private final boolean supportsBoating;
    @Nullable
    private final BlockPathTypes pathType;
    @Nullable
    private final BlockPathTypes adjacentPathType;
    private final boolean canHydrate;
    private final int lightLevel;
    private final int density;
    private final int temperature;
    private final int viscosity;
    private final Rarity rarity;
    protected final Map<SoundAction, SoundEvent> sounds;
    private Object renderProperties;

    public FluidType(Properties properties) {
        this.descriptionId = properties.descriptionId;
        this.motionScale = properties.motionScale;
        this.canPushEntity = properties.canPushEntity;
        this.canSwim = properties.canSwim;
        this.canDrown = properties.canDrown;
        this.fallDistanceModifier = properties.fallDistanceModifier;
        this.canExtinguish = properties.canExtinguish;
        this.canConvertToSource = properties.canConvertToSource;
        this.supportsBoating = properties.supportsBoating;
        this.pathType = properties.pathType;
        this.adjacentPathType = properties.adjacentPathType;
        this.sounds = ImmutableMap.copyOf(properties.sounds);
        this.canHydrate = properties.canHydrate;
        this.lightLevel = properties.lightLevel;
        this.density = properties.density;
        this.temperature = properties.temperature;
        this.viscosity = properties.viscosity;
        this.rarity = properties.rarity;
        this.initClient();
    }

    public Component getDescription() {
        return Component.m_237115_((String)this.getDescriptionId());
    }

    public String getDescriptionId() {
        if (this.descriptionId == null) {
            this.descriptionId = Util.m_137492_((String)"fluid_type", (ResourceLocation)ForgeRegistries.FLUID_TYPES.get().getKey(this));
        }
        return this.descriptionId;
    }

    public int getLightLevel() {
        return this.lightLevel;
    }

    public int getDensity() {
        return this.density;
    }

    public int getTemperature() {
        return this.temperature;
    }

    public int getViscosity() {
        return this.viscosity;
    }

    public Rarity getRarity() {
        return this.rarity;
    }

    @Nullable
    public SoundEvent getSound(SoundAction action) {
        return this.sounds.get(action);
    }

    public double motionScale(Entity entity) {
        return this.motionScale;
    }

    public boolean canPushEntity(Entity entity) {
        return this.canPushEntity;
    }

    public boolean canSwim(Entity entity) {
        return this.canSwim;
    }

    public float getFallDistanceModifier(Entity entity) {
        return this.fallDistanceModifier;
    }

    public boolean canExtinguish(Entity entity) {
        return this.canExtinguish;
    }

    public boolean move(FluidState state, LivingEntity entity, Vec3 movementVector, double gravity) {
        return false;
    }

    public boolean canDrownIn(LivingEntity entity) {
        return this.canDrown;
    }

    public void setItemMovement(ItemEntity entity) {
        Vec3 vec3 = entity.m_20184_();
        entity.m_20334_(vec3.f_82479_ * (double)0.99f, vec3.f_82480_ + (double)(vec3.f_82480_ < (double)0.06f ? 5.0E-4f : 0.0f), vec3.f_82481_ * (double)0.99f);
    }

    public boolean supportsBoating(Boat boat) {
        return this.supportsBoating;
    }

    public boolean supportsBoating(FluidState state, Boat boat) {
        return this.supportsBoating(boat);
    }

    public boolean shouldUpdateWhileBoating(FluidState state, Boat boat, Entity rider) {
        return !this.supportsBoating(state, boat);
    }

    public boolean canRideVehicleUnder(Entity vehicle, Entity rider) {
        if (this == ForgeMod.WATER_TYPE.get()) {
            return !vehicle.m_275843_();
        }
        return true;
    }

    public boolean canHydrate(Entity entity) {
        return this.canHydrate;
    }

    @Nullable
    public SoundEvent getSound(Entity entity, SoundAction action) {
        return this.getSound(action);
    }

    public boolean canExtinguish(FluidState state, BlockGetter getter, BlockPos pos) {
        return this.canExtinguish;
    }

    public boolean canConvertToSource(FluidState state, LevelReader reader, BlockPos pos) {
        return this.canConvertToSource;
    }

    @Nullable
    public BlockPathTypes getBlockPathType(FluidState state, BlockGetter level, BlockPos pos, @Nullable Mob mob, boolean canFluidLog) {
        return this.pathType;
    }

    @Nullable
    public BlockPathTypes getAdjacentBlockPathType(FluidState state, BlockGetter level, BlockPos pos, @Nullable Mob mob, BlockPathTypes originalType) {
        return this.adjacentPathType;
    }

    @Nullable
    public SoundEvent getSound(@Nullable Player player, BlockGetter getter, BlockPos pos, SoundAction action) {
        return this.getSound(action);
    }

    public boolean canHydrate(FluidState state, BlockGetter getter, BlockPos pos, BlockState source, BlockPos sourcePos) {
        return this.canHydrate;
    }

    public int getLightLevel(FluidState state, BlockAndTintGetter getter, BlockPos pos) {
        return this.getLightLevel();
    }

    public int getDensity(FluidState state, BlockAndTintGetter getter, BlockPos pos) {
        return this.getDensity();
    }

    public int getTemperature(FluidState state, BlockAndTintGetter getter, BlockPos pos) {
        return this.getTemperature();
    }

    public int getViscosity(FluidState state, BlockAndTintGetter getter, BlockPos pos) {
        return this.getViscosity();
    }

    public boolean canConvertToSource(FluidStack stack) {
        return this.canConvertToSource;
    }

    @Nullable
    public SoundEvent getSound(FluidStack stack, SoundAction action) {
        return this.getSound(action);
    }

    public Component getDescription(FluidStack stack) {
        return Component.m_237115_((String)this.getDescriptionId(stack));
    }

    public String getDescriptionId(FluidStack stack) {
        return this.getDescriptionId();
    }

    public boolean canHydrate(FluidStack stack) {
        return this.canHydrate;
    }

    public int getLightLevel(FluidStack stack) {
        return this.getLightLevel();
    }

    public int getDensity(FluidStack stack) {
        return this.getDensity();
    }

    public int getTemperature(FluidStack stack) {
        return this.getTemperature();
    }

    public int getViscosity(FluidStack stack) {
        return this.getViscosity();
    }

    public Rarity getRarity(FluidStack stack) {
        return this.getRarity();
    }

    public final boolean isAir() {
        return this == ForgeMod.EMPTY_TYPE.get();
    }

    public final boolean isVanilla() {
        return this == ForgeMod.LAVA_TYPE.get() || this == ForgeMod.WATER_TYPE.get();
    }

    public ItemStack getBucket(FluidStack stack) {
        return new ItemStack((ItemLike)stack.getFluid().m_6859_());
    }

    public BlockState getBlockForFluidState(BlockAndTintGetter getter, BlockPos pos, FluidState state) {
        return state.m_76188_();
    }

    public FluidState getStateForPlacement(BlockAndTintGetter getter, BlockPos pos, FluidStack stack) {
        return stack.getFluid().m_76145_();
    }

    public final boolean canBePlacedInLevel(BlockAndTintGetter getter, BlockPos pos, FluidState state) {
        return !this.getBlockForFluidState(getter, pos, state).m_60795_();
    }

    public final boolean canBePlacedInLevel(BlockAndTintGetter getter, BlockPos pos, FluidStack stack) {
        return this.canBePlacedInLevel(getter, pos, this.getStateForPlacement(getter, pos, stack));
    }

    public final boolean isLighterThanAir() {
        return this.getDensity() <= 0;
    }

    public boolean isVaporizedOnPlacement(Level level, BlockPos pos, FluidStack stack) {
        if (level.m_6042_().f_63857_()) {
            return this == ForgeMod.WATER_TYPE.get() || this.getStateForPlacement((BlockAndTintGetter)level, pos, stack).m_205070_(FluidTags.f_13131_);
        }
        return false;
    }

    public void onVaporize(@Nullable Player player, Level level, BlockPos pos, FluidStack stack) {
        SoundEvent sound = this.getSound(player, (BlockGetter)level, pos, SoundActions.FLUID_VAPORIZE);
        level.m_5594_(player, pos, sound != null ? sound : SoundEvents.f_11937_, SoundSource.BLOCKS, 0.5f, 2.6f + (level.f_46441_.m_188501_() - level.f_46441_.m_188501_()) * 0.8f);
        for (int l = 0; l < 8; ++l) {
            level.m_7107_((ParticleOptions)ParticleTypes.f_123755_, (double)pos.m_123341_() + Math.random(), (double)pos.m_123342_() + Math.random(), (double)pos.m_123343_() + Math.random(), 0.0, 0.0, 0.0);
        }
    }

    public String toString() {
        @Nullable ResourceLocation name = ForgeRegistries.FLUID_TYPES.get().getKey(this);
        return name != null ? name.toString() : "Unregistered FluidType";
    }

    public Object getRenderPropertiesInternal() {
        return this.renderProperties;
    }

    private void initClient() {
        if (FMLEnvironment.dist == Dist.CLIENT && !FMLLoader.getLaunchHandler().isData()) {
            this.initializeClient(properties -> {
                if (properties == this) {
                    throw new IllegalStateException("Don't extend IFluidTypeRenderProperties in your fluid type, use an anonymous class instead.");
                }
                this.renderProperties = properties;
            });
        }
    }

    public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
    }

    public static final class Properties {
        private String descriptionId;
        private double motionScale = 0.014;
        private boolean canPushEntity = true;
        private boolean canSwim = true;
        private boolean canDrown = true;
        private float fallDistanceModifier = 0.5f;
        private boolean canExtinguish = false;
        private boolean canConvertToSource = false;
        private boolean supportsBoating = false;
        @Nullable
        private BlockPathTypes pathType = BlockPathTypes.WATER;
        @Nullable
        private BlockPathTypes adjacentPathType = BlockPathTypes.WATER_BORDER;
        private final Map<SoundAction, SoundEvent> sounds = new HashMap<SoundAction, SoundEvent>();
        private boolean canHydrate = false;
        private int lightLevel = 0;
        private int density = 1000;
        private int temperature = 300;
        private int viscosity = 1000;
        private Rarity rarity = Rarity.COMMON;

        private Properties() {
        }

        public static Properties create() {
            return new Properties();
        }

        public Properties descriptionId(String descriptionId) {
            this.descriptionId = descriptionId;
            return this;
        }

        public Properties motionScale(double motionScale) {
            this.motionScale = motionScale;
            return this;
        }

        public Properties canPushEntity(boolean canPushEntity) {
            this.canPushEntity = canPushEntity;
            return this;
        }

        public Properties canSwim(boolean canSwim) {
            this.canSwim = canSwim;
            return this;
        }

        public Properties canDrown(boolean canDrown) {
            this.canDrown = canDrown;
            return this;
        }

        public Properties fallDistanceModifier(float fallDistanceModifier) {
            this.fallDistanceModifier = fallDistanceModifier;
            return this;
        }

        public Properties canExtinguish(boolean canExtinguish) {
            this.canExtinguish = canExtinguish;
            return this;
        }

        public Properties canConvertToSource(boolean canConvertToSource) {
            this.canConvertToSource = canConvertToSource;
            return this;
        }

        public Properties supportsBoating(boolean supportsBoating) {
            this.supportsBoating = supportsBoating;
            return this;
        }

        public Properties pathType(@Nullable BlockPathTypes pathType) {
            this.pathType = pathType;
            return this;
        }

        public Properties adjacentPathType(@Nullable BlockPathTypes adjacentPathType) {
            this.adjacentPathType = adjacentPathType;
            return this;
        }

        public Properties sound(SoundAction action, SoundEvent sound) {
            this.sounds.put(action, sound);
            return this;
        }

        public Properties canHydrate(boolean canHydrate) {
            this.canHydrate = canHydrate;
            return this;
        }

        public Properties lightLevel(int lightLevel) {
            if (lightLevel < 0 || lightLevel > 15) {
                throw new IllegalArgumentException("The light level should be between [0,15].");
            }
            this.lightLevel = lightLevel;
            return this;
        }

        public Properties density(int density) {
            this.density = density;
            return this;
        }

        public Properties temperature(int temperature) {
            this.temperature = temperature;
            return this;
        }

        public Properties viscosity(int viscosity) {
            if (viscosity < 0) {
                throw new IllegalArgumentException("The viscosity should never be negative.");
            }
            this.viscosity = viscosity;
            return this;
        }

        public Properties rarity(Rarity rarity) {
            this.rarity = rarity;
            return this;
        }
    }
}

