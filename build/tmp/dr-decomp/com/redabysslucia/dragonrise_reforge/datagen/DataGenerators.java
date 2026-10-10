/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.data.DataGenerator
 *  net.minecraft.data.DataProvider
 *  net.minecraft.data.PackOutput
 *  net.minecraftforge.common.data.ExistingFileHelper
 *  net.minecraftforge.data.event.GatherDataEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 */
package com.redabysslucia.dragonrise_reforge.datagen;

import com.redabysslucia.dragonrise_reforge.datagen.GeoOBBDataProvider;
import com.redabysslucia.dragonrise_reforge.datagen.MeshModelNormalRemover;
import com.redabysslucia.dragonrise_reforge.datagen.ModRecipeProvider;
import com.redabysslucia.dragonrise_reforge.datagen.ModWreckageLootProvider;
import com.redabysslucia.dragonrise_reforge.datagen.VehicleDataCompletenessChecker;
import com.redabysslucia.dragonrise_reforge.datagen.VehicleJavaGenerator;
import com.redabysslucia.dragonrise_reforge.datagen.VehicleLanguageGenerator;
import com.redabysslucia.dragonrise_reforge.datagen.VehicleSkinGenerator;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="dragonrise_reforge", bus=Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        generator.addProvider(event.includeServer(), (DataProvider)new ModRecipeProvider(packOutput));
        generator.addProvider(event.includeServer(), (DataProvider)new ModWreckageLootProvider(packOutput, existingFileHelper));
        generator.addProvider(event.includeServer(), (DataProvider)new GeoOBBDataProvider(packOutput, existingFileHelper));
        generator.addProvider(event.includeServer(), (DataProvider)new VehicleJavaGenerator(packOutput, existingFileHelper));
        generator.addProvider(event.includeServer(), (DataProvider)new VehicleLanguageGenerator(packOutput, existingFileHelper));
        generator.addProvider(event.includeServer(), (DataProvider)new MeshModelNormalRemover(packOutput, existingFileHelper));
        generator.addProvider(event.includeServer(), (DataProvider)new VehicleSkinGenerator(packOutput, existingFileHelper));
        generator.addProvider(event.includeServer(), (DataProvider)new VehicleDataCompletenessChecker(packOutput, existingFileHelper));
    }
}

