/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.packs.resources.ResourceManager
 *  net.minecraft.world.level.storage.loot.LootDataId
 *  net.minecraft.world.level.storage.loot.LootDataManager
 *  net.minecraft.world.level.storage.loot.LootDataType
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.At$Shift
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.LocalCapture
 */
package dev.latvian.mods.kubejs.core.mixin.common;

import com.google.gson.JsonElement;
import dev.latvian.mods.kubejs.core.LootTablesKJS;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.storage.loot.LootDataId;
import net.minecraft.world.level.storage.loot.LootDataManager;
import net.minecraft.world.level.storage.loot.LootDataType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value={LootDataManager.class})
public abstract class LootTablesMixin
implements LootTablesKJS {
    @Shadow
    private Map<LootDataId<?>, ?> f_278415_;

    @Inject(method={"apply*"}, at={@At(value="RETURN")})
    private void kjs$apply(Map<LootDataType<?>, Map<ResourceLocation, ?>> parsedMap, CallbackInfo ci) {
        this.kjs$completeReload(parsedMap, this.f_278415_);
    }

    @Inject(method={"method_51189", "lambda$scheduleElementParse$5", "m_278660_"}, remap=false, at={@At(value="INVOKE", target="Lnet/minecraft/server/packs/resources/SimpleJsonResourceReloadListener;scanDirectory(Lnet/minecraft/server/packs/resources/ResourceManager;Ljava/lang/String;Lcom/google/gson/Gson;Ljava/util/Map;)V", shift=At.Shift.AFTER, remap=true)}, locals=LocalCapture.CAPTURE_FAILHARD)
    private static void kjs$readLootTableJsons(ResourceManager rm, LootDataType type, Map map0, CallbackInfo ci, Map<ResourceLocation, JsonElement> map) {
        if (type.equals(LootDataType.f_278413_)) {
            LootTablesKJS.kjs$postLootEvents(map);
        }
    }
}

