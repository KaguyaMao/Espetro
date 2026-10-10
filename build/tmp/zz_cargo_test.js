// zz_cargo_test.js — 临时验证：载具 ITEM_HANDLER 能力 / ItemParser 解析 / 写入集装箱（测完删除）
var zzCargoDone = false

ServerEvents.tick(event => {
  if (zzCargoDone) return
  zzCargoDone = true
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
    if (!veh) { console.info('[zzcargo] 没找到 zbd04a'); return }
    console.info('[zzcargo] 载具 = ' + veh.getType() + ' @ ' + veh.getX().toFixed(1) + ',' + veh.getY().toFixed(1) + ',' + veh.getZ().toFixed(1))

    var ForgeCapabilities = Java.loadClass('net.minecraftforge.common.capabilities.ForgeCapabilities')
    var cap = veh.getCapability(ForgeCapabilities.ITEM_HANDLER)
    console.info('[zzcargo] ITEM_HANDLER present = ' + cap.isPresent())
    var handler = cap.orElse(null)
    if (!handler) { console.info('[zzcargo] 无物品栏能力'); return }
    console.info('[zzcargo] slots = ' + handler.getSlots())

    var emptySlot = -1
    var s = 0
    for (s = 0; s < handler.getSlots(); s++) {
      if (handler.getStackInSlot(s).isEmpty()) { emptySlot = s; break }
    }
    console.info('[zzcargo] 空槽 = ' + emptySlot)
    if (emptySlot < 0) return

    var ItemParser = Java.loadClass('net.minecraft.commands.arguments.item.ItemParser')
    var StringReaderCls = Java.loadClass('com.mojang.brigadier.StringReader')
    var Registries = Java.loadClass('net.minecraft.core.registries.Registries')
    var ItemStackCls = Java.loadClass('net.minecraft.world.item.ItemStack')

    var lookup = lvl.registryAccess().lookupOrThrow(Registries.ITEM)
    var raw = 'superbwarfare:large_shell_ap 12'
    var parsed = ItemParser.parseForItem(lookup, new StringReaderCls(raw))
    var stack = new ItemStackCls(parsed.item().value())
    if (parsed.nbt() != null) stack.setTag(parsed.nbt())
    stack.setCount(12)
    console.info('[zzcargo] 解析结果 = ' + stack)

    var rest = stack
    var k = 0
    for (k = 0; k < handler.getSlots() && !rest.isEmpty(); k++) {
      rest = handler.insertItem(k, rest, false)
    }
    console.info('[zzcargo] 写入后 剩余=' + rest.getCount() + '  槽' + emptySlot + '=' + handler.getStackInSlot(emptySlot))

    var back = handler.extractItem(emptySlot, 64, false)
    console.info('[zzcargo] 收回 = ' + back + '  槽' + emptySlot + ' 现在=' + handler.getStackInSlot(emptySlot))
    console.info('[zzcargo] 测试结束（已恢复空槽）')
  } catch (e) {
    console.error('[zzcargo] err: ' + e)
  }
})
