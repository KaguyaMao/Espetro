// zz_angle_probe.js — 实测角度抗性：正/背各挨 100 点伤害比较掉血（顶层函数，Rhino 不支持嵌套声明）
var AngProbeArmed = false
var AngProbeLvl = null

function angProbeVehicle() {
  var v = AngProbeLvl.createEntity('fcp:bmp2')
  v.setPosition(0.5, 120.0, 0.5)
  v.spawn()
  return v
}
function angProbeAttacker(z) {
  var a = AngProbeLvl.createEntity('minecraft:pig')
  a.setPosition(0.5, 120.0, z)
  a.spawn()
  return a
}
function angProbeHit(v, attacker) {
  var src = AngProbeLvl.damageSources().mobAttack(attacker)
  if (typeof v.attack === 'function') { v.attack(src, 100.0); return 'attack' }
  if (typeof v.hurt === 'function') { v.hurt(src, 100.0); return 'hurt' }
  if (v.minecraftEntity != null && typeof v.minecraftEntity.hurt === 'function') { v.minecraftEntity.hurt(src, 100.0); return 'rawHurt' }
  return 'NO_METHOD'
}

ServerEvents.tick(function (evt) {
  if (AngProbeArmed) return
  AngProbeArmed = true
  try {
    AngProbeLvl = evt.server.overworld()

    var vf = angProbeVehicle()
    var af = angProbeAttacker(10.5)
    var h0f = vf.health
    var how = angProbeHit(vf, af)
    var h1f = vf.health

    var vb = angProbeVehicle()
    var ab = angProbeAttacker(-9.5)
    var h0b = vb.health
    angProbeHit(vb, ab)
    var h1b = vb.health

    console.info('[AngProbe] method=' + how)
    console.info('[AngProbe] front ' + h0f + ' -> ' + h1f + ' = ' + (h0f - h1f))
    console.info('[AngProbe] back  ' + h0b + ' -> ' + h1b + ' = ' + (h0b - h1b))
    try { vf.discard(); vb.discard(); af.discard(); ab.discard() } catch (e) {}
  } catch (err) {
    console.error('[AngProbe] failed: ' + err)
  }
})
