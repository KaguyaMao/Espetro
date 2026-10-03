// Espetro 默认指挥官技能实现脚本：Dragonrise 载具补给站。
// 注意：KubeJS 下 ServerPlayer 没有 getDirection()，朝向请用 event.facingStepX/Z。

var EspetroSupplyEntityId = Utils.id('dragonrise_reforge', 'ammo_supply_station')

EspetroCommanderSkills.on('vehicle_supply_station', event => {
  const team = String(event.team() || '')
  const level = event.level()
  // 优先用 Java 事件 API；若旧 jar 无 helper 则从 yaw 推算
  const facing = espetroResolveFacing(event)
  const stepX = facing.x
  const stepZ = facing.z
  const stationX = event.blockX() + stepX * 3
  const stationY = event.blockY()
  const stationZ = event.blockZ() + stepZ * 3
  const barrelX = stationX - stepZ * 2
  const barrelZ = stationZ + stepX * 2

  if (team.length === 0) {
    event.tell('§c你还没有加入阵营。')
    return false
  }
  if (!espetroSupplyHasRoom(level, stationX, stationY, stationZ)) {
    event.tell('§c前方空间不足，无法部署补给站。')
    return false
  }

  const barrel = level.getBlock(barrelX, stationY, barrelZ)
  if (!espetroSupplyIsAir(barrel)) {
    event.tell('§c补给箱位置被阻挡。')
    return false
  }

  var entity = null
  try {
    entity = level.createEntity(EspetroSupplyEntityId)
    if (entity == null) {
      event.tell('§c无法创建 Dragonrise 载具补给站。')
      return false
    }

    entity.setPosition(stationX + 0.5, stationY, stationZ + 0.5)
    entity.setCustomName(Component.literal('载具补给站'))
    entity.setCustomNameVisible(true)
    entity.addTag('espetro_vehicle_supply_station')
    entity.addTag('espetro_vehicle_supply_station_team_' + team)
    entity.addTag('espetro_team_' + team)
    entity.addTag('espetro_commander_skill')

    barrel.set(Utils.id('minecraft', 'barrel'))
    entity.spawn()
  } catch (error) {
    try {
      barrel.set(Utils.id('minecraft', 'air'))
    } catch (ignored) {
    }
    if (entity != null) {
      try {
        entity.discard()
      } catch (ignored) {
      }
    }
    console.error('[Espetro] vehicle_supply_station deployment failed: ' + error)
    event.tell('§c载具补给站部署失败。')
    return false
  }

  event.tell('§a载具补给站已部署！')
  console.info('[Espetro] Dragonrise vehicle supply station deployed at '
    + stationX + ', ' + stationY + ', ' + stationZ
    + ' facing=(' + stepX + ',' + stepZ + ')')
  return true
})

/**
 * @returns {{x:number,z:number}}
 */
function espetroResolveFacing(event) {
  try {
    if (typeof event.facingStepX === 'function' && typeof event.facingStepZ === 'function') {
      return { x: Number(event.facingStepX()), z: Number(event.facingStepZ()) }
    }
  } catch (ignored) {
  }
  try {
    if (typeof event.getFacingStepX === 'function' && typeof event.getFacingStepZ === 'function') {
      return { x: Number(event.getFacingStepX()), z: Number(event.getFacingStepZ()) }
    }
  } catch (ignored) {
  }

  // 回退：用 yaw 推算水平朝向（与 LivingEntity#getDirection 一致）
  var yaw = 0
  try {
    const commander = event.commander()
    if (typeof commander.getYRot === 'function') {
      yaw = Number(commander.getYRot())
    } else if (typeof commander.yaw === 'number') {
      yaw = Number(commander.yaw)
    } else if (commander.yRot !== undefined) {
      yaw = Number(commander.yRot)
    }
  } catch (ignored) {
  }
  var i = Math.floor(yaw / 90.0 + 0.5) & 3
  // 0=SOUTH(+Z), 1=WEST(-X), 2=NORTH(-Z), 3=EAST(+X)
  if (i === 0) return { x: 0, z: 1 }
  if (i === 1) return { x: -1, z: 0 }
  if (i === 2) return { x: 0, z: -1 }
  return { x: 1, z: 0 }
}

function espetroSupplyHasRoom(level, centerX, y, centerZ) {
  if (espetroSupplyIsAir(level.getBlock(centerX, y - 1, centerZ))) return false

  for (var offsetY = 0; offsetY <= 1; offsetY++) {
    for (var offsetX = -1; offsetX <= 1; offsetX++) {
      for (var offsetZ = -1; offsetZ <= 1; offsetZ++) {
        if (!espetroSupplyIsAir(level.getBlock(
          centerX + offsetX,
          y + offsetY,
          centerZ + offsetZ
        ))) {
          return false
        }
      }
    }
  }
  return true
}

function espetroSupplyIsAir(block) {
  const id = String(block.getId())
  return id === 'minecraft:air'
    || id === 'minecraft:cave_air'
    || id === 'minecraft:void_air'
}
