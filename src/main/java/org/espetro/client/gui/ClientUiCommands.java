package org.espetro.client.gui;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.loading.FMLPaths;
import org.espetro.Espetro;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Locale;

/** Client-only commands, registered programmatically by EspetroClient. */
public final class ClientUiCommands {
    private static boolean initialized;

    private ClientUiCommands() {
    }

    public static void initialize(IEventBus modEventBus) {
        if (initialized) {
            return;
        }
        initialized = true;
        modEventBus.addListener(ClientUiCommands::onClientSetup);
        MinecraftForge.EVENT_BUS.addListener(ClientUiCommands::registerCommands);
    }

    private static Path preferencesFile() {
        return FMLPaths.CONFIGDIR.get().resolve(ClientUiPreferences.FILE_NAME);
    }

    private static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ClientUiPreferences.LoadResult result = ClientUiPreferences.load(preferencesFile());
            UiTheme.setMode(result.mode());
            if (!result.warning().isEmpty()) {
                Espetro.LOGGER.warn("{}", result.warning());
            }
        });
    }

    private static void registerCommands(RegisterClientCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("esui")
            .executes(context -> showStatus(context.getSource()))
            .then(Commands.literal("theme")
                .executes(context -> showStatus(context.getSource()))
                .then(Commands.literal("light")
                    .executes(context -> changeTheme(context.getSource(), UiTheme.Mode.LIGHT)))
                .then(Commands.literal("dark")
                    .executes(context -> changeTheme(context.getSource(), UiTheme.Mode.DARK)))
                .then(Commands.literal("toggle")
                    .executes(context -> changeTheme(context.getSource(),
                        UiTheme.mode() == UiTheme.Mode.DARK ? UiTheme.Mode.LIGHT : UiTheme.Mode.DARK))))
            // Consume invalid esui arguments locally too; Forge otherwise forwards unknown arguments.
            .then(Commands.argument("usage", StringArgumentType.greedyString())
                .executes(context -> showStatus(context.getSource()))));
    }

    private static int showStatus(CommandSourceStack source) {
        source.sendSuccess(() -> Component.literal("[Espetro UI] 当前主题：" + modeLabel(UiTheme.mode())
            + "。用法：/esui theme light | dark | toggle（仅影响本客户端）"), false);
        return 1;
    }

    private static int changeTheme(CommandSourceStack source, UiTheme.Mode mode) {
        UiTheme.setMode(mode);
        try {
            ClientUiPreferences.save(preferencesFile(), mode);
            source.sendSuccess(() -> Component.literal("[Espetro UI] 已切换为" + modeLabel(mode)
                + "并保存，无需重启。"), false);
        } catch (IOException | SecurityException failure) {
            Espetro.LOGGER.warn("UI theme changed to {} but preferences could not be saved to {}",
                mode.name().toLowerCase(Locale.ROOT), preferencesFile(), failure);
            source.sendSuccess(() -> Component.literal("[Espetro UI] 已切换为" + modeLabel(mode)
                + "，但配置保存失败；本次会话仍然生效。请检查 config/"
                + ClientUiPreferences.FILE_NAME + " 的写入权限。"), false);
        }
        return 1;
    }

    private static String modeLabel(UiTheme.Mode mode) {
        return mode == UiTheme.Mode.LIGHT ? "亮色（light）" : "暗色（dark）";
    }
}
