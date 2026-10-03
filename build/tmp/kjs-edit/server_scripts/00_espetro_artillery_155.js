// Espetro 指挥官技能实现脚本：155 火炮支援（密集覆盖 + 强化战斗部）。
// 排定方式：KubeJS 的 server.scheduleInTicks（本服务器实测 20/200/600/1400 tick 长延迟回调均可靠）。
// 释放后先等 EspetroArtilleryStartDelayTicks（默认 30 秒）再开始射击。
// 调参入口全部集中在下面的常量里，改完 /reload 即生效（无需重启）。

var EspetroArtilleryEntityId = 'superbwarfare:mortar_shell'
var EspetroArtilleryJavaRandom = null
var EspetroArtilleryFallbackSeed = 135791357
var EspetroArtilleryScriptVersion = 'dense-20260925'

// 释放后到第一发的延迟（tick）：30 秒 = 600
var EspetroArtilleryStartDelayTicks = 30 * 20

// 战斗部 NBT：superbwarfare 炮弹父类会读取 Damage / ExplosionDamage / Radius
// 实体默认值为 60 / 100 / 8，这里大幅强化（数值越高对步兵越致命；对载具还受其抗性表限制）
var EspetroArtilleryShellNbt = ',Damage:300.0f,ExplosionDamage:600.0f,Radius:12.0f'

// 覆盖密度
var EspetroArtilleryImpactRadius = 45        // 散布半径（格）——越小越密
var EspetroArtilleryCoverWaves = 8           // 覆盖轮数
var EspetroArtilleryShellsPerWave = 6        // 每轮发数
var EspetroArtilleryWaveIntervalTicks = 50   // 轮间隔（tick，50 = 2.5 秒）
var EspetroArtillerySightFirstOffsetTicks = 0    // 第一发校射（相对延迟）
var EspetroArtillerySightSecondOffsetTicks = 50  // 第二发校射
var EspetroArtilleryCoverStartOffsetTicks = 400  // 覆盖射击起点（相对延迟，400 = 20 秒）

try {
  if (typeof Java !== 'undefined') {
    EspetroArtilleryJavaRandom = Java.loadClass('java.util.concurrent.ThreadLocalRandom')
  }
} catch (error) {
  EspetroArtilleryJavaRandom = null
}

console.info('[Espetro] artillery_155 script loaded: ' + EspetroArtilleryScriptVersion)

EspetroCommanderSkills.on('artillery_155', event => {
  if (!event.hasTarget()) {
    event.tell('§c请先在战术地图上选择炮击位置。')
    return false
  }

  const cfg = {
    entity: EspetroArtilleryEntityId,
    dimensionId: String(event.dimensionId()),
    sessionId: Number(Espetro.getBattlefieldSessionId()),
    centerX: espetroArtilleryNumber(event.x(), 0),
    targetY: espetroArtilleryNumber(event.y(), 0),
    centerZ: espetroArtilleryNumber(event.z(), 0),
    spawnHeight: 20,
    downwardVelocity: 1.5,
    impactRadius: EspetroArtilleryImpactRadius,
    nbt: EspetroArtilleryShellNbt,
    cancelled: false
  }
  const server = event.server()

  if (!/^[a-z0-9_.-]+:[a-z0-9_./-]+$/.test(cfg.entity)) {
    event.tell('§c炮击实体配置无效。')
    return false
  }

  var totalShells = 0
  try {
    // 释放后先等 EspetroArtilleryStartDelayTicks（默认 30 秒）再开始射击。
    // 第一轮：两发校射。
    espetroScheduleArtilleryWave(server, cfg, EspetroArtilleryStartDelayTicks + EspetroArtillerySightFirstOffsetTicks, 1)
    espetroScheduleArtilleryWave(server, cfg, EspetroArtilleryStartDelayTicks + EspetroArtillerySightSecondOffsetTicks, 1)
    totalShells += 2

    // 第二轮：密集覆盖。
    for (var wave = 0; wave < EspetroArtilleryCoverWaves; wave++) {
      espetroScheduleArtilleryWave(
        server, cfg,
        EspetroArtilleryStartDelayTicks + EspetroArtilleryCoverStartOffsetTicks + wave * EspetroArtilleryWaveIntervalTicks,
        EspetroArtilleryShellsPerWave)
      totalShells += EspetroArtilleryShellsPerWave
    }
  } catch (error) {
    console.error('[Espetro] artillery_155 scheduling failed: ' + error)
    event.tell('§c炮击任务排定失败。')
    return false
  }

  event.tell('§a火炮支援已确认：30 秒后开始射击，' + EspetroArtilleryCoverWaves + ' 轮覆盖，共 ' + totalShells + ' 发。')
  console.info('[Espetro] artillery_155 scheduled ' + (EspetroArtilleryCoverWaves + 2)
    + ' waves / ' + totalShells + ' shells, first wave in ' + (EspetroArtilleryStartDelayTicks / 20)
    + 's, radius ' + cfg.impactRadius + ', session ' + cfg.sessionId)
  return true
})

function espetroScheduleArtilleryWave(server, cfg, delayTicks, count) {
  server.scheduleInTicks(Math.max(0, Math.floor(delayTicks)), scheduled => {
    const level = espetroResolveArtilleryLevel(cfg)
    if (level == null) return

    var failures = 0
    for (var i = 0; i < count; i++) {
      const point = espetroRandomArtilleryPoint(
        cfg.centerX,
        cfg.centerZ,
        cfg.impactRadius
      )
      if (!espetroSpawnArtilleryShell(
        level,
        cfg,
        point[0],
        cfg.targetY + cfg.spawnHeight,
        point[1]
      )) {
        failures++
      }
    }

    if (failures > 0) {
      console.warn('[Espetro] artillery_155 wave failed to spawn ' + failures + '/' + count + ' shells')
    }
  })
}

function espetroResolveArtilleryLevel(cfg) {
  const phase = String(Espetro.phaseId())
  const activeDimension = Espetro.getActiveBattlefieldDimension()
  const currentSession = Number(Espetro.getBattlefieldSessionId())
  if ((phase !== 'DEPLOYING' && phase !== 'BATTLE')
      || activeDimension == null
      || String(activeDimension) !== cfg.dimensionId
      || currentSession !== cfg.sessionId) {
    if (!cfg.cancelled) {
      cfg.cancelled = true
      console.info('[Espetro] artillery_155 remaining waves cancelled: battlefield changed')
    }
    return null
  }

  const server = Espetro.server()
  if (server == null) return null
  return server.getLevel(espetroArtilleryResourceId(cfg.dimensionId))
}

function espetroSpawnArtilleryShell(level, cfg, x, y, z) {
  const command = 'summon ' + cfg.entity + ' '
    + espetroArtilleryCommandNumber(x) + ' '
    + espetroArtilleryCommandNumber(y) + ' '
    + espetroArtilleryCommandNumber(z)
    + ' {Motion:[0.0d,' + espetroArtilleryCommandNumber(-cfg.downwardVelocity) + 'd,0.0d]'
    + (cfg.nbt || '') + '}'
  return level.runCommandSilent(command) > 0
}

function espetroRandomArtilleryPoint(centerX, centerZ, radius) {
  var offsetX = 0
  var offsetZ = 0
  const radiusSquared = radius * radius
  for (var attempt = 0; attempt < 16; attempt++) {
    offsetX = (espetroArtilleryRandom() * 2 - 1) * radius
    offsetZ = (espetroArtilleryRandom() * 2 - 1) * radius
    if (offsetX * offsetX + offsetZ * offsetZ <= radiusSquared) {
      return [centerX + offsetX, centerZ + offsetZ]
    }
  }
  return [centerX + offsetX * 0.5, centerZ + offsetZ * 0.5]
}

function espetroArtilleryRandom() {
  if (EspetroArtilleryJavaRandom != null) {
    try {
      return Number(EspetroArtilleryJavaRandom.current().nextDouble())
    } catch (error) {
      EspetroArtilleryJavaRandom = null
    }
  }

  EspetroArtilleryFallbackSeed = (EspetroArtilleryFallbackSeed * 48271) % 2147483647
  if (EspetroArtilleryFallbackSeed <= 0) EspetroArtilleryFallbackSeed += 2147483646
  return EspetroArtilleryFallbackSeed / 2147483647
}

function espetroArtilleryResourceId(id) {
  const value = String(id)
  const separator = value.indexOf(':')
  return separator >= 0
    ? Utils.id(value.substring(0, separator), value.substring(separator + 1))
    : Utils.id('minecraft', value)
}

function espetroArtilleryNumber(value, fallback) {
  const number = Number(value)
  return Number.isFinite(number) ? number : fallback
}

function espetroArtilleryCommandNumber(value) {
  return espetroArtilleryNumber(value, 0).toFixed(3)
}
