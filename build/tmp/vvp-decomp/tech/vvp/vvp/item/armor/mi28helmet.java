/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.init.ModAttributes
 *  com.google.common.collect.HashMultimap
 *  com.google.common.collect.Multimap
 *  net.minecraft.client.model.HumanoidModel
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.ai.attributes.Attribute
 *  net.minecraft.world.entity.ai.attributes.AttributeModifier
 *  net.minecraft.world.entity.ai.attributes.AttributeModifier$Operation
 *  net.minecraft.world.item.ArmorItem
 *  net.minecraft.world.item.ArmorItem$Type
 *  net.minecraft.world.item.ArmorMaterial
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.ItemStack
 *  net.minecraftforge.client.extensions.common.IClientItemExtensions
 *  software.bernie.geckolib.animatable.GeoItem
 *  software.bernie.geckolib.core.animatable.GeoAnimatable
 *  software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache
 *  software.bernie.geckolib.core.animation.AnimatableManager$ControllerRegistrar
 *  software.bernie.geckolib.renderer.GeoArmorRenderer
 *  software.bernie.geckolib.util.GeckoLibUtil
 */
package tech.vvp.vvp.item.armor;

import com.atsuishio.superbwarfare.init.ModAttributes;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;
import tech.vvp.vvp.client.renderer.armor.mi28helmetRenderer;
import tech.vvp.vvp.tiers.ModArmorMaterial;

public class mi28helmet
extends ArmorItem
implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache((GeoAnimatable)this);

    public mi28helmet() {
        super((ArmorMaterial)ModArmorMaterial.MI28, ArmorItem.Type.HELMET, new Item.Properties());
    }

    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions(){
            private GeoArmorRenderer<?> renderer;

            public HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
                if (this.renderer == null) {
                    this.renderer = new mi28helmetRenderer();
                }
                this.renderer.prepForRender((Entity)livingEntity, itemStack, equipmentSlot, original);
                return this.renderer;
            }
        });
    }

    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        Multimap map = super.m_7167_(slot);
        UUID uuid = new UUID(slot.toString().hashCode(), 0L);
        if (slot == EquipmentSlot.HEAD) {
            map = HashMultimap.create((Multimap)map);
            map.put((Object)((Attribute)ModAttributes.BULLET_RESISTANCE.get()), (Object)new AttributeModifier(uuid, "superbwarfare_attribute_modifier", 0.2 * Math.max(0.0, 1.0 - (double)stack.m_41773_() / (double)stack.m_41776_()), AttributeModifier.Operation.ADDITION));
        }
        return map;
    }

    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
    }

    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}

