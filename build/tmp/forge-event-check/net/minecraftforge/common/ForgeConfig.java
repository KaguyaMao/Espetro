/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.Logging
 *  org.apache.logging.log4j.LogManager
 */
package net.minecraftforge.common;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.Logging;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import org.apache.logging.log4j.LogManager;

public class ForgeConfig {
    static final ForgeConfigSpec clientSpec;
    public static final Client CLIENT;
    static final ForgeConfigSpec commonSpec;
    public static final Common COMMON;
    static final ForgeConfigSpec serverSpec;
    public static final Server SERVER;

    @SubscribeEvent
    public static void onLoad(ModConfigEvent.Loading configEvent) {
        LogManager.getLogger().debug(Logging.FORGEMOD, "Loaded forge config file {}", (Object)configEvent.getConfig().getFileName());
    }

    @SubscribeEvent
    public static void onFileChange(ModConfigEvent.Reloading configEvent) {
        LogManager.getLogger().debug(Logging.FORGEMOD, "Forge config just got changed on the file system!");
    }

    static {
        Object specPair = new ForgeConfigSpec.Builder().configure(Client::new);
        clientSpec = (ForgeConfigSpec)((Object)specPair.getRight());
        CLIENT = (Client)specPair.getLeft();
        specPair = new ForgeConfigSpec.Builder().configure(Common::new);
        commonSpec = (ForgeConfigSpec)((Object)specPair.getRight());
        COMMON = (Common)specPair.getLeft();
        specPair = new ForgeConfigSpec.Builder().configure(Server::new);
        serverSpec = (ForgeConfigSpec)((Object)specPair.getRight());
        SERVER = (Server)specPair.getLeft();
    }

    public static class Client {
        public final ForgeConfigSpec.BooleanValue alwaysSetupTerrainOffThread;
        public final ForgeConfigSpec.BooleanValue experimentalForgeLightPipelineEnabled;
        public final ForgeConfigSpec.BooleanValue showLoadWarnings;
        public final ForgeConfigSpec.BooleanValue useCombinedDepthStencilAttachment;
        @Deprecated(since="1.20.1", forRemoval=true)
        public final ForgeConfigSpec.BooleanValue compressLanIPv6Addresses;
        public final ForgeConfigSpec.BooleanValue calculateAllNormals;
        public final ForgeConfigSpec.BooleanValue stabilizeDirectionGetNearest;
        public final ForgeConfigSpec.BooleanValue allowMipmapLowering;

        Client(ForgeConfigSpec.Builder builder) {
            builder.comment("Client only settings, mostly things related to rendering").push("client");
            this.alwaysSetupTerrainOffThread = builder.comment("Enable Forge to queue all chunk updates to the Chunk Update thread.", "May increase FPS significantly, but may also cause weird rendering lag.", "Not recommended for computers without a significant number of cores available.").translation("forge.configgui.alwaysSetupTerrainOffThread").define("alwaysSetupTerrainOffThread", false);
            this.experimentalForgeLightPipelineEnabled = builder.comment("EXPERIMENTAL: Enable the Forge block rendering pipeline - fixes the lighting of custom models.").translation("forge.configgui.forgeLightPipelineEnabled").define("experimentalForgeLightPipelineEnabled", false);
            this.showLoadWarnings = builder.comment("When enabled, Forge will show any warnings that occurred during loading.").translation("forge.configgui.showLoadWarnings").define("showLoadWarnings", true);
            this.useCombinedDepthStencilAttachment = builder.comment("Set to true to use a combined DEPTH_STENCIL attachment instead of two separate ones.").translation("forge.configgui.useCombinedDepthStencilAttachment").define("useCombinedDepthStencilAttachment", false);
            this.compressLanIPv6Addresses = builder.comment("[DEPRECATED] Does nothing anymore, IPv6 addresses will be compressed always").translation("forge.configgui.compressLanIPv6Addresses").define("compressLanIPv6Addresses", true);
            this.calculateAllNormals = builder.comment("During block model baking, manually calculates the normal for all faces.", "This was the default behavior of forge between versions 31.0 and 47.1.", "May result in differences between vanilla rendering and forge rendering.", "Will only produce differences for blocks that contain non-axis aligned faces.", "You will need to reload your resources to see results.").translation("forge.configgui.calculateAllNormals").define("calculateAllNormals", false);
            this.stabilizeDirectionGetNearest = builder.comment("When enabled, a slightly biased Direction#getNearest calculation will be used to prevent normal fighting on 45 degree angle faces.").translation("forge.configgui.stabilizeDirectionGetNearest").define("stabilizeDirectionGetNearest", true);
            this.allowMipmapLowering = builder.comment("When enabled, Forge will allow mipmaps to be lowered in real-time. This is the default behavior in vanilla. Use this if you experience issues with resource packs that use textures lower than 8x8.").translation("forge.configgui.allowMipmapLowering").define("allowMipmapLowering", false);
            builder.pop();
        }

        public final boolean calculateAllNormals() {
            return clientSpec.isLoaded() ? (Boolean)this.calculateAllNormals.get() : (Boolean)this.calculateAllNormals.getDefault();
        }

        public final boolean showLoadWarnings() {
            return clientSpec.isLoaded() ? (Boolean)this.showLoadWarnings.get() : (Boolean)this.showLoadWarnings.getDefault();
        }

        public final boolean allowMipmapLowering() {
            return clientSpec.isLoaded() ? (Boolean)this.allowMipmapLowering.get() : (Boolean)this.allowMipmapLowering.getDefault();
        }
    }

    public static class Common {
        Common(ForgeConfigSpec.Builder builder) {
            builder.comment("[DEPRECATED / NO EFFECT]: General configuration settings").push("general");
            builder.pop();
        }
    }

    public static class Server {
        public final ForgeConfigSpec.BooleanValue removeErroringBlockEntities;
        public final ForgeConfigSpec.BooleanValue removeErroringEntities;
        public final ForgeConfigSpec.BooleanValue fullBoundingBoxLadders;
        public final ForgeConfigSpec.DoubleValue zombieBaseSummonChance;
        public final ForgeConfigSpec.DoubleValue zombieBabyChance;
        public final ForgeConfigSpec.ConfigValue<String> permissionHandler;
        public final ForgeConfigSpec.BooleanValue advertiseDedicatedServerToLan;
        public final ForgeConfigSpec.BooleanValue useItemWithDurationZero;

        Server(ForgeConfigSpec.Builder builder) {
            builder.comment("Server configuration settings").push("server");
            this.removeErroringBlockEntities = builder.comment("Set this to true to remove any BlockEntity that throws an error in its update method instead of closing the server and reporting a crash log. BE WARNED THIS COULD SCREW UP EVERYTHING USE SPARINGLY WE ARE NOT RESPONSIBLE FOR DAMAGES.").translation("forge.configgui.removeErroringBlockEntities").worldRestart().define("removeErroringBlockEntities", false);
            this.removeErroringEntities = builder.comment("Set this to true to remove any Entity (Note: Does not include BlockEntities) that throws an error in its tick method instead of closing the server and reporting a crash log. BE WARNED THIS COULD SCREW UP EVERYTHING USE SPARINGLY WE ARE NOT RESPONSIBLE FOR DAMAGES.").translation("forge.configgui.removeErroringEntities").worldRestart().define("removeErroringEntities", false);
            this.fullBoundingBoxLadders = builder.comment("Set this to true to check the entire entity's collision bounding box for ladders instead of just the block they are in. Causes noticeable differences in mechanics so default is vanilla behavior. Default: false.").translation("forge.configgui.fullBoundingBoxLadders").worldRestart().define("fullBoundingBoxLadders", false);
            this.zombieBaseSummonChance = builder.comment("Base zombie summoning spawn chance. Allows changing the bonus zombie summoning mechanic.").translation("forge.configgui.zombieBaseSummonChance").worldRestart().defineInRange("zombieBaseSummonChance", 0.1, 0.0, 1.0);
            this.zombieBabyChance = builder.comment("Chance that a zombie (or subclass) is a baby. Allows changing the zombie spawning mechanic.").translation("forge.configgui.zombieBabyChance").worldRestart().defineInRange("zombieBabyChance", 0.05, 0.0, 1.0);
            this.permissionHandler = builder.comment("The permission handler used by the server. Defaults to forge:default_handler if no such handler with that name is registered.").translation("forge.configgui.permissionHandler").define("permissionHandler", "forge:default_handler");
            this.advertiseDedicatedServerToLan = builder.comment("Set this to true to enable advertising the dedicated server to local LAN clients so that it shows up in the Multiplayer screen automatically.").translation("forge.configgui.advertiseDedicatedServerToLan").define("advertiseDedicatedServerToLan", true);
            this.useItemWithDurationZero = builder.comment("Set this to true to enable living entities to use items with durations of 0. Fixes being able to use Eyes of Ender repeatedly by holding down the use button. Disabled by default as it could change interactions with items of existing mods.").translation("forge.configgui.useItemWithDurationZero").define("useItemWithDurationZero", false);
            builder.pop();
        }

        public final int getUseItemDuration() {
            return (Boolean)this.useItemWithDurationZero.get() != false ? 0 : 1;
        }
    }
}

