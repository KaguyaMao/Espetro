/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.JvmField
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 */
package com.atsuishio.superbwarfare.data;

import com.atsuishio.superbwarfare.data.DataLoader;
import com.atsuishio.superbwarfare.data.DataMap;
import com.atsuishio.superbwarfare.data.drone_attachment.DroneAttachmentData;
import com.atsuishio.superbwarfare.data.gun.DefaultGunData;
import com.atsuishio.superbwarfare.data.gun.GunData;
import com.atsuishio.superbwarfare.data.gun.ProjectileInfo;
import com.atsuishio.superbwarfare.data.mob_guns.DefaultMobGunData;
import com.atsuishio.superbwarfare.data.mob_guns.MobGunData;
import com.atsuishio.superbwarfare.data.vehicle.DefaultVehicleData;
import com.atsuishio.superbwarfare.data.vehicle.VehicleData;
import com.atsuishio.superbwarfare.data.vehicle_skin.VehicleSkin;
import com.atsuishio.superbwarfare.data.vehicle_skin.VehicleSkinData;
import com.atsuishio.superbwarfare.resource.gun.DefaultGunResource;
import com.atsuishio.superbwarfare.resource.gun.GunResource;
import com.atsuishio.superbwarfare.resource.vehicle.DefaultVehicleResource;
import com.atsuishio.superbwarfare.resource.vehicle.VehicleResource;
import java.util.Map;
import kotlin.Metadata;
import kotlin.jvm.JvmField;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={2, 0, 0}, k=1, xi=48, d1={"\u0000F\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0006\u0010\u0015\u001a\u00020\u0016R\u0016\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u00058\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\b0\u00058\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\t\u001a\b\u0012\u0004\u0012\u00020\n0\u00058\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\f0\u00058\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000e0\u00058\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00100\u00058\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00120\u00058\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00140\u00058\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0017"}, d2={"Lcom/atsuishio/superbwarfare/data/CustomData;", "", "<init>", "()V", "LAUNCHABLE_ENTITY", "Lcom/atsuishio/superbwarfare/data/DataMap;", "Lcom/atsuishio/superbwarfare/data/gun/ProjectileInfo;", "VEHICLE_DATA", "Lcom/atsuishio/superbwarfare/data/vehicle/DefaultVehicleData;", "GUN_DATA", "Lcom/atsuishio/superbwarfare/data/gun/DefaultGunData;", "DRONE_ATTACHMENT", "Lcom/atsuishio/superbwarfare/data/drone_attachment/DroneAttachmentData;", "MOB_GUNS", "Lcom/atsuishio/superbwarfare/data/mob_guns/DefaultMobGunData;", "VEHICLE_SKINS", "Lcom/atsuishio/superbwarfare/data/vehicle_skin/VehicleSkinData;", "GUN_RESOURCE", "Lcom/atsuishio/superbwarfare/resource/gun/DefaultGunResource;", "VEHICLE_RESOURCE", "Lcom/atsuishio/superbwarfare/resource/vehicle/DefaultVehicleResource;", "load", "", "superbwarfare"})
public final class CustomData {
    @NotNull
    public static final CustomData INSTANCE = new CustomData();
    @JvmField
    @NotNull
    public static final DataMap<ProjectileInfo> LAUNCHABLE_ENTITY = DataLoader.createData$default(DataLoader.INSTANCE, "sbw/launchable", ProjectileInfo.class, false, false, null, 28, null);
    @JvmField
    @NotNull
    public static final DataMap<DefaultVehicleData> VEHICLE_DATA = DataLoader.INSTANCE.createData("sbw/vehicles", DefaultVehicleData.class, true, true, CustomData::VEHICLE_DATA$lambda$0);
    @JvmField
    @NotNull
    public static final DataMap<DefaultGunData> GUN_DATA = DataLoader.INSTANCE.createData("sbw/guns", DefaultGunData.class, true, true, CustomData::GUN_DATA$lambda$1);
    @JvmField
    @NotNull
    public static final DataMap<DroneAttachmentData> DRONE_ATTACHMENT = DataLoader.createData$default(DataLoader.INSTANCE, "sbw/drone_attachments", DroneAttachmentData.class, false, false, null, 28, null);
    @JvmField
    @NotNull
    public static final DataMap<DefaultMobGunData> MOB_GUNS = DataLoader.createData$default(DataLoader.INSTANCE, "sbw/mob_guns", DefaultMobGunData.class, false, false, CustomData::MOB_GUNS$lambda$2, 12, null);
    @JvmField
    @NotNull
    public static final DataMap<VehicleSkinData> VEHICLE_SKINS = DataLoader.INSTANCE.createData("sbw/vehicle_skins", VehicleSkinData.class, true, true, CustomData::VEHICLE_SKINS$lambda$3);
    @JvmField
    @NotNull
    public static final DataMap<DefaultGunResource> GUN_RESOURCE = DataLoader.createResource$default(DataLoader.INSTANCE, "sbw/guns", DefaultGunResource.class, false, CustomData::GUN_RESOURCE$lambda$4, 4, null);
    @JvmField
    @NotNull
    public static final DataMap<DefaultVehicleResource> VEHICLE_RESOURCE = DataLoader.INSTANCE.createResource("sbw/vehicles", DefaultVehicleResource.class, true, CustomData::VEHICLE_RESOURCE$lambda$5);

    private CustomData() {
    }

    public final void load() {
    }

    private static final void VEHICLE_DATA$lambda$0(Map map) {
        Intrinsics.checkNotNullParameter((Object)map, (String)"<unused var>");
        VehicleData.dataCache.invalidateAll();
    }

    private static final void GUN_DATA$lambda$1(Map map) {
        Intrinsics.checkNotNullParameter((Object)map, (String)"<unused var>");
        GunData.DATA_CACHE.invalidateAll();
    }

    private static final void MOB_GUNS$lambda$2(Map map) {
        Intrinsics.checkNotNullParameter((Object)map, (String)"<unused var>");
        MobGunData.dataCache.invalidateAll();
    }

    private static final void VEHICLE_SKINS$lambda$3(Map map) {
        Intrinsics.checkNotNullParameter((Object)map, (String)"<unused var>");
        VehicleSkin.DATA_CACHE.invalidateAll();
    }

    private static final void GUN_RESOURCE$lambda$4(Map map) {
        Intrinsics.checkNotNullParameter((Object)map, (String)"<unused var>");
        GunResource.RESOURCE_CACHE.invalidateAll();
    }

    private static final void VEHICLE_RESOURCE$lambda$5(Map map) {
        Intrinsics.checkNotNullParameter((Object)map, (String)"<unused var>");
        VehicleResource.Companion.getRESOURCE_CACHE().invalidateAll();
    }
}
