// zz_t5.js — 临时：修复"部件已损坏"的载具 + 验证 API 路径（测试完删除）
var t5Done = false

function t5AsVehicle(candidate) {
  try {
    candidate.getLeftWheelMaxHealth()
    return candidate
  } catch (err) {
    return null
  }
}

ServerEvents.tick(event => {
  if (t5Done) return
  t5Done = true
  try {
    var lvl = event.server.overworld()
    var AABBClass = Java.loadClass('net.minecraft.world.phys.AABB')
    var all = lvl.getEntities(null, new AABBClass(-2000000.0, -100.0, -2000000.0, 2000000.0, 400.0, 2000000.0))
    var fixed = 0
    var i = 0
    for (i = 0; i < all.size(); i++) {
      var ent = all.get(i)
      var veh = t5AsVehicle(ent)
      if (!veh) continue
      // 顺带验证 API：区域查询 / 包围盒 / 视线 / 骑乘
      var near = lvl.getEntities(veh, veh.getBoundingBox().inflate(6.0))
      console.info('[t5] api-check ' + String(ent.getType()) + ' near=' + near.size() + ' box=' + veh.getBoundingBox() +
        ' eye=' + veh.getEyeHeight() + ' vehicle=' + veh.getVehicle() + ' view=' + veh.getViewVector(1.0))

      var needFix = veh.getLeftWheelDamaged() || veh.getRightWheelDamaged() || veh.getTurretDamaged() ||
        veh.getMainEngineDamaged() || veh.getSubEngineDamaged()
      if (!needFix) continue

      var before = 'L' + veh.getLeftWheelHealth() + '/' + veh.getLeftWheelDamaged() +
        ' R' + veh.getRightWheelHealth() + '/' + veh.getRightWheelDamaged() +
        ' T' + veh.getTurretHealth() + '/' + veh.getTurretDamaged() +
        ' E' + veh.getMainEngineHealth() + '/' + veh.getMainEngineDamaged() +
        ' S' + veh.getSubEngineHealth() + '/' + veh.getSubEngineDamaged()

      if (veh.getLeftWheelDamaged()) veh.setLeftWheelHealth(veh.getLeftWheelMaxHealth())
      if (veh.getRightWheelDamaged()) veh.setRightWheelHealth(veh.getRightWheelMaxHealth())
      if (veh.getTurretDamaged()) veh.setTurretHealth(veh.getTurretMaxHealth())
      if (veh.getMainEngineDamaged()) veh.setMainEngineHealth(veh.getMainEngineMaxHealth())
      if (veh.getSubEngineDamaged()) veh.setSubEngineHealth(veh.getSubEngineMaxHealth())
      fixed++
      console.info('[t5] FIXED ' + String(ent.getType()) + ' @ ' + ent.getX().toFixed(1) + ',' + ent.getY().toFixed(1) + ',' + ent.getZ().toFixed(1) +
        ' | before ' + before +
        ' | after L' + veh.getLeftWheelHealth() + ' R' + veh.getRightWheelHealth() + ' T' + veh.getTurretHealth() +
        ' E' + veh.getMainEngineHealth() + ' S' + veh.getSubEngineHealth())
    }
    console.info('[t5] damaged vehicles fixed = ' + fixed)
  } catch (err) {
    console.error('[t5] err: ' + err)
  }
})
