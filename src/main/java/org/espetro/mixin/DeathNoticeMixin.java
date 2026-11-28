package org.espetro.mixin;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameRules;
import org.espetro.stats.RoundScoreConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/** Filter vanilla public notices while leaving the world's gamerule untouched. */
@Mixin(ServerPlayer.class)
abstract class DeathNoticeMixin {
    @Redirect(method = "die", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/world/level/GameRules;getBoolean(Lnet/minecraft/world/level/GameRules$Key;)Z"),
        require = 1)
    private boolean espetro$deathNotice(GameRules rules, GameRules.Key<GameRules.BooleanValue> key) {
        return (key != GameRules.RULE_SHOWDEATHMESSAGES || !RoundScoreConfig.hidden()) && rules.getBoolean(key);
    }
}
