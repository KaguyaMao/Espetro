// zz_angle_probe2.js 鈥?鍚屼竴鍙拌浇鍏枫€佺垎鐐告簮锛堢粫杩囨姢鐢诧級鍒嗗埆浠庢/鑳屽懡涓紝闅旂瑙掑害鍥犵礌
var Ang2Armed = false
var Ang2Lvl = null

function ang2Vehicle() {
  var v = Ang2Lvl.createEntity('superbwarfare:bmp_2')
  v.setPosition(0.5, 120.0, 0.5)
  v.spawn()
  return v
}
function ang2Attacker(z) {
  var a = Ang2Lvl.createEntity('minecraft:pig')
  a.setPosition(0.5, 120.0, z)
  a.spawn()
  return a
}
function ang2ExplodeHit(v, attacker, amount) {
  var src = Ang2Lvl.damageSources().explosion(attacker, attacker)
  if (typeof v.attack === 'function') { v.attack(src, amount); return 'attack' }
  return 'NO_METHOD'
}
function ang2Move(entity, z) {
  entity.setPosition(0.5, 120.0, z)
}

ServerEvents.tick(function (evt) {
  if (Ang2Armed) return
  Ang2Armed = true
  try {
    Ang2Lvl = evt.server.overworld()
    var v = ang2Vehicle()
    var a = ang2Attacker(10.5)
    console.info('[Ang3] yaw=' + v.yRot + ' pitch=' + v.xRot + ' maxHealth=' + v.maxHealth)

    var h0 = v.health
    var how = ang2ExplodeHit(v, a, 100.0)
    var h1 = v.health
    console.info('[Ang3] ' + how + ' 姝ｉ潰(z+10.5): ' + h0 + ' -> ' + h1 + ' = ' + (h0 - h1))

    ang2Move(a, -9.5)
    var h2 = v.health
    ang2ExplodeHit(v, a, 100.0)
    var h3 = v.health
    console.info('[Ang3] 鑳岄潰(z-9.5): ' + h2 + ' -> ' + h3 + ' = ' + (h2 - h3))

    ang2Move(a, 10.5)
    var h4 = v.health
    ang2ExplodeHit(v, a, 100.0)
    var h5 = v.health
    console.info('[Ang3] 姝ｉ潰鍐嶆潵涓€娆? ' + h4 + ' -> ' + h5 + ' = ' + (h4 - h5))

    try { v.discard(); a.discard() } catch (e) {}
  } catch (err) {
    console.error('[Ang3] failed: ' + err)
  }
})
