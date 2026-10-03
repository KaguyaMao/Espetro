// artillery-probe7.mjs — ① 生成后 data merge 设 Motion ② 对照 TNT 是否下落
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
  console.log(`${r.status === 200 ? 'OK ' : 'ERR'} ${c.slice(0, 95)}`);
  await sleep(600);
}
const S = 'superbwarfare:mortar_shell';
const X = 26, Y = 140, Z = 47;

console.log('===== ① 迫击炮弹：生成后 data merge 设 Motion =====');
await cmd(`kill @e[type=${S}]`);
await cmd(`kill @e[type=minecraft:tnt]`);
await cmd(`summon ${S} ${X} ${Y} ${Z}`);
await sleep(500);
await cmd(`data merge entity @e[type=${S},limit=1] {Motion:[0.0d,-1.5d,0.0d]}`);
await sleep(1500);
await cmd(`data get entity @e[type=${S},limit=1] Pos`);
await sleep(1500);
await cmd(`data get entity @e[type=${S},limit=1] Pos`);
await cmd(`kill @e[type=${S}]`);

console.log('\n===== ② 对照：原版 TNT 是否下落 =====');
await cmd(`summon minecraft:tnt ${X} ${Y} ${Z} {fuse:400s,Motion:[0.0d,-1.5d,0.0d]}`);
await sleep(1500);
await cmd(`data get entity @e[type=minecraft:tnt,limit=1] Pos`);
await sleep(1500);
await cmd(`data get entity @e[type=minecraft:tnt,limit=1] Pos`);
await cmd(`kill @e[type=minecraft:tnt]`);
console.log('\n完成');
