// zz_t2.js — 探明 KubeJS 脚本里声明变量在“重复调用的回调”中的行为
var n = 0
PlayerEvents.tick(event => {
  n++
  if (n > 4) return
  var a = 1
  const b = 2
  let c = 3
  console.info('[t2] call=' + n + ' var=' + a + ' const=' + b + ' let=' + c)
})
