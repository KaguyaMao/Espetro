// 主基地禁区警告（纯 kubejs）
// 功能：任意一方主基地（spawn_points 的 ATTACK / DEFEND 出生点）周围 300 格内，敌方玩家：
//   - 高亮：Glowing（穿墙可见）
//   - 大标题：持续显示「请回到正常作战区域内」
//   - 惩罚：失明 + 缓慢（等级/开关见下方常量；想改成掉血也可以，见文件末尾注释）
// 说明：
//   - 只在备战/对战阶段（DEPLOYING / BATTLE）且玩家已归属阵营时生效
//   - 距离按水平距离（X/Z）计算，避免高空等待区误判
//   - 使用 Espetro 的 KubeJS 绑定（全局名 Espetro）
//   - 注意：KubeJS 里 player.level / player.x / player.z 是【属性】而不是方法，写成 level() 会抛异常
//   - 热重载：/reload 即生效
var BaseWarningRadius = 300.0;    // 禁区半径（格）
var BaseWarningCheckTicks = 10;   // 每 10 tick（0.5 秒）检查一次
var BaseWarningTitleTicks = 20;   // 每 20 tick（1 秒）重推大标题 -> 持续播放
var BaseWarningGlowTicks = 60;    // 高亮持续（3 秒，配合 0.5 秒复刷）
var BaseWarningDebug = true;      // ★诊断日志：每 5 秒一行；确认无误后改 false
var BaseWarningTitle = '\u00a7c\u00a7l请回到正常作战区域内'
var BaseWarningSubtitle = '\u00a7e你已进入敌方主基地禁区'
var BaseWarningPunishBlindness = 0;   // 失明等级（0=失明 I；-1=不施加）
var BaseWarningPunishSlowness = 1;    // 缓慢等级（1=缓慢 II；-1=不施加）
var BaseWarningPunishTicks = 60;      // 惩罚持续（3 秒，配合 0.5 秒复刷）

var BaseWarningTickCounter = 0
var BaseWarningWarned = {}
var BaseWarningLastDebugTick = -1000

var BaseWarningComponent = Java.loadClass('net.minecraft.network.chat.Component')
var BaseWarningTitlePacket = Java.loadClass('net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket')
var BaseWarningSubtitlePacket = Java.loadClass('net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket')
var BaseWarningAnimPacket = Java.loadClass('net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket')
var BaseWarningEffectInstance = Java.loadClass('net.minecraft.world.effect.MobEffectInstance')
var BaseWarningEffects = Java.loadClass('net.minecraft.world.effect.MobEffects')

// —— 兼容读值：KubeJS 包装器里是属性，实在没有才退回 Java 方法 ——
var BaseWarningPlayerName = function (p) {
  try { if (p.username !== undefined && p.username !== null) return String(p.username) } catch (e) { }
  try { return p.getName().getString() } catch (e) { return '?' }
}
var BaseWarningPlayerX = function (p) {
  try { if (typeof p.x === 'number') return p.x } catch (e) { }
  return p.getX()
}
var BaseWarningPlayerZ = function (p) {
  try { if (typeof p.z === 'number') return p.z } catch (e) { }
  return p.getZ()
}
var BaseWarningIsSpectator = function (p) {
  try { if (p.spectator !== undefined) return p.spectator === true } catch (e) { }
  try { return p.isSpectator() } catch (e) { return false }
}
// 玩家唯一键：KubeJS 里是属性 uuid；取不到就退回方法，再退回名字
var BaseWarningPlayerKey = function (p) {
  try { if (p.uuid !== undefined && p.uuid !== null) return String(p.uuid) } catch (e) { }
  try { return String(p.getUUID()) } catch (e) { }
  return BaseWarningPlayerName(p)
}
var BaseWarningResolveBase = function (team) {
  try {
    var spawn = Espetro.getSpawnPoint(team)
    if (spawn == null) return null
    return { x: spawn.x, z: spawn.z }
  } catch (e) {
    return null
  }
}
var BaseWarningSendTitle = function (player) {
  try {
    player.connection.send(new BaseWarningAnimPacket(5, 60, 10))
    player.connection.send(new BaseWarningSubtitlePacket(BaseWarningComponent.literal(BaseWarningSubtitle)))
    player.connection.send(new BaseWarningTitlePacket(BaseWarningComponent.literal(BaseWarningTitle)))
  } catch (e) {
    console.error('[BaseZone] 发送标题失败: ' + e)
  }
}
// 施加单个效果：先试 Java 原版写法，失败再退回 KubeJS 写法
var BaseWarningAddEffect = function (player, effect, ticks, amplifier) {
  try {
    player.addEffect(new BaseWarningEffectInstance(effect, ticks, amplifier, false, false, false))
    return true
  } catch (e) { }
  try {
    player.potionEffects.add(effect, ticks, amplifier, false, false)
    return true
  } catch (e2) {
    console.error('[BaseZone] 施加效果失败: ' + e2)
    return false
  }
}

var BaseWarningApply = function (player) {
  BaseWarningAddEffect(player, BaseWarningEffects.GLOWING, BaseWarningGlowTicks, 0)
  if (BaseWarningPunishBlindness >= 0) {
    BaseWarningAddEffect(player, BaseWarningEffects.BLINDNESS, BaseWarningPunishTicks, BaseWarningPunishBlindness)
  }
  if (BaseWarningPunishSlowness >= 0) {
    BaseWarningAddEffect(player, BaseWarningEffects.MOVEMENT_SLOWDOWN, BaseWarningPunishTicks, BaseWarningPunishSlowness)
  }
}

ServerEvents.tick(function (event) {
  BaseWarningTickCounter++
  if (BaseWarningTickCounter % BaseWarningCheckTicks !== 0) return

  try {
    var server = Espetro.server()
    if (server == null) server = event.server
    if (server == null) return

    var phase = ''
    try { phase = String(Espetro.game().getCurrentPhase()) } catch (e) { phase = '?' }

    var bases = {
      ATTACK: BaseWarningResolveBase('ATTACK'),
      DEFEND: BaseWarningResolveBase('DEFEND')
    }
    var players = server.getPlayerList().getPlayers()
    var tick = BaseWarningTickCounter

    // 诊断：每 5 秒一行
    if (BaseWarningDebug && (tick - BaseWarningLastDebugTick) >= 100) {
      BaseWarningLastDebugTick = tick
      var lines = []
      for (var d = 0; d < players.size(); d++) {
        var dp = players.get(d)
        var dTeam = null
        try { dTeam = Espetro.getPlayerTeam(dp) } catch (e) { dTeam = 'ERR' }
        lines.push(BaseWarningPlayerName(dp)
          + ' team=' + dTeam
          + ' spec=' + BaseWarningIsSpectator(dp)
          + ' pos=' + Math.round(BaseWarningPlayerX(dp)) + ',' + Math.round(BaseWarningPlayerZ(dp)))
      }
      console.info('[BaseZone][诊断] 阶段=' + phase
        + ' ATTACK基地=' + (bases.ATTACK ? (Math.round(bases.ATTACK.x) + ',' + Math.round(bases.ATTACK.z)) : 'null')
        + ' DEFEND基地=' + (bases.DEFEND ? (Math.round(bases.DEFEND.x) + ',' + Math.round(bases.DEFEND.z)) : 'null')
        + ' 在线=' + players.size() + ' | ' + lines.join(' | '))
    }

    if (phase !== 'DEPLOYING' && phase !== 'BATTLE') return
    if (bases.ATTACK == null && bases.DEFEND == null) return

    var resendTitle = (tick % BaseWarningTitleTicks) === 0
    for (var i = 0; i < players.size(); i++) {
      var player = players.get(i)
      if (player == null) continue
      if (BaseWarningIsSpectator(player)) continue

      var team = Espetro.getPlayerTeam(player)
      if (team == null) continue

      var px = BaseWarningPlayerX(player)
      var pz = BaseWarningPlayerZ(player)
      var hit = null
      for (var key in bases) {
        if (key === team) continue
        var base = bases[key]
        if (base == null) continue
        var dx = px - base.x
        var dz = pz - base.z
        var dist = Math.sqrt(dx * dx + dz * dz)
        if (dist <= BaseWarningRadius) { hit = { team: key, dist: dist }; break }
      }

      var uuid = BaseWarningPlayerKey(player)
      if (hit != null) {
        BaseWarningApply(player)
        if (resendTitle) BaseWarningSendTitle(player)
        if (BaseWarningWarned[uuid] === undefined) {
          BaseWarningWarned[uuid] = tick
          console.info('[BaseZone] ' + BaseWarningPlayerName(player) + ' 进入 ' + hit.team
            + ' 主基地禁区（' + Math.round(hit.dist) + ' 格）-> 高亮 + 标题 + 惩罚')
        }
      } else if (BaseWarningWarned[uuid] !== undefined) {
        delete BaseWarningWarned[uuid]
        console.info('[BaseZone] ' + BaseWarningPlayerName(player) + ' 已离开主基地禁区')
      }
    }
  } catch (e) {
    console.error('[BaseZone] 检查失败: ' + e)
  }
})

// 想改成"掉血"惩罚：把上面 BaseWarningPunish* 关掉（设为 -1），并在这里加一行
//   player.hurt('minecraft:generic', 2)   // 每 0.5 秒 1 颗心
console.info('[BaseZone] 主基地禁区警告已加载：半径 ' + BaseWarningRadius
  + ' 格，检查 ' + BaseWarningCheckTicks + ' tick，标题 ' + BaseWarningTitleTicks
  + ' tick，诊断=' + BaseWarningDebug)
