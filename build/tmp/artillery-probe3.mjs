// artillery-probe3.mjs — 追踪迫击炮弹生命周期（是否下落/是否爆炸）
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
  await fetch(`${PANEL}/api/protected_instance/command?${q}&command=${encodeURIComponent(c)}`, {
    method: 'POST', headers: { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie }
  });
  console.log('> ' + c.slice(0, 110));
  await sleep(600);
}
const S = 'superbwarfare:mortar_shell';
await cmd('kill @e[type=minecraft:pig]');
await cmd(`kill @e[type=${S}]`);
await cmd('summon minecraft:pig 3000 200 3000');
await cmd(`summon ${S} 3000 203 3000 {Motion:[0.0d,-1.5d,0.0d]}`);
for (const t of [1, 2, 4]) {
  await sleep(1000);
  console.log(`--- t≈${t}s ---`);
  await cmd(`data get entity @e[type=${S},limit=1] Pos`);
  await cmd('data get entity @e[type=minecraft:pig,limit=1] Health');
}
await cmd(`kill @e[type=${S}]`);
await cmd('kill @e[type=minecraft:pig]');
console.log('完成');
