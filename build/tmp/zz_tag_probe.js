// zz_tag_probe.js — 临时探针：打印 #espetro:minable_in_battle 的有效成员（用后即停用）
var TagProbeArmed = false
ServerEvents.tick(function (evt) {
  if (TagProbeArmed) return
  TagProbeArmed = true
  try {
    var TagProbeReg = Java.loadClass('net.minecraft.core.registries.BuiltInRegistries')
    var TagProbeKey = Java.loadClass('net.minecraft.tags.TagKey')
    var TagProbeRegistries = Java.loadClass('net.minecraft.core.registries.Registries')
    var TagProbeRL = Java.loadClass('net.minecraft.resources.ResourceLocation')
    var probeTag = TagProbeKey.create(TagProbeRegistries.BLOCK, new TagProbeRL('espetro', 'minable_in_battle'))
    var probeOpt = TagProbeReg.BLOCK.getTag(probeTag)
    if (probeOpt == null || !probeOpt.isPresent()) {
      console.info('[TagProbe] tag NOT present')
    } else {
      var probeIds = []
      var probeIt = probeOpt.get().iterator()
      while (probeIt.hasNext()) {
        var probeHolder = probeIt.next()
        var probeKey = probeHolder.unwrapKey()
        if (probeKey.isPresent()) probeIds.push(String(probeKey.get().location()))
      }
      probeIds.sort()
      console.info('[TagProbe] members=' + probeIds.length)
      console.info('[TagProbe] ' + probeIds.join(', '))
    }
  } catch (TagProbeErr) {
    console.error('[TagProbe] failed: ' + TagProbeErr)
  }
})
