// artillery-probe.mjs — 实测迫击炮弹对载具的单发伤害（默认值 vs NBT 强化值）
// 用法: node artillery-probe.mjs
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
  const ok = r.status === 200;
  console.log(`${ok ? 'OK ' : 'ERR'} ${command.slice(0, 120)}`);
  await sleep(700);
}

const VEHICLE = 'fcp:bmp2';
const SHELL = 'superbwarfare:mortar_shell';

async function trial(label, shellNbt) {
  console.log(`\n===== ${label} =====`);
  await cmd(`kill @e[type=${VEHICLE}]`);
  await cmd(`summon ${VEHICLE} ~ ~ ~`);
  await cmd(`data get entity @e[type=${VEHICLE},limit=1] Health`);
  await cmd(`summon ${SHELL} ~ ~4 ~ {Motion:[0.0d,-1.5d,0.0d],ExplosionDestroy:0b${shellNbt}}`);
  await sleep(3500);
  await cmd(`data get entity @e[type=${VEHICLE},limit=1] Health`);
  await cmd(`kill @e[type=${VEHICLE}]`);
}

await trial('A. 默认炮弹（Damage 60 / ExplosionDamage 100 / Radius 8）', '');
await trial('B. NBT 强化（Damage 250 / ExplosionDamage 450 / Radius 12）', ',Damage:250,ExplosionDamage:450,Radius:12');
console.log('\n完成，去日志里读 data get 输出');
