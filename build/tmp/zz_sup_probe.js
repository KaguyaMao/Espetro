// zz_sup_probe.js — 临时：验证 Espetro 将要调用的补给站配置 API（测完删除）
var zzSupDone = false

ServerEvents.tick(event => {
  if (zzSupDone) return
  zzSupDone = true
  try {
    var Loader = Java.loadClass('com.redabysslucia.dragonrise_reforge.config.SupplyStationDataLoader')
    var cfg = Loader.getConfig()
    console.info('[zzsup] config = ' + (cfg == null ? 'null' : 'ok'))

    var ids = ['dragonrise_reforge:zbd04a', 'dragonrise_reforge:ztz99a', 'dragonrise_reforge:bmp3', 'fcp:stryker_m2', 'dragonrise_reforge:z20']
    var n = 0
    for (n = 0; n < ids.length; n++) {
      var id = ids[n]
      var rule = cfg.getRuleForVehicle(id)
      console.info('[zzsup] ' + id + ' -> mode=' + rule.mode + ' fixed=' + rule.fixedAmount +
        ' bonus=' + cfg.getEffectiveBonusItem(rule) + ' x' + cfg.getEffectiveBonusItemCount(rule) +
        ' ammoEntries=' + rule.ammoOverrides.size())
      var it = rule.ammoOverrides.entrySet().iterator()
      while (it.hasNext()) {
        var e = it.next()
        var ar = e.getValue()
        console.info('[zzsup]     ' + e.getKey() + ' mode=' + ar.mode + ' fixed=' + ar.fixedAmount +
          ' custom="' + ar.customItem + '" x' + ar.customItemCount)
      }
    }
  } catch (e) {
    console.error('[zzsup] err: ' + e)
  }
})
