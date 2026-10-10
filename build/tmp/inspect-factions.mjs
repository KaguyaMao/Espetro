import { createRequire } from 'module';
const require = createRequire('D:/minecraft/modp/Espetro/build/tmp/');
const fs = require('fs');
const PANEL = 'https://www.derpydoge.fun:20000';
const USER = 'boy';
const PASS = 'boY1145141919810Fuck';
const DAEMON_ID = '647a21e800714185aec74849f1b0f9a6';
const INSTANCE_ID = 'cd06335bc1534e028d92b4b335be248a';
(async () => {
  const loginRes = await fetch(`${PANEL}/api/auth/login`, { method: 'POST', headers: { 'Content-Type': 'application/json', 'X-Requested-With': 'XMLHttpRequest' }, body: JSON.stringify({ username: USER, password: PASS }) });
  const token = String((await loginRes.json()).data ?? '');
  const cookie = (loginRes.headers.getSetCookie?.() ?? []).map((l) => l.split(';')[0]).join('; ');
  const q = `daemonId=${DAEMON_ID}&uuid=${INSTANCE_ID}&token=${token}`;
  const files = ['pla_112th_brigade_mesh.json','plamc_5th.json','plamc_5th_at.json'];
  for (const f of files) {
    const r = await fetch(`${PANEL}/api/files?${q}`, { method: 'PUT', headers: { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie, 'Content-Type': 'application/json' }, body: JSON.stringify({ target: 'EsFactions/' + f }) });
    const b = await r.json();
    const txt = typeof b.data === 'string' ? Buffer.from(b.data, 'latin1').toString('utf8') : JSON.stringify(b);
    fs.writeFileSync('D:/minecraft/modp/Espetro/build/tmp/' + f, txt, 'utf8');
    // 提取 classes 段中的职业 id
    const ids = [...txt.matchAll(/"id"\s*:\s*"([^"]+)"/g)].map(m => m[1]).filter(v => /COMMANDER|CREW|ENGINEER|MACHINE|GRENADE|MARKSMAN|MEDIC|SCOUT|RIFLE|MG|AT|Leader/i.test(v));
    const all = [...new Set([...txt.matchAll(/"classId"\s*:\s*"([^"]+)"/g)].map(m => m[1]))];
    console.log('==== ' + f + ' (' + txt.length + ' chars) ====');
    console.log('classId refs:', all.join(', '));
    console.log('id-looking:', [...new Set(ids)].slice(0, 20).join(', '));
    await new Promise(x => setTimeout(x, 1200));
  }
})();
