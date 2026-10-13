// 徐进弹幕执行：把 2 发校射 + 6 轮覆盖共 26 发，按轮次均匀分配到指挥官画的直线上。
// 注意：本脚本自包含（自带随机/生成辅助函数），不依赖 artillery_155 脚本的全局变量。
var EspetroBarrageEntityId = 'superbwarfare:mortar_shell'
var EspetroBarrageImpactRadius = 90
var EspetroBarrageCoverWaves = 6
var EspetroBarrageShellsPerWave = 4
var EspetroBarrageSpawnHeight = 180
var EspetroBarrageDownwardVelocity = 1.5
var EspetroBarrageTasks = []
var EspetroBarrageTick = 0
var EspetroBarrageRandom = null
var EspetroBarrageSeed = 246813579

try {
  if (typeof Java !== 'undefined') {
    EspetroBarrageRandom = Java.loadClass('java.util.concurrent.ThreadLocalRandom')
  }
} catch (error) {
  EspetroBarrageRandom = null
}

console.info('[Espetro] artillery_barrage script loaded: line-spread-1')

ServerEvents.tick(event => {
  EspetroBarrageTick++
  if (EspetroBarrageTasks.length === 0) return
  for (var index = EspetroBarrageTasks.length - 1; index >= 0; index--) {
    const task = EspetroBarrageTasks[index]
    if (task.executeAt > EspetroBarrageTick) continue
    EspetroBarrageTasks.splice(index, 1)
    try {
      espetroBarrageExecuteWave(task)
    } catch (error) {
      console.error('[Espetro] artillery_barrage wave failed: ' + error)
    }
  }
})

EspetroCommanderSkills.on('artillery_barrage', event => {
  if (!event.hasTarget()) {
    event.tell('§c请先在战术地图上按住右键拖出弹幕直线。')
    return false
  }
  if (!event.hasLine || !event.hasLine()) {
    event.tell('§c未收到直线终点，请重新拖动选择。')
    return false
  }

  const cfg = {
    entity: EspetroBarrageEntityId,
    dimensionId: String(event.dimensionId()),
    sessionId: Number(Espetro.getBattlefieldSessionId()),
    x1: espetroBarrageNumber(event.x(), 0),
    targetY: espetroBarrageNumber(event.y(), 0),
    z1: espetroBarrageNumber(event.z(), 0),
    x2: espetroBarrageNumber(event.x2(), 0),
    z2: espetroBarrageNumber(event.z2(), 0),
    impactRadius: EspetroBarrageImpactRadius,
    spawnHeight: EspetroBarrageSpawnHeight,
    downwardVelocity: EspetroBarrageDownwardVelocity,
    cancelled: false
  }
  if (!/^[a-z0-9_.-]+:[a-z0-9_./-]+$/.test(cfg.entity)
      || cfg.sessionId <= 0 || cfg.dimensionId === 'null') {
    event.tell('§c当前战局不可使用火炮支援。')
    return false
  }

  // 两发校射打在直线起点，随后六轮覆盖沿直线从起点推进到终点
  espetroBarrageQueue(cfg, 0, 1, 0)
  espetroBarrageQueue(cfg, 20 * 20, 1, 0)
  for (var wave = 0; wave < EspetroBarrageCoverWaves; wave++) {
    espetroBarrageQueue(cfg, 2 * 20 * 20 + wave * 4 * 20, EspetroBarrageShellsPerWave, wave)
  }

  event.tell('§a徐进弹幕已确认：两发校射后，六轮覆盖将沿直线推进。')
  console.info('[Espetro] artillery_barrage queued 8 waves along line ('
    + Math.round(cfg.x1) + ',' + Math.round(cfg.z1) + ') -> ('
    + Math.round(cfg.x2) + ',' + Math.round(cfg.z2) + ')')
  return true
})

function espetroBarrageQueue(cfg, delayTicks, count, waveIndex) {
  EspetroBarrageTasks.push({
    executeAt: EspetroBarrageTick + Math.max(0, Math.floor(delayTicks)),
    cfg: cfg,
    count: count,
    waveIndex: waveIndex
  })
}

function espetroBarrageExecuteWave(task) {
  const level = espetroBarrageLevel(task.cfg)
  if (level == null) return
  const total = EspetroBarrageCoverWaves
  for (var i = 0; i < task.count; i++) {
    // 均匀分配：第 waveIndex 轮占据直线上的第 (waveIndex + 1) 段，
    // 每轮内部的若干发再均分到该段中，于是整条直线被逐轮走完。
    const t = (task.waveIndex + (i + 0.5) / Math.max(1, task.count)) / total
    const baseX = task.cfg.x1 + (task.cfg.x2 - task.cfg.x1) * t
    const baseZ = task.cfg.z1 + (task.cfg.z2 - task.cfg.z1) * t
    const point = espetroBarrageRandomPoint(baseX, baseZ, task.cfg.impactRadius)
    espetroBarrageSpawnShell(level, task.cfg, point[0],
      task.cfg.targetY + task.cfg.spawnHeight, point[1])
  }
}

function espetroBarrageLevel(cfg) {
  const phase = String(Espetro.phaseId())
  const activeDimension = Espetro.getActiveBattlefieldDimension()
  const currentSession = Number(Espetro.getBattlefieldSessionId())
  if ((phase !== 'DEPLOYING' && phase !== 'BATTLE')
      || activeDimension == null
      || String(activeDimension) !== cfg.dimensionId
      || currentSession !== cfg.sessionId) {
    if (!cfg.cancelled) {
      cfg.cancelled = true
      console.info('[Espetro] artillery_barrage remaining waves cancelled: battlefield changed')
    }
    return null
  }
  const server = Espetro.server()
  return server == null ? null : server.getLevel(espetroBarrageResourceId(cfg.dimensionId))
}

function espetroBarrageSpawnShell(level, cfg, x, y, z) {
  const command = 'summon ' + cfg.entity + ' '
    + espetroBarrageCommandNumber(x) + ' '
    + espetroBarrageCommandNumber(y) + ' '
    + espetroBarrageCommandNumber(z)
    + ' {Motion:[0.0d,' + espetroBarrageCommandNumber(-cfg.downwardVelocity) + 'd,0.0d]}'
  return level.runCommandSilent(command) > 0
}

function espetroBarrageRandomPoint(centerX, centerZ, radius) {
  var offsetX = 0
  var offsetZ = 0
  const radiusSquared = radius * radius
  for (var attempt = 0; attempt < 16; attempt++) {
    offsetX = (espetroBarrageRand() * 2 - 1) * radius
    offsetZ = (espetroBarrageRand() * 2 - 1) * radius
    if (offsetX * offsetX + offsetZ * offsetZ <= radiusSquared) return [centerX + offsetX, centerZ + offsetZ]
  }
  return [centerX + offsetX * 0.5, centerZ + offsetZ * 0.5]
}

function espetroBarrageRand() {
  if (EspetroBarrageRandom != null) {
    try {
      return Number(EspetroBarrageRandom.current().nextDouble())
    } catch (error) {
      EspetroBarrageRandom = null
    }
  }
  EspetroBarrageSeed = (EspetroBarrageSeed * 48271) % 2147483647
  if (EspetroBarrageSeed <= 0) EspetroBarrageSeed += 2147483646
  return EspetroBarrageSeed / 2147483647
}

function espetroBarrageResourceId(id) {
  const value = String(id)
  const separator = value.indexOf(':')
  return separator >= 0
    ? Utils.id(value.substring(0, separator), value.substring(separator + 1))
    : Utils.id('minecraft', value)
}

function espetroBarrageNumber(value, fallback) {
  const number = Number(value)
  return Number.isFinite(number) ? number : fallback
}

function espetroBarrageCommandNumber(value) {
  return espetroBarrageNumber(value, 0).toFixed(3)
}
