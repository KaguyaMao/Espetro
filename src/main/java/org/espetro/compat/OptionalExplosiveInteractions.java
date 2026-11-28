package org.espetro.compat;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import org.espetro.Espetro;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/** Optional Superb Warfare behavior implemented through registries and vanilla hold-to-use. */
@Mod.EventBusSubscriber(modid=Espetro.MOD_ID)
public final class OptionalExplosiveInteractions {
    public static final int HOLD_TICKS=100;
    private static final Set<String> ITEMS=Set.of("tm_62","claymore_mine","blu_43_mine","c4_bomb");
    private static final Set<String> MINES=Set.of("tm_62","claymore","blu_43");
    private record Hold(ItemStack stack,InteractionHand hand,UUID target,net.minecraft.server.level.ServerLevel level,long started) {}
    private static final Map<UUID,Hold> ACTIVE=new HashMap<>();
    private OptionalExplosiveInteractions() {}

    public static boolean isDeployable(ItemStack stack){
        var id=ForgeRegistries.ITEMS.getKey(stack.getItem());
        return id!=null && "superbwarfare".equals(id.getNamespace()) && ITEMS.contains(id.getPath());
    }

    public static boolean usesHoldDuration(ItemStack stack){
        return isDeployable(stack) || (ConstructionTools.isTool(stack)
            && ModList.get().isLoaded("superbwarfare"));
    }

    private static String targetType(Entity entity){
        var id=ForgeRegistries.ENTITY_TYPES.getKey(entity.getType());
        return id!=null && "superbwarfare".equals(id.getNamespace()) ? id.getPath() : "";
    }

    private static Entity target(Player player){
        Vec3 eye=player.getEyePosition(), ray=player.getLookAngle().scale(4), end=eye.add(ray);
        var block=player.level().clip(new ClipContext(eye,end,ClipContext.Block.COLLIDER,ClipContext.Fluid.NONE,player));
        double distance=block.getLocation().distanceToSqr(eye);
        var hit=ProjectileUtil.getEntityHitResult(player,eye,end,
            player.getBoundingBox().expandTowards(ray).inflate(1),e-> {
                if(!e.isAlive() || e.isSpectator())return false;
                var type=targetType(e);
                return "c4".equals(type) || MINES.contains(type);
            },distance);
        return hit==null?null:hit.getEntity();
    }

    private static boolean start(PlayerInteractEvent event){
        Player player=event.getEntity();
        InteractionHand hand=event.getHand();
        ItemStack stack=player.getItemInHand(hand);
        if(player.isSpectator() || stack.isEmpty())return false;
        boolean placing=isDeployable(stack);
        Entity bomb=null;
        if(!placing) {
            if(!ConstructionTools.isTool(stack) || !ModList.get().isLoaded("superbwarfare"))return false;
            bomb=target(player);
            if(bomb==null)return false;
        }
        if(player.isUsingItem() || player.getCooldowns().isOnCooldown(stack.getItem()))return true;
        if(player instanceof ServerPlayer server) {
            ACTIVE.put(server.getUUID(),new Hold(stack,hand,bomb==null?null:bomb.getUUID(),server.serverLevel(),server.level().getGameTime()));
            server.displayClientMessage(Component.literal(placing?"按住右键部署 · 5.0 秒":"按住右键拆除 · 5.0 秒"),true);
        }
        player.startUsingItem(hand);
        if(!player.isUsingItem() && player instanceof ServerPlayer) ACTIVE.remove(player.getUUID());
        return true;
    }

    @SubscribeEvent(priority=EventPriority.HIGHEST) public static void item(PlayerInteractEvent.RightClickItem event){
        if(start(event)){event.setCancellationResult(InteractionResult.sidedSuccess(event.getLevel().isClientSide));event.setCanceled(true);}
    }
    @SubscribeEvent(priority=EventPriority.HIGHEST) public static void block(PlayerInteractEvent.RightClickBlock event){
        if(start(event)){event.setCancellationResult(InteractionResult.sidedSuccess(event.getLevel().isClientSide));event.setCanceled(true);}
    }
    @SubscribeEvent(priority=EventPriority.HIGHEST) public static void entity(PlayerInteractEvent.EntityInteract event){
        if(start(event)){event.setCancellationResult(InteractionResult.sidedSuccess(event.getLevel().isClientSide));event.setCanceled(true);}
    }
    @SubscribeEvent(priority=EventPriority.HIGHEST) public static void specific(PlayerInteractEvent.EntityInteractSpecific event){
        if(start(event)){event.setCancellationResult(InteractionResult.sidedSuccess(event.getLevel().isClientSide));event.setCanceled(true);}
    }

    @SubscribeEvent public static void tick(LivingEntityUseItemEvent.Tick event){
        if(!(event.getEntity() instanceof ServerPlayer player))return;
        var hold=ACTIVE.get(player.getUUID());
        if(hold==null)return;
        if(event.getItem()!=hold.stack()){
            ACTIVE.remove(player.getUUID());
            return;
        }
        if(hold.stack()!=player.getItemInHand(hold.hand()) || player.serverLevel()!=hold.level() || !player.isAlive()
            || hold.target()!=null && !matchesTarget(player,hold.target())){
            ACTIVE.remove(player.getUUID());
            event.setCanceled(true);
            return;
        }
        if(event.getDuration()>1 && event.getDuration()%10==0) {
            String seconds=String.format(java.util.Locale.ROOT,"%.1f",event.getDuration()/20.0);
            player.displayClientMessage(Component.literal((hold.target()==null?"部署中 · ":"拆除中 · ")+seconds+" 秒"),true);
        }
    }

    @SubscribeEvent public static void finish(LivingEntityUseItemEvent.Finish event){
        if(!(event.getEntity() instanceof ServerPlayer player))return;
        var hold=ACTIVE.remove(player.getUUID());
        if(hold==null || player.serverLevel()!=hold.level() || hold.stack()!=player.getItemInHand(hold.hand())
            || player.level().getGameTime()-hold.started()<HOLD_TICKS-1
            || !player.isAlive())return;
        if(hold.target()==null && isDeployable(hold.stack())){
            var result=hold.stack().getItem().use(player.level(),player,hold.hand());
            event.setResultStack(result.getObject());
        } else if (hold.target()!=null && ConstructionTools.isTool(hold.stack())) {
            var bomb=target(player);
            if(bomb==null || !hold.target().equals(bomb.getUUID()))return;
            boolean removed;
            if("c4".equals(targetType(bomb))){
                try {
                    bomb.getClass().getMethod("defuse").invoke(bomb);
                    removed=bomb.isRemoved();
                } catch(ReflectiveOperationException ignored){ removed=false; }
            } else {
                bomb.discard();
                removed=true;
            }
            if(removed){
                hold.stack().hurtAndBreak(1,player,p->p.broadcastBreakEvent(hold.hand()));
                event.setResultStack(hold.stack());
                player.displayClientMessage(Component.literal("已拆除"),true);
            }
        }
    }

    private static boolean matchesTarget(Player player,UUID id){
        Entity seen=target(player);
        return seen!=null && id.equals(seen.getUUID());
    }
    @SubscribeEvent public static void stop(LivingEntityUseItemEvent.Stop event){
        if(event.getEntity() instanceof ServerPlayer player)ACTIVE.remove(player.getUUID());
    }
    @SubscribeEvent public static void logout(PlayerEvent.PlayerLoggedOutEvent event){
        ACTIVE.remove(event.getEntity().getUUID());
    }
}
