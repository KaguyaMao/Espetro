// fetch-server-check.mjs — 校验服务器 EsFactions/ 下文件的弹匣条目
import { createRequire } from 'module';
const require = createRequire('C:/Users/Administrator/.dsh/profiles/node_modules/');
const fs = require('fs');

const PANEL = 'https://www.derpydoge.fun:20000';
const USER = 'boy';
const PASS = 'boY1145141919810Fuck';
const DAEMON_ID = '647a21e800714185aec74849f1b0f9a6';
const INSTANCE_ID = 'cd06335bc1534e028d92b4b335be248a';
const TARGET = 'EsFactions';
const DST = 'D:/minecraft/modp/Espetro/build/tmp/server-verify/';

const sleep = (ms) => new Promise((r) => setTimeout(r, ms));

const loginRes = await fetch(`${PANEL}/api/auth/login`, {
  method: 'POST', headers: { 'Content-Type': 'application/json', 'X-Requested-With': 'XMLHttpRequest' },
  body: JSON.stringify({ username: USER, password: PASS })
});
const token = String((await loginRes.json()).data ?? '');
const cookie = (loginRes.headers.getSetCookie?.() ?? []).map((l) => l.split(';')[0]).join('; ');
const q = `daemonId=${DAEMON_ID}&uuid=${INSTANCE_ID}&token=${token}`;
fs.mkdirSync(DST, { recursive: true });

const names = process.argv.slice(2);
if (!names.length) {
  console.log('用法: node fetch-server-check.mjs <文件名...>');
  process.exit(1);
}
let looseTotal = 0, magTotal = 0, entriesTotal = 0, classTotal = 0;
for (const f of names) {
  let text = null;
  for (let a = 0; a < 6 && text === null; a++) {
    const r = await fetch(`${PANEL}/api/files?${q}`, {
      method: 'PUT',
      headers: { 'X-Requested-With': 'XMLHttpRequest', 'Cookie': cookie, 'Content-Type': 'application/json' },
      body: JSON.stringify({ target: TARGET + '/' + f })
    });
    const b = await r.json();
    const data = typeof b.data === 'string' ? b.data : '';
    if (data.trim().startsWith('{')) text = data; else await sleep(3000);
  }
  if (text === null) { console.log(`FAIL ${f}`); continue; }
  fs.writeFileSync(DST + f, text, 'utf8');
  const j = JSON.parse(text);
  let loose = 0, mag = 0;
  for (const c of Object.values(j.classes || {})) {
    classTotal++;
    for (const v of Object.values(c.variants || {})) {
      for (const it of (v.resupply && v.resupply.items) || []) {
        entriesTotal++;
        const id = String(it.id || '');
        if (/^taczmagazines:/i.test(id)) mag++;
        else if (/^tacz:ammo/i.test(id)) loose++;
      }
    }
  }
  looseTotal += loose; magTotal += mag;
  console.log(`${f}: 弹匣=${mag} 散装弹药=${loose}`);
  await sleep(2500);
}
console.log(`\n服务器汇总: 职业=${classTotal} 补给条目=${entriesTotal} 弹匣=${magTotal} 散装弹药=${looseTotal}`);
