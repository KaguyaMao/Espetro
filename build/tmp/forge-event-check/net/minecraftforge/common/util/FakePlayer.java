/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  javax.annotation.ParametersAreNonnullByDefault
 *  net.minecraft.network.Connection
 *  net.minecraft.network.PacketSendListener
 *  net.minecraft.network.chat.ChatType$Bound
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.PlayerChatMessage
 *  net.minecraft.network.protocol.Packet
 *  net.minecraft.network.protocol.PacketFlow
 *  net.minecraft.network.protocol.game.ServerboundAcceptTeleportationPacket
 *  net.minecraft.network.protocol.game.ServerboundBlockEntityTagQuery
 *  net.minecraft.network.protocol.game.ServerboundChangeDifficultyPacket
 *  net.minecraft.network.protocol.game.ServerboundChatAckPacket
 *  net.minecraft.network.protocol.game.ServerboundChatCommandPacket
 *  net.minecraft.network.protocol.game.ServerboundChatPacket
 *  net.minecraft.network.protocol.game.ServerboundChatSessionUpdatePacket
 *  net.minecraft.network.protocol.game.ServerboundClientCommandPacket
 *  net.minecraft.network.protocol.game.ServerboundClientInformationPacket
 *  net.minecraft.network.protocol.game.ServerboundCommandSuggestionPacket
 *  net.minecraft.network.protocol.game.ServerboundContainerButtonClickPacket
 *  net.minecraft.network.protocol.game.ServerboundContainerClickPacket
 *  net.minecraft.network.protocol.game.ServerboundContainerClosePacket
 *  net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket
 *  net.minecraft.network.protocol.game.ServerboundEditBookPacket
 *  net.minecraft.network.protocol.game.ServerboundEntityTagQuery
 *  net.minecraft.network.protocol.game.ServerboundInteractPacket
 *  net.minecraft.network.protocol.game.ServerboundJigsawGeneratePacket
 *  net.minecraft.network.protocol.game.ServerboundKeepAlivePacket
 *  net.minecraft.network.protocol.game.ServerboundLockDifficultyPacket
 *  net.minecraft.network.protocol.game.ServerboundMovePlayerPacket
 *  net.minecraft.network.protocol.game.ServerboundMoveVehiclePacket
 *  net.minecraft.network.protocol.game.ServerboundPaddleBoatPacket
 *  net.minecraft.network.protocol.game.ServerboundPickItemPacket
 *  net.minecraft.network.protocol.game.ServerboundPlaceRecipePacket
 *  net.minecraft.network.protocol.game.ServerboundPlayerAbilitiesPacket
 *  net.minecraft.network.protocol.game.ServerboundPlayerActionPacket
 *  net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket
 *  net.minecraft.network.protocol.game.ServerboundPlayerInputPacket
 *  net.minecraft.network.protocol.game.ServerboundRecipeBookChangeSettingsPacket
 *  net.minecraft.network.protocol.game.ServerboundRecipeBookSeenRecipePacket
 *  net.minecraft.network.protocol.game.ServerboundRenameItemPacket
 *  net.minecraft.network.protocol.game.ServerboundResourcePackPacket
 *  net.minecraft.network.protocol.game.ServerboundSeenAdvancementsPacket
 *  net.minecraft.network.protocol.game.ServerboundSelectTradePacket
 *  net.minecraft.network.protocol.game.ServerboundSetBeaconPacket
 *  net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket
 *  net.minecraft.network.protocol.game.ServerboundSetCommandBlockPacket
 *  net.minecraft.network.protocol.game.ServerboundSetCommandMinecartPacket
 *  net.minecraft.network.protocol.game.ServerboundSetCreativeModeSlotPacket
 *  net.minecraft.network.protocol.game.ServerboundSetJigsawBlockPacket
 *  net.minecraft.network.protocol.game.ServerboundSetStructureBlockPacket
 *  net.minecraft.network.protocol.game.ServerboundSignUpdatePacket
 *  net.minecraft.network.protocol.game.ServerboundSwingPacket
 *  net.minecraft.network.protocol.game.ServerboundTeleportToEntityPacket
 *  net.minecraft.network.protocol.game.ServerboundUseItemOnPacket
 *  net.minecraft.network.protocol.game.ServerboundUseItemPacket
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.server.network.ServerGamePacketListenerImpl
 *  net.minecraft.stats.Stat
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.entity.RelativeMovement
 *  net.minecraft.world.entity.player.Player
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.common.util;

import com.mojang.authlib.GameProfile;
import java.util.Set;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.game.ServerboundAcceptTeleportationPacket;
import net.minecraft.network.protocol.game.ServerboundBlockEntityTagQuery;
import net.minecraft.network.protocol.game.ServerboundChangeDifficultyPacket;
import net.minecraft.network.protocol.game.ServerboundChatAckPacket;
import net.minecraft.network.protocol.game.ServerboundChatCommandPacket;
import net.minecraft.network.protocol.game.ServerboundChatPacket;
import net.minecraft.network.protocol.game.ServerboundChatSessionUpdatePacket;
import net.minecraft.network.protocol.game.ServerboundClientCommandPacket;
import net.minecraft.network.protocol.game.ServerboundClientInformationPacket;
import net.minecraft.network.protocol.game.ServerboundCommandSuggestionPacket;
import net.minecraft.network.protocol.game.ServerboundContainerButtonClickPacket;
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.network.protocol.game.ServerboundCustomPayloadPacket;
import net.minecraft.network.protocol.game.ServerboundEditBookPacket;
import net.minecraft.network.protocol.game.ServerboundEntityTagQuery;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.network.protocol.game.ServerboundJigsawGeneratePacket;
import net.minecraft.network.protocol.game.ServerboundKeepAlivePacket;
import net.minecraft.network.protocol.game.ServerboundLockDifficultyPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundMoveVehiclePacket;
import net.minecraft.network.protocol.game.ServerboundPaddleBoatPacket;
import net.minecraft.network.protocol.game.ServerboundPickItemPacket;
import net.minecraft.network.protocol.game.ServerboundPlaceRecipePacket;
import net.minecraft.network.protocol.game.ServerboundPlayerAbilitiesPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerInputPacket;
import net.minecraft.network.protocol.game.ServerboundRecipeBookChangeSettingsPacket;
import net.minecraft.network.protocol.game.ServerboundRecipeBookSeenRecipePacket;
import net.minecraft.network.protocol.game.ServerboundRenameItemPacket;
import net.minecraft.network.protocol.game.ServerboundResourcePackPacket;
import net.minecraft.network.protocol.game.ServerboundSeenAdvancementsPacket;
import net.minecraft.network.protocol.game.ServerboundSelectTradePacket;
import net.minecraft.network.protocol.game.ServerboundSetBeaconPacket;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.network.protocol.game.ServerboundSetCommandBlockPacket;
import net.minecraft.network.protocol.game.ServerboundSetCommandMinecartPacket;
import net.minecraft.network.protocol.game.ServerboundSetCreativeModeSlotPacket;
import net.minecraft.network.protocol.game.ServerboundSetJigsawBlockPacket;
import net.minecraft.network.protocol.game.ServerboundSetStructureBlockPacket;
import net.minecraft.network.protocol.game.ServerboundSignUpdatePacket;
import net.minecraft.network.protocol.game.ServerboundSwingPacket;
import net.minecraft.network.protocol.game.ServerboundTeleportToEntityPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.stats.Stat;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;

public class FakePlayer
extends ServerPlayer {
    public FakePlayer(ServerLevel level, GameProfile name) {
        super(level.m_7654_(), level, name);
        this.f_8906_ = new FakePlayerNetHandler(level.m_7654_(), this);
    }

    public void m_5661_(Component chatComponent, boolean actionBar) {
    }

    public void m_6278_(Stat stat, int amount) {
    }

    public boolean m_6673_(DamageSource source) {
        return true;
    }

    public boolean m_7099_(Player player) {
        return false;
    }

    public void m_6667_(DamageSource source) {
    }

    public void m_8119_() {
    }

    public void m_9156_(ServerboundClientInformationPacket packet) {
    }

    @Nullable
    public MinecraftServer m_20194_() {
        return ServerLifecycleHooks.getCurrentServer();
    }

    @ParametersAreNonnullByDefault
    private static class FakePlayerNetHandler
    extends ServerGamePacketListenerImpl {
        private static final Connection DUMMY_CONNECTION = new Connection(PacketFlow.CLIENTBOUND);

        public FakePlayerNetHandler(MinecraftServer server, ServerPlayer player) {
            super(server, DUMMY_CONNECTION, player);
        }

        public void m_9933_() {
        }

        public void m_9953_() {
        }

        public void m_9942_(Component message) {
        }

        public void m_5918_(ServerboundPlayerInputPacket packet) {
        }

        public void m_5659_(ServerboundMoveVehiclePacket packet) {
        }

        public void m_7376_(ServerboundAcceptTeleportationPacket packet) {
        }

        public void m_7411_(ServerboundRecipeBookSeenRecipePacket packet) {
        }

        public void m_7982_(ServerboundRecipeBookChangeSettingsPacket packet) {
        }

        public void m_6947_(ServerboundSeenAdvancementsPacket packet) {
        }

        public void m_7741_(ServerboundCommandSuggestionPacket packet) {
        }

        public void m_7192_(ServerboundSetCommandBlockPacket packet) {
        }

        public void m_6629_(ServerboundSetCommandMinecartPacket packet) {
        }

        public void m_7965_(ServerboundPickItemPacket packet) {
        }

        public void m_5591_(ServerboundRenameItemPacket packet) {
        }

        public void m_5712_(ServerboundSetBeaconPacket packet) {
        }

        public void m_7424_(ServerboundSetStructureBlockPacket packet) {
        }

        public void m_8019_(ServerboundSetJigsawBlockPacket packet) {
        }

        public void m_6449_(ServerboundJigsawGeneratePacket packet) {
        }

        public void m_6321_(ServerboundSelectTradePacket packet) {
        }

        public void m_6829_(ServerboundEditBookPacket packet) {
        }

        public void m_7548_(ServerboundEntityTagQuery packet) {
        }

        public void m_6780_(ServerboundBlockEntityTagQuery packet) {
        }

        public void m_7185_(ServerboundMovePlayerPacket packet) {
        }

        public void m_9774_(double x, double y, double z, float yaw, float pitch) {
        }

        public void m_7502_(ServerboundPlayerActionPacket packet) {
        }

        public void m_6371_(ServerboundUseItemOnPacket packet) {
        }

        public void m_5760_(ServerboundUseItemPacket packet) {
        }

        public void m_6936_(ServerboundTeleportToEntityPacket packet) {
        }

        public void m_7529_(ServerboundResourcePackPacket packet) {
        }

        public void m_5938_(ServerboundPaddleBoatPacket packet) {
        }

        public void m_7026_(Component message) {
        }

        public void m_9829_(Packet<?> packet) {
        }

        public void m_243119_(Packet<?> packet, @Nullable PacketSendListener sendListener) {
        }

        public void m_7798_(ServerboundSetCarriedItemPacket packet) {
        }

        public void m_7388_(ServerboundChatPacket packet) {
        }

        public void m_7953_(ServerboundSwingPacket packet) {
        }

        public void m_5681_(ServerboundPlayerCommandPacket packet) {
        }

        public void m_6946_(ServerboundInteractPacket packet) {
        }

        public void m_6272_(ServerboundClientCommandPacket packet) {
        }

        public void m_7951_(ServerboundContainerClosePacket packet) {
        }

        public void m_5914_(ServerboundContainerClickPacket packet) {
        }

        public void m_7191_(ServerboundPlaceRecipePacket packet) {
        }

        public void m_6557_(ServerboundContainerButtonClickPacket packet) {
        }

        public void m_5964_(ServerboundSetCreativeModeSlotPacket packet) {
        }

        public void m_5527_(ServerboundSignUpdatePacket packet) {
        }

        public void m_5683_(ServerboundKeepAlivePacket packet) {
        }

        public void m_6828_(ServerboundPlayerAbilitiesPacket packet) {
        }

        public void m_5617_(ServerboundClientInformationPacket packet) {
        }

        public void m_7423_(ServerboundCustomPayloadPacket packet) {
        }

        public void m_7477_(ServerboundChangeDifficultyPacket packet) {
        }

        public void m_7728_(ServerboundLockDifficultyPacket packet) {
        }

        public void m_9780_(double x, double y, double z, float yaw, float pitch, Set<RelativeMovement> relativeSet) {
        }

        public void m_215201_(int sequence) {
        }

        public void m_214047_(ServerboundChatCommandPacket packet) {
        }

        public void m_241885_(ServerboundChatAckPacket packet) {
        }

        public void m_241992_(PlayerChatMessage message) {
        }

        public void m_245431_(PlayerChatMessage message, ChatType.Bound boundChatType) {
        }

        public void m_245903_(Component content, ChatType.Bound boundChatType) {
        }

        public void m_252797_(ServerboundChatSessionUpdatePacket packet) {
        }
    }
}

