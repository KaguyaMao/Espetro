// zz_probe.js — 临时探针 v2（测完删）
console.info('[probe] start')
try {
  console.info('[probe] inline loadClass = ' + Java.loadClass('java.lang.System').getProperty('java.version'))
} catch (e) { console.error('[probe] inline failed: ' + e) }

function dump(className, needles) {
  try {
    var arr = Java.loadClass(className).getMethods()
    var names = []
    for (var i = 0; i < arr.length; i++) names.push(arr[i].getName())
    var found = []
    for (var j = 0; j < needles.length; j++) {
      found.push(needles[j] + '=' + (names.indexOf(needles[j]) >= 0))
    }
    console.info('[probe] ' + className + ' -> ' + found.join(' '))
  } catch (e) { console.error('[probe] ' + className + ' failed: ' + e) }
}

dump('net.minecraft.world.entity.player.Player', ['getX', 'm_20185_', 'getLookAngle', 'm_20176_', 'getBoundingBox', 'm_20191_', 'isShiftKeyDown', 'm_6144_', 'getMainHandItem', 'm_21205_'])
dump('net.minecraft.world.level.Level', ['getEntities', 'm_6249_', 'getEntitiesOfClass', 'm_6443_'])
dump('com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity', ['setLeftWheelHealth', 'getLeftWheelMaxHealth', 'getTurretHealth', 'setTurretHealth', 'isWreck', 'setLeftWheelDamaged'])
dump('net.minecraft.world.phys.Vec3', ['dot', 'm_82526_', 'normalize', 'm_82541_', 'subtract', 'm_82549_'])
dump('net.minecraft.world.item.ItemStack', ['getItem', 'm_41720_', 'getDescriptionId'])
console.info('[probe] done')
