// 将所有物品（原版 + 模组）的盔甲值（armor）与盔甲韧性（armor_toughness）清零。
// 原理：装备属性统一由 ItemAttributeModifierEvent 计算，在这里移除两类修饰符即可全量覆盖。
// 注意：只影响物品提供的护甲属性；附魔"保护"、药水"抗性提升"等减伤效果不受影响。
ForgeEvents.onEvent('net.minecraftforge.event.ItemAttributeModifierEvent', event => {
    const Attributes = Java.loadClass('net.minecraft.world.entity.ai.attributes.Attributes');
    event.removeAttribute(Attributes.ARMOR);
    event.removeAttribute(Attributes.ARMOR_TOUGHNESS);
});
