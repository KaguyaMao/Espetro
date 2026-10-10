/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.logging.LogUtils
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.commands.Commands
 *  net.minecraft.network.chat.Component
 *  net.minecraftforge.event.RegisterCommandsEvent
 *  net.minecraftforge.event.server.ServerAboutToStartEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.ModList
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 *  net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
 *  org.slf4j.Logger
 */
package frontline.combat.fcp.compat.vpb;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.logging.LogUtils;
import frontline.combat.fcp.compat.vpb.VpbIntegrationConfig;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;

public final class VpbIntegration {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final String VPB_MODID = "pointblank";
    private static boolean loaded;

    private VpbIntegration() {
    }

    public static boolean isVpbLoaded() {
        return loaded;
    }

    @Mod.EventBusSubscriber(modid="fcp")
    public static final class ForgeBus {
        private ForgeBus() {
        }

        @SubscribeEvent
        public static void onServerAboutToStart(ServerAboutToStartEvent event) {
            VpbIntegrationConfig.load();
        }

        @SubscribeEvent
        public static void onRegisterCommands(RegisterCommandsEvent event) {
            event.getDispatcher().register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.m_82127_((String)"fcpvpb").requires(src -> src.m_6761_(2))).then(Commands.m_82127_((String)"reload").executes(ctx -> {
                VpbIntegrationConfig.load();
                VpbIntegrationConfig cfg = VpbIntegrationConfig.get();
                ((CommandSourceStack)ctx.getSource()).m_288197_(() -> Component.m_237113_((String)("[FCP/VPB] Reloaded: " + cfg.projectileWarheads.size() + " warhead mapping(s), " + cfg.muzzleSmokeProjectiles.size() + " muzzle-smoke projectile(s).")), true);
                return 1;
            })));
        }
    }

    @Mod.EventBusSubscriber(modid="fcp", bus=Mod.EventBusSubscriber.Bus.MOD)
    public static final class ModBus {
        private ModBus() {
        }

        @SubscribeEvent
        public static void onCommonSetup(FMLCommonSetupEvent event) {
            loaded = ModList.get().isLoaded(VpbIntegration.VPB_MODID);
            VpbIntegrationConfig.load();
            if (loaded) {
                LOGGER.info("[FCP/VPB] Point Blank detected - explosion replacement and muzzle smoke active.");
            } else {
                LOGGER.info("[FCP/VPB] Point Blank not installed - integration idle (FCP runs normally).");
            }
        }
    }
}

