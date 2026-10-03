/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.mojang.blaze3d.platform.NativeImage
 *  com.mojang.blaze3d.platform.Window
 *  com.mojang.blaze3d.shaders.FogShape
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  com.mojang.datafixers.util.Either
 *  net.minecraft.FileUtil
 *  net.minecraft.client.Camera
 *  net.minecraft.client.KeyMapping
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.MouseHandler
 *  net.minecraft.client.Options
 *  net.minecraft.client.color.block.BlockColors
 *  net.minecraft.client.color.item.ItemColors
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.LerpingBossEvent
 *  net.minecraft.client.gui.components.toasts.Toast
 *  net.minecraft.client.gui.screens.ConfirmScreen
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.gui.screens.TitleScreen
 *  net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent
 *  net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner
 *  net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen
 *  net.minecraft.client.model.HumanoidModel
 *  net.minecraft.client.model.Model
 *  net.minecraft.client.model.geom.ModelLayerLocation
 *  net.minecraft.client.model.geom.builders.LayerDefinition
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.multiplayer.MultiPlayerGameMode
 *  net.minecraft.client.multiplayer.PlayerInfo
 *  net.minecraft.client.multiplayer.ServerData
 *  net.minecraft.client.particle.ParticleEngine
 *  net.minecraft.client.particle.ParticleRenderType
 *  net.minecraft.client.player.AbstractClientPlayer
 *  net.minecraft.client.player.Input
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.client.renderer.FogRenderer$FogMode
 *  net.minecraft.client.renderer.GameRenderer
 *  net.minecraft.client.renderer.LevelRenderer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.ShaderInstance
 *  net.minecraft.client.renderer.block.BlockRenderDispatcher
 *  net.minecraft.client.renderer.block.model.BakedQuad
 *  net.minecraft.client.renderer.culling.Frustum
 *  net.minecraft.client.renderer.texture.SpriteContents
 *  net.minecraft.client.renderer.texture.TextureAtlas
 *  net.minecraft.client.renderer.texture.TextureAtlasSprite
 *  net.minecraft.client.resources.language.I18n
 *  net.minecraft.client.resources.metadata.animation.AnimationMetadataSection
 *  net.minecraft.client.resources.metadata.animation.FrameSize
 *  net.minecraft.client.resources.model.BakedModel
 *  net.minecraft.client.resources.model.Material
 *  net.minecraft.client.resources.model.ModelBakery
 *  net.minecraft.client.resources.model.ModelManager
 *  net.minecraft.client.resources.sounds.SoundInstance
 *  net.minecraft.client.sounds.SoundEngine
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.locale.Language
 *  net.minecraft.network.Connection
 *  net.minecraft.network.chat.ChatType
 *  net.minecraft.network.chat.ChatType$Bound
 *  net.minecraft.network.chat.ChatTypeDecoration
 *  net.minecraft.network.chat.ChatTypeDecoration$Parameter
 *  net.minecraft.network.chat.CommonComponents
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.network.chat.PlayerChatMessage
 *  net.minecraft.network.chat.Style
 *  net.minecraft.network.protocol.status.ServerStatus
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.packs.resources.ReloadableResourceManager
 *  net.minecraft.server.packs.resources.Resource
 *  net.minecraft.util.FormattedCharSequence
 *  net.minecraft.util.Mth
 *  net.minecraft.util.RandomSource
 *  net.minecraft.util.profiling.ProfilerFiller
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.effect.MobEffectInstance
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.entity.HumanoidArm
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.ai.attributes.AttributeInstance
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.tooltip.TooltipComponent
 *  net.minecraft.world.item.CreativeModeTab
 *  net.minecraft.world.item.CreativeModeTab$DisplayItemsGenerator
 *  net.minecraft.world.item.CreativeModeTab$ItemDisplayParameters
 *  net.minecraft.world.item.CreativeModeTab$Output
 *  net.minecraft.world.item.ItemDisplayContext
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.RecipeManager
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.GameType
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.material.FluidState
 *  net.minecraft.world.level.material.FogType
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.EntityHitResult
 *  net.minecraft.world.phys.HitResult
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.eventbus.api.Event
 *  net.minecraftforge.eventbus.api.Event$Result
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.IExtensionPoint$DisplayTest
 *  net.minecraftforge.fml.ModList
 *  net.minecraftforge.fml.ModLoader
 *  net.minecraftforge.fml.VersionChecker$Status
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.apache.logging.log4j.Marker
 *  org.apache.logging.log4j.MarkerManager
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Matrix4f
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package net.minecraftforge.client;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Either;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.FileUtil;
import net.minecraft.client.Camera;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.Options;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.locale.Language;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.ChatTypeDecoration;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.status.ServerStatus;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ColorResolverManager;
import net.minecraftforge.client.DimensionSpecialEffectsManager;
import net.minecraftforge.client.EntitySpectatorShaderManager;
import net.minecraftforge.client.ExtendedServerListData;
import net.minecraftforge.client.ItemDecoratorHandler;
import net.minecraftforge.client.NamedRenderTypeManager;
import net.minecraftforge.client.PresetEditorManager;
import net.minecraftforge.client.RecipeBookManager;
import net.minecraftforge.client.RenderTypeHelper;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.ClientPlayerChangeGameTypeEvent;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.ComputeFovModifierEvent;
import net.minecraftforge.client.event.CustomizeGuiOverlayEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.MovementInputUpdateEvent;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.client.event.RenderArmEvent;
import net.minecraftforge.client.event.RenderBlockScreenEffectEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderHighlightEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.client.event.ScreenshotEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.event.ToastAddEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.client.extensions.common.IClientMobEffectExtensions;
import net.minecraftforge.client.gui.ClientTooltipComponentManager;
import net.minecraftforge.client.gui.overlay.GuiOverlayManager;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.textures.ForgeTextureMetadata;
import net.minecraftforge.client.textures.TextureAtlasSpriteLoaderManager;
import net.minecraftforge.common.ForgeConfig;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.ForgeI18n;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.VersionChecker;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.snapshots.ForgeSnapshotsModClient;
import net.minecraftforge.gametest.ForgeGameTestHooks;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.ServerStatusPing;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.versions.forge.ForgeVersion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3fc;

@ApiStatus.Internal
public class ForgeHooksClient {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Marker CLIENTHOOKS = MarkerManager.getMarker((String)"CLIENTHOOKS");
    private static final Stack<Screen> guiLayers = new Stack();
    public static String forgeStatusLine;
    private static int slotMainHand;
    private static final Map<ModelLayerLocation, Supplier<LayerDefinition>> layerDefinitions;
    private static final ResourceLocation ICON_SHEET;
    private static final ChatTypeDecoration SYSTEM_CHAT_TYPE_DECORATION;
    private static final ChatType SYSTEM_CHAT_TYPE;
    private static final ChatType.Bound SYSTEM_CHAT_TYPE_BOUND;
    private static boolean initializedClientHooks;

    public static void resizeGuiLayers(Minecraft minecraft, int width, int height) {
        guiLayers.forEach(screen -> screen.m_6574_(minecraft, width, height));
    }

    public static void clearGuiLayers(Minecraft minecraft) {
        while (guiLayers.size() > 0) {
            ForgeHooksClient.popGuiLayerInternal(minecraft);
        }
    }

    private static void popGuiLayerInternal(Minecraft minecraft) {
        if (minecraft.f_91080_ != null) {
            minecraft.f_91080_.m_7861_();
        }
        minecraft.f_91080_ = guiLayers.pop();
    }

    public static void pushGuiLayer(Minecraft minecraft, Screen screen) {
        if (minecraft.f_91080_ != null) {
            guiLayers.push(minecraft.f_91080_);
        }
        minecraft.f_91080_ = Objects.requireNonNull(screen);
        screen.m_6575_(minecraft, minecraft.m_91268_().m_85445_(), minecraft.m_91268_().m_85446_());
        minecraft.m_240477_().m_168785_(screen.m_142562_());
    }

    public static void popGuiLayer(Minecraft minecraft) {
        if (guiLayers.size() == 0) {
            minecraft.m_91152_(null);
            return;
        }
        ForgeHooksClient.popGuiLayerInternal(minecraft);
        if (minecraft.f_91080_ != null) {
            minecraft.m_240477_().m_168785_(minecraft.f_91080_.m_142562_());
        }
    }

    public static float getGuiFarPlane() {
        return 1000.0f + 10000.0f * (float)(1 + guiLayers.size());
    }

    public static String getArmorTexture(Entity entity, ItemStack armor, String _default, EquipmentSlot slot, String type) {
        String result = armor.m_41720_().getArmorTexture(armor, entity, slot, type);
        return result != null ? result : _default;
    }

    public static boolean onDrawHighlight(LevelRenderer context, Camera camera, HitResult target, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource) {
        switch (target.m_6662_()) {
            case BLOCK: {
                if (!(target instanceof BlockHitResult)) {
                    return false;
                }
                BlockHitResult blockTarget = (BlockHitResult)target;
                return MinecraftForge.EVENT_BUS.post((Event)new RenderHighlightEvent.Block(context, camera, blockTarget, partialTick, poseStack, bufferSource));
            }
            case ENTITY: {
                if (!(target instanceof EntityHitResult)) {
                    return false;
                }
                EntityHitResult entityTarget = (EntityHitResult)target;
                return MinecraftForge.EVENT_BUS.post((Event)new RenderHighlightEvent.Entity(context, camera, entityTarget, partialTick, poseStack, bufferSource));
            }
        }
        return false;
    }

    public static void dispatchRenderStage(RenderLevelStageEvent.Stage stage, LevelRenderer levelRenderer, PoseStack poseStack, Matrix4f projectionMatrix, int renderTick, Camera camera, Frustum frustum) {
        Minecraft mc = Minecraft.m_91087_();
        ProfilerFiller profiler = mc.m_91307_();
        profiler.m_6180_(stage.toString());
        MinecraftForge.EVENT_BUS.post((Event)new RenderLevelStageEvent(stage, levelRenderer, poseStack, projectionMatrix, renderTick, mc.getPartialTick(), camera, frustum));
        profiler.m_7238_();
    }

    public static void dispatchRenderStage(RenderType renderType, LevelRenderer levelRenderer, PoseStack poseStack, Matrix4f projectionMatrix, int renderTick, Camera camera, Frustum frustum) {
        RenderLevelStageEvent.Stage stage = RenderLevelStageEvent.Stage.fromRenderType(renderType);
        if (stage != null) {
            ForgeHooksClient.dispatchRenderStage(stage, levelRenderer, poseStack, projectionMatrix, renderTick, camera, frustum);
        }
    }

    public static boolean renderSpecificFirstPersonHand(InteractionHand hand, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, float partialTick, float interpPitch, float swingProgress, float equipProgress, ItemStack stack) {
        return MinecraftForge.EVENT_BUS.post((Event)new RenderHandEvent(hand, poseStack, bufferSource, packedLight, partialTick, interpPitch, swingProgress, equipProgress, stack));
    }

    public static boolean renderSpecificFirstPersonArm(PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, AbstractClientPlayer player, HumanoidArm arm) {
        return MinecraftForge.EVENT_BUS.post((Event)new RenderArmEvent(poseStack, multiBufferSource, packedLight, player, arm));
    }

    public static void onTextureStitchedPost(TextureAtlas map) {
        ModLoader.get().postEvent((Event)new TextureStitchEvent.Post(map));
    }

    public static void onBlockColorsInit(BlockColors blockColors) {
        ModLoader.get().postEvent((Event)new RegisterColorHandlersEvent.Block(blockColors));
    }

    public static void onItemColorsInit(ItemColors itemColors, BlockColors blockColors) {
        ModLoader.get().postEvent((Event)new RegisterColorHandlersEvent.Item(itemColors, blockColors));
    }

    public static Model getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlot slot, HumanoidModel<?> _default) {
        return IClientItemExtensions.of(itemStack).getGenericArmorModel(entityLiving, itemStack, slot, _default);
    }

    public static <T extends LivingEntity> void copyModelProperties(HumanoidModel<T> original, HumanoidModel<?> replacement) {
        original.m_102872_(replacement);
        replacement.f_102808_.f_104207_ = original.f_102808_.f_104207_;
        replacement.f_102809_.f_104207_ = original.f_102809_.f_104207_;
        replacement.f_102810_.f_104207_ = original.f_102810_.f_104207_;
        replacement.f_102811_.f_104207_ = original.f_102811_.f_104207_;
        replacement.f_102812_.f_104207_ = original.f_102812_.f_104207_;
        replacement.f_102813_.f_104207_ = original.f_102813_.f_104207_;
        replacement.f_102814_.f_104207_ = original.f_102814_.f_104207_;
    }

    public static String fixDomain(String base, String complex) {
        int idx = complex.indexOf(58);
        if (idx == -1) {
            return base + complex;
        }
        String name = complex.substring(idx + 1, complex.length());
        if (idx > 1) {
            String domain = complex.substring(0, idx);
            return domain + ":" + base + name;
        }
        return base + name;
    }

    public static float getFieldOfViewModifier(Player entity, float fovModifier) {
        ComputeFovModifierEvent fovModifierEvent = new ComputeFovModifierEvent(entity, fovModifier);
        MinecraftForge.EVENT_BUS.post((Event)fovModifierEvent);
        return fovModifierEvent.getNewFovModifier();
    }

    public static double getFieldOfView(GameRenderer renderer, Camera camera, double partialTick, double fov, boolean usedConfiguredFov) {
        ViewportEvent.ComputeFov event = new ViewportEvent.ComputeFov(renderer, camera, partialTick, fov, usedConfiguredFov);
        MinecraftForge.EVENT_BUS.post((Event)event);
        return event.getFOV();
    }

    public static void renderMainMenu(TitleScreen gui, GuiGraphics guiGraphics, Font font, int width, int height, int alpha) {
        VersionChecker.Status status = ForgeVersion.getStatus();
        ForgeSnapshotsModClient.renderMainMenuWarning(status, gui, guiGraphics, font, width, height, alpha);
        forgeStatusLine = switch (status) {
            case VersionChecker.Status.OUTDATED, VersionChecker.Status.BETA_OUTDATED -> I18n.m_118938_((String)"forge.update.newversion", (Object[])new Object[]{ForgeVersion.getTarget()});
            default -> null;
        };
    }

    @Nullable
    public static SoundInstance playSound(SoundEngine manager, SoundInstance sound) {
        PlaySoundEvent e = new PlaySoundEvent(manager, sound);
        MinecraftForge.EVENT_BUS.post((Event)e);
        return e.getSound();
    }

    public static void drawScreen(Screen screen, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.m_280168_().m_85836_();
        guiLayers.forEach(layer -> {
            ForgeHooksClient.drawScreenInternal(layer, guiGraphics, Integer.MAX_VALUE, Integer.MAX_VALUE, partialTick);
            guiGraphics.m_280168_().m_252880_(0.0f, 0.0f, 2000.0f);
        });
        ForgeHooksClient.drawScreenInternal(screen, guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.m_280168_().m_85849_();
    }

    private static void drawScreenInternal(Screen screen, GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (!MinecraftForge.EVENT_BUS.post((Event)new ScreenEvent.Render.Pre(screen, guiGraphics, mouseX, mouseY, partialTick))) {
            screen.m_280264_(guiGraphics, mouseX, mouseY, partialTick);
        }
        MinecraftForge.EVENT_BUS.post((Event)new ScreenEvent.Render.Post(screen, guiGraphics, mouseX, mouseY, partialTick));
    }

    public static Vector3f getFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, float fogRed, float fogGreen, float fogBlue) {
        FluidState state = level.m_6425_(camera.m_90588_());
        Vector3f fluidFogColor = new Vector3f(fogRed, fogGreen, fogBlue);
        if (camera.m_90583_().f_82480_ < (double)((float)camera.m_90588_().m_123342_() + state.m_76155_((BlockGetter)level, camera.m_90588_()))) {
            fluidFogColor = IClientFluidTypeExtensions.of(state).modifyFogColor(camera, partialTick, level, renderDistance, darkenWorldAmount, fluidFogColor);
        }
        ViewportEvent.ComputeFogColor event = new ViewportEvent.ComputeFogColor(camera, partialTick, fluidFogColor.x(), fluidFogColor.y(), fluidFogColor.z());
        MinecraftForge.EVENT_BUS.post((Event)event);
        fluidFogColor.set(event.getRed(), event.getGreen(), event.getBlue());
        return fluidFogColor;
    }

    public static void onFogRender(FogRenderer.FogMode mode, FogType type, Camera camera, float partialTick, float renderDistance, float nearDistance, float farDistance, FogShape shape) {
        ViewportEvent.RenderFog event;
        FluidState state = camera.m_90592_().m_9236_().m_6425_(camera.m_90588_());
        if (camera.m_90583_().f_82480_ < (double)((float)camera.m_90588_().m_123342_() + state.m_76155_((BlockGetter)camera.m_90592_().m_9236_(), camera.m_90588_()))) {
            IClientFluidTypeExtensions.of(state).modifyFogRender(camera, mode, renderDistance, partialTick, nearDistance, farDistance, shape);
        }
        if (MinecraftForge.EVENT_BUS.post((Event)(event = new ViewportEvent.RenderFog(mode, type, camera, partialTick, nearDistance, farDistance, shape)))) {
            RenderSystem.setShaderFogStart((float)event.getNearPlaneDistance());
            RenderSystem.setShaderFogEnd((float)event.getFarPlaneDistance());
            RenderSystem.setShaderFogShape((FogShape)event.getFogShape());
        }
    }

    public static ViewportEvent.ComputeCameraAngles onCameraSetup(GameRenderer renderer, Camera camera, float partial) {
        ViewportEvent.ComputeCameraAngles event = new ViewportEvent.ComputeCameraAngles(renderer, camera, partial, camera.m_90590_(), camera.m_90589_(), 0.0f);
        MinecraftForge.EVENT_BUS.post((Event)event);
        return event;
    }

    public static void onModifyBakingResult(Map<ResourceLocation, BakedModel> models, ModelBakery modelBakery) {
        ModLoader.get().postEvent((Event)new ModelEvent.ModifyBakingResult(models, modelBakery));
    }

    public static void onModelBake(ModelManager modelManager, Map<ResourceLocation, BakedModel> models, ModelBakery modelBakery) {
        ModLoader.get().postEvent((Event)new ModelEvent.BakingCompleted(modelManager, Collections.unmodifiableMap(models), modelBakery));
    }

    public static BakedModel handleCameraTransforms(PoseStack poseStack, BakedModel model, ItemDisplayContext cameraTransformType, boolean applyLeftHandTransform) {
        model = model.applyTransform(cameraTransformType, poseStack, applyLeftHandTransform);
        return model;
    }

    public static TextureAtlasSprite[] getFluidSprites(BlockAndTintGetter level, BlockPos pos, FluidState fluidStateIn) {
        IClientFluidTypeExtensions props = IClientFluidTypeExtensions.of(fluidStateIn);
        ResourceLocation overlayTexture = props.getOverlayTexture(fluidStateIn, level, pos);
        return new TextureAtlasSprite[]{(TextureAtlasSprite)Minecraft.m_91087_().m_91258_(TextureAtlas.f_118259_).apply(props.getStillTexture(fluidStateIn, level, pos)), (TextureAtlasSprite)Minecraft.m_91087_().m_91258_(TextureAtlas.f_118259_).apply(props.getFlowingTexture(fluidStateIn, level, pos)), overlayTexture == null ? null : (TextureAtlasSprite)Minecraft.m_91087_().m_91258_(TextureAtlas.f_118259_).apply(overlayTexture)};
    }

    public static Material getBlockMaterial(ResourceLocation loc) {
        return new Material(TextureAtlas.f_118259_, loc);
    }

    public static void fillNormal(int[] faceData, Direction facing) {
        ForgeHooksClient.fillNormal(faceData, facing, false);
    }

    public static void fillNormal(int[] faceData, Direction facing, boolean calculateNormals) {
        Vector3f v2;
        if (calculateNormals || ForgeConfig.CLIENT.calculateAllNormals()) {
            Vector3f v1 = ForgeHooksClient.getVertexPos(faceData, 3);
            Vector3f t1 = ForgeHooksClient.getVertexPos(faceData, 1);
            v2 = ForgeHooksClient.getVertexPos(faceData, 2);
            Vector3f t2 = ForgeHooksClient.getVertexPos(faceData, 0);
            v1.sub((Vector3fc)t1);
            v2.sub((Vector3fc)t2);
            v2.cross((Vector3fc)v1);
            v2.normalize();
        } else {
            v2 = new Vector3f((float)facing.m_122436_().m_123341_(), (float)facing.m_122436_().m_123342_(), (float)facing.m_122436_().m_123343_());
        }
        int x = (byte)Math.round(v2.x() * 127.0f) & 0xFF;
        int y = (byte)Math.round(v2.y() * 127.0f) & 0xFF;
        int z = (byte)Math.round(v2.z() * 127.0f) & 0xFF;
        int normal = x | y << 8 | z << 16;
        for (int i = 0; i < 4; ++i) {
            faceData[i * 8 + 7] = normal;
        }
    }

    private static Vector3f getVertexPos(int[] data, int vertex) {
        int idx = vertex * 8;
        float x = Float.intBitsToFloat(data[idx]);
        float y = Float.intBitsToFloat(data[idx + 1]);
        float z = Float.intBitsToFloat(data[idx + 2]);
        return new Vector3f(x, y, z);
    }

    public static boolean calculateFaceWithoutAO(BlockAndTintGetter getter, BlockState state, BlockPos pos, BakedQuad quad, boolean isFaceCubic, float[] brightness, int[] lightmap) {
        if (quad.hasAmbientOcclusion()) {
            return false;
        }
        BlockPos lightmapPos = isFaceCubic ? pos.m_121945_(quad.m_111306_()) : pos;
        brightness[2] = brightness[3] = getter.m_7717_(quad.m_111306_(), quad.m_111307_());
        brightness[1] = brightness[3];
        brightness[0] = brightness[3];
        lightmap[2] = lightmap[3] = LevelRenderer.m_109537_((BlockAndTintGetter)getter, (BlockState)state, (BlockPos)lightmapPos);
        lightmap[1] = lightmap[3];
        lightmap[0] = lightmap[3];
        return true;
    }

    public static void loadEntityShader(Entity entity, GameRenderer entityRenderer) {
        ResourceLocation shader;
        if (entity != null && (shader = EntitySpectatorShaderManager.get(entity.m_6095_())) != null) {
            entityRenderer.m_109128_(shader);
        }
    }

    public static boolean shouldCauseReequipAnimation(@NotNull ItemStack from, @NotNull ItemStack to, int slot) {
        boolean fromInvalid = from.m_41619_();
        boolean toInvalid = to.m_41619_();
        if (fromInvalid && toInvalid) {
            return false;
        }
        if (fromInvalid || toInvalid) {
            return true;
        }
        boolean changed = false;
        if (slot != -1) {
            changed = slot != slotMainHand;
            slotMainHand = slot;
        }
        return from.m_41720_().shouldCauseReequipAnimation(from, to, changed);
    }

    public static CustomizeGuiOverlayEvent.BossEventProgress onCustomizeBossEventProgress(GuiGraphics guiGraphics, Window window, LerpingBossEvent bossInfo, int x, int y, int increment) {
        CustomizeGuiOverlayEvent.BossEventProgress evt = new CustomizeGuiOverlayEvent.BossEventProgress(window, guiGraphics, Minecraft.m_91087_().getPartialTick(), bossInfo, x, y, increment);
        MinecraftForge.EVENT_BUS.post((Event)evt);
        return evt;
    }

    public static ScreenshotEvent onScreenshot(NativeImage image, File screenshotFile) {
        ScreenshotEvent event = new ScreenshotEvent(image, screenshotFile);
        MinecraftForge.EVENT_BUS.post((Event)event);
        return event;
    }

    public static void onClientChangeGameType(PlayerInfo info, GameType currentGameMode, GameType newGameMode) {
        if (currentGameMode != newGameMode) {
            ClientPlayerChangeGameTypeEvent evt = new ClientPlayerChangeGameTypeEvent(info, currentGameMode, newGameMode);
            MinecraftForge.EVENT_BUS.post((Event)evt);
        }
    }

    public static void onMovementInputUpdate(Player player, Input movementInput) {
        MinecraftForge.EVENT_BUS.post((Event)new MovementInputUpdateEvent(player, movementInput));
    }

    public static boolean onScreenMouseClickedPre(Screen guiScreen, double mouseX, double mouseY, int button) {
        ScreenEvent.MouseButtonPressed.Pre event = new ScreenEvent.MouseButtonPressed.Pre(guiScreen, mouseX, mouseY, button);
        return MinecraftForge.EVENT_BUS.post((Event)event);
    }

    public static boolean onScreenMouseClickedPost(Screen guiScreen, double mouseX, double mouseY, int button, boolean handled) {
        ScreenEvent.MouseButtonPressed.Post event = new ScreenEvent.MouseButtonPressed.Post(guiScreen, mouseX, mouseY, button, handled);
        MinecraftForge.EVENT_BUS.post((Event)event);
        return event.getResult() == Event.Result.DEFAULT ? handled : event.getResult() == Event.Result.ALLOW;
    }

    public static boolean onScreenMouseReleasedPre(Screen guiScreen, double mouseX, double mouseY, int button) {
        ScreenEvent.MouseButtonReleased.Pre event = new ScreenEvent.MouseButtonReleased.Pre(guiScreen, mouseX, mouseY, button);
        return MinecraftForge.EVENT_BUS.post((Event)event);
    }

    public static boolean onScreenMouseReleasedPost(Screen guiScreen, double mouseX, double mouseY, int button, boolean handled) {
        ScreenEvent.MouseButtonReleased.Post event = new ScreenEvent.MouseButtonReleased.Post(guiScreen, mouseX, mouseY, button, handled);
        MinecraftForge.EVENT_BUS.post((Event)event);
        return event.getResult() == Event.Result.DEFAULT ? handled : event.getResult() == Event.Result.ALLOW;
    }

    public static boolean onScreenMouseDragPre(Screen guiScreen, double mouseX, double mouseY, int mouseButton, double dragX, double dragY) {
        ScreenEvent.MouseDragged.Pre event = new ScreenEvent.MouseDragged.Pre(guiScreen, mouseX, mouseY, mouseButton, dragX, dragY);
        return MinecraftForge.EVENT_BUS.post((Event)event);
    }

    public static void onScreenMouseDragPost(Screen guiScreen, double mouseX, double mouseY, int mouseButton, double dragX, double dragY) {
        ScreenEvent.MouseDragged.Post event = new ScreenEvent.MouseDragged.Post(guiScreen, mouseX, mouseY, mouseButton, dragX, dragY);
        MinecraftForge.EVENT_BUS.post((Event)event);
    }

    public static boolean onScreenMouseScrollPre(MouseHandler mouseHelper, Screen guiScreen, double scrollDelta) {
        Window mainWindow = guiScreen.getMinecraft().m_91268_();
        double mouseX = mouseHelper.m_91589_() * (double)mainWindow.m_85445_() / (double)mainWindow.m_85443_();
        double mouseY = mouseHelper.m_91594_() * (double)mainWindow.m_85446_() / (double)mainWindow.m_85444_();
        ScreenEvent.MouseScrolled.Pre event = new ScreenEvent.MouseScrolled.Pre(guiScreen, mouseX, mouseY, scrollDelta);
        return MinecraftForge.EVENT_BUS.post((Event)event);
    }

    public static void onScreenMouseScrollPost(MouseHandler mouseHelper, Screen guiScreen, double scrollDelta) {
        Window mainWindow = guiScreen.getMinecraft().m_91268_();
        double mouseX = mouseHelper.m_91589_() * (double)mainWindow.m_85445_() / (double)mainWindow.m_85443_();
        double mouseY = mouseHelper.m_91594_() * (double)mainWindow.m_85446_() / (double)mainWindow.m_85444_();
        ScreenEvent.MouseScrolled.Post event = new ScreenEvent.MouseScrolled.Post(guiScreen, mouseX, mouseY, scrollDelta);
        MinecraftForge.EVENT_BUS.post((Event)event);
    }

    public static boolean onScreenKeyPressedPre(Screen guiScreen, int keyCode, int scanCode, int modifiers) {
        ScreenEvent.KeyPressed.Pre event = new ScreenEvent.KeyPressed.Pre(guiScreen, keyCode, scanCode, modifiers);
        return MinecraftForge.EVENT_BUS.post((Event)event);
    }

    public static boolean onScreenKeyPressedPost(Screen guiScreen, int keyCode, int scanCode, int modifiers) {
        ScreenEvent.KeyPressed.Post event = new ScreenEvent.KeyPressed.Post(guiScreen, keyCode, scanCode, modifiers);
        return MinecraftForge.EVENT_BUS.post((Event)event);
    }

    public static boolean onScreenKeyReleasedPre(Screen guiScreen, int keyCode, int scanCode, int modifiers) {
        ScreenEvent.KeyReleased.Pre event = new ScreenEvent.KeyReleased.Pre(guiScreen, keyCode, scanCode, modifiers);
        return MinecraftForge.EVENT_BUS.post((Event)event);
    }

    public static boolean onScreenKeyReleasedPost(Screen guiScreen, int keyCode, int scanCode, int modifiers) {
        ScreenEvent.KeyReleased.Post event = new ScreenEvent.KeyReleased.Post(guiScreen, keyCode, scanCode, modifiers);
        return MinecraftForge.EVENT_BUS.post((Event)event);
    }

    public static boolean onScreenCharTypedPre(Screen guiScreen, char codePoint, int modifiers) {
        ScreenEvent.CharacterTyped.Pre event = new ScreenEvent.CharacterTyped.Pre(guiScreen, codePoint, modifiers);
        return MinecraftForge.EVENT_BUS.post((Event)event);
    }

    public static void onScreenCharTypedPost(Screen guiScreen, char codePoint, int modifiers) {
        ScreenEvent.CharacterTyped.Post event = new ScreenEvent.CharacterTyped.Post(guiScreen, codePoint, modifiers);
        MinecraftForge.EVENT_BUS.post((Event)event);
    }

    public static void onRecipesUpdated(RecipeManager mgr) {
        RecipesUpdatedEvent event = new RecipesUpdatedEvent(mgr);
        MinecraftForge.EVENT_BUS.post((Event)event);
    }

    public static boolean onMouseButtonPre(int button, int action, int mods) {
        return MinecraftForge.EVENT_BUS.post((Event)new InputEvent.MouseButton.Pre(button, action, mods));
    }

    public static void onMouseButtonPost(int button, int action, int mods) {
        MinecraftForge.EVENT_BUS.post((Event)new InputEvent.MouseButton.Post(button, action, mods));
    }

    public static boolean onMouseScroll(MouseHandler mouseHelper, double scrollDelta) {
        InputEvent.MouseScrollingEvent event = new InputEvent.MouseScrollingEvent(scrollDelta, mouseHelper.m_91560_(), mouseHelper.m_168090_(), mouseHelper.m_91584_(), mouseHelper.m_91589_(), mouseHelper.m_91594_());
        return MinecraftForge.EVENT_BUS.post((Event)event);
    }

    public static void onKeyInput(int key, int scanCode, int action, int modifiers) {
        MinecraftForge.EVENT_BUS.post((Event)new InputEvent.Key(key, scanCode, action, modifiers));
    }

    public static InputEvent.InteractionKeyMappingTriggered onClickInput(int button, KeyMapping keyBinding, InteractionHand hand) {
        InputEvent.InteractionKeyMappingTriggered event = new InputEvent.InteractionKeyMappingTriggered(button, keyBinding, hand);
        MinecraftForge.EVENT_BUS.post((Event)event);
        return event;
    }

    public static boolean isNameplateInRenderDistance(Entity entity, double squareDistance) {
        AttributeInstance attribute;
        if (entity instanceof LivingEntity && (attribute = ((LivingEntity)entity).m_21051_(ForgeMod.NAMETAG_DISTANCE.get())) != null) {
            return !(squareDistance > attribute.m_22135_() * attribute.m_22135_());
        }
        return !(squareDistance > 4096.0);
    }

    public static void renderPistonMovedBlocks(BlockPos pos, BlockState state, PoseStack stack, MultiBufferSource bufferSource, Level level, boolean checkSides, int packedOverlay, BlockRenderDispatcher blockRenderer) {
        BakedModel model = blockRenderer.m_110910_(state);
        for (RenderType renderType : model.getRenderTypes(state, RandomSource.m_216335_((long)state.m_60726_(pos)), ModelData.EMPTY)) {
            VertexConsumer vertexConsumer = bufferSource.m_6299_(RenderTypeHelper.getMovingBlockRenderType(renderType));
            blockRenderer.m_110937_().tesselateBlock((BlockAndTintGetter)level, model, state, pos, stack, vertexConsumer, checkSides, RandomSource.m_216327_(), state.m_60726_(pos), packedOverlay, ModelData.EMPTY, renderType);
        }
    }

    public static boolean shouldRenderEffect(MobEffectInstance effectInstance) {
        return IClientMobEffectExtensions.of(effectInstance).isVisibleInInventory(effectInstance);
    }

    @Nullable
    public static SpriteContents loadSpriteContents(ResourceLocation name, Resource resource, FrameSize frameSize, NativeImage image, AnimationMetadataSection animationMeta) {
        try {
            ForgeTextureMetadata forgeMeta = ForgeTextureMetadata.forResource(resource);
            return forgeMeta.getLoader() == null ? null : forgeMeta.getLoader().loadContents(name, resource, frameSize, image, animationMeta, forgeMeta);
        }
        catch (IOException e) {
            LOGGER.error("Unable to get Forge metadata for {}, falling back to vanilla loading", (Object)name);
            e.printStackTrace();
            return null;
        }
    }

    @Nullable
    public static TextureAtlasSprite loadTextureAtlasSprite(ResourceLocation atlasName, SpriteContents contents, int atlasWidth, int atlasHeight, int spriteX, int spriteY, int mipmapLevel) {
        if (contents.forgeMeta == null || contents.forgeMeta.getLoader() == null) {
            return null;
        }
        return contents.forgeMeta.getLoader().makeSprite(atlasName, contents, atlasWidth, atlasHeight, spriteX, spriteY, mipmapLevel);
    }

    public static void registerLayerDefinition(ModelLayerLocation layerLocation, Supplier<LayerDefinition> supplier) {
        layerDefinitions.put(layerLocation, supplier);
    }

    public static void loadLayerDefinitions(ImmutableMap.Builder<ModelLayerLocation, LayerDefinition> builder) {
        layerDefinitions.forEach((k, v) -> builder.put(k, (Object)((LayerDefinition)v.get())));
    }

    public static void processForgeListPingData(ServerStatus packet, ServerData target) {
        packet.forgeData().ifPresentOrElse(forgeData -> {
            Map<String, String> mods = forgeData.getRemoteModData();
            Map<ResourceLocation, ServerStatusPing.ChannelData> remoteChannels = forgeData.getRemoteChannels();
            int fmlver = forgeData.getFMLNetworkVersion();
            boolean fmlNetMatches = fmlver == 3;
            boolean channelsMatch = NetworkRegistry.checkListPingCompatibilityForClient(remoteChannels);
            AtomicBoolean result = new AtomicBoolean(true);
            ArrayList extraClientMods = new ArrayList();
            ModList.get().forEachModContainer((modid, mc) -> mc.getCustomExtension(IExtensionPoint.DisplayTest.class).ifPresent(ext -> {
                boolean foundModOnServer = ext.remoteVersionTest().test((String)mods.get(modid), true);
                result.compareAndSet(true, foundModOnServer);
                if (!foundModOnServer) {
                    extraClientMods.add(modid);
                }
            }));
            boolean modsMatch = result.get();
            Map<String, String> extraServerMods = mods.entrySet().stream().filter(e -> !Objects.equals("OHNOES\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31", e.getValue())).filter(e -> !ModList.get().isLoaded((String)e.getKey())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            LOGGER.debug(CLIENTHOOKS, "Received FML ping data from server at {}: FMLNETVER={}, mod list is compatible : {}, channel list is compatible: {}, extra server mods: {}", (Object)target.f_105363_, (Object)fmlver, (Object)modsMatch, (Object)channelsMatch, extraServerMods);
            String extraReason = null;
            if (!extraServerMods.isEmpty()) {
                extraReason = "fml.menu.multiplayer.extraservermods";
                LOGGER.info(CLIENTHOOKS, ForgeI18n.parseMessage(extraReason, new Object[0]) + ": {}", (Object)extraServerMods.entrySet().stream().map(e -> (String)e.getKey() + "@" + (String)e.getValue()).collect(Collectors.joining(", ")));
            }
            if (!modsMatch) {
                extraReason = "fml.menu.multiplayer.modsincompatible";
                LOGGER.info(CLIENTHOOKS, "Client has mods that are missing on server: {}", extraClientMods);
            }
            if (!channelsMatch) {
                extraReason = "fml.menu.multiplayer.networkincompatible";
            }
            if (fmlver < 3) {
                extraReason = "fml.menu.multiplayer.serveroutdated";
            }
            if (fmlver > 3) {
                extraReason = "fml.menu.multiplayer.clientoutdated";
            }
            target.forgeData = new ExtendedServerListData("FML", extraServerMods.isEmpty() && fmlNetMatches && channelsMatch && modsMatch, mods.size(), extraReason, forgeData.isTruncated());
        }, () -> {
            target.forgeData = new ExtendedServerListData("VANILLA", NetworkRegistry.canConnectToVanillaServer(), 0, null);
        });
    }

    public static void drawForgePingInfo(JoinMultiplayerScreen gui, ServerData target, GuiGraphics guiGraphics, int x, int y, int width, int relativeMouseX, int relativeMouseY) {
        Object tooltip;
        int idx;
        if (target.forgeData == null) {
            return;
        }
        switch (target.forgeData.type()) {
            case "FML": {
                if (target.forgeData.isCompatible()) {
                    idx = 0;
                    tooltip = ForgeI18n.parseMessage("fml.menu.multiplayer.compatible", target.forgeData.numberOfMods());
                } else {
                    idx = 16;
                    if (target.forgeData.extraReason() != null) {
                        String extraReason = ForgeI18n.parseMessage(target.forgeData.extraReason(), new Object[0]);
                        tooltip = ForgeI18n.parseMessage("fml.menu.multiplayer.incompatible.extra", extraReason);
                    } else {
                        tooltip = ForgeI18n.parseMessage("fml.menu.multiplayer.incompatible", new Object[0]);
                    }
                }
                if (!target.forgeData.truncated()) break;
                tooltip = (String)tooltip + "\n" + ForgeI18n.parseMessage("fml.menu.multiplayer.truncated", new Object[0]);
                break;
            }
            case "VANILLA": {
                if (target.forgeData.isCompatible()) {
                    idx = 48;
                    tooltip = ForgeI18n.parseMessage("fml.menu.multiplayer.vanilla", new Object[0]);
                    break;
                }
                idx = 80;
                tooltip = ForgeI18n.parseMessage("fml.menu.multiplayer.vanilla.incompatible", new Object[0]);
                break;
            }
            default: {
                idx = 64;
                tooltip = ForgeI18n.parseMessage("fml.menu.multiplayer.unknown", target.forgeData.type());
            }
        }
        guiGraphics.m_280411_(ICON_SHEET, x + width - 18, y + 10, 16, 16, 0.0f, (float)idx, 16, 16, 256, 256);
        if (relativeMouseX > width - 15 && relativeMouseX < width && relativeMouseY > 10 && relativeMouseY < 26) {
            gui.m_99707_(Arrays.stream(((String)tooltip).split("\n")).map(Component::m_237113_).collect(Collectors.toList()));
        }
    }

    private static Connection getClientConnection() {
        return Minecraft.m_91087_().m_91403_() != null ? Minecraft.m_91087_().m_91403_().m_104910_() : null;
    }

    public static void handleClientLevelClosing(ClientLevel level) {
        Connection client = ForgeHooksClient.getClientConnection();
        if (client != null && !client.m_129531_()) {
            GameData.revertToFrozen();
        }
    }

    public static void firePlayerLogin(MultiPlayerGameMode pc, LocalPlayer player, Connection networkManager) {
        MinecraftForge.EVENT_BUS.post((Event)new ClientPlayerNetworkEvent.LoggingIn(pc, player, networkManager));
    }

    public static void firePlayerLogout(@Nullable MultiPlayerGameMode pc, @Nullable LocalPlayer player) {
        MinecraftForge.EVENT_BUS.post((Event)new ClientPlayerNetworkEvent.LoggingOut(pc, player, player != null ? (player.f_108617_ != null ? player.f_108617_.m_104910_() : null) : null));
    }

    public static void firePlayerRespawn(MultiPlayerGameMode pc, LocalPlayer oldPlayer, LocalPlayer newPlayer, Connection networkManager) {
        MinecraftForge.EVENT_BUS.post((Event)new ClientPlayerNetworkEvent.Clone(pc, oldPlayer, newPlayer, networkManager));
    }

    public static void onRegisterParticleProviders(ParticleEngine particleEngine) {
        ModLoader.get().postEvent((Event)new RegisterParticleProvidersEvent(particleEngine));
    }

    public static void onRegisterKeyMappings(Options options) {
        ModLoader.get().postEvent((Event)new RegisterKeyMappingsEvent(options));
    }

    public static void onRegisterAdditionalModels(Set<ResourceLocation> additionalModels) {
        ModLoader.get().postEvent((Event)new ModelEvent.RegisterAdditional(additionalModels));
    }

    @Nullable
    public static Component onClientChat(ChatType.Bound boundChatType, Component message, UUID sender) {
        ClientChatReceivedEvent event = new ClientChatReceivedEvent(boundChatType, message, sender);
        return MinecraftForge.EVENT_BUS.post((Event)event) ? null : event.getMessage();
    }

    @Nullable
    public static Component onClientPlayerChat(ChatType.Bound boundChatType, Component message, PlayerChatMessage playerChatMessage, UUID sender) {
        ClientChatReceivedEvent.Player event = new ClientChatReceivedEvent.Player(boundChatType, message, playerChatMessage, sender);
        return MinecraftForge.EVENT_BUS.post((Event)event) ? null : event.getMessage();
    }

    @Nullable
    public static Component onClientSystemChat(Component message, boolean overlay) {
        ClientChatReceivedEvent.System event = new ClientChatReceivedEvent.System(SYSTEM_CHAT_TYPE_BOUND, message, overlay);
        return MinecraftForge.EVENT_BUS.post((Event)event) ? null : event.getMessage();
    }

    @NotNull
    public static String onClientSendMessage(String message) {
        ClientChatEvent event = new ClientChatEvent(message);
        return MinecraftForge.EVENT_BUS.post((Event)event) ? "" : event.getMessage();
    }

    @NotNull
    public static RenderType getEntityRenderType(RenderType chunkRenderType, boolean cull) {
        return RenderTypeHelper.getEntityRenderType(chunkRenderType, cull);
    }

    public static Font getTooltipFont(@NotNull ItemStack stack, Font fallbackFont) {
        Font stackFont = IClientItemExtensions.of(stack).getFont(stack, IClientItemExtensions.FontContext.TOOLTIP);
        return stackFont == null ? fallbackFont : stackFont;
    }

    public static RenderTooltipEvent.Pre onRenderTooltipPre(@NotNull ItemStack stack, GuiGraphics graphics, int x, int y, int screenWidth, int screenHeight, @NotNull List<ClientTooltipComponent> components, @NotNull Font fallbackFont, @NotNull ClientTooltipPositioner positioner) {
        RenderTooltipEvent.Pre preEvent = new RenderTooltipEvent.Pre(stack, graphics, x, y, screenWidth, screenHeight, ForgeHooksClient.getTooltipFont(stack, fallbackFont), components, positioner);
        MinecraftForge.EVENT_BUS.post((Event)preEvent);
        return preEvent;
    }

    public static RenderTooltipEvent.Color onRenderTooltipColor(@NotNull ItemStack stack, GuiGraphics graphics, int x, int y, @NotNull Font font, @NotNull List<ClientTooltipComponent> components) {
        RenderTooltipEvent.Color colorEvent = new RenderTooltipEvent.Color(stack, graphics, x, y, font, -267386864, 0x505000FF, 1344798847, components);
        MinecraftForge.EVENT_BUS.post((Event)colorEvent);
        return colorEvent;
    }

    public static List<ClientTooltipComponent> gatherTooltipComponents(ItemStack stack, List<? extends FormattedText> textElements, int mouseX, int screenWidth, int screenHeight, Font fallbackFont) {
        return ForgeHooksClient.gatherTooltipComponents(stack, textElements, Optional.empty(), mouseX, screenWidth, screenHeight, fallbackFont);
    }

    public static List<ClientTooltipComponent> gatherTooltipComponents(ItemStack stack, List<? extends FormattedText> textElements, Optional<TooltipComponent> itemComponent, int mouseX, int screenWidth, int screenHeight, Font fallbackFont) {
        List elements = textElements.stream().map(Either::left).collect(Collectors.toCollection(ArrayList::new));
        itemComponent.ifPresent(c -> elements.add(1, Either.right((Object)c)));
        return ForgeHooksClient.gatherTooltipComponentsFromElements(stack, elements, mouseX, screenWidth, screenHeight, fallbackFont);
    }

    public static List<ClientTooltipComponent> gatherTooltipComponentsFromElements(ItemStack stack, List<Either<FormattedText, TooltipComponent>> elements, int mouseX, int screenWidth, int screenHeight, Font fallbackFont) {
        Font font = ForgeHooksClient.getTooltipFont(stack, fallbackFont);
        RenderTooltipEvent.GatherComponents event = new RenderTooltipEvent.GatherComponents(stack, screenWidth, screenHeight, elements, -1);
        MinecraftForge.EVENT_BUS.post((Event)event);
        if (event.isCanceled()) {
            return List.of();
        }
        int tooltipTextWidth = event.getTooltipElements().stream().mapToInt(either -> (Integer)either.map(arg_0 -> ((Font)font).m_92852_(arg_0), component -> 0)).max().orElse(0);
        boolean needsWrap = false;
        int tooltipX = mouseX + 12;
        if (tooltipX + tooltipTextWidth + 4 > screenWidth && (tooltipX = mouseX - 16 - tooltipTextWidth) < 4) {
            tooltipTextWidth = mouseX > screenWidth / 2 ? mouseX - 12 - 8 : screenWidth - 16 - mouseX;
            needsWrap = true;
        }
        if (event.getMaxWidth() > 0 && tooltipTextWidth > event.getMaxWidth()) {
            tooltipTextWidth = event.getMaxWidth();
            needsWrap = true;
        }
        int tooltipTextWidthF = tooltipTextWidth;
        if (needsWrap) {
            return event.getTooltipElements().stream().flatMap(either -> (Stream)either.map(text -> ForgeHooksClient.splitLine(text, font, tooltipTextWidthF), component -> Stream.of(ClientTooltipComponent.m_169950_((TooltipComponent)component)))).toList();
        }
        return event.getTooltipElements().stream().map(either -> (ClientTooltipComponent)either.map(text -> ClientTooltipComponent.m_169948_((FormattedCharSequence)(text instanceof Component ? ((Component)text).m_7532_() : Language.m_128107_().m_5536_(text))), ClientTooltipComponent::m_169950_)).toList();
    }

    private static Stream<ClientTooltipComponent> splitLine(FormattedText text, Font font, int maxWidth) {
        Component component;
        if (text instanceof Component && (component = (Component)text).getString().isEmpty()) {
            return Stream.of(component.m_7532_()).map(ClientTooltipComponent::m_169948_);
        }
        return font.m_92923_(text, maxWidth).stream().map(ClientTooltipComponent::m_169948_);
    }

    public static Comparator<ParticleRenderType> makeParticleRenderTypeComparator(List<ParticleRenderType> renderOrder) {
        Comparator<ParticleRenderType> vanillaComparator = Comparator.comparingInt(renderOrder::indexOf);
        return (typeOne, typeTwo) -> {
            boolean vanillaOne = renderOrder.contains(typeOne);
            boolean vanillaTwo = renderOrder.contains(typeTwo);
            if (vanillaOne && vanillaTwo) {
                return vanillaComparator.compare((ParticleRenderType)typeOne, (ParticleRenderType)typeTwo);
            }
            if (!vanillaOne && !vanillaTwo) {
                return Integer.compare(System.identityHashCode(typeOne), System.identityHashCode(typeTwo));
            }
            return vanillaOne ? -1 : 1;
        };
    }

    public static ScreenEvent.RenderInventoryMobEffects onScreenPotionSize(Screen screen, int availableSpace, boolean compact, int horizontalOffset) {
        ScreenEvent.RenderInventoryMobEffects event = new ScreenEvent.RenderInventoryMobEffects(screen, availableSpace, compact, horizontalOffset);
        MinecraftForge.EVENT_BUS.post((Event)event);
        return event;
    }

    public static boolean onToastAdd(Toast toast) {
        return MinecraftForge.EVENT_BUS.post((Event)new ToastAddEvent(toast));
    }

    public static boolean isBlockInSolidLayer(BlockState state) {
        BakedModel model = Minecraft.m_91087_().m_91289_().m_110910_(state);
        return model.getRenderTypes(state, RandomSource.m_216327_(), ModelData.EMPTY).contains(RenderType.m_110451_());
    }

    public static void createWorldConfirmationScreen(Runnable doConfirmedWorldLoad) {
        MutableComponent title = Component.m_237115_((String)"selectWorld.backupQuestion.experimental");
        MutableComponent msg = Component.m_237115_((String)"selectWorld.backupWarning.experimental").m_130946_("\n\n").m_7220_((Component)Component.m_237115_((String)"forge.selectWorld.backupWarning.experimental.additional"));
        ConfirmScreen screen = new ConfirmScreen(confirmed -> {
            if (confirmed) {
                doConfirmedWorldLoad.run();
            } else {
                Minecraft.m_91087_().m_91152_(null);
            }
        }, (Component)title, (Component)msg, CommonComponents.f_130659_, CommonComponents.f_130656_);
        Minecraft.m_91087_().m_91152_((Screen)screen);
    }

    public static boolean renderFireOverlay(Player player, PoseStack mat) {
        return ForgeHooksClient.renderBlockOverlay(player, mat, RenderBlockScreenEffectEvent.OverlayType.FIRE, Blocks.f_50083_.m_49966_(), player.m_20183_());
    }

    public static boolean renderWaterOverlay(Player player, PoseStack mat) {
        return ForgeHooksClient.renderBlockOverlay(player, mat, RenderBlockScreenEffectEvent.OverlayType.WATER, Blocks.f_49990_.m_49966_(), player.m_20183_());
    }

    public static boolean renderBlockOverlay(Player player, PoseStack mat, RenderBlockScreenEffectEvent.OverlayType type, BlockState block, BlockPos pos) {
        return MinecraftForge.EVENT_BUS.post((Event)new RenderBlockScreenEffectEvent(player, mat, type, block, pos));
    }

    public static int getMaxMipmapLevel(int width, int height) {
        return Math.min(Mth.m_14173_((int)Math.max(1, width)), Mth.m_14173_((int)Math.max(1, height)));
    }

    public static ResourceLocation getShaderImportLocation(String basePath, boolean isRelative, String importPath) {
        ResourceLocation loc = new ResourceLocation(importPath);
        String normalised = FileUtil.m_179924_((String)((isRelative ? basePath : "shaders/include/") + loc.m_135815_()));
        return new ResourceLocation(loc.m_135827_(), normalised);
    }

    @Deprecated(forRemoval=true, since="1.20.1")
    public static void onCreativeModeTabBuildContents(CreativeModeTab tab, ResourceKey<CreativeModeTab> tabKey, CreativeModeTab.DisplayItemsGenerator originalGenerator, CreativeModeTab.ItemDisplayParameters params, CreativeModeTab.Output output) {
        ForgeHooks.onCreativeModeTabBuildContents(tab, tabKey, originalGenerator, params, output);
    }

    public static Direction getNearestStable(float nX, float nY, float nZ) {
        if (((Boolean)ForgeConfig.CLIENT.stabilizeDirectionGetNearest.get()).booleanValue()) {
            Direction ret = Direction.NORTH;
            float sum = Float.MIN_VALUE;
            for (Direction dir : Direction.values()) {
                float newSum = nX * (float)dir.m_122436_().m_123341_() + nY * (float)dir.m_122436_().m_123342_() + nZ * (float)dir.m_122436_().m_123343_();
                if (!(newSum > sum + 1.0E-6f)) continue;
                sum = newSum;
                ret = dir;
            }
            return ret;
        }
        return Direction.m_122372_((float)nX, (float)nY, (float)nZ);
    }

    @ApiStatus.Internal
    public static void initClientHooks(Minecraft mc, ReloadableResourceManager resourceManager) {
        if (initializedClientHooks) {
            throw new IllegalStateException("Client hooks initialized more than once");
        }
        initializedClientHooks = true;
        ForgeGameTestHooks.registerGametests();
        ModLoader.get().postEvent((Event)new RegisterClientReloadListenersEvent(resourceManager));
        ModLoader.get().postEvent((Event)new EntityRenderersEvent.RegisterLayerDefinitions());
        ModLoader.get().postEvent((Event)new EntityRenderersEvent.RegisterRenderers());
        TextureAtlasSpriteLoaderManager.init();
        ClientTooltipComponentManager.init();
        EntitySpectatorShaderManager.init();
        ForgeHooksClient.onRegisterKeyMappings(mc.f_91066_);
        RecipeBookManager.init();
        GuiOverlayManager.init();
        DimensionSpecialEffectsManager.init();
        NamedRenderTypeManager.init();
        ColorResolverManager.init();
        ItemDecoratorHandler.init();
        PresetEditorManager.init();
    }

    static {
        slotMainHand = 0;
        layerDefinitions = new HashMap<ModelLayerLocation, Supplier<LayerDefinition>>();
        ICON_SHEET = new ResourceLocation("forge", "textures/gui/icons.png");
        SYSTEM_CHAT_TYPE_DECORATION = new ChatTypeDecoration("forge.chatType.system", List.of(ChatTypeDecoration.Parameter.CONTENT), Style.f_131099_);
        SYSTEM_CHAT_TYPE = new ChatType(SYSTEM_CHAT_TYPE_DECORATION, SYSTEM_CHAT_TYPE_DECORATION);
        SYSTEM_CHAT_TYPE_BOUND = SYSTEM_CHAT_TYPE.m_240982_((Component)Component.m_237113_((String)"System"));
        initializedClientHooks = false;
    }

    @Mod.EventBusSubscriber(value={Dist.CLIENT}, modid="forge", bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientEvents {
        @Nullable
        private static ShaderInstance rendertypeEntityTranslucentUnlitShader;

        public static ShaderInstance getEntityTranslucentUnlitShader() {
            return Objects.requireNonNull(rendertypeEntityTranslucentUnlitShader, "Attempted to call getEntityTranslucentUnlitShader before shaders have finished loading.");
        }

        @SubscribeEvent
        public static void registerShaders(RegisterShadersEvent event) throws IOException {
            event.registerShader(new ShaderInstance(event.getResourceProvider(), new ResourceLocation("forge", "rendertype_entity_unlit_translucent"), DefaultVertexFormat.f_85812_), p_172645_ -> {
                rendertypeEntityTranslucentUnlitShader = p_172645_;
            });
        }
    }
}

