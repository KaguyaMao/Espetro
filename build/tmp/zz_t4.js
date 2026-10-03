// zz_t4.js — 临时诊断：扫描已加载区域内的 SBW 载具，报告部件耐久（测试完删除）
var t4Done = false

function t4AsVehicle(candidate) {
  try {
    candidate.getLeftWheelMaxHealth()
    return candidate
  } catch (err) {
    return null
  }
}

ServerEvents.tick(event => {
  if (t4Done) return
  t4Done = true
  try {
    var lvl = event.server.overworld()
    var AABBClass = Java.loadClass('net.minecraft.world.phys.AABB')
    var scanBox = new AABBClass(-2000000.0, -100.0, -2000000.0, 2000000.0, 400.0, 2000000.0)
    var all = lvl.getEntities(null, scanBox)
    console.info('[t4] loaded entities total = ' + all.size())
    var vehicles = 0
    var i = 0
    for (i = 0; i < all.size(); i++) {
      var ent = all.get(i)
      var veh = t4AsVehicle(ent)
      if (!veh) continue
      vehicles++
      console.info('[t4] VEHICLE ' + String(ent.getType()) + ' @ ' + ent.getX().toFixed(1) + ',' + ent.getY().toFixed(1) + ',' + ent.getZ().toFixed(1) +
        ' | HP ' + veh.getHealth() + '/' + veh.getMaxHealth() +
        ' | L ' + veh.getLeftWheelHealth() + '/' + veh.getLeftWheelMaxHealth() + ' damaged=' + veh.getLeftWheelDamaged() +
        ' R ' + veh.getRightWheelHealth() + '/' + veh.getRightWheelMaxHealth() + ' damaged=' + veh.getRightWheelDamaged() +
        ' | turret ' + veh.getTurretHealth() + '/' + veh.getTurretMaxHealth() + ' damaged=' + veh.getTurretDamaged() +
        ' | engine ' + veh.getMainEngineHealth() + '/' + veh.getMainEngineMaxHealth() + ' sub ' + veh.getSubEngineHealth() + '/' + veh.getSubEngineMaxHealth() +
        ' | wreck=' + veh.isWreck())
    }
    console.info('[t4] vehicles found = ' + vehicles)
  } catch (err) {
    console.error('[t4] err: ' + err)
  }
})
