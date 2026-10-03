/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.client.model.entity.VehicleModel$TransformContext
 *  com.atsuishio.superbwarfare.event.ClientEventHandler
 *  net.minecraft.client.CameraType
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.player.LocalPlayer
 *  org.jetbrains.annotations.Nullable
 */
package tech.vvp.vvp.client.model;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import com.atsuishio.superbwarfare.event.ClientEventHandler;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.jetbrains.annotations.Nullable;
import tech.vvp.vvp.client.model.VvpVehicleModel;
import tech.vvp.vvp.entity.vehicle.KornetEntity;

public class KornetModel
extends VvpVehicleModel<KornetEntity> {
    @Nullable
    public VehicleModel.TransformContext<KornetEntity> collectTransform(String boneName) {
        return switch (boneName) {
            case "guanmiao" -> (bone, vehicle, state) -> {
                LocalPlayer player = Minecraft.m_91087_().f_91074_;
                bone.setHidden(vehicle.m_146895_() == player && (Minecraft.m_91087_().f_91066_.m_92176_() == CameraType.FIRST_PERSON || ClientEventHandler.zoomVehicle));
            };
            case "missile" -> (bone, vehicle, state) -> bone.setHidden((Boolean)vehicle.m_20088_().m_135370_(KornetEntity.LOADED) == false);
            default -> super.collectTransform(boneName);
        };
    }
}

