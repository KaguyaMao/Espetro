package org.espetro.bastion;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FortificationNbtSanitizerTest {

    private static FortificationConfig.EntityPolicy filterPolicy() {
        return new FortificationConfig.EntityPolicy();
    }

    private static FortificationConfig.EntityPolicy rejectPolicy() {
        FortificationConfig.EntityPolicy policy = new FortificationConfig.EntityPolicy();
        policy.mode = "reject";
        return policy;
    }

    @Test
    void entityKeepsVisualFieldsAndStripsGameplayState() {
        CompoundTag raw = new CompoundTag();
        raw.putString("id", "minecraft:armor_stand");
        raw.putBoolean("Small", true);
        raw.putFloat("Health", 99F);
        raw.put("Attributes", new ListTag());
        raw.put("Brain", new CompoundTag());
        raw.put("ForgeCaps", new CompoundTag());
        raw.putUUID("UUID", java.util.UUID.randomUUID());
        CompoundTag clean = FortificationNbtSanitizer.sanitizeEntity(raw, 0, 4, 4096,
            filterPolicy(), new ArrayList<>());
        assertNotNull(clean);
        assertTrue(clean.getBoolean("Small"));
        assertEquals("minecraft:armor_stand", clean.getString("id"));
        for (String forbidden : new String[]{"Health", "Attributes", "Brain", "ForgeCaps", "UUID"}) {
            assertFalse(clean.contains(forbidden), forbidden);
        }
    }

    /** filter（默认）：不允许的类型被剔除并记警告，不再让整份配置冻结失败。 */
    @Test
    void filterModeDropsDisallowedTypesWithWarnings() {
        List<String> warnings = new ArrayList<>();

        CompoundTag player = new CompoundTag();
        player.putString("id", "minecraft:player");
        assertNull(FortificationNbtSanitizer.sanitizeEntity(player, 0, 4, 4096,
            filterPolicy(), warnings));

        CompoundTag pig = new CompoundTag();
        pig.putString("id", "minecraft:pig");
        assertNull(FortificationNbtSanitizer.sanitizeEntity(pig, 0, 4, 4096,
            filterPolicy(), warnings));

        CompoundTag chest = new CompoundTag();
        chest.putString("id", "minecraft:chest");
        chest.put("Items", new ListTag());
        assertNull(FortificationNbtSanitizer.sanitizeBlockEntity(chest, filterPolicy(), 4096,
            warnings));

        assertEquals(3, warnings.size(), warnings.toString());
    }

    /** reject：旧的一票否决行为。 */
    @Test
    void rejectModeThrows() {
        CompoundTag player = new CompoundTag();
        player.putString("id", "minecraft:player");
        assertThrows(IllegalArgumentException.class,
            () -> FortificationNbtSanitizer.sanitizeEntity(player, 0, 4, 4096,
                rejectPolicy(), new ArrayList<>()));

        CompoundTag chest = new CompoundTag();
        chest.putString("id", "minecraft:chest");
        chest.put("Items", new ListTag());
        assertThrows(IllegalArgumentException.class,
            () -> FortificationNbtSanitizer.sanitizeBlockEntity(chest, rejectPolicy(), 4096,
                new ArrayList<>()));
    }

    /** extra_* 白名单可放开某些类型与字段。 */
    @Test
    void extraPolicyAllowsConfiguredTypesAndFields() {
        FortificationConfig.EntityPolicy policy = filterPolicy();
        policy.extraEntityTypes = new ArrayList<>(List.of("minecraft:pig"));
        policy.extraEntityFields = new java.util.LinkedHashMap<>();
        policy.extraEntityFields.put("minecraft:pig", new ArrayList<>(List.of("CustomName")));

        CompoundTag pig = new CompoundTag();
        pig.putString("id", "minecraft:pig");
        pig.putString("CustomName", "{\"text\":\"猪\"}");
        pig.putBoolean("Saddle", true);
        CompoundTag clean = FortificationNbtSanitizer.sanitizeEntity(pig, 0, 4, 4096, policy,
            new ArrayList<>());
        assertNotNull(clean);
        assertTrue(clean.contains("CustomName"));
        assertFalse(clean.contains("Saddle"));
    }

    @Test
    void deepPassengersStillRejected() {
        CompoundTag passenger = new CompoundTag();
        passenger.putString("id", "minecraft:armor_stand");
        ListTag passengers = new ListTag();
        passengers.add(passenger);
        CompoundTag root = new CompoundTag();
        root.putString("id", "dragonrise_reforge:ammo_supply_station");
        root.put("Passengers", passengers);
        assertThrows(IllegalArgumentException.class,
            () -> FortificationNbtSanitizer.sanitizeRootEntity(root,
                new ResourceLocation("dragonrise_reforge:ammo_supply_station"), 0, 4096));
    }
}
