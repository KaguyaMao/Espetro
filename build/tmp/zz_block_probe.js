// zz_block_probe.js — 临时探针：列出所有已注册方块 ID 中的"草/蕨"类（用后即停用）
try {
  var BlockProbeReg = Java.loadClass('net.minecraft.core.registries.BuiltInRegistries')
  var BlockProbeOut = []
  var BlockProbeIt = BlockProbeReg.BLOCK.iterator()
  while (BlockProbeIt.hasNext()) {
    var BlockProbeB = BlockProbeIt.next()
    var BlockProbeId = String(BlockProbeReg.BLOCK.getKey(BlockProbeB))
    if (BlockProbeId.indexOf('grass') >= 0 || BlockProbeId.indexOf('fern') >= 0
        || BlockProbeId.indexOf('_bush') >= 0 || BlockProbeId.indexOf('clover') >= 0
        || BlockProbeId.indexOf('moss') >= 0 || BlockProbeId.indexOf('weed') >= 0) {
      BlockProbeOut.push(BlockProbeId)
    }
  }
  BlockProbeOut.sort()
  console.info('[BlockProbe] count=' + BlockProbeOut.length)
  for (var BlockProbeI = 0; BlockProbeI < BlockProbeOut.length; BlockProbeI++) {
    console.info('[BlockProbe] ' + BlockProbeOut[BlockProbeI])
  }
} catch (BlockProbeErr) {
  console.error('[BlockProbe] failed: ' + BlockProbeErr)
}
