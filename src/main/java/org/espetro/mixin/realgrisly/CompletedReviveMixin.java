package org.espetro.mixin.realgrisly;

import net.minecraft.server.level.ServerPlayer;
import org.espetro.stats.RoundScoring;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;
import java.util.UUID;

/** The optional downed-player mod clears its helper set during a completed revive. */
@Pseudo
@Mixin(targets = "com.lol141.realgrisly.server.DownedState", remap = false)
abstract class CompletedReviveMixin {
    @Unique private static final ThreadLocal<ServerPlayer> ESPETRO_HELPER = new ThreadLocal<>();

    @Inject(method = "revive", at = @At("HEAD"), require = 0, remap = false)
    private static void espetro$rememberHelper(ServerPlayer patient, @Coerce Object state, CallbackInfo callback) {
        ESPETRO_HELPER.remove();
        if (!patient.getPersistentData().getBoolean("realgrisly_downed")) return;
        try {
            if (state.getClass().getField("source").get(state) instanceof net.minecraft.world.damagesource.DamageSource source
                && RoundScoring.friendlyDowning(patient, source.getEntity())) return;
            var server = patient.serverLevel().getServer();
            if (state.getClass().getField("revivers").get(state) instanceof Set<?> helpers) {
                helpers.stream().filter(UUID.class::isInstance).map(UUID.class::cast).sorted()
                    .map(server.getPlayerList()::getPlayer).filter(p -> RoundScoring.canRescue(patient, p))
                    .findFirst().ifPresent(ESPETRO_HELPER::set);
            }
        } catch (ReflectiveOperationException error) {
            org.espetro.Espetro.LOGGER.debug("Unable to resolve RealGrisly revive helper", error);
        }
    }

    @Inject(method = "revive", at = @At("RETURN"), require = 0, remap = false)
    private static void espetro$scoreHelper(ServerPlayer patient, @Coerce Object state, CallbackInfo callback) {
        ServerPlayer helper = ESPETRO_HELPER.get();
        ESPETRO_HELPER.remove();
        if (helper != null && !patient.getPersistentData().getBoolean("realgrisly_downed")) {
            RoundScoring.rescue(patient, helper);
        }
    }
}
