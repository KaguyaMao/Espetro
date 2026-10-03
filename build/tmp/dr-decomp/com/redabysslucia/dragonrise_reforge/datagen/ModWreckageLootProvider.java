/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.data.loot.WreckageLootData$Builder
 *  com.atsuishio.superbwarfare.data.loot.WreckageLootData$Entry
 *  com.atsuishio.superbwarfare.data.loot.WreckageLootData$Pool$Builder
 *  com.atsuishio.superbwarfare.data.loot.WreckageLootData$Pool$Type
 *  com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity
 *  com.atsuishio.superbwarfare.init.ModDamageTypes
 *  com.atsuishio.superbwarfare.init.ModItems
 *  net.minecraft.data.PackOutput
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Items
 *  net.minecraftforge.common.data.ExistingFileHelper
 */
package com.redabysslucia.dragonrise_reforge.datagen;

import com.atsuishio.superbwarfare.data.loot.WreckageLootData;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.atsuishio.superbwarfare.init.ModDamageTypes;
import com.atsuishio.superbwarfare.init.ModItems;
import com.redabysslucia.dragonrise_reforge.datagen.base.SbwWreckageLootProvider;
import com.redabysslucia.dragonrise_reforge.init.ModEntities;
import net.minecraft.data.PackOutput;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModWreckageLootProvider
extends SbwWreckageLootProvider {
    public ModWreckageLootProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, existingFileHelper);
    }

    @Override
    public void generate() {
        this.add((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.ZTZ99A.get()), this.createTankLoot(6, 1));
        this.add((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.ZTZ99BH.get()), this.createTankLoot(8, 1));
        this.add((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.M1A2SEPV2.get()), this.createTankLoot(6, 1));
        this.add((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.T80.get()), this.createTankLoot(5, 1));
        this.add((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.T80B.get()), this.createTankLoot(5, 1));
        this.add((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.AMX56.get()), this.createTankLoot(6, 1));
        this.add((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.T90MH.get()), this.createTankLoot(6, 1));
        this.add((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.L1A2.get()), this.createTankLoot(6, 1));
        this.add((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.VT4A1.get()), this.createTankLoot(5, 1));
        this.add((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.VT4B.get()), this.createTankLoot(5, 1));
        this.add((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.ZTZ59A.get()), this.createTankLoot(4, 1));
        this.add((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.T3476.get()), this.createTankLoot(3, 1));
        this.add((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.IS2.get()), this.createTankLoot(4, 1));
        this.add((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.TIGER.get()), this.createTankLoot(4, 1));
        this.add((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.PANZER4.get()), this.createTankLoot(3, 1));
        this.add((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.M4A2.get()), this.createTankLoot(3, 1));
        this.add((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.M4A2105.get()), this.createTankLoot(3, 1));
        this.add((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.ZTQ15.get()), this.createLightTankLoot(4));
        this.add((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.M10BOOKER.get()), this.createLightTankLoot(4));
        this.add((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.R2S25M.get()), this.createLightTankLoot(3));
        this.add((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.BMP3.get()), this.createLightTankLoot(3));
        this.add((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.BMPT72.get()), this.createLightTankLoot(4));
        this.add((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.ZBD04A.get()), this.createLightTankLoot(3));
        this.add((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.ZBL08.get()), this.createLightTankLoot(3));
        this.add((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.CM34.get()), this.createLightTankLoot(3));
        this.add((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.CV90.get()), this.createLightTankLoot(3));
        this.add((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.M3A3.get()), this.createLightTankLoot(3));
        this.add((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.M3Stuart.get()), this.createLightTankLoot(3));
        this.add((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.CSK181.get()), this.createLightVehicleLoot(2));
        this.add((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.BMD4M.get()), this.createLightVehicleLoot(2));
        this.add((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.LVT.get()), this.createLightVehicleLoot(2));
        this.add((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.ZLT_11.get()), this.createLightVehicleLoot(3));
        this.add((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.SD905.get()), this.createLightVehicleLoot(3));
        this.add((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.S2S38.get()), this.createArtilleryLoot(5));
        this.add((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.ZSU234.get()), this.createArtilleryLoot(4));
        this.add((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.AA625E.get()), this.createArtilleryLoot(4));
        this.add((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.PROJECT640.get()), this.createArtilleryLoot(4));
        this.add((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.WLHGZU23.get()), this.createArtilleryLoot(2));
        this.add((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.WLSC.get()), this.createArtilleryLoot(3));
        this.add((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.SX1.get()), this.createLightVehicleLoot(2));
        this.add((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.FAVA.get()), this.createLightVehicleLoot(2));
        this.add((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.PZBJY.get()), this.createLightVehicleLoot(2));
        this.add((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.STRV103.get()), this.createLightVehicleLoot(3));
        this.add((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.type3.get()), this.createLightVehicleLoot(2));
        this.add((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.type97.get()), this.createLightVehicleLoot(2));
        this.add((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.type97Q.get()), this.createLightVehicleLoot(2));
        this.add((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.TOYOTASEIKI.get()), this.createLightVehicleLoot(1));
        this.add((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.motuo.get()), this.createLightVehicleLoot(1));
        this.add((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.SHIELD.get()), this.createLightVehicleLoot(1));
        this.add((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.MK19.get()), this.createLightVehicleLoot(1));
        this.add((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.ZU23.get()), this.createLightVehicleLoot(1));
        this.add((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.DSHK.get()), this.createLightVehicleLoot(1));
        this.add((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.M2.get()), this.createLightVehicleLoot(1));
        this.add((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.qjz89.get()), this.createLightVehicleLoot(1));
        this.add((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.AKM.get()), this.createLightVehicleLoot(1));
        this.add((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.AH64.get()), this.createHelicopterLoot(4));
        this.add((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.Z10A.get()), this.createHelicopterLoot(4));
        this.add((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.Z10ME.get()), this.createHelicopterLoot(5));
        this.add((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.EC665.get()), this.createHelicopterLoot(4));
        this.add((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.KA50.get()), this.createHelicopterLoot(4));
        this.add((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.Z9.get()), this.createHelicopterLoot(3));
        this.add((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.NH90.get()), this.createHelicopterLoot(3));
        this.add((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.Z20.get()), this.createHelicopterLoot(3));
        this.add((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.UH60.get()), this.createHelicopterLoot(3));
        this.add((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.J10.get()), this.createAircraftLoot(5));
        this.add((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.J10C.get()), this.createAircraftLoot(6));
        this.add((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.J11.get()), this.createAircraftLoot(5));
        this.add((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.J15T.get()), this.createAircraftLoot(5));
        this.add((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.J16.get()), this.createAircraftLoot(6));
        this.add((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.J20.get()), this.createAircraftLoot(8));
        this.add((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.J20VTOL.get()), this.createAircraftLoot(8));
        this.add((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.J35.get()), this.createAircraftLoot(5));
        this.add((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.J8.get()), this.createAircraftLoot(4));
        this.add((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.JF17.get()), this.createAircraftLoot(4));
        this.add((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.JAS39E.get()), this.createAircraftLoot(5));
        this.add((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.Q5.get()), this.createAircraftLoot(4));
        this.add((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.F14.get()), this.createAircraftLoot(6));
        this.add((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.F16C.get()), this.createAircraftLoot(5));
        this.add((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.F4U.get()), this.createAircraftLoot(3));
        this.add((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.REFALE.get()), this.createAircraftLoot(5));
        this.add((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.REFALEAA.get()), this.createAircraftLoot(6));
        this.add((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.AV8B.get()), this.createAircraftLoot(4));
        this.add((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.syy651.get()), this.createAircraftLoot(4));
        this.add((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.HYR0.get()), this.createAircraftLoot(6));
        this.add((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.CYBORG_TANK.get()), this.createSpecialLoot(8));
        this.add((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.TJGC.get()), this.createSpecialLoot(6));
        this.add((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.TEST.get()), this.createDefaultLoot());
        this.add((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.SPACEBAG.get()), this.createDefaultLoot());
        this.add((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.CAMEL.get()), this.createDefaultLoot());
        this.add((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.NPDS114.get()), this.createDefaultLoot());
        this.add((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.NPDS514.get()), this.createDefaultLoot());
        this.add((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.NPDS810.get()), this.createDefaultLoot());
        this.add((EntityType<? extends VehicleEntity>)((EntityType)ModEntities.TESTSHIP.get()), this.createDefaultLoot());
    }

    private WreckageLootData.Builder createTankLoot(int steelAmount, int moduleLevel) {
        WreckageLootData.Builder builder = new WreckageLootData.Builder();
        WreckageLootData.Pool.Builder pool1 = new WreckageLootData.Pool.Builder(1, "@Default", WreckageLootData.Pool.Type.COMPLETE);
        pool1.source(ModDamageTypes.REPAIR_TOOL);
        pool1.addEntry(new WreckageLootData.Entry((Item)ModItems.STEEL_BLOCK.get(), steelAmount, 1.0));
        if (moduleLevel >= 1) {
            pool1.addEntry(new WreckageLootData.Entry((Item)ModItems.MEDIUM_ARMAMENT_MODULE.get(), 1, 0.5));
        }
        if (moduleLevel >= 2) {
            pool1.addEntry(new WreckageLootData.Entry((Item)ModItems.HEAVY_ARMAMENT_MODULE.get(), 1, 0.3));
        }
        pool1.addEntry(new WreckageLootData.Entry((Item)ModItems.TRACK.get(), 1, 0.5));
        pool1.addEntry(new WreckageLootData.Entry((Item)ModItems.LARGE_MOTOR.get(), 1, 0.5));
        pool1.addEntry(new WreckageLootData.Entry((Item)ModItems.MEDIUM_BATTERY_PACK.get(), 1, 0.2));
        WreckageLootData.Pool.Builder pool2 = new WreckageLootData.Pool.Builder(1, "@Default", WreckageLootData.Pool.Type.COMPLETE);
        pool2.addEntry(new WreckageLootData.Entry((Item)ModItems.STEEL_BLOCK.get(), steelAmount, 0.5));
        builder.addPool(pool1.build());
        builder.addPool(pool2.build());
        WreckageLootData.Pool.Builder turretPool = new WreckageLootData.Pool.Builder(1, "@Default", WreckageLootData.Pool.Type.TURRET_ONLY);
        turretPool.source(ModDamageTypes.REPAIR_TOOL);
        turretPool.addEntry(new WreckageLootData.Entry((Item)ModItems.STEEL_BLOCK.get(), Math.max(1, steelAmount / 2), 1.0));
        if (moduleLevel >= 1) {
            turretPool.addEntry(new WreckageLootData.Entry((Item)ModItems.MEDIUM_ARMAMENT_MODULE.get(), 1, 0.5));
        }
        WreckageLootData.Pool.Builder turretPool2 = new WreckageLootData.Pool.Builder(1, "@Default", WreckageLootData.Pool.Type.TURRET_ONLY);
        turretPool2.addEntry(new WreckageLootData.Entry((Item)ModItems.STEEL_BLOCK.get(), Math.max(1, steelAmount / 2), 0.5));
        WreckageLootData.Pool.Builder vehiclePool = new WreckageLootData.Pool.Builder(1, "@Default", WreckageLootData.Pool.Type.VEHICLE_ONLY);
        vehiclePool.source(ModDamageTypes.REPAIR_TOOL);
        vehiclePool.addEntry(new WreckageLootData.Entry((Item)ModItems.STEEL_BLOCK.get(), Math.max(1, steelAmount / 2), 1.0));
        vehiclePool.addEntry(new WreckageLootData.Entry((Item)ModItems.TRACK.get(), 1, 0.5));
        vehiclePool.addEntry(new WreckageLootData.Entry((Item)ModItems.LARGE_MOTOR.get(), 1, 0.5));
        vehiclePool.addEntry(new WreckageLootData.Entry((Item)ModItems.MEDIUM_BATTERY_PACK.get(), 1, 0.2));
        WreckageLootData.Pool.Builder vehiclePool2 = new WreckageLootData.Pool.Builder(1, "@Default", WreckageLootData.Pool.Type.VEHICLE_ONLY);
        vehiclePool2.addEntry(new WreckageLootData.Entry((Item)ModItems.STEEL_BLOCK.get(), Math.max(1, steelAmount / 2), 0.5));
        builder.addPool(turretPool.build());
        builder.addPool(turretPool2.build());
        builder.addPool(vehiclePool.build());
        builder.addPool(vehiclePool2.build());
        return builder;
    }

    private WreckageLootData.Builder createLightTankLoot(int steelAmount) {
        WreckageLootData.Builder builder = new WreckageLootData.Builder();
        WreckageLootData.Pool.Builder pool1 = new WreckageLootData.Pool.Builder(1, "@Default", WreckageLootData.Pool.Type.COMPLETE);
        pool1.source(ModDamageTypes.REPAIR_TOOL);
        pool1.addEntry(new WreckageLootData.Entry((Item)ModItems.STEEL_BLOCK.get(), steelAmount, 1.0));
        pool1.addEntry(new WreckageLootData.Entry((Item)ModItems.LIGHT_ARMAMENT_MODULE.get(), 1, 0.5));
        pool1.addEntry(new WreckageLootData.Entry((Item)ModItems.WHEEL.get(), 2, 0.5));
        pool1.addEntry(new WreckageLootData.Entry((Item)ModItems.LARGE_MOTOR.get(), 1, 0.5));
        pool1.addEntry(new WreckageLootData.Entry((Item)ModItems.MEDIUM_BATTERY_PACK.get(), 1, 0.2));
        WreckageLootData.Pool.Builder pool2 = new WreckageLootData.Pool.Builder(1, "@Default", WreckageLootData.Pool.Type.COMPLETE);
        pool2.addEntry(new WreckageLootData.Entry((Item)ModItems.STEEL_BLOCK.get(), steelAmount, 0.5));
        builder.addPool(pool1.build());
        builder.addPool(pool2.build());
        return builder;
    }

    private WreckageLootData.Builder createArtilleryLoot(int steelAmount) {
        WreckageLootData.Builder builder = new WreckageLootData.Builder();
        WreckageLootData.Pool.Builder pool1 = new WreckageLootData.Pool.Builder(1, "@Default", WreckageLootData.Pool.Type.COMPLETE);
        pool1.source(ModDamageTypes.REPAIR_TOOL);
        pool1.addEntry(new WreckageLootData.Entry((Item)ModItems.STEEL_BLOCK.get(), steelAmount, 1.0));
        pool1.addEntry(new WreckageLootData.Entry((Item)ModItems.CANNON_CORE.get(), 2, 0.5));
        pool1.addEntry(new WreckageLootData.Entry((Item)ModItems.TRACK.get(), 1, 0.5));
        pool1.addEntry(new WreckageLootData.Entry((Item)ModItems.LARGE_MOTOR.get(), 1, 0.5));
        WreckageLootData.Pool.Builder pool2 = new WreckageLootData.Pool.Builder(1, "@Default", WreckageLootData.Pool.Type.COMPLETE);
        pool2.addEntry(new WreckageLootData.Entry((Item)ModItems.STEEL_BLOCK.get(), steelAmount, 0.5));
        builder.addPool(pool1.build());
        builder.addPool(pool2.build());
        return builder;
    }

    private WreckageLootData.Builder createHelicopterLoot(int steelAmount) {
        WreckageLootData.Builder builder = new WreckageLootData.Builder();
        WreckageLootData.Pool.Builder pool1 = new WreckageLootData.Pool.Builder(1, "@Default", WreckageLootData.Pool.Type.COMPLETE);
        pool1.source(ModDamageTypes.REPAIR_TOOL);
        pool1.addEntry(new WreckageLootData.Entry((Item)ModItems.STEEL_BLOCK.get(), steelAmount, 1.0));
        pool1.addEntry(new WreckageLootData.Entry((Item)ModItems.MEDIUM_ARMAMENT_MODULE.get(), 1, 0.5));
        pool1.addEntry(new WreckageLootData.Entry((Item)ModItems.LARGE_PROPELLER.get(), 1, 0.5));
        pool1.addEntry(new WreckageLootData.Entry((Item)ModItems.LARGE_MOTOR.get(), 1, 0.5));
        pool1.addEntry(new WreckageLootData.Entry((Item)ModItems.LARGE_BATTERY_PACK.get(), 1, 0.5));
        WreckageLootData.Pool.Builder pool2 = new WreckageLootData.Pool.Builder(1, "@Default", WreckageLootData.Pool.Type.COMPLETE);
        pool2.addEntry(new WreckageLootData.Entry((Item)ModItems.STEEL_BLOCK.get(), steelAmount, 0.5));
        builder.addPool(pool1.build());
        builder.addPool(pool2.build());
        return builder;
    }

    private WreckageLootData.Builder createAircraftLoot(int steelAmount) {
        WreckageLootData.Builder builder = new WreckageLootData.Builder();
        WreckageLootData.Pool.Builder pool1 = new WreckageLootData.Pool.Builder(1, "@Default", WreckageLootData.Pool.Type.COMPLETE);
        pool1.source(ModDamageTypes.REPAIR_TOOL);
        pool1.addEntry(new WreckageLootData.Entry((Item)ModItems.STEEL_BLOCK.get(), steelAmount, 1.0));
        pool1.addEntry(new WreckageLootData.Entry((Item)ModItems.HEAVY_ARMAMENT_MODULE.get(), 1, 0.5));
        pool1.addEntry(new WreckageLootData.Entry((Item)ModItems.LARGE_PROPELLER.get(), 1, 0.5));
        pool1.addEntry(new WreckageLootData.Entry((Item)ModItems.LARGE_MOTOR.get(), 1, 0.5));
        pool1.addEntry(new WreckageLootData.Entry((Item)ModItems.LARGE_BATTERY_PACK.get(), 1, 0.5));
        WreckageLootData.Pool.Builder pool2 = new WreckageLootData.Pool.Builder(1, "@Default", WreckageLootData.Pool.Type.COMPLETE);
        pool2.addEntry(new WreckageLootData.Entry((Item)ModItems.STEEL_BLOCK.get(), steelAmount, 0.5));
        builder.addPool(pool1.build());
        builder.addPool(pool2.build());
        return builder;
    }

    private WreckageLootData.Builder createSpecialLoot(int steelAmount) {
        WreckageLootData.Builder builder = new WreckageLootData.Builder();
        WreckageLootData.Pool.Builder pool1 = new WreckageLootData.Pool.Builder(1, "@Default", WreckageLootData.Pool.Type.COMPLETE);
        pool1.source(ModDamageTypes.REPAIR_TOOL);
        pool1.addEntry(new WreckageLootData.Entry((Item)ModItems.STEEL_BLOCK.get(), steelAmount, 1.0));
        pool1.addEntry(new WreckageLootData.Entry((Item)ModItems.HEAVY_ARMAMENT_MODULE.get(), 1, 0.5));
        pool1.addEntry(new WreckageLootData.Entry((Item)ModItems.LASER_UNIT.get(), 4, 0.5));
        pool1.addEntry(new WreckageLootData.Entry(Items.f_42791_, 2, 0.3));
        pool1.addEntry(new WreckageLootData.Entry((Item)ModItems.CEMENTED_CARBIDE_BLOCK.get(), 4, 0.5));
        pool1.addEntry(new WreckageLootData.Entry((Item)ModItems.LARGE_MOTOR.get(), 1, 0.5));
        pool1.addEntry(new WreckageLootData.Entry((Item)ModItems.LARGE_BATTERY_PACK.get(), 1, 0.5));
        WreckageLootData.Pool.Builder pool2 = new WreckageLootData.Pool.Builder(1, "@Default", WreckageLootData.Pool.Type.COMPLETE);
        pool2.addEntry(new WreckageLootData.Entry((Item)ModItems.STEEL_BLOCK.get(), steelAmount, 0.5));
        pool2.addEntry(new WreckageLootData.Entry(Items.f_42791_, 1, 0.15));
        builder.addPool(pool1.build());
        builder.addPool(pool2.build());
        return builder;
    }

    private WreckageLootData.Builder createLightVehicleLoot(int steelAmount) {
        WreckageLootData.Builder builder = new WreckageLootData.Builder();
        WreckageLootData.Pool.Builder pool1 = new WreckageLootData.Pool.Builder(1, "@Default", WreckageLootData.Pool.Type.COMPLETE);
        pool1.source(ModDamageTypes.REPAIR_TOOL);
        pool1.addEntry(new WreckageLootData.Entry((Item)ModItems.STEEL_BLOCK.get(), steelAmount, 1.0));
        pool1.addEntry(new WreckageLootData.Entry((Item)ModItems.WHEEL.get(), 1, 0.5));
        pool1.addEntry(new WreckageLootData.Entry((Item)ModItems.LARGE_MOTOR.get(), 1, 0.5));
        pool1.addEntry(new WreckageLootData.Entry((Item)ModItems.SMALL_BATTERY_PACK.get(), 1, 0.2));
        WreckageLootData.Pool.Builder pool2 = new WreckageLootData.Pool.Builder(1, "@Default", WreckageLootData.Pool.Type.COMPLETE);
        pool2.addEntry(new WreckageLootData.Entry((Item)ModItems.STEEL_BLOCK.get(), steelAmount, 0.5));
        builder.addPool(pool1.build());
        builder.addPool(pool2.build());
        return builder;
    }

    private WreckageLootData.Builder createDefaultLoot() {
        WreckageLootData.Builder builder = new WreckageLootData.Builder();
        WreckageLootData.Pool.Builder pool1 = new WreckageLootData.Pool.Builder(1, "@Default", WreckageLootData.Pool.Type.COMPLETE);
        pool1.source(ModDamageTypes.REPAIR_TOOL);
        pool1.addEntry(new WreckageLootData.Entry((Item)ModItems.STEEL_BLOCK.get(), 1, 1.0));
        WreckageLootData.Pool.Builder pool2 = new WreckageLootData.Pool.Builder(1, "@Default", WreckageLootData.Pool.Type.COMPLETE);
        pool2.addEntry(new WreckageLootData.Entry((Item)ModItems.STEEL_BLOCK.get(), 1, 0.2));
        builder.addPool(pool1.build());
        builder.addPool(pool2.build());
        return builder;
    }
}

