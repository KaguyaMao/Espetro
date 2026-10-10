/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.InputConstants$Type
 *  net.minecraft.client.KeyMapping
 *  net.minecraftforge.common.util.Lazy
 */
package LOL_141.vehicle_addition.init;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.common.util.Lazy;

public final class ModKeyMappings {
    public static final Lazy<KeyMapping> RADIO_CONFIG = Lazy.of(() -> new KeyMapping("key.vehicle_addition.config", InputConstants.Type.KEYSYM, 75, "key.categories.vehicle_addition"));

    private ModKeyMappings() {
    }
}

