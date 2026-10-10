// artillery-probe5.mjs — 在出生点（ticking 区块）实测迫击炮弹：是否下落 + 对载具伤害
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
async function cmd(c) {
  const r = await fetch(`${PANEL}/api/protected_instance/command?${q}&command=${encodeURIComponent(c)}`, {
    method: 'POST', headers: { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie }
  });
  console.log(`${r.status === 200 ? 'OK ' : 'ERR'} ${c.slice(0, 100)}`);
  await sleep(600);
}
const V = 'fcp:bmp2';
const S = 'superbwarfare:mortar_shell';

async function trial(label, shellNbt) {
  console.log(`\n===== ${label} =====`);
  await cmd(`kill @e[type=${V}]`);
  await cmd(`kill @e[type=${S}]`);
  await cmd(`summon ${V} ~ ~ ~`);
  await cmd(`data get entity @e[type=${V},limit=1] Health`);
  await cmd(`summon ${S} ~ ~10 ~ {Motion:[0.0d,-1.5d,0.0d]${shellNbt}}`);
  await sleep(1000);
  await cmd(`data get entity @e[type=${S},limit=1] Pos`);
  await sleep(2000);
  await cmd(`data get entity @e[type=${S},limit=1] Pos`);
  await cmd(`data get entity @e[type=${V},limit=1] Health`);
  await cmd(`kill @e[type=${V}]`);
  await cmd(`kill @e[type=${S}]`);
}

await trial('A 默认（60/100/8，方块破坏=默认开）', '');
await trial('B 强化（Damage 250 / ExplosionDamage 450 / Radius 12）', ',Damage:250,ExplosionDamage:450,Radius:12');
console.log('\n完成');
