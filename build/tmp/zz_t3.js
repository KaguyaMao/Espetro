// zz_t3.js — 定位 handler 里哪种写法会触发 "redeclaration"
let n = 0
function helper(target) {
  const item = target.getMainHandItem()
  return String(item)
}
PlayerEvents.tick(event => {
  n++
  if (n > 3) return
  const player = event.player
  try { console.info('[t3] named=' + helper(player)) } catch (e) { console.error('[t3] named err: ' + e) }
  try { const hand = player.getMainHandItem(); console.info('[t3] inline=' + hand) } catch (e) { console.error('[t3] inline err: ' + e) }
  try { var stack = player.getMainHandItem(); console.info('[t3] varstack=' + stack) } catch (e) { console.error('[t3] varstack err: ' + e) }
})
