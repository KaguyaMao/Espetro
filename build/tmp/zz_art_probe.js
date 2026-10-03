// zz_art_probe.js — 临时探针：用 KubeJS createEntity + setMotionX/Y/Z 生成迫击炮弹，观察是否飞行
var ArtProbeShell = null
var ArtProbeCount = 0
var ArtProbeArmed = false

ServerEvents.tick(function (evt) {
  if (!ArtProbeArmed) {
    ArtProbeArmed = true
    try {
      var lvl = evt.server.overworld()
      var shell = lvl.createEntity('superbwarfare:mortar_shell')
      shell.setPosition(26.5, 140.0, 47.5)
      shell.setMotionX(0.0)
      shell.setMotionY(-1.5)
      shell.setMotionZ(0.0)
      shell.spawn()
      ArtProbeShell = shell
      console.info('[ArtProbe] created+motion shell ok')
    } catch (err) {
      console.error('[ArtProbe] failed: ' + err)
    }
    return
  }
  if (ArtProbeShell == null) return
  ArtProbeCount++
  if (ArtProbeCount % 10 === 0) {
    console.info('[ArtProbe] t=' + ArtProbeCount + ' pos=' + ArtProbeShell.x + ',' + ArtProbeShell.y + ',' + ArtProbeShell.z)
  }
  if (ArtProbeCount > 80) {
    try { ArtProbeShell.discard() } catch (e) {}
    ArtProbeShell = null
    console.info('[ArtProbe] done')
  }
})
