/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  net.minecraft.ChatFormatting
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.commands.Commands
 *  net.minecraft.commands.arguments.TeamArgument
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.entity.projectile.ProjectileUtil
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.EntityHitResult
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.scores.PlayerTeam
 *  net.minecraftforge.common.util.FakePlayer
 *  net.minecraftforge.event.RegisterCommandsEvent
 *  net.minecraftforge.event.entity.EntityMountEvent
 *  net.minecraftforge.event.entity.player.PlayerInteractEvent
 *  net.minecraftforge.event.entity.player.PlayerInteractEvent$EntityInteract
 *  net.minecraftforge.event.entity.player.PlayerInteractEvent$EntityInteractSpecific
 *  net.minecraftforge.event.server.ServerAboutToStartEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.registries.ForgeRegistries
 */
package frontline.combat.fcp.team;

import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import frontline.combat.fcp.team.TeamLockConfig;
import frontline.combat.fcp.team.VehicleTeamLock;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.TeamArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid="fcp")
public final class VehicleTeamEventHandler {
    private static final Map<UUID, Long> LAST_MESSAGE = new HashMap<UUID, Long>();
    private static final double TARGET_REACH = 12.0;

    private VehicleTeamEventHandler() {
    }

    @SubscribeEvent
    public static void onServerAboutToStart(ServerAboutToStartEvent event) {
        TeamLockConfig.load();
    }

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        VehicleTeamEventHandler.handleInteract((PlayerInteractEvent)event, event.getTarget(), event.getEntity(), event.getItemStack());
    }

    @SubscribeEvent
    public static void onEntityInteractSpecific(PlayerInteractEvent.EntityInteractSpecific event) {
        VehicleTeamEventHandler.handleInteract((PlayerInteractEvent)event, event.getTarget(), event.getEntity(), event.getItemStack());
    }

    private static void handleInteract(PlayerInteractEvent event, Entity target, Player player, ItemStack stack) {
        if (!(target instanceof VehicleEntity)) {
            return;
        }
        VehicleEntity vehicle = (VehicleEntity)target;
        if (player.m_9236_().m_5776_()) {
            return;
        }
        if (player instanceof FakePlayer) {
            return;
        }
        TeamLockConfig cfg = TeamLockConfig.get();
        if (!cfg.enforce) {
            return;
        }
        if (!cfg.applyToNonFcpVehicles && !VehicleTeamLock.isFcpVehicle(vehicle)) {
            return;
        }
        if (cfg.opBypass && (player.m_20310_(2) || player.m_7500_())) {
            return;
        }
        if (VehicleTeamEventHandler.isBypassItem(cfg, stack)) {
            return;
        }
        if (!VehicleTeamEventHandler.isTeamBlocked(cfg, vehicle, player)) {
            return;
        }
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.FAIL);
        VehicleTeamEventHandler.notify(player, VehicleTeamEventHandler.denyMessage(vehicle), false);
    }

    @SubscribeEvent
    public static void onMount(EntityMountEvent event) {
        if (!event.isMounting()) {
            return;
        }
        Entity entity = event.getEntityBeingMounted();
        if (!(entity instanceof VehicleEntity)) {
            return;
        }
        VehicleEntity vehicle = (VehicleEntity)entity;
        Entity entity2 = event.getEntityMounting();
        if (!(entity2 instanceof Player)) {
            return;
        }
        Player player = (Player)entity2;
        if (player.m_9236_().m_5776_()) {
            return;
        }
        if (player instanceof FakePlayer) {
            return;
        }
        TeamLockConfig cfg = TeamLockConfig.get();
        if (!cfg.enforce) {
            return;
        }
        if (!cfg.applyToNonFcpVehicles && !VehicleTeamLock.isFcpVehicle(vehicle)) {
            return;
        }
        if (cfg.opBypass && (player.m_20310_(2) || player.m_7500_())) {
            return;
        }
        String vehicleTeam = VehicleTeamLock.getTeam(vehicle);
        if (vehicleTeam == null) {
            if (cfg.autoClaimOnEnter && vehicle.m_20197_().isEmpty()) {
                String playerTeam = VehicleTeamLock.teamNameOf((Entity)player);
                if (playerTeam != null) {
                    VehicleTeamLock.setTeam(vehicle, playerTeam);
                    VehicleTeamEventHandler.notify(player, (Component)Component.m_237113_((String)"Vehicle claimed for team ").m_7220_((Component)Component.m_237113_((String)playerTeam).m_130940_(ChatFormatting.YELLOW)).m_130946_("."), true);
                }
                return;
            }
            if (cfg.blockUnclaimed) {
                event.setCanceled(true);
                VehicleTeamEventHandler.notify(player, (Component)Component.m_237113_((String)"This vehicle is locked. Ask an admin to assign it to your team.").m_130940_(ChatFormatting.RED), false);
            }
            return;
        }
        if (!vehicleTeam.equals(VehicleTeamLock.teamNameOf((Entity)player))) {
            event.setCanceled(true);
            VehicleTeamEventHandler.notify(player, VehicleTeamEventHandler.denyMessage(vehicle), false);
        }
    }

    private static boolean isTeamBlocked(TeamLockConfig cfg, VehicleEntity vehicle, Player player) {
        String vehicleTeam = VehicleTeamLock.getTeam(vehicle);
        if (vehicleTeam == null) {
            return cfg.blockUnclaimed;
        }
        return !vehicleTeam.equals(VehicleTeamLock.teamNameOf((Entity)player));
    }

    private static boolean isBypassItem(TeamLockConfig cfg, ItemStack stack) {
        if (cfg.interactionBypassItems.isEmpty() || stack.m_41619_()) {
            return false;
        }
        ResourceLocation id = ForgeRegistries.ITEMS.getKey((Object)stack.m_41720_());
        return id != null && cfg.interactionBypassItems.contains(id.toString());
    }

    private static Component denyMessage(VehicleEntity vehicle) {
        String team = VehicleTeamLock.getTeam(vehicle);
        if (team == null) {
            return Component.m_237113_((String)"This vehicle is locked.").m_130940_(ChatFormatting.RED);
        }
        return Component.m_237113_((String)"This vehicle belongs to team ").m_7220_((Component)Component.m_237113_((String)team).m_130940_(ChatFormatting.YELLOW)).m_7220_((Component)Component.m_237113_((String)" - you can't use it.").m_130940_(ChatFormatting.RED)).m_130940_(ChatFormatting.RED);
    }

    private static void notify(Player player, Component msg, boolean actionBar) {
        long now = player.m_9236_().m_46467_();
        Long last = LAST_MESSAGE.get(player.m_20148_());
        int cd = TeamLockConfig.get().messageCooldownTicks;
        if (last != null && now - last < (long)cd) {
            return;
        }
        LAST_MESSAGE.put(player.m_20148_(), now);
        if (player instanceof ServerPlayer) {
            ServerPlayer sp = (ServerPlayer)player;
            sp.m_5661_(msg, actionBar);
        }
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.m_82127_((String)"fcpvehicle").requires(src -> src.m_6761_(2))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.m_82127_((String)"team").then(Commands.m_82127_((String)"set").then(Commands.m_82129_((String)"team", (ArgumentType)TeamArgument.m_112088_()).executes(ctx -> {
            PlayerTeam team = TeamArgument.m_112091_((CommandContext)ctx, (String)"team");
            return VehicleTeamEventHandler.setTeam(((CommandSourceStack)ctx.getSource()).m_81375_(), team.m_5758_());
        })))).then(Commands.m_82127_((String)"clear").executes(ctx -> VehicleTeamEventHandler.clearTeam(((CommandSourceStack)ctx.getSource()).m_81375_())))).then(Commands.m_82127_((String)"query").executes(ctx -> VehicleTeamEventHandler.queryTeam(((CommandSourceStack)ctx.getSource()).m_81375_())))));
    }

    private static VehicleEntity targetVehicle(ServerPlayer player) {
        Entity entity;
        AABB searchBox;
        Vec3 look;
        Vec3 end;
        Entity entity2 = player.m_20202_();
        if (entity2 instanceof VehicleEntity) {
            VehicleEntity ridden = (VehicleEntity)entity2;
            return ridden;
        }
        Vec3 eye = player.m_20299_(1.0f);
        EntityHitResult hit = ProjectileUtil.m_37287_((Entity)player, (Vec3)eye, (Vec3)(end = eye.m_82549_((look = player.m_20252_(1.0f)).m_82490_(12.0))), (AABB)(searchBox = player.m_20191_().m_82369_(look.m_82490_(12.0)).m_82400_(1.0)), e -> e instanceof VehicleEntity, (double)144.0);
        if (hit != null && (entity = hit.m_82443_()) instanceof VehicleEntity) {
            VehicleEntity looked = (VehicleEntity)entity;
            return looked;
        }
        return null;
    }

    private static int setTeam(ServerPlayer player, String teamName) throws CommandSyntaxException {
        VehicleEntity v = VehicleTeamEventHandler.targetVehicle(player);
        if (v == null) {
            player.m_213846_(VehicleTeamEventHandler.noTarget());
            return 0;
        }
        VehicleTeamLock.setTeam(v, teamName);
        player.m_213846_((Component)Component.m_237113_((String)"[FCP] Vehicle assigned to team ").m_7220_((Component)Component.m_237113_((String)teamName).m_130940_(ChatFormatting.YELLOW)).m_130946_("."));
        return 1;
    }

    private static int clearTeam(ServerPlayer player) throws CommandSyntaxException {
        VehicleEntity v = VehicleTeamEventHandler.targetVehicle(player);
        if (v == null) {
            player.m_213846_(VehicleTeamEventHandler.noTarget());
            return 0;
        }
        VehicleTeamLock.clearTeam(v);
        player.m_213846_((Component)Component.m_237113_((String)"[FCP] Vehicle team cleared (now neutral)."));
        return 1;
    }

    private static int queryTeam(ServerPlayer player) throws CommandSyntaxException {
        VehicleEntity v = VehicleTeamEventHandler.targetVehicle(player);
        if (v == null) {
            player.m_213846_(VehicleTeamEventHandler.noTarget());
            return 0;
        }
        String t = VehicleTeamLock.getTeam(v);
        player.m_213846_((Component)(t == null ? Component.m_237113_((String)"[FCP] This vehicle is neutral (no team).") : Component.m_237113_((String)"[FCP] This vehicle belongs to team ").m_7220_((Component)Component.m_237113_((String)t).m_130940_(ChatFormatting.YELLOW)).m_130946_(".")));
        return 1;
    }

    private static Component noTarget() {
        return Component.m_237113_((String)"[FCP] No vehicle found - look at a vehicle (within 12 blocks) or sit in one, then run the command.").m_130940_(ChatFormatting.RED);
    }
}

