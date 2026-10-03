/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.data.gun.GunData
 *  com.atsuishio.superbwarfare.data.gun.ShootParameters
 *  com.atsuishio.superbwarfare.item.gun.GunGeoItem
 *  com.atsuishio.superbwarfare.tools.ParticleTool
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.Rarity
 *  org.jetbrains.annotations.NotNull
 *  software.bernie.geckolib.renderer.GeoItemRenderer
 */
package tech.vvp.vvp.item.gun;

import com.atsuishio.superbwarfare.data.gun.GunData;
import com.atsuishio.superbwarfare.data.gun.ShootParameters;
import com.atsuishio.superbwarfare.item.gun.GunGeoItem;
import com.atsuishio.superbwarfare.tools.ParticleTool;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import tech.vvp.vvp.client.renderer.gun.At4ItemRenderer;

public class At4Item
extends GunGeoItem {
    public At4Item() {
        super(new Item.Properties().m_41497_(Rarity.RARE));
    }

    public Supplier<? extends GeoItemRenderer<? extends Item>> getRenderer() {
        return At4ItemRenderer::new;
    }

    public boolean shootBullet(@NotNull ShootParameters parameters) {
        if (!super.shootBullet(parameters)) {
            return false;
        }
        Entity shooter = parameters.shooter;
        ServerLevel level = parameters.level;
        if (shooter != null) {
            ParticleTool.sendParticle((ServerLevel)level, (ParticleOptions)ParticleTypes.f_123796_, (double)(shooter.m_20185_() + 1.8 * shooter.m_20154_().f_82479_), (double)(shooter.m_20186_() + (double)shooter.m_20206_() - 0.1 + 1.8 * shooter.m_20154_().f_82480_), (double)(shooter.m_20189_() + 1.8 * shooter.m_20154_().f_82481_), (int)30, (double)0.4, (double)0.4, (double)0.4, (double)0.005, (boolean)true);
        }
        return true;
    }

    public void whenNoAmmo(GunData data) {
        data.isEmpty.set(true);
        data.closeHammer.set(true);
    }

    public void addReloadTimeBehavior(Map<Integer, Consumer<GunData>> behaviors) {
        super.addReloadTimeBehavior(behaviors);
        behaviors.put(84, data -> data.isEmpty.set(false));
        behaviors.put(9, data -> data.closeHammer.set(false));
    }

    public boolean canEditAttachments(GunData data) {
        return true;
    }
}

