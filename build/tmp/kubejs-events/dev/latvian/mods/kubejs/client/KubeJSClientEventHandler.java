/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.architectury.event.EventResult
 *  dev.architectury.event.events.client.ClientGuiEvent
 *  dev.architectury.event.events.client.ClientPlayerEvent
 *  dev.architectury.event.events.client.ClientTooltipEvent
 *  dev.architectury.hooks.client.screen.ScreenAccess
 *  dev.architectury.hooks.fluid.FluidBucketHooks
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.components.AbstractWidget
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.gui.screens.TitleScreen
 *  net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.BucketItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.item.SpawnEggItem
 *  net.minecraft.world.item.TooltipFlag
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.material.Fluid
 *  net.minecraft.world.level.material.Fluids
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.client;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.client.ClientGuiEvent;
import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.event.events.client.ClientTooltipEvent;
import dev.architectury.hooks.client.screen.ScreenAccess;
import dev.architectury.hooks.fluid.FluidBucketHooks;
import dev.latvian.mods.kubejs.CommonProperties;
import dev.latvian.mods.kubejs.bindings.event.ClientEvents;
import dev.latvian.mods.kubejs.bindings.event.ItemEvents;
import dev.latvian.mods.kubejs.client.ClientEventJS;
import dev.latvian.mods.kubejs.client.ClientProperties;
import dev.latvian.mods.kubejs.client.DebugInfoEventJS;
import dev.latvian.mods.kubejs.client.KubeJSErrorScreen;
import dev.latvian.mods.kubejs.client.TagInstance;
import dev.latvian.mods.kubejs.client.painter.Painter;
import dev.latvian.mods.kubejs.core.ImageButtonKJS;
import dev.latvian.mods.kubejs.item.ItemTooltipEventJS;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.kubejs.util.Tags;
import java.lang.invoke.LambdaMetafactory;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

public class KubeJSClientEventHandler {
    private static final ResourceLocation RECIPE_BUTTON_TEXTURE = new ResourceLocation("textures/gui/recipe_button.png");
    public static Map<Item, List<ItemTooltipEventJS.StaticTooltipHandler>> staticItemTooltips = null;
    private final Map<ResourceLocation, TagInstance> tempTagNames = new LinkedHashMap<ResourceLocation, TagInstance>();

    public void init() {
        ClientGuiEvent.DEBUG_TEXT_LEFT.register(this::debugInfoLeft);
        ClientGuiEvent.DEBUG_TEXT_RIGHT.register(this::debugInfoRight);
        ClientTooltipEvent.ITEM.register(this::itemTooltip);
        ClientPlayerEvent.CLIENT_PLAYER_JOIN.register(this::loggedIn);
        ClientPlayerEvent.CLIENT_PLAYER_QUIT.register(this::loggedOut);
        ClientPlayerEvent.CLIENT_PLAYER_RESPAWN.register(this::respawn);
        ClientGuiEvent.RENDER_HUD.register(Painter.INSTANCE::inGameScreenDraw);
        ClientGuiEvent.RENDER_POST.register(Painter.INSTANCE::guiScreenDraw);
        ClientGuiEvent.INIT_PRE.register(this::guiPreInit);
        ClientGuiEvent.INIT_POST.register(this::guiPostInit);
    }

    private void debugInfoLeft(List<String> lines) {
        if (Minecraft.m_91087_().f_91074_ != null && ClientEvents.DEBUG_LEFT.hasListeners()) {
            ClientEvents.DEBUG_LEFT.post(ScriptType.CLIENT, new DebugInfoEventJS(lines));
        }
    }

    private void debugInfoRight(List<String> lines) {
        if (Minecraft.m_91087_().f_91074_ != null && ClientEvents.DEBUG_RIGHT.hasListeners()) {
            ClientEvents.DEBUG_RIGHT.post(ScriptType.CLIENT, new DebugInfoEventJS(lines));
        }
    }

    private void itemTooltip(ItemStack stack, List<Component> lines, TooltipFlag flag) {
        if (stack.m_41619_()) {
            return;
        }
        boolean advanced = flag.m_7050_();
        if (advanced && ClientProperties.get().getShowTagNames() && Screen.m_96638_()) {
            BucketItem bucket;
            Item fluid;
            Object item;
            Consumer<TagKey> addToTempTags = tag -> this.tempTagNames.computeIfAbsent((ResourceLocation)tag.f_203868_(), (Function<ResourceLocation, TagInstance>)LambdaMetafactory.metafactory(null, null, null, (Ljava/lang/Object;)Ljava/lang/Object;, <init>(net.minecraft.resources.ResourceLocation ), (Lnet/minecraft/resources/ResourceLocation;)Ldev/latvian/mods/kubejs/client/TagInstance;)()).registries.add(tag.f_203867_());
            Tags.byItemStack(stack).forEach((Consumer<TagKey<Item>>)addToTempTags);
            Item item2 = stack.m_41720_();
            if (item2 instanceof BlockItem) {
                item = (BlockItem)item2;
                Tags.byBlock(item.m_40614_()).forEach((Consumer<TagKey<Block>>)addToTempTags);
            }
            if ((item2 = stack.m_41720_()) instanceof BucketItem && (fluid = FluidBucketHooks.getFluid((BucketItem)(bucket = (BucketItem)item2))) != Fluids.f_76191_) {
                Tags.byFluid((Fluid)fluid).forEach((Consumer<TagKey<Fluid>>)addToTempTags);
            }
            if ((fluid = stack.m_41720_()) instanceof SpawnEggItem) {
                item = (SpawnEggItem)fluid;
                Tags.byEntityType(item.m_43228_(stack.m_41783_())).forEach(addToTempTags);
            }
            for (TagInstance instance : this.tempTagNames.values()) {
                lines.add(instance.toText());
            }
            this.tempTagNames.clear();
        }
        if (staticItemTooltips == null) {
            staticItemTooltips = new HashMap<Item, List<ItemTooltipEventJS.StaticTooltipHandler>>();
            ItemEvents.TOOLTIP.post(ScriptType.CLIENT, new ItemTooltipEventJS(staticItemTooltips));
        }
        try {
            for (ItemTooltipEventJS.StaticTooltipHandler handler : staticItemTooltips.getOrDefault(Items.f_41852_, List.of())) {
                handler.tooltip(stack, advanced, lines);
            }
        }
        catch (Exception ex) {
            ConsoleJS.CLIENT.error("Error while gathering tooltip for " + String.valueOf(stack), ex);
        }
        try {
            for (ItemTooltipEventJS.StaticTooltipHandler handler : staticItemTooltips.getOrDefault(stack.m_41720_(), List.of())) {
                handler.tooltip(stack, advanced, lines);
            }
        }
        catch (Exception ex) {
            ConsoleJS.CLIENT.error("Error while gathering tooltip for " + String.valueOf(stack), ex);
        }
    }

    private void loggedIn(LocalPlayer player) {
        ClientEvents.LOGGED_IN.post(ScriptType.CLIENT, new ClientEventJS());
    }

    private void loggedOut(LocalPlayer player) {
        ClientEvents.LOGGED_OUT.post(ScriptType.CLIENT, new ClientEventJS());
        Painter.INSTANCE.clear();
    }

    private void respawn(LocalPlayer oldPlayer, LocalPlayer newPlayer) {
    }

    @Nullable
    public static Screen setScreen(Screen screen) {
        if (screen instanceof TitleScreen && !ConsoleJS.STARTUP.errors.isEmpty() && CommonProperties.get().startupErrorGUI) {
            return new KubeJSErrorScreen(screen, ConsoleJS.STARTUP);
        }
        return screen;
    }

    private EventResult guiPreInit(Screen screen, ScreenAccess screenAccess) {
        return EventResult.pass();
    }

    private void guiPostInit(Screen screen, ScreenAccess access) {
        if (ClientProperties.get().getDisableRecipeBook() && screen instanceof RecipeUpdateListener) {
            Iterator iterator = screen.m_6702_().iterator();
            while (iterator.hasNext()) {
                ImageButtonKJS buttonKJS;
                GuiEventListener listener = (GuiEventListener)iterator.next();
                if (!(listener instanceof AbstractWidget) || !(listener instanceof ImageButtonKJS) || !RECIPE_BUTTON_TEXTURE.equals((Object)(buttonKJS = (ImageButtonKJS)listener).kjs$getButtonTexture())) continue;
                access.getRenderables().remove(listener);
                access.getNarratables().remove(listener);
                iterator.remove();
                return;
            }
        }
    }
}

