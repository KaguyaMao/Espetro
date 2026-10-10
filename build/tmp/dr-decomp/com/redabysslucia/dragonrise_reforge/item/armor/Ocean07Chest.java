/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.init.ModAttributes
 *  com.atsuishio.superbwarfare.tiers.ModArmorMaterial
 *  com.google.common.collect.HashMultimap
 *  com.google.common.collect.Multimap
 *  net.minecraft.client.model.HumanoidModel
 *  net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer
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
 *  software.bernie.geckolib.model.GeoModel
 *  software.bernie.geckolib.renderer.GeoArmorRenderer
 *  software.bernie.geckolib.renderer.GeoItemRenderer
 *  software.bernie.geckolib.util.GeckoLibUtil
 */
package com.redabysslucia.dragonrise_reforge.item.armor;

import com.atsuishio.superbwarfare.init.ModAttributes;
import com.atsuishio.superbwarfare.tiers.ModArmorMaterial;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.redabysslucia.dragonrise_reforge.client.model.armor.Ocean07ChestModel;
import com.redabysslucia.dragonrise_reforge.client.renderer.armor.Ocean07ChestRenderer;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
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
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

public class Ocean07Chest
extends ArmorItem
implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache((GeoAnimatable)this);

    public Ocean07Chest() {
        super((ArmorMaterial)ModArmorMaterial.CEMENTED_CARBIDE, ArmorItem.Type.CHESTPLATE, new Item.Properties());
    }

    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions(){
            private GeoArmorRenderer<?> renderer;
            private GeoItemRenderer<Ocean07Chest> itemRenderer;

            public HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
                if (this.renderer == null) {
                    this.renderer = new Ocean07ChestRenderer();
                }
                this.renderer.prepForRender((Entity)livingEntity, itemStack, equipmentSlot, original);
                return this.renderer;
            }

            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (this.itemRenderer == null) {
                    this.itemRenderer = new GeoItemRenderer((GeoModel)new Ocean07ChestModel());
                }
                return this.itemRenderer;
            }
        });
    }

    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        Multimap map = super.m_7167_(slot);
        UUID uuid = new UUID(slot.toString().hashCode(), 0L);
        if (slot == EquipmentSlot.CHEST) {
            map = HashMultimap.create((Multimap)map);
            map.put((Object)((Attribute)ModAttributes.BULLET_RESISTANCE.get()), (Object)new AttributeModifier(uuid, "superbwarfare_attribute_modifier", 0.5 * Math.max(0.0, 1.0 - (double)stack.m_41773_() / (double)stack.m_41776_()), AttributeModifier.Operation.ADDITION));
        }
        return map;
    }

    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
    }

    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}

