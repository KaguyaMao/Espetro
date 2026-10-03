// zz_json_probe.js — 找出被截断的、含 "OBB" 的 sbw JSON 资源（服务端全数据包，含模组 jar）
// 注意：Rhino 会把 Java Map 的属性访问当作 map.get(key)，所以 entrySet 必须用反射调用。
var JsonProbeArmed = false

function jsonProbeRead(res) {
  var reader = res.openAsReader()
  var sb = new java.lang.StringBuilder()
  var line = reader.readLine()
  while (line != null) {
    sb.append(line).append('\n')
    line = reader.readLine()
  }
  reader.close()
  return String(sb.toString())
}

ServerEvents.tick(function (evt) {
  if (JsonProbeArmed) return
  JsonProbeArmed = true
  try {
    var srv = evt.server
    var rm = null
    try { rm = srv.getResourceManager() } catch (e) { console.error('[JsonProbe] getResourceManager(wrapper) failed: ' + e) }
    if (rm == null) {
      try { rm = srv.minecraftServer.getResourceManager() } catch (e) { console.error('[JsonProbe] getResourceManager(raw) failed: ' + e) }
    }
    if (rm == null) { console.error('[JsonProbe] no resource manager'); return }

    var map = rm.listResources('sbw', function (rl) { return String(rl.getPath()).endsWith('.json') })
    var entrySetMethod = Java.loadClass('java.util.Map').getMethod('entrySet')
    var set = entrySetMethod.invoke(map)
    var it = set.iterator()
    var total = 0, withObb = 0, broken = 0
    while (it.hasNext()) {
      var entry = it.next()
      var rl = String(entry.getKey())
      var res = entry.getValue()
      total++
      var text = null
      try { text = jsonProbeRead(res) } catch (e) { console.error('[JsonProbe] read failed ' + rl + ': ' + e); continue }
      if (text.indexOf('"OBB"') < 0) continue
      withObb++
      var trimmed = text.replace(/[\s\u0000]+$/, '')
      var pack = 'unknown'
      try { pack = String(res.sourcePackId()) } catch (e) {}
      if (trimmed.endsWith('}')) {
        console.info('[JsonProbe] OK   ' + rl + '  pack=' + pack + '  bytes=' + text.length)
      } else {
        broken++
        console.info('[JsonProbe] BAD  ' + rl + '  pack=' + pack + '  bytes=' + text.length
          + '  tail=' + JSON.stringify(trimmed.slice(-60)))
      }
    }
    console.info('[JsonProbe] summary: sbw json=' + total + ' withOBB=' + withObb + ' broken=' + broken)
  } catch (err) {
    console.error('[JsonProbe] failed: ' + err)
  }
})
