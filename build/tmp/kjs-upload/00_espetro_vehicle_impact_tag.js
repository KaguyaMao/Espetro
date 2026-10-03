// Espetro：载具撞击破坏 —— 剔除 羊毛 / 台阶 / 半砖 / 门
// 原理：载具撞击可破坏集合 = #sbw_addition:impact_breakable，其中引用了
//       #superbwarfare:normal_collision；从后者移除这几组即可（嵌套标签自动传导）。
//       羊毛=#minecraft:wool、台阶=#minecraft:stairs、半砖=#minecraft:slabs、门=#minecraft:doors
// 备注：活板门/栅栏/栅栏门/墙 仍在可撞列表内；要一起剔除就把对应标签加进下面的数组。
var EspetroImpactExcludedTags = [
  '#minecraft:wool',
  '#minecraft:stairs',
  '#minecraft:slabs',
  '#minecraft:doors'
]

ServerEvents.tags('block', event => {
  event.remove('superbwarfare:normal_collision', EspetroImpactExcludedTags)
  console.info('[Espetro] 载具撞击破坏已剔除: ' + EspetroImpactExcludedTags.join(', '))
})
