// artillery-probe4.mjs — 对照实验：原版 TNT vs 迫击炮弹，贴着载具爆炸
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
  await sleep(700);
}
const V = 'fcp:bmp2';
const X = 3000, Y = 200, Z = 3000;

async function trial(label, spawnCmd) {
  console.log(`\n===== ${label} =====`);
  await cmd(`kill @e[type=${V}]`);
  await cmd(`kill @e[type=minecraft:tnt]`);
  await cmd(`kill @e[type=superbwarfare:mortar_shell]`);
  await cmd(`summon ${V} ${X} ${Y} ${Z}`);
  await cmd(`data get entity @e[type=${V},limit=1] Health`);
  await cmd(spawnCmd);
  await sleep(4000);
  await cmd(`data get entity @e[type=${V},limit=1] Health`);
  await cmd(`kill @e[type=${V}]`);
  await cmd(`kill @e[type=minecraft:tnt]`);
  await cmd(`kill @e[type=superbwarfare:mortar_shell]`);
}

await trial('A 对照：原版 TNT（fuse 20）', `summon minecraft:tnt ${X} ${Y + 1} ${Z} {fuse:20s}`);
await trial('B 迫击炮弹 默认 NBT（60/100/8，可破坏方块）', `summon superbwarfare:mortar_shell ${X} ${Y + 1} ${Z} {Motion:[0.0d,-1.5d,0.0d]}`);
await trial('C 迫击炮弹 强化 NBT（250/450/12）', `summon superbwarfare:mortar_shell ${X} ${Y + 1} ${Z} {Motion:[0.0d,-1.5d,0.0d],Damage:250,ExplosionDamage:450,Radius:12}`);
console.log('\n完成');
