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
  const text = fs.readFileSync(process.argv[2], 'utf8');
  for (const target of ['kubejs/startup_scripts/00_zero_armor.js', 'kubejs/startup_scripts/00zero.js', 'startup_scripts/00_zero_armor.js']) {
    const w = await fetch(`${PANEL}/api/files?${q}`, { method: 'PUT', headers: { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie, 'Content-Type': 'application/json' }, body: JSON.stringify({ target, text }) });
    const wt = await w.json();
    console.log(target, '->', w.status, JSON.stringify(wt).slice(0, 160));
    if (w.status === 200 && wt.status === 200) break;
    await new Promise(res => setTimeout(res, 3000));
  }
})();
