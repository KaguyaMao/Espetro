// zz_chunk_test.js — 临时：验证 >64 整摞会被拒收、分块则能进（测完删除）
var zzChunkDone = false

ServerEvents.tick(event => {
  if (zzChunkDone) return
  zzChunkDone = true
  try {
    var lvl = event.server.overworld()
    var AABBc = Java.loadClass('net.minecraft.world.phys.AABB')
    var all = lvl.getEntities(null, new AABBc(-2000000.0, -100.0, -2000000.0, 2000000.0, 400.0, 2000000.0))
    var veh = null
    var i = 0
    for (i = 0; i < all.size(); i++) {
      var cand = all.get(i)
      if (String(cand.getType()).indexOf('zbd04a') >= 0) { veh = cand; break }
    }
    if (!veh) { console.info('[zzchunk] 没找到载具'); return }

    var ForgeCapabilities = Java.loadClass('net.minecraftforge.common.capabilities.ForgeCapabilities')
    var handler = veh.getCapability(ForgeCapabilities.ITEM_HANDLER).orElse(null)
    if (!handler) { console.info('[zzchunk] 无物品栏'); return }
    console.info('[zzchunk] 载具=' + veh.getType() + ' 载具堆叠上限=' + veh.getMaxStackSize() + ' 格数=' + handler.getSlots())

    var ItemParser = Java.loadClass('net.minecraft.commands.arguments.item.ItemParser')
    var StringReaderCls = Java.loadClass('com.mojang.brigadier.StringReader')
    var Registries = Java.loadClass('net.minecraft.core.registries.Registries')
    var ItemStackCls = Java.loadClass('net.minecraft.world.item.ItemStack')
    var lookup = lvl.registryAccess().lookupOrThrow(Registries.ITEM)
    var parsed = ItemParser.parseForItem(lookup, new StringReaderCls('superbwarfare:rifle_ammo'))
    var template = new ItemStackCls(parsed.item().value())
    console.info('[zzchunk] 物品堆叠上限=' + template.getMaxStackSize())

    // A) 整摞 500 直接 insert
    var big = template.copy()
    big.setCount(500)
    var leftA = handler.insertItem(0, big, false)
    console.info('[zzchunk] A 整摞500 -> 被退回 ' + leftA.getCount() + '（500=全被拒 / 0=全进）')
    // 清干净
    var s = 0
    for (s = 0; s < handler.getSlots(); s++) if (!handler.getStackInSlot(s).isEmpty()) handler.extractItem(s, 64, false)

    // B) 分块 64 插入
    var remaining = 500
    var guard = 0
    while (remaining > 0 && guard++ < 100) {
      var chunk = Math.min(remaining, template.getMaxStackSize())
      var piece = template.copy()
      piece.setCount(chunk)
      var slot = 0
      for (slot = 0; slot < handler.getSlots() && !piece.isEmpty(); slot++) piece = handler.insertItem(slot, piece, false)
      var moved = chunk - piece.getCount()
      if (moved <= 0) break
      remaining -= moved
    }
    var used = 0
    var slots = []
    for (s = 0; s < handler.getSlots(); s++) {
      var st = handler.getStackInSlot(s)
      if (!st.isEmpty()) { used += st.getCount(); slots.push(st.getCount()) }
    }
    console.info('[zzchunk] B 分块64 -> 进车 ' + used + ' 发，剩余未进 ' + remaining + '，占用槽位=' + (slots.join('/') || '无'))

    // 清理
    for (s = 0; s < handler.getSlots(); s++) if (!handler.getStackInSlot(s).isEmpty()) handler.extractItem(s, 64, false)
    console.info('[zzchunk] 测试结束，已清空')
  } catch (e) {
    console.error('[zzchunk] err: ' + e)
  }
})
