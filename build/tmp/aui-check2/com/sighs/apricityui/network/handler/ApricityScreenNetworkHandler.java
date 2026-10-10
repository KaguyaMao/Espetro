/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.MenuProvider
 *  net.minecraft.world.SimpleMenuProvider
 *  net.minecraftforge.network.NetworkHooks
 */
package com.sighs.apricityui.network.handler;

import com.sighs.apricityui.ApricityUI;
import com.sighs.apricityui.container.SlotLayout;
import com.sighs.apricityui.container.bind.ContainerBindType;
import com.sighs.apricityui.container.datasource.ContainerDataSource;
import com.sighs.apricityui.container.datasource.DataSourceFactory;
import com.sighs.apricityui.element.ContainerDeclaration;
import com.sighs.apricityui.network.api.INetworkContext;
import com.sighs.apricityui.network.packet.CloseContainerRequestPacket;
import com.sighs.apricityui.network.packet.OpenScreenRequestPacket;
import com.sighs.apricityui.screen.ApricityContainerMenu;
import com.sighs.apricityui.util.common.NormalizeUtil;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraftforge.network.NetworkHooks;

public final class ApricityScreenNetworkHandler {
    public static void openScreen(ServerPlayer player, String templatePath, List<ContainerDeclaration> declarations) {
        ApricityScreenNetworkHandler.openScreen(player, templatePath, declarations, Map.of());
    }

    public static void openScreen(ServerPlayer player, String templatePath, List<ContainerDeclaration> declarations, Map<String, Map<String, String>> argsById) {
        if (player == null) {
            return;
        }
        String normalizedPath = NormalizeUtil.normalizeTemplatePath(templatePath);
        if (normalizedPath == null) {
            ApricityUI.LOGGER.warn("Open screen ignored: invalid template path={}", (Object)templatePath);
            return;
        }
        if (declarations == null || declarations.isEmpty()) {
            SlotLayout layout = SlotLayout.createUiOnly(normalizedPath);
            ApricityScreenNetworkHandler.openScreenFromServer(player, layout, Map.of(), null);
            return;
        }
        Map<String, ContainerDataSource> sources = ApricityScreenNetworkHandler.resolveDataSources(player, declarations, argsById);
        if (sources == null) {
            return;
        }
        SlotLayout layout = ApricityScreenNetworkHandler.buildSlotLayout(normalizedPath, declarations, sources);
        if (layout == null) {
            return;
        }
        ApricityScreenNetworkHandler.openScreenFromServer(player, layout, sources, null);
    }

    public static void handleOpenScreenRequest(OpenScreenRequestPacket packet, INetworkContext context) {
        context.enqueueWork(() -> {
            ServerPlayer player = context.sender();
            if (player == null) {
                return;
            }
            String normalizedPath = NormalizeUtil.normalizeTemplatePath(packet.templatePath());
            if (normalizedPath == null) {
                ApricityUI.LOGGER.warn("Open screen request ignored: invalid path={}", (Object)packet.templatePath());
                return;
            }
            List<ContainerDeclaration> declarations = packet.containers();
            if (declarations == null || declarations.isEmpty()) {
                SlotLayout layout = SlotLayout.createUiOnly(normalizedPath);
                ApricityScreenNetworkHandler.openScreenFromServer(player, layout, Map.of(), null);
                return;
            }
            Map<String, ContainerDataSource> sources = ApricityScreenNetworkHandler.resolveDataSources(player, declarations, Map.of());
            if (sources == null) {
                return;
            }
            SlotLayout layout = ApricityScreenNetworkHandler.buildSlotLayout(normalizedPath, declarations, sources);
            if (layout == null) {
                return;
            }
            ApricityScreenNetworkHandler.openScreenFromServer(player, layout, sources, null);
        });
    }

    public static void handleCloseContainerRequest(CloseContainerRequestPacket packet, INetworkContext context) {
        context.enqueueWork(() -> {
            ServerPlayer player = context.sender();
            if (player == null) {
                return;
            }
            if (player.f_36096_ instanceof ApricityContainerMenu) {
                player.m_6915_();
            }
        });
    }

    private static Map<String, ContainerDataSource> resolveDataSources(ServerPlayer player, List<ContainerDeclaration> declarations, Map<String, Map<String, String>> argsById) {
        LinkedHashMap<String, ContainerDataSource> sources = new LinkedHashMap<String, ContainerDataSource>();
        Map<Object, Object> safeArgs = argsById == null ? Map.of() : argsById;
        for (ContainerDeclaration decl : declarations) {
            ContainerBindType bindType;
            if (decl == null || (bindType = decl.bindType()) == ContainerBindType.PLAYER) continue;
            Map<String, String> args = safeArgs.getOrDefault(decl.id(), Map.of());
            try {
                ContainerDataSource dataSource = DataSourceFactory.resolve(player, decl.id(), bindType, args, decl.capacity());
                if (dataSource == null) {
                    ApricityUI.LOGGER.warn("Open container failed: bindType={} / container={} / reason=UNRESOLVED_BINDING", (Object)bindType.id(), (Object)decl.id());
                    return null;
                }
                sources.put(decl.id(), dataSource);
            }
            catch (Exception exception) {
                ApricityUI.LOGGER.warn("Open container failed: bindType={} / container={} / reason={}", new Object[]{bindType.id(), decl.id(), exception.getMessage()});
                return null;
            }
        }
        return sources;
    }

    private static SlotLayout buildSlotLayout(String templatePath, List<ContainerDeclaration> declarations, Map<String, ContainerDataSource> sources) {
        if (declarations == null || declarations.isEmpty()) {
            return SlotLayout.createUiOnly(templatePath);
        }
        String primaryContainerId = "";
        for (ContainerDeclaration decl : declarations) {
            if (!decl.primary()) continue;
            primaryContainerId = decl.id();
            break;
        }
        if (primaryContainerId.isEmpty() && !declarations.isEmpty()) {
            primaryContainerId = declarations.get(0).id();
        }
        int customCursor = 0;
        int playerPoolCapacity = 0;
        LinkedHashMap<String, Integer> customBaseById = new LinkedHashMap<String, Integer>();
        LinkedHashMap<String, Integer> customCapacityById = new LinkedHashMap<String, Integer>();
        for (ContainerDeclaration decl : declarations) {
            ContainerBindType bindType = decl.bindType();
            int requiredCapacity = decl.capacity();
            if (bindType == ContainerBindType.PLAYER) {
                playerPoolCapacity = Math.max(playerPoolCapacity, Math.min(36, requiredCapacity));
                continue;
            }
            int resolvedCapacity = requiredCapacity;
            ContainerDataSource source = sources.get(decl.id());
            if (source != null) {
                resolvedCapacity = Math.max(resolvedCapacity, source.capacity());
            }
            customBaseById.put(decl.id(), customCursor);
            customCapacityById.put(decl.id(), Math.max(0, resolvedCapacity));
            customCursor += Math.max(0, resolvedCapacity);
        }
        int playerBaseIndex = customCursor;
        ArrayList<SlotLayout.ContainerEntry> entries = new ArrayList<SlotLayout.ContainerEntry>(declarations.size());
        for (ContainerDeclaration decl : declarations) {
            String containerId = decl.id();
            ContainerBindType bindType = decl.bindType();
            boolean primary = containerId.equals(primaryContainerId);
            if (bindType == ContainerBindType.PLAYER) {
                int capacity = Math.min(playerPoolCapacity, Math.max(0, decl.capacity()));
                entries.add(new SlotLayout.ContainerEntry(containerId, bindType, playerBaseIndex, capacity, primary));
                continue;
            }
            int baseIndex = customBaseById.getOrDefault(containerId, 0);
            int capacity = customCapacityById.getOrDefault(containerId, 0);
            entries.add(new SlotLayout.ContainerEntry(containerId, bindType, baseIndex, capacity, primary));
        }
        return new SlotLayout(templatePath, entries);
    }

    private static void openScreenFromServer(ServerPlayer player, SlotLayout layout, Map<String, ContainerDataSource> containerSources, String titleLiteral) {
        if (player == null || layout == null) {
            return;
        }
        MutableComponent titleComponent = titleLiteral == null || titleLiteral.isBlank() ? Component.m_237119_() : Component.m_237113_((String)titleLiteral);
        NetworkHooks.openScreen((ServerPlayer)player, (MenuProvider)new SimpleMenuProvider((menuContainerId, playerInventory, ignoredPlayer) -> new ApricityContainerMenu(menuContainerId, playerInventory, layout, containerSources, player), (Component)titleComponent), layout::write);
    }
}

