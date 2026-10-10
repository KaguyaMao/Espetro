// zz_ammo_probe.js — 临时：列出各载具武器实际使用的弹药条目 / loadAmount / 弹匣（测完删除）
var zzAmmoDone = false

ServerEvents.tick(event => {
  if (zzAmmoDone) return
  zzAmmoDone = true
  try {
    var lvl = event.server.overworld()
    var AABBc = Java.loadClass('net.minecraft.world.phys.AABB')
    var all = lvl.getEntities(null, new AABBc(-2000000.0, -100.0, -2000000.0, 2000000.0, 400.0, 2000000.0))
    var GunPropCls = Java.loadClass('com.atsuishio.superbwarfare.data.gun.GunProp')
    var ForgeRegistries = Java.loadClass('net.minecraftforge.registries.ForgeRegistries')
    var seen = {}
    var i = 0
    for (i = 0; i < all.size(); i++) {
      var veh = all.get(i)
      try { veh.getLeftWheelMaxHealth() } catch (e) { continue }
      var vid = String(veh.getType())
      if (seen[vid]) continue
      seen[vid] = true
      var parts = []
      var seat = 0
      for (seat = 0; seat < veh.getMaxPassengers(); seat++) {
        var seatInfo = veh.getSeat(seat)
        if (!seatInfo) continue
        var weapons = seatInfo.weapons()
        if (!weapons || weapons.isEmpty()) continue
        var w = 0
        for (w = 0; w < weapons.size(); w++) {
          var gd = veh.getGunData(seat, w)
          if (!gd) continue
          var consumers = gd.get(GunPropCls.AMMO_CONSUMER)
          if (!consumers || consumers.isEmpty()) continue
          var c = 0
          for (c = 0; c < consumers.size(); c++) {
            var cons = consumers.get(c)
            var st = cons.stack()
            var id = st.isEmpty() ? '(空)' : String(ForgeRegistries.ITEMS.getKey(st.getItem()))
            parts.push(String(weapons.get(w)) + ':' + id + ' load=' + cons.getLoadAmount() + ' mag=' + gd.get(GunPropCls.MAGAZINE))
          }
        }
      }
      console.info('[zzammo] ' + vid + ' -> ' + (parts.length ? parts.join(' | ') : '(无弹药消费者)'))
    }
  } catch (e) { console.error('[zzammo] err: ' + e) }
})
