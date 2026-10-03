// zz_impact_tag_probe.js — 临时探针：打印 #superbwarfare:normal_collision 的有效成员
// 用途：核对"羊毛 / 台阶 / 半砖 / 门"是否已从载具撞击可破坏列表剔除。
// 核对完成后把本文件改名为 zz_impact_tag_probe.js.disabled 即可（或直接删掉）。
var ImpactTagProbeArmed = false

ServerEvents.tick(function (evt) {
  if (ImpactTagProbeArmed) return
  ImpactTagProbeArmed = true
  try {
    var Reg = Java.loadClass('net.minecraft.core.registries.BuiltInRegistries')
    var TagKey = Java.loadClass('net.minecraft.tags.TagKey')
    var Registries = Java.loadClass('net.minecraft.core.registries.Registries')
    var RL = Java.loadClass('net.minecraft.resources.ResourceLocation')
    var tag = TagKey.create(Registries.BLOCK, new RL('superbwarfare', 'normal_collision'))
    var opt = Reg.BLOCK.getTag(tag)
    if (opt == null || !opt.isPresent()) {
      console.info('[ImpactProbe] tag NOT present')
      return
    }
    var ids = []
    var it = opt.get().iterator()
    while (it.hasNext()) {
      var k = it.next().unwrapKey()
      if (k.isPresent()) ids.push(String(k.get().location()))
    }
    ids.sort()
    var wool = ids.filter(function (v) { return v.indexOf('_wool') >= 0 })
    var stairs = ids.filter(function (v) { return v.indexOf('_stairs') >= 0 })
    var slab = ids.filter(function (v) { return v.indexOf('_slab') >= 0 })
    var door = ids.filter(function (v) { return v.indexOf('_door') >= 0 })
    console.info('[ImpactProbe] normal_collision 成员数=' + ids.length)
    console.info('[ImpactProbe] 羊毛=' + wool.length + ' 台阶=' + stairs.length + ' 半砖=' + slab.length + ' 门=' + door.length)
    console.info('[ImpactProbe] members=' + ids.join(', '))
  } catch (e) {
    console.error('[ImpactProbe] failed: ' + e)
  }
})
