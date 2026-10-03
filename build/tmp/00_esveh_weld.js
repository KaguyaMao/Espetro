// 00_esveh_weld.js — 焊枪焊接载具部件（履带 / 炮塔 / 发动机）
//
// 背景：SBW 0.8.9.1 的维修工具(焊枪)只调用 vehicle.heal()，即只恢复"载具主耐久"。
//       部件耐久(PartHealth: Turret/LeftWheel/RightWheel/MainEngine/SubEngine) 在代码里
//       只有 4 条写入路径：初始化、读 NBT、hurt 里扣减、主耐久<5%时归零 —— 没有任何恢复路径；
//       而"部件已损坏"标记要求部件耐久 > 95% 上限才会清除。
//       => 履带一旦被打断就永久损坏，焊枪修不回来（维基也写明焊枪只恢复耐久）。
//
// 本脚本补上恢复路径：手持焊枪、面朝载具（≤6 格）或坐在载具上时，
//       每 4 tick 恢复各部件"上限的 5%"（至少 1 点）。履带/炮塔/发动机都会逐步焊回来。
//
// 附带指令：/esveh repair —— 把正前方(或骑乘)载具的所有部件一次修满
//           /esveh parts  —— 查看正前方载具各部件耐久

const ESVEH_RANGE = 6.0
const ESVEH_INTERVAL = 4
const ESVEH_PERCENT = 0.05
const ESVEH_ITEM = 'repair_tool' // item.superbwarfare.repair_tool

var esvehTick = 0
var esvehErrStamp = 0

// 试着当载具用：普通实体调用 SBW 的方法会抛错，直接吞掉
function esvehAsVehicle(candidate) {
  try {
    candidate.getLeftWheelMaxHealth()
    return candidate
  } catch (err) {
    return null
  }
}

function esvehIsWreck(vehicle) {
  try {
    return vehicle.isWreck()
  } catch (err) {
    return false
  }
}

// 各部件按上限百分比恢复
function esvehWeldParts(vehicle) {
  var worked = false
  var partMax = 0
  var partNow = 0
  var step = 0

  partMax = vehicle.getLeftWheelMaxHealth()
  partNow = vehicle.getLeftWheelHealth()
  if (partNow < partMax) {
    step = Math.max(1, partMax * ESVEH_PERCENT)
    vehicle.setLeftWheelHealth(Math.min(partMax, partNow + step))
    worked = true
  }

  partMax = vehicle.getRightWheelMaxHealth()
  partNow = vehicle.getRightWheelHealth()
  if (partNow < partMax) {
    step = Math.max(1, partMax * ESVEH_PERCENT)
    vehicle.setRightWheelHealth(Math.min(partMax, partNow + step))
    worked = true
  }

  partMax = vehicle.getTurretMaxHealth()
  partNow = vehicle.getTurretHealth()
  if (partNow < partMax) {
    step = Math.max(1, partMax * ESVEH_PERCENT)
    vehicle.setTurretHealth(Math.min(partMax, partNow + step))
    worked = true
  }

  partMax = vehicle.getMainEngineMaxHealth()
  partNow = vehicle.getMainEngineHealth()
  if (partNow < partMax) {
    step = Math.max(1, partMax * ESVEH_PERCENT)
    vehicle.setMainEngineHealth(Math.min(partMax, partNow + step))
    worked = true
  }

  partMax = vehicle.getSubEngineMaxHealth()
  partNow = vehicle.getSubEngineHealth()
  if (partNow < partMax) {
    step = Math.max(1, partMax * ESVEH_PERCENT)
    vehicle.setSubEngineHealth(Math.min(partMax, partNow + step))
    worked = true
  }

  return worked
}

// 目标：先看是否坐在载具上，否则取正前方 range 格内、与视线最贴合的一台载具
function esvehFindTarget(player, range, minDot) {
  var riding = esvehAsVehicle(player.getVehicle())
  if (riding && !esvehIsWreck(riding)) return riding

  var look = player.getViewVector(1.0)
  var eyeY = player.getY() + player.getEyeHeight()
  var found = player.level.getEntities(player, player.getBoundingBox().inflate(range))
  var best = null
  var bestDot = minDot
  var i = 0
  for (i = 0; i < found.size(); i++) {
    var raw = found.get(i)
    var veh = esvehAsVehicle(raw)
    if (!veh || esvehIsWreck(veh)) continue
    var dx = raw.getX() - player.getX()
    var dy = raw.getY() + 1.0 - eyeY
    var dz = raw.getZ() - player.getZ()
    var dist = Math.sqrt(dx * dx + dy * dy + dz * dz)
    if (dist < 0.05 || dist > range) continue
    var dot = (dx * look.x() + dy * look.y() + dz * look.z()) / dist
    if (dot > bestDot) {
      bestDot = dot
      best = veh
    }
  }
  return best
}

function esvehAllFull(vehicle) {
  return vehicle.getLeftWheelHealth() >= vehicle.getLeftWheelMaxHealth() &&
    vehicle.getRightWheelHealth() >= vehicle.getRightWheelMaxHealth() &&
    vehicle.getTurretHealth() >= vehicle.getTurretMaxHealth() &&
    vehicle.getMainEngineHealth() >= vehicle.getMainEngineMaxHealth() &&
    vehicle.getSubEngineHealth() >= vehicle.getSubEngineMaxHealth()
}

function esvehWeldPlayer(player) {
  var held = player.getMainHandItem()
  if (!held) return
  if (String(held).indexOf(ESVEH_ITEM) < 0) return   // ItemStack.toString() = "数量 物品id"
  var target = esvehFindTarget(player, ESVEH_RANGE, 0.75)
  if (!target) return
  if (esvehWeldParts(target) && esvehAllFull(target)) {
    player.tell(Text.gold('焊枪：该载具部件已全部修复'))
  }
}

PlayerEvents.tick(event => {
  esvehTick++
  if (esvehTick % ESVEH_INTERVAL !== 0) return
  if (!event.player) return
  try {
    esvehWeldPlayer(event.player)
  } catch (err) {
    if (Date.now() - esvehErrStamp > 60000) {
      esvehErrStamp = Date.now()
      console.error('[esveh_weld] ' + err)
    }
  }
})

ServerEvents.commandRegistry(event => {
  const { commands: Commands } = event
  event.register(
    Commands.literal('esveh')
      .then(Commands.literal('repair')
        .requires(source => source.hasPermission(2))
        .executes(ctx => {
          const who = ctx.source.player
          if (!who) return 0
          const veh = esvehFindTarget(who, 8.0, 0.6)
          if (!veh) {
            who.tell(Text.red('没有找到正前方的载具'))
            return 0
          }
          veh.setLeftWheelHealth(veh.getLeftWheelMaxHealth())
          veh.setRightWheelHealth(veh.getRightWheelMaxHealth())
          veh.setTurretHealth(veh.getTurretMaxHealth())
          veh.setMainEngineHealth(veh.getMainEngineMaxHealth())
          veh.setSubEngineHealth(veh.getSubEngineMaxHealth())
          who.tell(Text.gold('已修满该载具的所有部件'))
          return 1
        })
      )
      .then(Commands.literal('parts')
        .requires(source => source.hasPermission(2))
        .executes(ctx => {
          const who = ctx.source.player
          if (!who) return 0
          const veh = esvehFindTarget(who, 8.0, 0.6)
          if (!veh) {
            who.tell(Text.red('没有找到正前方的载具'))
            return 0
          }
          who.tell(Text.of('履带 ' + veh.getLeftWheelHealth() + '/' + veh.getLeftWheelMaxHealth() +
            ' / ' + veh.getRightWheelHealth() + '/' + veh.getRightWheelMaxHealth() +
            '　炮塔 ' + veh.getTurretHealth() + '/' + veh.getTurretMaxHealth() +
            '　引擎 ' + veh.getMainEngineHealth() + '/' + veh.getMainEngineMaxHealth() +
            ' / ' + veh.getSubEngineHealth() + '/' + veh.getSubEngineMaxHealth()))
          return 1
        })
      )
  )
})
