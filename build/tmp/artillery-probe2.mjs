// artillery-probe2.mjs — 高空开阔处实测炮弹对载具伤害（默认 vs 强化 NBT）
const PANEL = 'https://www.derpydoge.fun:20000';
const USER = 'boy', PASS = 'boY1145141919810Fuck';
const DAEMON_ID = '647a21e800714185aec74849f1b0f9a6';
const INSTANCE_ID = 'cd06335bc1534e028d92b4b335be248a';

const loginRes = await fetch(`${PANEL}/api/auth/login`, {
  method: 'POST', headers: { 'Content-Type': 'application/json', 'X-Requested-With': 'XMLHttpRequest' },
  body: JSON.stringify({ username: USER, password: PASS })
});
const token = String((await loginRes.json()).data ?? '');
const cookie = (loginRes.headers.getSetCookie?.() ?? []).map(l => l.split(';')[0]).join('; ');
const q = `daemonId=${DAEMON_ID}&uuid=${INSTANCE_ID}&token=${token}`;
const sleep = (ms) => new Promise(r => setTimeout(r, ms));
async function cmd(command) {
  const r = await fetch(`${PANEL}/api/protected_instance/command?${q}&command=${encodeURIComponent(command)}`, {
    method: 'POST', headers: { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie }
  });
  console.log(`${r.status === 200 ? 'OK ' : 'ERR'} ${command.slice(0, 110)}`);
  await sleep(650);
}
const VEHICLE = 'fcp:bmp2';
const SHELL = 'superbwarfare:mortar_shell';
const X = 3000, Y = 200, Z = 3000;   // 高空开阔处，避免地形遮挡

async function trial(label, nbt) {
  console.log(`\n===== ${label} =====`);
  await cmd(`kill @e[type=${VEHICLE}]`);
  await cmd(`summon ${VEHICLE} ${X} ${Y} ${Z}`);
  await cmd(`data get entity @e[type=${VEHICLE},limit=1] Health`);
  // 上方 3 格垂直落体命中车顶
  await cmd(`summon ${SHELL} ${X} ${Y + 3} ${Z} {Motion:[0.0d,-1.5d,0.0d],ExplosionDestroy:0b${nbt}}`);
  await sleep(3000);
  await cmd(`data get entity @e[type=${VEHICLE},limit=1] Health`);
  await cmd(`kill @e[type=${VEHICLE}]`);
}

await trial('A. 默认（60/100/8）', '');
await trial('B. 强化（250/450/12）', ',Damage:250,ExplosionDamage:450,Radius:12');
await trial('C. 只加爆炸伤害（100->800）', ',ExplosionDamage:800');
console.log('\n完成');
